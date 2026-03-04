package com.nexoohub.almacen.ventas.controller;
import com.nexoohub.almacen.ventas.dto.VentaRequestDTO;
import com.nexoohub.almacen.ventas.entity.Venta;
import com.nexoohub.almacen.ventas.service.VentaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/ventas")
public class VentaController {

    @Autowired
    private VentaService ventaService;

    @PostMapping
    public ResponseEntity<Venta> realizarVenta(@Valid @RequestBody VentaRequestDTO request) {
    String username = SecurityContextHolder.getContext().getAuthentication().getName();
    Venta ventaRealizada = ventaService.procesarVenta(request, username);
    return new ResponseEntity<>(ventaRealizada, HttpStatus.CREATED);
}
}