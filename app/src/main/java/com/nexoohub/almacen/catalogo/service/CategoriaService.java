package com.nexoohub.almacen.catalogo.service;

import com.nexoohub.almacen.catalogo.entity.Categoria;
import com.nexoohub.almacen.catalogo.repository.CategoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Servicio de Categorías con cache Redis.
 * 
 * CACHE STRATEGY:
 * - TTL: 2 horas (categorías cambian raramente)
 * - Eviction: Al crear/actualizar categoría se limpia TODO el cache
 * - Beneficio: 90-95% reducción en queries a BD para listados
 * 
 * COMPORTAMIENTO SIN REDIS:
 * - Las anotaciones @Cacheable/@CacheEvict se ignoran
 * - Funciona normal, solo más lento (consulta BD cada vez)
 * 
 * Phase 3.3: Performance Optimization - Redis Caching
 */
@Service
@Transactional(readOnly = true)
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

    public CategoriaService(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    /**
     * Listar todas las categorías con paginación.
     * Cache key: "categorias::page-{pageNumber}-size-{pageSize}-sort-{sort}"
     */
    @Cacheable(value = "categorias", key = "'page-' + #pageable.pageNumber + '-size-' + #pageable.pageSize + '-sort-' + #pageable.sort.toString()")
    public Page<Categoria> listarCategorias(Pageable pageable) {
        return categoriaRepository.findAll(pageable);
    }

    /**
     * Buscar categoría por ID.
     * Cache key: "categorias::id-{id}"
     */
    @Cacheable(value = "categorias", key = "'id-' + #id")
    public Optional<Categoria> buscarPorId(Integer id) {
        return categoriaRepository.findById(id);
    }

    /**
     * Crear nueva categoría.
     * Limpia TODO el cache de categorías (para reflejar cambios inmediatamente)
     */
    @Transactional
    @CacheEvict(value = "categorias", allEntries = true)
    public Categoria crear(Categoria categoria) {
        return categoriaRepository.save(categoria);
    }

    /**
     * Actualizar categoría existente.
     * Limpia TODO el cache de categorías
     */
    @Transactional
    @CacheEvict(value = "categorias", allEntries = true)
    public Categoria actualizar(Categoria categoria) {
        return categoriaRepository.save(categoria);
    }
}
