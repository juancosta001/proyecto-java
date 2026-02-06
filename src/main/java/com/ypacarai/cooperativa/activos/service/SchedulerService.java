package com.ypacarai.cooperativa.activos.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Servicio de automatización para ejecución programada de tareas del sistema
 * Implementa la funcionalidad crítica faltante identificada en análisis de protocolo
 * 
 * Cooperativa Ypacaraí LTDA - Sistema de Activos
 */
public class SchedulerService {
    private static final Logger LOGGER = Logger.getLogger(SchedulerService.class.getName());
    
    // Configuración por defecto (valores de respaldo si no se encuentra en BD)
    private static final int INTERVALO_ALERTAS_HORAS_DEFAULT = 8; 
    private static final int INTERVALO_PREVENTIVO_HORAS_DEFAULT = 24;
    private static final int INTERVALO_TICKETS_HORAS_DEFAULT = 168; // 1 semana
    private static final int DELAY_INICIAL_MINUTOS_DEFAULT = 5;
    private static final int MAX_HILOS_DEFAULT = 3;
    
    // Configuraciones dinámicas (leídas de BD)
    private int intervaloAlertasHoras;
    private int intervaloPreventivoHoras;
    private int intervaloTicketsHoras;
    private int delayInicialMinutos;
    private int maxHilos;
    private boolean autoInicio;
    
    // Componentes del scheduler
    private final ScheduledExecutorService scheduler;
    private final MantenimientoPreventivoService mantenimientoService;
    private final TicketService ticketService;
    private final EmailService emailService;
    private final ConfiguracionService configuracionService;
    
    // Control de jobs
    private ScheduledFuture<?> alertasJob;
    private ScheduledFuture<?> mantenimientoPreventivoJob;
    private ScheduledFuture<?> ticketsPreventivosJob;
    private boolean schedulerActivo = false;
    
    // Estadísticas
    private int ejecucionesAlertas = 0;
    private int ejecucionesMantenimiento = 0;
    private int ejecucionesTickets = 0;
    private LocalDateTime ultimaEjecucionAlertas;
    private LocalDateTime ultimaEjecucionMantenimiento;
    private LocalDateTime ultimaEjecucionTickets;
    
    public SchedulerService() {
        // Inicializar servicios
        this.configuracionService = new ConfiguracionService();
        this.mantenimientoService = new MantenimientoPreventivoService();
        
        try {
            this.ticketService = new TicketService();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error inicializando TicketService", e);
            throw new RuntimeException("Fallo inicializando TicketService", e);
        }
        
        this.emailService = new EmailService();
        
        // Cargar configuraciones desde BD (con valores por defecto como respaldo)
        cargarConfiguraciones();
        
        // Crear scheduler con hilos configurables
        this.scheduler = Executors.newScheduledThreadPool(this.maxHilos);
        
        LOGGER.log(Level.INFO, "SchedulerService inicializado con pool de {0} hilos", this.maxHilos);
        LOGGER.log(Level.INFO, "Configuración: Alertas cada {0}h, Mantenimiento cada {1}h, Delay inicial {2}min", 
                   new Object[]{this.intervaloAlertasHoras, this.intervaloPreventivoHoras, this.delayInicialMinutos});
        
        // Auto-inicio si está configurado
        if (this.autoInicio) {
            LOGGER.log(Level.INFO, "Auto-inicio habilitado - iniciando scheduler automáticamente");
            iniciarScheduler();
        }
    }
    
