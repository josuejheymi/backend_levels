package com.levels.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.levels.backend.model.Categoria;

/**
 * REPOSITORIO: CATEGORÍA
 * ----------------------------------------------------
 * Actúa como la capa de acceso a datos (DAO) para la entidad Categoria.
 * Permite las operaciones CRUD básicas y consultas específicas para la lógica de negocio.
 */
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

    /**
     * MÉTODO DE CONSULTA PERSONALIZADO (Query Method)
     * ----------------------------------------------------
     * Spring Data JPA construye automáticamente la consulta SQL a partir del nombre del método.
     * * Traducción SQL: SELECT * FROM categoria WHERE nombre = ?
     * * * Uso: Es vital para el ProductoController. Permite verificar si una categoría
     * con ese nombre ya existe antes de crear una nueva, evitando duplicados.
     * * @param nombre El nombre de la categoría a buscar.
     * @return Un objeto Optional<Categoria> (puede estar presente o vacío).
     */
    Optional<Categoria> findByNombre(String nombre);
}