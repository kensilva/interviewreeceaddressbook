package com.interview.reece.addressbook.dto;

import org.springframework.data.domain.Page;

public class CustomerPageResult extends PagedResult<CustomerDTO> {

    public CustomerPageResult() {
        super(0, 0);
    }

    public CustomerPageResult(int page, int pageSize) {
        super(page, pageSize);
    }

    public CustomerPageResult(int page, int pageSize, Page pageResult) {
        super(page, pageSize, pageResult);
    }
}
