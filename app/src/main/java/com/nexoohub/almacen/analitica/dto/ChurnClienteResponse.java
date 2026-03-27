package com.nexoohub.almacen.analitica.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ChurnClienteResponse {
    private Integer clienteId;
    private String nombreCliente;
    private String telefono;
    private String email;
    private Integer scoreRiesgo;
    private String factoresRiesgo;
    private Integer diasSinComprar;
    private Integer frecuenciaPromedioDias;
    private LocalDateTime fechaAnalisis;
}
