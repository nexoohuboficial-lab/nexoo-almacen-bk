package com.nexoohub.almacen.comisiones.service;

import com.nexoohub.almacen.comisiones.dto.AsignarMetaRequest;
import com.nexoohub.almacen.comisiones.dto.ProgresoMetaResponse;
import com.nexoohub.almacen.comisiones.entity.MetaVentasEmpleado;
import com.nexoohub.almacen.comisiones.entity.ReglaComisionEscalonada;
import com.nexoohub.almacen.comisiones.repository.MetaVentasEmpleadoRepository;
import com.nexoohub.almacen.comisiones.repository.ReglaComisionEscalonadaRepository;
import com.nexoohub.almacen.common.exception.BusinessException;
import com.nexoohub.almacen.common.exception.ResourceNotFoundException;
import com.nexoohub.almacen.empleados.entity.Empleado;
import com.nexoohub.almacen.empleados.repository.EmpleadoRepository;
import com.nexoohub.almacen.ventas.entity.Venta;
import com.nexoohub.almacen.ventas.repository.VentaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MetaComisionService {

    private final MetaVentasEmpleadoRepository metaRepository;
    private final ReglaComisionEscalonadaRepository reglaRepository;
    private final EmpleadoRepository empleadoRepository;
    private final VentaRepository ventaRepository;

    @Transactional
    public MetaVentasEmpleado asignarMeta(AsignarMetaRequest request, String usuarioCreacion) {
        Empleado empleado = empleadoRepository.findById(request.getEmpleadoId())
                .orElseThrow(() -> new ResourceNotFoundException("Empleado con ID " + request.getEmpleadoId() + " no encontrado"));

        MetaVentasEmpleado metaExistente = metaRepository.findByEmpleadoIdAndMesAndAnioAndActivoTrue(
                empleado.getId(), request.getMes(), request.getAnio()
        ).orElse(null);

        if (metaExistente != null) {
            // Actualizamos la meta
            metaExistente.setMontoMeta(request.getMontoMeta());
            metaExistente.setUsuarioActualizacion(usuarioCreacion);
            metaExistente.setFechaActualizacion(LocalDateTime.now());
            return metaRepository.save(metaExistente);
        }

        // Creamos nueva meta
        MetaVentasEmpleado nuevaMeta = new MetaVentasEmpleado();
        nuevaMeta.setEmpleado(empleado);
        nuevaMeta.setMes(request.getMes());
        nuevaMeta.setAnio(request.getAnio());
        nuevaMeta.setMontoMeta(request.getMontoMeta());
        nuevaMeta.setUsuarioCreacion(usuarioCreacion);
        
        return metaRepository.save(nuevaMeta);
    }

    @Transactional(readOnly = true)
    public ProgresoMetaResponse calcularProgreso(Integer empleadoId, Integer mes, Integer anio) {
        Empleado empleado = empleadoRepository.findById(empleadoId)
                .orElseThrow(() -> new ResourceNotFoundException("Empleado con ID " + empleadoId + " no encontrado"));

        MetaVentasEmpleado meta = metaRepository.findByEmpleadoIdAndMesAndAnioAndActivoTrue(empleadoId, mes, anio)
                .orElseThrow(() -> new BusinessException("No hay meta asignada para el empleado en el mes/año seleccionado (" + mes + "/" + anio + ")"));

        // Calcular ventas acumuladas reales del mes
        LocalDate fechaInicio = LocalDate.of(anio, mes, 1);
        LocalDate fechaFin = fechaInicio.withDayOfMonth(fechaInicio.lengthOfMonth());
        
        List<Venta> ventasPeriodo = ventaRepository.findVentasByVendedorAndPeriodo(
                empleadoId, 
                fechaInicio.atStartOfDay(), 
                fechaFin.atTime(LocalTime.MAX)
        );

        BigDecimal acumulado = ventasPeriodo.stream()
                .map(Venta::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Actualizamos virtualmente para el cálculo (No forzamos COMMIT para no penalizar el rendimiento)
        meta.setMontoVentasActual(acumulado);

        BigDecimal porcentajeLogro = meta.getPorcentajeLogro(); // (acumulado / meta) * 100

        // Buscar TIER (escalón) y calcular comisión extra
        List<ReglaComisionEscalonada> reglasAplicables = reglaRepository.findReglasAplicables(porcentajeLogro);
        
        ReglaComisionEscalonada tierAlcanzado = null;
        BigDecimal porcentajeComision = BigDecimal.ZERO;
        
        if (!reglasAplicables.isEmpty()) {
            // Evaluamos la más alta (el query ya hace ORDER BY porcentaje_comision DESC)
            tierAlcanzado = reglasAplicables.get(0);
            porcentajeComision = tierAlcanzado.getPorcentajeComision();
        }

        BigDecimal comisionDinero = acumulado.multiply(porcentajeComision).divide(new BigDecimal("100"), 2, java.math.RoundingMode.HALF_UP);

        return ProgresoMetaResponse.builder()
                .empleadoId(empleadoId)
                .nombreEmpleado(empleado.getNombre() + " " + (empleado.getApellidos() != null ? empleado.getApellidos() : ""))
                .mes(mes)
                .anio(anio)
                .montoMeta(meta.getMontoMeta())
                .montoVentasAcumuladas(acumulado)
                .porcentajeLogro(porcentajeLogro)
                .porcentajeComisionAplicable(porcentajeComision)
                .comisionProyectadaVentas(comisionDinero)
                .tierLogrado(tierAlcanzado != null ? tierAlcanzado.getNombre() : "Sin Tier Mínimo")
                .fechaUltimoCalculo(LocalDateTime.now())
                .build();
    }
}
