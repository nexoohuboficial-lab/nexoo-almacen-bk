package com.nexoohub.almacen.inventario.service;

import com.nexoohub.almacen.common.entity.Usuario;
import com.nexoohub.almacen.common.exception.InvalidOperationException;
import com.nexoohub.almacen.common.exception.ResourceNotFoundException;
import com.nexoohub.almacen.common.exception.StockInsuficienteException;
import com.nexoohub.almacen.common.repository.UsuarioRepository;
import com.nexoohub.almacen.inventario.dto.TraspasoRequestDTO;
import com.nexoohub.almacen.inventario.entity.InventarioSucursal;
import com.nexoohub.almacen.inventario.entity.InventarioSucursalId;
import com.nexoohub.almacen.inventario.entity.MovimientoInventario;
import com.nexoohub.almacen.inventario.repository.InventarioSucursalRepository;
import com.nexoohub.almacen.inventario.repository.MovimientoInventarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

/**
 * Servicio para gestión de traspasos entre sucursales.
 * 
 * <p>Maneja la lógica de negocio para transferir inventario entre sucursales,
 * incluyendo:</p>
 * <ul>
 *   <li>Validación de stock disponible</li>
 *   <li>Recálculo de costo promedio ponderado (CPP)</li>
 *   <li>Actualización automática de inventarios origen y destino</li>
 *   <li>Registro de movimientos para auditoría</li>
 * </ul>
 * 
 * @author NexooHub Development Team
 * @since 1.0
 */
@Service
public class TraspasoService {

    private final InventarioSucursalRepository inventarioRepository;
    private final MovimientoInventarioRepository movimientoRepository;
    private final UsuarioRepository usuarioRepository;

    /**
     * Constructor con inyección de dependencias.
     * 
     * @param inventarioRepository Repositorio de inventario por sucursal
     * @param movimientoRepository Repositorio de movimientos de inventario
     * @param usuarioRepository Repositorio de usuarios
     */
    public TraspasoService(
            InventarioSucursalRepository inventarioRepository,
            MovimientoInventarioRepository movimientoRepository,
            UsuarioRepository usuarioRepository) {
        this.inventarioRepository = inventarioRepository;
        this.movimientoRepository = movimientoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    /**
     * Ejecuta un traspaso de inventario entre sucursales con recálculo de costos.
     * 
     * <p>El traspaso actualiza automáticamente:</p>
     * <ul>
     *   <li>Stock de sucursal origen (resta)</li>
     *   <li>Stock de sucursal destino (suma)</li>
     *   <li>Costo Promedio Ponderado (CPP) en destino</li>
     *   <li>Registros de movimientos SALIDA_TRASPASO y ENTRADA_TRASPASO</li>
     * </ul>
     * 
     * @param request DTO con datos del traspaso (origen, destino, items)
     * @param username Username del usuario que autoriza el traspaso
     * @return ID de rastreo único para seguimiento del traspaso (formato: TR-XXXXXXXX)
     * @throws InvalidOperationException si origen y destino son la misma sucursal
     * @throws ResourceNotFoundException si el usuario o producto no existen
     * @throws StockInsuficienteException si no hay stock suficiente en origen
     */
    @Transactional
    public String ejecutarTraspaso(TraspasoRequestDTO request, String username) {
        if (request.getSucursalOrigenId().equals(request.getSucursalDestinoId())) {
            throw new InvalidOperationException(
                "TRASPASO", 
                "La sucursal de origen y destino no pueden ser la misma"
            );
        }

        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", username));

        String rastreoId = "TR-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        for (TraspasoRequestDTO.ItemTraspasoDTO item : request.getItems()) {
            
            // 1. VALIDAR ORIGEN
            InventarioSucursalId idOrigen = new InventarioSucursalId(request.getSucursalOrigenId(), item.getSkuInterno());
            InventarioSucursal invOrigen = inventarioRepository.findById(idOrigen)
                    .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("Producto '%s' no existe en sucursal origen %d", 
                                      item.getSkuInterno(), request.getSucursalOrigenId()),
                        "Producto",
                        item.getSkuInterno()
                    ));

            if (invOrigen.getStockActual() < item.getCantidad()) {
                throw new StockInsuficienteException(
                    item.getSkuInterno(),
                    request.getSucursalOrigenId(),
                    invOrigen.getStockActual(),
                    item.getCantidad()
                );
            }

            // 2. OBTENER O CREAR DESTINO
            InventarioSucursalId idDestino = new InventarioSucursalId(request.getSucursalDestinoId(), item.getSkuInterno());
            InventarioSucursal invDestino = inventarioRepository.findById(idDestino)
                    .orElseGet(() -> {
                        InventarioSucursal nuevo = new InventarioSucursal();
                        nuevo.setId(idDestino);
                        nuevo.setStockActual(0);
                        nuevo.setCostoPromedioPonderado(BigDecimal.ZERO);
                        return nuevo;
                    });

            // 3. TRANSFERENCIA DE COSTOS (MATEMÁTICA FINANCIERA)
            BigDecimal cppOrigen = invOrigen.getCostoPromedioPonderado();
            BigDecimal valorTransferido = cppOrigen.multiply(new BigDecimal(item.getCantidad()));
            
            BigDecimal stockDestinoViejo = new BigDecimal(invDestino.getStockActual());
            BigDecimal valorDestinoViejo = invDestino.getCostoPromedioPonderado().multiply(stockDestinoViejo);
            
            BigDecimal valorTotalDestino = valorDestinoViejo.add(valorTransferido);
            Integer nuevoStockDestino = invDestino.getStockActual() + item.getCantidad();
            
            BigDecimal nuevoCppDestino = valorTotalDestino.divide(new BigDecimal(nuevoStockDestino), 2, RoundingMode.HALF_UP);

            // 4. ACTUALIZAR INVENTARIOS (SUMAS Y RESTAS)
            invOrigen.setStockActual(invOrigen.getStockActual() - item.getCantidad());
            invDestino.setStockActual(nuevoStockDestino);
            invDestino.setCostoPromedioPonderado(nuevoCppDestino);

            inventarioRepository.save(invOrigen);
            inventarioRepository.save(invDestino);

            // 5. REGISTRO DE AUDITORÍA (BITÁCORA)
            // Salida
            MovimientoInventario salida = new MovimientoInventario();
            salida.setSkuInterno(item.getSkuInterno());
            salida.setSucursalId(request.getSucursalOrigenId());
            salida.setTipoMovimiento("SALIDA_TRASPASO");
            salida.setCantidad(item.getCantidad());
            salida.setRastreoId(rastreoId);
            salida.setUsuarioId(usuario.getId().intValue());
            salida.setComentarios(request.getComentarios());
            movimientoRepository.save(salida);

            // Entrada
            MovimientoInventario entrada = new MovimientoInventario();
            entrada.setSkuInterno(item.getSkuInterno());
            entrada.setSucursalId(request.getSucursalDestinoId());
            entrada.setTipoMovimiento("ENTRADA_TRASPASO");
            entrada.setCantidad(item.getCantidad());
            entrada.setRastreoId(rastreoId);
            salida.setUsuarioId(usuario.getId().intValue());
            entrada.setComentarios(request.getComentarios());
            movimientoRepository.save(entrada);
        }

        return rastreoId;
    }
}