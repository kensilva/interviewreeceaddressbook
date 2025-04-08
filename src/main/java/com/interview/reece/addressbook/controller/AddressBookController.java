package com.interview.reece.addressbook.controller;

import com.interview.reece.addressbook.dto.AddressBookDTO;
import com.interview.reece.addressbook.dto.CustomerPageResult;
import com.interview.reece.addressbook.dto.request.AddressBookRequestDTO;
import com.interview.reece.addressbook.dto.CustomerDTO;
import com.interview.reece.addressbook.dto.request.CustomerRequestDTO;
import com.interview.reece.addressbook.service.AddressBookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/address-book")
@Tag(name = "Address Book", description = "Address Book API")
public class AddressBookController {

    private AddressBookService addressBookService;

    public AddressBookController(AddressBookService addressBookService) {
        this.addressBookService = addressBookService;
    }

    @GetMapping("/books")
    @Operation(summary = "Get address books", description = "Return all address books")
    public List<AddressBookDTO> getAddressBooks() {
        return addressBookService.getAllAddressBooks();
    }

    @PostMapping("/books")
    @Operation(summary = "Create new address book")
    public ResponseEntity<AddressBookDTO> createAddressBook(@Valid @RequestBody final AddressBookRequestDTO requestDTO) {
        final AddressBookDTO addressBook = addressBookService.createAddressBook(requestDTO);
        return created(addressBook);
    }

    @GetMapping("/books/{bookId}/customers")
    @Operation(summary = "Get customers", description = "Get customers from the address book with id")
    public List<CustomerDTO> getCustomer(@PathVariable final Long bookId) {
        return this.addressBookService.getAllCustomerByAddressBookId(bookId);
    }

    @PostMapping("/books/{bookId}/customers")
    @Operation(summary = "Create customer", description = "Create a new customer on the address book id")
    public ResponseEntity<CustomerDTO> createCustomer(@PathVariable final Long bookId, @RequestBody @Valid final CustomerRequestDTO customerRequest) {
        final CustomerDTO customer = this.addressBookService.createCustomer(bookId, customerRequest);
        return created(customer);
    }

    @DeleteMapping("/customers/{customerId}")
    @Operation(summary = "Delete customer", description = "Delete a customer with id.")
    public void removeCustomer(@PathVariable final Long customerId) {
        this.addressBookService.removeCustomer(customerId);
    }

    @GetMapping("/customers")
    @Operation(summary = "Retrieve all unique customers", description = "Retrieve all unique customers regardless of which address book.Unique currently means same name only.")
    public CustomerPageResult getAllCustomers(@RequestParam(defaultValue = "0") final int page, @RequestParam(defaultValue = "20") int pageSize) {
        return this.addressBookService.getAllUniqueCustomers(page, pageSize);
    }

    protected <T> ResponseEntity<T> created(final T body) {
        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }

}
