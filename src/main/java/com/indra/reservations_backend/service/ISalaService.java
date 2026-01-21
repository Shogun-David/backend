package com.indra.reservations_backend.service;

import com.indra.reservations_backend.commons.interfaces.ICrudCommonsDto;
import com.indra.reservations_backend.commons.interfaces.IPaginationCommons;
import com.indra.reservations_backend.dto.SalaRequestDto;
import com.indra.reservations_backend.dto.SalaResponseDto;

public interface ISalaService
        extends ICrudCommonsDto<SalaRequestDto, SalaResponseDto, Long>, IPaginationCommons<SalaResponseDto> {

}
