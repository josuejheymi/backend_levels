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

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;
    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 1. Obtener el header "Authorization"
        String authHeader = request.getHeader("Authorization");
        String token = null;
        String username = null;

        // 2. Verificar que empiece con "Bearer "
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7); // Quitar "Bearer "
            try {
                username = jwtService.extractUsername(token);
            } catch (Exception e) {
                System.out.println("Error al extraer usuario del token: " + e.getMessage());
            }
        }

        // 3. Si hay usuario pero no está autenticado en el contexto
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            // 4. Validar token
            if (jwtService.validateToken(token, username)) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                
                // 5. Autenticar oficialmente
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // 6. Continuar con la petición
        filterChain.doFilter(request, response);
    }
}