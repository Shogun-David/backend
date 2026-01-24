package com.indra.reservations_backend.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReservaListadoAdminDto {
    private Long idReserva;
    private String usuario;
    private String sala;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private String estado;
}
//-- IGNORE ---