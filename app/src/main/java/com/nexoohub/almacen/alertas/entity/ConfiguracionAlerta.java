package com.nexoohub.almacen.alertas.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "configuracion_alerta")
@Getter
@Setter
@NoArgsConstructor
public class ConfiguracionAlerta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "sucursal_id", nullable = false, unique = true)
    private Integer sucursalId;

    /** Unidades mínimas en inventario antes de alertar */
    @Column(name = "stock_minimo", nullable = false)
    private int stockMinimo = 5;

    /** Días de antigüedad de una CxC para considerarla vencida */
    @Column(name = "dias_vencimiento_cxc", nullable = false)
    private int diasVencimientoCxC = 30;

    /** Porcentaje de avance de meta mensual por debajo del cual se alerta */
    @Column(name = "porcentaje_meta_alerta", nullable = false)
    private int porcentajeMetaAlerta = 60;

    @Column(name = "activo", nullable = false)
    private boolean activo = true;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    @Column(name = "usuario_creacion", length = 50)
    private String usuarioCreacion;

    @Column(name = "usuario_actualizacion", length = 50)
    private String usuarioActualizacion;

    @PrePersist
    protected void onCreate() {
        this.fechaCreacion = LocalDateTime.now();
        this.fechaActualizacion = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.fechaActualizacion = LocalDateTime.now();
    }
}
