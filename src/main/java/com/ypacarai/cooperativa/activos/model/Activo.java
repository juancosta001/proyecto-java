package com.ypacarai.cooperativa.activos.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Modelo para la entidad ACTIVO
 * Sistema de Gestión de Activos - Cooperativa Ypacaraí LTDA
 */
public class Activo {
    
    public enum Estado {
        Operativo, En_Mantenimiento, Fuera_Servicio, Trasladado, En_Servicio_Externo
    }
    
    private int actId;
    private String actNumeroActivo;
    private int tipActId;
    private String actMarca;
    private String actModelo;
    private String actNumeroSerie;
    private String actEspecificaciones;
    private LocalDate actFechaAdquisicion;
    private Estado actEstado;
    private int actUbicacionActual;
    private String actResponsableActual;
    private String actObservaciones;
    private int creadoPor;
    private LocalDateTime creadoEn;
    private LocalDateTime actualizadoEn;
    
    // Campos relacionados (para joins)
    private String tipoActivoNombre;
    private String ubicacionNombre;
    private String usuarioCreadorNombre;
    
    // Constructores
    public Activo() {}
    
    public Activo(String actNumeroActivo, int tipActId, String actMarca, 
                 String actModelo, int actUbicacionActual, int creadoPor) {
        this.actNumeroActivo = actNumeroActivo;
        this.tipActId = tipActId;
        this.actMarca = actMarca;
        this.actModelo = actModelo;
        this.actUbicacionActual = actUbicacionActual;
        this.creadoPor = creadoPor;
        this.actEstado = Estado.Operativo;
    }
    
    // Getters y Setters
    public int getActId() {
        return actId;
    }
    
    public void setActId(int actId) {
        this.actId = actId;
    }
    
    public String getActNumeroActivo() {
        return actNumeroActivo;
    }
    
    public void setActNumeroActivo(String actNumeroActivo) {
        this.actNumeroActivo = actNumeroActivo;
    }
    
    public int getTipActId() {
        return tipActId;
    }
    
    public void setTipActId(int tipActId) {
        this.tipActId = tipActId;
    }
    
    public String getActMarca() {
        return actMarca;
    }
    
    public void setActMarca(String actMarca) {
        this.actMarca = actMarca;
    }
    
    public String getActModelo() {
        return actModelo;
    }
    
    public void setActModelo(String actModelo) {
        this.actModelo = actModelo;
    }
    
    public String getActNumeroSerie() {
        return actNumeroSerie;
    }
    
    public void setActNumeroSerie(String actNumeroSerie) {
        this.actNumeroSerie = actNumeroSerie;
    }
    
    public String getActEspecificaciones() {
        return actEspecificaciones;
    }
    
    public void setActEspecificaciones(String actEspecificaciones) {
        this.actEspecificaciones = actEspecificaciones;
    }
    
    public LocalDate getActFechaAdquisicion() {
        return actFechaAdquisicion;
    }
    
    public void setActFechaAdquisicion(LocalDate actFechaAdquisicion) {
        this.actFechaAdquisicion = actFechaAdquisicion;
    }
    
    public Estado getActEstado() {
        return actEstado;
    }
    
    public void setActEstado(Estado actEstado) {
        this.actEstado = actEstado;
    }
    
    public int getActUbicacionActual() {
        return actUbicacionActual;
    }
    
    public void setActUbicacionActual(int actUbicacionActual) {
        this.actUbicacionActual = actUbicacionActual;
    }
    
    public String getActResponsableActual() {
        return actResponsableActual;
    }
    
    public void setActResponsableActual(String actResponsableActual) {
        this.actResponsableActual = actResponsableActual;
    }
    
    public String getActObservaciones() {
        return actObservaciones;
    }
    
    public void setActObservaciones(String actObservaciones) {
        this.actObservaciones = actObservaciones;
    }
    
    public int getCreadoPor() {
        return creadoPor;
    }
    
    public void setCreadoPor(int creadoPor) {
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
    
    // Campos relacionados
    public String getTipoActivoNombre() {
        return tipoActivoNombre;
    }
    
    public void setTipoActivoNombre(String tipoActivoNombre) {
        this.tipoActivoNombre = tipoActivoNombre;
    }
    
    public String getUbicacionNombre() {
        return ubicacionNombre;
    }
    
    public void setUbicacionNombre(String ubicacionNombre) {
        this.ubicacionNombre = ubicacionNombre;
    }
    
    public String getUsuarioCreadorNombre() {
        return usuarioCreadorNombre;
    }
    
    public void setUsuarioCreadorNombre(String usuarioCreadorNombre) {
        this.usuarioCreadorNombre = usuarioCreadorNombre;
    }
    
    @Override
    public String toString() {
        return String.format("Activo{id=%d, numero='%s', tipo=%d, marca='%s', modelo='%s', estado=%s, ubicacion=%d}", 
                           actId, actNumeroActivo, tipActId, actMarca, actModelo, actEstado, actUbicacionActual);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Activo activo = (Activo) obj;
        return actId == activo.actId;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(actId);
    }
}
