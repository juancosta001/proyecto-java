import java.sql.*;
import com.ypacarai.cooperativa.activos.config.DatabaseConfig;

public class TestFechas {
    public static void main(String[] args) {
        try {
            Connection conn = DatabaseConfig.getConnection();
            
            // Consultar el registro recién creado
            String query = "SELECT id, fecha_solicitud, fecha_retiro, fecha_creacion FROM mantenimiento_tercerizado WHERE id = 9";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            
            if (rs.next()) {
                System.out.println("=== FECHAS DEL MANTENIMIENTO ID 9 ===");
                System.out.println("ID: " + rs.getInt("id"));
                System.out.println("Fecha solicitud: " + rs.getTimestamp("fecha_solicitud"));
                System.out.println("Fecha retiro: " + rs.getTimestamp("fecha_retiro"));
                System.out.println("Fecha creación: " + rs.getTimestamp("fecha_creacion"));
            } else {
                System.out.println("No se encontró el registro con ID 9");
            }
            
            rs.close();
            stmt.close();
            conn.close();
            
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}