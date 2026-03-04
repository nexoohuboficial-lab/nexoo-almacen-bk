package com.nexoohub.almacen.inventario.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public class TraspasoRequestDTO {

    @NotNull(message = "La sucursal de origen es obligatoria")
    private Integer sucursalOrigenId;

    @NotNull(message = "La sucursal de destino es obligatoria")
    private Integer sucursalDestinoId;

    private String comentarios;

    @NotEmpty(message = "Debes enviar al menos un producto para traspasar")
    private List<ItemTraspasoDTO> items;

    public static class ItemTraspasoDTO {
        @NotNull private String skuInterno;
        @NotNull private Integer cantidad;

        public String getSkuInterno() { return skuInterno; }
        public void setSkuInterno(String skuInterno) { this.skuInterno = skuInterno; }
        public Integer getCantidad() { return cantidad; }
        public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
    }

    // Getters y Setters
    public Integer getSucursalOrigenId() { return sucursalOrigenId; }
    public void setSucursalOrigenId(Integer sucursalOrigenId) { this.sucursalOrigenId = sucursalOrigenId; }
    public Integer getSucursalDestinoId() { return sucursalDestinoId; }
    public void setSucursalDestinoId(Integer sucursalDestinoId) { this.sucursalDestinoId = sucursalDestinoId; }
    public String getComentarios() { return comentarios; }
    public void setComentarios(String comentarios) { this.comentarios = comentarios; }
    public List<ItemTraspasoDTO> getItems() { return items; }
    public void setItems(List<ItemTraspasoDTO> items) { this.items = items; }
}