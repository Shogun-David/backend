package com.indra.reservations_backend.service.impl;

import com.indra.reservations_backend.commons.models.PaginationModel;
import com.indra.reservations_backend.dto.UsuarioRequestDto;
import com.indra.reservations_backend.dto.UsuarioResponseDto;
import com.indra.reservations_backend.exception.ConflictException;
import com.indra.reservations_backend.exception.ResourceNotFoundException;
import com.indra.reservations_backend.mappers.UsuarioMapper;
import com.indra.reservations_backend.models.Rol;
import com.indra.reservations_backend.models.UsuarioEntity;
import com.indra.reservations_backend.models.UsuarioRol;
import com.indra.reservations_backend.repository.RolRepository;
import com.indra.reservations_backend.repository.UsuarioRepository;
import com.indra.reservations_backend.repository.UsuarioRolRepository;
import com.indra.reservations_backend.service.IUsuarioService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementación del servicio de gestión de usuarios.
 * Implementa IUsuarioService con operaciones CRUD, paginación y autenticación.
 */
@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements IUsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final UsuarioRolRepository usuarioRolRepository;
    private final UsuarioMapper usuarioMapper;
    private final PasswordEncoder passwordEncoder;
    private final EntityManager entityManager;

    /**
     * Implementa loadUserByUsername de UserDetailsService.
     * Llamado automáticamente por Spring Security durante autenticación.
     */
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return (UserDetails) usuarioRepository.getByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));
    }

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioResponseDto> findAll() {
        return usuarioRepository.findAll()
                .stream()
                .map(usuarioMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    /**
     * Busca un usuario por ID.
     */
    @Override
    @Transactional(readOnly = true)
    public UsuarioResponseDto findById(Long id) {
        UsuarioEntity usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        return usuarioMapper.toResponseDto(usuario);
    }

    /**
     * Crea un nuevo usuario y lo asigna automáticamente al rol USUARIO por defecto.
     */
    @Override
    @Transactional
    public UsuarioResponseDto save(UsuarioRequestDto dto) {
        if (usuarioRepository.getByUsername(dto.getUsername()).isPresent()) {
            throw new ConflictException("El usuario ya existe");
        }

        // Obtener el rol por defecto (USUARIO)
        Rol rolDefecto = rolRepository.findById(1L)
                .orElseThrow(() -> new ResourceNotFoundException("Rol USUARIO con id=1 no encontrado. Verifica que exista en la tabla ROL"));

        // Crear y guardar el usuario
        UsuarioEntity usuario = usuarioMapper.toEntity(dto);
        usuario.setPassword(passwordEncoder.encode(dto.getPassword()));
        usuario.setEstado("A");
        usuario.setFechaCreacion(LocalDateTime.now());

        UsuarioEntity guardado = usuarioRepository.save(usuario);
        usuarioRepository.flush(); // Asegurar que el usuario se guarde antes de crear la relación

        // Crear la relación usuario-rol automáticamente en USUARIO_ROL
        UsuarioRol usuarioRol = UsuarioRol.builder()
                .usuario(guardado)
                .rol(rolDefecto)
                .build();

        usuarioRolRepository.save(usuarioRol);
        usuarioRolRepository.flush(); // Asegurar que la relación se guarde

        return usuarioMapper.toResponseDto(guardado);
    }

    /**
     * Actualiza un usuario existente.
     */
    @Override
    @Transactional
    public UsuarioResponseDto update(Long id, UsuarioRequestDto dto) {
        UsuarioEntity usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        usuario.setUsername(dto.getUsername());
        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            usuario.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        UsuarioEntity actualizado = usuarioRepository.save(usuario);
        return usuarioMapper.toResponseDto(actualizado);
    }

    /**
     * Elimina un usuario.
     */
    @Override
    @Transactional
    public UsuarioResponseDto delete(Long id) {
        UsuarioEntity usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        UsuarioResponseDto response = usuarioMapper.toResponseDto(usuario);

        usuarioRepository.delete(usuario);
        return response;
    }

    /**
     * Obtiene usuarios con paginación, filtros y ordenamiento.
     */
    @Override
    @Transactional(readOnly = true)
    public PageImpl<UsuarioResponseDto> getPagination(PaginationModel paginationModel) {
        int page = (paginationModel.getPageNumber() != null) ? paginationModel.getPageNumber() : 0;
        int size = (paginationModel.getRowsPerPage() != null) ? paginationModel.getRowsPerPage() : 10;
        Pageable pageable = PageRequest.of(page, size);

        // Construcción de la consulta
        StringBuilder sql = new StringBuilder("SELECT u FROM UsuarioEntity u WHERE 1=1");

        // Aplicar filtros
        if (paginationModel.getFilters() != null && !paginationModel.getFilters().isEmpty()) {
            for (var filter : paginationModel.getFilters()) {
                if ("username".equals(filter.getField())) {
                    sql.append(" AND u.username LIKE '%").append(filter.getValue()).append("%'");
                }
                if ("estado".equals(filter.getField())) {
                    sql.append(" AND u.estado = '").append(filter.getValue()).append("'");
                }
            }
        }

        // Aplicar ordenamiento
        if (paginationModel.getSorts() != null && !paginationModel.getSorts().isEmpty()) {
            var sort = paginationModel.getSorts().get(0);
            sql.append(" ORDER BY u.").append(sort.getColName()).append(" ").append(sort.getDirection());
        } else {
            sql.append(" ORDER BY u.idUsuario ASC");
        }

        // Ejecutar consulta con paginación
        TypedQuery<UsuarioEntity> query = entityManager.createQuery(sql.toString(), UsuarioEntity.class);
        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());

        List<UsuarioEntity> usuarios = query.getResultList();
        List<UsuarioResponseDto> dtos = usuarios.stream()
                .map(usuarioMapper::toResponseDto)
                .collect(Collectors.toList());

        // Contar total de registros
        String countSql = "SELECT COUNT(u) FROM UsuarioEntity u";
        Long total = entityManager.createQuery(countSql, Long.class).getSingleResult();

        return new PageImpl<>(dtos, pageable, total);
    }
}

