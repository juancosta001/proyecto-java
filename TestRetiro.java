import com.ypacarai.cooperativa.activos.service.MantenimientoTercerizadoService;
import java.time.LocalDate;

public class TestRetiro {
    public static void main(String[] args) {
        System.out.println("=== TEST REGISTRO DE RETIRO ===");
        
        try {
            MantenimientoTercerizadoService service = new MantenimientoTercerizadoService();
            
            // Usar el ID 9 del test anterior (o cambiarlo por uno existente)
            int mantId = 9;
            
            System.out.println("\n--- TEST: Registrar retiro para mantenimiento ID " + mantId + " ---");
            
            boolean resultado = service.registrarRetiroEquipo(
                mantId,
                LocalDate.now(),
                "Retiro para mantenimiento de prueba"
            );
            
            System.out.println("Resultado del retiro: " + resultado);
            
            if (resultado) {
                // Verificar el estado actualizado
                var mantenimiento = service.obtenerPorId(mantId);
                if (mantenimiento != null) {
                    System.out.println("Estado después del retiro: " + mantenimiento.getEstado());
                    System.out.println("Fecha de retiro: " + mantenimiento.getFechaRetiro());
                }
            }
            
        } catch (Exception e) {
            System.err.println("Error en el test de retiro: " + e.getMessage());
            e.printStackTrace();
        }
    }
}