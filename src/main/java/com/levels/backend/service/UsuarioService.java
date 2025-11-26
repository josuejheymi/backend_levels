package com.levels.backend.service;

import com.levels.backend.model.Usuario;
import com.levels.backend.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.UUID;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    // Método principal para registrar usuarios con todas las reglas
    public Usuario registrarUsuario(Usuario usuario, String codigoReferidoIngresado) {
        
        // REGLA 1: Validar edad (+18 años)
        if (usuario.getFechaNacimiento() == null) {
            throw new RuntimeException("La fecha de nacimiento es obligatoria.");
        }
        int edad = Period.between(usuario.getFechaNacimiento(), LocalDate.now()).getYears();
        if (edad < 18) {
            throw new RuntimeException("Lo sentimos, debes ser mayor de 18 años para registrarte.");
        }

        // REGLA 2: Correo único
        if (usuarioRepository.findByEmail(usuario.getEmail()) != null) {
            throw new RuntimeException("Este correo ya está registrado.");
        }

        // REGLA 3: Descuento Duoc (20%)
        // Si el correo termina en @duoc.cl o @profesor.duoc.cl, activamos el flag
        if (usuario.getEmail().endsWith("@duoc.cl") || usuario.getEmail().endsWith("@profesor.duoc.cl")) {
            usuario.setEsEstudianteDuoc(true);
        } else {
            usuario.setEsEstudianteDuoc(false);
        }

        // REGLA 4: Generar código de referido propio
        // Creamos un código único de 8 letras para que este usuario invite a otros
        String codigoPropio = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        usuario.setCodigoReferidoPropio(codigoPropio);

        // REGLA 5: Sistema de Referidos (Si vino invitado por alguien)
        if (codigoReferidoIngresado != null && !codigoReferidoIngresado.isEmpty()) {
            Usuario padrino = usuarioRepository.findByCodigoReferidoPropio(codigoReferidoIngresado);
            if (padrino != null) {
                // El "padrino" gana puntos por traer a un amigo
                sumarPuntos(padrino, 100); 
                usuarioRepository.save(padrino);
                System.out.println("Puntos sumados al usuario: " + padrino.getNombre());
            }
        }

        // Inicializar valores por defecto
        usuario.setPuntosLevelUp(0);
        usuario.setNivel("Novato");
        if (usuario.getRol() == null || usuario.getRol().isEmpty()) {
            usuario.setRol("CLIENTE"); // Por seguridad, siempre empieza como cliente
        }

        return usuarioRepository.save(usuario);
    }

    public Usuario login(String email, String password) {
        Usuario usuario = usuarioRepository.findByEmail(email);
        if (usuario != null && usuario.getPassword().equals(password)) {
            return usuario;
        }
        throw new RuntimeException("Credenciales incorrectas");
    }

    // Método auxiliar para Gamificación
    private void sumarPuntos(Usuario u, int puntos) {
        u.setPuntosLevelUp(u.getPuntosLevelUp() + puntos);
        
        // Lógica de Niveles
        if (u.getPuntosLevelUp() >= 1000) u.setNivel("Leyenda");
        else if (u.getPuntosLevelUp() >= 500) u.setNivel("Pro");
        else u.setNivel("Novato");
    }

    // NUEVO MÉTODO: Actualizar perfil
    public Usuario actualizarPerfil(Long id, Usuario datosNuevos) {
        // 1. Buscamos si el usuario existe
        Usuario usuarioActual = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // 2. Actualizamos solo si el dato viene en la petición
        if (datosNuevos.getNombre() != null) {
            usuarioActual.setNombre(datosNuevos.getNombre());
        }
        
        if (datosNuevos.getPassword() != null && !datosNuevos.getPassword().isEmpty()) {
            usuarioActual.setPassword(datosNuevos.getPassword());
        }

        // 3. ¿Permitimos cambiar la fecha de nacimiento? 
        // Si lo permitimos, hay que re-validar que siga siendo mayor de 18
        if (datosNuevos.getFechaNacimiento() != null) {
            int edad = Period.between(datosNuevos.getFechaNacimiento(), LocalDate.now()).getYears();
            if (edad < 18) {
                throw new RuntimeException("No puedes poner una fecha que te haga menor de edad.");
            }
            usuarioActual.setFechaNacimiento(datosNuevos.getFechaNacimiento());
        }

        // Guardamos los cambios
        return usuarioRepository.save(usuarioActual);
    }
}
    