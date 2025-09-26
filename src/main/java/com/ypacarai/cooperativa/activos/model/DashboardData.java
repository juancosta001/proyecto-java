package com.ypacarai.cooperativa.activos.model;

import java.time.LocalDateTime;

/**
 * Modelo para el Dashboard Ejecutivo
 * Contiene los KPIs principales del sistema
 */
public class DashboardData {
    // KPIs Principales
    private int totalActivos;
    private int mantenimientosPendientes;
    private int ticketsAbiertos;
    private int activosOperativos;
    private int activosEnMantenimiento;
    private int activosFueraServicio;
    
    // Estadísticas de Productividad
    private int mantenimientosCompletadosMes;
    private double tiempoPromedioResolucion;
    private int alertasCriticas;
    private int trasladosEnProceso;
    
    // Rankings y Tendencias
    private String activoMasProblematico;
    private int fallasMesActual;
    private int fallasMesAnterior;
    private String tecnicoMasProductivo;
    private int mantenimientosTecnicoMes;
    
    // Datos para gráficos
    private int[] fallasPorMes = new int[12]; // Últimos 12 meses
    private int[] mantenimientosPorTipo = new int[2]; // Preventivo, Correctivo
    private double[] costosPorMes = new double[12];
    
    private LocalDateTime fechaGeneracion;
    
    // Constructor
    public DashboardData() {
        this.fechaGeneracion = LocalDateTime.now();
    }
    
    // Getters y Setters para KPIs Principales
    public int getTotalActivos() { return totalActivos; }
    public void setTotalActivos(int totalActivos) { this.totalActivos = totalActivos; }
    
    public int getMantenimientosPendientes() { return mantenimientosPendientes; }
    public void setMantenimientosPendientes(int mantenimientosPendientes) { this.mantenimientosPendientes = mantenimientosPendientes; }
    
    public int getTicketsAbiertos() { return ticketsAbiertos; }
    public void setTicketsAbiertos(int ticketsAbiertos) { this.ticketsAbiertos = ticketsAbiertos; }
    
    public int getActivosOperativos() { return activosOperativos; }
    public void setActivosOperativos(int activosOperativos) { this.activosOperativos = activosOperativos; }
    
    public int getActivosEnMantenimiento() { return activosEnMantenimiento; }
    public void setActivosEnMantenimiento(int activosEnMantenimiento) { this.activosEnMantenimiento = activosEnMantenimiento; }
    
    public int getActivosFueraServicio() { return activosFueraServicio; }
    public void setActivosFueraServicio(int activosFueraServicio) { this.activosFueraServicio = activosFueraServicio; }
    
    // Getters y Setters para Estadísticas de Productividad
    public int getMantenimientosCompletadosMes() { return mantenimientosCompletadosMes; }
    public void setMantenimientosCompletadosMes(int mantenimientosCompletadosMes) { this.mantenimientosCompletadosMes = mantenimientosCompletadosMes; }
    
    public double getTiempoPromedioResolucion() { return tiempoPromedioResolucion; }
    public void setTiempoPromedioResolucion(double tiempoPromedioResolucion) { this.tiempoPromedioResolucion = tiempoPromedioResolucion; }
    
    public int getAlertasCriticas() { return alertasCriticas; }
    public void setAlertasCriticas(int alertasCriticas) { this.alertasCriticas = alertasCriticas; }
    
    public int getTrasladosEnProceso() { return trasladosEnProceso; }
    public void setTrasladosEnProceso(int trasladosEnProceso) { this.trasladosEnProceso = trasladosEnProceso; }
    
    // Getters y Setters para Rankings y Tendencias
    public String getActivoMasProblematico() { return activoMasProblematico; }
    public void setActivoMasProblematico(String activoMasProblematico) { this.activoMasProblematico = activoMasProblematico; }
    
    public int getFallasMesActual() { return fallasMesActual; }
    public void setFallasMesActual(int fallasMesActual) { this.fallasMesActual = fallasMesActual; }
    
    public int getFallasMesAnterior() { return fallasMesAnterior; }
    public void setFallasMesAnterior(int fallasMesAnterior) { this.fallasMesAnterior = fallasMesAnterior; }
    
    public String getTecnicoMasProductivo() { return tecnicoMasProductivo; }
    public void setTecnicoMasProductivo(String tecnicoMasProductivo) { this.tecnicoMasProductivo = tecnicoMasProductivo; }
    
    public int getMantenimientosTecnicoMes() { return mantenimientosTecnicoMes; }
    public void setMantenimientosTecnicoMes(int mantenimientosTecnicoMes) { this.mantenimientosTecnicoMes = mantenimientosTecnicoMes; }
    
    // Getters y Setters para datos de gráficos
    public int[] getFallasPorMes() { return fallasPorMes; }
    public void setFallasPorMes(int[] fallasPorMes) { this.fallasPorMes = fallasPorMes; }
    
    public int[] getMantenimientosPorTipo() { return mantenimientosPorTipo; }
    public void setMantenimientosPorTipo(int[] mantenimientosPorTipo) { this.mantenimientosPorTipo = mantenimientosPorTipo; }
    
    public double[] getCostosPorMes() { return costosPorMes; }
    public void setCostosPorMes(double[] costosPorMes) { this.costosPorMes = costosPorMes; }
    
    public LocalDateTime getFechaGeneracion() { return fechaGeneracion; }
    public void setFechaGeneracion(LocalDateTime fechaGeneracion) { this.fechaGeneracion = fechaGeneracion; }
    
    // Métodos de utilidad
    public double getPorcentajeActivosOperativos() {
        return totalActivos > 0 ? (activosOperativos * 100.0) / totalActivos : 0;
    }
    
    public double getTendenciaFallas() {
        return fallasMesAnterior > 0 ? ((fallasMesActual - fallasMesAnterior) * 100.0) / fallasMesAnterior : 0;
    }
    
    public String getEstadoSistema() {
        if (alertasCriticas > 5) return "CRÍTICO";
        if (alertasCriticas > 2) return "ADVERTENCIA";
        return "NORMAL";
    }
    
    @Override
    public String toString() {
        return String.format("Dashboard{activos=%d, pendientes=%d, tickets=%d, alertas=%d, estado='%s'}", 
                           totalActivos, mantenimientosPendientes, ticketsAbiertos, alertasCriticas, getEstadoSistema());
    }
}
