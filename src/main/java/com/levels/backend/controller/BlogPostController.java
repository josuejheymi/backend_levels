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

import com.levels.backend.model.BlogPost;
import com.levels.backend.repository.BlogPostRepository;
import com.levels.backend.service.BlogPostService;

/**
 * CONTROLADOR: BLOG (Noticias)
 * ----------------------------------------------------
 * Expone los endpoints REST para gestionar las noticias del sistema.
 * Permite listar, crear, borrar y ver detalles de publicaciones.
 */
@RestController
@RequestMapping("/api/blog")
@CrossOrigin(origins = "*") // Permite peticiones desde cualquier origen (útil para desarrollo)
public class BlogPostController {

    // Inyección del Servicio (Lógica de Negocio Principal)
    @Autowired
    private BlogPostService blogService;

    // Inyección del Repositorio (Acceso Directo a BD para lectura rápida)
    // Mantenemos esto aquí para no modificar el Service existente.
    @Autowired
    private BlogPostRepository blogRepository;

    /**
     * 1. LISTAR NOTICIAS (GET /api/blog)
     * Devuelve la lista completa de publicaciones.
     */
    @GetMapping
    public List<BlogPost> listar() {
        return blogService.listarNoticias();
    }

    /**
     * 2. OBTENER NOTICIA POR ID (GET /api/blog/{id})
     * Busca una noticia específica. Si existe devuelve 200 OK, si no, 404 Not Found.
     * * Nota: Accede directamente al repositorio.
     */
    @GetMapping("/{id}")
    public ResponseEntity<BlogPost> obtenerPorId(@PathVariable Long id) {
        return blogRepository.findById(id)
                .map(ResponseEntity::ok) // Si encuentra la noticia, la devuelve con OK
                .orElse(ResponseEntity.notFound().build()); // Si no, devuelve Error 404
    }

    /**
     * 3. PUBLICAR NOTICIA (POST /api/blog)
     * Recibe una noticia nueva y la guarda.
     * Requiere permisos de Admin (configurado en SecurityConfig).
     */
    @PostMapping
    public ResponseEntity<?> publicar(@RequestBody BlogPost post) {
        try {
            BlogPost nuevoPost = blogService.crearNoticia(post);
            return ResponseEntity.ok(nuevoPost);
        } catch (Exception e) {
            // Manejo de errores controlado
            return ResponseEntity.badRequest().body("Error al publicar: " + e.getMessage());
        }
    }

    /**
     * 4. ELIMINAR NOTICIA (DELETE /api/blog/{id})
     * Elimina una noticia por su ID.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        if (blogService.eliminarNoticia(id)) {
            return ResponseEntity.ok("Noticia eliminada correctamente");
        }
        return ResponseEntity.notFound().build();
    }
}