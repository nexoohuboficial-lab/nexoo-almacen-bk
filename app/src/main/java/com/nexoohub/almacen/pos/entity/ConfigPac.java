package com.nexoohub.almacen.pos.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entidad de configuración de Proveedor Autorizado de Certificación (PAC).
 * Almacena credenciales de Facturapi, SW Sapien, u otros PACs.
 *
 * @author NexooHub Development Team
 * @since 1.0 — POS-03
 */
@Entity
@Table(name = "config_pac")
public class ConfigPac {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "proveedor", nullable = false, length = 50)
    private String proveedor; // FACTURAPI | SW_SAPIEN | EDICOM

    @Column(name = "clave_api", nullable = false, length = 255)
    private String claveApi;

    @Column(name = "url_endpoint", nullable = false, length = 255)
    private String urlEndpoint;

    @Column(name = "is_activo")
    private Boolean isActivo = false;

    @Column(name = "entorno", nullable = false, length = 20)
    private String entorno = "PRUEBAS"; // PRUEBAS | PRODUCCION

    @Column(name = "rfc_emisor", nullable = false, length = 13)
    private String rfcEmisor;

    @Column(name = "razon_social_emisor", nullable = false, length = 255)
    private String razonSocialEmisor;

    @Column(name = "regimen_fiscal_emisor", nullable = false, length = 10)
    private String regimenFiscalEmisor;

    @Column(name = "codigo_postal_emisor", nullable = false, length = 5)
    private String codigoPostalEmisor;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "usuario_creacion", length = 50)
    private String usuarioCreacion;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    @Column(name = "usuario_actualizacion", length = 50)
    private String usuarioActualizacion;

    @PrePersist
    protected void onCreate() {
        if (fechaCreacion == null) fechaCreacion = LocalDateTime.now();
        fechaActualizacion = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        fechaActualizacion = LocalDateTime.now();
    }

    // Getters y Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getProveedor() { return proveedor; }
    public void setProveedor(String proveedor) { this.proveedor = proveedor; }
    public String getClaveApi() { return claveApi; }
    public void setClaveApi(String claveApi) { this.claveApi = claveApi; }
    public String getUrlEndpoint() { return urlEndpoint; }
    public void setUrlEndpoint(String urlEndpoint) { this.urlEndpoint = urlEndpoint; }
    public Boolean getActivo() { return isActivo; }
    public void setActivo(Boolean activo) { isActivo = activo; }
    public String getEntorno() { return entorno; }
    public void setEntorno(String entorno) { this.entorno = entorno; }
    public String getRfcEmisor() { return rfcEmisor; }
    public void setRfcEmisor(String rfcEmisor) { this.rfcEmisor = rfcEmisor; }
    public String getRazonSocialEmisor() { return razonSocialEmisor; }
    public void setRazonSocialEmisor(String razonSocialEmisor) { this.razonSocialEmisor = razonSocialEmisor; }
    public String getRegimenFiscalEmisor() { return regimenFiscalEmisor; }
    public void setRegimenFiscalEmisor(String regimenFiscalEmisor) { this.regimenFiscalEmisor = regimenFiscalEmisor; }
    public String getCodigoPostalEmisor() { return codigoPostalEmisor; }
    public void setCodigoPostalEmisor(String codigoPostalEmisor) { this.codigoPostalEmisor = codigoPostalEmisor; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
    public String getUsuarioCreacion() { return usuarioCreacion; }
    public void setUsuarioCreacion(String usuarioCreacion) { this.usuarioCreacion = usuarioCreacion; }
    public LocalDateTime getFechaActualizacion() { return fechaActualizacion; }
    public void setFechaActualizacion(LocalDateTime fechaActualizacion) { this.fechaActualizacion = fechaActualizacion; }
    public String getUsuarioActualizacion() { return usuarioActualizacion; }
    public void setUsuarioActualizacion(String usuarioActualizacion) { this.usuarioActualizacion = usuarioActualizacion; }
}
