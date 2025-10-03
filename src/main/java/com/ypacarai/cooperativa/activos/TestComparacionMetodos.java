package com.ypacarai.cooperativa.activos;

import com.ypacarai.cooperativa.activos.service.MantenimientoTercerizadoService;
import com.ypacarai.cooperativa.activos.model.MantenimientoTercerizado;
import java.util.List;

public class TestComparacionMetodos {
    public static void main(String[] args) {
        try {
            System.out.println("=== TEST: Comparación findAll vs findById ===");
            
            MantenimientoTercerizadoService service = new MantenimientoTercerizadoService();
            
            // 1. Obtener con findAll
            System.out.println("\n1️⃣ USANDO obtenerTodosMantenimientos() (findAll):");
            List<MantenimientoTercerizado> todosMantenimientos = service.obtenerTodosMantenimientos();
            
            // Buscar el ID 14 en la lista
            MantenimientoTercerizado mant14FromList = null;
            for (MantenimientoTercerizado mant : todosMantenimientos) {
                if (mant.getMantTercId() == 14) {
                    mant14FromList = mant;
                    break;
                }
            }
            
            if (mant14FromList != null) {
                System.out.println("ID 14 encontrado en la lista:");
                System.out.println("- Número Activo: '" + mant14FromList.getNumeroActivo() + "'");
                System.out.println("- Marca Activo: '" + mant14FromList.getMarcaActivo() + "'");
                System.out.println("- Modelo Activo: '" + mant14FromList.getModeloActivo() + "'");
                System.out.println("- Nombre Registrador: '" + mant14FromList.getNombreRegistrador() + "'");
            } else {
                System.out.println("❌ ID 14 NO encontrado en la lista");
            }
            
            // 2. Obtener con findById
            System.out.println("\n2️⃣ USANDO obtenerPorId(14) (findById):");
            MantenimientoTercerizado mant14FromId = service.obtenerPorId(14);
            
            if (mant14FromId != null) {
                System.out.println("ID 14 obtenido directamente:");
                System.out.println("- Número Activo: '" + mant14FromId.getNumeroActivo() + "'");
                System.out.println("- Marca Activo: '" + mant14FromId.getMarcaActivo() + "'");
                System.out.println("- Modelo Activo: '" + mant14FromId.getModeloActivo() + "'");
                System.out.println("- Nombre Registrador: '" + mant14FromId.getNombreRegistrador() + "'");
            } else {
                System.out.println("❌ ID 14 NO encontrado con findById");
            }
            
            // 3. Comparación
            System.out.println("\n3️⃣ COMPARACIÓN:");
            if (mant14FromList != null && mant14FromId != null) {
                boolean activoIgual = equalOrNull(mant14FromList.getNumeroActivo(), mant14FromId.getNumeroActivo());
                boolean marcaIgual = equalOrNull(mant14FromList.getMarcaActivo(), mant14FromId.getMarcaActivo());
                boolean usuarioIgual = equalOrNull(mant14FromList.getNombreRegistrador(), mant14FromId.getNombreRegistrador());
                
                System.out.println("Número Activo igual: " + (activoIgual ? "✅" : "❌"));
                System.out.println("Marca Activo igual: " + (marcaIgual ? "✅" : "❌"));
                System.out.println("Nombre Usuario igual: " + (usuarioIgual ? "✅" : "❌"));
                
                if (!activoIgual || !marcaIgual || !usuarioIgual) {
                    System.out.println("\n🔍 DIFERENCIAS DETECTADAS - findAll tiene problemas");
                } else {
                    System.out.println("\n✅ AMBOS MÉTODOS DEVUELVEN DATOS IGUALES");
                }
            }
            
        } catch (Exception e) {
            System.err.println("❌ Error en el test: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static boolean equalOrNull(String a, String b) {
        if (a == null && b == null) return true;
        if (a == null || b == null) return false;
        return a.equals(b);
    }
}