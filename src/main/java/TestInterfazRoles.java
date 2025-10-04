import com.ypacarai.cooperativa.activos.model.Usuario;
import com.ypacarai.cooperativa.activos.view.MainWindowNew;

public class TestInterfazRoles {
    public static void main(String[] args) {
        try {
            System.out.println("=== Test Interfaz Basada en Roles ===");
            
            // Test 1: Usuario Jefe_Informatica - debe ver todos los menús
            System.out.println("\n1. Probando usuario Jefe_Informatica...");
            Usuario admin = new Usuario();
            admin.setUsuNombre("Admin Test");
            admin.setUsuUsuario("admin");
            admin.setUsuRol(Usuario.Rol.Jefe_Informatica);
            
            MainWindowNew ventanaAdmin = new MainWindowNew(admin);
            ventanaAdmin.setVisible(true);
            
            Thread.sleep(3000); // Esperar 3 segundos para ver la ventana
            ventanaAdmin.dispose();
            
            // Test 2: Usuario TECNICO - debe ver menos menús
            System.out.println("\n2. Probando usuario Tecnico...");
            Usuario tecnico = new Usuario();
            tecnico.setUsuNombre("Tecnico Test");
            tecnico.setUsuUsuario("tecnico");
            tecnico.setUsuRol(Usuario.Rol.Tecnico);
            
            MainWindowNew ventanaTecnico = new MainWindowNew(tecnico);
            ventanaTecnico.setVisible(true);
            
            Thread.sleep(3000); // Esperar 3 segundos para ver la ventana
            ventanaTecnico.dispose();
            
            // Test 3: Usuario Consulta - debe ver muy pocos menús
            System.out.println("\n3. Probando usuario Consulta...");
            Usuario usuarioComun = new Usuario();
            usuarioComun.setUsuNombre("Usuario Test");
            usuarioComun.setUsuUsuario("usuario");
            usuarioComun.setUsuRol(Usuario.Rol.Consulta);
            
            MainWindowNew ventanaUsuario = new MainWindowNew(usuarioComun);
            ventanaUsuario.setVisible(true);
            
            Thread.sleep(3000); // Esperar 3 segundos para ver la ventana
            ventanaUsuario.dispose();
            
            System.out.println("\n=== Test completado exitosamente ===");
            System.out.println("Verificar que:");
            System.out.println("- Jefe_Informatica vio todos los menús");
            System.out.println("- Tecnico vio menos menús (solo los permitidos)");
            System.out.println("- Consulta vio muy pocos menús");
            System.out.println("- No aparecieron botones deshabilitados, solo los permitidos");
            
        } catch (InterruptedException e) {
            System.err.println("Error durante el test: " + e.getMessage());
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            System.err.println("Error durante el test: " + e.getMessage());
            e.printStackTrace();
        } finally {
            System.exit(0);
        }
    }
}