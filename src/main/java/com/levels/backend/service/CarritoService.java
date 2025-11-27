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

@Service
public class CarritoService {

    @Autowired
    private CarritoRepository carritoRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private ProductoRepository productoRepository;
    @Autowired
    private DetalleCarritoRepository detalleRepository;

    public Carrito agregarProducto(Long usuarioId, Long productoId, Integer cantidad) {
        // 1. Buscar Usuario y Producto
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        // 2. Validar Stock
        if (producto.getStock() < cantidad) {
            throw new RuntimeException("No hay suficiente stock. Disponible: " + producto.getStock());
        }

        // 3. Buscar o Crear Carrito para este usuario
        Carrito carrito = carritoRepository.findByUsuarioId(usuarioId);
        if (carrito == null) {
            carrito = new Carrito();
            carrito.setUsuario(usuario);
            carrito = carritoRepository.save(carrito);
        }

        // 4. Verificar si el producto ya estaba en el carrito
        // (Si está, sumamos cantidad. Si no, creamos línea nueva)
        DetalleCarrito detalleExistente = null;
        for (DetalleCarrito detalle : carrito.getItems()) {
            if (detalle.getProducto().getId().equals(productoId)) {
                detalleExistente = detalle;
                break;
            }
        }

        if (detalleExistente != null) {
            detalleExistente.setCantidad(detalleExistente.getCantidad() + cantidad);
            detalleRepository.save(detalleExistente);
        } else {
            DetalleCarrito nuevoDetalle = new DetalleCarrito();
            nuevoDetalle.setCarrito(carrito);
            nuevoDetalle.setProducto(producto);
            nuevoDetalle.setCantidad(cantidad);
            nuevoDetalle.setPrecioUnitario(producto.getPrecio());
            carrito.getItems().add(nuevoDetalle);
            // Guardamos el detalle primero para asegurar ID
            detalleRepository.save(nuevoDetalle); 
        }

        // 5. Calcular Totales y Descuento DUOC
        calcularTotalCarrito(carrito);

        return carritoRepository.save(carrito);
    }

    public Carrito obtenerCarrito(Long usuarioId) {
        return carritoRepository.findByUsuarioId(usuarioId);
    }
    
    // Método privado para recalcular precios
    private void calcularTotalCarrito(Carrito carrito) {
        double suma = 0;
        for (DetalleCarrito item : carrito.getItems()) {
            suma += item.getPrecioUnitario() * item.getCantidad();
        }

        // APLICAR DESCUENTO DUOC
        if (carrito.getUsuario().isEsEstudianteDuoc()) {
            // Descuenta el 20%
            suma = suma * 0.80; 
        }

        carrito.setTotal(suma);
    }
    // 1. ELIMINAR UN PRODUCTO DEL CARRITO (VERSIÓN ROBUSTA)
    @Transactional // Importante: Asegura que el borrado se ejecute realmente
    public Carrito eliminarProducto(Long usuarioId, Long productoId) {
        Carrito carrito = carritoRepository.findByUsuarioId(usuarioId);
        if (carrito == null) throw new RuntimeException("Carrito no encontrado");

        // BORRADO DIRECTO EN BASE DE DATOS
        // Esto elimina el producto (y sus duplicados si los hubiera por error)
        detalleRepository.deleteByCarritoIdAndProductoId(carrito.getId(), productoId);
        
        // Limpiamos la lista en memoria para recalcular el total correctamente
        carrito.getItems().removeIf(item -> item.getProducto().getId().equals(productoId));
        
        // Recalculamos el total con lo que queda
        calcularTotalCarrito(carrito);
        
        return carritoRepository.save(carrito);
    }

    // 2. ACTUALIZAR CANTIDAD (+ o -)
    public Carrito actualizarCantidad(Long usuarioId, Long productoId, Integer nuevaCantidad) {
        Carrito carrito = carritoRepository.findByUsuarioId(usuarioId);
        if (carrito == null) throw new RuntimeException("Carrito no encontrado");

        // Buscamos el item específico
        DetalleCarrito detalle = carrito.getItems().stream()
                .filter(item -> item.getProducto().getId().equals(productoId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Producto no encontrado en el carrito"));

        // Validaciones
        if (nuevaCantidad <= 0) {
            // Si baja a 0, lo eliminamos
            return eliminarProducto(usuarioId, productoId);
        }
        
        if (nuevaCantidad > detalle.getProducto().getStock()) {
            throw new RuntimeException("Stock insuficiente. Máximo disponible: " + detalle.getProducto().getStock());
        }

        // Actualizamos
        detalle.setCantidad(nuevaCantidad);
        
        // Recalculamos y guardamos
        calcularTotalCarrito(carrito);
        return carritoRepository.save(carrito);
    }
}