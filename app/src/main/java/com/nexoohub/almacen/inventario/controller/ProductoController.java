package com.nexoohub.almacen.inventario.controller;

import com.nexoohub.almacen.common.ApiResponse;
import com.nexoohub.almacen.common.exception.ResourceNotFoundException;
import com.nexoohub.almacen.inventario.dto.ProductoMaestroResponseDTO;
import com.nexoohub.almacen.inventario.dto.ProductoResumenDTO;
import com.nexoohub.almacen.inventario.entity.ProductoMaestro;
import com.nexoohub.almacen.inventario.mapper.ProductoMaestroMapper;
import com.nexoohub.almacen.inventario.repository.ProductoMaestroRepository;
import com.nexoohub.almacen.inventario.specification.ProductoMaestroSpecification;

import jakarta.validation.Valid;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controller REST para gestión de productos maestros.
 * 
 * <p>Proporciona endpoints para CRUD completo de productos incluyendo:</p>
 * <ul>
 *   <li>Búsqueda dinámica con filtros omnicanal</li>
 *   <li>Búsqueda rápida para punto de venta</li>
 *   <li>Gestión completa de catálogo de productos</li>
 * </ul>
 * 
 * <p><b>Seguridad:</b> Todos los endpoints retornan DTOs en lugar de entidades
 * para evitar exposición de datos de auditoría.</p>
 * 
 * @author NexooHub Development Team
 * @version 1.0
 * @since 2026-03-06
 */
@RestController
@RequestMapping("/api/v1/productos")
@CrossOrigin(origins = "*")
public class ProductoController {
    
    // Definimos el logger para esta clase
    private static final Logger log = LoggerFactory.getLogger(ProductoController.class);

    @Autowired
    private ProductoMaestroRepository repository;

    @Autowired
    private ProductoMaestroRepository productoRepository;
    
    @Autowired
    private ProductoMaestroMapper mapper;

