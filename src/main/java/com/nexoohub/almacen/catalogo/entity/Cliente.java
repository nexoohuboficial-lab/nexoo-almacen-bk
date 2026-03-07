package com.nexoohub.almacen.catalogo.entity;

import com.nexoohub.almacen.common.entity.AuditableEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "cliente")
public class Cliente extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull(message = "El tipo de cliente es obligatorio")
    @Column(name = "tipo_cliente_id")
    private Integer tipoClienteId;

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    private String rfc;
    private String telefono;
    private String email;

    @Column(name = "direccion_fiscal")
    private String direccionFiscal;

    @Column(name = "bloqueado")
    private Boolean bloqueado = false; // Bloqueado por morosidad

    @Column(name = "saldo_pendiente")
    private java.math.BigDecimal saldoPendiente = java.math.BigDecimal.ZERO;

    @Column(name = "motivo_bloqueo", length = 500)
    private String motivoBloqueo;

    // Getters y Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getTipoClienteId() { return tipoClienteId; }
    public void setTipoClienteId(Integer tipoClienteId) { this.tipoClienteId = tipoClienteId; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getRfc() { return rfc; }
    public void setRfc(String rfc) { this.rfc = rfc; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getDireccionFiscal() { return direccionFiscal; }
    public void setDireccionFiscal(String direccionFiscal) { this.direccionFiscal = direccionFiscal; }

    public Boolean getBloqueado() { return bloqueado; }
    public void setBloqueado(Boolean bloqueado) { this.bloqueado = bloqueado; }

    public java.math.BigDecimal getSaldoPendiente() { return saldoPendiente; }
    public void setSaldoPendiente(java.math.BigDecimal saldoPendiente) { this.saldoPendiente = saldoPendiente; }

    public String getMotivoBloqueo() { return motivoBloqueo; }
    public void setMotivoBloqueo(String motivoBloqueo) { this.motivoBloqueo = motivoBloqueo; }
}