package com.levels.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.levels.backend.model.BlogPost;

/**
 * REPOSITORIO: BLOG POST
 * ----------------------------------------------------
 * Actúa como la capa de acceso a datos (DAO) para la entidad BlogPost.
 * Es el puente que conecta el Servicio (Lógica de Negocio) con la base de datos MySQL.
 * * * MAGIA DE SPRING DATA JPA:
 * Al extender JpaRepository, Spring genera automáticamente todas las consultas SQL
 * necesarias (SELECT * FROM, INSERT INTO, UPDATE, DELETE) en tiempo de ejecución.
 */
public interface BlogPostRepository extends JpaRepository<BlogPost, Long> {

    /**
     * JpaRepository ya ofrece métodos estándar como:
     * - save(BlogPost post)
     * - findAll()
     * - findById(Long id)
     * - deleteById(Long id)
     */
    
    // Aquí se podrían añadir métodos de búsqueda personalizados si fuera necesario,
    // siguiendo el estándar de nombramiento de Spring Data (Query Method).
    // Ejemplo: List<BlogPost> findByTituloContaining(String keyword);
}