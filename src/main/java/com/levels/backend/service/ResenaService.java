package com.levels.backend.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.levels.backend.model.Producto;
import com.levels.backend.model.Resena;
import com.levels.backend.model.Usuario;
import com.levels.backend.repository.ProductoRepository;
import com.levels.backend.repository.ResenaRepository;
import com.levels.backend.repository.UsuarioRepository;

/**
 * SERVICIO: RESEÑAS (Opiniones de Usuario)
 * ----------------------------------------------------
 * Contiene la lógica de negocio para crear, validar y listar opiniones sobre los productos.
 * Actúa como punto de control para la integridad de los datos sociales.
 */
@Service
public class ResenaService {

    // Inyección de los Repositorios necesarios para las entidades involucradas
    @Autowired
    private ResenaRepository resenaRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private ProductoRepository productoRepository;

    /**
     * 1. AGREGAR UNA RESEÑA
     * ----------------------------------------------------
     * Responsabilidad: Valida la calificación, verifica la existencia de las entidades
     * relacionadas y guarda la nueva reseña.
     */
    public Resena crearResena(Long usuarioId, Long productoId, Integer estrellas, String comentario) {
        
        // Validación de Datos: Regla de negocio simple (ej: rango de estrellas)
        if (estrellas < 1 || estrellas > 5) {
            throw new RuntimeException("La calificación debe ser entre 1 y 5 estrellas.");
        }

        // Validación de Entidades (Integridad Referencial en la capa Service)
        // 1. Buscamos el usuario por su ID
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        // 2. Buscamos el producto por su ID
        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        // 3. Creación del Objeto Reseña
        Resena resena = new Resena();
        resena.setUsuario(usuario);
        resena.setProducto(producto);
        resena.setCalificacion(estrellas);
        resena.setComentario(comentario);
        
        // Asignación del dato de auditoría (fecha/hora de publicación)
        // Nota: Si la entidad tuviera @PrePersist, este paso sería redundante.
        resena.setFecha(LocalDateTime.now()); 

        // 4. Persistencia en la Base de Datos
        return resenaRepository.save(resena);
    }

    /**
     * 2. VER RESEÑAS DE UN PRODUCTO
     * ----------------------------------------------------
     * Uso: Llenar la sección de comentarios en la página de detalle del producto.
     * @param productoId El ID del producto cuyas reseñas queremos ver.
     * @return Lista de reseñas asociadas.
     */
    public List<Resena> obtenerResenasPorProducto(Long productoId) {
        // Delegamos la búsqueda por llave foránea al repositorio (findByProductoId)
        return resenaRepository.findByProductoId(productoId);
    }
}