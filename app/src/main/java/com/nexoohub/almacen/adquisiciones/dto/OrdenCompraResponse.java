package com.nexoohub.almacen.adquisiciones.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class OrdenCompraResponse {
    private Integer id;
    private String folio;
    private Integer proveedorId;
    private String nombreProveedor;
    private String rfcProveedor;
    private Integer sucursalId;
    private String nombreSucursal;
    private String estado;
    private BigDecimal totalEstimado;
    private String notas;
    private LocalDateTime fechaEnvio;
    private LocalDateTime fechaEsperadaEntrega;
    private String creadoPor;
    private LocalDateTime fechaCreacion;
    private List<DetalleResponse> detalles;

    @Data
    @Builder
    public static class DetalleResponse {
        private Integer id;
        private String skuInterno;
        private String skuProveedor;
        private String nombreProducto;
        private Integer cantidad;
        private BigDecimal precioCostoUnitario;
        private BigDecimal subtotal;
    }
}
