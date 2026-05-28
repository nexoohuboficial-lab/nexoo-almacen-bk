package com.nexoohub.almacen.catalogo.controller;

import com.nexoohub.almacen.catalogo.dto.ClienteBloqueadoDTO;
import com.nexoohub.almacen.catalogo.dto.MorosidadReporteDTO;
import com.nexoohub.almacen.catalogo.repository.ClienteRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@RestController
@RequestMapping("/api/v1/morosidad")
@RequiredArgsConstructor
@Tag(name = "Morosidad V1", description = "Endpoints para la gestión y reporte de clientes morosos")
public class MorosidadV1Controller {

    private final ClienteRepository clienteRepository;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'AUDITOR')")
    @Operation(summary = "Listar clientes morosos", description = "Obtiene la lista de clientes con saldo pendiente de pago")
    public ResponseEntity<List<ClienteBloqueadoDTO>> obtenerClientesMorosos() {
        List<ClienteBloqueadoDTO> clientes = clienteRepository.obtenerClientesConSaldoPendiente();
        return ResponseEntity.ok(clientes);
    }

    @GetMapping("/reporte")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'AUDITOR')")
    @Operation(summary = "Generar reporte de morosidad", description = "Calcula métricas globales de deuda (total, promedio, máxima) y desglosa clientes morosos")
    public ResponseEntity<MorosidadReporteDTO> obtenerReporte() {
        List<ClienteBloqueadoDTO> clientes = clienteRepository.obtenerClientesConSaldoPendiente();
        
        int totalClientes = clientes.size();
        BigDecimal totalDeuda = BigDecimal.ZERO;
        BigDecimal deudaMaxima = BigDecimal.ZERO;
        BigDecimal deudaPromedio = BigDecimal.ZERO;

        for (ClienteBloqueadoDTO c : clientes) {
            BigDecimal saldo = c.getSaldoPendiente() != null ? c.getSaldoPendiente() : BigDecimal.ZERO;
            totalDeuda = totalDeuda.add(saldo);
            if (saldo.compareTo(deudaMaxima) > 0) {
                deudaMaxima = saldo;
            }
        }

        if (totalClientes > 0) {
            deudaPromedio = totalDeuda.divide(BigDecimal.valueOf(totalClientes), 2, RoundingMode.HALF_UP);
        }

        MorosidadReporteDTO reporte = new MorosidadReporteDTO(
                totalClientes,
                totalDeuda,
                deudaPromedio,
                deudaMaxima,
                clientes
        );

        return ResponseEntity.ok(reporte);
    }
}
