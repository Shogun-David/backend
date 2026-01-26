package com.indra.reservations_backend.security.config;

import com.indra.reservations_backend.security.filter.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
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
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

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
public class SecurityConfig {

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
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {
        http
                // Deshabilitar CSRF (no necesario para API REST stateless con JWT)
                .csrf(AbstractHttpConfigurer::disable)
                
                // Habilitar CORS con configuración personalizada
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                
                // Configurar autorización de requests
                .authorizeHttpRequests(auth -> auth
                        // Endpoints públicos (sin autenticación) - ORDEN IMPORTANTE
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers("/api/reservas/**", "/salas/**").permitAll()
                        .requestMatchers("/swagger-ui/**").permitAll()
                        .requestMatchers("/swagger-ui.html").permitAll()
                        .requestMatchers("/v3/api-docs/**").permitAll()
                        .requestMatchers("/swagger-resources/**").permitAll()
                        .requestMatchers("/webjars/**").permitAll()
                        .requestMatchers("/error").permitAll()
                        
                        // Todos los demás endpoints requieren autenticación
                        .anyRequest().authenticated()
                )
                
                // Configurar manejo de sesiones: STATELESS (sin sesiones)
                // Toda la autenticación se maneja por JWT
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                
                // Deshabilitar formLogin y httpBasic por defecto
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                
                // Agregar el filtro JWT antes del filtro de autenticación estándar
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
    
    /**
     * Configuración de CORS para permitir peticiones desde Swagger y frontend.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("*")); // En producción, especificar dominios
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(false); // false cuando allowedOrigins es "*"
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
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
     * Se utiliza en el AuthService para validar credenciales durante el proceso de login.
     * Spring Security automáticamente configura el AuthenticationManager con el 
     * UserDetailsService (usuarioService) y el PasswordEncoder definidos como beans.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
