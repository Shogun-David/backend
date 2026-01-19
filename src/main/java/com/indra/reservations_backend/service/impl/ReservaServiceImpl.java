package com.indra.reservations_backend.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Service;

import com.indra.reservations_backend.dto.ReservaRequestDto;
import com.indra.reservations_backend.dto.ReservaResponseDto;
import com.indra.reservations_backend.service.IReservaService;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class ReservaServiceImpl implements IReservaService{

    private final EntityManager entityManager;
    
    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<ReservaResponseDto> getReservasByUser(Long userId) {
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
            .withCatalogName("PKG_RESERVAS")
            .withProcedureName("SP_LISTAR_POR_USUARIO")
            .returningResultSet(
                "CUR_RESERVAS",
                new BeanPropertyRowMapper<>(ReservaResponseDto.class)
            );

        Map<String, Object> result = jdbcCall.execute(userId);

        List<ReservaResponseDto> reservas =
            (List<ReservaResponseDto>) result.get("CUR_RESERVAS");
       
        return reservas;
       
    }

    @Override
    public ReservaResponseDto save(ReservaRequestDto reserva) {
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
            .withCatalogName("PKG_RESERVAS")
            .withProcedureName("SP_CREAR_RESERVA")
            .returningResultSet(
                "CUR_RESERVA_NUEVA",
                new BeanPropertyRowMapper<>(ReservaResponseDto.class))
            ;
        Map<String, Object> result = jdbcCall.execute(
                reserva.getIdSala(),
                reserva.getFechaInicio(),
                reserva.getFechaFin(),
                reserva.getObservacion()
        );

        List<ReservaResponseDto> reservas =
            (List<ReservaResponseDto>) result.get("CUR_RESERVA_NUEVA");
       
        return reservas.get(0);
    }

    @Override
    public ReservaResponseDto update(Long id, ReservaRequestDto request) {
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }

    @Override
    public ReservaResponseDto findById(Long id) {
        throw new UnsupportedOperationException("Unimplemented method 'findById'");
    }

    @Override
    public ReservaResponseDto delete(Long id) {

        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }

    @Override
    public ReservaResponseDto cancelarReserva(Long idReserva) {
        throw new UnsupportedOperationException("Unimplemented method 'cancelReserva'");
    }


}
