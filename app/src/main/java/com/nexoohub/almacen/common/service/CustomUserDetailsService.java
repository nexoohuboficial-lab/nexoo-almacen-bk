package com.nexoohub.almacen.common.service;

import com.nexoohub.almacen.common.entity.Usuario;
import com.nexoohub.almacen.common.repository.UsuarioRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository repository;

    public CustomUserDetailsService(UsuarioRepository repository) {
        this.repository = repository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Buscamos en tu tabla
        Usuario usuario = repository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        java.util.Set<org.springframework.security.core.GrantedAuthority> authorities = new java.util.HashSet<>();
        
        // Por transición, mantenemos el legacy original en caso de que alguna firma lo ocupe
        if (usuario.getRole() != null && !usuario.getRole().isEmpty()) {
            authorities.add(new org.springframework.security.core.authority.SimpleGrantedAuthority(
                usuario.getRole().startsWith("ROLE_") ? usuario.getRole() : "ROLE_" + usuario.getRole()
            ));
        }

        // Roles RBAC granulares
        for (com.nexoohub.almacen.common.entity.Rol r : usuario.getRoles()) {
            if (r.getActivo()) {
                // Agregar el propio Rol (Ej. ROLE_GERENTE_SUCURSAL)
                authorities.add(new org.springframework.security.core.authority.SimpleGrantedAuthority(r.getNombre()));
                // Desplegar cada permiso atómico a modo de Authority (Ej. CREAR_VENTA)
                for (com.nexoohub.almacen.common.entity.Permiso p : r.getPermisos()) {
                    authorities.add(new org.springframework.security.core.authority.SimpleGrantedAuthority(p.getNombre()));
                }
            }
        }

        return new User(
                usuario.getUsername(),
                usuario.getPassword(),
                authorities
        );
    }
}
