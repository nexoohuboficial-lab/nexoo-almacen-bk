package com.nexoohub.almacen.analitica.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "reporte_rendimiento_empleado")
@Getter
@Setter
public class ReporteRendimientoEmpleado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "empleado_id", nullable = false)
    private Integer empleadoId;

    @Column(name = "mes", nullable = false)
    private Integer mes;

    @Column(name = "anio", nullable = false)
    private Integer anio;

    // ── KPIs de Ventas ─────────────────────────────────────────────
    @Column(name = "total_ventas")
    private Integer totalVentas = 0;

    @Column(name = "monto_total_ventas")
    private BigDecimal montoTotalVentas = BigDecimal.ZERO;

    @Column(name = "ticket_promedio")
    private BigDecimal ticketPromedio = BigDecimal.ZERO;

    // ── KPIs de Conversión (Cotizaciones) ──────────────────────────
    @Column(name = "total_cotizaciones")
    private Integer totalCotizaciones = 0;

    @Column(name = "cotizaciones_convertidas")
    private Integer cotizacionesConvertidas = 0;

    @Column(name = "tasa_conversion")
    private BigDecimal tasaConversion = BigDecimal.ZERO;

    // ── KPIs de Calidad (Devoluciones) ─────────────────────────────
    @Column(name = "total_devoluciones")
    private Integer totalDevoluciones = 0;

    @Column(name = "monto_devoluciones")
    private BigDecimal montoDevoluciones = BigDecimal.ZERO;

    @Column(name = "tasa_devolucion")
    private BigDecimal tasaDevolucion = BigDecimal.ZERO;

    // ── KPIs de Productividad ───────────────────────────────────────
    @Column(name = "hora_pico")
    private Integer horaPico;  // 0-23

    // ── Auditoría ──────────────────────────────────────────────────
    @Column(name = "fecha_calculo")
    private LocalDateTime fechaCalculo;

    @Column(name = "fecha_creacion", updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    @PrePersist
    public void prePersist() {
        this.fechaCreacion = LocalDateTime.now();
        this.fechaActualizacion = LocalDateTime.now();
        this.fechaCalculo = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.fechaActualizacion = LocalDateTime.now();
        this.fechaCalculo = LocalDateTime.now();
    }
}
