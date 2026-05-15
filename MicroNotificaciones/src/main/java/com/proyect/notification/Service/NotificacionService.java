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

    // 1. Crear una nueva notificación (ej: cuando el sistema de Match detecta una coincidencia)
    public Notificacion crearNotificacion(Notificacion notificacion) {
        // Configuraciones por defecto al crear
        if (notificacion.getCreatedAt() == null) {
            notificacion.setCreatedAt(LocalDateTime.now());
        }
        // Aseguramos que empiece como no leída
        notificacion.setRead(false); 
        return notificacionRepository.save(notificacion);
    }

    // 2. Obtener todas las notificaciones de un usuario (para el historial de la campana)
    public List<Notificacion> obtenerNotificacionesPorUsuario(String userId) {
        return notificacionRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    // 3. Obtener solo las notificaciones sin leer de un usuario
    public List<Notificacion> obtenerNotificacionesNoLeidas(String userId) {
        return notificacionRepository.findByUserIdAndIsReadFalseOrderByCreatedAtDesc(userId);
    }

    // 4. Contar cuántas notificaciones sin leer tiene el usuario (para el numerito rojo en la campana)
    public long contarNotificacionesNoLeidas(String userId) {
        return notificacionRepository.countByUserIdAndIsReadFalse(userId);
    }

    // 5. Marcar una notificación específica como leída (cuando el usuario hace clic en ella)
    public Optional<Notificacion> marcarComoLeida(String id) {
        return notificacionRepository.findById(id).map(notificacion -> {
            notificacion.setRead(true);
            return notificacionRepository.save(notificacion);
        });
    }
    
    // 6. Marcar TODAS las notificaciones de un usuario como leídas (Botón "Marcar todas como leídas")
    public void marcarTodasComoLeidas(String userId) {
        List<Notificacion> noLeidas = notificacionRepository.findByUserIdAndIsReadFalseOrderByCreatedAtDesc(userId);
        for (Notificacion notificacion : noLeidas) {
            notificacion.setRead(true);
        }
        notificacionRepository.saveAll(noLeidas);
    }

    // 7. Eliminar una notificación
    public boolean eliminarNotificacion(String id) {
        return notificacionRepository.findById(id).map(notificacion -> {
            notificacionRepository.delete(notificacion);
            return true;
        }).orElse(false);
    }

    // 8. Obtener una notificación por su ID (Para el GET /{id})
    public Optional<Notificacion> obtenerPorId(String id) {
        return notificacionRepository.findById(id);
    }

    // Nuevo: Obtener todas las notificaciones del sistema (Para el GET /)
    public List<Notificacion> obtenerTodas() {
        return notificacionRepository.findAll();
    }
}