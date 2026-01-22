package com.indra.reservations_backend.service.impl;

import java.sql.SQLException;
import java.sql.Types;
import java.util.List;
import java.util.Map;

import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Service;

import com.indra.reservations_backend.commons.exception.BadRequestException;
import com.indra.reservations_backend.commons.exception.BussinessException;
import com.indra.reservations_backend.commons.exception.ResourceNotFoundException;
import com.indra.reservations_backend.dto.CancelarReservaRequestDto;
import com.indra.reservations_backend.dto.ReservaRequestDto;
import com.indra.reservations_backend.dto.ReservaResponseDto;
import com.indra.reservations_backend.security.utils.SecurityUtils;
import com.indra.reservations_backend.service.IReservaService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class ReservaServiceImpl implements IReservaService{
    
    private final JdbcTemplate jdbcTemplate;

    @Override
    public ReservaResponseDto findById(Long id) {
        Long userId = SecurityUtils.getAuthenticatedUserId();

        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
            .withCatalogName("PKG_RESERVAS")
            .withProcedureName("SP_OBTENER_POR_ID")
            .returningResultSet(
                "CUR_RESERVA",
                new BeanPropertyRowMapper<>(ReservaResponseDto.class)
            );

        Map<String, Object> result = jdbcCall.execute(id, userId);

        List<ReservaResponseDto> reservas =
            (List<ReservaResponseDto>) result.get("CUR_RESERVA");
        
        if (reservas == null || reservas.isEmpty()) {
            throw new ResourceNotFoundException("Reserva no encontrada");
        }
        return reservas.get(0);
    }

    @Override
    public PageImpl<ReservaResponseDto> getReservasByUser(
            String estado,
            int page,
            int size
    ) {
        Long userId = SecurityUtils.getAuthenticatedUserId();
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
            .withCatalogName("PKG_RESERVAS")
            .withProcedureName("SP_LISTAR_POR_USUARIO")
            .declareParameters(
                new SqlParameter("p_user_id", Types.NUMERIC),
                new SqlParameter("p_estado", Types.VARCHAR),
                new SqlParameter("p_page", Types.NUMERIC),
                new SqlParameter("p_page_size", Types.NUMERIC),
                new SqlOutParameter("p_total", Types.NUMERIC)
            )
            .returningResultSet(
                "CUR_RESERVAS",
                new BeanPropertyRowMapper<>(ReservaResponseDto.class)
            );

        MapSqlParameterSource params = new MapSqlParameterSource()
            .addValue("p_user_id", userId)
            .addValue("p_estado", estado)
            .addValue("p_page", page)
            .addValue("p_page_size", size);

        Map<String, Object> result = jdbcCall.execute(params);

        @SuppressWarnings("unchecked")
        List<ReservaResponseDto> reservas =
            (List<ReservaResponseDto>) result.get("CUR_RESERVAS");

        long totalRegistros =
            ((Number) result.get("p_total")).longValue();

        Pageable pageable = PageRequest.of(page - 1, size);

        return new PageImpl<>(reservas, pageable, totalRegistros);
    }



    @Transactional
    @Override
    public ReservaResponseDto save(ReservaRequestDto reserva) {
        Long userId = SecurityUtils.getAuthenticatedUserId();

        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
            .withCatalogName("PKG_RESERVAS")
            .withProcedureName("SP_CREAR_RESERVA")
            .declareParameters(
                new SqlParameter("p_id_usuario", Types.NUMERIC),
                new SqlParameter("p_id_sala", Types.NUMERIC),
                new SqlParameter("p_fecha_inicio", Types.TIMESTAMP),
                new SqlParameter("p_fecha_fin", Types.TIMESTAMP),
                new SqlParameter("p_observacion", Types.VARCHAR),
                new SqlOutParameter("p_id_reserva", Types.NUMERIC)
            );

        try {
            Map<String, Object> result = jdbcCall.execute(
                userId,
                reserva.getIdSala(),
                reserva.getFechaInicio(),
                reserva.getFechaFin(),
                reserva.getObservacion()
            );

            Long idReserva = ((Number) result.get("p_id_reserva")).longValue();

            return this.findById(idReserva);

        } catch (DataAccessException ex) {

            Throwable root = ex.getCause();

            if (root instanceof SQLException sqlEx) {
                switch (sqlEx.getErrorCode()) {

                    case 20101 -> 
                        throw new BadRequestException(
                            "Fecha invalida"
                        );

                    case 20104 -> 
                        throw new BadRequestException(
                            "Sala no encontrada"
                    );
                    case 20102 -> 
                        throw new BussinessException(
                            "Sala no disponible"
                    );
                    default -> throw new BussinessException("ERROR_TECNICO");
                }
            }

            throw ex; // error técnico real
        }

        
    }


   @Override
    public ReservaResponseDto update(Long id, ReservaRequestDto reserva) {

        Long userId = SecurityUtils.getAuthenticatedUserId(); // luego vendrá del token

        try {

            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withCatalogName("PKG_RESERVAS")
                .withProcedureName("SP_ACTUALIZAR_RESERVA")
                .declareParameters(
                    new SqlParameter("p_id_reserva", Types.NUMERIC),
                    new SqlParameter("p_id_usuario", Types.NUMERIC),
                    new SqlParameter("p_id_sala", Types.NUMERIC),
                    new SqlParameter("p_fecha_inicio", Types.TIMESTAMP),
                    new SqlParameter("p_fecha_fin", Types.TIMESTAMP),
                    new SqlParameter("p_observacion", Types.VARCHAR)
                );

            MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("p_id_reserva", id)
                .addValue("p_id_usuario", userId)
                .addValue("p_id_sala", reserva.getIdSala())
                .addValue("p_fecha_inicio", reserva.getFechaInicio())
                .addValue("p_fecha_fin", reserva.getFechaFin())
                .addValue("p_observacion", reserva.getObservacion());

            jdbcCall.execute(params);

            return this.findById(id);

        } catch (DataAccessException ex) {

            SQLException sqlEx = (SQLException) ex.getCause();

            int errorCode = sqlEx.getErrorCode();
            String message = sqlEx.getMessage();

            // Errores de negocio (los que tú definiste)
            if (errorCode >= 20100 && errorCode <= 20999) {
                throw new BussinessException(message);
            }

            // Error técnico
            throw new BussinessException("ERROR_ACTUALIZAR_RESERVA" + ex);
        }
    }

    @Transactional
    public void cancelarReserva(Long idReserva, CancelarReservaRequestDto requestDto) {
        Long userId = SecurityUtils.getAuthenticatedUserId();

        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
            .withCatalogName("PKG_RESERVAS")
            .withProcedureName("SP_CANCELAR_RESERVA")
            .declareParameters(
                new SqlParameter("p_id_reserva", Types.NUMERIC),
                new SqlParameter("p_id_usuario", Types.NUMERIC),
                new SqlParameter("p_motivo", Types.VARCHAR)
            );

        try {
            jdbcCall.execute(idReserva, userId, requestDto.getMotivo());
         } catch (DataAccessException ex) {

            Throwable root = ex.getCause();

            if (root instanceof SQLException sqlEx) {
                switch (sqlEx.getErrorCode()) {

                    case 20105 -> 
                        throw new BadRequestException(
                            "Reserva ya cancelada o finalizada"
                        );

                    case 20104 -> 
                        throw new BadRequestException(
                            "Sala no encontrada"
                    );
                    default -> throw new BussinessException("ERROR_TECNICO");
                }
            }

            throw ex; // error técnico real
        }

        

    }

    @Override
    public ReservaResponseDto delete(Long id) {

        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }


}
