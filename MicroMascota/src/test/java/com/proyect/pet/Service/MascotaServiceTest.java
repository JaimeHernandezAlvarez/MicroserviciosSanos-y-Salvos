package com.proyect.pet.Service;

import com.proyect.pet.Model.Location;
import com.proyect.pet.Model.Mascota;
import com.proyect.pet.Repository.MascotaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

//import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MascotaServiceTest {

    @Mock
    private MascotaRepository mascotaRepository;

    @InjectMocks
    private MascotaService mascotaService;

    private Mascota mascota;
    private Location ubicacion;

    @BeforeEach
    void setUp() {
        // Preparamos la ubicación
        ubicacion = new Location();
        ubicacion.setLatitude(-33.3828);
        ubicacion.setLongitude(-70.6749);
        ubicacion.setAddress("Conchalí, Chile");

        // Preparamos la mascota
        mascota = new Mascota();
        mascota.setId("pet-123");
        mascota.setName("Firulais");
        mascota.setSpecies("Perro");
        mascota.setBreed("Mestizo");
        mascota.setOwnerId("user-1");
        mascota.setLastLocation(ubicacion);
        // No seteamos reportedAt ni status para probar la lógica de creación por defecto
    }

    @SuppressWarnings("null")
    @Test
    void save_AsignaStatusYFechaPorDefecto() {
        when(mascotaRepository.save(any(Mascota.class))).thenReturn(mascota);

        Mascota resultado = mascotaService.save(mascota);

        // Verificamos que se asignaron los valores por defecto
        assertNotNull(resultado.getReportedAt(), "Debería asignar la fecha de reporte actual");
        assertEquals("LOST", resultado.getStatus(), "El estado inicial debería ser LOST");
        verify(mascotaRepository, times(1)).save(mascota);
    }

    @SuppressWarnings("null")
    @Test
    void update_MascotaExistente_ActualizaCamposYLugar() {
        // Simulamos una mascota con datos nuevos para actualizar
        Mascota datosNuevos = new Mascota();
        datosNuevos.setName("Firulais Actualizado");
        
        Location nuevaUbicacion = new Location();
        nuevaUbicacion.setAddress("Santiago Centro");
        datosNuevos.setLastLocation(nuevaUbicacion);

        when(mascotaRepository.findById("pet-123")).thenReturn(Optional.of(mascota));
        when(mascotaRepository.save(any(Mascota.class))).thenReturn(mascota);

        Mascota resultado = mascotaService.update("pet-123", datosNuevos);

        // Verificamos que los campos se actualizaron
        assertNotNull(resultado);
        assertEquals("Firulais Actualizado", mascota.getName());
        assertEquals("Santiago Centro", mascota.getLastLocation().getAddress(), "La ubicación también debió actualizarse");
    }

    @SuppressWarnings("null")
    @Test
    void updateStatus_AEstadoFound_AsignaFechaYFundador() {
        when(mascotaRepository.findById("pet-123")).thenReturn(Optional.of(mascota));
        when(mascotaRepository.save(any(Mascota.class))).thenReturn(mascota);

        // Simulamos que el usuario "user-2" encontró a la mascota
        Mascota resultado = mascotaService.updateStatus("pet-123", "FOUND", "user-2");

        // Verificamos la lógica específica de negocio
        assertEquals("FOUND", resultado.getStatus());
        assertNotNull(resultado.getFoundAt(), "Debe asignar la fecha de hallazgo");
        assertEquals("user-2", resultado.getFounderId(), "Debe registrar el ID de quien lo encontró");
    }

    @SuppressWarnings("null")
    @Test
    void updateStatus_AEstadoReunited_AsignaFechaDeReencuentro() {
        when(mascotaRepository.findById("pet-123")).thenReturn(Optional.of(mascota));
        when(mascotaRepository.save(any(Mascota.class))).thenReturn(mascota);

        Mascota resultado = mascotaService.updateStatus("pet-123", "REUNITED", null);

        // Verificamos la lógica del reencuentro
        assertEquals("REUNITED", resultado.getStatus());
        assertNotNull(resultado.getReunitedAt(), "Debe asignar la fecha de reencuentro");
    }

    @SuppressWarnings("null")
    @Test
    void delete_CuandoExiste_RetornaTrue() {
        when(mascotaRepository.findById("pet-123")).thenReturn(Optional.of(mascota));

        boolean resultado = mascotaService.delete("pet-123");

        assertTrue(resultado);
        verify(mascotaRepository, times(1)).delete(mascota);
    }
}