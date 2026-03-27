package com.nexoohub.almacen.comisiones.entity;

import com.nexoohub.almacen.common.entity.AuditableEntity;
import com.nexoohub.almacen.empleados.entity.Empleado;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "meta_ventas_empleado")
@Getter
@Setter
public class MetaVentasEmpleado extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empleado_id", nullable = false)
    private Empleado empleado;

    @Column(nullable = false)
    private Integer mes;

    @Column(nullable = false)
    private Integer anio;

    @Column(name = "monto_meta", nullable = false, precision = 12, scale = 2)
    private BigDecimal montoMeta;

    @Column(name = "monto_ventas_actual", precision = 12, scale = 2)
    private BigDecimal montoVentasActual = BigDecimal.ZERO;

    @Column(name = "fecha_asignacion")
    private LocalDateTime fechaAsignacion = LocalDateTime.now();

    @Column(nullable = false)
    private Boolean activo = true;

    // Métodos útiles de negocio
    public BigDecimal getPorcentajeLogro() {
        if (montoMeta == null || montoMeta.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        if (montoVentasActual == null) return BigDecimal.ZERO;
        
        // (montoVentas / montoMeta) * 100
        return montoVentasActual.multiply(new BigDecimal("100"))
                .divide(montoMeta, 2, java.math.RoundingMode.HALF_UP);
    }
}
