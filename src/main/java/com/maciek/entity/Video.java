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
@AllArgsConstructor
@ToString
@Entity
public class Video {

    @Id
    @GeneratedValue
    private Long id;

    private String title;

    private String director;

    private Integer year;

    @OneToMany(mappedBy = "video", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Rental> rentals;

    public Video(String title, String director, Integer year) {
        this.title = title;
        this.director = director;
        this.year = year;
    }

    //private Integer copyNumber;


}
