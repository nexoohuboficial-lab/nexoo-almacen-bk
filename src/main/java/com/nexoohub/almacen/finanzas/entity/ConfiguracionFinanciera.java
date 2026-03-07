package com.nexoohub.almacen.finanzas.entity;
import com.nexoohub.almacen.common.entity.AuditableEntity;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "configuracion_financiera")
public class ConfiguracionFinanciera extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "gastos_fijos_mensuales")
    private BigDecimal gastosFijosMensuales;

    @Column(name = "meta_ventas_mensual")
    private BigDecimal metaVentasMensual;

    @Column(name = "margen_ganancia_base")
    private BigDecimal margenGananciaBase;

    @Column(name = "comision_tarjeta")
    private BigDecimal comisionTarjeta;

    private BigDecimal iva;

    // ==========================================
    // GETTERS Y SETTERS
    // ==========================================
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public BigDecimal getGastosFijosMensuales() { return gastosFijosMensuales; }
    public void setGastosFijosMensuales(BigDecimal gastosFijosMensuales) { this.gastosFijosMensuales = gastosFijosMensuales; }

    public BigDecimal getMetaVentasMensual() { return metaVentasMensual; }
    public void setMetaVentasMensual(BigDecimal metaVentasMensual) { this.metaVentasMensual = metaVentasMensual; }

    public BigDecimal getMargenGananciaBase() { return margenGananciaBase; }
    public void setMargenGananciaBase(BigDecimal margenGananciaBase) { this.margenGananciaBase = margenGananciaBase; }

    public BigDecimal getComisionTarjeta() { return comisionTarjeta; }
    public void setComisionTarjeta(BigDecimal comisionTarjeta) { this.comisionTarjeta = comisionTarjeta; }

    public BigDecimal getIva() { return iva; }
    public void setIva(BigDecimal iva) { this.iva = iva; }
}