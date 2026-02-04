package com.ypacarai.cooperativa.activos.test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.ypacarai.cooperativa.activos.dao.ActivoDAO;
import com.ypacarai.cooperativa.activos.dao.TicketDAO;
import com.ypacarai.cooperativa.activos.model.Activo;
import com.ypacarai.cooperativa.activos.model.Ticket;
import com.ypacarai.cooperativa.activos.service.NotificationService;

/**
 * Servicio para ejecutar pruebas reales del sistema de notificaciones
 * Integra con base de datos real y envía emails de prueba
 */
public class RealTestService {
    
    private final ActivoDAO activoDAO;
    private final TicketDAO ticketDAO;
    private final NotificationService notificationService;
    
    public RealTestService() {
        this.activoDAO = new ActivoDAO();
        this.ticketDAO = new TicketDAO();
        this.notificationService = new NotificationService();
    }
    
    /**
     * Ejecuta pruebas con activos reales del sistema
     */
    public String ejecutarPruebaActivosReales() {
        StringBuilder resultado = new StringBuilder();
        resultado.append("=== PRUEBA: ACTIVOS REALES ===\n\n");
        
        try {
            List<Activo> activos = activoDAO.findAll();
            
            if (activos.isEmpty()) {
                resultado.append("❌ No se encontraron activos en el sistema\n");
                return resultado.toString();
            }
            
            resultado.append(String.format("🔍 Activos encontrados: %d\n", activos.size()));
            resultado.append("Enviando notificaciones preventivas...\n\n");
            
            int totalEnviados = 0;
            int totalErrores = 0;
            
            for (int i = 0; i < activos.size(); i++) {
                Activo activo = activos.get(i);
                
                resultado.append(String.format("%d. Activo: %s (%s)\n", 
                    (i + 1), activo.getActNumeroActivo(), activo.getTipoActivoNombre() != null ? activo.getTipoActivoNombre() : "Tipo N/A"));
                resultado.append(String.format("   Estado: %s | Ubicación: %s\n", 
                    activo.getActEstado(), activo.getUbicacionNombre() != null ? activo.getUbicacionNombre() : "Ubicación N/A"));
                
                // Variar días restantes para diversidad
                int diasRestantes = 1 + (i % 15); // Entre 1 y 15 días
                resultado.append(String.format("   Días para mantenimiento: %d\n", diasRestantes));
                
                boolean enviado = notificationService.notificarMantenimientoPreventivo(activo, diasRestantes);
                
                if (enviado) {
                    resultado.append("   Resultado: ✅ NOTIFICACIÓN ENVIADA\n");
                    totalEnviados++;
                } else {
                    resultado.append("   Resultado: ❌ ERROR AL ENVIAR\n");
                    totalErrores++;
                }
                
                resultado.append("\n");
                Thread.sleep(300); // Pausa entre envíos
            }
            
            resultado.append(String.format("=== RESUMEN NOTIFICACIONES ACTIVOS ===\n"));
            resultado.append(String.format("Total procesados: %d\n", activos.size()));
            resultado.append(String.format("Enviadas exitosas: %d\n", totalEnviados));
            resultado.append(String.format("Errores: %d\n", totalErrores));
            resultado.append(String.format("Tasa de éxito: %.1f%%\n", (totalEnviados * 100.0) / activos.size()));
            
        } catch (Exception e) {
            resultado.append(String.format("❌ ERROR: %s\n", e.getMessage()));
        }
        
        return resultado.toString();
    }
    
