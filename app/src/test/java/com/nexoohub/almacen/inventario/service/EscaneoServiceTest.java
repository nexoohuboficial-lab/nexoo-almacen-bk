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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("EscaneoService — BC-01 Pruebas Unitarias")
class EscaneoServiceTest {

    @Mock private CodigoBarrasProductoRepository codigoBarrasRepo;
    @Mock private ProductoMaestroRepository productoRepo;
    @Mock private InventarioSucursalRepository inventarioRepo;

    @InjectMocks
    private EscaneoService service;

    private ProductoMaestro producto;
    private CodigoBarrasProducto codigoBarras;
    private InventarioSucursal inventario;

    @BeforeEach
    void setUp() {
        producto = new ProductoMaestro();
        producto.setSkuInterno("BRAKE-01");
        producto.setNombreComercial("Pastilla de freno Brembo");
        producto.setMarca("BREMBO");

        codigoBarras = new CodigoBarrasProducto();
        codigoBarras.setId(1);
        codigoBarras.setSkuInterno("BRAKE-01");
        codigoBarras.setCodigo("7501031311309");
        codigoBarras.setTipo("EAN13");
        codigoBarras.setActivo(true);
        codigoBarras.setEsPrincipal(true);

        inventario = new InventarioSucursal();
        inventario.setId(new InventarioSucursalId(1, "BRAKE-01"));
        inventario.setStockActual(50);
    }

    // ========================================================================
    // ESCANEO UNIVERSAL
    // ========================================================================

    @Test
    @DisplayName("procesarEscaneo → Producto Encontrado (COMPRA)")
    void procesarEscaneo_ProductoEncontrado_Compra() {
        EscaneoRequest req = new EscaneoRequest();
        req.setCodigo("7501031311309");
        req.setContexto("COMPRA");
        req.setSucursalId(1);

        when(codigoBarrasRepo.findByCodigoAndActivoTrue("7501031311309"))
                .thenReturn(Optional.of(codigoBarras));
        when(productoRepo.findById("BRAKE-01")).thenReturn(Optional.of(producto));
        when(inventarioRepo.findById(new InventarioSucursalId(1, "BRAKE-01")))
                .thenReturn(Optional.of(inventario));

        EscaneoResponse resp = service.procesarEscaneo(req);

        assertThat(resp.getResultado()).isEqualTo("ENCONTRADO");
        assertThat(resp.getSkuInterno()).isEqualTo("BRAKE-01");
        assertThat(resp.getNombreComercial()).isEqualTo("Pastilla de freno Brembo");
        assertThat(resp.getStockEnSucursal()).isEqualTo(50);
        assertThat(resp.getAccionSugerida()).isEqualTo("AGREGAR_A_COMPRA");
    }

    @Test
    @DisplayName("procesarEscaneo → Producto Encontrado (VENTA)")
    void procesarEscaneo_ProductoEncontrado_Venta() {
        EscaneoRequest req = new EscaneoRequest();
        req.setCodigo("7501031311309");
        req.setContexto("VENTA");
        req.setSucursalId(1);

        when(codigoBarrasRepo.findByCodigoAndActivoTrue("7501031311309"))
                .thenReturn(Optional.of(codigoBarras));
        when(productoRepo.findById("BRAKE-01")).thenReturn(Optional.of(producto));
        when(inventarioRepo.findById(any(InventarioSucursalId.class)))
                .thenReturn(Optional.of(inventario));

        EscaneoResponse resp = service.procesarEscaneo(req);

        assertThat(resp.getAccionSugerida()).isEqualTo("AGREGAR_A_VENTA");
    }

    @Test
    @DisplayName("procesarEscaneo → Código Desconocido")
    void procesarEscaneo_CodigoDesconocido() {
        EscaneoRequest req = new EscaneoRequest();
        req.setCodigo("9999999999999");
        req.setContexto("COMPRA");
        req.setSucursalId(1);

        when(codigoBarrasRepo.findByCodigoAndActivoTrue("9999999999999"))
                .thenReturn(Optional.empty());

        EscaneoResponse resp = service.procesarEscaneo(req);

        assertThat(resp.getResultado()).isEqualTo("PRODUCTO_DESCONOCIDO");
        assertThat(resp.getAccionSugerida()).isEqualTo("REGISTRAR_NUEVO_PRODUCTO");
        assertThat(resp.getCodigoEscaneado()).isEqualTo("9999999999999");
    }

    // ========================================================================
    // VINCULAR CÓDIGO
    // ========================================================================

