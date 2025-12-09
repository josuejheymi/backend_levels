package com.levels.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.levels.backend.model.DetalleOrden;

/**
 * REPOSITORIO: DETALLE ORDEN (Línea de Factura)
 * ----------------------------------------------------
 * Actúa como la capa de acceso a datos (DAO) para la entidad DetalleOrden.
 * * * Propósito: Este repositorio casi nunca se usa para buscar datos directamente, 
 * ya que los detalles se cargan automáticamente al pedir su entidad padre (la Orden).
 * Su función principal es ser el objetivo de la operación de guardado 
 * en cascada (CascadeType.ALL) durante el Checkout.
 */
@Repository
public interface DetalleOrdenRepository extends JpaRepository<DetalleOrden, Long> {
    
    /**
     * MÉTODOS HEREDADOS
     * Spring Data JPA genera automáticamente:
     * - save(DetalleOrden detalle)  // Usado implícitamente por el Cascade en Orden
     * - findAll()
     * - findById(Long id)
     * - deleteById(Long id)
     */
    
    // NOTA: No se necesitan métodos personalizados de búsqueda (findBy...)
    // porque el DetalleOrden siempre se consulta a través de su Orden asociada.
}