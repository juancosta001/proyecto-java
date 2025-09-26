package com.ypacarai.cooperativa.activos.util;

import com.ypacarai.cooperativa.activos.model.Usuario;
import java.util.*;

/**
 * Gestor de Control de Acceso Basado en Roles (RBAC)
 * Sistema de Gestión de Activos - Cooperativa Ypacaraí LTDA
 */
public class ControlAccesoRoles {
    
    // Enum de permisos del sistema
    public enum Permiso {
        // Dashboard
        VER_DASHBOARD,
        
        // Activos
        VER_ACTIVOS,
        CREAR_ACTIVOS,
        EDITAR_ACTIVOS,
        ELIMINAR_ACTIVOS,
        
        // Tickets
        VER_TICKETS,
        CREAR_TICKETS,
        EDITAR_TICKETS,
        ELIMINAR_TICKETS,
        ASIGNAR_TICKETS,
        
        // Mantenimiento
        VER_MANTENIMIENTO,
        CREAR_MANTENIMIENTO,
        EDITAR_MANTENIMIENTO,
        ELIMINAR_MANTENIMIENTO,
        
        // Reportes
        VER_REPORTES_BASICOS,
        VER_REPORTES_AVANZADOS,
        EXPORTAR_REPORTES,
        
        // Usuarios
        VER_USUARIOS,
        CREAR_USUARIOS,
        EDITAR_USUARIOS,
        ELIMINAR_USUARIOS,
        GESTIONAR_PERMISOS,
        
        // Configuración
        VER_CONFIGURACION,
        EDITAR_CONFIGURACION_BASICA,
        EDITAR_CONFIGURACION_AVANZADA,
        
        // Sistema
        VER_AUDITORIA,
        ADMINISTRAR_SISTEMA
    }
    
    // Mapa de permisos por rol
    private static final Map<Usuario.Rol, Set<Permiso>> PERMISOS_POR_ROL = new HashMap<>();
    
    static {
        // Configurar permisos para JEFE DE INFORMÁTICA (Acceso total)
        Set<Permiso> permisosJefe = new HashSet<>();
        permisosJefe.addAll(Arrays.asList(Permiso.values())); // Todos los permisos
        PERMISOS_POR_ROL.put(Usuario.Rol.Jefe_Informatica, permisosJefe);
        
        // Configurar permisos para TÉCNICO (Operaciones normales)
        Set<Permiso> permisosTecnico = new HashSet<>();
        permisosTecnico.addAll(Arrays.asList(
            // Dashboard
            Permiso.VER_DASHBOARD,
            // Activos - puede gestionar activos
            Permiso.VER_ACTIVOS,
            Permiso.CREAR_ACTIVOS,
            Permiso.EDITAR_ACTIVOS,
            // Tickets - puede trabajar con tickets
            Permiso.VER_TICKETS,
            Permiso.CREAR_TICKETS,
            Permiso.EDITAR_TICKETS,
            // Mantenimiento - puede realizar mantenimientos
            Permiso.VER_MANTENIMIENTO,
            Permiso.CREAR_MANTENIMIENTO,
            Permiso.EDITAR_MANTENIMIENTO,
            // Reportes básicos solamente
            Permiso.VER_REPORTES_BASICOS
        ));
        PERMISOS_POR_ROL.put(Usuario.Rol.Tecnico, permisosTecnico);
        
        // Configurar permisos para CONSULTA (Solo lectura)
        Set<Permiso> permisosConsulta = new HashSet<>();
        permisosConsulta.addAll(Arrays.asList(
            // Dashboard
            Permiso.VER_DASHBOARD,
            // Solo ver activos
            Permiso.VER_ACTIVOS,
            // Solo ver tickets
            Permiso.VER_TICKETS,
            // Solo ver mantenimientos
            Permiso.VER_MANTENIMIENTO,
            // Solo reportes básicos
            Permiso.VER_REPORTES_BASICOS
        ));
        PERMISOS_POR_ROL.put(Usuario.Rol.Consulta, permisosConsulta);
    }
    
