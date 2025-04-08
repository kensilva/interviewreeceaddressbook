package com.interview.reece.addressbook.exception;

public class CustomerNotFoundException extends AddressBookRuntimeException{
    public CustomerNotFoundException(String message) {
        super(message);
    }
}
