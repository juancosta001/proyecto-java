package com.ypacarai.cooperativa.activos.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.ypacarai.cooperativa.activos.dao.ActivoDAO;
import com.ypacarai.cooperativa.activos.dao.MantenimientoTercerizadoDAO;
import com.ypacarai.cooperativa.activos.dao.ProveedorServicioDAO;
import com.ypacarai.cooperativa.activos.model.Activo;
import com.ypacarai.cooperativa.activos.model.MantenimientoTercerizado;
import com.ypacarai.cooperativa.activos.model.ProveedorServicio;

/**
 * Servicio para la gestión de Mantenimiento Técnico Tercerizado
 * Sistema de Gestión de Activos - Cooperativa Ypacaraí LTDA
 */
public class MantenimientoTercerizadoService {
    
    private final MantenimientoTercerizadoDAO mantenimientoDAO;
    private final ProveedorServicioDAO proveedorDAO;
    private final ActivoDAO activoDAO;
    
    public MantenimientoTercerizadoService() {
        this.mantenimientoDAO = new MantenimientoTercerizadoDAO();
        this.proveedorDAO = new ProveedorServicioDAO();
        this.activoDAO = new ActivoDAO();
    }
    
    // ==================== GESTIÓN DE PROVEEDORES ====================
    
    /**
     * Registra un nuevo proveedor de servicios
     */
    public int registrarProveedor(ProveedorServicio proveedor) {
        validarProveedor(proveedor);
        return proveedorDAO.insert(proveedor);
    }
    
    /**
     * Obtiene todos los proveedores activos
     */
    public List<ProveedorServicio> obtenerProveedoresActivos() {
        return proveedorDAO.findActivos();
    }
    
    /**
     * Obtiene todos los proveedores
     */
    public List<ProveedorServicio> obtenerTodosProveedores() {
        return proveedorDAO.findAll();
    }
    
    /**
     * Busca un proveedor por ID
     */
    public Optional<ProveedorServicio> buscarProveedorPorId(int id) {
        return proveedorDAO.findById(id);
    }
    
    /**
     * Actualiza un proveedor
     */
    public boolean actualizarProveedor(ProveedorServicio proveedor) {
        validarProveedor(proveedor);
        return proveedorDAO.update(proveedor);
    }
    
    /**
     * Activa o desactiva un proveedor
     */
    public boolean toggleProveedorActivo(int id, boolean activo) {
        return proveedorDAO.toggleActivo(id, activo);
    }
    
    // ==================== GESTIÓN DE MANTENIMIENTOS ====================
    
    /**
     * Solicita un mantenimiento tercerizado
     */
    public int solicitarMantenimiento(int activoId, int proveedorId, String descripcionProblema,
                                    String estadoEquipoAntes, BigDecimal montoPresupuestado, 
                                    int registradoPor) {
        
        // Debug en el servicio
        System.out.println("=== DEBUG SERVICIO ===");
        System.out.println("Activo ID: " + activoId);
        System.out.println("Proveedor ID: " + proveedorId);
        System.out.println("Usuario registrador: " + registradoPor);
        
        // Validaciones
        validarSolicitudMantenimiento(activoId, proveedorId, descripcionProblema);
        
        // Crear el mantenimiento
        MantenimientoTercerizado mantenimiento = new MantenimientoTercerizado(
            activoId, proveedorId, descripcionProblema, estadoEquipoAntes, registradoPor
        );
        
        if (montoPresupuestado != null && montoPresupuestado.compareTo(BigDecimal.ZERO) > 0) {
            mantenimiento.setMontoPresupuestado(montoPresupuestado);
        }
        
        int mantId = mantenimientoDAO.insert(mantenimiento);
        
        if (mantId > 0) {
            System.out.println("Mantenimiento tercerizado solicitado: ID " + mantId);
        }
        
        return mantId;
    }
    
    /**
     * Registra el retiro del equipo al proveedor
     */
    public boolean registrarRetiroEquipo(int mantTercId, LocalDate fechaRetiro, String observaciones) {
        
        System.out.println("=== DEBUG: Registrando retiro ===");
        System.out.println("MantenimientoTercerizado ID: " + mantTercId);
        System.out.println("Fecha retiro: " + fechaRetiro);
        System.out.println("Observaciones: " + observaciones);
        
        // Validar que el mantenimiento existe y está en estado correcto
        Optional<MantenimientoTercerizado> mantOpt = mantenimientoDAO.findById(mantTercId);
        if (!mantOpt.isPresent()) {
            System.err.println("ERROR: Mantenimiento no encontrado con ID: " + mantTercId);
            throw new IllegalArgumentException("Mantenimiento no encontrado");
        }
        
        MantenimientoTercerizado mantenimiento = mantOpt.get();
        System.out.println("Estado actual del mantenimiento: " + mantenimiento.getEstado());
        
        if (mantenimiento.getEstado() != MantenimientoTercerizado.EstadoMantenimiento.Solicitado) {
            System.err.println("ERROR: Estado incorrecto. Esperado: Solicitado, Actual: " + mantenimiento.getEstado());
            throw new IllegalStateException("El mantenimiento debe estar en estado 'Solicitado' para registrar retiro");
        }
        
        // Registrar el retiro
        boolean retiroRegistrado = mantenimientoDAO.registrarRetiro(mantTercId, fechaRetiro, observaciones);
        
        if (retiroRegistrado) {
            // Cambiar estado del activo a "En_Mantenimiento"
            activoDAO.updateEstado(mantenimiento.getActivoId(), Activo.Estado.En_Mantenimiento);
            System.out.println("Equipo retirado exitosamente. ID Mantenimiento: " + mantTercId);
        }
        
        return retiroRegistrado;
    }
    
