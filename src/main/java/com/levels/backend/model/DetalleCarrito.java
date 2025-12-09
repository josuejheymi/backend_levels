package com.levels.backend.model;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction; 

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

/**
 * ENTIDAD: DETALLE DE CARRITO (Item)
 * ----------------------------------------------------
 * Representa un producto específico y su cantidad dentro del carrito de un usuario.
 * Es la tabla intermedia entre 'Carrito' y 'Producto'.
 */
@Entity
public class DetalleCarrito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * RELACIÓN CON EL CARRITO (Padre)
     * Muchos items pertenecen a un solo carrito.
     * * @JsonIgnore: Vital. Cuando pedimos la lista de items, no queremos que cada item
     * vuelva a imprimir el carrito completo (bucle infinito).
     */
    @ManyToOne
    @JoinColumn(name = "carrito_id")
    @JsonIgnore 
    private Carrito carrito;

    /**
     * RELACIÓN CON EL PRODUCTO
     * Muchos items pueden referenciar al mismo producto (varios usuarios comprando lo mismo).
     * * * LA LÍNEA MÁGICA: @OnDelete(action = OnDeleteAction.CASCADE)
     * ¿Qué pasa si el Admin borra un Producto de la base de datos, pero un usuario
     * todavía lo tiene en su carrito? Normalmente, la BD daría error (Foreign Key Constraint).
     * Con esta anotación, le decimos a Hibernate: "Si borran el Producto, borra también
     * esta línea del carrito automáticamente". Mantiene la BD limpia.
     */
    @ManyToOne
    @JoinColumn(name = "producto_id")
    @OnDelete(action = OnDeleteAction.CASCADE) 
    private Producto producto;

    private Integer cantidad;
    
    // Precio al momento de agregar (útil si el precio cambia mientras compras)
    private Double precioUnitario;

    /**
     * MÉTODO HELPER (Lógica de Negocio en la Entidad)
     * Calcula el total de esta línea.
     * No se guarda en la BD, se calcula al vuelo cuando Java usa el objeto.
     */
    public Double getSubtotal() {
        if (precioUnitario != null && cantidad != null) {
            return precioUnitario * cantidad;
        }
        return 0.0;
    }

    // --- GETTERS Y SETTERS ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Carrito getCarrito() { return carrito; }
    public void setCarrito(Carrito carrito) { this.carrito = carrito; }

    public Producto getProducto() { return producto; }
    public void setProducto(Producto producto) { this.producto = producto; }

    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }

    public Double getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(Double precioUnitario) { this.precioUnitario = precioUnitario; }
}