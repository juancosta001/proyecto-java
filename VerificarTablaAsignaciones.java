import java.sql.*;
import com.ypacarai.cooperativa.activos.config.DatabaseConfigComplete;

/**
 * Programa para verificar y crear la tabla ticket_asignaciones
 */
public class VerificarTablaAsignaciones {
    
    public static void main(String[] args) {
        try {
            System.out.println("Verificando tabla ticket_asignaciones...");
            
            Connection conn = DatabaseConfigComplete.getConnection();
            
            // Verificar si la tabla existe
            DatabaseMetaData metaData = conn.getMetaData();
            ResultSet tables = metaData.getTables(null, null, "ticket_asignaciones", null);
            
            if (tables.next()) {
                System.out.println("✅ La tabla ticket_asignaciones ya existe.");
            } else {
                System.out.println("❌ La tabla ticket_asignaciones no existe. Creándola...");
                
                // Crear la tabla
                String createTableSQL = 
                    "CREATE TABLE IF NOT EXISTS ticket_asignaciones (" +
                    "tas_id INT PRIMARY KEY AUTO_INCREMENT, " +
                    "tick_id INT NOT NULL, " +
                    "usu_id INT NOT NULL, " +
                    "tas_fecha_asignacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                    "tas_activo BOOLEAN DEFAULT TRUE, " +
                    "tas_rol_asignacion ENUM('Responsable', 'Colaborador', 'Supervisor') DEFAULT 'Responsable', " +
                    "tas_observaciones TEXT, " +
                    "creado_en TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                    "actualizado_en TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, " +
                    "CONSTRAINT fk_tas_ticket FOREIGN KEY (tick_id) REFERENCES ticket(tick_id) ON DELETE CASCADE, " +
                    "CONSTRAINT fk_tas_usuario FOREIGN KEY (usu_id) REFERENCES usuario(usu_id) ON DELETE CASCADE, " +
                    "UNIQUE KEY uk_ticket_usuario (tick_id, usu_id)" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci";
                
                Statement stmt = conn.createStatement();
                stmt.execute(createTableSQL);
                
                // Crear índices
                stmt.execute("CREATE INDEX idx_tas_ticket_activo ON ticket_asignaciones(tick_id, tas_activo)");
                stmt.execute("CREATE INDEX idx_tas_usuario_activo ON ticket_asignaciones(usu_id, tas_activo)");
                stmt.execute("CREATE INDEX idx_tas_rol ON ticket_asignaciones(tas_rol_asignacion)");
                
                System.out.println("✅ Tabla ticket_asignaciones creada exitosamente con todos los índices.");
            }
            
            // Verificar estructura de la tabla
            ResultSet columns = metaData.getColumns(null, null, "ticket_asignaciones", null);
            System.out.println("\n📋 Estructura de la tabla ticket_asignaciones:");
            while (columns.next()) {
                String columnName = columns.getString("COLUMN_NAME");
                String columnType = columns.getString("TYPE_NAME");
                String columnSize = columns.getString("COLUMN_SIZE");
                String nullable = columns.getString("IS_NULLABLE");
                System.out.println("  - " + columnName + " (" + columnType + 
                                 (columnSize != null ? "(" + columnSize + ")" : "") + 
                                 ", nullable: " + nullable + ")");
            }
            
            conn.close();
            System.out.println("\n🎉 Verificación completada exitosamente!");
            
        } catch (Exception e) {
            System.err.println("❌ Error al verificar/crear tabla: " + e.getMessage());
            e.printStackTrace();
        }
    }
}