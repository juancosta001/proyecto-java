package com.ypacarai.cooperativa.activos.model;

import java.time.LocalDateTime;

/**
 * Modelo para la entidad USUARIO
 * Sistema de Gestión de Activos - Cooperativa Ypacaraí LTDA
 */
public class Usuario {
    
    public enum Rol {
        Jefe_Informatica, Tecnico, Consulta
    }
    
    private int usuId;
    private String usuNombre;
    private String usuUsuario;
    private String usuPassword;
    private Rol usuRol;
    private String usuEmail;
    private boolean activo;
    private LocalDateTime creadoEn;
    private LocalDateTime actualizadoEn;
    
    // Constructores
    public Usuario() {}
    
    public Usuario(String usuNombre, String usuUsuario, String usuPassword, 
                  Rol usuRol, String usuEmail) {
        this.usuNombre = usuNombre;
        this.usuUsuario = usuUsuario;
        this.usuPassword = usuPassword;
        this.usuRol = usuRol;
        this.usuEmail = usuEmail;
        this.activo = true;
    }
    
    // Getters y Setters
    public int getUsuId() {
        return usuId;
    }
    
    public void setUsuId(int usuId) {
        this.usuId = usuId;
    }
    
    public String getUsuNombre() {
        return usuNombre;
    }
    
    public void setUsuNombre(String usuNombre) {
        this.usuNombre = usuNombre;
    }
    
    public String getUsuUsuario() {
        return usuUsuario;
    }
    
    public void setUsuUsuario(String usuUsuario) {
        this.usuUsuario = usuUsuario;
    }
    
    public String getUsuPassword() {
        return usuPassword;
    }
    
    public void setUsuPassword(String usuPassword) {
        this.usuPassword = usuPassword;
    }
    
    public Rol getUsuRol() {
        return usuRol;
    }
    
    public void setUsuRol(Rol usuRol) {
        this.usuRol = usuRol;
    }
    
    public String getUsuEmail() {
        return usuEmail;
    }
    
    public void setUsuEmail(String usuEmail) {
        this.usuEmail = usuEmail;
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
        return String.format("Usuario{id=%d, nombre='%s', usuario='%s', rol=%s, email='%s', activo=%s}", 
                           usuId, usuNombre, usuUsuario, usuRol, usuEmail, activo);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Usuario usuario = (Usuario) obj;
        return usuId == usuario.usuId;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(usuId);
    }
}
