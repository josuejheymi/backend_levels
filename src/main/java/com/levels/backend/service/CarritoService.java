package com.levels.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.levels.backend.model.Carrito;
import com.levels.backend.model.DetalleCarrito;
import com.levels.backend.model.Producto;
import com.levels.backend.model.Usuario;
import com.levels.backend.repository.CarritoRepository;
import com.levels.backend.repository.DetalleCarritoRepository;
import com.levels.backend.repository.ProductoRepository;
import com.levels.backend.repository.UsuarioRepository; 

import jakarta.transaction.Transactional;

/**
 * SERVICIO: GESTIÓN DEL CARRITO
 * ----------------------------------------------------
 * Contiene toda la lógica para manipular el carrito de compras, incluyendo:
 * 1. Validación de stock antes de agregar.
 * 2. Creación/Actualización del carrito y sus detalles.
 * 3. Aplicación de reglas de negocio (Descuento DUOC).
 * 4. Eliminación robusta de ítems.
 */
@Service
public class CarritoService {

    // Inyección de dependencias de todos los repositorios necesarios
    @Autowired
    private CarritoRepository carritoRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private ProductoRepository productoRepository;
    @Autowired
    private DetalleCarritoRepository detalleRepository;

    /**
     * 1. AGREGAR PRODUCTO AL CARRITO
     * ----------------------------------------------------
     * Flujo de adición de un nuevo producto o suma de cantidad a uno existente.
     */
    public Carrito agregarProducto(Long usuarioId, Long productoId, Integer cantidad) {
        
        // 1. Carga de Entidades: Aseguramos que el Usuario y el Producto existan.
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        // 2. Validación Crítica de Stock: Regla de negocio fundamental.
        if (producto.getStock() < cantidad) {
            throw new RuntimeException("No hay suficiente stock. Disponible: " + producto.getStock());
        }

        // 3. Buscar o Crear Carrito: Cada usuario tiene un carrito único.
        Carrito carrito = carritoRepository.findByUsuarioId(usuarioId);
        if (carrito == null) {
            carrito = new Carrito();
            carrito.setUsuario(usuario);
            carrito = carritoRepository.save(carrito); // Persistimos la cabecera
        }

        // 4. Lógica de Ítems: Buscar si el producto ya estaba.
        DetalleCarrito detalleExistente = null;
        for (DetalleCarrito detalle : carrito.getItems()) {
            if (detalle.getProducto().getId().equals(productoId)) {
                detalleExistente = detalle;
                break;
            }
        }

        if (detalleExistente != null) {
            // Si existe, sumamos la cantidad y guardamos el detalle actualizado
            detalleExistente.setCantidad(detalleExistente.getCantidad() + cantidad);
            detalleRepository.save(detalleExistente);
        } else {
            // Si es nuevo, creamos una línea de detalle, asignamos el precio de lista (snapshot)
            DetalleCarrito nuevoDetalle = new DetalleCarrito();
            nuevoDetalle.setCarrito(carrito);
            nuevoDetalle.setProducto(producto);
            nuevoDetalle.setCantidad(cantidad);
            nuevoDetalle.setPrecioUnitario(producto.getPrecio());
            
            carrito.getItems().add(nuevoDetalle);
            detalleRepository.save(nuevoDetalle); 
        }

        // 5. Calcular Totales (Incluye Gamificación)
        calcularTotalCarrito(carrito);

        return carritoRepository.save(carrito);
    }

    /**
     * 2. OBTENER CARRITO
     * @return El carrito del usuario o null.
     */
    public Carrito obtenerCarrito(Long usuarioId) {
        return carritoRepository.findByUsuarioId(usuarioId);
    }
    
    // --- LÓGICA PRIVADA ---

    /**
     * Método privado para recalcular el precio total aplicando reglas de negocio.
     */
    private void calcularTotalCarrito(Carrito carrito) {
        double suma = 0;
        for (DetalleCarrito item : carrito.getItems()) {
            suma += item.getPrecioUnitario() * item.getCantidad();
        }

        // REGLA DE NEGOCIO: Descuento DUOC (Gamificación/Beneficio)
        if (carrito.getUsuario().isEsEstudianteDuoc()) {
            suma = suma * 0.80; // Descuenta el 20%
        }

        carrito.setTotal(suma);
    }

    /**
     * 3. ELIMINAR UN PRODUCTO ESPECÍFICO DEL CARRITO
     * ----------------------------------------------------
     * * @Transactional: Asegura que el borrado masivo en el Repositorio se ejecute.
     */
    @Transactional 
    public Carrito eliminarProducto(Long usuarioId, Long productoId) {
        Carrito carrito = carritoRepository.findByUsuarioId(usuarioId);
        if (carrito == null) throw new RuntimeException("Carrito no encontrado");

        // Borrado directo en BD (más robusto que solo usar la lista en Java)
        detalleRepository.deleteByCarritoIdAndProductoId(carrito.getId(), productoId);
        
        // Sincronizamos la lista en memoria para evitar errores de cálculo
        carrito.getItems().removeIf(item -> item.getProducto().getId().equals(productoId));
        
        calcularTotalCarrito(carrito);
        
        return carritoRepository.save(carrito);
    }

    /**
     * 4. ACTUALIZAR CANTIDAD (+ o -)
     */
    public Carrito actualizarCantidad(Long usuarioId, Long productoId, Integer nuevaCantidad) {
        Carrito carrito = carritoRepository.findByUsuarioId(usuarioId);
        if (carrito == null) throw new RuntimeException("Carrito no encontrado");

        DetalleCarrito detalle = carrito.getItems().stream()
                .filter(item -> item.getProducto().getId().equals(productoId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Producto no encontrado en el carrito"));

        // Lógica de Eliminación/Stock
        if (nuevaCantidad <= 0) {
            return eliminarProducto(usuarioId, productoId); // Delega la eliminación
        }
        
        if (nuevaCantidad > detalle.getProducto().getStock()) {
            throw new RuntimeException("Stock insuficiente. Máximo disponible: " + detalle.getProducto().getStock());
        }

        // Actualizamos la cantidad, recalculamos y guardamos
        detalle.setCantidad(nuevaCantidad);
        
        calcularTotalCarrito(carrito);
        return carritoRepository.save(carrito);
    }
}