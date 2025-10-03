package com.ypacarai.cooperativa.activos;

import com.ypacarai.cooperativa.activos.service.MantenimientoTercerizadoService;
import com.ypacarai.cooperativa.activos.model.MantenimientoTercerizado;
import java.math.BigDecimal;
import java.time.LocalDate;

public class TestRetiroEntregaCompleto {
    public static void main(String[] args) {
        try {
            System.out.println("=== TEST: Retiro y Entrega de Mantenimiento ===");
            
            MantenimientoTercerizadoService service = new MantenimientoTercerizadoService();
            
            // 1. Crear una solicitud de mantenimiento con activo y monto
            System.out.println("\n1. Creando solicitud de mantenimiento...");
            int mantId = service.solicitarMantenimiento(
                1, // activoId
                1, // proveedorId  
                "Test de retiro/entrega - equipo dañado",
                "Operativo", // estado antes
                new BigDecimal("150000"), // monto presupuestado
                1 // registradoPor
            );
            
            if (mantId > 0) {
                System.out.println("✅ Solicitud creada exitosamente. ID: " + mantId);
                
                // Verificar que se guardó con activo y monto
                MantenimientoTercerizado mant = service.obtenerPorId(mantId);
                System.out.println("- Activo ID guardado: " + mant.getActivoId());
                System.out.println("- Monto guardado: " + mant.getMontoPresupuestado());
                
                // 2. Registrar retiro
                System.out.println("\n2. Registrando retiro...");
                boolean retiroOk = service.registrarRetiroEquipo(
                    mantId,
                    LocalDate.now(),
                    "Equipo retirado para reparación por técnico especializado"
                );
                
                if (retiroOk) {
                    System.out.println("✅ Retiro registrado exitosamente");
                    
                    // Verificar estado después del retiro
                    mant = service.obtenerPorId(mantId);
                    System.out.println("- Estado después del retiro: " + mant.getEstado());
                    System.out.println("- Fecha retiro: " + mant.getFechaRetiro());
                    
                    // 3. Registrar entrega
                    System.out.println("\n3. Registrando entrega...");
                    boolean entregaOk = service.registrarEntregaEquipo(
                        mantId,
                        LocalDate.now(),
                        "Reparación de placa madre y actualización de software",
                        "Equipo funcionando correctamente",
                        new BigDecimal("125000"),
                        30,
                        LocalDate.now().plusDays(30),
                        "Pendiente"
                    );
                    
                    if (entregaOk) {
                        System.out.println("✅ Entrega registrada exitosamente");
                        
                        // Verificar estado final
                        mant = service.obtenerPorId(mantId);
                        System.out.println("- Estado final: " + mant.getEstado());
                        System.out.println("- Fecha entrega: " + mant.getFechaEntrega());
                        System.out.println("- Costo final: " + mant.getMontoCobrado());
                        System.out.println("- Garantía: " + mant.getDiasGarantia() + " días");
                        
                        System.out.println("\n🎉 TODAS LAS FUNCIONES FUNCIONAN CORRECTAMENTE!");
                    } else {
                        System.out.println("❌ Error registrando entrega");
                    }
                } else {
                    System.out.println("❌ Error registrando retiro");
                }
            } else {
                System.out.println("❌ Error creando solicitud");
            }
            
        } catch (Exception e) {
            System.err.println("❌ Error en el test: " + e.getMessage());
            e.printStackTrace();
        }
    }
}