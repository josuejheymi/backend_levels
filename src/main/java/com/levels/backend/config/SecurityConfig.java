package com.levels.backend.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration; // <--- IMPORTANTE
import org.springframework.web.cors.CorsConfigurationSource; // <--- IMPORTANTE
import org.springframework.web.cors.UrlBasedCorsConfigurationSource; // <--- IMPORTANTE

import com.levels.backend.security.JwtAuthFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthFilter jwtAuthFilter;
    
    @Autowired
    private UserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource())) // <--- 1. ACTIVAR CORS AQUÍ
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                // Rutas PÚBLICAS
                .requestMatchers("/api/usuarios/login", "/api/usuarios/registro").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/productos/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/blog/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/resenas/**").permitAll()
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()

                // Rutas ADMIN
                .requestMatchers(HttpMethod.POST, "/api/productos/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/productos/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/blog/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/blog/**").hasRole("ADMIN")

                // Todo lo demás requiere Token
                .anyRequest().authenticated()
            )
            .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authenticationProvider(authenticationProvider())
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // --- 2. DEFINIR LA CONFIGURACIÓN DE CORS ---
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000")); // Permitir React
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS")); // Verbos permitidos
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type")); // Headers permitidos
        configuration.setAllowCredentials(true); // Permitir credenciales

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder()); 
        return authProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance(); 
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}