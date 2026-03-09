package com.nexoohub.almacen.inventario.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO de respuesta para alertas de productos de lento movimiento.
 * 
 * <p>Incluye información completa del producto, sucursal y estado de la alerta.</p>
 * 
 * @author NexooHub Development Team
 * @since 1.2.0
 */
public class AlertaLentoMovimientoResponseDTO {
    private Integer id;
    private String skuInterno;
    private String nombreProducto;
    private String marcaProducto;
    private Integer sucursalId;
    private String nombreSucursal;
    private Integer diasSinVenta;
    private LocalDate ultimaVenta;
    private Integer stockActual;
    private BigDecimal costoInmovilizado;
    private String estadoAlerta; // ADVERTENCIA, CRITICO, RESUELTA
    private LocalDate fechaDeteccion;
    private LocalDate fechaResolucion;
    private String accionTomada;
    private String observaciones;
    private Boolean resuelto;
    private String sugerenciaAccion; // Calculado en el servicio

    // ==========================================
    // GETTERS Y SETTERS
    // ==========================================

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getSkuInterno() { return skuInterno; }
    public void setSkuInterno(String skuInterno) { this.skuInterno = skuInterno; }

    public String getNombreProducto() { return nombreProducto; }
    public void setNombreProducto(String nombreProducto) { this.nombreProducto = nombreProducto; }

    public String getMarcaProducto() { return marcaProducto; }
    public void setMarcaProducto(String marcaProducto) { this.marcaProducto = marcaProducto; }

    public Integer getSucursalId() { return sucursalId; }
    public void setSucursalId(Integer sucursalId) { this.sucursalId = sucursalId; }

    public String getNombreSucursal() { return nombreSucursal; }
    public void setNombreSucursal(String nombreSucursal) { this.nombreSucursal = nombreSucursal; }

    public Integer getDiasSinVenta() { return diasSinVenta; }
    public void setDiasSinVenta(Integer diasSinVenta) { this.diasSinVenta = diasSinVenta; }

    public LocalDate getUltimaVenta() { return ultimaVenta; }
    public void setUltimaVenta(LocalDate ultimaVenta) { this.ultimaVenta = ultimaVenta; }

    public Integer getStockActual() { return stockActual; }
    public void setStockActual(Integer stockActual) { this.stockActual = stockActual; }

    public BigDecimal getCostoInmovilizado() { return costoInmovilizado; }
    public void setCostoInmovilizado(BigDecimal costoInmovilizado) { 
        this.costoInmovilizado = costoInmovilizado; 
    }

    public String getEstadoAlerta() { return estadoAlerta; }
    public void setEstadoAlerta(String estadoAlerta) { this.estadoAlerta = estadoAlerta; }

    public LocalDate getFechaDeteccion() { return fechaDeteccion; }
    public void setFechaDeteccion(LocalDate fechaDeteccion) { this.fechaDeteccion = fechaDeteccion; }

    public LocalDate getFechaResolucion() { return fechaResolucion; }
    public void setFechaResolucion(LocalDate fechaResolucion) { 
        this.fechaResolucion = fechaResolucion; 
    }

    public String getAccionTomada() { return accionTomada; }
    public void setAccionTomada(String accionTomada) { this.accionTomada = accionTomada; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    public Boolean getResuelto() { return resuelto; }
    public void setResuelto(Boolean resuelto) { this.resuelto = resuelto; }

    public String getSugerenciaAccion() { return sugerenciaAccion; }
    public void setSugerenciaAccion(String sugerenciaAccion) { 
        this.sugerenciaAccion = sugerenciaAccion; 
    }
}
