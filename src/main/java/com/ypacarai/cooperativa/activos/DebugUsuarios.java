package com.ypacarai.cooperativa.activos;

import com.ypacarai.cooperativa.activos.config.DatabaseConfig;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DebugUsuarios {
    public static void main(String[] args) {
        try {
            System.out.println("=== DEBUG: Usuarios en la base de datos ===");
            
            String query = "SELECT usu_id, usu_nombres, usu_apellidos FROM usuario LIMIT 10";
            
            try (Connection conn = DatabaseConfig.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query);
                 ResultSet rs = stmt.executeQuery()) {
                
                System.out.println("ID\tNombres\t\tApellidos");
                System.out.println("--\t-------\t\t---------");
                
                while (rs.next()) {
                    System.out.printf("%d\t%s\t\t%s%n",
                        rs.getInt("usu_id"),
                        rs.getString("usu_nombres"),
                        rs.getString("usu_apellidos"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error consultando usuarios: " + e.getMessage());
            e.printStackTrace();
        }
    }
}