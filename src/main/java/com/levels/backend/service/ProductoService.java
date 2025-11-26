package com.levels.backend.service;

import com.levels.backend.model.Producto;
import com.levels.backend.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    // 1. Obtener productos (con o sin filtro de categoría)
    public List<Producto> listarProductos(String categoria) {
        if (categoria != null && !categoria.isEmpty()) {
            return productoRepository.findByCategoria(categoria);
        }
        return productoRepository.findAll();
    }

    // 2. Obtener un producto por ID (útil para ver detalles)
    public Optional<Producto> obtenerPorId(Long id) {
        return productoRepository.findById(id);
    }

    // 3. Guardar/Crear producto (Para el Admin)
    public Producto guardarProducto(Producto producto) {
        // Aquí podrías validar que el precio no sea negativo, etc.
        if (producto.getPrecio() < 0) {
            throw new RuntimeException("El precio no puede ser negativo");
        }
        return productoRepository.save(producto);
    }

    // 4. Eliminar producto
    public boolean eliminarProducto(Long id) {
        if (productoRepository.existsById(id)) {
            productoRepository.deleteById(id);
            return true;
        }
        return false;
    }
}