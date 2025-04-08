package com.interview.reece.addressbook.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class AddressBookRequestDTO {

    @NotBlank(message = "Title cannot be empty.")
    @Size(min = 1, max = 100, message = "Invalid title. Must have at least length of 1 to 100")
    private String title;

    public AddressBookRequestDTO() {
    }

    public AddressBookRequestDTO(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
