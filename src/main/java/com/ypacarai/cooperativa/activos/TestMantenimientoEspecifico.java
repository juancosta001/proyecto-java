package com.ypacarai.cooperativa.activos;

import com.ypacarai.cooperativa.activos.service.MantenimientoTercerizadoService;
import com.ypacarai.cooperativa.activos.model.MantenimientoTercerizado;

public class TestMantenimientoEspecifico {
    public static void main(String[] args) {
        try {
            System.out.println("=== TEST: Mantenimiento Específico ID 14 ===");
            
            MantenimientoTercerizadoService service = new MantenimientoTercerizadoService();
            
            // Obtener el mantenimiento ID 14 (el que se ve en la captura)
            MantenimientoTercerizado mant = service.obtenerPorId(14);
            
            if (mant != null) {
                System.out.println("\n🔧 Mantenimiento encontrado:");
                System.out.println("ID: " + mant.getMantTercId());
                System.out.println("Estado: " + mant.getEstado());
                
                System.out.println("\n📦 DATOS DEL ACTIVO:");
                System.out.println("Activo ID: " + mant.getActivoId());
                System.out.println("Número Activo: '" + mant.getNumeroActivo() + "'");
                System.out.println("Marca Activo: '" + mant.getMarcaActivo() + "'");
                System.out.println("Modelo Activo: '" + mant.getModeloActivo() + "'");
                
                System.out.println("\n👤 DATOS DEL USUARIO:");
                System.out.println("Usuario ID: " + mant.getRegistradoPor());
                System.out.println("Nombre Registrador: '" + mant.getNombreRegistrador() + "'");
                
                System.out.println("\n🏢 DATOS DEL PROVEEDOR:");
                System.out.println("Proveedor ID: " + mant.getProveedorId());
                System.out.println("Nombre Proveedor: '" + mant.getNombreProveedor() + "'");
                
                System.out.println("\n💰 DATOS DEL MONTO:");
                System.out.println("Monto Presupuestado: " + mant.getMontoPresupuestado());
                System.out.println("Monto Cobrado: " + mant.getMontoCobrado());
                System.out.println("Monto a Pagar: " + mant.getMontoAPagar());
                
                // Verificar si los campos están null o vacíos
                boolean activoOk = mant.getNumeroActivo() != null && !mant.getNumeroActivo().trim().isEmpty();
                boolean marcaOk = mant.getMarcaActivo() != null && !mant.getMarcaActivo().trim().isEmpty();
                boolean usuarioOk = mant.getNombreRegistrador() != null && !mant.getNombreRegistrador().trim().isEmpty();
                
                System.out.println("\n✅ VERIFICACIÓN:");
                System.out.println("Número Activo está bien: " + (activoOk ? "✅ SÍ" : "❌ NO"));
                System.out.println("Marca Activo está bien: " + (marcaOk ? "✅ SÍ" : "❌ NO"));
                System.out.println("Nombre Usuario está bien: " + (usuarioOk ? "✅ SÍ" : "❌ NO"));
                
            } else {
                System.out.println("❌ No se encontró el mantenimiento ID 14");
            }
            
        } catch (Exception e) {
            System.err.println("❌ Error en el test: " + e.getMessage());
            e.printStackTrace();
        }
    }
}