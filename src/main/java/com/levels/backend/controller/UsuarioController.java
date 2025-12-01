package com.levels.backend.controller;

import com.levels.backend.model.Usuario;
import com.levels.backend.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.levels.backend.security.JwtService;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*") // Importante: Permite que React entre sin problemas
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;
    @Autowired
    private JwtService jwtService; // <---  servicio de JWT

    // --- ENDPOINT DE REGISTRO ---
    // URL: POST x
    @PostMapping("/registro")
    public ResponseEntity<?> registrar(@RequestBody Map<String, Object> payload) {
        try {
            // 1. Extraemos los datos del JSON que envía el frontend
            Usuario usuario = new Usuario();
            usuario.setNombre((String) payload.get("nombre"));
            usuario.setEmail((String) payload.get("email"));
            usuario.setPassword((String) payload.get("password"));
            
            // Convertimos el String de fecha a LocalDate (formato YYYY-MM-DD)
            String fechaStr = (String) payload.get("fechaNacimiento");
            usuario.setFechaNacimiento(LocalDate.parse(fechaStr));

            // Capturamos el código de referido (si es que viene)
            String codigoReferido = (String) payload.get("codigoReferido");

            // 2. Llamamos al servicio para que haga la magia
            Usuario nuevoUsuario = usuarioService.registrarUsuario(usuario, codigoReferido);
            
            return ResponseEntity.ok(nuevoUsuario);

        } catch (RuntimeException e) {
            // Si falla (ej: es menor de edad), devolvemos error 400 y el mensaje
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error del servidor: " + e.getMessage());
        }
    }

    // --- ENDPOINT DE LOGIN ---
    // URL: POST http://localhost:8080/api/usuarios/login
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credenciales) {
        try {
            String email = credenciales.get("email");
            String password = credenciales.get("password");
            
            // 1. Validamos credenciales (Lanza error si falla)
            Usuario usuario = usuarioService.login(email, password);
            
            // 2. Generamos el Token JWT
            String token = jwtService.generateToken(usuario.getEmail());

            // 3. Devolvemos TODO: Datos del usuario + Token
            // Creamos una respuesta personalizada (Map)
            Map<String, Object> respuesta = Map.of(
                "token", token,          // <--- ¡AQUÍ VA EL TOKEN!
                "id", usuario.getId(),
                "nombre", usuario.getNombre(),
                "email", usuario.getEmail(),
                "rol", usuario.getRol(),
                "esEstudianteDuoc", usuario.isEsEstudianteDuoc(),
                "puntosLevelUp", usuario.getPuntosLevelUp(),
                "nivel", usuario.getNivel(),
                "fechaNacimiento", usuario.getFechaNacimiento()
            );

            return ResponseEntity.ok(respuesta);

        } catch (RuntimeException e) {
            return ResponseEntity.status(401).body(Map.of("error", "Credenciales incorrectas"));
        }
    }

    // NUEVO ENDPOINT: Actualizar Perfil
    // URL: PUT http://localhost:8080/api/usuarios/{id}
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @RequestBody Usuario usuario) {
        try {
            Usuario usuarioActualizado = usuarioService.actualizarPerfil(id, usuario);
            return ResponseEntity.ok(usuarioActualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
}