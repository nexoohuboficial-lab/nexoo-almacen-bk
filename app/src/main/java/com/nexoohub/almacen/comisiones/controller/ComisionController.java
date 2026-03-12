package com.nexoohub.almacen.comisiones.controller;

import com.nexoohub.almacen.comisiones.dto.*;
import com.nexoohub.almacen.comisiones.service.ComisionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * Controller REST para gestionar comisiones de vendedores.
 * 
 * Endpoints principales:
 * - Configuración de reglas de comisión
 * - Cálculo automático de comisiones
 * - Consulta de comisiones por vendedor/periodo
 * - Aprobación y pago de comisiones
 * 
 * @author NexooHub Development Team
 * @since 1.0
 */
@RestController
@RequestMapping("/api/comisiones")
public class ComisionController {

    private final ComisionService comisionService;

    public ComisionController(ComisionService comisionService) {
        this.comisionService = comisionService;
    }

    // ==========================================
    // ENDPOINTS DE REGLAS
    // ==========================================

    @PostMapping("/reglas")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<ReglaComisionResponseDTO> crearRegla(
            @Valid @RequestBody ReglaComisionRequestDTO request) {
        ReglaComisionResponseDTO response = comisionService.crearRegla(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/reglas/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<ReglaComisionResponseDTO> actualizarRegla(
            @PathVariable Integer id,
            @Valid @RequestBody ReglaComisionRequestDTO request) {
        ReglaComisionResponseDTO response = comisionService.actualizarRegla(id, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/reglas/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'AUDITOR')")
    public ResponseEntity<ReglaComisionResponseDTO> obtenerReglaPorId(@PathVariable Integer id) {
        ReglaComisionResponseDTO response = comisionService.obtenerReglaPorId(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/reglas")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'AUDITOR')")
    public ResponseEntity<List<ReglaComisionResponseDTO>> listarReglas(
            @RequestParam(required = false, defaultValue = "true") Boolean soloActivas) {
        List<ReglaComisionResponseDTO> reglas = soloActivas 
                ? comisionService.listarReglasActivas()
                : comisionService.listarTodasLasReglas();
        return ResponseEntity.ok(reglas);
    }

    @DeleteMapping("/reglas/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<Void> eliminarRegla(@PathVariable Integer id) {
        comisionService.eliminarRegla(id);
        return ResponseEntity.ok().build();
    }

    // ==========================================
    // ENDPOINTS DE CÁLCULO
    // ==========================================

    @PostMapping("/calcular")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'SUPERVISOR')")
    public ResponseEntity<List<ComisionResponseDTO>> calcularComisionesPeriodo(
            @RequestParam Integer anio,
            @RequestParam Integer mes) {
        List<ComisionResponseDTO> comisiones = comisionService.calcularComisionesPorPeriodo(anio, mes);
        return ResponseEntity.ok(comisiones);
    }

    @PostMapping("/calcular/vendedor/{vendedorId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'SUPERVISOR')")
    public ResponseEntity<ComisionResponseDTO> calcularComisionVendedor(
            @PathVariable Integer vendedorId,
            @RequestParam Integer anio,
            @RequestParam Integer mes) {
        ComisionResponseDTO comision = comisionService.calcularComisionVendedor(vendedorId, anio, mes);
        return ResponseEntity.ok(comision);
    }

    // ==========================================
    // ENDPOINTS DE CONSULTA
    // ==========================================

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'AUDITOR')")
    public ResponseEntity<ComisionResponseDTO> obtenerComisionPorId(@PathVariable Integer id) {
        ComisionResponseDTO comision = comisionService.obtenerComisionPorId(id);
        return ResponseEntity.ok(comision);
    }

    @GetMapping("/vendedor/{vendedorId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'AUDITOR')")
    public ResponseEntity<List<ComisionResponseDTO>> listarComisionesPorVendedor(
            @PathVariable Integer vendedorId) {
        List<ComisionResponseDTO> comisiones = comisionService.listarComisionesPorVendedor(vendedorId);
        return ResponseEntity.ok(comisiones);
    }

    @GetMapping("/periodo")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'AUDITOR')")
    public ResponseEntity<List<ComisionResponseDTO>> listarComisionesPorPeriodo(
            @RequestParam Integer anio,
            @RequestParam Integer mes) {
        List<ComisionResponseDTO> comisiones = comisionService.listarComisionesPorPeriodo(anio, mes);
        return ResponseEntity.ok(comisiones);
    }

    @GetMapping("/estado/{estado}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'AUDITOR')")
    public ResponseEntity<List<ComisionResponseDTO>> listarComisionesPorEstado(
            @PathVariable String estado) {
        List<ComisionResponseDTO> comisiones = comisionService.listarComisionesPorEstado(estado);
        return ResponseEntity.ok(comisiones);
    }

    @GetMapping("/resumen")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'AUDITOR')")
    public ResponseEntity<ResumenComisionesDTO> obtenerResumenPeriodo(
            @RequestParam Integer anio,
            @RequestParam Integer mes) {
        ResumenComisionesDTO resumen = comisionService.obtenerResumenPorPeriodo(anio, mes);
        return ResponseEntity.ok(resumen);
    }

    // ==========================================
    // ENDPOINTS DE APROBACIÓN Y PAGO
    // ==========================================

    @PutMapping("/{id}/aprobar")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<ComisionResponseDTO> aprobarComision(
            @PathVariable Integer id,
            @Valid @RequestBody AprobarComisionRequestDTO request) {
        ComisionResponseDTO comision = comisionService.aprobarComision(id, request);
        return ResponseEntity.ok(comision);
    }

    @PutMapping("/{id}/pagar")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<ComisionResponseDTO> marcarComoPagada(@PathVariable Integer id) {
        ComisionResponseDTO comision = comisionService.marcarComoPagada(id);
        return ResponseEntity.ok(comision);
    }

    @PutMapping("/{id}/ajustar")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<ComisionResponseDTO> ajustarComision(
            @PathVariable Integer id,
            @RequestParam BigDecimal ajuste,
            @RequestParam String motivo) {
        ComisionResponseDTO comision = comisionService.ajustarComision(id, ajuste, motivo);
        return ResponseEntity.ok(comision);
    }
}
