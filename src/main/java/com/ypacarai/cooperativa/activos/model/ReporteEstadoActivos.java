package com.ypacarai.cooperativa.activos.model;

import java.time.LocalDate;

/**
 * Modelo para el Reporte de Estado de Activos
 * Contiene información agregada sobre el estado de los activos
 */
public class ReporteEstadoActivos {
    private String tipoActivo;
    private String estado;
    private int cantidadTotal;
    private int activosProximosMantenimiento;
    private int activosMantenimientoVencido;
    private String ubicacion;
    private double valorTotal;
    private LocalDate fechaConsulta;
    
    // Constructores
    public ReporteEstadoActivos() {}
    
    public ReporteEstadoActivos(String tipoActivo, String estado, int cantidadTotal, String ubicacion) {
        this.tipoActivo = tipoActivo;
        this.estado = estado;
        this.cantidadTotal = cantidadTotal;
        this.ubicacion = ubicacion;
        this.fechaConsulta = LocalDate.now();
    }
    
    // Getters y Setters
    public String getTipoActivo() { return tipoActivo; }
    public void setTipoActivo(String tipoActivo) { this.tipoActivo = tipoActivo; }
    
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    
    public int getCantidadTotal() { return cantidadTotal; }
    public void setCantidadTotal(int cantidadTotal) { this.cantidadTotal = cantidadTotal; }
    
    public int getActivosProximosMantenimiento() { return activosProximosMantenimiento; }
    public void setActivosProximosMantenimiento(int activosProximosMantenimiento) { 
        this.activosProximosMantenimiento = activosProximosMantenimiento; 
    }
    
    public int getActivosMantenimientoVencido() { return activosMantenimientoVencido; }
    public void setActivosMantenimientoVencido(int activosMantenimientoVencido) { 
        this.activosMantenimientoVencido = activosMantenimientoVencido; 
    }
    
    public String getUbicacion() { return ubicacion; }
    public void setUbicacion(String ubicacion) { this.ubicacion = ubicacion; }
    
    public double getValorTotal() { return valorTotal; }
    public void setValorTotal(double valorTotal) { this.valorTotal = valorTotal; }
    
    public LocalDate getFechaConsulta() { return fechaConsulta; }
    public void setFechaConsulta(LocalDate fechaConsulta) { this.fechaConsulta = fechaConsulta; }
    
    @Override
    public String toString() {
        return String.format("ReporteEstadoActivos{tipo='%s', estado='%s', cantidad=%d, ubicacion='%s'}", 
                           tipoActivo, estado, cantidadTotal, ubicacion);
    }
}
