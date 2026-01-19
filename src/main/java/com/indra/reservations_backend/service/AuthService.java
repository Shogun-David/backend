package com.indra.reservations_backend.service;

import com.indra.reservations_backend.dto.LoginRequestDto;
import com.indra.reservations_backend.dto.LoginResponseDto;
import com.indra.reservations_backend.model.Usuario;
import com.indra.reservations_backend.security.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

/**
 * Servicio de autenticación.
 * 
 * Responsabilidades:
 * - Procesar el login del usuario
 * - Validar credenciales usando AuthenticationManager
 * - Generar token JWT tras autenticación exitosa
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    /**
     * Procesa el login de un usuario.
     * 
     * Flujo:
     * 1. Autentica las credenciales usando Spring Security
     * 2. Si es exitoso, obtiene el usuario autenticado
     * 3. Genera un token JWT con username y roles
     * 4. Retorna el token en un DTO
     * 
     * @param loginRequest Credenciales del usuario (username y password)
     * @return LoginResponseDto con el token JWT
     * @throws org.springframework.security.core.AuthenticationException si las credenciales son inválidas
     */
    public LoginResponseDto login(LoginRequestDto loginRequest) {
        // Autenticar al usuario con Spring Security
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        // Obtener el usuario autenticado
        Usuario usuario = (Usuario) authentication.getPrincipal();

        // Generar token JWT
        String token = jwtService.generateToken(usuario);

        // Retornar respuesta con el token
        return new LoginResponseDto(token);
    }
}
