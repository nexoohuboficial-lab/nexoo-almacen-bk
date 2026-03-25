package com.nexoohub.almacen.crm.dto;

public class CampanaMetricasResponse {
    private Integer campanaId;
    private String nombre;
    private String estado;
    private long totalEnviados;
    private long totalEntregados;
    private long totalFallidos;

    public CampanaMetricasResponse() {}

    public CampanaMetricasResponse(Integer campanaId, String nombre, String estado, long totalEnviados, long totalEntregados, long totalFallidos) {
        this.campanaId = campanaId;
        this.nombre = nombre;
        this.estado = estado;
        this.totalEnviados = totalEnviados;
        this.totalEntregados = totalEntregados;
        this.totalFallidos = totalFallidos;
    }

    public Integer getCampanaId() {
        return campanaId;
    }

    public void setCampanaId(Integer campanaId) {
        this.campanaId = campanaId;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public long getTotalEnviados() {
        return totalEnviados;
    }

    public void setTotalEnviados(long totalEnviados) {
        this.totalEnviados = totalEnviados;
    }

    public long getTotalEntregados() {
        return totalEntregados;
    }

    public void setTotalEntregados(long totalEntregados) {
        this.totalEntregados = totalEntregados;
    }

    public long getTotalFallidos() {
        return totalFallidos;
    }

    public void setTotalFallidos(long totalFallidos) {
        this.totalFallidos = totalFallidos;
    }
}
