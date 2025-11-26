package com.levels.backend.service;

import com.levels.backend.model.*;
import com.levels.backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

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
}