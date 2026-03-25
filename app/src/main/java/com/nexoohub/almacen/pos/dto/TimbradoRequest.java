package com.nexoohub.almacen.pos.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class TimbradoRequest {

    @NotNull(message = "El ID de la venta es obligatorio")
    private Integer ventaId;

    @NotNull(message = "El ID del cliente es obligatorio")
    private Integer clienteId;

    @NotBlank(message = "El uso de CFDI es obligatorio (ej. G03)")
    private String usoCfdi;

    @NotBlank(message = "El método de pago es obligatorio (PUE/PPD)")
    private String metodoPago;

    @NotBlank(message = "La forma de pago es obligatoria (ej. 01, 04)")
    private String formaPago;

    // Aquí irían datos del cliente si se sobreescriben temporalmente, o se pueden ommir si se sacan del clienteId.
    // Para no acoplar al cliente entity (no existe en este scope de prueba o no la hemos refactorizado acá), pedimos los básicos:
    @NotBlank(message = "El RFC del receptor es obligatorio")
    private String rfcReceptor;

    @NotBlank(message = "La razón social del receptor es obligatoria")
    private String razonSocialReceptor;

    @NotBlank(message = "El CP del receptor es obligatorio")
    private String codigoPostalReceptor;

    @NotBlank(message = "El Régimen Fiscal del receptor es obligatorio")
    private String regimenFiscalReceptor;

    @NotNull(message = "El Monto Total es obligatorio")
    private BigDecimal montoTotal;

    // Getters y Setters
    public Integer getVentaId() { return ventaId; }
    public void setVentaId(Integer ventaId) { this.ventaId = ventaId; }
    public Integer getClienteId() { return clienteId; }
    public void setClienteId(Integer clienteId) { this.clienteId = clienteId; }
    public String getUsoCfdi() { return usoCfdi; }
    public void setUsoCfdi(String usoCfdi) { this.usoCfdi = usoCfdi; }
    public String getMetodoPago() { return metodoPago; }
    public void setMetodoPago(String metodoPago) { this.metodoPago = metodoPago; }
    public String getFormaPago() { return formaPago; }
    public void setFormaPago(String formaPago) { this.formaPago = formaPago; }
    public String getRfcReceptor() { return rfcReceptor; }
    public void setRfcReceptor(String rfcReceptor) { this.rfcReceptor = rfcReceptor; }
    public String getRazonSocialReceptor() { return razonSocialReceptor; }
    public void setRazonSocialReceptor(String razonSocialReceptor) { this.razonSocialReceptor = razonSocialReceptor; }
    public String getCodigoPostalReceptor() { return codigoPostalReceptor; }
    public void setCodigoPostalReceptor(String codigoPostalReceptor) { this.codigoPostalReceptor = codigoPostalReceptor; }
    public String getRegimenFiscalReceptor() { return regimenFiscalReceptor; }
    public void setRegimenFiscalReceptor(String regimenFiscalReceptor) { this.regimenFiscalReceptor = regimenFiscalReceptor; }
    public BigDecimal getMontoTotal() { return montoTotal; }
    public void setMontoTotal(BigDecimal montoTotal) { this.montoTotal = montoTotal; }
}
