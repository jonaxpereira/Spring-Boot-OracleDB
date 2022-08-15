package com.example.oracle.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.example.oracle.entities.Customer;
import com.example.oracle.repositories.CustomerRepository;

@RestController
public class CustomerController {

    @Autowired
    private CustomerRepository customerRepository;

    @GetMapping("/customers")
    public Iterable<Customer> getAll() {
        return customerRepository.findAll();
    }

    @GetMapping(value = "/customers/{id}")
    public Customer findCustomerById(@PathVariable(name = "id") Long id) {
        return customerRepository.findById(id).orElse(null);
    }
    
}
