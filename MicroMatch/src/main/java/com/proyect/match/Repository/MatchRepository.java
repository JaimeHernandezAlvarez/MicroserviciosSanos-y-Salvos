package com.proyect.match.Repository;

import com.proyect.match.Model.Match;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MatchRepository extends MongoRepository<Match, String> {
    
    List<Match> findByLostPetId(String lostPetId);
    
    List<Match> findByFoundPetId(String foundPetId);
    
    List<Match> findByOwnerId(String ownerId);
    
    List<Match> findByFounderId(String founderId);
    
    List<Match> findByOwnerIdAndStatus(String ownerId, String status);
    
    List<Match> findByFounderIdAndStatus(String founderId, String status);
    
    Optional<Match> findByLostPetIdAndFoundPetId(String lostPetId, String foundPetId);
    
    List<Match> findByStatus(String status);
    
    boolean existsByLostPetIdAndFoundPetId(String lostPetId, String foundPetId);
}