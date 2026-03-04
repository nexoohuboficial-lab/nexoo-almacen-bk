package com.nexoohub.almacen.catalogo.entity;
import com.nexoohub.almacen.common.entity.AuditableEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

@Entity
@Table(name = "precio_especial")
public class PrecioEspecial extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @Column(name = "sku_interno")
    private String skuInterno;

    @NotNull
    @Column(name = "tipo_cliente_id")
    private Integer tipoClienteId; // Ej. 2 = Taller Mecánico

    @NotNull
    @Column(name = "precio_fijo")
    private BigDecimal precioFijo; // El precio pactado (Ej. $50.00)

    // Getters y Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getSkuInterno() { return skuInterno; }
    public void setSkuInterno(String skuInterno) { this.skuInterno = skuInterno; }
    public Integer getTipoClienteId() { return tipoClienteId; }
    public void setTipoClienteId(Integer tipoClienteId) { this.tipoClienteId = tipoClienteId; }
    public BigDecimal getPrecioFijo() { return precioFijo; }
    public void setPrecioFijo(BigDecimal precioFijo) { this.precioFijo = precioFijo; }
}
