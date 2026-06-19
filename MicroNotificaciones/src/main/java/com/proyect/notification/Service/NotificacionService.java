package com.proyect.notification.Service;

import com.proyect.notification.Model.Notificacion;
import com.proyect.notification.Repository.NotificacionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@SuppressWarnings("null")
public class NotificacionService {

    @Autowired
    private NotificacionRepository notificacionRepository;

    public Notificacion crearNotificacion(Notificacion notificacion) {
        if (notificacion.getCreatedAt() == null) {
            notificacion.setCreatedAt(LocalDateTime.now());
        }
        notificacion.setRead(false); 
        return notificacionRepository.save(notificacion);
    }

    public List<Notificacion> obtenerNotificacionesPorUsuario(String userId) {
        return notificacionRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public List<Notificacion> obtenerNotificacionesNoLeidas(String userId) {
        return notificacionRepository.findByUserIdAndIsReadFalseOrderByCreatedAtDesc(userId);
    }

    public long contarNotificacionesNoLeidas(String userId) {
        return notificacionRepository.countByUserIdAndIsReadFalse(userId);
    }

    public Optional<Notificacion> marcarComoLeida(String id) {
        return notificacionRepository.findById(id).map(notificacion -> {
            notificacion.setRead(true);
            return notificacionRepository.save(notificacion);
        });
    }
    
    public void marcarTodasComoLeidas(String userId) {
        List<Notificacion> noLeidas = notificacionRepository.findByUserIdAndIsReadFalseOrderByCreatedAtDesc(userId);
        for (Notificacion notificacion : noLeidas) {
            notificacion.setRead(true);
        }
        notificacionRepository.saveAll(noLeidas);
    }

    public boolean eliminarNotificacion(String id) {
        return notificacionRepository.findById(id).map(notificacion -> {
            notificacionRepository.delete(notificacion);
            return true;
        }).orElse(false);
    }

    public Optional<Notificacion> obtenerPorId(String id) {
        return notificacionRepository.findById(id);
    }

    public List<Notificacion> obtenerTodas() {
        return notificacionRepository.findAll();
    }
}