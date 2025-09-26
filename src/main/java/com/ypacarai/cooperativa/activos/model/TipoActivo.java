package com.ypacarai.cooperativa.activos.model;

/**
 * Entidad que representa los tipos de activos en el sistema
 * Solo PC e Impresoras según protocolo
 */
public class TipoActivo {
    
    private Integer tipActId;
    private String nombre;
    private String descripcion;
    private Boolean activo;
    
    // Constructores
    public TipoActivo() {
        this.activo = true;
    }
    
    public TipoActivo(String nombre, String descripcion) {
        this();
        this.nombre = nombre;
        this.descripcion = descripcion;
    }
    
    // Getters y Setters
    public Integer getTipActId() {
        return tipActId;
    }
    
    public void setTipActId(Integer tipActId) {
        this.tipActId = tipActId;
    }
    
    public String getNombre() {
        return nombre;
    }
    
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
    
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    
    public Boolean getActivo() {
        return activo;
    }
    
    public void setActivo(Boolean activo) {
        this.activo = activo;
    }
    
    @Override
    public String toString() {
        return nombre;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        TipoActivo that = (TipoActivo) obj;
        return tipActId != null && tipActId.equals(that.tipActId);
    }
    
    @Override
    public int hashCode() {
        return tipActId != null ? tipActId.hashCode() : 0;
    }
}
