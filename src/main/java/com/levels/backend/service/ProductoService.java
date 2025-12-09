package com.levels.backend.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.levels.backend.model.Producto;
import com.levels.backend.repository.CategoriaRepository; // Importamos todos los repositorios
import com.levels.backend.repository.DetalleCarritoRepository;
import com.levels.backend.repository.ProductoRepository;
import com.levels.backend.repository.ResenaRepository;

import jakarta.transaction.Transactional;

/**
 * SERVICIO: PRODUCTOS (Gestión de Inventario y Catálogo)
 * ----------------------------------------------------
 * Contiene la lógica para listar, crear, y, críticamente, eliminar productos
 * de forma segura, respetando todas las llaves foráneas.
 */
@Service
public class ProductoService {

    @Autowired private ProductoRepository productoRepository;
    @Autowired private DetalleCarritoRepository detalleRepository; // Para limpiar carritos
    @Autowired private ResenaRepository resenaRepository;         // Para limpiar reseñas
    @Autowired private CategoriaRepository categoriaRepository;    // Para buscar categorías (aunque lo haga el Controller, aquí está disponible)

    /**
     * 1. OBTENER PRODUCTOS (con o sin filtro)
     * @param nombreCategoria Nombre de la categoría a filtrar (opcional).
     * @return Lista de productos.
     */
    public List<Producto> listarProductos(String nombreCategoria) { 
        if (nombreCategoria != null && !nombreCategoria.isEmpty()) {
            // Filtrado: Busca por el nombre dentro de la entidad Categoria
            return productoRepository.findByCategoria_Nombre(nombreCategoria); 
        }
        // Sin filtro, devuelve todo
        return productoRepository.findAll();
    }

    /**
     * 2. OBTENER UN PRODUCTO POR ID
     * Uso: Para la página de detalle (ProductDetail.jsx).
     */
    public Optional<Producto> obtenerPorId(Long id) {
        return productoRepository.findById(id);
    }

    /**
     * 3. GUARDAR/CREAR PRODUCTO
     * @param producto Producto con la Categoria ya resuelta.
     */
    public Producto guardarProducto(Producto producto) {
        // LÓGICA DE NEGOCIO: Evitar datos inconsistentes
        if (producto.getPrecio() < 0) {
            throw new RuntimeException("El precio no puede ser negativo");
        }
        return productoRepository.save(producto);
    }

    /**
     * 4. ELIMINAR PRODUCTO (Limpieza en Cascada Manual)
     * ----------------------------------------------------
     * Es una operación @Transactional para garantizar que si la BD falla en
     * el paso C, los pasos A y B se reviertan.
     */
    @Transactional 
    public boolean eliminarProducto(Long id) {
        if (productoRepository.existsById(id)) {
            
            // PASO A: Limpieza del Carrito (Si está en el carrito, no puede eliminarse).
            // Usamos una consulta DELETE masiva que elimina todas las referencias al producto.
            detalleRepository.deleteByProductoId(id);
            
            // PASO B: Limpieza de Reseñas (Si tiene reviews, no puede eliminarse)
            // Usamos una consulta DELETE masiva que elimina todas las reseñas asociadas.
            resenaRepository.deleteByProductoId(id);

            // PASO C: Eliminar el Producto principal
            // Esto solo es seguro si los pasos A y B eliminaron todas las FK que apuntaban a él.
            productoRepository.deleteById(id);
            
            return true;
        }
        return false;
    }
    
    /**
     * 5. ACTUALIZAR PRODUCTO EXISTENTE (Recomendado para el Admin Panel)
     * Patrón: Busca el original -> Aplica cambios -> Guarda la versión modificada.
     */
    public Producto actualizarProducto(Long id, Producto nuevosDatos) {
        // Busca el producto original. Si no existe, lanza excepción.
        return productoRepository.findById(id).map(prod -> {
            
            // Mapeo defensivo: Solo actualizamos los campos que el Frontend puede enviar
            prod.setNombre(nuevosDatos.getNombre());
            prod.setDescripcion(nuevosDatos.getDescripcion());
            prod.setPrecio(nuevosDatos.getPrecio());
            prod.setStock(nuevosDatos.getStock()); 
            
            // Las relaciones se actualizan si vienen con el objeto adjunto
            prod.setCategoria(nuevosDatos.getCategoria()); 
            prod.setImagenUrl(nuevosDatos.getImagenUrl());
            prod.setVideoUrl(nuevosDatos.getVideoUrl());

            return productoRepository.save(prod); // Guarda la entidad actualizada
        }).orElseThrow(() -> new RuntimeException("Producto no encontrado"));
    }
}