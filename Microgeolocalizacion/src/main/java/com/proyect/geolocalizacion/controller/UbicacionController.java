package com.proyect.geolocalizacion.controller;

import com.proyect.geolocalizacion.model.Ubicacion;
import com.proyect.geolocalizacion.service.UbicacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/geolocalizacion")
public class UbicacionController {

    @Autowired
    private UbicacionService ubicacionService;

    // 1. Endpoint para registrar un nuevo avistamiento/ubicación
    // POST http://localhost:8082/api/geolocalizacion
    @PostMapping
    public ResponseEntity<Ubicacion> registrarUbicacion(@RequestBody Ubicacion ubicacion) {
        Ubicacion nuevaUbicacion = ubicacionService.guardarUbicacion(ubicacion);
        return new ResponseEntity<>(nuevaUbicacion, HttpStatus.CREATED);
    }

    // 2. Endpoint para obtener el historial de ubicaciones de una mascota específica
    // GET http://localhost:8082/api/geolocalizacion/reporte/{reportId}
    @getMapping("/reporte/{reportId}")
    public ResponseEntity<List<Ubicacion>> obtenerPorReporte(@PathVariable String reportId) {
        List<Ubicacion> historial = ubicacionService.obtenerPorReporte(reportId);
        return ResponseEntity.ok(historial);
    }

    // 3. Endpoint para buscar avistamientos cercanos (Ideal para el radar o mapa del Frontend)
    // GET http://localhost:8082/api/geolocalizacion/cercanos?lng=-70.648&lat=-33.456&radio=5.0
    @getMapping("/cercanos")
    public ResponseEntity<List<Ubicacion>> buscarCercanos(
            @RequestParam double lng,
            @RequestParam double lat,
            @RequestParam(defaultValue = "5.0") double radio) {
        List<Ubicacion> cercanas = ubicacionService.buscarCercanos(lng, lat, radio);
        return ResponseEntity.ok(cercanas);
    }

    // 4. Endpoint para traer todo (Generación del Heatmap / Mapa de calor)
    // GET http://localhost:8082/api/geolocalizacion/heatmap
    @getMapping("/heatmap")
    public ResponseEntity<List<Ubicacion>> obtenerHeatmap() {
        List<Ubicacion> todas = ubicacionService.obtenerTodas();
        return ResponseEntity.ok(todas);
    }
}