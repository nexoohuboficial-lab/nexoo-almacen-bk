package com.nexoohub.almacen.catalogo.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("TipoCliente - Tests de Entidad")
class TipoClienteTest {

    @Test
    @DisplayName("Debe crear tipoCliente con ID")
    void testCrearTipoClienteConId() {
        // Given & When
        TipoCliente tipoCliente = new TipoCliente();
        tipoCliente.setId(1);

        // Then
        assertEquals(1, tipoCliente.getId());
    }

    @Test
    @DisplayName("Debe establecer y obtener nombre")
    void testNombre() {
        // Given
        TipoCliente tipoCliente = new TipoCliente();

        // When
        tipoCliente.setNombre("Mayorista");

        // Then
        assertEquals("Mayorista", tipoCliente.getNombre());
    }

    @Test
    @DisplayName("Debe establecer y obtener descripcion")
    void testDescripcion() {
        // Given
        TipoCliente tipoCliente = new TipoCliente();

        // When
        tipoCliente.setDescripcion("Cliente que compra en grandes volúmenes");

        // Then
        assertEquals("Cliente que compra en grandes volúmenes", tipoCliente.getDescripcion());
    }

    @Test
    @DisplayName("Debe crear tipoCliente completo")
    void testTipoClienteCompleto() {
        // Given & When
        TipoCliente tipoCliente = new TipoCliente();
        tipoCliente.setId(5);
        tipoCliente.setNombre("VIP");
        tipoCliente.setDescripcion("Clientes con descuentos especiales y atención premium");

        // Then
        assertNotNull(tipoCliente);
        assertEquals(5, tipoCliente.getId());
        assertEquals("VIP", tipoCliente.getNombre());
        assertEquals("Clientes con descuentos especiales y atención premium", tipoCliente.getDescripcion());
    }

    @Test
    @DisplayName("Debe permitir descripcion null")
    void testDescripcionNull() {
        // Given & When
        TipoCliente tipoCliente = new TipoCliente();
        tipoCliente.setNombre("Estándar");
        tipoCliente.setDescripcion(null);

        // Then
        assertEquals("Estándar", tipoCliente.getNombre());
        assertNull(tipoCliente.getDescripcion());
    }

    @Test
    @DisplayName("Debe crear instancia sin valores por defecto")
    void testInstanciaPorDefecto() {
        // Given & When
        TipoCliente tipoCliente = new TipoCliente();

        // Then
        assertNotNull(tipoCliente);
        assertNull(tipoCliente.getId());
        assertNull(tipoCliente.getNombre());
        assertNull(tipoCliente.getDescripcion());
    }

    @Test
    @DisplayName("Debe manejar diferentes tipos de cliente")
    void testDiferentesTipos() {
        // Given & When
        TipoCliente minorista = new TipoCliente();
        minorista.setNombre("Minorista");
        minorista.setDescripcion("Compra al por menor");

        TipoCliente distribuidor = new TipoCliente();
        distribuidor.setNombre("Distribuidor");
        distribuidor.setDescripcion("Distribuidor autorizado");

        TipoCliente corporativo = new TipoCliente();
        corporativo.setNombre("Corporativo");
        corporativo.setDescripcion("Empresa con contrato especial");

        // Then
        assertEquals("Minorista", minorista.getNombre());
        assertEquals("Distribuidor", distribuidor.getNombre());
        assertEquals("Corporativo", corporativo.getNombre());
    }

    @Test
    @DisplayName("Debe manejar nombres cortos y largos")
    void testNombresVariados() {
        // Given
        TipoCliente tipo1 = new TipoCliente();
        TipoCliente tipo2 = new TipoCliente();

        // When
        tipo1.setNombre("VIP");
        tipo2.setNombre("Cliente Especial con Descuento Corporativo Premium");

        // Then
        assertEquals("VIP", tipo1.getNombre());
        assertEquals("Cliente Especial con Descuento Corporativo Premium", tipo2.getNombre());
        assertTrue(tipo2.getNombre().length() > tipo1.getNombre().length());
    }
}
