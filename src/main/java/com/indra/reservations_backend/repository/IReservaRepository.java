package com.indra.reservations_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.indra.reservations_backend.models.ReservaEntity;

public interface IReservaRepository extends JpaRepository<ReservaEntity, Long>{

    

}
