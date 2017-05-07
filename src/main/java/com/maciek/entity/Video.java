package com.maciek.entity;

import lombok.*;

import javax.persistence.*;
import java.util.Set;

/**
 * Created by maciej on 04.05.17.
 */

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Video {

    @Id
    @GeneratedValue
    private Long id;

    private String title;

    private String director;

    private String year;

    //I'm not sure it's a good idea to put a new attribute to filter rows, but it seems to be more effective than fetching Rentals
    // every time I want to display available Videos
    private Boolean rented;

    @OneToMany(mappedBy = "video", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Rental> rentals;

    public Video(String title, String director, String year, Boolean rented) {
        this.title = title;
        this.director = director;
        this.year = year;
        this.rented = rented;
    }

    public String toString(){
        return "("+id+") "+title+" "+director+" "+year;
    }

}
