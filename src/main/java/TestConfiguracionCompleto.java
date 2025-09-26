import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.swing.SwingUtilities;

import com.ypacarai.cooperativa.activos.config.DatabaseConfigComplete;
import com.ypacarai.cooperativa.activos.model.ConfiguracionAlerta;
import com.ypacarai.cooperativa.activos.model.ConfiguracionSistema;
import com.ypacarai.cooperativa.activos.model.Usuario;
import com.ypacarai.cooperativa.activos.service.ConfiguracionService;
import com.ypacarai.cooperativa.activos.view.MainWindowNew;

/**
 * Clase de prueba para verificar el funcionamiento completo del módulo de configuración
 * Cooperativa Ypacaraí LTDA - Sistema de Gestión de Activos
 */
public class TestConfiguracionCompleto {
    
    public static void main(String[] args) {
        System.out.println("=== PRUEBA COMPLETA DEL MÓDULO DE CONFIGURACIÓN ===");
        
        // Test 1: Verificar conexión a base de datos
        System.out.println("\n1. Verificando conexión a base de datos...");
        if (testConexionBaseDatos()) {
            System.out.println("   ✓ Conexión a base de datos exitosa");
        } else {
            System.out.println("   ✗ Error en conexión a base de datos");
            return;
        }
        
        // Test 2: Verificar configuraciones del sistema
        System.out.println("\n2. Verificando configuraciones del sistema...");
        if (testConfiguracionesSistema()) {
            System.out.println("   ✓ Configuraciones del sistema cargadas correctamente");
        } else {
            System.out.println("   ✗ Error al cargar configuraciones del sistema");
        }
        
        // Test 3: Verificar configuraciones de alertas
        System.out.println("\n3. Verificando configuraciones de alertas...");
        if (testConfiguracionesAlertas()) {
            System.out.println("   ✓ Configuraciones de alertas cargadas correctamente");
        } else {
            System.out.println("   ✗ Error al cargar configuraciones de alertas");
        }
        
        // Test 4: Verificar funciones del servicio
        System.out.println("\n4. Verificando funciones del servicio de configuración...");
        if (testServicioConfiguracion()) {
            System.out.println("   ✓ Servicio de configuración funcionando correctamente");
        } else {
            System.out.println("   ✗ Error en el servicio de configuración");
        }
        
        // Test 5: Lanzar interfaz gráfica
        System.out.println("\n5. Lanzando interfaz gráfica...");
        SwingUtilities.invokeLater(() -> {
            try {
                // Crear usuario de prueba para la interfaz
                Usuario usuarioPrueba = new Usuario();
                usuarioPrueba.setUsuId(1);
                usuarioPrueba.setUsuUsuario("admin");
                usuarioPrueba.setUsuNombre("Administrador del Sistema");
                usuarioPrueba.setUsuRol(Usuario.Rol.Jefe_Informatica);
                usuarioPrueba.setActivo(true);
                
                MainWindowNew ventana = new MainWindowNew(usuarioPrueba);
                ventana.setVisible(true);
                System.out.println("   ✓ Interfaz gráfica iniciada correctamente");
                System.out.println("\n=== MÓDULO DE CONFIGURACIÓN OPERATIVO ===");
                System.out.println("Puede acceder al módulo de configuración desde el menú principal.");
            } catch (Exception e) {
                System.out.println("   ✗ Error al iniciar interfaz gráfica: " + e.getMessage());
                System.err.println("Stack trace: " + e.toString());
            }
        });
    }
    
    private static boolean testConexionBaseDatos() {
        try {
            Connection conexion = DatabaseConfigComplete.getConnection();
            if (conexion != null && !conexion.isClosed()) {
                conexion.close();
                return true;
            }
            return false;
        } catch (SQLException e) {
            System.out.println("   Error de SQL: " + e.getMessage());
            return false;
        } catch (Exception e) {
            System.out.println("   Error general: " + e.getMessage());
            return false;
        }
    }
    
    private static boolean testConfiguracionesSistema() {
        try {
            ConfiguracionService servicio = new ConfiguracionService();
            
            // Test básico de obtención de configuraciones
            Map<ConfiguracionSistema.CategoriaParametro, List<ConfiguracionSistema>> configs = 
                servicio.obtenerConfiguracionesAgrupadasPorCategoria();
            
            if (configs == null || configs.isEmpty()) {
                System.out.println("   No se encontraron configuraciones del sistema");
                return false;
            }
            
            System.out.println("   Categorías encontradas: " + configs.size());
            for (ConfiguracionSistema.CategoriaParametro categoria : configs.keySet()) {
                List<ConfiguracionSistema> listaConfigs = configs.get(categoria);
                System.out.println("     - " + categoria + ": " + listaConfigs.size() + " configuraciones");
            }
            
            return true;
        } catch (Exception e) {
            System.out.println("   Error: " + e.getMessage());
            return false;
        }
    }
    
    private static boolean testConfiguracionesAlertas() {
        try {
            ConfiguracionService servicio = new ConfiguracionService();
            
            List<ConfiguracionAlerta> alertas = servicio.obtenerConfiguracionesAlerta();
            
            if (alertas == null || alertas.isEmpty()) {
                System.out.println("   No se encontraron configuraciones de alertas");
                return false;
            }
            
            System.out.println("   Alertas encontradas: " + alertas.size());
            for (ConfiguracionAlerta alerta : alertas) {
                System.out.println("     - " + alerta.getTipoAlerta().getDescripcion() + 
                                 " [" + (alerta.getActiva() ? "Activa" : "Inactiva") + "]");
            }
            
            return true;
        } catch (Exception e) {
            System.out.println("   Error: " + e.getMessage());
            return false;
        }
    }
    
    private static boolean testServicioConfiguracion() {
        try {
            ConfiguracionService servicio = new ConfiguracionService();
            
            // Test 1: Obtener configuración específica
            String nombreSistema = servicio.obtenerValorConfiguracion("sistema.nombre", "Sistema Activos");
            System.out.println("   Nombre del sistema: " + nombreSistema);
            
            // Test 2: Obtener horarios laborales
            Map<String, java.time.LocalTime> horarios = servicio.getHorariosLaborales();
            System.out.println("   Horario laboral: " + horarios.get("inicio") + " - " + horarios.get("fin"));
            
            // Test 3: Verificar si está en horario laboral
            boolean enHorario = servicio.estaEnHorarioLaboral();
            System.out.println("   ¿En horario laboral?: " + (enHorario ? "Sí" : "No"));
            
            // Test 4: Obtener estadísticas
            Map<String, Object> estadisticas = servicio.obtenerEstadisticasConfiguracion();
            System.out.println("   Total configuraciones: " + estadisticas.get("total_configuraciones"));
            
            return true;
        } catch (Exception e) {
            System.out.println("   Error: " + e.getMessage());
            return false;
        }
    }
}
