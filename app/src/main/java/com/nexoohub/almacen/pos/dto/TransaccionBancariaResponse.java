package com.nexoohub.almacen.pos.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransaccionBancariaResponse {

    private String referenciaVenta;
    private BigDecimal monto;
    private String tipoOperacion;
    private String estatus;
    private String autorizacionBanco;
    private String terminalId;
    private String tarjetaTerminacion;
    private String marcaTarjeta;
    private String mensajeRespuesta;
    private LocalDateTime fechaTransaccion;

    public String getReferenciaVenta() { return referenciaVenta; }
    public void setReferenciaVenta(String referenciaVenta) { this.referenciaVenta = referenciaVenta; }

    public BigDecimal getMonto() { return monto; }
    public void setMonto(BigDecimal monto) { this.monto = monto; }

    public String getTipoOperacion() { return tipoOperacion; }
    public void setTipoOperacion(String tipoOperacion) { this.tipoOperacion = tipoOperacion; }

    public String getEstatus() { return estatus; }
    public void setEstatus(String estatus) { this.estatus = estatus; }

    public String getAutorizacionBanco() { return autorizacionBanco; }
    public void setAutorizacionBanco(String autorizacionBanco) { this.autorizacionBanco = autorizacionBanco; }

    public String getTerminalId() { return terminalId; }
    public void setTerminalId(String terminalId) { this.terminalId = terminalId; }

    public String getTarjetaTerminacion() { return tarjetaTerminacion; }
    public void setTarjetaTerminacion(String tarjetaTerminacion) { this.tarjetaTerminacion = tarjetaTerminacion; }

    public String getMarcaTarjeta() { return marcaTarjeta; }
    public void setMarcaTarjeta(String marcaTarjeta) { this.marcaTarjeta = marcaTarjeta; }

    public String getMensajeRespuesta() { return mensajeRespuesta; }
    public void setMensajeRespuesta(String mensajeRespuesta) { this.mensajeRespuesta = mensajeRespuesta; }

    public LocalDateTime getFechaTransaccion() { return fechaTransaccion; }
    public void setFechaTransaccion(LocalDateTime fechaTransaccion) { this.fechaTransaccion = fechaTransaccion; }
}
