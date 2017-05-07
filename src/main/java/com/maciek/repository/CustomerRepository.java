package com.maciek.repository;

import com.maciek.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by maciej on 04.05.17.
 */
public interface CustomerRepository extends JpaRepository<Customer,Long>{

    List<Customer> findByLastNameStartsWithIgnoreCase(String filterText);

}
