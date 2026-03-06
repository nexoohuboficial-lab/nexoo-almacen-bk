package com.nexoohub.almacen.inventario.entity;

import com.nexoohub.almacen.catalogo.entity.Categoria;
import com.nexoohub.almacen.catalogo.entity.Proveedor;
import com.nexoohub.almacen.common.entity.AuditableEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "producto_maestro")
public class ProductoMaestro extends AuditableEntity {

    @Id
    @NotBlank(message = "El SKU interno es obligatorio")
    @Size(min = 3, max = 50, message = "El SKU interno debe tener entre 3 y 50 caracteres")
    private String skuInterno;

    private String skuProveedor;

    @NotBlank(message = "El nombre comercial es obligatorio")
    private String nombreComercial;

    private String descripcion;

    @Column(name = "categoria_id", insertable = false, updatable = false)
    private Integer categoriaId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id")
    private Categoria categoria;

    @Column(name = "proveedor_id", insertable = false, updatable = false)
    private Integer proveedorId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proveedor_id")
    private Proveedor proveedor;

    @Pattern(regexp = "^[0-9]{8}$", message = "La clave SAT debe tener exactamente 8 números")
    private String claveSat;

    private Integer stockMinimoGlobal = 2;

    private Boolean activo = true;
    @Column(name = "sensibilidad_precio")
    private String sensibilidadPrecio = "MEDIA";

// ... y sus Getters y Setters correspondientes ...
    public String getSensibilidadPrecio() { return sensibilidadPrecio; }
    public void setSensibilidadPrecio(String sensibilidadPrecio) { 
        this.sensibilidadPrecio = sensibilidadPrecio; 
    }

    // ==========================================
    // GETTERS Y SETTERS
    // ==========================================

    public String getSkuInterno() { return skuInterno; }
    public void setSkuInterno(String skuInterno) { this.skuInterno = skuInterno; }

    public String getSkuProveedor() { return skuProveedor; }
    public void setSkuProveedor(String skuProveedor) { this.skuProveedor = skuProveedor; }

    public String getNombreComercial() { return nombreComercial; }
    public void setNombreComercial(String nombreComercial) { this.nombreComercial = nombreComercial; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public Integer getCategoriaId() { return categoriaId; }
    public void setCategoriaId(Integer categoriaId) { this.categoriaId = categoriaId; }
    public Categoria getCategoria() { return categoria; }
    public void setCategoria(Categoria categoria) { this.categoria = categoria; }

    public Integer getProveedorId() { return proveedorId; }
    public void setProveedorId(Integer proveedorId) { this.proveedorId = proveedorId; }
    public Proveedor getProveedor() { return proveedor; }
    public void setProveedor(Proveedor proveedor) { this.proveedor = proveedor; }

    public String getClaveSat() { return claveSat; }
    public void setClaveSat(String claveSat) { this.claveSat = claveSat; }

    public Integer getStockMinimoGlobal() { return stockMinimoGlobal; }
    public void setStockMinimoGlobal(Integer stockMinimoGlobal) { this.stockMinimoGlobal = stockMinimoGlobal; }

    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }
}