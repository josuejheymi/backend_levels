package com.levels.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * ENTIDAD: DETALLE DE ORDEN (Línea de Factura)
 * ----------------------------------------------------
 * Representa un ítem específico dentro de una compra finalizada.
 * Ej: "2 unidades del Mouse Logitech a $50.000 c/u".
 * * A diferencia del 'DetalleCarrito' (que es temporal), esta entidad
 * es HISTÓRICA e INMUTABLE. No debe cambiar una vez creada.
 */
@Entity
@Table(name = "detalles_orden")
public class DetalleOrden {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * RELACIÓN CON LA ORDEN (Padre)
     * Muchos detalles pertenecen a una sola orden de compra.
     * * @JsonIgnore: Vital. Cuando consultamos una Orden, esta trae sus detalles.
     * Si el detalle intentara traer de vuelta a la Orden, entraríamos en un
     * bucle infinito (StackOverflowError) al generar el JSON.
     */
    @ManyToOne
    @JoinColumn(name = "orden_id")
    @JsonIgnore 
    private Orden orden;

    /**
     * RELACIÓN CON EL PRODUCTO
     * Referencia al artículo vendido.
     * Nota: Aquí no usamos Cascade ni OrphanRemoval, porque si borramos
     * un historial de compra, ¡NO queremos borrar el producto del catálogo!
     */
    @ManyToOne
    @JoinColumn(name = "producto_id")
    private Producto producto;

    private Integer cantidad;

    /**
     * 🚨 EL CAMPO MÁS IMPORTANTE: PRECIO HISTÓRICO (Snapshot)
     * Este campo guarda cuánto costaba el producto EN EL MOMENTO DE LA COMPRA.
     * * ¿Por qué no usamos producto.getPrecio()?
     * Porque el precio del producto puede subir mañana. Si miramos una orden
     * del año pasado, debe mostrar el precio antiguo (lo que pagó el cliente),
     * no el precio actual del catálogo.
     */
    private Double precioUnitario; 

    // --- GETTERS Y SETTERS ---
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Orden getOrden() { return orden; }
    public void setOrden(Orden orden) { this.orden = orden; }

    public Producto getProducto() { return producto; }
    public void setProducto(Producto producto) { this.producto = producto; }

    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }

    public Double getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(Double precioUnitario) { this.precioUnitario = precioUnitario; }
}