package com.interview.reece.addressbook.dto;

import java.util.Set;

public class CustomerDTO extends BaseDTO {

    private String name;

    private Set<String> phoneNumbers;

    public CustomerDTO() {
    }

    public CustomerDTO(long id, String name,Set<String> phoneNumbers) {
        super(id);
        this.name = name;
        this.phoneNumbers = phoneNumbers;
    }

    public CustomerDTO(String name, Set<String> phoneNumbers) {
        this.name = name;
        this.phoneNumbers = phoneNumbers;
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
}
