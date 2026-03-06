package com.nexoohub.almacen.catalogo.controller;

import com.nexoohub.almacen.catalogo.entity.Cliente;
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

@RestController
@RequestMapping("/api/v1/clientes")
public class ClienteController {

    @Autowired
    private ClienteRepository clienteRepository;

    // 1. LISTAR TODOS LOS CLIENTES
    @GetMapping
    public ResponseEntity<Page<Cliente>> listarClientes(
            @PageableDefault(size = 20, sort = "nombre") Pageable pageable) {
        return ResponseEntity.ok(clienteRepository.findAll(pageable));
    }

    // 2. REGISTRAR UN NUEVO CLIENTE (Taller, Mayorista, etc.)
    @PostMapping
    public ResponseEntity<Map<String, Object>> crearCliente(@Valid @RequestBody Cliente cliente) {
        Cliente guardado = clienteRepository.save(cliente);
        
        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("exitoso", true);
        respuesta.put("mensaje", "Cliente registrado correctamente");
        respuesta.put("id", guardado.getId());
        
        return new ResponseEntity<>(respuesta, HttpStatus.CREATED);
    }

    // 3. ACTUALIZAR DATOS (Teléfono, Dirección, RFC)
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> actualizarCliente(@PathVariable Integer id, @Valid @RequestBody Cliente detalles) {
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
