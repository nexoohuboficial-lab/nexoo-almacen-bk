package com.nexoohub.almacen.inventario.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "codigo_barras_producto")
public class CodigoBarrasProducto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "sku_interno", nullable = false, length = 50)
    private String skuInterno;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sku_interno", insertable = false, updatable = false)
    private ProductoMaestro productoMaestro;

    @Column(nullable = false, unique = true, length = 100)
    private String codigo;

    /** EAN13, QR, UPC, INTERNO */
    @Column(nullable = false, length = 20)
    private String tipo = "EAN13";

    @Column(nullable = false)
    private Boolean activo = true;

    @Column(name = "es_principal", nullable = false)
    private Boolean esPrincipal = false;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
    }

    // ==========================================
    // GETTERS Y SETTERS
    // ==========================================

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getSkuInterno() { return skuInterno; }
    public void setSkuInterno(String skuInterno) { this.skuInterno = skuInterno; }

    public ProductoMaestro getProductoMaestro() { return productoMaestro; }
    public void setProductoMaestro(ProductoMaestro productoMaestro) { this.productoMaestro = productoMaestro; }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }

    public Boolean getEsPrincipal() { return esPrincipal; }
    public void setEsPrincipal(Boolean esPrincipal) { this.esPrincipal = esPrincipal; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
}
