package com.nexoohub.almacen.analitica.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class RfmClienteResponse {
    private Integer clienteId;
    private String nombreCliente;
    private Integer recenciaDias;
    private Integer frecuenciaCompras;
    private BigDecimal montoGastado;
    private Integer scoreR;
    private Integer scoreF;
    private Integer scoreM;
    private String segmento;
    private LocalDateTime fechaCalculo;

    public RfmClienteResponse() {
    }

    public Integer getClienteId() {
        return clienteId;
    }

    public void setClienteId(Integer clienteId) {
        this.clienteId = clienteId;
    }

    public String getNombreCliente() {
        return nombreCliente;
    }

    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
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
