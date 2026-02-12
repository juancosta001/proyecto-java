package com.ypacarai.cooperativa.activos.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.ypacarai.cooperativa.activos.dao.ActivoDAO;
import com.ypacarai.cooperativa.activos.dao.FichaReporteDAO;
import com.ypacarai.cooperativa.activos.dao.MantenimientoDAO;
import com.ypacarai.cooperativa.activos.dao.UsuarioDAO;
import com.ypacarai.cooperativa.activos.model.Activo;
import com.ypacarai.cooperativa.activos.model.FichaReporte;
import com.ypacarai.cooperativa.activos.model.FichaReporte.EstadoFicha;
import com.ypacarai.cooperativa.activos.model.Mantenimiento;
import com.ypacarai.cooperativa.activos.model.Usuario;

/**
 * Servicio de negocio para Fichas de Reporte de Mantenimientos Correctivos
 * Gestiona la creación, envío y trazabilidad de fichas técnicas
 * Sistema de Gestión de Activos - Cooperativa Ypacaraí LTDA
 */
public class FichaReporteService {
    
    private final FichaReporteDAO fichaReporteDAO;
    private final MantenimientoDAO mantenimientoDAO;
    private final ActivoDAO activoDAO;
    private final UsuarioDAO usuarioDAO;
    private final EmailService emailService;
    
    public FichaReporteService() {
        this.fichaReporteDAO = new FichaReporteDAO();
        this.mantenimientoDAO = new MantenimientoDAO();
        this.activoDAO = new ActivoDAO();
        this.usuarioDAO = new UsuarioDAO();
        this.emailService = new EmailService();
    }
    
    /**
     * Crea una nueva ficha de reporte
     * Genera número automático y valida datos
     */
    public FichaReporte crearFicha(FichaReporte ficha) throws IllegalArgumentException {
        // Validaciones
        validarFicha(ficha);
        
        // Generar número de ficha si no existe
        if (ficha.getFichaNumero() == null || ficha.getFichaNumero().isEmpty()) {
            ficha.setFichaNumero(generarNumeroFicha());
        }
        
        // Establecer fechas
        if (ficha.getCreadoEn() == null) {
            ficha.setCreadoEn(LocalDateTime.now());
        }
        if (ficha.getActualizadoEn() == null) {
            ficha.setActualizadoEn(LocalDateTime.now());
        }
        if (ficha.getFichaFecha() == null) {
            ficha.setFichaFecha(LocalDate.now());
        }
        
        // Estado inicial
        if (ficha.getFichaEstado() == null) {
            ficha.setFichaEstado(EstadoFicha.Borrador);
        }
        
        // Guardar
        if (fichaReporteDAO.save(ficha)) {
            return ficha;
        } else {
            throw new RuntimeException("No se pudo guardar la ficha de reporte");
        }
    }
    
    /**
     * Actualiza una ficha existente
     */
    public boolean actualizarFicha(FichaReporte ficha) throws IllegalArgumentException {
        if (ficha.getFichaId() == null) {
            throw new IllegalArgumentException("La ficha debe tener un ID");
        }
        
        validarFicha(ficha);
        ficha.setActualizadoEn(LocalDateTime.now());
        return fichaReporteDAO.update(ficha);
    }
    
