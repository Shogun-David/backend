package com.indra.reservations_backend.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.indra.reservations_backend.models.UsuarioEntity;
import com.indra.reservations_backend.repository.UsuarioRolRepository;
import lombok.RequiredArgsConstructor;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final UsuarioRolRepository usuarioRolRepository;

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration; // En milisegundos

    
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    
    public String generateToken(UsuarioEntity usuario) {
        Map<String, Object> claims = new HashMap<>();
        
        String roles = extraerRolesDelUsuario(usuario);
        claims.put("roles", roles);
        claims.put("username", usuario.getUsername());
        
        return Jwts.builder()
                .claims(claims)
                .subject(usuario.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * Extrae los roles del usuario desde USUARIO_ROL
     */
    private String extraerRolesDelUsuario(UsuarioEntity usuario) {
        return usuarioRolRepository.getRolesByUsuario(usuario)
                .stream()
                .map(usuarioRol -> usuarioRol.getRol().getNombre())
                .collect(Collectors.joining(","));
    }

    /**
     * Extrae el username del token JWT
     */
    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    /**
     * Extrae los roles del token JWT (formato: "ADMIN,USUARIO")
     */
    public String extractRoles(String token) {
        return extractAllClaims(token).get("roles", String.class);
    }

    /**
     * Extrae los roles como List
     */
    public List<String> extractRolesAsList(String token) {
        String roles = extractRoles(token);
        if (roles == null || roles.isEmpty()) {
            return List.of();
        }
        return List.of(roles.split(","));
    }

    /**
     * Valida que el token no esté expirado
     */
    public Boolean validateToken(String token) {
        try {
            Date expiration = extractAllClaims(token).getExpiration();
            return expiration.after(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Extrae todos los claims del token
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
