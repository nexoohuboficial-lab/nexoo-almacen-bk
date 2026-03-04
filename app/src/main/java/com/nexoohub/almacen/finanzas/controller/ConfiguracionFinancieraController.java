package com.nexoohub.almacen.finanzas.controller;

import com.nexoohub.almacen.finanzas.entity.ConfiguracionFinanciera;
import com.nexoohub.almacen.finanzas.repository.ConfiguracionFinancieraRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/finanzas/parametros")
public class ConfiguracionFinancieraController {

    @Autowired
    private ConfiguracionFinancieraRepository configuracionRepository;

    // 1. OBTENER LOS PARÁMETROS ACTUALES (Para mostrarlos en la pantalla de ajustes)
    @GetMapping
    public ResponseEntity<ConfiguracionFinanciera> obtenerParametros() {
        // Asumimos que la configuración global maestra es el registro con ID 1
        return configuracionRepository.findById(1)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 2. ACTUALIZAR LAS "PALANCAS" FINANCIERAS
    @PutMapping
    public ResponseEntity<Map<String, Object>> actualizarParametros(@RequestBody ConfiguracionFinanciera detalles) {
        return configuracionRepository.findById(1)
                .map(config -> {
                    // Actualizamos solo los campos de negocio
                    config.setIva(detalles.getIva());
                    config.setMargenGananciaBase(detalles.getMargenGananciaBase());
                    config.setGastosFijosMensuales(detalles.getGastosFijosMensuales());
                    config.setMetaVentasMensual(detalles.getMetaVentasMensual());
                    config.setComisionTarjeta(detalles.getComisionTarjeta());
                    
                    configuracionRepository.save(config);
                    
                    Map<String, Object> respuesta = new HashMap<>();
                    respuesta.put("exitoso", true);
                    respuesta.put("mensaje", "Parámetros financieros actualizados correctamente. Los nuevos precios se calcularán en la próxima compra.");
                    
                    return ResponseEntity.ok(respuesta);
                })
                .orElse(ResponseEntity.notFound().build());
    }
}