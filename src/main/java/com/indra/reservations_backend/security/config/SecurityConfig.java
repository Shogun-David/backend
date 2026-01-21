package com.indra.reservations_backend.security.config;

import com.indra.reservations_backend.security.filter.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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
                        // Las reglas más específicas deben ir PRIMERO
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()  // ✅ Permitir preflight CORS en TODOS los endpoints
                        .requestMatchers("/auth/login").permitAll()
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers("/auth/test").permitAll()
                        .requestMatchers("/swagger-ui/**").permitAll()
                        .requestMatchers("/swagger-ui.html").permitAll()
                        .requestMatchers("/v3/api-docs/**").permitAll()
                        .requestMatchers("/swagger-resources/**").permitAll()
                        .requestMatchers("/webjars/**").permitAll()
                        .requestMatchers("/error").permitAll()
                        .requestMatchers("/", "/**/*.js", "/**/*.css", "/**/*.html", "/**/*.png", "/**/*.jpg").permitAll()
                        
                        // 🔓 POST /api/usuarios PÚBLICO (registro de usuarios)
                        // DEBE ir ANTES de otros matchers de /api/usuarios
                        .requestMatchers(HttpMethod.POST, "/api/usuarios").permitAll()
                        
                        // 🔐 Endpoints ADMIN - requieren rol ADMIN
                        // GET /api/usuarios (listar)
                        // DELETE /api/usuarios/{id} (eliminar)
                        // POST /api/usuarios/admin (crear por admin)
                        .requestMatchers(HttpMethod.GET, "/api/usuarios").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/usuarios/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/usuarios/admin").hasRole("ADMIN")
                        
                        // 🔐 Endpoints del panel de USUARIO (requieren @usuario en email)
                        .requestMatchers("/api/panel-usuario/**").hasRole("USUARIO")
                        
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
     * 
     * ⚠️ IMPORTANTE: El orden de CORS es crítico
     * Las solicitudes preflight (OPTIONS) deben ser permitidas ANTES que POST/PUT/DELETE
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Permitir orígenes específicos (cambiar en producción)
        configuration.setAllowedOrigins(Arrays.asList(
            "http://localhost:4200",      // Angular frontend
            "http://localhost:3000",      // Otro puerto común
            "http://127.0.0.1:4200",
            "http://127.0.0.1:3000"
        ));
        
        // Permitir todos los métodos HTTP incluyendo OPTIONS (preflight)
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        
        // Permitir headers comunes (incluyendo Authorization y Content-Type)
        configuration.setAllowedHeaders(Arrays.asList(
            "Authorization",
            "Content-Type",
            "Accept",
            "Origin",
            "Access-Control-Request-Method",
            "Access-Control-Request-Headers"
        ));
        
        // Permitir headers de respuesta CORS
        configuration.setExposedHeaders(Arrays.asList(
            "Authorization",
            "Access-Control-Allow-Origin",
            "Access-Control-Allow-Credentials"
        ));
        
        // Permitir credenciales si se usan orígenes específicos
        configuration.setAllowCredentials(true);
        
        // Cache del preflight por 1 hora
        configuration.setMaxAge(3600L);
        
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
