package com.levels.backend.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;

@Entity
public class Orden {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime fecha;
    private Double total;
    
    // NUEVO CAMPO: Dirección de Envío
    private String direccionEnvio; 

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @OneToMany(mappedBy = "orden", cascade = CascadeType.ALL)
    private List<DetalleOrden> detalles = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        this.fecha = LocalDateTime.now();
    }

    // Getters y Setters (No olvides agregar el de direccionEnvio)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }
    public Double getTotal() { return total; }
    public void setTotal(Double total) { this.total = total; }
    
    public String getDireccionEnvio() { return direccionEnvio; }
    public void setDireccionEnvio(String direccionEnvio) { this.direccionEnvio = direccionEnvio; }
    
    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
    public List<DetalleOrden> getDetalles() { return detalles; }
    public void setDetalles(List<DetalleOrden> detalles) { this.detalles = detalles; }
}