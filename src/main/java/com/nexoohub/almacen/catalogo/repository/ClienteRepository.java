package com.nexoohub.almacen.catalogo.repository;

import com.nexoohub.almacen.catalogo.dto.ClienteBloqueadoDTO;
import com.nexoohub.almacen.catalogo.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Integer> {
    
    /**
     * Obtiene todos los clientes bloqueados por morosidad.
     * 
     * @return lista de clientes bloqueados
     */
    @Query("SELECT new com.nexoohub.almacen.catalogo.dto.ClienteBloqueadoDTO(" +
           "c.id, c.nombre, c.rfc, c.telefono, c.saldoPendiente, " +
           "c.motivoBloqueo, c.bloqueado) " +
           "FROM Cliente c " +
           "WHERE c.bloqueado = true " +
           "ORDER BY c.saldoPendiente DESC")
    List<ClienteBloqueadoDTO> obtenerClientesBloqueados();
    
    /**
     * Obtiene clientes con saldo pendiente.
     * 
     * @return lista de clientes con deuda
     */
    @Query("SELECT new com.nexoohub.almacen.catalogo.dto.ClienteBloqueadoDTO(" +
           "c.id, c.nombre, c.rfc, c.telefono, c.saldoPendiente, " +
           "c.motivoBloqueo, c.bloqueado) " +
           "FROM Cliente c " +
           "WHERE c.saldoPendiente > 0 " +
           "ORDER BY c.saldoPendiente DESC")
    List<ClienteBloqueadoDTO> obtenerClientesConSaldoPendiente();
}