    /**
     * Registra la entrega del equipo por el proveedor (versión simplificada)
     */
    public boolean registrarEntregaEquipo(int mantTercId, LocalDate fechaEntrega, 
                                        String servicioRealizado, String observaciones,
                                        BigDecimal costoFinal, int garantiaDias, 
                                        LocalDate fechaVencimientoGarantia, String estadoPago) {
        
        // Validar que el mantenimiento existe y está en estado correcto
        Optional<MantenimientoTercerizado> mantOpt = mantenimientoDAO.findById(mantTercId);
        if (!mantOpt.isPresent()) {
            throw new IllegalArgumentException("Mantenimiento no encontrado");
        }
        
        MantenimientoTercerizado mantenimiento = mantOpt.get();
        if (mantenimiento.getEstado() != MantenimientoTercerizado.EstadoMantenimiento.En_Proceso) {
            throw new IllegalStateException("El mantenimiento debe estar en estado 'En_Proceso' para registrar entrega");
        }
        
        // Convertir BigDecimal a String si no es null
        String costoFinalStr = costoFinal != null ? costoFinal.toString() : null;
        
        // Registrar la entrega - usando el método existente con parámetros adaptados
        boolean entregaRegistrada = mantenimientoDAO.registrarEntrega(
            mantTercId, fechaEntrega, observaciones, "Operativo", // estado por defecto
            servicioRealizado, costoFinal, garantiaDias > 0, garantiaDias
        );
        
        if (entregaRegistrada) {
            // Cambiar estado del activo de vuelta a Operativo
            activoDAO.updateEstado(mantenimiento.getActivoId(), Activo.Estado.Operativo);
            System.out.println("Equipo entregado exitosamente. ID Mantenimiento: " + mantTercId);
        }
        
        return entregaRegistrada;
    }
    
    /**
     * Cancela un mantenimiento
     */
    public boolean cancelarMantenimiento(int mantTercId, String motivo) {
        Optional<MantenimientoTercerizado> mantOpt = mantenimientoDAO.findById(mantTercId);
        if (!mantOpt.isPresent()) {
            throw new IllegalArgumentException("Mantenimiento no encontrado");
        }
        
        MantenimientoTercerizado mantenimiento = mantOpt.get();
        
        // Solo se puede cancelar si está en estado Solicitado
        if (mantenimiento.getEstado() != MantenimientoTercerizado.EstadoMantenimiento.Solicitado) {
            throw new IllegalStateException("Solo se pueden cancelar mantenimientos en estado 'Solicitado'");
        }
        
        boolean cancelado = mantenimientoDAO.updateEstado(mantTercId, MantenimientoTercerizado.EstadoMantenimiento.Cancelado);
        
        if (cancelado) {
            // Si el equipo ya había sido retirado, restaurar su estado original
            if (mantenimiento.getFechaRetiro() != null) {
                activoDAO.updateEstado(mantenimiento.getActivoId(), Activo.Estado.Operativo);
            }
            System.out.println("Mantenimiento cancelado: " + mantTercId + ". Motivo: " + motivo);
        }
        
        return cancelado;
    }
    
    // ==================== CONSULTAS ====================
    
    /**
     * Obtiene todos los mantenimientos
     */
    public List<MantenimientoTercerizado> obtenerTodosMantenimientos() {
        return mantenimientoDAO.findAll();
    }
    
    /**
     * Obtiene mantenimientos por activo
     */
    public List<MantenimientoTercerizado> obtenerMantenimientosPorActivo(int activoId) {
        return mantenimientoDAO.findByActivo(activoId);
    }
    
    /**
     * Obtiene mantenimientos por proveedor
     */
    public List<MantenimientoTercerizado> obtenerMantenimientosPorProveedor(int proveedorId) {
        return mantenimientoDAO.findByProveedor(proveedorId);
    }
    
