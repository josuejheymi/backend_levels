package com.levels.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Desactivar CSRF para Postman
            .authorizeHttpRequests(auth -> auth
                // PERMITIR ACCESO A SWAGGER UI
                .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                // Permitir tus endpoints (Usuarios, Productos, etc)
                .requestMatchers("/api/**").permitAll()
                // Cualquier otra cosa
                .anyRequest().permitAll()
            );
            
        return http.build();
    }
}