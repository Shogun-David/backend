package com.indra.reservations_backend.mappers;

import java.util.List;

import org.springframework.stereotype.Component;

import com.indra.reservations_backend.dto.SalaRequestDto;
import com.indra.reservations_backend.dto.SalaResponseDto;
import com.indra.reservations_backend.models.SalaEntity;

@Component
public class SalaMapper {

    public SalaEntity toEntity(SalaRequestDto dto) {
        return SalaEntity.builder()
                .nombre(dto.getNombre())
                .capacidad(dto.getCapacidad())
                .ubicacion(dto.getUbicacion())
                .estado(dto.getEstado())
                .build();
    }

    public SalaResponseDto toResponseDto(SalaEntity entity) {
        return SalaResponseDto.builder()
                .idSala(entity.getIdSala())
                .nombre(entity.getNombre())
                .capacidad(entity.getCapacidad())
                .ubicacion(entity.getUbicacion())
                .estado(entity.getEstado())
                .build();
    }

    public List<SalaResponseDto> toResponseDtoList(List<SalaEntity> entities) {
        return entities.stream()
                .map(this::toResponseDto)
                .toList();
    }

    public List<SalaEntity> toEntityList(List<SalaRequestDto> dtos) {
        return dtos.stream()
                .map(this::toEntity)
                .toList();
    }
}
