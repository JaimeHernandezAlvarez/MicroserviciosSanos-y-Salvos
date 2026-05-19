package com.proyect.notification.Repository;

import com.proyect.notification.Model.Notificacion;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificacionRepository extends MongoRepository<Notificacion, String> {

    // 1. Obtener todas las notificaciones de un usuario, ordenadas de la más reciente a la más antigua
    List<Notificacion> findByUserIdOrderByCreatedAtDesc(String userId);

    // 2. Obtener solo las notificaciones NO leídas de un usuario (para la campana activa)
    List<Notificacion> findByUserIdAndIsReadFalseOrderByCreatedAtDesc(String userId);

    // 3. Contar cuántas notificaciones sin leer tiene el usuario (ideal para el contador flotante "el puntito rojo")
    long countByUserIdAndIsReadFalse(String userId);
}