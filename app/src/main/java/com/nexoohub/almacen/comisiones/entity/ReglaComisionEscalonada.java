package com.nexoohub.almacen.comisiones.entity;

import com.nexoohub.almacen.common.entity.AuditableEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "regla_comision_escalonada")
@Getter
@Setter
public class ReglaComisionEscalonada extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(name = "porcentaje_minimo_logro", nullable = false, precision = 5, scale = 2)
    private BigDecimal porcentajeMinimoLogro;

    @Column(name = "porcentaje_maximo_logro", nullable = false, precision = 5, scale = 2)
    private BigDecimal porcentajeMaximoLogro;

    @Column(name = "porcentaje_comision", nullable = false, precision = 5, scale = 2)
    private BigDecimal porcentajeComision;

    @Column(nullable = false)
    private Boolean activo = true;
}
