package com.nexoohub.almacen.finanzas.entity;

import com.nexoohub.almacen.catalogo.entity.Cliente;
import com.nexoohub.almacen.ventas.entity.Venta;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidad que representa el historial de movimientos de crédito de un cliente.
 * 
 * <p>Registra cada cargo (venta a crédito) y abono (pago) que realiza el cliente,
 * permitiendo trazabilidad completa del estado de cuenta.</p>
 * 
 * <p><b>Tipos de movimiento:</b></p>
 * <ul>
 *   <li>CARGO: Venta a crédito que incrementa el saldo utilizado</li>
 *   <li>ABONO: Pago que reduce el saldo utilizado</li>
 *   <li>AJUSTE: Corrección manual del saldo (notas de crédito/débito)</li>
 *   <li>BLOQUEO: Registra cuando se bloquea el crédito</li>
 *   <li>DESBLOQUEO: Registra cuando se reactiva el crédito</li>
 * </ul>
 * 
 * <p><b>Flujo típico:</b></p>
 * <ol>
 *   <li>Cliente compra $1,000 → CARGO de $1,000 (saldo: $1,000)</li>
 *   <li>Cliente paga $500 → ABONO de $500 (saldo: $500)</li>
 *   <li>Cliente compra $800 → CARGO de $800 (saldo: $1,300)</li>
 * </ol>
 * 
 * @author NexooHub Development Team
 * @since 1.2.0
 */
@Entity
@Table(name = "historial_credito")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HistorialCredito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * Cliente propietario del movimiento.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    @NotNull(message = "El cliente es obligatorio")
    private Cliente cliente;

    /**
     * Venta asociada al movimiento (solo para CARGO).
     * Permite rastrear qué venta generó cada cargo.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "venta_id")
    private Venta venta;

    /**
     * Tipo de movimiento.
     * Valores: CARGO, ABONO, AJUSTE, BLOQUEO, DESBLOQUEO
     */
    @Column(name = "tipo_movimiento", nullable = false, length = 20)
    @NotNull(message = "El tipo de movimiento es obligatorio")
    private String tipoMovimiento;

    /**
     * Monto del movimiento.
     * Siempre se almacena como valor positivo, el tipo indica si suma o resta.
     */
    @Column(name = "monto", nullable = false, precision = 12, scale = 2)
    @NotNull(message = "El monto es obligatorio")
    @Positive(message = "El monto debe ser mayor a cero")
    private BigDecimal monto;

    /**
     * Saldo del cliente DESPUÉS de aplicar este movimiento.
     * Permite reconstruir el estado de cuenta en cualquier momento.
     */
    @Column(name = "saldo_resultante", nullable = false, precision = 12, scale = 2)
    @NotNull(message = "El saldo resultante es obligatorio")
    private BigDecimal saldoResultante;

    /**
     * Método de pago utilizado (solo para ABONO).
     * Ejemplos: EFECTIVO, TRANSFERENCIA, CHEQUE, TARJETA
     */
    @Column(name = "metodo_pago", length = 30)
    private String metodoPago;

    /**
     * Folio del comprobante de pago (solo para ABONO).
     * Puede ser número de cheque, referencia de transferencia, etc.
     */
    @Column(name = "folio_comprobante", length = 50)
    private String folioComprobante;

    /**
     * Descripción o concepto del movimiento.
     * Ejemplos: "Venta #1234", "Pago parcial", "Nota de crédito por devolución"
     */
    @Column(name = "concepto", nullable = false, length = 500)
    @NotNull(message = "El concepto es obligatorio")
    private String concepto;

    /**
     * Observaciones adicionales sobre el movimiento.
     */
    @Column(name = "observaciones", length = 1000)
    private String observaciones;

    /**
     * Fecha en que se registró el movimiento.
     */
    @Column(name = "fecha_movimiento", nullable = false, updatable = false)
    private LocalDateTime fechaMovimiento;

    /**
     * Usuario que registró el movimiento.
     * Útil para auditoría.
     */
    @Column(name = "usuario_registro", length = 100)
    private String usuarioRegistro;

    @PrePersist
    protected void prePersist() {
        if (fechaMovimiento == null) {
            fechaMovimiento = LocalDateTime.now();
        }
    }

    /**
     * Crea un movimiento de cargo por una venta a crédito.
     */
    public static HistorialCredito crearCargo(
            Cliente cliente,
            Venta venta,
            BigDecimal monto,
            BigDecimal saldoResultante,
            String usuario) {
        
        HistorialCredito historial = new HistorialCredito();
        historial.setCliente(cliente);
        historial.setVenta(venta);
        historial.setTipoMovimiento("CARGO");
        historial.setMonto(monto);
        historial.setSaldoResultante(saldoResultante);
        historial.setConcepto("Venta a crédito #" + (venta != null ? venta.getId() : "N/A"));
        historial.setUsuarioRegistro(usuario);
        historial.setFechaMovimiento(LocalDateTime.now());
        return historial;
    }

    /**
     * Crea un movimiento de abono por un pago.
     */
    public static HistorialCredito crearAbono(
            Cliente cliente,
            BigDecimal monto,
            BigDecimal saldoResultante,
            String metodoPago,
            String folioComprobante,
            String concepto,
            String usuario) {
        
        HistorialCredito historial = new HistorialCredito();
        historial.setCliente(cliente);
        historial.setTipoMovimiento("ABONO");
        historial.setMonto(monto);
        historial.setSaldoResultante(saldoResultante);
        historial.setMetodoPago(metodoPago);
        historial.setFolioComprobante(folioComprobante);
        historial.setConcepto(concepto != null ? concepto : "Pago recibido");
        historial.setUsuarioRegistro(usuario);
        historial.setFechaMovimiento(LocalDateTime.now());
        return historial;
    }

    /**
     * Crea un movimiento de ajuste manual.
     */
    public static HistorialCredito crearAjuste(
            Cliente cliente,
            BigDecimal monto,
            BigDecimal saldoResultante,
            String concepto,
            String observaciones,
            String usuario) {
        
        HistorialCredito historial = new HistorialCredito();
        historial.setCliente(cliente);
        historial.setTipoMovimiento("AJUSTE");
        historial.setMonto(monto);
        historial.setSaldoResultante(saldoResultante);
        historial.setConcepto(concepto);
        historial.setObservaciones(observaciones);
        historial.setUsuarioRegistro(usuario);
        historial.setFechaMovimiento(LocalDateTime.now());
        return historial;
    }
}
