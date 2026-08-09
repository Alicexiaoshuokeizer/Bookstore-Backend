package com.cfg.BookStoreBackend.model.repository;

import com.cfg.BookStoreBackend.model.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

}
