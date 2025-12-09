package com.levels.backend.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.levels.backend.model.Orden;
import com.levels.backend.repository.OrdenRepository;
import com.levels.backend.service.OrdenService;

/**
 * CONTROLADOR: ÓRDENES DE COMPRA (Ventas)
 * ----------------------------------------------------
 * Gestiona el ciclo de vida de una venta:
 * 1. Checkout (Creación de la orden).
 * 2. Historial (Mis pedidos).
 * 3. Dashboard (Estadísticas para el Admin).
 */
@RestController
@RequestMapping("/api/ordenes")
@CrossOrigin(origins = "*") // Permite acceso desde el Frontend
public class OrdenController {

    @Autowired
    private OrdenService ordenService;

    // Inyección Directa del Repositorio
    // * Nota: Usualmente usamos el Service, pero para consultas estadísticas rápidas (COUNT, SUM)
    // es aceptable inyectar el repositorio aquí para no sobrecargar la capa de servicio.
    @Autowired
    private OrdenRepository ordenRepository;

    /**
     * 1. CHECKOUT (Finalizar Compra)
     * Método: POST /api/ordenes/checkout
     * Body: { "usuarioId": 1, "direccion": "...", "region": "...", "comuna": "..." }
     * Responsabilidad: Recibe los datos de envío, mueve los items del carrito a la orden y vacía el carrito.
     */
    @PostMapping("/checkout")
    public ResponseEntity<?> checkout(@RequestBody Map<String, Object> payload) {
        try {
            // EXTRACCIÓN DE DATOS DEL MAPA (Parsing Seguro)
            // Jackson (Librería JSON) puede interpretar números como Integer o Long.
            // Castear a (Number) primero es la forma segura de obtener el valor numérico.
            Long usuarioId = ((Number) payload.get("usuarioId")).longValue();
            
            String direccion = (String) payload.get("direccion");
            String region = (String) payload.get("region");
            String comuna = (String) payload.get("comuna");

            // Llamamos al servicio transaccional que hace la magia
            Orden orden = ordenService.generarOrden(usuarioId, direccion, region, comuna);
            
            // Devolvemos la orden creada (Status 200 OK)
            return ResponseEntity.ok(orden);
        } catch (Exception e) {
            // Si el carrito estaba vacío o hubo error de stock
            return ResponseEntity.badRequest().body("Error al procesar compra: " + e.getMessage());
        }
    }

    /**
     * 2. HISTORIAL DE UN USUARIO
     * Método: GET /api/ordenes/usuario/{id}
     * Uso: Pantalla "Mi Perfil" en React.
     */
    @GetMapping("/usuario/{id}")
    public List<Orden> historial(@PathVariable Long id) {
        return ordenService.obtenerOrdenesUsuario(id);
    }

    /**
     * 3. DETALLE DE UNA ORDEN
     * Método: GET /api/ordenes/{id}
     * Uso: Pantalla de "Comprobante de venta" (OrderDetail.js).
     */
    @GetMapping("/{id}")
    public ResponseEntity<Orden> obtenerPorId(@PathVariable Long id) {
        Orden orden = ordenService.findById(id);
        
        if (orden == null) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(orden);
    }

    /**
     * 4. ESTADÍSTICAS (Solo Admin)
     * Método: GET /api/ordenes/stats
     * Uso: Panel de Control (Dashboard). Muestra cuánto dinero ha entrado.
     */
    @GetMapping("/stats")
    public ResponseEntity<?> obtenerEstadisticas() {
        // Usamos consultas nativas del repositorio (Queries)
        Double totalVentas = ordenRepository.sumarVentasTotales(); // SELECT SUM(total)...
        long cantidadOrdenes = ordenRepository.count();            // SELECT COUNT(*)...

        // Devolvemos un JSON simple construido al vuelo con Map.of
        return ResponseEntity.ok(Map.of(
            "totalVentas", totalVentas != null ? totalVentas : 0.0,
            "cantidadOrdenes", cantidadOrdenes
        ));
    }

    /**
     * 5. LISTAR TODAS LAS VENTAS (Solo Admin)
     * Método: GET /api/ordenes
     * Uso: Tabla general de ventas en el Admin Panel.
     */
    @GetMapping
    // @PreAuthorize("hasRole('ADMIN')") <--- Ya lo configuramos en SecurityConfig, es redundante pero sirve de refuerzo.
    public List<Orden> listarTodas() {
        return ordenService.listarTodas();
    }
}