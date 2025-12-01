package com.levels.backend.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    // CLAVE SECRETA: En producción esto iría en variables de entorno.
    // Debe ser larga (mínimo 256 bits) para que funcione con HS256.
    private static final String SECRET_KEY = "esta_es_una_clave_muy_secreta_para_el_proyecto_fullstack_duoc_2024";

    // 1. Generar Token para un usuario (email)
    public String generateToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, username);
    }

    // 2. Crear el token con fecha de expiración (ej: 10 horas)
    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject) // El "dueño" del token (email)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // 10 Horas
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // 3. Validar si el token le pertenece al usuario
    public boolean validateToken(String token, String username) {
        final String tokenUsername = extractUsername(token);
        return (tokenUsername.equals(username) && !isTokenExpired(token));
    }

    // 4. Extraer el usuario (email) del token
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // --- Métodos Auxiliares ---
    
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
}