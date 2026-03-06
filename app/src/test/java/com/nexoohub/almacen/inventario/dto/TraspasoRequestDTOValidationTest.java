package com.nexoohub.almacen.inventario.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("TraspasoRequestDTO - Tests de Validación")
class TraspasoRequestDTOValidationTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Debe validar correctamente un DTO completo y válido")
    void testDTOValido() {
        // Given
        TraspasoRequestDTO dto = crearDTOValido();

        // When
        Set<ConstraintViolation<TraspasoRequestDTO>> violations = validator.validate(dto);

        // Then
        assertTrue(violations.isEmpty(), "No debería haber violaciones de validación");
    }

    @Test
    @DisplayName("Debe fallar cuando sucursalOrigenId es null")
    void testSucursalOrigenIdNull() {
        // Given
        TraspasoRequestDTO dto = crearDTOValido();
        dto.setSucursalOrigenId(null);

        // When
        Set<ConstraintViolation<TraspasoRequestDTO>> violations = validator.validate(dto);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().contains("sucursal de origen")));
    }

    @Test
    @DisplayName("Debe fallar cuando sucursalDestinoId es null")
    void testSucursalDestinoIdNull() {
        // Given
        TraspasoRequestDTO dto = crearDTOValido();
        dto.setSucursalDestinoId(null);

        // When
        Set<ConstraintViolation<TraspasoRequestDTO>> violations = validator.validate(dto);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().contains("sucursal de destino")));
    }

    @Test
    @DisplayName("Debe fallar cuando items está vacío")
    void testItemsVacio() {
        // Given
        TraspasoRequestDTO dto = crearDTOValido();
        dto.setItems(new ArrayList<>());

        // When
        Set<ConstraintViolation<TraspasoRequestDTO>> violations = validator.validate(dto);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().contains("al menos un producto")));
    }

    @Test
    @DisplayName("Debe fallar cuando items es null")
    void testItemsNull() {
        // Given
        TraspasoRequestDTO dto = crearDTOValido();
        dto.setItems(null);

        // When
        Set<ConstraintViolation<TraspasoRequestDTO>> violations = validator.validate(dto);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().contains("al menos un producto")));
    }

    @Test
    @DisplayName("Debe permitir comentarios null (campo opcional)")
    void testComentariosOpcional() {
        // Given
        TraspasoRequestDTO dto = crearDTOValido();
        dto.setComentarios(null);

        // When
        Set<ConstraintViolation<TraspasoRequestDTO>> violations = validator.validate(dto);

        // Then
        assertTrue(violations.isEmpty(), "Comentarios es opcional, no debería fallar");
    }

    @Test
    @DisplayName("Debe validar correctamente múltiples items")
    void testMultiplesItems() {
        // Given
        TraspasoRequestDTO dto = crearDTOValido();
        
        TraspasoRequestDTO.ItemTraspasoDTO item2 = new TraspasoRequestDTO.ItemTraspasoDTO();
        item2.setSkuInterno("SKU002");
        item2.setCantidad(10);
        
        TraspasoRequestDTO.ItemTraspasoDTO item3 = new TraspasoRequestDTO.ItemTraspasoDTO();
        item3.setSkuInterno("SKU003");
        item3.setCantidad(15);
        
        dto.setItems(List.of(dto.getItems().get(0), item2, item3));

        // When
        Set<ConstraintViolation<TraspasoRequestDTO>> violations = validator.validate(dto);

        // Then
        assertTrue(violations.isEmpty(), "Múltiples items válidos no deberían generar violaciones");
    }

    @Test
    @DisplayName("ItemTraspasoDTO - Debe fallar cuando skuInterno es null")
    void testItemSkuInternoNull() {
        // Given
        TraspasoRequestDTO dto = crearDTOValido();
        TraspasoRequestDTO.ItemTraspasoDTO item = new TraspasoRequestDTO.ItemTraspasoDTO();
        item.setSkuInterno(null);
        item.setCantidad(5);
        dto.setItems(List.of(item));

        // When
        Set<ConstraintViolation<TraspasoRequestDTO>> violations = validator.validate(dto);

        // Then
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("ItemTraspasoDTO - Debe fallar cuando cantidad es null")
    void testItemCantidadNull() {
        // Given
        TraspasoRequestDTO dto = crearDTOValido();
        TraspasoRequestDTO.ItemTraspasoDTO item = new TraspasoRequestDTO.ItemTraspasoDTO();
        item.setSkuInterno("SKU001");
        item.setCantidad(null);
        dto.setItems(List.of(item));

        // When
        Set<ConstraintViolation<TraspasoRequestDTO>> violations = validator.validate(dto);

        // Then
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("ItemTraspasoDTO - Debe validar correctamente getters y setters")
    void testItemGettersSetters() {
        // Given
        TraspasoRequestDTO.ItemTraspasoDTO item = new TraspasoRequestDTO.ItemTraspasoDTO();
        
        // When
        item.setSkuInterno("SKU999");
        item.setCantidad(20);

        // Then
        assertEquals("SKU999", item.getSkuInterno());
        assertEquals(20, item.getCantidad());
    }

    // ==================== Métodos Auxiliares ====================

    private TraspasoRequestDTO crearDTOValido() {
        TraspasoRequestDTO dto = new TraspasoRequestDTO();
        dto.setSucursalOrigenId(1);
        dto.setSucursalDestinoId(2);
        dto.setComentarios("Traspaso de prueba");

        TraspasoRequestDTO.ItemTraspasoDTO item = new TraspasoRequestDTO.ItemTraspasoDTO();
        item.setSkuInterno("SKU001");
        item.setCantidad(5);

        dto.setItems(List.of(item));
        return dto;
    }
}
