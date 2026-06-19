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
    private String reportId; 
    private String descripcion;
    private LocalDateTime fechaRegistro;
    //Utiliza geoJson
    @GeoSpatialIndexed(type = org.springframework.data.mongodb.core.index.GeoSpatialIndexType.GEO_2DSPHERE)
    private GeoJsonPoint posicion;
}