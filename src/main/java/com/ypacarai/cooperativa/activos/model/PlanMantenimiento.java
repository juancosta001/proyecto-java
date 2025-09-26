package com.ypacarai.cooperativa.activos.model;

import java.time.LocalDateTime;

/**
 * Entidad que representa los planes de mantenimiento preventivo
 */
public class PlanMantenimiento {
    
    private Integer planId;
    private Integer tipActId;
    private String planNombre;
    private String planDescripcion;
    private Integer planFrecuenciaDias; // Cada cuántos días
    private Integer planDiasAlerta; // Días antes para generar alerta
    private Boolean planActivo;
    private String planProcedimiento; // Pasos del mantenimiento
    private Integer creadoPor;
    private LocalDateTime creadoEn;
    private LocalDateTime actualizadoEn;
    
    // Constructores
    public PlanMantenimiento() {
        this.planActivo = true;
        this.creadoEn = LocalDateTime.now();
        this.actualizadoEn = LocalDateTime.now();
        this.planFrecuenciaDias = 90;
        this.planDiasAlerta = 7;
    }
    
    public PlanMantenimiento(Integer tipActId, String nombre, String descripcion, Integer frecuenciaDias) {
        this();
        this.tipActId = tipActId;
        this.planNombre = nombre;
        this.planDescripcion = descripcion;
        this.planFrecuenciaDias = frecuenciaDias;
    }
    
    // Getters y Setters
    public Integer getPlanId() {
        return planId;
    }
    
    public void setPlanId(Integer planId) {
        this.planId = planId;
    }
    
    public Integer getTipActId() {
        return tipActId;
    }
    
    public void setTipActId(Integer tipActId) {
        this.tipActId = tipActId;
    }
    
    public String getPlanNombre() {
        return planNombre;
    }
    
    public void setPlanNombre(String planNombre) {
        this.planNombre = planNombre;
    }
    
    public String getPlanDescripcion() {
        return planDescripcion;
    }
    
    public void setPlanDescripcion(String planDescripcion) {
        this.planDescripcion = planDescripcion;
    }
    
    public Integer getPlanFrecuenciaDias() {
        return planFrecuenciaDias;
    }
    
    public void setPlanFrecuenciaDias(Integer planFrecuenciaDias) {
        this.planFrecuenciaDias = planFrecuenciaDias;
    }
    
    public Integer getPlanDiasAlerta() {
        return planDiasAlerta;
    }
    
    public void setPlanDiasAlerta(Integer planDiasAlerta) {
        this.planDiasAlerta = planDiasAlerta;
    }
    
    public Boolean getPlanActivo() {
        return planActivo;
    }
    
    public void setPlanActivo(Boolean planActivo) {
        this.planActivo = planActivo;
    }
    
    public String getPlanProcedimiento() {
        return planProcedimiento;
    }
    
    public void setPlanProcedimiento(String planProcedimiento) {
        this.planProcedimiento = planProcedimiento;
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
        return planNombre + " (cada " + planFrecuenciaDias + " días)";
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        PlanMantenimiento that = (PlanMantenimiento) obj;
        return planId != null && planId.equals(that.planId);
    }
    
    @Override
    public int hashCode() {
        return planId != null ? planId.hashCode() : 0;
    }
}
