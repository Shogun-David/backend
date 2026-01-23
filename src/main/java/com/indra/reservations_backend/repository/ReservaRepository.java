package com.indra.reservations_backend.repository;

import com.indra.reservations_backend.model.Reserva;
import com.indra.reservations_backend.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio JPA para gestionar la entidad Reserva.
 */
@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {
    
    /**
     * Busca todas las reservas de un usuario específico.
     * 
     * @param usuario El usuario propietario de las reservas
     * @return Lista de reservas del usuario
     */
    List<Reserva> findByUsuario(Usuario usuario);
    
    /**
     * Busca reservas activas de un usuario.
     * 
     * @param usuario El usuario
     * @param estado El estado (ej: "A" para activas)
     * @return Lista de reservas activas del usuario
     */
    List<Reserva> findByUsuarioAndEstado(Usuario usuario, String estado);
}
