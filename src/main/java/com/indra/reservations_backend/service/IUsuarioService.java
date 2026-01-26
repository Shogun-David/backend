package com.indra.reservations_backend.service;

import com.indra.reservations_backend.commons.interfaces.ICrudCommonsDto;
import com.indra.reservations_backend.commons.interfaces.IPaginationCommons;
import com.indra.reservations_backend.dto.UsuarioRequestDto;
import com.indra.reservations_backend.dto.UsuarioResponseDto;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * Interfaz para gestión de usuarios.
 * Extiende ICrudCommonsDto, IPaginationCommons y UserDetailsService.
 */
public interface IUsuarioService 
        extends ICrudCommonsDto<UsuarioRequestDto, UsuarioResponseDto, Long>, 
                IPaginationCommons<UsuarioResponseDto>,
                UserDetailsService {

}