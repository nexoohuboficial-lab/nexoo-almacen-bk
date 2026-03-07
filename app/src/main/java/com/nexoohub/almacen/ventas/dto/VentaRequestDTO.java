package com.nexoohub.almacen.ventas.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

public class VentaRequestDTO {

    @NotNull(message = "El cliente es obligatorio")
    private Integer clienteId;

    @NotNull(message = "La sucursal es obligatoria")
    private Integer sucursalId;

    @NotNull(message = "El método de pago es obligatorio")
    private String metodoPago; // EFECTIVO, TARJETA, TRANSFERENCIA

    @NotEmpty(message = "La venta debe tener al menos un artículo")
    private List<ItemVentaDTO> items;

    public static class ItemVentaDTO {
        @NotNull private String skuInterno;
        @NotNull private Integer cantidad;
        private BigDecimal precioOfertaEspecial; // Opcional: precio con descuento aplicado por el vendedor

        // Getters y Setters
        public String getSkuInterno() { return skuInterno; }
        public void setSkuInterno(String skuInterno) { this.skuInterno = skuInterno; }
        public Integer getCantidad() { return cantidad; }
        public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
        public BigDecimal getPrecioOfertaEspecial() { return precioOfertaEspecial; }
        public void setPrecioOfertaEspecial(BigDecimal precioOfertaEspecial) { this.precioOfertaEspecial = precioOfertaEspecial; }
    }

    // Getters y Setters del encabezado...
    public Integer getClienteId() { return clienteId; }
    public void setClienteId(Integer clienteId) { this.clienteId = clienteId; }
    public Integer getSucursalId() { return sucursalId; }
    public void setSucursalId(Integer sucursalId) { this.sucursalId = sucursalId; }
    public String getMetodoPago() { return metodoPago; }
    public void setMetodoPago(String metodoPago) { this.metodoPago = metodoPago; }
    public List<ItemVentaDTO> getItems() { return items; }
    public void setItems(List<ItemVentaDTO> items) { this.items = items; }
}