    /**
     * Ejecuta pruebas con tickets reales del sistema
     */
    public String ejecutarPruebaTicketsReales() {
        StringBuilder resultado = new StringBuilder();
        resultado.append("=== PRUEBA: TICKETS REALES ===\n\n");
        
        try {
            List<Ticket> tickets = ticketDAO.obtenerTodos();
            
            if (tickets.isEmpty()) {
                resultado.append("❌ No se encontraron tickets en el sistema\n");
                return resultado.toString();
            }
            
            resultado.append(String.format("🔍 Tickets encontrados: %d\n", tickets.size()));
            resultado.append("Enviando notificaciones de tickets...\n\n");
            
            int totalVencidos = 0;
            int totalCompletados = 0;
            int totalErrores = 0;
            
            for (int i = 0; i < tickets.size(); i++) {
                Ticket ticket = tickets.get(i);
                
                resultado.append(String.format("%d. Ticket ID: %d | Tipo: %s\n", 
                    (i + 1), ticket.getTickId(), ticket.getTickTipo()));
                resultado.append(String.format("   Estado: %s | Prioridad: %s\n", 
                    ticket.getTickEstado(), ticket.getTickPrioridad()));
                resultado.append(String.format("   Descripción: %s\n", 
                    ticket.getTickDescripcion().substring(0, Math.min(50, ticket.getTickDescripcion().length())) + "..."));
                
                boolean enviado = false;
                boolean procesado = false; // Flag para determinar si el ticket fue procesado
                
                // Filtrar según el estado REAL del ticket
                if (ticket.getTickEstado() == Ticket.Estado.Resuelto) {
                    // SOLO tickets RESUELTOS: enviar notificación de completado
                    procesado = true;
                    if (ticket.getTickTipo() == Ticket.Tipo.Correctivo) {
                        resultado.append("Tipo notificación: ✅ MANTENIMIENTO CORRECTIVO COMPLETADO\n");
                        String[] soluciones = {
                            "Problema de hardware resuelto - componente reemplazado",
                            "Software actualizado y sistema optimizado", 
                            "Reparación de conectividad - problema de red solucionado",
                            "Limpieza profunda y calibración realizada"
                        };
                        String solucion = soluciones[i % soluciones.length];
                        enviado = notificationService.notificarMantenimientoCorrectivoCompletado(ticket, solucion);
                    } else {
                        resultado.append("Tipo notificación: ✅ MANTENIMIENTO PREVENTIVO COMPLETADO\n");
                        enviado = notificationService.notificarMantenimientoPreventoCompleto(ticket);
                    }
                    if (enviado) totalCompletados++;
                    
                } else if (ticket.getTickEstado() == Ticket.Estado.Abierto || 
                          ticket.getTickEstado() == Ticket.Estado.En_Proceso) {
                    // Tickets abiertos o en proceso: notificar como vencidos diferenciando por tipo
                    procesado = true;
                    if (ticket.getTickTipo() == Ticket.Tipo.Correctivo) {
                        resultado.append("Tipo notificación: 🚨 TICKET CORRECTIVO VENCIDO\n");
                    } else {
                        resultado.append("Tipo notificación: ⏰ TICKET PREVENTIVO VENCIDO\n");
                    }
                    enviado = notificationService.notificarTicketVencido(ticket);
                    if (enviado) totalVencidos++;
                    
                } else {
                    // Tickets cancelados, cerrados u otros estados: NO enviar notificaciones
                    procesado = false;
                    resultado.append(String.format("Tipo notificación: 🚫 OMITIDO (%s) - No requiere notificación\n", ticket.getTickEstado()));
                    enviado = false; // No enviar nada para estos estados
                }
                
                // Resultado basado en si fue procesado y enviado exitosamente
                if (!procesado) {
                    resultado.append("Resultado: ⚪ NO PROCESADO\n");
                } else if (enviado) {
                    resultado.append("Resultado: ✅ ENVIADO\n");
                } else {
                    totalErrores++;
                    resultado.append("Resultado: ❌ ERROR EN ENVÍO\n");
                }
                
                Thread.sleep(400); // Pausa corta entre tickets
                resultado.append("\n");
            }
            
            // Calcular tickets omitidos correctamente
            int ticketsOmitidos = tickets.size() - totalVencidos - totalCompletados - totalErrores;
            
            resultado.append(String.format("=== RESUMEN NOTIFICACIONES TICKETS ===\n"));
            resultado.append(String.format("Total tickets en sistema: %d\n", tickets.size()));
            resultado.append(String.format("✅ Notificaciones completados (Resueltos): %d\n", totalCompletados));
            resultado.append(String.format("🚨 Notificaciones vencidos (Abiertos/En_Proceso): %d\n", totalVencidos));
            resultado.append(String.format("⚪ Tickets omitidos (Cancelados/Cerrados): %d\n", ticketsOmitidos));
            resultado.append(String.format("❌ Errores en envío: %d\n", totalErrores));
            
            int totalProcesados = totalVencidos + totalCompletados;
            if (totalProcesados > 0) {
                resultado.append(String.format("📈 Tasa de éxito: %.1f%% (%d exitosos de %d procesados)\n", 
                    ((totalProcesados - totalErrores) * 100.0) / totalProcesados, 
                    (totalProcesados - totalErrores), totalProcesados));
            } else {
                resultado.append("📈 Tasa de éxito: N/A (no hay tickets para procesar)\n");
            }
            
        } catch (Exception e) {
            resultado.append(String.format("❌ ERROR: %s\n", e.getMessage()));
        }
        
        return resultado.toString();
    }
    
