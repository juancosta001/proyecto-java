package com.ypacarai.cooperativa.activos.model;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Clase contenedora para reportes completos con análisis estadístico
 */
public class ReporteCompleto {
    private String tipoReporte;
    private LocalDate fechaGeneracion;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private List<?> datosOriginales;
    private Map<String, Object> estadisticas;
    private String resumenEjecutivo;
    private Map<String, Object> metadatos;
    private int totalRegistros;
    private String formatoExportacion;
    
    // Constructores
    public ReporteCompleto() {
        this.fechaGeneracion = LocalDate.now();
    }
    
    public ReporteCompleto(String tipoReporte) {
        this.tipoReporte = tipoReporte;
        this.fechaGeneracion = LocalDate.now();
    }
    
    // Getters y Setters
    public String getTipoReporte() { return tipoReporte; }
    public void setTipoReporte(String tipoReporte) { this.tipoReporte = tipoReporte; }
    
    public LocalDate getFechaGeneracion() { return fechaGeneracion; }
    public void setFechaGeneracion(LocalDate fechaGeneracion) { this.fechaGeneracion = fechaGeneracion; }
    
    public LocalDate getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDate fechaInicio) { this.fechaInicio = fechaInicio; }
    
    public LocalDate getFechaFin() { return fechaFin; }
    public void setFechaFin(LocalDate fechaFin) { this.fechaFin = fechaFin; }
    
    public List<?> getDatosOriginales() { return datosOriginales; }
    public void setDatosOriginales(List<?> datosOriginales) { 
        this.datosOriginales = datosOriginales;
        this.totalRegistros = datosOriginales != null ? datosOriginales.size() : 0;
    }
    
    public Map<String, Object> getEstadisticas() { return estadisticas; }
    public void setEstadisticas(Map<String, Object> estadisticas) { this.estadisticas = estadisticas; }
    
    public String getResumenEjecutivo() { return resumenEjecutivo; }
    public void setResumenEjecutivo(String resumenEjecutivo) { this.resumenEjecutivo = resumenEjecutivo; }
    
    public Map<String, Object> getMetadatos() { return metadatos; }
    public void setMetadatos(Map<String, Object> metadatos) { this.metadatos = metadatos; }
    
    public int getTotalRegistros() { return totalRegistros; }
    public void setTotalRegistros(int totalRegistros) { this.totalRegistros = totalRegistros; }
    
    public String getFormatoExportacion() { return formatoExportacion; }
    public void setFormatoExportacion(String formatoExportacion) { this.formatoExportacion = formatoExportacion; }
    
    /**
     * Obtiene un dato específico de las estadísticas
     */
    public Object getEstadistica(String clave) {
        return estadisticas != null ? estadisticas.get(clave) : null;
    }
    
    /**
     * Agrega una estadística
     */
    public void agregarEstadistica(String clave, Object valor) {
        if (estadisticas == null) {
            estadisticas = new java.util.HashMap<>();
        }
        estadisticas.put(clave, valor);
    }
    
    /**
     * Obtiene un metadato específico
     */
    public Object getMetadato(String clave) {
        return metadatos != null ? metadatos.get(clave) : null;
    }
    
    /**
     * Agrega un metadato
     */
    public void agregarMetadato(String clave, Object valor) {
        if (metadatos == null) {
            metadatos = new java.util.HashMap<>();
        }
        metadatos.put(clave, valor);
    }
    
    /**
     * Verifica si el reporte tiene datos
     */
    public boolean tieneDatos() {
        return datosOriginales != null && !datosOriginales.isEmpty();
    }
    
    @Override
    public String toString() {
        return String.format("ReporteCompleto{tipo='%s', fecha=%s, registros=%d}", 
                           tipoReporte, fechaGeneracion, totalRegistros);
    }
}
