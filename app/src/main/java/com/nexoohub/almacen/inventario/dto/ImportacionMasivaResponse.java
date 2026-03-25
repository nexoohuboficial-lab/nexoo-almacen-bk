package com.nexoohub.almacen.inventario.dto;

import java.util.List;

/**
 * Resultado de la importación masiva: resumen global + detalle por fila.
 */
public class ImportacionMasivaResponse {

    private int totalProcesados;
    private int totalExitosos;
    private int totalFallidos;
    private List<FilaResultado> detalle;

    public static class FilaResultado {
        private String skuInterno;
        private boolean exitoso;
        private String mensaje;

        public FilaResultado(String skuInterno, boolean exitoso, String mensaje) {
            this.skuInterno = skuInterno;
            this.exitoso = exitoso;
            this.mensaje = mensaje;
        }

        public String getSkuInterno() { return skuInterno; }
        public boolean isExitoso() { return exitoso; }
        public String getMensaje() { return mensaje; }
    }

    // ==========================================
    // GETTERS Y SETTERS
    // ==========================================

    public int getTotalProcesados() { return totalProcesados; }
    public void setTotalProcesados(int totalProcesados) { this.totalProcesados = totalProcesados; }
    public int getTotalExitosos() { return totalExitosos; }
    public void setTotalExitosos(int totalExitosos) { this.totalExitosos = totalExitosos; }
    public int getTotalFallidos() { return totalFallidos; }
    public void setTotalFallidos(int totalFallidos) { this.totalFallidos = totalFallidos; }
    public List<FilaResultado> getDetalle() { return detalle; }
    public void setDetalle(List<FilaResultado> detalle) { this.detalle = detalle; }
}
