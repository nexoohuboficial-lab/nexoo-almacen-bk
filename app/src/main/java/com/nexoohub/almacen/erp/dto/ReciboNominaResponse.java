package com.nexoohub.almacen.erp.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class ReciboNominaResponse {
    private Integer id;
    private Integer periodoId;
    private EmpleadoDTO empleado;
    private BigDecimal diasTrabajados;
    private BigDecimal totalPercepciones;
    private BigDecimal totalDeducciones;
    private BigDecimal netoPagar;
    private String metodoPago;
    private LocalDateTime createdAt;
    private List<ReciboNominaDetalleDTO> conceptos;

    // Getters y Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getPeriodoId() { return periodoId; }
    public void setPeriodoId(Integer periodoId) { this.periodoId = periodoId; }
    public EmpleadoDTO getEmpleado() { return empleado; }
    public void setEmpleado(EmpleadoDTO empleado) { this.empleado = empleado; }
    public BigDecimal getDiasTrabajados() { return diasTrabajados; }
    public void setDiasTrabajados(BigDecimal diasTrabajados) { this.diasTrabajados = diasTrabajados; }
    public BigDecimal getTotalPercepciones() { return totalPercepciones; }
    public void setTotalPercepciones(BigDecimal totalPercepciones) { this.totalPercepciones = totalPercepciones; }
    public BigDecimal getTotalDeducciones() { return totalDeducciones; }
    public void setTotalDeducciones(BigDecimal totalDeducciones) { this.totalDeducciones = totalDeducciones; }
    public BigDecimal getNetoPagar() { return netoPagar; }
    public void setNetoPagar(BigDecimal netoPagar) { this.netoPagar = netoPagar; }
    public String getMetodoPago() { return metodoPago; }
    public void setMetodoPago(String metodoPago) { this.metodoPago = metodoPago; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public List<ReciboNominaDetalleDTO> getConceptos() { return conceptos; }
    public void setConceptos(List<ReciboNominaDetalleDTO> conceptos) { this.conceptos = conceptos; }
}
