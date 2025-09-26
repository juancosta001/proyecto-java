package com.ypacarai.cooperativa.activos.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.ypacarai.cooperativa.activos.config.DatabaseConfig;
import com.ypacarai.cooperativa.activos.model.DashboardData;
import com.ypacarai.cooperativa.activos.model.ReporteEstadoActivos;
import com.ypacarai.cooperativa.activos.model.ReporteFallas;
import com.ypacarai.cooperativa.activos.model.ReporteMantenimientos;
import com.ypacarai.cooperativa.activos.model.ReporteTraslados;

/**
 * DAO para generar todos los tipos de reportes del sistema
 * Implementación robusta y optimizada para producción
 */
public class ReportesDAOSimple {
    private static final Logger LOGGER = Logger.getLogger(ReportesDAOSimple.class.getName());
    
    /**
     * Genera el reporte completo de estado de activos
     */
    public List<ReporteEstadoActivos> generarReporteEstadoActivos(LocalDate fechaInicio, LocalDate fechaFin, 
                                                                 String tipoActivo, String ubicacion) {
        List<ReporteEstadoActivos> reportes = new ArrayList<>();
        
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ")
           .append("    ta.nombre as tipo_activo, ")
           .append("    a.act_estado as estado, ")
           .append("    COUNT(*) as cantidad_total, ")
           .append("    u.ubi_nombre as ubicacion, ")
           .append("    SUM(CASE WHEN EXISTS( ")
           .append("        SELECT 1 FROM TICKET t ")
           .append("        WHERE t.act_id = a.act_id ")
           .append("        AND t.tick_tipo = 'Preventivo' ")
           .append("        AND t.tick_fecha_vencimiento BETWEEN CURRENT_DATE ")
           .append("        AND DATE_ADD(CURRENT_DATE, INTERVAL 7 DAY) ")
           .append("        AND t.tick_estado IN ('Abierto', 'En_Proceso') ")
           .append("    ) THEN 1 ELSE 0 END) as proximos_mantenimiento, ")
           .append("    SUM(CASE WHEN EXISTS( ")
           .append("        SELECT 1 FROM TICKET t ")
           .append("        WHERE t.act_id = a.act_id ")
           .append("        AND t.tick_tipo = 'Preventivo' ")
           .append("        AND t.tick_fecha_vencimiento < CURRENT_DATE ")
           .append("        AND t.tick_estado IN ('Abierto', 'En_Proceso') ")
           .append("    ) THEN 1 ELSE 0 END) as mantenimiento_vencido ")
           .append("FROM ACTIVO a ")
           .append("INNER JOIN TIPO_ACTIVO ta ON a.tip_act_id = ta.tip_act_id ")
           .append("INNER JOIN UBICACION u ON a.act_ubicacion_actual = u.ubi_id ")
           .append("WHERE 1=1 ");
        
        List<Object> parametros = new ArrayList<>();
        
        if (fechaInicio != null && fechaFin != null) {
            sql.append("AND DATE(a.creado_en) BETWEEN ? AND ? ");
            parametros.add(java.sql.Date.valueOf(fechaInicio));
            parametros.add(java.sql.Date.valueOf(fechaFin));
        }
        
        if (tipoActivo != null && !tipoActivo.trim().isEmpty()) {
            sql.append("AND ta.nombre = ? ");
            parametros.add(tipoActivo);
        }
        
        if (ubicacion != null && !ubicacion.trim().isEmpty()) {
            sql.append("AND u.ubi_nombre LIKE ? ");
            parametros.add("%" + ubicacion + "%");
        }
        
        sql.append("GROUP BY ta.nombre, a.act_estado, u.ubi_nombre ")
           .append("ORDER BY ta.nombre, u.ubi_nombre, a.act_estado");
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            
            // Establecer parámetros
            for (int i = 0; i < parametros.size(); i++) {
                stmt.setObject(i + 1, parametros.get(i));
            }
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ReporteEstadoActivos reporte = new ReporteEstadoActivos();
                    reporte.setTipoActivo(rs.getString("tipo_activo"));
                    reporte.setEstado(rs.getString("estado"));
                    reporte.setCantidadTotal(rs.getInt("cantidad_total"));
                    reporte.setUbicacion(rs.getString("ubicacion"));
                    reporte.setActivosProximosMantenimiento(rs.getInt("proximos_mantenimiento"));
                    reporte.setActivosMantenimientoVencido(rs.getInt("mantenimiento_vencido"));
                    reporte.setFechaConsulta(LocalDate.now());
                    
                    reportes.add(reporte);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error generando reporte de estado de activos", e);
        }
        
