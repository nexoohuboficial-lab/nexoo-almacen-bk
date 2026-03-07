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
import org.springframework.web.bind.annotation.*;

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
     * </ul>
     * 
     * <p><b>Ejemplos de uso:</b></p>
     * <ul>
     *   <li>Buscar aceites para Honda: ?marcaMoto=Honda&amp;nombreCategoria=Aceite</li>
     *   <li>Buscar productos de un proveedor: ?nombreProveedor=Motul</li>
     *   <li>Buscar para moto 250cc del 2020: ?cilindrada=250&amp;anio=2020</li>
     * </ul>
     * 
     * @param q texto de búsqueda en producto
     * @param categoriaId ID de categoría
     * @param nombreCategoria nombre de categoría
     * @param proveedorId ID de proveedor
     * @param nombreProveedor nombre de proveedor
     * @param motoId ID de moto
     * @param marcaMoto marca de moto
     * @param modeloMoto modelo de moto
     * @param cilindrada cilindrada de moto
     * @param anio año de compatibilidad
     * @param pageable configuración de paginación (default: 20 items)
     * @return página de productos en formato DTO
     */
    @GetMapping("/search")
    @Transactional(readOnly = true)
    public ResponseEntity<Page<ProductoMaestroResponseDTO>> buscarProductos(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Integer categoriaId,
            @RequestParam(required = false) String nombreCategoria,
            @RequestParam(required = false) Integer proveedorId,
            @RequestParam(required = false) String nombreProveedor,
            @RequestParam(required = false) Integer motoId,
            @RequestParam(required = false) String marcaMoto,
            @RequestParam(required = false) String modeloMoto,
            @RequestParam(required = false) Integer cilindrada,
            @RequestParam(required = false) Integer anio,
            @PageableDefault(size = 20, page = 0) Pageable pageable) {
        
        // El Cerebro arma la consulta SQL dinámicamente con todos los filtros
        Specification<ProductoMaestro> spec = ProductoMaestroSpecification.busquedaDinamica(
            q, categoriaId, nombreCategoria, proveedorId, nombreProveedor,
            motoId, marcaMoto, modeloMoto, cilindrada, anio
        );
        
        // El Repositorio ejecuta la consulta con los límites de paginación
        Page<ProductoMaestro> resultados = productoRepository.findAll(spec, pageable);
        
        // Convertimos a DTO antes de devolver
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
    public ResponseEntity<ApiResponse<ProductoMaestroResponseDTO>> obtenerPorSku(@PathVariable String sku) {
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
    public ResponseEntity<ApiResponse<ProductoMaestroResponseDTO>> actualizar(
            @PathVariable String sku, 
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
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable String sku) {
        log.info("Intento de eliminar producto: SKU {}", sku);
        ProductoMaestro producto = repository.findById(sku)
            .orElseThrow(() -> new ResourceNotFoundException("No se puede eliminar: El SKU " + sku + " no existe"));
        repository.delete(producto);
        log.info("Producto eliminado: SKU {}", sku);
        
        return ResponseEntity.ok(new ApiResponse<>("Producto eliminado correctamente", null));
    }

    @GetMapping("/mostrador")
    @Transactional(readOnly = true)
    public ResponseEntity<List<ProductoResumenDTO>> buscarParaMostrador(
            @RequestParam String q,
            @RequestParam Integer sucursalId) {
        return ResponseEntity.ok(productoRepository.buscarParaMostrador(q, sucursalId));
    }

}
