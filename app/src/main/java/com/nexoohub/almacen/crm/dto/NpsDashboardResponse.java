package com.nexoohub.almacen.crm.dto;

import java.math.BigDecimal;

public class NpsDashboardResponse {

    private long totalRespuestas;
    private long promotores;
    private long pasivos;
    private long detractores;
    private BigDecimal scoreNps;

    // Getters y Setters
    public long getTotalRespuestas() { return totalRespuestas; }
    public void setTotalRespuestas(long totalRespuestas) { this.totalRespuestas = totalRespuestas; }
    
    public long getPromotores() { return promotores; }
    public void setPromotores(long promotores) { this.promotores = promotores; }
    
    public long getPasivos() { return pasivos; }
    public void setPasivos(long pasivos) { this.pasivos = pasivos; }
    
    public long getDetractores() { return detractores; }
    public void setDetractores(long detractores) { this.detractores = detractores; }
    
    public BigDecimal getScoreNps() { return scoreNps; }
    public void setScoreNps(BigDecimal scoreNps) { this.scoreNps = scoreNps; }
}
