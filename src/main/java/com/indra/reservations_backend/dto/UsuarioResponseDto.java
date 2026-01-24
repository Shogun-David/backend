package com.indra.reservations_backend.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioResponseDto {
    private Long idUsuario;
    private String username;
    private String estado;
    private String fechaCreacion;
}
