import java.sql.*;
import com.ypacarai.cooperativa.activos.config.DatabaseConfig;

public class TestConstraint {
    public static void main(String[] args) {
        try {
            Connection conn = DatabaseConfig.getConnection();
            
            // Consultar los constraints de la tabla
            String query = "SHOW CREATE TABLE mantenimiento_tercerizado";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            
            if (rs.next()) {
                String createTableSQL = rs.getString(2);
                System.out.println("=== CREATE TABLE mantenimiento_tercerizado ===");
                System.out.println(createTableSQL);
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