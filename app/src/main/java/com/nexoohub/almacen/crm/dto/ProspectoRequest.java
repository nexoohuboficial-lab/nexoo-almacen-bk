package com.nexoohub.almacen.crm.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ProspectoRequest {

    @NotBlank(message = "El nombre de la empresa es obligatorio")
    @Size(max = 255)
    private String empresa;

    @Size(max = 20)
    private String rfc;

    @Size(max = 150)
    private String contactoPrincipal;

    @Email(message = "Debe ser un correo electrónico válido")
    @Size(max = 255)
    private String correo;

    @Size(max = 50)
    private String telefono;

    private String notas;

    // Getters y Setters
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
    public String getNotas() { return notas; }
    public void setNotas(String notas) { this.notas = notas; }
}
