package com.ypacarai.cooperativa.activos.model;

import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Modelo para configuraciones generales del sistema
 * Cooperativa Ypacaraí LTDA
 */
public class ConfiguracionSistema {
    
    public enum TipoParametro {
        TEXTO("Texto"),
        NUMERO("Número"),
        BOOLEAN("Verdadero/Falso"),
        TIEMPO("Tiempo"),
        COLOR("Color"),
        EMAIL("Correo Electrónico"),
        JSON("Configuración Avanzada");
        
        private final String descripcion;
        
        TipoParametro(String descripcion) {
            this.descripcion = descripcion;
        }
        
        public String getDescripcion() {
            return descripcion;
        }
    }
    
    public enum CategoriaParametro {
        GENERAL("Configuración General"),
        MANTENIMIENTO("Mantenimiento Preventivo"),
        ALERTAS("Sistema de Alertas"),
        EMAIL("Configuración de Correos"),
        NOTIFICACIONES("Notificaciones"),
        SEGURIDAD("Seguridad del Sistema"),
        REPORTES("Configuración de Reportes");
        
        private final String descripcion;
        
        CategoriaParametro(String descripcion) {
            this.descripcion = descripcion;
        }
        
        public String getDescripcion() {
            return descripcion;
        }
    }
    
    private Integer confId;
    private String confClave;
    private String confValor;
    private String confDescripcion;
    private TipoParametro confTipo;
    private CategoriaParametro confCategoria;
    private String confValorDefecto;
    private Boolean confObligatoria;
    private Boolean confActiva;
    private String confValidacion; // Regex o regla de validación
    private String confOpciones; // JSON con opciones disponibles
    private LocalDateTime creadoEn;
    private LocalDateTime actualizadoEn;
    
    // Constructores
    public ConfiguracionSistema() {
        this.confActiva = true;
        this.confObligatoria = false;
        this.creadoEn = LocalDateTime.now();
        this.actualizadoEn = LocalDateTime.now();
    }
    
    public ConfiguracionSistema(String clave, String valor, String descripcion, 
                               TipoParametro tipo, CategoriaParametro categoria) {
        this();
        this.confClave = clave;
        this.confValor = valor;
        this.confDescripcion = descripcion;
        this.confTipo = tipo;
        this.confCategoria = categoria;
        this.confValorDefecto = valor;
    }
    
    // Getters y Setters
    public Integer getConfId() {
        return confId;
    }
    
    public void setConfId(Integer confId) {
        this.confId = confId;
    }
    
    public String getConfClave() {
        return confClave;
    }
    
    public void setConfClave(String confClave) {
        this.confClave = confClave;
    }
    
    public String getConfValor() {
        return confValor;
    }
    
    public void setConfValor(String confValor) {
        this.confValor = confValor;
    }
    
    public String getConfDescripcion() {
        return confDescripcion;
    }
    
    public void setConfDescripcion(String confDescripcion) {
        this.confDescripcion = confDescripcion;
    }
    
    public TipoParametro getConfTipo() {
        return confTipo;
    }
    
    public void setConfTipo(TipoParametro confTipo) {
        this.confTipo = confTipo;
    }
    
    public CategoriaParametro getConfCategoria() {
        return confCategoria;
    }
    
    public void setConfCategoria(CategoriaParametro confCategoria) {
        this.confCategoria = confCategoria;
    }
    
    public String getConfValorDefecto() {
        return confValorDefecto;
    }
    
    public void setConfValorDefecto(String confValorDefecto) {
        this.confValorDefecto = confValorDefecto;
    }
    
    public Boolean getConfObligatoria() {
        return confObligatoria;
    }
    
    public void setConfObligatoria(Boolean confObligatoria) {
        this.confObligatoria = confObligatoria;
    }
    
    public Boolean getConfActiva() {
        return confActiva;
    }
    
    public void setConfActiva(Boolean confActiva) {
        this.confActiva = confActiva;
    }
    
    public String getConfValidacion() {
        return confValidacion;
    }
    
    public void setConfValidacion(String confValidacion) {
        this.confValidacion = confValidacion;
    }
    
    public String getConfOpciones() {
        return confOpciones;
    }
    
    public void setConfOpciones(String confOpciones) {
        this.confOpciones = confOpciones;
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
    
    // Métodos de utilidad
    
    /**
     * Convierte el valor string a entero
     */
    public Integer getValorComoEntero() {
        try {
            return confValor != null ? Integer.parseInt(confValor) : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }
    
    /**
     * Convierte el valor string a boolean
     */
    public Boolean getValorComoBoolean() {
        return confValor != null && Boolean.parseBoolean(confValor);
    }
    
    /**
     * Convierte el valor string a LocalTime
     */
    public LocalTime getValorComoTiempo() {
        try {
            return confValor != null ? LocalTime.parse(confValor) : null;
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * Valida si el valor actual cumple con las reglas de validación
     */
    public boolean esValorValido() {
        if (confValor == null || confValor.trim().isEmpty()) {
            return !confObligatoria;
        }
        
        if (confValidacion != null && !confValidacion.trim().isEmpty()) {
            return confValor.matches(confValidacion);
        }
        
        return true;
    }
    
    /**
     * Restaura el valor por defecto
     */
    public void restaurarValorDefecto() {
        this.confValor = this.confValorDefecto;
        this.actualizadoEn = LocalDateTime.now();
    }
    
    @Override
    public String toString() {
        return confClave + " = " + confValor + " (" + confCategoria + ")";
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        ConfiguracionSistema that = (ConfiguracionSistema) obj;
        return confClave != null && confClave.equals(that.confClave);
    }
    
    @Override
    public int hashCode() {
        return confClave != null ? confClave.hashCode() : 0;
    }
}
