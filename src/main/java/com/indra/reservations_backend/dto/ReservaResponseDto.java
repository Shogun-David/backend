package com.indra.reservations_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReservaResponseDto {
    private Long idReserva;
    private String sala;
    private String fechaInicio;
    private String fechaFin;
    private String estado;
    private String fechaCreacion;
}
