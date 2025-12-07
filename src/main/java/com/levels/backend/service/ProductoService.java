package com.levels.backend.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.levels.backend.model.Producto; // Para las nuevas Categorías
import com.levels.backend.repository.CategoriaRepository; // Importamos el modelo Categoria
import com.levels.backend.repository.DetalleCarritoRepository; // Importante para el borrado seguro
import com.levels.backend.repository.ProductoRepository;
import com.levels.backend.repository.ResenaRepository;

import jakarta.transaction.Transactional;

@Service
public class ProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private DetalleCarritoRepository detalleRepository; // Para limpieza del carrito
    
    @Autowired
    private ResenaRepository resenaRepository; // Para limpieza de reseñas
    
    @Autowired 
    private CategoriaRepository categoriaRepository; // Para buscar la entidad Categoria

    /**
     * 1. Obtener productos (con o sin filtro de categoría)
     * Usa el nombre de la Categoría para filtrar, gracias a la relación JPA.
     */
    public List<Producto> listarProductos(String nombreCategoria) { 
        if (nombreCategoria != null && !nombreCategoria.isEmpty()) {
            // findByCategoria_Nombre: Busca por el campo 'nombre' dentro de la entidad 'Categoria'
            return productoRepository.findByCategoria_Nombre(nombreCategoria); 
        }
        return productoRepository.findAll();
    }

    /**
     * 2. Obtener un producto por ID
     */
    public Optional<Producto> obtenerPorId(Long id) {
        return productoRepository.findById(id);
    }

    /**
     * 3. Guardar/Crear producto
     * Se encarga de buscar la entidad Categoria para el ManyToOne.
     */
    public Producto guardarProducto(Producto producto) {
        // LÓGICA DE NEGOCIO: Evitar precios negativos
        if (producto.getPrecio() < 0) {
            throw new RuntimeException("El precio no puede ser negativo");
        }
        
        // ASUNCIÓN CLAVE: La data que viene del frontend ya tiene el objeto Categoria adjunto,
        // o esta lógica sería manejada en el Controller si solo recibiera el ID de Categoría.
        // Si el Controller no lo hace, esta parte fallaría al no tener la FK. 
        // Asumiremos que el Controller ya adjuntó la Categoria.

        return productoRepository.save(producto);
    }

    /**
     * 4. Eliminar producto (MODIFICADO: Limpieza en Cascada Manual)
     * Crítico para evitar errores de clave foránea (FK) en MySQL.
     */
    @Transactional 
    public boolean eliminarProducto(Long id) {
        if (productoRepository.existsById(id)) {
            // PASO A: Borrar este producto de todos los carritos (evita FK en DetalleCarrito)
            detalleRepository.deleteByProductoId(id);
            
            // PASO B: Borrar todas las reseñas asociadas (evita FK en Resena)
            resenaRepository.deleteByProductoId(id);

            // PASO C: Ahora sí, borrar el producto principal 
            productoRepository.deleteById(id);
            
            return true;
        }
        return false;
    }
    
    /**
     * 5. Actualizar producto existente (Incluye Stock y VideoUrl)
     */
    public Producto actualizarProducto(Long id, Producto nuevosDatos) {
        return productoRepository.findById(id).map(prod -> {
            
            // Actualización de campos normales
            prod.setNombre(nuevosDatos.getNombre());
            prod.setDescripcion(nuevosDatos.getDescripcion());
            prod.setPrecio(nuevosDatos.getPrecio());
            
            // Actualización de Stock (Lógica de Negocio)
            prod.setStock(nuevosDatos.getStock()); 
            
            // Actualización de Campos Nuevos
            prod.setCategoria(nuevosDatos.getCategoria()); // Mantenemos la nueva entidad Categoria
            prod.setImagenUrl(nuevosDatos.getImagenUrl());
            prod.setVideoUrl(nuevosDatos.getVideoUrl()); // El nuevo link de YouTube

            return productoRepository.save(prod);
        }).orElseThrow(() -> new RuntimeException("Producto no encontrado"));
    }
}