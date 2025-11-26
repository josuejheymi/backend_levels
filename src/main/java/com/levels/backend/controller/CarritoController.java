package com.levels.backend.controller;

import com.levels.backend.model.Carrito;
import com.levels.backend.service.CarritoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/carrito")
@CrossOrigin(origins = "*")
public class CarritoController {

    @Autowired
    private CarritoService carritoService;

    // POST: Agregar producto al carrito
    // Body: { "usuarioId": 1, "productoId": 1, "cantidad": 2 }
    @PostMapping("/agregar")
    public ResponseEntity<?> agregar(@RequestBody Map<String, Object> payload) {
        try {
            Long usuarioId = ((Number) payload.get("usuarioId")).longValue();
            Long productoId = ((Number) payload.get("productoId")).longValue();
            Integer cantidad = ((Number) payload.get("cantidad")).intValue();

            Carrito carrito = carritoService.agregarProducto(usuarioId, productoId, cantidad);
            return ResponseEntity.ok(carrito);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    // GET: Ver mi carrito
    @GetMapping("/{usuarioId}")
    public ResponseEntity<?> verCarrito(@PathVariable Long usuarioId) {
        Carrito carrito = carritoService.obtenerCarrito(usuarioId);
        if (carrito == null) {
            return ResponseEntity.ok("El carrito está vacío o no existe.");
        }
        return ResponseEntity.ok(carrito);
    }
}