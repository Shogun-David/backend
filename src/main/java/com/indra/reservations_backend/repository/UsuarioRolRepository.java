package com.indra.reservations_backend.repository;

import com.indra.reservations_backend.models.UsuarioEntity;
import com.indra.reservations_backend.models.UsuarioRol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface UsuarioRolRepository extends JpaRepository<UsuarioRol, Long> {

    
    List<UsuarioRol> findByUsuario(UsuarioEntity usuario);

    void deleteByUsuario(UsuarioEntity usuario);
}
