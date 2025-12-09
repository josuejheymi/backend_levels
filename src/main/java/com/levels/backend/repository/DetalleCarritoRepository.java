package com.levels.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.levels.backend.model.DetalleCarrito;

/**
 * REPOSITORIO: DETALLE CARRITO (Ítem por Ítem)
 * ----------------------------------------------------
 * Capa de acceso a datos para las líneas de producto individuales en la cesta.
 * Es vital para las funciones de 'Eliminar' y 'Actualizar Cantidad' del carrito.
 */
@Repository
public interface DetalleCarritoRepository extends JpaRepository<DetalleCarrito, Long> {
    
    /**
     * MÉTODO DE ELIMINACIÓN AUTOMÁTICA (Derived Delete Query)
     * ----------------------------------------------------
     * Spring Data JPA infiere la consulta DELETE a partir del nombre del método.
     * * Traducción SQL: DELETE FROM detalle_carrito WHERE producto_id = ?
     * * Uso: Permite borrar todos los items de carrito que apunten a un producto
     * que acaba de ser eliminado del catálogo. Esto mantiene la BD limpia.
     * @param productoId El ID del producto a eliminar de todos los carritos.
     */
    void deleteByProductoId(Long productoId);

    /**
     * MÉTODO DE ELIMINACIÓN COMPUESTA (Derived Delete Query)
     * ----------------------------------------------------
     * Permite eliminar una línea de ítem específica dentro de UN carrito específico.
     * * Traducción SQL: DELETE FROM detalle_carrito WHERE carrito_id = ? AND producto_id = ?
     * * Uso: Es la base para el botón 'Eliminar' en la vista del carrito.
     * @param carritoId El ID del carrito padre.
     * @param productoId El ID del producto a remover del carrito.
     */
    void deleteByCarritoIdAndProductoId(Long carritoId, Long productoId);
}