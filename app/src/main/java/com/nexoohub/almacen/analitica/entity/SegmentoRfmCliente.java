package com.nexoohub.almacen.analitica.entity;

import com.nexoohub.almacen.catalogo.entity.Cliente;
import com.nexoohub.almacen.common.entity.AuditableEntity;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "segmento_rfm_cliente")
public class SegmentoRfmCliente extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "cliente_id", insertable = false, updatable = false)
    private Integer clienteId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    @Column(name = "recencia_dias")
    private Integer recenciaDias;

    @Column(name = "frecuencia_compras")
    private Integer frecuenciaCompras;

    @Column(name = "monto_gastado")
    private BigDecimal montoGastado;

    @Column(name = "score_r")
    private Integer scoreR;

    @Column(name = "score_f")
    private Integer scoreF;

    @Column(name = "score_m")
    private Integer scoreM;

    @Column(name = "segmento")
    private String segmento;

    @Column(name = "fecha_calculo")
    private LocalDateTime fechaCalculo;

    @PrePersist
    public void prePersist() {
        if (fechaCalculo == null) {
            fechaCalculo = LocalDateTime.now();
        }
    }

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

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
        if (cliente != null) {
            this.clienteId = cliente.getId();
        }
    }

    public Integer getRecenciaDias() {
        return recenciaDias;
    }

    public void setRecenciaDias(Integer recenciaDias) {
        this.recenciaDias = recenciaDias;
    }

    public Integer getFrecuenciaCompras() {
        return frecuenciaCompras;
    }

    public void setFrecuenciaCompras(Integer frecuenciaCompras) {
        this.frecuenciaCompras = frecuenciaCompras;
    }

    public BigDecimal getMontoGastado() {
        return montoGastado;
    }

    public void setMontoGastado(BigDecimal montoGastado) {
        this.montoGastado = montoGastado;
    }

    public Integer getScoreR() {
        return scoreR;
    }

    public void setScoreR(Integer scoreR) {
        this.scoreR = scoreR;
    }

    public Integer getScoreF() {
        return scoreF;
    }

    public void setScoreF(Integer scoreF) {
        this.scoreF = scoreF;
    }

    public Integer getScoreM() {
        return scoreM;
    }

    public void setScoreM(Integer scoreM) {
        this.scoreM = scoreM;
    }

    public String getSegmento() {
        return segmento;
    }

    public void setSegmento(String segmento) {
        this.segmento = segmento;
    }

    public LocalDateTime getFechaCalculo() {
        return fechaCalculo;
    }

    public void setFechaCalculo(LocalDateTime fechaCalculo) {
        this.fechaCalculo = fechaCalculo;
    }
}
