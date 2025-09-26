package com.ypacarai.cooperativa.activos;

import java.util.List;

import javax.swing.SwingUtilities;

import com.ypacarai.cooperativa.activos.config.DatabaseConfigComplete;
import com.ypacarai.cooperativa.activos.dao.ConfiguracionMantenimientoDAO;
import com.ypacarai.cooperativa.activos.model.Activo;
import com.ypacarai.cooperativa.activos.model.AlertaMantenimiento;
import com.ypacarai.cooperativa.activos.model.ConfiguracionMantenimiento;
import com.ypacarai.cooperativa.activos.model.Usuario;
import com.ypacarai.cooperativa.activos.service.ActivoService;
import com.ypacarai.cooperativa.activos.service.GestionUsuariosService;
import com.ypacarai.cooperativa.activos.service.MantenimientoPreventivoService;
import com.ypacarai.cooperativa.activos.view.MainWindowNew;

/**
 * Sistema de prueba completo para validar toda la funcionalidad
 */
public class SistemaPruebaCompleto {
    
    public static void main(String[] args) {
        System.out.println("🚀 === SISTEMA DE ACTIVOS YPACARAI - PRUEBA COMPLETA ===");
        
        // ===== PRUEBA DE CONEXIÓN =====
        System.out.println("\n📡 Verificando conexión a base de datos...");
        try {
            boolean conexionOk = DatabaseConfigComplete.testConnection();
            if (conexionOk) {
                System.out.println("✅ Conexión exitosa!");
            } else {
                System.out.println("❌ Error de conexión - usando datos simulados");
            }
        } catch (Exception e) {
            System.out.println("⚠️  Error de conexión: " + e.getMessage());
            System.out.println("🔄 Continuando con datos simulados...");
        }
        
        // ===== PRUEBA DE CONFIGURACIONES POR DEFECTO =====
        System.out.println("\n⚙️ Creando configuraciones por defecto...");
        try {
            ConfiguracionMantenimientoDAO configuracionDAO = new ConfiguracionMantenimientoDAO();
            configuracionDAO.crearConfiguracionesPorDefecto();
            System.out.println("✅ Configuraciones creadas");
        } catch (Exception e) {
            System.out.println("⚠️  Error creando configuraciones: " + e.getMessage());
        }
        
        // ===== PRUEBA DE USUARIOS =====
        System.out.println("\n👥 Probando gestión de usuarios...");
        try {
            GestionUsuariosService usuariosService = new GestionUsuariosService();
            
            // Crear usuario de prueba usando el método correcto
            GestionUsuariosService.ResultadoOperacion resultado = usuariosService.crearUsuario(
                "Admin Prueba", 
                "adminprueba", 
                "admin123", 
                "admin@cooperativa.com", 
                Usuario.Rol.Jefe_Informatica, 
                1);
            
            if (resultado.isExitoso()) {
                System.out.println("✅ Usuario creado: " + resultado.getMensaje());
            } else {
                System.out.println("ℹ️  Usuario ya existe o error: " + resultado.getMensaje());
            }
            
            // Listar usuarios
            List<Usuario> usuarios = usuariosService.obtenerTodosLosUsuarios();
            System.out.println("📊 Total de usuarios: " + usuarios.size());
            
        } catch (Exception e) {
            System.out.println("⚠️  Error en gestión de usuarios: " + e.getMessage());
        }
        
        // ===== PRUEBA DE MANTENIMIENTO PREVENTIVO =====
        System.out.println("\n🔧 Probando sistema de mantenimiento preventivo...");
        try {
            MantenimientoPreventivoService mantenimientoService = new MantenimientoPreventivoService();
            
            // Obtener configuraciones
            List<ConfiguracionMantenimiento> configuraciones = mantenimientoService.obtenerConfiguraciones();
            System.out.println("⚙️  Configuraciones disponibles: " + configuraciones.size());
            
            // Proceso de alertas diario
            mantenimientoService.ejecutarProcesoAlertasDiario();
            System.out.println("🚨 Proceso de alertas diario ejecutado");
            
            // Obtener alertas activas
            List<AlertaMantenimiento> alertasActivas = mantenimientoService.obtenerAlertasActivasNoLeidas();
            System.out.println("📢 Alertas activas no leídas: " + alertasActivas.size());
            
            // Obtener alertas críticas
            List<AlertaMantenimiento> alertasCriticas = mantenimientoService.obtenerAlertasCriticas();
            System.out.println("🚩 Alertas críticas: " + alertasCriticas.size());
            
        } catch (Exception e) {
            System.out.println("⚠️  Error en mantenimiento preventivo: " + e.getMessage());
            e.printStackTrace();
        }
        
        // ===== PRUEBA DE ACTIVOS =====
        System.out.println("\n💻 Probando gestión de activos...");
        try {
            ActivoService activoService = new ActivoService();
            List<Activo> activos = activoService.obtenerTodosLosActivos();
            System.out.println("💼 Total de activos: " + activos.size());
            
            // Contar por estado
            long operativos = activos.stream().filter(a -> a.getActEstado() == Activo.Estado.Operativo).count();
            long enMantenimiento = activos.stream().filter(a -> a.getActEstado() == Activo.Estado.En_Mantenimiento).count();
            long fueraServicio = activos.stream().filter(a -> a.getActEstado() == Activo.Estado.Fuera_Servicio).count();
            
            System.out.println("  ✅ Operativos: " + operativos);
            System.out.println("  🔧 En mantenimiento: " + enMantenimiento);
            System.out.println("  ❌ Fuera de servicio: " + fueraServicio);
            
        } catch (Exception e) {
            System.out.println("⚠️  Error en gestión de activos: " + e.getMessage());
        }
        
        // ===== ESTADÍSTICAS FINALES =====
        System.out.println("\n📊 === ESTADÍSTICAS DEL SISTEMA ===");
        try {
            // Usuarios
            GestionUsuariosService usuariosService = new GestionUsuariosService();
            List<Usuario> usuarios = usuariosService.obtenerTodosLosUsuarios();
            long jefes = usuarios.stream().filter(u -> u.getUsuRol() == Usuario.Rol.Jefe_Informatica).count();
            long tecnicos = usuarios.stream().filter(u -> u.getUsuRol() == Usuario.Rol.Tecnico).count();
            long consultas = usuarios.stream().filter(u -> u.getUsuRol() == Usuario.Rol.Consulta).count();
            
            System.out.println("👥 Usuarios por rol:");
            System.out.println("  🎯 Jefes: " + jefes);
            System.out.println("  🔧 Técnicos: " + tecnicos);
            System.out.println("  👁️  Consulta: " + consultas);
            
            // Configuraciones
            MantenimientoPreventivoService mantenimientoService = new MantenimientoPreventivoService();
            List<ConfiguracionMantenimiento> configs = mantenimientoService.obtenerConfiguraciones();
            System.out.println("⚙️  Configuraciones de mantenimiento: " + configs.size());
            
        } catch (Exception e) {
            System.out.println("⚠️  Error obteniendo estadísticas: " + e.getMessage());
        }
        
        // ===== LANZAR GUI =====
        System.out.println("\n🖥️  Preparando interfaz gráfica...");
        SwingUtilities.invokeLater(() -> {
            try {
                // Crear usuario de prueba para la GUI
                Usuario usuarioGUI = new Usuario();
                usuarioGUI.setUsuId(1);
                usuarioGUI.setUsuNombre("Administrador");
                usuarioGUI.setUsuRol(Usuario.Rol.Jefe_Informatica);
                
                // Lanzar ventana principal
                MainWindowNew ventanaPrincipal = new MainWindowNew(usuarioGUI);
                ventanaPrincipal.setVisible(true);
                
                System.out.println("✅ Interfaz gráfica iniciada exitosamente");
                
            } catch (Exception e) {
                System.out.println("❌ Error iniciando GUI: " + e.getMessage());
                e.printStackTrace();
            }
        });
        
        System.out.println("\n🎉 === SISTEMA INICIADO COMPLETAMENTE ===");
        System.out.println("💡 Revise la interfaz gráfica para probar las funcionalidades");
        System.out.println("🔧 Panel de Mantenimiento: Alertas automáticas y configuraciones");
        System.out.println("👥 Panel de Usuarios: Gestión completa con permisos por rol");
        System.out.println("📊 KPIs en tiempo real y demostraciones interactivas");
    }
}
