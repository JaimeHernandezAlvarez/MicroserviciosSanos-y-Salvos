package com.proyect.geolocalizacion.controller;

import com.proyect.geolocalizacion.Assembler.UbicacionModelAssembler;
import com.proyect.geolocalizacion.model.Ubicacion;
import com.proyect.geolocalizacion.service.UbicacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/api/geolocalizacion") // O "/api/geo" si el equipo acordó acortarlo
public class UbicacionController {

    @Autowired
    private UbicacionService ubicacionService;

    @Autowired
    private UbicacionModelAssembler assembler;

    // 1. POST / -> Registrar ubicación
    @PostMapping
    public ResponseEntity<EntityModel<Ubicacion>> registrarUbicacion(@RequestBody Ubicacion ubicacion) {
        Ubicacion nueva = ubicacionService.guardarUbicacion(ubicacion);
        return new ResponseEntity<>(assembler.toModel(nueva), HttpStatus.CREATED);
    }

    // 2. GET / -> Obtener todas (Reemplaza al antiguo /heatmap)
    @SuppressWarnings("null")
    @GetMapping
    public CollectionModel<EntityModel<Ubicacion>> obtenerTodas() {
        List<EntityModel<Ubicacion>> todas = ubicacionService.obtenerTodas().stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(todas,
                linkTo(methodOn(UbicacionController.class).obtenerTodas()).withSelfRel());
    }

    // 3. GET /{id} -> Obtener una ubicación individual
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Ubicacion>> obtenerPorId(@PathVariable String id) {
        return ubicacionService.obtenerPorId(id)
                .map(assembler::toModel)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 4. GET /pet/{petId} -> Historial de ubicaciones de una mascota
    @SuppressWarnings("null")
    @GetMapping("/pet/{petId}")
    public CollectionModel<EntityModel<Ubicacion>> obtenerPorMascota(@PathVariable String petId) {
        List<EntityModel<Ubicacion>> historial = ubicacionService.obtenerPorReporte(petId).stream() // Asumiendo que el service aún usa "obtenerPorReporte"
                .map(assembler::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(historial,
                linkTo(methodOn(UbicacionController.class).obtenerPorMascota(petId)).withSelfRel());
    }

    // 5. GET /nearby -> Buscar avistamientos cercanos
    @SuppressWarnings("null")
    @GetMapping("/nearby")
    public CollectionModel<EntityModel<Ubicacion>> buscarCercanos(
            @RequestParam double lng,
            @RequestParam double lat,
            @RequestParam(defaultValue = "5.0") double radio) {
        
        List<EntityModel<Ubicacion>> cercanas = ubicacionService.buscarCercanos(lng, lat, radio).stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(cercanas,
                linkTo(methodOn(UbicacionController.class).buscarCercanos(lng, lat, radio)).withSelfRel());
    }

    // 6. PUT /{id} -> Actualizar una ubicación
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<Ubicacion>> actualizarUbicacion(@PathVariable String id, @RequestBody Ubicacion ubicacion) {
        return ubicacionService.actualizarUbicacion(id, ubicacion)
                .map(assembler::toModel)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 7. DELETE /{id} -> Eliminar una ubicación
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarUbicacion(@PathVariable String id) {
        return ubicacionService.eliminarUbicacion(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
}