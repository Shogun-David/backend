package com.indra.reservations_backend.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReservaResponseDto {
    private Long idReserva;
    private String sala;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private String estado;
    private LocalDateTime fechaCreacion;
}
