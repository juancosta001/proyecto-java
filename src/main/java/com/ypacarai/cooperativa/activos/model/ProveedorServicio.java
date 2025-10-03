package com.ypacarai.cooperativa.activos.model;

import java.time.LocalDateTime;

/**
 * Modelo para Proveedores de Servicios Técnicos Tercerizados
 * Sistema de Gestión de Activos - Cooperativa Ypacaraí LTDA
 */
public class ProveedorServicio {
    
    private int prvId;
    private String prvNombre;
    private String prvNumeroTelefono;
    private String prvEmail;
    private String prvDireccion;
    private String prvContactoPrincipal;
    private String prvEspecialidades;
    private boolean activo;
    private LocalDateTime creadoEn;
    private LocalDateTime actualizadoEn;
    
    // Constructores
    public ProveedorServicio() {
        this.activo = true;
        this.creadoEn = LocalDateTime.now();
    }
    
    public ProveedorServicio(String prvNombre, String prvNumeroTelefono, String prvEmail, 
                           String prvContactoPrincipal, String prvEspecialidades) {
        this();
        this.prvNombre = prvNombre;
        this.prvNumeroTelefono = prvNumeroTelefono;
        this.prvEmail = prvEmail;
        this.prvContactoPrincipal = prvContactoPrincipal;
        this.prvEspecialidades = prvEspecialidades;
    }
    
    // Getters y Setters
    public int getPrvId() {
        return prvId;
    }
    
    public void setPrvId(int prvId) {
        this.prvId = prvId;
    }
    
    public String getPrvNombre() {
        return prvNombre;
    }
    
    public void setPrvNombre(String prvNombre) {
        this.prvNombre = prvNombre;
    }
    
    public String getPrvNumeroTelefono() {
        return prvNumeroTelefono;
    }
    
    public void setPrvNumeroTelefono(String prvNumeroTelefono) {
        this.prvNumeroTelefono = prvNumeroTelefono;
    }
    
    public String getPrvEmail() {
        return prvEmail;
    }
    
    public void setPrvEmail(String prvEmail) {
        this.prvEmail = prvEmail;
    }
    
    public String getPrvDireccion() {
        return prvDireccion;
    }
    
    public void setPrvDireccion(String prvDireccion) {
        this.prvDireccion = prvDireccion;
    }
    
    public String getPrvContactoPrincipal() {
        return prvContactoPrincipal;
    }
    
    public void setPrvContactoPrincipal(String prvContactoPrincipal) {
        this.prvContactoPrincipal = prvContactoPrincipal;
    }
    
    public String getPrvEspecialidades() {
        return prvEspecialidades;
    }
    
    public void setPrvEspecialidades(String prvEspecialidades) {
        this.prvEspecialidades = prvEspecialidades;
    }
    
    public boolean isActivo() {
        return activo;
    }
    
    public void setActivo(boolean activo) {
        this.activo = activo;
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
        return prvNombre + " - " + prvContactoPrincipal;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        ProveedorServicio that = (ProveedorServicio) obj;
        return prvId == that.prvId;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(prvId);
    }
}