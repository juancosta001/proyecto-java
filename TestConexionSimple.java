import java.sql.*;

public class TestConexionSimple {
    public static void main(String[] args) {
        System.out.println("=== Test de Conexión Simple ===");
        
        String url = "jdbc:mysql://localhost:3306/?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=America/Asuncion";
        String username = "root";
        String password = "";
        
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("✓ Driver MySQL cargado");
            
            System.out.println("Conectando a: " + url);
            Connection connection = DriverManager.getConnection(url, username, password);
            System.out.println("✓ Conexión establecida!");
            
            // Listar bases de datos
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SHOW DATABASES");
            System.out.println("✓ Bases de datos disponibles:");
            boolean encontrado = false;
            while (rs.next()) {
                String dbName = rs.getString(1);
                System.out.println("   - " + dbName);
                if (dbName.equals("sistema_activos_ypacarai")) {
                    encontrado = true;
                }
            }
            
            if (!encontrado) {
                System.out.println("✗ Base de datos 'sistema_activos_ypacarai' NO ENCONTRADA");
                System.out.println("Necesitas ejecutar el script setup_database.sql");
            } else {
                System.out.println("✓ Base de datos 'sistema_activos_ypacarai' encontrada");
            }
            
            connection.close();
            System.out.println("✓ Test completado");
            
        } catch (Exception e) {
            System.err.println("✗ Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
