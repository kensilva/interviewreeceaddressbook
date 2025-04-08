package com.interview.reece.addressbook.dto;


public class AddressBookDTO extends BaseDTO {

    private String title;


    public AddressBookDTO() {
        super();
    }

    public AddressBookDTO(long id, String title) {
        super(id);
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

}
