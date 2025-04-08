package com.interview.reece.addressbook.integration;

import com.interview.reece.addressbook.model.AddressBook;
import com.interview.reece.addressbook.model.Customer;
import com.interview.reece.addressbook.repository.AddressBookRepository;
import com.interview.reece.addressbook.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class AbstractAddressBookIntegrationTest {

    @Autowired
    protected CustomerRepository customerRepository;

    @Autowired
    protected AddressBookRepository addressBookRepository;


    protected void cleanDB() {
        addressBookRepository.deleteAll();
    }

    protected AddressBook insertAddressBook(final String title, String... customerNames) {
        final Set<Customer> newCustomers = Arrays.stream(customerNames)
                .map(Customer::new)
                .collect(Collectors.toSet());
        final AddressBook addressBook = addressBookRepository.save(new AddressBook(title));
        newCustomers.forEach(c -> c.setAddressBook(addressBook));
        customerRepository.saveAll(newCustomers);
        return addressBook;

    }

    protected Customer insertCustomer(final AddressBook addressBook,final String name,String... phoneNumbers){
        Customer customer = new Customer(name);
        customer.setAddressBook(addressBook);
        if(phoneNumbers != null || phoneNumbers.length > 0){
            customer.setPhoneNumbers(Set.of(phoneNumbers));
        }
        return customerRepository.save(customer);
    }


    protected AddressBook insertAddressBook(final String title, Map<String, Set<String>> nameToPhoneNumbers) {
        final Set<Customer> newCustomers = nameToPhoneNumbers.entrySet().stream()
                .map(entry -> new Customer(entry.getKey(), entry.getValue()))
                .collect(Collectors.toSet());
        final AddressBook addressBook = addressBookRepository.save(new AddressBook(title));
        newCustomers.forEach(c -> c.setAddressBook(addressBook));
        customerRepository.saveAll(newCustomers);
        return addressBook;

    }

}
