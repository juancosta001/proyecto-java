import com.ypacarai.cooperativa.activos.view.LoginWindowNew;

public class TestLoginDirectoTecnico {
    public static void main(String[] args) {
        try {
            System.out.println("=== Test Login Directo para Técnicos ===");
            
            // Test 1: Verificar que la ventana de login se abre correctamente
            System.out.println("\n1. Abriendo ventana de login...");
            LoginWindowNew loginWindow = new LoginWindowNew();
            loginWindow.setVisible(true);
            
            System.out.println("\n=== Instrucciones para el Test ===");
            System.out.println("1. La ventana de login debería estar abierta");
            System.out.println("2. Ingrese credenciales de un usuario TÉCNICO");
            System.out.println("3. Al hacer login exitoso, debería abrirse DIRECTAMENTE");
            System.out.println("   la ventana de 'Mantenimiento Técnico' sin pasar por el menú principal");
            System.out.println("4. Para usuarios con otros roles (Jefe_Informatica, Consulta)");
            System.out.println("   debería abrirse la ventana principal normal");
            
            System.out.println("\n=== Comportamiento Esperado ===");
            System.out.println("✅ TÉCNICO → Ventana Mantenimiento Técnico (DIRECTO)");
            System.out.println("✅ JEFE_INFORMÁTICA → Ventana Principal");
            System.out.println("✅ CONSULTA → Ventana Principal");
            
            System.out.println("\n=== Beneficio ===");
            System.out.println("Los técnicos ahorran tiempo al no tener que navegar");
            System.out.println("por el menú principal para acceder a su área de trabajo.");
            
        } catch (Exception e) {
            System.err.println("Error durante el test: " + e.getMessage());
        }
    }
}