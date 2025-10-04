import java.sql.*;

public class VerificarEstructuraTablas {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/sistema_activos_ypacarai";
        String usuario = "root";
        String password = "";
        
        try (Connection conn = DriverManager.getConnection(url, usuario, password)) {
            System.out.println("=== ESTRUCTURA DE TABLAS PARA REPORTES ===");
            
            // Primero listar todas las tablas
            System.out.println("\n0. TODAS LAS TABLAS:");
            DatabaseMetaData metaData = conn.getMetaData();
            ResultSet tables = metaData.getTables(null, null, "%", new String[]{"TABLE"});
            while (tables.next()) {
                String tableName = tables.getString("TABLE_NAME");
                System.out.println("   " + tableName);
            }
            
            // Verificar tabla ticket (singular)
            System.out.println("\n1. TABLA TICKET:");
            ResultSet columns = metaData.getColumns(null, null, "ticket", null);
            while (columns.next()) {
                String columnName = columns.getString("COLUMN_NAME");
                String dataType = columns.getString("TYPE_NAME");
                System.out.println("   " + columnName + " - " + dataType);
            }
            
            // Verificar tabla mantenimiento (singular) 
            System.out.println("\n2. TABLA MANTENIMIENTO:");
            columns = metaData.getColumns(null, null, "mantenimiento", null);
            while (columns.next()) {
                String columnName = columns.getString("COLUMN_NAME");
                String dataType = columns.getString("TYPE_NAME");
                System.out.println("   " + columnName + " - " + dataType);
            }
            
            // Verificar tabla traslado (singular)
            System.out.println("\n3. TABLA TRASLADO:");
            columns = metaData.getColumns(null, null, "traslado", null);
            while (columns.next()) {
                String columnName = columns.getString("COLUMN_NAME");
                String dataType = columns.getString("TYPE_NAME");
                System.out.println("   " + columnName + " - " + dataType);
            }
            
        } catch (SQLException e) {
            System.err.println("Error verificando estructura: " + e.getMessage());
        }
    }
}