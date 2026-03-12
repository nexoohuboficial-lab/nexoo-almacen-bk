package com.nexoohub.almacen.inventario.controller;

import com.nexoohub.almacen.inventario.dto.TraspasoRequestDTO;
import com.nexoohub.almacen.inventario.service.TraspasoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/inventario/traspasos")
public class TraspasoController {

    @Autowired
    private TraspasoService traspasoService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'ALMACENISTA')")
    public ResponseEntity<Map<String, Object>> realizarTraspaso(@Valid @RequestBody TraspasoRequestDTO request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        
        String rastreoId = traspasoService.ejecutarTraspaso(request, username);
        
        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("exitoso", true);
        respuesta.put("mensaje", "Traspaso completado correctamente");
        respuesta.put("rastreoId", rastreoId);
        
        return new ResponseEntity<>(respuesta, HttpStatus.OK);
    }
}