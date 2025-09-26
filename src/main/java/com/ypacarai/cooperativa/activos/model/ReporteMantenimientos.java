package com.ypacarai.cooperativa.activos.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Modelo para el Reporte de Mantenimientos
 * Contiene información agregada sobre los mantenimientos realizados
 */
public class ReporteMantenimientos {
    private String tipoMantenimiento;
    private String tipoActivo;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private int totalMantenimientos;
    private double tiempoPromedioResolucion; // En horas
    private String tecnicoAsignado;
    private int productividadTecnico;
    private double costoTotal;
    private String estadoMantenimiento;
    private LocalDateTime fechaGeneracion;
    
    // Constructores
    public ReporteMantenimientos() {
        this.fechaGeneracion = LocalDateTime.now();
    }
    
    public ReporteMantenimientos(String tipoMantenimiento, String tipoActivo, int totalMantenimientos, 
                                double tiempoPromedioResolucion, String tecnicoAsignado) {
        this.tipoMantenimiento = tipoMantenimiento;
        this.tipoActivo = tipoActivo;
        this.totalMantenimientos = totalMantenimientos;
        this.tiempoPromedioResolucion = tiempoPromedioResolucion;
        this.tecnicoAsignado = tecnicoAsignado;
        this.fechaGeneracion = LocalDateTime.now();
    }
    
    // Getters y Setters
    public String getTipoMantenimiento() { return tipoMantenimiento; }
    public void setTipoMantenimiento(String tipoMantenimiento) { this.tipoMantenimiento = tipoMantenimiento; }
    
    public String getTipoActivo() { return tipoActivo; }
    public void setTipoActivo(String tipoActivo) { this.tipoActivo = tipoActivo; }
    
    public LocalDate getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDate fechaInicio) { this.fechaInicio = fechaInicio; }
    
    public LocalDate getFechaFin() { return fechaFin; }
    public void setFechaFin(LocalDate fechaFin) { this.fechaFin = fechaFin; }
    
    public int getTotalMantenimientos() { return totalMantenimientos; }
    public void setTotalMantenimientos(int totalMantenimientos) { this.totalMantenimientos = totalMantenimientos; }
    
    public double getTiempoPromedioResolucion() { return tiempoPromedioResolucion; }
    public void setTiempoPromedioResolucion(double tiempoPromedioResolucion) { 
        this.tiempoPromedioResolucion = tiempoPromedioResolucion; 
    }
    
    public String getTecnicoAsignado() { return tecnicoAsignado; }
    public void setTecnicoAsignado(String tecnicoAsignado) { this.tecnicoAsignado = tecnicoAsignado; }
    
    public int getProductividadTecnico() { return productividadTecnico; }
    public void setProductividadTecnico(int productividadTecnico) { this.productividadTecnico = productividadTecnico; }
    
    public double getCostoTotal() { return costoTotal; }
    public void setCostoTotal(double costoTotal) { this.costoTotal = costoTotal; }
    
    public String getEstadoMantenimiento() { return estadoMantenimiento; }
    public void setEstadoMantenimiento(String estadoMantenimiento) { this.estadoMantenimiento = estadoMantenimiento; }
    
    public LocalDateTime getFechaGeneracion() { return fechaGeneracion; }
    public void setFechaGeneracion(LocalDateTime fechaGeneracion) { this.fechaGeneracion = fechaGeneracion; }
    
    @Override
    public String toString() {
        return String.format("ReporteMantenimientos{tipo='%s', activo='%s', total=%d, tiempo=%.1fh, tecnico='%s'}", 
                           tipoMantenimiento, tipoActivo, totalMantenimientos, tiempoPromedioResolucion, tecnicoAsignado);
    }
}
