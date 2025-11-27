package com.levels.backend.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.levels.backend.model.Carrito;
import com.levels.backend.model.DetalleCarrito;
import com.levels.backend.model.DetalleOrden;
import com.levels.backend.model.Orden;
import com.levels.backend.model.Producto;
import com.levels.backend.repository.CarritoRepository;
import com.levels.backend.repository.DetalleCarritoRepository;
import com.levels.backend.repository.OrdenRepository;
import com.levels.backend.repository.ProductoRepository;

import jakarta.transaction.Transactional;

@Service
public class OrdenService {

    @Autowired
    private OrdenRepository ordenRepository;
    @Autowired
    private CarritoRepository carritoRepository;
    @Autowired
    private DetalleCarritoRepository detalleCarritoRepository; // Para vaciar carrito
    @Autowired
    private ProductoRepository productoRepository; // Para descontar stock

    @Transactional // Importante: Todo o nada
    public Orden generarOrden(Long usuarioId) {
        // 1. Obtener el carrito del usuario
        Carrito carrito = carritoRepository.findByUsuarioId(usuarioId);
        if (carrito == null || carrito.getItems().isEmpty()) {
            throw new RuntimeException("El carrito está vacío.");
        }

        // 2. Crear la Orden (Cabecera)
        Orden nuevaOrden = new Orden();
        nuevaOrden.setUsuario(carrito.getUsuario());
        nuevaOrden.setTotal(carrito.getTotal()); // Ya viene con descuento Duoc aplicado
        nuevaOrden = ordenRepository.save(nuevaOrden);

        // 3. Procesar cada item del carrito
        for (DetalleCarrito itemCarrito : carrito.getItems()) {
            Producto producto = itemCarrito.getProducto();

            // A. VALIDAR STOCK DE NUEVO (Por si alguien compró justo antes)
            if (producto.getStock() < itemCarrito.getCantidad()) {
                throw new RuntimeException("Stock insuficiente para: " + producto.getNombre());
            }

            // B. RESTAR STOCK REAL
            producto.setStock(producto.getStock() - itemCarrito.getCantidad());
            productoRepository.save(producto);

            // C. CREAR DETALLE DE ORDEN (Historial)
            DetalleOrden detalleOrden = new DetalleOrden();
            detalleOrden.setOrden(nuevaOrden);
            detalleOrden.setProducto(producto);
            detalleOrden.setCantidad(itemCarrito.getCantidad());
            detalleOrden.setPrecioAlMomento(itemCarrito.getPrecioUnitario());
            
            // Lo agregamos a la lista de la orden (para que JPA lo guarde por cascada o manual)
            nuevaOrden.getDetalles().add(detalleOrden);
        }

        // 4. Vaciar el Carrito (Borrar los items temporales)
        detalleCarritoRepository.deleteAll(carrito.getItems());
        carrito.getItems().clear();
        carrito.setTotal(0.0);
        carritoRepository.save(carrito);

        return ordenRepository.save(nuevaOrden); // Devolvemos la orden confirmada
    }

    // Historial para el perfil
    public List<Orden> obtenerOrdenesUsuario(Long usuarioId) {
        return ordenRepository.findByUsuarioId(usuarioId);
    }
}
