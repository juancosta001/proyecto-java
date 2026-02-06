package com.ypacarai.cooperativa.activos.model;

import java.time.LocalDate;
import java.util.Map;

/**
 * Clase para manejar filtros de reportes de manera unificada
 */
public class FiltrosReporte {
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private String tipoReporte;
    private String tipoActivo;
    private String ubicacion;
    private String ubicacionOrigen;
    private String ubicacionDestino;
    private String tipoMantenimiento;
    private Integer tecnicoId;
    private String estado;
    private String prioridad;
    private Map<String, Object> filtrosPersonalizados;
    
    // Constructores
    public FiltrosReporte() {}
    
    public FiltrosReporte(LocalDate fechaInicio, LocalDate fechaFin) {
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
    }
    
    // Getters y Setters
    public LocalDate getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDate fechaInicio) { this.fechaInicio = fechaInicio; }
    
    public LocalDate getFechaFin() { return fechaFin; }
    public void setFechaFin(LocalDate fechaFin) { this.fechaFin = fechaFin; }
    
    public String getTipoReporte() { return tipoReporte; }
    public void setTipoReporte(String tipoReporte) { this.tipoReporte = tipoReporte; }
    
    public String getTipoActivo() { return tipoActivo; }
    public void setTipoActivo(String tipoActivo) { this.tipoActivo = tipoActivo; }
    
    public String getUbicacion() { return ubicacion; }
    public void setUbicacion(String ubicacion) { this.ubicacion = ubicacion; }
    
    public String getUbicacionOrigen() { return ubicacionOrigen; }
    public void setUbicacionOrigen(String ubicacionOrigen) { this.ubicacionOrigen = ubicacionOrigen; }
    
    public String getUbicacionDestino() { return ubicacionDestino; }
    public void setUbicacionDestino(String ubicacionDestino) { this.ubicacionDestino = ubicacionDestino; }
    
    public String getTipoMantenimiento() { return tipoMantenimiento; }
    public void setTipoMantenimiento(String tipoMantenimiento) { this.tipoMantenimiento = tipoMantenimiento; }
    
    public Integer getTecnicoId() { return tecnicoId; }
    public void setTecnicoId(Integer tecnicoId) { this.tecnicoId = tecnicoId; }
    
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    
    public String getPrioridad() { return prioridad; }
    public void setPrioridad(String prioridad) { this.prioridad = prioridad; }
    
    public Map<String, Object> getFiltrosPersonalizados() { return filtrosPersonalizados; }
    public void setFiltrosPersonalizados(Map<String, Object> filtrosPersonalizados) { 
        this.filtrosPersonalizados = filtrosPersonalizados; 
    }
    
    /**
     * Métodos de utilidad para construcción fluida
     */
    public FiltrosReporte conFechas(LocalDate inicio, LocalDate fin) {
        this.fechaInicio = inicio;
        this.fechaFin = fin;
        return this;
    }
    
    public FiltrosReporte conTipoActivo(String tipo) {
        this.tipoActivo = tipo;
        return this;
    }
    
    public FiltrosReporte conUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
        return this;
    }
    
    public FiltrosReporte conTecnico(Integer tecnicoId) {
        this.tecnicoId = tecnicoId;
        return this;
    }
    
    /**
     * Valida si los filtros de fecha son válidos
     * Si las fechas son null, se considera válido (sin filtro de fecha)
     * Si solo una fecha está presente, se considera inválido
     * Si ambas están presentes, valida que fechaInicio <= fechaFin
     */
    public boolean validarFechas() {
        // Si ambas son null, es válido (sin filtro)
        if (fechaInicio == null && fechaFin == null) {
            return true;
        }
        
        // Si solo una está presente, es inválido
        if (fechaInicio == null || fechaFin == null) {
            return false;
        }
        
        // Si ambas están presentes, validar orden
        return !fechaInicio.isAfter(fechaFin);
    }
    
    @Override
    public String toString() {
        return String.format("FiltrosReporte{fechaInicio=%s, fechaFin=%s, tipoActivo='%s', ubicacion='%s'}", 
                           fechaInicio, fechaFin, tipoActivo, ubicacion);
    }
}
