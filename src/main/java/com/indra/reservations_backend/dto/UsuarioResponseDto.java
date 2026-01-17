package com.indra.reservations_backend.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioResponseDto {
    private Long idUsuario;
    private String username;
    private String email;
    private List<String> roles;
    private String estado;
}
