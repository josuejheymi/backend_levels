package com.levels.backend.controller;

import java.time.LocalDate;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.levels.backend.model.Usuario;
import com.levels.backend.security.JwtService;
import com.levels.backend.service.UsuarioService;

/**
 * CONTROLADOR: USUARIOS (Autenticación y Perfil)
 * ----------------------------------------------------
 * Gestiona el registro de nuevos jugadores, el inicio de sesión (Login)
 * y la actualización de datos personales.
 * * Clave de Seguridad: Aquí es donde se genera el Token JWT que el Frontend
 * debe guardar para hacer futuras peticiones.
 */
@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*") // Habilitar CORS para React
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;
    
    // Servicio de utilidad para crear/firmar Tokens JWT
    @Autowired
    private JwtService jwtService; 

    /**
     * 1. REGISTRO DE USUARIO
     * Método: POST /api/usuarios/registro
     * Body: { "nombre": "...", "email": "...", "password": "...", "fechaNacimiento": "YYYY-MM-DD", "codigoReferido": "OPT" }
     */
    @PostMapping("/registro")
    public ResponseEntity<?> registrar(@RequestBody Map<String, Object> payload) {
        try {
            // A. Construcción del objeto Usuario desde el Mapa JSON
            Usuario usuario = new Usuario();
            usuario.setNombre((String) payload.get("nombre"));
            usuario.setEmail((String) payload.get("email"));
            usuario.setPassword((String) payload.get("password"));
            
            // Conversión de Fecha (String -> LocalDate)
            // Importante: El formato debe ser ISO (2000-12-31)
            String fechaStr = (String) payload.get("fechaNacimiento");
            if (fechaStr != null && !fechaStr.isEmpty()) {
                usuario.setFechaNacimiento(LocalDate.parse(fechaStr));
            }

            // B. Código de Referido (Lógica de negocio: Puntos extra)
            String codigoReferido = (String) payload.get("codigoReferido");

            // C. Llamada al Servicio (Validaciones de edad, email único, etc.)
            Usuario nuevoUsuario = usuarioService.registrarUsuario(usuario, codigoReferido);
            
            return ResponseEntity.ok(nuevoUsuario);

        } catch (RuntimeException e) {
            // Errores de negocio (ej: "Eres menor de edad", "Email ocupado")
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            // Errores inesperados
            return ResponseEntity.internalServerError().body("Error del servidor: " + e.getMessage());
        }
    }

    /**
     * 2. LOGIN (Inicio de Sesión)
     * Método: POST /api/usuarios/login
     * Body: { "email": "...", "password": "..." }
     * * * Retorno Crítico: Devuelve el TOKEN JWT + Datos del usuario.
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credenciales) {
        try {
            String email = credenciales.get("email");
            String password = credenciales.get("password");
            
            // A. Validar credenciales contra la BD
            // Si la contraseña no coincide, el servicio lanza RuntimeException.
            Usuario usuario = usuarioService.login(email, password);
            
            // B. Generar el Token JWT (La "Llave Maestra")
            // Usamos el email como identificador principal (Subject).
            String token = jwtService.generateToken(usuario.getEmail());

            // C. Construir la respuesta completa para el Frontend
            // React necesita el token para guardarlo en localStorage y los datos para mostrar el perfil.
            Map<String, Object> respuesta = Map.of(
                "token", token,          // <--- VITAL
                "id", usuario.getId(),
                "nombre", usuario.getNombre(),
                "email", usuario.getEmail(),
                "rol", usuario.getRol(), // ADMIN, VENDEDOR, CLIENTE
                "esEstudianteDuoc", usuario.isEsEstudianteDuoc(), // Para descuentos
                "puntosLevelUp", usuario.getPuntosLevelUp(),      // Gamificación
                "nivel", usuario.getNivel(),
                "fechaNacimiento", usuario.getFechaNacimiento() != null ? usuario.getFechaNacimiento() : ""
            );

            return ResponseEntity.ok(respuesta);

        } catch (RuntimeException e) {
            // Error 401 Unauthorized: Credenciales inválidas
            return ResponseEntity.status(401).body(Map.of("error", "Credenciales incorrectas"));
        }
    }

    /**
     * 3. ACTUALIZAR PERFIL
     * Método: PUT /api/usuarios/{id}
     * Body: Objeto Usuario con los campos nuevos (nombre, password, etc.)
     * Uso: Pantalla "Mi Perfil".
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @RequestBody Usuario usuario) {
        try {
            // El servicio se encarga de verificar que el usuario exista y actualizar solo lo necesario.
            // (Ej: Si password viene vacío, no la cambia).
            Usuario usuarioActualizado = usuarioService.actualizarPerfil(id, usuario);
            return ResponseEntity.ok(usuarioActualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
}