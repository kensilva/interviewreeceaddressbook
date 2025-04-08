package com.interview.reece.addressbook.model;

import jakarta.persistence.*;

import java.util.Set;


@Entity
@Table(name = "customers")
public class Customer extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @ElementCollection
    @CollectionTable(name = "customer_phone_numbers")
    @Column(length = 50)
    private Set<String> phoneNumbers;

    @ManyToOne
    @JoinColumn(name = "address_book_id", nullable = false)
    private AddressBook addressBook;

    protected Customer() {
    }

    public Customer(String name) {
        this.name = name;
    }

    public Customer(String name, Set<String> phoneNumbers) {
        this.name = name;
        this.phoneNumbers = phoneNumbers;
    }

    public Customer(String name, Set<String> phoneNumbers, AddressBook addressBook) {
        this.name = name;
        this.phoneNumbers = phoneNumbers;
        this.addressBook = addressBook;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<String> getPhoneNumbers() {
        return phoneNumbers;
    }

    public void setPhoneNumbers(Set<String> phoneNumbers) {
        this.phoneNumbers = phoneNumbers;
    }

    public AddressBook getAddressBook() {
        return addressBook;
    }

    public void setAddressBook(AddressBook addressBook) {
        this.addressBook = addressBook;
    }
}
