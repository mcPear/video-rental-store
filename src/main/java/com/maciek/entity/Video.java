package com.maciek.entity;

import lombok.*;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.Size;
import java.util.Set;

/**
 * Created by maciej on 04.05.17.
 */

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Video {

    @Id
    @GeneratedValue
    private Long id;

    private String title;

    private String director;

    private String year;

    @OneToMany(mappedBy = "video", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Rental> rentals;

    public String toString(){
        return "("+id+") "+title+" "+director+" "+year;
    }

}
