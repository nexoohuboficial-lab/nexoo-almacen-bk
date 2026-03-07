package com.nexoohub.almacen.catalogo.dto;

import java.math.BigDecimal;

/**
 * DTO para clientes bloqueados por morosidad.
 * 
 * @author NexooHub Development Team
 * @since 1.0
 */
public class ClienteBloqueadoDTO {
    private final Integer id;
    private final String nombre;
    private final String rfc;
    private final String telefono;
    private final BigDecimal saldoPendiente;
    private final String motivoBloqueo;
    private final Boolean bloqueado;
    
    public ClienteBloqueadoDTO(
            Integer id,
            String nombre,
            String rfc,
            String telefono,
            BigDecimal saldoPendiente,
            String motivoBloqueo,
            Boolean bloqueado) {
        this.id = id;
        this.nombre = nombre;
        this.rfc = rfc;
        this.telefono = telefono;
        this.saldoPendiente = saldoPendiente;
        this.motivoBloqueo = motivoBloqueo;
        this.bloqueado = bloqueado;
    }
    
    // Getters
    public Integer getId() { return id; }
    public String getNombre() { return nombre; }
    public String getRfc() { return rfc; }
    public String getTelefono() { return telefono; }
    public BigDecimal getSaldoPendiente() { return saldoPendiente; }
    public String getMotivoBloqueo() { return motivoBloqueo; }
    public Boolean getBloqueado() { return bloqueado; }
}
