package com.nexoohub.almacen.common.controller;

import com.nexoohub.almacen.common.ApiResponse;
import com.nexoohub.almacen.common.config.JwtUtil;
import com.nexoohub.almacen.common.dto.AuthResponse;
import com.nexoohub.almacen.common.dto.LoginRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private static final Logger log = LoggerFactory.getLogger(AuthController.class);
    
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public AuthController(AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        log.info("Intento de login para el usuario: {}", request.username());

        // 1. Spring Security verifica la contraseña contra el Hash de la DB
        // Si falla, lanza una excepción automáticamente y no pasa a la siguiente línea
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );

        // 2. Si es correcto, fabricamos la llave JWT
        String token = jwtUtil.generateToken(request.username());
        
        log.info("Login exitoso. Token generado para: {}", request.username());

        return ResponseEntity.ok(
            new ApiResponse<>("Autenticación exitosa", new AuthResponse(token))
        );
    }
}
