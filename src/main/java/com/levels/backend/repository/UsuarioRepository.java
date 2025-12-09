package com.levels.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.levels.backend.model.Usuario;

/**
 * REPOSITORIO: USUARIO
 * ----------------------------------------------------
 * Capa de acceso a datos para la entidad Usuario.
 * Es la base para la autenticación (login) y el sistema de referidos.
 */
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    /**
     * 1. BÚSQUEDA POR EMAIL (Para el Login)
     * ----------------------------------------------------
     * Spring Data JPA infiere la consulta: SELECT * FROM usuario WHERE email = ?
     * * Uso Crítico: 
     * a) Es esencial en el proceso de Login para buscar al usuario por su credencial principal.
     * b) Es usado por Spring Security para cargar los datos del usuario.
     * @param email El correo electrónico del usuario.
     * @return El objeto Usuario completo, o null si no existe.
     */
    Usuario findByEmail(String email);

    /**
     * 2. BÚSQUEDA POR CÓDIGO DE REFERIDO (Para Gamificación)
     * ----------------------------------------------------
     * Spring Data JPA infiere la consulta: SELECT * FROM usuario WHERE codigo_referido_propio = ?
     * * Uso: Permite verificar si un código de referido existe durante el registro de un nuevo usuario.
     * Si existe, se le pueden asignar puntos o beneficios al dueño del código.
     * @param codigo El código de referido proporcionado durante el registro.
     * @return El objeto Usuario dueño de ese código, o null.
     */
    Usuario findByCodigoReferidoPropio(String codigo);
}