package com.levels.backend.service;

import com.levels.backend.model.*;
import com.levels.backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ResenaService {

    @Autowired
    private ResenaRepository resenaRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private ProductoRepository productoRepository;

    // 1. Agregar una reseña
    public Resena crearResena(Long usuarioId, Long productoId, Integer estrellas, String comentario) {
        // Validaciones
        if (estrellas < 1 || estrellas > 5) {
            throw new RuntimeException("La calificación debe ser entre 1 y 5 estrellas.");
        }

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        // Creamos la reseña
        Resena resena = new Resena();
        resena.setUsuario(usuario);
        resena.setProducto(producto);
        resena.setCalificacion(estrellas);
        resena.setComentario(comentario);
        resena.setFecha(LocalDateTime.now()); // Fecha y hora actual

        return resenaRepository.save(resena);
    }

    // 2. Ver reseñas de un producto
    public List<Resena> obtenerResenasPorProducto(Long productoId) {
        return resenaRepository.findByProductoId(productoId);
    }
}