package com.nexoohub.almacen.crm.dto;

import java.time.LocalDateTime;

public class ProspectoResponse {

    private Integer id;
    private String empresa;
    private String rfc;
    private String contactoPrincipal;
    private String correo;
    private String telefono;
    private String estatusViabilidad;
    private String notas;
    private LocalDateTime fechaCreacion;

    // Getters y Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getEmpresa() { return empresa; }
    public void setEmpresa(String empresa) { this.empresa = empresa; }
    public String getRfc() { return rfc; }
    public void setRfc(String rfc) { this.rfc = rfc; }
    public String getContactoPrincipal() { return contactoPrincipal; }
    public void setContactoPrincipal(String contactoPrincipal) { this.contactoPrincipal = contactoPrincipal; }
    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public String getEstatusViabilidad() { return estatusViabilidad; }
    public void setEstatusViabilidad(String estatusViabilidad) { this.estatusViabilidad = estatusViabilidad; }
    public String getNotas() { return notas; }
    public void setNotas(String notas) { this.notas = notas; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
}
