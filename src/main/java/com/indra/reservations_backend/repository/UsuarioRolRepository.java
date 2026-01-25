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

    @Query("SELECT ur FROM UsuarioRol ur WHERE ur.usuario = :id_usuario")    
    List<UsuarioRol> getRolesByUsuario(@Param("id_usuario") UsuarioEntity id_usuario);

    
}
