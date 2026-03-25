package com.nexoohub.almacen.erp.service;

import com.nexoohub.almacen.erp.dto.GastoOperativoRequest;
import com.nexoohub.almacen.erp.dto.GastoOperativoResponse;
import com.nexoohub.almacen.erp.entity.GastoOperativo;
import com.nexoohub.almacen.erp.repository.GastoOperativoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("GastoOperativoService — ERP-01 Pruebas Unitarias")
class GastoOperativoServiceTest {

    @Mock
    private GastoOperativoRepository gastoRepo;

    @InjectMocks
    private GastoOperativoService gastoService;

    private GastoOperativoRequest gastoRequest;
    private GastoOperativo gastoEntity;

    @BeforeEach
    void setUp() {
        gastoRequest = new GastoOperativoRequest();
        gastoRequest.setConcepto("Renta local norte");
        gastoRequest.setCategoria("RENTA");
        gastoRequest.setMonto(new BigDecimal("12000.00"));
        gastoRequest.setFechaGasto(LocalDate.now());
        gastoRequest.setSucursalId(1);
        gastoRequest.setUsuarioId(10);

        gastoEntity = new GastoOperativo();
        gastoEntity.setId(1);
        gastoEntity.setConcepto("Renta local norte");
        gastoEntity.setCategoria("RENTA");
        gastoEntity.setMonto(new BigDecimal("12000.00"));
        gastoEntity.setFechaGasto(LocalDate.now());
        gastoEntity.setSucursalId(1);
        gastoEntity.setUsuarioId(10);
    }

    @Test
    @DisplayName("registrar → éxito")
    void registrar_exito() {
        when(gastoRepo.save(any(GastoOperativo.class))).thenAnswer(i -> {
            GastoOperativo g = i.getArgument(0);
            g.setId(100);
            return g;
        });

        GastoOperativoResponse resp = gastoService.registrar(gastoRequest);

        assertThat(resp.getConcepto()).isEqualTo("Renta local norte");
        assertThat(resp.getCategoria()).isEqualTo("RENTA");
        assertThat(resp.getMonto()).isEqualByComparingTo("12000.00");
        verify(gastoRepo).save(any());
    }

    @Test
    @DisplayName("listar → sin sucursal, filtra solo por fechas")
    void listar_sinSucursal() {
        LocalDate desde = LocalDate.now().minusDays(30);
        LocalDate hasta = LocalDate.now();
        when(gastoRepo.findByFechaGastoBetweenOrderByFechaGastoDesc(desde, hasta))
            .thenReturn(Collections.singletonList(gastoEntity));

        List<GastoOperativoResponse> result = gastoService.listar(desde, hasta, null);

        assertThat(result).hasSize(1);
        verify(gastoRepo).findByFechaGastoBetweenOrderByFechaGastoDesc(desde, hasta);
    }

    @Test
    @DisplayName("listar → con sucursal, filtra por sucursal y fechas")
    void listar_conSucursal() {
        LocalDate desde = LocalDate.now().minusDays(30);
        LocalDate hasta = LocalDate.now();
        when(gastoRepo.findBySucursalIdAndFechaGastoBetweenOrderByFechaGastoDesc(1, desde, hasta))
            .thenReturn(Collections.singletonList(gastoEntity));

        List<GastoOperativoResponse> result = gastoService.listar(desde, hasta, 1);

        assertThat(result).hasSize(1);
        verify(gastoRepo).findBySucursalIdAndFechaGastoBetweenOrderByFechaGastoDesc(1, desde, hasta);
    }
}
