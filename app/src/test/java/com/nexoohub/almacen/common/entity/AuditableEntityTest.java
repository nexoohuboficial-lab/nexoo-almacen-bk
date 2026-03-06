package com.nexoohub.almacen.common.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("AuditableEntity - Tests de Entidad Base")
class AuditableEntityTest {

    // Clase concreta de prueba para poder instanciar AuditableEntity
    static class TestAuditableEntity extends AuditableEntity {
        private Long id;
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
    }

    @Test
    @DisplayName("Debe establecer y obtener fechaCreacion")
    void testFechaCreacion() {
        // Given
        TestAuditableEntity entity = new TestAuditableEntity();
        LocalDateTime fecha = LocalDateTime.now();

        // When
        entity.setFechaCreacion(fecha);

        // Then
        assertEquals(fecha, entity.getFechaCreacion());
    }

    @Test
    @DisplayName("Debe establecer y obtener usuarioCreacion")
    void testUsuarioCreacion() {
        // Given
        TestAuditableEntity entity = new TestAuditableEntity();

        // When
        entity.setUsuarioCreacion("admin");

        // Then
        assertEquals("admin", entity.getUsuarioCreacion());
    }

    @Test
    @DisplayName("Debe establecer y obtener fechaActualizacion")
    void testFechaActualizacion() {
        // Given
        TestAuditableEntity entity = new TestAuditableEntity();
        LocalDateTime fecha = LocalDateTime.now();

        // When
        entity.setFechaActualizacion(fecha);

        // Then
        assertEquals(fecha, entity.getFechaActualizacion());
    }

    @Test
    @DisplayName("Debe establecer y obtener usuarioActualizacion")
    void testUsuarioActualizacion() {
        // Given
        TestAuditableEntity entity = new TestAuditableEntity();

        // When
        entity.setUsuarioActualizacion("editor");

        // Then
        assertEquals("editor", entity.getUsuarioActualizacion());
    }

    @Test
    @DisplayName("Debe establecer todos los campos de auditoría")
    void testCamposAuditoriaCompletos() {
        // Given
        TestAuditableEntity entity = new TestAuditableEntity();
        LocalDateTime fechaCreacion = LocalDateTime.of(2024, 1, 1, 10, 0);
        LocalDateTime fechaActualizacion = LocalDateTime.of(2024, 1, 2, 15, 30);

        // When
        entity.setFechaCreacion(fechaCreacion);
        entity.setUsuarioCreacion("creador");
        entity.setFechaActualizacion(fechaActualizacion);
        entity.setUsuarioActualizacion("actualizador");

        // Then
        assertNotNull(entity);
        assertEquals(fechaCreacion, entity.getFechaCreacion());
        assertEquals("creador", entity.getUsuarioCreacion());
        assertEquals(fechaActualizacion, entity.getFechaActualizacion());
        assertEquals("actualizador", entity.getUsuarioActualizacion());
    }

    @Test
    @DisplayName("Debe permitir valores null en campos de auditoría")
    void testValoresNull() {
        // Given & When
        TestAuditableEntity entity = new TestAuditableEntity();
        entity.setFechaCreacion(null);
        entity.setUsuarioCreacion(null);
        entity.setFechaActualizacion(null);
        entity.setUsuarioActualizacion(null);

        // Then
        assertNotNull(entity);
        assertNull(entity.getFechaCreacion());
        assertNull(entity.getUsuarioCreacion());
        assertNull(entity.getFechaActualizacion());
        assertNull(entity.getUsuarioActualizacion());
    }

    @Test
    @DisplayName("Debe crear instancia sin valores por defecto")
    void testInstanciaPorDefecto() {
        // Given & When
        TestAuditableEntity entity = new TestAuditableEntity();

        // Then
        assertNotNull(entity);
        assertNull(entity.getFechaCreacion());
        assertNull(entity.getUsuarioCreacion());
        assertNull(entity.getFechaActualizacion());
        assertNull(entity.getUsuarioActualizacion());
    }

    @Test
    @DisplayName("Debe manejar múltiples actualizaciones")
    void testMultiplesActualizaciones() {
        // Given
        TestAuditableEntity entity = new TestAuditableEntity();
        LocalDateTime fecha1 = LocalDateTime.now().minusDays(1);
        LocalDateTime fecha2 = LocalDateTime.now();

        // When
        entity.setFechaCreacion(fecha1);
        entity.setUsuarioCreacion("admin");
        
        entity.setFechaActualizacion(fecha2);
        entity.setUsuarioActualizacion("editor");

        // Then
        assertTrue(entity.getFechaActualizacion().isAfter(entity.getFechaCreacion()));
        assertNotEquals(entity.getUsuarioCreacion(), entity.getUsuarioActualizacion());
    }

    @Test
    @DisplayName("Debe validar que fechaActualizacion sea posterior a fechaCreacion")
    void testOrdenFechas() {
        // Given
        TestAuditableEntity entity = new TestAuditableEntity();
        LocalDateTime fechaCreacion = LocalDateTime.of(2024, 1, 1, 10, 0);
        LocalDateTime fechaActualizacion = LocalDateTime.of(2024, 1, 5, 14, 30);

        // When
        entity.setFechaCreacion(fechaCreacion);
        entity.setFechaActualizacion(fechaActualizacion);

        // Then
        assertTrue(entity.getFechaActualizacion().isAfter(entity.getFechaCreacion()));
    }

    @Test
    @DisplayName("Debe manejar nombres de usuario largos")
    void testNombresUsuarioLargos() {
        // Given
        TestAuditableEntity entity = new TestAuditableEntity();
        String nombreLargo = "usuario.con.nombre.muy.largo@empresa.com";

        // When
        entity.setUsuarioCreacion(nombreLargo);
        entity.setUsuarioActualizacion(nombreLargo);

        // Then
        assertEquals(nombreLargo, entity.getUsuarioCreacion());
        assertEquals(nombreLargo, entity.getUsuarioActualizacion());
    }

    @Test
    @DisplayName("Debe heredarse correctamente en subclases")
    void testHerenciaEnSubclases() {
        // Given & When
        TestAuditableEntity entity = new TestAuditableEntity();
        entity.setId(1L);
        entity.setUsuarioCreacion("test");

        // Then
        assertNotNull(entity);
        assertEquals(1L, entity.getId());
        assertEquals("test", entity.getUsuarioCreacion());
    }
}
