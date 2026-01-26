package com.indra.reservations_backend.controller;

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
import org.springframework.web.bind.annotation.RestController;

import com.indra.reservations_backend.commons.models.PaginationModel;
import com.indra.reservations_backend.dto.ReservaCalendarRequest;
import com.indra.reservations_backend.dto.ReservaListadoAdminDto;
import com.indra.reservations_backend.dto.ReservaRequestDto;
import com.indra.reservations_backend.dto.ReservaResponseDto;
import com.indra.reservations_backend.service.IReservaService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/reservas")
@Tag(name = "Reservas", description = "Gestión de reservas")
@SecurityRequirement(name = "Bearer Authentication")
public class ReservaController {

    private final IReservaService reservaService;

    
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<ReservaResponseDto> getReservaById(@PathVariable Long id) {
        return ResponseEntity.ok(reservaService.findById(id));
    }


    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @PostMapping("/by-user")
    @Operation(summary = "Listar reservas por usuario", description = "Retorna las reservas del usuario autenticado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de reservas retornada correctamente"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado")
    })
    public ResponseEntity<Iterable<ReservaResponseDto>> getReservasByUser(@RequestBody PaginationModel paginationModel) {
        return ResponseEntity.ok(reservaService.getReservasByUser(paginationModel));
    }

    
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @PostMapping("/by-user/calendar")
    public ResponseEntity<List<ReservaResponseDto>> getUserReservasForCalendar(
            @Valid @RequestBody ReservaCalendarRequest request
    ) {
        return ResponseEntity.ok(reservaService.getUserReservasForCalendar(request));
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
    @PostMapping("/admin")
    @Operation(summary = "Listar reservas (admin)", description = "Retorna todas las reservas para el administrador.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de reservas retornada correctamente"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado")
    })
    public ResponseEntity<Iterable<ReservaListadoAdminDto>> getReservaListadoAdmin(@RequestBody PaginationModel paginationModel ) {
        return ResponseEntity.ok(reservaService.getReservaListadoAdmin(paginationModel));
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
    public ResponseEntity<ReservaResponseDto> crearReserva(@Valid @RequestBody ReservaRequestDto requestDto) {
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
            @Parameter(description = "ID de la reserva") @PathVariable Long id
    ) {
        reservaService.cancelarReserva(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    
}