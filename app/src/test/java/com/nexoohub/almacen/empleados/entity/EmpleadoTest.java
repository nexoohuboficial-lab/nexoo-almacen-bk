package com.nexoohub.almacen.empleados.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Empleado - Tests de Entidad")
class EmpleadoTest {

    @Test
    @DisplayName("Debe crear empleado con valor activo por defecto")
    void testCrearEmpleadoConDefaults() {
        Empleado empleado = new Empleado();
        
        assertTrue(empleado.getActivo());
    }

    @Test
    @DisplayName("Debe establecer y obtener ID correctamente")
    void testGetSetId() {
        Empleado empleado = new Empleado();
        empleado.setId(1);
        
        assertEquals(1, empleado.getId());
    }

    @Test
    @DisplayName("Debe establecer y obtener nombre correctamente")
    void testGetSetNombre() {
        Empleado empleado = new Empleado();
        empleado.setNombre("Carlos");
        
        assertEquals("Carlos", empleado.getNombre());
    }

    @Test
    @DisplayName("Debe establecer y obtener apellidos correctamente")
    void testGetSetApellidos() {
        Empleado empleado = new Empleado();
        empleado.setApellidos("Martínez López");
        
        assertEquals("Martínez López", empleado.getApellidos());
    }

    @Test
    @DisplayName("Debe establecer y obtener puesto correctamente")
    void testGetSetPuesto() {
        Empleado empleado = new Empleado();
        empleado.setPuesto("Gerente");
        
        assertEquals("Gerente", empleado.getPuesto());
    }

    @Test
    @DisplayName("Debe establecer y obtener sucursal ID correctamente")
    void testGetSetSucursalId() {
        Empleado empleado = new Empleado();
        empleado.setSucursalId(3);
        
        assertEquals(3, empleado.getSucursalId());
    }

    @Test
    @DisplayName("Debe establecer y obtener fecha de contratación correctamente")
    void testGetSetFechaContratacion() {
        Empleado empleado = new Empleado();
        LocalDate fecha = LocalDate.of(2024, 1, 15);
        empleado.setFechaContratacion(fecha);
        
        assertEquals(fecha, empleado.getFechaContratacion());
    }

    @Test
    @DisplayName("Debe establecer y obtener estado activo correctamente")
    void testGetSetActivo() {
        Empleado empleado = new Empleado();
        empleado.setActivo(false);
        
        assertFalse(empleado.getActivo());
    }

    @Test
    @DisplayName("Debe crear empleado completo con todos los campos")
    void testCrearEmpleadoCompleto() {
        Empleado empleado = new Empleado();
        LocalDate fecha = LocalDate.of(2024, 3, 1);
        
        empleado.setId(1);
        empleado.setNombre("Ana");
        empleado.setApellidos("García Ruiz");
        empleado.setPuesto("Cajero");
        empleado.setSucursalId(2);
        empleado.setFechaContratacion(fecha);
        empleado.setActivo(true);
        
        assertAll("empleado",
            () -> assertEquals(1, empleado.getId()),
            () -> assertEquals("Ana", empleado.getNombre()),
            () -> assertEquals("García Ruiz", empleado.getApellidos()),
            () -> assertEquals("Cajero", empleado.getPuesto()),
            () -> assertEquals(2, empleado.getSucursalId()),
            () -> assertEquals(fecha, empleado.getFechaContratacion()),
            () -> assertTrue(empleado.getActivo())
        );
    }
}
