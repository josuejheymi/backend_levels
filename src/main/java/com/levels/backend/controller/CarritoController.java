package com.levels.backend.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.levels.backend.model.Carrito;
import com.levels.backend.service.CarritoService;

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
    // DELETE: Eliminar producto
    // URL: /api/carrito/{usuarioId}/producto/{productoId}
    @DeleteMapping("/{usuarioId}/producto/{productoId}")
    public ResponseEntity<?> eliminarDelCarrito(@PathVariable Long usuarioId, @PathVariable Long productoId) {
        try {
            Carrito carrito = carritoService.eliminarProducto(usuarioId, productoId);
            return ResponseEntity.ok(carrito);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    // PUT: Actualizar cantidad
    // URL: /api/carrito/{usuarioId}/producto/{productoId}?cantidad=5
    @PutMapping("/{usuarioId}/producto/{productoId}")
    public ResponseEntity<?> actualizarCantidad(
            @PathVariable Long usuarioId, 
            @PathVariable Long productoId,
            @RequestParam Integer cantidad) {
        try {
            Carrito carrito = carritoService.actualizarCantidad(usuarioId, productoId, cantidad);
            return ResponseEntity.ok(carrito);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage())); // Enviamos JSON de error
        }
    }
}