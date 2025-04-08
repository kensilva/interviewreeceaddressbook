package com.interview.reece.addressbook;

import com.interview.reece.addressbook.aggregate.MergeCustomer;
import com.interview.reece.addressbook.dto.AddressBookDTO;
import com.interview.reece.addressbook.dto.CustomerPageResult;
import com.interview.reece.addressbook.dto.request.AddressBookRequestDTO;
import com.interview.reece.addressbook.dto.CustomerDTO;
import com.interview.reece.addressbook.dto.request.CustomerRequestDTO;
import com.interview.reece.addressbook.exception.AddressBookNotFoundException;
import com.interview.reece.addressbook.model.AddressBook;
import com.interview.reece.addressbook.model.BaseEntity;
import com.interview.reece.addressbook.model.Customer;
import com.interview.reece.addressbook.repository.AddressBookRepository;
import com.interview.reece.addressbook.repository.CustomerRepository;
import com.interview.reece.addressbook.service.AddressBookService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import org.springframework.data.domain.Page;
import org.springframework.test.context.ActiveProfiles;

import java.util.*;
import java.util.stream.Collectors;

import static com.interview.reece.addressbook.TestUtil.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
@Tag(TestUtil.UNIT_TEST)
@ActiveProfiles(SPRING_PROFILE_TEST)
public class AddressBookServiceTest {

    @InjectMocks
    private AddressBookService addressBookService;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private AddressBookRepository addressBookRepository;


    @Test
    public void testCreateAddressBookGivenValidInput() {
        final AddressBookRequestDTO addressBookRequestDTO = new AddressBookRequestDTO();
        addressBookRequestDTO.setTitle("AddressBook1");
        given(addressBookRepository.save(any())).willAnswer(mockSaveAnswer(1));

        final AddressBookDTO result = addressBookService.createAddressBook(addressBookRequestDTO);

        assertEquals(addressBookRequestDTO.getTitle(), result.getTitle());
    }

    @Test
    public void testCreateAddressBookGivenInvalidInputThrowsException() {
        Assertions.assertThrowsExactly(NullPointerException.class, () -> addressBookService.createAddressBook(null));
    }

    @Test
    public void testFindAllAddressBooks() {
        final AddressBook addressBook1 = mock(AddressBook.class);
        final AddressBook addressBook2 = mock(AddressBook.class);

        given(addressBook1.getTitle()).willReturn("book1");
        given(addressBook2.getTitle()).willReturn("book2");
        given(addressBookRepository.findAll()).willReturn(Arrays.asList(addressBook1, addressBook2));
        final List<AddressBookDTO> result = addressBookService.getAllAddressBooks();

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(2, result.size());
        assertEquals(addressBook1.getTitle(), result.get(0).getTitle());
        assertEquals(addressBook2.getTitle(), result.get(1).getTitle());
    }

    @Test
    public void testFindAddressBookByCustomerGivenAddressBookNotFound() {
        long nonExistingId = 1;

        given(addressBookRepository.findById(nonExistingId)).willReturn(Optional.empty());
        Assertions.assertThrowsExactly(AddressBookNotFoundException.class, () -> addressBookService.getAllCustomerByAddressBookId(nonExistingId));
    }

    @Test
    public void testFindAddressBookByCustomerGivenAddressBookFound() {
        long addressBookId = 1;

        final AddressBook existingAddressBook = mock(AddressBook.class);
        final Customer customer1 = mock(Customer.class);
        final Customer customer2 = mock(Customer.class);

        given(customer1.getName()).willReturn("customer1Name");
        given(customer2.getName()).willReturn("customer2Name");
        given(existingAddressBook.getCustomers()).willReturn(Set.of(customer1, customer2));
        given(addressBookRepository.findById(addressBookId)).willReturn(Optional.of(existingAddressBook));
        final List<CustomerDTO> customers = addressBookService.getAllCustomerByAddressBookId(addressBookId);

        assertNotNull(customers);
        assertFalse(customers.isEmpty());
        assertEquals(2, customers.size());

        assertContains(customers, c -> customer1.getName().equals(c.getName()));
        assertContains(customers, c -> customer2.getName().equals(c.getName()));
    }