    /**
     * Ejecuta prueba de activos fuera de servicio REALES (no simulación)
     */
    public String ejecutarPruebaActivosFueraServicio() {
        StringBuilder resultado = new StringBuilder();
        resultado.append("=== PRUEBA: ACTIVOS FUERA DE SERVICIO REALES ===\n\n");
        
        try {
            List<Activo> activos = activoDAO.findAll();
            
            if (activos.isEmpty()) {
                resultado.append("❌ No hay activos disponibles para la prueba\n");
                return resultado.toString();
            }
            
            // Buscar SOLO activos que REALMENTE están fuera de servicio
            List<Activo> activosFueraServicio = activos.stream()
                .filter(a -> a.getActEstado() == Activo.Estado.Fuera_Servicio)
                .collect(java.util.stream.Collectors.toList());
            
            resultado.append(String.format("🔍 Total activos en sistema: %d\n", activos.size()));
            resultado.append(String.format("⛔ Activos fuera de servicio: %d\n\n", activosFueraServicio.size()));
            
            if (activosFueraServicio.isEmpty()) {
                resultado.append("✅ ¡EXCELENTE! No hay activos fuera de servicio\n");
                resultado.append("📧 No se enviarán notificaciones innecesarias\n");
                return resultado.toString();
            }
            
            // Procesar SOLO los activos que realmente están fuera de servicio
            int totalEnviados = 0;
            int totalErrores = 0;
            
            for (int i = 0; i < activosFueraServicio.size(); i++) {
                Activo activo = activosFueraServicio.get(i);
                String motivo = activo.getActObservaciones() != null ? 
                    activo.getActObservaciones() : "Activo marcado como fuera de servicio en el sistema";
                
                resultado.append(String.format("%d. Activo: %s\n", (i + 1), activo.getActNumeroActivo()));
                resultado.append(String.format("   Estado BD: %s\n", activo.getActEstado()));
                resultado.append(String.format("   Motivo: %s\n", motivo));
                
                boolean enviado = notificationService.notificarActivoFueraServicio(activo, motivo);
                
                if (enviado) {
                    resultado.append("   Resultado: ✅ NOTIFICACIÓN ENVIADA\n");
                    totalEnviados++;
                } else {
                    resultado.append("   Resultado: ❌ ERROR AL ENVIAR\n");
                    totalErrores++;
                }
                
                resultado.append("\n");
                Thread.sleep(500);
            }
            
            resultado.append(String.format("=== RESUMEN ACTIVOS FUERA DE SERVICIO ===\n"));
            resultado.append(String.format("Activos procesados: %d\n", activosFueraServicio.size()));
            resultado.append(String.format("Notificaciones enviadas: %d\n", totalEnviados));
            resultado.append(String.format("Errores: %d\n", totalErrores));
            resultado.append(String.format("Tasa de éxito: %.1f%%\n", (totalEnviados * 100.0) / activosFueraServicio.size()));
            
        } catch (Exception e) {
            resultado.append(String.format("❌ ERROR: %s\n", e.getMessage()));
        }
        
        return resultado.toString();
    }
    
