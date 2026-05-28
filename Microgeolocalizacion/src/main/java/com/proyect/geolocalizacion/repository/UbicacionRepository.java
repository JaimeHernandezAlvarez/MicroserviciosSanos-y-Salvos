package com.proyect.geolocalizacion.repository;

import com.proyect.geolocalizacion.model.Ubicacion;
import org.springframework.data.domain.Distance;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface UbicacionRepository extends MongoRepository<Ubicacion, String> {
    
    // Busca avistamientos de mascotas que estén cerca de un punto específico (dentro de una distancia máxima) 
    List<Ubicacion> findByPosicionNear(Point location, Distance distance);
    
    // Busca todas las ubicaciones o avistamientos reportados para una misma mascota [cite: 60]
    List<Ubicacion> findByReportId(String reportId);
}