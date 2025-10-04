import java.time.LocalDate;
import java.util.HashMap;

import com.ypacarai.cooperativa.activos.service.ReporteService;
import com.ypacarai.cooperativa.activos.model.FiltrosReporte;
import com.ypacarai.cooperativa.activos.model.ReporteCompleto;

/**
 * Test para verificar todos los tipos de reportes
 */
public class TestTodosLosReportes {
    
    public static void main(String[] args) {
        System.out.println("=== TEST DE TODOS LOS TIPOS DE REPORTES ===");
        
        ReporteService reporteService = new ReporteService();
        
        // Crear filtros básicos
        FiltrosReporte filtros = new FiltrosReporte();
        filtros.setFechaInicio(LocalDate.now().minusMonths(1));
        filtros.setFechaFin(LocalDate.now());
        filtros.setTipoActivo("Todos");
        filtros.setUbicacion("Todos");
        filtros.setFiltrosPersonalizados(new HashMap<>());
        
        System.out.println("Filtros configurados:");
        System.out.println("- Fecha inicio: " + filtros.getFechaInicio());
        System.out.println("- Fecha fin: " + filtros.getFechaFin());
        System.out.println();
        
        // Test 1: Reporte Estado de Activos
        System.out.println("1. PROBANDO REPORTE ESTADO DE ACTIVOS...");
        try {
            ReporteCompleto reporte1 = reporteService.generarReporteEstadoActivos(filtros);
            System.out.println("✅ Estado de Activos: " + reporte1.getDatosOriginales().size() + " registros");
            System.out.println("   Tipo: " + reporte1.getTipoReporte());
        } catch (Exception e) {
            System.out.println("❌ Estado de Activos: ERROR - " + e.getMessage());
            System.out.println("   Causa: " + (e.getCause() != null ? e.getCause().getMessage() : "N/A"));
        }
        System.out.println();
        
        // Test 2: Reporte Mantenimientos  
        System.out.println("2. PROBANDO REPORTE MANTENIMIENTOS...");
        try {
            ReporteCompleto reporte2 = reporteService.generarReporteMantenimientos(filtros);
            System.out.println("✅ Mantenimientos: " + reporte2.getDatosOriginales().size() + " registros");
            System.out.println("   Tipo: " + reporte2.getTipoReporte());
        } catch (Exception e) {
            System.out.println("❌ Mantenimientos: ERROR - " + e.getMessage());
            System.out.println("   Causa: " + (e.getCause() != null ? e.getCause().getMessage() : "N/A"));
        }
        System.out.println();
        
        // Test 3: Reporte Fallas
        System.out.println("3. PROBANDO REPORTE FALLAS...");
        try {
            ReporteCompleto reporte3 = reporteService.generarReporteFallas(filtros);
            System.out.println("✅ Fallas: " + reporte3.getDatosOriginales().size() + " registros");
            System.out.println("   Tipo: " + reporte3.getTipoReporte());
        } catch (Exception e) {
            System.out.println("❌ Fallas: ERROR - " + e.getMessage());
            System.out.println("   Causa: " + (e.getCause() != null ? e.getCause().getMessage() : "N/A"));
        }
        System.out.println();
        
        // Test 4: Reporte Traslados
        System.out.println("4. PROBANDO REPORTE TRASLADOS...");
        try {
            ReporteCompleto reporte4 = reporteService.generarReporteTraslados(filtros);
            System.out.println("✅ Traslados: " + reporte4.getDatosOriginales().size() + " registros");
            System.out.println("   Tipo: " + reporte4.getTipoReporte());
        } catch (Exception e) {
            System.out.println("❌ Traslados: ERROR - " + e.getMessage());
            System.out.println("   Causa: " + (e.getCause() != null ? e.getCause().getMessage() : "N/A"));
        }
        System.out.println();
        
        System.out.println("=== FIN DEL TEST ===");
    }
}