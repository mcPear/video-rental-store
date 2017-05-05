package com.maciek.repository;

import com.maciek.entity.Customer;
import com.maciek.entity.Video;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by maciej on 05.05.17.
 */
public interface VideoRepository extends JpaRepository<Video,Long> {
}
