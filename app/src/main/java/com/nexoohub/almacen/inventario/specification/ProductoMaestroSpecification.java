package com.nexoohub.almacen.inventario.specification;

import com.nexoohub.almacen.catalogo.entity.Categoria;
import com.nexoohub.almacen.catalogo.entity.CompatibilidadProducto;
import com.nexoohub.almacen.catalogo.entity.Moto;
import com.nexoohub.almacen.catalogo.entity.Proveedor;
import com.nexoohub.almacen.finanzas.entity.HistorialPrecio;
import com.nexoohub.almacen.inventario.entity.AnalisisABC;
import com.nexoohub.almacen.inventario.entity.InventarioSucursal;
import com.nexoohub.almacen.inventario.entity.ProductoMaestro;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ProductoMaestroSpecification {

    /**
     * Búsqueda dinámica avanzada de productos con múltiples criterios.
     *
     * @param termino          Búsqueda de texto en SKU, nombre, descripción o marca del producto
     * @param categoriaId      ID de categoría
     * @param nombreCategoria  Nombre de categoría (búsqueda por texto)
     * @param proveedorId      ID de proveedor
     * @param nombreProveedor  Nombre de empresa del proveedor (búsqueda por texto)
     * @param motoId           ID específico de moto
     * @param marcaMoto        Marca de moto (Honda, Yamaha, etc.)
     * @param modeloMoto       Modelo de moto (búsqueda por texto)
     * @param cilindrada       Cilindrada específica de moto
     * @param anio             Año de compatibilidad
     * @param soloActivos      Si true, solo retorna productos con activo=true
     * @param conStock         Si true, solo retorna productos con stock_actual > 0 en la sucursal indicada
     * @param sucursalIdStock  ID de sucursal para verificar stock disponible (requiere conStock=true)
     * @param precioMin        Precio mínimo de venta al público (historial_precio)
     * @param precioMax        Precio máximo de venta al público (historial_precio)
     * @param clasificacionAbc Clasificación ABC: 'A', 'B' o 'C' (requiere análisis previo)
     * @return Specification para filtrado dinámico
     */
    public static Specification<ProductoMaestro> busquedaDinamica(
            String termino,
            Integer categoriaId,
            String nombreCategoria,
            Integer proveedorId,
            String nombreProveedor,
            Integer motoId,
            String marcaMoto,
            String modeloMoto,
            Integer cilindrada,
            Integer anio,
            Boolean soloActivos,
            Boolean conStock,
            Integer sucursalIdStock,
            BigDecimal precioMin,
            BigDecimal precioMax,
            String clasificacionAbc) {

        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicados = new ArrayList<>();

            // ----------------------------------------------------------------
            // 1. Búsqueda por coincidencia de texto (SKU, Nombre, Descripción o Marca)
            // ----------------------------------------------------------------
            if (termino != null && !termino.trim().isEmpty()) {
                String busqueda = "%" + termino.toLowerCase() + "%";
                Predicate porSku         = criteriaBuilder.like(criteriaBuilder.lower(root.get("skuInterno")),      busqueda);
                Predicate porNombre      = criteriaBuilder.like(criteriaBuilder.lower(root.get("nombreComercial")), busqueda);
                Predicate porDescripcion = criteriaBuilder.like(criteriaBuilder.lower(root.get("descripcion")),     busqueda);
                Predicate porMarca       = criteriaBuilder.like(criteriaBuilder.lower(root.get("marca")),           busqueda);
                predicados.add(criteriaBuilder.or(porSku, porNombre, porDescripcion, porMarca));
            }

            // ----------------------------------------------------------------
            // 2. Filtro exacto por ID de Categoría
            // ----------------------------------------------------------------
            if (categoriaId != null) {
                predicados.add(criteriaBuilder.equal(root.get("categoriaId"), categoriaId));
            }

            // ----------------------------------------------------------------
            // 3. Filtro por nombre de Categoría (JOIN)
            // ----------------------------------------------------------------
            if (nombreCategoria != null && !nombreCategoria.trim().isEmpty()) {
                Join<ProductoMaestro, Categoria> categoriaJoin = root.join("categoria", JoinType.INNER);
                String busquedaCategoria = "%" + nombreCategoria.toLowerCase() + "%";
                predicados.add(criteriaBuilder.like(
                    criteriaBuilder.lower(categoriaJoin.get("nombre")),
                    busquedaCategoria
                ));
            }

            // ----------------------------------------------------------------
            // 4. Filtro exacto por ID de Proveedor
            // ----------------------------------------------------------------
            if (proveedorId != null) {
                predicados.add(criteriaBuilder.equal(root.get("proveedorId"), proveedorId));
            }

            // ----------------------------------------------------------------
            // 5. Filtro por nombre de Proveedor (JOIN)
            // ----------------------------------------------------------------
            if (nombreProveedor != null && !nombreProveedor.trim().isEmpty()) {
                Join<ProductoMaestro, Proveedor> proveedorJoin = root.join("proveedor", JoinType.INNER);
                String busquedaProveedor = "%" + nombreProveedor.toLowerCase() + "%";
                predicados.add(criteriaBuilder.like(
                    criteriaBuilder.lower(proveedorJoin.get("nombreEmpresa")),
                    busquedaProveedor
                ));
            }

            // ----------------------------------------------------------------
            // 6. Filtros de compatibilidad con motos (subquery)
            // ----------------------------------------------------------------
            if (motoId != null || marcaMoto != null || modeloMoto != null || cilindrada != null || anio != null) {
                Subquery<String> subquery = query.subquery(String.class);
                Root<CompatibilidadProducto> compatibilidadRoot = subquery.from(CompatibilidadProducto.class);
                subquery.select(compatibilidadRoot.get("skuInterno"));

                List<Predicate> subPredicados = new ArrayList<>();

                if (motoId != null) {
                    subPredicados.add(criteriaBuilder.equal(compatibilidadRoot.get("motoId"), motoId));
                }

                if (marcaMoto != null || modeloMoto != null || cilindrada != null) {
                    Join<CompatibilidadProducto, Moto> motoJoin = compatibilidadRoot.join("moto", JoinType.INNER);

                    if (marcaMoto != null && !marcaMoto.trim().isEmpty()) {
                        subPredicados.add(criteriaBuilder.like(
                            criteriaBuilder.lower(motoJoin.get("marca")),
                            "%" + marcaMoto.toLowerCase() + "%"
                        ));
                    }
                    if (modeloMoto != null && !modeloMoto.trim().isEmpty()) {
                        subPredicados.add(criteriaBuilder.like(
                            criteriaBuilder.lower(motoJoin.get("modelo")),
                            "%" + modeloMoto.toLowerCase() + "%"
                        ));
                    }
                    if (cilindrada != null) {
                        subPredicados.add(criteriaBuilder.equal(motoJoin.get("cilindrada"), cilindrada));
                    }
                }

                if (anio != null) {
                    Predicate inicio = criteriaBuilder.lessThanOrEqualTo(compatibilidadRoot.get("anioInicio"), anio);
                    Predicate fin    = criteriaBuilder.greaterThanOrEqualTo(compatibilidadRoot.get("anioFin"), anio);
                    subPredicados.add(criteriaBuilder.and(inicio, fin));
                }

                subquery.where(subPredicados.toArray(new Predicate[0]));
                predicados.add(criteriaBuilder.in(root.get("skuInterno")).value(subquery));
            }

            // ----------------------------------------------------------------
            // 7. NUEVO: Filtro por estado activo del producto
            // ----------------------------------------------------------------
            if (Boolean.TRUE.equals(soloActivos)) {
                predicados.add(criteriaBuilder.isTrue(root.get("activo")));
            }

            // ----------------------------------------------------------------
            // 8. NUEVO: Filtro por stock disponible en una sucursal específica
            // ----------------------------------------------------------------
            if (Boolean.TRUE.equals(conStock) && sucursalIdStock != null) {
                Subquery<String> subqueryStock = query.subquery(String.class);
                Root<InventarioSucursal> invRoot = subqueryStock.from(InventarioSucursal.class);
                subqueryStock.select(invRoot.get("id").get("skuInterno"));
                subqueryStock.where(
                    criteriaBuilder.and(
                        criteriaBuilder.equal(invRoot.get("id").get("sucursalId"), sucursalIdStock),
                        criteriaBuilder.greaterThan(invRoot.get("stockActual"), 0)
                    )
                );
                predicados.add(criteriaBuilder.in(root.get("skuInterno")).value(subqueryStock));
            }

            // ----------------------------------------------------------------
            // 9. NUEVO: Filtro por rango de precio de venta al público
            // ----------------------------------------------------------------
            if (precioMin != null || precioMax != null) {
                // Subquery que obtiene el precio más reciente de historial_precio por SKU
                // y filtra por el rango indicado
                Subquery<String> subqueryPrecio = query.subquery(String.class);
                Root<HistorialPrecio> hpRoot = subqueryPrecio.from(HistorialPrecio.class);
                subqueryPrecio.select(hpRoot.get("skuInterno"));

                // Subconsulta para obtener la fecha más reciente de cada SKU
                Subquery<LocalDateTime> subqueryMaxFecha = query.subquery(LocalDateTime.class);
                Root<HistorialPrecio> hpMaxRoot = subqueryMaxFecha.from(HistorialPrecio.class);
                subqueryMaxFecha.select(criteriaBuilder.greatest(hpMaxRoot.<LocalDateTime>get("fechaCalculo")));
                subqueryMaxFecha.where(criteriaBuilder.equal(hpMaxRoot.get("skuInterno"), hpRoot.get("skuInterno")));

                List<Predicate> precioPredicados = new ArrayList<>();
                precioPredicados.add(criteriaBuilder.equal(hpRoot.get("fechaCalculo"), subqueryMaxFecha));

                if (precioMin != null) {
                    precioPredicados.add(criteriaBuilder.greaterThanOrEqualTo(
                        hpRoot.get("precioFinalPublico"), precioMin));
                }
                if (precioMax != null) {
                    precioPredicados.add(criteriaBuilder.lessThanOrEqualTo(
                        hpRoot.get("precioFinalPublico"), precioMax));
                }
                subqueryPrecio.where(precioPredicados.toArray(new Predicate[0]));
                predicados.add(criteriaBuilder.in(root.get("skuInterno")).value(subqueryPrecio));
            }

            // ----------------------------------------------------------------
            // 10. NUEVO: Filtro por clasificación ABC (A, B o C)
            // ----------------------------------------------------------------
            if (clasificacionAbc != null && !clasificacionAbc.trim().isEmpty()) {
                Subquery<String> subqueryAbc = query.subquery(String.class);
                Root<AnalisisABC> abcRoot = subqueryAbc.from(AnalisisABC.class);
                subqueryAbc.select(abcRoot.get("skuProducto"));

                // Obtenemos el análisis ABC más reciente por SKU
                Subquery<java.time.LocalDate> subqueryMaxFechaAbc = query.subquery(java.time.LocalDate.class);
                Root<AnalisisABC> abcMaxRoot = subqueryMaxFechaAbc.from(AnalisisABC.class);
                subqueryMaxFechaAbc.select(criteriaBuilder.greatest(abcMaxRoot.<java.time.LocalDate>get("fechaAnalisis")));
                subqueryMaxFechaAbc.where(criteriaBuilder.equal(abcMaxRoot.get("skuProducto"), abcRoot.get("skuProducto")));

                subqueryAbc.where(
                    criteriaBuilder.and(
                        criteriaBuilder.equal(abcRoot.get("fechaAnalisis"), subqueryMaxFechaAbc),
                        criteriaBuilder.equal(
                            criteriaBuilder.upper(abcRoot.get("clasificacion")),
                            clasificacionAbc.toUpperCase()
                        )
                    )
                );
                predicados.add(criteriaBuilder.in(root.get("skuInterno")).value(subqueryAbc));
            }

            // Todos los filtros se unen con AND
            return criteriaBuilder.and(predicados.toArray(new Predicate[0]));
        };
    }
}
