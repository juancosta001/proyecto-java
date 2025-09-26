package com.ypacarai.cooperativa.activos.dao;

import com.ypacarai.cooperativa.activos.config.DatabaseConfig;
import com.ypacarai.cooperativa.activos.model.*;
import java.sql.*;
import java.time.LocalDate;
import java.util.*;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * DAO para generar todos los tipos de reportes del sistema
 * Implementación robusta y optimizada para producción
 */
public class ReportesDAO {
    private static final Logger LOGGER = Logger.getLogger(ReportesDAO.class.getName());
    
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
     * Genera el reporte completo de mantenimientos
     */
    public List<ReporteMantenimientos> generarReporteMantenimientos(LocalDate fechaInicio, LocalDate fechaFin, 
                                                                   String tipoMantenimiento, String tecnico) {
        List<ReporteMantenimientos> reportes = new ArrayList<>();
        
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ")
           .append("    m.mant_tipo as tipo_mantenimiento, ")
           .append("    ta.nombre as tipo_activo, ")
           .append("    COUNT(*) as total_mantenimientos, ")
           .append("    AVG(CASE WHEN m.mant_fecha_fin IS NOT NULL ")
           .append("        THEN TIMESTAMPDIFF(MINUTE, m.mant_fecha_inicio, m.mant_fecha_fin) / 60.0 ")
           .append("        ELSE NULL END) as tiempo_promedio_horas, ")
           .append("    u.usu_nombre as tecnico_asignado, ")
           .append("    m.mant_estado as estado_mantenimiento ")
           .append("FROM MANTENIMIENTO m ")
           .append("INNER JOIN ACTIVO a ON m.act_id = a.act_id ")
           .append("INNER JOIN TIPO_ACTIVO ta ON a.tip_act_id = ta.tip_act_id ")
           .append("INNER JOIN USUARIO u ON m.mant_tecnico_asignado = u.usu_id ")
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
        
        if (tecnico != null && !tecnico.trim().isEmpty()) {
            sql.append("AND u.usu_nombre LIKE ? ");
            parametros.add("%" + tecnico + "%");
        }
        
        sql.append("GROUP BY m.mant_tipo, ta.nombre, u.usu_nombre, m.mant_estado ")
           .append("ORDER BY total_mantenimientos DESC, tiempo_promedio_horas ASC");
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            
            // Establecer parámetros
            for (int i = 0; i < parametros.size(); i++) {
                stmt.setObject(i + 1, parametros.get(i));
            }
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ReporteMantenimientos reporte = new ReporteMantenimientos();
                    reporte.setTipoMantenimiento(rs.getString("tipo_mantenimiento"));
                    reporte.setTipoActivo(rs.getString("tipo_activo"));
                    reporte.setTotalMantenimientos(rs.getInt("total_mantenimientos"));
                    reporte.setTiempoPromedioResolucion(rs.getDouble("tiempo_promedio_horas"));
                    reporte.setTecnicoAsignado(rs.getString("tecnico_asignado"));
                    reporte.setEstadoMantenimiento(rs.getString("estado_mantenimiento"));
                    reporte.setProductividadTecnico(rs.getInt("total_mantenimientos"));
                    
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
                                                   String tipoActivo, String numeroActivo) {
        List<ReporteFallas> reportes = new ArrayList<>();
        
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ")
           .append("    ta.nombre as tipo_activo, ")
           .append("    a.act_numero_activo as numero_activo, ")
           .append("    COUNT(DISTINCT t.tick_id) as total_fallas, ")
           .append("    COUNT(DISTINCT t.tick_id) / ")
           .append("        (TIMESTAMPDIFF(MONTH, MIN(t.tick_fecha_apertura), MAX(t.tick_fecha_apertura)) + 1) as indice_fallas_mes, ")
           .append("    MAX(t.tick_fecha_apertura) as fecha_ultima_falla, ")
           .append("    AVG(CASE WHEN t.tick_estado = 'Resuelto' THEN 100.0 ELSE 0.0 END) as efectividad_reparacion, ")
           .append("    GROUP_CONCAT(DISTINCT t.tick_descripcion SEPARATOR '; ') as descripcion_fallas ")
           .append("FROM TICKET t ")
           .append("INNER JOIN ACTIVO a ON t.act_id = a.act_id ")
           .append("INNER JOIN TIPO_ACTIVO ta ON a.tip_act_id = ta.tip_act_id ")
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
        
        if (numeroActivo != null && !numeroActivo.trim().isEmpty()) {
            sql.append("AND a.act_numero_activo LIKE ? ");
            parametros.add("%" + numeroActivo + "%");
        }
        
        sql.append("GROUP BY ta.nombre, a.act_numero_activo ")
           .append("HAVING total_fallas > 0 ")
           .append("ORDER BY total_fallas DESC, indice_fallas_mes DESC");
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            
            // Establecer parámetros
            for (int i = 0; i < parametros.size(); i++) {
                stmt.setObject(i + 1, parametros.get(i));
            }
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ReporteFallas reporte = new ReporteFallas();
                    reporte.setTipoActivo(rs.getString("tipo_activo"));
                    reporte.setNumeroActivo(rs.getString("numero_activo"));
                    reporte.setTotalFallasRegistradas(rs.getInt("total_fallas"));
                    reporte.setFrecuenciaFallas(rs.getInt("total_fallas"));
                    reporte.setIndiceFallas(rs.getDouble("indice_fallas_mes"));
                    reporte.setFechaUltimaFalla(rs.getDate("fecha_ultima_falla").toLocalDate());
                    reporte.setEfectividadReparacion(rs.getDouble("efectividad_reparacion"));
                    reporte.setDescripcionFalla(rs.getString("descripcion_fallas"));
                    
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
                                                         String numeroActivo, String ubicacion) {
        List<ReporteTraslados> reportes = new ArrayList<>();
        
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ")
           .append("    a.act_numero_activo as numero_activo, ")
           .append("    ta.nombre as tipo_activo, ")
           .append("    tr.tras_numero as numero_traslado, ")
           .append("    tr.tras_fecha_salida as fecha_salida, ")
           .append("    tr.tras_fecha_retorno as fecha_retorno, ")
           .append("    uo.ubi_nombre as ubicacion_origen, ")
           .append("    ud.ubi_nombre as ubicacion_destino, ")
           .append("    tr.tras_estado as estado_traslado, ")
           .append("    tr.tras_motivo as motivo_traslado, ")
           .append("    tr.tras_responsable_envio as responsable_envio, ")
           .append("    tr.tras_responsable_recibo as responsable_recibo, ")
           .append("    CASE WHEN tr.tras_fecha_retorno IS NOT NULL ")
           .append("        THEN DATEDIFF(tr.tras_fecha_retorno, tr.tras_fecha_salida) ")
           .append("        ELSE DATEDIFF(CURRENT_DATE, tr.tras_fecha_salida) END as dias_ubicacion, ")
           .append("    COUNT(*) OVER (PARTITION BY a.act_id) as total_traslados_activo ")
           .append("FROM TRASLADO tr ")
           .append("INNER JOIN ACTIVO a ON tr.act_id = a.act_id ")
           .append("INNER JOIN TIPO_ACTIVO ta ON a.tip_act_id = ta.tip_act_id ")
           .append("INNER JOIN UBICACION uo ON tr.tras_ubicacion_origen = uo.ubi_id ")
           .append("INNER JOIN UBICACION ud ON tr.tras_ubicacion_destino = ud.ubi_id ")
           .append("WHERE 1=1 ");
        
        List<Object> parametros = new ArrayList<>();
        
        if (fechaInicio != null && fechaFin != null) {
            sql.append("AND DATE(tr.tras_fecha_salida) BETWEEN ? AND ? ");
            parametros.add(java.sql.Date.valueOf(fechaInicio));
            parametros.add(java.sql.Date.valueOf(fechaFin));
        }
        
        if (numeroActivo != null && !numeroActivo.trim().isEmpty()) {
            sql.append("AND a.act_numero_activo LIKE ? ");
            parametros.add("%" + numeroActivo + "%");
        }
        
        if (ubicacion != null && !ubicacion.trim().isEmpty()) {
            sql.append("AND (uo.ubi_nombre LIKE ? OR ud.ubi_nombre LIKE ?) ");
            parametros.add("%" + ubicacion + "%");
            parametros.add("%" + ubicacion + "%");
        }
        
        sql.append("ORDER BY tr.tras_fecha_salida DESC, total_traslados_activo DESC");
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            
            // Establecer parámetros
            for (int i = 0; i < parametros.size(); i++) {
                stmt.setObject(i + 1, parametros.get(i));
            }
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ReporteTraslados reporte = new ReporteTraslados();
                    reporte.setNumeroActivo(rs.getString("numero_activo"));
                    reporte.setTipoActivo(rs.getString("tipo_activo"));
                    reporte.setNumeroTraslado(rs.getString("numero_traslado"));
                    reporte.setFechaSalida(rs.getDate("fecha_salida").toLocalDate());
                    
                    java.sql.Date fechaRetorno = rs.getDate("fecha_retorno");
                    if (fechaRetorno != null) {
                        reporte.setFechaRetorno(fechaRetorno.toLocalDate());
                    }
                    
                    reporte.setUbicacionOrigen(rs.getString("ubicacion_origen"));
                    reporte.setUbicacionDestino(rs.getString("ubicacion_destino"));
                    reporte.setEstadoTraslado(rs.getString("estado_traslado"));
                    reporte.setMotivoTraslado(rs.getString("motivo_traslado"));
                    reporte.setResponsableEnvio(rs.getString("responsable_envio"));
                    reporte.setResponsableRecibo(rs.getString("responsable_recibo"));
                    reporte.setDiasEnUbicacion(rs.getInt("dias_ubicacion"));
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
        
        // Fallas por mes
        String sqlFallas = "SELECT " +
            "(SELECT COUNT(*) FROM TICKET WHERE tick_tipo = 'Correctivo' " +
             "AND MONTH(tick_fecha_apertura) = MONTH(CURRENT_DATE) " +
             "AND YEAR(tick_fecha_apertura) = YEAR(CURRENT_DATE)) as fallas_actual, " +
            "(SELECT COUNT(*) FROM TICKET WHERE tick_tipo = 'Correctivo' " +
             "AND MONTH(tick_fecha_apertura) = MONTH(DATE_SUB(CURRENT_DATE, INTERVAL 1 MONTH)) " +
             "AND YEAR(tick_fecha_apertura) = YEAR(DATE_SUB(CURRENT_DATE, INTERVAL 1 MONTH))) as fallas_anterior";
        
        try (PreparedStatement stmt = conn.prepareStatement(sqlFallas);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                dashboard.setFallasMesActual(rs.getInt("fallas_actual"));
                dashboard.setFallasMesAnterior(rs.getInt("fallas_anterior"));
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
}
