package com.nexoohub.almacen.erp.service;

import com.nexoohub.almacen.common.exception.ResourceNotFoundException;
import com.nexoohub.almacen.erp.dto.DevolucionProveedorDetalleRequest;
import com.nexoohub.almacen.erp.dto.DevolucionProveedorRequest;
import com.nexoohub.almacen.erp.dto.DevolucionProveedorResponse;
import com.nexoohub.almacen.erp.entity.DevolucionProveedor;
import com.nexoohub.almacen.erp.entity.DevolucionProveedorDetalle;
import com.nexoohub.almacen.catalogo.entity.Proveedor;
import com.nexoohub.almacen.erp.repository.DevolucionProveedorRepository;
import com.nexoohub.almacen.catalogo.repository.ProveedorRepository;
import com.nexoohub.almacen.inventario.entity.InventarioSucursal;
import com.nexoohub.almacen.inventario.entity.InventarioSucursalId;
import com.nexoohub.almacen.inventario.entity.MovimientoInventario;
import com.nexoohub.almacen.inventario.entity.ProductoMaestro;
import com.nexoohub.almacen.inventario.repository.InventarioSucursalRepository;
import com.nexoohub.almacen.inventario.repository.MovimientoInventarioRepository;
import com.nexoohub.almacen.inventario.repository.ProductoMaestroRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("DevolucionProveedorService — ERP-05 Pruebas Unitarias")
class DevolucionProveedorServiceTest {

    @Mock private DevolucionProveedorRepository devolucionRepo;
    @Mock private ProveedorRepository proveedorRepo;
    @Mock private InventarioSucursalRepository inventarioRepo;
    @Mock private MovimientoInventarioRepository movimientoRepo;

    @InjectMocks
    private DevolucionProveedorService service;

    private Proveedor proveedor;
    private DevolucionProveedor devolucionCreada;
    private InventarioSucursal inventario;

    @BeforeEach
    void setUp() {
        proveedor = new Proveedor();
        proveedor.setId(1);
        proveedor.setNombreEmpresa("Intel");


        inventario = new InventarioSucursal();
        inventario.setId(new InventarioSucursalId(1, "INT-01")); // Sucursal 1, Producto 10
        inventario.setStockActual(50); // Tenemos 50 en la sucursal

        devolucionCreada = new DevolucionProveedor();
        devolucionCreada.setId(100);
        devolucionCreada.setProveedor(proveedor);
        devolucionCreada.setSucursalId(1);
        devolucionCreada.setUsuarioId(5);
        devolucionCreada.setMotivo("Empaque Dañado");
        devolucionCreada.setEstatus("CREADA");
        devolucionCreada.setFecha(LocalDate.now());

        DevolucionProveedorDetalle det = new DevolucionProveedorDetalle();
        det.setId(1);
        det.setSkuInterno("INT-01");
        det.setCantidad(5);
        det.setCostoUnitario(new BigDecimal("100.00"));
        det.setSubtotal(new BigDecimal("500.00"));
        devolucionCreada.addDetalle(det);
    }

    @Test
    @DisplayName("registrarDevolucion → Exito")
    void registrarDevolucion_Exito() {
        DevolucionProveedorRequest req = new DevolucionProveedorRequest();
        req.setProveedorId(1);
        req.setSucursalId(1);
        req.setUsuarioId(5);
        req.setMotivo("Error de pedido");
        
        DevolucionProveedorDetalleRequest det = new DevolucionProveedorDetalleRequest();
        det.setSkuInterno("INT-01");
        det.setCantidad(2);
        det.setCostoUnitario(new BigDecimal("50.0"));
        req.setDetalles(List.of(det));

        when(proveedorRepo.findById(1)).thenReturn(Optional.of(proveedor));
        when(devolucionRepo.save(any(DevolucionProveedor.class))).thenReturn(devolucionCreada);

        DevolucionProveedorResponse resp = service.registrarDevolucion(req);

        assertThat(resp).isNotNull();
        assertThat(resp.getEstatus()).isEqualTo("CREADA");
        verify(devolucionRepo).save(any(DevolucionProveedor.class));
    }

    @Test
    @DisplayName("aplicarDevolucion → Exito Descuenta Inventario")
    void aplicarDevolucion_Exito() {
        when(devolucionRepo.findById(100)).thenReturn(Optional.of(devolucionCreada));
        when(inventarioRepo.findById(new InventarioSucursalId(1, "INT-01"))).thenReturn(Optional.of(inventario));
        // Devuelve 5 items
        when(devolucionRepo.save(any(DevolucionProveedor.class))).thenReturn(devolucionCreada);

        DevolucionProveedorResponse resp = service.aplicarDevolucion(100);

        assertThat(resp.getEstatus()).isEqualTo("APLICADA");
        assertThat(inventario.getStockActual()).isEqualTo(45); // 50 - 5 = 45

        verify(inventarioRepo).save(inventario);
        verify(movimientoRepo).save(any(MovimientoInventario.class));
    }

    @Test
    @DisplayName("aplicarDevolucion → Error por Inventario Insuficiente")
    void aplicarDevolucion_ErrorInventarioInsuficiente() {
        // Ponemos solo 2 en stock
        inventario.setStockActual(2);

        when(devolucionRepo.findById(100)).thenReturn(Optional.of(devolucionCreada));
        when(inventarioRepo.findById(new InventarioSucursalId(1, "INT-01"))).thenReturn(Optional.of(inventario));

        assertThatThrownBy(() -> service.aplicarDevolucion(100))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Stock insuficiente");

        verify(inventarioRepo, never()).save(any());
        verify(movimientoRepo, never()).save(any());
    }

    @Test
    @DisplayName("aplicarDevolucion → Error Ya Aplicada")
    void aplicarDevolucion_ErrorYaAplicada() {
        devolucionCreada.setEstatus("APLICADA");
        when(devolucionRepo.findById(100)).thenReturn(Optional.of(devolucionCreada));

        assertThatThrownBy(() -> service.aplicarDevolucion(100))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("ya fue aplicada o cancelada");
    }
}
