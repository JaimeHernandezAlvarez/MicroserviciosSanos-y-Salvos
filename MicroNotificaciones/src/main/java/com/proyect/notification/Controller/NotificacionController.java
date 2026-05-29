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

    @PostMapping
    public ResponseEntity<EntityModel<Notificacion>> crear(@RequestBody Notificacion notificacion) {
        Notificacion nueva = notificacionService.crearNotificacion(notificacion);
        return ResponseEntity.ok(assembler.toModel(nueva));
    }

    @SuppressWarnings("null")
    @GetMapping
    public CollectionModel<EntityModel<Notificacion>> listarTodas() {
        List<EntityModel<Notificacion>> notificaciones = notificacionService.obtenerTodas().stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(notificaciones, 
                linkTo(methodOn(NotificacionController.class).listarTodas()).withSelfRel());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Notificacion>> obtenerPorId(@PathVariable String id) {
        return notificacionService.obtenerPorId(id) // <-- El punto y el método son la clave aquí
                .map(assembler::toModel)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @SuppressWarnings("null")
    @GetMapping("/user/{userid}")
    public CollectionModel<EntityModel<Notificacion>> listarPorUsuario(@PathVariable("userid") String userId) {
        List<EntityModel<Notificacion>> notificaciones = notificacionService.obtenerNotificacionesPorUsuario(userId).stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(notificaciones, 
                linkTo(methodOn(NotificacionController.class).listarPorUsuario(userId)).withSelfRel());
    }

    @SuppressWarnings("null")
    @GetMapping("/user/{userid}/activas")
    public CollectionModel<EntityModel<Notificacion>> listarNoLeidas(@PathVariable("userid") String userId) {
        List<EntityModel<Notificacion>> notificaciones = notificacionService.obtenerNotificacionesNoLeidas(userId).stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(notificaciones,
                linkTo(methodOn(NotificacionController.class).listarNoLeidas(userId)).withSelfRel());
    }

    @GetMapping("/user/{userid}/cuenta")
    public ResponseEntity<Long> obtenerContadorNoLeidas(@PathVariable("userid") String userId) {
        long cuenta = notificacionService.contarNotificacionesNoLeidas(userId);
        return ResponseEntity.ok(cuenta);
    }

    @PatchMapping("/{id}/leer")
    public ResponseEntity<EntityModel<Notificacion>> marcarComoLeida(@PathVariable String id) {
        return notificacionService.marcarComoLeida(id)
                .map(assembler::toModel)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/user/{userid}/leer-todas")
    public ResponseEntity<Void> marcarTodasComoLeidas(@PathVariable("userid") String userId) {
        notificacionService.marcarTodasComoLeidas(userId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable String id) {
        return notificacionService.eliminarNotificacion(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
}