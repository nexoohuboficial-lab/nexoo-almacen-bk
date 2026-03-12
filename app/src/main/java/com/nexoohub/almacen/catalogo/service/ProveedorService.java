package com.nexoohub.almacen.catalogo.service;

import com.nexoohub.almacen.catalogo.entity.Proveedor;
import com.nexoohub.almacen.catalogo.repository.ProveedorRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Servicio de Proveedores con cache Redis.
 * 
 * CACHE STRATEGY:
 * - TTL: 1 hora (puede haber altas/bajas esporádicas)
 * - Eviction: Al crear/actualizar se limpia TODO el cache
 * - Beneficio: ~80% reducción queries (consultas frecuentes en módulo compras)
 * 
 * Phase 3.3: Performance Optimization - Redis Caching
 */
@Service
@Transactional(readOnly = true)
public class ProveedorService {

    private final ProveedorRepository proveedorRepository;

    public ProveedorService(ProveedorRepository proveedorRepository) {
        this.proveedorRepository = proveedorRepository;
    }

    @Cacheable(value = "proveedores", key = "'page-' + #pageable.pageNumber + '-size-' + #pageable.pageSize + '-sort-' + #pageable.sort.toString()")
    public Page<Proveedor> listarProveedores(Pageable pageable) {
        return proveedorRepository.findAll(pageable);
    }

    @Cacheable(value = "proveedores", key = "'id-' + #id")
    public Optional<Proveedor> buscarPorId(Integer id) {
        return proveedorRepository.findById(id);
    }

    @Transactional
    @CacheEvict(value = "proveedores", allEntries = true)
    public Proveedor crear(Proveedor proveedor) {
        return proveedorRepository.save(proveedor);
    }

    @Transactional
    @CacheEvict(value = "proveedores", allEntries = true)
    public Proveedor actualizar(Proveedor proveedor) {
        return proveedorRepository.save(proveedor);
    }
}
