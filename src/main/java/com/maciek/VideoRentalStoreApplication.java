package com.maciek;

import com.maciek.entity.Customer;
import com.maciek.entity.Rental;
import com.maciek.entity.Video;
import com.maciek.repository.CustomerRepository;
import com.maciek.repository.RentalRepository;
import com.maciek.repository.VideoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.sql.Date;

@SpringBootApplication
public class VideoRentalStoreApplication {

	public static void main(String[] args) {
		SpringApplication.run(VideoRentalStoreApplication.class, args);
	}

	private static final Logger log = LoggerFactory.getLogger(VideoRentalStoreApplication.class);

	@Autowired
	CustomerRepository customerRepository;

	@Autowired
	VideoRepository videoRepository;

	@Autowired
	RentalRepository rentalRepository;

	@Bean
	public CommandLineRunner loadData() {
		return (args) -> {
			customerRepository.save(new Customer("Maciek","Gru","email1"));
			customerRepository.save(new Customer("Adam","Szu","email2"));
			videoRepository.save(new Video("lotr","Spielberg",2016));
			videoRepository.save(new Video("hobbit","Rafacinski",2012));
			rentalRepository.save(new Rental(customerRepository.findOne(2l),videoRepository.findOne(1l), new Date(new java.util.Date().getTime())));
			customerRepository.delete(2l);
		};
	}


}
