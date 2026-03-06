package com.nexoohub.almacen.common.controller;

import com.nexoohub.almacen.common.dto.ActualizarUsuarioDTO;
import com.nexoohub.almacen.common.dto.CambiarPasswordDTO;
import com.nexoohub.almacen.common.dto.UsuarioDTO;
import com.nexoohub.almacen.common.entity.Usuario;
import com.nexoohub.almacen.common.repository.UsuarioRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Controlador REST para gestión de usuarios.
 * Endpoints:
 *   - POST /api/v1/usuarios - Crear nuevo usuario
 *   - GET /api/v1/usuarios - Listar todos los usuarios
 *   - GET /api/v1/usuarios/{id} - Obtener un usuario por ID
 *   - PUT /api/v1/usuarios/{id} - Actualizar datos del usuario
 *   - PUT /api/v1/usuarios/{id}/password - Cambiar contraseña
 *   - DELETE /api/v1/usuarios/{id} - Eliminar usuario
 */
@RestController
@RequestMapping("/api/v1/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Crear un nuevo usuario
     * POST /api/v1/usuarios
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> crearUsuario(@Valid @RequestBody Usuario usuario) {
        
        // Verificar si el username ya existe
        if (usuarioRepository.findByUsername(usuario.getUsername()).isPresent()) {
            Map<String, Object> error = new HashMap<>();
            error.put("exitoso", false);
            error.put("mensaje", "El nombre de usuario '" + usuario.getUsername() + "' ya existe");
            return new ResponseEntity<>(error, HttpStatus.CONFLICT);
        }
        
        // Encriptar la contraseña
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        
        // Guardar en BD
        Usuario guardado = usuarioRepository.save(usuario);
        
        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("exitoso", true);
        respuesta.put("mensaje", "Usuario creado exitosamente");
        respuesta.put("usuario", convertirADTO(guardado));
        
        return new ResponseEntity<>(respuesta, HttpStatus.CREATED);
    }

    /**
     * Listar todos los usuarios (sin passwords)
     * GET /api/v1/usuarios
     */
    @GetMapping
    public ResponseEntity<Page<UsuarioDTO>> listarUsuarios(
            @PageableDefault(size = 20, sort = "username") Pageable pageable) {
        Page<Usuario> usuarios = usuarioRepository.findAll(pageable);
        
        Page<UsuarioDTO> usuariosDTO = usuarios.map(this::convertirADTO);
        
        return ResponseEntity.ok(usuariosDTO);
    }

    /**
     * Obtener un usuario por ID
     * GET /api/v1/usuarios/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerUsuario(@PathVariable Long id) {
        return usuarioRepository.findById(id)
                .map(usuario -> ResponseEntity.ok(convertirADTO(usuario)))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Actualizar datos de un usuario (excepto password)
     * PUT /api/v1/usuarios/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarUsuario(
            @PathVariable Long id, 
            @Valid @RequestBody ActualizarUsuarioDTO dto) {
        
        return usuarioRepository.findById(id)
                .map(usuario -> {
                    // Verificar si el nuevo username ya existe (y no es el mismo usuario)
                    usuarioRepository.findByUsername(dto.getUsername())
                            .ifPresent(u -> {
                                if (!u.getId().equals(id)) {
                                    throw new com.nexoohub.almacen.common.exception.DuplicateResourceException(
                                        "Usuario", 
                                        "username", 
                                        dto.getUsername()
                                    );
                                }
                            });
                    
                    usuario.setUsername(dto.getUsername());
                    if (dto.getRole() != null) {
                        usuario.setRole(dto.getRole());
                    }
                    if (dto.getEmpleadoId() != null) {
                        usuario.setEmpleadoId(dto.getEmpleadoId());
                    }
                    
                    Usuario actualizado = usuarioRepository.save(usuario);
                    
                    Map<String, Object> respuesta = new HashMap<>();
                    respuesta.put("exitoso", true);
                    respuesta.put("mensaje", "Usuario actualizado exitosamente");
                    respuesta.put("usuario", convertirADTO(actualizado));
                    
                    return ResponseEntity.ok(respuesta);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Cambiar contraseña de un usuario
     * PUT /api/v1/usuarios/{id}/password
     */
    @PutMapping("/{id}/password")
    public ResponseEntity<?> cambiarPassword(
            @PathVariable Long id,
            @Valid @RequestBody CambiarPasswordDTO dto) {
        
        return usuarioRepository.findById(id)
                .map(usuario -> {
                    // Verificar la contraseña actual
                    if (!passwordEncoder.matches(dto.getOldPassword(), usuario.getPassword())) {
                        Map<String, Object> error = new HashMap<>();
                        error.put("exitoso", false);
                        error.put("mensaje", "La contraseña actual es incorrecta");
                        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
                    }
                    
                    // Encriptar y guardar la nueva contraseña
                    usuario.setPassword(passwordEncoder.encode(dto.getNewPassword()));
                    usuarioRepository.save(usuario);
                    
                    Map<String, Object> respuesta = new HashMap<>();
                    respuesta.put("exitoso", true);
                    respuesta.put("mensaje", "Contraseña actualizada exitosamente");
                    
                    return ResponseEntity.ok(respuesta);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Eliminar un usuario
     * DELETE /api/v1/usuarios/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarUsuario(@PathVariable Long id) {
        return usuarioRepository.findById(id)
                .map(usuario -> {
                    usuarioRepository.delete(usuario);
                    
                    Map<String, Object> respuesta = new HashMap<>();
                    respuesta.put("exitoso", true);
                    respuesta.put("mensaje", "Usuario eliminado exitosamente");
                    
                    return ResponseEntity.ok(respuesta);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Método helper para convertir Usuario a UsuarioDTO (sin password)
     */
    private UsuarioDTO convertirADTO(Usuario usuario) {
        return new UsuarioDTO(
                usuario.getId(),
                usuario.getUsername(),
                usuario.getRole(),
                usuario.getEmpleadoId(),
                usuario.getUsuarioCreacion(),
                usuario.getUsuarioActualizacion()
        );
    }
}