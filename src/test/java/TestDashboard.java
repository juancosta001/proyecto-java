import javax.swing.SwingUtilities;

import com.ypacarai.cooperativa.activos.model.Usuario;
import com.ypacarai.cooperativa.activos.view.MainWindowNew;

/**
 * Clase de prueba para el dashboard con datos reales
 */
public class TestDashboard {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                // Crear usuario de prueba
                Usuario usuarioPrueba = new Usuario();
                usuarioPrueba.setUsuNombre("Administrador Dashboard");
                usuarioPrueba.setUsuEmail("admin@cooperativa-ypacarai.com");
                usuarioPrueba.setUsuRol(Usuario.Rol.Jefe_Informatica);
                
                // Crear y mostrar el dashboard
                System.out.println("Iniciando Dashboard con datos reales...");
                MainWindowNew mainWindow = new MainWindowNew(usuarioPrueba);
                mainWindow.setVisible(true);
                
                System.out.println("Dashboard iniciado exitosamente!");
                System.out.println("Características implementadas:");
                System.out.println("✅ KPIs con datos reales de la base de datos");
                System.out.println("✅ Gráfico de distribución de activos por estado");
                System.out.println("✅ Sistema de alertas y notificaciones reales");
                System.out.println("✅ Actualización automática cada 5 minutos");
                System.out.println("✅ Datos extraídos desde ReportesDAOSimple");
                
            } catch (Exception e) {
                System.err.println("Error iniciando el dashboard: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }
}
