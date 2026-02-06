package com.ypacarai.cooperativa.activos.test;

import java.util.List;
import java.util.Scanner;

import com.ypacarai.cooperativa.activos.model.Ticket;
import com.ypacarai.cooperativa.activos.service.SchedulerService;
import com.ypacarai.cooperativa.activos.service.TicketService;

/**
 * Test completo del SchedulerService con generación automática de tickets
 * Cooperativa Ypacaraí LTDA
 */
public class TestSchedulerConTickets {
    
    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════════════════════════╗");
        System.out.println("║    TEST SCHEDULER CON GENERACIÓN AUTOMÁTICA DE TICKETS        ║");
        System.out.println("║    Sistema de Gestión de Activos - Cooperativa Ypacaraí       ║");
        System.out.println("╚════════════════════════════════════════════════════════════════╝");
        System.out.println();
        
        SchedulerService scheduler = null;
        TicketService ticketService = null;
        Scanner scanner = new Scanner(System.in);
        
        try {
            // Inicializar TicketService
            ticketService = new TicketService();
            
            // 1. Mostrar estado inicial de tickets
            System.out.println("📊 ESTADO INICIAL DEL SISTEMA");
            System.out.println("═".repeat(60));
            mostrarEstadoTickets(ticketService);
            System.out.println();
            
            // 2. Inicializar scheduler
            System.out.println("🔧 INICIALIZANDO SCHEDULER...");
            System.out.println("═".repeat(60));
            scheduler = new SchedulerService();
            System.out.println();
            
            // 3. Mostrar configuraciones
            System.out.println("⚙️  CONFIGURACIONES ACTUALES");
            System.out.println("═".repeat(60));
            System.out.println(scheduler.obtenerConfiguracionesActuales());
            System.out.println();
            
            // 4. Mostrar estado del scheduler
            System.out.println("📈 ESTADO DEL SCHEDULER");
            System.out.println("═".repeat(60));
            System.out.println(scheduler.getEstadoScheduler());
            System.out.println();
            
            // 5. Menú de pruebas
            boolean continuar = true;
            while (continuar) {
                System.out.println("\n╔════════════════════════════════════════════════════════════════╗");
                System.out.println("║                    MENÚ DE PRUEBAS                             ║");
                System.out.println("╚════════════════════════════════════════════════════════════════╝");
                System.out.println("1. 🔔 Ejecutar proceso de alertas manualmente");
                System.out.println("2. 🔧 Ejecutar proceso de mantenimiento preventivo");
                System.out.println("3. 🎫 Ejecutar generación de tickets preventivos");
                System.out.println("4. 📊 Ver estado actual del scheduler");
                System.out.println("5. 📋 Ver tickets generados");
                System.out.println("6. ⏸️  Detener scheduler");
                System.out.println("7. ▶️  Iniciar scheduler");
                System.out.println("8. 🔄 Recargar configuraciones y reiniciar");
                System.out.println("9. ❌ Salir");
                System.out.println();
                System.out.print("Seleccione una opción: ");
                
                int opcion = -1;
                try {
                    opcion = Integer.parseInt(scanner.nextLine().trim());
                } catch (NumberFormatException e) {
                    System.out.println("❌ Opción inválida");
                    continue;
                }
                
                System.out.println();
                
                switch (opcion) {
                    case 1:
                        System.out.println("🔔 Ejecutando proceso de alertas...");
                        System.out.println("─".repeat(60));
                        scheduler.ejecutarAlertasAhora();
                        System.out.println("✅ Proceso de alertas completado");
                        break;
                        
                    case 2:
                        System.out.println("🔧 Ejecutando proceso de mantenimiento preventivo...");
                        System.out.println("─".repeat(60));
                        scheduler.ejecutarMantenimientoPreventivoAhora();
                        System.out.println("✅ Proceso de mantenimiento completado");
                        break;
                        
                    case 3:
                        System.out.println("🎫 Ejecutando generación de tickets preventivos...");
                        System.out.println("─".repeat(60));
                        int ticketsAntes = ticketService.obtenerTodosLosTickets().size();
                        System.out.println("📊 Tickets antes: " + ticketsAntes);
                        
                        scheduler.ejecutarTicketsPreventivosAhora();
                        
                        Thread.sleep(2000); // Esperar a que termine
                        
                        int ticketsDespues = ticketService.obtenerTodosLosTickets().size();
                        System.out.println("📊 Tickets después: " + ticketsDespues);
                        System.out.println("✅ Tickets generados: " + (ticketsDespues - ticketsAntes));
                        mostrarEstadoTickets(ticketService);
                        break;
                        
                    case 4:
                        System.out.println("📈 ESTADO ACTUAL DEL SCHEDULER");
                        System.out.println("─".repeat(60));
                        System.out.println(scheduler.getEstadoScheduler());
                        System.out.println();
                        System.out.println("📊 ESTADÍSTICAS DETALLADAS:");
                        System.out.println("  • Ejecuciones de alertas: " + scheduler.getEjecucionesAlertas());
                        System.out.println("  • Ejecuciones de mantenimiento: " + scheduler.getEjecucionesMantenimiento());
                        System.out.println("  • Ejecuciones de tickets: " + scheduler.getEjecucionesTickets());
                        System.out.println("  • Última ejecución de alertas: " + 
                            (scheduler.getUltimaEjecucionAlertas() != null ? 
                             scheduler.getUltimaEjecucionAlertas() : "Ninguna"));
                        System.out.println("  • Última ejecución de mantenimiento: " + 
                            (scheduler.getUltimaEjecucionMantenimiento() != null ? 
                             scheduler.getUltimaEjecucionMantenimiento() : "Ninguna"));
                        System.out.println("  • Última ejecución de tickets: " + 
                            (scheduler.getUltimaEjecucionTickets() != null ? 
                             scheduler.getUltimaEjecucionTickets() : "Ninguna"));
                        break;
                        
                    case 5:
                        System.out.println("📋 TICKETS EN EL SISTEMA");
                        System.out.println("─".repeat(60));
                        mostrarEstadoTickets(ticketService);
                        break;
                        
                    case 6:
                        System.out.println("⏸️  Deteniendo scheduler...");
                        scheduler.detenerScheduler();
                        System.out.println("✅ Scheduler detenido");
                        break;
                        
                    case 7:
                        System.out.println("▶️  Iniciando scheduler...");
                        scheduler.iniciarScheduler();
                        System.out.println("✅ Scheduler iniciado");
                        break;
                        
                    case 8:
                        System.out.println("🔄 Recargando configuraciones...");
                        scheduler.recargarConfiguracionesYReiniciar();
                        System.out.println("✅ Configuraciones recargadas");
                        System.out.println("\n📊 NUEVAS CONFIGURACIONES:");
                        System.out.println(scheduler.obtenerConfiguracionesActuales());
                        break;
                        
                    case 9:
                        System.out.println("❌ Saliendo del test...");
                        continuar = false;
                        break;
                        
                    default:
                        System.out.println("❌ Opción no válida. Por favor intente nuevamente.");
                }
                
                if (continuar) {
                    System.out.println("\nPresione ENTER para continuar...");
                    scanner.nextLine();
                }
            }
            
        } catch (Exception e) {
            System.err.println("\n❌ ERROR DURANTE LA PRUEBA:");
            System.err.println("═".repeat(60));
            e.printStackTrace();
            
        } finally {
            // Limpiar recursos
            if (scheduler != null) {
                System.out.println("\n🔧 Cerrando scheduler...");
                scheduler.shutdown();
                System.out.println("✅ Scheduler cerrado");
            }
            scanner.close();
            
            System.out.println("\n╔════════════════════════════════════════════════════════════════╗");
            System.out.println("║                    TEST FINALIZADO                             ║");
            System.out.println("╚════════════════════════════════════════════════════════════════╝");
        }
    }
    
    private static void mostrarEstadoTickets(TicketService ticketService) {
        try {
            List<Ticket> tickets = ticketService.obtenerTodosLosTickets();
            
            System.out.println("📊 Total de tickets: " + tickets.size());
            System.out.println();
            
            // Contar por tipo
            long preventivos = tickets.stream()
                .filter(t -> t.getTickTipo() == Ticket.Tipo.Preventivo)
                .count();
            long correctivos = tickets.stream()
                .filter(t -> t.getTickTipo() == Ticket.Tipo.Correctivo)
                .count();
            
            System.out.println("📋 Por tipo:");
            System.out.println("   • Preventivo: " + preventivos);
            System.out.println("   • Correctivo: " + correctivos);
            System.out.println();
            
            // Contar por estado
            long abiertos = tickets.stream()
                .filter(t -> t.getTickEstado() == Ticket.Estado.Abierto)
                .count();
            long enProceso = tickets.stream()
                .filter(t -> t.getTickEstado() == Ticket.Estado.En_Proceso)
                .count();
            long resueltos = tickets.stream()
                .filter(t -> t.getTickEstado() == Ticket.Estado.Resuelto)
                .count();
            long cerrados = tickets.stream()
                .filter(t -> t.getTickEstado() == Ticket.Estado.Cerrado)
                .count();
            
            System.out.println("📊 Por estado:");
            System.out.println("   • Abierto: " + abiertos);
            System.out.println("   • En Proceso: " + enProceso);
            System.out.println("   • Resuelto: " + resueltos);
            System.out.println("   • Cerrado: " + cerrados);
            System.out.println();
            
            // Contar por prioridad
            long criticos = tickets.stream()
                .filter(t -> t.getTickPrioridad() == Ticket.Prioridad.Critica)
                .count();
            long altos = tickets.stream()
                .filter(t -> t.getTickPrioridad() == Ticket.Prioridad.Alta)
                .count();
            
            System.out.println("⚠️  Por prioridad:");
            System.out.println("   • Crítica: " + criticos);
            System.out.println("   • Alta: " + altos);
            
        } catch (Exception e) {
            System.err.println("❌ Error obteniendo estado de tickets: " + e.getMessage());
        }
    }
}
