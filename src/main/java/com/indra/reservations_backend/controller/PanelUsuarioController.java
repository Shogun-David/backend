package com.indra.reservations_backend.controller;

import com.indra.reservations_backend.dto.UsuarioResponseDto;
import com.indra.reservations_backend.model.Usuario;
import com.indra.reservations_backend.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador para el panel de usuario (Rol: USUARIO).
 * 
 * SEGURIDAD: Solo usuarios con rol USUARIO y email @usuario pueden acceder.
 * 
 * 🔒 Validación:
 * - @PreAuthorize("hasRole('USUARIO')") → requiere rol USUARIO
 * - ValidarDominioEmailAspect → valida que email sea @usuario
 * 
 * Si intenta acceder sin cumplir → 403 FORBIDDEN
 */
@RestController
@RequestMapping("/api/panel-usuario")
@RequiredArgsConstructor
@Tag(name = "Panel Usuario", description = "Panel de usuario - Solo accesible con rol USUARIO y email @usuario")
@SecurityRequirement(name = "Bearer Authentication")
public class PanelUsuarioController {

    private final UsuarioService usuarioService;

    /**
     * 🔐 Obtener perfil del usuario autenticado.
     * 
     * SOLO USUARIO con email @usuario
     * 
     * @param authentication Usuario autenticado
     * @return Datos del usuario
     */
    @GetMapping("/perfil")
    @PreAuthorize("hasRole('USUARIO')")
    @Operation(
            summary = "Obtener mi perfil",
            description = "Retorna los datos del usuario autenticado. Requiere rol USUARIO y email @usuario."
    )
    public ResponseEntity<UsuarioResponseDto> obtenerPerfil(Authentication authentication) {
        Usuario usuario = (Usuario) authentication.getPrincipal();
        UsuarioResponseDto perfil = usuarioService.getUsuarioById(usuario.getIdUsuario());
        return ResponseEntity.ok(perfil);
    }

    /**
     * 🔐 Dashboard del usuario.
     * 
     * SOLO USUARIO con email @usuario
     * 
     * @return Mensaje de bienvenida
     */
    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('USUARIO')")
    @Operation(
            summary = "Dashboard de usuario",
            description = "Panel principal del usuario. Requiere rol USUARIO y email @usuario."
    )
    public ResponseEntity<String> dashboard(Authentication authentication) {
        Usuario usuario = (Usuario) authentication.getPrincipal();
        return ResponseEntity.ok("Bienvenido " + usuario.getUsername() + " al panel de usuario");
    }
}
