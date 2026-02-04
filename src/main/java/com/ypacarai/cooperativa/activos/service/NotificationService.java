package com.ypacarai.cooperativa.activos.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.ypacarai.cooperativa.activos.model.Activo;
import com.ypacarai.cooperativa.activos.model.Ticket;

/**
 * Servicio coordinador para todas las notificaciones del sistema
 * Gestiona envío de emails para alertas, reportes y notificaciones automáticas
 * Sistema de Gestión de Activos - Cooperativa Ypacaraí LTDA
 */
public class NotificationService {
    
    private static final Logger LOGGER = Logger.getLogger(NotificationService.class.getName());
    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    
    private final EmailService emailService;
    
    // Emails por defecto del sistema
    private static final String EMAIL_JEFE_INFORMATICA = "jefe.informatica@ypacarai.coop.py";
    private static final String EMAIL_SISTEMA = "sistema.activos@ypacarai.local";
    
    public NotificationService() {
        this.emailService = new EmailService();
    }
    
    public NotificationService(EmailService emailService) {
        this.emailService = emailService;
    }
    
    /**
     * Envía notificación de mantenimiento preventivo próximo a vencer
     */
    public boolean notificarMantenimientoPreventivo(Activo activo, int diasRestantes) {
        try {
            String asunto = String.format("⚠️ Mantenimiento Preventivo - %s (%d días)", 
                                        activo.getActNumeroActivo(), diasRestantes);
            
            String mensaje = String.format(
                "El activo %s requiere mantenimiento preventivo en %d días.\n\n" +
                "Detalles del activo:\n" +
                "- Número: %s\n" +
                "- Tipo: %s\n" +
                "- Ubicación: %s\n" +
                "- Estado actual: %s\n\n" +
                "Por favor, coordine el mantenimiento correspondiente.",
                activo.getActNumeroActivo(),
                diasRestantes,
                activo.getActNumeroActivo(),
                activo.getTipoActivoNombre() != null ? activo.getTipoActivoNombre() : "N/A",
                activo.getUbicacionNombre() != null ? activo.getUbicacionNombre() : "N/A",
                activo.getActEstado().toString()
            );
            
            String fechaObjetivo = LocalDateTime.now().plusDays(diasRestantes).format(FORMATO_FECHA);
            
            return emailService.enviarAlerta(EMAIL_JEFE_INFORMATICA, asunto, 
                                           activo.getActNumeroActivo(), mensaje, fechaObjetivo);
                                           
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error enviando notificación de mantenimiento preventivo", e);
            return false;
        }
    }
    
    /**
     * Envía notificación de mantenimiento correctivo completado
     */
    public boolean notificarMantenimientoCorrectivoCompletado(Ticket ticket, String solucionAplicada) {
        try {
            String asunto = String.format("✅ Mantenimiento Correctivo Completado - Ticket %s", 
                                        ticket.getTickNumero());
            
            String mensaje = String.format(
                "El mantenimiento correctivo ha sido completado.\n\n" +
                "Detalles del ticket:\n" +
                "- Número: %s\n" +
                "- Activo: %s\n" +
                "- Problema: %s\n" +
                "- Solución aplicada: %s\n" +
                "- Técnico: %s\n" +
                "- Fecha finalización: %s\n\n" +
                "El activo está nuevamente operativo.",
                ticket.getTickNumero(),
                ticket.getActivoNumero() != null ? ticket.getActivoNumero() : "N/A",
                ticket.getTickDescripcion(),
                solucionAplicada != null ? solucionAplicada : "Ver detalles en el sistema",
                ticket.getTecnicoAsignado() != null ? ticket.getTecnicoAsignado() : "N/A",
                LocalDateTime.now().format(FORMATO_FECHA)
            );
            
            return emailService.enviarAlerta(EMAIL_JEFE_INFORMATICA, asunto, 
                                           ticket.getActivoNumero(), mensaje, null);
                                           
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error enviando notificación de mantenimiento correctivo", e);
            return false;
        }
    }
    
    /**
     * Envía notificación de ticket vencido
     */
    public boolean notificarTicketVencido(Ticket ticket) {
        try {
            String asunto = String.format("🚨 Ticket Vencido - %s", ticket.getTickNumero());
            
            String mensaje = String.format(
                "ATENCIÓN: El siguiente ticket ha vencido sin resolverse.\n\n" +
                "Detalles del ticket:\n" +
                "- Número: %s\n" +
                "- Tipo: %s\n" +
                "- Prioridad: %s\n" +
                "- Activo: %s\n" +
                "- Descripción: %s\n" +
                "- Fecha vencimiento: %s\n" +
                "- Asignado a: %s\n" +
                "- Estado actual: %s\n\n" +
                "Requiere atención inmediata.",
                ticket.getTickNumero(),
                ticket.getTickTipo(),
                ticket.getTickPrioridad(),
                ticket.getActivoNumero() != null ? ticket.getActivoNumero() : "N/A",
                ticket.getTickDescripcion(),
                ticket.getTickFechaVencimiento() != null ? 
                    ticket.getTickFechaVencimiento().format(FORMATO_FECHA) : "N/A",
                ticket.getTecnicoAsignado() != null ? ticket.getTecnicoAsignado() : "Sin asignar",
                ticket.getTickEstado()
            );
            
            String fechaVencimiento = ticket.getTickFechaVencimiento() != null ? 
                ticket.getTickFechaVencimiento().format(FORMATO_FECHA) : null;
            
            return emailService.enviarAlerta(EMAIL_JEFE_INFORMATICA, asunto, 
                                           ticket.getActivoNumero(), mensaje, fechaVencimiento);
                                           
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error enviando notificación de ticket vencido", e);
            return false;
        }
    }
    
