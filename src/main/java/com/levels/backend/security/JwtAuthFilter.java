package com.levels.backend.security;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * FILTRO DE AUTENTICACIÓN JWT (Inspector de Pasaportes)
 * ----------------------------------------------------
 * Intercepta CADA petición HTTP para verificar si tiene un Token JWT válido.
 * Si el Token es válido, autentica al usuario internamente para que Spring Security
 * permita el acceso a las rutas protegidas.
 */
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService; // Servicio para leer/validar el token
    
    @Autowired
    private CustomUserDetailsService userDetailsService; // Servicio para cargar el usuario desde la BD

    /**
     * Lógica que se ejecuta una vez por cada solicitud HTTP.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 1. Obtener el header "Authorization" que envía React
        String authHeader = request.getHeader("Authorization");
        String token = null;
        String username = null; // En nuestro caso, el email del usuario

        // 2. Verificar formato y extraer el Token
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7); // Quitar "Bearer " (que son los primeros 7 caracteres)
            try {
                // Extraer el sujeto del token (el email)
                username = jwtService.extractUsername(token);
            } catch (Exception e) {
                // Ignoramos errores de tokens expirados o inválidos y dejamos que el flujo continúe (será bloqueado después)
                System.out.println("Error al extraer usuario del token: " + e.getMessage());
            }
        }

        // 3. Chequeo de Autenticación
        // Condiciones:
        // a) Se encontró un usuario en el token.
        // b) El contexto de seguridad de Spring (SecurityContextHolder) está vacío (aún no está autenticado).
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            
            // 4. Cargar los detalles del usuario desde la BD (roles, password)
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            // 5. Validar que el token no esté expirado y coincida con el usuario cargado
            if (jwtService.validateToken(token, userDetails.getUsername())) {
                
                // 6. Crear un objeto de autenticación válido
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                        
                // Añadir detalles de la solicitud (IP, sesión, etc.)
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                
                // 7. Autenticar oficialmente en el Contexto de Spring
                // Esto le dice a Spring Security: "Este usuario es válido, déjalo pasar."
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // 8. Continuar con la cadena de filtros de Spring Security (va hacia el controlador o es bloqueado si no se autenticó)
        filterChain.doFilter(request, response);
    }
}