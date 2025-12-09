package com.levels.backend.service;

import java.util.Date;
import java.util.List; // Import necesario para la fecha

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

/**
 * SERVICIO: GESTIÓN DE ÓRDENES DE COMPRA (Checkout)
 * ----------------------------------------------------
 * Contiene la lógica transaccional para convertir un Carrito (temporal)
 * en una Orden Histórica (permanente), asegurando la consistencia de stock.
 */
@Service
public class OrdenService {

    // Repositorios necesarios para las 4 entidades involucradas en el Checkout
    @Autowired private OrdenRepository ordenRepository;
    @Autowired private CarritoRepository carritoRepository;
    @Autowired private DetalleCarritoRepository detalleCarritoRepository; 
    @Autowired private ProductoRepository productoRepository; 

    /**
     * CHECKOUT: Genera una orden de compra completa.
     * ----------------------------------------------------
     * Es una operación de 5 pasos que toca 4 tablas.
     * @param usuarioId ID del cliente que compra.
     * @param direccion, region, comuna Datos de envío.
     */
    @Transactional // ¡CRÍTICO! Si la BD falla en el paso 3 o 4, todos los cambios se revierten (rollback).
    public Orden generarOrden(Long usuarioId, String direccion, String region, String comuna) {
        
        // 1. Obtener el carrito y validar datos iniciales
        Carrito carrito = carritoRepository.findByUsuarioId(usuarioId);
        
        if (carrito == null || carrito.getItems().isEmpty()) {
            throw new RuntimeException("No se puede procesar la compra: El carrito está vacío.");
        }

        // Validación de datos de envío
        if (direccion == null || direccion.trim().isEmpty()) throw new RuntimeException("La dirección es obligatoria.");
        if (region == null || region.trim().isEmpty()) throw new RuntimeException("La región es obligatoria.");
        if (comuna == null || comuna.trim().isEmpty()) throw new RuntimeException("La comuna es obligatoria.");

        // 2. Crear la Cabecera de la Orden (Snapshot de datos del usuario)
        Orden nuevaOrden = new Orden();
        nuevaOrden.setUsuario(carrito.getUsuario());
        nuevaOrden.setTotal(carrito.getTotal()); // Usamos el total calculado con descuentos
        nuevaOrden.setFechaCreacion(new Date()); 
        
        // Guardamos los datos de envío
        nuevaOrden.setDireccion(direccion);
        nuevaOrden.setRegion(region);
        nuevaOrden.setComuna(comuna);
        
        // Guardamos la orden inicial para generar el ID (necesario para las FK de los detalles)
        nuevaOrden = ordenRepository.save(nuevaOrden);

        // 3. Procesar cada producto del carrito (Bucle transaccional)
        for (DetalleCarrito itemCarrito : carrito.getItems()) {
            Producto producto = itemCarrito.getProducto();

            // A. REVALIDAR STOCK (Doble check antes de comprometer la venta)
            if (producto.getStock() < itemCarrito.getCantidad()) {
                // Si el stock fallara aquí, la excepción fuerza el ROLLBACK de toda la orden (paso 2 se borra)
                throw new RuntimeException("Stock insuficiente para: " + producto.getNombre());
            }

            // B. RESTAR STOCK REAL (Actualizamos el inventario)
            producto.setStock(producto.getStock() - itemCarrito.getCantidad());
            productoRepository.save(producto); // Persistimos el cambio de stock

            // C. CREAR DETALLE DE ORDEN (El SNAPSHOT)
            DetalleOrden detalleOrden = new DetalleOrden();
            detalleOrden.setOrden(nuevaOrden);
            detalleOrden.setProducto(producto);
            detalleOrden.setCantidad(itemCarrito.getCantidad());
            
            // Guardamos el precio del producto en ese momento (evita el error de precios futuros)
            // Usamos el precio final (después de descuentos, si el descuento es global)
            detalleOrden.setPrecioUnitario(producto.getPrecio()); 
            
            // Añadimos el detalle a la Orden (se guarda gracias al CascadeType.ALL en la entidad Orden)
            nuevaOrden.getDetalles().add(detalleOrden);
        }

        // 4. Limpieza del Carrito (Borramos los temporales)
        detalleCarritoRepository.deleteAll(carrito.getItems()); // Borra los items de la tabla
        
        // Reseteamos el objeto Carrito en memoria para que no tenga items ni total
        carrito.getItems().clear();
        carrito.setTotal(0.0);
        carritoRepository.save(carrito);

        // 5. Devolver la Orden final (se persiste automáticamente el detalle gracias al Cascade)
        return nuevaOrden;
    }

    // --- MÉTODOS DE LECTURA ---
    
    /**
     * Obtiene el historial de compras de un usuario específico.
     */
    public List<Orden> obtenerOrdenesUsuario(Long usuarioId) {
        return ordenRepository.findByUsuarioId(usuarioId);
    }

    /**
     * Ver todas las ventas (Admin)
     */
    public List<Orden> listarTodas() {
        return ordenRepository.findAll();
    }
    
    /**
     * Buscar una orden por ID
     */
    public Orden findById(Long id) {
        // Usamos orElse(null) para devolver null si no se encuentra la orden.
        return ordenRepository.findById(id).orElse(null);
    }
}