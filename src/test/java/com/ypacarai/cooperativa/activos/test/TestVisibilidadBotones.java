package com.ypacarai.cooperativa.activos.test;

import com.ypacarai.cooperativa.activos.model.Usuario;
import com.ypacarai.cooperativa.activos.util.ControlAccesoRoles;

/**
 * Test para verificar visibilidad de botones por rol
 */
public class TestVisibilidadBotones {
    
    public static void main(String[] args) {
        System.out.println("=== Test Visibilidad de Botones por Rol ===");
        System.out.println();
        
        // Simular diferentes roles
        Usuario tecnico = new Usuario();
        tecnico.setUsuId(1);
        tecnico.setUsuNombre("jose");
        tecnico.setUsuRol(Usuario.Rol.Tecnico);
        
        Usuario jefe = new Usuario();
        jefe.setUsuId(2);
        jefe.setUsuNombre("admin");
        jefe.setUsuRol(Usuario.Rol.Jefe_Informatica);
        
        Usuario consulta = new Usuario();
        consulta.setUsuId(3);
        consulta.setUsuNombre("consultor");
        consulta.setUsuRol(Usuario.Rol.Consulta);
        
        // Módulos a probar
        String[] modulos = {"dashboard", "activos", "mantenimiento", "reportes", "configuracion"};
        
        System.out.println("🔍 PERMISOS POR ROL:");
        System.out.println();
        
        System.out.println("TÉCNICO (jose):");
        for (String modulo : modulos) {
            boolean acceso = ControlAccesoRoles.puedeAccederModulo(tecnico, modulo);
            String icono = acceso ? "✅" : "❌";
            System.out.println("  " + icono + " " + modulo + " = " + (acceso ? "VISIBLE" : "OCULTO"));
        }
        System.out.println();
        
        System.out.println("JEFE_INFORMATICA (admin):");
        for (String modulo : modulos) {
            boolean acceso = ControlAccesoRoles.puedeAccederModulo(jefe, modulo);
            String icono = acceso ? "✅" : "❌";
            System.out.println("  " + icono + " " + modulo + " = " + (acceso ? "VISIBLE" : "OCULTO"));
        }
        System.out.println();
        
        System.out.println("CONSULTA (consultor):");
        for (String modulo : modulos) {
            boolean acceso = ControlAccesoRoles.puedeAccederModulo(consulta, modulo);
            String icono = acceso ? "✅" : "❌";
            System.out.println("  " + icono + " " + modulo + " = " + (acceso ? "VISIBLE" : "OCULTO"));
        }
        System.out.println();
        
        System.out.println("✅ CAMBIO IMPLEMENTADO EN MainWindowNew.java:");
        System.out.println("   - ANTES: Botones deshabilitados para módulos sin acceso");
        System.out.println("   - AHORA: Botones completamente ocultos si no hay permisos");
        System.out.println("   - RESULTADO: Interface más limpia y menos confusa");
        System.out.println();
        
        System.out.println("🎯 Para probar visualmente:");
        System.out.println("   1. Ejecutar aplicación principal");
        System.out.println("   2. Login con diferentes roles");
        System.out.println("   3. Verificar que el menú muestra solo botones permitidos");
        
        System.out.println("\n=== Test completado ===");
    }
}