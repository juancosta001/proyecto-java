package com.ypacarai.cooperativa.activos;

import com.ypacarai.cooperativa.activos.service.MantenimientoTercerizadoService;
import com.ypacarai.cooperativa.activos.model.MantenimientoTercerizado;
import java.util.List;

public class TestVisualizacionActivos {
    public static void main(String[] args) {
        try {
            System.out.println("=== TEST: Visualización de Datos de Activos ===");
            
            MantenimientoTercerizadoService service = new MantenimientoTercerizadoService();
            
            // Obtener todos los mantenimientos
            List<MantenimientoTercerizado> mantenimientos = service.obtenerTodosMantenimientos();
            
            System.out.println("\n📋 Mantenimientos encontrados: " + mantenimientos.size());
            
            for (MantenimientoTercerizado mant : mantenimientos) {
                System.out.println("\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
                System.out.println("🔧 Mantenimiento ID: " + mant.getMantTercId());
                
                // Verificar datos del activo
                System.out.println("📦 Activo ID: " + mant.getActivoId());
                System.out.println("📦 Número Activo: " + (mant.getNumeroActivo() != null ? mant.getNumeroActivo() : "❌ NULL"));
                System.out.println("📦 Marca: " + (mant.getMarcaActivo() != null ? mant.getMarcaActivo() : "❌ NULL"));
                System.out.println("📦 Modelo: " + (mant.getModeloActivo() != null ? mant.getModeloActivo() : "❌ NULL"));
                
                // Verificar datos del usuario
                System.out.println("👤 Usuario ID: " + mant.getRegistradoPor());
                System.out.println("👤 Nombre Registrador: " + (mant.getNombreRegistrador() != null ? mant.getNombreRegistrador() : "❌ NULL"));
                
                // Verificar montos
                System.out.println("💰 Monto Presupuestado: " + mant.getMontoPresupuestado());
                System.out.println("💰 Monto Cobrado: " + mant.getMontoCobrado());
                System.out.println("💰 Monto a Pagar: " + mant.getMontoAPagar());
                
                // Verificar proveedor
                System.out.println("🏢 Proveedor: " + (mant.getNombreProveedor() != null ? mant.getNombreProveedor() : "❌ NULL"));
                
                System.out.println("📅 Estado: " + mant.getEstado());
            }
            
            if (mantenimientos.isEmpty()) {
                System.out.println("\n⚠️ No hay mantenimientos registrados en la base de datos.");
                System.out.println("Puedes crear uno usando el test anterior o la interfaz gráfica.");
            }
            
        } catch (Exception e) {
            System.err.println("❌ Error en el test: " + e.getMessage());
            e.printStackTrace();
        }
    }
}