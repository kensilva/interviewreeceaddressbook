package com.interview.reece.addressbook.repository;

import com.interview.reece.addressbook.aggregate.MergeCustomer;
import com.interview.reece.addressbook.model.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.stereotype.Repository;


@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    /**
     * Returns a list of distinct customers with same name and merge the phone numbers
     *
     * @return list of customers
     */
    @NativeQuery(value = "SELECT c.name, ARRAY_AGG(p.PHONE_NUMBERS) as phoneNumbers  FROM CUSTOMERS as c JOIN customer_phone_numbers as p ON c.pk=p.customer_pk group by name",
            countQuery = "SELECT count(c.name) FROM CUSTOMERS as c JOIN customer_phone_numbers as p ON c.pk=p.customer_pk group by name")
    Page<MergeCustomer> findDistinctName(final Pageable pageable);
}