    /**
     * Ejecuta prueba de estrés con múltiples notificaciones
     */
    public String ejecutarPruebaEstres() {
        StringBuilder resultado = new StringBuilder();
        resultado.append("=== PRUEBA DE ESTRÉS ===\n\n");
        
        int totalEnviados = 0;
        int totalErrores = 0;
        
        try {
            List<Activo> activos = activoDAO.findAll();
            
            if (activos.isEmpty()) {
                resultado.append("❌ No hay datos suficientes para prueba de estrés\n");
                return resultado.toString();
            }
            
            // Usar un número más realista basado en los datos disponibles
            int numPruebas = Math.min(20, activos.size()); // Hasta 20 notificaciones
            resultado.append(String.format("Enviando %d notificaciones en ráfaga...\n\n", numPruebas));
            
            for (int i = 0; i < numPruebas; i++) {
                Activo activo = activos.get(i % activos.size());
                
                resultado.append(String.format("%d. ", i + 1));
                
                boolean enviado;
                if (i % 3 == 0) {
                    // Mantenimiento preventivo
                    enviado = notificationService.notificarMantenimientoPreventivo(activo, 2);
                    resultado.append("Preventivo");
                } else if (i % 3 == 1) {
                    // Activo fuera de servicio
                    enviado = notificationService.notificarActivoFueraServicio(activo, "Prueba de estrés - falla simulada");
                    resultado.append("Fuera de servicio");
                } else {
                    // Email de prueba
                    enviado = notificationService.enviarEmailPrueba("stress.test@ypacarai.local");
                    resultado.append("Email prueba");
                }
                
                if (enviado) {
                    totalEnviados++;
                    resultado.append(" ✅");
                } else {
                    totalErrores++;
                    resultado.append(" ❌");
                }
                resultado.append("\n");
                
                // Pausa corta entre envíos
                Thread.sleep(500);
            }
            
            resultado.append(String.format("\n--- Resumen Prueba de Estrés ---\n"));
            resultado.append(String.format("Total enviados: %d\n", totalEnviados));
            resultado.append(String.format("Total errores: %d\n", totalErrores));
            resultado.append(String.format("Tasa éxito: %.1f%%\n", (totalEnviados * 100.0) / numPruebas));
            
        } catch (Exception e) {
            resultado.append(String.format("❌ ERROR EN PRUEBA DE ESTRÉS: %s\n", e.getMessage()));
        }
        
        return resultado.toString();
    }
    
