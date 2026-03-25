package com.nexoohub.almacen.inventario.service;

import com.nexoohub.almacen.common.exception.ResourceNotFoundException;
import com.nexoohub.almacen.inventario.dto.*;
import com.nexoohub.almacen.inventario.entity.CodigoBarrasProducto;
import com.nexoohub.almacen.inventario.entity.InventarioSucursal;
import com.nexoohub.almacen.inventario.entity.InventarioSucursalId;
import com.nexoohub.almacen.inventario.entity.ProductoMaestro;
import com.nexoohub.almacen.inventario.repository.CodigoBarrasProductoRepository;
import com.nexoohub.almacen.inventario.repository.InventarioSucursalRepository;
import com.nexoohub.almacen.inventario.repository.ProductoMaestroRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EscaneoService {

    private static final Logger log = LoggerFactory.getLogger(EscaneoService.class);

    private final CodigoBarrasProductoRepository codigoBarrasRepo;
    private final ProductoMaestroRepository productoRepo;
    private final InventarioSucursalRepository inventarioRepo;

    public EscaneoService(CodigoBarrasProductoRepository codigoBarrasRepo,
                          ProductoMaestroRepository productoRepo,
                          InventarioSucursalRepository inventarioRepo) {
        this.codigoBarrasRepo = codigoBarrasRepo;
        this.productoRepo = productoRepo;
        this.inventarioRepo = inventarioRepo;
    }

    // ========================================================================
    // ENDPOINT UNIVERSAL DE ESCANEO
    // ========================================================================

    @Transactional(readOnly = true)
    public EscaneoResponse procesarEscaneo(EscaneoRequest req) {
        log.info("Procesando escaneo: codigo={}, contexto={}, sucursal={}",
                req.getCodigo(), req.getContexto(), req.getSucursalId());

        Optional<CodigoBarrasProducto> codBarras =
                codigoBarrasRepo.findByCodigoAndActivoTrue(req.getCodigo());

        if (codBarras.isEmpty()) {
            // El código no está registrado en el sistema
            EscaneoResponse resp = new EscaneoResponse();
            resp.setResultado("PRODUCTO_DESCONOCIDO");
            resp.setCodigoEscaneado(req.getCodigo());
            resp.setAccionSugerida("REGISTRAR_NUEVO_PRODUCTO");
            return resp;
        }

        // Código encontrado → cargar el producto
        CodigoBarrasProducto cb = codBarras.get();
        ProductoMaestro producto = productoRepo.findById(cb.getSkuInterno())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Producto no encontrado para SKU: " + cb.getSkuInterno()));

        EscaneoResponse resp = new EscaneoResponse();
        resp.setResultado("ENCONTRADO");
        resp.setCodigoEscaneado(req.getCodigo());
        resp.setSkuInterno(producto.getSkuInterno());
        resp.setNombreComercial(producto.getNombreComercial());
        resp.setMarca(producto.getMarca());

        // Stock en la sucursal solicitada
        InventarioSucursalId invId = new InventarioSucursalId(
                req.getSucursalId(), producto.getSkuInterno());
        Optional<InventarioSucursal> inv = inventarioRepo.findById(invId);
        resp.setStockEnSucursal(inv.map(InventarioSucursal::getStockActual).orElse(0));

        // Acción sugerida según contexto
        switch (req.getContexto().toUpperCase()) {
            case "COMPRA":
                resp.setAccionSugerida("AGREGAR_A_COMPRA");
                break;
            case "VENTA":
                resp.setAccionSugerida("AGREGAR_A_VENTA");
                break;
            case "INVENTARIO":
                resp.setAccionSugerida("ACTUALIZAR_STOCK");
                break;
            case "GARANTIA":
                resp.setAccionSugerida("ABRIR_TICKET_GARANTIA");
                break;
            default:
                resp.setAccionSugerida("VER_DETALLE");
        }

        return resp;
    }

    // ========================================================================
    // GESTIÓN DE CÓDIGOS DE BARRAS
    // ========================================================================

    @Transactional
    public CodigoBarrasResponse vincularCodigo(String skuInterno, CodigoBarrasRequest req) {
        log.info("Vinculando código {} al producto {}", req.getCodigo(), skuInterno);

        // Validar que el producto existe
        if (!productoRepo.existsById(skuInterno)) {
            throw new ResourceNotFoundException("Producto no encontrado: " + skuInterno);
        }

        // Validar que el código no esté ya registrado
        if (codigoBarrasRepo.existsByCodigo(req.getCodigo())) {
            throw new IllegalStateException(
                    "El código de barras ya está registrado: " + req.getCodigo());
        }

        CodigoBarrasProducto cb = new CodigoBarrasProducto();
        cb.setSkuInterno(skuInterno);
        cb.setCodigo(req.getCodigo());
        cb.setTipo(req.getTipo());
        cb.setEsPrincipal(req.getEsPrincipal() != null ? req.getEsPrincipal() : false);

        return mapToResponse(codigoBarrasRepo.save(cb));
    }

    @Transactional(readOnly = true)
    public List<CodigoBarrasResponse> listarCodigos(String skuInterno) {
        return codigoBarrasRepo.findBySkuInternoAndActivoTrue(skuInterno)
                .stream().map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void desactivarCodigo(Integer id) {
        CodigoBarrasProducto cb = codigoBarrasRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Código de barras no encontrado: " + id));
        cb.setActivo(false);
        codigoBarrasRepo.save(cb);
        log.info("Código de barras desactivado: id={}, codigo={}", id, cb.getCodigo());
    }

    // ========================================================================
    // IMPORTACIÓN MASIVA
    // ========================================================================

    @Transactional
    public ImportacionMasivaResponse importarMasivo(List<ImportacionMasivaItemRequest> items) {
        log.info("Importación masiva: {} productos recibidos", items.size());

        List<ImportacionMasivaResponse.FilaResultado> detalle = new ArrayList<>();
        int exitosos = 0;
        int fallidos = 0;

        for (ImportacionMasivaItemRequest item : items) {
            try {
                // 1. Crear o verificar producto
                ProductoMaestro producto;
                if (productoRepo.existsById(item.getSkuInterno())) {
                    producto = productoRepo.findById(item.getSkuInterno()).get();
                } else {
                    producto = new ProductoMaestro();
                    producto.setSkuInterno(item.getSkuInterno());
                    producto.setSkuProveedor(item.getSkuProveedor());
                    producto.setNombreComercial(item.getNombreComercial());
                    producto.setDescripcion(item.getDescripcion());
                    producto.setMarca(item.getMarca());
                    producto.setClaveSat(item.getClaveSat());
                    producto.setStockMinimoGlobal(
                            item.getStockMinimoGlobal() != null ? item.getStockMinimoGlobal() : 2);
                    productoRepo.save(producto);
                }

                // 2. Vincular códigos de barras
                if (item.getCodigosBarras() != null) {
                    for (CodigoBarrasRequest cbReq : item.getCodigosBarras()) {
                        if (!codigoBarrasRepo.existsByCodigo(cbReq.getCodigo())) {
                            CodigoBarrasProducto cb = new CodigoBarrasProducto();
                            cb.setSkuInterno(item.getSkuInterno());
                            cb.setCodigo(cbReq.getCodigo());
                            cb.setTipo(cbReq.getTipo());
                            cb.setEsPrincipal(cbReq.getEsPrincipal() != null
                                    ? cbReq.getEsPrincipal() : false);
                            codigoBarrasRepo.save(cb);
                        }
                    }
                }

                detalle.add(new ImportacionMasivaResponse.FilaResultado(
                        item.getSkuInterno(), true, "Producto creado/actualizado correctamente"));
                exitosos++;

            } catch (Exception e) {
                detalle.add(new ImportacionMasivaResponse.FilaResultado(
                        item.getSkuInterno(), false, e.getMessage()));
                fallidos++;
                log.warn("Error importando producto {}: {}", item.getSkuInterno(), e.getMessage());
            }
        }

        ImportacionMasivaResponse resp = new ImportacionMasivaResponse();
        resp.setTotalProcesados(items.size());
        resp.setTotalExitosos(exitosos);
        resp.setTotalFallidos(fallidos);
        resp.setDetalle(detalle);

        log.info("Importación masiva finalizada: {} exitosos, {} fallidos de {} total",
                exitosos, fallidos, items.size());
        return resp;
    }

    // ========================================================================
    // MAPPER
    // ========================================================================

    private CodigoBarrasResponse mapToResponse(CodigoBarrasProducto cb) {
        CodigoBarrasResponse r = new CodigoBarrasResponse();
        r.setId(cb.getId());
        r.setSkuInterno(cb.getSkuInterno());
        r.setCodigo(cb.getCodigo());
        r.setTipo(cb.getTipo());
        r.setActivo(cb.getActivo());
        r.setEsPrincipal(cb.getEsPrincipal());
        r.setFechaCreacion(cb.getFechaCreacion());
        return r;
    }
}
