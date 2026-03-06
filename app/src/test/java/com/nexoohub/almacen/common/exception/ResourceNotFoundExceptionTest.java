package com.nexoohub.almacen.common.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ResourceNotFoundException - Tests de Excepción")
class ResourceNotFoundExceptionTest {

    @Test
    @DisplayName("Debe crear excepción con mensaje")
    void testCrearExcepcionConMensaje() {
        // Given
        String mensaje = "Recurso no encontrado";

        // When
        ResourceNotFoundException exception = new ResourceNotFoundException(mensaje);

        // Then
        assertNotNull(exception);
        assertEquals(mensaje, exception.getMessage());
    }

    @Test
    @DisplayName("Debe ser una RuntimeException")
    void testEsRuntimeException() {
        // Given & When
        ResourceNotFoundException exception = new ResourceNotFoundException("test");

        // Then
        assertInstanceOf(RuntimeException.class, exception);
    }

    @Test
    @DisplayName("Debe poder ser lanzada y capturada")
    void testPuedeLanzarseYCapturarse() {
        // Given
        String mensaje = "Producto no encontrado";

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            throw new ResourceNotFoundException(mensaje);
        });
    }

    @Test
    @DisplayName("Debe capturar mensaje al lanzar excepción")
    void testCapturarMensaje() {
        // Given
        String mensajeEsperado = "Cliente con ID 123 no encontrado";

        // When
        ResourceNotFoundException exception = assertThrows(
            ResourceNotFoundException.class,
            () -> { throw new ResourceNotFoundException(mensajeEsperado); }
        );

        // Then
        assertEquals(mensajeEsperado, exception.getMessage());
    }

    @Test
    @DisplayName("Debe manejar mensajes vacíos")
    void testMensajeVacio() {
        // Given & When
        ResourceNotFoundException exception = new ResourceNotFoundException("");

        // Then
        assertEquals("", exception.getMessage());
    }

    @Test
    @DisplayName("Debe manejar mensajes null")
    void testMensajeNull() {
        // Given & When
        ResourceNotFoundException exception = new ResourceNotFoundException(null);

        // Then
        assertNull(exception.getMessage());
    }

    @Test
    @DisplayName("Debe crear excepciones para diferentes recursos")
    void testDiferentesRecursos() {
        // Given
        ResourceNotFoundException e1 = new ResourceNotFoundException("Producto no encontrado");
        ResourceNotFoundException e2 = new ResourceNotFoundException("Cliente no encontrado");
        ResourceNotFoundException e3 = new ResourceNotFoundException("Venta no encontrada");

        // Then
        assertEquals("Producto no encontrado", e1.getMessage());
        assertEquals("Cliente no encontrado", e2.getMessage());
        assertEquals("Venta no encontrada", e3.getMessage());
        assertNotEquals(e1.getMessage(), e2.getMessage());
    }

    @Test
    @DisplayName("Debe manejar mensajes con IDs")
    void testMensajesConIds() {
        // Given & When
        ResourceNotFoundException e1 = new ResourceNotFoundException("Usuario con ID 1 no encontrado");
        ResourceNotFoundException e2 = new ResourceNotFoundException("Sucursal con ID 999 no encontrada");

        // Then
        assertTrue(e1.getMessage().contains("ID 1"));
        assertTrue(e2.getMessage().contains("ID 999"));
    }

    @Test
    @DisplayName("Debe incluir mensaje en toString")
    void testToStringIncluyeMensaje() {
        // Given
        String mensaje = "Recurso inexistente";
        ResourceNotFoundException exception = new ResourceNotFoundException(mensaje);

        // When
        String resultado = exception.toString();

        // Then
        assertTrue(resultado.contains("ResourceNotFoundException"));
    }
}
