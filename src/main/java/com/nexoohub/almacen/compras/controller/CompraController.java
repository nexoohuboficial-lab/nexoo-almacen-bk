package com.nexoohub.almacen.compras.controller;

import com.nexoohub.almacen.compras.dto.CompraRequestDTO;
import com.nexoohub.almacen.compras.entity.Compra;
import com.nexoohub.almacen.compras.service.CompraService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/compras")
public class CompraController {

    @Autowired
    private CompraService compraService;

    @PostMapping("/ingreso")
    public ResponseEntity<Map<String, Object>> registrarIngreso(@Valid @RequestBody CompraRequestDTO request) {
        // Obtenemos el username de quien está logueado (el admin o el empleado)
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        // Ejecutamos la magia financiera
        Compra compraProcesada = compraService.procesarIngresoMercancia(request, username);

        // Respuesta limpia y profesional
        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("exitoso", true);
        respuesta.put("mensaje", "Ingreso de mercancía procesado y precios actualizados");
        respuesta.put("folioInterno", compraProcesada.getId());
        respuesta.put("totalFacturaConIva", compraProcesada.getTotalCompra());
        respuesta.put("fechaRegistro", compraProcesada.getFechaCompra());

        return new ResponseEntity<>(respuesta, HttpStatus.CREATED);
    }
}
