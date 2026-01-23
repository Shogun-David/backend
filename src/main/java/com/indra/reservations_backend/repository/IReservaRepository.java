package com.indra.reservations_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;

import com.indra.reservations_backend.models.ReservaEntity;

public interface IReservaRepository extends JpaRepository<ReservaEntity, Long>{

    @Procedure(procedureName = "SALA_PKG.EXISTE_RESERVA_ACTIVA_SALA")
    Integer existeReservaActivaSala(
            @Param("P_ID_SALA") Long idSala
    );

}
