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
     * Endpoint de login.
     * 
     * Recibe credenciales (username y password) y retorna un token JWT si son válidas.
     * 
     * Flujo:
     * 1. Recibe LoginRequestDto con username y password
     * 2. El AuthService valida las credenciales
     * 3. Si son válidas, genera un token JWT
     * 4. Retorna LoginResponseDto con el token
     * 
     * @param loginRequest DTO con username y password
     * @return ResponseEntity con LoginResponseDto conteniendo el token JWT
     * @throws org.springframework.security.core.AuthenticationException si las credenciales son inválidas
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
        return ResponseEntity.ok("Usuario autenticado correctamente");
    }
}
