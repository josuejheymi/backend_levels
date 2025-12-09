package com.levels.backend.controller;

import java.util.List;
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

import com.levels.backend.model.Categoria;
import com.levels.backend.model.Producto;
import com.levels.backend.repository.CategoriaRepository;
import com.levels.backend.service.ProductoService;

/**
 * CONTROLADOR: PRODUCTOS (Inventario)
 * ----------------------------------------------------
 * Gestiona el catálogo de productos. Permite listar, filtrar por categoría,
 * ver detalles y (para administradores) crear, editar y eliminar items.
 */
@RestController
@RequestMapping("/api/productos")
@CrossOrigin(origins = "*") // Habilita acceso desde React (localhost:3000)
public class ProductoController {

    @Autowired
    private ProductoService productoService;
    
    // Inyección del Repositorio de Categorías para resolver relaciones
    @Autowired
    private CategoriaRepository categoriaRepository;

    /**
     * HELPER PRIVADO: Resolver Categoría
     * ----------------------------------------------------
     * Transforma un String (ej: "Teclados") en una Entidad (Categoria con ID=5).
     * Si la categoría no existe en la base de datos, la crea automáticamente.
     * Esto simplifica enormemente la gestión desde el Frontend.
     */
    private Categoria resolveCategoria(String nombreCategoria, String imagenUrl) {
        if (nombreCategoria == null || nombreCategoria.isEmpty()) {
            throw new RuntimeException("La categoría no puede estar vacía.");
        }
        
        // Buscamos si ya existe (para no duplicar "Teclados" y "teclados")
        return categoriaRepository.findByNombre(nombreCategoria)
                .orElseGet(() -> {
                    // Si no existe, la creamos al vuelo.
                    Categoria nueva = new Categoria();
                    nueva.setNombre(nombreCategoria);
                    nueva.setImagenUrl(imagenUrl); // Usamos la imagen del producto como portada por defecto
                    return categoriaRepository.save(nueva);
                });
    }

    /**
     * 1. LISTAR PRODUCTOS (Catálogo)
     * Método: GET /api/productos?categoria=Teclados
     * Si viene el parámetro 'categoria', filtra. Si no, devuelve todos.
     */
    @GetMapping
    public List<Producto> listar(@RequestParam(required = false) String categoria) {
        return productoService.listarProductos(categoria); 
    }
    
    /**
     * 2. DETALLE DE PRODUCTO
     * Método: GET /api/productos/{id}
     * Devuelve toda la info de un producto para la página ProductDetail.js
     */
    @GetMapping("/{id}")
    public ResponseEntity<Producto> obtenerPorId(@PathVariable Long id) {
        return productoService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 3. CREAR PRODUCTO (Solo Admin)
     * Método: POST /api/productos
     * Body: Map genérico (JSON)
     * Recibe datos planos desde React y construye el objeto Producto con sus relaciones.
     */
    @PostMapping
    public ResponseEntity<?> crear(@RequestBody Map<String, Object> payload) {
        try {
            Producto producto = new Producto();
            
            // A. Extraemos datos del JSON
            String nombreCategoria = (String) payload.get("categoria");
            String imagenUrl = (String) payload.get("imagenUrl");
            
            // B. Resolvemos la relación con Categoría
            Categoria categoriaEntidad = resolveCategoria(nombreCategoria, imagenUrl);
            
            // C. Llenamos el objeto Producto (Casting seguro de números)
            producto.setNombre((String) payload.get("nombre"));
            producto.setDescripcion((String) payload.get("descripcion"));
            producto.setPrecio(((Number) payload.get("precio")).doubleValue()); // Soporta int y double
            producto.setStock(((Number) payload.get("stock")).intValue());
            producto.setImagenUrl(imagenUrl);
            producto.setVideoUrl((String) payload.get("videoUrl"));
            
            // D. Asignamos la relación y guardamos
            producto.setCategoria(categoriaEntidad);

            Producto nuevo = productoService.guardarProducto(producto);
            return ResponseEntity.ok(nuevo);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al crear producto: " + e.getMessage());
        }
    }

    /**
     * 4. ACTUALIZAR PRODUCTO (Solo Admin)
     * Método: PUT /api/productos/{id}
     * Sobrescribe los datos de un producto existente.
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @RequestBody Map<String, Object> payload) {
        try {
            // Repetimos la lógica de resolución de categoría (podría haber cambiado)
            String nombreCategoria = (String) payload.get("categoria");
            String imagenUrl = (String) payload.get("imagenUrl");
            Categoria categoriaEntidad = resolveCategoria(nombreCategoria, imagenUrl);
            
            Producto datosNuevos = new Producto();
            datosNuevos.setNombre((String) payload.get("nombre"));
            datosNuevos.setDescripcion((String) payload.get("descripcion"));
            datosNuevos.setPrecio(((Number) payload.get("precio")).doubleValue());
            datosNuevos.setStock(((Number) payload.get("stock")).intValue());
            datosNuevos.setImagenUrl(imagenUrl);
            datosNuevos.setVideoUrl((String) payload.get("videoUrl"));
            datosNuevos.setCategoria(categoriaEntidad);

            Producto actualizado = productoService.actualizarProducto(id, datosNuevos);
            return ResponseEntity.ok(actualizado);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 5. ELIMINAR PRODUCTO (Solo Admin)
     * Método: DELETE /api/productos/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        try {
            if (productoService.eliminarProducto(id)) {
                return ResponseEntity.ok("Producto eliminado correctamente");
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            // Error común: Intentar borrar un producto que ya fue comprado (está en una Orden).
            return ResponseEntity.badRequest().body("No se puede eliminar: El producto está en historiales de compra.");
        }
    }
}