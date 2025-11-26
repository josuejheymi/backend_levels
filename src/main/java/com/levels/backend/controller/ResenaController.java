package com.levels.backend.controller;

import com.levels.backend.model.Resena;
import com.levels.backend.service.ResenaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/resenas")
@CrossOrigin(origins = "*")
public class ResenaController {

    @Autowired
    private ResenaService resenaService;

    // POST: Crear reseña
    // Body: { "usuarioId": 1, "productoId": 1, "calificacion": 5, "comentario": "Excelente!" }
    @PostMapping
    public ResponseEntity<?> agregar(@RequestBody Map<String, Object> payload) {
        try {
            Long usuarioId = ((Number) payload.get("usuarioId")).longValue();
            Long productoId = ((Number) payload.get("productoId")).longValue();
            Integer calificacion = ((Number) payload.get("calificacion")).intValue();
            String comentario = (String) payload.get("comentario");

            Resena nuevaResena = resenaService.crearResena(usuarioId, productoId, calificacion, comentario);
            return ResponseEntity.ok(nuevaResena);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    // GET: Ver reseñas de un producto
    // URL: /api/resenas/producto/1
    @GetMapping("/producto/{productoId}")
    public List<Resena> listarPorProducto(@PathVariable Long productoId) {
        return resenaService.obtenerResenasPorProducto(productoId);
    }
}