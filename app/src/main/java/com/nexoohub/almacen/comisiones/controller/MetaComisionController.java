package com.nexoohub.almacen.comisiones.controller;

import com.nexoohub.almacen.comisiones.dto.AsignarMetaRequest;
import com.nexoohub.almacen.comisiones.dto.ProgresoMetaResponse;
import com.nexoohub.almacen.comisiones.entity.MetaVentasEmpleado;
import com.nexoohub.almacen.comisiones.service.MetaComisionService;
import com.nexoohub.almacen.common.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/rh/metas")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class MetaComisionController {

    private final MetaComisionService comisionService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<ApiResponse<Object>> asignarMeta(@Valid @RequestBody AsignarMetaRequest request) {
        
        MetaVentasEmpleado meta = comisionService.asignarMeta(request, "API_USER");
        
        return new ResponseEntity<>(
                new ApiResponse<>("Meta de ventas asignada exitosamente al empleado", meta.getId()),
                HttpStatus.CREATED
        );
    }

    @GetMapping("/{empleadoId}/progreso")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'VENDEDOR')")
    public ResponseEntity<ApiResponse<ProgresoMetaResponse>> obtenerProgreso(
            @PathVariable Integer empleadoId,
            @RequestParam(required = false) Integer mes,
            @RequestParam(required = false) Integer anio) {
            
        // Si no se manda el mes/año, tomamos el actual
        if (mes == null) mes = LocalDate.now().getMonthValue();
        if (anio == null) anio = LocalDate.now().getYear();

        ProgresoMetaResponse response = comisionService.calcularProgreso(empleadoId, mes, anio);
        
        return ResponseEntity.ok(
                new ApiResponse<>("Progreso de ventas y comisión proyectada calculado con éxito", response)
        );
    }
}
