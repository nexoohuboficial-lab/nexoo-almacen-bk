package com.nexoohub.almacen.inventario.service;

import com.nexoohub.almacen.inventario.dto.TraspasoRequestDTO;
import com.nexoohub.almacen.inventario.entity.InventarioSucursal;
import com.nexoohub.almacen.inventario.entity.InventarioSucursalId;
import com.nexoohub.almacen.inventario.entity.MovimientoInventario;
import com.nexoohub.almacen.inventario.repository.InventarioSucursalRepository;
import com.nexoohub.almacen.inventario.repository.MovimientoInventarioRepository;
import com.nexoohub.almacen.common.entity.Usuario;
import com.nexoohub.almacen.common.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

@Service
public class TraspasoService {

    @Autowired private InventarioSucursalRepository inventarioRepository;
    @Autowired private MovimientoInventarioRepository movimientoRepository;
    @Autowired private UsuarioRepository usuarioRepository;

    @Transactional
    public String ejecutarTraspaso(TraspasoRequestDTO request, String username) {
        if (request.getSucursalOrigenId().equals(request.getSucursalDestinoId())) {
            throw new RuntimeException("La sucursal de origen y destino no pueden ser la misma.");
        }

        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        String rastreoId = "TR-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        for (TraspasoRequestDTO.ItemTraspasoDTO item : request.getItems()) {
            
            // 1. VALIDAR ORIGEN
            InventarioSucursalId idOrigen = new InventarioSucursalId(request.getSucursalOrigenId(), item.getSkuInterno());
            InventarioSucursal invOrigen = inventarioRepository.findById(idOrigen)
                    .orElseThrow(() -> new RuntimeException("El producto " + item.getSkuInterno() + " no existe en la sucursal de origen."));

            if (invOrigen.getStockActual() < item.getCantidad()) {
                throw new RuntimeException("Stock insuficiente en origen para " + item.getSkuInterno() + ". Disponible: " + invOrigen.getStockActual());
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