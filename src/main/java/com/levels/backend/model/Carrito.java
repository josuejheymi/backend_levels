package com.levels.backend.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;

/**
 * ENTIDAD: CARRITO DE COMPRAS
 * ----------------------------------------------------
 * Representa el "cesto" virtual del usuario.
 * A diferencia de una Orden (que es histórica e inmutable),
 * el Carrito es volátil: cambia constantemente mientras el usuario navega.
 */
@Entity
public class Carrito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * RELACIÓN 1 a 1 (OneToOne)
     * Un Usuario tiene UN solo Carrito activo.
     * Un Carrito pertenece a UN solo Usuario.
     * @JoinColumn: Define que la tabla 'carrito' tendrá una columna 'usuario_id'.
     */
    @OneToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    /**
     * RELACIÓN 1 a N (OneToMany)
     * Un Carrito puede tener MUCHOS items (DetalleCarrito).
     * * Configuración Crítica:
     * 1. mappedBy = "carrito": Indica que la dueña de la relación es la clase DetalleCarrito.
     * 2. cascade = CascadeType.ALL: Si guardamos/borramos el Carrito, le pasa lo mismo a sus items.
     * 3. orphanRemoval = true: Si quitamos un item de esta lista en Java (lista.remove(0)),
     * JPA lo borrará automáticamente de la base de datos. ¡Vital para el botón "Eliminar"!
     */
    @OneToMany(mappedBy = "carrito", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetalleCarrito> items = new ArrayList<>();

    // Total acumulado (Suma de precio * cantidad de todos los items)
    private Double total = 0.0;
    
    // --- GETTERS Y SETTERS ---
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
    
    public List<DetalleCarrito> getItems() { return items; }
    public void setItems(List<DetalleCarrito> items) { this.items = items; }
    
    public Double getTotal() { return total; }
    public void setTotal(Double total) { this.total = total; }
}