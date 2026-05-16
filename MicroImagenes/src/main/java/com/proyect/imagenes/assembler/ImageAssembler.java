package com.proyect.imagenes.assembler;

import com.proyect.imagenes.controller.ImageController;
import com.proyect.imagenes.dto.ImageResponseDTO;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class ImageAssembler implements RepresentationModelAssembler<ImageResponseDTO, EntityModel<ImageResponseDTO>> {
    
    @Override
    public EntityModel<ImageResponseDTO> toModel(ImageResponseDTO dto) {
        return EntityModel.of(dto,
                linkTo(methodOn(ImageController.class).getImageById(dto.getImageId())).withSelfRel(),
                linkTo(methodOn(ImageController.class).downloadImage(dto.getImageId(), dto.getUserId())).withRel("download"),
                linkTo(methodOn(ImageController.class).deleteImage(dto.getImageId(), dto.getUserId())).withRel("delete"),
                linkTo(methodOn(ImageController.class).getImagesByUser(dto.getUserId())).withRel("user-images"),
                linkTo(methodOn(ImageController.class).getAllImages()).withRel("all-images")
        );
    }
}