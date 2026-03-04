package com.nexoohub.almacen.sucursal.entity;

import com.nexoohub.almacen.common.entity.AuditableEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "sucursal")
public class Sucursal extends AuditableEntity {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Integer id;
    
        @NotBlank(message = "El nombre de la sucursal es obligatorio")
        @Size(max = 100, message = "El nombre no puede exceder los 100 caracteres")
        private String nombre;
    
        private String direccion;
    
        private Boolean activo = true;
    
        // ==========================================
        // GETTERS Y SETTERS
        // ==========================================
    
        public Integer getId() { return id; }
        public void setId(Integer id) { this.id = id; }
    
        public String getNombre() { return nombre; }
        public void setNombre(String nombre) { this.nombre = nombre; }
    
        public String getDireccion() { return direccion; }
        public void setDireccion(String direccion) { this.direccion = direccion; }
    
        public Boolean getActivo() { return activo; }
        public void setActivo(Boolean activo) { this.activo = activo; }
}