    @Test
    @DisplayName("vincularCodigo → Éxito")
    void vincularCodigo_Exito() {
        CodigoBarrasRequest req = new CodigoBarrasRequest();
        req.setCodigo("7501031311310");
        req.setTipo("EAN13");
        req.setEsPrincipal(false);

        when(productoRepo.existsById("BRAKE-01")).thenReturn(true);
        when(codigoBarrasRepo.existsByCodigo("7501031311310")).thenReturn(false);
        when(codigoBarrasRepo.save(any(CodigoBarrasProducto.class))).thenAnswer(inv -> {
            CodigoBarrasProducto cb = inv.getArgument(0);
            cb.setId(2);
            return cb;
        });

        CodigoBarrasResponse resp = service.vincularCodigo("BRAKE-01", req);

        assertThat(resp.getCodigo()).isEqualTo("7501031311310");
        assertThat(resp.getSkuInterno()).isEqualTo("BRAKE-01");
        verify(codigoBarrasRepo).save(any(CodigoBarrasProducto.class));
    }

    @Test
    @DisplayName("vincularCodigo → Error: Producto No Existe")
    void vincularCodigo_ProductoNoExiste() {
        CodigoBarrasRequest req = new CodigoBarrasRequest();
        req.setCodigo("7501031311310");
        req.setTipo("EAN13");

        when(productoRepo.existsById("NOEXISTE")).thenReturn(false);

        assertThatThrownBy(() -> service.vincularCodigo("NOEXISTE", req))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Producto no encontrado");
    }

    @Test
    @DisplayName("vincularCodigo → Error: Código Ya Registrado")
    void vincularCodigo_CodigoYaRegistrado() {
        CodigoBarrasRequest req = new CodigoBarrasRequest();
        req.setCodigo("7501031311309");
        req.setTipo("EAN13");

        when(productoRepo.existsById("BRAKE-01")).thenReturn(true);
        when(codigoBarrasRepo.existsByCodigo("7501031311309")).thenReturn(true);

        assertThatThrownBy(() -> service.vincularCodigo("BRAKE-01", req))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("ya está registrado");
    }

    // ========================================================================
    // LISTAR CÓDIGOS
    // ========================================================================

    @Test
    @DisplayName("listarCodigos → Retorna Lista")
    void listarCodigos_Exito() {
        when(codigoBarrasRepo.findBySkuInternoAndActivoTrue("BRAKE-01"))
                .thenReturn(List.of(codigoBarras));

        List<CodigoBarrasResponse> lista = service.listarCodigos("BRAKE-01");

        assertThat(lista).hasSize(1);
        assertThat(lista.get(0).getCodigo()).isEqualTo("7501031311309");
    }

    // ========================================================================
    // DESACTIVAR CÓDIGO
    // ========================================================================

    @Test
    @DisplayName("desactivarCodigo → Éxito")
    void desactivarCodigo_Exito() {
        when(codigoBarrasRepo.findById(1)).thenReturn(Optional.of(codigoBarras));
        when(codigoBarrasRepo.save(any(CodigoBarrasProducto.class))).thenReturn(codigoBarras);

        service.desactivarCodigo(1);

        assertThat(codigoBarras.getActivo()).isFalse();
        verify(codigoBarrasRepo).save(codigoBarras);
    }

    @Test
    @DisplayName("desactivarCodigo → Error: No Encontrado")
    void desactivarCodigo_NoEncontrado() {
        when(codigoBarrasRepo.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.desactivarCodigo(999))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ========================================================================
    // IMPORTACIÓN MASIVA
    // ========================================================================

    @Test
    @DisplayName("importarMasivo → Éxito Parcial")
    void importarMasivo_ExitoParcial() {
        ImportacionMasivaItemRequest item1 = new ImportacionMasivaItemRequest();
        item1.setSkuInterno("NEW-01");
        item1.setNombreComercial("Producto Nuevo 1");

        CodigoBarrasRequest cb1 = new CodigoBarrasRequest();
        cb1.setCodigo("1111111111111");
        cb1.setTipo("EAN13");
        item1.setCodigosBarras(List.of(cb1));

        ImportacionMasivaItemRequest item2 = new ImportacionMasivaItemRequest();
        item2.setSkuInterno("NEW-02");
        item2.setNombreComercial("Producto Nuevo 2");

        // item1 exitoso
        when(productoRepo.existsById("NEW-01")).thenReturn(false);
        when(productoRepo.save(any(ProductoMaestro.class))).thenAnswer(inv -> inv.getArgument(0));
        when(codigoBarrasRepo.existsByCodigo("1111111111111")).thenReturn(false);
        when(codigoBarrasRepo.save(any(CodigoBarrasProducto.class))).thenAnswer(inv -> inv.getArgument(0));

        // item2 falla (producto ya existe pero excepción simulada)
        when(productoRepo.existsById("NEW-02")).thenThrow(new RuntimeException("Error simulado"));

        ImportacionMasivaResponse resp = service.importarMasivo(List.of(item1, item2));

        assertThat(resp.getTotalProcesados()).isEqualTo(2);
        assertThat(resp.getTotalExitosos()).isEqualTo(1);
        assertThat(resp.getTotalFallidos()).isEqualTo(1);
        assertThat(resp.getDetalle()).hasSize(2);
        assertThat(resp.getDetalle().get(0).isExitoso()).isTrue();
        assertThat(resp.getDetalle().get(1).isExitoso()).isFalse();
    }
}
