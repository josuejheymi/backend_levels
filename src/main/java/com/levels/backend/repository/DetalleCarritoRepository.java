package com.levels.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.levels.backend.model.DetalleCarrito;

@Repository
public interface DetalleCarritoRepository extends JpaRepository<DetalleCarrito, Long> {
    
    // Método que agregamos ayer para borrar producto
    void deleteByProductoId(Long productoId);

    // --- NUEVO MÉTODO PARA EL CARRITO ---
    // Borra todas las filas que coincidan con este carrito y este producto
    void deleteByCarritoIdAndProductoId(Long carritoId, Long productoId);
}