package com.indra.reservations_backend.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "ROL")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Rol {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator ="rol_seq")
    @SequenceGenerator(name ="rol_seq", sequenceName = "SEQ_ROL", allocationSize = 1)

    @Column(name = "ID_ROL")
    private Long idRol;

    @Column(nullable = false, unique = true, length = 50)
    private String nombre;
}
