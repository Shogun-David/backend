package com.indra.reservations_backend.controller;

import com.indra.reservations_backend.dto.SalaRequestDto;
import com.indra.reservations_backend.dto.SalaResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador para gestión de salas.
 * 
 * ADMIN y USUARIO pueden:
 * - Ver todas las salas
 * - Ver detalles de una sala
 * 
 * SOLO ADMIN puede:
 * - Crear salas
 * - Actualizar salas
 * - Eliminar salas
 * 
 * Usa @PreAuthorize para distinguir permisos por rol.
 */
@RestController
@RequestMapping("/api/salas")
@RequiredArgsConstructor
@Tag(name = "Salas", description = "Gestión de salas de reuniones")
@SecurityRequirement(name = "Bearer Authentication")
public class SalaController {

    /**
     * Obtiene todas las salas disponibles.
     * Accesible para ADMIN y USUARIO.
     * 
     * @return Lista de salas
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'USUARIO')")
    @Operation(
            summary = "Listar todas las salas",
            description = "Obtiene la lista de salas disponibles. Requiere autenticación (ADMIN o USUARIO)."
    )
    public ResponseEntity<String> getAllSalas() {
        // TODO: Implementar servicio de salas
        return ResponseEntity.ok("Lista de salas (implementar servicio)");
    }

    /**
     * Obtiene los detalles de una sala específica.
     * Accesible para ADMIN y USUARIO.
     * 
     * @param id ID de la sala
     * @return Datos de la sala
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USUARIO')")
    @Operation(
            summary = "Obtener sala por ID",
            description = "Obtiene los detalles de una sala específica. Requiere autenticación (ADMIN o USUARIO)."
    )
    public ResponseEntity<String> getSalaById(@PathVariable Long id) {
        // TODO: Implementar servicio de salas
        return ResponseEntity.ok("Detalles de sala " + id + " (implementar servicio)");
    }

    /**
     * Crea una nueva sala.
     * SOLO ADMIN puede crear salas.
     * 
     * @param request Datos de la nueva sala
     * @return Sala creada
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Crear nueva sala",
            description = "Crea una nueva sala en el sistema. Requiere rol ADMIN."
    )
    public ResponseEntity<String> createSala(@RequestBody SalaRequestDto request) {
        // TODO: Implementar lógica de creación
        return ResponseEntity.ok("Sala creada: " + request.toString());
    }

    /**
     * Actualiza una sala existente.
     * SOLO ADMIN puede actualizar salas.
     * 
     * @param id ID de la sala a actualizar
     * @param request Nuevos datos de la sala
     * @return Sala actualizada
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Actualizar sala",
            description = "Actualiza los datos de una sala existente. Requiere rol ADMIN."
    )
    public ResponseEntity<String> updateSala(
            @PathVariable Long id,
            @RequestBody SalaRequestDto request) {
        // TODO: Implementar lógica de actualización
        return ResponseEntity.ok("Sala " + id + " actualizada: " + request.toString());
    }

    /**
     * Elimina una sala.
     * SOLO ADMIN puede eliminar salas.
     * 
     * @param id ID de la sala a eliminar
     * @return Confirmación de eliminación
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Eliminar sala",
            description = "Elimina una sala del sistema. Requiere rol ADMIN."
    )
    public ResponseEntity<String> deleteSala(@PathVariable Long id) {
        // TODO: Implementar lógica de eliminación
        return ResponseEntity.ok("Sala " + id + " eliminada");
    }
}
