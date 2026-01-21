package com.indra.reservations_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.indra.reservations_backend.models.SalaEntity;

public interface ISalaRepository extends JpaRepository<SalaEntity, Long> {

    

}
