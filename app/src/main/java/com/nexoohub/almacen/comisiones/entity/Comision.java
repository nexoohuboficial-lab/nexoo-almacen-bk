package com.nexoohub.almacen.comisiones.entity;

import com.nexoohub.almacen.common.entity.AuditableEntity;
import com.nexoohub.almacen.empleados.entity.Empleado;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;

/**
 * Entidad que representa una comisión calculada para un vendedor en un periodo específico.
 * 
 * @author NexooHub Development Team
 * @since 1.0
 */
@Entity
@Table(name = "comision", indexes = {
    @Index(name = "idx_comision_vendedor", columnList = "vendedor_id"),
    @Index(name = "idx_comision_periodo", columnList = "periodo_anio, periodo_mes"),
    @Index(name = "idx_comision_estado", columnList = "estado")
})
public class Comision extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull(message = "El vendedor es obligatorio")
    @Column(name = "vendedor_id", insertable = false, updatable = false)
    private Integer vendedorId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendedor_id")
    private Empleado vendedor;

    @NotNull(message = "El año del periodo es obligatorio")
    @Column(name = "periodo_anio")
    private Integer periodoAnio;

    @NotNull(message = "El mes del periodo es obligatorio")
    @Column(name = "periodo_mes")
    private Integer periodoMes; // 1-12

    @NotNull(message = "El total de ventas es obligatorio")
    @DecimalMin(value = "0.00", message = "El total de ventas no puede ser negativo")
    @Column(name = "total_ventas", precision = 12, scale = 2)
    private BigDecimal totalVentas = BigDecimal.ZERO;

    @Column(name = "cantidad_ventas")
    private Integer cantidadVentas = 0;

    @NotNull(message = "La comisión base es obligatoria")
    @DecimalMin(value = "0.00", message = "La comisión base no puede ser negativa")
    @Column(name = "comision_base", precision = 10, scale = 2)
    private BigDecimal comisionBase = BigDecimal.ZERO;

    @DecimalMin(value = "0.00", message = "El bono no puede ser negativo")
    @Column(name = "bonos", precision = 10, scale = 2)
    private BigDecimal bonos = BigDecimal.ZERO;

    @DecimalMin(value = "0.00", message = "Los ajustes no pueden ser negativos en valor absoluto")
    @Column(name = "ajustes", precision = 10, scale = 2)
    private BigDecimal ajustes = BigDecimal.ZERO; // Puede ser positivo o negativo

    @NotNull(message = "El total de comisión es obligatorio")
    @DecimalMin(value = "0.00", message = "El total de comisión no puede ser negativo")
    @Column(name = "total_comision", precision = 10, scale = 2)
    private BigDecimal totalComision = BigDecimal.ZERO;

    @NotNull(message = "El estado es obligatorio")
    @Column(name = "estado", length = 20)
    private String estado = "PENDIENTE"; // "PENDIENTE", "APROBADA", "PAGADA", "RECHAZADA"

    @Column(name = "fecha_aprobacion")
    private LocalDate fechaAprobacion;

    @Column(name = "fecha_pago")
    private LocalDate fechaPago;

    @Column(name = "usuario_aprobador", length = 100)
    private String usuarioAprobador;

    @Column(name = "notas", length = 1000)
    private String notas;

    // ==========================================
    // MÉTODOS DE NEGOCIO
    // ==========================================
    
    /**
     * Calcula el total de la comisión sumando base + bonos + ajustes
     */
    public void calcularTotal() {
        this.totalComision = this.comisionBase
                .add(this.bonos != null ? this.bonos : BigDecimal.ZERO)
                .add(this.ajustes != null ? this.ajustes : BigDecimal.ZERO);
        
        // Asegurar que nunca sea negativa
        if (this.totalComision.compareTo(BigDecimal.ZERO) < 0) {
            this.totalComision = BigDecimal.ZERO;
        }
    }

    /**
     * Obtiene el periodo en formato YearMonth
     */
    public YearMonth getPeriodo() {
        return YearMonth.of(periodoAnio, periodoMes);
    }

    /**
     * Establece el periodo desde un YearMonth
     */
    public void setPeriodo(YearMonth periodo) {
        this.periodoAnio = periodo.getYear();
        this.periodoMes = periodo.getMonthValue();
    }

    // ==========================================
    // GETTERS Y SETTERS
    // ==========================================
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getVendedorId() { return vendedorId; }
    public void setVendedorId(Integer vendedorId) { this.vendedorId = vendedorId; }

    public Empleado getVendedor() { return vendedor; }
    public void setVendedor(Empleado vendedor) { this.vendedor = vendedor; }

    public Integer getPeriodoAnio() { return periodoAnio; }
    public void setPeriodoAnio(Integer periodoAnio) { this.periodoAnio = periodoAnio; }

    public Integer getPeriodoMes() { return periodoMes; }
    public void setPeriodoMes(Integer periodoMes) { this.periodoMes = periodoMes; }

    public BigDecimal getTotalVentas() { return totalVentas; }
    public void setTotalVentas(BigDecimal totalVentas) { this.totalVentas = totalVentas; }

    public Integer getCantidadVentas() { return cantidadVentas; }
    public void setCantidadVentas(Integer cantidadVentas) { this.cantidadVentas = cantidadVentas; }

    public BigDecimal getComisionBase() { return comisionBase; }
    public void setComisionBase(BigDecimal comisionBase) { this.comisionBase = comisionBase; }

    public BigDecimal getBonos() { return bonos; }
    public void setBonos(BigDecimal bonos) { this.bonos = bonos; }

    public BigDecimal getAjustes() { return ajustes; }
    public void setAjustes(BigDecimal ajustes) { this.ajustes = ajustes; }

    public BigDecimal getTotalComision() { return totalComision; }
    public void setTotalComision(BigDecimal totalComision) { this.totalComision = totalComision; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public LocalDate getFechaAprobacion() { return fechaAprobacion; }
    public void setFechaAprobacion(LocalDate fechaAprobacion) { this.fechaAprobacion = fechaAprobacion; }

    public LocalDate getFechaPago() { return fechaPago; }
    public void setFechaPago(LocalDate fechaPago) { this.fechaPago = fechaPago; }

    public String getUsuarioAprobador() { return usuarioAprobador; }
    public void setUsuarioAprobador(String usuarioAprobador) { this.usuarioAprobador = usuarioAprobador; }

    public String getNotas() { return notas; }
    public void setNotas(String notas) { this.notas = notas; }
}
