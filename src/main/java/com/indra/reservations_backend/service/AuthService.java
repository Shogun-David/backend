package com.indra.reservations_backend.service;

import com.indra.reservations_backend.dto.LoginRequestDto;
import com.indra.reservations_backend.dto.LoginResponseDto;
import com.indra.reservations_backend.model.Usuario;
import com.indra.reservations_backend.security.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
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
@Slf4j
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    /**
     * 🔹 PASO 2-3-4: Procesa la autenticación
     * 
     * Flujo interno:
     * 2️⃣ AuthenticationManager.authenticate() valida credenciales
     *    ↓
     * 3️⃣ Internamente llama a UsuarioService.loadUserByUsername()
     *    ↓ Busca en BD (usuario + roles)
     *    ↓ BCrypt compara passwords
     * 4️⃣ Si válido → JwtService.generateToken() crea JWT firmado
     * 
     * @param loginRequest Credenciales del usuario
     * @return LoginResponseDto con el token JWT
     * @throws AuthenticationException si credenciales inválidas
     */
    public LoginResponseDto login(LoginRequestDto loginRequest) {
        log.info("🔐 Intento de login para usuario: {}", loginRequest.getUsername());
        
        try {
            // Autenticar al usuario con Spring Security
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );

            // Obtener el usuario autenticado
            Usuario usuario = (Usuario) authentication.getPrincipal();
            log.info("✅ Autenticación exitosa para usuario: {}", usuario.getUsername());
            log.info("   Roles: {}", usuario.getAuthorities());
            log.info("   Estado: {}", usuario.getEstado());

            // Generar token JWT
            String token = jwtService.generateToken(usuario);
            log.debug("🎫 Token JWT generado para usuario: {}", usuario.getUsername());

            // Retornar respuesta con el token
            return new LoginResponseDto(token);
            
        } catch (BadCredentialsException e) {
            log.warn("❌ Credenciales inválidas para usuario: {} - Contraseña incorrecta", loginRequest.getUsername());
            throw new BadCredentialsException("Username o contraseña inválidos", e);
        } catch (AuthenticationException e) {
            log.error("❌ Error de autenticación para usuario: {} - {}", loginRequest.getUsername(), e.getMessage(), e);
            throw e;
        }
    }
}
