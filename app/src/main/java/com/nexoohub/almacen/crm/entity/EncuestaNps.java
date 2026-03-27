package com.nexoohub.almacen.crm.entity;

import com.nexoohub.almacen.catalogo.entity.Cliente;
import com.nexoohub.almacen.common.entity.AuditableEntity;
import com.nexoohub.almacen.ventas.entity.Venta;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "encuesta_nps")
public class EncuestaNps extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "venta_id", insertable = false, updatable = false)
    private Integer ventaId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "venta_id")
    private Venta venta;

    @Column(name = "cliente_id", insertable = false, updatable = false)
    private Integer clienteId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    @Column(name = "enlace_unico")
    private String enlaceUnico;

    private String estado;

    @Column(name = "fecha_envio")
    private LocalDateTime fechaEnvio;

    @Column(name = "fecha_expiracion")
    private LocalDateTime fechaExpiracion;

    @OneToOne(mappedBy = "encuesta", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private RespuestaNps respuesta;

    // Getters y Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    
    public Integer getVentaId() { return ventaId; }
    public void setVentaId(Integer ventaId) { this.ventaId = ventaId; }
    
    public Venta getVenta() { return venta; }
    public void setVenta(Venta venta) { this.venta = venta; }
    
    public Integer getClienteId() { return clienteId; }
    public void setClienteId(Integer clienteId) { this.clienteId = clienteId; }
    
    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }
    
    public String getEnlaceUnico() { return enlaceUnico; }
    public void setEnlaceUnico(String enlaceUnico) { this.enlaceUnico = enlaceUnico; }
    
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    
    public LocalDateTime getFechaEnvio() { return fechaEnvio; }
    public void setFechaEnvio(LocalDateTime fechaEnvio) { this.fechaEnvio = fechaEnvio; }
    
    public LocalDateTime getFechaExpiracion() { return fechaExpiracion; }
    public void setFechaExpiracion(LocalDateTime fechaExpiracion) { this.fechaExpiracion = fechaExpiracion; }
    
    public RespuestaNps getRespuesta() { return respuesta; }
    public void setRespuesta(RespuestaNps respuesta) { this.respuesta = respuesta; }
}
