import java.sql.Connection;import java.sql.Connection;

import java.sql.ResultSet;import java.sql.ResultSet;

import java.sql.PreparedStatement;import java.sql.PreparedStatement;

import com.ypacarai.cooperativa.activos.config.DatabaseConfig;import com.ypacarai.cooperativa.activos.config.DatabaseConfig;



public class TestBaseDatos {public class TestBaseDatos {

    public static void main(String[] args) {    public static void main(String[] args) {

        try {        try {

            System.out.println("Probando conexión a base de datos...");            System.out.println("Probando conexión a base de datos...");

            Connection conn = DatabaseConfig.getConnection();            Connection conn = DatabaseConfig.getConnection();

                        

            // Verificar tabla proveedor_servicio            // Verificar tabla proveedor_servicio

            PreparedStatement stmt = conn.prepareStatement("SHOW TABLES LIKE 'proveedor_servicio'");            PreparedStatement stmt = conn.prepareStatement("SHOW TABLES LIKE \"proveedor_servicio\"");

            ResultSet rs = stmt.executeQuery();            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {            if (rs.next()) {

                System.out.println("✓ Tabla proveedor_servicio existe");                System.out.println(" Tabla proveedor_servicio existe");

            } else {            } else {

                System.out.println("✗ Tabla proveedor_servicio NO existe");                System.out.println(" Tabla proveedor_servicio NO existe");

            }            }

            rs.close();            rs.close();

            stmt.close();            stmt.close();

                        

            // Verificar tabla mantenimiento_tercerizado              // Verificar tabla mantenimiento_tercerizado  

            stmt = conn.prepareStatement("SHOW TABLES LIKE 'mantenimiento_tercerizado'");            stmt = conn.prepareStatement("SHOW TABLES LIKE \"mantenimiento_tercerizado\"");

            rs = stmt.executeQuery();            rs = stmt.executeQuery();

            if (rs.next()) {            if (rs.next()) {

                System.out.println("✓ Tabla mantenimiento_tercerizado existe");                System.out.println(" Tabla mantenimiento_tercerizado existe");

            } else {            } else {

                System.out.println("✗ Tabla mantenimiento_tercerizado NO existe");                System.out.println(" Tabla mantenimiento_tercerizado NO existe");

            }            }

            rs.close();            rs.close();

            stmt.close();            stmt.close();

                        

            // Verificar estructura de proveedor_servicio            // Verificar estructura de proveedor_servicio

            System.out.println("\nEstructura de proveedor_servicio:");            System.out.println("\nEstructura de proveedor_servicio:");

            stmt = conn.prepareStatement("DESCRIBE proveedor_servicio");            stmt = conn.prepareStatement("DESCRIBE proveedor_servicio");

            rs = stmt.executeQuery();            rs = stmt.executeQuery();

            while (rs.next()) {            while (rs.next()) {

                System.out.println("  " + rs.getString("Field") + " - " + rs.getString("Type"));                System.out.println("  " + rs.getString("Field") + " - " + rs.getString("Type"));

            }            }

                        

            conn.close();            conn.close();

            System.out.println("\n✓ Conexión exitosa");            System.out.println("\n Conexión exitosa");

        } catch (Exception e) {        } catch (Exception e) {

            System.err.println("✗ Error: " + e.getMessage());            System.err.println(" Error: " + e.getMessage());

            e.printStackTrace();            e.printStackTrace();

        }        }

    }    }

}}
