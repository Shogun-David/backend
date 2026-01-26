package com.indra.reservations_backend.service.impl;

import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.indra.reservations_backend.commons.dto.EstadoSala;

import com.indra.reservations_backend.exception.BussinessException;
import com.indra.reservations_backend.exception.ResourceNotFoundException;

import com.indra.reservations_backend.commons.models.FilterModel;
import com.indra.reservations_backend.commons.models.PaginationModel;
import com.indra.reservations_backend.commons.models.SortModel;
import com.indra.reservations_backend.dto.SalaRequestDto;
import com.indra.reservations_backend.dto.SalaResponseDto;
import com.indra.reservations_backend.mappers.SalaMapper;
import com.indra.reservations_backend.models.SalaEntity;
import com.indra.reservations_backend.repository.IReservaRepository;
import com.indra.reservations_backend.repository.ISalaRepository;
import com.indra.reservations_backend.service.ISalaService;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SalaServiceImpl implements ISalaService {

    private final SalaMapper salaMapper;
    private final ISalaRepository salaRepository;
    private final EntityManager entityManager;
    private final IReservaRepository reservaRepository;

    @Override
    public SalaResponseDto save(SalaRequestDto request) {
        if (request.getCapacidad() <= 0) {
            throw new BussinessException("La capacidad debe ser mayor a 0");
        }
        SalaEntity salaEntity = salaMapper.toEntity(request);
        SalaEntity savedEntity = salaRepository.save(salaEntity);
        return salaMapper.toResponseDto(savedEntity);
    }

    @Override
    public SalaResponseDto update(Long id, SalaRequestDto dto) {
        SalaEntity salaEntity = salaMapper.toEntity(dto);

        SalaEntity existingEntity = salaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró la Sala con id: " + id));

        BeanUtils.copyProperties(salaEntity, existingEntity, "idSala", "estado");

        SalaEntity updatedEntity = salaRepository.save(existingEntity);
        return salaMapper.toResponseDto(updatedEntity);
    }

    @Override
    public SalaResponseDto findById(Long id) {
        SalaEntity salaEntity = salaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró la Sala con id: " + id));
        return salaMapper.toResponseDto(salaEntity);
    }

    @Override
    public SalaResponseDto delete(Long id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }

    @Override
    public PageImpl<SalaResponseDto> getPagination(PaginationModel paginationModel) {
        int page = paginationModel.getPageNumber() != null
                ? paginationModel.getPageNumber()
                : 0;

        int rowsPerPage = paginationModel.getRowsPerPage() != null
                ? paginationModel.getRowsPerPage()
                : 5;

        Pageable pageable = PageRequest.of(page, rowsPerPage);

        // Query base
        String baseSelect = """
                    SELECT new com.indra.reservations_backend.dto.SalaResponseDto(
                        s.idSala,
                        s.nombre,
                        s.capacidad,
                        s.ubicacion,
                        s.estado
                    )
                    FROM SalaEntity s
                """;

        String baseCount = """
                    SELECT COUNT(s.idSala)
                    FROM SalaEntity s
                """;

        // WHERE dinámico (solo estado)
        String whereClause = buildWhereClause(paginationModel.getFilters());

        // ORDER BY dinámico controlado
        String orderByClause = buildOrderByClause(paginationModel.getSorts());

        String sql = baseSelect + whereClause + orderByClause;
        String sqlCount = baseCount + whereClause;

        TypedQuery<SalaResponseDto> querySelect = entityManager.createQuery(sql, SalaResponseDto.class);
        TypedQuery<Long> queryCount = entityManager.createQuery(sqlCount, Long.class);

        // Seteo de parámetros
        setWhereParameters(querySelect, paginationModel.getFilters());
        setWhereParameters(queryCount, paginationModel.getFilters());

        // Paginación
        querySelect.setFirstResult((int) pageable.getOffset());
        querySelect.setMaxResults(pageable.getPageSize());

        List<SalaResponseDto> results = querySelect.getResultList();
        Long total = queryCount.getSingleResult();

        return new PageImpl<>(results, pageable, total);
    }

    private String buildWhereClause(List<FilterModel> filters) {
        if (filters == null || filters.isEmpty()) {
            return "";
        }

        StringBuilder where = new StringBuilder(" WHERE 1 = 1 ");

        for (FilterModel filter : filters) {

            if ("estado".equals(filter.getField())) {
                where.append(" AND s.estado = :estado ");
            }

        }
        return where.toString();
    }

    private void setWhereParameters(TypedQuery<?> query, List<FilterModel> filters) {
        if (filters == null)
            return;

        for (FilterModel filter : filters) {
            if ("estado".equals(filter.getField())) {
                query.setParameter("estado", filter.getValue());
            }
        }
    }

    private String buildOrderByClause(List<SortModel> sorts) {

        // Orden por defecto
        if (sorts == null || sorts.isEmpty()) {
            return " ORDER BY s.idSala ASC ";
        }

        SortModel sort = sorts.get(0);

        String direction = "ASC";
        if ("DESC".equalsIgnoreCase(sort.getDirection())) {
            direction = "DESC";
        }

        if ("nombre".equals(sort.getColName())) {
            return " ORDER BY s.nombre " + direction;
        }

        if ("capacidad".equals(sort.getColName())) {
            return " ORDER BY s.capacidad " + direction;
        }

        return " ORDER BY s.idSala ASC ";
    }

    @Override
    public SalaResponseDto cambiarEstadoSala(Long id) {
        Integer existe = reservaRepository.existeReservaActivaSala(id);

        if (existe != null && existe == 1) {
            throw new BussinessException("No se puede cambiar el estado de la sala con reservas activas");
        }
        
        SalaEntity salaEntity = salaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró la Sala con id: " + id));

        EstadoSala estadoActual = EstadoSala.fromCode(salaEntity.getEstado());

        EstadoSala nuevoEstado = estadoActual == EstadoSala.DISPONIBLE
                ? EstadoSala.NO_DISPONIBLE
                : EstadoSala.DISPONIBLE;

        salaEntity.setEstado(nuevoEstado.getCode());

        SalaEntity updatedSala = salaRepository.save(salaEntity);

        return salaMapper.toResponseDto(updatedSala);
    }

}
