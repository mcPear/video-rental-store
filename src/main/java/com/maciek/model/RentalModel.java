package com.maciek.model;

import lombok.*;
import java.sql.Date;

/**
 * Created by maciej on 06.05.17.
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class RentalModel {

    private String customerId;

    private String videoId;

    private Date date;

    private Boolean returned;

}
