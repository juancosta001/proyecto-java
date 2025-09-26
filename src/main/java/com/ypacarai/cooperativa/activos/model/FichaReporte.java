package com.ypacarai.cooperativa.activos.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entidad que representa las fichas de reporte para mantenimientos correctivos
 */
public class FichaReporte {
    
    public enum EstadoFicha {
        Borrador, Enviada, Archivada
    }
    
    private Integer fichaId;
    private Integer mantId; // Mantenimiento asociado
    private String fichaNumero; // FR-YYYY-NNNN
    private LocalDate fichaFecha;
    private String fichaProblemaReportado;
    private String fichaDiagnostico;
    private String fichaSolucionAplicada;
    private String fichaComponentesCambio; // Componentes reemplazados
    private Integer fichaTiempoEstimado; // Minutos
    private Integer fichaTiempoReal; // Minutos reales
    private String fichaObservaciones;
    private String fichaTecnicoFirma; // Nombre del técnico
    private String fichaUsuarioFirma; // Usuario que reportó
    private EstadoFicha fichaEstado;
    private Integer creadoPor;
    private LocalDateTime creadoEn;
    private LocalDateTime actualizadoEn;
    
    // Constructores
    public FichaReporte() {
        this.fichaEstado = EstadoFicha.Borrador;
        this.fichaFecha = LocalDate.now();
        this.creadoEn = LocalDateTime.now();
        this.actualizadoEn = LocalDateTime.now();
    }
    
    public FichaReporte(Integer mantId, String problemaReportado, Integer creadoPor) {
        this();
        this.mantId = mantId;
        this.fichaProblemaReportado = problemaReportado;
        this.creadoPor = creadoPor;
    }
    
    // Getters y Setters
    public Integer getFichaId() {
        return fichaId;
    }
    
    public void setFichaId(Integer fichaId) {
        this.fichaId = fichaId;
    }
    
    public Integer getMantId() {
        return mantId;
    }
    
    public void setMantId(Integer mantId) {
        this.mantId = mantId;
    }
    
    public String getFichaNumero() {
        return fichaNumero;
    }
    
    public void setFichaNumero(String fichaNumero) {
        this.fichaNumero = fichaNumero;
    }
    
    public LocalDate getFichaFecha() {
        return fichaFecha;
    }
    
    public void setFichaFecha(LocalDate fichaFecha) {
        this.fichaFecha = fichaFecha;
    }
    
    public String getFichaProblemaReportado() {
        return fichaProblemaReportado;
    }
    
    public void setFichaProblemaReportado(String fichaProblemaReportado) {
        this.fichaProblemaReportado = fichaProblemaReportado;
    }
    
    public String getFichaDiagnostico() {
        return fichaDiagnostico;
    }
    
    public void setFichaDiagnostico(String fichaDiagnostico) {
        this.fichaDiagnostico = fichaDiagnostico;
    }
    
    public String getFichaSolucionAplicada() {
        return fichaSolucionAplicada;
    }
    
    public void setFichaSolucionAplicada(String fichaSolucionAplicada) {
        this.fichaSolucionAplicada = fichaSolucionAplicada;
    }
    
    public String getFichaComponentesCambio() {
        return fichaComponentesCambio;
    }
    
    public void setFichaComponentesCambio(String fichaComponentesCambio) {
        this.fichaComponentesCambio = fichaComponentesCambio;
    }
    
    public Integer getFichaTiempoEstimado() {
        return fichaTiempoEstimado;
    }
    
    public void setFichaTiempoEstimado(Integer fichaTiempoEstimado) {
        this.fichaTiempoEstimado = fichaTiempoEstimado;
    }
    
    public Integer getFichaTiempoReal() {
        return fichaTiempoReal;
    }
    
    public void setFichaTiempoReal(Integer fichaTiempoReal) {
        this.fichaTiempoReal = fichaTiempoReal;
    }
    
    public String getFichaObservaciones() {
        return fichaObservaciones;
    }
    
    public void setFichaObservaciones(String fichaObservaciones) {
        this.fichaObservaciones = fichaObservaciones;
    }
    
    public String getFichaTecnicoFirma() {
        return fichaTecnicoFirma;
    }
    
    public void setFichaTecnicoFirma(String fichaTecnicoFirma) {
        this.fichaTecnicoFirma = fichaTecnicoFirma;
    }
    
    public String getFichaUsuarioFirma() {
        return fichaUsuarioFirma;
    }
    
    public void setFichaUsuarioFirma(String fichaUsuarioFirma) {
        this.fichaUsuarioFirma = fichaUsuarioFirma;
    }
    
    public EstadoFicha getFichaEstado() {
        return fichaEstado;
    }
    
    public void setFichaEstado(EstadoFicha fichaEstado) {
        this.fichaEstado = fichaEstado;
    }
    
    public Integer getCreadoPor() {
        return creadoPor;
    }
    
    public void setCreadoPor(Integer creadoPor) {
        this.creadoPor = creadoPor;
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
    
    @Override
    public String toString() {
        return fichaNumero != null ? fichaNumero : "Ficha " + fichaId;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        FichaReporte that = (FichaReporte) obj;
        return fichaId != null && fichaId.equals(that.fichaId);
    }
    
    @Override
    public int hashCode() {
        return fichaId != null ? fichaId.hashCode() : 0;
    }
}
