package com.nexoohub.almacen.empleados.controller;

import com.nexoohub.almacen.empleados.entity.Empleado;
import com.nexoohub.almacen.empleados.repository.EmpleadoRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/empleados")
public class EmpleadoController {

    @Autowired
    private EmpleadoRepository empleadoRepository;

    // Dar de alta a un empleado nuevo
    @PostMapping
    public ResponseEntity<Map<String, Object>> registrarEmpleado(@Valid @RequestBody Empleado empleado) {
        Empleado guardado = empleadoRepository.save(empleado);
        
        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("exitoso", true);
        respuesta.put("mensaje", "Empleado registrado correctamente");
        respuesta.put("id", guardado.getId());
        
        return new ResponseEntity<>(respuesta, HttpStatus.CREATED);
    }

    // Ver a todos los empleados de una sucursal
    @GetMapping("/sucursal/{sucursalId}")
    public ResponseEntity<List<Empleado>> obtenerEmpleadosPorSucursal(@PathVariable Integer sucursalId) {
        return ResponseEntity.ok(empleadoRepository.findBySucursalIdAndActivoTrue(sucursalId));
    }

    // Dar de baja (borrado lógico)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> darDeBajaEmpleado(@PathVariable Integer id) {
        return empleadoRepository.findById(id).map(empleado -> {
            empleado.setActivo(false); // No lo borramos de la BD para no perder el historial de ventas, solo lo desactivamos
            empleadoRepository.save(empleado);
            return ResponseEntity.noContent().<Void>build();
        }).orElse(ResponseEntity.notFound().build());
    }
}