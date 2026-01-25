package com.indra.reservations_backend.mappers;

import com.indra.reservations_backend.dto.UsuarioRequestDto;
import com.indra.reservations_backend.dto.UsuarioResponseDto;
import com.indra.reservations_backend.models.UsuarioEntity;
import com.indra.reservations_backend.models.UsuarioRol;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Mapper para convertir entre UsuarioEntity y DTOs.
 */
@Component
public class UsuarioMapper {

    /**
     * Convierte UsuarioEntity a UsuarioResponseDto (con roles).
     */
    public UsuarioResponseDto toResponseDto(UsuarioEntity usuario, List<UsuarioRol> roles) {
        if (usuario == null) {
            return null;
        }
        return new UsuarioResponseDto(
                usuario.getIdUsuario(),
                usuario.getUsername(),
                usuario.getEstado(),
                usuario.getFechaCreacion() != null ? usuario.getFechaCreacion().toString() : null
        );
    }

    /**
     * Convierte UsuarioEntity a UsuarioResponseDto (sin roles).
     */
    public UsuarioResponseDto toResponseDto(UsuarioEntity usuario) {
        if (usuario == null) {
            return null;
        }
        return new UsuarioResponseDto(
                usuario.getIdUsuario(),
                usuario.getUsername(),
                usuario.getEstado(),
                usuario.getFechaCreacion() != null ? usuario.getFechaCreacion().toString() : null
        );
    }

    /**
     * Convierte UsuarioRequestDto a UsuarioEntity.
     */
    public UsuarioEntity toEntity(UsuarioRequestDto dto) {
        if (dto == null) {
            return null;
        }
        return UsuarioEntity.builder()
                .username(dto.getUsername())
                .password(dto.getPassword())
                .build();
    }
}
