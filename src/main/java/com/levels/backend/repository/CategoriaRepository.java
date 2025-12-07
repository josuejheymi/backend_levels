package com.levels.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.levels.backend.model.Categoria;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
    
    // Método útil para buscar por nombre (asegura que no haya duplicados)
    Optional<Categoria> findByNombre(String nombre);
}