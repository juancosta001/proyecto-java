package com.ypacarai.cooperativa.activos.test;

import com.ypacarai.cooperativa.activos.config.DatabaseConfigComplete;
import com.ypacarai.cooperativa.activos.model.Usuario;
import com.ypacarai.cooperativa.activos.service.GestionUsuariosService;

import java.sql.Connection;

/**
 * Test para verificar la creación de usuarios
 */
public class TestCrearUsuario {
    
    public static void main(String[] args) {
        System.out.println("=== TEST DE CREACIÓN DE USUARIO ===\n");
        
        // 1. Probar conexión a la base de datos
        System.out.println("1. Probando conexión a la base de datos...");
        try (Connection conn = DatabaseConfigComplete.getConnection()) {
            if (conn != null && !conn.isClosed()) {
                System.out.println("✅ Conexión exitosa a la base de datos");
                System.out.println("   URL: " + conn.getMetaData().getURL());
                System.out.println("   Usuario: " + conn.getMetaData().getUserName());
            } else {
                System.out.println("❌ No se pudo conectar a la base de datos");
                return;
            }
        } catch (Exception e) {
            System.out.println("❌ Error de conexión: " + e.getMessage());
            e.printStackTrace();
            return;
        }
        
        // 2. Probar creación de usuario
        System.out.println("\n2. Probando creación de usuario...");
        GestionUsuariosService service = new GestionUsuariosService();
        
        String nombre = "Usuario Prueba";
        String username = "test_" + System.currentTimeMillis();
        String password = "123456";
        String email = "test@cooperativa.com";
        Usuario.Rol rol = Usuario.Rol.Consulta;
        Integer usuarioCreadorId = 1; // ID del admin
        
        System.out.println("   Datos del usuario:");
        System.out.println("   - Nombre: " + nombre);
        System.out.println("   - Usuario: " + username);
        System.out.println("   - Password: " + password);
        System.out.println("   - Email: " + email);
        System.out.println("   - Rol: " + rol);
        System.out.println("   - Creado por ID: " + usuarioCreadorId);
        
        try {
            var resultado = service.crearUsuario(
                nombre, username, password, email, rol, usuarioCreadorId
            );
            
            if (resultado.isExitoso()) {
                System.out.println("✅ Usuario creado exitosamente!");
                System.out.println("   Mensaje: " + resultado.getMensaje());
                if (resultado.getId() != null) {
                    System.out.println("   ID asignado: " + resultado.getId());
                }
            } else {
                System.out.println("❌ Error al crear usuario:");
                System.out.println("   Mensaje: " + resultado.getMensaje());
            }
            
        } catch (Exception e) {
            System.out.println("❌ Excepción durante la creación:");
            System.out.println("   Error: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("\n=== FIN DEL TEST ===");
    }
}