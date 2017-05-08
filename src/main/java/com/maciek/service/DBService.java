package com.maciek.service;

import com.maciek.entity.Customer;
import com.maciek.entity.Rental;
import com.maciek.entity.Video;
import com.maciek.model.RentalModel;
import com.maciek.repository.CustomerRepository;
import com.maciek.repository.RentalRepository;
import com.maciek.repository.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.sql.Date;
import java.util.List;
import java.util.stream.Collectors;

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

    public List<Video> findAvailableVideos() {
        List<Rental> notReturnedRentals = rentalRepository.findByReturnedFalse();
        List<Video> unavailableVideos = notReturnedRentals.stream().map(Rental::getVideo).collect(Collectors.toList());
            List<Video> availableVideos = videoRepository.findAll();
            for(Video uv:unavailableVideos){
                availableVideos = availableVideos.stream().filter(v -> v.getId()!=uv.getId()).collect(Collectors.toList());
            }
        return  availableVideos;
    }

    public List<Video> findAvailableVideosStartsWithIgnoreCase(String phrase) {
        List<Rental> notReturnedRentals = rentalRepository.findByReturnedFalse();
        List<Video> unavailableVideos = notReturnedRentals.stream().map(Rental::getVideo).collect(Collectors.toList());
        List<Video> availableVideos = videoRepository.findByTitleStartsWithIgnoreCase(phrase);
        for(Video uv:unavailableVideos){
            availableVideos = availableVideos.stream().filter(v -> v.getId()!=uv.getId()).collect(Collectors.toList());
        }
        return  availableVideos;
    }

    public Boolean customerExists(Long id){
        return customerRepository.findOne(id)!=null;
    }

    public Boolean videoExists(Long id){
        return  videoRepository.findOne(id)!=null;
    }

    public Rental constructRental(RentalModel rentalModel){
        Customer customer = customerRepository.findOne(Long.parseLong(rentalModel.getCustomerId()));
        Video video = videoRepository.findOne(Long.parseLong(rentalModel.getVideoId()));
        Date date = rentalModel.getDate();
        Boolean returned = rentalModel.getReturned();
        return new Rental(customer, video, date, returned);
    }

}
