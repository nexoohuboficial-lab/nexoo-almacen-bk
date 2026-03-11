package com.nexoohub.almacen.fidelidad.entity;

import com.nexoohub.almacen.common.entity.AuditableEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

/**
 * Entidad que representa el saldo de puntos acumulados de un cliente.
 * 
 * <p>Un cliente puede tener puntos acumulados que puede canjear
 * por descuentos o productos.</p>
 * 
 * <p><b>Reglas de negocio:</b></p>
 * <ul>
 *   <li>1 punto = $10 MXN de compra</li>
 *   <li>100 puntos = $10 MXN de descuento</li>
 *   <li>Los puntos no caducan</li>
 * </ul>
 * 
 * @author NexooHub Development Team
 * @since 1.0
 */
@Entity
@Table(name = "programa_fidelidad")
public class ProgramaFidelidad extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull(message = "El ID del cliente es obligatorio")
    @Column(name = "cliente_id", nullable = false, unique = true)
    private Integer clienteId;

    @NotNull
    @Column(name = "puntos_acumulados", nullable = false)
    private Integer puntosAcumulados = 0;

    @NotNull
    @Column(name = "total_compras", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalCompras = BigDecimal.ZERO;

    @NotNull
    @Column(name = "total_canjeado", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalCanjeado = BigDecimal.ZERO;

    @NotNull
    @Column(name = "activo", nullable = false)
    private Boolean activo = true;

    // Getters y Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getClienteId() {
        return clienteId;
    }

    public void setClienteId(Integer clienteId) {
        this.clienteId = clienteId;
    }

    public Integer getPuntosAcumulados() {
        return puntosAcumulados;
    }

    public void setPuntosAcumulados(Integer puntosAcumulados) {
        this.puntosAcumulados = puntosAcumulados;
    }

    public BigDecimal getTotalCompras() {
        return totalCompras;
    }

    public void setTotalCompras(BigDecimal totalCompras) {
        this.totalCompras = totalCompras;
    }

    public BigDecimal getTotalCanjeado() {
        return totalCanjeado;
    }

    public void setTotalCanjeado(BigDecimal totalCanjeado) {
        this.totalCanjeado = totalCanjeado;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }
}
