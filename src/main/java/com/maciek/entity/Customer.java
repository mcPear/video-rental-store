package com.maciek.entity;

import lombok.*;

import javax.persistence.*;
import java.util.Set;

/**
 * Created by maciej on 04.05.17.
 */

@Setter
@Getter
@AllArgsConstructor
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

    public String toString(){
        return "("+id+") "+lastName+" "+firstName;
    }

}
