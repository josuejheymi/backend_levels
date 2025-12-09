package com.levels.backend.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.levels.backend.model.Categoria;
import com.levels.backend.repository.CategoriaRepository;

/**
 * SERVICIO: CATEGORÍA
 * ----------------------------------------------------
 * Contiene la lógica de negocio para la gestión del catálogo de categorías.
 * Su rol principal es aplicar validaciones (ej: no duplicados) antes de interactuar
 * con la base de datos.
 */
@Service
public class CategoriaService {

    // Inyección de la capa de persistencia (DAO)
    @Autowired
    private CategoriaRepository categoriaRepository;

    /**
     * 1. LISTAR TODAS
     * Uso: Llenar los menús y filtros de navegación en el Frontend.
     * @return Lista de todas las categorías disponibles.
     */
    public List<Categoria> findAll() {
        return categoriaRepository.findAll();
    }

    /**
     * 2. CREAR Y VALIDAR CATEGORÍA
     * Responsabilidad: Ejecutar la lógica de negocio antes de guardar.
     * @param categoria El objeto Categoria recibido del Controller.
     * @return La categoría guardada.
     */
    public Categoria save(Categoria categoria) {
        
        // Validación 1: El nombre no puede ser nulo o vacío
        if (categoria.getNombre() == null || categoria.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la categoría es obligatorio.");
        }
        
        // Validación 2: Chequeo de duplicados
        // Buscamos si ya existe una categoría con ese nombre.
        if (categoriaRepository.findByNombre(categoria.getNombre()).isPresent()) {
            // Si findByNombre devuelve algo (Optional.isPresent()), lanzamos excepción.
            throw new IllegalArgumentException("La categoría " + categoria.getNombre() + " ya existe.");
        }
        
        // Si las validaciones pasan, delegamos la persistencia al repositorio.
        return categoriaRepository.save(categoria);
    }
    
    /**
     * 3. ELIMINAR CATEGORÍA
     * Uso: Operación de administrador.
     * * Nota Importante: La protección para que no se borre una categoría si tiene
     * productos asociados la maneja directamente la Base de Datos (Integridad Referencial).
     * El Controller captura esa excepción y devuelve un mensaje amigable.
     * @param id El ID de la categoría a eliminar.
     */
    public void delete(Long id) {
        categoriaRepository.deleteById(id);
    }
}