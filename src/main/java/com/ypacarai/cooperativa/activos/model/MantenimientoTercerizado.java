package com.ypacarai.cooperativa.activos.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Modelo para Mantenimiento Técnico Tercerizado
 * Sistema de Gestión de Activos - Cooperativa Ypacaraí LTDA
 */
public class MantenimientoTercerizado {
    
    public enum EstadoMantenimiento {
        Solicitado,         // Mantenimiento solicitado, equipo aún no retirado
        En_Proceso,         // Equipo retirado, en proceso de mantenimiento
        Finalizado,         // Mantenimiento finalizado, equipo entregado
        Cancelado           // Mantenimiento cancelado
    }
    
    private int mantTercId;
    private int activoId;
    private int proveedorId;
    private String descripcionProblema;
    private LocalDate fechaRetiro;
    private LocalDate fechaEntrega;
    private BigDecimal montoPresupuestado;
    private BigDecimal montoCobrado;
    private EstadoMantenimiento estado;
    private String observacionesRetiro;
    private String observacionesEntrega;
    private String estadoEquipoAntes;
    private String estadoEquipoDespues;
    private String trabajoRealizado;
    private boolean garantia;
    private int diasGarantia;
    private int registradoPor;
    private LocalDateTime creadoEn;
    private LocalDateTime actualizadoEn;
    
    // Campos relacionados para joins
    private String numeroActivo;
    private String marcaActivo;
    private String modeloActivo;
    private String nombreProveedor;
    private String telefonoProveedor;
    private String nombreRegistrador;
    
    // Constructores
    public MantenimientoTercerizado() {
        this.estado = EstadoMantenimiento.Solicitado;
        this.creadoEn = LocalDateTime.now();
        this.garantia = false;
        this.diasGarantia = 0;
    }
    
    public MantenimientoTercerizado(int activoId, int proveedorId, String descripcionProblema, 
                                 String estadoEquipoAntes, int registradoPor) {
        this();
        this.activoId = activoId;
        this.proveedorId = proveedorId;
        this.descripcionProblema = descripcionProblema;
        this.estadoEquipoAntes = estadoEquipoAntes;
        this.registradoPor = registradoPor;
    }
    
    // Getters y Setters
    public int getMantTercId() {
        return mantTercId;
    }
    
    public void setMantTercId(int mantTercId) {
        this.mantTercId = mantTercId;
    }
    
    public int getActivoId() {
        return activoId;
    }
    
    public void setActivoId(int activoId) {
        this.activoId = activoId;
    }
    
    public int getProveedorId() {
        return proveedorId;
    }
    
    public void setProveedorId(int proveedorId) {
        this.proveedorId = proveedorId;
    }
    
    public String getDescripcionProblema() {
        return descripcionProblema;
    }
    
    public void setDescripcionProblema(String descripcionProblema) {
        this.descripcionProblema = descripcionProblema;
    }
    
    public LocalDate getFechaRetiro() {
        return fechaRetiro;
    }
    
    public void setFechaRetiro(LocalDate fechaRetiro) {
        this.fechaRetiro = fechaRetiro;
    }
    
    public LocalDate getFechaEntrega() {
        return fechaEntrega;
    }
    
    public void setFechaEntrega(LocalDate fechaEntrega) {
        this.fechaEntrega = fechaEntrega;
    }
    
    public BigDecimal getMontoPresupuestado() {
        return montoPresupuestado;
    }
    
    public void setMontoPresupuestado(BigDecimal montoPresupuestado) {
        this.montoPresupuestado = montoPresupuestado;
    }
    
    public BigDecimal getMontoCobrado() {
        return montoCobrado;
    }
    
    public void setMontoCobrado(BigDecimal montoCobrado) {
        this.montoCobrado = montoCobrado;
    }
    
    public EstadoMantenimiento getEstado() {
        return estado;
    }
    
    public void setEstado(EstadoMantenimiento estado) {
        this.estado = estado;
    }
    
    public String getObservacionesRetiro() {
        return observacionesRetiro;
    }
    
    public void setObservacionesRetiro(String observacionesRetiro) {
        this.observacionesRetiro = observacionesRetiro;
    }
    
