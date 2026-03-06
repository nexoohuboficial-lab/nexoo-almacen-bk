package com.nexoohub.almacen.catalogo.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Categoria - Tests de Entidad")
class CategoriaTest {

    @Test
    @DisplayName("Debe crear categoria con ID")
    void testCrearCategoriaConId() {
        // Given & When
        Categoria categoria = new Categoria();
        categoria.setId(1);

        // Then
        assertEquals(1, categoria.getId());
    }

    @Test
    @DisplayName("Debe establecer y obtener nombre")
    void testNombre() {
        // Given
        Categoria categoria = new Categoria();

        // When
        categoria.setNombre("Repuestos de Motor");

        // Then
        assertEquals("Repuestos de Motor", categoria.getNombre());
    }

    @Test
    @DisplayName("Debe establecer y obtener descripcion")
    void testDescripcion() {
        // Given
        Categoria categoria = new Categoria();

        // When
        categoria.setDescripcion("Piezas y componentes para motores de motocicletas");

        // Then
        assertEquals("Piezas y componentes para motores de motocicletas", categoria.getDescripcion());
    }

    @Test
    @DisplayName("Debe crear categoria completa")
    void testCategoriaCompleta() {
        // Given & When
        Categoria categoria = new Categoria();
        categoria.setId(10);
        categoria.setNombre("Sistema de Frenos");
        categoria.setDescripcion("Pastillas, discos y componentes del sistema de frenos");

        // Then
        assertNotNull(categoria);
        assertEquals(10, categoria.getId());
        assertEquals("Sistema de Frenos", categoria.getNombre());
        assertEquals("Pastillas, discos y componentes del sistema de frenos", categoria.getDescripcion());
    }

    @Test
    @DisplayName("Debe permitir descripcion null")
    void testDescripcionNull() {
        // Given & When
        Categoria categoria = new Categoria();
        categoria.setNombre("Categoría Prueba");
        categoria.setDescripcion(null);

        // Then
        assertEquals("Categoría Prueba", categoria.getNombre());
        assertNull(categoria.getDescripcion());
    }

    @Test
    @DisplayName("Debe crear instancia sin valores por defecto")
    void testInstanciaPorDefecto() {
        // Given & When
        Categoria categoria = new Categoria();

        // Then
        assertNotNull(categoria);
        assertNull(categoria.getId());
        assertNull(categoria.getNombre());
        assertNull(categoria.getDescripcion());
    }

    @Test
    @DisplayName("Debe manejar nombres con caracteres especiales")
    void testNombresEspeciales() {
        // Given
        Categoria categoria = new Categoria();

        // When
        categoria.setNombre("Categoría #1 & Especial (Test)");

        // Then
        assertEquals("Categoría #1 & Especial (Test)", categoria.getNombre());
    }

    @Test
    @DisplayName("Debe manejar descripciones largas")
    void testDescripcionLarga() {
        // Given
        Categoria categoria = new Categoria();
        String descripcionLarga = "Esta es una descripción muy larga que contiene múltiples palabras " +
                "y explica en detalle qué tipo de productos pertenecen a esta categoría. " +
                "Incluye especificaciones técnicas, recomendaciones de uso y advertencias importantes.";

        // When
        categoria.setDescripcion(descripcionLarga);

        // Then
        assertEquals(descripcionLarga, categoria.getDescripcion());
        assertTrue(categoria.getDescripcion().length() > 100);
    }
}
