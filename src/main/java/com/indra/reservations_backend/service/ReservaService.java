package com.indra.reservations_backend.service;

import com.indra.reservations_backend.dto.ReservaRequestDto;
import com.indra.reservations_backend.dto.ReservaResponseDto;
import com.indra.reservations_backend.model.Reserva;
import com.indra.reservations_backend.model.Usuario;
import com.indra.reservations_backend.repository.ReservaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio para gestionar reservas.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ReservaService {

    private final ReservaRepository reservaRepository;
    private final UsuarioService usuarioService;

    /**
     * Obtiene todas las reservas de un usuario.
     * 
     * @param username El username del usuario
     * @return Lista de reservas del usuario
     */
    @Transactional(readOnly = true)
    public List<ReservaResponseDto> getMisReservas(String username) {
        Usuario usuario = usuarioService.findByUsername(username);
        
        List<Reserva> reservas = reservaRepository.findByUsuario(usuario);
        log.info("Cargadas {} reservas para usuario: {}", reservas.size(), username);
        
        return reservas.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene todas las reservas del sistema (solo para ADMIN).
     * 
     * @return Lista de todas las reservas
     */
    @Transactional(readOnly = true)
    public List<ReservaResponseDto> getAllReservas() {
        List<Reserva> reservas = reservaRepository.findAll();
        log.info("Cargadas {} reservas del sistema (ADMIN)", reservas.size());
        
        return reservas.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Crea una nueva reserva.
     * 
     * @param request Datos de la reserva
     * @param username Username del usuario que crea la reserva
     * @return Reserva creada
     */
    @Transactional
    public ReservaResponseDto createReserva(ReservaRequestDto request, String username) {
        Usuario usuario = usuarioService.findByUsername(username);
        
        Reserva reserva = Reserva.builder()
                .usuario(usuario)
                .fechaInicio(request.getFechaInicio())
                .fechaFin(request.getFechaFin())
                .proposito(request.getProposito())
                .estado("A") // Activa por defecto
                .fechaCreacion(LocalDateTime.now())
                .build();
        
        Reserva guardada = reservaRepository.save(reserva);
        log.info("Reserva creada: ID {} por usuario: {}", guardada.getIdReserva(), username);
        
        return toDto(guardada);
    }

    /**
     * Cancela una reserva.
     * 
     * @param idReserva ID de la reserva a cancelar
     * @param username Username del usuario que intenta cancelar
     * @param isAdmin Si es admin puede cancelar cualquier reserva
     */
    @Transactional
    public void cancelarReserva(Long idReserva, String username, boolean isAdmin) {
        Reserva reserva = reservaRepository.findById(idReserva)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));
        
        // Verificar permisos
        if (!isAdmin && !reserva.getUsuario().getUsername().equals(username)) {
            throw new RuntimeException("No tienes permiso para cancelar esta reserva");
        }
        
        reserva.setEstado("C"); // Cancelada
        reserva.setFechaModificacion(LocalDateTime.now());
        reservaRepository.save(reserva);
        
        log.info("Reserva {} cancelada por usuario: {}", idReserva, username);
    }

    /**
     * Convierte una entidad Reserva a DTO.
     */
    private ReservaResponseDto toDto(Reserva reserva) {
        return ReservaResponseDto.builder()
                .idReserva(reserva.getIdReserva())
                .sala(reserva.getSala() != null ? reserva.getSala().getNombre() : null)
                .fechaInicio(reserva.getFechaInicio())
                .fechaFin(reserva.getFechaFin())
                .estado(reserva.getEstado())
                .fechaCreacion(reserva.getFechaCreacion())
                .build();
    }
}
