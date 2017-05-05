package com.maciek.repository;

import com.maciek.entity.Customer;
import com.maciek.entity.Rental;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by maciej on 05.05.17.
 */
public interface RentalRepository extends JpaRepository<Rental,Long> {
}
