package com.maciek.service;

import com.maciek.entity.Rental;
import com.maciek.entity.Video;
import com.maciek.repository.CustomerRepository;
import com.maciek.repository.RentalRepository;
import com.maciek.repository.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by maciej on 08.05.17.
 */
@Service
public class DBService {

    private final RentalRepository rentalRepository;
    private final VideoRepository videoRepository;
    private final CustomerRepository customerRepository;

    @Autowired
    public DBService(RentalRepository rentalRepository, VideoRepository videoRepository, CustomerRepository customerRepository) {
        this.rentalRepository = rentalRepository;
        this.videoRepository = videoRepository;
        this.customerRepository = customerRepository;
    }

    public Boolean videoIsAvailable(Long id){
        List<Rental> notReturnedRentals = rentalRepository.findByReturnedFalse();
        boolean result = true;
        for (Rental r:notReturnedRentals){
            if(r.getVideo().getId()==id)
                result=false;
        }

        return result;
    }
}
