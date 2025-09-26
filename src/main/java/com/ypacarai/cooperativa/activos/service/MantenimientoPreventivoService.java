package com.ypacarai.cooperativa.activos.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import com.ypacarai.cooperativa.activos.dao.ActivoDAO;
import com.ypacarai.cooperativa.activos.dao.AlertaMantenimientoDAO;
import com.ypacarai.cooperativa.activos.dao.ConfiguracionMantenimientoDAO;
import com.ypacarai.cooperativa.activos.dao.MantenimientoDAO;
import com.ypacarai.cooperativa.activos.dao.TicketDAO;
import com.ypacarai.cooperativa.activos.model.Activo;
import com.ypacarai.cooperativa.activos.model.AlertaMantenimiento;
import com.ypacarai.cooperativa.activos.model.AlertaMantenimiento.NivelUrgencia;
import com.ypacarai.cooperativa.activos.model.AlertaMantenimiento.TipoAlerta;
import com.ypacarai.cooperativa.activos.model.ConfiguracionMantenimiento;
import com.ypacarai.cooperativa.activos.model.ConfiguracionMantenimiento.TipoActivo;
import com.ypacarai.cooperativa.activos.model.Mantenimiento;
import com.ypacarai.cooperativa.activos.model.Ticket;

/**
 * Servicio para mantenimiento preventivo con calendarios y alertas automáticas
 */
public class MantenimientoPreventivoService {
    
    private final ConfiguracionMantenimientoDAO configuracionDAO;
    private final AlertaMantenimientoDAO alertaDAO;
    private final ActivoDAO activoDAO;
    private final MantenimientoDAO mantenimientoDAO;
    private final TicketDAO ticketDAO;
    
    public MantenimientoPreventivoService() {
        this.configuracionDAO = new ConfiguracionMantenimientoDAO();
        this.alertaDAO = new AlertaMantenimientoDAO();
        this.activoDAO = new ActivoDAO();
        this.mantenimientoDAO = new MantenimientoDAO();
        this.ticketDAO = new TicketDAO();
    }
    
    // ===== CONFIGURACIÓN DE MANTENIMIENTOS =====
    
    /**
     * Obtiene todas las configuraciones de mantenimiento
     */
    public List<ConfiguracionMantenimiento> obtenerConfiguraciones() {
        return configuracionDAO.findAll();
    }
    
    /**
     * Obtiene configuración por tipo de activo
     */
    public Optional<ConfiguracionMantenimiento> obtenerConfiguracionPorTipo(TipoActivo tipoActivo) {
        ConfiguracionMantenimiento config = configuracionDAO.findByTipoActivo(tipoActivo);
        return Optional.ofNullable(config);
    }
    
    /**
     * Guarda o actualiza configuración de mantenimiento
     */
    public boolean guardarConfiguracion(ConfiguracionMantenimiento configuracion) {
        if (configuracion.getConfigId() == null) {
            return configuracionDAO.save(configuracion);
        } else {
            return configuracionDAO.update(configuracion);
        }
    }
    
    /**
     * Crea configuraciones por defecto si no existen
     */
    public void inicializarConfiguracionesPorDefecto() {
        configuracionDAO.crearConfiguracionesPorDefecto();
    }
    
    // ===== GENERACIÓN DE ALERTAS AUTOMÁTICAS =====
    
