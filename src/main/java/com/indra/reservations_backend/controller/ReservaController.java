package com.indra.reservations_backend.controller;

import com.indra.reservations_backend.dto.ReservaAdminDto;
import com.indra.reservations_backend.dto.ReservaRequestDto;
import com.indra.reservations_backend.dto.ReservaResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador para gestión de reservas.
 * 
 * ADMIN y USUARIO pueden:
 * - Ver sus propias reservas
 * - Crear reservas
 * - Cancelar sus propias reservas
 * 
 * SOLO ADMIN puede:
 * - Ver todas las reservas del sistema
 * - Cancelar cualquier reserva
 * - Acceder a estadísticas y reportes
 * 
 * Usa @PreAuthorize para distinguir permisos por rol.
 */
@RestController
@RequestMapping("/api/reservas")
@RequiredArgsConstructor
@Tag(name = "Reservas", description = "Gestión de reservas de salas")
@SecurityRequirement(name = "Bearer Authentication")
public class ReservaController {

    /**
     * Obtiene las reservas del usuario autenticado.
     * Accesible para ADMIN y USUARIO (cada uno ve sus propias reservas).
     * 
     * @param authentication Información del usuario autenticado
     * @return Lista de reservas del usuario
     */
    @GetMapping("/mis-reservas")
    @PreAuthorize("hasAnyRole('ADMIN', 'USUARIO')")
    @Operation(
            summary = "Ver mis reservas",
            description = "Obtiene las reservas del usuario autenticado. Requiere autenticación."
    )
    public ResponseEntity<String> getMisReservas(Authentication authentication) {
        String username = authentication.getName();
        // TODO: Implementar servicio de reservas
        return ResponseEntity.ok("Reservas de " + username + " (implementar servicio)");
    }

    /**
     * Obtiene TODAS las reservas del sistema.
     * SOLO ADMIN puede ver todas las reservas.
     * 
     * @return Lista completa de reservas
     */
    @GetMapping("/todas")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Ver todas las reservas",
            description = "Obtiene todas las reservas del sistema. Requiere rol ADMIN."
    )
    public ResponseEntity<String> getAllReservas() {
        // TODO: Implementar servicio de reservas
        return ResponseEntity.ok("Todas las reservas del sistema (implementar servicio)");
    }

    /**
     * Crea una nueva reserva.
     * Accesible para ADMIN y USUARIO.
     * 
     * @param request Datos de la reserva
     * @param authentication Usuario que crea la reserva
     * @return Reserva creada
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'USUARIO')")
    @Operation(
            summary = "Crear reserva",
            description = "Crea una nueva reserva. Requiere autenticación (ADMIN o USUARIO)."
    )
    public ResponseEntity<String> createReserva(
            @RequestBody ReservaRequestDto request,
            Authentication authentication) {
        String username = authentication.getName();
        // TODO: Implementar lógica de creación
        return ResponseEntity.ok("Reserva creada por " + username + ": " + request.toString());
    }

    /**
     * Cancela una reserva.
     * - USUARIO puede cancelar solo sus propias reservas
     * - ADMIN puede cancelar cualquier reserva
     * 
     * @param id ID de la reserva
     * @param authentication Usuario que intenta cancelar
     * @return Confirmación de cancelación
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USUARIO')")
    @Operation(
            summary = "Cancelar reserva",
            description = "Cancela una reserva. USUARIO solo puede cancelar sus propias reservas, ADMIN puede cancelar cualquiera."
    )
    public ResponseEntity<String> cancelarReserva(
            @PathVariable Long id,
            Authentication authentication) {
        String username = authentication.getName();
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        
        // TODO: Implementar lógica de cancelación con validación:
        // - Si es ADMIN, puede cancelar cualquier reserva
        // - Si es USUARIO, verificar que la reserva le pertenece
        
        return ResponseEntity.ok("Reserva " + id + " cancelada por " + username + 
                " (isAdmin: " + isAdmin + ")");
    }

    /**
     * Obtiene estadísticas de reservas.
     * SOLO ADMIN puede ver estadísticas completas.
     * 
     * @return Estadísticas del sistema
     */
    @GetMapping("/estadisticas")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Ver estadísticas",
            description = "Obtiene estadísticas de uso de salas. Requiere rol ADMIN."
    )
    public ResponseEntity<String> getEstadisticas() {
        // TODO: Implementar servicio de estadísticas
        return ResponseEntity.ok("Estadísticas del sistema (implementar servicio)");
    }
}
