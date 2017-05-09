package com.maciek;

import com.maciek.entity.Customer;
import com.maciek.entity.Rental;
import com.maciek.entity.Video;
import com.maciek.repository.CustomerRepository;
import com.maciek.repository.RentalRepository;
import com.maciek.repository.VideoRepository;
import com.maciek.service.DBService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

/**
 * Created by maciej on 08.05.17.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={
        "classpath*:spring-context.xml"
})
@DataJpaTest
public class DBServiceTests {

    @Autowired
    private VideoRepository videoRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private RentalRepository rentalRepository;

    @Autowired
    DBService sut;

    @Test
    public void shouldReturnTrueWhenVideoIsAvailable(){

        Long savedId = videoRepository.save(new Video(null, "title", "director", "1995", null)).getId();
        Assert.assertTrue(sut.videoIsAvailable(savedId));
    }

    @Test
    public void shouldReturnFalseWhenVideoIsNotAvailable(){

        Long savedVideoId = videoRepository.save(new Video(null, "title", "director", "1995", null)).getId();
        Long savedCustomerId = customerRepository.save(new Customer(null, "firstname", "secondname", "email", null)).getId();
        rentalRepository.save(new Rental(null, customerRepository.findOne(savedCustomerId),videoRepository.findOne(savedVideoId), null, false));
        Assert.assertFalse(sut.videoIsAvailable(savedVideoId));
    }

    @Test
    public void shouldReturnFalseWhenVideoDoesNotExist(){

        videoRepository.deleteAll();
        Assert.assertFalse(sut.videoIsAvailable(1l));
    }

    @Test
    public void shouldReturnTrueWhenContainsAvailableVideos(){

        Long savedId1 = videoRepository.save(new Video(null, "title", "director", "1995", null)).getId();
        Long savedId2 = videoRepository.save(new Video(null, "title2", "director2", "1996", null)).getId();
        List<Video> sutAvailableVideosList = sut.findAvailableVideos();
        Video savedVideo1 = videoRepository.findOne(savedId1);
        Video savedVideo2 = videoRepository.findOne(savedId2);
        Assert.assertTrue(sutAvailableVideosList.contains(savedVideo1) && sutAvailableVideosList.contains(savedVideo2));
    }

    @Test
    public void shouldReturnFalseWhenDoesNotContainUnavailableVideos(){

        Long savedRentedVideoId = videoRepository.save(new Video(null, "title", "director", "1995", null)).getId();
        Long savedCustomerId = customerRepository.save(new Customer(null, "firstname", "secondname", "email", null)).getId();
        rentalRepository.save(new Rental(null, customerRepository.findOne(savedCustomerId),videoRepository.findOne(savedRentedVideoId), null, false));
        Video savedRentedVideo = videoRepository.findOne(savedRentedVideoId);
        List<Video> sutAvailableVideosList = sut.findAvailableVideos();
        Assert.assertFalse(sutAvailableVideosList.contains(savedRentedVideo));
    }

    @Test
    public void shouldReturnTrueWhenContainsAvailableVideosStartWithIgnoreCase(){

        Long savedId1 = videoRepository.save(new Video(null, "pIrAteS", "director", "1995", null)).getId();
        Long savedId2 = videoRepository.save(new Video(null, "piratEs", "director2", "1996", null)).getId();
        List<Video> sutAvailableVideosList = sut.findAvailableVideosStartsWithIgnoreCase("pirate");
        Video savedVideo1 = videoRepository.findOne(savedId1);
        Video savedVideo2 = videoRepository.findOne(savedId2);
        Assert.assertTrue(sutAvailableVideosList.contains(savedVideo1) && sutAvailableVideosList.contains(savedVideo2));
    }

    @Test
    public void shouldReturnFalseWhenDoesNotContainAvailableVideosStartWithIgnoreCase(){

        videoRepository.deleteAll();
        Long savedId1 = videoRepository.save(new Video(null, "pIrAteS", "director", "1995", null)).getId();
        Long savedId2 = videoRepository.save(new Video(null, "piratEs", "director2", "1996", null)).getId();
        List<Video> sutAvailableVideosList = sut.findAvailableVideosStartsWithIgnoreCase("irate");
        Video savedVideo1 = videoRepository.findOne(savedId1);
        Video savedVideo2 = videoRepository.findOne(savedId2);
        Assert.assertFalse(sutAvailableVideosList.contains(savedVideo1) && sutAvailableVideosList.contains(savedVideo2));
    }

    @Test
    public void shouldReturnFalseWhenVideoDoesNotExist2(){
        videoRepository.deleteAll();
        Assert.assertFalse(sut.videoExists(1l));
    }

    @Test
    public void shouldReturnTrueWhenVideoExists(){
        Long savedId = videoRepository.save(new Video(null, "title", "director", "1997", null)).getId();
        Assert.assertTrue(sut.videoExists(savedId));
    }

    @Test
    public void shouldReturnFalseWhenCustomerDoesNotExist2(){
        customerRepository.deleteAll();
        Assert.assertFalse(sut.customerExists(1l));
    }

    @Test
    public void shouldReturnTrueWhenCustomerExists(){
        Long savedId = customerRepository.save(new Customer(null, "firstname", "secondname", "email", null)).getId();
        Assert.assertTrue(sut.customerExists(savedId));
    }

    @Test
    public void shouldSaveRental(){
        Video video=new Video(null, "title", "director", "1995", null);
        Customer customer = new Customer(null, "firstname", "secondname", "email", null);
        videoRepository.save(video);
        customerRepository.save(customer);
        rentalRepository.deleteAll();
        sut.saveNewRental(customer, video);
        Assert.assertTrue(rentalRepository.findAll().size()==1);
    }

}
