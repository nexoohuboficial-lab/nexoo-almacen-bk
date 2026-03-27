package com.nexoohub.almacen.erp.service;

import com.nexoohub.almacen.erp.dto.EmpleadoDTO;
import com.nexoohub.almacen.erp.dto.NominaPeriodoRequest;
import com.nexoohub.almacen.erp.dto.NominaPeriodoResponse;
import com.nexoohub.almacen.empleados.entity.Empleado;
import com.nexoohub.almacen.erp.entity.NominaPeriodo;
import com.nexoohub.almacen.erp.entity.ReciboNomina;
import com.nexoohub.almacen.erp.repository.ErpEmpleadoRepository;
import com.nexoohub.almacen.erp.repository.NominaPeriodoRepository;
import com.nexoohub.almacen.erp.repository.ReciboNominaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("NominaService — ERP-04 Pruebas Unitarias")
class NominaServiceTest {

    @Mock
    private ErpEmpleadoRepository empleadoRepo;

    @Mock
    private NominaPeriodoRepository periodoRepo;

    @Mock
    private ReciboNominaRepository reciboRepo;

    @InjectMocks
    private NominaService nominaService;

    private Empleado empleado;
    private NominaPeriodo periodo;

    @BeforeEach
    void setUp() {
        empleado = new Empleado();
        empleado.setId(1);
        empleado.setNombreCompleto("Juan Perez");
        empleado.setRfc("PELJ900101XYZ");
        empleado.setSucursalId(1);
        empleado.setSalarioDiario(new BigDecimal("300.00")); // 300 base
        empleado.setEstatus("ACTIVO");

        periodo = new NominaPeriodo();
        periodo.setId(100);
        periodo.setNombre("1ra Quincena");
        periodo.setTipoPeriodo("QUINCENAL");
        periodo.setEstatus("BORRADOR");
    }

    @Test
    @DisplayName("registrarEmpleado → Éxito")
    void registrarEmpleado_Exito() {
        EmpleadoDTO req = new EmpleadoDTO();
        req.setNombreCompleto("Juan Perez");
        req.setRfc("PELJ900101XYZ");

        when(empleadoRepo.findByRfc("PELJ900101XYZ")).thenReturn(Optional.empty());
        when(empleadoRepo.save(any(Empleado.class))).thenReturn(empleado);

        EmpleadoDTO resp = nominaService.registrarEmpleado(req);
        
        assertThat(resp).isNotNull();
        assertThat(resp.getNombreCompleto()).isEqualTo("Juan Perez");
    }

    @Test
    @DisplayName("registrarEmpleado → RFC Duplicado lanza Error")
    void registrarEmpleado_RfcDuplicado() {
        EmpleadoDTO req = new EmpleadoDTO();
        req.setRfc("PELJ900101XYZ");

        when(empleadoRepo.findByRfc("PELJ900101XYZ")).thenReturn(Optional.of(empleado));

        assertThatThrownBy(() -> nominaService.registrarEmpleado(req))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Ya existe un empleado con RFC");
    }

    @Test
    @DisplayName("crearPeriodo → Exito")
    void crearPeriodo_Exito() {
        NominaPeriodoRequest req = new NominaPeriodoRequest();
        req.setNombre("Q1");
        req.setFechaInicio(LocalDate.of(2026, 1, 1));
        req.setFechaFin(LocalDate.of(2026, 1, 15));
        req.setTipoPeriodo("QUINCENAL");
        req.setUsuarioId(1);

        when(periodoRepo.save(any(NominaPeriodo.class))).thenReturn(periodo);

        NominaPeriodoResponse resp = nominaService.crearPeriodo(req);

        assertThat(resp).isNotNull();
        verify(periodoRepo).save(any(NominaPeriodo.class));
    }

    @Test
    @DisplayName("crearPeriodo → Error por Fecha Fin invertida")
    void crearPeriodo_FechaInvalida() {
        NominaPeriodoRequest req = new NominaPeriodoRequest();
        req.setFechaInicio(LocalDate.of(2026, 1, 15));
        req.setFechaFin(LocalDate.of(2026, 1, 1));

        assertThatThrownBy(() -> nominaService.crearPeriodo(req))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("generarRecibosParaPeriodo → Genera Recibo Quincenal Automático (15 días)")
    void generarRecibos_Exito() {
        when(periodoRepo.findById(100)).thenReturn(Optional.of(periodo));
        when(empleadoRepo.findBySucursalIdAndEstatus(1, "ACTIVO")).thenReturn(List.of(empleado));
        when(reciboRepo.findByPeriodoIdAndEmpleadoId(100, 1)).thenReturn(Optional.empty());
        when(periodoRepo.save(any(NominaPeriodo.class))).thenReturn(periodo);

        NominaPeriodoResponse resp = nominaService.generarRecibosParaPeriodo(100, 1);
        
        assertThat(periodo.getRecibos()).hasSize(1);
        
        ReciboNomina reciboGenerado = periodo.getRecibos().get(0);
        
        // 15 días x 300 = 4500 (Percepciones)
        // ISR 10% de 4500 = 450, IMSS 2% de 4500 = 90. Deducciones = 540
        // Neto = 4500 - 540 = 3960
        assertThat(reciboGenerado.getDiasTrabajados()).isEqualTo(new BigDecimal("15.00"));
        assertThat(reciboGenerado.getTotalPercepciones().setScale(2)).isEqualTo(new BigDecimal("4500.00"));
        assertThat(reciboGenerado.getTotalDeducciones().setScale(2)).isEqualTo(new BigDecimal("540.00"));
        assertThat(reciboGenerado.getNetoPagar().setScale(2)).isEqualTo(new BigDecimal("3960.00"));
    }
}
