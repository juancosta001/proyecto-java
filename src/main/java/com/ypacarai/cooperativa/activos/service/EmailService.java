package com.ypacarai.cooperativa.activos.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * Servicio para envío de emails usando SMTP
 * Compatible con Zimbra y MailHog para testing
 * Sistema de Gestión de Activos - Cooperativa Ypacaraí LTDA
 */
public class EmailService {
    
    private static final Logger LOGGER = Logger.getLogger(EmailService.class.getName());
    
    private Properties emailConfig;
    private Session mailSession;
    
    public EmailService() {
        cargarConfiguracion();
        inicializarSesion();
    }
    
    /**
     * Carga configuración desde application.properties
     */
    private void cargarConfiguracion() {
        emailConfig = new Properties();
        
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("application.properties")) {
            if (input == null) {
                LOGGER.severe("No se pudo encontrar application.properties");
                return;
            }
            
            Properties props = new Properties();
            props.load(input);
            
            // Mapear propiedades de email
            emailConfig.setProperty("mail.smtp.host", props.getProperty("email.smtp.host", "localhost"));
            emailConfig.setProperty("mail.smtp.port", props.getProperty("email.smtp.port", "1025"));
            emailConfig.setProperty("mail.smtp.auth", props.getProperty("email.smtp.ssl", "false").equals("true") ? "true" : "false");
            emailConfig.setProperty("mail.smtp.starttls.enable", props.getProperty("email.smtp.ssl", "false"));
            emailConfig.setProperty("mail.smtp.ssl.enable", props.getProperty("email.smtp.ssl", "false"));
            emailConfig.setProperty("mail.smtp.user", props.getProperty("email.smtp.user", "sistema.activos@ypacarai.local"));
            emailConfig.setProperty("mail.smtp.password", props.getProperty("email.smtp.password", ""));
            
            LOGGER.info("Configuración de email cargada: " + emailConfig.getProperty("mail.smtp.host") + ":" + emailConfig.getProperty("mail.smtp.port"));
            
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error cargando configuración de email", e);
        }
    }
    
    /**
     * Inicializa la sesión de correo
     */
    private void inicializarSesion() {
        try {
            boolean requiresAuth = Boolean.parseBoolean(emailConfig.getProperty("mail.smtp.auth", "false"));
            
            if (requiresAuth) {
                // Para servidores que requieren autenticación (Zimbra real)
                Authenticator auth = new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(
                            emailConfig.getProperty("mail.smtp.user"), 
                            emailConfig.getProperty("mail.smtp.password")
                        );
                    }
                };
                mailSession = Session.getInstance(emailConfig, auth);
            } else {
                // Para MailHog (sin autenticación)
                mailSession = Session.getInstance(emailConfig);
            }
            
            mailSession.setDebug(true); // Para ver logs detallados en testing
            LOGGER.info("Sesión de correo inicializada correctamente");
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error inicializando sesión de correo", e);
        }
    }
    
    /**
     * Envía un email simple
     */
    public boolean enviarEmail(String destinatario, String asunto, String mensaje) {
        return enviarEmail(destinatario, asunto, mensaje, false);
    }
    
    /**
     * Envía un email con formato HTML opcional
     */
    public boolean enviarEmail(String destinatario, String asunto, String mensaje, boolean esHTML) {
        try {
            if (mailSession == null) {
                LOGGER.severe("Sesión de correo no inicializada");
                return false;
            }
            
            // Crear mensaje
            MimeMessage message = new MimeMessage(mailSession);
            
            // Remitente
            String fromEmail = emailConfig.getProperty("mail.smtp.user", "sistema.activos@ypacarai.local");
            message.setFrom(new InternetAddress(fromEmail, "Sistema de Gestión de Activos"));
            
            // Destinatario
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(destinatario));
            
            // Asunto
            message.setSubject(asunto, "UTF-8");
            
            // Contenido
            if (esHTML) {
                message.setContent(mensaje, "text/html; charset=UTF-8");
            } else {
                message.setText(mensaje, "UTF-8");
            }
            
            // Fecha
            message.setSentDate(new Date());
            
            // Enviar
            Transport.send(message);
            
            LOGGER.info("Email enviado exitosamente a: " + destinatario + " - Asunto: " + asunto);
            return true;
            
        } catch (MessagingException e) {
            LOGGER.log(Level.SEVERE, "Error enviando email a " + destinatario, e);
            return false;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error inesperado enviando email", e);
            return false;
        }
    }
    
    /**
     * Envía email usando plantilla HTML para alertas
     */
    public boolean enviarAlerta(String destinatario, String asunto, String activoNumero, 
                               String mensaje, String fechaObjetivo) {
        
        String plantillaHTML = generarPlantillaAlerta(activoNumero, mensaje, fechaObjetivo);
        return enviarEmail(destinatario, asunto, plantillaHTML, true);
    }
    
    /**
     * Genera plantilla HTML para alertas
     */
    private String generarPlantillaAlerta(String activoNumero, String mensaje, String fechaObjetivo) {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>");
        html.append("<html><head><meta charset='UTF-8'></head><body>");
        html.append("<div style='font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #ddd;'>");
        html.append("<h2 style='color: #2c3e50; border-bottom: 2px solid #3498db; padding-bottom: 10px;'>");
        html.append("🔧 Alerta del Sistema de Gestión de Activos</h2>");
        
        html.append("<div style='background-color: #f8f9fa; padding: 15px; border-left: 4px solid #3498db; margin: 20px 0;'>");
        html.append("<p><strong>📋 Activo:</strong> ").append(activoNumero != null ? activoNumero : "N/A").append("</p>");
        html.append("<p><strong>📝 Mensaje:</strong> ").append(mensaje != null ? mensaje : "Sin mensaje").append("</p>");
        if (fechaObjetivo != null && !fechaObjetivo.isEmpty()) {
            html.append("<p><strong>📅 Fecha Objetivo:</strong> ").append(fechaObjetivo).append("</p>");
        }
        html.append("</div>");
        
        html.append("<hr style='margin: 30px 0; border: 0; border-top: 1px solid #eee;'>");
        html.append("<p style='color: #7f8c8d; font-size: 12px; text-align: center;'>");
        html.append("Sistema de Gestión de Activos - Cooperativa Ypacaraí LTDA<br>");
        html.append("Este es un mensaje automático, no responder a este email.");
        html.append("</p>");
        html.append("</div></body></html>");
        
        return html.toString();
    }
    
    /**
     * Envía notificación sobre tickets preventivos generados automáticamente
     */
    public boolean enviarNotificacionTicketsGenerados(int cantidadTickets) {
        try {
            LOGGER.log(Level.INFO, "Enviando notificación de {0} tickets preventivos generados", cantidadTickets);
            
            // Email por defecto para notificaciones
            String destinatario = emailConfig.getProperty("mail.admin.email", "admin@cooperativaypacarai.coop.py");
            
            StringBuilder mensaje = new StringBuilder();
            mensaje.append("<html><body>");
            mensaje.append("<div style='font-family: Arial, sans-serif; max-width: 600px;'>");
            mensaje.append("<h2 style='color: #2c7a3a; background-color: #e8f5e9; padding: 10px; border-left: 5px solid #2c7a3a;'>");
            mensaje.append("🎫 Tickets Preventivos Generados Automáticamente</h2>");
            
            mensaje.append("<div style='background-color: #f8f9fa; padding: 20px; border: 1px solid #ddd; margin: 15px 0;'>");
            mensaje.append("<p style='font-size: 16px;'>El sistema ha generado automáticamente ");
            mensaje.append("<strong style='color: #2c7a3a; font-size: 20px;'>").append(cantidadTickets).append("</strong>");
            mensaje.append(" ticket(s) de mantenimiento preventivo.</p>");
            mensaje.append("</div>");
            
            mensaje.append("<div style='background-color: #e3f2fd; border-left: 4px solid #2196f3; padding: 15px; margin: 15px 0;'>");
            mensaje.append("<h3 style='margin-top: 0; color: #1976d2;'>📋 Resumen</h3>");
            mensaje.append("<ul style='margin: 10px 0;'>");
            mensaje.append("<li>Se crearon tickets para activos sin mantenimiento preventivo reciente (últimos 6 meses)</li>");
            mensaje.append("<li>Los tickets están asignados como \"Preventivo\" y priorizados según criticidad del activo</li>");
            mensaje.append("<li>Revise el panel de tickets para asignar técnicos y programar las intervenciones</li>");
            mensaje.append("</ul>");
            mensaje.append("</div>");
            
            mensaje.append("<div style='background-color: #fff9c4; border: 1px solid #fbc02d; padding: 15px; margin: 15px 0;'>");
            mensaje.append("<h4 style='margin-top: 0; color: #f57f17;'>⚠️ Acciones Recomendadas:</h4>");
            mensaje.append("<ol style='margin: 10px 0;'>");
            mensaje.append("<li>Revisar los nuevos tickets en el sistema</li>");
            mensaje.append("<li>Asignar técnicos responsables</li>");
            mensaje.append("<li>Establecer fechas de vencimiento apropiadas</li>");
            mensaje.append("<li>Priorizar tickets críticos</li>");
            mensaje.append("</ol>");
            mensaje.append("</div>");
            
            mensaje.append("<div style='border-top: 2px solid #ddd; padding-top: 15px; margin-top: 20px; color: #666; font-size: 12px;'>");
            mensaje.append("<p><strong>Sistema:</strong> Gestión de Activos - Cooperativa Ypacaraí LTDA</p>");
            mensaje.append("<p><strong>Fecha:</strong> ").append(new Date().toString()).append("</p>");
            mensaje.append("<p><em>Este es un mensaje automático del sistema. Por favor no responder.</em></p>");
            mensaje.append("</div>");
            mensaje.append("</div></body></html>");
            
            String asunto = "🎫 Sistema de Activos - " + cantidadTickets + " tickets preventivos generados";
            
            boolean enviado = enviarEmail(destinatario, asunto, mensaje.toString(), true);
            
            if (enviado) {
                LOGGER.log(Level.INFO, "✅ Notificación de tickets enviada exitosamente a {0}", destinatario);
            } else {
                LOGGER.log(Level.WARNING, "⚠️ Fallo enviando notificación de tickets a {0}", destinatario);
            }
            
            return enviado;
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error enviando notificación de tickets generados", e);
            return false;
        }
    }
    
    /**
     * Envía alerta de error del sistema a administradores
     */
    public boolean enviarAlertaError(String titulo, String descripcionError) {
        try {
            LOGGER.log(Level.WARNING, "Enviando alerta de error del sistema: {0}", titulo);
            
            // Email por defecto para alertas de sistema
            String destinatario = emailConfig.getProperty("mail.admin.email", "admin@cooperativaypacarai.coop.py");
            
            StringBuilder mensajeError = new StringBuilder();
            mensajeError.append("<html><body>");
            mensajeError.append("<div style='font-family: Arial, sans-serif; max-width: 600px;'>");
            mensajeError.append("<h2 style='color: #cc0000; background-color: #ffe6e6; padding: 10px; border-left: 5px solid #cc0000;'>");
            mensajeError.append("🚨 ALERTA DEL SISTEMA - ").append(titulo).append("</h2>");
            
            mensajeError.append("<div style='background-color: #f8f8f8; padding: 15px; border: 1px solid #ddd; margin: 10px 0;'>");
            mensajeError.append("<h3>Descripción del Error:</h3>");
            mensajeError.append("<p style='font-family: monospace; background-color: white; padding: 10px; border: 1px solid #ccc;'>");
            mensajeError.append(descripcionError.replace("\n", "<br>"));
            mensajeError.append("</p>");
            mensajeError.append("</div>");
            
            mensajeError.append("<div style='background-color: #fff3cd; border: 1px solid #ffeaa7; padding: 10px; margin: 10px 0;'>");
            mensajeError.append("<h4>⚠️ Acción Requerida:</h4>");
            mensajeError.append("<ul>");
            mensajeError.append("<li>Revisar logs del sistema para más detalles</li>");
            mensajeError.append("<li>Verificar conexiones de base de datos</li>");
            mensajeError.append("<li>Contactar al equipo de IT si persiste el problema</li>");
            mensajeError.append("</ul>");
            mensajeError.append("</div>");
            
            mensajeError.append("<div style='border-top: 1px solid #ddd; padding-top: 15px; margin-top: 20px; color: #666; font-size: 12px;'>");
            mensajeError.append("<p><strong>Sistema:</strong> Gestión de Activos - Cooperativa Ypacaraí LTDA</p>");
            mensajeError.append("<p><strong>Hora:</strong> ").append(new Date().toString()).append("</p>");
            mensajeError.append("<p>Este es un mensaje automático del sistema. Por favor no responder.</p>");
            mensajeError.append("</div>");
            mensajeError.append("</div></body></html>");
            
            String asuntoCompleto = "[SISTEMA ALERTA] " + titulo + " - " + new Date();
            
            boolean enviado = enviarEmail(destinatario, asuntoCompleto, mensajeError.toString(), true);
            
            if (enviado) {
                LOGGER.log(Level.INFO, "✅ Alerta de error enviada exitosamente a {0}", destinatario);
            } else {
                LOGGER.log(Level.SEVERE, "❌ Fallo enviando alerta de error a {0}", destinatario);
            }
            
            return enviado;
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error crítico enviando alerta de error del sistema", e);
            return false;
        }
    }
    
    /**
     * Prueba la conexión SMTP
     */
    public boolean probarConexion() {
        try {
            LOGGER.info("Probando conexión SMTP...");
            String emailPrueba = "admin@test.local";
            return enviarEmail(emailPrueba, "Prueba de Conexión", 
                "Este es un email de prueba para verificar la configuración SMTP.");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error probando conexión SMTP", e);
            return false;
        }
    }
    
    /**
     * Obtiene la configuración actual
     */
    public Properties getConfiguracion() {
        return new Properties(emailConfig);
    }
}