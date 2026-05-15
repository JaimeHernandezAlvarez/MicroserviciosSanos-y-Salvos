package com.proyect.notification.Controller;

import com.proyect.notification.Assembler.NotificacionModelAssembler;
import com.proyect.notification.Model.Notificacion;
import com.proyect.notification.Service.NotificacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/api/notificaciones")
public class NotificacionController {

    @Autowired
    private NotificacionService notificacionService;

    @Autowired
    private NotificacionModelAssembler assembler;

    // 1. Crear una notificación (POST /)
    @PostMapping
    public ResponseEntity<EntityModel<Notificacion>> crear(@RequestBody Notificacion notificacion) {
        Notificacion nueva = notificacionService.crearNotificacion(notificacion);
        return ResponseEntity.ok(assembler.toModel(nueva));
    }

    // 2. NUEVO: Obtener todas las notificaciones del sistema (GET /)
    @SuppressWarnings("null")
    @GetMapping
    public CollectionModel<EntityModel<Notificacion>> listarTodas() {
        List<EntityModel<Notificacion>> notificaciones = notificacionService.obtenerTodas().stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(notificaciones, 
                linkTo(methodOn(NotificacionController.class).listarTodas()).withSelfRel());
    }

    // 3. Obtener una notificación individual por su ID (GET /{id})
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Notificacion>> obtenerPorId(@PathVariable String id) {
        return notificacionService.obtenerPorId(id) // <-- El punto y el método son la clave aquí
                .map(assembler::toModel)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 4. AJUSTADO: Obtener el historial completo de un usuario (GET /user/{userid})
    @SuppressWarnings("null")
    @GetMapping("/user/{userid}")
    public CollectionModel<EntityModel<Notificacion>> listarPorUsuario(@PathVariable("userid") String userId) {
        List<EntityModel<Notificacion>> notificaciones = notificacionService.obtenerNotificacionesPorUsuario(userId).stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(notificaciones, 
                linkTo(methodOn(NotificacionController.class).listarPorUsuario(userId)).withSelfRel());
    }

    // 5. BONUS AJUSTADO: Obtener solo las notificaciones activas/no leídas (GET /user/{userid}/activas)
    @SuppressWarnings("null")
    @GetMapping("/user/{userid}/activas")
    public CollectionModel<EntityModel<Notificacion>> listarNoLeidas(@PathVariable("userid") String userId) {
        List<EntityModel<Notificacion>> notificaciones = notificacionService.obtenerNotificacionesNoLeidas(userId).stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(notificaciones,
                linkTo(methodOn(NotificacionController.class).listarNoLeidas(userId)).withSelfRel());
    }

    // 6. BONUS AJUSTADO: Cuenta de no leídas para la campanita (GET /user/{userid}/cuenta)
    @GetMapping("/user/{userid}/cuenta")
    public ResponseEntity<Long> obtenerContadorNoLeidas(@PathVariable("userid") String userId) {
        long cuenta = notificacionService.contarNotificacionesNoLeidas(userId);
        return ResponseEntity.ok(cuenta);
    }

    // 7. Marcar una sola notificación como leída (PATCH /{id}/leer)
    @PatchMapping("/{id}/leer")
    public ResponseEntity<EntityModel<Notificacion>> marcarComoLeida(@PathVariable String id) {
        return notificacionService.marcarComoLeida(id)
                .map(assembler::toModel)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 8. BONUS AJUSTADO: Marcar TODO como leído (PATCH /user/{userid}/leer-todas)
    @PatchMapping("/user/{userid}/leer-todas")
    public ResponseEntity<Void> marcarTodasComoLeidas(@PathVariable("userid") String userId) {
        notificacionService.marcarTodasComoLeidas(userId);
        return ResponseEntity.noContent().build();
    }

    // 9. Eliminar una notificación del historial (DELETE /{id})
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable String id) {
        return notificacionService.eliminarNotificacion(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
}