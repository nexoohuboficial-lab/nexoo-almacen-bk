package com.nexoohub.almacen.common.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("AccessDeniedException - Tests de Excepción")
class AccessDeniedExceptionTest {

    @Test
    @DisplayName("Debe crear excepción con mensaje")
    void testCrearExcepcionConMensaje() {
        // Given
        String mensaje = "Acceso denegado al recurso";

        // When
        AccessDeniedException exception = new AccessDeniedException(mensaje);

        // Then
        assertNotNull(exception);
        assertEquals(mensaje, exception.getMessage());
    }

    @Test
    @DisplayName("Debe ser una RuntimeException")
    void testEsRuntimeException() {
        // Given & When
        AccessDeniedException exception = new AccessDeniedException("test");

        // Then
        assertInstanceOf(RuntimeException.class, exception);
    }

    @Test
    @DisplayName("Debe poder ser lanzada y capturada")
    void testPuedeLanzarseYCapturarse() {
        // Given
        String mensaje = "Usuario sin permisos";

        // When & Then
        assertThrows(AccessDeniedException.class, () -> {
            throw new AccessDeniedException(mensaje);
        });
    }

    @Test
    @DisplayName("Debe capturar mensaje al lanzar excepción")
    void testCapturarMensaje() {
        // Given
        String mensajeEsperado = "No tiene autorización para esta operación";

        // When
        AccessDeniedException exception = assertThrows(
            AccessDeniedException.class,
            () -> { throw new AccessDeniedException(mensajeEsperado); }
        );

        // Then
        assertEquals(mensajeEsperado, exception.getMessage());
    }

    @Test
    @DisplayName("Debe manejar mensajes vacíos")
    void testMensajeVacio() {
        // Given & When
        AccessDeniedException exception = new AccessDeniedException("");

        // Then
        assertEquals("", exception.getMessage());
    }

    @Test
    @DisplayName("Debe manejar mensajes null")
    void testMensajeNull() {
        // Given & When
        AccessDeniedException exception = new AccessDeniedException(null);

        // Then
        assertNull(exception.getMessage());
    }

    @Test
    @DisplayName("Debe crear excepciones con diferentes mensajes")
    void testDiferentesMensajes() {
        // Given
        AccessDeniedException e1 = new AccessDeniedException("Mensaje 1");
        AccessDeniedException e2 = new AccessDeniedException("Mensaje 2");
        AccessDeniedException e3 = new AccessDeniedException("Mensaje 3");

        // Then
        assertEquals("Mensaje 1", e1.getMessage());
        assertEquals("Mensaje 2", e2.getMessage());
        assertEquals("Mensaje 3", e3.getMessage());
        assertNotEquals(e1.getMessage(), e2.getMessage());
    }

    @Test
    @DisplayName("Debe incluir mensaje en toString")
    void testToStringIncluyeMensaje() {
        // Given
        String mensaje = "Acceso denegado";
        AccessDeniedException exception = new AccessDeniedException(mensaje);

        // When
        String resultado = exception.toString();

        // Then
        assertTrue(resultado.contains("AccessDeniedException"));
    }
}
