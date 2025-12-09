package com.levels.backend.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

/**
 * ENTIDAD: ORDEN DE COMPRA (Boleta)
 * ----------------------------------------------------
 * Representa una transacción finalizada.
 * A diferencia del Carrito, esta entidad es HISTÓRICA.
 * Una vez creada, no debería modificarse (inmutabilidad financiera).
 */
@Entity
@Table(name = "ordenes")
public class Orden {

    // --- IDENTIFICADOR ---
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Monto final a pagar (Suma de los subtotales de los detalles)
    private Double total;

    /**
     * RELACIÓN: CLIENTE (Quién compró)
     * * @ManyToOne: Un usuario puede tener muchas compras.
     * * @JsonIgnoreProperties("password"): ¡SEGURIDAD!
     * Cuando pidamos la lista de órdenes, Spring traerá al usuario dueño de cada una.
     * Con esto evitamos que, por accidente, el JSON incluya la contraseña encriptada del cliente.
     */
    @ManyToOne
    @JoinColumn(name = "usuario_id")
    @JsonIgnoreProperties("password") 
    private Usuario usuario;

    /**
     * RELACIÓN: DETALLES (Qué compró)
     * * Composición Fuerte (CascadeType.ALL):
     * La Orden es "dueña" de sus líneas de detalle. Si guardamos la Orden,
     * se guardan automáticamente sus detalles. Si borramos la Orden, se borran sus detalles.
     */
    @OneToMany(mappedBy = "orden", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetalleOrden> detalles = new ArrayList<>();

    // --- DATOS DE AUDITORÍA ---
    
    // Fecha y hora exacta de la compra. Vital para reportes y ordenamiento.
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaCreacion; 

    // --- DATOS DE DESPACHO ---
    // Guardamos la dirección en la orden (Snapshot).
    // Si el usuario se muda mañana, esta orden debe conservar la dirección antigua donde se envió.
    private String direccion;
    private String region;
    private String comuna;

    // --- CONSTRUCTORES ---
    
    public Orden() {
        // Al crear una nueva instancia (new Orden()), asignamos la fecha/hora actual automáticamente.
        this.fechaCreacion = new Date(); 
    }

    // --- GETTERS Y SETTERS ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Double getTotal() { return total; }
    public void setTotal(Double total) { this.total = total; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public List<DetalleOrden> getDetalles() { return detalles; }
    public void setDetalles(List<DetalleOrden> detalles) { this.detalles = detalles; }

    public Date getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(Date fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public String getRegion() { return region; }
    public void setRegion(String region) { this.region = region; }

    public String getComuna() { return comuna; }
    public void setComuna(String comuna) { this.comuna = comuna; }
}