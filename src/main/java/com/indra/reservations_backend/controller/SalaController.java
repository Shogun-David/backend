package com.indra.reservations_backend.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.indra.reservations_backend.commons.models.PaginationModel;
import com.indra.reservations_backend.dto.SalaRequestDto;
import com.indra.reservations_backend.dto.SalaResponseDto;
import com.indra.reservations_backend.service.ISalaService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/salas")
@RestController
public class SalaController {

    @Autowired
    private ISalaService salaService;

    @PostMapping
    public ResponseEntity<SalaResponseDto> create(@RequestBody SalaRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(salaService.save(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SalaResponseDto> getMethodName(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(salaService.findById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<SalaResponseDto> delete(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(salaService.delete(id));
    }

    @PostMapping("/pagination")
    public ResponseEntity<Iterable<SalaResponseDto>> getPagination(@RequestBody PaginationModel paginationModel) {
        return ResponseEntity.status(HttpStatus.OK).body(salaService.getPagination(paginationModel));
    }

}
