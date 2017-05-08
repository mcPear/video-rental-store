package com.maciek.repository;

import com.maciek.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by maciej on 04.05.17.
 */
@Repository
public interface CustomerRepository extends JpaRepository<Customer,Long>{

    List<Customer> findByLastNameStartsWithIgnoreCase(String filterText);

}
