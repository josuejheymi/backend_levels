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

@RestController
@RequestMapping("/api/categorias")
@CrossOrigin(origins = "*")
public class CategoriaController {

    @Autowired
    private CategoriaService categoriaService;

    // GET: Listar todas las categorías (Endpoint clave para el Dropdown)
    @GetMapping 
    public List<Categoria> getAll() {
        return categoriaService.findAll();
    }

    // POST: Crear una nueva categoría (Requiere Rol ADMIN)
    @PostMapping 
    public ResponseEntity<?> create(@RequestBody Categoria categoria) {
        try {
            Categoria nueva = categoriaService.save(categoria);
            return ResponseEntity.ok(nueva);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
             return ResponseEntity.badRequest().body("Error interno al crear categoría.");
        }
    }
    
    // DELETE: Eliminar una categoría (Requiere Rol ADMIN)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            categoriaService.delete(id);
            return ResponseEntity.ok("Categoría eliminada.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al eliminar. Asegúrese de que no haya productos usándola.");
        }
    }
}   