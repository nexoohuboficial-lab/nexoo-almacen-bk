package com.nexoohub.almacen.erp.service;

import com.nexoohub.almacen.common.exception.ResourceNotFoundException;
import com.nexoohub.almacen.erp.dto.CuentaPorPagarRequest;
import com.nexoohub.almacen.erp.dto.CuentaPorPagarResponse;
import com.nexoohub.almacen.erp.dto.PagoProveedorRequest;
import com.nexoohub.almacen.erp.entity.CuentaPorPagar;
import com.nexoohub.almacen.erp.entity.PagoProveedor;
import com.nexoohub.almacen.erp.repository.CuentaPorPagarRepository;
import com.nexoohub.almacen.erp.repository.PagoProveedorRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CuentaPorPagarService {

    private static final Logger log = LoggerFactory.getLogger(CuentaPorPagarService.class);

    private final CuentaPorPagarRepository cxpRepo;
    private final PagoProveedorRepository pagoRepo;

    public CuentaPorPagarService(CuentaPorPagarRepository cxpRepo, PagoProveedorRepository pagoRepo) {
        this.cxpRepo = cxpRepo;
        this.pagoRepo = pagoRepo;
    }

    @Transactional(readOnly = true)
    public List<CuentaPorPagarResponse> listarPendientes() {
        List<CuentaPorPagar> pendientes = cxpRepo.findByEstatusOrderByFechaVencimientoAsc("PENDIENTE");
        List<CuentaPorPagar> parciales = cxpRepo.findByEstatusOrderByFechaVencimientoAsc("PARCIAL");
        pendientes.addAll(parciales);
        return pendientes.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Transactional
    public CuentaPorPagarResponse registrar(CuentaPorPagarRequest req) {
        log.info("Registrando CxP: proveedor={}, factura={}", req.getProveedorId(), req.getNumeroFactura());

        CuentaPorPagar cxp = new CuentaPorPagar();
        cxp.setProveedorId(req.getProveedorId());
        cxp.setNumeroFactura(req.getNumeroFactura());
        cxp.setDescripcion(req.getDescripcion());
        cxp.setMontoTotal(req.getMontoTotal());
        cxp.setMontoPagado(BigDecimal.ZERO);
        cxp.setSaldoPendiente(req.getMontoTotal());
        cxp.setFechaFactura(req.getFechaFactura());
        cxp.setFechaVencimiento(req.getFechaVencimiento());
        cxp.setEstatus("PENDIENTE");
        cxp.setSucursalId(req.getSucursalId());

        return mapToResponse(cxpRepo.save(cxp));
    }

    @Transactional
    public CuentaPorPagarResponse abonar(Integer cxpId, PagoProveedorRequest req) {
        CuentaPorPagar cxp = cxpRepo.findById(cxpId)
            .orElseThrow(() -> new ResourceNotFoundException("Cuenta por pagar no encontrada: " + cxpId));

        if ("PAGADA".equals(cxp.getEstatus())) {
            throw new IllegalStateException("La cuenta ya está completamente PAGADA");
        }

        if (req.getMontoAbono().compareTo(cxp.getSaldoPendiente()) > 0) {
            throw new IllegalStateException("El abono ($" + req.getMontoAbono() +
                ") excede el saldo pendiente ($" + cxp.getSaldoPendiente() + ")");
        }

        // Registrar el pago
        PagoProveedor pago = new PagoProveedor();
        pago.setCuentaPorPagarId(cxpId);
        pago.setMontoAbono(req.getMontoAbono());
        pago.setMetodoPago(req.getMetodoPago());
        pago.setReferenciaPago(req.getReferenciaPago());
        pago.setFechaPago(req.getFechaPago());
        pago.setObservaciones(req.getObservaciones());
        pagoRepo.save(pago);

        // Actualizar saldos
        BigDecimal nuevoPagado = cxp.getMontoPagado().add(req.getMontoAbono());
        BigDecimal nuevoSaldo = cxp.getMontoTotal().subtract(nuevoPagado);
        cxp.setMontoPagado(nuevoPagado);
        cxp.setSaldoPendiente(nuevoSaldo);

        if (nuevoSaldo.compareTo(BigDecimal.ZERO) == 0) {
            cxp.setEstatus("PAGADA");
            log.info("CxP {} liquidada completamente", cxpId);
        } else {
            cxp.setEstatus("PARCIAL");
            log.info("CxP {} abonada. Saldo pendiente: ${}", cxpId, nuevoSaldo);
        }

        return mapToResponse(cxpRepo.save(cxp));
    }

    private CuentaPorPagarResponse mapToResponse(CuentaPorPagar entity) {
        CuentaPorPagarResponse dto = new CuentaPorPagarResponse();
        dto.setId(entity.getId());
        dto.setProveedorId(entity.getProveedorId());
        dto.setNumeroFactura(entity.getNumeroFactura());
        dto.setDescripcion(entity.getDescripcion());
        dto.setMontoTotal(entity.getMontoTotal());
        dto.setMontoPagado(entity.getMontoPagado());
        dto.setSaldoPendiente(entity.getSaldoPendiente());
        dto.setFechaFactura(entity.getFechaFactura());
        dto.setFechaVencimiento(entity.getFechaVencimiento());
        dto.setEstatus(entity.getEstatus());
        dto.setDiasAntiguedad(ChronoUnit.DAYS.between(entity.getFechaFactura(), LocalDate.now()));
        return dto;
    }
}
