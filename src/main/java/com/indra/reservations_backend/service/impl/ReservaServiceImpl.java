package com.indra.reservations_backend.service.impl;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.indra.reservations_backend.exception.BadRequestException;
import com.indra.reservations_backend.exception.BussinessException;
import com.indra.reservations_backend.exception.ResourceNotFoundException;
import com.indra.reservations_backend.mappers.ReservaMapper;
import com.indra.reservations_backend.commons.models.FilterModel;
import com.indra.reservations_backend.commons.models.PaginationModel;
import com.indra.reservations_backend.dto.ReservaCalendarRequest;
import com.indra.reservations_backend.dto.ReservaListadoAdminDto;
import com.indra.reservations_backend.dto.ReservaRequestDto;
import com.indra.reservations_backend.dto.ReservaResponseDto;
import com.indra.reservations_backend.enums.FilterContext;
import com.indra.reservations_backend.models.EstadoReservaEntity;
import com.indra.reservations_backend.models.ReservaEntity;
import com.indra.reservations_backend.models.SalaEntity;
import com.indra.reservations_backend.repository.IEstadoReservaRepository;
import com.indra.reservations_backend.repository.IReservaRepository;
import com.indra.reservations_backend.repository.ISalaRepository;
import com.indra.reservations_backend.security.utils.SecurityUtils;
import com.indra.reservations_backend.service.IReservaService;

