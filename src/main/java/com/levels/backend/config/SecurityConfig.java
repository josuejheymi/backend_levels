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
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.levels.backend.security.JwtAuthFilter;

/**
 * CONFIGURACIÓN DE SEGURIDAD (Spring Security 6)
 * ----------------------------------------------------
 * Esta clase actúa como el "Portero" de la aplicación.
 * Define reglas de acceso, cors, filtros JWT y roles.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // Filtro personalizado que intercepta cada petición para buscar el Token JWT
    @Autowired
    private JwtAuthFilter jwtAuthFilter;
    
    // Servicio para buscar usuarios en la base de datos MySQL
    @Autowired
    private UserDetailsService userDetailsService;

    /**
     * CADENA DE FILTROS DE SEGURIDAD (Security Filter Chain)
     * Aquí se definen las reglas de tráfico HTTP.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // 1. CORS: Permitir peticiones desde el Frontend (React localhost:3000)
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            
            // 2. CSRF: Deshabilitado porque usamos Tokens (Stateless), no cookies de sesión
            .csrf(csrf -> csrf.disable())
            
            // 3. REGLAS DE AUTORIZACIÓN (El semáforo)
            .authorizeHttpRequests(auth -> auth
            
                // --- A. RUTAS PÚBLICAS (Acceso libre) ---
                .requestMatchers("/api/usuarios/login", "/api/usuarios/registro").permitAll()
                
                // Catálogo (Ver productos, categorías, blog y reseñas es gratis)
                .requestMatchers(HttpMethod.GET, "/api/productos/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/categorias/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/blog/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/resenas/**").permitAll()
                
                // Documentación Swagger/OpenAPI
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()

                // --- B. RUTAS DE CLIENTE (Requieren Login) ---
                
                // Ver detalle de UNA orden (Mi comprobante)
                // Permitimos 'authenticated()' para que cualquier usuario vea SU propia orden.
                // (Nota: Idealmente el controlador debe verificar que la orden pertenezca al usuario).
                .requestMatchers(HttpMethod.GET, "/api/ordenes/{id}").authenticated()
                
                // Ver historial de compras (Perfil)
                .requestMatchers(HttpMethod.GET, "/api/ordenes/usuario/**").authenticated()

                // --- C. RUTAS DE STAFF (Admin / Vendedor) ---
                
                // Ver TODAS las ventas del sistema (Dashboard)
                .requestMatchers(HttpMethod.GET, "/api/ordenes").hasAnyRole("ADMIN", "VENDEDOR") 
                .requestMatchers(HttpMethod.GET, "/api/ordenes/stats").hasAnyRole("ADMIN", "VENDEDOR")

                // Gestión de Inventario (Solo el Jefe puede borrar/crear)
                .requestMatchers(HttpMethod.POST, "/api/productos/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/productos/**").hasRole("ADMIN") // Agregado PUT
                .requestMatchers(HttpMethod.DELETE, "/api/productos/**").hasRole("ADMIN")
                
                // Gestión del Blog
                .requestMatchers("/api/blog/**").hasRole("ADMIN")

                // --- D. TODO LO DEMÁS ---
                // Cualquier otra ruta no listada arriba requiere al menos estar logueado.
                .anyRequest().authenticated()
            )
            
            // 4. SESIÓN: Stateless (Sin estado)
            // No guardamos sesión en el servidor. Cada petición debe traer su Token.
            .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            
            // 5. PROVEEDOR: Conectamos nuestro UserDetailsService y PasswordEncoder
            .authenticationProvider(authenticationProvider())
            
            // 6. FILTRO: Ejecutar nuestro filtro JWT antes del filtro estándar de usuario/password
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * CONFIGURACIÓN CORS
     * Permite que React (puerto 3000) hable con Spring Boot (puerto 8080).
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Origen permitido (Tu Frontend)
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000")); 
        
        // Métodos HTTP permitidos
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        
        // Cabeceras permitidas (Authorization es vital para enviar el Token)
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
        
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    /**
     * PROVEEDOR DE AUTENTICACIÓN
     * Une el servicio de usuarios con el encriptador de contraseñas.
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder()); 
        return authProvider;
    }

    /**
     * ENCRIPTADOR (PasswordEncoder)
     * * IMPORTANTE ACADÉMICO: Usamos NoOp (texto plano) para facilitar pruebas.
     * * EN PRODUCCIÓN: Se debe usar BCryptPasswordEncoder.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance(); 
    }

    /**
     * MANAGER DE AUTENTICACIÓN
     * Bean necesario para inyectarlo en el AuthController (Login).
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}