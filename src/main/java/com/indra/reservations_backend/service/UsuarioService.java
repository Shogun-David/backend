package com.indra.reservations_backend.service;

import com.indra.reservations_backend.dto.UsuarioRequestDto;
import com.indra.reservations_backend.dto.UsuarioResponseDto;
import com.indra.reservations_backend.models.Rol;
import com.indra.reservations_backend.models.Usuario;
import com.indra.reservations_backend.repository.RolRepository;
import com.indra.reservations_backend.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.StoredProcedureQuery;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Servicio para gestionar usuarios.
 * Implementa UserDetailsService para integrarse con Spring Security.
 * 
 * Responsabilidades:
 * - Cargar usuarios por username (requerido por Spring Security)
 * - CRUD de usuarios
 * - Conversión de entidades a DTOs
 */
@Service
@RequiredArgsConstructor
public class UsuarioService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;
    private final EntityManager entityManager;

    /**
     * 🔹 PASO 3: Carga usuario desde BD durante autenticación
     * 
     * Llamado automáticamente por AuthenticationManager al hacer login.
     * 
     * Flujo:
     * - Busca usuario en BD por username
     * - Carga roles asociados (ADMIN, USUARIO)
     * - Retorna Usuario que implementa UserDetails
     * - Spring Security compara password automáticamente
     * 
     * @param username El nombre de usuario
     * @return UserDetails (Usuario con roles)
     * @throws UsernameNotFoundException si el usuario no existe
     */
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Usuario no encontrado: " + username));
    }

    /**
     * Busca un usuario por username y lo retorna como entidad.
     * 
     * @param username El nombre de usuario
     * @return La entidad Usuario
     * @throws UsernameNotFoundException si no existe
     */
    @Transactional(readOnly = true)
    public Usuario findByUsername(String username) {
        return usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Usuario no encontrado: " + username));
    }

    /**
     * Obtiene todos los usuarios del sistema.
     * 
     * @return Lista de usuarios como DTOs
     */
    @Transactional(readOnly = true)
    public List<UsuarioResponseDto> getAllUsuarios() {
        return usuarioRepository.findAllByRoleNombre("USER")
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Busca un usuario por ID.
     * 
     * @param id ID del usuario
     * @return UsuarioResponseDto
     */
    @Transactional(readOnly = true)
    public UsuarioResponseDto getUsuarioById(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));
        return toDto(usuario);
    }

    /**
     * Crea un nuevo usuario aplicando validaciones básicas y usando builder para la entidad.
     *
     * @param request datos de entrada del usuario
     * @return UsuarioResponseDto con la información persistida
     */
    @Transactional
    public UsuarioResponseDto createUsuario(UsuarioRequestDto request) {
        validarUnicidad(request);

        Usuario usuario = Usuario.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .estado("A")
                .fechaCreacion(LocalDateTime.now())
                .roles(obtenerRolPorDefecto()) // ✅ SIEMPRE USUARIO
                .build();

        Usuario guardado = usuarioRepository.save(usuario);
        return toDto(guardado);
    }



    private void validarUnicidad(UsuarioRequestDto request) {
        if (usuarioRepository.existsByUsername(request.getUsername())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El username ya se encuentra registrado");
        }
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El email ya se encuentra registrado");
        }
    }

    private Set<Rol> obtenerRolPorDefecto() {
        Rol rolUsuario = rolRepository.findByNombre("USER")
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        "Rol por defecto USER no existe en la BD"
                ));

        Set<Rol> roles = new HashSet<>();
        roles.add(rolUsuario);
    return roles;
}


    /**
     * Convierte una entidad Usuario a UsuarioResponseDto.
     * 
     * @param usuario La entidad Usuario
     * @return DTO con datos del usuario (sin password)
     */
    private UsuarioResponseDto toDto(Usuario usuario) {
        return new UsuarioResponseDto(
                usuario.getIdUsuario(),
                usuario.getUsername(),
                usuario.getEmail(),
                usuario.getRolesList(),
                usuario.getEstado()
        );
    }

    // ═══════════════════════════════════════════════════════════════════
    // STORED PROCEDURES (EntityManager)
    // ═══════════════════════════════════════════════════════════════════

    /**
     * Lista todos los usuarios usando el SP DBSGR.AUTH.SP_LISTAR_USUARIOS
     * 
     * Stored Procedure retorna:
     * - ID_USUARIO, USERNAME, EMAIL, ESTADO, FECHA_CREACION
     * 
     * @return Lista de UsuarioResponseDto con todos los usuarios
     * @throws RuntimeException si falla la ejecución del SP
     */
    @Transactional(readOnly = true)
    public List<UsuarioResponseDto> listarUsuariosConSP() {
        try {
            // Crear StoredProcedureQuery
            StoredProcedureQuery query = entityManager
                    .createStoredProcedureQuery("DBSGR.AUTH.SP_LISTAR_USUARIOS")
                    .registerStoredProcedureParameter("p_cursor", void.class, ParameterMode.OUT);

            // Ejecutar SP
            query.execute();

            // Obtener ResultSet del cursor
            @SuppressWarnings("unchecked")
            List<Object[]> resultados = query.getResultList();

            // Mapear a UsuarioResponseDto
            return resultados.stream()
                    .map(row -> new UsuarioResponseDto(
                            ((Number) row[0]).longValue(),        // ID_USUARIO
                            (String) row[1],                      // USERNAME
                            (String) row[2],                      // EMAIL
                            List.of(),                            // roles (vacío, necesitaría JOIN)
                            (String) row[3]                       // ESTADO
                    ))
                    .collect(Collectors.toList());

        } catch (Exception e) {
            throw new RuntimeException("Error ejecutando SP_LISTAR_USUARIOS: " + e.getMessage(), e);
        }
    }

    /**
     * Asigna un rol a un usuario usando el SP DBSGR.AUTH.SP_ASIGNAR_ROL
     * 
     * Valida:
     * - Que el usuario exista
     * - Que el rol exista
     * - Que el usuario no tenga ya ese rol asignado
     * 
     * @param idUsuario ID del usuario
     * @param idRol ID del rol
     * @throws ResponseStatusException 400 si usuario/rol no existe
     * @throws ResponseStatusException 409 si ya tiene ese rol
     * @throws RuntimeException si falla la ejecución del SP
     */
    @Transactional
    public void asignarRolConSP(Long idUsuario, Long idRol) {
        try {
            // Crear StoredProcedureQuery
            StoredProcedureQuery query = entityManager
                    .createStoredProcedureQuery("DBSGR.AUTH.SP_ASIGNAR_ROL")
                    .registerStoredProcedureParameter("p_id_usuario", Long.class, ParameterMode.IN)
                    .registerStoredProcedureParameter("p_id_rol", Long.class, ParameterMode.IN)
                    .setParameter("p_id_usuario", idUsuario)
                    .setParameter("p_id_rol", idRol);

            // Ejecutar SP
            query.execute();

        } catch (Exception e) {
            String msgError = e.getMessage();

            // Manejo de errores personalizados desde el SP
            if (msgError != null && msgError.contains("20001")) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                    "Usuario o rol no existe");
            }
            if (msgError != null && msgError.contains("20002")) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, 
                    "El usuario ya tiene asignado ese rol");
            }

            throw new RuntimeException("Error ejecutando SP_ASIGNAR_ROL: " + msgError, e);
        }
    }

    /**
     * Lista todos los usuarios usando el SP y devuelve como Entity
     * (alternativa si necesitas entidades completas con roles)
     * 
     * @return Lista de Usuario entities con roles cargados
     */
    @Transactional(readOnly = true)
    public List<Usuario> listarUsuariosConSPEntity() {
        try {
            StoredProcedureQuery query = entityManager
                    .createStoredProcedureQuery("DBSGR.AUTH.SP_LISTAR_USUARIOS", Usuario.class)
                    .registerStoredProcedureParameter("p_cursor", void.class, ParameterMode.OUT);

            query.execute();

            @SuppressWarnings("unchecked")
            List<Usuario> usuarios = query.getResultList();
            return usuarios;

        } catch (Exception e) {
            throw new RuntimeException("Error ejecutando SP_LISTAR_USUARIOS: " + e.getMessage(), e);
        }
    }
}
