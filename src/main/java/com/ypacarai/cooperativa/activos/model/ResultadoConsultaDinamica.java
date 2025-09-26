package com.ypacarai.cooperativa.activos.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Clase para almacenar resultados de consultas dinámicas
 */
public class ResultadoConsultaDinamica {
    private String nombreConsulta;
    private String descripcionConsulta;
    private LocalDate fechaEjecucion;
    private LocalDateTime horaEjecucion;
    private List<Map<String, Object>> datos;
    private List<String> columnasResultado;
    private int totalRegistros;
    private long tiempoEjecucionMs;
    private Map<String, Object> parametrosEjecucion;
    private String sqlEjecutado;
    private boolean exitoso;
    private String mensajeError;
    private Map<String, Object> estadisticasResumen;
    
    // Constructores
    public ResultadoConsultaDinamica() {
        this.fechaEjecucion = LocalDate.now();
        this.horaEjecucion = LocalDateTime.now();
        this.exitoso = true;
    }
    
    public ResultadoConsultaDinamica(String nombreConsulta) {
        this();
        this.nombreConsulta = nombreConsulta;
    }
    
    // Getters y Setters
    public String getNombreConsulta() { return nombreConsulta; }
    public void setNombreConsulta(String nombreConsulta) { this.nombreConsulta = nombreConsulta; }
    
    public String getDescripcionConsulta() { return descripcionConsulta; }
    public void setDescripcionConsulta(String descripcionConsulta) { this.descripcionConsulta = descripcionConsulta; }
    
    public LocalDate getFechaEjecucion() { return fechaEjecucion; }
    public void setFechaEjecucion(LocalDate fechaEjecucion) { this.fechaEjecucion = fechaEjecucion; }
    
    public LocalDateTime getHoraEjecucion() { return horaEjecucion; }
    public void setHoraEjecucion(LocalDateTime horaEjecucion) { this.horaEjecucion = horaEjecucion; }
    
    public List<Map<String, Object>> getDatos() { return datos; }
    public void setDatos(List<Map<String, Object>> datos) { 
        this.datos = datos;
        this.totalRegistros = datos != null ? datos.size() : 0;
        
        // Extraer nombres de columnas del primer registro
        if (datos != null && !datos.isEmpty()) {
            this.columnasResultado = new java.util.ArrayList<>(datos.get(0).keySet());
        }
    }
    
    public List<String> getColumnasResultado() { return columnasResultado; }
    public void setColumnasResultado(List<String> columnasResultado) { this.columnasResultado = columnasResultado; }
    
    public int getTotalRegistros() { return totalRegistros; }
    public void setTotalRegistros(int totalRegistros) { this.totalRegistros = totalRegistros; }
    
    public long getTiempoEjecucionMs() { return tiempoEjecucionMs; }
    public void setTiempoEjecucionMs(long tiempoEjecucionMs) { this.tiempoEjecucionMs = tiempoEjecucionMs; }
    
    public Map<String, Object> getParametrosEjecucion() { return parametrosEjecucion; }
    public void setParametrosEjecucion(Map<String, Object> parametrosEjecucion) { this.parametrosEjecucion = parametrosEjecucion; }
    
    public String getSqlEjecutado() { return sqlEjecutado; }
    public void setSqlEjecutado(String sqlEjecutado) { this.sqlEjecutado = sqlEjecutado; }
    
    public boolean isExitoso() { return exitoso; }
    public void setExitoso(boolean exitoso) { this.exitoso = exitoso; }
    
    public String getMensajeError() { return mensajeError; }
    public void setMensajeError(String mensajeError) { 
        this.mensajeError = mensajeError;
        if (mensajeError != null && !mensajeError.trim().isEmpty()) {
            this.exitoso = false;
        }
    }
    
    public Map<String, Object> getEstadisticasResumen() { return estadisticasResumen; }
    public void setEstadisticasResumen(Map<String, Object> estadisticasResumen) { this.estadisticasResumen = estadisticasResumen; }
    
    /**
     * Obtiene un valor específico de un registro
     */
    public Object getValor(int indiceRegistro, String columna) {
        if (datos != null && indiceRegistro >= 0 && indiceRegistro < datos.size()) {
            return datos.get(indiceRegistro).get(columna);
        }
        return null;
    }
    
    /**
     * Obtiene todos los valores de una columna específica
     */
    public List<Object> getValoresColumna(String columna) {
        if (datos == null) {
            return new java.util.ArrayList<>();
        }
        
        return datos.stream()
                .map(fila -> fila.get(columna))
                .collect(java.util.stream.Collectors.toList());
    }
    
    /**
     * Agrega una estadística de resumen
     */
    public void agregarEstadistica(String clave, Object valor) {
        if (estadisticasResumen == null) {
            estadisticasResumen = new java.util.HashMap<>();
        }
        estadisticasResumen.put(clave, valor);
    }
    
    /**
     * Verifica si la consulta tuvo resultados
     */
    public boolean tieneResultados() {
        return exitoso && datos != null && !datos.isEmpty();
    }
    
    /**
     * Obtiene un resumen textual del resultado
     */
    public String getResumenTexto() {
        if (!exitoso) {
            return String.format("Error en consulta '%s': %s", nombreConsulta, mensajeError);
        }
        
        return String.format("Consulta '%s' ejecutada exitosamente. %d registros encontrados en %dms",
                           nombreConsulta, totalRegistros, tiempoEjecucionMs);
    }
    
    @Override
    public String toString() {
        return String.format("ResultadoConsultaDinamica{consulta='%s', registros=%d, exitoso=%s}", 
                           nombreConsulta, totalRegistros, exitoso);
    }
}
