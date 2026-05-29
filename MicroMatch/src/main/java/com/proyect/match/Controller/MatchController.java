package com.proyect.match.Controller;

import com.proyect.match.Assembler.MatchAssembler;
import com.proyect.match.Dto.MatchActionDTO;
import com.proyect.match.Dto.MatchRequestDTO;
import com.proyect.match.Dto.MatchResponseDTO;
import com.proyect.match.Service.MatchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/api/matches")
@RequiredArgsConstructor
@Tag(name = "Match Management", description = "Endpoints for managing matches between lost and found pets")
public class MatchController {
    
    private final MatchService matchService;
    private final MatchAssembler matchAssembler;
    
    @PostMapping
    @Operation(summary = "Create a new match between lost and found pet")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Match created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "409", description = "Match already exists")
    })
    public ResponseEntity<EntityModel<MatchResponseDTO>> createMatch(@Valid @RequestBody MatchRequestDTO request) {
        MatchResponseDTO response = matchService.createMatch(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(matchAssembler.toModel(response));
    }
    
    @GetMapping("/{matchId}")
    @Operation(summary = "Get match by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Match found"),
        @ApiResponse(responseCode = "404", description = "Match not found")
    })
    public ResponseEntity<EntityModel<MatchResponseDTO>> getMatchById(@PathVariable String matchId) {
        MatchResponseDTO match = matchService.getMatchById(matchId);
        return ResponseEntity.ok(matchAssembler.toModel(match));
    }
    
    @SuppressWarnings("null")
    @GetMapping("/owner/{ownerId}")
    @Operation(summary = "Get all matches for an owner")
    public ResponseEntity<CollectionModel<EntityModel<MatchResponseDTO>>> getMatchesByOwnerId(@PathVariable String ownerId) {
        List<EntityModel<MatchResponseDTO>> matches = matchService.getMatchesByOwnerId(ownerId)
                .stream()
                .map(matchAssembler::toModel)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(CollectionModel.of(matches,
                linkTo(methodOn(MatchController.class).getMatchesByOwnerId(ownerId)).withSelfRel()));
    }
    
    @SuppressWarnings("null")
    @GetMapping("/founder/{founderId}")
    @Operation(summary = "Get all matches for a founder")
    public ResponseEntity<CollectionModel<EntityModel<MatchResponseDTO>>> getMatchesByFounderId(@PathVariable String founderId) {
        List<EntityModel<MatchResponseDTO>> matches = matchService.getMatchesByFounderId(founderId)
                .stream()
                .map(matchAssembler::toModel)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(CollectionModel.of(matches,
                linkTo(methodOn(MatchController.class).getMatchesByFounderId(founderId)).withSelfRel()));
    }
    
    @SuppressWarnings("null")
    @GetMapping("/owner/{ownerId}/pending")
    @Operation(summary = "Get pending matches for an owner")
    public ResponseEntity<CollectionModel<EntityModel<MatchResponseDTO>>> getPendingMatchesForOwner(@PathVariable String ownerId) {
        List<EntityModel<MatchResponseDTO>> matches = matchService.getPendingMatchesForOwner(ownerId)
                .stream()
                .map(matchAssembler::toModel)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(CollectionModel.of(matches,
                linkTo(methodOn(MatchController.class).getPendingMatchesForOwner(ownerId)).withSelfRel()));
    }
    
    @PutMapping("/respond")
    @Operation(summary = "Respond to a match (ACCEPT/REJECT)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Response recorded"),
        @ApiResponse(responseCode = "404", description = "Match not found"),
        @ApiResponse(responseCode = "400", description = "Invalid action")
    })
    public ResponseEntity<EntityModel<MatchResponseDTO>> respondToMatch(@Valid @RequestBody MatchActionDTO action) {
        MatchResponseDTO response = matchService.respondToMatch(action);
        return ResponseEntity.ok(matchAssembler.toModel(response));
    }
    
    @PutMapping("/{matchId}/complete")
    @Operation(summary = "Complete a match (pet reunited)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Match completed"),
        @ApiResponse(responseCode = "404", description = "Match not found")
    })
    public ResponseEntity<EntityModel<MatchResponseDTO>> completeMatch(@PathVariable String matchId) {
        MatchResponseDTO response = matchService.completeMatch(matchId);
        return ResponseEntity.ok(matchAssembler.toModel(response));
    }
    
    @DeleteMapping("/{matchId}")
    @Operation(summary = "Delete a match")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Match deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Match not found")
    })
    public ResponseEntity<Void> deleteMatch(@PathVariable String matchId) {
        matchService.deleteMatch(matchId);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/{matchId}/exists")
    @Operation(summary = "Check if match exists")
    public ResponseEntity<Boolean> checkMatchExists(@PathVariable String matchId) {
        return ResponseEntity.ok(matchService.matchExists(matchId));
    }
}