    /**
     * Motor de búsqueda omnicanal avanzado de productos.
     *
     * <p>Permite búsqueda dinámica con múltiples criterios:</p>
     * <ul>
     *   <li><b>q:</b> Búsqueda por texto en producto (SKU, nombre, descripción, marca)</li>
     *   <li><b>categoriaId:</b> Filtro por ID de categoría</li>
     *   <li><b>nombreCategoria:</b> Búsqueda por nombre de categoría</li>
     *   <li><b>proveedorId:</b> Filtro por ID de proveedor</li>
     *   <li><b>nombreProveedor:</b> Búsqueda por nombre de proveedor</li>
     *   <li><b>motoId:</b> Filtro por ID específico de moto</li>
     *   <li><b>marcaMoto:</b> Búsqueda por marca de moto (Honda, Yamaha, etc.)</li>
     *   <li><b>modeloMoto:</b> Búsqueda por modelo de moto (CBR, YZF, etc.)</li>
     *   <li><b>cilindrada:</b> Filtro por cilindrada específica (150, 200, 250, etc.)</li>
     *   <li><b>anio:</b> Filtro por año de compatibilidad</li>
     *   <li><b>soloActivos:</b> Si true, devuelve solo productos activos</li>
     *   <li><b>conStock:</b> Si true, devuelve solo productos con stock &gt; 0 en la sucursal indicada</li>
     *   <li><b>sucursalIdStock:</b> Sucursal en la que verificar disponibilidad de stock</li>
     *   <li><b>precioMin:</b> Precio mínimo de venta al público</li>
     *   <li><b>precioMax:</b> Precio máximo de venta al público</li>
     *   <li><b>clasificacionAbc:</b> Clasificación ABC (A, B o C)</li>
     * </ul>
     *
     * @return página de productos en formato DTO
     */
    @GetMapping("/search")
    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'ALMACENISTA', 'VENDEDOR', 'CAJERO')")
    public ResponseEntity<Page<ProductoMaestroResponseDTO>> buscarProductos(
            @RequestParam(value = "q",                required = false) String q,
            @RequestParam(value = "categoriaId",      required = false) Integer categoriaId,
            @RequestParam(value = "nombreCategoria",  required = false) String nombreCategoria,
            @RequestParam(value = "proveedorId",      required = false) Integer proveedorId,
            @RequestParam(value = "nombreProveedor",  required = false) String nombreProveedor,
            @RequestParam(value = "motoId",           required = false) Integer motoId,
            @RequestParam(value = "marcaMoto",        required = false) String marcaMoto,
            @RequestParam(value = "modeloMoto",       required = false) String modeloMoto,
            @RequestParam(value = "cilindrada",       required = false) Integer cilindrada,
            @RequestParam(value = "anio",             required = false) Integer anio,
            // --- Nuevos filtros SRCH-01 ---
            @RequestParam(value = "soloActivos",      required = false) Boolean soloActivos,
            @RequestParam(value = "conStock",         required = false) Boolean conStock,
            @RequestParam(value = "sucursalIdStock",  required = false) Integer sucursalIdStock,
            @RequestParam(value = "precioMin",        required = false) java.math.BigDecimal precioMin,
            @RequestParam(value = "precioMax",        required = false) java.math.BigDecimal precioMax,
            @RequestParam(value = "clasificacionAbc", required = false) String clasificacionAbc,
            @PageableDefault(size = 20, page = 0) Pageable pageable) {

        log.info("Búsqueda de productos: q={}, soloActivos={}, conStock={}, sucursal={}, precioMin={}, precioMax={}, abc={}",
                q, soloActivos, conStock, sucursalIdStock, precioMin, precioMax, clasificacionAbc);

        Specification<ProductoMaestro> spec = ProductoMaestroSpecification.busquedaDinamica(
            q, categoriaId, nombreCategoria, proveedorId, nombreProveedor,
            motoId, marcaMoto, modeloMoto, cilindrada, anio,
            soloActivos, conStock, sucursalIdStock, precioMin, precioMax, clasificacionAbc
        );

        Page<ProductoMaestro> resultados = productoRepository.findAll(spec, pageable);
        Page<ProductoMaestroResponseDTO> dtos = resultados.map(mapper::toResponseDTO);
        return ResponseEntity.ok(dtos);
    }

    /**
     * Crea un nuevo producto maestro en el catálogo.
     * 
     * @param producto datos del producto a crear (validados con @Valid)
     * @return respuesta con SKU generado y mensaje de éxito
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")
    public ResponseEntity<Map<String, Object>> crearProducto(@Valid @RequestBody ProductoMaestro producto) {
        ProductoMaestro guardado = productoRepository.save(producto);
        
        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("exitoso", true);
        respuesta.put("mensaje", "Producto creado correctamente");
        respuesta.put("skuInterno", guardado.getSkuInterno());
        
        return new ResponseEntity<>(respuesta, HttpStatus.CREATED);
    }

    /**
     * Obtiene un producto específico por su SKU.
     * 
     * @param sku identificador único del producto
     * @return producto en formato DTO
     * @throws ResourceNotFoundException si el SKU no existe
     */
    @GetMapping("/{sku}")
    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'ALMACENISTA', 'VENDEDOR', 'CAJERO')")
    public ResponseEntity<ApiResponse<ProductoMaestroResponseDTO>> obtenerPorSku(@PathVariable("sku") String sku) {
        log.info("Consultando producto con SKU: {}", sku);
        ProductoMaestro producto = repository.findById(sku)
            .orElseThrow(() -> new ResourceNotFoundException("El SKU " + sku + " no existe."));
        return ResponseEntity.ok(new ApiResponse<>("Producto encontrado", mapper.toResponseDTO(producto)));
    }

    /**
     * Actualiza información de un producto existente.
     * 
     * <p><b>Nota:</b> El SKU no se actualiza porque es la clave primaria.</p>
     * 
     * @param sku identificador del producto a actualizar
     * @param detalles nuevos datos del producto
     * @return producto actualizado en formato DTO
     * @throws ResourceNotFoundException si el SKU no existe
     */
    @PutMapping("/{sku}")
    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")
    public ResponseEntity<ApiResponse<ProductoMaestroResponseDTO>> actualizar(
            @PathVariable("sku") String sku, 
            @Valid @RequestBody ProductoMaestro detalles) {
        log.info("Actualizando producto SKU: {}", sku);
        ProductoMaestro productoExistente = repository.findById(sku)
            .orElseThrow(() -> new ResourceNotFoundException("El SKU " + sku + " no existe."));
        // Actualizamos los campos permitidos
        productoExistente.setNombreComercial(detalles.getNombreComercial());
        productoExistente.setClaveSat(detalles.getClaveSat());
        productoExistente.setStockMinimoGlobal(detalles.getStockMinimoGlobal());
        // El SKU interno NO se actualiza porque es la llave primaria
        ProductoMaestro actualizado = repository.save(productoExistente);
        return ResponseEntity.ok(new ApiResponse<>("Producto actualizado con éxito", mapper.toResponseDTO(actualizado)));
    }

    /**
     * Elimina un producto del catálogo de forma permanente.
     * 
     * <p><b>Advertencia:</b> Esta operación es irreversible y puede afectar
     * referencias en inventario y compatibilidades.</p>
     * 
     * @param sku identificador del producto a eliminar
     * @return confirmación de eliminación
     * @throws ResourceNotFoundException si el SKU no existe
     */
    @DeleteMapping("/{sku}")
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable("sku") String sku) {
        log.info("Intento de eliminar producto: SKU {}", sku);
        ProductoMaestro producto = repository.findById(sku)
            .orElseThrow(() -> new ResourceNotFoundException("No se puede eliminar: El SKU " + sku + " no existe"));
        repository.delete(producto);
        log.info("Producto eliminado: SKU {}", sku);
        
        return ResponseEntity.ok(new ApiResponse<>("Producto eliminado correctamente", null));
    }

    @GetMapping("/mostrador")
    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR', 'CAJERO')")
    public ResponseEntity<List<ProductoResumenDTO>> buscarParaMostrador(
            @RequestParam("q") String q,
            @RequestParam("sucursalId") Integer sucursalId) {
        return ResponseEntity.ok(productoRepository.buscarParaMostrador(q, sucursalId));
    }

}
