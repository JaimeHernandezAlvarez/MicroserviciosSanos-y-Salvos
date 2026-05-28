package com.proyect.geolocalizacion.service;

import com.proyect.geolocalizacion.model.Ubicacion;
import com.proyect.geolocalizacion.repository.UbicacionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Distance;
import org.springframework.data.geo.Metrics;
import org.springframework.data.geo.Point;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UbicacionService {

    @Autowired
    private UbicacionRepository ubicacionRepository;

    // Registrar un nuevo avistamiento o ubicación de mascota
    public Ubicacion guardarUbicacion(Ubicacion ubicacion) {
        if (ubicacion.getFechaRegistro() == null) {
            ubicacion.setFechaRegistro(LocalDateTime.now());
        }
        return ubicacionRepository.save(ubicacion);
    }

    // Obtener el historial de puntos de una misma mascota
    public List<Ubicacion> obtenerPorReporte(String reportId) {
        return ubicacionRepository.findByReportId(reportId);
    }

    // Buscar avistamientos cercanos en un radio de kilómetros (ideal para el mapa)
    public List<Ubicacion> buscarCercanos(double longitud, double latitud, double radioKilometros) {
        Point punto = new Point(longitud, latitud);
        // Convertimos el radio de kilómetros a la métrica que entiende Spring Data
        Distance distancia = new Distance(radioKilometros, Metrics.KILOMETERS);
        return ubicacionRepository.findByPosicionNear(punto, distancia);
    }

    // Obtener absolutamente todas las ubicaciones (útil para armar el mapa de calor general)
    public List<Ubicacion> obtenerTodas() {
        return ubicacionRepository.findAll();
    }
}