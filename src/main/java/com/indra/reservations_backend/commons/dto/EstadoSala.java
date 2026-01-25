package com.indra.reservations_backend.commons.dto;

import java.util.Arrays;
import java.util.function.Supplier;

import com.indra.reservations_backend.exception.BussinessException;

public enum EstadoSala {
    DISPONIBLE("D"),
    NO_DISPONIBLE("N");

    private final String code;

    EstadoSala(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static EstadoSala fromCode(String code) {
        return Arrays.stream(values())
                .filter(e -> e.code.equals(code))
                .findFirst()
                .orElseThrow((Supplier<RuntimeException>) () ->
        new BussinessException("Estado de sala inválido: " + code)
);

    }
}
