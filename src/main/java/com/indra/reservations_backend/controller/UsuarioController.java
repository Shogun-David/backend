package com.indra.reservations_backend.controller;

import com.indra.reservations_backend.dto.UsuarioRequestDto;
import com.indra.reservations_backend.dto.UsuarioResponseDto;
import com.indra.reservations_backend.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador para gestión de usuarios.
 * 
 * SOLO ADMIN y USUARIO puede:
 * - Ver todos los usuarios
 * - Ver detalles de un usuario
 * - Crear, actualizar y eliminar usuarios
 * 
 * Usa @PreAuthorize("hasAnyRole('ADMIN', 'USUARIO')") para validar el rol.
 */
@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
@Tag(name = "Usuarios", description = "Gestión de usuarios (Solo ADMIN)")
@SecurityRequirement(name = "Bearer Authentication")
public class UsuarioController {

    private final UsuarioService usuarioService;

    /**
     * Obtiene todos los usuarios del sistema.
     * Solo accesible para usuarios con rol ADMIN.
     * 
     * @return Lista de usuarios
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Listar todos los usuarios",
            description = "Obtiene la lista completa de usuarios. Requiere rol ADMIN."
    )
    public ResponseEntity<List<UsuarioResponseDto>> getAllUsuarios() {
        List<UsuarioResponseDto> usuarios = usuarioService.getAllUsuarios();
        return ResponseEntity.ok(usuarios);
    }

    /**
     * Obtiene un usuario específico por su ID.
     * Solo accesible para usuarios con rol ADMIN.
     * 
     * @param id ID del usuario
     * @return Datos del usuario
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USUARIO')")
    @Operation(
            summary = "Obtener usuario por ID",
            description = "Obtiene los detalles de un usuario específico. Requiere rol ADMIN."
    )
    public ResponseEntity<UsuarioResponseDto> getUsuarioById(@PathVariable Long id) {
        UsuarioResponseDto usuario = usuarioService.getUsuarioById(id);
        return ResponseEntity.ok(usuario);
    }

    /**
     * 🔓 ENDPOINT PÚBLICO: Registrar nuevo usuario.
     * 
     * Permite que cualquier usuario NO autenticado se registre.
     * NO requiere token JWT.
     * 
     * @param request Datos del nuevo usuario (username, password, email)
     * @return UsuarioResponseDto creado
     */
    @PostMapping
    @Operation(
            summary = "Registrar nuevo usuario (PÚBLICO)",
            description = "Crea un nuevo usuario en el sistema. PÚBLICO - NO requiere autenticación."
    )
    public ResponseEntity<UsuarioResponseDto> registrar(@RequestBody @Valid UsuarioRequestDto request) {
        UsuarioResponseDto creado = usuarioService.createUsuario(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    /**
     * 🔐 Crear usuario por ADMIN.
     * 
     * SOLO ADMIN puede crear usuarios desde el panel de administración.
     * Requiere token JWT con rol ADMIN.
     * 
     * @param request Datos del nuevo usuario
     * @return UsuarioResponseDto creado
     */
    @PostMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Crear usuario (ADMIN)",
            description = "Crea un nuevo usuario. SOLO ADMIN. Requiere autenticación con rol ADMIN."
    )
    public ResponseEntity<UsuarioResponseDto> crearUsuarioAdmin(@RequestBody @Valid UsuarioRequestDto request) {
        UsuarioResponseDto creado = usuarioService.createUsuario(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    /**
     * Ejemplo de endpoint para actualizar usuario (solo esqueleto).
     * Solo ADMIN puede actualizar usuarios.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USUARIO')")
    @Operation(
            summary = "Actualizar usuario",
            description = "Actualiza los datos de un usuario existente. Requiere rol ADMIN."
    )
    public ResponseEntity<String> updateUsuario(@PathVariable Long id) {
        // TODO: Implementar lógica de actualización
        return ResponseEntity.ok("Usuario " + id + " actualizado (implementar lógica)");
    }

    /**
     * Ejemplo de endpoint para eliminar usuario (solo esqueleto).
     * Solo ADMIN puede eliminar usuarios.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Eliminar usuario",
            description = "Elimina un usuario del sistema. Requiere rol ADMIN."
    )
    public ResponseEntity<String> deleteUsuario(@PathVariable Long id) {
        usuarioService.deleteUsuario(id);
        return ResponseEntity.ok("Usuario " + id + " eliminado correctamente");
    }
}
