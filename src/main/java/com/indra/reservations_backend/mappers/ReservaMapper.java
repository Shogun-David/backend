package com.indra.reservations_backend.mappers;

import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Component;

import com.indra.reservations_backend.dto.ReservaRequestDto;
import com.indra.reservations_backend.dto.ReservaResponseDto;
import com.indra.reservations_backend.models.ReservaEntity;
import com.indra.reservations_backend.models.SalaEntity;

@Component
public class ReservaMapper {

    
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");


    public ReservaResponseDto toDto(ReservaEntity entity){

        return ReservaResponseDto.builder()
            .idReserva(entity.getIdReserva())
            .sala(entity.getSala() != null ? entity.getSala().getNombre() : null)
            .fechaInicio(entity.getFechaCreacion())
            .fechaFin(entity.getFechaFin())
            .numeroAsistentes(entity.getNumeroAsistentes())
            //.fechaInicio(entity.getFechaInicio() != null ? entity.getFechaInicio().format(FORMATTER) : null)
            //.fechaFin(entity.getFechaFin() != null ? entity.getFechaFin().format(FORMATTER) : null)
            .estado(entity.getEstado() != null ? 
                                            entity.getEstado().getDescripcion() : null)
            //.fechaCreacion(entity.getFechaCreacion() != null ? entity.getFechaCreacion().format(FORMATTER) : null)
            .fechaCreacion(entity.getFechaCreacion())
            .build();


    }


    public ReservaEntity toEntity(ReservaRequestDto request){
        SalaEntity salaEntity = SalaEntity.builder()
            .idSala(request.getIdSala())
        .build();

        return ReservaEntity.builder()
            .sala(salaEntity)
            .fechaInicio(request.getFechaInicio())
            .fechaFin(request.getFechaFin())
            .numeroAsistentes(request.getNumeroAsistentes())
            .observacion(request.getObservacion())
        
        .build();
    }
}
