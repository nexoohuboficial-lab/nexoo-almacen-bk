package com.nexoohub.almacen.pos.service;

import com.nexoohub.almacen.common.exception.ResourceNotFoundException;
import com.nexoohub.almacen.pos.dto.CancelacionPagoRequest;
import com.nexoohub.almacen.pos.dto.PagoTarjetaRequest;
import com.nexoohub.almacen.pos.dto.TransaccionBancariaResponse;
import com.nexoohub.almacen.pos.entity.LogTransaccionBancaria;
import com.nexoohub.almacen.pos.repository.LogTransaccionBancariaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Servicio para integrar pagos por terminal bancaria (PinPad).
 *
 * <p>En esta versión simula la comunicación con la terminal física,
 * retornando siempre una aprobación automática después de registrar el log.</p>
 *
 * @author NexooHub Development Team
 * @since 1.0 — POS-02
 */
@Service
public class TerminalBancariaService {

    private static final Logger log = LoggerFactory.getLogger(TerminalBancariaService.class);

    private final LogTransaccionBancariaRepository repository;

    public TerminalBancariaService(LogTransaccionBancariaRepository repository) {
        this.repository = repository;
    }

    /**
     * Inicia un cobro en la terminal bancaria enviando el monto.
     */
    @Transactional
    public TransaccionBancariaResponse procesarPagoTarjeta(PagoTarjetaRequest request) {
        log.info("Procesando pago con tarjeta: ref={}, monto={}, terminal={}", 
            request.getReferenciaVenta(), request.getMonto(), request.getTerminalId());

        LogTransaccionBancaria logTx = new LogTransaccionBancaria();
        logTx.setReferenciaVenta(request.getReferenciaVenta());
        logTx.setMonto(request.getMonto());
        logTx.setTipoOperacion("VENTA");
        logTx.setTerminalId(request.getTerminalId());
        
        // Simulación de interacción con PinPad...
        logTx.setEstatus("APROBADO");
        logTx.setAutorizacionBanco(generarAutorizacionSimulada());
        logTx.setTarjetaTerminacion("1234");
        logTx.setMarcaTarjeta("VISA");
        logTx.setMensajeRespuesta("Aprobado automáticamente (Simulador)");
        logTx.setXmlRequest("<venta><monto>" + request.getMonto() + "</monto></venta>");
        logTx.setXmlResponse("<respuesta><estatus>APROBADO</estatus></respuesta>");

        LogTransaccionBancaria guardado = repository.save(logTx);
        log.info("Pago aprobado: ref={}, autorizacion={}", guardado.getReferenciaVenta(), guardado.getAutorizacionBanco());
        
        return mapToResponse(guardado);
    }

    /**
     * Inicia una cancelación (reverso) de un cobro previo en la terminal.
     */
    @Transactional
    public TransaccionBancariaResponse cancelarPago(CancelacionPagoRequest request) {
        log.info("Cancelando pago en terminal: ref={}, terminal={}", 
            request.getReferenciaVenta(), request.getTerminalId());

        LogTransaccionBancaria txOriginal = repository.findByReferenciaVenta(request.getReferenciaVenta())
            .orElseThrow(() -> new ResourceNotFoundException("No se encontró transacción para la referencia: " + request.getReferenciaVenta()));

        if (!"APROBADO".equals(txOriginal.getEstatus())) {
            throw new IllegalStateException("Solo se pueden cancelar transacciones APROBADAS.");
        }

        LogTransaccionBancaria logTx = new LogTransaccionBancaria();
        logTx.setReferenciaVenta(request.getReferenciaVenta());
        logTx.setMonto(txOriginal.getMonto());
        logTx.setTipoOperacion("CANCELACION");
        logTx.setTerminalId(request.getTerminalId());
        
        // Simulación de cancelación en PinPad
        logTx.setEstatus("CANCELADO");
        logTx.setAutorizacionBanco(txOriginal.getAutorizacionBanco());
        logTx.setMensajeRespuesta("Cancelado correctamente (Simulador)");
        
        // El original también se marca como cancelado
        txOriginal.setEstatus("CANCELADO");
        txOriginal.setMensajeRespuesta("Cancelado por nueva operación de reverso");

        repository.save(txOriginal);
        LogTransaccionBancaria guardado = repository.save(logTx);
        log.info("Pago cancelado: ref={}", request.getReferenciaVenta());

        return mapToResponse(guardado);
    }

    /**
     * Consulta el estatus de una transacción específica por su referencia.
     */
    @Transactional(readOnly = true)
    public TransaccionBancariaResponse consultarEstatus(String referenciaVenta) {
        log.info("Consultando estatus de transacción: ref={}", referenciaVenta);
        
        LogTransaccionBancaria tx = repository.findByReferenciaVenta(referenciaVenta)
            .orElseThrow(() -> new ResourceNotFoundException("Transacción no encontrada: " + referenciaVenta));
            
        return mapToResponse(tx);
    }

    private String generarAutorizacionSimulada() {
        return UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }

    private TransaccionBancariaResponse mapToResponse(LogTransaccionBancaria entity) {
        TransaccionBancariaResponse dto = new TransaccionBancariaResponse();
        dto.setReferenciaVenta(entity.getReferenciaVenta());
        dto.setMonto(entity.getMonto());
        dto.setTipoOperacion(entity.getTipoOperacion());
        dto.setEstatus(entity.getEstatus());
        dto.setAutorizacionBanco(entity.getAutorizacionBanco());
        dto.setTerminalId(entity.getTerminalId());
        dto.setTarjetaTerminacion(entity.getTarjetaTerminacion());
        dto.setMarcaTarjeta(entity.getMarcaTarjeta());
        dto.setMensajeRespuesta(entity.getMensajeRespuesta());
        dto.setFechaTransaccion(entity.getFechaTransaccion());
        return dto;
    }
}
