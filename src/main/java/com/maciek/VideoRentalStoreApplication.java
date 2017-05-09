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

	@Autowired
	CustomerRepository customerRepository;

	@Autowired
	VideoRepository videoRepository;

	@Autowired
	RentalRepository rentalRepository;

	@Bean
	public CommandLineRunner loadSamples() {
		return (args) -> {
			customerRepository.save(new Customer(null,"Maciek","Gruszczyński","maciek.gruszka@gmail.com", null));
			customerRepository.save(new Customer(null, "Adam","Nowak","adam.nowak@gmail.com", null));
			videoRepository.save(new Video(null,"Pirates of The Caribbean 5","Dontknow","2017", null));
			videoRepository.save(new Video(null, "Fast and furious 12","Spielberg","2012", null));
			rentalRepository.save(new Rental(null, customerRepository.findOne(2l),videoRepository.findOne(1l), new Date(new java.util.Date().getTime()),false));
		};
	}


}
