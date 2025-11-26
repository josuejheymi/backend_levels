package com.levels.backend.controller;

import com.levels.backend.model.BlogPost;
import com.levels.backend.service.BlogPostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
}