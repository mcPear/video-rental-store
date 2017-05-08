package com.maciek.entity;

import lombok.*;

import javax.persistence.*;
import java.sql.Date;

/**
 * Created by maciej on 05.05.17.
 */

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
public class Rental {

    @Id
    @GeneratedValue
    private Long id;


    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "video_id")
    private Video video;

    private Date date;

    private Boolean returned;

}
