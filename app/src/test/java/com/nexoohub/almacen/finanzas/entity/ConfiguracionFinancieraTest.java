package com.nexoohub.almacen.finanzas.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ConfiguracionFinanciera - Tests de Entidad")
class ConfiguracionFinancieraTest {

    @Test
    @DisplayName("Debe crear configuración con ID")
    void testCrearConfiguracionConId() {
        // Given & When
        ConfiguracionFinanciera config = new ConfiguracionFinanciera();
        config.setId(1);

        // Then
        assertEquals(1, config.getId());
    }

    @Test
    @DisplayName("Debe establecer y obtener gastosFijosMensuales")
    void testGastosFijosMensuales() {
        // Given
        ConfiguracionFinanciera config = new ConfiguracionFinanciera();
        BigDecimal gastos = new BigDecimal("50000.00");

        // When
        config.setGastosFijosMensuales(gastos);

        // Then
        assertEquals(gastos, config.getGastosFijosMensuales());
    }

    @Test
    @DisplayName("Debe establecer y obtener metaVentasMensual")
    void testMetaVentasMensual() {
        // Given
        ConfiguracionFinanciera config = new ConfiguracionFinanciera();
        BigDecimal meta = new BigDecimal("200000.00");

        // When
        config.setMetaVentasMensual(meta);

        // Then
        assertEquals(meta, config.getMetaVentasMensual());
    }

    @Test
    @DisplayName("Debe establecer y obtener margenGananciaBase")
    void testMargenGananciaBase() {
        // Given
        ConfiguracionFinanciera config = new ConfiguracionFinanciera();
        BigDecimal margen = new BigDecimal("0.30");

        // When
        config.setMargenGananciaBase(margen);

        // Then
        assertEquals(margen, config.getMargenGananciaBase());
    }

    @Test
    @DisplayName("Debe establecer y obtener comisionTarjeta")
    void testComisionTarjeta() {
        // Given
        ConfiguracionFinanciera config = new ConfiguracionFinanciera();
        BigDecimal comision = new BigDecimal("0.035");

        // When
        config.setComisionTarjeta(comision);

        // Then
        assertEquals(comision, config.getComisionTarjeta());
    }

    @Test
    @DisplayName("Debe establecer y obtener IVA")
    void testIva() {
        // Given
        ConfiguracionFinanciera config = new ConfiguracionFinanciera();
        BigDecimal iva = new BigDecimal("0.16");

        // When
        config.setIva(iva);

        // Then
        assertEquals(iva, config.getIva());
    }

    @Test
    @DisplayName("Debe crear configuración financiera completa")
    void testConfiguracionCompleta() {
        // Given & When
        ConfiguracionFinanciera config = new ConfiguracionFinanciera();
        config.setId(10);
        config.setGastosFijosMensuales(new BigDecimal("75000.00"));
        config.setMetaVentasMensual(new BigDecimal("300000.00"));
        config.setMargenGananciaBase(new BigDecimal("0.25"));
        config.setComisionTarjeta(new BigDecimal("0.03"));
        config.setIva(new BigDecimal("0.16"));

        // Then
        assertNotNull(config);
        assertEquals(10, config.getId());
        assertEquals(new BigDecimal("75000.00"), config.getGastosFijosMensuales());
        assertEquals(new BigDecimal("300000.00"), config.getMetaVentasMensual());
        assertEquals(new BigDecimal("0.25"), config.getMargenGananciaBase());
        assertEquals(new BigDecimal("0.03"), config.getComisionTarjeta());
        assertEquals(new BigDecimal("0.16"), config.getIva());
    }

    @Test
    @DisplayName("Debe manejar diferentes porcentajes de margen")
    void testDiferentesMargenes() {
        // Given
        ConfiguracionFinanciera c1 = new ConfiguracionFinanciera();
        ConfiguracionFinanciera c2 = new ConfiguracionFinanciera();
        ConfiguracionFinanciera c3 = new ConfiguracionFinanciera();

        // When
        c1.setMargenGananciaBase(new BigDecimal("0.20")); // 20%
        c2.setMargenGananciaBase(new BigDecimal("0.30")); // 30%
        c3.setMargenGananciaBase(new BigDecimal("0.40")); // 40%

        // Then
        assertEquals(new BigDecimal("0.20"), c1.getMargenGananciaBase());
        assertEquals(new BigDecimal("0.30"), c2.getMargenGananciaBase());
        assertEquals(new BigDecimal("0.40"), c3.getMargenGananciaBase());
    }

