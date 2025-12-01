package com.levels.backend.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.levels.backend.model.Orden;
import com.levels.backend.service.OrdenService;

@RestController
@RequestMapping("/api/ordenes")
@CrossOrigin(origins = "*")
public class OrdenController {

    @Autowired
    private OrdenService ordenService;

    @Autowired // Inyectamos el repositorio directo para este reporte rápido
    private com.levels.backend.repository.OrdenRepository ordenRepository;

    // POST: Finalizar compra (Checkout)
    // Body: { "usuarioId": 1 }
    // POST: Finalizar compra (Checkout)
    // Body esperado: { "usuarioId": 1, "direccion": "Calle Falsa 123" }
    @PostMapping("/checkout")
    public ResponseEntity<?> checkout(@RequestBody Map<String, Object> payload) {
        try {
            // Convertimos los datos del JSON
            Long usuarioId = ((Number) payload.get("usuarioId")).longValue();
            String direccion = (String) payload.get("direccion"); // <--- Nuevo

            Orden orden = ordenService.generarOrden(usuarioId, direccion);
            return ResponseEntity.ok(orden);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error en la compra: " + e.getMessage());
        }
    }

    // GET: Historial de compras
    @GetMapping("/usuario/{id}")
    public List<Orden> historial(@PathVariable Long id) {
        return ordenService.obtenerOrdenesUsuario(id);
    }
    @GetMapping("/stats")
    public ResponseEntity<?> obtenerEstadisticas() {
        Double totalVentas = ordenRepository.sumarVentasTotales();
        long cantidadOrdenes = ordenRepository.count(); // Cuenta cuántas filas hay

        // Devolvemos un JSON bonito
        return ResponseEntity.ok(Map.of(
            "totalVentas", totalVentas != null ? totalVentas : 0.0,
            "cantidadOrdenes", cantidadOrdenes
        ));
    }
    // ...
    // GET: Ver todas las órdenes (Solo Admin)
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')") // O confía en el SecurityConfig
    public List<Orden> listarTodas() {
        return ordenService.listarTodas();
    }
}