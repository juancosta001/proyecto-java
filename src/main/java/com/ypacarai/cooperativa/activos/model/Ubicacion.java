package com.ypacarai.cooperativa.activos.model;

import java.time.LocalDateTime;

/**
 * Entidad que representa las ubicaciones (Casa Central y Sucursales)
 */
public class Ubicacion {
    
    public enum TipoUbicacion {
        Casa_Central, Sucursal
    }
    
    private Integer ubiId;
    private String ubiCodigo;
    private String ubiNombre;
    private TipoUbicacion ubiTipo;
    private String ubiDireccion;
    private String ubiTelefono;
    private Boolean activo;
    private LocalDateTime creadoEn;
    
    // Constructores
    public Ubicacion() {
        this.activo = true;
        this.creadoEn = LocalDateTime.now();
    }
    
    public Ubicacion(String codigo, String nombre, TipoUbicacion tipo, String direccion) {
        this();
        this.ubiCodigo = codigo;
        this.ubiNombre = nombre;
        this.ubiTipo = tipo;
        this.ubiDireccion = direccion;
    }
    
    // Getters y Setters
    public Integer getUbiId() {
        return ubiId;
    }
    
    public void setUbiId(Integer ubiId) {
        this.ubiId = ubiId;
    }
    
    public String getUbiCodigo() {
        return ubiCodigo;
    }
    
    public void setUbiCodigo(String ubiCodigo) {
        this.ubiCodigo = ubiCodigo;
    }
    
    public String getUbiNombre() {
        return ubiNombre;
    }
    
    public void setUbiNombre(String ubiNombre) {
        this.ubiNombre = ubiNombre;
    }
    
    public TipoUbicacion getUbiTipo() {
        return ubiTipo;
    }
    
    public void setUbiTipo(TipoUbicacion ubiTipo) {
        this.ubiTipo = ubiTipo;
    }
    
    public String getUbiDireccion() {
        return ubiDireccion;
    }
    
    public void setUbiDireccion(String ubiDireccion) {
        this.ubiDireccion = ubiDireccion;
    }
    
    public String getUbiTelefono() {
        return ubiTelefono;
    }
    
    public void setUbiTelefono(String ubiTelefono) {
        this.ubiTelefono = ubiTelefono;
    }
    
    public Boolean getActivo() {
        return activo;
    }
    
    public void setActivo(Boolean activo) {
        this.activo = activo;
    }
    
    public LocalDateTime getCreadoEn() {
        return creadoEn;
    }
    
    public void setCreadoEn(LocalDateTime creadoEn) {
        this.creadoEn = creadoEn;
    }
    
    @Override
    public String toString() {
        return ubiNombre + " (" + ubiCodigo + ")";
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        Ubicacion that = (Ubicacion) obj;
        return ubiId != null && ubiId.equals(that.ubiId);
    }
    
    @Override
    public int hashCode() {
        return ubiId != null ? ubiId.hashCode() : 0;
    }
}
