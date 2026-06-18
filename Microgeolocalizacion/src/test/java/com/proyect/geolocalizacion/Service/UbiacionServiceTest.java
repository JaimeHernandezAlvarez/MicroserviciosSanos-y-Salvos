package com.proyect.geolocalizacion.Service;

import com.proyect.geolocalizacion.model.Ubicacion;
import com.proyect.geolocalizacion.repository.UbicacionRepository;
import com.proyect.geolocalizacion.service.UbicacionService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Metrics;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UbiacionServiceTest {

    @Mock
    private UbicacionRepository ubicacionRepository;

    @InjectMocks
    private UbicacionService ubicacionService;

    private Ubicacion ubicacion;
    private GeoJsonPoint puntoGeo;

    @BeforeEach
    void setUp() {
        // Coordenadas simuladas (Ej: Santiago de Chile)
        puntoGeo = new GeoJsonPoint(-70.6749, -33.3828);

        ubicacion = new Ubicacion();
        ubicacion.setId("geo-123");
        ubicacion.setReportId("report-1");
        ubicacion.setDescripcion("Perrito visto en la plaza");
        ubicacion.setPosicion(puntoGeo);
        // Dejamos la fecha nula para probar tu lógica de creación
    }

    @SuppressWarnings("null")
    @Test
    void guardarUbicacion_AsignaFechaActualYGuarda() {
        when(ubicacionRepository.save(any(Ubicacion.class))).thenReturn(ubicacion);

        Ubicacion resultado = ubicacionService.guardarUbicacion(ubicacion);

        assertNotNull(resultado.getFechaRegistro(), "Debe asignar la fecha de registro automáticamente si es nula");
        assertEquals("Perrito visto en la plaza", resultado.getDescripcion());
        verify(ubicacionRepository, times(1)).save(ubicacion);
    }

    @Test
    void buscarCercanos_ConvierteMetricasYLlamaAlRepo() {
        // Preparamos los datos de entrada
        double longitud = -70.6749;
        double latitud = -33.3828;
        double radioKm = 5.0;

        List<Ubicacion> listaEsperada = Arrays.asList(ubicacion);
        
        // Simulamos la respuesta del repositorio
        when(ubicacionRepository.findByPosicionNear(any(Point.class), any(Distance.class))).thenReturn(listaEsperada);

        List<Ubicacion> resultado = ubicacionService.buscarCercanos(longitud, latitud, radioKm);

        // Verificamos el resultado
        assertFalse(resultado.isEmpty());
        assertEquals(1, resultado.size());
        
        // Magia Negra de Mockito: Capturamos los argumentos exactos que le pasaste al repositorio
        ArgumentCaptor<Point> pointCaptor = ArgumentCaptor.forClass(Point.class);
        ArgumentCaptor<Distance> distanceCaptor = ArgumentCaptor.forClass(Distance.class);
        
        verify(ubicacionRepository).findByPosicionNear(pointCaptor.capture(), distanceCaptor.capture());
        
        // Comprobamos que convertiste el radio a Kilómetros correctamente
        assertEquals(5.0, distanceCaptor.getValue().getValue());
        assertEquals(Metrics.KILOMETERS, distanceCaptor.getValue().getMetric());
        assertEquals(longitud, pointCaptor.getValue().getX());
        assertEquals(latitud, pointCaptor.getValue().getY());
    }

    @Test
    void obtenerPorReporte_RetornaListaDeHistorial() {
        when(ubicacionRepository.findByReportId("report-1")).thenReturn(Arrays.asList(ubicacion));

        List<Ubicacion> resultado = ubicacionService.obtenerPorReporte("report-1");

        assertFalse(resultado.isEmpty());
        assertEquals("geo-123", resultado.get(0).getId());
    }

    @SuppressWarnings("null")
    @Test
    void actualizarUbicacion_CuandoExiste_EjecutaSave() {
        // Simulamos los datos nuevos
        Ubicacion datosNuevos = new Ubicacion();
        datosNuevos.setDescripcion("Nueva descripción");
        
        when(ubicacionRepository.findById("geo-123")).thenReturn(Optional.of(ubicacion));
        when(ubicacionRepository.save(any(Ubicacion.class))).thenReturn(ubicacion);

        Optional<Ubicacion> resultado = ubicacionService.actualizarUbicacion("geo-123", datosNuevos);

        assertTrue(resultado.isPresent());
        verify(ubicacionRepository, times(1)).save(ubicacion);
    }

    @SuppressWarnings("null")
    @Test
    void eliminarUbicacion_CuandoExiste_RetornaTrueYBorra() {
        when(ubicacionRepository.findById("geo-123")).thenReturn(Optional.of(ubicacion));

        boolean resultado = ubicacionService.eliminarUbicacion("geo-123");

        assertTrue(resultado);
        verify(ubicacionRepository, times(1)).delete(ubicacion);
    }
}