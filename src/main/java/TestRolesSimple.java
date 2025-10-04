import com.ypacarai.cooperativa.activos.model.Usuario;
import com.ypacarai.cooperativa.activos.util.ControlAccesoRoles;

public class TestRolesSimple {
    public static void main(String[] args) {
        System.out.println("=== Test Control de Acceso por Roles ===");
        
        // Test con diferentes usuarios
        Usuario jefeInformatica = new Usuario();
        jefeInformatica.setUsuRol(Usuario.Rol.Jefe_Informatica);
        
        Usuario tecnico = new Usuario();
        tecnico.setUsuRol(Usuario.Rol.Tecnico);
        
        Usuario consulta = new Usuario();
        consulta.setUsuRol(Usuario.Rol.Consulta);
        
        // Test diferentes módulos
        String[] modulos = {
            "dashboard",
            "activos", 
            "tickets",
            "mantenimiento",
            "reportes",
            "usuarios",
            "configuracion"
        };
        
        System.out.println("\nPermisos por rol:");
        System.out.println("=".repeat(60));
        
        for (String modulo : modulos) {
            System.out.println("\nMódulo: " + modulo);
            
            boolean puedeJefe = ControlAccesoRoles.puedeAccederModulo(jefeInformatica, modulo);
            boolean puedeTecnico = ControlAccesoRoles.puedeAccederModulo(tecnico, modulo);
            boolean puedeConsulta = ControlAccesoRoles.puedeAccederModulo(consulta, modulo);
            
            System.out.println("  Jefe_Informatica: " + (puedeJefe ? "✓ PERMITIDO" : "✗ DENEGADO"));
            System.out.println("  Tecnico:          " + (puedeTecnico ? "✓ PERMITIDO" : "✗ DENEGADO"));
            System.out.println("  Consulta:         " + (puedeConsulta ? "✓ PERMITIDO" : "✗ DENEGADO"));
        }
        
        System.out.println("\n=== Test completado ===");
        System.out.println("\nLa nueva interfaz solo mostrará los botones permitidos para cada rol.");
        System.out.println("Ya no habrá botones deshabilitados ocupando espacio innecesario.");
    }
}