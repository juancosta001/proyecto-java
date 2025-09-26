package com.ypacarai.cooperativa.activos.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Modelo para el Reporte de Traslados
 * Contiene información agregada sobre los traslados de activos
 */
public class ReporteTraslados {
    private String numeroActivo;
    private String tipoActivo;
    private String numeroTraslado;
    private LocalDate fechaSalida;
    private LocalDate fechaRetorno;
    private String ubicacionOrigen;
    private String ubicacionDestino;
    private String estadoTraslado;
    private String motivoTraslado;
    private int diasEnUbicacion;
    private int totalTrasladosActivo;
    private String responsableEnvio;
    private String responsableRecibo;
    private LocalDateTime fechaGeneracion;
    
    // Constructores
    public ReporteTraslados() {
        this.fechaGeneracion = LocalDateTime.now();
    }
    
    public ReporteTraslados(String numeroActivo, String tipoActivo, String numeroTraslado, 
                           LocalDate fechaSalida, String ubicacionOrigen, String ubicacionDestino, String estadoTraslado) {
        this.numeroActivo = numeroActivo;
        this.tipoActivo = tipoActivo;
        this.numeroTraslado = numeroTraslado;
        this.fechaSalida = fechaSalida;
        this.ubicacionOrigen = ubicacionOrigen;
        this.ubicacionDestino = ubicacionDestino;
        this.estadoTraslado = estadoTraslado;
        this.fechaGeneracion = LocalDateTime.now();
    }
    
    // Getters y Setters
    public String getNumeroActivo() { return numeroActivo; }
    public void setNumeroActivo(String numeroActivo) { this.numeroActivo = numeroActivo; }
    
    public String getTipoActivo() { return tipoActivo; }
    public void setTipoActivo(String tipoActivo) { this.tipoActivo = tipoActivo; }
    
    public String getNumeroTraslado() { return numeroTraslado; }
    public void setNumeroTraslado(String numeroTraslado) { this.numeroTraslado = numeroTraslado; }
    
    public LocalDate getFechaSalida() { return fechaSalida; }
    public void setFechaSalida(LocalDate fechaSalida) { this.fechaSalida = fechaSalida; }
    
    public LocalDate getFechaRetorno() { return fechaRetorno; }
    public void setFechaRetorno(LocalDate fechaRetorno) { this.fechaRetorno = fechaRetorno; }
    
    public String getUbicacionOrigen() { return ubicacionOrigen; }
    public void setUbicacionOrigen(String ubicacionOrigen) { this.ubicacionOrigen = ubicacionOrigen; }
    
    public String getUbicacionDestino() { return ubicacionDestino; }
    public void setUbicacionDestino(String ubicacionDestino) { this.ubicacionDestino = ubicacionDestino; }
    
    public String getEstadoTraslado() { return estadoTraslado; }
    public void setEstadoTraslado(String estadoTraslado) { this.estadoTraslado = estadoTraslado; }
    
    public String getMotivoTraslado() { return motivoTraslado; }
    public void setMotivoTraslado(String motivoTraslado) { this.motivoTraslado = motivoTraslado; }
    
    public int getDiasEnUbicacion() { return diasEnUbicacion; }
    public void setDiasEnUbicacion(int diasEnUbicacion) { this.diasEnUbicacion = diasEnUbicacion; }
    
    public int getTotalTrasladosActivo() { return totalTrasladosActivo; }
    public void setTotalTrasladosActivo(int totalTrasladosActivo) { this.totalTrasladosActivo = totalTrasladosActivo; }
    
    public String getResponsableEnvio() { return responsableEnvio; }
    public void setResponsableEnvio(String responsableEnvio) { this.responsableEnvio = responsableEnvio; }
    
    public String getResponsableRecibo() { return responsableRecibo; }
    public void setResponsableRecibo(String responsableRecibo) { this.responsableRecibo = responsableRecibo; }
    
    public LocalDateTime getFechaGeneracion() { return fechaGeneracion; }
    public void setFechaGeneracion(LocalDateTime fechaGeneracion) { this.fechaGeneracion = fechaGeneracion; }
    
    @Override
    public String toString() {
        return String.format("ReporteTraslados{activo='%s', traslado='%s', origen='%s', destino='%s', estado='%s'}", 
                           numeroActivo, numeroTraslado, ubicacionOrigen, ubicacionDestino, estadoTraslado);
    }
}
