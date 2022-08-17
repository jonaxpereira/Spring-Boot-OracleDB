package com.example.oracle.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.example.oracle.entities.Customer;

@Repository
public interface CustomerRepository extends CrudRepository<Customer, Long> {

}