    @Test
    @DisplayName("Debe validar que margen sea mayor que comisión")
    void testMargenMayorQueComision() {
        // Given
        ConfiguracionFinanciera config = new ConfiguracionFinanciera();
        config.setMargenGananciaBase(new BigDecimal("0.25"));
        config.setComisionTarjeta(new BigDecimal("0.03"));

        // Then
        assertTrue(config.getMargenGananciaBase()
                .compareTo(config.getComisionTarjeta()) > 0);
    }

    @Test
    @DisplayName("Debe manejar diferentes tasas de IVA")
    void testDiferentesTasasIva() {
        // Given
        ConfiguracionFinanciera config = new ConfiguracionFinanciera();

        // When - IVA del 16%
        config.setIva(new BigDecimal("0.16"));

        // Then
        assertEquals(new BigDecimal("0.16"), config.getIva());
    }

    @Test
    @DisplayName("Debe calcular precio con IVA correctamente")
    void testCalculoPrecioConIva() {
        // Given
        ConfiguracionFinanciera config = new ConfiguracionFinanciera();
        config.setIva(new BigDecimal("0.16"));
        BigDecimal precioBase = new BigDecimal("100.00");

        // When
        BigDecimal precioConIva = precioBase.multiply(
                BigDecimal.ONE.add(config.getIva())
        );

        // Then
        assertEquals(0, new BigDecimal("116.00").compareTo(precioConIva));
    }

    @Test
    @DisplayName("Debe manejar gastos fijos grandes")
    void testGastosFijosGrandes() {
        // Given
        ConfiguracionFinanciera config = new ConfiguracionFinanciera();
        BigDecimal gastosGrandes = new BigDecimal("500000.00");

        // When
        config.setGastosFijosMensuales(gastosGrandes);

        // Then
        assertEquals(gastosGrandes, config.getGastosFijosMensuales());
        assertTrue(config.getGastosFijosMensuales()
                .compareTo(new BigDecimal("100000.00")) > 0);
    }

    @Test
    @DisplayName("Debe validar meta mayor que gastos")
    void testMetaMayorQueGastos() {
        // Given
        ConfiguracionFinanciera config = new ConfiguracionFinanciera();
        config.setGastosFijosMensuales(new BigDecimal("50000.00"));
        config.setMetaVentasMensual(new BigDecimal("200000.00"));

        // Then
        assertTrue(config.getMetaVentasMensual()
                .compareTo(config.getGastosFijosMensuales()) > 0);
    }

    @Test
    @DisplayName("Debe permitir valores null")
    void testValoresNull() {
        // Given & When
        ConfiguracionFinanciera config = new ConfiguracionFinanciera();
        config.setGastosFijosMensuales(null);
        config.setMetaVentasMensual(null);
        config.setMargenGananciaBase(null);
        config.setComisionTarjeta(null);
        config.setIva(null);

        // Then
        assertNull(config.getGastosFijosMensuales());
        assertNull(config.getMetaVentasMensual());
        assertNull(config.getMargenGananciaBase());
        assertNull(config.getComisionTarjeta());
        assertNull(config.getIva());
    }

    @Test
    @DisplayName("Debe crear instancia sin valores por defecto")
    void testInstanciaPorDefecto() {
        // Given & When
        ConfiguracionFinanciera config = new ConfiguracionFinanciera();

        // Then
        assertNotNull(config);
        assertNull(config.getId());
        assertNull(config.getGastosFijosMensuales());
        assertNull(config.getMetaVentasMensual());
        assertNull(config.getMargenGananciaBase());
        assertNull(config.getComisionTarjeta());
        assertNull(config.getIva());
    }
}
