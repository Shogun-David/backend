package com.indra.reservations_backend.service;


import org.springframework.data.domain.PageImpl;

import com.indra.reservations_backend.commons.interfaces.ICrudCommonsDto;
import com.indra.reservations_backend.dto.CancelarReservaRequestDto;
import com.indra.reservations_backend.dto.ReservaRequestDto;
import com.indra.reservations_backend.dto.ReservaResponseDto;

public interface IReservaService extends ICrudCommonsDto<ReservaRequestDto, ReservaResponseDto, Long>{
    
    PageImpl<ReservaResponseDto> getReservasByUser(Long userId, String estado, int page, int size);

    void cancelarReserva(Long idReserva, CancelarReservaRequestDto requestDto);
}
