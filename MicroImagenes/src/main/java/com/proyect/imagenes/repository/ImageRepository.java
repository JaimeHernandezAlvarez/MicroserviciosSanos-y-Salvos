package com.proyect.imagenes.repository;

import com.proyect.imagenes.model.Image;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ImageRepository extends MongoRepository<Image, String> {
    
    Optional<Image> findByImageId(String imageId);
    
    boolean existsByImageId(String imageId);
    
    void deleteByImageId(String imageId);
}