package com.indra.reservations_backend.repository;

import com.indra.reservations_backend.models.UsuarioEntity;
import com.indra.reservations_backend.models.UsuarioRol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


@Repository
public interface UsuarioRolRepository extends JpaRepository<UsuarioRol, Long> {

    
    @Query("""
    SELECT ur FROM UsuarioRol ur
    JOIN FETCH ur.rol
    WHERE ur.usuario = :usuario
    """)
    List<UsuarioRol> getRolesByUsuario(@Param("usuario") UsuarioEntity usuario);
    
}
