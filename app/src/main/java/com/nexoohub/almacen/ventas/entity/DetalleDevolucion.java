package com.nexoohub.almacen.ventas.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

/**
 * Detalle de los productos devueltos en una devolución.
 * 
 * @author NexooHub Development Team
 * @since 1.0
 */
@Entity
@Table(name = "detalle_devolucion")
public class DetalleDevolucion {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(name = "devolucion_id", insertable = false, updatable = false)
    private Integer devolucionId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "devolucion_id", nullable = false)
    private Devolucion devolucion;
    
    @Column(name = "sku_interno", nullable = false)
    private String skuInterno;
    
    @Column(nullable = false)
    private Integer cantidad;
    
    @Column(name = "precio_unitario", nullable = false)
    private BigDecimal precioUnitario; // Precio al que se vendió originalmente
    
    @Column(nullable = false)
    private BigDecimal subtotal;
    
    @Column(length = 300)
    private String motivoItem; // Motivo específico del item
    
    // ==========================================
    // GETTERS Y SETTERS
    // ==========================================
    
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    
    public Integer getDevolucionId() { return devolucionId; }
    public void setDevolucionId(Integer devolucionId) { this.devolucionId = devolucionId; }
    
    public Devolucion getDevolucion() { return devolucion; }
    public void setDevolucion(Devolucion devolucion) { this.devolucion = devolucion; }
    
    public String getSkuInterno() { return skuInterno; }
    public void setSkuInterno(String skuInterno) { this.skuInterno = skuInterno; }
    
    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
    
    public BigDecimal getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(BigDecimal precioUnitario) { this.precioUnitario = precioUnitario; }
    
    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }
    
    public String getMotivoItem() { return motivoItem; }
    public void setMotivoItem(String motivoItem) { this.motivoItem = motivoItem; }
}
