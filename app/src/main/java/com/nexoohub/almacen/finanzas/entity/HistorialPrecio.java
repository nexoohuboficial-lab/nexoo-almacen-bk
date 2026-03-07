package com.nexoohub.almacen.finanzas.entity;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "historial_precio")
public class HistorialPrecio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "sku_interno")
    private String skuInterno;

    @Column(name = "costo_base")
    private BigDecimal costoBase;

    @Column(name = "precio_ponderado")
    private BigDecimal precioPonderado;

    @Column(name = "precio_final_publico")
    private BigDecimal precioFinalPublico;

    @Column(name = "precio_publico_proveedor")
    private BigDecimal precioPublicoProveedor;

    @Column(name = "precio_anterior")
    private BigDecimal precioAnterior; // Precio público anterior para calcular cambio

    @Column(name = "porcentaje_cambio")
    private BigDecimal porcentajeCambio; // % de cambio respecto al precio anterior

    @Column(name = "razon_cambio", length = 500)
    private String razonCambio; // AJUSTE_MANUAL, CAMBIO_COSTO, ACTUALIZACION_PROVEEDOR, etc.

    @Column(name = "fecha_calculo", updatable = false)
    private LocalDateTime fechaCalculo;

    @Column(name = "usuario_creacion")
    private String usuarioCreacion;

    @PrePersist
    public void prePersist() {
        this.fechaCalculo = LocalDateTime.now();
    }

    // ==========================================
    // GETTERS Y SETTERS
    // ==========================================
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getSkuInterno() { return skuInterno; }
    public void setSkuInterno(String skuInterno) { this.skuInterno = skuInterno; }

    public BigDecimal getCostoBase() { return costoBase; }
    public void setCostoBase(BigDecimal costoBase) { this.costoBase = costoBase; }

    public BigDecimal getPrecioPonderado() { return precioPonderado; }
    public void setPrecioPonderado(BigDecimal precioPonderado) { this.precioPonderado = precioPonderado; }

    public BigDecimal getPrecioFinalPublico() { return precioFinalPublico; }
    public void setPrecioFinalPublico(BigDecimal precioFinalPublico) { this.precioFinalPublico = precioFinalPublico; }

    public BigDecimal getPrecioPublicoProveedor() { return precioPublicoProveedor; }
    public void setPrecioPublicoProveedor(BigDecimal precioPublicoProveedor) { this.precioPublicoProveedor = precioPublicoProveedor; }

    public LocalDateTime getFechaCalculo() { return fechaCalculo; }
    public void setFechaCalculo(LocalDateTime fechaCalculo) { this.fechaCalculo = fechaCalculo; }

    public String getUsuarioCreacion() { return usuarioCreacion; }
    public void setUsuarioCreacion(String usuarioCreacion) { this.usuarioCreacion = usuarioCreacion; }

    public BigDecimal getPrecioAnterior() { return precioAnterior; }
    public void setPrecioAnterior(BigDecimal precioAnterior) { this.precioAnterior = precioAnterior; }

    public BigDecimal getPorcentajeCambio() { return porcentajeCambio; }
    public void setPorcentajeCambio(BigDecimal porcentajeCambio) { this.porcentajeCambio = porcentajeCambio; }

    public String getRazonCambio() { return razonCambio; }
    public void setRazonCambio(String razonCambio) { this.razonCambio = razonCambio; }
}