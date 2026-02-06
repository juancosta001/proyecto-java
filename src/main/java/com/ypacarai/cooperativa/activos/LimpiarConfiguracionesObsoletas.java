package com.ypacarai.cooperativa.activos;

import java.sql.Connection;
import java.sql.SQLException;

import com.ypacarai.cooperativa.activos.config.DatabaseConfigComplete;
import com.ypacarai.cooperativa.activos.service.ConfiguracionService;

/**
 * Utilidad para limpiar configuraciones obsoletas del sistema
 * Ejecutar este archivo para eliminar configuraciones que ya no se utilizan
 */
public class LimpiarConfiguracionesObsoletas {
    
    public static void main(String[] args) {
        System.out.println("=====================================");
        System.out.println("   LIMPIEZA DE CONFIGURACIONES"); 
        System.out.println("   OBSOLETAS DEL SISTEMA");
        System.out.println("=====================================");
        
        try {
            // Verificar conexión a la base de datos
            try (Connection conn = DatabaseConfigComplete.getConnection()) {
                System.out.println("✅ Conexión a la base de datos establecida correctamente");
            }
            
            // Ejecutar limpieza
            ConfiguracionService configuracionService = new ConfiguracionService();
            boolean resultado = configuracionService.limpiarConfiguracionesObsoletas();
            
            if (resultado) {
                System.out.println("\n=====================================");
                System.out.println("✅ LIMPIEZA COMPLETADA EXITOSAMENTE");
                System.out.println("=====================================");
                System.out.println("\nConfiguraciones eliminadas:");
                System.out.println("• horarios.inicio_laboral");
                System.out.println("• horarios.fin_laboral"); 
                System.out.println("• horarios.sabado_laboral");
                System.out.println("• alertas.color_critica");
                System.out.println("• alertas.color_advertencia");
                System.out.println("• sistema.descripcion");
                System.out.println("• sistema.logo_path");
                System.out.println("\nEstas configuraciones ya no aparecerán en el");
                System.out.println("filtro por categorías del panel de configuración.");
            } else {
                System.out.println("\n❌ ERROR: No se pudo completar la limpieza");
                System.out.println("Revise los logs para más detalles.");
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error de conexión a la base de datos:");
            System.err.println(e.getMessage());
        } catch (Exception e) {
            System.err.println("❌ Error durante la limpieza:");
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("\nPresione Enter para salir...");
        try {
            System.in.read();
        } catch (Exception e) {
            // Ignorar
        }
    }
}