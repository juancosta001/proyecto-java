import java.time.LocalDate;

import com.ypacarai.cooperativa.activos.model.FiltrosReporte;
import com.ypacarai.cooperativa.activos.model.ReporteCompleto;
import com.ypacarai.cooperativa.activos.service.ReporteService;

/**
 * Test que reproduce exactamente lo que hace la aplicación al generar el reporte
 */
public class TestReporteServiceCompleto {
    
    public static void main(String[] args) {
        System.out.println("=== Test del ReporteService Completo ===");
        
        try {
            System.out.println("1. Creando instancia de ReporteService...");
            ReporteService reporteService = new ReporteService();
            System.out.println("   ✅ ReporteService creado exitosamente");
            
            System.out.println("\n2. Creando filtros de reporte...");
            FiltrosReporte filtros = new FiltrosReporte();
            
            // Establecer algunos filtros básicos para probar
            filtros.setFechaInicio(LocalDate.now().minusDays(30));
            filtros.setFechaFin(LocalDate.now());
            filtros.setTipoActivo(null); // Todos los tipos
            filtros.setUbicacion(null);  // Todas las ubicaciones
            
            System.out.println("   ✅ Filtros creados: " + filtros.getFechaInicio() + " a " + filtros.getFechaFin());
            
            System.out.println("\n3. Generando reporte de estado de activos...");
            ReporteCompleto reporte = reporteService.generarReporteEstadoActivos(filtros);
            
            if (reporte != null) {
                System.out.println("   ✅ Reporte generado exitosamente");
                System.out.println("   - Tipo: " + reporte.getTipoReporte());
                System.out.println("   - Fecha: " + reporte.getFechaGeneracion());
                System.out.println("   - Datos: " + (reporte.getDatosOriginales() != null ? reporte.getDatosOriginales().size() + " registros" : "null"));
                System.out.println("   - Resumen: " + (reporte.getResumenEjecutivo() != null ? reporte.getResumenEjecutivo().length() + " caracteres" : "null"));
            } else {
                System.out.println("   ❌ El reporte es null");
            }
            
            System.out.println("\n4. Probando filtros específicos...");
            FiltrosReporte filtrosEspecificos = new FiltrosReporte();
            filtrosEspecificos.setTipoActivo("PC");
            
            ReporteCompleto reporteEspecifico = reporteService.generarReporteEstadoActivos(filtrosEspecificos);
            
            if (reporteEspecifico != null) {
                System.out.println("   ✅ Reporte específico (PC) generado exitosamente");
                System.out.println("   - Datos: " + (reporteEspecifico.getDatosOriginales() != null ? reporteEspecifico.getDatosOriginales().size() + " registros" : "null"));
            } else {
                System.out.println("   ❌ El reporte específico es null");
            }
            
        } catch (Exception e) {
            System.err.println("❌ Error detallado: " + e.getMessage());
            System.err.println("   Tipo de error: " + e.getClass().getSimpleName());
            if (e.getCause() != null) {
                System.err.println("   Causa: " + e.getCause().getMessage());
            }
            e.printStackTrace();
        }
    }
}