package com.nexoohub.almacen.adquisiciones.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class CarritoResumenResponse {
    private Integer totalArticulos;
    private BigDecimal totalEstimadoGlobal;
    private List<GrupoProveedorCarritoDTO> gruposPorProveedor;
    
    @Data
    @Builder
    public static class GrupoProveedorCarritoDTO {
        private Integer proveedorId;
        private String nombreProveedor;
        private BigDecimal subtotalProveedor;
        private List<ItemCarritoDTO> items;
    }

    @Data
    @Builder
    public static class ItemCarritoDTO {
        private Integer sesionId;
        private Integer catalogoId;
        private String skuInterno;
        private String skuProveedor;
        private String nombreProducto;
        private Integer cantidad;
        private BigDecimal precioCostoUnitario;
        private BigDecimal subtotal;
        private BigDecimal precioVentaSugerido;
        private Integer diasEntregaEstimado;
    }
}
