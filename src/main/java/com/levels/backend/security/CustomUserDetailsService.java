package com.levels.backend.security;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.levels.backend.model.Usuario;
import com.levels.backend.repository.UsuarioRepository;

/**
 * SERVICIO: CARGA DE DATOS DE USUARIO (Spring Security)
 * ----------------------------------------------------
 * Implementa la interfaz central UserDetailsService.
 * Su única responsabilidad es cargar los detalles de un usuario (principalmente 
 * el email, la contraseña y el rol) desde la base de datos al ser solicitados 
 * por el proceso de autenticación o validación de tokens.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    // Necesitamos el Repositorio para buscar en la tabla 'usuario'
    @Autowired
    private UsuarioRepository usuarioRepository;

    /**
     * MÉTODO PRINCIPAL DE LA SEGURIDAD
     * ----------------------------------------------------
     * Spring Security llama a este método cuando:
     * 1. Un usuario intenta iniciar sesión (Login).
     * 2. Se recibe un Token JWT y Spring necesita verificar si el usuario existe.
     * * @param email El correo electrónico (que funciona como nombre de usuario).
     * @throws UsernameNotFoundException Si no encuentra el email en la BD.
     * @return Un objeto UserDetails con la info esencial de seguridad.
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // 1. Buscar el usuario en la BD usando el método personalizado del Repositorio
        Usuario usuario = usuarioRepository.findByEmail(email);
        
        if (usuario == null) {
            // Si no existe, lanzamos la excepción estándar de Spring Security
            throw new UsernameNotFoundException("Usuario no encontrado: " + email);
        }

        // 2. Mapeo: Convertir nuestra entidad 'Usuario' a 'User' de Spring Security
        // Spring Security solo necesita: email, password y roles/autoridades.
        return new User(
                usuario.getEmail(), // Credencial (Username)
                usuario.getPassword(), // Contraseña (Debe estar hasheada, aunque aquí usamos NoOp)
                
                // Roles (Autoridades)
                // Es obligatorio anteponer "ROLE_" al nombre del rol (ADMIN -> ROLE_ADMIN)
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + usuario.getRol()))
        );
    }
}