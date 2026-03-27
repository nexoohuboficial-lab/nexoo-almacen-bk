package com.nexoohub.almacen.analitica.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "regla_asociacion_productos")
@Data
public class ReglaAsociacionProductos {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "sku_origen", nullable = false)
    private String skuOrigen;

    @Column(name = "sku_destino", nullable = false)
    private String skuDestino;

    @Column(name = "soporte", nullable = false)
    private Double soporte;

    @Column(name = "confianza", nullable = false)
    private Double confianza;

    @Column(name = "lift", nullable = false)
    private Double lift;

    @Column(name = "creado_en", updatable = false)
    private LocalDateTime creadoEn;

    @PrePersist
    public void prePersist() {
        this.creadoEn = LocalDateTime.now();
    }
}
