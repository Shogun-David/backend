package com.indra.reservations_backend.service;

import com.indra.reservations_backend.dto.LoginRequestDto;
import com.indra.reservations_backend.dto.LoginResponseDto;
import com.indra.reservations_backend.models.UsuarioEntity;
import com.indra.reservations_backend.security.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public LoginResponseDto login(LoginRequestDto loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        // Obtener el usuario autenticado
        UsuarioEntity usuario = (UsuarioEntity) authentication.getPrincipal();
       
        String token = jwtService.generateToken(usuario);

        return new LoginResponseDto(token);
    }

   

}
