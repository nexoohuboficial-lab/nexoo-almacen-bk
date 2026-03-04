package com.nexoohub.almacen.common.controller;

import com.nexoohub.almacen.common.entity.Usuario;
import com.nexoohub.almacen.common.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder; // El encriptador que ya tienes en SecurityConfig

    @PostMapping
    public ResponseEntity<Map<String, Object>> crearUsuarioCredenciales(@RequestBody Usuario usuario) {
        
        // 1. Encriptamos la contraseña "12345" a algo como "$2a$10$wYQ..."
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        
        // 2. Guardamos en BD
        Usuario guardado = usuarioRepository.save(usuario);
        
        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("exitoso", true);
        respuesta.put("mensaje", "Usuario " + guardado.getUsername() + " creado y vinculado al Empleado ID: " + guardado.getEmpleadoId());
        
        return new ResponseEntity<>(respuesta, HttpStatus.CREATED);
    }
}