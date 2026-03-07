package com.nexoohub.almacen.ventas.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;

/**
 * DTO para solicitar una devolución de productos.
 * 
 * @author NexooHub Development Team
 * @since 1.0
 */
public class DevolucionRequestDTO {
    
    @NotNull(message = "El ID de la venta es obligatorio")
    private Integer ventaId;
    
    @NotNull(message = "El ID de la sucursal es obligatorio")
    private Integer sucursalId;
    
    @NotBlank(message = "El motivo de la devolución es obligatorio")
    private String motivo;
    
    @NotBlank(message = "El método de reembolso es obligatorio")
    private String metodoReembolso; // EFECTIVO, TARJETA, NOTA_CREDITO
    
    @NotEmpty(message = "Debe incluir al menos un item a devolver")
    @Valid
    private List<ItemDevolucionDTO> items;
    
    // ==========================================
    // INNER CLASS: ItemDevolucionDTO
    // ==========================================
    
    public static class ItemDevolucionDTO {
        @NotBlank(message = "El SKU es obligatorio")
        private String skuInterno;
        
        @NotNull(message = "La cantidad es obligatoria")
        @Positive(message = "La cantidad debe ser mayor a cero")
        private Integer cantidad;
        
        private String motivoItem;
        
        public String getSkuInterno() { return skuInterno; }
        public void setSkuInterno(String skuInterno) { this.skuInterno = skuInterno; }
        
        public Integer getCantidad() { return cantidad; }
        public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
        
        public String getMotivoItem() { return motivoItem; }
        public void setMotivoItem(String motivoItem) { this.motivoItem = motivoItem; }
    }
    
    // ==========================================
    // GETTERS Y SETTERS
    // ==========================================
    
    public Integer getVentaId() { return ventaId; }
    public void setVentaId(Integer ventaId) { this.ventaId = ventaId; }
    
    public Integer getSucursalId() { return sucursalId; }
    public void setSucursalId(Integer sucursalId) { this.sucursalId = sucursalId; }
    
    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }
    
    public String getMetodoReembolso() { return metodoReembolso; }
    public void setMetodoReembolso(String metodoReembolso) { this.metodoReembolso = metodoReembolso; }
    
    public List<ItemDevolucionDTO> getItems() { return items; }
    public void setItems(List<ItemDevolucionDTO> items) { this.items = items; }
}
