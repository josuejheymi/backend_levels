package com.levels.backend.repository;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.levels.backend.model.Producto;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
    // 1. Opcion A (Lógica del Frontend): Buscar por el NOMBRE del objeto Categoria
    // Esto funciona porque la entidad Categoria tiene un campo 'nombre'.
    List<Producto> findByCategoria_Nombre(String nombre);
    
    // 2. Opcion B (Si necesitas buscar por el ID de la categoría)
    // List<Producto> findByCategoriaId(Long id); 
}