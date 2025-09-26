package com.ypacarai.cooperativa.activos.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Entidad que representa los traslados entre Casa Central y Sucursales
 */
public class Traslado {
    
    public enum EstadoTraslado {
        Programado, En_Transito, Entregado, Devuelto
    }
    
    private Integer trasId;
    private Integer actId; // Activo a trasladar
    private String trasNumero; // TR-YYYY-NNNN
    private LocalDateTime trasFechaSalida;
    private LocalDateTime trasFechaRetorno;
    private Integer trasUbicacionOrigen;
    private Integer trasUbicacionDestino;
    private String trasMotivo;
    private EstadoTraslado trasEstado;
    private String trasResponsableEnvio; // Persona que entrega
    private String trasResponsableRecibo; // Persona que recibe
    private String trasObservaciones;
    private LocalDate trasFechaDevolucionProg; // Fecha programada de devolución
    private Integer autorizadoPor; // Usuario que autoriza
    private Integer creadoPor;
    private LocalDateTime creadoEn;
    private LocalDateTime actualizadoEn;
    
    // Constructores
    public Traslado() {
        this.trasEstado = EstadoTraslado.Programado;
        this.creadoEn = LocalDateTime.now();
        this.actualizadoEn = LocalDateTime.now();
    }
    
    public Traslado(Integer actId, Integer ubicacionOrigen, Integer ubicacionDestino, String motivo) {
        this();
        this.actId = actId;
        this.trasUbicacionOrigen = ubicacionOrigen;
        this.trasUbicacionDestino = ubicacionDestino;
        this.trasMotivo = motivo;
        this.trasFechaSalida = LocalDateTime.now();
    }
    
    // Getters y Setters
    public Integer getTrasId() {
        return trasId;
    }
    
    public void setTrasId(Integer trasId) {
        this.trasId = trasId;
    }
    
    public Integer getActId() {
        return actId;
    }
    
    public void setActId(Integer actId) {
        this.actId = actId;
    }
    
    public String getTrasNumero() {
        return trasNumero;
    }
    
    public void setTrasNumero(String trasNumero) {
        this.trasNumero = trasNumero;
    }
    
    public LocalDateTime getTrasFechaSalida() {
        return trasFechaSalida;
    }
    
    public void setTrasFechaSalida(LocalDateTime trasFechaSalida) {
        this.trasFechaSalida = trasFechaSalida;
    }
    
    public LocalDateTime getTrasFechaRetorno() {
        return trasFechaRetorno;
    }
    
    public void setTrasFechaRetorno(LocalDateTime trasFechaRetorno) {
        this.trasFechaRetorno = trasFechaRetorno;
    }
    
    public Integer getTrasUbicacionOrigen() {
        return trasUbicacionOrigen;
    }
    
    public void setTrasUbicacionOrigen(Integer trasUbicacionOrigen) {
        this.trasUbicacionOrigen = trasUbicacionOrigen;
    }
    
    public Integer getTrasUbicacionDestino() {
        return trasUbicacionDestino;
    }
    
    public void setTrasUbicacionDestino(Integer trasUbicacionDestino) {
        this.trasUbicacionDestino = trasUbicacionDestino;
    }
    
    public String getTrasMotivo() {
        return trasMotivo;
    }
    
    public void setTrasMotivo(String trasMotivo) {
        this.trasMotivo = trasMotivo;
    }
    
    public EstadoTraslado getTrasEstado() {
        return trasEstado;
    }
    
    public void setTrasEstado(EstadoTraslado trasEstado) {
        this.trasEstado = trasEstado;
    }
    
    public String getTrasResponsableEnvio() {
        return trasResponsableEnvio;
    }
    
    public void setTrasResponsableEnvio(String trasResponsableEnvio) {
        this.trasResponsableEnvio = trasResponsableEnvio;
    }
    
    public String getTrasResponsableRecibo() {
        return trasResponsableRecibo;
    }
    
    public void setTrasResponsableRecibo(String trasResponsableRecibo) {
        this.trasResponsableRecibo = trasResponsableRecibo;
    }
    
    public String getTrasObservaciones() {
        return trasObservaciones;
    }
    
    public void setTrasObservaciones(String trasObservaciones) {
        this.trasObservaciones = trasObservaciones;
    }
    
    public LocalDate getTrasFechaDevolucionProg() {
        return trasFechaDevolucionProg;
    }
    
    public void setTrasFechaDevolucionProg(LocalDate trasFechaDevolucionProg) {
        this.trasFechaDevolucionProg = trasFechaDevolucionProg;
    }
    
    public Integer getAutorizadoPor() {
        return autorizadoPor;
    }
    
    public void setAutorizadoPor(Integer autorizadoPor) {
        this.autorizadoPor = autorizadoPor;
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
    
    /**
     * Calcula los días que el activo ha estado fuera
     */
    public long getDiasFuera() {
        if (trasFechaSalida != null) {
            LocalDateTime fechaReferencia = trasFechaRetorno != null ? trasFechaRetorno : LocalDateTime.now();
            return ChronoUnit.DAYS.between(trasFechaSalida.toLocalDate(), fechaReferencia.toLocalDate());
        }
        return 0;
    }
    
    @Override
    public String toString() {
        return trasNumero != null ? trasNumero : "Traslado " + trasId;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        Traslado that = (Traslado) obj;
        return trasId != null && trasId.equals(that.trasId);
    }
    
    @Override
    public int hashCode() {
        return trasId != null ? trasId.hashCode() : 0;
    }
}
