package com.proyect.imagenes.repository;

import com.proyect.imagenes.model.Image;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ImageRepository extends MongoRepository<Image, String> {
    
    Optional<Image> findByImageId(String imageId);
    
    boolean existsByImageId(String imageId);
    
    void deleteByImageId(String imageId);
    
    List<Image> findByUserId(String userId);
    
    List<Image> findByUserEmail(String userEmail);
    
    List<Image> findByPetId(String petId);
    
    List<Image> findByUserIdAndCategory(String userId, String category);
    
    List<Image> findByUserIdAndActiveTrue(String userId);
    
    long countByUserId(String userId);
    
    long countByPetId(String petId);
    
    void deleteByUserId(String userId);
    
    void deleteByPetId(String petId);
    
    @Query("{ 'userId': ?0, 'category': ?1, 'active': true }")
    List<Image> findActiveImagesByUserAndCategory(String userId, String category);
}