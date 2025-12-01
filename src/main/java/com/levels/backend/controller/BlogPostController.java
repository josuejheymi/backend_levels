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

@RestController
@RequestMapping("/api/blog")
@CrossOrigin(origins = "*")
public class BlogPostController {

    @Autowired
    private BlogPostService blogService;

    // GET: Ver todas las noticias
    @GetMapping
    public List<BlogPost> listar() {
        return blogService.listarNoticias();
    }

    // POST: Publicar noticia (Admin)
    @PostMapping
    public ResponseEntity<?> publicar(@RequestBody BlogPost post) {
        try {
            BlogPost nuevoPost = blogService.crearNoticia(post);
            return ResponseEntity.ok(nuevoPost);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al publicar: " + e.getMessage());
        }
    }

    // DELETE: Borrar noticia
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        if (blogService.eliminarNoticia(id)) {
            return ResponseEntity.ok("Noticia eliminada correctamente");
        }
        return ResponseEntity.notFound().build();
    }
    // En BlogPostController.java

@Autowired
private BlogPostRepository blogRepository; // Asegúrate de tener el repositorio inyectado

// GET: Obtener noticia por ID
@GetMapping("/{id}")
public ResponseEntity<BlogPost> obtenerPorId(@PathVariable Long id) {
    return blogRepository.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
}
}