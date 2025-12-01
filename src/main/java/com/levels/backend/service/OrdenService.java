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
    private DetalleCarritoRepository detalleCarritoRepository; // Para vaciar el carrito

    @Autowired
    private ProductoRepository productoRepository; // Para descontar stock

    /**
     * Genera una orden de compra a partir del carrito actual del usuario.
     * Realiza validación de stock, descuento de inventario y limpieza del carrito.
     */
    @Transactional // Importante: Si algo falla (ej: sin stock), se deshacen todos los cambios
    public Orden generarOrden(Long usuarioId, String direccionEnvio) {
        
        // 1. Obtener el carrito del usuario
        Carrito carrito = carritoRepository.findByUsuarioId(usuarioId);
        
        // Validación: El carrito debe existir y tener productos
        if (carrito == null || carrito.getItems().isEmpty()) {
            throw new RuntimeException("No se puede procesar la compra: El carrito está vacío.");
        }

        // Validación: La dirección es obligatoria
        if (direccionEnvio == null || direccionEnvio.trim().isEmpty()) {
            throw new RuntimeException("La dirección de envío es obligatoria.");
        }

        // 2. Crear la Cabecera de la Orden
        Orden nuevaOrden = new Orden();
        nuevaOrden.setUsuario(carrito.getUsuario());
        nuevaOrden.setTotal(carrito.getTotal()); // El total ya viene con el descuento aplicado desde el carrito
        nuevaOrden.setDireccionEnvio(direccionEnvio); // Guardamos la dirección recibida
        
        // Guardamos la orden inicial para tener un ID
        nuevaOrden = ordenRepository.save(nuevaOrden);

        // 3. Procesar cada producto del carrito
        for (DetalleCarrito itemCarrito : carrito.getItems()) {
            Producto producto = itemCarrito.getProducto();

            // A. RE-VALIDAR STOCK (Crucial en sistemas reales)
            if (producto.getStock() < itemCarrito.getCantidad()) {
                throw new RuntimeException("Stock insuficiente para el producto: " + producto.getNombre());
            }

            // B. RESTAR STOCK REAL EN LA BASE DE DATOS
            producto.setStock(producto.getStock() - itemCarrito.getCantidad());
            productoRepository.save(producto);

            // C. CREAR DETALLE DE ORDEN (Snapshot de la venta)
            DetalleOrden detalleOrden = new DetalleOrden();
            detalleOrden.setOrden(nuevaOrden);
            detalleOrden.setProducto(producto);
            detalleOrden.setCantidad(itemCarrito.getCantidad());
            detalleOrden.setPrecioAlMomento(itemCarrito.getPrecioUnitario()); // Guardamos el precio histórico
            
            // Agregamos el detalle a la lista de la orden
            nuevaOrden.getDetalles().add(detalleOrden);
        }

        // 4. Vaciar el Carrito (Limpieza)
        // Borramos los items de la tabla detalle_carrito
        detalleCarritoRepository.deleteAll(carrito.getItems());
        
        // Reseteamos el objeto carrito en memoria y BD
        carrito.getItems().clear();
        carrito.setTotal(0.0);
        carritoRepository.save(carrito);

        // 5. Guardar y devolver la Orden completa con sus detalles
        return ordenRepository.save(nuevaOrden);
    }

    /**
     * Obtiene el historial de compras de un usuario específico via ID.
     */
    public List<Orden> obtenerOrdenesUsuario(Long usuarioId) {
        return ordenRepository.findByUsuarioId(usuarioId);
    }

    /**
     * (Opcional) Para el Panel de Admin: Ver todas las ventas
     */
    public List<Orden> listarTodas() {
        return ordenRepository.findAll();
    }
}