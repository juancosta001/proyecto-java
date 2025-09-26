import java.sql.*;

public class TestConexionBD {
    public static void main(String[] args) {
        System.out.println("=== Test de Conexión a Base de Datos ===");
        
        String url = "jdbc:mysql://localhost:3306/sistema_activos_ypacarai?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=America/Asuncion";
        String username = "root";
        String password = "";
        
        try {
            // Cargar driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("✓ Driver MySQL cargado correctamente");
            
            // Intentar conectar
            System.out.println("Intentando conectar a: " + url);
            Connection connection = DriverManager.getConnection(url, username, password);
            System.out.println("✓ Conexión establecida exitosamente!");
            
            // Test básico
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT 'Conexión OK' as mensaje");
            if (rs.next()) {
                System.out.println("✓ Test query exitoso: " + rs.getString("mensaje"));
            }
            
            // Verificar si existe la base de datos
            rs = stmt.executeQuery("SELECT DATABASE() as db_actual");
            if (rs.next()) {
                System.out.println("✓ Base de datos actual: " + rs.getString("db_actual"));
            }
            
            // Listar tablas
            rs = stmt.executeQuery("SHOW TABLES");
            System.out.println("✓ Tablas encontradas:");
            while (rs.next()) {
                System.out.println("   - " + rs.getString(1));
            }
            
            connection.close();
            System.out.println("✓ Conexión cerrada correctamente");
            
        } catch (ClassNotFoundException e) {
            System.err.println("✗ Error: Driver MySQL no encontrado");
            System.err.println("Detalles: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("✗ Error de conexión a la base de datos");
            System.err.println("Código de error: " + e.getErrorCode());
            System.err.println("Estado SQL: " + e.getSQLState());
            System.err.println("Mensaje: " + e.getMessage());
            
            if (e.getMessage().contains("Unknown database")) {
                System.out.println("\n=== SOLUCIÓN SUGERIDA ===");
                System.out.println("La base de datos 'sistema_activos_ypacarai' no existe.");
                System.out.println("Debes crearla primero ejecutando el script setup_database.sql");
            } else if (e.getMessage().contains("Access denied")) {
                System.out.println("\n=== SOLUCIÓN SUGERIDA ===");
                System.out.println("Credenciales incorrectas. Verifica usuario/contraseña de MySQL.");
            } else if (e.getMessage().contains("Connection refused")) {
                System.out.println("\n=== SOLUCIÓN SUGERIDA ===");
                System.out.println("MySQL no está ejecutándose o no es accesible en localhost:3306");
            }
        } catch (Exception e) {
            System.err.println("✗ Error inesperado: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
