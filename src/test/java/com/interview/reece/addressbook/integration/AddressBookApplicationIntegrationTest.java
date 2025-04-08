package com.interview.reece.addressbook.integration;

import com.interview.reece.addressbook.TestUtil;
import com.interview.reece.addressbook.dto.AddressBookDTO;
import com.interview.reece.addressbook.dto.CustomerDTO;
import com.interview.reece.addressbook.dto.CustomerPageResult;
import com.interview.reece.addressbook.dto.request.AddressBookRequestDTO;
import com.interview.reece.addressbook.dto.request.CustomerRequestDTO;
import com.interview.reece.addressbook.model.AddressBook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.interview.reece.addressbook.TestUtil.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Tag(INTEGRATION_TEST)
@ActiveProfiles(SPRING_PROFILE_TEST)
public class AddressBookApplicationIntegrationTest extends AbstractAddressBookIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    TestRestTemplate restTemplate;


    @BeforeEach
    public void prepare() {
        cleanDB();
    }

    @Test
    public void testCreateNewAddressBookGivenValidInput() {

        final AddressBookRequestDTO addressBookRequestDTO = new AddressBookRequestDTO("AddressBook1");
        final ResponseEntity<AddressBookDTO> response = restTemplate.postForEntity(getUrl("/address-book/books"), addressBookRequestDTO, AddressBookDTO.class);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        assertTrue(addressBookRepository.findById(response.getBody().getId()).isPresent());

    }

    @Test
    public void testCreateNewAddressBookGivenInvalidInput() {

        final ResponseEntity<ErrorResponse> response = restTemplate.exchange(getUrl("/address-book/books"), HttpMethod.POST, new HttpEntity<>(new AddressBookRequestDTO()), ErrorResponse.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());

        final ErrorResponse errorResponse = response.getBody();
        assertNotNull(errorResponse.getErrors());
        assertTrue(errorResponse.getErrors().size() > 0);
    }

    @Test
    public void testGetAllAddressBook() {
        insertAddressBook("Address Book 100");
        insertAddressBook("Address Book 101");
        final ResponseEntity<List<AddressBookDTO>> addressBooksResponse = restTemplate.exchange(getUrl("/address-book/books"), HttpMethod.GET, null, new ParameterizedTypeReference<>() {
        });
        assertEquals(HttpStatus.OK, addressBooksResponse.getStatusCode());

        final List<AddressBookDTO> addressBooks = addressBooksResponse.getBody();
        assertEquals(2, addressBooks.size());
        assertContains(addressBooks, book -> "Address Book 100".equals(book.getTitle()));
        assertContains(addressBooks, book -> "Address Book 101".equals(book.getTitle()));
    }

    @Test
    public void testGetAddressBookCustomersGivenNotExisting() {

        long addressBookId = 0;
        final ResponseEntity<String> errorResponse = restTemplate.getForEntity(getUrl("/address-book/books/%d/customers".formatted(addressBookId)), String.class);
        assertEquals(HttpStatus.NOT_FOUND, errorResponse.getStatusCode());
    }

    @Test
    public void testGetAddressBookCustomersGivenExistingCustomer() {

        long addressBookId1 = insertAddressBook("Address Book 1", "Merry Go", "Round Robin").getPk();
        insertAddressBook("Address Book 2", "Jose Jan", "Roberto Pot");
        final ResponseEntity<List<CustomerDTO>> customersResponse = restTemplate.exchange(getUrl("/address-book//books/%d/customers".formatted(addressBookId1)), HttpMethod.GET, null, new ParameterizedTypeReference<>() {
        });
        assertEquals(HttpStatus.OK, customersResponse.getStatusCode());

        final List<CustomerDTO> result = customersResponse.getBody();
        assertEquals(2, result.size());
        assertContains(result, c -> "Merry Go".equals(c.getName()));
        assertContains(result, c -> "Round Robin".equals(c.getName()));
        assertNotContains(result, c -> "Jose Jan".equals(c.getName()));
        assertNotContains(result, c -> "Roberto Pot".equals(c.getName()));
    }

    @Test
    public void testCreateCustomerInvalidAddressBook() {
        final ResponseEntity<String> customersResponse = restTemplate.getForEntity(getUrl("/address-book/books/%d/customers".formatted(0)), String.class);
        assertEquals(HttpStatus.NOT_FOUND, customersResponse.getStatusCode());
    }

    @Test
    public void testRemoveCustomer() {
        final AddressBook addressBook = insertAddressBook("Address Book 1");
        final long customerID = insertCustomer(addressBook, "Rowe", "02121", "022222").getPk();

        restTemplate.delete(getUrl("/address-book/customers/%d".formatted(customerID)));
        assertTrue(customerRepository.findById(customerID).isEmpty());
    }

    @Test
    public void testRemoveCustomerNotExisting() {

        final ResponseEntity<ErrorResponse> errorResponseEntity = restTemplate.exchange(getUrl("/address-book/customers/%d".formatted(0)), HttpMethod.DELETE, null, ErrorResponse.class);
        assertEquals(HttpStatus.NOT_FOUND,errorResponseEntity.getStatusCode());
        final ErrorResponse errorResponse = errorResponseEntity.getBody();
        assertNotNull(errorResponse);
        assertNotNull(errorResponse.getErrors());
        assertTrue(errorResponse.getErrors().size() >0);
    }

    @Test
    public void testCreateCustomerValidAddressBook() {
        long id = insertAddressBook("Address Book 1").getPk();
        final ResponseEntity<CustomerDTO> customersResponse = restTemplate.postForEntity(getUrl("/address-book/books/%d/customers".formatted(id)), new CustomerRequestDTO("Martha", Set.of("0123121", "0121022")), CustomerDTO.class);
        assertEquals(HttpStatus.CREATED, customersResponse.getStatusCode());
        final CustomerDTO customerDTO = customersResponse.getBody();

        assertNotNull(customerDTO);
        assertEquals("Martha", customerDTO.getName());
        assertContains(customerDTO.getPhoneNumbers(), "0123121"::equals);
        assertContains(customerDTO.getPhoneNumbers(), "0121022"::equals);
    }

    @Test
    public void testCreateCustomerValidAddressBookInvalidCustomer() {
        long id = insertAddressBook("Address Book 1").getPk();
        final ResponseEntity<ErrorResponse> customersResponse = restTemplate.postForEntity(getUrl("/address-book/books/%d/customers".formatted(id)), new CustomerRequestDTO("", Set.of("0123121", "0121022")), ErrorResponse.class);
        assertEquals(HttpStatus.BAD_REQUEST, customersResponse.getStatusCode());
        final ErrorResponse errorResponse = customersResponse.getBody();

        assertNotNull(errorResponse);
        assertNotNull(errorResponse.getErrors());
        assertTrue(errorResponse.getErrors().size() > 0);
    }

    @Test
    public void testGetAllUniqueCustomers() {
        insertAddressBook("Address Book 1", Map.of("Allan", Set.of("01", "02", "03"), "Jo", Set.of("121", "122", "123")));
        insertAddressBook("Address Book 2", Map.of("Allan", Set.of("11", "12", "13"), "Jo", Set.of("221", "222", "223")));
        insertAddressBook("Address Book 3", Map.of("Allan", Set.of("31", "12", "13"), "Jean", Set.of("330", "331", "332")));
        int pageSize = 2;
        final ResponseEntity<CustomerPageResult> customersResponse = restTemplate.exchange(getUrl("/address-book/customers?page=%d&pageSize=%d".formatted(0, pageSize)), HttpMethod.GET, null, new ParameterizedTypeReference<CustomerPageResult>() {
        });

        assertEquals(HttpStatus.OK, customersResponse.getStatusCode());

        final CustomerPageResult customerPageResult = customersResponse.getBody();

        assertEquals(3, customerPageResult.getTotalSize());
        assertEquals(0, customerPageResult.getPage());
        assertEquals(2, customerPageResult.getCurrentPageSize());
        assertEquals(2, customerPageResult.getTotalPages());

        final List<CustomerDTO> customers = customerPageResult.getResults();
        assertContains(customers, c -> "Allan".contains(c.getName()));
        assertContains(customers, c -> "Jean".contains(c.getName()));
        assertNotContains(customers, c -> "Jo".contains(c.getName()));

        final CustomerDTO allan = customers.get(0);
        assertEquals(7, allan.getPhoneNumbers().size());
        assertContains(allan.getPhoneNumbers(), "01"::equals);
        assertContains(allan.getPhoneNumbers(), "11"::equals);
        assertContains(allan.getPhoneNumbers(), "31"::equals);

        //Second Page test ensuring
        final ResponseEntity<CustomerPageResult> customersResponse2 = restTemplate.exchange(getUrl("/address-book/customers?page=%d&pageSize=%d".formatted(1, pageSize)), HttpMethod.GET, null, new ParameterizedTypeReference<CustomerPageResult>() {
        });

        assertEquals(HttpStatus.OK, customersResponse2.getStatusCode());

        final CustomerPageResult customerPageResult2 = customersResponse2.getBody();

        assertEquals(3, customerPageResult2.getTotalSize());
        assertEquals(1, customerPageResult2.getPage());
        assertEquals(1, customerPageResult2.getCurrentPageSize());
        assertEquals(2, customerPageResult2.getTotalPages());

        final List<CustomerDTO> customers2 = customerPageResult2.getResults();
        assertNotContains(customers2, c -> "Allan".contains(c.getName()));
        assertNotContains(customers2, c -> "Jean".contains(c.getName()));
        assertContains(customers2, c -> "Jo".contains(c.getName()));


        final CustomerDTO jo = customers2.get(0);
        assertEquals(6, jo.getPhoneNumbers().size());
        assertContains(jo.getPhoneNumbers(), "121"::equals);
        assertContains(jo.getPhoneNumbers(), "221"::equals);

    }

    protected String getUrl(final String path) {
        return TestUtil.BASE_URL_FORMAT.formatted(port, path);
    }


    private static class ErrorResponse {
        private List<String> errors;

        private ErrorResponse() {

        }

        public List<String> getErrors() {
            return errors;
        }

        public void setErrors(List<String> errors) {
            this.errors = errors;
        }


    }
}
