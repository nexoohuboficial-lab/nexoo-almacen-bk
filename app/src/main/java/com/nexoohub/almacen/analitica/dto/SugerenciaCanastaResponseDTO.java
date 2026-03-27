package com.nexoohub.almacen.analitica.dto;

import java.util.List;

public record SugerenciaCanastaResponseDTO(
    String skuOrigen,
    List<ReglaAsociacionDTO> recomendaciones
) {}