        return reportes;
    }
    
    /**
     * Obtiene los datos para el Dashboard Ejecutivo
     */
    public DashboardData obtenerDatosDashboard() {
        DashboardData dashboard = new DashboardData();
        
        try (Connection conn = DatabaseConfig.getConnection()) {
            // KPIs principales
            obtenerKPIsPrincipales(conn, dashboard);
            
            // Estadísticas de productividad
            obtenerEstadisticasProductividad(conn, dashboard);
            
            // Rankings y tendencias
            obtenerRankingsYTendencias(conn, dashboard);
            
            // Datos para gráficos
            obtenerDatosGraficos(conn, dashboard);
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error obteniendo datos del dashboard", e);
        }
        
        return dashboard;
    }
    
    private void obtenerKPIsPrincipales(Connection conn, DashboardData dashboard) throws SQLException {
        String sql = "SELECT " +
            "(SELECT COUNT(*) FROM ACTIVO) as total_activos, " +
            "(SELECT COUNT(*) FROM TICKET WHERE tick_estado IN ('Abierto', 'En_Proceso')) as tickets_abiertos, " +
            "(SELECT COUNT(*) FROM MANTENIMIENTO WHERE mant_estado IN ('Programado', 'En_Proceso')) as mantenimientos_pendientes, " +
            "(SELECT COUNT(*) FROM ACTIVO WHERE act_estado = 'Operativo') as activos_operativos, " +
            "(SELECT COUNT(*) FROM ACTIVO WHERE act_estado = 'En_Mantenimiento') as activos_mantenimiento, " +
            "(SELECT COUNT(*) FROM ACTIVO WHERE act_estado = 'Fuera_Servicio') as activos_fuera_servicio";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                dashboard.setTotalActivos(rs.getInt("total_activos"));
                dashboard.setTicketsAbiertos(rs.getInt("tickets_abiertos"));
                dashboard.setMantenimientosPendientes(rs.getInt("mantenimientos_pendientes"));
                dashboard.setActivosOperativos(rs.getInt("activos_operativos"));
                dashboard.setActivosEnMantenimiento(rs.getInt("activos_mantenimiento"));
                dashboard.setActivosFueraServicio(rs.getInt("activos_fuera_servicio"));
            }
        }
    }
    
    private void obtenerEstadisticasProductividad(Connection conn, DashboardData dashboard) throws SQLException {
        String sql = "SELECT " +
            "COUNT(*) as mantenimientos_mes, " +
            "AVG(CASE WHEN mant_fecha_fin IS NOT NULL " +
                "THEN TIMESTAMPDIFF(MINUTE, mant_fecha_inicio, mant_fecha_fin) / 60.0 " +
                "ELSE NULL END) as tiempo_promedio, " +
            "(SELECT COUNT(*) FROM ALERTA WHERE ale_prioridad = 'Critica' AND ale_estado = 'Pendiente') as alertas_criticas, " +
            "(SELECT COUNT(*) FROM TRASLADO WHERE tras_estado IN ('Programado', 'En_Transito')) as traslados_proceso " +
            "FROM MANTENIMIENTO " +
            "WHERE MONTH(mant_fecha_inicio) = MONTH(CURRENT_DATE) " +
            "AND YEAR(mant_fecha_inicio) = YEAR(CURRENT_DATE) " +
            "AND mant_estado = 'Completado'";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                dashboard.setMantenimientosCompletadosMes(rs.getInt("mantenimientos_mes"));
                dashboard.setTiempoPromedioResolucion(rs.getDouble("tiempo_promedio"));
                dashboard.setAlertasCriticas(rs.getInt("alertas_criticas"));
                dashboard.setTrasladosEnProceso(rs.getInt("traslados_proceso"));
            }
        }
    }
    
    private void obtenerRankingsYTendencias(Connection conn, DashboardData dashboard) throws SQLException {
        // Activo más problemático
        String sqlActivo = "SELECT a.act_numero_activo, COUNT(*) as total_fallas " +
            "FROM TICKET t " +
            "INNER JOIN ACTIVO a ON t.act_id = a.act_id " +
            "WHERE t.tick_tipo = 'Correctivo' " +
            "AND YEAR(t.tick_fecha_apertura) = YEAR(CURRENT_DATE) " +
            "GROUP BY a.act_numero_activo " +
            "ORDER BY total_fallas DESC " +
            "LIMIT 1";
        
        try (PreparedStatement stmt = conn.prepareStatement(sqlActivo);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                dashboard.setActivoMasProblematico(rs.getString("act_numero_activo"));
            }
        }
        
        // Técnico más productivo
        String sqlTecnico = "SELECT u.usu_nombre, COUNT(*) as total_mantenimientos " +
            "FROM MANTENIMIENTO m " +
            "INNER JOIN USUARIO u ON m.mant_tecnico_asignado = u.usu_id " +
            "WHERE MONTH(m.mant_fecha_inicio) = MONTH(CURRENT_DATE) " +
            "AND YEAR(m.mant_fecha_inicio) = YEAR(CURRENT_DATE) " +
            "AND m.mant_estado = 'Completado' " +
            "GROUP BY u.usu_nombre " +
            "ORDER BY total_mantenimientos DESC " +
            "LIMIT 1";
        
        try (PreparedStatement stmt = conn.prepareStatement(sqlTecnico);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                dashboard.setTecnicoMasProductivo(rs.getString("usu_nombre"));
                dashboard.setMantenimientosTecnicoMes(rs.getInt("total_mantenimientos"));
            }
        }
    }
    
    private void obtenerDatosGraficos(Connection conn, DashboardData dashboard) throws SQLException {
        // Datos para gráfico de fallas por mes (últimos 12 meses)
        String sqlFallasMes = "SELECT " +
            "MONTH(tick_fecha_apertura) as mes, " +
            "COUNT(*) as total_fallas " +
            "FROM TICKET " +
            "WHERE tick_tipo = 'Correctivo' " +
            "AND tick_fecha_apertura >= DATE_SUB(CURRENT_DATE, INTERVAL 12 MONTH) " +
            "GROUP BY MONTH(tick_fecha_apertura) " +
            "ORDER BY mes";
        
        int[] fallasPorMes = new int[12];
        try (PreparedStatement stmt = conn.prepareStatement(sqlFallasMes);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                int mes = rs.getInt("mes") - 1; // Convertir a índice 0-11
                if (mes >= 0 && mes < 12) {
                    fallasPorMes[mes] = rs.getInt("total_fallas");
                }
            }
        }
        dashboard.setFallasPorMes(fallasPorMes);
        
        // Datos para gráfico de mantenimientos por tipo
        String sqlMantenimientoTipo = "SELECT " +
            "mant_tipo, " +
            "COUNT(*) as total " +
            "FROM MANTENIMIENTO " +
            "WHERE YEAR(mant_fecha_inicio) = YEAR(CURRENT_DATE) " +
            "GROUP BY mant_tipo";
        
        int[] mantenimientosPorTipo = new int[2]; // [Preventivo, Correctivo]
        try (PreparedStatement stmt = conn.prepareStatement(sqlMantenimientoTipo);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                String tipo = rs.getString("mant_tipo");
                int total = rs.getInt("total");
                if ("Preventivo".equals(tipo)) {
                    mantenimientosPorTipo[0] = total;
                } else if ("Correctivo".equals(tipo)) {
                    mantenimientosPorTipo[1] = total;
                }
            }
        }
        dashboard.setMantenimientosPorTipo(mantenimientosPorTipo);
    }
    
    /**
     * Genera el reporte completo de mantenimientos
     */
    public List<ReporteMantenimientos> generarReporteMantenimientos(LocalDate fechaInicio, LocalDate fechaFin,
                                                                   String tipoMantenimiento, Integer tecnicoId) {
        List<ReporteMantenimientos> reportes = new ArrayList<>();
        
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ")
           .append("    m.mant_tipo, ")
           .append("    COUNT(*) as total_mantenimientos, ")
           .append("    AVG(CASE WHEN m.mant_fecha_fin IS NOT NULL ")
           .append("        THEN TIMESTAMPDIFF(MINUTE, m.mant_fecha_inicio, m.mant_fecha_fin) / 60.0 ")
           .append("        ELSE NULL END) as tiempo_promedio_horas, ")
           .append("    u.usu_nombre as tecnico_nombre, ")
           .append("    SUM(m.mant_costo) as costo_total, ")
           .append("    COUNT(CASE WHEN m.mant_estado = 'Completado' THEN 1 END) as completados, ")
           .append("    COUNT(CASE WHEN m.mant_estado = 'En_Proceso' THEN 1 END) as en_proceso, ")
           .append("    COUNT(CASE WHEN m.mant_estado = 'Programado' THEN 1 END) as programados, ")
           .append("    ta.nombre as tipo_activo ")
           .append("FROM MANTENIMIENTO m ")
           .append("LEFT JOIN USUARIO u ON m.mant_tecnico_asignado = u.usu_id ")
           .append("LEFT JOIN ACTIVO a ON m.act_id = a.act_id ")
           .append("LEFT JOIN TIPO_ACTIVO ta ON a.tip_act_id = ta.tip_act_id ")
           .append("WHERE 1=1 ");
        
        List<Object> parametros = new ArrayList<>();
        
        if (fechaInicio != null && fechaFin != null) {
            sql.append("AND DATE(m.mant_fecha_inicio) BETWEEN ? AND ? ");
            parametros.add(java.sql.Date.valueOf(fechaInicio));
            parametros.add(java.sql.Date.valueOf(fechaFin));
        }
        
        if (tipoMantenimiento != null && !tipoMantenimiento.trim().isEmpty()) {
            sql.append("AND m.mant_tipo = ? ");
            parametros.add(tipoMantenimiento);
        }
        
        if (tecnicoId != null) {
            sql.append("AND m.mant_tecnico_asignado = ? ");
            parametros.add(tecnicoId);
        }
        
        sql.append("GROUP BY m.mant_tipo, u.usu_nombre, ta.nombre ")
           .append("ORDER BY m.mant_tipo, u.usu_nombre, ta.nombre");
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            
            for (int i = 0; i < parametros.size(); i++) {
                stmt.setObject(i + 1, parametros.get(i));
            }
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ReporteMantenimientos reporte = new ReporteMantenimientos();
                    reporte.setTipoMantenimiento(rs.getString("mant_tipo"));
                    reporte.setTotalMantenimientos(rs.getInt("total_mantenimientos"));
                    reporte.setTiempoPromedioResolucion(rs.getDouble("tiempo_promedio_horas"));
                    reporte.setTecnicoAsignado(rs.getString("tecnico_nombre"));
                    
                    java.math.BigDecimal costo = rs.getBigDecimal("costo_total");
                    if (costo != null) {
                        reporte.setCostoTotal(costo.doubleValue());
                    }
                    
                    reporte.setTipoActivo(rs.getString("tipo_activo"));
                    
                    // Calcular productividad del técnico (mantenimientos completados)
                    reporte.setProductividadTecnico(rs.getInt("completados"));
                    
                    // Determinar estado predominante
                    int completados = rs.getInt("completados");
                    int enProceso = rs.getInt("en_proceso");
                    int programados = rs.getInt("programados");
                    
                    if (completados >= enProceso && completados >= programados) {
                        reporte.setEstadoMantenimiento("Completado");
                    } else if (enProceso >= programados) {
                        reporte.setEstadoMantenimiento("En_Proceso");
                    } else {
                        reporte.setEstadoMantenimiento("Programado");
                    }
                    
                    reportes.add(reporte);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error generando reporte de mantenimientos", e);
        }
        
        return reportes;
    }
    
    /**
     * Genera el reporte completo de fallas
     */
    public List<ReporteFallas> generarReporteFallas(LocalDate fechaInicio, LocalDate fechaFin,
                                                   String tipoActivo, String ubicacion) {
        List<ReporteFallas> reportes = new ArrayList<>();
        
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ")
           .append("    ta.nombre as tipo_activo, ")
           .append("    a.act_numero_activo, ")
           .append("    t.tick_descripcion as descripcion_falla, ")
           .append("    COUNT(*) as frecuencia_fallas, ")
           .append("    u.ubi_nombre as ubicacion, ")
           .append("    AVG(CASE WHEN t.tick_fecha_cierre IS NOT NULL ")
           .append("        THEN TIMESTAMPDIFF(HOUR, t.tick_fecha_apertura, t.tick_fecha_cierre) ")
           .append("        ELSE NULL END) as tiempo_promedio_resolucion, ")
           .append("    COUNT(CASE WHEN t.tick_estado = 'Completado' AND ")
           .append("                   t.tick_fecha_cierre IS NOT NULL AND ")
           .append("                   NOT EXISTS(SELECT 1 FROM TICKET t2 ")
           .append("                             WHERE t2.act_id = t.act_id ")
           .append("                             AND t2.tick_tipo = 'Correctivo' ")
           .append("                             AND t2.tick_fecha_apertura > t.tick_fecha_cierre ")
           .append("                             AND t2.tick_fecha_apertura <= DATE_ADD(t.tick_fecha_cierre, INTERVAL 30 DAY)) ")
           .append("               THEN 1 END) as reparaciones_exitosas, ")
           .append("    MAX(t.tick_fecha_apertura) as ultima_falla ")
           .append("FROM TICKET t ")
           .append("INNER JOIN ACTIVO a ON t.act_id = a.act_id ")
           .append("INNER JOIN TIPO_ACTIVO ta ON a.tip_act_id = ta.tip_act_id ")
           .append("INNER JOIN UBICACION u ON a.act_ubicacion_actual = u.ubi_id ")
           .append("WHERE t.tick_tipo = 'Correctivo' ");
        
        List<Object> parametros = new ArrayList<>();
        
        if (fechaInicio != null && fechaFin != null) {
            sql.append("AND DATE(t.tick_fecha_apertura) BETWEEN ? AND ? ");
            parametros.add(java.sql.Date.valueOf(fechaInicio));
            parametros.add(java.sql.Date.valueOf(fechaFin));
        }
        
        if (tipoActivo != null && !tipoActivo.trim().isEmpty()) {
            sql.append("AND ta.nombre = ? ");
            parametros.add(tipoActivo);
        }
        
        if (ubicacion != null && !ubicacion.trim().isEmpty()) {
            sql.append("AND u.ubi_nombre LIKE ? ");
            parametros.add("%" + ubicacion + "%");
        }
        
        sql.append("GROUP BY ta.nombre, a.act_numero_activo, t.tick_descripcion, u.ubi_nombre ")
           .append("ORDER BY frecuencia_fallas DESC, ta.nombre, a.act_numero_activo");
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            
            for (int i = 0; i < parametros.size(); i++) {
                stmt.setObject(i + 1, parametros.get(i));
            }
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ReporteFallas reporte = new ReporteFallas();
                    reporte.setTipoActivo(rs.getString("tipo_activo"));
                    reporte.setNumeroActivo(rs.getString("act_numero_activo"));
                    reporte.setDescripcionFalla(rs.getString("descripcion_falla"));
                    reporte.setFrecuenciaFallas(rs.getInt("frecuencia_fallas"));
                    
                    // Calcular efectividad de reparación
                    int reparacionesExitosas = rs.getInt("reparaciones_exitosas");
                    int frecuenciaFallas = rs.getInt("frecuencia_fallas");
                    if (frecuenciaFallas > 0) {
                        double efectividad = (reparacionesExitosas * 100.0) / frecuenciaFallas;
                        reporte.setEfectividadReparacion(efectividad);
                    }
                    
                    Timestamp ultimaFalla = rs.getTimestamp("ultima_falla");
                    if (ultimaFalla != null) {
                        reporte.setFechaUltimaFalla(ultimaFalla.toLocalDateTime().toLocalDate());
                    }
                    
                    // Calcular índice de fallas (fallas por mes aproximado)
                    double tiempoPromedio = rs.getDouble("tiempo_promedio_resolucion");
                    if (tiempoPromedio > 0) {
                        reporte.setIndiceFallas(frecuenciaFallas / Math.max(1, tiempoPromedio / 168)); // 168 horas = 1 semana
                    }
                    
                    reporte.setTotalFallasRegistradas(frecuenciaFallas);
                    reportes.add(reporte);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error generando reporte de fallas", e);
        }
        
        return reportes;
    }
    
    /**
     * Genera el reporte completo de traslados
     */
    public List<ReporteTraslados> generarReporteTraslados(LocalDate fechaInicio, LocalDate fechaFin,
                                                         String ubicacionOrigen, String ubicacionDestino) {
        List<ReporteTraslados> reportes = new ArrayList<>();
        
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ")
           .append("    a.act_numero_activo, ")
           .append("    ta.nombre as tipo_activo, ")
           .append("    uo.ubi_nombre as ubicacion_origen, ")
           .append("    ud.ubi_nombre as ubicacion_destino, ")
           .append("    t.tras_fecha_devolucion_prog, ")
           .append("    t.tras_fecha_real, ")
           .append("    t.tras_estado, ")
           .append("    t.tras_motivo, ")
           .append("    u.usu_nombre as responsable, ")
           .append("    CASE WHEN t.tras_fecha_real IS NOT NULL ")
           .append("        THEN TIMESTAMPDIFF(DAY, t.tras_fecha_devolucion_prog, t.tras_fecha_retorno) ")
           .append("        ELSE TIMESTAMPDIFF(DAY, t.tras_fecha_devolucion_prog, CURRENT_DATE) ")
           .append("    END as dias_diferencia, ")
           .append("    COUNT(*) OVER (PARTITION BY a.act_id) as total_traslados_activo ")
           .append("FROM TRASLADO t ")
           .append("INNER JOIN ACTIVO a ON t.act_id = a.act_id ")
           .append("INNER JOIN TIPO_ACTIVO ta ON a.tip_act_id = ta.tip_act_id ")
           .append("INNER JOIN UBICACION uo ON t.tras_ubicacion_origen = uo.ubi_id ")
           .append("INNER JOIN UBICACION ud ON t.tras_ubicacion_destino = ud.ubi_id ")
           .append("LEFT JOIN USUARIO u ON t.tras_responsable = u.usu_id ")
           .append("WHERE 1=1 ");
        
        List<Object> parametros = new ArrayList<>();
        
        if (fechaInicio != null && fechaFin != null) {
            sql.append("AND DATE(t.tras_fecha_devolucion_prog) BETWEEN ? AND ? ");
            parametros.add(java.sql.Date.valueOf(fechaInicio));
            parametros.add(java.sql.Date.valueOf(fechaFin));
        }
        
        if (ubicacionOrigen != null && !ubicacionOrigen.trim().isEmpty()) {
            sql.append("AND uo.ubi_nombre LIKE ? ");
            parametros.add("%" + ubicacionOrigen + "%");
        }
        
        if (ubicacionDestino != null && !ubicacionDestino.trim().isEmpty()) {
            sql.append("AND ud.ubi_nombre LIKE ? ");
            parametros.add("%" + ubicacionDestino + "%");
        }
        
        sql.append("ORDER BY t.tras_fecha_devolucion_prog DESC, a.act_numero_activo");
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            
            for (int i = 0; i < parametros.size(); i++) {
                stmt.setObject(i + 1, parametros.get(i));
            }
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ReporteTraslados reporte = new ReporteTraslados();
                    reporte.setNumeroActivo(rs.getString("act_numero_activo"));
                    reporte.setTipoActivo(rs.getString("tipo_activo"));
                    reporte.setUbicacionOrigen(rs.getString("ubicacion_origen"));
                    reporte.setUbicacionDestino(rs.getString("ubicacion_destino"));
                    
                    java.sql.Date fechaProgramada = rs.getDate("tras_fecha_devolucion_prog");
                    if (fechaProgramada != null) {
                        reporte.setFechaSalida(fechaProgramada.toLocalDate());
                    }
                    
                    Timestamp fechaReal = rs.getTimestamp("tras_fecha_real");
                    if (fechaReal != null) {
                        reporte.setFechaRetorno(fechaReal.toLocalDateTime().toLocalDate());
                    }
                    
                    reporte.setEstadoTraslado(rs.getString("tras_estado"));
                    reporte.setMotivoTraslado(rs.getString("tras_motivo"));
                    
                    // Usar responsable para envío y recibo
                    String responsable = rs.getString("responsable");
                    reporte.setResponsableEnvio(responsable);
                    reporte.setResponsableRecibo(responsable);
                    
                    // Calcular días en ubicación
                    int diasDiferencia = rs.getInt("dias_diferencia");
                    reporte.setDiasEnUbicacion(Math.abs(diasDiferencia));
                    
                    reporte.setTotalTrasladosActivo(rs.getInt("total_traslados_activo"));
                    
                    reportes.add(reporte);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error generando reporte de traslados", e);
        }
        
        return reportes;
    }
    
    /**
     * Ejecuta consultas dinámicas personalizadas
     */
    public List<Map<String, Object>> ejecutarConsultaDinamica(String sql, Map<String, Object> parametros) {
        List<Map<String, Object>> resultados = new ArrayList<>();
        
        // Validaciones de seguridad
        if (!validarConsultaSegura(sql)) {
            LOGGER.log(Level.WARNING, "Consulta rechazada por motivos de seguridad: {0}", sql);
            return resultados;
        }
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            // Establecer parámetros
            if (parametros != null) {
                int index = 1;
                for (Object valor : parametros.values()) {
                    stmt.setObject(index++, valor);
                }
            }
            
            try (ResultSet rs = stmt.executeQuery()) {
                ResultSetMetaData metaData = rs.getMetaData();
                int columnCount = metaData.getColumnCount();
                
                while (rs.next()) {
                    Map<String, Object> fila = new LinkedHashMap<>();
                    for (int i = 1; i <= columnCount; i++) {
                        String nombreColumna = metaData.getColumnLabel(i);
                        Object valor = rs.getObject(i);
                        fila.put(nombreColumna, valor);
                    }
                    resultados.add(fila);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error ejecutando consulta dinámica", e);
        }
        
        return resultados;
    }
    
    /**
     * Valida que la consulta SQL sea segura
     */
    private boolean validarConsultaSegura(String sql) {
        if (sql == null || sql.trim().isEmpty()) {
            return false;
        }
        
        String sqlUpper = sql.toUpperCase().trim();
        
        // Solo permitir SELECT
        if (!sqlUpper.startsWith("SELECT")) {
            return false;
        }
        
        // Prohibir operaciones peligrosas
        String[] operacionesProhibidas = {
            "DELETE", "UPDATE", "INSERT", "DROP", "ALTER", "CREATE", 
            "TRUNCATE", "EXEC", "EXECUTE", "CALL", "GRANT", "REVOKE",
            "LOAD_FILE", "INTO OUTFILE", "INTO DUMPFILE", "--", "/*", "*/"
        };
        
        for (String operacion : operacionesProhibidas) {
            if (sqlUpper.contains(operacion)) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Obtiene las opciones disponibles para filtros
     */
    public Map<String, List<String>> obtenerOpcionesFiltros() {
        Map<String, List<String>> opciones = new HashMap<>();
        
        try (Connection conn = DatabaseConfig.getConnection()) {
            // Tipos de activo
            List<String> tiposActivo = new ArrayList<>();
            try (PreparedStatement stmt = conn.prepareStatement("SELECT DISTINCT nombre FROM TIPO_ACTIVO ORDER BY nombre");
                 ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    tiposActivo.add(rs.getString("nombre"));
                }
            }
            opciones.put("tiposActivo", tiposActivo);
            
            // Ubicaciones
            List<String> ubicaciones = new ArrayList<>();
            try (PreparedStatement stmt = conn.prepareStatement("SELECT DISTINCT ubi_nombre FROM UBICACION ORDER BY ubi_nombre");
                 ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ubicaciones.add(rs.getString("ubi_nombre"));
                }
            }
            opciones.put("ubicaciones", ubicaciones);
            
            // Estados de activos
            List<String> estados = Arrays.asList("Operativo", "En_Mantenimiento", "Fuera_Servicio", "Baja");
            opciones.put("estados", estados);
            
            // Tipos de mantenimiento
            List<String> tiposMantenimiento = Arrays.asList("Preventivo", "Correctivo");
            opciones.put("tiposMantenimiento", tiposMantenimiento);
            
            // Técnicos
            List<String> tecnicos = new ArrayList<>();
            try (PreparedStatement stmt = conn.prepareStatement(
                    "SELECT DISTINCT usu_nombre FROM USUARIO WHERE usu_rol = 'Tecnico' ORDER BY usu_nombre");
                 ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    tecnicos.add(rs.getString("usu_nombre"));
                }
            }
            opciones.put("tecnicos", tecnicos);
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error obteniendo opciones de filtros", e);
        }
        
        return opciones;
    }
    
    /**
     * Obtiene estadísticas resumidas para resúmenes ejecutivos
     */
    public Map<String, Object> obtenerEstadisticasResumen(LocalDate fechaInicio, LocalDate fechaFin) {
        Map<String, Object> estadisticas = new HashMap<>();
        
        try (Connection conn = DatabaseConfig.getConnection()) {
            // Resumen general
            String sqlResumen = "SELECT " +
                "(SELECT COUNT(*) FROM ACTIVO WHERE creado_en BETWEEN ? AND ?) as activos_registrados, " +
                "(SELECT COUNT(*) FROM TICKET WHERE tick_fecha_apertura BETWEEN ? AND ?) as tickets_creados, " +
                "(SELECT COUNT(*) FROM MANTENIMIENTO WHERE mant_fecha_inicio BETWEEN ? AND ?) as mantenimientos_realizados, " +
                "(SELECT COUNT(*) FROM TRASLADO WHERE tras_fecha_devolucion_prog BETWEEN ? AND ?) as traslados_programados, " +
                "(SELECT SUM(mant_costo) FROM MANTENIMIENTO WHERE mant_fecha_inicio BETWEEN ? AND ?) as costo_total_mantenimiento";
            
            try (PreparedStatement stmt = conn.prepareStatement(sqlResumen)) {
                java.sql.Date inicio = java.sql.Date.valueOf(fechaInicio);
                java.sql.Date fin = java.sql.Date.valueOf(fechaFin);
                
                for (int i = 1; i <= 10; i += 2) {
                    stmt.setDate(i, inicio);
                    stmt.setDate(i + 1, fin);
                }
                
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        estadisticas.put("activosRegistrados", rs.getInt("activos_registrados"));
                        estadisticas.put("ticketsCreados", rs.getInt("tickets_creados"));
                        estadisticas.put("mantenimientosRealizados", rs.getInt("mantenimientos_realizados"));
                        estadisticas.put("trasladosProgramados", rs.getInt("traslados_programados"));
                        estadisticas.put("costoTotalMantenimiento", rs.getBigDecimal("costo_total_mantenimiento"));
                    }
                }
            }
            
            // Eficiencia operativa
            String sqlEficiencia = "SELECT " +
                "(SELECT AVG(TIMESTAMPDIFF(HOUR, tick_fecha_apertura, tick_fecha_cierre)) " +
                " FROM TICKET WHERE tick_estado = 'Completado' AND tick_fecha_apertura BETWEEN ? AND ?) as tiempo_promedio_tickets, " +
                "(SELECT COUNT(*) * 100.0 / (SELECT COUNT(*) FROM TICKET WHERE tick_fecha_apertura BETWEEN ? AND ?) " +
                " FROM TICKET WHERE tick_estado = 'Completado' AND tick_fecha_apertura BETWEEN ? AND ?) as porcentaje_tickets_resueltos";
            
            try (PreparedStatement stmt = conn.prepareStatement(sqlEficiencia)) {
                java.sql.Date inicio = java.sql.Date.valueOf(fechaInicio);
                java.sql.Date fin = java.sql.Date.valueOf(fechaFin);
                
                for (int i = 1; i <= 6; i += 2) {
                    stmt.setDate(i, inicio);
                    stmt.setDate(i + 1, fin);
                }
                
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        estadisticas.put("tiempoPromedioTickets", rs.getDouble("tiempo_promedio_tickets"));
                        estadisticas.put("porcentajeTicketsResueltos", rs.getDouble("porcentaje_tickets_resueltos"));
                    }
                }
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error obteniendo estadísticas resumen", e);
        }
        
        return estadisticas;
    }
}