    /**
     * Genera un resumen del estado actual del sistema
     */
    public String obtenerResumenSistema() {
        StringBuilder resumen = new StringBuilder();
        resumen.append("=== RESUMEN DEL SISTEMA ===\n\n");
        
        try {
            // Estadísticas de activos
            List<Activo> activos = activoDAO.findAll();
            resumen.append(String.format("📊 ACTIVOS TOTALES: %d\n", activos.size()));
            
            if (activos.size() > 0) {
                // Análisis detallado por estado
                long operativos = activos.stream().filter(a -> a.getActEstado() == Activo.Estado.Operativo).count();
                long enMantenimiento = activos.stream().filter(a -> a.getActEstado() == Activo.Estado.En_Mantenimiento).count();
                long fueraServicio = activos.stream().filter(a -> a.getActEstado() == Activo.Estado.Fuera_Servicio).count();
                long trasladados = activos.stream().filter(a -> a.getActEstado() == Activo.Estado.Trasladado).count();
                
                resumen.append(String.format("  ✅ Operativos: %d (%.1f%%)\n", operativos, (operativos * 100.0) / activos.size()));
                resumen.append(String.format("  🔧 En mantenimiento: %d (%.1f%%)\n", enMantenimiento, (enMantenimiento * 100.0) / activos.size()));
                resumen.append(String.format("  ❌ Fuera de servicio: %d (%.1f%%)\n", fueraServicio, (fueraServicio * 100.0) / activos.size()));
                resumen.append(String.format("  🚚 Trasladados: %d (%.1f%%)\n\n", trasladados, (trasladados * 100.0) / activos.size()));
            }
            
            // Estadísticas de tickets
            List<Ticket> tickets = ticketDAO.obtenerTodos();
            resumen.append(String.format("🎫 TICKETS TOTALES: %d\n", tickets.size()));
            
            if (tickets.size() > 0) {
                long preventivos = tickets.stream().filter(t -> t.getTickTipo() == Ticket.Tipo.Preventivo).count();
                long correctivos = tickets.stream().filter(t -> t.getTickTipo() == Ticket.Tipo.Correctivo).count();
                
                resumen.append(String.format("  🔄 Preventivos: %d (%.1f%%)\n", preventivos, (preventivos * 100.0) / tickets.size()));
                resumen.append(String.format("  🔧 Correctivos: %d (%.1f%%)\n", correctivos, (correctivos * 100.0) / tickets.size()));
            
                long abiertos = tickets.stream().filter(t -> 
                    t.getTickEstado() == Ticket.Estado.Abierto).count();
                long enProceso = tickets.stream().filter(t -> 
                    t.getTickEstado() == Ticket.Estado.En_Proceso).count();
                long resueltos = tickets.stream().filter(t -> 
                    t.getTickEstado() == Ticket.Estado.Resuelto).count();
                
                resumen.append(String.format("  📂 Abiertos: %d\n", abiertos));
                resumen.append(String.format("  ⚙️ En proceso: %d\n", enProceso));
                resumen.append(String.format("  ✅ Resueltos: %d\n\n", resueltos));
            }
            
            // Estado del servicio de email
            boolean emailFunciona = notificationService.verificarEstadoServicio();
            resumen.append(String.format("📧 EMAIL SERVICE:\n"));
            resumen.append(String.format("  Estado: %s\n", emailFunciona ? "✅ FUNCIONANDO" : "❌ CON PROBLEMAS"));
            resumen.append(String.format("  Fecha verificación: %s\n\n", 
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))));
            
