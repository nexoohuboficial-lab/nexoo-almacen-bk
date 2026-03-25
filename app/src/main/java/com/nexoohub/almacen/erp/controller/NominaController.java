package com.nexoohub.almacen.erp.controller;

import com.nexoohub.almacen.common.ApiResponse;
import com.nexoohub.almacen.erp.dto.EmpleadoDTO;
import com.nexoohub.almacen.erp.dto.NominaPeriodoRequest;
import com.nexoohub.almacen.erp.dto.NominaPeriodoResponse;
import com.nexoohub.almacen.erp.dto.ReciboNominaResponse;
import com.nexoohub.almacen.erp.service.NominaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/nomina")
@CrossOrigin(origins = "*")
@Tag(name = "ERP — Nómina y RRHH", description = "Catálogo de empleados y cálculo automático de recibos de nómina")
public class NominaController {

    private final NominaService nominaService;

    public NominaController(NominaService nominaService) {
        this.nominaService = nominaService;
    }

    // =====================================
    // Empleados
    // =====================================

    @PostMapping("/empleados")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    @Operation(summary = "Registrar Empleado", description = "Da de alta a un trabajador en el catálogo de RRHH")
    public ResponseEntity<ApiResponse<EmpleadoDTO>> registrarEmpleado(
            @Valid @RequestBody EmpleadoDTO req) {
        EmpleadoDTO resp = nominaService.registrarEmpleado(req);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>("Empleado registrado exitosamente", resp));
    }

    @GetMapping("/empleados")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'MOSTRADOR')")
    @Operation(summary = "Listar Empleados", description = "Lista empleados activos en la sucursal especificada")
    public ResponseEntity<ApiResponse<List<EmpleadoDTO>>> listarEmpleados(
            @RequestParam Integer sucursalId) {
        return ResponseEntity.ok(new ApiResponse<>("Empleados encontrados", nominaService.listarEmpleados(sucursalId)));
    }

    // =====================================
    // Periodos de Nómina
    // =====================================

    @PostMapping("/periodos")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    @Operation(summary = "Crear Periodo de Nómina", description = "Abre un periodo (Ej. 1ra Quincena Ene) en estatus BORRADOR")
    public ResponseEntity<ApiResponse<NominaPeriodoResponse>> crearPeriodo(
            @Valid @RequestBody NominaPeriodoRequest req) {
        NominaPeriodoResponse resp = nominaService.crearPeriodo(req);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>("Periodo creado exitosamente", resp));
    }

    @GetMapping("/periodos")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    @Operation(summary = "Listar Periodos", description = "Busca los periodos de nómina en un rango de fechas")
    public ResponseEntity<ApiResponse<List<NominaPeriodoResponse>>> listarPeriodos(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        return ResponseEntity.ok(new ApiResponse<>("Periodos encontrados", nominaService.listarPeriodos(desde, hasta)));
    }

    // =====================================
    // Generación y Recibos
    // =====================================

    @PostMapping("/periodos/{id}/generar")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    @Operation(summary = "Generar Recibos (Cálculo)", description = "Calcula la nómina (Sueldo base, ISR, IMSS) para todos los empleados activos de la sucursal en el periodo indicado")
    public ResponseEntity<ApiResponse<NominaPeriodoResponse>> generarRecibosParaPeriodo(
            @PathVariable Integer id,
            @RequestParam Integer sucursalId) {
        NominaPeriodoResponse resp = nominaService.generarRecibosParaPeriodo(id, sucursalId);
        return ResponseEntity.ok(new ApiResponse<>("Recibos generados y nómina calculada correctamente", resp));
    }

    @GetMapping("/recibos/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    @Operation(summary = "Ver detalle de Recibo", description = "Devuelve las percepciones, deducciones y neto a pagar de un recibo en específico")
    public ResponseEntity<ApiResponse<ReciboNominaResponse>> obtenerRecibo(
            @PathVariable Integer id) {
        return ResponseEntity.ok(new ApiResponse<>("Recibo de nómina recuperado", nominaService.obtenerRecibo(id)));
    }
}
