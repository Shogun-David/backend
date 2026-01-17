package com.indra.reservations_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SalaResponseDto {
    private Long idSala;
    private String nombre;
    private Integer capacidad;
    private String ubicacion;
    private String estado;
}
