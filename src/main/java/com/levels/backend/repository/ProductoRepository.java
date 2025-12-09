package com.levels.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.levels.backend.model.Producto;

/**
 * REPOSITORIO: PRODUCTO (Inventario)
 * ----------------------------------------------------
 * Actúa como la capa de acceso a datos (DAO) para la entidad Producto.
 * Ofrece métodos básicos (CRUD) y consultas específicas para el filtrado del catálogo.
 */
@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    /**
     * 1. FILTRAR POR CATEGORÍA (Consulta por Relación)
     * ----------------------------------------------------
     * Spring Data JPA permite buscar por un campo de una ENTIDAD RELACIONADA.
     * * Traducción SQL: SELECT * FROM producto p JOIN categoria c ON p.categoria_id = c.id WHERE c.nombre = ?
     * * Mecanismo: Se usa la convención 'NombreDeLaRelacion_NombreDelCampo' (Categoria_Nombre).
     * * Uso: Es la base para mostrar los productos cuando el usuario hace clic en "Consolas" o "Periféricos".
     * @param nombre El nombre exacto de la categoría (ej: "Consolas").
     * @return Lista de productos que pertenecen a esa categoría.
     */
    List<Producto> findByCategoria_Nombre(String nombre);
    
    // 2. Opcion B: Buscar por ID de la Categoría (Alternativa)
    // List<Producto> findByCategoriaId(Long id);
}