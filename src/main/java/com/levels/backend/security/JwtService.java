package com.levels.backend.security;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

/**
 * SERVICIO: GESTIÓN DE TOKENS JWT
 * ----------------------------------------------------
 * Clase utilitaria encargada de generar, firmar, y validar los Tokens JWT.
 * Es la base de la autenticación Stateless (sin estado de sesión en el servidor).
 */
@Service
public class JwtService {

    // CLAVE SECRETA: Se utiliza para FIRMAR el token. Solo el servidor la conoce.
    // Si esta clave se filtra, cualquier atacante podría falsificar tokens válidos.
    private static final String SECRET_KEY = "esta_es_una_clave_muy_secreta_para_el_proyecto_fullstack_duoc_2024";

    /**
     * 1. GENERAR TOKEN PÚBLICO
     * Prepara los Claims (metadatos) y delega la creación del token.
     * @param username El identificador del usuario (email).
     * @return El token JWT firmado (String).
     */
    public String generateToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, username);
    }

    /**
     * 2. CONSTRUCCIÓN DEL TOKEN
     * Define la estructura, la expiración y aplica la firma.
     */
    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject) // El "dueño" del token (el email del usuario)
                .setIssuedAt(new Date(System.currentTimeMillis())) // Fecha de emisión (ahora)
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // Expira en 10 Horas
                .signWith(getSigningKey(), SignatureAlgorithm.HS256) // Firma el token con la clave secreta usando HS256
                .compact(); // Finaliza y genera la cadena de texto JWT
    }

    /**
     * 3. VALIDACIÓN PRINCIPAL
     * Verifica que el token: 1) Pertenezca al usuario esperado, y 2) No haya expirado.
     * @param token Cadena JWT.
     * @param username El email esperado.
     * @return true si es válido y no está expirado.
     */
    public boolean validateToken(String token, String username) {
        final String tokenUsername = extractUsername(token);
        return (tokenUsername.equals(username) && !isTokenExpired(token));
    }

    /**
     * 4. EXTRAER EL SUJETO (Username/Email)
     * Método público para que JwtAuthFilter sepa a quién buscar en la BD.
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // --- Métodos Auxiliares de Lectura y Firma ---
    
    /**
     * Genera la Key (llave) de cifrado a partir de la cadena secreta.
     */
    private Key getSigningKey() {
        // Usa la biblioteca Keys para asegurar que la clave sea lo suficientemente fuerte (256 bits)
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    /**
     * Función genérica para leer cualquier campo (Claim) del token.
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Descifra y verifica la firma del token.
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey()) // Usa la clave secreta para verificar la firma
                .build()
                .parseClaimsJws(token) // Verifica la firma
                .getBody(); // Devuelve los Claims (metadatos)
    }

    /**
     * Verifica si la fecha de expiración es anterior a la fecha actual.
     */
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Extrae el Claim de Expiración.
     */
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
}