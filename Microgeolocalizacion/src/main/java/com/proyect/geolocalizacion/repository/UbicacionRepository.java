package com.proyect.geolocalizacion.repository;

import com.proyect.geolocalizacion.model.Ubicacion;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface UbicacionRepository extends MongoRepository<Ubicacion, String> {
    List<Ubicacion> findByPosicionNear(Point location, Distance distance);
    List<Ubicacion> findByReportId(String reportId);
}