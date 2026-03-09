package com.nexoohub.almacen.ventas.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para crear una nueva reserva de producto.
 * 
 * @author NexooHub Development Team
 * @since 1.1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservaRequestDTO {

    /**
     * ID del cliente que realiza la reserva.
     */
    @NotNull(message = "El ID del cliente es obligatorio")
    @Positive(message = "El ID del cliente debe ser positivo")
    private Integer clienteId;

    /**
     * SKU del producto a reservar.
     */
    @NotBlank(message = "El SKU del producto es obligatorio")
    private String skuInterno;

    /**
     * ID de la sucursal donde se recogerá.
     */
    @NotNull(message = "El ID de la sucursal es obligatorio")
    @Positive(message = "El ID de la sucursal debe ser positivo")
    private Integer sucursalId;

    /**
     * Cantidad de unidades a reservar.
     */
    @NotNull(message = "La cantidad es obligatoria")
    @Positive(message = "La cantidad debe ser mayor a cero")
    private Integer cantidad;

    /**
     * Comentarios opcionales del cliente.
     */
    private String comentarios;

    /**
     * Días de vigencia de la reserva (opcional, default: 7 días).
     */
    private Integer diasVigencia;
}