    /**
     * Ejecuta el proceso diario de generación de alertas
     * Este método debe ser llamado por el scheduler diariamente
     */
    public void ejecutarProcesoAlertasDiario() {
        System.out.println("Iniciando proceso diario de alertas de mantenimiento...");
        
        try {
            List<Activo> activosOperativos = activoDAO.findByEstado(Activo.Estado.Operativo);
            System.out.println("Revisando " + activosOperativos.size() + " activos operativos");
            
            int alertasGeneradas = 0;
            
            for (Activo activo : activosOperativos) {
                try {
                    boolean alertaGenerada = verificarYGenerarAlertaParaActivo(activo);
                    if (alertaGenerada) {
                        alertasGeneradas++;
                    }
                } catch (Exception e) {
                    System.err.println("Error al procesar activo " + activo.getActId() + ": " + e.getMessage());
                }
            }
            
            System.out.println("Proceso completado. Alertas generadas: " + alertasGeneradas);
            
        } catch (Exception e) {
            System.err.println("Error en proceso diario de alertas: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Verifica si un activo necesita mantenimiento y genera alerta si es necesario
     */
    private boolean verificarYGenerarAlertaParaActivo(Activo activo) {
        // Obtener configuración para este tipo de activo
        TipoActivo tipoActivo = mapearTipoActivo(activo.getTipoActivoNombre());
        if (tipoActivo == null) {
            return false; // Tipo no reconocido
        }
        
        Optional<ConfiguracionMantenimiento> configOpt = obtenerConfiguracionPorTipo(tipoActivo);
        if (!configOpt.isPresent()) {
            return false; // No hay configuración para este tipo
        }
        
        ConfiguracionMantenimiento config = configOpt.get();
        
        // Calcular fecha del último mantenimiento
        LocalDate fechaUltimoMantenimiento = obtenerFechaUltimoMantenimiento(activo.getActId());
        
        // Si no hay mantenimientos previos, usar fecha de adquisición
        if (fechaUltimoMantenimiento == null) {
            fechaUltimoMantenimiento = activo.getActFechaAdquisicion();
        }
        
        if (fechaUltimoMantenimiento == null) {
            return false; // No se puede determinar fecha base
        }
        
        // Calcular próxima fecha de mantenimiento
        LocalDate proximaFechaMantenimiento = fechaUltimoMantenimiento.plusDays(config.getDiasMantenimiento());
        
        // Calcular días restantes
        long diasRestantes = ChronoUnit.DAYS.between(LocalDate.now(), proximaFechaMantenimiento);
        
        // Verificar si debe generar alerta
        boolean debeGenerarAlerta = diasRestantes <= config.getDiasAnticipoAlerta();
        
        if (debeGenerarAlerta) {
            // Verificar que no exista ya una alerta activa para este activo
            if (!existeAlertaActivaParaActivo(activo.getActId(), TipoAlerta.PREVENTIVO_PROXIMO)) {
                return generarAlertaMantenimientoPreventivo(activo, config, (int) diasRestantes, proximaFechaMantenimiento);
            }
        }
        
        return false;
    }
    
    /**
     * Genera alerta de mantenimiento preventivo
     */
    private boolean generarAlertaMantenimientoPreventivo(Activo activo, ConfiguracionMantenimiento config, 
                                                        int diasRestantes, LocalDate fechaVencimiento) {
        
        // Determinar nivel de urgencia según días restantes
        NivelUrgencia nivelUrgencia;
        TipoAlerta tipoAlerta;
        
        if (diasRestantes <= 0) {
            nivelUrgencia = NivelUrgencia.CRITICO;
            tipoAlerta = TipoAlerta.PREVENTIVO_VENCIDO;
        } else if (diasRestantes <= 3) {
            nivelUrgencia = NivelUrgencia.URGENTE;
            tipoAlerta = TipoAlerta.PREVENTIVO_PROXIMO;
        } else if (diasRestantes <= 7) {
            nivelUrgencia = NivelUrgencia.ADVERTENCIA;
            tipoAlerta = TipoAlerta.PREVENTIVO_PROXIMO;
        } else {
            nivelUrgencia = NivelUrgencia.INFO;
            tipoAlerta = TipoAlerta.PREVENTIVO_PROXIMO;
        }
        
        // Crear alerta
        AlertaMantenimiento alerta = new AlertaMantenimiento();
        alerta.setActivoId(activo.getActId());
        alerta.setTipoAlerta(tipoAlerta);
        alerta.setNivelUrgencia(nivelUrgencia);
        alerta.setDiasRestantes(diasRestantes);
        alerta.setFechaVencimiento(fechaVencimiento);
        alerta.setUsuarioAsignadoId(config.getTecnicoDefaultId());
        
        // Configurar título y mensaje
        if (diasRestantes <= 0) {
            alerta.setTitulo("Mantenimiento Preventivo VENCIDO");
            alerta.setMensaje(String.format(
                "El mantenimiento preventivo del activo %s está VENCIDO desde hace %d día(s). " +
                "Es necesario realizar el mantenimiento inmediatamente.",
                activo.getActNumeroActivo(),
                Math.abs(diasRestantes)
            ));
        } else {
            alerta.setTitulo("Mantenimiento Preventivo Próximo");
            alerta.setMensaje(String.format(
                "El activo %s (%s) requiere mantenimiento preventivo en %d día(s). " +
                "Fecha programada: %s",
                activo.getActEspecificaciones(),
                activo.getTipoActivoNombre(),
                diasRestantes,
                fechaVencimiento.toString()
            ));
        }
        
        // Guardar alerta
        boolean alertaGuardada = alertaDAO.save(alerta);
        
        if (alertaGuardada) {
            System.out.println("Alerta generada para activo " + activo.getActId() + 
                             " - Días restantes: " + diasRestantes);
            
            // Si está vencido, también generar ticket automático
            if (diasRestantes <= 0) {
                generarTicketPreventivoAutomatico(activo, config, alerta);
            }
        }
        
        return alertaGuardada;
    }
    
    /**
     * Genera ticket preventivo automático para mantenimientos vencidos
     */
    private void generarTicketPreventivoAutomatico(Activo activo, ConfiguracionMantenimiento config, 
                                                  AlertaMantenimiento alerta) {
        try {
            // Verificar que no existe ticket preventivo pendiente para este activo
            if (existeTicketPreventivoActivoParaActivo(activo.getActId())) {
                return;
            }
            
            Ticket ticket = new Ticket();
            ticket.setActId(activo.getActId());
            ticket.setTickTipo(Ticket.Tipo.Preventivo);
            ticket.setTickEstado(Ticket.Estado.Abierto);
            ticket.setTickPrioridad(Ticket.Prioridad.Alta); // Preventivos vencidos son alta prioridad
            ticket.setTickTitulo("Mantenimiento Preventivo - " + activo.getActEspecificaciones());
            ticket.setTickDescripcion("Mantenimiento preventivo automáticamente programado para " + 
                                config.getTipoActivo().getDescripcion() + ".\n\n" +
                                "Actividades a realizar:\n" + 
                                (config.getActividadesPredefinidas() != null ? 
                                 config.getActividadesPredefinidas().replace("\n", "\n• ") : 
                                 "Revisar procedimientos estándar"));
            ticket.setTickAsignadoA(config.getTecnicoDefaultId());
            ticket.setTickFechaVencimiento(LocalDateTime.now().plusDays(1)); // Para mañana
            
            boolean ticketCreado = ticketDAO.save(ticket);
            
            if (ticketCreado) {
                System.out.println("Ticket preventivo automático creado: " + ticket.getTickId());
            }
            
        } catch (Exception e) {
            System.err.println("Error al generar ticket preventivo automático: " + e.getMessage());
        }
    }
    
    // ===== CONSULTAS Y UTILIDADES =====
    
    /**
     * Obtiene todas las alertas activas no leídas
     */
    public List<AlertaMantenimiento> obtenerAlertasActivasNoLeidas() {
        return alertaDAO.findAlertasActivasNoLeidas();
    }
    
    /**
     * Obtiene alertas por usuario
     */
    public List<AlertaMantenimiento> obtenerAlertasPorUsuario(Integer usuarioId, boolean soloActivas) {
        return alertaDAO.findByUsuarioAsignado(usuarioId, soloActivas);
    }
    
    /**
     * Obtiene alertas críticas
     */
    public List<AlertaMantenimiento> obtenerAlertasCriticas() {
        return alertaDAO.findAlertasCriticas();
    }
    
    /**
     * Marca alerta como leída
     */
    public boolean marcarAlertaComoLeida(Integer alertaId) {
        return alertaDAO.marcarComoLeida(alertaId);
    }
    
    /**
     * Desactiva una alerta
     */
    public boolean desactivarAlerta(Integer alertaId) {
        return alertaDAO.desactivar(alertaId);
    }
    
    /**
     * Cuenta alertas pendientes por usuario
     */
    public int contarAlertasPendientesPorUsuario(Integer usuarioId) {
        return alertaDAO.contarAlertasActivasPorUsuario(usuarioId);
    }
    
    /**
     * Obtiene resumen de alertas por tipo
     */
    public List<String[]> obtenerResumenAlertasPorTipo() {
        return alertaDAO.getResumenAlertasPorTipo();
    }
    
    // ===== MÉTODOS PRIVADOS DE UTILIDAD =====
    
    /**
     * Obtiene la fecha del último mantenimiento realizado para un activo
     */
    private LocalDate obtenerFechaUltimoMantenimiento(Integer activoId) {
        try {
            List<Mantenimiento> mantenimientos = mantenimientoDAO.findByActivo(activoId);
            
            if (!mantenimientos.isEmpty()) {
                // Obtener el mantenimiento más reciente completado
                for (Mantenimiento mant : mantenimientos) {
                    if (mant.getMantEstado() == Mantenimiento.EstadoMantenimiento.Completado && 
                        mant.getMantFechaFin() != null) {
                        return mant.getMantFechaFin().toLocalDate();
                    }
                }
            }
            
            return null;
            
        } catch (Exception e) {
            System.err.println("Error al obtener fecha último mantenimiento: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Verifica si existe alerta activa para un activo y tipo específico
     */
    private boolean existeAlertaActivaParaActivo(Integer activoId, TipoAlerta tipoAlerta) {
        try {
            List<AlertaMantenimiento> alertas = alertaDAO.findByActivo(activoId);
            
            return alertas.stream()
                    .anyMatch(alerta -> alerta.getActiva() && 
                             alerta.getTipoAlerta() == tipoAlerta);
            
        } catch (Exception e) {
            System.err.println("Error al verificar alertas existentes: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Verifica si existe ticket preventivo activo para un activo
     */
    private boolean existeTicketPreventivoActivoParaActivo(Integer activoId) {
        try {
            List<Ticket> tickets = ticketDAO.findByActivo(activoId);
            
            return tickets.stream()
                    .anyMatch(ticket -> ticket.getTickTipo() == Ticket.Tipo.Preventivo && 
                             (ticket.getTickEstado() == Ticket.Estado.Abierto || 
                              ticket.getTickEstado() == Ticket.Estado.En_Proceso));
            
        } catch (Exception e) {
            System.err.println("Error al verificar tickets preventivos existentes: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Mapea string de tipo de activo a enum TipoActivo
     */
    private TipoActivo mapearTipoActivo(String tipoActivoString) {
        if (tipoActivoString == null) return null;
        
        // Intentar mapeo directo primero
        for (TipoActivo tipo : TipoActivo.values()) {
            if (tipo.name().equalsIgnoreCase(tipoActivoString) ||
                tipo.getDescripcion().equalsIgnoreCase(tipoActivoString)) {
                return tipo;
            }
        }
        
        // Mapeo manual para casos especiales
        switch (tipoActivoString.toLowerCase()) {
            case "pc":
            case "computadora":
            case "desktop":
                return TipoActivo.PC_Escritorio;
                
            case "notebook":
            case "portatil":
                return TipoActivo.Laptop;
                
            case "impresora":
                return TipoActivo.Impresora_Inyeccion; // Por defecto
                
            case "ups":
            case "bateria":
                return TipoActivo.UPS;
                
            case "switch":
                return TipoActivo.Switch_Red;
                
            case "telefono":
            case "voip":
                return TipoActivo.Telefono_IP;
                
            default:
                return null;
        }
    }
}
