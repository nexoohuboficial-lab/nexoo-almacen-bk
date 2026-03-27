package com.nexoohub.almacen.analitica.dto;

public class RfmSegmentoStatsResponse {
    private String segmento;
    private Long cantidad;

    public RfmSegmentoStatsResponse() {
    }

    public RfmSegmentoStatsResponse(String segmento, Long cantidad) {
        this.segmento = segmento;
        this.cantidad = cantidad;
    }

    public String getSegmento() {
        return segmento;
    }

    public void setSegmento(String segmento) {
        this.segmento = segmento;
    }

    public Long getCantidad() {
        return cantidad;
    }

    public void setCantidad(Long cantidad) {
        this.cantidad = cantidad;
    }
}
