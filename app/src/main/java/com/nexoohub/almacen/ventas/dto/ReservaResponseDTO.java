package com.nexoohub.almacen.ventas.dto;

import com.nexoohub.almacen.ventas.entity.Reserva;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO de respuesta con información de una reserva.
 * 
 * @author NexooHub Development Team
 * @since 1.1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservaResponseDTO {

    private Integer id;
    private Integer clienteId;
    private String clienteNombre;
    private String skuInterno;
    private String productoNombre;
    private Integer sucursalId;
    private String sucursalNombre;
    private Integer cantidad;
    private String estado;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaNotificacion;
    private LocalDateTime fechaVencimiento;
    private LocalDateTime fechaFinalizacion;
    private Integer ventaId;
    private String comentarios;
    private String usuarioRegistro;

    /**
     * Convierte una entidad Reserva a DTO.
     */
    public static ReservaResponseDTO fromEntity(Reserva reserva) {
        ReservaResponseDTO dto = new ReservaResponseDTO();
        dto.setId(reserva.getId());
        dto.setClienteId(reserva.getCliente().getId());
        dto.setClienteNombre(reserva.getCliente().getNombre());
        dto.setSkuInterno(reserva.getProducto().getSkuInterno());
        dto.setProductoNombre(reserva.getProducto().getNombreComercial());
        dto.setSucursalId(reserva.getSucursal().getId());
        dto.setSucursalNombre(reserva.getSucursal().getNombre());
        dto.setCantidad(reserva.getCantidad());
        dto.setEstado(reserva.getEstado().name());
        dto.setFechaCreacion(reserva.getFechaCreacion());
        dto.setFechaNotificacion(reserva.getFechaNotificacion());
        dto.setFechaVencimiento(reserva.getFechaVencimiento());
        dto.setFechaFinalizacion(reserva.getFechaFinalizacion());
        dto.setVentaId(reserva.getVentaId());
        dto.setComentarios(reserva.getComentarios());
        dto.setUsuarioRegistro(reserva.getUsuarioRegistro());
        return dto;
    }
}
