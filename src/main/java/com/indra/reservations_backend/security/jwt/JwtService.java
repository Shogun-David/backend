package com.indra.reservations_backend.security.jwt;

import com.indra.reservations_backend.model.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Servicio para manejo de JSON Web Tokens (JWT).
 * 
 * Responsabilidades:
 * - Generar tokens JWT con información del usuario
 * - Validar tokens JWT
 * - Extraer información (claims) de los tokens
 * - Verificar expiración de tokens
 */
@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration; // En milisegundos

    /**
     * Genera la clave secreta a partir del String configurado.
     * Usa HMAC-SHA para firmar los tokens.
     */
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 🔹 PASO 4: Genera token JWT firmado
     * 
     * Crea un token que contiene:
     * - Subject: username
     * - Claims: roles (ADMIN, USUARIO)
     * - Issued At: fecha/hora de creación
     * - Expiration: fecha/hora de expiración (24h)
     * - Signature: HMAC-SHA256 con clave secreta
     * 
     * Cliente debe guardar este token y enviarlo en cada request:
     * Authorization: Bearer <token>
     * 
     * @param usuario El usuario autenticado
     * @return Token JWT firmado (String)
     */
    public String generateToken(Usuario usuario) {
        Map<String, Object> claims = new HashMap<>();
        // Convertir los roles a String separado por comas
        String rolesString = usuario.getRoles().stream()
                .map(rol -> rol.getNombre())
                .collect(java.util.stream.Collectors.joining(","));
        claims.put("roles", rolesString);
        
        return Jwts.builder()
                .claims(claims)
                .subject(usuario.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * Extrae el username del token JWT.
     * 
     * @param token El token JWT
     * @return El username contenido en el token
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extrae los roles del token JWT.
     * 
     * @param token El token JWT
     * @return String con los roles (separados por coma)
     */
    public String extractRoles(String token) {
        return extractClaim(token, claims -> claims.get("roles", String.class));
    }

    /**
     * Extrae la fecha de expiración del token.
     * 
     * @param token El token JWT
     * @return Fecha de expiración
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Extrae un claim específico del token usando una función.
     * 
     * @param token El token JWT
     * @param claimsResolver Función para extraer el claim deseado
     * @return El valor del claim
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Extrae todos los claims del token.
     * 
     * @param token El token JWT
     * @return Claims del token
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Verifica si el token ha expirado.
     * 
     * @param token El token JWT
     * @return true si el token expiró, false en caso contrario
     */
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Valida si un token es válido para un usuario específico.
     * 
     * Verifica:
     * - Que el username del token coincida con el del usuario
     * - Que el token no haya expirado
     * 
     * @param token El token JWT
     * @param userDetails Detalles del usuario a validar
     * @return true si el token es válido, false en caso contrario
     */
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    /**
     * Valida si un token es válido (sin comparar con un usuario específico).
     * Útil para verificaciones iniciales.
     * 
     * @param token El token JWT
     * @return true si el token no ha expirado
     */
    public Boolean validateToken(String token) {
        try {
            return !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }
}
