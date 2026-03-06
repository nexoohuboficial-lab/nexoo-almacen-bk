package com.nexoohub.almacen.catalogo.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Moto - Tests de Entidad")
class MotoTest {

    @Test
    @DisplayName("Debe crear moto con ID")
    void testCrearMotoConId() {
        // Given & When
        Moto moto = new Moto();
        moto.setId(1);

        // Then
        assertEquals(1, moto.getId());
    }

    @Test
    @DisplayName("Debe establecer y obtener marca")
    void testMarca() {
        // Given
        Moto moto = new Moto();

        // When
        moto.setMarca("Honda");

        // Then
        assertEquals("Honda", moto.getMarca());
    }

    @Test
    @DisplayName("Debe establecer y obtener modelo")
    void testModelo() {
        // Given
        Moto moto = new Moto();

        // When
        moto.setModelo("CBR 600");

        // Then
        assertEquals("CBR 600", moto.getModelo());
    }

    @Test
    @DisplayName("Debe establecer y obtener cilindrada")
    void testCilindrada() {
        // Given
        Moto moto = new Moto();

        // When
        moto.setCilindrada(600);

        // Then
        assertEquals(600, moto.getCilindrada());
    }

    @Test
    @DisplayName("Debe crear moto completa")
    void testMotoCompleta() {
        // Given & When
        Moto moto = new Moto();
        moto.setId(10);
        moto.setMarca("Yamaha");
        moto.setModelo("YZF-R1");
        moto.setCilindrada(1000);

        // Then
        assertNotNull(moto);
        assertEquals(10, moto.getId());
        assertEquals("Yamaha", moto.getMarca());
        assertEquals("YZF-R1", moto.getModelo());
        assertEquals(1000, moto.getCilindrada());
    }

    @Test
    @DisplayName("Debe manejar diferentes marcas")
    void testDiferentesMarcas() {
        // Given
        Moto moto1 = new Moto();
        Moto moto2 = new Moto();
        Moto moto3 = new Moto();
        Moto moto4 = new Moto();

        // When
        moto1.setMarca("Honda");
        moto2.setMarca("Yamaha");
        moto3.setMarca("Suzuki");
        moto4.setMarca("Kawasaki");

        // Then
        assertEquals("Honda", moto1.getMarca());
        assertEquals("Yamaha", moto2.getMarca());
        assertEquals("Suzuki", moto3.getMarca());
        assertEquals("Kawasaki", moto4.getMarca());
    }

    @Test
    @DisplayName("Debe manejar diferentes cilindradas")
    void testDiferentesCilindradas() {
        // Given
        Moto moto1 = new Moto();
        Moto moto2 = new Moto();
        Moto moto3 = new Moto();

        // When
        moto1.setCilindrada(125);
        moto2.setCilindrada(250);
        moto3.setCilindrada(1000);

        // Then
        assertEquals(125, moto1.getCilindrada());
        assertEquals(250, moto2.getCilindrada());
        assertEquals(1000, moto3.getCilindrada());
    }

    @Test
    @DisplayName("Debe manejar modelos con caracteres especiales")
    void testModelosEspeciales() {
        // Given
        Moto moto = new Moto();

        // When
        moto.setModelo("XL 700V Transalp");

        // Then
        assertEquals("XL 700V Transalp", moto.getModelo());
    }

    @Test
    @DisplayName("Debe permitir cilindrada null")
    void testCilindradaNull() {
        // Given & When
        Moto moto = new Moto();
        moto.setMarca("Kawasaki");
        moto.setModelo("Ninja");
        moto.setCilindrada(null);

        // Then
        assertEquals("Kawasaki", moto.getMarca());
        assertEquals("Ninja", moto.getModelo());
        assertNull(moto.getCilindrada());
    }

    @Test
    @DisplayName("Debe crear instancia sin valores por defecto")
    void testInstanciaPorDefecto() {
        // Given & When
        Moto moto = new Moto();

        // Then
        assertNotNull(moto);
        assertNull(moto.getId());
        assertNull(moto.getMarca());
        assertNull(moto.getModelo());
        assertNull(moto.getCilindrada());
    }

    @Test
    @DisplayName("Debe crear diferentes motos Honda")
    void testDiferentesMotosHonda() {
        // Given
        Moto cbr = new Moto();
        Moto cb = new Moto();
        Moto crf = new Moto();

        // When
        cbr.setMarca("Honda");
        cbr.setModelo("CBR 1000RR");
        cbr.setCilindrada(1000);

        cb.setMarca("Honda");
        cb.setModelo("CB 500F");
        cb.setCilindrada(500);

        crf.setMarca("Honda");
        crf.setModelo("CRF 250L");
        crf.setCilindrada(250);

        // Then
        assertEquals("CBR 1000RR", cbr.getModelo());
        assertEquals("CB 500F", cb.getModelo());
        assertEquals("CRF 250L", crf.getModelo());
        assertTrue(cbr.getCilindrada() > cb.getCilindrada());
        assertTrue(cb.getCilindrada() > crf.getCilindrada());
    }
}
