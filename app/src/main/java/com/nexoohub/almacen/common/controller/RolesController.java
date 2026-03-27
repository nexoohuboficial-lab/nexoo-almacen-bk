package com.nexoohub.almacen.common.controller;

import com.nexoohub.almacen.common.ApiResponse;
import com.nexoohub.almacen.common.entity.Permiso;
import com.nexoohub.almacen.common.entity.Rol;
import com.nexoohub.almacen.common.entity.Usuario;
import com.nexoohub.almacen.common.repository.PermisoRepository;
import com.nexoohub.almacen.common.repository.RolRepository;
import com.nexoohub.almacen.common.repository.UsuarioRepository;
import com.nexoohub.almacen.common.exception.ResourceNotFoundException;
import com.nexoohub.almacen.sucursal.entity.Sucursal;
import com.nexoohub.almacen.sucursal.repository.SucursalRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class RolesController {

    private final RolRepository rolRepository;
    private final PermisoRepository permisoRepository;
    private final UsuarioRepository usuarioRepository;
    private final SucursalRepository sucursalRepository;

    @Data
    public static class CrearRolRequest {
        private String nombre;
        private String descripcion;
        private List<String> permisos;
    }

    @PostMapping("/roles")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Transactional
    public ResponseEntity<ApiResponse<Rol>> crearRol(@RequestBody CrearRolRequest request) {
        Rol rol = new Rol();
        rol.setNombre(request.getNombre().startsWith("ROLE_") ? request.getNombre() : "ROLE_" + request.getNombre());
        rol.setDescripcion(request.getDescripcion());
        
        if (request.getPermisos() != null && !request.getPermisos().isEmpty()) {
            Set<Permiso> permisosExistentes = permisoRepository.findByNombreIn(request.getPermisos());
            rol.setPermisos(permisosExistentes);
        }

        Rol guardado = rolRepository.save(rol);
        return new ResponseEntity<>(new ApiResponse<>("Rol creado exitosamente", guardado), HttpStatus.CREATED);
    }

    @Data
    public static class AsignarAccesosUsuarioRequest {
        private List<Integer> rolesIds;
        private List<Integer> sucursalesIds;
    }

    @PostMapping("/usuarios/{id}/roles")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Transactional
    public ResponseEntity<ApiResponse<String>> asignarRolesYSucursales(@PathVariable Long id, @RequestBody AsignarAccesosUsuarioRequest request) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        if (request.getRolesIds() != null) {
            List<Rol> rolesList = rolRepository.findAllById(request.getRolesIds());
            usuario.setRoles(new java.util.HashSet<>(rolesList));
        }

        if (request.getSucursalesIds() != null) {
            List<Sucursal> sucursalesList = sucursalRepository.findAllById(request.getSucursalesIds());
            usuario.setSucursalesPermitidas(new java.util.HashSet<>(sucursalesList));
        }

        usuarioRepository.save(usuario);
        return ResponseEntity.ok(new ApiResponse<>("Accesos actualizados correctamente", null));
    }

    @GetMapping("/usuarios/{id}/permisos")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_GERENTE_SUCURSAL')")
    public ResponseEntity<ApiResponse<Set<String>>> obtenerPermisos(@PathVariable Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        Set<String> permisosEfectivos = usuario.getRoles().stream()
                .filter(Rol::getActivo)
                .flatMap(r -> r.getPermisos().stream())
                .map(Permiso::getNombre)
                .collect(Collectors.toSet());

        return ResponseEntity.ok(new ApiResponse<>("Permisos efectivos", permisosEfectivos));
    }
}
