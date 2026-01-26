package com.indra.reservations_backend.service;


import java.util.List;

import org.springframework.data.domain.PageImpl;

import com.indra.reservations_backend.commons.interfaces.ICrudCommonsDto;
import com.indra.reservations_backend.commons.models.PaginationModel;
import com.indra.reservations_backend.dto.ReservaCalendarRequest;
import com.indra.reservations_backend.dto.ReservaListadoAdminDto;
import com.indra.reservations_backend.dto.ReservaRequestDto;
import com.indra.reservations_backend.dto.ReservaResponseDto;

public interface IReservaService extends ICrudCommonsDto<ReservaRequestDto, ReservaResponseDto, Long>{
    
    ReservaResponseDto findById(Long id);

    ReservaResponseDto save(ReservaRequestDto requestDto);

    PageImpl<ReservaResponseDto> getReservasByUser(PaginationModel paginationModel);

    PageImpl<ReservaListadoAdminDto> getReservaListadoAdmin(PaginationModel paginationModel);

    void cancelarReserva(Long idReserva);

    List<ReservaResponseDto> getUserReservasForCalendar(ReservaCalendarRequest request);

}
