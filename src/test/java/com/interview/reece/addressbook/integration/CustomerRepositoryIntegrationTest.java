package com.interview.reece.addressbook.integration;

import com.interview.reece.addressbook.aggregate.MergeCustomer;
import com.interview.reece.addressbook.model.AddressBook;
import com.interview.reece.addressbook.model.Customer;
import com.interview.reece.addressbook.repository.CustomerRepository;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.interview.reece.addressbook.TestUtil.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@Tag(INTEGRATION_TEST)
@ActiveProfiles(SPRING_PROFILE_TEST)
public class CustomerRepositoryIntegrationTest {

    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private CustomerRepository customerRepository;

    @Test
    public void testFindDisticntNameGivenCustomers() {

        insertAddressBook("Address Book 1", Map.of("Anna", Set.of("001", "002", "003"), "Beth", Set.of("010"), "Eli", Set.of("020")));
        insertAddressBook("Address Book 2", Map.of("Anna", Set.of("101", "102", "003"), "Cell", Set.of("110"), "Dan", Set.of("120")));

        final int pageSize = 3;

        //Page 1
        final Pageable page1 = PageRequest.of(0, pageSize, Sort.by("name"));
        final Page<MergeCustomer> response = customerRepository.findDistinctName(page1);

        assertEquals(5, response.getTotalElements());
        assertEquals(2, response.getTotalPages());
        assertEquals(3, response.getNumberOfElements());

        final List<MergeCustomer> customers = response.getContent();
        assertContains(customers,c->"Anna".equals(c.getName()) && c.getPhoneNumbers().size() == 5); // ensure it's merge
        assertContains(customers,c->"Beth".equals(c.getName()) && c.getPhoneNumbers().size() == 1);
        assertContains(customers,c->"Cell".equals(c.getName()) && c.getPhoneNumbers().size() == 1);
        assertNotContains(customers,c->"Dan".equals(c.getName()));
        assertNotContains(customers,c->"Eli".equals(c.getName()));


        //ensure sorting by name
        final MergeCustomer anna = customers.get(0);

        assertEquals("Anna",anna.getName());
        assertMatchesAllUnordered(Set.of("001","002","003","101","102"),anna.getPhoneNumbers());

        final MergeCustomer beth = customers.get(1);
        assertEquals("Beth",beth.getName());
        assertTrue(beth.getPhoneNumbers().contains("010"));

        final MergeCustomer cell = customers.get(2);
        assertEquals("Cell",cell.getName());
        assertTrue(cell.getPhoneNumbers().contains("110"));


        final Pageable page2 = PageRequest.of(1, pageSize, Sort.by("name"));
        final Page<MergeCustomer> response2 = customerRepository.findDistinctName(page2);

        assertEquals(5, response2.getTotalElements());
        assertEquals(2, response2.getTotalPages());
        assertEquals(2, response2.getNumberOfElements());

        final List<MergeCustomer> customers2 = response2.getContent();
        assertContains(customers2,c->"Dan".equals(c.getName()) && c.getPhoneNumbers().size() == 1);
        assertContains(customers2,c->"Eli".equals(c.getName()) && c.getPhoneNumbers().size() == 1);
        assertNotContains(customers2,c->"Anna".equals(c.getName()));
        assertNotContains(customers2,c->"Beth".equals(c.getName()));
        assertNotContains(customers2,c->"Cell".equals(c.getName()));

        //ensure sorting by name continuation page 2
        final MergeCustomer dan = customers2.get(0);

        assertEquals("Dan",dan.getName());
        assertTrue(dan.getPhoneNumbers().contains("120"));

        final MergeCustomer eli = customers2.get(1);
        assertEquals("Eli",eli.getName());
        assertTrue(eli.getPhoneNumbers().contains("020"));

    }

    private void insertAddressBook(final String title, Map<String, Set<String>> customers) {
        final AddressBook addressBook = testEntityManager.persist(new AddressBook(title));

        customers.entrySet().stream()
                .map(entry -> new Customer(entry.getKey(), entry.getValue(), addressBook))
                .forEach(testEntityManager::persist);
    }
}
