import com.ypacarai.cooperativa.activos.service.MantenimientoTercerizadoService;
import java.math.BigDecimal;

public class TestMantenimientoTercerizado {
    public static void main(String[] args) {
        System.out.println("=== TEST MANTENIMIENTO TERCERIZADO ===");
        
        try {
            MantenimientoTercerizadoService service = new MantenimientoTercerizadoService();
            
            // Test 1: Crear solicitud con monto
            System.out.println("\n--- TEST 1: Crear solicitud ---");
            int mantId = service.solicitarMantenimiento(
                1, // activoId
                1, // proveedorId
                "Problema de prueba",
                "Operativo",
                new BigDecimal("500.00"), // monto presupuestado
                1 // usuario registrador
            );
            
            System.out.println("Solicitud creada con ID: " + mantId);
            
            if (mantId > 0) {
                // Test 2: Obtener el mantenimiento para verificar datos
                System.out.println("\n--- TEST 2: Verificar datos insertados ---");
                var mantenimiento = service.obtenerPorId(mantId);
                if (mantenimiento != null) {
                    System.out.println("Activo ID insertado: " + mantenimiento.getActivoId());
                    System.out.println("Monto insertado: " + mantenimiento.getMontoPresupuestado());
                    System.out.println("Estado: " + mantenimiento.getEstado());
                } else {
                    System.out.println("No se pudo recuperar el mantenimiento creado");
                }
            }
            
        } catch (Exception e) {
            System.err.println("Error en el test: " + e.getMessage());
            e.printStackTrace();
        }
    }
}