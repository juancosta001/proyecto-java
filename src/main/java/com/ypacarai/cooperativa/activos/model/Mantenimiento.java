package com.ypacarai.cooperativa.activos.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entidad que representa los mantenimientos realizados a los activos
 */
public class Mantenimiento {
    
    public enum TipoMantenimiento {
        Preventivo, Correctivo
    }
    
    public enum EstadoMantenimiento {
        Programado, En_Proceso, Completado, Suspendido
    }
    
    private Integer mantId;
    private Integer tickId; // Ticket asociado
    private Integer actId; // Activo
    private Integer planId; // Plan de mantenimiento (si es preventivo)
    private LocalDateTime mantFechaInicio;
    private LocalDateTime mantFechaFin;
    private TipoMantenimiento mantTipo;
    private String mantDescripcionInicial; // Problema reportado
    private String mantDiagnostico; // Diagnóstico técnico
    private String mantProcedimiento; // Pasos realizados
    private String mantResultado; // Resultado obtenido
    private LocalDate mantProximaFecha; // Próximo mantenimiento preventivo
    private Integer mantTecnicoAsignado;
    private EstadoMantenimiento mantEstado;
    private String mantObservaciones;
    private LocalDateTime creadoEn;
    private LocalDateTime actualizadoEn;
    
    // Constructores
    public Mantenimiento() {
        this.mantEstado = EstadoMantenimiento.Programado;
        this.creadoEn = LocalDateTime.now();
        this.actualizadoEn = LocalDateTime.now();
    }
    
    public Mantenimiento(Integer tickId, Integer actId, TipoMantenimiento tipo, Integer tecnicoAsignado) {
        this();
        this.tickId = tickId;
        this.actId = actId;
        this.mantTipo = tipo;
        this.mantTecnicoAsignado = tecnicoAsignado;
        this.mantFechaInicio = LocalDateTime.now();
    }
    
    // Getters y Setters
    public Integer getMantId() {
        return mantId;
    }
    
    public void setMantId(Integer mantId) {
        this.mantId = mantId;
    }
    
    public Integer getTickId() {
        return tickId;
    }
    
    public void setTickId(Integer tickId) {
        this.tickId = tickId;
    }
    
    public Integer getActId() {
        return actId;
    }
    
    public void setActId(Integer actId) {
        this.actId = actId;
    }
    
    public Integer getPlanId() {
        return planId;
    }
    
    public void setPlanId(Integer planId) {
        this.planId = planId;
    }
    
    public LocalDateTime getMantFechaInicio() {
        return mantFechaInicio;
    }
    
    public void setMantFechaInicio(LocalDateTime mantFechaInicio) {
        this.mantFechaInicio = mantFechaInicio;
    }
    
    public LocalDateTime getMantFechaFin() {
        return mantFechaFin;
    }
    
    public void setMantFechaFin(LocalDateTime mantFechaFin) {
        this.mantFechaFin = mantFechaFin;
    }
    
    public TipoMantenimiento getMantTipo() {
        return mantTipo;
    }
    
    public void setMantTipo(TipoMantenimiento mantTipo) {
        this.mantTipo = mantTipo;
    }
    
    public String getMantDescripcionInicial() {
        return mantDescripcionInicial;
    }
    
    public void setMantDescripcionInicial(String mantDescripcionInicial) {
        this.mantDescripcionInicial = mantDescripcionInicial;
    }
    
    public String getMantDiagnostico() {
        return mantDiagnostico;
    }
    
    public void setMantDiagnostico(String mantDiagnostico) {
        this.mantDiagnostico = mantDiagnostico;
    }
    
    public String getMantProcedimiento() {
        return mantProcedimiento;
    }
    
    public void setMantProcedimiento(String mantProcedimiento) {
        this.mantProcedimiento = mantProcedimiento;
    }
    
    public String getMantResultado() {
        return mantResultado;
    }
    
    public void setMantResultado(String mantResultado) {
        this.mantResultado = mantResultado;
    }
    
    public LocalDate getMantProximaFecha() {
        return mantProximaFecha;
    }
    
    public void setMantProximaFecha(LocalDate mantProximaFecha) {
        this.mantProximaFecha = mantProximaFecha;
    }
    
    public Integer getMantTecnicoAsignado() {
        return mantTecnicoAsignado;
    }
    
    public void setMantTecnicoAsignado(Integer mantTecnicoAsignado) {
        this.mantTecnicoAsignado = mantTecnicoAsignado;
    }
    
    public EstadoMantenimiento getMantEstado() {
        return mantEstado;
    }
    
    public void setMantEstado(EstadoMantenimiento mantEstado) {
        this.mantEstado = mantEstado;
    }
    
    public String getMantObservaciones() {
        return mantObservaciones;
    }
    
    public void setMantObservaciones(String mantObservaciones) {
        this.mantObservaciones = mantObservaciones;
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
    
    /**
     * Calcula la duración del mantenimiento en minutos
     */
    public Integer getDuracionMinutos() {
        if (mantFechaInicio != null && mantFechaFin != null) {
            return (int) java.time.Duration.between(mantFechaInicio, mantFechaFin).toMinutes();
        }
        return null;
    }
    
    @Override
    public String toString() {
        return "Mantenimiento " + mantTipo + " - " + mantEstado;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        Mantenimiento that = (Mantenimiento) obj;
        return mantId != null && mantId.equals(that.mantId);
    }
    
    @Override
    public int hashCode() {
        return mantId != null ? mantId.hashCode() : 0;
    }
}
