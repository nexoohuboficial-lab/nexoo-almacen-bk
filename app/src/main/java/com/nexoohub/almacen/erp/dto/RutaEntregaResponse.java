package com.nexoohub.almacen.erp.dto;

import java.time.LocalDate;
import java.util.List;

public class RutaEntregaResponse {
    private Integer id;
    private String codigoRuta;
    private LocalDate fechaProgramada;
    private ChoferDTO chofer;
    private VehiculoDTO vehiculo;
    private Boolean esPaqueteria;
    private String proveedorEnvio;
    private String estatus;
    private String observaciones;
    private List<RutaFacturaDTO> facturasProgramadas;

    // Getters y Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getCodigoRuta() { return codigoRuta; }
    public void setCodigoRuta(String codigoRuta) { this.codigoRuta = codigoRuta; }
    public LocalDate getFechaProgramada() { return fechaProgramada; }
    public void setFechaProgramada(LocalDate fechaProgramada) { this.fechaProgramada = fechaProgramada; }
    public ChoferDTO getChofer() { return chofer; }
    public void setChofer(ChoferDTO chofer) { this.chofer = chofer; }
    public VehiculoDTO getVehiculo() { return vehiculo; }
    public void setVehiculo(VehiculoDTO vehiculo) { this.vehiculo = vehiculo; }
    public Boolean getEsPaqueteria() { return esPaqueteria; }
    public void setEsPaqueteria(Boolean esPaqueteria) { this.esPaqueteria = esPaqueteria; }
    public String getProveedorEnvio() { return proveedorEnvio; }
    public void setProveedorEnvio(String proveedorEnvio) { this.proveedorEnvio = proveedorEnvio; }
    public String getEstatus() { return estatus; }
    public void setEstatus(String estatus) { this.estatus = estatus; }
    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
    public List<RutaFacturaDTO> getFacturasProgramadas() { return facturasProgramadas; }
    public void setFacturasProgramadas(List<RutaFacturaDTO> facturasProgramadas) { this.facturasProgramadas = facturasProgramadas; }
}
