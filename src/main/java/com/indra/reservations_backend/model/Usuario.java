package com.indra.reservations_backend.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Entidad Usuario que representa la tabla usuario en la base de datos.
 * Implementa UserDetails para integración con Spring Security.
 * 
 * Estructura de BD:
 * - id_usuario (PK)
 * - username
 * - password (BCrypt)
 * - email
 * - estado (ACTIVO/INACTIVO)
 * - fecha_creacion
 * - Relación ManyToMany con Rol a través de usuario_rol
 */
@Entity
@Table(name = "usuario")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "usuario_seq")
    @SequenceGenerator(name = "usuario_seq", sequenceName = "SEQ_USUARIO", allocationSize = 1)
    @Column(name = "id_usuario")
    private Long idUsuario;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false, length = 1)
    private String estado; // ACTIVO, INACTIVO

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;

    /**
     * Relación ManyToMany con Rol.
     * Se mapea a través de la tabla usuario_rol.
     */
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "usuario_rol",
            joinColumns = @JoinColumn(name = "id_usuario"),
            inverseJoinColumns = @JoinColumn(name = "id_rol")
    )
    private Set<Rol> roles;

    // Métodos de UserDetails para Spring Security
    
    /**
     * Convierte los roles del usuario en authorities para Spring Security.
     * Agrega el prefijo "ROLE_" requerido por Spring Security.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(rol -> new SimpleGrantedAuthority("ROLE_" + rol.getNombre()))
                .collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return "A".equalsIgnoreCase(estado);
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return "A".equalsIgnoreCase(estado);
    }

    /**
     * Retorna la lista de nombres de roles como List<String> para uso en DTOs
     */
    public List<String> getRolesList() {
        return roles.stream()
                .map(Rol::getNombre)
                .collect(Collectors.toList());
    }
}
