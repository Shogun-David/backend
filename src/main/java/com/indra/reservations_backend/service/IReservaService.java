package com.indra.reservations_backend.service;

import java.util.List;

import com.indra.reservations_backend.commons.interfaces.ICrudCommonsDto;
import com.indra.reservations_backend.dto.ReservaRequestDto;
import com.indra.reservations_backend.dto.ReservaResponseDto;

public interface IReservaService extends ICrudCommonsDto<ReservaRequestDto, ReservaResponseDto, Long>{
    
    List<ReservaResponseDto> getReservasByUser(Long userId);

    ReservaResponseDto cancelarReserva(Long idReserva);
}
