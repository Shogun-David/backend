package com.indra.reservations_backend.security.config;

import com.indra.reservations_backend.security.filter.JwtAuthenticationFilter;
import com.indra.reservations_backend.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Configuración de Spring Security.
 * 
 * Configuración principal de seguridad del sistema:
 * - Define SecurityFilterChain (Spring Boot 3)
 * - Configura autenticación stateless con JWT
 * - Define endpoints públicos y protegidos
 * - Configura CORS y CSRF
 * - Registra el filtro JWT
 * 
 * NO usa WebSecurityConfigurerAdapter (deprecated desde Spring Security 5.7)
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true) // Permite @Secured, @PreAuthorize, etc.
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final UsuarioService usuarioService;

    /**
     * Configuración principal del SecurityFilterChain.
     * 
     * Define:
     * - Endpoints públicos: /auth/login, /swagger-ui/**, /v3/api-docs/**
     * - Endpoints protegidos: todos los demás
     * - Sesiones: STATELESS (sin estado, todo por JWT)
     * - CSRF: deshabilitado (no necesario en API REST stateless)
     * - Filtro JWT: se ejecuta antes de UsernamePasswordAuthenticationFilter
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Deshabilitar CSRF (no necesario para API REST stateless con JWT)
                .csrf(AbstractHttpConfigurer::disable)
                
                // Configurar autorización de requests
                .authorizeHttpRequests(auth -> auth
                        // Endpoints públicos (sin autenticación)
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers(
                                "/swagger-ui/**", 
                                "/swagger-ui.html",
                                "/v3/api-docs/**",
                                "/swagger-resources/**",
                                "/webjars/**"
                        ).permitAll()
                        
                        // Todos los demás endpoints requieren autenticación
                        .anyRequest().authenticated()
                )
                
                // Configurar manejo de sesiones: STATELESS (sin sesiones)
                // Toda la autenticación se maneja por JWT
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                
                // Agregar el filtro JWT antes del filtro de autenticación estándar
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * PasswordEncoder para cifrar y validar contraseñas.
     * Usa BCrypt con factor de trabajo 10 (default).
     * 
     * BCrypt es un algoritmo de hash adaptativo que:
     * - Incluye salt automático
     * - Es resistente a ataques de fuerza bruta
     * - Es configurable en su complejidad
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * AuthenticationManager para procesar autenticaciones.
     * 
     * Se utiliza en el AuthService para validar credenciales
     * durante el proceso de login.
     */
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config,
            PasswordEncoder passwordEncoder) throws Exception {
        return config.getAuthenticationManager();
    }
}
