package com.levels.backend.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.levels.backend.model.BlogPost;
import com.levels.backend.repository.BlogPostRepository;

/**
 * SERVICIO: BLOG POST (Noticias)
 * ----------------------------------------------------
 * Contiene la lógica de negocio para la gestión de publicaciones.
 * Actúa como intermediario entre el Controller y el Repositorio.
 */
@Service
public class BlogPostService {

    // Inyectamos el Repositorio: Nuestra conexión directa con la base de datos
    @Autowired
    private BlogPostRepository blogRepository;

    /**
     * 1. LISTAR TODAS LAS NOTICIAS
     * Propósito: Llenar el feed de noticias en el Frontend.
     * @return Lista de todos los BlogPost en la base de datos.
     */
    public List<BlogPost> listarNoticias() {
        // El findAll() es heredado de JpaRepository
        return blogRepository.findAll();
    }
    
    /**
     * 2. CREAR UNA NOTICIA
     * Responsabilidad: Aplicar cualquier regla de negocio antes de guardar.
     * @param post El objeto BlogPost recibido desde el Controller.
     * @return El BlogPost guardado y persistido.
     */
    public BlogPost crearNoticia(BlogPost post) {
        // Verificación de Fecha: Si la fecha no viene, la asignamos al momento actual.
        // Esto refuerza la lógica del @PrePersist en la entidad.
        if (post.getFechaPublicacion() == null) {
            post.setFechaPublicacion(LocalDateTime.now());
        }
        return blogRepository.save(post);
    }

    /**
     * 3. ELIMINAR NOTICIA
     * Responsabilidad: Verifica la existencia antes de la eliminación.
     * @param id El ID del post a eliminar.
     * @return true si el post fue encontrado y eliminado, false si no existía.
     */
    public boolean eliminarNoticia(Long id) {
        // existsById() evita una excepción si intentamos borrar algo que no existe
        if (blogRepository.existsById(id)) {
            blogRepository.deleteById(id);
            return true;
        }
        return false;
    }
}