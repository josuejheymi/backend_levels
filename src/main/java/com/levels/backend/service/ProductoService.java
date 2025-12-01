package com.levels.backend.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.levels.backend.model.Producto; // Importante para la atomicidad
import com.levels.backend.repository.DetalleCarritoRepository;
import com.levels.backend.repository.ProductoRepository;
import com.levels.backend.repository.ResenaRepository;

import jakarta.transaction.Transactional;

@Service
public class ProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private DetalleCarritoRepository detalleRepository; // Nuevo: Para limpiar carritos

    @Autowired
    private ResenaRepository resenaRepository; // Nuevo: Para limpiar reseñas

    // 1. Obtener productos (con o sin filtro de categoría)
    public List<Producto> listarProductos(String categoria) {
        if (categoria != null && !categoria.isEmpty()) {
            return productoRepository.findByCategoria(categoria);
        }
        return productoRepository.findAll();
    }

    // 2. Obtener un producto por ID
    public Optional<Producto> obtenerPorId(Long id) {
        return productoRepository.findById(id);
    }

    // 3. Guardar/Crear producto
    public Producto guardarProducto(Producto producto) {
        if (producto.getPrecio() < 0) {
            throw new RuntimeException("El precio no puede ser negativo");
        }
        return productoRepository.save(producto);
    }

    // 4. Eliminar producto (MODIFICADO: Limpieza en Cascada Manual)
    @Transactional // <--- Esto asegura que todas las operaciones se hagan juntas o ninguna
    public boolean eliminarProducto(Long id) {
        if (productoRepository.existsById(id)) {
            
            // PASO A: Borrar este producto de todos los carritos activos
            detalleRepository.deleteByProductoId(id);
            
            // PASO B: Borrar todas las reseñas asociadas a este producto
            resenaRepository.deleteByProductoId(id);

            // PASO C: Ahora sí, borrar el producto principal (ya no tiene ataduras)
            productoRepository.deleteById(id);
            
            return true;
        }
        return false;
    }
    // 5. Actualizar producto existente
    public Producto actualizarProducto(Long id, Producto nuevosDatos) {
        return productoRepository.findById(id).map(prod -> {
            prod.setNombre(nuevosDatos.getNombre());
            prod.setDescripcion(nuevosDatos.getDescripcion());
            prod.setPrecio(nuevosDatos.getPrecio());
            prod.setStock(nuevosDatos.getStock()); // <--- AQUÍ ACTUALIZAS EL STOCK
            prod.setCategoria(nuevosDatos.getCategoria());
            prod.setImagenUrl(nuevosDatos.getImagenUrl());
            return productoRepository.save(prod);
        }).orElseThrow(() -> new RuntimeException("Producto no encontrado"));
    }
}