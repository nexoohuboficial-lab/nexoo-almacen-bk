package com.nexoohub.almacen.cotizaciones.entity;

import com.nexoohub.almacen.catalogo.entity.Cliente;
import com.nexoohub.almacen.empleados.entity.Empleado;
import com.nexoohub.almacen.sucursal.entity.Sucursal;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad que representa una cotización o presupuesto.
 * Una cotización es una oferta formal de venta que puede convertirse en una venta real.
 */
@Entity
@Table(name = "cotizacion", indexes = {
    @Index(name = "idx_cotizacion_cliente", columnList = "cliente_id"),
    @Index(name = "idx_cotizacion_sucursal", columnList = "sucursal_id"),
    @Index(name = "idx_cotizacion_estado", columnList = "estado"),
    @Index(name = "idx_cotizacion_fecha", columnList = "fecha_cotizacion"),
    @Index(name = "idx_cotizacion_validez", columnList = "fecha_validez")
})
public class Cotizacion {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "El folio es obligatorio")
    @Column(name = "folio", nullable = false, unique = true, length = 50)
    private String folio; // Ej: COT-2026-0001
    
    @NotNull(message = "El cliente es obligatorio")
    @Column(name = "cliente_id", insertable = false, updatable = false)
    private Integer clienteId;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;
    
    @NotNull(message = "La sucursal es obligatoria")
    @Column(name = "sucursal_id", insertable = false, updatable = false)
    private Integer sucursalId;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "sucursal_id", nullable = false)
    private Sucursal sucursal;
    
    @Column(name = "vendedor_id", insertable = false, updatable = false)
    private Integer vendedorId;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "vendedor_id")
    private Empleado vendedor;
    
    @NotNull(message = "El estado es obligatorio")
    @Column(name = "estado", nullable = false, length = 20)
    private String estado; // BORRADOR, ENVIADA, ACEPTADA, RECHAZADA, VENCIDA, CONVERTIDA
    
    @Column(name = "fecha_cotizacion", nullable = false, updatable = false)
    private LocalDateTime fechaCotizacion;
    
    @NotNull(message = "La fecha de validez es obligatoria")
    @Column(name = "fecha_validez", nullable = false)
    private LocalDate fechaValidez;
    
    @NotNull(message = "El total es obligatorio")
    @DecimalMin(value = "0.00", message = "El total no puede ser negativo")
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal total;
    
    @DecimalMin(value = "0.00", message = "El subtotal no puede ser negativo")
    @Column(precision = 15, scale = 2)
    private BigDecimal subtotal;
    
    @DecimalMin(value = "0.00", message = "El IVA no puede ser negativo")
    @Column(precision = 15, scale = 2)
    private BigDecimal iva;
    
    @DecimalMin(value = "0.00", message = "El descuento total no puede ser negativo")
    @Column(name = "descuento_total", precision = 15, scale = 2)
    private BigDecimal descuentoTotal = BigDecimal.ZERO;
    
    @Column(length = 1000)
    private String notas;
    
    @Column(name = "terminos_condiciones", length = 2000)
    private String terminosCondiciones;
    
    @Column(name = "observaciones_internas", length = 1000)
    private String observacionesInternas;
    
    @Column(name = "fecha_aceptacion")
    private LocalDateTime fechaAceptacion;
    
    @Column(name = "fecha_rechazo")
    private LocalDateTime fechaRechazo;
    
    @Column(name = "motivo_rechazo", length = 500)
    private String motivoRechazo;
    
    @Column(name = "venta_id")
    private Integer ventaId; // Si se convirtió en venta
    
    @Column(name = "fecha_conversion")
    private LocalDateTime fechaConversion;
    
    @OneToMany(mappedBy = "cotizacion", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<DetalleCotizacion> detalles = new ArrayList<>();
    
    @PrePersist
    protected void onCreate() {
        if (fechaCotizacion == null) {
            fechaCotizacion = LocalDateTime.now();
        }
        if (estado == null) {
            estado = "BORRADOR";
        }
        if (fechaValidez == null) {
            fechaValidez = LocalDate.now().plusDays(15); // 15 días de validez por defecto
        }
    }
    
    // Métodos de negocio
    
    /**
     * Calcula el total de la cotización basándose en los detalles
     */
    public void calcularTotales() {
        calcularTotales(this.detalles);
    }
    
    /**
     * Calcula el total de la cotización basándose en detalles externos
     * (útil para evitar modificar colecciones managed)
     * @param detallesExternos Lista de detalles para calcular
     */
    public void calcularTotales(List<DetalleCotizacion> detallesExternos) {
        if (detallesExternos == null || detallesExternos.isEmpty()) {
            this.subtotal = BigDecimal.ZERO;
            this.iva = BigDecimal.ZERO;
            this.descuentoTotal = BigDecimal.ZERO;
            this.total = BigDecimal.ZERO;
            return;
        }
        
        BigDecimal subtotalCalculado = BigDecimal.ZERO;
        BigDecimal descuentoCalculado = BigDecimal.ZERO;
        
        for (DetalleCotizacion detalle : detallesExternos) {
            BigDecimal precioTotal = detalle.getPrecioUnitario()
                .multiply(new BigDecimal(detalle.getCantidad()));
            subtotalCalculado = subtotalCalculado.add(precioTotal);
            
            BigDecimal descuentoDetalle = detalle.getDescuentoEspecial() != null 
                ? detalle.getDescuentoEspecial() 
                : BigDecimal.ZERO;
            descuentoCalculado = descuentoCalculado.add(descuentoDetalle);
        }
        
        this.subtotal = subtotalCalculado;
        this.descuentoTotal = descuentoCalculado;
        BigDecimal subtotalConDescuento = subtotalCalculado.subtract(descuentoCalculado);
        this.iva = subtotalConDescuento.multiply(new BigDecimal("0.16")); // 16% IVA
        this.total = subtotalConDescuento.add(iva);
    }
    
    /**
     * Verifica si la cotización está vencida
     */
    public boolean estaVencida() {
        return LocalDate.now().isAfter(fechaValidez) && 
               !estado.equals("CONVERTIDA") && 
               !estado.equals("ACEPTADA");
    }
    
    /**
     * Verifica si la cotización puede ser editada
     */
    public boolean esEditable() {
        return estado.equals("BORRADOR");
    }
    
    /**
     * Verifica si la cotización puede ser convertida en venta
     */
    public boolean puedeConvertirseEnVenta() {
        return (estado.equals("ENVIADA") || estado.equals("ACEPTADA")) && 
               !estaVencida() && 
               ventaId == null;
    }
    
    /**
     * Marca la cotización como enviada al cliente
     */
    public void marcarComoEnviada() {
        if (!estado.equals("BORRADOR")) {
            throw new IllegalStateException("Solo se pueden enviar cotizaciones en estado BORRADOR");
        }
        this.estado = "ENVIADA";
    }
    
    /**
     * Marca la cotización como aceptada por el cliente
     */
    public void marcarComoAceptada() {
        if (!estado.equals("ENVIADA")) {
            throw new IllegalStateException("Solo se pueden aceptar cotizaciones en estado ENVIADA");
        }
        this.estado = "ACEPTADA";
        this.fechaAceptacion = LocalDateTime.now();
    }
    
    /**
     * Marca la cotización como rechazada
     */
    public void marcarComoRechazada(String motivo) {
        if (estado.equals("CONVERTIDA")) {
            throw new IllegalStateException("No se puede rechazar una cotización ya convertida en venta");
        }
        this.estado = "RECHAZADA";
        this.fechaRechazo = LocalDateTime.now();
        this.motivoRechazo = motivo;
    }
    
    /**
     * Marca la cotización como convertida en venta
     */
    public void marcarComoConvertida(Integer idVenta) {
        if (!puedeConvertirseEnVenta()) {
            throw new IllegalStateException("La cotización no puede ser convertida en venta en su estado actual");
        }
        this.estado = "CONVERTIDA";
        this.ventaId = idVenta;
        this.fechaConversion = LocalDateTime.now();
    }
    
    // Getters y Setters
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getFolio() {
        return folio;
    }
    
    public void setFolio(String folio) {
        this.folio = folio;
    }
    
    public Integer getClienteId() {
        return clienteId;
    }
    
    public void setClienteId(Integer clienteId) {
        this.clienteId = clienteId;
    }
    
    public Cliente getCliente() {
        return cliente;
    }
    
    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }
    
    public Integer getSucursalId() {
        return sucursalId;
    }
    
    public void setSucursalId(Integer sucursalId) {
        this.sucursalId = sucursalId;
    }
    
    public Sucursal getSucursal() {
        return sucursal;
    }
    
    public void setSucursal(Sucursal sucursal) {
        this.sucursal = sucursal;
    }
    
    public Integer getVendedorId() {
        return vendedorId;
    }
    
    public void setVendedorId(Integer vendedorId) {
        this.vendedorId = vendedorId;
    }
    
    public Empleado getVendedor() {
        return vendedor;
    }
    
    public void setVendedor(Empleado vendedor) {
        this.vendedor = vendedor;
    }
    
    public String getEstado() {
        return estado;
    }
    
    public void setEstado(String estado) {
        this.estado = estado;
    }
    
    public LocalDateTime getFechaCotizacion() {
        return fechaCotizacion;
    }
    
    public void setFechaCotizacion(LocalDateTime fechaCotizacion) {
        this.fechaCotizacion = fechaCotizacion;
    }
    
    public LocalDate getFechaValidez() {
        return fechaValidez;
    }
    
    public void setFechaValidez(LocalDate fechaValidez) {
        this.fechaValidez = fechaValidez;
    }
    
    public BigDecimal getTotal() {
        return total;
    }
    
    public void setTotal(BigDecimal total) {
        this.total = total;
    }
    
    public BigDecimal getSubtotal() {
        return subtotal;
    }
    
    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }
    
    public BigDecimal getIva() {
        return iva;
    }
    
    public void setIva(BigDecimal iva) {
        this.iva = iva;
    }
    
    public BigDecimal getDescuentoTotal() {
        return descuentoTotal;
    }
    
    public void setDescuentoTotal(BigDecimal descuentoTotal) {
        this.descuentoTotal = descuentoTotal;
    }
    
    public String getNotas() {
        return notas;
    }
    
    public void setNotas(String notas) {
        this.notas = notas;
    }
    
    public String getTerminosCondiciones() {
        return terminosCondiciones;
    }
    
    public void setTerminosCondiciones(String terminosCondiciones) {
        this.terminosCondiciones = terminosCondiciones;
    }
    
    public String getObservacionesInternas() {
        return observacionesInternas;
    }
    
    public void setObservacionesInternas(String observacionesInternas) {
        this.observacionesInternas = observacionesInternas;
    }
    
    public LocalDateTime getFechaAceptacion() {
        return fechaAceptacion;
    }
    
    public void setFechaAceptacion(LocalDateTime fechaAceptacion) {
        this.fechaAceptacion = fechaAceptacion;
    }
    
    public LocalDateTime getFechaRechazo() {
        return fechaRechazo;
    }
    
    public void setFechaRechazo(LocalDateTime fechaRechazo) {
        this.fechaRechazo = fechaRechazo;
    }
    
    public String getMotivoRechazo() {
        return motivoRechazo;
    }
    
    public void setMotivoRechazo(String motivoRechazo) {
        this.motivoRechazo = motivoRechazo;
    }
    
    public Integer getVentaId() {
        return ventaId;
    }
    
    public void setVentaId(Integer ventaId) {
        this.ventaId = ventaId;
    }
    
    public LocalDateTime getFechaConversion() {
        return fechaConversion;
    }
    
    public void setFechaConversion(LocalDateTime fechaConversion) {
        this.fechaConversion = fechaConversion;
    }
    
    public List<DetalleCotizacion> getDetalles() {
        return detalles;
    }
    
    public void setDetalles(List<DetalleCotizacion> detalles) {
        this.detalles = detalles;
    }
}
