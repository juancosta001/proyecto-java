import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.ypacarai.cooperativa.activos.config.DatabaseConfig;

/**
 * Test para diagnosticar problemas con el reporte de estado de activos
 */
public class TestDiagnosticoReportes {
    
    public static void main(String[] args) {
        System.out.println("=== Diagnóstico de Reportes ===");
        
        try {
            // Test 1: Conexión a la base de datos
            System.out.println("1. Probando conexión a base de datos...");
            try (Connection conn = DatabaseConfig.getConnection()) {
                if (conn != null && !conn.isClosed()) {
                    System.out.println("   ✅ Conexión exitosa");
                } else {
                    System.out.println("   ❌ Conexión fallida");
                    return;
                }
            }
            
            // Test 2: Verificar tablas principales
            System.out.println("\n2. Verificando existencia de tablas...");
            verificarTabla("ACTIVO");
            verificarTabla("TIPO_ACTIVO");
            verificarTabla("UBICACION");
            verificarTabla("TICKET");
            
            // Test 3: Verificar columnas críticas
            System.out.println("\n3. Verificando columnas de la tabla ACTIVO...");
            verificarColumnas("ACTIVO");
            
            // Test 4: Consulta básica de activos
            System.out.println("\n4. Probando consulta básica de activos...");
            consultaBasicaActivos();
            
            // Test 5: Consulta simplificada del reporte
            System.out.println("\n5. Probando consulta simplificada del reporte...");
            consultaSimplificadaReporte();
            
        } catch (Exception e) {
            System.err.println("Error durante el diagnóstico: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void verificarTabla(String nombreTabla) {
        try (Connection conn = DatabaseConfig.getConnection()) {
            DatabaseMetaData metaData = conn.getMetaData();
            try (ResultSet rs = metaData.getTables(null, null, nombreTabla, new String[]{"TABLE"})) {
                if (rs.next()) {
                    System.out.println("   ✅ Tabla " + nombreTabla + " existe");
                } else {
                    System.out.println("   ❌ Tabla " + nombreTabla + " NO existe");
                }
            }
        } catch (SQLException e) {
            System.out.println("   ❌ Error verificando tabla " + nombreTabla + ": " + e.getMessage());
        }
    }
    
    private static void verificarColumnas(String nombreTabla) {
        try (Connection conn = DatabaseConfig.getConnection()) {
            DatabaseMetaData metaData = conn.getMetaData();
            try (ResultSet rs = metaData.getColumns(null, null, nombreTabla, null)) {
                System.out.println("   Columnas en " + nombreTabla + ":");
                while (rs.next()) {
                    String columnName = rs.getString("COLUMN_NAME");
                    String dataType = rs.getString("TYPE_NAME");
                    System.out.println("     - " + columnName + " (" + dataType + ")");
                }
            }
        } catch (SQLException e) {
            System.out.println("   ❌ Error verificando columnas de " + nombreTabla + ": " + e.getMessage());
        }
    }
    
    private static void consultaBasicaActivos() {
        String sql = "SELECT COUNT(*) as total FROM ACTIVO";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                int total = rs.getInt("total");
                System.out.println("   ✅ Total de activos en BD: " + total);
            }
        } catch (SQLException e) {
            System.out.println("   ❌ Error en consulta básica: " + e.getMessage());
        }
    }
    
    private static void consultaSimplificadaReporte() {
        String sql = "SELECT " +
            "ta.nombre as tipo_activo, " +
            "a.act_estado as estado, " +
            "COUNT(*) as cantidad_total, " +
            "u.ubi_nombre as ubicacion " +
            "FROM ACTIVO a " +
            "INNER JOIN TIPO_ACTIVO ta ON a.tip_act_id = ta.tip_act_id " +
            "INNER JOIN UBICACION u ON a.act_ubicacion_actual = u.ubi_id " +
            "GROUP BY ta.nombre, a.act_estado, u.ubi_nombre " +
            "ORDER BY ta.nombre, u.ubi_nombre, a.act_estado " +
            "LIMIT 5";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            System.out.println("   Resultados de consulta simplificada:");
            int count = 0;
            while (rs.next() && count < 5) {
                System.out.println("     - Tipo: " + rs.getString("tipo_activo") + 
                                 ", Estado: " + rs.getString("estado") + 
                                 ", Cantidad: " + rs.getInt("cantidad_total") + 
                                 ", Ubicación: " + rs.getString("ubicacion"));
                count++;
            }
            
            if (count == 0) {
                System.out.println("   ⚠️ No se encontraron resultados");
            } else {
                System.out.println("   ✅ Consulta simplificada exitosa");
            }
            
        } catch (SQLException e) {
            System.out.println("   ❌ Error en consulta simplificada: " + e.getMessage());
            System.out.println("   Detalle del error: " + e.getSQLState());
        }
    }
}