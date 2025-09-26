package com.ypacarai.cooperativa.activos.model;

import java.time.LocalDateTime;

/**
 * Configuración de mantenimientos preventivos por tipo de activo
 */
public class ConfiguracionMantenimiento {
    
    public enum TipoActivo {
        PC_Escritorio("PC de Escritorio"),
        Laptop("Laptop"),
        Impresora_Laser("Impresora Láser"),
        Impresora_Inyeccion("Impresora Inyección"),
        Switch_Red("Switch de Red"),
        Router("Router"),
        UPS("UPS"),
        Monitor("Monitor"),
        Servidor("Servidor"),
        Telefono_IP("Teléfono IP");
        
        private String descripcion;
        
        TipoActivo(String descripcion) {
            this.descripcion = descripcion;
        }
        
        public String getDescripcion() {
            return descripcion;
        }
    }
    
    private Integer configId;
    private TipoActivo tipoActivo;
    private Integer diasMantenimiento; // Días entre mantenimientos
    private Integer diasAnticipoAlerta; // Días de anticipación para generar alerta
    private Integer tecnicoDefaultId; // Técnico asignado por defecto
    private String actividadesPredefinidas; // JSON con lista de actividades
    private String descripcionProcedimiento; // Procedimiento estándar
    private Boolean activo;
    private LocalDateTime creadoEn;
    private LocalDateTime actualizadoEn;
    
    // Constructores
    public ConfiguracionMantenimiento() {
        this.activo = true;
        this.creadoEn = LocalDateTime.now();
        this.actualizadoEn = LocalDateTime.now();
    }
    
    public ConfiguracionMantenimiento(TipoActivo tipoActivo, Integer diasMantenimiento, 
                                    Integer diasAnticipoAlerta) {
        this();
        this.tipoActivo = tipoActivo;
        this.diasMantenimiento = diasMantenimiento;
        this.diasAnticipoAlerta = diasAnticipoAlerta;
    }
    
    // Getters y Setters
    public Integer getConfigId() {
        return configId;
    }
    
    public void setConfigId(Integer configId) {
        this.configId = configId;
    }
    
    public TipoActivo getTipoActivo() {
        return tipoActivo;
    }
    
    public void setTipoActivo(TipoActivo tipoActivo) {
        this.tipoActivo = tipoActivo;
    }
    
    public Integer getDiasMantenimiento() {
        return diasMantenimiento;
    }
    
    public void setDiasMantenimiento(Integer diasMantenimiento) {
        this.diasMantenimiento = diasMantenimiento;
    }
    
    public Integer getDiasAnticipoAlerta() {
        return diasAnticipoAlerta;
    }
    
    public void setDiasAnticipoAlerta(Integer diasAnticipoAlerta) {
        this.diasAnticipoAlerta = diasAnticipoAlerta;
    }
    
    public Integer getTecnicoDefaultId() {
        return tecnicoDefaultId;
    }
    
    public void setTecnicoDefaultId(Integer tecnicoDefaultId) {
        this.tecnicoDefaultId = tecnicoDefaultId;
    }
    
    public String getActividadesPredefinidas() {
        return actividadesPredefinidas;
    }
    
    public void setActividadesPredefinidas(String actividadesPredefinidas) {
        this.actividadesPredefinidas = actividadesPredefinidas;
    }
    
    public String getDescripcionProcedimiento() {
        return descripcionProcedimiento;
    }
    
    public void setDescripcionProcedimiento(String descripcionProcedimiento) {
        this.descripcionProcedimiento = descripcionProcedimiento;
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
    
    public LocalDateTime getActualizadoEn() {
        return actualizadoEn;
    }
    
    public void setActualizadoEn(LocalDateTime actualizadoEn) {
        this.actualizadoEn = actualizadoEn;
    }
    
    /**
     * Obtiene lista de actividades como array
     */
    public String[] getActividadesArray() {
        if (actividadesPredefinidas == null || actividadesPredefinidas.trim().isEmpty()) {
            return new String[0];
        }
        
        // Simple split por líneas - podríamos usar JSON pero esto es más simple
        return actividadesPredefinidas.split("\n");
    }
    
    /**
     * Establece actividades desde array
     */
    public void setActividadesArray(String[] actividades) {
        if (actividades == null || actividades.length == 0) {
            this.actividadesPredefinidas = "";
        } else {
            this.actividadesPredefinidas = String.join("\n", actividades);
        }
    }
    
    @Override
    public String toString() {
        return tipoActivo.getDescripcion() + " - " + diasMantenimiento + " días";
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        ConfiguracionMantenimiento that = (ConfiguracionMantenimiento) obj;
        return configId != null && configId.equals(that.configId);
    }
    
    @Override
    public int hashCode() {
        return configId != null ? configId.hashCode() : 0;
    }
}
