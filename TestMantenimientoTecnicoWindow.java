import javax.swing.*;
import com.ypacarai.cooperativa.activos.view.MantenimientoTecnicoWindow;
import com.ypacarai.cooperativa.activos.model.Usuario;

/**
 * Test para verificar que MantenimientoTecnicoWindow funciona correctamente
 */
public class TestMantenimientoTecnicoWindow {
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                // Configurar Look and Feel
                try {
                    UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
                } catch (Exception lnfEx) {
                    // Usar Look and Feel por defecto si falla
                }
                
                // Crear usuario de prueba
                Usuario usuarioTecnico = new Usuario();
                usuarioTecnico.setUsuId(1);
                usuarioTecnico.setUsuNombre("Juan Técnico Test");
                usuarioTecnico.setUsuRol(Usuario.Rol.Tecnico);
                
                System.out.println("Iniciando test de MantenimientoTecnicoWindow...");
                
                // Crear y mostrar la ventana
                MantenimientoTecnicoWindow ventana = new MantenimientoTecnicoWindow(null, usuarioTecnico);
                ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                ventana.setVisible(true);
                
                System.out.println("✅ MantenimientoTecnicoWindow creada exitosamente!");
                
            } catch (Exception e) {
                System.err.println("❌ Error al crear MantenimientoTecnicoWindow:");
                System.err.println("Mensaje: " + e.getMessage());
                System.err.println("Tipo: " + e.getClass().getSimpleName());
                if (e.getCause() != null) {
                    System.err.println("Causa: " + e.getCause().getMessage());
                }
                e.printStackTrace();
            }
        });
    }
}