import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.StoredProcedureQuery;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class ReservaServiceImpl implements IReservaService{
    
    private final EntityManager entityManager;
    private final IReservaRepository reservaRepository;
    private final ReservaMapper reservaMapper;
    private final IEstadoReservaRepository estadoReservaRepository;
    private final ISalaRepository salaRepository;

    @Override
    public ReservaResponseDto findById(Long id) {
        ReservaEntity entity = reservaRepository.findById(id).orElseThrow(
            () -> new ResourceNotFoundException("La reserva no existe ")
        );
        return reservaMapper.toDto(entity);
        
    }

    @Override
    public PageImpl<ReservaResponseDto> getReservasByUser(PaginationModel paginationModel) {

        Long userId = SecurityUtils.getAuthenticatedUserId();

        int page = (paginationModel.getPageNumber() != null) ? paginationModel.getPageNumber() : 0;
        int rowPage = (paginationModel.getRowsPerPage() != null && paginationModel.getRowsPerPage() > 0)
                ? paginationModel.getRowsPerPage()
                : 8;

        Pageable pageable = PageRequest.of(page, rowPage);

        String jpql = """
            SELECT new com.indra.reservations_backend.dto.ReservaResponseDto(
                r.idReserva,
                s.nombre,
                r.fechaInicio,
                r.fechaFin,
                r.numeroAsistentes,
                e.descripcion,
                r.fechaCreacion
            )
            FROM ReservaEntity r
            JOIN r.sala s
            JOIN r.estado e
            WHERE r.usuario.idUsuario = :userId
        """;

        String jpqlCount = """
            SELECT COUNT(r.idReserva)
            FROM ReservaEntity r
            JOIN r.sala s
            JOIN r.estado e
            WHERE r.usuario.idUsuario = :userId
        """;

        // Agregar filtros dinámicos
        jpql = buildWhereFilters(jpql, paginationModel.getFilters(), FilterContext.USER);
        jpqlCount = buildWhereFilters(jpqlCount, paginationModel.getFilters(), FilterContext.USER);

        TypedQuery<ReservaResponseDto> querySelect =
                entityManager.createQuery(jpql, ReservaResponseDto.class);
        querySelect.setParameter("userId", userId);
        querySelect.setFirstResult((int) pageable.getOffset());
        querySelect.setMaxResults(pageable.getPageSize());

        TypedQuery<Long> queryCount =
                entityManager.createQuery(jpqlCount, Long.class);
        queryCount.setParameter("userId", userId);

        // Setear parámetros de filtros
        setFilterParams(querySelect, paginationModel.getFilters(), FilterContext.USER);
        setFilterParams(queryCount, paginationModel.getFilters(), FilterContext.USER);

        List<ReservaResponseDto> results = querySelect.getResultList();
        Long total = queryCount.getSingleResult();

        return new PageImpl<>(results, pageable, total);
    }

    @Transactional
    public List<ReservaResponseDto> getUserReservasForCalendar(
            ReservaCalendarRequest request
    ) {

        Long userId = SecurityUtils.getAuthenticatedUserId();

        String jpql = """
            SELECT new com.indra.reservations_backend.dto.ReservaResponseDto(
                r.idReserva,
                s.nombre,
                r.fechaInicio,
                r.fechaFin,
                r.numeroAsistentes,
                e.descripcion,
                r.fechaCreacion
            )
            FROM ReservaEntity r
            JOIN r.sala s
            JOIN r.estado e
            WHERE r.usuario.idUsuario = :userId
            AND r.fechaInicio < :end
            AND r.fechaFin > :start
        """;

        jpql = buildWhereFilters(jpql, request.getFilters(), FilterContext.USER);

        TypedQuery<ReservaResponseDto> query =
                entityManager.createQuery(jpql, ReservaResponseDto.class);

        query.setParameter("userId", userId);
        query.setParameter("start", request.getStart());
        query.setParameter("end", request.getEnd());

        setFilterParams(query, request.getFilters(), FilterContext.USER);

        return query.getResultList();
    }


    @Override
    public PageImpl<ReservaListadoAdminDto> getReservaListadoAdmin(
            PaginationModel paginationModel
    ) {

        int page = paginationModel.getPageNumber() != null
                ? paginationModel.getPageNumber()
                : 0;

        int rowPage = paginationModel.getRowsPerPage() != null
                && paginationModel.getRowsPerPage() > 0
                ? paginationModel.getRowsPerPage()
                : 8;

        Pageable pageable = PageRequest.of(page, rowPage);

        String jpql = """
            SELECT new com.indra.reservations_backend.dto.ReservaListadoAdminDto(
                r.idReserva,
                u.username,
                s.nombre,
                r.fechaInicio,
                r.fechaFin,
                e.descripcion
            )
            FROM ReservaEntity r
            JOIN r.usuario u
            JOIN UsuarioRol ur ON ur.usuario = u
            JOIN ur.rol rol
            JOIN r.sala s
            JOIN r.estado e
            WHERE 1 = 1
            AND rol.nombre = :rolUsuario
        """;


        String jpqlCount = """
            SELECT COUNT(r.idReserva)
            FROM ReservaEntity r
            JOIN r.usuario u
            JOIN UsuarioRol ur ON ur.usuario = u
            JOIN ur.rol rol
            JOIN r.sala s
            JOIN r.estado e
            WHERE 1 = 1
            AND rol.nombre = :rolUsuario
        """;


        // filtros dinámicos
        jpql = buildWhereFilters(jpql, paginationModel.getFilters(), FilterContext.ADMIN);
        jpqlCount = buildWhereFilters(jpqlCount, paginationModel.getFilters(), FilterContext.ADMIN);

        TypedQuery<ReservaListadoAdminDto> querySelect =
                entityManager.createQuery(jpql, ReservaListadoAdminDto.class);

        querySelect.setParameter("rolUsuario", "USER");

        querySelect.setFirstResult((int) pageable.getOffset());
        querySelect.setMaxResults(pageable.getPageSize());

        TypedQuery<Long> queryCount =
                entityManager.createQuery(jpqlCount, Long.class);
        
        queryCount.setParameter("rolUsuario", "USER");
        setFilterParams(querySelect, paginationModel.getFilters(), FilterContext.ADMIN);
        setFilterParams(queryCount, paginationModel.getFilters(), FilterContext.ADMIN);

        List<ReservaListadoAdminDto> results = querySelect.getResultList();
        Long total = queryCount.getSingleResult();

        return new PageImpl<>(results, pageable, total);
    }
        private String buildWhereFilters(
        String jpql, 
        List<FilterModel> filters,
        FilterContext filterContext
    ) {

        if (filters == null || filters.isEmpty()) {
            return jpql;
        }

        for (FilterModel filter : filters) {

            if ("estado".equalsIgnoreCase(filter.getField())) {
                jpql += " AND UPPER(e.descripcion) LIKE :paramEstado ";
            }
            if ("sala".equalsIgnoreCase(filter.getField())) {
                jpql += " AND UPPER(s.nombre) LIKE :paramSala ";
            }

            // SOLO ADMIN
            if (filterContext == FilterContext.ADMIN
                    && "usuario".equalsIgnoreCase(filter.getField())) {

                jpql += " AND UPPER(u.username) LIKE :paramUsuario ";
            }
        }

        return jpql;
    }

    @SuppressWarnings("rawtypes")
    private void setFilterParams(TypedQuery query, List<FilterModel> filters, FilterContext context) {

        if (filters == null || filters.isEmpty()) {
            return;
        }

        for (FilterModel filter : filters) {

            if ("estado".equalsIgnoreCase(filter.getField())) {
                query.setParameter(
                    "paramEstado",
                    "%" + filter.getValue().toUpperCase() + "%"
                );
            }

            if ("sala".equalsIgnoreCase(filter.getField())) {
                query.setParameter(
                    "paramSala",
                    "%" + filter.getValue().toUpperCase() + "%"
                );
            }

             // SOLO ADMIN
            if (context == FilterContext.ADMIN
                    && "usuario".equalsIgnoreCase(filter.getField())) {

                query.setParameter(
                    "paramUsuario",
                    "%" + filter.getValue().toUpperCase() + "%"
                );
            }
        }
    }


    @Transactional
    @Override
    public ReservaResponseDto save(ReservaRequestDto reserva) {
        Long userId = SecurityUtils.getAuthenticatedUserId();

        if (reserva.getFechaFin().isBefore(reserva.getFechaInicio())
            || reserva.getFechaFin().isEqual(reserva.getFechaInicio())) {
            throw new BadRequestException("La fecha fin debe ser mayor a la fecha inicio");
        }

        SalaEntity salaEntity = salaRepository.findById(reserva.getIdSala()).orElseThrow(
            () -> new BadRequestException("La sala no existe")
        );

        if (reserva.getNumeroAsistentes() > salaEntity.getCapacidad()) {
            throw new BadRequestException(
                String.format(
                    "El número de asistentes (%d) excede la capacidad de la sala (%d)",
                    reserva.getNumeroAsistentes(),
                    salaEntity.getCapacidad()
                )
            );
        }

        try {
            StoredProcedureQuery query = entityManager
                .createStoredProcedureQuery("PKG_RESERVAS.SP_CREAR_RESERVA");

            // Parámetros IN
            query.registerStoredProcedureParameter(1, Long.class, ParameterMode.IN);
            query.registerStoredProcedureParameter(2, Long.class, ParameterMode.IN);
            query.registerStoredProcedureParameter(3, LocalDateTime.class, ParameterMode.IN);
            query.registerStoredProcedureParameter(4, LocalDateTime.class, ParameterMode.IN);
            query.registerStoredProcedureParameter(5, Integer.class, ParameterMode.IN);
            query.registerStoredProcedureParameter(6, String.class, ParameterMode.IN);

            // Parámetro OUT
            query.registerStoredProcedureParameter(7, Long.class, ParameterMode.OUT);

            query.setParameter(1, userId);
            query.setParameter(2, reserva.getIdSala());
            query.setParameter(3, reserva.getFechaInicio());
            query.setParameter(4, reserva.getFechaFin());
            query.setParameter(5, reserva.getNumeroAsistentes());
            query.setParameter(6, reserva.getObservacion());

            query.execute();

            Long idReserva = (Long) query.getOutputParameterValue(7);

            return this.findById(idReserva);

        } catch (PersistenceException ex) {

            Throwable cause = ex;

            while (cause != null) {
                if (cause instanceof SQLException sqlEx) {
                    switch (sqlEx.getErrorCode()) {
                        case 20102, -20102 ->
                            throw new BussinessException(
                                "La Sala no esta disponible en ese horario");
                        case 20121, -20121 ->
                            throw new BadRequestException("EstadoReserva no existe");
                        default ->
                            throw new BussinessException(
                                "ERROR_ORACLE_" + sqlEx.getErrorCode()
                            );
                    }
                }
                cause = cause.getCause();
            }

            throw ex;
        }
    }

  
    @Transactional
    public void cancelarReserva(Long idReserva) {
        ReservaEntity reserva = reservaRepository.findById(idReserva)
            .orElseThrow(() -> new ResourceNotFoundException("La reserva no existe") );

        EstadoReservaEntity estadoCancelada = estadoReservaRepository
        .findByDescripcion("CANCELADA")
        .orElseThrow(() -> new ResourceNotFoundException("Estado CANCELADA no existe"));

        // 3. Cambiar el estado
        reserva.setEstado(estadoCancelada);

        // 4. Guardar 
        reservaRepository.save(reserva);
    }

    @Override
    public ReservaResponseDto update(Long id, ReservaRequestDto request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }

    @Override
    public ReservaResponseDto delete(Long id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }


}



