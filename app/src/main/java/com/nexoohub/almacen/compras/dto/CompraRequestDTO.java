package com.nexoohub.almacen.compras.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

public class CompraRequestDTO {

    @NotNull(message = "El ID del proveedor es obligatorio")
    private Integer proveedorId;

    private String folioFactura;

    @NotNull(message = "Debes indicar si los precios incluyen IVA")
    private Boolean preciosIncluyenIva;

    @NotNull(message = "El ID de la sucursal destino es obligatorio")
    private Integer sucursalDestinoId;

    @NotEmpty(message = "La compra debe tener al menos un producto")
    private List<DetalleItemDTO> detalles;

    // ==========================================
    // CLASE INTERNA PARA LOS RENGLONES
    // ==========================================
    public static class DetalleItemDTO {
        @NotNull(message = "El SKU es obligatorio")
        private String skuInterno;

        @NotNull(message = "La cantidad es obligatoria")
        private Integer cantidad;

        @NotNull(message = "El costo unitario es obligatorio")
        private BigDecimal costoUnitario;

        // Este campo es opcional, por si el vendedor capturó en cuánto lo vende el proveedor
        private BigDecimal precioPublicoProveedor;

        // Getters y Setters del Detalle
        public String getSkuInterno() { return skuInterno; }
        public void setSkuInterno(String skuInterno) { this.skuInterno = skuInterno; }

        public Integer getCantidad() { return cantidad; }
        public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }

        public BigDecimal getCostoUnitario() { return costoUnitario; }
        public void setCostoUnitario(BigDecimal costoUnitario) { this.costoUnitario = costoUnitario; }

        public BigDecimal getPrecioPublicoProveedor() { return precioPublicoProveedor; }
        public void setPrecioPublicoProveedor(BigDecimal precioPublicoProveedor) { this.precioPublicoProveedor = precioPublicoProveedor; }
    }

    // ==========================================
    // GETTERS Y SETTERS DEL ENCABEZADO
    // ==========================================
    public Integer getProveedorId() { return proveedorId; }
    public void setProveedorId(Integer proveedorId) { this.proveedorId = proveedorId; }

    public String getFolioFactura() { return folioFactura; }
    public void setFolioFactura(String folioFactura) { this.folioFactura = folioFactura; }

    public Boolean getPreciosIncluyenIva() { return preciosIncluyenIva; }
    public void setPreciosIncluyenIva(Boolean preciosIncluyenIva) { this.preciosIncluyenIva = preciosIncluyenIva; }

    public Integer getSucursalDestinoId() { return sucursalDestinoId; }
    public void setSucursalDestinoId(Integer sucursalDestinoId) { this.sucursalDestinoId = sucursalDestinoId; }

    public List<DetalleItemDTO> getDetalles() { return detalles; }
    public void setDetalles(List<DetalleItemDTO> detalles) { this.detalles = detalles; }
}