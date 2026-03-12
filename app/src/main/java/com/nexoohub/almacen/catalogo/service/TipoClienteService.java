package com.nexoohub.almacen.catalogo.service;

import com.nexoohub.almacen.catalogo.entity.TipoCliente;
import com.nexoohub.almacen.catalogo.repository.TipoClienteRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Servicio de Tipos de Cliente con cache Redis.
 * 
 * CACHE STRATEGY:
 * - TTL: 2 horas (tipos de cliente son catálogo estático)
 * - Eviction: Al crear/actualizar se limpia TODO el cache
 * - Beneficio: ~95% reducción queries (raramente cambia)
 * 
 * Phase 3.3: Performance Optimization - Redis Caching
 */
@Service
@Transactional(readOnly = true)
public class TipoClienteService {

    private final TipoClienteRepository tipoClienteRepository;

    public TipoClienteService(TipoClienteRepository tipoClienteRepository) {
        this.tipoClienteRepository = tipoClienteRepository;
    }

    @Cacheable(value = "tiposCliente", key = "'page-' + #pageable.pageNumber + '-size-' + #pageable.pageSize + '-sort-' + #pageable.sort.toString()")
    public Page<TipoCliente> listarTiposCliente(Pageable pageable) {
        return tipoClienteRepository.findAll(pageable);
    }

    @Cacheable(value = "tiposCliente", key = "'id-' + #id")
    public Optional<TipoCliente> buscarPorId(Integer id) {
        return tipoClienteRepository.findById(id);
    }

    @Transactional
    @CacheEvict(value = "tiposCliente", allEntries = true)
    public TipoCliente crear(TipoCliente tipoCliente) {
        return tipoClienteRepository.save(tipoCliente);
    }

    @Transactional
    @CacheEvict(value = "tiposCliente", allEntries = true)
    public TipoCliente actualizar(TipoCliente tipoCliente) {
        return tipoClienteRepository.save(tipoCliente);
    }
}
