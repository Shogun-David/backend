package com.indra.reservations_backend.security.filter;

import com.indra.reservations_backend.security.jwt.JwtService;
import com.indra.reservations_backend.service.IUsuarioService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;


@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final IUsuarioService usuarioService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        
        // Excluir endpoints públicos del filtro JWT
        String path = request.getServletPath();
        log.debug("JwtAuthenticationFilter - Path: {}", path);
        
        if (path.startsWith("/api/auth") || 
            path.equals("/api/usuarios") ||
            path.startsWith("/swagger-ui") ||
            path.startsWith("/v3/api-docs") ||
            path.startsWith("/swagger-resources") ||
            path.startsWith("/webjars") ||
            path.equals("/swagger-ui.html")) {
            log.debug("JwtAuthenticationFilter - Skipping JWT validation for public path: {}", path);
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

                // Validar el token (verifica que no esté expirado)
                if (jwtService.validateToken(jwt)) {
                    
                    // Crear el objeto Authentication con roles del token
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
                    log.debug("JwtAuthenticationFilter - Usuario autenticado: {} con roles: {}", 
                            username, userDetails.getAuthorities());
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
