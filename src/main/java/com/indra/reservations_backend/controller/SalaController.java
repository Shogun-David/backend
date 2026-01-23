package com.indra.reservations_backend.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.indra.reservations_backend.commons.models.PaginationModel;
import com.indra.reservations_backend.dto.SalaRequestDto;
import com.indra.reservations_backend.dto.SalaResponseDto;
import com.indra.reservations_backend.service.ISalaService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;


@RequestMapping("/salas")
@RestController
public class SalaController {

    @Autowired
    private ISalaService salaService;

    @PreAuthorize("hasAnyRole('ADMIN')")
    @PostMapping
    public ResponseEntity<SalaResponseDto> create(@Valid @RequestBody SalaRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(salaService.save(dto));
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<SalaResponseDto> update(@PathVariable Long id, @Valid @RequestBody SalaRequestDto dto) {
        return ResponseEntity.status(HttpStatus.OK).body(salaService.update(id, dto));
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<SalaResponseDto> getMethodName(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(salaService.findById(id));
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @PatchMapping("/{id}/estado")
    public ResponseEntity<SalaResponseDto> delete(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(salaService.cambiarEstadoSala(id));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @PostMapping("/pagination")
    public ResponseEntity<Iterable<SalaResponseDto>> getPagination(@RequestBody PaginationModel paginationModel) {
        return ResponseEntity.status(HttpStatus.OK).body(salaService.getPagination(paginationModel));
    }

}
