package com.ypacarai.cooperativa.activos.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.ypacarai.cooperativa.activos.dao.ActivoDAO;
import com.ypacarai.cooperativa.activos.dao.TrasladoDAO;
import com.ypacarai.cooperativa.activos.model.Activo;
import com.ypacarai.cooperativa.activos.model.Traslado;
import com.ypacarai.cooperativa.activos.model.Traslado.EstadoTraslado;

/**
 * Servicio de lógica de negocio para Traslados
 * Gestiona traslados de activos entre Casa Central y Sucursales
 */
public class TrasladoService {
    
    private final TrasladoDAO trasladoDAO;
    private final ActivoDAO activoDAO;
    
    public TrasladoService() {
        this.trasladoDAO = new TrasladoDAO();
        this.activoDAO = new ActivoDAO();
    }
    
    public TrasladoService(TrasladoDAO trasladoDAO, ActivoDAO activoDAO) {
        this.trasladoDAO = trasladoDAO;
        this.activoDAO = activoDAO;
    }
    
    /**
     * Registra un nuevo traslado con validaciones de negocio
     */
    public boolean registrarTraslado(Traslado traslado) throws IllegalArgumentException {
        // Validación 1: El activo debe existir
        Optional<Activo> activoOpt = activoDAO.findById(traslado.getActId());
        if (!activoOpt.isPresent()) {
            throw new IllegalArgumentException("El activo no existe");
        }
        
        Activo activo = activoOpt.get();
        
        // Validación 2: El activo debe estar operativo
        if (activo.getActEstado() != Activo.Estado.Operativo) {
            throw new IllegalArgumentException("Solo se pueden trasladar activos en estado Operativo");
        }
        
        // Validación 3: Origen y destino no pueden ser iguales
        if (traslado.getTrasUbicacionOrigen().equals(traslado.getTrasUbicacionDestino())) {
            throw new IllegalArgumentException("La ubicación de origen y destino no pueden ser iguales");
        }
        
        // Validación 4: La ubicación actual del activo debe coincidir con el origen
        if (activo.getActUbicacionActual() != traslado.getTrasUbicacionOrigen()) {
            throw new IllegalArgumentException(
                "El activo no se encuentra en la ubicación de origen especificada. " +
                "Ubicación actual del activo: " + activo.getActUbicacionActual()
            );
        }
        
        // Validación 5: Validar campos obligatorios
        if (traslado.getTrasMotivo() == null || traslado.getTrasMotivo().trim().isEmpty()) {
            throw new IllegalArgumentException("El motivo del traslado es obligatorio");
        }
        
        if (traslado.getTrasResponsableEnvio() == null || traslado.getTrasResponsableEnvio().trim().isEmpty()) {
            throw new IllegalArgumentException("El responsable de envío es obligatorio");
        }
        
        // Establecer valores por defecto
        if (traslado.getTrasEstado() == null) {
            traslado.setTrasEstado(EstadoTraslado.Programado);
        }
        
        if (traslado.getTrasFechaSalida() == null) {
            traslado.setTrasFechaSalida(LocalDateTime.now());
        }
        
        if (traslado.getCreadoEn() == null) {
            traslado.setCreadoEn(LocalDateTime.now());
        }
        
        if (traslado.getActualizadoEn() == null) {
            traslado.setActualizadoEn(LocalDateTime.now());
        }
        
        // Guardar traslado
        return trasladoDAO.save(traslado);
    }
    
    /**
     * Cambia el estado de un traslado con validaciones
     */
    public boolean cambiarEstado(Integer trasladoId, EstadoTraslado nuevoEstado, String observaciones) 
            throws IllegalArgumentException, IllegalStateException {
        
        Traslado traslado = trasladoDAO.findById(trasladoId);
        
        if (traslado == null) {
            throw new IllegalArgumentException("Traslado no encontrado");
        }
        
        EstadoTraslado estadoActual = traslado.getTrasEstado();
        
        // Validar transiciones de estado permitidas
        if (!esTransicionValida(estadoActual, nuevoEstado)) {
            throw new IllegalStateException(
                String.format("Transición de estado inválida: %s -> %s", 
                    estadoActual, nuevoEstado)
            );
        }
        
        // Actualizar estado y metadatos
        traslado.setTrasEstado(nuevoEstado);
        traslado.setActualizadoEn(LocalDateTime.now());
        
        // Actualizar observaciones si se proporcionan
        if (observaciones != null && !observaciones.trim().isEmpty()) {
            String obsActuales = traslado.getTrasObservaciones();
            if (obsActuales != null && !obsActuales.isEmpty()) {
                traslado.setTrasObservaciones(obsActuales + "\n" + observaciones);
            } else {
                traslado.setTrasObservaciones(observaciones);
            }
        }
        
        // Si se marca como Devuelto, registrar fecha de retorno
        if (nuevoEstado == EstadoTraslado.Devuelto && traslado.getTrasFechaRetorno() == null) {
            traslado.setTrasFechaRetorno(LocalDateTime.now());
        }
        
        return trasladoDAO.update(traslado);
    }
    
    /**
     * Valida si una transición de estado es permitida
     */
    private boolean esTransicionValida(EstadoTraslado estadoActual, EstadoTraslado nuevoEstado) {
        if (estadoActual == null || nuevoEstado == null) {
            return false;
        }
        
        switch (estadoActual) {
            case Programado:
                return nuevoEstado == EstadoTraslado.En_Transito;
                
            case En_Transito:
                return nuevoEstado == EstadoTraslado.Entregado;
                
            case Entregado:
                return nuevoEstado == EstadoTraslado.Devuelto;
                
            case Devuelto:
                // Estado final, no se permite cambiar
                return false;
                
            default:
                return false;
        }
    }
    
