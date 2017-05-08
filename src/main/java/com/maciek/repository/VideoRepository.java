package com.maciek.repository;

import com.maciek.entity.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by maciej on 05.05.17.
 */
@Repository
public interface VideoRepository extends JpaRepository<Video,Long> {
    List<Video> findByTitleStartsWithIgnoreCase(String filterText);
}
