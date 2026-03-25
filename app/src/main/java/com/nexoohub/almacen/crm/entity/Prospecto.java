package com.nexoohub.almacen.crm.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "prospecto")
public class Prospecto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "empresa", nullable = false)
    private String empresa;

    @Column(name = "rfc", length = 20)
    private String rfc;

    @Column(name = "contacto_principal", length = 150)
    private String contactoPrincipal;

    @Column(name = "correo", length = 255)
    private String correo;

    @Column(name = "telefono", length = 50)
    private String telefono;

    @Column(name = "estatus_viabilidad", length = 50)
    private String estatusViabilidad = "NUEVO";

    @Column(name = "notas", columnDefinition = "TEXT")
    private String notas;

    @Column(name = "fecha_creacion", updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "usuario_creacion", length = 50)
    private String usuarioCreacion;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    @Column(name = "usuario_actualizacion", length = 50)
    private String usuarioActualizacion;

    @OneToMany(mappedBy = "prospecto", cascade = CascadeType.ALL, orphanRemoval = true)
    private java.util.List<OportunidadVenta> oportunidades = new java.util.ArrayList<>();

    @PrePersist
    protected void onCreate() {
        this.fechaCreacion = LocalDateTime.now();
        this.fechaActualizacion = LocalDateTime.now();
        if(this.estatusViabilidad == null) this.estatusViabilidad = "NUEVO";
    }

    @PreUpdate
    protected void onUpdate() {
        this.fechaActualizacion = LocalDateTime.now();
    }

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
    public String getUsuarioCreacion() { return usuarioCreacion; }
    public void setUsuarioCreacion(String usuarioCreacion) { this.usuarioCreacion = usuarioCreacion; }
    public LocalDateTime getFechaActualizacion() { return fechaActualizacion; }
    public void setFechaActualizacion(LocalDateTime fechaActualizacion) { this.fechaActualizacion = fechaActualizacion; }
    public String getUsuarioActualizacion() { return usuarioActualizacion; }
    public void setUsuarioActualizacion(String usuarioActualizacion) { this.usuarioActualizacion = usuarioActualizacion; }
    public java.util.List<OportunidadVenta> getOportunidades() { return oportunidades; }
    public void setOportunidades(java.util.List<OportunidadVenta> oportunidades) { this.oportunidades = oportunidades; }
}