    /**
     * Envía la ficha por email al Jefe de Informática
     */
    public boolean enviarFichaAJefe(Integer fichaId) throws Exception {
        FichaReporte ficha = fichaReporteDAO.findById(fichaId);
        if (ficha == null) {
            throw new IllegalArgumentException("Ficha no encontrada");
        }
        
        if (ficha.getFichaEstado() == EstadoFicha.Enviada) {
            throw new IllegalStateException("La ficha ya fue enviada");
        }
        
        // Obtener datos relacionados para el email
        Mantenimiento mantenimiento = null;
        Activo activo = null;
        
        if (ficha.getMantId() != null) {
            mantenimiento = mantenimientoDAO.findById(ficha.getMantId());
            if (mantenimiento != null && mantenimiento.getActId() != null) {
                activo = activoDAO.findById(mantenimiento.getActId()).orElse(null);
            }
        }
        
        Usuario tecnico = null;
        if (ficha.getCreadoPor() != null) {
            tecnico = usuarioDAO.findById(ficha.getCreadoPor()).orElse(null);
        }
        
        // Buscar Jefe de Informática
        List<Usuario> jefes = usuarioDAO.findByRol(Usuario.Rol.Jefe_Informatica);
        if (jefes.isEmpty()) {
            throw new IllegalStateException("No hay un Jefe de Informática registrado para recibir la ficha");
        }
        
        // Generar email
        String asunto = "Ficha de Reporte Correctivo - " + ficha.getFichaNumero();
        String cuerpo = generarEmailFicha(ficha, mantenimiento, activo, tecnico);
        
        // Enviar a todos los jefes
        boolean enviadoExitosamente = false;
        for (Usuario jefe : jefes) {
            if (jefe.getUsuEmail() != null && !jefe.getUsuEmail().isEmpty()) {
                try {
                    emailService.enviarEmail(
                        jefe.getUsuEmail(),
                        asunto,
                        cuerpo
                    );
                    enviadoExitosamente = true;
                } catch (Exception e) {
                    System.err.println("Error al enviar ficha a " + jefe.getUsuEmail() + ": " + e.getMessage());
                }
            }
        }
        
        if (enviadoExitosamente) {
            // Marcar como enviada
            ficha.setFichaEstado(EstadoFicha.Enviada);
            ficha.setActualizadoEn(LocalDateTime.now());
            fichaReporteDAO.update(ficha);
            return true;
        } else {
            throw new RuntimeException("No se pudo enviar el email a ningún destinatario");
        }
    }
    
    /**
     * Busca ficha por ID
     */
    public FichaReporte buscarPorId(Integer id) {
        return fichaReporteDAO.findById(id);
    }
    
    /**
     * Obtiene todas las fichas
     */
    public List<FichaReporte> obtenerTodas() {
        return fichaReporteDAO.findAll();
    }
    
    /**
     * Busca fichas por mantenimiento
     */
    public FichaReporte buscarPorMantenimiento(Integer mantenimientoId) {
        return fichaReporteDAO.findByMantenimiento(mantenimientoId);
    }
    
    /**
     * Busca fichas por estado
     */
    public List<FichaReporte> buscarPorEstado(EstadoFicha estado) {
        return fichaReporteDAO.findByEstado(estado);
    }
    
    /**
     * Busca fichas por rango de fechas
     */
    public List<FichaReporte> buscarPorRangoFechas(LocalDate fechaInicio, LocalDate fechaFin) {
        return fichaReporteDAO.findByFechaRango(fechaInicio, fechaFin);
    }
    
    /**
     * Elimina una ficha (solo si está en estado Borrador)
     */
    public boolean eliminarFicha(Integer id) throws IllegalStateException {
        FichaReporte ficha = fichaReporteDAO.findById(id);
        if (ficha == null) {
            throw new IllegalArgumentException("Ficha no encontrada");
        }
        
        if (ficha.getFichaEstado() == EstadoFicha.Enviada) {
            throw new IllegalStateException("No se puede eliminar una ficha que ya fue enviada");
        }
        
        return fichaReporteDAO.delete(id);
    }
    
    /**
     * Validaciones de negocio
     */
    private void validarFicha(FichaReporte ficha) throws IllegalArgumentException {
        if (ficha.getMantId() == null) {
            throw new IllegalArgumentException("Debe especificar el mantenimiento");
        }
        
        if (ficha.getFichaDiagnostico() == null || ficha.getFichaDiagnostico().trim().isEmpty()) {
            throw new IllegalArgumentException("El diagnóstico es obligatorio");
        }
        
        if (ficha.getFichaSolucionAplicada() == null || ficha.getFichaSolucionAplicada().trim().isEmpty()) {
            throw new IllegalArgumentException("La solución aplicada es obligatoria");
        }
        
        if (ficha.getCreadoPor() == null) {
            throw new IllegalArgumentException("Debe especificar el técnico responsable");
        }
        
        // Validar que el mantenimiento existe
        Mantenimiento mantenimiento = mantenimientoDAO.findById(ficha.getMantId());
        if (mantenimiento == null) {
            throw new IllegalArgumentException("El mantenimiento especificado no existe");
        }
        
        // Validar que el técnico existe
        Usuario tecnico = usuarioDAO.findById(ficha.getCreadoPor()).orElse(null);
        if (tecnico == null) {
            throw new IllegalArgumentException("El técnico especificado no existe");
        }
    }
    
