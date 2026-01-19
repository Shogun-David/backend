package com.indra.reservations_backend.service;

import com.indra.reservations_backend.dto.UsuarioResponseDto;
import com.indra.reservations_backend.model.Usuario;
import com.indra.reservations_backend.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio para gestionar usuarios.
 * Implementa UserDetailsService para integrarse con Spring Security.
 * 
 * Responsabilidades:
 * - Cargar usuarios por username (requerido por Spring Security)
 * - CRUD de usuarios
 * - Conversión de entidades a DTOs
 */
@Service
@RequiredArgsConstructor
public class UsuarioService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    /**
     * Método requerido por Spring Security para cargar un usuario durante la autenticación.
     * Se llama automáticamente cuando se intenta autenticar un usuario.
     * 
     * @param username El nombre de usuario
     * @return UserDetails del usuario
     * @throws UsernameNotFoundException si el usuario no existe
     */
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Usuario no encontrado: " + username));
    }

    /**
     * Busca un usuario por username y lo retorna como entidad.
     * 
     * @param username El nombre de usuario
     * @return La entidad Usuario
     * @throws UsernameNotFoundException si no existe
     */
    @Transactional(readOnly = true)
    public Usuario findByUsername(String username) {
        return usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Usuario no encontrado: " + username));
    }

    /**
     * Obtiene todos los usuarios del sistema.
     * 
     * @return Lista de usuarios como DTOs
     */
    @Transactional(readOnly = true)
    public List<UsuarioResponseDto> getAllUsuarios() {
        return usuarioRepository.findAll()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Busca un usuario por ID.
     * 
     * @param id ID del usuario
     * @return UsuarioResponseDto
     */
    @Transactional(readOnly = true)
    public UsuarioResponseDto getUsuarioById(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));
        return toDto(usuario);
    }

    /**
     * Convierte una entidad Usuario a UsuarioResponseDto.
     * 
     * @param usuario La entidad Usuario
     * @return DTO con datos del usuario (sin password)
     */
    private UsuarioResponseDto toDto(Usuario usuario) {
        return new UsuarioResponseDto(
                usuario.getIdUsuario(),
                usuario.getUsername(),
                usuario.getEmail(),
                usuario.getRolesList(),
                usuario.getEstado()
        );
    }
}
