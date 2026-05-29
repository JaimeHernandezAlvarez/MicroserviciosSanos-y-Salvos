package com.proyect.pet.Repository;

import com.proyect.pet.Model.Mascota;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MascotaRepository extends MongoRepository<Mascota, String> {
    //Buscar mascotas por estado
    List<Mascota> findByStatus(String status);
    
    //Buscar por dueño
    List<Mascota> findByOwnerId(String ownerId);
    
    //Buscar por especie
    List<Mascota> findBySpecies(String species);
}