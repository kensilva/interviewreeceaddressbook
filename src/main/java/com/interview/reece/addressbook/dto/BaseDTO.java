package com.interview.reece.addressbook.dto;

public abstract class BaseDTO {

    private long id;

    protected BaseDTO() {
    }

    protected BaseDTO(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
