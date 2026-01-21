package com.indra.reservations_backend.security.aspect;

import com.indra.reservations_backend.model.Usuario;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

/**
 * Aspecto (AOP) para validar que usuarios USUARIO solo accedan si tienen email @usuario
 * 
 * Se aplica a endpoints del panel de usuario.
 * Si el usuario tiene rol USUARIO pero su email NO es @usuario, retorna 403 FORBIDDEN.
 */
@Aspect
@Component
public class ValidarDominioEmailAspect {

    /**
     * Valida que usuarios con rol USUARIO tengan email @usuario
     * Se ejecuta ANTES de cualquier método en controladores del panel usuario
     * 
     * @param joinPoint Información del método interceptado
     * @throws ResponseStatusException 403 si el email no cumple
     */
    @Before("execution(* com.indra.reservations_backend.controller.PanelUsuarioController.*(..))")
    public void validarDominioEmail(JoinPoint joinPoint) {
        // Obtener el usuario autenticado
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication != null && authentication.getPrincipal() instanceof Usuario) {
            Usuario usuario = (Usuario) authentication.getPrincipal();
            
            // Validar que tenga rol USUARIO
            boolean esUsuario = usuario.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_USUARIO"));
            
            if (esUsuario) {
                // 🔒 Validar que el email sea @usuario
                if (!usuario.getEmail().toLowerCase().endsWith("@usuario")) {
                    throw new ResponseStatusException(
                            HttpStatus.FORBIDDEN,
                            "Acceso denegado: El email debe ser @usuario para acceder al panel de usuario"
                    );
                }
            }
        }
    }
}
