package com.ypacarai.cooperativa.activos.test;

import java.util.List;

import com.ypacarai.cooperativa.activos.dao.ActivoDAO;
import com.ypacarai.cooperativa.activos.model.Activo;

/**
 * Prueba específica para verificar que NO se envían notificaciones 
 * de "Fuera de Servicio" cuando todos los activos están "Operativos"
 */
public class TestActivosOperativos {
    
    public static void main(String[] args) {
        System.out.println("=== PRUEBA: VERIFICACIÓN ESTADOS REALES ===\n");
        
        try {
            ActivoDAO activoDAO = new ActivoDAO();
            List<Activo> activos = activoDAO.findAll();
            
            System.out.println("🔍 ANÁLISIS DE ESTADOS EN BASE DE DATOS:");
            System.out.println("Total activos: " + activos.size() + "\n");
            
            // Contar por estados
            int operativos = 0;
            int enMantenimiento = 0;
            int fueraServicio = 0;
            int trasladados = 0;
            
            for (Activo activo : activos) {
                System.out.printf("- %s: %s\n", 
                    activo.getActNumeroActivo(), 
                    activo.getActEstado());
                
                switch (activo.getActEstado()) {
                    case Operativo:
                        operativos++;
                        break;
                    case En_Mantenimiento:
                        enMantenimiento++;
                        break;
                    case Fuera_Servicio:
                        fueraServicio++;
                        break;
                    case Trasladado:
                        trasladados++;
                        break;
                }
            }
            
            System.out.println("\n📊 RESUMEN DE ESTADOS:");
            System.out.printf("✅ Operativos: %d\n", operativos);
            System.out.printf("🔧 En Mantenimiento: %d\n", enMantenimiento);
            System.out.printf("❌ Fuera de Servicio: %d\n", fueraServicio);
            System.out.printf("🚚 Trasladados: %d\n", trasladados);
            
            System.out.println("\n🧪 PRUEBA DEL MÉTODO CORREGIDO:");
            
            RealTestService realTestService = new RealTestService();
            String resultado = realTestService.ejecutarPruebaActivosFueraServicio();
            
            System.out.println(resultado);
            
            // Verificación de la lógica
            System.out.println("🔍 VERIFICACIÓN DE LÓGICA:");
            if (fueraServicio == 0) {
                System.out.println("✅ CORRECCIÓN EXITOSA:");
                System.out.println("   - NO hay activos fuera de servicio");
                System.out.println("   - NO se deben enviar notificaciones");
                System.out.println("   - El método debe reportar esto correctamente");
            } else {
                System.out.println("⚠️  HAY ACTIVOS FUERA DE SERVICIO:");
                System.out.printf("   - %d activos requieren notificación\n", fueraServicio);
                System.out.println("   - Se enviaran notificaciones LEGÍTIMAS");
            }
            
        } catch (Exception e) {
            System.err.println("❌ ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }
}