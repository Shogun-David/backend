package com.indra.reservations_backend.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ValidateTokenResponseDto {
    private boolean valid;
    private String username;
    private List<String> roles;
}
