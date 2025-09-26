package com.ypacarai.cooperativa.activos.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Modelo para el Reporte de Fallas
 * Contiene información agregada sobre las fallas de los activos
 */
public class ReporteFallas {
    private String tipoActivo;
    private String descripcionFalla;
    private String numeroActivo;
    private int frecuenciaFallas;
    private double indiceFallas; // Fallas por mes
    private LocalDate fechaUltimaFalla;
    private String estadoReparacion;
    private double efectividadReparacion; // Porcentaje de éxito
    private String tendenciaPeriodo;
    private int totalFallasRegistradas;
    private LocalDateTime fechaGeneracion;
    
    // Constructores
    public ReporteFallas() {
        this.fechaGeneracion = LocalDateTime.now();
    }
    
    public ReporteFallas(String tipoActivo, String descripcionFalla, String numeroActivo, 
                        int frecuenciaFallas, double indiceFallas) {
        this.tipoActivo = tipoActivo;
        this.descripcionFalla = descripcionFalla;
        this.numeroActivo = numeroActivo;
        this.frecuenciaFallas = frecuenciaFallas;
        this.indiceFallas = indiceFallas;
        this.fechaGeneracion = LocalDateTime.now();
    }
    
    // Getters y Setters
    public String getTipoActivo() { return tipoActivo; }
    public void setTipoActivo(String tipoActivo) { this.tipoActivo = tipoActivo; }
    
    public String getDescripcionFalla() { return descripcionFalla; }
    public void setDescripcionFalla(String descripcionFalla) { this.descripcionFalla = descripcionFalla; }
    
    public String getNumeroActivo() { return numeroActivo; }
    public void setNumeroActivo(String numeroActivo) { this.numeroActivo = numeroActivo; }
    
    public int getFrecuenciaFallas() { return frecuenciaFallas; }
    public void setFrecuenciaFallas(int frecuenciaFallas) { this.frecuenciaFallas = frecuenciaFallas; }
    
    public double getIndiceFallas() { return indiceFallas; }
    public void setIndiceFallas(double indiceFallas) { this.indiceFallas = indiceFallas; }
    
    public LocalDate getFechaUltimaFalla() { return fechaUltimaFalla; }
    public void setFechaUltimaFalla(LocalDate fechaUltimaFalla) { this.fechaUltimaFalla = fechaUltimaFalla; }
    
    public String getEstadoReparacion() { return estadoReparacion; }
    public void setEstadoReparacion(String estadoReparacion) { this.estadoReparacion = estadoReparacion; }
    
    public double getEfectividadReparacion() { return efectividadReparacion; }
    public void setEfectividadReparacion(double efectividadReparacion) { this.efectividadReparacion = efectividadReparacion; }
    
    public String getTendenciaPeriodo() { return tendenciaPeriodo; }
    public void setTendenciaPeriodo(String tendenciaPeriodo) { this.tendenciaPeriodo = tendenciaPeriodo; }
    
    public int getTotalFallasRegistradas() { return totalFallasRegistradas; }
    public void setTotalFallasRegistradas(int totalFallasRegistradas) { this.totalFallasRegistradas = totalFallasRegistradas; }
    
    public LocalDateTime getFechaGeneracion() { return fechaGeneracion; }
    public void setFechaGeneracion(LocalDateTime fechaGeneracion) { this.fechaGeneracion = fechaGeneracion; }
    
    @Override
    public String toString() {
        return String.format("ReporteFallas{activo='%s', tipo='%s', frecuencia=%d, indice=%.2f, efectividad=%.1f%%}", 
                           numeroActivo, tipoActivo, frecuenciaFallas, indiceFallas, efectividadReparacion);
    }
}
