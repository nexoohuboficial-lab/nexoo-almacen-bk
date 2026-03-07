package com.nexoohub.almacen.inventario.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "movimiento_inventario")
public class MovimientoInventario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "sku_interno")
    private String skuInterno;

    @Column(name = "sucursal_id")
    private Integer sucursalId;

    @Column(name = "tipo_movimiento")
    private String tipoMovimiento; // ENTRADA_COMPRA, SALIDA_VENTA, SALIDA_TRASPASO, ENTRADA_TRASPASO

    private Integer cantidad;
    private String comentarios;

    @Column(name = "rastreo_id")
    private String rastreoId; // Para agrupar movimientos de un mismo traspaso

    @Column(name = "fecha_movimiento", updatable = false)
    private LocalDateTime fechaMovimiento;

    @Column(name = "usuario_id")
    private Integer usuarioId;

    @PrePersist
    public void prePersist() { this.fechaMovimiento = LocalDateTime.now(); }

    // Getters y Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getSkuInterno() { return skuInterno; }
    public void setSkuInterno(String skuInterno) { this.skuInterno = skuInterno; }
    public Integer getSucursalId() { return sucursalId; }
    public void setSucursalId(Integer sucursalId) { this.sucursalId = sucursalId; }
    public String getTipoMovimiento() { return tipoMovimiento; }
    public void setTipoMovimiento(String tipoMovimiento) { this.tipoMovimiento = tipoMovimiento; }
    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
    public String getComentarios() { return comentarios; }
    public void setComentarios(String comentarios) { this.comentarios = comentarios; }
    public String getRastreoId() { return rastreoId; }
    public void setRastreoId(String rastreoId) { this.rastreoId = rastreoId; }
    public LocalDateTime getFechaMovimiento() { return fechaMovimiento; }
    public Integer getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Integer usuarioId) { this.usuarioId = usuarioId; }
}
