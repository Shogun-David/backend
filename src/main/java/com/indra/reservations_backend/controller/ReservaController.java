package com.indra.reservations_backend.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.indra.reservations_backend.dto.ReservaRequestDto;
import com.indra.reservations_backend.dto.ReservaResponseDto;
import com.indra.reservations_backend.service.IReservaService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/reservas")
public class ReservaController {

    private final IReservaService reservaService;


    @GetMapping("/by-user/{userId}")
    public ResponseEntity<List<ReservaResponseDto>> getReservasByUser(@PathVariable Long userId){
        return ResponseEntity.ok(reservaService.getReservasByUser(userId));
    }


    @PostMapping
    public ResponseEntity<ReservaResponseDto> crearReserva(@RequestBody ReservaRequestDto requestDto){
        return new ResponseEntity<ReservaResponseDto>(reservaService.save(requestDto), HttpStatus.CREATED);
    }

    
}
