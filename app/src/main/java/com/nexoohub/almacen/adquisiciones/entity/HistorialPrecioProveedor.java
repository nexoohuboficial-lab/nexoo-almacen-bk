package com.nexoohub.almacen.adquisiciones.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "historial_precio_proveedor")
public class HistorialPrecioProveedor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "catalogo_id", nullable = false)
    private CatalogoProveedorProducto catalogo;

    @Column(name = "precio_costo_anterior", precision = 12, scale = 2)
    private BigDecimal precioCostoAnterior;

    @Column(name = "precio_costo_nuevo", precision = 12, scale = 2, nullable = false)
    private BigDecimal precioCostoNuevo;

    @Column(name = "variacion_porcentual", precision = 5, scale = 2)
    private BigDecimal variacionPorcentual;

    @Column(name = "motivo", length = 255)
    private String motivo;

    @Column(name = "fecha_actualizacion", nullable = false, updatable = false)
    private LocalDateTime fechaActualizacion;

    @Column(name = "usuario_actualizacion", length = 50)
    private String usuarioActualizacion;

    @PrePersist
    public void prePersist() {
        if (fechaActualizacion == null) {
            fechaActualizacion = LocalDateTime.now();
        }
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public CatalogoProveedorProducto getCatalogo() { return catalogo; }
    public void setCatalogo(CatalogoProveedorProducto catalogo) { this.catalogo = catalogo; }

    public BigDecimal getPrecioCostoAnterior() { return precioCostoAnterior; }
    public void setPrecioCostoAnterior(BigDecimal precioCostoAnterior) { this.precioCostoAnterior = precioCostoAnterior; }

    public BigDecimal getPrecioCostoNuevo() { return precioCostoNuevo; }
    public void setPrecioCostoNuevo(BigDecimal precioCostoNuevo) { this.precioCostoNuevo = precioCostoNuevo; }

    public BigDecimal getVariacionPorcentual() { return variacionPorcentual; }
    public void setVariacionPorcentual(BigDecimal variacionPorcentual) { this.variacionPorcentual = variacionPorcentual; }

    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }

    public LocalDateTime getFechaActualizacion() { return fechaActualizacion; }
    public void setFechaActualizacion(LocalDateTime fechaActualizacion) { this.fechaActualizacion = fechaActualizacion; }

    public String getUsuarioActualizacion() { return usuarioActualizacion; }
    public void setUsuarioActualizacion(String usuarioActualizacion) { this.usuarioActualizacion = usuarioActualizacion; }
}
