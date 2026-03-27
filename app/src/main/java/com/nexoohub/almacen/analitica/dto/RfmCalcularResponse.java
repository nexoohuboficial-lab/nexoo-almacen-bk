package com.nexoohub.almacen.analitica.dto;

public class RfmCalcularResponse {
    private String mensaje;
    private int clientesEvaluados;

    public RfmCalcularResponse(String mensaje, int clientesEvaluados) {
        this.mensaje = mensaje;
        this.clientesEvaluados = clientesEvaluados;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public int getClientesEvaluados() {
        return clientesEvaluados;
    }

    public void setClientesEvaluados(int clientesEvaluados) {
        this.clientesEvaluados = clientesEvaluados;
    }
}
