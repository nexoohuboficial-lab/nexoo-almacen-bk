package com.nexoohub.almacen.finanzas.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO de respuesta con información del límite de crédito de un cliente.
 * 
 * @author NexooHub Development Team
 * @since 1.2.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LimiteCreditoResponseDTO {

    /**
     * ID del límite de crédito.
     */
    private Integer id;

    /**
     * Información básica del cliente.
     */
    private ClienteBasicoDTO cliente;

    /**
     * Monto máximo autorizado.
     */
    private BigDecimal limiteAutorizado;

    /**
     * Saldo actualmente utilizado.
     */
    private BigDecimal saldoUtilizado;

    /**
     * Crédito disponible (calculado: límite - saldo).
     */
    private BigDecimal creditoDisponible;

    /**
     * Porcentaje de utilización del crédito.
     */
    private BigDecimal porcentajeUtilizacion;

    /**
     * Estado del crédito (ACTIVO, BLOQUEADO, SUSPENDIDO, INACTIVO).
     */
    private String estado;

    /**
     * Plazo de pago en días.
     */
    private Integer plazoPagoDias;

    /**
     * Máximo de facturas vencidas permitidas.
     */
    private Integer maxFacturasVencidas;

    /**
     * Indica si permite sobregiro.
     */
    private Boolean permiteSobregiro;

    /**
     * Monto de sobregiro autorizado.
     */
    private BigDecimal montoSobregiro;

    /**
     * Fecha de última revisión.
     */
    private LocalDate fechaRevision;

    /**
     * Observaciones.
     */
    private String observaciones;

    /**
     * DTO con datos básicos del cliente.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ClienteBasicoDTO {
        private Integer id;
        private String nombre;
        private String rfc;
        private String telefono;
        private String email;
    }
}
