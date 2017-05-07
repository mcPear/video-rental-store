package com.maciek.repository;

import com.maciek.entity.Video;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by maciej on 05.05.17.
 */
public interface VideoRepository extends JpaRepository<Video,Long> {
    List<Video> findByRentedFalse();
    List<Video> findByRentedFalseAndTitleStartsWithIgnoreCase(String filterText);
}
