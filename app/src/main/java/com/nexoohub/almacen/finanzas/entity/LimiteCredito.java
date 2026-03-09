package com.nexoohub.almacen.finanzas.entity;

import com.nexoohub.almacen.catalogo.entity.Cliente;
import com.nexoohub.almacen.common.entity.AuditableEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Entidad que representa el límite de crédito configurado para un cliente.
 * 
 * <p>Permite establecer un monto máximo de crédito que el cliente puede usar,
 * controlar automáticamente bloqueos cuando se excede el límite, y gestionar
 * políticas de plazo de pago.</p>
 * 
 * <p><b>Estados del crédito:</b></p>
 * <ul>
 *   <li>ACTIVO: Cliente puede usar crédito dentro de su límite</li>
 *   <li>BLOQUEADO: Excedió límite o tiene morosidad</li>
 *   <li>SUSPENDIDO: Suspensión temporal por política comercial</li>
 *   <li>INACTIVO: Cliente sin acceso a crédito</li>
 * </ul>
 * 
 * <p><b>Cálculo del crédito disponible:</b></p>
 * <pre>
 * Crédito Disponible = Límite Autorizado - Saldo Utilizado
 * </pre>
 * 
 * <p><b>Bloqueo automático:</b> El sistema bloquea el crédito cuando:</p>
 * <ul>
 *   <li>Saldo utilizado excede el límite autorizado</li>
 *   <li>Cliente tiene facturas vencidas mayores al plazo permitido</li>
 *   <li>Se supera el número máximo de facturas vencidas</li>
 * </ul>
 * 
 * @author NexooHub Development Team
 * @since 1.2.0
 */
@Entity
@Table(name = "limite_credito")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class LimiteCredito extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * Cliente al que pertenece este límite de crédito.
     * Relación OneToOne ya que cada cliente tiene un único límite de crédito.
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false, unique = true)
    @NotNull(message = "El cliente es obligatorio")
    private Cliente cliente;

    /**
     * Monto máximo de crédito autorizado para este cliente.
     * El cliente no puede tener un saldo utilizado mayor a este límite.
     */
    @Column(name = "limite_autorizado", nullable = false, precision = 12, scale = 2)
    @NotNull(message = "El límite autorizado es obligatorio")
    @PositiveOrZero(message = "El límite debe ser mayor o igual a cero")
    private BigDecimal limiteAutorizado;

    /**
     * Saldo actualmente utilizado por el cliente.
     * Se incrementa con nuevas ventas a crédito y se reduce con pagos.
     */
    @Column(name = "saldo_utilizado", nullable = false, precision = 12, scale = 2)
    @PositiveOrZero(message = "El saldo no puede ser negativo")
    private BigDecimal saldoUtilizado = BigDecimal.ZERO;

    /**
     * Estado actual del crédito del cliente.
     * Valores: ACTIVO, BLOQUEADO, SUSPENDIDO, INACTIVO
     */
    @Column(name = "estado", nullable = false, length = 20)
    @NotNull(message = "El estado es obligatorio")
    private String estado = "ACTIVO";

    /**
     * Plazo de pago en días que se otorga al cliente.
     * Ejemplo: 30 días significa que las facturas vencen 30 días después de la compra.
     */
    @Column(name = "plazo_pago_dias")
    private Integer plazoPagoDias = 30;

    /**
     * Número máximo de facturas vencidas permitidas antes de bloquear el crédito.
     */
    @Column(name = "max_facturas_vencidas")
    private Integer maxFacturasVencidas = 3;

    /**
     * Indica si el cliente puede exceder temporalmente su límite de crédito.
     * Útil para clientes preferenciales o situaciones excepcionales.
     */
    @Column(name = "permite_sobregiro")
    private Boolean permiteSobregiro = false;

    /**
     * Monto máximo de sobregiro permitido si permite_sobregiro = true.
     */
    @Column(name = "monto_sobregiro", precision = 12, scale = 2)
    @PositiveOrZero(message = "El monto de sobregiro no puede ser negativo")
    private BigDecimal montoSobregiro = BigDecimal.ZERO;

    /**
     * Fecha de última revisión del límite de crédito.
     * Se actualiza cada vez que se modifica el límite autorizado.
     */
    @Column(name = "fecha_revision")
    private LocalDate fechaRevision;

    /**
     * Observaciones sobre el crédito del cliente.
     * Puede incluir razones de bloqueo, condiciones especiales, etc.
     */
    @Column(name = "observaciones", length = 1000)
    private String observaciones;

    /**
     * Calcula el crédito disponible del cliente.
     * 
     * @return Límite autorizado - Saldo utilizado
     */
    public BigDecimal getCreditoDisponible() {
        return limiteAutorizado.subtract(saldoUtilizado);
    }

    /**
     * Calcula el porcentaje de utilización del crédito.
     * 
     * @return Porcentaje (0-100) de crédito utilizado
     */
    public BigDecimal getPorcentajeUtilizacion() {
        if (limiteAutorizado.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return saldoUtilizado.multiply(BigDecimal.valueOf(100))
                .divide(limiteAutorizado, 2, java.math.RoundingMode.HALF_UP);
    }

    /**
     * Verifica si el cliente tiene crédito disponible para un monto específico.
     * 
     * @param monto Monto a verificar
     * @return true si hay crédito suficiente (considerando sobregiro si aplica)
     */
    public boolean tieneCreditoDisponible(BigDecimal monto) {
        if (!"ACTIVO".equals(estado)) {
            return false;
        }
        
        BigDecimal limiteTotal = limiteAutorizado;
        if (Boolean.TRUE.equals(permiteSobregiro)) {
            limiteTotal = limiteTotal.add(montoSobregiro);
        }
        
        BigDecimal saldoProyectado = saldoUtilizado.add(monto);
        return saldoProyectado.compareTo(limiteTotal) <= 0;
    }

    /**
     * Marca el crédito como bloqueado.
     * 
     * @param motivo Razón del bloqueo
     */
    public void bloquear(String motivo) {
        this.estado = "BLOQUEADO";
        this.observaciones = motivo;
        this.fechaRevision = LocalDate.now();
    }

    /**
     * Activa el crédito del cliente.
     */
    public void activar() {
        this.estado = "ACTIVO";
        this.fechaRevision = LocalDate.now();
    }

    /**
     * Suspende temporalmente el crédito.
     * 
     * @param motivo Razón de la suspensión
     */
    public void suspender(String motivo) {
        this.estado = "SUSPENDIDO";
        this.observaciones = motivo;
        this.fechaRevision = LocalDate.now();
    }

    @PrePersist
    @PreUpdate
    protected void validarDatos() {
        if (fechaRevision == null) {
            fechaRevision = LocalDate.now();
        }
        
        // Validar que saldo no exceda límite (excepto con sobregiro)
        BigDecimal limiteTotal = limiteAutorizado;
        if (Boolean.TRUE.equals(permiteSobregiro)) {
            limiteTotal = limiteTotal.add(montoSobregiro);
        }
        
        if (saldoUtilizado.compareTo(limiteTotal) > 0) {
            this.estado = "BLOQUEADO";
            this.observaciones = "Límite de crédito excedido";
        }
    }
}
