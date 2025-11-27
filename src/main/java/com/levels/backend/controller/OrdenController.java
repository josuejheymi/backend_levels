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
import com.levels.backend.service.OrdenService;

@RestController
@RequestMapping("/api/ordenes")
@CrossOrigin(origins = "*")
public class OrdenController {

    @Autowired
    private OrdenService ordenService;

    // POST: Finalizar compra (Checkout)
    // Body: { "usuarioId": 1 }
    @PostMapping("/checkout")
    public ResponseEntity<?> checkout(@RequestBody Map<String, Long> payload) {
        try {
            Long usuarioId = payload.get("usuarioId");
            Orden orden = ordenService.generarOrden(usuarioId);
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
}