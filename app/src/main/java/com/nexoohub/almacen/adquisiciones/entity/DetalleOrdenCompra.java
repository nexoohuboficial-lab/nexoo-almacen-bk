package com.nexoohub.almacen.adquisiciones.entity;

import com.nexoohub.almacen.common.entity.AuditableEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "detalle_orden_compra")
@Getter
@Setter
public class DetalleOrdenCompra extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orden_compra_id", nullable = false)
    private OrdenCompraProveedor ordenCompra;

    @Column(name = "sku_interno", nullable = false, length = 50)
    private String skuInterno;

    @Column(name = "sku_proveedor", length = 50)
    private String skuProveedor;

    @Column(name = "nombre_producto", nullable = false, length = 150)
    private String nombreProducto;

    @Column(nullable = false)
    private Integer cantidad;

    @Column(name = "precio_costo_unitario", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioCostoUnitario;

    @Column(name = "precio_venta_sugerido", precision = 10, scale = 2)
    private BigDecimal precioVentaSugerido;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal subtotal;

    @Column(nullable = false)
    private Boolean activo = true;
}
