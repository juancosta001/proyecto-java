import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.ypacarai.cooperativa.activos.config.DatabaseConfig;

/**
 * Test específico de la consulta del reporte que está fallando
 */
public class TestConsultaReporteCompleta {
    
    public static void main(String[] args) {
        System.out.println("=== Test de Consulta Completa del Reporte ===");
        
        try {
            testConsultaOriginal();
            System.out.println("\n" + "=".repeat(50));
            testConsultaSinSubconsultas();
            
        } catch (Exception e) {
            System.err.println("Error durante el test: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void testConsultaOriginal() {
        System.out.println("1. Probando consulta original completa...");
        
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ")
           .append("    ta.nombre as tipo_activo, ")
           .append("    a.act_estado as estado, ")
           .append("    COUNT(*) as cantidad_total, ")
           .append("    u.ubi_nombre as ubicacion, ")
           .append("    SUM(CASE WHEN EXISTS( ")
           .append("        SELECT 1 FROM TICKET t ")
           .append("        WHERE t.act_id = a.act_id ")
           .append("        AND t.tick_tipo = 'Preventivo' ")
           .append("        AND t.tick_fecha_vencimiento BETWEEN CURRENT_DATE ")
           .append("        AND DATE_ADD(CURRENT_DATE, INTERVAL 7 DAY) ")
           .append("        AND t.tick_estado IN ('Abierto', 'En_Proceso') ")
           .append("    ) THEN 1 ELSE 0 END) as proximos_mantenimiento, ")
           .append("    SUM(CASE WHEN EXISTS( ")
           .append("        SELECT 1 FROM TICKET t ")
           .append("        WHERE t.act_id = a.act_id ")
           .append("        AND t.tick_tipo = 'Preventivo' ")
           .append("        AND t.tick_fecha_vencimiento < CURRENT_DATE ")
           .append("        AND t.tick_estado IN ('Abierto', 'En_Proceso') ")
           .append("    ) THEN 1 ELSE 0 END) as mantenimiento_vencido ")
           .append("FROM ACTIVO a ")
           .append("INNER JOIN TIPO_ACTIVO ta ON a.tip_act_id = ta.tip_act_id ")
           .append("INNER JOIN UBICACION u ON a.act_ubicacion_actual = u.ubi_id ")
           .append("WHERE 1=1 ")
           .append("GROUP BY ta.nombre, a.act_estado, u.ubi_nombre ")
           .append("ORDER BY ta.nombre, u.ubi_nombre, a.act_estado ")
           .append("LIMIT 5");
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString());
             ResultSet rs = stmt.executeQuery()) {
            
            System.out.println("   Resultados:");
            int count = 0;
            while (rs.next() && count < 5) {
                System.out.println("     - Tipo: " + rs.getString("tipo_activo") + 
                                 ", Estado: " + rs.getString("estado") + 
                                 ", Cantidad: " + rs.getInt("cantidad_total") + 
                                 ", Ubicación: " + rs.getString("ubicacion") +
                                 ", Próximos: " + rs.getInt("proximos_mantenimiento") +
                                 ", Vencidos: " + rs.getInt("mantenimiento_vencido"));
                count++;
            }
            
            if (count == 0) {
                System.out.println("   ⚠️ No se encontraron resultados");
            } else {
                System.out.println("   ✅ Consulta original exitosa");
            }
            
        } catch (SQLException e) {
            System.out.println("   ❌ Error en consulta original: " + e.getMessage());
            System.out.println("   SQLState: " + e.getSQLState());
            System.out.println("   ErrorCode: " + e.getErrorCode());
        }
    }
    
    private static void testConsultaSinSubconsultas() {
        System.out.println("2. Probando consulta simplificada sin subconsultas...");
        
        String sql = "SELECT " +
            "ta.nombre as tipo_activo, " +
            "a.act_estado as estado, " +
            "COUNT(*) as cantidad_total, " +
            "u.ubi_nombre as ubicacion, " +
            "0 as proximos_mantenimiento, " +
            "0 as mantenimiento_vencido " +
            "FROM ACTIVO a " +
            "INNER JOIN TIPO_ACTIVO ta ON a.tip_act_id = ta.tip_act_id " +
            "INNER JOIN UBICACION u ON a.act_ubicacion_actual = u.ubi_id " +
            "GROUP BY ta.nombre, a.act_estado, u.ubi_nombre " +
            "ORDER BY ta.nombre, u.ubi_nombre, a.act_estado " +
            "LIMIT 5";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            System.out.println("   Resultados:");
            int count = 0;
            while (rs.next() && count < 5) {
                System.out.println("     - Tipo: " + rs.getString("tipo_activo") + 
                                 ", Estado: " + rs.getString("estado") + 
                                 ", Cantidad: " + rs.getInt("cantidad_total") + 
                                 ", Ubicación: " + rs.getString("ubicacion") +
                                 ", Próximos: " + rs.getInt("proximos_mantenimiento") +
                                 ", Vencidos: " + rs.getInt("mantenimiento_vencido"));
                count++;
            }
            
            if (count == 0) {
                System.out.println("   ⚠️ No se encontraron resultados");
            } else {
                System.out.println("   ✅ Consulta simplificada exitosa");
            }
            
        } catch (SQLException e) {
            System.out.println("   ❌ Error en consulta simplificada: " + e.getMessage());
        }
    }
}