package com.levels.backend.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.levels.backend.model.Categoria;
import com.levels.backend.repository.CategoriaRepository;

@Service
public class CategoriaService {

    @Autowired
    private CategoriaRepository categoriaRepository;

    public List<Categoria> findAll() {
        return categoriaRepository.findAll();
    }

    public Categoria save(Categoria categoria) {
        // Validación básica
        if (categoria.getNombre() == null || categoria.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la categoría es obligatorio.");
        }
        // Lógica de validación si ya existe
        if (categoriaRepository.findByNombre(categoria.getNombre()).isPresent()) {
             throw new IllegalArgumentException("La categoría " + categoria.getNombre() + " ya existe.");
        }
        return categoriaRepository.save(categoria);
    }
    
    public void delete(Long id) {
        // En un app real, esto fallaría si hay productos asociados (protección FK).
        categoriaRepository.deleteById(id);
    }
}