    @Test
    public void testCreateCustomerGivenNoCustomer() {
        assertThrowsExactly(NullPointerException.class, () -> addressBookService.createCustomer(1, null));
    }


    @Test
    public void testCreateCustomerGivenInvalidAddressBook() {
        given(addressBookRepository.findById(any())).willReturn(Optional.empty());
        assertThrowsExactly(AddressBookNotFoundException.class, () -> addressBookService.createCustomer(0, new CustomerRequestDTO("John", Set.of())));
    }

    @Test
    public void testCreateCustomerGivenValidIdAndCustomer() {
        final long validAddressBookId = 1;
        given(addressBookRepository.findById(validAddressBookId)).willAnswer(mockFindAddressBookAnswer());

        final CustomerRequestDTO request = new CustomerRequestDTO("John", Set.of("01", "02"));
        given(customerRepository.save(any())).willAnswer(mockSaveAnswer(1));
        final CustomerDTO customer = addressBookService.createCustomer(validAddressBookId, request);

        assertNotNull(customer);
        assertEquals(request.getName(), customer.getName());
        assertEquals(1, customer.getId());
        assertMatchesAllUnordered(request.getPhoneNumbers(), customer.getPhoneNumbers());

    }

    @Test
    public void testGetAllUniqueCustomer() {
        int page = 0;
        int pageSize = 12;

        final Page<MergeCustomer> pageResponse = mock(Page.class);
        final List<MergeCustomer> mergeCustomers = mockMergeCustomers(Map.of("Jose", Set.of("123,234,567"), "Milla", Set.of("323"), "Anna", Set.of("345")));
        given(pageResponse.stream()).willReturn(mergeCustomers.stream());
        given(pageResponse.getTotalElements()).willReturn((long) mergeCustomers.size());
        given(pageResponse.getTotalPages()).willReturn(1);
        given(customerRepository.findDistinctName(any())).willReturn(pageResponse);

        final CustomerPageResult customerPageResult = addressBookService.getAllUniqueCustomers(page, pageSize);

        assertNotNull(customerPageResult);
        assertEquals(page,customerPageResult.getPage());
        assertEquals(pageSize,customerPageResult.getRequestedPageSize());
        assertEquals(1,customerPageResult.getTotalPages());
        assertEquals(3,customerPageResult.getTotalSize());

        final List<CustomerDTO> customerResponse = customerPageResult.getResults();
        assertContains(customerResponse,c->"Jose".equals(c.getName()) && c.getPhoneNumbers().containsAll(Set.of("123,234,567")));
        assertContains(customerResponse,c->"Milla".equals(c.getName()) && c.getPhoneNumbers().contains("323"));
    }

    private static <T extends BaseEntity> Answer<T> mockSaveAnswer(long pk) {
        return inv -> {
            final BaseEntity base = inv.getArgument(0, BaseEntity.class);
            base.setPk(pk);
            return (T) base;
        };
    }

    private static Answer<Optional<AddressBook>> mockFindAddressBookAnswer() {
        return inv -> {
            final Long pk = inv.getArgument(0, Long.class);
            final AddressBook addressBook = new AddressBook(null);
            addressBook.setPk(pk);
            return Optional.of(addressBook);
        };
    }

    private List<MergeCustomer> mockMergeCustomers(final Map<String, Set<String>> customerNameByPhoneNumber) {
        return customerNameByPhoneNumber.entrySet().stream()
                .map(entry -> createMergeCustomer(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    private MergeCustomer createMergeCustomer(final String name, final Set<String> phoneNumbers) {
        return new MergeCustomer() {
            @Override
            public String getName() {
                return name;
            }

            @Override
            public Set<String> getPhoneNumbers() {
                return phoneNumbers;
            }
        };
    }
}
