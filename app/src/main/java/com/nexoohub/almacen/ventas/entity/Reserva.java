package com.nexoohub.almacen.ventas.entity;

import com.nexoohub.almacen.catalogo.entity.Cliente;
import com.nexoohub.almacen.inventario.entity.ProductoMaestro;
import com.nexoohub.almacen.sucursal.entity.Sucursal;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entidad que representa una reserva/apartado de producto.
 * 
 * <p>Permite a los clientes reservar productos que no están en stock,
 * garantizando prioridad cuando llegue la mercancía.</p>
 * 
 * <p><b>Estados de la reserva:</b></p>
 * <ul>
 *   <li>PENDIENTE: Esperando llegada de mercancía</li>
 *   <li>NOTIFICADA: Mercancía disponible, cliente notificado</li>
 *   <li>COMPLETADA: Cliente recogió el producto (convertida a venta)</li>
 *   <li>VENCIDA: Tiempo límite expirado sin recoger</li>
 *   <li>CANCELADA: Cliente o sistema canceló la reserva</li>
 * </ul>
 * 
 * @author NexooHub Development Team
 * @since 1.1.0
 */
@Entity
@Table(name = "reserva")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * Cliente que realiza la reserva.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    @NotNull(message = "El cliente es obligatorio")
    private Cliente cliente;

    /**
     * Producto reservado.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sku_interno", nullable = false)
    @NotNull(message = "El producto es obligatorio")
    private ProductoMaestro producto;

    /**
     * Sucursal donde se recogerá el producto.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sucursal_id", nullable = false)
    @NotNull(message = "La sucursal es obligatoria")
    private Sucursal sucursal;

    /**
     * Cantidad de unidades reservadas.
     */
    @Column(nullable = false)
    @Positive(message = "La cantidad debe ser mayor a cero")
    private Integer cantidad;

    /**
     * Estado actual de la reserva.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoReserva estado;

    /**
     * Fecha y hora de creación de la reserva.
     */
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    /**
     * Fecha y hora en que se notificó al cliente.
     */
    @Column(name = "fecha_notificacion")
    private LocalDateTime fechaNotificacion;

    /**
     * Fecha y hora de vencimiento de la reserva.
     */
    @Column(name = "fecha_vencimiento", nullable = false)
    private LocalDateTime fechaVencimiento;

    /**
     * Fecha y hora de completado/cancelación.
     */
    @Column(name = "fecha_finalizacion")
    private LocalDateTime fechaFinalizacion;

    /**
     * ID de la venta generada al completar la reserva.
     */
    @Column(name = "venta_id")
    private Integer ventaId;

    /**
     * Comentarios adicionales del cliente o sistema.
     */
    @Column(length = 500)
    private String comentarios;

    /**
     * Usuario que registró la reserva.
     */
    @Column(name = "usuario_registro", nullable = false, length = 100)
    private String usuarioRegistro;

    @PrePersist
    protected void onCreate() {
        this.fechaCreacion = LocalDateTime.now();
        if (this.estado == null) {
            this.estado = EstadoReserva.PENDIENTE;
        }
    }

    /**
     * Estados posibles de una reserva.
     */
    public enum EstadoReserva {
        PENDIENTE,      // Esperando mercancía
        NOTIFICADA,     // Cliente notificado, disponible para recoger
        COMPLETADA,     // Convertida a venta exitosamente
        VENCIDA,        // Tiempo límite expirado
        CANCELADA       // Cancelada por cliente o sistema
    }
}
