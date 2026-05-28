package com.proyect.geolocalizacion.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Document(collection = "ubicaciones")
public class Ubicacion {

    @Id
    private String id;
    
    // Conecta con el ID del reporte en 'pet-service' [cite: 59, 60]
    private String reportId; 
    
    // Descripción opcional (ej: "Visto cerca de la plaza")
    private String descripcion;

    // Fecha y hora exacta del avistamiento
    private LocalDateTime fechaRegistro;

    /*
     * Almacena coordenadas en formato GeoJSON [Longitud, Latitud].
     * Crea automáticamente el índice '2dsphere' para consultas de proximidad[cite: 59].
     */
    @GeoSpatialIndexed(type = org.springframework.data.mongodb.core.index.GeoSpatialIndexType.GEO_2DSPHERE)
    private GeoJsonPoint posicion;
}