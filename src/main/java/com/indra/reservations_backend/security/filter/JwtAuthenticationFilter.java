package com.indra.reservations_backend.security.filter;

import com.indra.reservations_backend.security.jwt.JwtService;
import com.indra.reservations_backend.service.UsuarioService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filtro de autenticación JWT.
 * Extiende OncePerRequestFilter para garantizar una única ejecución por request.
 * 
 * Responsabilidades:
 * - Interceptar cada request HTTP
 * - Extraer el token JWT del header Authorization
 * - Validar el token
 * - Poblar el SecurityContext con el usuario autenticado
 * 
 * Flujo:
 * 1. Lee el header Authorization
 * 2. Si existe y comienza con "Bearer ", extrae el token
 * 3. Valida el token y extrae el username
 * 4. Carga los detalles del usuario desde la BD
 * 5. Crea un Authentication y lo almacena en SecurityContext
 * 6. Continúa con la cadena de filtros
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UsuarioService usuarioService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        
        // Excluir endpoints públicos del filtro JWT
        String path = request.getServletPath();
        if (path.startsWith("/auth/") || 
            path.startsWith("/swagger-ui") || 
            path.startsWith("/v3/api-docs")) {
            filterChain.doFilter(request, response);
            return;
        }
        
        // Obtener el header Authorization
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String username;

        // Verificar si el header existe y tiene el formato correcto
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Extraer el token (después de "Bearer ")
        jwt = authHeader.substring(7);
        
        try {
            // Extraer el username del token
            username = jwtService.extractUsername(jwt);

            // Si hay username y no hay autenticación previa en el contexto
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                
                // Cargar los detalles del usuario desde la BD
                UserDetails userDetails = usuarioService.loadUserByUsername(username);

                // Validar el token
                if (jwtService.validateToken(jwt, userDetails)) {
                    
                    // Crear el objeto Authentication
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    
                    // Agregar detalles adicionales del request
                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );
                    
                    // Establecer la autenticación en el SecurityContext
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (Exception e) {
            // Si hay error al procesar el token, simplemente continuar sin autenticar
            // El endpoint protegido rechazará el request
            logger.error("Error procesando token JWT: " + e.getMessage());
        }

        // Continuar con la cadena de filtros
        filterChain.doFilter(request, response);
    }
}
