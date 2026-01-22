package com.indra.reservations_backend.security.utils;


import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.indra.reservations_backend.models.Usuario;

public class SecurityUtils {

    private SecurityUtils() {}

    public static Usuario getAuthenticatedUser() {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        return (Usuario) authentication.getPrincipal();
    }

    public static Long getAuthenticatedUserId() {
        Usuario usuario = getAuthenticatedUser();
        return usuario != null ? usuario.getIdUsuario() : null;
    }
}