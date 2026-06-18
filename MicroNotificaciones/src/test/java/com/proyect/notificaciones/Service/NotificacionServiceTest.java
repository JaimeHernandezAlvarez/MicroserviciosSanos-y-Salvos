package com.proyect.notificaciones.Service;

import com.proyect.notification.Model.Notificacion;
import com.proyect.notification.Repository.NotificacionRepository;
import com.proyect.notification.Service.NotificacionService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NotificacionServiceTest {

    @Mock
    private NotificacionRepository notificacionRepository;

    @InjectMocks
    private NotificacionService notificacionService;

    private Notificacion notificacion;

    @BeforeEach
    void setUp() {
        notificacion = new Notificacion();
        notificacion.setId("noti-123");
        notificacion.setUserId("user-1");
        notificacion.setTitle("Nueva Mascota");
        notificacion.setMessage("Se ha perdido un perrito cerca de ti");
        // No le seteamos createdAt ni isRead para probar la lógica de creación
    }

    @SuppressWarnings("null")
    @Test
    void crearNotificacion_AsignaFechaYMarcaComoNoLeida() {
        // Simulamos que el repo nos devuelve la misma notificación al guardar
        when(notificacionRepository.save(any(Notificacion.class))).thenReturn(notificacion);

        Notificacion resultado = notificacionService.crearNotificacion(notificacion);

        // Verificamos la lógica de negocio
        assertNotNull(resultado.getCreatedAt(), "La fecha de creación debió asignarse automáticamente");
        assertFalse(resultado.isRead(), "La notificación debe nacer como 'no leída' (isRead = false)");
        
        // Verificamos que se llamó a save
        verify(notificacionRepository, times(1)).save(notificacion);
    }

    @Test
    void obtenerNotificacionesPorUsuario_RetornaLista() {
        List<Notificacion> listaEsperada = Arrays.asList(notificacion);
        when(notificacionRepository.findByUserIdOrderByCreatedAtDesc("user-1")).thenReturn(listaEsperada);

        List<Notificacion> resultado = notificacionService.obtenerNotificacionesPorUsuario("user-1");

        assertFalse(resultado.isEmpty());
        assertEquals(1, resultado.size());
        assertEquals("user-1", resultado.get(0).getUserId());
    }

    @SuppressWarnings("null")
    @Test
    void marcarComoLeida_CuandoExiste_CambiaEstadoYGuarda() {
        // Preparamos la notificación como 'no leída'
        notificacion.setRead(false);
        
        // Simulamos que la encuentra en la BD
        when(notificacionRepository.findById("noti-123")).thenReturn(Optional.of(notificacion));
        // Simulamos el guardado
        when(notificacionRepository.save(any(Notificacion.class))).thenReturn(notificacion);

        Optional<Notificacion> resultado = notificacionService.marcarComoLeida("noti-123");

        assertTrue(resultado.isPresent());
        assertTrue(resultado.get().isRead(), "La notificación debió cambiar a 'leída'");
        verify(notificacionRepository, times(1)).save(notificacion);
    }

    @SuppressWarnings("null")
    @Test
    void marcarComoLeida_CuandoNoExiste_RetornaEmpty() {
        when(notificacionRepository.findById("noti-999")).thenReturn(Optional.empty());

        Optional<Notificacion> resultado = notificacionService.marcarComoLeida("noti-999");

        assertFalse(resultado.isPresent(), "No debería devolver nada si el ID no existe");
        verify(notificacionRepository, never()).save(any(Notificacion.class));
    }

    @SuppressWarnings("null")
    @Test
    void eliminarNotificacion_CuandoExiste_RetornaTrue() {
        when(notificacionRepository.findById("noti-123")).thenReturn(Optional.of(notificacion));

        boolean resultado = notificacionService.eliminarNotificacion("noti-123");

        assertTrue(resultado);
        verify(notificacionRepository, times(1)).delete(notificacion);
    }
}