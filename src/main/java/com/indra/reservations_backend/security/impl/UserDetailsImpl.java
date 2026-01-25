package com.indra.reservations_backend.security.impl;
import java.util.Collection;
import java.util.List;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.indra.reservations_backend.models.UsuarioEntity;
import com.indra.reservations_backend.models.UsuarioRol;

public class UserDetailsImpl implements UserDetails {

    private UsuarioEntity usuario;
    private List<UsuarioRol> usuarioRoles;

    public UserDetailsImpl(UsuarioEntity usuario, List<UsuarioRol> usuarioRoles) {
        this.usuario = usuario;
        this.usuarioRoles = usuarioRoles;
    }

    /**
     * Retorna la lista de autoridades/roles del usuario
     * Ej: ROLE_ADMIN, ROLE_USUARIO
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (usuarioRoles.isEmpty()) {
            return List.of();
        }

        return usuarioRoles.stream()
                .map(usuarioRol -> new SimpleGrantedAuthority("ROLE_" + usuarioRol.getRol().getNombre()))
                .toList();
    }

    /**
     * Retorna la contraseña del usuario
     */
    @Override
    public String getPassword() {
        return usuario.getPassword();
    }

    /**
     * Retorna el username del usuario
     */
    @Override
    public String getUsername() {
        return usuario.getUsername();
    }

    /**
     * Valida si la cuenta NO está expirada
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * Valida si la cuenta NO está bloqueada
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * Valida si las credenciales NO están expiradas
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * Valida si el usuario está HABILITADO
     * Retorna true si estado es 'A' (ACTIVO)
     */
    @Override
    public boolean isEnabled() {
        return "A".equals(usuario.getEstado());
    }

    public UsuarioEntity getUsuario() {
    return usuario;
    }

    public List<UsuarioRol> getUsuarioRoles() {
    return usuarioRoles;
    }
}


