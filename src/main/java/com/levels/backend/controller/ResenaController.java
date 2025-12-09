package com.levels.backend.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.levels.backend.model.Resena;
import com.levels.backend.service.ResenaService;

/**
 * CONTROLADOR: RESEÑAS (Opiniones)
 * ----------------------------------------------------
 * Gestiona el sistema de valoraciones del e-commerce.
 * Permite que los usuarios califiquen productos y dejen comentarios.
 * Es fundamental para la credibilidad de la tienda ("Social Proof").
 */
@RestController
@RequestMapping("/api/resenas")
@CrossOrigin(origins = "*") // Permite acceso desde el Frontend (React)
public class ResenaController {

    @Autowired
    private ResenaService resenaService;

    /**
     * 1. CREAR RESEÑA
     * Método: POST /api/resenas
     * Body: { "usuarioId": 5, "productoId": 10, "calificacion": 5, "comentario": "¡Buenardo!" }
     * Responsabilidad: Recibir datos crudos, validarlos y guardar la opinión.
     */
    @PostMapping
    public ResponseEntity<?> agregar(@RequestBody Map<String, Object> payload) {
        try {
            // EXTRACCIÓN Y CASTING SEGURO DE DATOS
            // El JSON llega como un Mapa genérico. Debemos extraer cada dato con cuidado.
            // ((Number)...).longValue() es la forma segura de leer IDs numéricos desde JSON.
            Long usuarioId = ((Number) payload.get("usuarioId")).longValue();
            Long productoId = ((Number) payload.get("productoId")).longValue();
            Integer calificacion = ((Number) payload.get("calificacion")).intValue();
            String comentario = (String) payload.get("comentario");

            // Llamamos al servicio para guardar la reseña en la base de datos
            Resena nuevaResena = resenaService.crearResena(usuarioId, productoId, calificacion, comentario);
            
            // Devolvemos 200 OK con el objeto creado para que React actualice la lista al instante
            return ResponseEntity.ok(nuevaResena);
        } catch (Exception e) {
            // Manejo de errores (ej: Usuario no existe, Producto no existe)
            return ResponseEntity.badRequest().body("Error al publicar reseña: " + e.getMessage());
        }
    }

    /**
     * 2. LISTAR RESEÑAS POR PRODUCTO
     * Método: GET /api/resenas/producto/{productoId}
     * Uso: Se llama automáticamente al entrar a la página 'ProductDetail' en el Frontend.
     * Devuelve todas las opiniones asociadas a un producto específico.
     */
    @GetMapping("/producto/{productoId}")
    public List<Resena> listarPorProducto(@PathVariable Long productoId) {
        return resenaService.obtenerResenasPorProducto(productoId);
    }
}