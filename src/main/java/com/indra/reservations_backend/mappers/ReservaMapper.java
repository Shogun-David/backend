package com.indra.reservations_backend.mappers;

import org.springframework.stereotype.Component;

import com.indra.reservations_backend.dto.ReservaResponseDto;
import com.indra.reservations_backend.models.ReservaEntity;

@Component
public class ReservaMapper {

    ReservaResponseDto toDto(ReservaEntity reservaEntity){
        
        return ReservaResponseDto.builder()
            .idReserva(reservaEntity.getIdReserva())
             
            .build();


    }
}
