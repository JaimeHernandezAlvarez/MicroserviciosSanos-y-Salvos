package com.proyect.pet.Service;

import com.proyect.pet.Config.RabbitMQConfig;
import com.proyect.pet.Dto.MascotaEncontradaEvent;
import com.proyect.pet.Model.Mascota;
import com.proyect.pet.Repository.MascotaRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
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

    @Autowired
    private RabbitTemplate rabbitTemplate;

    //crear mascota
    public Mascota save(Mascota mascota) {
        if (mascota.getReportedAt() == null) {
            mascota.setReportedAt(LocalDateTime.now());
        }
        if (mascota.getStatus() == null) {
            mascota.setStatus("LOST");
        }
        return mascotaRepository.save(mascota);
    }

    //todos
    public List<Mascota> findAll() {
        return mascotaRepository.findAll();
    }

    //obtener por id
    public Optional<Mascota> findById(String id) {
        return mascotaRepository.findById(id);
    }

    //actualizar
    public Mascota update(String id, Mascota mascotaDetails) {
        return mascotaRepository.findById(id).map(mascotaExistente -> {
            mascotaExistente.setName(mascotaDetails.getName());
            mascotaExistente.setSpecies(mascotaDetails.getSpecies());
            mascotaExistente.setBreed(mascotaDetails.getBreed());
            mascotaExistente.setColor(mascotaDetails.getColor());
            mascotaExistente.setSize(mascotaDetails.getSize());
            mascotaExistente.setDescription(mascotaDetails.getDescription());
            mascotaExistente.setImageId(mascotaDetails.getImageId());
            mascotaExistente.setLastLocation(mascotaDetails.getLastLocation());

            return mascotaRepository.save(mascotaExistente);
        }).orElse(null);
    }

    //actualizar estado (LOST, FOUND, REUNITED)
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

            Mascota guardada = mascotaRepository.save(mascota);

            // Publicar evento en CloudAMQP cuando la mascota sea ENCONTRADA
            if ("FOUND".equalsIgnoreCase(newStatus)) {
                MascotaEncontradaEvent evento = new MascotaEncontradaEvent(
                    guardada.getId(),
                    guardada.getOwnerId(),
                    guardada.getFounderId(),
                    guardada.getStatus()
                );

                rabbitTemplate.convertAndSend(
                    RabbitMQConfig.EXCHANGE_NAME,
                    "mascota.evento.encontrada",
                    evento
                );

                System.out.println("📤 Evento publicado a CloudAMQP: " + evento);
            }

            return guardada;
        }).orElse(null);
    }

    //eliminar
    public boolean delete(String id) {
        return mascotaRepository.findById(id).map(mascota -> {
            mascotaRepository.delete(mascota);
            return true;
        }).orElse(false);
    }
}