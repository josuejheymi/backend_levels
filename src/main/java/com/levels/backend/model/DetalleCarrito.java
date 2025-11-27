package com.levels.backend.model;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;       // <--- IMPORTAR

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity; // <--- IMPORTAR
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class DetalleCarrito {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "carrito_id")
    @JsonIgnore     // Evitar referencia circular en JSON
    private Carrito carrito;

    @ManyToOne
    @JoinColumn(name = "producto_id")
    // AGREGAR ESTA LÍNEA MÁGICA:
    @OnDelete(action = OnDeleteAction.CASCADE) 
    private Producto producto;

    private Integer cantidad;
    private Double precioUnitario;

    public Double getSubtotal() {
        return precioUnitario * cantidad;
    }

    // Getters y Setters...
    // (El resto de tu código sigue igual)
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