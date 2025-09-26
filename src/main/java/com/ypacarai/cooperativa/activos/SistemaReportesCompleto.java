package com.ypacarai.cooperativa.activos;

import java.time.LocalDate;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import com.ypacarai.cooperativa.activos.model.DashboardData;
import com.ypacarai.cooperativa.activos.model.FiltrosReporte;
import com.ypacarai.cooperativa.activos.model.ReporteCompleto;
import com.ypacarai.cooperativa.activos.model.Usuario;
import com.ypacarai.cooperativa.activos.service.ReporteService;
import com.ypacarai.cooperativa.activos.view.MainWindowNew;

/**
 * Clase principal para demostrar el módulo completo de reportes
 * Sistema de Activos - Cooperativa Ypacaraí
 */
public class SistemaReportesCompleto {
    private static final Logger LOGGER = Logger.getLogger(SistemaReportesCompleto.class.getName());
    
    public static void main(String[] args) {
        // Configurar look and feel
        try {
            javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "No se pudo establecer el Look and Feel del sistema", e);
        }
        
        SwingUtilities.invokeLater(() -> {
            try {
                // Mostrar información del módulo
                mostrarInformacionModulo();
                
                // Validar servicios
                validarServicios();
                
                // Crear y mostrar ventana principal
                crearVentanaPrincipal();
                
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error iniciando el sistema de reportes", e);
                System.err.println("Error crítico: " + e.getMessage());
                System.exit(1);
            }
        });
    }
    
    private static void mostrarInformacionModulo() {
        System.out.println("=".repeat(80));
        System.out.println("    SISTEMA DE GESTIÓN DE ACTIVOS - COOPERATIVA YPACARAÍ");
        System.out.println("    MÓDULO DE REPORTES Y CONSULTAS - VERSIÓN COMPLETA");
        System.out.println("=".repeat(80));
        System.out.println();
        System.out.println("Funcionalidades implementadas:");
        System.out.println("✅ Reportes Operativos:");
        System.out.println("   • Reporte de Estado de Activos");
        System.out.println("   • Reporte de Mantenimientos");  
        System.out.println("   • Reporte de Fallas");
        System.out.println("   • Reporte de Traslados");
        System.out.println();
        System.out.println("✅ Dashboard Ejecutivo:");
        System.out.println("   • KPIs principales");
        System.out.println("   • Estadísticas de productividad");
        System.out.println("   • Rankings y tendencias");
        System.out.println("   • Alertas críticas");
        System.out.println();
        System.out.println("✅ Consultas Dinámicas:");
        System.out.println("   • Constructor de consultas SQL");
        System.out.println("   • Consultas guardadas");
        System.out.println("   • Filtros múltiples");
        System.out.println("   • Validación de seguridad");
        System.out.println();
        System.out.println("✅ Exportación:");
        System.out.println("   • CSV (completamente funcional)");
        System.out.println("   • Texto plano");
        System.out.println("   • Excel/PDF (información para implementación)");
        System.out.println();
        System.out.println("Iniciando aplicación...");
        System.out.println("-".repeat(80));
    }
    
    private static void validarServicios() {
        System.out.println("Validando servicios del sistema...");
        
        try {
            // Validar servicio de reportes
            ReporteService reporteService = new ReporteService();
            System.out.println("✅ ReporteService inicializado correctamente");
            
            // Validar obtención de opciones de filtros
            var opciones = reporteService.obtenerOpcionesFiltros();
            System.out.println("✅ Opciones de filtros cargadas: " + opciones.size() + " categorías");
            
            // Validar estadísticas resumidas
            LocalDate fechaInicio = LocalDate.now().minusDays(30);
            LocalDate fechaFin = LocalDate.now();
            var estadisticas = reporteService.obtenerEstadisticasResumen(fechaInicio, fechaFin);
            System.out.println("✅ Estadísticas resumidas obtenidas: " + estadisticas.size() + " métricas");
            
            // Validar dashboard
            DashboardData dashboard = reporteService.obtenerDatosDashboard();
            System.out.println("✅ Dashboard cargado - Activos: " + dashboard.getTotalActivos());
            
            System.out.println("✅ Todos los servicios validados exitosamente");
            
        } catch (Exception e) {
            System.err.println("❌ Error validando servicios: " + e.getMessage());
            LOGGER.log(Level.SEVERE, "Error en validación de servicios", e);
        }
        
        System.out.println("-".repeat(80));
    }
    
    private static void crearVentanaPrincipal() {
        System.out.println("Creando ventana principal del sistema...");
        
        // Crear usuario de prueba para inicializar la ventana
        Usuario usuarioPrueba = new Usuario();
        usuarioPrueba.setUsuNombre("Usuario Demo");
        usuarioPrueba.setUsuRol(Usuario.Rol.Jefe_Informatica);
        
        // Crear ventana principal (ya incluye el módulo de reportes)
        MainWindowNew mainWindow = new MainWindowNew(usuarioPrueba);
        
        // Configurar ventana
        mainWindow.setTitle("Sistema de Activos - Cooperativa Ypacaraí - Módulo de Reportes");
        mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainWindow.setExtendedState(JFrame.MAXIMIZED_BOTH);
        mainWindow.setLocationRelativeTo(null);
        
        // Mostrar ventana
        mainWindow.setVisible(true);
        
        System.out.println("✅ Sistema iniciado exitosamente");
        System.out.println();
        System.out.println("INSTRUCCIONES DE USO:");
        System.out.println("1. Haga clic en 'Reportes' en el menú principal");
        System.out.println("2. Seleccione el tipo de reporte deseado");
        System.out.println("3. Configure los filtros según sus necesidades");
        System.out.println("4. Genere el reporte y explore las opciones de exportación");
        System.out.println("5. Acceda al Dashboard para ver KPIs en tiempo real");
        System.out.println("6. Use Consultas Dinámicas para análisis personalizados");
        System.out.println();
        System.out.println("El sistema está listo para usar. ¡Bienvenido!");
        System.out.println("=".repeat(80));
    }
    
    /**
     * Método para ejecutar pruebas específicas del módulo de reportes
     */
    public static void ejecutarPruebasReportes() {
        System.out.println("Ejecutando pruebas del módulo de reportes...");
        
        try {
            ReporteService reporteService = new ReporteService();
            
            // Prueba 1: Reporte de estado de activos
            System.out.println("Prueba 1: Reporte de Estado de Activos");
            FiltrosReporte filtros = new FiltrosReporte();
            filtros.setFechaInicio(LocalDate.now().minusDays(30));
            filtros.setFechaFin(LocalDate.now());
            
            ReporteCompleto reporteEstado = reporteService.generarReporteEstadoActivos(filtros);
            System.out.println("✅ Reporte generado con " + reporteEstado.getTotalRegistros() + " registros");
            
            // Prueba 2: Reporte de mantenimientos
            System.out.println("Prueba 2: Reporte de Mantenimientos");
            ReporteCompleto reporteMantenimientos = reporteService.generarReporteMantenimientos(filtros);
            System.out.println("✅ Reporte generado con " + reporteMantenimientos.getTotalRegistros() + " registros");
            
            // Prueba 3: Dashboard
            System.out.println("Prueba 3: Dashboard Ejecutivo");
            DashboardData dashboard = reporteService.obtenerDatosDashboard();
            System.out.println("✅ Dashboard cargado - Total de activos: " + dashboard.getTotalActivos());
            
            System.out.println("✅ Todas las pruebas completadas exitosamente");
            
        } catch (Exception e) {
            System.err.println("❌ Error en pruebas: " + e.getMessage());
            LOGGER.log(Level.SEVERE, "Error en pruebas del módulo", e);
        }
    }
}
