package com.nexoohub.almacen.analitica.controller;

import com.nexoohub.almacen.analitica.dto.RendimientoEmpleadoResponse;
import com.nexoohub.almacen.analitica.service.RendimientoPersonalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/rendimiento-personal")
@RequiredArgsConstructor
@Tag(name = "Rendimiento Personal V1", description = "Endpoints para la consulta y cálculo de KPIs de rendimiento de vendedores")
public class RendimientoPersonalV1Controller {

    private final RendimientoPersonalService service;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'VENDEDOR')")
    @Operation(
        summary = "Dashboard de KPIs por vendedor del periodo",
        description = "Devuelve la lista de todos los vendedores con sus KPIs calculados para el mes/año indicado"
    )
    public ResponseEntity<List<RendimientoEmpleadoResponse>> getDashboard(
            @RequestParam(defaultValue = "0") int mes,
            @RequestParam(defaultValue = "0") int anio) {

        if (mes == 0) mes = LocalDate.now().getMonthValue();
        if (anio == 0) anio = LocalDate.now().getYear();

        return ResponseEntity.ok(service.obtenerDashboard(mes, anio));
    }

    @GetMapping("/empleado/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'VENDEDOR')")
    @Operation(
        summary = "Evolución mensual de un empleado",
        description = "Lista todos los snapshots de rendimiento disponibles para el empleado, del más reciente al más antiguo"
    )
    public ResponseEntity<List<RendimientoEmpleadoResponse>> getTendencia(
            @PathVariable("id") Integer empleadoId) {
        return ResponseEntity.ok(service.obtenerTendenciaEmpleado(empleadoId));
    }

    @PostMapping("/calcular")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Calcula el rendimiento de personal para un periodo",
        description = "Proceso masivo: recorre todos los empleados activos y persiste los KPIs mensuales"
    )
    public ResponseEntity<List<RendimientoEmpleadoResponse>> calcular(
            @RequestParam(defaultValue = "0") int mes,
            @RequestParam(defaultValue = "0") int anio) {

        if (mes == 0) mes = LocalDate.now().getMonthValue();
        if (anio == 0) anio = LocalDate.now().getYear();

        List<RendimientoEmpleadoResponse> resultado = service.calcularRendimientoMensual(mes, anio);
        return ResponseEntity.ok(resultado);
    }
}
