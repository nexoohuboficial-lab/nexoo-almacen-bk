package com.nexoohub.almacen.catalogo.controller;

import com.nexoohub.almacen.catalogo.dto.ClienteResponseDTO;
import com.nexoohub.almacen.catalogo.entity.Cliente;
import com.nexoohub.almacen.catalogo.mapper.ClienteMapper;
import com.nexoohub.almacen.catalogo.repository.ClienteRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller REST para gestión de clientes.
 * 
 * <p>Proporciona endpoints para administración de clientes incluyendo:</p>
 * <ul>
 *   <li>Registro de clientes con tipos (Público General, Taller, Mayorista)</li>
 *   <li>Consulta paginada de clientes</li>
 *   <li>Actualización de datos de contacto y fiscales</li>
 * </ul>
 * 
 * <p><b>Seguridad:</b> Retorna DTOs para ocultar campos de auditoría.</p>
 * 
 * @author NexooHub Development Team
 * @version 1.0
 */
@RestController
@RequestMapping("/api/v1/clientes")
public class ClienteController {

    @Autowired
    private ClienteRepository clienteRepository;
    
    @Autowired
    private ClienteMapper mapper;

    /**
     * Lista todos los clientes del sistema con paginación.
     * 
     * @param pageable configuración de paginación (default: 20 items, ordenados por nombre)
     * @return página de clientes en formato DTO
     */
    @GetMapping
    public ResponseEntity<Page<ClienteResponseDTO>> listarClientes(
            @PageableDefault(size = 20, sort = "nombre") Pageable pageable) {
        Page<Cliente> clientes = clienteRepository.findAll(pageable);
        return ResponseEntity.ok(clientes.map(mapper::toResponseDTO));
    }

    /**
     * Registra un nuevo cliente en el sistema.
     * 
     * @param cliente datos del cliente a registrar (validados con @Valid)
     * @return respuesta con ID generado y mensaje de éxito
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> crearCliente(@Valid @RequestBody Cliente cliente) {
        Cliente guardado = clienteRepository.save(cliente);
        
        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("exitoso", true);
        respuesta.put("mensaje", "Cliente registrado correctamente");
        respuesta.put("id", guardado.getId());
        
        return new ResponseEntity<>(respuesta, HttpStatus.CREATED);
    }

    /**
     * Actualiza datos de un cliente existente.
     * 
     * <p>Permite actualizar: nombre, tipo de cliente, RFC, teléfono, email y dirección fiscal.</p>
     * 
     * @param id identificador del cliente
     * @param detalles nuevos datos del cliente
     * @return respuesta de éxito o 404 si no existe
     */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> actualizarCliente(@PathVariable("id") Integer id, @Valid @RequestBody Cliente detalles) {
        return clienteRepository.findById(id)
                .map(clienteExistente -> {
                    clienteExistente.setNombre(detalles.getNombre());
                    clienteExistente.setTipoClienteId(detalles.getTipoClienteId());
                    clienteExistente.setRfc(detalles.getRfc());
                    clienteExistente.setTelefono(detalles.getTelefono());
                    clienteExistente.setEmail(detalles.getEmail());
                    clienteExistente.setDireccionFiscal(detalles.getDireccionFiscal());
                    clienteRepository.save(clienteExistente);
                    
                    Map<String, Object> respuesta = new HashMap<>();
                    respuesta.put("exitoso", true);
                    respuesta.put("mensaje", "Datos del cliente actualizados correctamente");
                    
                    return ResponseEntity.ok(respuesta);
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
