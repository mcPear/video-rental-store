package com.maciek.repository;

import com.maciek.entity.Rental;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by maciej on 05.05.17.
 */
@Repository
public interface RentalRepository extends JpaRepository<Rental,Long> {
    List<Rental> findByReturnedFalse();
    List<Rental> findByReturnedFalseAndCustomerLastNameStartsWithIgnoreCase(String filterText);
    List<Rental> findByReturnedTrue();
    List<Rental> findByReturnedTrueAndCustomerLastNameStartsWithIgnoreCase(String filterText);
}
