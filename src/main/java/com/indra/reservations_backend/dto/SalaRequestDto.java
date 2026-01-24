package com.indra.reservations_backend.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SalaRequestDto {

    @NotBlank
    private String nombre;

    @NotNull
    @Min(1)
    private Integer capacidad;

    @NotBlank
    private String ubicacion;

    private String estado; // D / N
}
//-- IGNORE ---