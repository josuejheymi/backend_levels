package com.levels.backend.service;

import java.time.LocalDate;
import java.time.Period;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.levels.backend.model.Usuario;
import com.levels.backend.repository.UsuarioRepository;

/**
 * SERVICIO: GESTIÓN DE USUARIOS
 * ----------------------------------------------------
 * Contiene la lógica central para:
 * 1. Aplicar reglas de negocio (validaciones, descuentos, referidos) durante el registro.
 * 2. Autenticar usuarios (Login).
 * 3. Gestionar el sistema de Gamificación (Puntos y Nivel).
 */
@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    /**
     * MÉTODO PRINCIPAL: REGISTRAR USUARIO
     * ----------------------------------------------------
     * Aplica 5 reglas de negocio antes de permitir la creación de la cuenta.
     */
    public Usuario registrarUsuario(Usuario usuario, String codigoReferidoIngresado) {
        
        // REGLA 1: Validación de Edad (+18 años)
        if (usuario.getFechaNacimiento() == null) {
            throw new RuntimeException("La fecha de nacimiento es obligatoria.");
        }
        // Calcula la edad comparando la fecha de nacimiento con el día actual
        int edad = Period.between(usuario.getFechaNacimiento(), LocalDate.now()).getYears();
        if (edad < 18) {
            throw new RuntimeException("Lo sentimos, debes ser mayor de 18 años para registrarte.");
        }

        // REGLA 2: Correo único
        if (usuarioRepository.findByEmail(usuario.getEmail()) != null) {
            throw new RuntimeException("Este correo ya está registrado.");
        }

        // REGLA 3: Descuento Duoc (20% - Lógica de Negocio)
        if (usuario.getEmail().endsWith("@duoc.cl") || usuario.getEmail().endsWith("@profesor.duoc.cl")) {
            usuario.setEsEstudianteDuoc(true); // Activa el beneficio
        } else {
            usuario.setEsEstudianteDuoc(false);
        }

        // REGLA 4: Generar código de referido propio
        // UUID genera una cadena única que se recorta para hacerla más manejable (8 caracteres)
        String codigoPropio = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        usuario.setCodigoReferidoPropio(codigoPropio);

        // REGLA 5: Sistema de Referidos (Si el usuario fue invitado)
        if (codigoReferidoIngresado != null && !codigoReferidoIngresado.isEmpty()) {
            Usuario padrino = usuarioRepository.findByCodigoReferidoPropio(codigoReferidoIngresado);
            if (padrino != null) {
                // El "padrino" gana puntos por traer a un amigo
                sumarPuntos(padrino, 100); 
                usuarioRepository.save(padrino); // Persistimos los puntos del padrino
                System.out.println("Puntos sumados al usuario: " + padrino.getNombre());
            }
        }

        // Inicializar valores por defecto
        usuario.setPuntosLevelUp(0);
        usuario.setNivel("Novato");
        // Aseguramos que, si el rol no fue seteado (ej: por un admin), sea CLIENTE
        if (usuario.getRol() == null || usuario.getRol().isEmpty()) {
            usuario.setRol("CLIENTE"); 
        }

        return usuarioRepository.save(usuario);
    }

    /**
     * MÉTODO: LOGIN (Autenticación)
     * * Nota: En un sistema profesional, se compararía el hash de la contraseña.
     */
    public Usuario login(String email, String password) {
        Usuario usuario = usuarioRepository.findByEmail(email);
        
        // Verificación simple: Si existe y la contraseña coincide
        if (usuario != null && usuario.getPassword().equals(password)) {
            return usuario;
        }
        // Lanza una excepción que el Controller captura y convierte en HTTP 401
        throw new RuntimeException("Credenciales incorrectas"); 
    }

    /**
     * MÉTODO AUXILIAR: Lógica de Gamificación (Puntos y Nivel)
     * * Es privado porque solo debe ser llamado por otros métodos dentro del Servicio.
     */
    private void sumarPuntos(Usuario u, int puntos) {
        u.setPuntosLevelUp(u.getPuntosLevelUp() + puntos);
        
        // Lógica de Niveles (Reglas de ascenso)
        if (u.getPuntosLevelUp() >= 1000) u.setNivel("Leyenda");
        else if (u.getPuntosLevelUp() >= 500) u.setNivel("Pro");
        else u.setNivel("Novato");
    }

    /**
     * MÉTODO: ACTUALIZAR PERFIL
     * Aplica los cambios enviados por el usuario, actualizando solo los campos necesarios.
     */
    public Usuario actualizarPerfil(Long id, Usuario datosNuevos) {
        // 1. Buscamos la versión original del usuario
        Usuario usuarioActual = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // 2. Aplicamos cambios solo si el nuevo dato viene seteado (defensivo)
        if (datosNuevos.getNombre() != null) {
            usuarioActual.setNombre(datosNuevos.getNombre());
        }
        if (datosNuevos.getPassword() != null && !datosNuevos.getPassword().isEmpty()) {
            // En un app real, aquí se hashearía la nueva contraseña: usuarioActual.setPassword(hashear(nueva));
            usuarioActual.setPassword(datosNuevos.getPassword());
        }

        // 3. Re-validación de Edad si cambia la fecha
        if (datosNuevos.getFechaNacimiento() != null) {
            int edad = Period.between(datosNuevos.getFechaNacimiento(), LocalDate.now()).getYears();
            if (edad < 18) {
                throw new RuntimeException("No puedes poner una fecha que te haga menor de edad.");
            }
            usuarioActual.setFechaNacimiento(datosNuevos.getFechaNacimiento());
        }

        // 4. Guardamos los cambios
        return usuarioRepository.save(usuarioActual);
    }
}