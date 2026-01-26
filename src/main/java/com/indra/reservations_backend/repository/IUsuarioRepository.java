package com.indra.reservations_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.indra.reservations_backend.models.UsuarioEntity;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio JPA para gestionar la entidad Usuario.
 * 
 * Proporciona:
 * - Operaciones CRUD básicas heredadas de JpaRepository
 * - Búsqueda de usuario por username (necesario para autenticación)
 */
@Repository
public interface IUsuarioRepository extends JpaRepository<UsuarioEntity, Long> {
    /* 
    @Query("SELECT u FROM Usuario u JOIN u.roles r WHERE r.nombre = :rolNombre")
    List<UsuarioEntity> findAllByRoleNombre(@Param("rolNombre") String rolNombre);
    */
    /**
     * Busca un usuario por su username.
     * Se utiliza para cargar el usuario durante el proceso de autenticación.
     * 
     * @param username El nombre de usuario
     * @return Optional con el usuario si existe
     */
    Optional<UsuarioEntity> findByUsername(String username);
    
    /**
     * Verifica si existe un usuario con el username dado.
     * Útil para validaciones de registro.
     * 
     * @param username El nombre de usuario
     * @return true si existe, false en caso contrario
     */
    boolean existsByUsername(String username);
}
