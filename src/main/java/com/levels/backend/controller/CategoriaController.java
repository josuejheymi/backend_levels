package com.levels.backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.levels.backend.model.Categoria;
import com.levels.backend.service.CategoriaService;

/**
 * CONTROLADOR: CATEGORÍAS
 * ----------------------------------------------------
 * Gestiona la clasificación de los productos (ej: Consolas, Periféricos).
 * Es vital para:
 * 1. El menú de navegación del Frontend (Navbar).
 * 2. El dropdown "Seleccionar Categoría" al crear un producto nuevo.
 */
@RestController
@RequestMapping("/api/categorias")
@CrossOrigin(origins = "*") // Permite acceso desde cualquier IP (Útil para desarrollo con React)
public class CategoriaController {

    @Autowired
    private CategoriaService categoriaService;

    /**
     * 1. LISTAR TODAS (Público)
     * Método: GET /api/categorias
     * Uso: El Frontend llama a esto apenas carga la página para llenar el menú.
     */
    @GetMapping 
    public List<Categoria> getAll() {
        return categoriaService.findAll();
    }

    /**
     * 2. CREAR CATEGORÍA (Solo Admin)
     * Método: POST /api/categorias
     * Body: { "nombre": "Sillas Gamer", "imagenUrl": "..." }
     */
    @PostMapping 
    public ResponseEntity<?> create(@RequestBody Categoria categoria) {
        try {
            // Guardamos usando el servicio
            Categoria nueva = categoriaService.save(categoria);
            // Devolvemos 200 OK con el objeto creado
            return ResponseEntity.ok(nueva);
        } catch (IllegalArgumentException e) {
            // Error de validación (ej: nombre vacío) -> 400 Bad Request
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error interno al crear categoría.");
        }
    }
    
    /**
     * 3. ELIMINAR CATEGORÍA (Solo Admin)
     * Método: DELETE /api/categorias/{id}
     * * PUNTO CRÍTICO: Integridad Referencial
     * No puedes borrar una categoría si ya tiene productos asignados (MySQL lanzará error).
     */
    @DeleteMapping("/{id}")                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                              
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            categoriaService.delete(id);
            return ResponseEntity.ok("✅ Categoría eliminada correctamente.");
        } catch (Exception e) {
            // Este mensaje es vital para que el usuario entienda por qué falló
            return ResponseEntity.badRequest().body("❌ Error al eliminar: Es probable que existan productos asociados a esta categoría.");
        }
    }
}