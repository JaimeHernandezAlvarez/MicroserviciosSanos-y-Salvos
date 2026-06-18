package com.proyect.match.Service;

import com.proyect.match.Dto.MatchActionDTO;
import com.proyect.match.Dto.MatchRequestDTO;
import com.proyect.match.Dto.MatchResponseDTO;
import com.proyect.match.Exceptions.MatchAlreadyExistsException;
import com.proyect.match.Exceptions.MatchNotFoundException;
import com.proyect.match.Model.Match;
import com.proyect.match.Repository.MatchRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MatchServiceTest {

    @Mock
    private MatchRepository matchRepository;

    @InjectMocks
    private MatchService matchService;

    private Match match;

    @BeforeEach
    void setUp() {
        // Aprovechamos tu @Builder del modelo Match
        match = Match.builder()
                .id("match-123")
                .lostPetId("lost-1")
                .foundPetId("found-1")
                .ownerId("owner-1")
                .founderId("founder-1")
                .status("PENDING")
                .similarityScore(85.5)
                .matchReason("Ambos son poodles blancos")
                .matchedAt(LocalDateTime.now())
                .active(true)
                .build();
    }

    @SuppressWarnings("null")
    @Test
    void createMatch_Exitoso_RetornaResponseDTO() {
        // 1. Mockeamos el DTO de entrada para no depender de su constructor
        MatchRequestDTO requestMock = mock(MatchRequestDTO.class);
        when(requestMock.getLostPetId()).thenReturn("lost-1");
        when(requestMock.getFoundPetId()).thenReturn("found-1");
        when(requestMock.getSimilarityScore()).thenReturn(85.5);
        when(requestMock.getMatchReason()).thenReturn("Ambos son poodles blancos");

        // 2. Simulamos que NO existe un match previo
        when(matchRepository.existsByLostPetIdAndFoundPetId("lost-1", "found-1")).thenReturn(false);
        // Simulamos el guardado
        when(matchRepository.save(any(Match.class))).thenReturn(match);

        // 3. Ejecutamos
        MatchResponseDTO resultado = matchService.createMatch(requestMock);

        // 4. Verificamos
        assertNotNull(resultado);
        assertEquals("match-123", resultado.getId());
        assertEquals("PENDING", resultado.getStatus());
        verify(matchRepository, times(1)).save(any(Match.class));
    }

    @SuppressWarnings("null")
    @Test
    void createMatch_CuandoYaExiste_LanzaExcepcion() {
        MatchRequestDTO requestMock = mock(MatchRequestDTO.class);
        when(requestMock.getLostPetId()).thenReturn("lost-1");
        when(requestMock.getFoundPetId()).thenReturn("found-1");

        // Simulamos que YA existe un match entre estas dos mascotas
        when(matchRepository.existsByLostPetIdAndFoundPetId("lost-1", "found-1")).thenReturn(true);

        // Verificamos que lance la excepción correcta
        assertThrows(MatchAlreadyExistsException.class, () -> matchService.createMatch(requestMock));
        
        // Verificamos que NUNCA intentó guardar nada
        verify(matchRepository, never()).save(any(Match.class));
    }

    @Test
    void getMatchById_CuandoExiste_RetornaDTO() {
        when(matchRepository.findById("match-123")).thenReturn(Optional.of(match));

        MatchResponseDTO resultado = matchService.getMatchById("match-123");

        assertNotNull(resultado);
        assertEquals("match-123", resultado.getId());
    }

    @Test
    void getMatchById_CuandoNoExiste_LanzaExcepcion() {
        when(matchRepository.findById("match-999")).thenReturn(Optional.empty());

        assertThrows(MatchNotFoundException.class, () -> matchService.getMatchById("match-999"));
    }

    @SuppressWarnings("null")
    @Test
    void respondToMatch_AceptarMatch_CambiaEstadoAAccepted() {
        MatchActionDTO actionMock = mock(MatchActionDTO.class);
        when(actionMock.getMatchId()).thenReturn("match-123");
        when(actionMock.getAction()).thenReturn("ACCEPT");
        when(actionMock.getMessage()).thenReturn("¡Es mi perrito!");

        when(matchRepository.findById("match-123")).thenReturn(Optional.of(match));
        when(matchRepository.save(any(Match.class))).thenReturn(match);

        @SuppressWarnings("unused")
        MatchResponseDTO resultado = matchService.respondToMatch(actionMock);

        // Verificamos que el estado interno cambió correctamente
        assertEquals("ACCEPTED", match.getStatus(), "El estado debió cambiar a ACCEPTED");
        assertEquals("¡Es mi perrito!", match.getOwnerResponse());
        assertNotNull(match.getRespondedAt(), "Debe registrar la fecha de respuesta");
        verify(matchRepository, times(1)).save(match);
    }

    @SuppressWarnings("null")
    @Test
    void respondToMatch_AccionInvalida_LanzaIllegalArgumentException() {
        MatchActionDTO actionMock = mock(MatchActionDTO.class);
        when(actionMock.getMatchId()).thenReturn("match-123");
        when(actionMock.getAction()).thenReturn("ACCION_RARA");

        when(matchRepository.findById("match-123")).thenReturn(Optional.of(match));

        assertThrows(IllegalArgumentException.class, () -> matchService.respondToMatch(actionMock));
        verify(matchRepository, never()).save(any(Match.class));
    }
}