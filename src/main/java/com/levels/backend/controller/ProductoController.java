package com.levels.backend.controller;

import java.util.List;
import java.util.Map; // Nuevo import

import org.springframework.beans.factory.annotation.Autowired; // Para la búsqueda de categorías
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

@RestController
@RequestMapping("/api/productos")
@CrossOrigin(origins = "*")
public class ProductoController {

    @Autowired
    private ProductoService productoService;
    
    @Autowired
    private CategoriaRepository categoriaRepository; // 1. Inyectamos repositorio para buscar/crear categorías

    /**
     * Helper: Busca una categoría por nombre. Si no existe, la crea (para el Admin Panel).
     * @param nombreCategoria Nombre (Ej: "Consolas")
     * @return Objeto Categoria
     */
    private Categoria resolveCategoria(String nombreCategoria, String imagenUrl) {
        if (nombreCategoria == null || nombreCategoria.isEmpty()) {
            throw new RuntimeException("La categoría no puede estar vacía.");
        }
        
        // Intentamos buscar por nombre (la búsqueda no distingue mayúsculas/minúsculas)
        return categoriaRepository.findByNombre(nombreCategoria)
                .orElseGet(() -> {
                    // Si no existe, la creamos al instante (Permiso del Admin Panel)
                    Categoria nueva = new Categoria();
                    nueva.setNombre(nombreCategoria);
                    nueva.setImagenUrl(imagenUrl); // Usamos la URL del producto como default para la categoría
                    return categoriaRepository.save(nueva);
                });
    }

    // 1. GET: Listar todos o filtrar por nombre de categoría
    // Conecta con: Home.jsx y Categoria.jsx
    @GetMapping
    public List<Producto> listar(@RequestParam(required = false) String categoria) {
        // Llama al servicio, que ahora usa findByCategoria_Nombre
        return productoService.listarProductos(categoria); 
    }
    
    // 2. GET: Obtener producto por ID (para la página de detalle)
    @GetMapping("/{id}")
    public ResponseEntity<Producto> obtenerPorId(@PathVariable Long id) {
        // Conecta con: ProductDetail.jsx
        return productoService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 3. POST: Crear un nuevo producto (ADMIN)
    @PostMapping
    public ResponseEntity<?> crear(@RequestBody Map<String, Object> payload) {
        try {
            // Asumimos que la lógica de negocio debe verificar stock y precio en el servicio.
            // Aquí, mapeamos los datos de String a la entidad Producto:

            Producto producto = new Producto();
            
            // CRÍTICO: Resolvemos el String de Categoría al Objeto Categoria
            String nombreCategoria = (String) payload.get("categoria");
            String imagenUrl = (String) payload.get("imagenUrl");
            
            Categoria categoriaEntidad = resolveCategoria(nombreCategoria, imagenUrl);
            
            producto.setNombre((String) payload.get("nombre"));
            producto.setDescripcion((String) payload.get("descripcion"));
            producto.setPrecio(((Number) payload.get("precio")).doubleValue());
            producto.setStock(((Number) payload.get("stock")).intValue());
            producto.setImagenUrl(imagenUrl);
            producto.setVideoUrl((String) payload.get("videoUrl"));
            
            // Asignamos el objeto Categoria completo al Producto
            producto.setCategoria(categoriaEntidad);

            Producto nuevo = productoService.guardarProducto(producto);
            return ResponseEntity.ok(nuevo);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al crear producto: " + e.getMessage());
        }
    }

    // 4. PUT: Actualizar producto (ADMIN)
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @RequestBody Map<String, Object> payload) {
        try {
            // Repetimos el proceso para resolver la categoría antes de actualizar
            String nombreCategoria = (String) payload.get("categoria");
            String imagenUrl = (String) payload.get("imagenUrl");
            Categoria categoriaEntidad = resolveCategoria(nombreCategoria, imagenUrl);
            
            // Creamos un objeto Producto con los nuevos datos
            Producto datosNuevos = new Producto();
            datosNuevos.setNombre((String) payload.get("nombre"));
            datosNuevos.setDescripcion((String) payload.get("descripcion"));
            datosNuevos.setPrecio(((Number) payload.get("precio")).doubleValue());
            datosNuevos.setStock(((Number) payload.get("stock")).intValue());
            datosNuevos.setImagenUrl(imagenUrl);
            datosNuevos.setVideoUrl((String) payload.get("videoUrl"));
            datosNuevos.setCategoria(categoriaEntidad); // Adjuntamos la Entidad

            Producto actualizado = productoService.actualizarProducto(id, datosNuevos);
            return ResponseEntity.ok(actualizado);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // 5. DELETE: Borrar producto (ADMIN)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        try {
            if (productoService.eliminarProducto(id)) {
                return ResponseEntity.ok("Producto eliminado correctamente");
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
             return ResponseEntity.badRequest().body("Error al eliminar el producto (Posible FK): " + e.getMessage());
        }
    }
}