            resumen.append("=== SISTEMA LISTO PARA PRUEBAS ===\n");
            
        } catch (Exception e) {
            resumen.append(String.format("❌ ERROR obteniendo resumen: %s\n", e.getMessage()));
        }
        
        return resumen.toString();
    }
    
    /**
     * Ejecuta notificaciones masivas usando TODOS los datos del sistema
     */
    public String ejecutarNotificacionesMasivas() {
        StringBuilder resultado = new StringBuilder();
        resultado.append("=== NOTIFICACIONES MASIVAS - SISTEMA COMPLETO ===\n\n");
        
        try {
            int totalNotificaciones = 0;
            int totalExitosas = 0;
            
            // 1. Notificaciones preventivas para TODOS los activos
            List<Activo> activos = activoDAO.findAll();
            resultado.append(String.format("📋 FASE 1: Notificaciones Preventivas (%d activos)\n", activos.size()));
            
            for (Activo activo : activos) {
                // Calcular días basado en el ID para variedad
                int diasRestantes = 1 + (activo.getActId() % 30); // Entre 1 y 30 días
                boolean enviado = notificationService.notificarMantenimientoPreventivo(activo, diasRestantes);
                totalNotificaciones++;
                if (enviado) totalExitosas++;
                Thread.sleep(200); // Pausa corta
            }
            
            // 2. Notificaciones de tickets para TODOS los tickets
            List<Ticket> tickets = ticketDAO.obtenerTodos();
            resultado.append(String.format("\n🎫 FASE 2: Notificaciones de Tickets (%d tickets)\n", tickets.size()));
            
            for (Ticket ticket : tickets) {
                boolean enviado = false;
                // Solo procesar tickets en estados que requieren notificación
                if (ticket.getTickEstado() == Ticket.Estado.Resuelto) {
                    // SOLO tickets RESUELTOS: notificar completado según el tipo
                    if (ticket.getTickTipo() == Ticket.Tipo.Preventivo) {
                        enviado = notificationService.notificarMantenimientoPreventoCompleto(ticket);
                    } else { // Correctivo
                        String solucion = "Trabajo completado según especificaciones técnicas. Activo restaurado a condiciones operativas.";
                        enviado = notificationService.notificarMantenimientoCorrectivoCompletado(ticket, solucion);
                    }
                    totalNotificaciones++;
                    if (enviado) totalExitosas++;
                    
                } else if (ticket.getTickEstado() == Ticket.Estado.Abierto || 
                          ticket.getTickEstado() == Ticket.Estado.En_Proceso) {
                    // Tickets abiertos o en proceso: notificar como vencidos
                    enviado = notificationService.notificarTicketVencido(ticket);
                    totalNotificaciones++;
                    if (enviado) totalExitosas++;
                }
                // Tickets cancelados, cerrados u otros estados: se omiten completamente
                
                if (enviado) {
                    Thread.sleep(250);
                }
            }
            
            // 3. Notificaciones de activos fuera de servicio (SOLO LOS REALES)
            resultado.append(String.format("\n⛔ FASE 3: Verificación de Activos Fuera de Servicio\n"));
            
            // SOLO notificar activos que REALMENTE están fuera de servicio
            int activosConProblemas = 0;
            for (Activo activo : activos) {
                if (activo.getActEstado() == Activo.Estado.Fuera_Servicio) {
                    // Este activo SÍ está fuera de servicio según la BD
                    String motivo = activo.getActObservaciones() != null ? 
                        activo.getActObservaciones() : "Activo marcado fuera de servicio";
                    boolean enviado = notificationService.notificarActivoFueraServicio(activo, motivo);
                    totalNotificaciones++;
                    if (enviado) totalExitosas++;
                    activosConProblemas++;
                    Thread.sleep(300);
                }
            }
            
            if (activosConProblemas == 0) {
                resultado.append("✅ NO hay activos fuera de servicio - omitiendo notificaciones\n");
            }
            
            // Resumen final
            resultado.append(String.format("\n=== RESUMEN FINAL NOTIFICACIONES MASIVAS ===\n"));
            resultado.append(String.format("📊 Total notificaciones enviadas: %d\n", totalNotificaciones));
            resultado.append(String.format("✅ Exitosas: %d\n", totalExitosas));
            resultado.append(String.format("❌ Fallidas: %d\n", totalNotificaciones - totalExitosas));
            resultado.append(String.format("📈 Tasa de éxito: %.2f%%\n\n", (totalExitosas * 100.0) / totalNotificaciones));
            
            resultado.append(String.format("📋 Detalles:\n"));
            resultado.append(String.format("   - Activos procesados: %d\n", activos.size()));
            resultado.append(String.format("   - Tickets procesados: %d\n", tickets.size()));
            resultado.append(String.format("   - Activos con problemas: %d\n", activosConProblemas));
            resultado.append(String.format("\n🌐 Verificar emails en: http://localhost:8025\n"));
            
        } catch (Exception e) {
            resultado.append(String.format("❌ ERROR EN NOTIFICACIONES MASIVAS: %s\n", e.getMessage()));
        }
        
        return resultado.toString();
    }
}