package com.ypacarai.cooperativa.activos.model;

import java.time.LocalDateTime;

/**
 * Modelo para la entidad TICKET_ASIGNACIONES
 * Gestiona múltiples técnicos asignados a un ticket
 * Sistema de Gestión de Activos - Cooperativa Ypacaraí LTDA
 */
public class TicketAsignacion {
    
    public enum RolAsignacion {
        Responsable("Responsable - Técnico principal del ticket"),
        Colaborador("Colaborador - Técnico de apoyo"),
        Supervisor("Supervisor - Supervisa la ejecución");
        
        private final String descripcion;
        
        RolAsignacion(String descripcion) {
            this.descripcion = descripcion;
        }
        
        public String getDescripcion() {
            return descripcion;
        }
        
        @Override
        public String toString() {
            return name();
        }
    }
    
    private int tasId;
    private int tickId;
    private int usuId;
    private LocalDateTime tasFechaAsignacion;
    private boolean tasActivo;
    private RolAsignacion tasRolAsignacion;
    private String tasObservaciones;
    private LocalDateTime creadoEn;
    private LocalDateTime actualizadoEn;
    
    // Campos relacionados (para joins)
    private String usuarioNombre;
    private String usuarioEmail;
    private Usuario.Rol usuarioRol;
    
    // Constructores
    public TicketAsignacion() {
        this.tasFechaAsignacion = LocalDateTime.now();
        this.tasActivo = true;
        this.tasRolAsignacion = RolAsignacion.Responsable;
    }
    
    public TicketAsignacion(int tickId, int usuId, RolAsignacion rolAsignacion) {
        this();
        this.tickId = tickId;
        this.usuId = usuId;
        this.tasRolAsignacion = rolAsignacion;
    }
    
    public TicketAsignacion(int tickId, int usuId, RolAsignacion rolAsignacion, String observaciones) {
        this(tickId, usuId, rolAsignacion);
        this.tasObservaciones = observaciones;
    }
    
    // Getters y Setters
    public int getTasId() {
        return tasId;
    }
    
    public void setTasId(int tasId) {
        this.tasId = tasId;
    }
    
    public int getTickId() {
        return tickId;
    }
    
    public void setTickId(int tickId) {
        this.tickId = tickId;
    }
    
    public int getUsuId() {
        return usuId;
    }
    
    public void setUsuId(int usuId) {
        this.usuId = usuId;
    }
    
    public LocalDateTime getTasFechaAsignacion() {
        return tasFechaAsignacion;
    }
    
    public void setTasFechaAsignacion(LocalDateTime tasFechaAsignacion) {
        this.tasFechaAsignacion = tasFechaAsignacion;
    }
    
    public boolean isTasActivo() {
        return tasActivo;
    }
    
    public void setTasActivo(boolean tasActivo) {
        this.tasActivo = tasActivo;
    }
    
    public RolAsignacion getTasRolAsignacion() {
        return tasRolAsignacion;
    }
    
    public void setTasRolAsignacion(RolAsignacion tasRolAsignacion) {
        this.tasRolAsignacion = tasRolAsignacion;
    }
    
    public String getTasObservaciones() {
        return tasObservaciones;
    }
    
    public void setTasObservaciones(String tasObservaciones) {
        this.tasObservaciones = tasObservaciones;
    }
    
    public LocalDateTime getCreadoEn() {
        return creadoEn;
    }
    
    public void setCreadoEn(LocalDateTime creadoEn) {
        this.creadoEn = creadoEn;
    }
    
    public LocalDateTime getActualizadoEn() {
        return actualizadoEn;
    }
    
    public void setActualizadoEn(LocalDateTime actualizadoEn) {
        this.actualizadoEn = actualizadoEn;
    }
    
    // Campos relacionados
    public String getUsuarioNombre() {
        return usuarioNombre;
    }
    
    public void setUsuarioNombre(String usuarioNombre) {
        this.usuarioNombre = usuarioNombre;
    }
    
    public String getUsuarioEmail() {
        return usuarioEmail;
    }
    
    public void setUsuarioEmail(String usuarioEmail) {
        this.usuarioEmail = usuarioEmail;
    }
    
    public Usuario.Rol getUsuarioRol() {
        return usuarioRol;
    }
    
    public void setUsuarioRol(Usuario.Rol usuarioRol) {
        this.usuarioRol = usuarioRol;
    }
    
    // Métodos de utilidad
    public boolean esResponsable() {
        return tasRolAsignacion == RolAsignacion.Responsable;
    }
    
    public boolean esColaborador() {
        return tasRolAsignacion == RolAsignacion.Colaborador;
    }
    
    public boolean esSupervisor() {
        return tasRolAsignacion == RolAsignacion.Supervisor;
    }
    
    public String getDisplayText() {
        StringBuilder sb = new StringBuilder();
        if (usuarioNombre != null) {
            sb.append(usuarioNombre);
        } else {
            sb.append("Usuario ID: ").append(usuId);
        }
        sb.append(" (").append(tasRolAsignacion).append(")");
        return sb.toString();
    }
    
    public String getDescripcionCompleta() {
        StringBuilder sb = new StringBuilder();
        sb.append(getDisplayText());
        if (tasObservaciones != null && !tasObservaciones.trim().isEmpty()) {
            sb.append(" - ").append(tasObservaciones);
        }
        return sb.toString();
    }
    
    @Override
    public String toString() {
        return getDisplayText();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        TicketAsignacion that = (TicketAsignacion) obj;
        return tickId == that.tickId && usuId == that.usuId;
    }
    
    @Override
    public int hashCode() {
        return 31 * tickId + usuId;
    }
}