package com.indra.reservations_backend.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.indra.reservations_backend.dto.CancelarReservaRequestDto;
import com.indra.reservations_backend.dto.ReservaAdminDto;
import com.indra.reservations_backend.dto.ReservaDisponibilidadDto;
import com.indra.reservations_backend.dto.ReservaRequestDto;
import com.indra.reservations_backend.dto.ReservaResponseDto;
import com.indra.reservations_backend.service.IReservaService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/reservas")
public class ReservaController {

    private final IReservaService reservaService;

    /**
     * Obtiene todas las reservas del usuario autenticado.
     *
     * @param estado (opcional) filtro por estado de la reserva
     * @param page   página actual para paginación
     * @param size   tamaño de página
     * @return lista de reservas del usuario
     */
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/by-user")
    @Operation(summary = "Listar reservas por usuario", description = "Retorna las reservas del usuario autenticado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de reservas retornada correctamente"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado")
    })
    public ResponseEntity<Iterable<ReservaResponseDto>> getReservasByUser(
            @Parameter(description = "Estado de la reserva (opcional)") @RequestParam(required = false) String estado,
            @Parameter(description = "Página actual (default = 1)") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "Tamaño de página (default = 8)") @RequestParam(defaultValue = "8") int size
    ) {
        return ResponseEntity.ok(reservaService.getReservasByUser(estado, page, size));
    }

    /**
     * Obtiene todas las reservas (solo admin).
     *
     * @param estado (opcional) filtro por estado de la reserva
     * @param page   página actual para paginación
     * @param size   tamaño de página
     * @return lista de reservas para admin
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin")
    @Operation(summary = "Listar reservas (admin)", description = "Retorna todas las reservas para el administrador.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de reservas retornada correctamente"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado")
    })
    public ResponseEntity<Iterable<ReservaAdminDto>> getReservasAdmin(
            @Parameter(description = "Estado de la reserva (opcional)") @RequestParam(required = false) String estado,
            @Parameter(description = "Página actual (default = 1)") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "Tamaño de página (default = 8)") @RequestParam(defaultValue = "8") int size
    ) {
        return ResponseEntity.ok(reservaService.getReservationsAdmin(estado, page, size));
    }

    /**
     * Crea una nueva reserva.
     *
     * @param requestDto datos de la reserva
     * @return reserva creada
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @PostMapping
    @Operation(summary = "Crear reserva", description = "Crea una nueva reserva en el sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Reserva creada correctamente"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado")
    })
    public ResponseEntity<ReservaResponseDto> crearReserva(@RequestBody ReservaRequestDto requestDto) {
        return new ResponseEntity<>(reservaService.save(requestDto), HttpStatus.CREATED);
    }

    /**
     * Cancela una reserva por id.
     *
     * @param id         id de la reserva
     * @param requestDto datos para cancelar la reserva
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @DeleteMapping("/cancelar/{id}")
    @Operation(summary = "Cancelar reserva", description = "Cancela una reserva existente.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Reserva cancelada correctamente"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado"),
            @ApiResponse(responseCode = "404", description = "Reserva no encontrada")
    })
    public ResponseEntity<Void> cancelarReserva(
            @Parameter(description = "ID de la reserva") @PathVariable Long id,
            @RequestBody CancelarReservaRequestDto requestDto
    ) {
        reservaService.cancelarReserva(id, requestDto);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * Obtiene disponibilidad de una sala por mes.
     *
     * @param salaId id de la sala
     * @param year   año
     * @param month  mes
     * @return lista con disponibilidad de la sala
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping("/disponibilidad")
    @Operation(summary = "Disponibilidad sala", description = "Retorna disponibilidad de una sala en un mes específico.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Disponibilidad retornada correctamente"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado")
    })
    public ResponseEntity<List<ReservaDisponibilidadDto>> disponibilidad(
            @Parameter(description = "ID de la sala") @RequestParam Long salaId,
            @Parameter(description = "Año") @RequestParam int year,
            @Parameter(description = "Mes") @RequestParam int month
    ) {

        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.plusMonths(1);

        return ResponseEntity.ok(reservaService.getDisponibilidadSalaMes(salaId, start, end));
    }
}
