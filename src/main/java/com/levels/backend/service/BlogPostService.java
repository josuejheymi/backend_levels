package com.levels.backend.service;

import com.levels.backend.model.BlogPost;
import com.levels.backend.repository.BlogPostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BlogPostService {

    @Autowired
    private BlogPostRepository blogRepository;

    // 1. Listar todas las noticias (Para el Home de la web)
    public List<BlogPost> listarNoticias() {
        // Podríamos ordenarlas por fecha descendente (la más nueva primero)
        // Pero por ahora usamos findAll() simple
        return blogRepository.findAll();
    }

    // 2. Crear una noticia (Para el Admin)
    public BlogPost crearNoticia(BlogPost post) {
        // La fecha se pone sola gracias al @PrePersist en el modelo, 
        // pero podemos forzarla aquí si queremos asegurar
        if (post.getFechaPublicacion() == null) {
            post.setFechaPublicacion(LocalDateTime.now());
        }
        return blogRepository.save(post);
    }

    // 3. Eliminar noticia (Para el Admin)
    public boolean eliminarNoticia(Long id) {
        if (blogRepository.existsById(id)) {
            blogRepository.deleteById(id);
            return true;
        }
        return false;
    }
}