package com.proyect.pet.Controller;

import com.proyect.pet.Assembler.MascotaModelAssembler;
import com.proyect.pet.Model.Mascota;
import com.proyect.pet.Service.MascotaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/api/mascotas")
@SuppressWarnings("null")
public class MascotaController {

    @Autowired
    private MascotaService mascotaService;

    @Autowired
    private MascotaModelAssembler assembler;

    @GetMapping
    public CollectionModel<EntityModel<Mascota>> listarTodas() {
        List<EntityModel<Mascota>> mascotas = mascotaService.findAll().stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(mascotas, 
                linkTo(methodOn(MascotaController.class).listarTodas()).withSelfRel());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Mascota>> obtenerPorId(@PathVariable String id) {
        return mascotaService.findById(id)
                .map(assembler::toModel)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<EntityModel<Mascota>> crear(@RequestBody Mascota mascota) {
        Mascota nuevaMascota = mascotaService.save(mascota);
        return ResponseEntity.ok(assembler.toModel(nuevaMascota));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<Mascota>> actualizar(@PathVariable String id, @RequestBody Mascota mascota) {
        Mascota actualizada = mascotaService.update(id, mascota);
        return (actualizada != null) 
                ? ResponseEntity.ok(assembler.toModel(actualizada)) 
                : ResponseEntity.notFound().build();
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<EntityModel<Mascota>> cambiarEstado(
            @PathVariable String id, 
            @RequestParam String nuevoEstado,
            @RequestParam(required = false) String founderId) {
            
        Mascota actualizada = mascotaService.updateStatus(id, nuevoEstado, founderId);
        return (actualizada != null) 
                ? ResponseEntity.ok(assembler.toModel(actualizada)) 
                : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable String id) {
        return mascotaService.delete(id) 
                ? ResponseEntity.noContent().build() 
                : ResponseEntity.notFound().build();
    }
}