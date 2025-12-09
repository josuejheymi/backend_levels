package com.levels.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.levels.backend.model.Orden;

/**
 * REPOSITORIO: ÓRDENES DE COMPRA (Ventas)
 * ----------------------------------------------------
 * Capa de acceso a datos para las transacciones finalizadas.
 * Es crucial para el historial de compras del cliente y los reportes de administración.
 */
@Repository
public interface OrdenRepository extends JpaRepository<Orden, Long> {
    
    /**
     * 1. BUSCAR ÓRDENES POR USUARIO (Query Method)
     * ----------------------------------------------------
     * Spring Data JPA infiere la consulta: SELECT * FROM ordenes WHERE usuario_id = ?
     * * Uso: Obtener el historial de compras para la página "Mi Perfil" en React.
     * @param usuarioId El ID del cliente.
     * @return Lista de órdenes asociadas a ese cliente.
     */
    List<Orden> findByUsuarioId(Long usuarioId);

    // OPCIÓN MEJORADA (Ejemplo de optimización en el Query Method):
    // List<Orden> findByUsuarioIdOrderByIdDesc(Long usuarioId);

    /**
     * 2. SUMAR VENTAS TOTALES (JPQL Personalizada)
     * ----------------------------------------------------
     * * Uso: Reporte de Dashboard (Estadísticas). Calcula el monto total de todas las órdenes.
     * * @Query: Indica que Spring debe usar esta consulta escrita en JPQL (similar a SQL, pero usa nombres de Clases/Campos de Java).
     * * COALESCE(SUM(o.total), 0):
     * - SUM(o.total): Suma la columna total.
     * - COALESCE(..., 0): Si la suma es NULL (porque no hay ventas), la convierte en 0. Esto previene errores en el Controller/Frontend.
     * @return El monto total de las ventas.
     */
    @Query("SELECT COALESCE(SUM(o.total), 0) FROM Orden o")
    Double sumarVentasTotales();
}