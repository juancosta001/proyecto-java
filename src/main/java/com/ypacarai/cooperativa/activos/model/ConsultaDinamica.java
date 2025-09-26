package com.ypacarai.cooperativa.activos.model;

import java.util.List;
import java.util.Map;

/**
 * Clase para definir consultas dinámicas personalizables
 */
public class ConsultaDinamica {
    private String nombre;
    private String descripcion;
    private String tablaBase;
    private List<String> camposSeleccionados;
    private List<String> joins;
    private Map<String, Object> filtros;
    private List<String> camposAgrupacion;
    private Map<String, String> ordenamiento;
    private Map<String, Object> filtrosPostProceso;
    private int limite;
    private boolean exportarExcel;
    private boolean exportarPDF;
    private boolean exportarCSV;
    
    // Constructores
    public ConsultaDinamica() {}
    
    public ConsultaDinamica(String nombre, String tablaBase) {
        this.nombre = nombre;
        this.tablaBase = tablaBase;
    }
    
    // Getters y Setters
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    
    public String getTablaBase() { return tablaBase; }
    public void setTablaBase(String tablaBase) { this.tablaBase = tablaBase; }
    
    public List<String> getCamposSeleccionados() { return camposSeleccionados; }
    public void setCamposSeleccionados(List<String> camposSeleccionados) { this.camposSeleccionados = camposSeleccionados; }
    
    public List<String> getJoins() { return joins; }
    public void setJoins(List<String> joins) { this.joins = joins; }
    
    public Map<String, Object> getFiltros() { return filtros; }
    public void setFiltros(Map<String, Object> filtros) { this.filtros = filtros; }
    
    public List<String> getCamposAgrupacion() { return camposAgrupacion; }
    public void setCamposAgrupacion(List<String> camposAgrupacion) { this.camposAgrupacion = camposAgrupacion; }
    
    public Map<String, String> getOrdenamiento() { return ordenamiento; }
    public void setOrdenamiento(Map<String, String> ordenamiento) { this.ordenamiento = ordenamiento; }
    
    public Map<String, Object> getFiltrosPostProceso() { return filtrosPostProceso; }
    public void setFiltrosPostProceso(Map<String, Object> filtrosPostProceso) { this.filtrosPostProceso = filtrosPostProceso; }
    
    public int getLimite() { return limite; }
    public void setLimite(int limite) { this.limite = limite; }
    
    public boolean isExportarExcel() { return exportarExcel; }
    public void setExportarExcel(boolean exportarExcel) { this.exportarExcel = exportarExcel; }
    
    public boolean isExportarPDF() { return exportarPDF; }
    public void setExportarPDF(boolean exportarPDF) { this.exportarPDF = exportarPDF; }
    
    public boolean isExportarCSV() { return exportarCSV; }
    public void setExportarCSV(boolean exportarCSV) { this.exportarCSV = exportarCSV; }
    
    /**
     * Métodos de construcción fluida
     */
    public ConsultaDinamica conCampos(List<String> campos) {
        this.camposSeleccionados = campos;
        return this;
    }
    
    public ConsultaDinamica conFiltro(String campo, Object valor) {
        if (filtros == null) {
            filtros = new java.util.HashMap<>();
        }
        filtros.put(campo, valor);
        return this;
    }
    
    public ConsultaDinamica conOrden(String campo, String direccion) {
        if (ordenamiento == null) {
            ordenamiento = new java.util.HashMap<>();
        }
        ordenamiento.put(campo, direccion);
        return this;
    }
    
    public ConsultaDinamica conLimite(int limite) {
        this.limite = limite;
        return this;
    }
    
    /**
     * Valida que la consulta tenga los elementos mínimos
     */
    public boolean validar() {
        return nombre != null && !nombre.trim().isEmpty() &&
               tablaBase != null && !tablaBase.trim().isEmpty();
    }
    
    @Override
    public String toString() {
        return String.format("ConsultaDinamica{nombre='%s', tabla='%s', limite=%d}", 
                           nombre, tablaBase, limite);
    }
}