    /**
     * Verifica si un usuario tiene un permiso específico
     */
    public static boolean tienePermiso(Usuario usuario, Permiso permiso) {
        if (usuario == null || usuario.getUsuRol() == null) {
            return false;
        }
        
        Set<Permiso> permisos = PERMISOS_POR_ROL.get(usuario.getUsuRol());
        return permisos != null && permisos.contains(permiso);
    }
    
    /**
     * Obtiene todos los permisos de un rol
     */
    public static Set<Permiso> obtenerPermisos(Usuario.Rol rol) {
        return new HashSet<>(PERMISOS_POR_ROL.getOrDefault(rol, new HashSet<>()));
    }
    
    /**
     * Verifica si un usuario puede acceder a un módulo específico
     */
    public static boolean puedeAccederModulo(Usuario usuario, String modulo) {
        switch (modulo.toLowerCase()) {
            case "dashboard":
                return tienePermiso(usuario, Permiso.VER_DASHBOARD);
                
            case "activos":
                return tienePermiso(usuario, Permiso.VER_ACTIVOS);
                
            case "tickets":
                return tienePermiso(usuario, Permiso.VER_TICKETS);
                
            case "mantenimiento":
                return tienePermiso(usuario, Permiso.VER_MANTENIMIENTO);
                
            case "reportes":
                return tienePermiso(usuario, Permiso.VER_REPORTES_BASICOS);
                
            case "usuarios":
                return tienePermiso(usuario, Permiso.VER_USUARIOS);
                
            case "configuracion":
                return tienePermiso(usuario, Permiso.VER_CONFIGURACION);
                
            default:
                return false;
        }
    }
    
    /**
     * Obtiene una descripción de los permisos de un rol
     */
    public static String obtenerDescripcionRol(Usuario.Rol rol) {
        switch (rol) {
            case Jefe_Informatica:
                return "👑 Control total del sistema • Gestión completa • Todos los permisos";
                
            case Tecnico:
                return "🔧 Gestión de activos y tickets • Mantenimientos • Reportes básicos";
                
            case Consulta:
                return "👁️ Solo lectura • Visualización de información • Sin modificaciones";
                
            default:
                return "Sin permisos definidos";
        }
    }
    
    /**
     * Verifica múltiples permisos a la vez
     */
    public static boolean tieneAlgunPermiso(Usuario usuario, Permiso... permisos) {
        for (Permiso permiso : permisos) {
            if (tienePermiso(usuario, permiso)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Verifica que tenga todos los permisos
     */
    public static boolean tieneTodosLosPermisos(Usuario usuario, Permiso... permisos) {
        for (Permiso permiso : permisos) {
            if (!tienePermiso(usuario, permiso)) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Obtiene los módulos a los que un usuario puede acceder
     */
    public static List<String> obtenerModulosPermitidos(Usuario usuario) {
        List<String> modulos = new ArrayList<>();
        
        if (puedeAccederModulo(usuario, "dashboard")) modulos.add("dashboard");
        if (puedeAccederModulo(usuario, "activos")) modulos.add("activos");
        if (puedeAccederModulo(usuario, "tickets")) modulos.add("tickets");
        if (puedeAccederModulo(usuario, "mantenimiento")) modulos.add("mantenimiento");
        if (puedeAccederModulo(usuario, "reportes")) modulos.add("reportes");
        if (puedeAccederModulo(usuario, "usuarios")) modulos.add("usuarios");
        if (puedeAccederModulo(usuario, "configuracion")) modulos.add("configuracion");
        
        return modulos;
    }
    
    /**
     * Crea un mensaje de error personalizado para acceso denegado
     */
    public static String mensajeAccesoDenegado(Usuario usuario, String accion) {
        return String.format(
            "❌ Acceso Denegado\n\n" +
            "Usuario: %s (%s)\n" +
            "Acción: %s\n\n" +
            "Su rol '%s' no tiene permisos para realizar esta acción.\n" +
            "Contacte al administrador del sistema si necesita acceso adicional.",
            usuario.getUsuNombre(),
            usuario.getUsuRol(),
            accion,
            usuario.getUsuRol()
        );
    }
}