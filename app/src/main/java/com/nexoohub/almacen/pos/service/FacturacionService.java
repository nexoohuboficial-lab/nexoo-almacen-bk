package com.nexoohub.almacen.pos.service;

import com.nexoohub.almacen.common.exception.ResourceNotFoundException;
import com.nexoohub.almacen.pos.dto.CancelacionCfdiRequest;
import com.nexoohub.almacen.pos.dto.FacturaFiscalResponse;
import com.nexoohub.almacen.pos.dto.TimbradoRequest;
import com.nexoohub.almacen.pos.entity.ConfigPac;
import com.nexoohub.almacen.pos.entity.FacturaFiscal;
import com.nexoohub.almacen.pos.repository.ConfigPacRepository;
import com.nexoohub.almacen.pos.repository.FacturaFiscalRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Servicio para la facturación electrónica CFDI 4.0 (POS-03).
 * Simula la comunicación con un PAC (Proveedor Autorizado de Certificación).
 *
 * @author NexooHub Development Team
 * @since 1.0 — POS-03
 */
@Service
public class FacturacionService {

    private static final Logger log = LoggerFactory.getLogger(FacturacionService.class);

    private final FacturaFiscalRepository facturaRepo;
    private final ConfigPacRepository pacRepo;

    public FacturacionService(FacturaFiscalRepository facturaRepo, ConfigPacRepository pacRepo) {
        this.facturaRepo = facturaRepo;
        this.pacRepo     = pacRepo;
    }

    /**
     * Simula el proceso de timbrado generando un UUID válido y guardando el registro.
     */
    @Transactional
    public FacturaFiscalResponse timbrarFactura(TimbradoRequest request) {
        log.info("Timbrando factura para ventaId={} clienteId={}", request.getVentaId(), request.getClienteId());

        // Verificar si la venta ya está facturada
        facturaRepo.findByVentaId(request.getVentaId()).ifPresent(f -> {
            throw new IllegalStateException("La venta ID " + request.getVentaId() + " ya cuenta con una factura (UUID: " + f.getUuid() + ")");
        });

        // Obtener PAC activo para validar que esté configurado
        ConfigPac pacActivo = pacRepo.findActiveConfiguration()
            .orElseThrow(() -> new IllegalStateException("No hay un PAC configurado o activo en el sistema."));
        
        log.info("Utilizando proveedor PAC: {} (Entorno: {})", pacActivo.getProveedor(), pacActivo.getEntorno());

        FacturaFiscal factura = new FacturaFiscal();
        factura.setVentaId(request.getVentaId());
        factura.setClienteId(request.getClienteId());
        factura.setMontoTotal(request.getMontoTotal());
        factura.setUsoCfdi(request.getUsoCfdi());
        factura.setMetodoPago(request.getMetodoPago());
        factura.setFormaPago(request.getFormaPago());
        factura.setRfcReceptor(request.getRfcReceptor());
        factura.setRazonSocialReceptor(request.getRazonSocialReceptor());
        factura.setRegimenFiscalReceptor(request.getRegimenFiscalReceptor());
        factura.setCodigoPostalReceptor(request.getCodigoPostalReceptor());
        
        // Simulación SAT/PAC
        factura.setUuid(UUID.randomUUID().toString().toUpperCase()); // Simula el UUID (Folio Fiscal)
        factura.setEstatus("TIMBRADA");
        factura.setFechaEmision(LocalDateTime.now());
        factura.setXmlGenerado("<?xml version=\"1.0\" encoding=\"UTF-8\"?><cfdi:Comprobante Version=\"4.0\" UUID=\"" + factura.getUuid() + "\">...</cfdi:Comprobante>");
        factura.setUrlPdf("https://api.sat.gob.mx/descargas/cfdi/" + factura.getUuid() + ".pdf"); // Simulado

        FacturaFiscal guardada = facturaRepo.save(factura);
        log.info("Factura timbrada exitosamente: UUID={}, Monto={}", guardada.getUuid(), guardada.getMontoTotal());

        return mapToResponse(guardada);
    }

    /**
     * Cancela una factura previamente timbrada ante el PAC/SAT (simulado).
     */
    @Transactional
    public FacturaFiscalResponse cancelarFactura(Integer facturaId, CancelacionCfdiRequest request) {
        log.info("Cancelando factura ID={}, motivo={}", facturaId, request.getMotivoCancelacion());

        FacturaFiscal factura = facturaRepo.findById(facturaId)
            .orElseThrow(() -> new ResourceNotFoundException("Factura no encontrada: " + facturaId));

        if (!"TIMBRADA".equals(factura.getEstatus())) {
            throw new IllegalStateException("La factura no está TIMBRADA, estatus actual: " + factura.getEstatus());
        }

        factura.setMotivoCancelacion(request.getMotivoCancelacion());
        factura.setEstatus("CANCELADA");
        factura.setAcuseCancelacion("<AjusteCancelacion><UUID>" + factura.getUuid() + "</UUID><Estatus>Cancelado</Estatus></AjusteCancelacion>");

        FacturaFiscal cancelada = facturaRepo.save(factura);
        log.info("Factura cancelada con éxito: UUID={}", cancelada.getUuid());

        return mapToResponse(cancelada);
    }

    /**
     * Obtiene el DTO de una factura por su ID para descarga.
     */
    @Transactional(readOnly = true)
    public FacturaFiscalResponse descargarFactura(Integer facturaId) {
        FacturaFiscal factura = facturaRepo.findById(facturaId)
            .orElseThrow(() -> new ResourceNotFoundException("Factura no encontrada con ID: " + facturaId));
        return mapToResponse(factura);
    }

    /**
     * Consulta todas las facturas emitidas de un cliente.
     */
    @Transactional(readOnly = true)
    public List<FacturaFiscalResponse> consultarFacturasPorCliente(Integer clienteId) {
        return facturaRepo.findByClienteIdOrderByFechaEmisionDesc(clienteId)
            .stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    private FacturaFiscalResponse mapToResponse(FacturaFiscal entity) {
        FacturaFiscalResponse dto = new FacturaFiscalResponse();
        dto.setFacturaId(entity.getId());
        dto.setVentaId(entity.getVentaId());
        dto.setUuid(entity.getUuid());
        dto.setEstatus(entity.getEstatus());
        dto.setFechaEmision(entity.getFechaEmision());
        dto.setMontoTotal(entity.getMontoTotal());
        dto.setMoneda(entity.getMoneda());
        dto.setUsoCfdi(entity.getUsoCfdi());
        dto.setMetodoPago(entity.getMetodoPago());
        dto.setFormaPago(entity.getFormaPago());
        dto.setRfcReceptor(entity.getRfcReceptor());
        dto.setRazonSocialReceptor(entity.getRazonSocialReceptor());
        dto.setUrlPdf(entity.getUrlPdf());
        dto.setXmlGenerado(entity.getXmlGenerado());
        dto.setMotivoCancelacion(entity.getMotivoCancelacion());
        dto.setAcuseCancelacion(entity.getAcuseCancelacion());
        return dto;
    }
}
