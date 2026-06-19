package com.proyect.match.Service;

import com.proyect.match.Dto.MatchActionDTO;
import com.proyect.match.Dto.MatchRequestDTO;
import com.proyect.match.Dto.MatchResponseDTO;
import com.proyect.match.Exceptions.MatchNotFoundException;
import com.proyect.match.Exceptions.MatchAlreadyExistsException;
import com.proyect.match.Model.Match;
import com.proyect.match.Repository.MatchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MatchService {
    
    private final MatchRepository matchRepository;
    
    // Crear un nuevo match
    public MatchResponseDTO createMatch(MatchRequestDTO request) {
        if (matchRepository.existsByLostPetIdAndFoundPetId(request.getLostPetId(), request.getFoundPetId())) {
            throw new MatchAlreadyExistsException("Match already exists between these pets");
        }
        
        Match match = Match.builder()
                .lostPetId(request.getLostPetId())
                .foundPetId(request.getFoundPetId())
                .similarityScore(request.getSimilarityScore())
                .matchReason(request.getMatchReason())
                .status("PENDING")
                .matchedAt(LocalDateTime.now())
                .active(true)
                .build();
        
        @SuppressWarnings("null")
        Match savedMatch = matchRepository.save(match);
        log.info("Match created: {} between {} and {}", savedMatch.getId(), 
                 request.getLostPetId(), request.getFoundPetId());
        
        return convertToResponseDTO(savedMatch);
    }
    
    // Obtener match por ID
    public MatchResponseDTO getMatchById(String matchId) {
        @SuppressWarnings("null")
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new MatchNotFoundException("Match not found with ID: " + matchId));
        return convertToResponseDTO(match);
    }
    
    // Obtener todos los matches de un dueño
    public List<MatchResponseDTO> getMatchesByOwnerId(String ownerId) {
        return matchRepository.findByOwnerId(ownerId).stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }
    
    // Obtener todos los matches de un fundador
    public List<MatchResponseDTO> getMatchesByFounderId(String founderId) {
        return matchRepository.findByFounderId(founderId).stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }
    
    // Obtener matches pendientes para un dueño
    public List<MatchResponseDTO> getPendingMatchesForOwner(String ownerId) {
        return matchRepository.findByOwnerIdAndStatus(ownerId, "PENDING").stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }
    
    // Responder a un match
    public MatchResponseDTO respondToMatch(MatchActionDTO action) {
        @SuppressWarnings("null")
        Match match = matchRepository.findById(action.getMatchId())
                .orElseThrow(() -> new MatchNotFoundException("Match not found"));
        
        if ("ACCEPT".equals(action.getAction())) {
            match.setStatus("ACCEPTED");
            match.setOwnerResponse(action.getMessage());
            log.info("Match accepted: {}", action.getMatchId());
        } else if ("REJECT".equals(action.getAction())) {
            match.setStatus("REJECTED");
            match.setOwnerResponse(action.getMessage());
            log.info("Match rejected: {}", action.getMatchId());
        } else {
            throw new IllegalArgumentException("Invalid action: " + action.getAction());
        }
        
        match.setRespondedAt(LocalDateTime.now());
        Match updatedMatch = matchRepository.save(match);
        
        return convertToResponseDTO(updatedMatch);
    }
    
    // Completar el match
    public MatchResponseDTO completeMatch(String matchId) {
        @SuppressWarnings("null")
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new MatchNotFoundException("Match not found"));
        
        match.setStatus("COMPLETED");
        match.setRespondedAt(LocalDateTime.now());
        
        Match updatedMatch = matchRepository.save(match);
        log.info("Match completed: {}", matchId);
        
        return convertToResponseDTO(updatedMatch);
    }
    
    // Eliminar match
    @SuppressWarnings("null")
    public void deleteMatch(String matchId) {
        if (!matchRepository.existsById(matchId)) {
            throw new MatchNotFoundException("Match not found with ID: " + matchId);
        }
        matchRepository.deleteById(matchId);
        log.info("Match deleted: {}", matchId);
    }
    
    // ✅ MÉTODO AGREGADO - Verificar si existe
    @SuppressWarnings("null")
    public boolean matchExists(String matchId) {
        return matchRepository.existsById(matchId);
    }
    
    // Actualizar ownerId y founderId
    @SuppressWarnings("null")
    public void updatePetOwners(String matchId, String ownerId, String founderId) {
        @SuppressWarnings("null")
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new MatchNotFoundException("Match not found"));
        
        if (ownerId != null) match.setOwnerId(ownerId);
        if (founderId != null) match.setFounderId(founderId);
        
        matchRepository.save(match);
    }
    
    private MatchResponseDTO convertToResponseDTO(Match match) {
        return MatchResponseDTO.builder()
                .id(match.getId())
                .lostPetId(match.getLostPetId())
                .foundPetId(match.getFoundPetId())
                .ownerId(match.getOwnerId())
                .founderId(match.getFounderId())
                .status(match.getStatus())
                .similarityScore(match.getSimilarityScore())
                .matchReason(match.getMatchReason())
                .matchedAt(match.getMatchedAt())
                .respondedAt(match.getRespondedAt())
                .build();
    }
}