    /**
     * Genera número único de ficha: FR-YYYY-NNNN
     */
    private String generarNumeroFicha() {
        int anio = LocalDate.now().getYear();
        List<FichaReporte> fichasDelAnio = fichaReporteDAO.findAll().stream()
            .filter(f -> f.getFichaNumero() != null && f.getFichaNumero().startsWith("FR-" + anio))
            .toList();
        
        int siguiente = fichasDelAnio.size() + 1;
        return String.format("FR-%d-%04d", anio, siguiente);
    }
    
    /**
     * Genera el cuerpo del email con formato HTML
     */
    private String generarEmailFicha(FichaReporte ficha, Mantenimiento mantenimiento, 
                                      Activo activo, Usuario tecnico) {
        StringBuilder html = new StringBuilder();
        
        html.append("<!DOCTYPE html><html><head><meta charset='UTF-8'></head><body>");
        html.append("<div style='font-family: Arial, sans-serif; max-width: 800px; margin: 0 auto; padding: 20px;'>");
        
        // Header
        html.append("<div style='background-color: #228B22; color: white; padding: 20px; border-radius: 8px 8px 0 0;'>");
        html.append("<h1 style='margin: 0;'>📋 Ficha de Reporte - Mantenimiento Correctivo</h1>");
        html.append("<p style='margin: 10px 0 0 0; font-size: 14px;'>Cooperativa Ypacaraí LTDA - Sistema de Gestión de Activos</p>");
        html.append("</div>");
        
        // Información general
        html.append("<div style='background-color: #f5f5f5; padding: 20px; border-left: 4px solid #228B22;'>");
        html.append("<h2 style='color: #228B22; margin-top: 0;'>Información General</h2>");
        html.append("<table style='width: 100%; border-collapse: collapse;'>");
        html.append("<tr><td style='padding: 8px 0; font-weight: bold; width: 180px;'>Número de Ficha:</td><td>").append(ficha.getFichaNumero()).append("</td></tr>");
        html.append("<tr><td style='padding: 8px 0; font-weight: bold;'>Fecha de Ficha:</td><td>").append(ficha.getFichaFecha()).append("</td></tr>");
        html.append("<tr><td style='padding: 8px 0; font-weight: bold;'>Fecha de Creación:</td><td>").append(ficha.getCreadoEn().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))).append("</td></tr>");
        
        if (activo != null) {
            html.append("<tr><td style='padding: 8px 0; font-weight: bold;'>Activo:</td><td>").append(activo.getActNumeroActivo()).append(" - ").append(activo.getActModelo()).append("</td></tr>");
        }
        
        if (tecnico != null) {
            html.append("<tr><td style='padding: 8px 0; font-weight: bold;'>Técnico Responsable:</td><td>").append(tecnico.getUsuNombre()).append("</td></tr>");
        }
        
        if (mantenimiento != null) {
            html.append("<tr><td style='padding: 8px 0; font-weight: bold;'>Mantenimiento ID:</td><td>").append(mantenimiento.getMantId()).append("</td></tr>");
            html.append("<tr><td style='padding: 8px 0; font-weight: bold;'>Tipo:</td><td>").append(mantenimiento.getMantTipo()).append("</td></tr>");
        }
        
        html.append("</table>");
        html.append("</div>");
        
        // Problema reportado
        if (ficha.getFichaProblemaReportado() != null && !ficha.getFichaProblemaReportado().isEmpty()) {
            html.append("<div style='background-color: white; padding: 20px; margin-top: 20px; border-left: 4px solid #FF6347;'>");
            html.append("<h3 style='color: #FF6347; margin-top: 0;'>⚠ Problema Reportado</h3>");
            html.append("<p style='white-space: pre-wrap;'>").append(ficha.getFichaProblemaReportado()).append("</p>");
            html.append("</div>");
        }
        
        // Diagnóstico
        html.append("<div style='background-color: white; padding: 20px; margin-top: 20px; border-left: 4px solid #4682B4;'>");
        html.append("<h3 style='color: #4682B4; margin-top: 0;'>🔍 Diagnóstico</h3>");
        html.append("<p style='white-space: pre-wrap;'>").append(ficha.getFichaDiagnostico()).append("</p>");
        html.append("</div>");
        
        // Solución aplicada
        html.append("<div style='background-color: white; padding: 20px; margin-top: 20px; border-left: 4px solid #32CD32;'>");
        html.append("<h3 style='color: #32CD32; margin-top: 0;'>🔧 Solución Aplicada</h3>");
        html.append("<p style='white-space: pre-wrap;'>").append(ficha.getFichaSolucionAplicada()).append("</p>");
        html.append("</div>");
        
        // Componentes reemplazados
        if (ficha.getFichaComponentesCambio() != null && !ficha.getFichaComponentesCambio().trim().isEmpty()) {
            html.append("<div style='background-color: white; padding: 20px; margin-top: 20px; border-left: 4px solid #FF8C00;'>");
            html.append("<h3 style='color: #FF8C00; margin-top: 0;'>🔩 Componentes Reemplazados</h3>");
            html.append("<p style='white-space: pre-wrap;'>").append(ficha.getFichaComponentesCambio()).append("</p>");
            html.append("</div>");
        }
        
        // Tiempos
        if (ficha.getFichaTiempoEstimado() != null || ficha.getFichaTiempoReal() != null) {
            html.append("<div style='background-color: white; padding: 20px; margin-top: 20px; border-left: 4px solid #9370DB;'>");
            html.append("<h3 style='color: #9370DB; margin-top: 0;'>⏱ Registro de Tiempos</h3>");
            html.append("<table style='width: 100%; border-collapse: collapse;'>");
            if (ficha.getFichaTiempoEstimado() != null) {
                html.append("<tr><td style='padding: 8px 0; font-weight: bold; width: 180px;'>Tiempo Estimado:</td><td>").append(ficha.getFichaTiempoEstimado()).append(" minutos</td></tr>");
            }
            if (ficha.getFichaTiempoReal() != null) {
                html.append("<tr><td style='padding: 8px 0; font-weight: bold;'>Tiempo Real:</td><td>").append(ficha.getFichaTiempoReal()).append(" minutos</td></tr>");
            }
            html.append("</table>");
            html.append("</div>");
        }
        
        // Observaciones
        if (ficha.getFichaObservaciones() != null && !ficha.getFichaObservaciones().trim().isEmpty()) {
            html.append("<div style='background-color: white; padding: 20px; margin-top: 20px; border-left: 4px solid #808080;'>");
            html.append("<h3 style='color: #808080; margin-top: 0;'>📝 Observaciones</h3>");
            html.append("<p style='white-space: pre-wrap;'>").append(ficha.getFichaObservaciones()).append("</p>");
            html.append("</div>");
        }
        
        // Firmas
        if ((ficha.getFichaTecnicoFirma() != null && !ficha.getFichaTecnicoFirma().isEmpty()) ||
            (ficha.getFichaUsuarioFirma() != null && !ficha.getFichaUsuarioFirma().isEmpty())) {
            html.append("<div style='background-color: #f5f5f5; padding: 20px; margin-top: 20px; border-left: 4px solid #333;'>");
            html.append("<h3 style='color: #333; margin-top: 0;'>✍ Firmas</h3>");
            html.append("<table style='width: 100%; border-collapse: collapse;'>");
            if (ficha.getFichaTecnicoFirma() != null && !ficha.getFichaTecnicoFirma().isEmpty()) {
                html.append("<tr><td style='padding: 8px 0; font-weight: bold; width: 180px;'>Técnico:</td><td>").append(ficha.getFichaTecnicoFirma()).append("</td></tr>");
            }
            if (ficha.getFichaUsuarioFirma() != null && !ficha.getFichaUsuarioFirma().isEmpty()) {
                html.append("<tr><td style='padding: 8px 0; font-weight: bold;'>Usuario:</td><td>").append(ficha.getFichaUsuarioFirma()).append("</td></tr>");
            }
            html.append("</table>");
            html.append("</div>");
        }
        
        // Footer
        html.append("<div style='margin-top: 30px; padding: 20px; background-color: #f5f5f5; border-radius: 0 0 8px 8px; text-align: center; font-size: 12px; color: #666;'>");
        html.append("<p>Este es un mensaje automático generado por el Sistema de Gestión de Activos</p>");
        html.append("<p><strong>Cooperativa Ypacaraí LTDA</strong> | Departamento de Informática</p>");
        html.append("<p style='margin-top: 10px; font-size: 11px;'>Generado el: ").append(LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))).append("</p>");
        html.append("</div>");
        
        html.append("</div></body></html>");
        
        return html.toString();
    }
    
    /**
     * Cuenta fichas por estado
     */
    public long contarPorEstado(EstadoFicha estado) {
        return buscarPorEstado(estado).size();
    }
    
    /**
     * Obtiene fichas pendientes (en estado Borrador)
     */
    public List<FichaReporte> obtenerPendientes() {
        return fichaReporteDAO.findPendientes();
    }
}
