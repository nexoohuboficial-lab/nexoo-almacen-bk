package com.nexoohub.almacen.inventario.specification;

import com.nexoohub.almacen.catalogo.entity.Categoria;
import com.nexoohub.almacen.catalogo.entity.CompatibilidadProducto;
import com.nexoohub.almacen.catalogo.entity.Moto;
import com.nexoohub.almacen.catalogo.entity.Proveedor;
import com.nexoohub.almacen.inventario.entity.ProductoMaestro;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.*;

import java.util.ArrayList;
import java.util.List;

public class ProductoMaestroSpecification {

    /**
     * Búsqueda dinámica avanzada de productos con múltiples criterios.
     * 
     * @param termino Búsqueda de texto en SKU, nombre, descripción o marca del producto
     * @param categoriaId ID de categoría
     * @param nombreCategoria Nombre de categoría (búsqueda por texto)
     * @param proveedorId ID de proveedor
     * @param nombreProveedor Nombre de empresa del proveedor (búsqueda por texto)
     * @param motoId ID específico de moto
     * @param marcaMoto Marca de moto (Honda, Yamaha, etc.)
     * @param modeloMoto Modelo de moto (búsqueda por texto)
     * @param cilindrada Cilindrada específica de moto
     * @param anio Año de compatibilidad
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
            Integer anio) {
        
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicados = new ArrayList<>();

            // 1. Búsqueda por coincidencia de texto (SKU, Nombre, Descripción o Marca del producto)
            if (termino != null && !termino.trim().isEmpty()) {
                String busqueda = "%" + termino.toLowerCase() + "%";
                Predicate porSku = criteriaBuilder.like(criteriaBuilder.lower(root.get("skuInterno")), busqueda);
                Predicate porNombre = criteriaBuilder.like(criteriaBuilder.lower(root.get("nombreComercial")), busqueda);
                Predicate porDescripcion = criteriaBuilder.like(criteriaBuilder.lower(root.get("descripcion")), busqueda);
                Predicate porMarca = criteriaBuilder.like(criteriaBuilder.lower(root.get("marca")), busqueda);
                
                // Agrupamos con OR (que coincida con cualquiera de los 4)
                predicados.add(criteriaBuilder.or(porSku, porNombre, porDescripcion, porMarca));
            }

            // 2. Filtro exacto por ID de Categoría
            if (categoriaId != null) {
                predicados.add(criteriaBuilder.equal(root.get("categoriaId"), categoriaId));
            }
            
            // 3. Filtro por nombre de Categoría (mediante JOIN)
            if (nombreCategoria != null && !nombreCategoria.trim().isEmpty()) {
                Join<ProductoMaestro, Categoria> categoriaJoin = root.join("categoria", JoinType.INNER);
                String busquedaCategoria = "%" + nombreCategoria.toLowerCase() + "%";
                predicados.add(criteriaBuilder.like(
                    criteriaBuilder.lower(categoriaJoin.get("nombre")), 
                    busquedaCategoria
                ));
            }
            
            // 4. Filtro exacto por ID de Proveedor
            if (proveedorId != null) {
                predicados.add(criteriaBuilder.equal(root.get("proveedorId"), proveedorId));
            }
            
            // 5. Filtro por nombre de Proveedor (mediante JOIN)
            if (nombreProveedor != null && !nombreProveedor.trim().isEmpty()) {
                Join<ProductoMaestro, Proveedor> proveedorJoin = root.join("proveedor", JoinType.INNER);
                String busquedaProveedor = "%" + nombreProveedor.toLowerCase() + "%";
                predicados.add(criteriaBuilder.like(
                    criteriaBuilder.lower(proveedorJoin.get("nombreEmpresa")), 
                    busquedaProveedor
                ));
            }

            // 6. Filtros de compatibilidad con motos (mediante subquery)
            if (motoId != null || marcaMoto != null || modeloMoto != null || cilindrada != null || anio != null) {
                // Hacemos un SubQuery que cruza con compatibilidad_producto y moto
                Subquery<String> subquery = query.subquery(String.class);
                Root<CompatibilidadProducto> compatibilidadRoot = subquery.from(CompatibilidadProducto.class);
                subquery.select(compatibilidadRoot.get("skuInterno"));

                List<Predicate> subPredicados = new ArrayList<>();
                
                // 6.1 Filtro por ID específico de moto
                if (motoId != null) {
                    subPredicados.add(criteriaBuilder.equal(compatibilidadRoot.get("motoId"), motoId));
                }
                
                // 6.2 Filtros adicionales de moto (requieren JOIN con la tabla moto)
                if (marcaMoto != null || modeloMoto != null || cilindrada != null) {
                    Join<CompatibilidadProducto, Moto> motoJoin = compatibilidadRoot.join("moto", JoinType.INNER);
                    
                    // Marca de moto (Honda, Yamaha, etc.)
                    if (marcaMoto != null && !marcaMoto.trim().isEmpty()) {
                        String busquedaMarcaMoto = "%" + marcaMoto.toLowerCase() + "%";
                        subPredicados.add(criteriaBuilder.like(
                            criteriaBuilder.lower(motoJoin.get("marca")), 
                            busquedaMarcaMoto
                        ));
                    }
                    
                    // Modelo de moto (CBR, YZF, etc.)
                    if (modeloMoto != null && !modeloMoto.trim().isEmpty()) {
                        String busquedaModeloMoto = "%" + modeloMoto.toLowerCase() + "%";
                        subPredicados.add(criteriaBuilder.like(
                            criteriaBuilder.lower(motoJoin.get("modelo")), 
                            busquedaModeloMoto
                        ));
                    }
                    
                    // Cilindrada específica (150, 200, 250, etc.)
                    if (cilindrada != null) {
                        subPredicados.add(criteriaBuilder.equal(motoJoin.get("cilindrada"), cilindrada));
                    }
                }
                
                // 6.3 Filtro por año de compatibilidad
                if (anio != null) {
                    // El año buscado debe estar en el rango [anioInicio, anioFin]
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
