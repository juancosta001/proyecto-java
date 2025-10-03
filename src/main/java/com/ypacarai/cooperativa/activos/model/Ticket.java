package com.ypacarai.cooperativa.activos.model;

import java.time.LocalDateTime;

/**
 * Modelo para la entidad TICKET
 * Sistema de Gestión de Activos - Cooperativa Ypacaraí LTDA
 */
public class Ticket {
    
    public enum Tipo {
        Preventivo, Correctivo
    }
    
    public enum Prioridad {
        Baja, Media, Alta, Critica
    }
    
    public enum Estado {
        Abierto, En_Proceso, Resuelto, Cerrado, Cancelado
    }
    
    private int tickId;
    private int actId;
    private String tickNumero;
    private Tipo tickTipo;
    private Prioridad tickPrioridad;
    private String tickTitulo;
    private String tickDescripcion;
    private Estado tickEstado;
    private LocalDateTime tickFechaApertura;
    private LocalDateTime tickFechaVencimiento;
    private LocalDateTime tickFechaCierre;
    private Integer tickAsignadoA;
    private int tickReportadoPor;
    private String tickSolucion;
    private Integer tickTiempoResolucion;
    private boolean tickNotificacionEnviada;
    private LocalDateTime creadoEn;
    private LocalDateTime actualizadoEn;
    
    // Campos relacionados (para joins)
    private String activoNumero;
    private String tecnicoAsignado;
    private String usuarioReporta;
    private String ubicacionNombre;
    
    // Constructores
    public Ticket() {}
    
    public Ticket(int actId, Tipo tickTipo, Prioridad tickPrioridad, 
                 String tickTitulo, String tickDescripcion, int tickReportadoPor) {
        this.actId = actId;
        this.tickTipo = tickTipo;
        this.tickPrioridad = tickPrioridad;
        this.tickTitulo = tickTitulo;
        this.tickDescripcion = tickDescripcion;
        this.tickReportadoPor = tickReportadoPor;
        this.tickEstado = Estado.Abierto;
        this.tickNotificacionEnviada = false;
    }
    
    // Getters y Setters
    public int getTickId() {
        return tickId;
    }
    
    public void setTickId(int tickId) {
        this.tickId = tickId;
    }
    
    public int getActId() {
        return actId;
    }
    
    public void setActId(int actId) {
        this.actId = actId;
    }
    
    public String getTickNumero() {
        return tickNumero;
    }
    
    public void setTickNumero(String tickNumero) {
        this.tickNumero = tickNumero;
    }
    
    public Tipo getTickTipo() {
        return tickTipo;
    }
    
    public void setTickTipo(Tipo tickTipo) {
        this.tickTipo = tickTipo;
    }
    
    public Prioridad getTickPrioridad() {
        return tickPrioridad;
    }
    
    public void setTickPrioridad(Prioridad tickPrioridad) {
        this.tickPrioridad = tickPrioridad;
    }
    
    public String getTickTitulo() {
        return tickTitulo;
    }
    
    public void setTickTitulo(String tickTitulo) {
        this.tickTitulo = tickTitulo;
    }
    
    public String getTickDescripcion() {
        return tickDescripcion;
    }
    
    public void setTickDescripcion(String tickDescripcion) {
        this.tickDescripcion = tickDescripcion;
    }
    
    public Estado getTickEstado() {
        return tickEstado;
    }
    
    public void setTickEstado(Estado tickEstado) {
        this.tickEstado = tickEstado;
    }
    
    public LocalDateTime getTickFechaApertura() {
        return tickFechaApertura;
    }
    
    public void setTickFechaApertura(LocalDateTime tickFechaApertura) {
        this.tickFechaApertura = tickFechaApertura;
    }
    
    public LocalDateTime getTickFechaVencimiento() {
        return tickFechaVencimiento;
    }
    
    public void setTickFechaVencimiento(LocalDateTime tickFechaVencimiento) {
        this.tickFechaVencimiento = tickFechaVencimiento;
    }
    
    public LocalDateTime getTickFechaCierre() {
        return tickFechaCierre;
    }
    
    public void setTickFechaCierre(LocalDateTime tickFechaCierre) {
        this.tickFechaCierre = tickFechaCierre;
    }
    
    public Integer getTickAsignadoA() {
        return tickAsignadoA;
    }
    
    public void setTickAsignadoA(Integer tickAsignadoA) {
        this.tickAsignadoA = tickAsignadoA;
    }
    
    public int getTickReportadoPor() {
        return tickReportadoPor;
    }
    
    public void setTickReportadoPor(int tickReportadoPor) {
        this.tickReportadoPor = tickReportadoPor;
    }
    
    public String getTickSolucion() {
        return tickSolucion;
    }
    
    public void setTickSolucion(String tickSolucion) {
        this.tickSolucion = tickSolucion;
    }
    
    public Integer getTickTiempoResolucion() {
        return tickTiempoResolucion;
    }
    
    public void setTickTiempoResolucion(Integer tickTiempoResolucion) {
        this.tickTiempoResolucion = tickTiempoResolucion;
    }
    
    public boolean isTickNotificacionEnviada() {
        return tickNotificacionEnviada;
    }
    
    public void setTickNotificacionEnviada(boolean tickNotificacionEnviada) {
        this.tickNotificacionEnviada = tickNotificacionEnviada;
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
    public String getActivoNumero() {
        return activoNumero;
    }
    
    public void setActivoNumero(String activoNumero) {
        this.activoNumero = activoNumero;
    }
    
    public String getTecnicoAsignado() {
        return tecnicoAsignado;
    }
    
    public void setTecnicoAsignado(String tecnicoAsignado) {
        this.tecnicoAsignado = tecnicoAsignado;
    }
    
    public String getUsuarioReporta() {
        return usuarioReporta;
    }
    
    public void setUsuarioReporta(String usuarioReporta) {
        this.usuarioReporta = usuarioReporta;
    }
    
    public String getUbicacionNombre() {
        return ubicacionNombre;
    }
    
    public void setUbicacionNombre(String ubicacionNombre) {
        this.ubicacionNombre = ubicacionNombre;
    }
    
    @Override
    public String toString() {
        return String.format("Ticket{id=%d, numero='%s', tipo=%s, prioridad=%s, estado=%s, activo=%d}", 
                           tickId, tickNumero, tickTipo, tickPrioridad, tickEstado, actId);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Ticket ticket = (Ticket) obj;
        return tickId == ticket.tickId;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(tickId);
    }
}
