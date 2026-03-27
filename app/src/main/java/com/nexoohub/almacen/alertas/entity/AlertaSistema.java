package com.nexoohub.almacen.alertas.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "alerta_sistema")
@Getter
@Setter
@NoArgsConstructor
public class AlertaSistema {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false, length = 30)
    private TipoAlerta tipo;

    @Column(name = "mensaje", nullable = false, length = 500)
    private String mensaje;

    @Column(name = "sucursal_id")
    private Integer sucursalId;

    @Column(name = "usuario_destino_id")
    private Integer usuarioDestinoId;

    @Column(name = "resuelta", nullable = false)
    private boolean resuelta = false;

    @Column(name = "leida", nullable = false)
    private boolean leida = false;

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

    // Constructor de conveniencia para el servicio
    public AlertaSistema(TipoAlerta tipo, String mensaje, Integer sucursalId, Integer usuarioDestinoId) {
        this.tipo = tipo;
        this.mensaje = mensaje;
        this.sucursalId = sucursalId;
        this.usuarioDestinoId = usuarioDestinoId;
    }
}
