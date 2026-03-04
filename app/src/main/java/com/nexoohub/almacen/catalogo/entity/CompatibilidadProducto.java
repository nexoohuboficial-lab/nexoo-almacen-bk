package com.nexoohub.almacen.catalogo.entity;

import com.nexoohub.almacen.common.entity.AuditableEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "compatibilidad_producto")
public class CompatibilidadProducto extends AuditableEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "El SKU es obligatorio")
    @Column(name = "sku_interno")
    private String skuInterno;

    @NotNull(message = "El ID de la moto es obligatorio")
    @Column(name = "moto_id")
    private Integer motoId;

    @Column(name = "anio_inicio")
    private Integer anioInicio;

    @Column(name = "anio_fin")
    private Integer anioFin;

    // Y agrega sus respectivos Getters y Setters abajo:
    public Integer getAnioInicio() { return anioInicio; }
    public void setAnioInicio(Integer anioInicio) { this.anioInicio = anioInicio; }

    public Integer getAnioFin() { return anioFin; }
    public void setAnioFin(Integer anioFin) { this.anioFin = anioFin; }
    // ==========================================
    // GETTERS Y SETTERS
    // ==========================================
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getSkuInterno() { return skuInterno; }
    public void setSkuInterno(String skuInterno) { this.skuInterno = skuInterno; }

    public Integer getMotoId() { return motoId; }
    public void setMotoId(Integer motoId) { this.motoId = motoId; }
}
