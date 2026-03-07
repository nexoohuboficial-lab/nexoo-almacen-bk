package com.nexoohub.almacen.catalogo.controller;

import com.nexoohub.almacen.catalogo.dto.ClienteBloqueadoDTO;
import com.nexoohub.almacen.catalogo.entity.Cliente;
import com.nexoohub.almacen.catalogo.repository.ClienteRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * Controlador para gestión de bloqueo de clientes por morosidad.
 * 
 * <p>Endpoints disponibles:</p>
 * <ul>
 *   <li>GET /api/v1/clientes/bloqueados - Obtener clientes bloqueados</li>
 *   <li>GET /api/v1/clientes/morosos - Obtener clientes con saldo pendiente</li>
 *   <li>POST /api/v1/clientes/{id}/bloquear - Bloquear cliente</li>
 *   <li>POST /api/v1/clientes/{id}/desbloquear - Desbloquear cliente</li>
 * </ul>
 * 
 * @author NexooHub Development Team
 * @since 1.0
 */
@RestController
@RequestMapping("/api/v1/clientes")
public class MorosidadController {
    
    private final ClienteRepository clienteRepository;
    
    public MorosidadController(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }
    
    /**
     * Obtiene todos los clientes bloqueados.
     * 
     * @return lista de clientes bloqueados
     */
    @GetMapping("/bloqueados")
    public ResponseEntity<List<ClienteBloqueadoDTO>> obtenerClientesBloqueados() {
        List<ClienteBloqueadoDTO> clientes = clienteRepository.obtenerClientesBloqueados();
        return ResponseEntity.ok(clientes);
    }
    
    /**
     * Obtiene clientes con saldo pendiente.
     * 
     * @return lista de clientes morosos
     */
    @GetMapping("/morosos")
    public ResponseEntity<List<ClienteBloqueadoDTO>> obtenerClientesMorosos() {
        List<ClienteBloqueadoDTO> clientes = clienteRepository.obtenerClientesConSaldoPendiente();
        return ResponseEntity.ok(clientes);
    }
    
    /**
     * Bloquea un cliente por morosidad.
     * 
     * @param id ID del cliente
     * @param motivo motivo del bloqueo
     * @return cliente actualizado
     */
    @PostMapping("/{id}/bloquear")
    public ResponseEntity<Cliente> bloquearCliente(
            @PathVariable("id") Integer id,
            @RequestParam("motivo") String motivo) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado con ID: " + id));
        
        cliente.setBloqueado(true);
        cliente.setMotivoBloqueo(motivo);
        Cliente actualizado = clienteRepository.save(cliente);
        
        return ResponseEntity.ok(actualizado);
    }
    
    /**
     * Desbloquea un cliente.
     * 
     * @param id ID del cliente
     * @return cliente actualizado
     */
    @PostMapping("/{id}/desbloquear")
    public ResponseEntity<Cliente> desbloquearCliente(@PathVariable("id") Integer id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado con ID: " + id));
        
        cliente.setBloqueado(false);
        cliente.setMotivoBloqueo(null);
        Cliente actualizado = clienteRepository.save(cliente);
        
        return ResponseEntity.ok(actualizado);
    }
    
    /**
     * Registra un pago de cliente y actualiza su saldo.
     * 
     * @param id ID del cliente
     * @param monto monto del pago
     * @return cliente actualizado
     */
    @PostMapping("/{id}/registrar-pago")
    public ResponseEntity<Cliente> registrarPago(
            @PathVariable("id") Integer id,
            @RequestParam("monto") BigDecimal monto) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado con ID: " + id));
        
        BigDecimal saldoActual = cliente.getSaldoPendiente();
        BigDecimal nuevoSaldo = saldoActual.subtract(monto);
        
        cliente.setSaldoPendiente(nuevoSaldo.max(BigDecimal.ZERO));
        
        // Si liquidó su deuda, desbloquear automáticamente
        if (cliente.getSaldoPendiente().compareTo(BigDecimal.ZERO) == 0 && cliente.getBloqueado()) {
            cliente.setBloqueado(false);
            cliente.setMotivoBloqueo(null);
        }
        
        Cliente actualizado = clienteRepository.save(cliente);
        
        return ResponseEntity.ok(actualizado);
    }
}
