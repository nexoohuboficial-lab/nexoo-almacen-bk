package com.nexoohub.almacen.inventario.specification;

import com.nexoohub.almacen.catalogo.entity.CompatibilidadProducto;
import com.nexoohub.almacen.inventario.entity.ProductoMaestro;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.*;

import java.util.ArrayList;
import java.util.List;

public class ProductoMaestroSpecification {

    public static Specification<ProductoMaestro> busquedaDinamica(String termino, Integer categoriaId, Integer motoId, Integer anio) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicados = new ArrayList<>();

            // 1. Búsqueda por coincidencia de texto (SKU, Nombre o Descripción)
            if (termino != null && !termino.trim().isEmpty()) {
                String busqueda = "%" + termino.toLowerCase() + "%";
                Predicate porSku = criteriaBuilder.like(criteriaBuilder.lower(root.get("skuInterno")), busqueda);
                Predicate porNombre = criteriaBuilder.like(criteriaBuilder.lower(root.get("nombreComercial")), busqueda);
                Predicate porDescripcion = criteriaBuilder.like(criteriaBuilder.lower(root.get("descripcion")), busqueda);
                
                // Agrupamos con OR (que coincida con cualquiera de los 3)
                predicados.add(criteriaBuilder.or(porSku, porNombre, porDescripcion));
            }

            // 2. Filtro exacto por Categoría
            if (categoriaId != null) {
                predicados.add(criteriaBuilder.equal(root.get("categoriaId"), categoriaId));
            }

            // 3. El cruce mágico con la tabla de Compatibilidad y Años
            if (motoId != null || anio != null) {
                // Hacemos un SubQuery (SELECT sku_interno FROM compatibilidad_producto WHERE ...)
                Subquery<String> subquery = query.subquery(String.class);
                Root<CompatibilidadProducto> compatibilidadRoot = subquery.from(CompatibilidadProducto.class);
                subquery.select(compatibilidadRoot.get("skuInterno"));

                List<Predicate> subPredicados = new ArrayList<>();
                
                if (motoId != null) {
                    subPredicados.add(criteriaBuilder.equal(compatibilidadRoot.get("motoId"), motoId));
                }
                
                if (anio != null) {
                    // El año buscado debe ser >= anioInicio AND <= anioFin
                    Predicate inicio = criteriaBuilder.lessThanOrEqualTo(compatibilidadRoot.get("anioInicio"), anio);
                    Predicate fin = criteriaBuilder.greaterThanOrEqualTo(compatibilidadRoot.get("anioFin"), anio);
                    subPredicados.add(criteriaBuilder.and(inicio, fin));
                }
                
                subquery.where(subPredicados.toArray(new Predicate[0]));
                
                // Le decimos a la consulta principal: "Tráeme los productos cuyo SKU esté en este subquery"
                predicados.add(criteriaBuilder.in(root.get("skuInterno")).value(subquery));
            }

            // Unimos todos los filtros con AND
            return criteriaBuilder.and(predicados.toArray(new Predicate[0]));
        };
    }
}