    /**
     * Carga las configuraciones del scheduler desde la base de datos
     * Usa valores por defecto si no se encuentran en BD
     */
    private void cargarConfiguraciones() {
        try {
            // Cargar configuraciones con valores por defecto como respaldo
            this.intervaloAlertasHoras = Integer.parseInt(
                configuracionService.obtenerValorConfiguracion("scheduler.alertas_intervalo_horas", 
                String.valueOf(INTERVALO_ALERTAS_HORAS_DEFAULT)));
            
            this.intervaloPreventivoHoras = Integer.parseInt(
                configuracionService.obtenerValorConfiguracion("scheduler.mantenimiento_intervalo_horas", 
                String.valueOf(INTERVALO_PREVENTIVO_HORAS_DEFAULT)));
            
            this.intervaloTicketsHoras = Integer.parseInt(
                configuracionService.obtenerValorConfiguracion("scheduler.tickets_intervalo_horas", 
                String.valueOf(INTERVALO_TICKETS_HORAS_DEFAULT)));
            
            this.delayInicialMinutos = Integer.parseInt(
                configuracionService.obtenerValorConfiguracion("scheduler.delay_inicial_minutos", 
                String.valueOf(DELAY_INICIAL_MINUTOS_DEFAULT)));
            
            this.maxHilos = Integer.parseInt(
                configuracionService.obtenerValorConfiguracion("scheduler.max_hilos", 
                String.valueOf(MAX_HILOS_DEFAULT)));
            
            this.autoInicio = Boolean.parseBoolean(
                configuracionService.obtenerValorConfiguracion("scheduler.auto_inicio", "true"));
            
            LOGGER.log(Level.INFO, "📋 Configuraciones del scheduler cargadas desde BD");
            
        } catch (Exception e) {
            // En caso de error, usar valores por defecto
            LOGGER.log(Level.WARNING, "⚠️ Error cargando configuraciones del scheduler, usando valores por defecto: {0}", e.getMessage());
            this.intervaloAlertasHoras = INTERVALO_ALERTAS_HORAS_DEFAULT;
            this.intervaloPreventivoHoras = INTERVALO_PREVENTIVO_HORAS_DEFAULT;
            this.intervaloTicketsHoras = INTERVALO_TICKETS_HORAS_DEFAULT;
            this.delayInicialMinutos = DELAY_INICIAL_MINUTOS_DEFAULT;
            this.maxHilos = MAX_HILOS_DEFAULT;
            this.autoInicio = true;
        }
    }
    
