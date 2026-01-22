package com.indra.reservations_backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.indra.reservations_backend.dto.CancelarReservaRequestDto;
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
    public ResponseEntity<Iterable<ReservaResponseDto>> getReservasByUser(@PathVariable Long userId,
            @RequestParam(required = false) String estado,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(reservaService.getReservasByUser(userId, estado, page, size));
    }

    @PostMapping
    public ResponseEntity<ReservaResponseDto> crearReserva(@RequestBody ReservaRequestDto requestDto) {
        return new ResponseEntity<ReservaResponseDto>(reservaService.save(requestDto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReservaResponseDto> actualizarReserva(@PathVariable Long id,
            @RequestBody ReservaRequestDto requestDto) {
        return new ResponseEntity<ReservaResponseDto>(reservaService.update(id, requestDto), HttpStatus.OK);
    }

    @DeleteMapping("/cancelar/{id}")
    public ResponseEntity<Void> cancelarReserva(@PathVariable Long id,
            @RequestBody CancelarReservaRequestDto requestDto) {
        reservaService.cancelarReserva(id, requestDto);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