    /**
     * Confirma la entrega de un traslado
     */
    public boolean confirmarEntrega(Integer trasladoId, String responsableRecibo, String observaciones) 
            throws IllegalArgumentException {
        
        if (responsableRecibo == null || responsableRecibo.trim().isEmpty()) {
            throw new IllegalArgumentException("El responsable de recibo es obligatorio");
        }
        
        Traslado traslado = trasladoDAO.findById(trasladoId);
        
        if (traslado == null) {
            throw new IllegalArgumentException("Traslado no encontrado");
        }
        
        if (traslado.getTrasEstado() != EstadoTraslado.En_Transito) {
            throw new IllegalStateException("Solo se pueden confirmar entregas de traslados En_Transito");
        }
        
        traslado.setTrasResponsableRecibo(responsableRecibo);
        traslado.setTrasEstado(EstadoTraslado.Entregado);
        traslado.setActualizadoEn(LocalDateTime.now());
        
        if (observaciones != null && !observaciones.trim().isEmpty()) {
            String obsActuales = traslado.getTrasObservaciones();
            if (obsActuales != null && !obsActuales.isEmpty()) {
                traslado.setTrasObservaciones(obsActuales + "\n" + observaciones);
            } else {
                traslado.setTrasObservaciones(observaciones);
            }
        }
        
        // Actualizar ubicación del activo al destino
        try {
            Activo activo = activoDAO.findById(traslado.getActId()).orElse(null);
            if (activo != null) {
                activo.setActUbicacionActual(traslado.getTrasUbicacionDestino());
                activoDAO.update(activo);
            }
        } catch (Exception e) {
            System.err.println("Error al actualizar ubicación del activo: " + e.getMessage());
        }
        
        return trasladoDAO.update(traslado);
    }
    
    /**
     * Confirma la devolución de un traslado
     */
    public boolean confirmarDevolucion(Integer trasladoId, String observaciones) 
            throws IllegalArgumentException {
        
        Traslado traslado = trasladoDAO.findById(trasladoId);
        
        if (traslado == null) {
            throw new IllegalArgumentException("Traslado no encontrado");
        }
        
        if (traslado.getTrasEstado() != EstadoTraslado.Entregado) {
            throw new IllegalStateException("Solo se pueden devolver traslados Entregados");
        }
        
        traslado.setTrasEstado(EstadoTraslado.Devuelto);
        traslado.setTrasFechaRetorno(LocalDateTime.now());
        traslado.setActualizadoEn(LocalDateTime.now());
        
        if (observaciones != null && !observaciones.trim().isEmpty()) {
            String obsActuales = traslado.getTrasObservaciones();
            if (obsActuales != null && !obsActuales.isEmpty()) {
                traslado.setTrasObservaciones(obsActuales + "\n" + observaciones);
            } else {
                traslado.setTrasObservaciones(observaciones);
            }
        }
        
        // Restaurar ubicación del activo a la origen
        try {
            Activo activo = activoDAO.findById(traslado.getActId()).orElse(null);
            if (activo != null) {
                activo.setActUbicacionActual(traslado.getTrasUbicacionOrigen());
                activoDAO.update(activo);
            }
        } catch (Exception e) {
            System.err.println("Error al restaurar ubicación del activo: " + e.getMessage());
        }
        
        return trasladoDAO.update(traslado);
    }
    
    /**
     * Obtiene todos los traslados
     */
    public List<Traslado> obtenerTodos() {
        return trasladoDAO.findAll();
    }
    
    /**
     * Busca traslados por activo
     */
    public List<Traslado> buscarPorActivo(Integer activoId) {
        return trasladoDAO.findByActivo(activoId);
    }
    
    /**
     * Busca traslados por estado
     */
    public List<Traslado> buscarPorEstado(EstadoTraslado estado) {
        return trasladoDAO.findByEstado(estado);
    }
    
    /**
     * Obtiene traslados pendientes de devolución
     */
    public List<Traslado> obtenerPendientesDevolucion() {
        return trasladoDAO.findPendientesDevolucion();
    }
    
    /**
     * Busca un traslado por ID
     */
    public Traslado buscarPorId(Integer id) {
        return trasladoDAO.findById(id);
    }
    
    /**
     * Cuenta traslados por estado
     */
    public int contarPorEstado(EstadoTraslado estado) {
        return trasladoDAO.countByEstado(estado);
    }
    
    /**
     * Actualiza un traslado existente
     */
    public boolean actualizar(Traslado traslado) {
        traslado.setActualizadoEn(LocalDateTime.now());
        return trasladoDAO.update(traslado);
    }
    
    /**
     * Elimina un traslado (solo si está en estado Programado)
     */
    public boolean eliminar(Integer trasladoId) throws IllegalStateException {
        Traslado traslado = trasladoDAO.findById(trasladoId);
        
        if (traslado == null) {
            throw new IllegalArgumentException("Traslado no encontrado");
        }
        
        if (traslado.getTrasEstado() != EstadoTraslado.Programado) {
            throw new IllegalStateException("Solo se pueden eliminar traslados en estado Programado");
        }
        
        return trasladoDAO.delete(trasladoId);
    }
}
