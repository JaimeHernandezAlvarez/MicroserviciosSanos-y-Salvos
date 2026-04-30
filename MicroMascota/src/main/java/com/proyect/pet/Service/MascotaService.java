package com.proyect.pet.Service;

import com.proyect.pet.Model.Mascota;
import com.proyect.pet.Repository.MascotaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@SuppressWarnings("null")
public class MascotaService {

    @Autowired
    private MascotaRepository mascotaRepository;

    // 1. Crear o reportar una mascota (Por defecto asumimos que se reporta perdida)
    public Mascota save(Mascota mascota) {
        if (mascota.getReportedAt() == null) {
            mascota.setReportedAt(LocalDateTime.now());
        }
        if (mascota.getStatus() == null) {
            mascota.setStatus("LOST");
        }
        return mascotaRepository.save(mascota);
    }

    // 2. Obtener todas las mascotas
    public List<Mascota> findAll() {
        return mascotaRepository.findAll();
    }

    // 3. Obtener una mascota por su ID
    public Optional<Mascota> findById(String id) {
        return mascotaRepository.findById(id);
    }

    // 4. Actualizar información general de la mascota
    public Mascota update(String id, Mascota mascotaDetails) {
        return mascotaRepository.findById(id).map(mascotaExistente -> {
            mascotaExistente.setName(mascotaDetails.getName());
            mascotaExistente.setSpecies(mascotaDetails.getSpecies());
            mascotaExistente.setBreed(mascotaDetails.getBreed());
            mascotaExistente.setColor(mascotaDetails.getColor());
            mascotaExistente.setSize(mascotaDetails.getSize());
            mascotaExistente.setDescription(mascotaDetails.getDescription());
            mascotaExistente.setImageId(mascotaDetails.getImageId());
            mascotaExistente.setLastLocation(mascotaDetails.getLastLocation()); // Aquí actualizamos la ubicación
            
            return mascotaRepository.save(mascotaExistente);
        }).orElse(null); // Retorna null si no encontró la mascota
    }

    // 5. Método especial para actualizar solo el ESTADO (LOST -> FOUND -> REUNITED)
    public Mascota updateStatus(String id, String newStatus, String founderId) {
        return mascotaRepository.findById(id).map(mascota -> {
            mascota.setStatus(newStatus.toUpperCase());
            
            // Lógica de negocio: Asignar fechas automáticamente según el nuevo estado
            if ("FOUND".equalsIgnoreCase(newStatus)) {
                mascota.setFoundAt(LocalDateTime.now());
                if (founderId != null) {
                    mascota.setFounderId(founderId);
                }
            } else if ("REUNITED".equalsIgnoreCase(newStatus)) {
                mascota.setReunitedAt(LocalDateTime.now());
            }
            
            return mascotaRepository.save(mascota);
        }).orElse(null);
    }

    // 6. Eliminar mascota del registro
    public boolean delete(String id) {
        return mascotaRepository.findById(id).map(mascota -> {
            mascotaRepository.delete(mascota);
            return true;
        }).orElse(false);
    }
}