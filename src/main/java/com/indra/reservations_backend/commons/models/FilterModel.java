package com.indra.reservations_backend.commons.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FilterModel {

    private String field;
    private String value;
}
