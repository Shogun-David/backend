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
@Table(name = "sala")
public class SalaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sala_seq")
    @SequenceGenerator(name = "sala_seq", sequenceName = "SEQ_SALA", allocationSize = 1)
    @Column(name = "id_sala")
    private Long idSala;

    private String nombre;

    private Integer capacidad;

    private String ubicacion;

    private String estado;
}
