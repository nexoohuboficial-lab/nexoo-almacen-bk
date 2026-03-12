package com.nexoohub.almacen.common.security;

/**
 * Enumeración de roles de usuario en el sistema.
 * 
 * <p>Jerarquía de permisos (de mayor a menor):</p>
 * <ul>
 *   <li><strong>ADMIN:</strong> Acceso total al sistema, gestión de usuarios y configuración global</li>
 *   <li><strong>GERENTE:</strong> Acceso a reportes ejecutivos, métricas financieras, configuración de sucursales</li>
 *   <li><strong>SUPERVISOR:</strong> Gestión de inventario, compras, traspasos y análisis operativos</li>
 *   <li><strong>VENDEDOR:</strong> Registro de ventas, consulta de productos, gestión de clientes</li>
 *   <li><strong>ALMACENISTA:</strong> Gestión de inventario, recepción de mercancía, alertas de stock</li>
 *   <li><strong>CAJERO:</strong> Registro de ventas y consulta básica de productos</li>
 *   <li><strong>AUDITOR:</strong> Solo lectura de reportes, métricas y auditorías (no puede modificar)</li>
 * </ul>
 * 
 * @author NexooHub Development Team
 * @since 1.0
 */
public enum Role {
    /**
     * Administrador del sistema.
     * Tiene acceso completo a todas las funcionalidades.
     */
    ADMIN,
    
    /**
     * Gerente de negocio.
     * Acceso a reportes ejecutivos, métricas financieras y configuración de sucursales.
     */
    GERENTE,
    
    /**
     * Supervisor de operaciones.
     * Gestión de inventario, compras, traspasos y análisis operativos.
     */
    SUPERVISOR,
    
    /**
     * Vendedor.
     * Registro de ventas, consulta de productos, gestión de clientes.
     */
    VENDEDOR,
    
    /**
     * Almacenista.
     * Gestión de inventario, recepción de mercancía, alertas de stock.
     */
    ALMACENISTA,
    
    /**
     * Cajero.
     * Registro de ventas y consulta básica de productos.
     */
    CAJERO,
    
    /**
     * Auditor.
     * Solo lectura de reportes, métricas y auditorías. No puede realizar modificaciones.
     */
    AUDITOR;
    
    /**
     * Obtiene el nombre del rol con prefijo "ROLE_" para Spring Security.
     * 
     * @return String con formato "ROLE_NOMBRE" (ej: "ROLE_ADMIN")
     */
    public String getRoleWithPrefix() {
        return "ROLE_" + this.name();
    }
}
