package com.proyect.match.Assembler;

import com.proyect.match.Controller.MatchController;
import com.proyect.match.Dto.MatchResponseDTO;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class MatchAssembler implements RepresentationModelAssembler<MatchResponseDTO, EntityModel<MatchResponseDTO>> {
    
    @Override
    public EntityModel<MatchResponseDTO> toModel(MatchResponseDTO dto) {
        return EntityModel.of(dto,
                linkTo(methodOn(MatchController.class).getMatchById(dto.getId())).withSelfRel(),
                linkTo(methodOn(MatchController.class).getMatchesByOwnerId(dto.getOwnerId())).withRel("owner-matches"),
                linkTo(methodOn(MatchController.class).getMatchesByFounderId(dto.getFounderId())).withRel("founder-matches"),
                linkTo(methodOn(MatchController.class).respondToMatch(null)).withRel("respond"),
                linkTo(methodOn(MatchController.class).completeMatch(dto.getId())).withRel("complete")
        );
    }
}