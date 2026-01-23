package com.indra.reservations_backend.service;


import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.PageImpl;


import com.indra.reservations_backend.dto.CancelarReservaRequestDto;
import com.indra.reservations_backend.dto.ReservaAdminDto;
import com.indra.reservations_backend.dto.ReservaDisponibilidadDto;
import com.indra.reservations_backend.dto.ReservaRequestDto;
import com.indra.reservations_backend.dto.ReservaResponseDto;

public interface IReservaService{
    
    ReservaResponseDto findById(Long id);

    ReservaResponseDto save(ReservaRequestDto requestDto);

    PageImpl<ReservaResponseDto> getReservasByUser(String estado, int page, int size);

    PageImpl<ReservaAdminDto> getReservationsAdmin(String estado, int page, int size);

    void cancelarReserva(Long idReserva, CancelarReservaRequestDto requestDto);

    List<ReservaDisponibilidadDto> getDisponibilidadSalaMes(
        Long idSala,
        LocalDate fechaInicio,
        LocalDate fechaFin
    );
}
