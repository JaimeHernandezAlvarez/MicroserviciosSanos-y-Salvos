package com.proyect.geolocalizacion.service;

import com.proyect.geolocalizacion.model.Ubicacion;
import com.proyect.geolocalizacion.repository.UbicacionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Metrics;
import org.springframework.data.geo.Point;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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

    // 1. Obtener por ID
    @SuppressWarnings("null")
    public Optional<Ubicacion> obtenerPorId(String id) {
        return ubicacionRepository.findById(id);
    }

    // 2. Actualizar Ubicación
    @SuppressWarnings("null")
    public Optional<Ubicacion> actualizarUbicacion(String id, Ubicacion datosNuevos) {
        return ubicacionRepository.findById(id).map(ubicacion -> {
            // Aquí él debe mapear los campos, ej:
            // ubicacion.setLat(datosNuevos.getLat());
            // ubicacion.setLng(datosNuevos.getLng());
            return ubicacionRepository.save(ubicacion);
        });
    }

    // 3. Eliminar
    @SuppressWarnings("null")
    public boolean eliminarUbicacion(String id) {
        return ubicacionRepository.findById(id).map(ubicacion -> {
            ubicacionRepository.delete(ubicacion);
            return true;
        }).orElse(false);
    }
}