    /**
     * Inicia todos los jobs automáticos del sistema
     */
    public synchronized void iniciarScheduler() {
        if (schedulerActivo) {
            LOGGER.log(Level.WARNING, "Scheduler ya está activo, saltando inicialización");
            return;
        }
        
        try {
            LOGGER.log(Level.INFO, "=== INICIANDO SCHEDULER AUTOMÁTICO ===");
            
            // Job 1: Alertas de mantenimiento (intervalo configurable)
            alertasJob = scheduler.scheduleAtFixedRate(
                this::ejecutarProcesoAlertas,
                this.delayInicialMinutos, // Delay inicial configurable
                this.intervaloAlertasHoras * 60, // Período en minutos 
                TimeUnit.MINUTES
            );
            
            // Job 2: Mantenimiento preventivo (intervalo configurable)
            mantenimientoPreventivoJob = scheduler.scheduleAtFixedRate(
                this::ejecutarProcesoMantenimientoPreventivo,
                this.delayInicialMinutos + 2, // Delay inicial + 2 min
                this.intervaloPreventivoHoras * 60, // Período en minutos  
                TimeUnit.MINUTES
            );
            
            // Job 3: Generación automática de tickets preventivos (intervalo configurable)
            ticketsPreventivosJob = scheduler.scheduleAtFixedRate(
                this::ejecutarProcesoTicketsPreventivos,
                this.delayInicialMinutos + 5, // Delay inicial + 5 min
                this.intervaloTicketsHoras * 60, // Período en minutos (default: 1 semana)
                TimeUnit.MINUTES
            );
            
            schedulerActivo = true;
            
            LOGGER.log(Level.INFO, "✅ Scheduler iniciado exitosamente:");
            LOGGER.log(Level.INFO, "   🔔 Alertas automáticas cada {0} horas", this.intervaloAlertasHoras);
            LOGGER.log(Level.INFO, "   🔧 Mantenimiento preventivo cada {0} horas", this.intervaloPreventivoHoras);
            LOGGER.log(Level.INFO, "   🎫 Tickets preventivos cada {0} horas", this.intervaloTicketsHoras);
            LOGGER.log(Level.INFO, "   ⏱️  Próxima ejecución en {0} minutos", this.delayInicialMinutos);
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "❌ Error iniciando scheduler automático", e);
            throw new RuntimeException("Fallo en inicialización del scheduler", e);
        }
    }
    
    /**
     * Detiene todos los jobs automáticos
     */
    public synchronized void detenerScheduler() {
        LOGGER.log(Level.INFO, "=== DETENIENDO SCHEDULER ===");
        
        try {
            if (alertasJob != null && !alertasJob.isCancelled()) {
                alertasJob.cancel(true);
                LOGGER.log(Level.INFO, "✅ Job de alertas cancelado");
            }
            
            if (mantenimientoPreventivoJob != null && !mantenimientoPreventivoJob.isCancelled()) {
                mantenimientoPreventivoJob.cancel(true);
                LOGGER.log(Level.INFO, "✅ Job de mantenimiento preventivo cancelado");
            }
            
            if (ticketsPreventivosJob != null && !ticketsPreventivosJob.isCancelled()) {
                ticketsPreventivosJob.cancel(true);
                LOGGER.log(Level.INFO, "✅ Job de tickets preventivos cancelado");
            }
            
            schedulerActivo = false;
            LOGGER.log(Level.INFO, "✅ Scheduler detenido exitosamente");
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "❌ Error deteniendo scheduler", e);
        }
    }
    
    /**
     * Shutdown completo del scheduler
     */
    public void shutdown() {
        LOGGER.log(Level.INFO, "=== SHUTDOWN SCHEDULER ===");
        
        detenerScheduler();
        
        try {
            scheduler.shutdown();
            if (!scheduler.awaitTermination(10, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
                if (!scheduler.awaitTermination(10, TimeUnit.SECONDS)) {
                    LOGGER.log(Level.WARNING, "⚠️ Scheduler no terminó correctamente");
                }
            }
            LOGGER.log(Level.INFO, "✅ Scheduler shutdown completo");
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
            LOGGER.log(Level.WARNING, "⚠️ Shutdown interrumpido", e);
        }
    }
    
    /**
     * Job automático: Proceso de alertas de mantenimiento
     */
    private void ejecutarProcesoAlertas() {
        try {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
            LOGGER.log(Level.INFO, "🔔 [SCHEDULER] Ejecutando proceso automático de alertas - {0}", timestamp);
            
            // Ejecutar lógica existente de alertas
            mantenimientoService.ejecutarProcesoAlertasDiario();
            
            // Estadísticas
            ejecucionesAlertas++;
            ultimaEjecucionAlertas = LocalDateTime.now();
            
            LOGGER.log(Level.INFO, "✅ [SCHEDULER] Proceso de alertas completado - Ejecución #{0}", ejecucionesAlertas);
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "❌ [SCHEDULER] Error en proceso automático de alertas", e);
            
            // Intentar enviar alerta de error por email
            try {
                emailService.enviarAlertaError("Error en Scheduler de Alertas", 
                    "Error ejecutando proceso automático de alertas: " + e.getMessage());
            } catch (Exception emailError) {
                LOGGER.log(Level.SEVERE, "❌ Error adicional enviando email de error", emailError);
            }
        }
    }
    
    /**
     * Job automático: Proceso de mantenimiento preventivo
     */
    private void ejecutarProcesoMantenimientoPreventivo() {
        try {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
            LOGGER.log(Level.INFO, "🔧 [SCHEDULER] Ejecutando proceso automático de mantenimiento preventivo - {0}", timestamp);
            
            // Lógica específica de mantenimiento preventivo diario
            ejecutarMantenimientoPreventivoCompleto();
            
            // Estadísticas
            ejecucionesMantenimiento++;
            ultimaEjecucionMantenimiento = LocalDateTime.now();
            
            LOGGER.log(Level.INFO, "✅ [SCHEDULER] Proceso de mantenimiento preventivo completado - Ejecución #{0}", ejecucionesMantenimiento);
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "❌ [SCHEDULER] Error en proceso automático de mantenimiento preventivo", e);
            
            try {
                emailService.enviarAlertaError("Error en Scheduler de Mantenimiento", 
                    "Error ejecutando proceso automático de mantenimiento preventivo: " + e.getMessage());
            } catch (Exception emailError) {
                LOGGER.log(Level.SEVERE, "❌ Error adicional enviando email de error", emailError);
            }
        }
    }
    
    /**
     * Ejecuta proceso completo de mantenimiento preventivo
     */
    private void ejecutarMantenimientoPreventivoCompleto() {
        // 1. Actualizar configuraciones desde base de datos
        configuracionService.recargarConfiguraciones();
        
        // 2. Ejecutar proceso de alertas preventivas
        mantenimientoService.ejecutarProcesoAlertasDiario();
        
        // 3. Actualizar días restantes de alertas existentes
        // (Esto se maneja automáticamente en la lógica de alertas)
        
        // 4. Log de resumen
        int alertasActivas = mantenimientoService.obtenerAlertasActivasNoLeidas().size();
        int alertasCriticas = mantenimientoService.obtenerAlertasCriticas().size();
        
        LOGGER.log(Level.INFO, "📊 [SCHEDULER] Resumen mantenimiento preventivo:");
        LOGGER.log(Level.INFO, "   📢 Alertas activas: {0}", alertasActivas);
        LOGGER.log(Level.INFO, "   🚩 Alertas críticas: {0}", alertasCriticas);
    }
    
    /**
     * Job automático: Generación de tickets preventivos
     */
    private void ejecutarProcesoTicketsPreventivos() {
        try {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
            LOGGER.log(Level.INFO, "🎫 [SCHEDULER] Ejecutando generación automática de tickets preventivos - {0}", timestamp);
            
            // Generar tickets preventivos automáticamente
            int ticketsGenerados = ticketService.generarTicketsPreventivos();
            
            // Estadísticas
            ejecucionesTickets++;
            ultimaEjecucionTickets = LocalDateTime.now();
            
            LOGGER.log(Level.INFO, "✅ [SCHEDULER] Generación de tickets completada - {0} tickets creados - Ejecución #{1}", 
                      new Object[]{ticketsGenerados, ejecucionesTickets});
            
            // Si se generaron tickets, enviar notificación
            if (ticketsGenerados > 0) {
                try {
                    emailService.enviarNotificacionTicketsGenerados(ticketsGenerados);
                } catch (Exception emailError) {
                    LOGGER.log(Level.WARNING, "⚠️ No se pudo enviar notificación por email: {0}", emailError.getMessage());
                }
            }
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "❌ [SCHEDULER] Error en generación automática de tickets", e);
            
            try {
                emailService.enviarAlertaError("Error en Scheduler de Tickets", 
                    "Error ejecutando generación automática de tickets preventivos: " + e.getMessage());
            } catch (Exception emailError) {
                LOGGER.log(Level.SEVERE, "❌ Error adicional enviando email de error", emailError);
            }
        }
    }
    
    /**
     * Ejecuta una tarea única programada (para pruebas)
     */
    public void ejecutarTareaUnica(Runnable tarea, int delayMinutos) {
        scheduler.schedule(tarea, delayMinutos, TimeUnit.MINUTES);
        LOGGER.log(Level.INFO, "⏰ Tarea única programada para ejecutar en {0} minutos", delayMinutos);
    }
    
    /**
     * Fuerza ejecución inmediata de alertas (para pruebas)
     */
    public void ejecutarAlertasAhora() {
        LOGGER.log(Level.INFO, "🔔 Ejecutando alertas manualmente...");
        ejecutarProcesoAlertas();
    }
    
    /**
     * Fuerza ejecución inmediata de mantenimiento preventivo (para pruebas)
     */
    public void ejecutarMantenimientoPreventivoAhora() {
        LOGGER.log(Level.INFO, "🔧 Ejecutando mantenimiento preventivo manualmente...");
        ejecutarProcesoMantenimientoPreventivo();
    }
    
    /**
     * Fuerza ejecución inmediata de generación de tickets (para pruebas)
     */
    public void ejecutarTicketsPreventivosAhora() {
        LOGGER.log(Level.INFO, "🎫 Ejecutando generación de tickets manualmente...");
        ejecutarProcesoTicketsPreventivos();
    }
    
    // ===== GETTERS PARA MONITOREO =====
    
    public boolean isSchedulerActivo() { return schedulerActivo; }
    public int getEjecucionesAlertas() { return ejecucionesAlertas; }
    public int getEjecucionesMantenimiento() { return ejecucionesMantenimiento; }
    public int getEjecucionesTickets() { return ejecucionesTickets; }
    public LocalDateTime getUltimaEjecucionAlertas() { return ultimaEjecucionAlertas; }
    public LocalDateTime getUltimaEjecucionMantenimiento() { return ultimaEjecucionMantenimiento; }
    public LocalDateTime getUltimaEjecucionTickets() { return ultimaEjecucionTickets; }
    
    /**
     * Obtiene estado detallado del scheduler para monitoreo
     */
    public String getEstadoScheduler() {
        StringBuilder estado = new StringBuilder();
        estado.append("=== ESTADO SCHEDULER ===\n");
        estado.append("Activo: ").append(schedulerActivo ? "✅ SÍ" : "❌ NO").append("\n");
        estado.append("Ejecuciones alertas: ").append(ejecucionesAlertas).append("\n");
        estado.append("Ejecuciones mantenimiento: ").append(ejecucionesMantenimiento).append("\n");
        estado.append("Ejecuciones tickets: ").append(ejecucionesTickets).append("\n");
        
        if (ultimaEjecucionAlertas != null) {
            estado.append("Última ejecución alertas: ")
                  .append(ultimaEjecucionAlertas.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")))
                  .append("\n");
        }
        
        if (ultimaEjecucionMantenimiento != null) {
            estado.append("Última ejecución mantenimiento: ")
                  .append(ultimaEjecucionMantenimiento.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")))
                  .append("\n");
        }
        
        if (ultimaEjecucionTickets != null) {
            estado.append("Última ejecución tickets: ")
                  .append(ultimaEjecucionTickets.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")))
                  .append("\n");
        }
        
        return estado.toString();
    }
    
    /**
     * Recarga las configuraciones desde la base de datos y reinicia el scheduler si está activo
     * Útil para aplicar cambios de configuración sin reiniciar la aplicación
     */
    public synchronized void recargarConfiguracionesYReiniciar() {
        LOGGER.log(Level.INFO, "🔄 Recargando configuraciones del scheduler...");
        
        boolean estabaPreviamenteActivo = schedulerActivo;
        
        // Detener scheduler si estaba activo
        if (estabaPreviamenteActivo) {
            detenerScheduler();
        }
        
        // Recargar configuraciones  
        cargarConfiguraciones();
        
        // Crear nuevo pool con el número de hilos actualizado si es necesario
        if (scheduler.isShutdown()) {
            // Solo recrear si el scheduler anterior fue cerrado completamente
            LOGGER.log(Level.INFO, "🔧 Recreando pool de hilos con {0} hilos", this.maxHilos);
        }
        
        LOGGER.log(Level.INFO, "✅ Configuraciones recargadas - Alertas: {0}h, Mantenimiento: {1}h, Tickets: {2}h", 
                  new Object[]{this.intervaloAlertasHoras, this.intervaloPreventivoHoras, this.intervaloTicketsHoras});
        
        // Reiniciar si estaba activo anteriormente
        if (estabaPreviamenteActivo) {
            LOGGER.log(Level.INFO, "🚀 Reiniciando scheduler con nuevas configuraciones...");
            iniciarScheduler();
        }
    }
    
    /**
     * Obtiene las configuraciones actuales como String para mostrar en interfaz
     */
    public String obtenerConfiguracionesActuales() {
        StringBuilder config = new StringBuilder();
        config.append("=== CONFIGURACIONES SCHEDULER ===\n");
        config.append("Intervalo alertas: ").append(this.intervaloAlertasHoras).append(" horas\n");
        config.append("Intervalo mantenimiento: ").append(this.intervaloPreventivoHoras).append(" horas\n");
        config.append("Intervalo tickets: ").append(this.intervaloTicketsHoras).append(" horas\n");
        config.append("Delay inicial: ").append(this.delayInicialMinutos).append(" minutos\n");
        config.append("Máximo hilos: ").append(this.maxHilos).append("\n");
        config.append("Auto-inicio: ").append(this.autoInicio ? "Habilitado" : "Deshabilitado").append("\n");
        return config.toString();
    }
}