    public String getObservacionesEntrega() {
        return observacionesEntrega;
    }
    
    public void setObservacionesEntrega(String observacionesEntrega) {
        this.observacionesEntrega = observacionesEntrega;
    }
    
    public String getEstadoEquipoAntes() {
        return estadoEquipoAntes;
    }
    
    public void setEstadoEquipoAntes(String estadoEquipoAntes) {
        this.estadoEquipoAntes = estadoEquipoAntes;
    }
    
    public String getEstadoEquipoDespues() {
        return estadoEquipoDespues;
    }
    
    public void setEstadoEquipoDespues(String estadoEquipoDespues) {
        this.estadoEquipoDespues = estadoEquipoDespues;
    }
    
    public String getTrabajoRealizado() {
        return trabajoRealizado;
    }
    
    public void setTrabajoRealizado(String trabajoRealizado) {
        this.trabajoRealizado = trabajoRealizado;
    }
    
    public boolean isGarantia() {
        return garantia;
    }
    
    public void setGarantia(boolean garantia) {
        this.garantia = garantia;
    }
    
    public int getDiasGarantia() {
        return diasGarantia;
    }
    
    public void setDiasGarantia(int diasGarantia) {
        this.diasGarantia = diasGarantia;
    }
    
    public int getRegistradoPor() {
        return registradoPor;
    }
    
    public void setRegistradoPor(int registradoPor) {
        this.registradoPor = registradoPor;
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
    public String getNumeroActivo() {
        return numeroActivo;
    }
    
    public void setNumeroActivo(String numeroActivo) {
        this.numeroActivo = numeroActivo;
    }
    
    public String getMarcaActivo() {
        return marcaActivo;
    }
    
    public void setMarcaActivo(String marcaActivo) {
        this.marcaActivo = marcaActivo;
    }
    
    public String getModeloActivo() {
        return modeloActivo;
    }
    
    public void setModeloActivo(String modeloActivo) {
        this.modeloActivo = modeloActivo;
    }
    
    public String getNombreProveedor() {
        return nombreProveedor;
    }
    
    public void setNombreProveedor(String nombreProveedor) {
        this.nombreProveedor = nombreProveedor;
    }
    
    public String getTelefonoProveedor() {
        return telefonoProveedor;
    }
    
    public void setTelefonoProveedor(String telefonoProveedor) {
        this.telefonoProveedor = telefonoProveedor;
    }
    
    public String getNombreRegistrador() {
        return nombreRegistrador;
    }
    
    public void setNombreRegistrador(String nombreRegistrador) {
        this.nombreRegistrador = nombreRegistrador;
    }
    
    /**
     * Calcula los días transcurridos desde el retiro (si ya fue retirado)
     */
    public Long getDiasEnProceso() {
        if (fechaRetiro != null && estado == EstadoMantenimiento.En_Proceso) {
            return LocalDate.now().toEpochDay() - fechaRetiro.toEpochDay();
        }
        return null;
    }
    
    /**
     * Verifica si el mantenimiento está dentro del período de garantía
     */
    public boolean estaEnGarantia() {
        if (!garantia || fechaEntrega == null || diasGarantia <= 0) {
            return false;
        }
        LocalDate fechaVencimientoGarantia = fechaEntrega.plusDays(diasGarantia);
        return LocalDate.now().isBefore(fechaVencimientoGarantia) || 
               LocalDate.now().isEqual(fechaVencimientoGarantia);
    }
    
    /**
     * Obtiene el monto a pagar (considerando si hay presupuesto o cobro final)
     */
    public BigDecimal getMontoAPagar() {
        return montoCobrado != null ? montoCobrado : 
               (montoPresupuestado != null ? montoPresupuestado : BigDecimal.ZERO);
    }
    
    @Override
    public String toString() {
        return String.format("Mantenimiento %d - Activo %s - %s - %s", 
                           mantTercId, numeroActivo, nombreProveedor, estado);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        MantenimientoTercerizado that = (MantenimientoTercerizado) obj;
        return mantTercId == that.mantTercId;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(mantTercId);
    }
}