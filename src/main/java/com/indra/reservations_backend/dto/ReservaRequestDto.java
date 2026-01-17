package com.indra.reservations_backend.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReservaRequestDto {
    private Long idSala;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private String observacion;
}   
