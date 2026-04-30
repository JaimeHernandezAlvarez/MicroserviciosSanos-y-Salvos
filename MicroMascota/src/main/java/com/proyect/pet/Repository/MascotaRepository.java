package com.proyect.pet.Repository;

import com.proyect.pet.Model.Mascota;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MascotaRepository extends MongoRepository<Mascota, String> {
    // Buscar mascotas por estado (LOST, FOUND, REUNITED)
    List<Mascota> findByStatus(String status);
    
    // Buscar todas las mascotas de un dueño específico
    List<Mascota> findByOwnerId(String ownerId);
    
    // Buscar por especie (ej: todos los perros)
    List<Mascota> findBySpecies(String species);
}