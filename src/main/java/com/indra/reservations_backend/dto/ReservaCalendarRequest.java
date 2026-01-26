package com.indra.reservations_backend.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.indra.reservations_backend.commons.models.FilterModel;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ReservaCalendarRequest {

    @NotNull
    private LocalDateTime start;

    @NotNull
    private LocalDateTime end;

    private List<FilterModel> filters;

}
