package com.nexoohub.almacen.analitica.entity;

import com.nexoohub.almacen.catalogo.entity.Cliente;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "prediccion_churn_cliente")
@Data
@NoArgsConstructor
public class PrediccionChurnCliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull(message = "El ID del cliente es obligatorio")
    @Column(name = "cliente_id", insertable = false, updatable = false)
    private Integer clienteId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    @NotNull(message = "El score de riesgo es obligatorio")
    @Min(value = 0, message = "El score mínimo es 0")
    @Max(value = 100, message = "El score máximo es 100")
    @Column(name = "score_riesgo")
    private Integer scoreRiesgo;

    @Column(name = "dias_sin_comprar")
    private Integer diasSinComprar;

    @Column(name = "frecuencia_promedio_dias")
    private Integer frecuenciaPromedioDias;

    @Column(name = "factores_riesgo", length = 500)
    private String factoresRiesgo;

    @Column(name = "fecha_analisis")
    private LocalDateTime fechaAnalisis;

    @Column(name = "fecha_creacion", updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "usuario_creacion", length = 50)
    private String usuarioCreacion;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    @Column(name = "usuario_actualizacion", length = 50)
    private String usuarioActualizacion;

    @PrePersist
    public void prePersist() {
        fechaCreacion = LocalDateTime.now();
        fechaActualizacion = LocalDateTime.now();
        if (fechaAnalisis == null) {
            fechaAnalisis = LocalDateTime.now();
        }
    }

    @PreUpdate
    public void preUpdate() {
        fechaActualizacion = LocalDateTime.now();
        fechaAnalisis = LocalDateTime.now();
    }
}
