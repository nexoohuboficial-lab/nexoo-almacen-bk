package com.nexoohub.almacen.common.exception;

import java.math.BigDecimal;

/**
 * Excepción lanzada cuando un cliente no tiene crédito suficiente para completar una venta.
 * 
 * <p>Se utiliza en operaciones de:</p>
 * <ul>
 *   <li>Validación de crédito antes de ventas</li>
 *   <li>Procesamiento de ventas a crédito</li>
 *   <li>Registro de cargos en cuenta de cliente</li>
 * </ul>
 * 
 * <p><b>HTTP Status recomendado:</b> 409 CONFLICT</p>
 * 
 * @author NexooHub Development Team
 * @since 1.0
 */
public class CreditoInsuficienteException extends BusinessException {

    private final Integer clienteId;
    private final BigDecimal creditoDisponible;
    private final BigDecimal montoSolicitado;
    private final String codigoValidacion;

    /**
     * Constructor con mensaje simple.
     * 
     * @param message Mensaje descriptivo del error de crédito
     */
    public CreditoInsuficienteException(String message) {
        super(message, "CREDITO_INSUFICIENTE");
        this.clienteId = null;
        this.creditoDisponible = null;
        this.montoSolicitado = null;
        this.codigoValidacion = null;
    }

    /**
     * Constructor completo con detalles del cliente y crédito.
     * 
     * @param clienteId ID del cliente
     * @param creditoDisponible Crédito disponible actual
     * @param montoSolicitado Monto que se intentó cargar
     */
    public CreditoInsuficienteException(Integer clienteId, BigDecimal creditoDisponible, BigDecimal montoSolicitado) {
        super(String.format("Crédito insuficiente para cliente %d. Disponible: $%s, Solicitado: $%s", 
              clienteId, creditoDisponible, montoSolicitado), "CREDITO_INSUFICIENTE");
        this.clienteId = clienteId;
        this.creditoDisponible = creditoDisponible;
        this.montoSolicitado = montoSolicitado;
        this.codigoValidacion = "LIMITE_EXCEDIDO";
    }

    /**
     * Constructor con código de validación específico.
     * 
     * @param clienteId ID del cliente
     * @param mensaje Mensaje descriptivo del error
     * @param codigoValidacion Código de estado (BLOQUEADO, SIN_CREDITO, LIMITE_EXCEDIDO)
     */
    public CreditoInsuficienteException(Integer clienteId, String mensaje, String codigoValidacion) {
        super(mensaje, "CREDITO_INSUFICIENTE");
        this.clienteId = clienteId;
        this.creditoDisponible = null;
        this.montoSolicitado = null;
        this.codigoValidacion = codigoValidacion;
    }

    /**
     * Constructor completo con todos los detalles.
     * 
     * @param clienteId ID del cliente
     * @param creditoDisponible Crédito disponible actual
     * @param montoSolicitado Monto que se intentó cargar
     * @param mensaje Mensaje personalizado
     * @param codigoValidacion Código de estado específico
     */
    public CreditoInsuficienteException(
            Integer clienteId, 
            BigDecimal creditoDisponible, 
            BigDecimal montoSolicitado,
            String mensaje,
            String codigoValidacion) {
        super(mensaje, "CREDITO_INSUFICIENTE");
        this.clienteId = clienteId;
        this.creditoDisponible = creditoDisponible;
        this.montoSolicitado = montoSolicitado;
        this.codigoValidacion = codigoValidacion;
    }

    // Getters
    public Integer getClienteId() {
        return clienteId;
    }

    public BigDecimal getCreditoDisponible() {
        return creditoDisponible;
    }

    public BigDecimal getMontoSolicitado() {
        return montoSolicitado;
    }

    public String getCodigoValidacion() {
        return codigoValidacion;
    }
}
