package com.interview.reece.addressbook.exception;

public class AddressBookNotFoundException extends AddressBookRuntimeException {
    public AddressBookNotFoundException(String message) {
        super(message);
    }
}
