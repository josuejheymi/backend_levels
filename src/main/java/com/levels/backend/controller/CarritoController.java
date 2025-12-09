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

/**
 * CONTROLADOR: CARRITO DE COMPRAS
 * ----------------------------------------------------
 * Gestiona el "estado previo a la compra". Permite agregar items,
 * ver qué hay guardado, modificar cantidades y eliminar items.
 * * NOTA: Este controlador asume que el carrito es persistente en BD
 * (se guarda aunque cierres el navegador), a diferencia de un carrito
 * puramente local en el Frontend.
 */
@RestController
@RequestMapping("/api/carrito")
@CrossOrigin(origins = "*") // Permite peticiones desde React (localhost:3000)
public class CarritoController {

    // Inyectamos la lógica de negocio (El "Cocinero" del carrito)
    @Autowired
    private CarritoService carritoService;

    /**
     * 1. AGREGAR AL CARRITO
     * Método: POST /api/carrito/agregar
     * Body JSON: { "usuarioId": 1, "productoId": 5, "cantidad": 2 }
     * * * Concepto Clave: Uso de Map<String, Object>
     * En lugar de crear una clase Java (DTO) para recibir estos 3 datos, 
     * usamos un Map dinámico. Es rápido de programar, pero requiere 'castear' 
     * (convertir) los datos manualmente.
     */
    @PostMapping("/agregar")
    public ResponseEntity<?> agregar(@RequestBody Map<String, Object> payload) {
        try {
            // Conversión segura de tipos:
            // JSON envía números, Java los puede leer como Integer o Double.
            // Usamos (Number) para evitar errores de cast y luego convertimos a Long.
            Long usuarioId = ((Number) payload.get("usuarioId")).longValue();
            Long productoId = ((Number) payload.get("productoId")).longValue();
            Integer cantidad = ((Number) payload.get("cantidad")).intValue();

            // Llamamos al servicio para que haga la magia (buscar producto, sumar stock, guardar)
            Carrito carrito = carritoService.agregarProducto(usuarioId, productoId, cantidad);
            
            // Retornamos el carrito actualizado (Status 200 OK)
            return ResponseEntity.ok(carrito);
        } catch (Exception e) {
            // Si falta stock o el producto no existe, devolvemos error (Status 400)
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    /**
     * 2. VER MI CARRITO
     * Método: GET /api/carrito/{usuarioId}
     * Recupera el estado actual del carrito de un usuario específico.
     */
    @GetMapping("/{usuarioId}")
    public ResponseEntity<?> verCarrito(@PathVariable Long usuarioId) {
        Carrito carrito = carritoService.obtenerCarrito(usuarioId);
        
        if (carrito == null) {
            // Mensaje amigable si es la primera vez que entra
            return ResponseEntity.ok("El carrito está vacío o no existe.");
        }
        return ResponseEntity.ok(carrito);
    }

    /**
     * 3. ELIMINAR UN PRODUCTO ESPECÍFICO
     * Método: DELETE /api/carrito/{usuarioId}/producto/{productoId}
     * Borra una línea completa del carrito (ej: saca todos los Teclados).
     */
    @DeleteMapping("/{usuarioId}/producto/{productoId}")
    public ResponseEntity<?> eliminarDelCarrito(@PathVariable Long usuarioId, @PathVariable Long productoId) {
        try {
            Carrito carrito = carritoService.eliminarProducto(usuarioId, productoId);
            return ResponseEntity.ok(carrito);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    /**
     * 4. ACTUALIZAR CANTIDAD (Subir/Bajar)
     * Método: PUT /api/carrito/{usuarioId}/producto/{productoId}?cantidad=5
     * * * Concepto Clave: @RequestParam vs @PathVariable
     * - @PathVariable: Son parte de la ruta (usuario y producto son recursos).
     * - @RequestParam: Es un modificador (?cantidad=5).
     */
    @PutMapping("/{usuarioId}/producto/{productoId}")
    public ResponseEntity<?> actualizarCantidad(
            @PathVariable Long usuarioId, 
            @PathVariable Long productoId,
            @RequestParam Integer cantidad) { // Se lee del ?cantidad=X en la URL
        try {
            Carrito carrito = carritoService.actualizarCantidad(usuarioId, productoId, cantidad);
            return ResponseEntity.ok(carrito);
        } catch (Exception e) {
            // Devolvemos un JSON estructurado para que el Frontend pueda leerlo fácil
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage())); 
        }
    }
}