package com.indra.reservations_backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.indra.reservations_backend.models.EstadoReservaEntity;

public interface IEstadoReservaRepository extends JpaRepository<EstadoReservaEntity, Long>{

    Optional<EstadoReservaEntity> findByDescripcion(String descripcion);
}
