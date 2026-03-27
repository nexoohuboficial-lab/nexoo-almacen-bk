package com.nexoohub.almacen.adquisiciones.entity;

import com.nexoohub.almacen.catalogo.entity.Proveedor;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "sesion_carrito_compra", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"usuario_id", "catalogo_id"})
})
@Getter
@Setter
public class SesionCarritoCompra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "usuario_id", nullable = false)
    private Integer usuarioId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "catalogo_id", nullable = false)
    private CatalogoProveedorProducto catalogo;

    @Column(name = "sku_interno", nullable = false, length = 50)
    private String skuInterno;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proveedor_id", nullable = false)
    private Proveedor proveedor;

    @Column(nullable = false)
    private Integer cantidad;

    @Column(name = "fecha_agregado", nullable = false)
    private LocalDateTime fechaAgregado;

    @PrePersist
    public void prePersist() {
        if (fechaAgregado == null) {
            fechaAgregado = LocalDateTime.now();
        }
    }
}