    /**
     * Envía notificación de activo fuera de servicio
     */
    public boolean notificarActivoFueraServicio(Activo activo, String motivo) {
        try {
            String asunto = String.format("⛔ Activo Fuera de Servicio - %s", 
                                        activo.getActNumeroActivo());
            
            String mensaje = String.format(
                "Se ha reportado un activo fuera de servicio.\n\n" +
                "Detalles del activo:\n" +
                "- Número: %s\n" +
                "- Tipo: %s\n" +
                "- Ubicación: %s\n" +
                "- Motivo: %s\n" +
                "- Fecha reporte: %s\n\n" +
                "Se requiere evaluación para determinar las acciones necesarias.",
                activo.getActNumeroActivo(),
                activo.getTipoActivoNombre() != null ? activo.getTipoActivoNombre() : "N/A",
                activo.getUbicacionNombre() != null ? activo.getUbicacionNombre() : "N/A",
                motivo != null ? motivo : "No especificado",
                LocalDateTime.now().format(FORMATO_FECHA)
            );
            
            return emailService.enviarAlerta(EMAIL_JEFE_INFORMATICA, asunto, 
                                           activo.getActNumeroActivo(), mensaje, null);
                                           
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error enviando notificación de activo fuera de servicio", e);
            return false;
        }
    }
    
    /**
     * Envía email de prueba para verificar configuración
     */
    public boolean enviarEmailPrueba(String destinatario) {
        try {
            String asunto = "✅ Prueba de Configuración - Sistema de Activos";
            
            String mensaje = "Este es un email de prueba del Sistema de Gestión de Activos.\n\n" +
                           "Si recibe este mensaje, la configuración de correo está funcionando correctamente.\n\n" +
                           "Fecha y hora: " + LocalDateTime.now().format(FORMATO_FECHA) + "\n\n" +
                           "Sistema de Gestión de Activos - Cooperativa Ypacaraí LTDA";
            
            return emailService.enviarEmail(destinatario, asunto, mensaje);
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error enviando email de prueba", e);
            return false;
        }
    }
    
    /**
     * Envía notificación de mantenimiento preventivo completado
     */
    public boolean notificarMantenimientoPreventoCompleto(Ticket ticket) {
        try {
            String asunto = String.format("✅ Mantenimiento Preventivo Completado - Ticket %s", 
                                        ticket.getTickNumero());
            
            String mensaje = String.format(
                "El mantenimiento preventivo ha sido completado exitosamente.\n\n" +
                "Detalles del ticket:\n" +
                "- Número: %s\n" +
                "- Activo: %s\n" +
                "- Descripción: %s\n" +
                "- Técnico: %s\n" +
                "- Fecha finalización: %s\n\n" +
                "El mantenimiento preventivo ha sido realizado según el cronograma.\n" +
                "El activo continúa operativo y con mantenimiento actualizado.",
                ticket.getTickNumero(),
                ticket.getActivoNumero() != null ? ticket.getActivoNumero() : "N/A",
                ticket.getTickDescripcion(),
                ticket.getTecnicoAsignado() != null ? ticket.getTecnicoAsignado() : "N/A",
                LocalDateTime.now().format(FORMATO_FECHA)
            );
            
            return emailService.enviarAlerta(EMAIL_JEFE_INFORMATICA, asunto, 
                                           ticket.getActivoNumero(), mensaje, null);
                                           
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error enviando notificación de mantenimiento preventivo completado", e);
            return false;
        }
    }
    
    /**
     * Verifica si el servicio de email está funcionando
     */
    public boolean verificarEstadoServicio() {
        try {
            return emailService.probarConexion();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error verificando estado del servicio de email", e);
            return false;
        }
    }
    
    /**
     * Obtiene la configuración actual del servicio de email
     */
    public String obtenerInformacionConfiguracion() {
        try {
            var config = emailService.getConfiguracion();
            return String.format(
                "Configuración Email:\n" +
                "- Servidor: %s:%s\n" +
                "- Usuario: %s\n" +
                "- SSL: %s\n" +
                "- Autenticación: %s",
                config.getProperty("mail.smtp.host", "N/A"),
                config.getProperty("mail.smtp.port", "N/A"),
                config.getProperty("mail.smtp.user", "N/A"),
                config.getProperty("mail.smtp.ssl.enable", "false"),
                config.getProperty("mail.smtp.auth", "false")
            );
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error obteniendo información de configuración", e);
            return "Error obteniendo configuración";
        }
    }
}