package com.interview.reece.addressbook.service;

import com.interview.reece.addressbook.aggregate.MergeCustomer;
import com.interview.reece.addressbook.dto.AddressBookDTO;
import com.interview.reece.addressbook.dto.CustomerPageResult;
import com.interview.reece.addressbook.dto.request.AddressBookRequestDTO;
import com.interview.reece.addressbook.dto.CustomerDTO;
import com.interview.reece.addressbook.dto.request.CustomerRequestDTO;
import com.interview.reece.addressbook.exception.AddressBookNotFoundException;
import com.interview.reece.addressbook.exception.CustomerNotFoundException;
import com.interview.reece.addressbook.model.AddressBook;
import com.interview.reece.addressbook.model.Customer;
import com.interview.reece.addressbook.repository.AddressBookRepository;
import com.interview.reece.addressbook.repository.CustomerRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class AddressBookService {

    private CustomerRepository customerRepository;
    private AddressBookRepository addressBookRepository;

    public AddressBookService(CustomerRepository customerRepository, AddressBookRepository addressBookRepository) {
        this.customerRepository = customerRepository;
        this.addressBookRepository = addressBookRepository;
    }

    /**
     * Returns all customers that belong to the address book with given id
     *
     * @param addressBookId the address book id
     * @return list of customers
     */
    public List<CustomerDTO> getAllCustomerByAddressBookId(final long addressBookId) {
        final AddressBook addressBook = getAddressBook(addressBookId);
        return addressBook.getCustomers().stream().map(this::convertToCustomerDTO).collect(Collectors.toList());
    }

    /**
     * Creates new address book with empty customers
     *
     * @param addressBookRequest the request containing the address book details
     * @return new Address book
     */
    public AddressBookDTO createAddressBook(final AddressBookRequestDTO addressBookRequest) {
        Objects.requireNonNull(addressBookRequest,"addressBookRequest is required.");
        final AddressBook addressBook = addressBookRepository.save(convertRequestToAddressBook(addressBookRequest));
        return convertToAddressBookDTO(addressBook);
    }

    /**
     * Creates new customer and assign it to the address book with addressBookId
     *
     * @param addressBookId      the addressBookId to attach the customer
     * @param customerRequest the request with customer details
     * @return new Customer
     */
    public CustomerDTO createCustomer(final long addressBookId, final CustomerRequestDTO customerRequest) {
        Objects.requireNonNull(customerRequest,"customerRequest is required.");
        final AddressBook addressBook = getAddressBook(addressBookId);
        final Customer customer = convertRequestToCustomer(customerRequest);
        customer.setAddressBook(addressBook);
        customerRepository.save(customer);
        return convertToCustomerDTO(customer);

    }

    /**
     * Delete the customer entry
     *
     * @param customerId the customer's id to remove
     */
    public void removeCustomer(final long customerId) {
        final Customer customer = getCustomer(customerId);
        customerRepository.delete(customer);
    }

    /**
     * Returns all address book
     *
     * @return address books
     */
    public List<AddressBookDTO> getAllAddressBooks() {
        return addressBookRepository.findAll().stream()
                .map(this::convertToAddressBookDTO)
                .collect(Collectors.toList());
    }

    public CustomerPageResult getAllUniqueCustomers(int page, int pageSize) {

        final Page<MergeCustomer> pagedCustomer = customerRepository.findDistinctName(PageRequest.of(page, pageSize, Sort.by("name")));
        final CustomerPageResult result = new CustomerPageResult(page,pageSize,pagedCustomer);
        final List<CustomerDTO> customers = pagedCustomer.stream()
                .map(this::convertToCustomerDTO)
                .collect(Collectors.toList());
        result.setResults(customers);
        return result;
    }

    protected AddressBook getAddressBook(final long addressBookId) {
        return addressBookRepository.findById(addressBookId).orElseThrow(() -> new AddressBookNotFoundException("No address book found for [%d]".formatted(addressBookId)));
    }

    protected Customer getCustomer(final long customerId) {
        return customerRepository.findById(customerId).orElseThrow(() -> new CustomerNotFoundException("No customer found for id [%d]".formatted(customerId)));
    }

    protected CustomerDTO convertToCustomerDTO(final Customer customer) {
        return new CustomerDTO(customer.getPk(), customer.getName(), customer.getPhoneNumbers());
    }

    protected CustomerDTO convertToCustomerDTO(final MergeCustomer customer) {
        return new CustomerDTO(customer.getName(), customer.getPhoneNumbers());
    }

    protected AddressBookDTO convertToAddressBookDTO(final AddressBook addressBook) {
        return new AddressBookDTO(addressBook.getPk(), addressBook.getTitle());
    }

    protected AddressBook convertRequestToAddressBook(final AddressBookRequestDTO addressBookRequestDTO) {
        return new AddressBook(addressBookRequestDTO.getTitle());
    }

    protected Customer convertRequestToCustomer(final CustomerRequestDTO customerRequest) {
        return new Customer(customerRequest.getName(), customerRequest.getPhoneNumbers());
    }
}
