package com.image.demo.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.image.demo.entity.Image;

@Repository
public interface ImageRepo extends JpaRepository<Image, Long>{

}
