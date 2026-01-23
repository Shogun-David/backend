package com.indra.reservations_backend.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Modelo para representar una sala disponible para reservar.
 */
@Entity
@Table(name = "sala")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Sala {
    
    @Id
    @SequenceGenerator(name = "seq_sala", sequenceName = "seq_sala", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_sala")
    @Column(name = "id_sala")
    private Long idSala;
    
    @Column(name = "nombre", nullable = false)
    private String nombre;
    
    @Column(name = "descripcion")
    private String descripcion;
    
    @Column(name = "capacidad", nullable = false)
    private Integer capacidad;
    
    @Column(name = "ubicacion")
    private String ubicacion;
    
    @Column(name = "estado", nullable = false)
    private String estado; // A = Activa, I = Inactiva
    
    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;
    
    @Column(name = "fecha_modificacion")
    private LocalDateTime fechaModificacion;
}
