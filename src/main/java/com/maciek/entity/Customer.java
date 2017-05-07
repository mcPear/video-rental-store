package com.maciek.entity;

import lombok.*;

import javax.persistence.*;
import java.util.Set;

/**
 * Created by maciej on 04.05.17.
 */

@Setter
@Getter
@NoArgsConstructor
@Entity
public class Customer {

    @Id
    @GeneratedValue
    private Long id;

    private String firstName;

    private String lastName;

    private String emailAddress;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Rental> rentals;

    public Customer(String firstName, String lastName, String emailAddress){
        this.firstName=firstName;
        this.lastName=lastName;
        this.emailAddress=emailAddress;
    }

    public String toString(){
        return "("+id+") "+lastName+" "+firstName;
    }

}
