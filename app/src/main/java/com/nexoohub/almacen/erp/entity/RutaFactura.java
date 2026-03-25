package com.nexoohub.almacen.erp.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "ruta_factura")
public class RutaFactura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ruta_id", nullable = false)
    private RutaEntrega ruta;

    @Column(name = "factura_cliente_id", nullable = false)
    private Integer facturaClienteId;

    @Column(name = "numero_guia", length = 100)
    private String numeroGuia;

    @Column(name = "url_rastreo", length = 255)
    private String urlRastreo;

    /** PENDIENTE, ENTREGADO, RECHAZADO */
    @Column(name = "estatus_entrega", nullable = false, length = 20)
    private String estatusEntrega = "PENDIENTE";

    @Column(name = "fecha_entrega")
    private LocalDateTime fechaEntrega;

    @Column(name = "firma_recibido", length = 100)
    private String firmaRecibido;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // Getters y Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public RutaEntrega getRuta() { return ruta; }
    public void setRuta(RutaEntrega ruta) { this.ruta = ruta; }
    public Integer getFacturaClienteId() { return facturaClienteId; }
    public void setFacturaClienteId(Integer facturaClienteId) { this.facturaClienteId = facturaClienteId; }
    public String getNumeroGuia() { return numeroGuia; }
    public void setNumeroGuia(String numeroGuia) { this.numeroGuia = numeroGuia; }
    public String getUrlRastreo() { return urlRastreo; }
    public void setUrlRastreo(String urlRastreo) { this.urlRastreo = urlRastreo; }
    public String getEstatusEntrega() { return estatusEntrega; }
    public void setEstatusEntrega(String estatusEntrega) { this.estatusEntrega = estatusEntrega; }
    public LocalDateTime getFechaEntrega() { return fechaEntrega; }
    public void setFechaEntrega(LocalDateTime fechaEntrega) { this.fechaEntrega = fechaEntrega; }
    public String getFirmaRecibido() { return firmaRecibido; }
    public void setFirmaRecibido(String firmaRecibido) { this.firmaRecibido = firmaRecibido; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
