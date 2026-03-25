package com.nexoohub.almacen.inventario.controller;

import com.nexoohub.almacen.inventario.entity.ProductoMaestro;
import com.nexoohub.almacen.inventario.mapper.ProductoMaestroMapper;
import com.nexoohub.almacen.inventario.repository.ProductoMaestroRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias para ProductoController — SRCH-01.
 * Cubre: búsqueda sin filtros, con filtros nuevos (soloActivos, conStock,
 * rango de precio, clasificacionAbc) y combinaciones vacías.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ProductoController — SRCH-01 Buscador de Productos")
class ProductoControllerTest {

    @Mock
    private ProductoMaestroRepository productoRepository;

    @Mock
    private ProductoMaestroMapper mapper;

    @InjectMocks
    private ProductoController controller;

    private Pageable pageable;

    @BeforeEach
    void setUp() {
        pageable = PageRequest.of(0, 20);
    }

    // ----------------------------------------------------------------
    // Casos base
    // ----------------------------------------------------------------

    @Test
    @DisplayName("Búsqueda sin ningún filtro → devuelve página vacía correctamente")
    void buscarProductos_sinFiltros_devuelvePaginaVacia() {
        Page<ProductoMaestro> paginaVacia = new PageImpl<>(Collections.emptyList());
        when(productoRepository.findAll(any(Specification.class), any(Pageable.class)))
            .thenReturn(paginaVacia);

        ResponseEntity<?> response = controller.buscarProductos(
            null, null, null, null, null,
            null, null, null, null, null,
            null, null, null, null, null, null,
            pageable
        );

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        verify(productoRepository, times(1)).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    @DisplayName("Búsqueda con término de texto → delega al repositorio")
    void buscarProductos_conTermino_delegaAlRepositorio() {
        Page<ProductoMaestro> pagina = new PageImpl<>(Collections.emptyList());
        when(productoRepository.findAll(any(Specification.class), any(Pageable.class)))
            .thenReturn(pagina);

        controller.buscarProductos(
            "freno", null, null, null, null,
            null, null, null, null, null,
            null, null, null, null, null, null,
            pageable
        );

        verify(productoRepository).findAll(any(Specification.class), any(Pageable.class));
    }

    // ----------------------------------------------------------------
    // Nuevos filtros SRCH-01
    // ----------------------------------------------------------------

    @Test
    @DisplayName("soloActivos=true → el repositorio recibe una Specification y responde 200")
    void buscarProductos_soloActivos_responde200() {
        Page<ProductoMaestro> pagina = new PageImpl<>(Collections.emptyList());
        when(productoRepository.findAll(any(Specification.class), any(Pageable.class)))
            .thenReturn(pagina);

        ResponseEntity<?> response = controller.buscarProductos(
            null, null, null, null, null,
            null, null, null, null, null,
            true, null, null, null, null, null,
            pageable
        );

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        verify(productoRepository).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    @DisplayName("conStock=true + sucursalIdStock=1 → delega al repositorio correctamente")
    void buscarProductos_conStockEnSucursal_delegaCorrectamente() {
        Page<ProductoMaestro> pagina = new PageImpl<>(Collections.emptyList());
        when(productoRepository.findAll(any(Specification.class), any(Pageable.class)))
            .thenReturn(pagina);

        controller.buscarProductos(
            null, null, null, null, null,
            null, null, null, null, null,
            null, true, 1, null, null, null,
            pageable
        );

        verify(productoRepository).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    @DisplayName("precioMin + precioMax → el repositorio es llamado con Specification")
    void buscarProductos_conRangoPrecio_delegaAlRepositorio() {
        Page<ProductoMaestro> pagina = new PageImpl<>(Collections.emptyList());
        when(productoRepository.findAll(any(Specification.class), any(Pageable.class)))
            .thenReturn(pagina);

        controller.buscarProductos(
            null, null, null, null, null,
            null, null, null, null, null,
            null, null, null,
            new BigDecimal("100.00"), new BigDecimal("500.00"),
            null,
            pageable
        );

        verify(productoRepository).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    @DisplayName("clasificacionAbc='A' → delega al repositorio correctamente")
    void buscarProductos_clasificacionA_delegaAlRepositorio() {
        Page<ProductoMaestro> pagina = new PageImpl<>(Collections.emptyList());
        when(productoRepository.findAll(any(Specification.class), any(Pageable.class)))
            .thenReturn(pagina);

        controller.buscarProductos(
            null, null, null, null, null,
            null, null, null, null, null,
            null, null, null, null, null,
            "A",
            pageable
        );

        verify(productoRepository).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    @DisplayName("Combinación de filtros nuevos → el repositorio es llamado una sola vez")
    void buscarProductos_combinacionNuevosFiltros_llamaRepositorioUnaVez() {
        Page<ProductoMaestro> pagina = new PageImpl<>(Collections.emptyList());
        when(productoRepository.findAll(any(Specification.class), any(Pageable.class)))
            .thenReturn(pagina);

        controller.buscarProductos(
            "aceite", null, null, null, null,
            null, "Honda", null, 150, 2022,
            true, true, 2,
            new BigDecimal("50.00"), new BigDecimal("300.00"),
            "A",
            pageable
        );

        verify(productoRepository, times(1)).findAll(any(Specification.class), any(Pageable.class));
    }
}
