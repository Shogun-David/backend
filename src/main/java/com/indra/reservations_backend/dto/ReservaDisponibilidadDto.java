package com.indra.reservations_backend.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ReservaDisponibilidadDto {
    
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
}