    /**
     * Obtiene mantenimientos por estado
     */
    public List<MantenimientoTercerizado> obtenerMantenimientosPorEstado(MantenimientoTercerizado.EstadoMantenimiento estado) {
        return mantenimientoDAO.findByEstado(estado);
    }
    
    /**
     * Obtiene mantenimientos pendientes (solicitados y en proceso)
     */
    public List<MantenimientoTercerizado> obtenerMantenimientosPendientes() {
        List<MantenimientoTercerizado> pendientes = obtenerMantenimientosPorEstado(MantenimientoTercerizado.EstadoMantenimiento.Solicitado);
        pendientes.addAll(obtenerMantenimientosPorEstado(MantenimientoTercerizado.EstadoMantenimiento.En_Proceso));
        return pendientes;
    }
    
    /**
     * Busca un mantenimiento por ID
     */
    public Optional<MantenimientoTercerizado> buscarMantenimientoPorId(int id) {
        return mantenimientoDAO.findById(id);
    }
    
    // ==================== REPORTES Y ESTADÍSTICAS ====================
    
    /**
     * Calcula el costo total de mantenimientos por período
     */
    public BigDecimal calcularCostoTotalMantenimientos(LocalDate fechaInicio, LocalDate fechaFin) {
        List<MantenimientoTercerizado> todos = obtenerTodosMantenimientos();
        BigDecimal total = BigDecimal.ZERO;
        
        for (MantenimientoTercerizado mant : todos) {
            if (mant.getFechaEntrega() != null && 
                !mant.getFechaEntrega().isBefore(fechaInicio) && 
                !mant.getFechaEntrega().isAfter(fechaFin) &&
                mant.getEstado() == MantenimientoTercerizado.EstadoMantenimiento.Finalizado) {
                
                BigDecimal costo = mant.getMontoAPagar();
                if (costo != null) {
                    total = total.add(costo);
                }
            }
        }
        
        return total;
    }
    
    /**
     * Obtiene mantenimientos con garantía vigente
     */
    public List<MantenimientoTercerizado> obtenerMantenimientosEnGarantia() {
        List<MantenimientoTercerizado> finalizados = obtenerMantenimientosPorEstado(MantenimientoTercerizado.EstadoMantenimiento.Finalizado);
        return finalizados.stream()
                         .filter(MantenimientoTercerizado::estaEnGarantia)
                         .collect(Collectors.toList());
    }
    
    // ==================== MÉTODOS PRIVADOS DE VALIDACIÓN ====================
    
    private void validarProveedor(ProveedorServicio proveedor) {
        if (proveedor.getPrvNombre() == null || proveedor.getPrvNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del proveedor es obligatorio");
        }
        
        if (proveedor.getPrvNumeroTelefono() == null || proveedor.getPrvNumeroTelefono().trim().isEmpty()) {
            throw new IllegalArgumentException("El número de teléfono es obligatorio");
        }
        
        if (proveedor.getPrvContactoPrincipal() == null || proveedor.getPrvContactoPrincipal().trim().isEmpty()) {
            throw new IllegalArgumentException("El contacto principal es obligatorio");
        }
    }
    
    private void validarSolicitudMantenimiento(int activoId, int proveedorId, String descripcionProblema) {
        // Validar que el activo existe
        Optional<Activo> activoOpt = activoDAO.findById(activoId);
        if (!activoOpt.isPresent()) {
            throw new IllegalArgumentException("Activo no encontrado");
        }
        
        // Validar que el proveedor existe y está activo
        Optional<ProveedorServicio> proveedorOpt = proveedorDAO.findById(proveedorId);
        if (!proveedorOpt.isPresent() || !proveedorOpt.get().isActivo()) {
            throw new IllegalArgumentException("Proveedor no encontrado o inactivo");
        }
        
        // Validar descripción
        if (descripcionProblema == null || descripcionProblema.trim().isEmpty()) {
            throw new IllegalArgumentException("La descripción del problema es obligatoria");
        }
        
        // Verificar que el activo no esté ya en servicio externo
        Activo activo = activoOpt.get();
        if (activo.getActEstado() == Activo.Estado.En_Servicio_Externo) {
            throw new IllegalStateException("El activo ya está en servicio técnico externo");
        }
    }
    
    // ==================== MÉTODOS ADICIONALES ====================
    
    /**
     * Obtiene un mantenimiento por su ID
     */
    public MantenimientoTercerizado obtenerPorId(int id) {
        Optional<MantenimientoTercerizado> resultado = mantenimientoDAO.findById(id);
        return resultado.orElse(null);
    }
    
    /**
     * Obtiene un proveedor por su ID
     */
    public ProveedorServicio obtenerProveedorPorId(int id) {
        Optional<ProveedorServicio> resultado = proveedorDAO.findById(id);
        return resultado.orElse(null);
    }
}