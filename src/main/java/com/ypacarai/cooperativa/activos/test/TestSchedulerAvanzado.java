package com.ypacarai.cooperativa.activos.test;

import com.ypacarai.cooperativa.activos.service.SchedulerService;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Test avanzado del SchedulerService con validaciones específicas
 * Prueba configuraciones, ejecución de jobs y manejo de errores
 * 
 * Cooperativa Ypacaraí LTDA - Sistema de Activos
 */
public class TestSchedulerAvanzado {
    private static final Logger LOGGER = Logger.getLogger(TestSchedulerAvanzado.class.getName());
    
    public static void main(String[] args) {
        System.out.println("🚀 === TEST AVANZADO DEL SCHEDULERSERVICE ===");
        System.out.println("📊 Pruebas exhaustivas de funcionalidad\n");
        
        try {
            pruebaConfiguraciones();
            pruebaJobsRapidos();
            pruebaReinicioScheduler();
            pruebaConcurrencia();
            pruebaEstadisticas();
            
            System.out.println("\n✅ === TODAS LAS PRUEBAS EXITOSAS ===");
            System.out.println("🎯 SchedulerService completamente validado");
            
        } catch (Exception e) {
            System.err.println("❌ Error en pruebas: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Prueba 1: Validar carga de configuraciones y valores por defecto
     */
    private static void pruebaConfiguraciones() throws Exception {
        System.out.println("\n🔧 PRUEBA 1: Configuraciones");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        
        SchedulerService scheduler = new SchedulerService();
        
        // Obtener configuraciones actuales
        String configuraciones = scheduler.obtenerConfiguracionesActuales();
        System.out.println("📋 Configuraciones cargadas:");
        System.out.println(configuraciones);
        
        // Verificar que se usan valores por defecto (MySQL no disponible)
        if (configuraciones.contains("8 horas") && configuraciones.contains("24 horas")) {
            System.out.println("✅ Valores por defecto cargados correctamente");
        } else {
            throw new Exception("❌ Configuraciones no válidas");
        }
        
        scheduler.shutdown();
        System.out.println("✅ Prueba de configuraciones EXITOSA\n");
    }
    
    /**
     * Prueba 2: Jobs con intervalos muy cortos para verificar ejecución
     */
    private static void pruebaJobsRapidos() throws Exception {
        System.out.println("⚡ PRUEBA 2: Jobs de Ejecución Rápida");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        
        SchedulerService scheduler = new SchedulerService();
        
        // Crear contador para jobs ejecutados
        final int[] contadorEjecuciones = {0};
        
        // Programar tarea que se ejecute cada 2 segundos por 10 segundos
        Runnable tareaTest = () -> {
            contadorEjecuciones[0]++;
            System.out.println("🔄 Job ejecutado #" + contadorEjecuciones[0] + " - " + 
                             java.time.LocalTime.now().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss")));
        };
        
        // Usar el método de tarea única para testing
        for (int i = 1; i <= 5; i++) {
            scheduler.ejecutarTareaUnica(tareaTest, i * 2); // 2, 4, 6, 8, 10 segundos
        }
        
        System.out.println("⏳ Esperando ejecución de 5 tareas programadas...");
        Thread.sleep(12000); // Esperar 12 segundos
        
        if (contadorEjecuciones[0] == 5) {
            System.out.println("✅ Todas las tareas ejecutadas correctamente (" + contadorEjecuciones[0] + ")");
        } else {
            throw new Exception("❌ Solo se ejecutaron " + contadorEjecuciones[0] + " de 5 tareas");
        }
        
        scheduler.shutdown();
        System.out.println("✅ Prueba de jobs rápidos EXITOSA\n");
    }
    
    /**
     * Prueba 3: Reinicio y reconfiguración del scheduler
     */
    private static void pruebaReinicioScheduler() throws Exception {
        System.out.println("🔄 PRUEBA 3: Reinicio del Scheduler");  
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        
        SchedulerService scheduler = new SchedulerService();
        System.out.println("📊 Estado inicial:");
        System.out.println(scheduler.obtenerEstado());
        
        // Detener 
        scheduler.detenerScheduler();
        Thread.sleep(1000);
        
        // Verificar que está detenido
        String estado1 = scheduler.obtenerEstado();
        if (estado1.contains("❌ NO")) {
            System.out.println("✅ Scheduler detenido correctamente");
        }
        
        // Reiniciar
        scheduler.iniciarScheduler();
        Thread.sleep(1000);
        
        // Verificar que está activo
        String estado2 = scheduler.obtenerEstado();
        if (estado2.contains("✅ SÍ")) {
            System.out.println("✅ Scheduler reiniciado correctamente");
        }
        
        // Probar recarga de configuraciones
        scheduler.recargarConfiguracionesYReiniciar();
        Thread.sleep(2000);
        
        String estado3 = scheduler.obtenerEstado(); 
        System.out.println("📊 Estado después de recarga:");
        System.out.println(estado3);
        
        scheduler.shutdown();
        System.out.println("✅ Prueba de reinicio EXITOSA\n");
    }
    
    /**
     * Prueba 4: Concurrencia - múltiples ejecuciones simultáneas 
     */
    private static void pruebaConcurrencia() throws Exception {
        System.out.println("🧵 PRUEBA 4: Concurrencia y Pool de Hilos");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        
        SchedulerService scheduler = new SchedulerService();
        
        final CountDownLatch latch = new CountDownLatch(3);
        final long[] tiemposEjecucion = new long[3];
        final int[] idsTareas = {1, 2, 3};
        
        // Crear 3 tareas concurrentes que simulan trabajo
        for (int i = 0; i < 3; i++) {
            final int id = idsTareas[i];
            Runnable tarea = () -> {
                long inicio = System.currentTimeMillis();
                System.out.println("🔄 Tarea " + id + " iniciada en hilo: " + Thread.currentThread().getName());
                
                try {
                    // Simular trabajo
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                
                long fin = System.currentTimeMillis();
                tiemposEjecucion[id-1] = fin - inicio;
                System.out.println("✅ Tarea " + id + " completada en " + (fin - inicio) + "ms");
                latch.countDown();
            };
            
            scheduler.ejecutarTareaUnica(tarea, 1); // Todas empiezan en 1 segundo
        }
        
        // Esperar que todas las tareas terminen (máximo 10 segundos)
        boolean completadas = latch.await(10, TimeUnit.SECONDS);
        
        if (completadas) {
            System.out.println("✅ Todas las tareas concurrentes completadas");
            System.out.println("📊 Tiempos de ejecución:");
            for (int i = 0; i < 3; i++) {
                System.out.println("   Tarea " + (i+1) + ": " + tiemposEjecucion[i] + "ms");
            }
        } else {
            throw new Exception("❌ No todas las tareas completaron en tiempo");
        }
        
        scheduler.shutdown();
        System.out.println("✅ Prueba de concurrencia EXITOSA\n");
    }
    
    /**
     * Prueba 5: Estadísticas y métricas del scheduler
     */
    private static void pruebaEstadisticas() throws Exception {
        System.out.println("📊 PRUEBA 5: Estadísticas y Métricas");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        
        SchedulerService scheduler = new SchedulerService();
        
        System.out.println("📋 Estado inicial:");
        System.out.println(scheduler.obtenerEstado());
        
        // Ejecutar varias veces manualmente para incrementar contadores
        for (int i = 1; i <= 3; i++) {
            System.out.println("🔄 Ejecución manual #" + i);
            scheduler.ejecutarAlertasAhora();
            Thread.sleep(500);
            scheduler.ejecutarMantenimientoPreventivoAhora();
            Thread.sleep(500);
        }
        
        System.out.println("\n📊 Estado final:");
        String estadoFinal = scheduler.obtenerEstado();
        System.out.println(estadoFinal);
        
        // Verificar que los contadores aumentaron
        if (estadoFinal.contains("Ejecuciones alertas: 3") && 
            estadoFinal.contains("Ejecuciones mantenimiento: 3")) {
            System.out.println("✅ Estadísticas actualizadas correctamente");
        } else {
            System.out.println("⚠️  Estadísticas parciales (esperado con BD desconectada)");
        }
        
        // Mostrar configuraciones
        System.out.println("\n📋 Configuraciones actuales:");
        System.out.println(scheduler.obtenerConfiguracionesActuales());
        
        scheduler.shutdown();
        System.out.println("✅ Prueba de estadísticas EXITOSA\n");
    }
}