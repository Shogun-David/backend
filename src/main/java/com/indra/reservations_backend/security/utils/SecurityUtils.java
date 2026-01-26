package com.indra.reservations_backend.security.utils;


import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.indra.reservations_backend.models.UsuarioEntity;

public class SecurityUtils {

    private SecurityUtils() {}

    public static UsuarioEntity getAuthenticatedUser() {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        return (UsuarioEntity) authentication.getPrincipal();
    }

    public static Long getAuthenticatedUserId() {
        UsuarioEntity usuario = getAuthenticatedUser();
        return usuario != null ? usuario.getIdUsuario() : null;
    }

    public static boolean hasRole(String role) {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        String roleName = role.startsWith("ROLE_") ? role : "ROLE_" + role;

        return authentication.getAuthorities().stream()
                .anyMatch(auth -> roleName.equals(auth.getAuthority()));
    }

}