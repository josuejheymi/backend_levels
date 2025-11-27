package com.levels.backend.repository;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.levels.backend.model.Resena;

public interface ResenaRepository extends JpaRepository<Resena, Long> {
    List<Resena> findByProductoId(Long productoId);
    void deleteByProductoId(Long productoId); 
}