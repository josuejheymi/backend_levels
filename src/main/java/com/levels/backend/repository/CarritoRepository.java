package com.levels.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.levels.backend.model.Carrito;

/**
 * REPOSITORIO: CARRITO DE COMPRAS
 * ----------------------------------------------------
 * Actúa como la capa de acceso a datos (DAO) para la entidad Carrito.
 * Ofrece métodos básicos (CRUD) y consultas específicas para la lógica de negocio.
 */
public interface CarritoRepository extends JpaRepository<Carrito, Long> {

    /**
     * MÉTODO DE CONSULTA PERSONALIZADO (Query Method)
     * ----------------------------------------------------
     * Spring Data JPA automáticamente construye una consulta SQL a partir del nombre del método.
     * * Traducción SQL: SELECT * FROM carrito WHERE usuario_id = ?
     * * Uso: Es esencial para el CarritoService. Permite buscar y cargar el carrito 
     * único asociado al ID del usuario que está logueado en ese momento.
     * * @param usuarioId El ID del usuario.
     * @return El objeto Carrito asociado a ese usuario (relación OneToOne).
     */
    Carrito findByUsuarioId(Long usuarioId);
}