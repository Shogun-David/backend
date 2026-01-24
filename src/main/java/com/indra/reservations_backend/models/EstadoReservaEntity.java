package com.indra.reservations_backend.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
@Table(name = "ESTADO_RESERVA")
public class EstadoReservaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "estado_reserva_seq")
    @SequenceGenerator(name = "estado_reserva_seq", sequenceName = "SEQ_ESTADO_RESERVA", allocationSize = 1)

    
    @Column(name = "ID_ESTADO")
    private Long idEstado;

    @Column(name = "DESCRIPCION", nullable = false, unique = true, length = 30)
    private String descripcion;
}






