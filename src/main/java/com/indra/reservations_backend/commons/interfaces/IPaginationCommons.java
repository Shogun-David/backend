package com.indra.reservations_backend.commons.interfaces;

import org.springframework.data.domain.PageImpl;

import com.indra.reservations_backend.commons.models.PaginationModel;

public interface IPaginationCommons<T> {

    public PageImpl<T> getPagination(PaginationModel paginationModel);
}
