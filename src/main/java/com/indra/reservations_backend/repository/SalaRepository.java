package com.indra.reservations_backend.repository;

import com.indra.reservations_backend.model.Sala;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio JPA para gestionar la entidad Sala.
 */
@Repository
public interface SalaRepository extends JpaRepository<Sala, Long> {
    
    /**
     * Busca una sala por su nombre.
     * 
     * @param nombre Nombre de la sala
     * @return Optional con la sala si existe
     */
    Optional<Sala> findByNombre(String nombre);
    
    /**
     * Busca todas las salas activas.
     * 
     * @param estado El estado (ej: "A" para activas)
     * @return Lista de salas activas
     */
    List<Sala> findByEstado(String estado);
}
