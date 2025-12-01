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
 * CLASE PRINCIPAL DE SEGURIDAD (El "Portero" del Edificio)
 * Esta clase define las reglas de quién puede entrar y a dónde en nuestra aplicación.
 * Configura el manejo de Tokens JWT, los permisos por rol y la conexión con el Frontend.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // Inyectamos nuestro filtro personalizado que sabe leer el Token del header.
    @Autowired
    private JwtAuthFilter jwtAuthFilter;
    
    // Inyectamos el servicio que sabe buscar usuarios en nuestra Base de Datos MySQL.
    @Autowired
    private UserDetailsService userDetailsService;

    /**
     * CONFIGURACIÓN DE LA CADENA DE SEGURIDAD
     * Aquí definimos las reglas de tráfico HTTP.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // 1. CORS (Cross-Origin Resource Sharing):
            // Permite que nuestra página Web (React en puerto 3000) hable con este Backend (en puerto 8080).
            // Sin esto, el navegador bloquearía la conexión.
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            
            // 2. CSRF (Cross-Site Request Forgery):
            // Lo desactivamos porque usamos Tokens (JWT) y no sesiones de navegador tradicionales.
            // Es necesario para que funcionen los métodos POST, PUT y DELETE desde Postman o React.
            .csrf(csrf -> csrf.disable())
            
            // 3. REGLAS DE AUTORIZACIÓN (El semáforo de acceso):
            .authorizeHttpRequests(auth -> auth
            
                // --- RUTAS PÚBLICAS (Acceso libre para todos) ---
                // Conecta con: Pantalla de Login y Pantalla de Registro.
                .requestMatchers("/api/usuarios/login", "/api/usuarios/registro").permitAll()
                
                // Conecta con: Home (Ver catálogo), Blog (Leer noticias) y Detalle de Producto.
                // Permitimos solo GET (ver), nadie puede borrar o crear aquí sin permiso.
                .requestMatchers(HttpMethod.GET, "/api/productos/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/blog/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/resenas/**").permitAll()
                
                // Documentación de la API (Swagger)
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()

                // --- RUTAS PROTEGIDAS POR ROL (Solo personal autorizado) ---
                
                // 1. VENTAS Y ESTADÍSTICAS
                // Conecta con: Panel de Admin -> Pestaña "Ventas" y "Resumen".
                // Permite que tanto el Jefe (ADMIN) como los empleados (VENDEDOR) vean el historial.
                .requestMatchers(HttpMethod.GET, "/api/ordenes").hasAnyRole("ADMIN", "VENDEDOR") 
                .requestMatchers(HttpMethod.GET, "/api/ordenes/stats").hasAnyRole("ADMIN", "VENDEDOR")

                // 2. GESTIÓN DE INVENTARIO (Crear/Borrar Productos)
                // Conecta con: Panel de Admin -> Botón "Nuevo Producto" y "Eliminar".
                // REGLA DE NEGOCIO: El vendedor solo mira, el ADMIN toca.
                .requestMatchers(HttpMethod.POST, "/api/productos/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/productos/**").hasRole("ADMIN")
                
                // 3. GESTIÓN DEL BLOG
                // Conecta con: Panel de Admin -> Pestaña "Noticias".
                .requestMatchers("/api/blog/**").hasRole("ADMIN")

                // --- RESTO DEL SISTEMA ---
                // Conecta con: Carrito de Compras, Perfil de Usuario, Checkout.
                // Cualquier otra ruta no listada arriba requiere que el usuario tenga un Token válido.
                .anyRequest().authenticated()
            )
            
            // 4. GESTIÓN DE SESIÓN:
            // STATELESS significa que el servidor NO guarda memoria de quién está logueado.
            // Cada petición debe traer su propio Token (como mostrar el DNI cada vez que entras).
            .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            
            // 5. PROVEEDOR DE AUTENTICACIÓN:
            // Le dice a Spring cómo verificar la contraseña con la base de datos.
            .authenticationProvider(authenticationProvider())
            
            // 6. FILTRO JWT:
            // Ejecuta nuestro filtro (JwtAuthFilter) ANTES del filtro estándar de usuario/contraseña.
            // Esto permite entrar con el Token sin tener que mandar la contraseña de nuevo.
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * CONFIGURACIÓN CORS (El Puente con el Frontend)
     * Define quién tiene permiso para llamar a este servidor.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // IMPORTANTE: Aquí defines la URL de tu Frontend.
        // Si subes esto a internet, cambia "localhost:3000" por tu dominio real.
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000")); 
        
        // Qué acciones permitimos hacer al Frontend
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        
        // Permitimos enviar el Token en la cabecera (Authorization)
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
        
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    /**
     * PROVEEDOR DE AUTENTICACIÓN
     * Es el cerebro que conecta Spring Security con tu base de datos MySQL.
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        
        // Le decimos: "Usa este servicio para buscar usuarios en la BD"
        authProvider.setUserDetailsService(userDetailsService);
        
        // Le decimos: "Usa este encriptador para verificar las contraseñas"
        authProvider.setPasswordEncoder(passwordEncoder()); 
        
        return authProvider;
    }

    /**
     * ENCRIPTADOR DE CONTRASEÑAS
     * IMPORTANTE: Para este ejercicio académico usamos NoOp (sin encriptar).
     * En un entorno real, aquí usaríamos 'new BCryptPasswordEncoder()'.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance(); 
    }

    /**
     * MANEJADOR DE AUTENTICACIÓN
     * Es la herramienta que usas en el 'UsuarioController' para procesar el login.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}