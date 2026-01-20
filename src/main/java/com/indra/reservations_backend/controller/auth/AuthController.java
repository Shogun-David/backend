package com.indra.reservations_backend.controller.auth;

import com.indra.reservations_backend.dto.LoginRequestDto;
import com.indra.reservations_backend.dto.LoginResponseDto;
import com.indra.reservations_backend.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador de autenticación.
 * 
 * Endpoints:
 * - POST /auth/login: Autenticar usuario y obtener token JWT
 * 
 * Este endpoint es público (configurado en SecurityConfig).
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticación", description = "Endpoints para autenticación de usuarios")
public class AuthController {

    private final AuthService authService;

    /**
     * Endpoint de prueba sin seguridad para verificar configuración.
     */
    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("✅ Endpoint público funcionando correctamente");
    }

    /**
     * 🔹 PASO 1: Endpoint de login público
     * 
     * Cliente → POST /auth/login con {"username": "admin", "password": "admin123"}
     * 
     * Flujo completo:
     * 1️⃣ Cliente envía POST /auth/login con credenciales
     * 2️⃣ AuthController recibe y delega a AuthService
     * 3️⃣ AuthService → AuthenticationManager.authenticate()
     * 4️⃣ UsuarioService.loadUserByUsername() busca en BD
     * 5️⃣ BCrypt compara password con hash de BD
     * 6️⃣ Si válido → JwtService.generateToken() crea JWT
     * 7️⃣ Retorna token al cliente
     * 8️⃣ Cliente guarda JWT en localStorage/memoria
     * 
     * @param loginRequest DTO con username y password
     * @return ResponseEntity con token JWT
     */
    @PostMapping("/login")
    @Operation(
            summary = "Iniciar sesión",
            description = "Autentica un usuario y retorna un token JWT válido"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Autenticación exitosa",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = LoginResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Credenciales inválidas",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos de entrada inválidos",
                    content = @Content
            )
    })
    public ResponseEntity<LoginResponseDto> login(@Valid @RequestBody LoginRequestDto loginRequest) {
        LoginResponseDto response = authService.login(loginRequest);
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint de prueba para verificar autenticación.
     * Requiere token JWT válido.
     * 
     * @return Mensaje de confirmación
     */
    @GetMapping("/me")
    @Operation(
            summary = "Obtener información del usuario autenticado",
            description = "Retorna información del usuario actual (requiere autenticación)"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Usuario autenticado",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "No autenticado",
                    content = @Content
            )
    })
    public ResponseEntity<String> getCurrentUser() {
        // Obtener el usuario autenticado del SecurityContext
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            String roles = authentication.getAuthorities().toString();
            
            String mensaje = String.format(
                "✅ Autenticación válida\n" +
                "Usuario: %s\n" +
                "Roles: %s\n" +
                "Tipo: %s",
                username,
                roles,
                authentication.getClass().getSimpleName()
            );
            
            return ResponseEntity.ok(mensaje);
        }
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body("❌ No autenticado");
    }
}
