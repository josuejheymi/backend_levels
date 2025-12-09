package com.levels.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.levels.backend.model.Resena;

/**
 * REPOSITORIO: RESEÑA (Opiniones)
 * ----------------------------------------------------
 * Capa de acceso a datos para la entidad Resena.
 * Maneja la persistencia de las opiniones y las consultas por producto,
 * siendo fundamental para la "Prueba Social" de la tienda.
 */
@Repository
public interface ResenaRepository extends JpaRepository<Resena, Long> {

    /**
     * 1. BUSCAR RESEÑAS POR PRODUCTO (Derived Select Query)
     * ----------------------------------------------------
     * Spring Data JPA infiere la consulta: SELECT * FROM resena WHERE producto_id = ?
     * * Uso: Es la base para mostrar todas las opiniones en la página de Detalle de Producto.
     * @param productoId El ID del producto que se está revisando.
     * @return Lista de reseñas asociadas a ese producto.
     */
    List<Resena> findByProductoId(Long productoId);

    /**
     * 2. ELIMINACIÓN POR PRODUCTO (Derived Delete Query)
     * ----------------------------------------------------
     * Spring Data JPA infiere la consulta: DELETE FROM resena WHERE producto_id = ?
     * * Uso: Esta herramienta de mantenimiento es necesaria si un administrador elimina
     * un producto del catálogo; esta consulta elimina todas sus reseñas asociadas de la base de datos.
     * * Nota: Se prefiere este método a la cascada de JPA si el producto tiene muchas relaciones.
     * @param productoId El ID del producto a eliminar.
     */
    void deleteByProductoId(Long productoId); 
}