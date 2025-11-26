package com.levels.backend.controller;

import com.levels.backend.model.Producto;
import com.levels.backend.service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
@CrossOrigin(origins = "*")
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    // GET: Listar todos o filtrar por categoría
    // Ejemplo: GET /api/productos?categoria=Consolas
    @GetMapping
    public List<Producto> listar(@RequestParam(required = false) String categoria) {
        return productoService.listarProductos(categoria);
    }

    // POST: Crear un nuevo producto (Simulando Admin)
    @PostMapping
    public ResponseEntity<?> crear(@RequestBody Producto producto) {
        try {
            Producto nuevo = productoService.guardarProducto(producto);
            return ResponseEntity.ok(nuevo);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al crear producto: " + e.getMessage());
        }
    }

    // DELETE: Borrar producto por ID
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        if (productoService.eliminarProducto(id)) {
            return ResponseEntity.ok("Producto eliminado correctamente");
        }
        return ResponseEntity.notFound().build();
    }
}