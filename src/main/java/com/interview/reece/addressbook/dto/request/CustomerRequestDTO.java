package com.interview.reece.addressbook.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.Set;

public class CustomerRequestDTO {

    @NotEmpty(message = "name is required.")
    @NotBlank(message = "name cannot be blank.")
    @Size(min = 1, max = 50, message = "name must have length between 1 to 50")
    private String name;

    private Set<@NotEmpty String> phoneNumbers;


    public CustomerRequestDTO() {
    }

    public CustomerRequestDTO(String name, Set<@NotEmpty String> phoneNumbers) {
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
