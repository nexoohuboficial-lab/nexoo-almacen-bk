package com.nexoohub.almacen.pos.dto;

import jakarta.validation.constraints.NotBlank;

public class CancelacionCfdiRequest {

    @NotBlank(message = "El motivo de cancelación es obligatorio (01, 02, 03, 04)")
    private String motivoCancelacion;

    /** Aplica si motivo = 01 (Comprobantes emitidos con errores con relación) */
    private String folioSustitucion;

    public String getMotivoCancelacion() { return motivoCancelacion; }
    public void setMotivoCancelacion(String motivoCancelacion) { this.motivoCancelacion = motivoCancelacion; }
    
    public String getFolioSustitucion() { return folioSustitucion; }
    public void setFolioSustitucion(String folioSustitucion) { this.folioSustitucion = folioSustitucion; }
}
