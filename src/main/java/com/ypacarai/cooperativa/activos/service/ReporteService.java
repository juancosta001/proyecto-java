package com.ypacarai.cooperativa.activos.service;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.ypacarai.cooperativa.activos.dao.ReportesDAOSimple;
import com.ypacarai.cooperativa.activos.model.ConsultaDinamica;
import com.ypacarai.cooperativa.activos.model.DashboardData;
import com.ypacarai.cooperativa.activos.model.FiltrosReporte;
import com.ypacarai.cooperativa.activos.model.ReporteCompleto;
import com.ypacarai.cooperativa.activos.model.ReporteEstadoActivos;
import com.ypacarai.cooperativa.activos.model.ReporteFallas;
import com.ypacarai.cooperativa.activos.model.ReporteMantenimientos;
import com.ypacarai.cooperativa.activos.model.ReporteTraslados;
import com.ypacarai.cooperativa.activos.model.ResultadoConsultaDinamica;

/**
 * Servicio completo para generación y gestión de reportes
 * Implementación robusta para producción con múltiples formatos de exportación
 */
public class ReporteService {
    private static final Logger LOGGER = Logger.getLogger(ReporteService.class.getName());
    private final ReportesDAOSimple reportesDAO;
    private final DateTimeFormatter formatoFecha = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final NumberFormat formatoMoneda = NumberFormat.getCurrencyInstance();
    
    public ReporteService() {
        this.reportesDAO = new ReportesDAOSimple();
    }
    
    /**
     * REPORTES OPERATIVOS
     */
    
    /**
     * Genera reporte completo de estado de activos con análisis detallado
     */
    public ReporteCompleto generarReporteEstadoActivos(FiltrosReporte filtros) {
        try {
            LOGGER.info("Generando reporte de estado de activos");
            
            List<ReporteEstadoActivos> datos = reportesDAO.generarReporteEstadoActivos(
                filtros.getFechaInicio(), 
                filtros.getFechaFin(),
                filtros.getTipoActivo(),
                filtros.getUbicacion()
            );
            
            ReporteCompleto reporte = new ReporteCompleto();
            reporte.setTipoReporte("Estado de Activos");
            reporte.setFechaGeneracion(LocalDate.now());
            reporte.setDatosOriginales(datos);
            
            // Análisis estadístico
            Map<String, Object> estadisticas = calcularEstadisticasEstadoActivos(datos);
            reporte.setEstadisticas(estadisticas);
            
            // Generar resumen ejecutivo
            String resumen = generarResumenEstadoActivos(datos, estadisticas);
            reporte.setResumenEjecutivo(resumen);
            
            LOGGER.info("Reporte de estado de activos generado exitosamente");
            return reporte;
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error generando reporte de estado de activos", e);
            throw new RuntimeException("Error generando reporte de estado de activos", e);
        }
    }
    
    /**
     * Genera reporte completo de mantenimientos con análisis de productividad
     */
    public ReporteCompleto generarReporteMantenimientos(FiltrosReporte filtros) {
        try {
            LOGGER.info("Generando reporte de mantenimientos");
            
            List<ReporteMantenimientos> datos = reportesDAO.generarReporteMantenimientos(
                filtros.getFechaInicio(),
                filtros.getFechaFin(),
                filtros.getTipoMantenimiento(),
                filtros.getTecnicoId()
            );
            
            ReporteCompleto reporte = new ReporteCompleto();
            reporte.setTipoReporte("Mantenimientos");
            reporte.setFechaGeneracion(LocalDate.now());
            reporte.setDatosOriginales(datos);
            
            // Análisis estadístico
            Map<String, Object> estadisticas = calcularEstadisticasMantenimientos(datos);
            reporte.setEstadisticas(estadisticas);
            
            // Generar resumen ejecutivo
            String resumen = generarResumenMantenimientos(datos, estadisticas);
            reporte.setResumenEjecutivo(resumen);
            
            LOGGER.info("Reporte de mantenimientos generado exitosamente");
            return reporte;
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error generando reporte de mantenimientos", e);
            throw new RuntimeException("Error generando reporte de mantenimientos", e);
        }
    }
    
    /**
     * Genera reporte completo de fallas con análisis de tendencias
     */
    public ReporteCompleto generarReporteFallas(FiltrosReporte filtros) {
        try {
            LOGGER.info("Generando reporte de fallas");
            
            List<ReporteFallas> datos = reportesDAO.generarReporteFallas(
                filtros.getFechaInicio(),
                filtros.getFechaFin(),
                filtros.getTipoActivo(),
                filtros.getUbicacion()
            );
            
            ReporteCompleto reporte = new ReporteCompleto();
            reporte.setTipoReporte("Fallas");
            reporte.setFechaGeneracion(LocalDate.now());
            reporte.setDatosOriginales(datos);
            
            // Análisis estadístico
            Map<String, Object> estadisticas = calcularEstadisticasFallas(datos);
            reporte.setEstadisticas(estadisticas);
            
            // Generar resumen ejecutivo
            String resumen = generarResumenFallas(datos, estadisticas);
            reporte.setResumenEjecutivo(resumen);
            
            LOGGER.info("Reporte de fallas generado exitosamente");
            return reporte;
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error generando reporte de fallas", e);
            throw new RuntimeException("Error generando reporte de fallas", e);
        }
    }
    
    /**
     * Genera reporte completo de traslados con análisis de movimientos
     */
    public ReporteCompleto generarReporteTraslados(FiltrosReporte filtros) {
        try {
            LOGGER.info("Generando reporte de traslados");
            
            List<ReporteTraslados> datos = reportesDAO.generarReporteTraslados(
                filtros.getFechaInicio(),
                filtros.getFechaFin(),
                filtros.getUbicacionOrigen(),
                filtros.getUbicacionDestino()
            );
            
            ReporteCompleto reporte = new ReporteCompleto();
            reporte.setTipoReporte("Traslados");
            reporte.setFechaGeneracion(LocalDate.now());
            reporte.setDatosOriginales(datos);
            
            // Análisis estadístico
            Map<String, Object> estadisticas = calcularEstadisticasTraslados(datos);
            reporte.setEstadisticas(estadisticas);
            
            // Generar resumen ejecutivo
            String resumen = generarResumenTraslados(datos, estadisticas);
            reporte.setResumenEjecutivo(resumen);
            
            LOGGER.info("Reporte de traslados generado exitosamente");
            return reporte;
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error generando reporte de traslados", e);
            throw new RuntimeException("Error generando reporte de traslados", e);
        }
    }
    
    /**
     * DASHBOARD EJECUTIVO
     */
    
    /**
     * Obtiene todos los datos para el dashboard ejecutivo
     */
    public DashboardData obtenerDatosDashboard() {
        try {
            LOGGER.info("Obteniendo datos del dashboard ejecutivo");
            
            DashboardData dashboard = reportesDAO.obtenerDatosDashboard();
            
            // Enriquecer con datos calculados
            enriquecerDashboard(dashboard);
            
            LOGGER.info("Datos del dashboard obtenidos exitosamente");
            return dashboard;
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error obteniendo datos del dashboard", e);
            throw new RuntimeException("Error obteniendo datos del dashboard", e);
        }
    }
    
    /**
     * CONSULTAS DINÁMICAS
     */
    
    /**
     * Ejecuta consultas dinámicas con validación y formato
     */
    public ResultadoConsultaDinamica ejecutarConsultaDinamica(ConsultaDinamica consulta) {
        try {
            LOGGER.log(Level.INFO, "Ejecutando consulta dinámica: {0}", consulta.getNombre());
            
            // Construir SQL basado en la configuración
            String sql = construirSQLDinamico(consulta);
            Map<String, Object> parametros = construirParametros(consulta);
            
            // Ejecutar consulta
            List<Map<String, Object>> datos = reportesDAO.ejecutarConsultaDinamica(sql, parametros);
            
            // Crear resultado
            ResultadoConsultaDinamica resultado = new ResultadoConsultaDinamica();
            resultado.setNombreConsulta(consulta.getNombre());
            resultado.setFechaEjecucion(LocalDate.now());
            resultado.setTotalRegistros(datos.size());
            resultado.setDatos(datos);
            
            // Aplicar filtros adicionales si es necesario
            if (consulta.getFiltrosPostProceso() != null && !consulta.getFiltrosPostProceso().isEmpty()) {
                datos = aplicarFiltrosPostProceso(datos, consulta.getFiltrosPostProceso());
                resultado.setDatos(datos);
                resultado.setTotalRegistros(datos.size());
            }
            
            // Aplicar ordenamiento
            if (consulta.getOrdenamiento() != null && !consulta.getOrdenamiento().isEmpty()) {
                datos = aplicarOrdenamiento(datos, consulta.getOrdenamiento());
                resultado.setDatos(datos);
            }
            
            LOGGER.info("Consulta dinámica ejecutada exitosamente");
            return resultado;
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error ejecutando consulta dinámica", e);
            throw new RuntimeException("Error ejecutando consulta dinámica", e);
        }
    }
    
    /**
     * MÉTODOS DE ANÁLISIS ESTADÍSTICO
     */
    
    private Map<String, Object> calcularEstadisticasEstadoActivos(List<ReporteEstadoActivos> datos) {
        Map<String, Object> stats = new HashMap<>();
        
        if (datos.isEmpty()) {
            stats.put("totalActivos", 0);
            stats.put("proximosMantenimiento", 0);
            stats.put("mantenimientoVencido", 0);
            stats.put("porcentajeProximoMantenimiento", 0.0);
            stats.put("porcentajeMantenimientoVencido", 0.0);
            stats.put("distribucionPorTipo", new HashMap<String, Integer>());
            stats.put("distribucionPorUbicacion", new HashMap<String, Integer>());
            return stats;
        }
        
        int totalActivos = datos.stream().mapToInt(ReporteEstadoActivos::getCantidadTotal).sum();
        int proximosMantenimiento = datos.stream().mapToInt(ReporteEstadoActivos::getActivosProximosMantenimiento).sum();
        int mantenimientoVencido = datos.stream().mapToInt(ReporteEstadoActivos::getActivosMantenimientoVencido).sum();
        
        stats.put("totalActivos", totalActivos);
        stats.put("proximosMantenimiento", proximosMantenimiento);
        stats.put("mantenimientoVencido", mantenimientoVencido);
        stats.put("porcentajeProximoMantenimiento", totalActivos > 0 ? (proximosMantenimiento * 100.0 / totalActivos) : 0);
        stats.put("porcentajeMantenimientoVencido", totalActivos > 0 ? (mantenimientoVencido * 100.0 / totalActivos) : 0);
        
        // Análisis por tipo de activo
        Map<String, Integer> activosPorTipo = datos.stream()
            .collect(Collectors.groupingBy(
                ReporteEstadoActivos::getTipoActivo,
                Collectors.summingInt(ReporteEstadoActivos::getCantidadTotal)
            ));
        stats.put("distribucionPorTipo", activosPorTipo);
        
        // Análisis por ubicación
        Map<String, Integer> activosPorUbicacion = datos.stream()
            .collect(Collectors.groupingBy(
                ReporteEstadoActivos::getUbicacion,
                Collectors.summingInt(ReporteEstadoActivos::getCantidadTotal)
            ));
        stats.put("distribucionPorUbicacion", activosPorUbicacion);
        
        return stats;
    }
    
    private Map<String, Object> calcularEstadisticasMantenimientos(List<ReporteMantenimientos> datos) {
        Map<String, Object> stats = new HashMap<>();
        
        if (datos.isEmpty()) {
            // Valores por defecto cuando no hay datos
            stats.put("totalMantenimientos", 0);
            stats.put("tiempoPromedioResolucion", 0.0);
            stats.put("costoTotal", 0.0);
            stats.put("costoPromedioPorMantenimiento", 0.0);
            stats.put("distribucionPorTipo", new HashMap<String, Integer>());
            stats.put("rankingTecnicos", new HashMap<String, Integer>());
            return stats;
        }
        
        int totalMantenimientos = datos.stream().mapToInt(ReporteMantenimientos::getTotalMantenimientos).sum();
        double tiempoPromedio = datos.stream().mapToDouble(ReporteMantenimientos::getTiempoPromedioResolucion).average().orElse(0);
        double costoTotal = datos.stream().mapToDouble(ReporteMantenimientos::getCostoTotal).sum();
        
        stats.put("totalMantenimientos", totalMantenimientos);
        stats.put("tiempoPromedioResolucion", tiempoPromedio);
        stats.put("costoTotal", costoTotal);
        stats.put("costoPromedioPorMantenimiento", totalMantenimientos > 0 ? costoTotal / totalMantenimientos : 0.0);
        
        // Análisis por tipo de mantenimiento
        Map<String, Integer> mantenimientosPorTipo = datos.stream()
            .collect(Collectors.groupingBy(
                ReporteMantenimientos::getTipoMantenimiento,
                Collectors.summingInt(ReporteMantenimientos::getTotalMantenimientos)
            ));
        stats.put("distribucionPorTipo", mantenimientosPorTipo);
        
        // Ranking de técnicos más productivos
        Map<String, Integer> productividadTecnicos = datos.stream()
            .filter(r -> r.getTecnicoAsignado() != null)
            .collect(Collectors.groupingBy(
                ReporteMantenimientos::getTecnicoAsignado,
                Collectors.summingInt(ReporteMantenimientos::getProductividadTecnico)
            ));
        stats.put("rankingTecnicos", productividadTecnicos);
        
        return stats;
    }
    
    private Map<String, Object> calcularEstadisticasFallas(List<ReporteFallas> datos) {
        Map<String, Object> stats = new HashMap<>();
        
        if (datos.isEmpty()) {
            stats.put("totalFallas", 0);
            stats.put("efectividadPromedioReparacion", 0.0);
            stats.put("fallasPorTipo", new HashMap<String, Integer>());
            stats.put("tiposActivosConMasFallas", new HashMap<String, Integer>());
            return stats;
        }
        
        int totalFallas = datos.stream().mapToInt(ReporteFallas::getFrecuenciaFallas).sum();
        double efectividadPromedio = datos.stream().mapToDouble(ReporteFallas::getEfectividadReparacion).average().orElse(0);
        
        stats.put("totalFallas", totalFallas);
        stats.put("efectividadPromedioReparacion", efectividadPromedio);
        
        // Fallas más frecuentes por tipo de activo
        Map<String, Integer> fallasPorTipo = datos.stream()
            .collect(Collectors.groupingBy(
                ReporteFallas::getTipoActivo,
                Collectors.summingInt(ReporteFallas::getFrecuenciaFallas)
            ));
        stats.put("fallasPorTipoActivo", fallasPorTipo);
        
        // Activos más problemáticos
        List<Map.Entry<String, Integer>> activosProblematicos = datos.stream()
            .collect(Collectors.groupingBy(
                ReporteFallas::getNumeroActivo,
                Collectors.summingInt(ReporteFallas::getFrecuenciaFallas)
            ))
            .entrySet().stream()
            .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
            .limit(10)
            .collect(Collectors.toList());
        
        Map<String, Integer> top10Problematicos = new LinkedHashMap<>();
        activosProblematicos.forEach(entry -> top10Problematicos.put(entry.getKey(), entry.getValue()));
        stats.put("activosMasProblematicos", top10Problematicos);
        
        return stats;
    }
    
    private Map<String, Object> calcularEstadisticasTraslados(List<ReporteTraslados> datos) {
        Map<String, Object> stats = new HashMap<>();
        
        if (datos.isEmpty()) {
            stats.put("totalTraslados", 0);
            stats.put("diasPromedioEnUbicacion", 0.0);
            stats.put("distribucionPorEstado", new HashMap<String, Long>());
            stats.put("ubicacionesOrigen", new HashMap<String, Long>());
            return stats;
        }
        
        int totalTraslados = datos.size();
        double diasPromedioUbicacion = datos.stream().mapToInt(ReporteTraslados::getDiasEnUbicacion).average().orElse(0);
        
        stats.put("totalTraslados", totalTraslados);
        stats.put("diasPromedioEnUbicacion", diasPromedioUbicacion);
        
        // Distribución por estado
        Map<String, Long> trasladosPorEstado = datos.stream()
            .collect(Collectors.groupingBy(
                ReporteTraslados::getEstadoTraslado,
                Collectors.counting()
            ));
        stats.put("distribucionPorEstado", trasladosPorEstado);
        
        // Ubicaciones más activas (origen)
        Map<String, Long> ubicacionesOrigen = datos.stream()
            .collect(Collectors.groupingBy(
                ReporteTraslados::getUbicacionOrigen,
                Collectors.counting()
            ));
        stats.put("ubicacionesOrigenMasActivas", ubicacionesOrigen);
        
        // Ubicaciones más activas (destino)
        Map<String, Long> ubicacionesDestino = datos.stream()
            .collect(Collectors.groupingBy(
                ReporteTraslados::getUbicacionDestino,
                Collectors.counting()
            ));
        stats.put("ubicacionesDestinoMasActivas", ubicacionesDestino);
        
        // Activos más trasladados
        Map<String, Integer> activosMasTrasladados = datos.stream()
            .collect(Collectors.groupingBy(
                ReporteTraslados::getNumeroActivo,
                Collectors.summingInt(ReporteTraslados::getTotalTrasladosActivo)
            ));
        stats.put("activosMasTrasladados", activosMasTrasladados);
        
        return stats;
    }
    
    /**
     * MÉTODOS DE GENERACIÓN DE RESÚMENES EJECUTIVOS
     */
    
    private String generarResumenEstadoActivos(List<ReporteEstadoActivos> datos, Map<String, Object> stats) {
        StringBuilder resumen = new StringBuilder();
        resumen.append("RESUMEN EJECUTIVO - ESTADO DE ACTIVOS\n");
        resumen.append("=====================================\n\n");
        
        resumen.append("Total de Activos: ").append(stats.get("totalActivos")).append("\n");
        resumen.append("Próximos a Mantenimiento: ").append(stats.get("proximosMantenimiento")).append(" (")
               .append(String.format("%.1f", (Double)stats.get("porcentajeProximoMantenimiento"))).append("%)\n");
        resumen.append("Mantenimiento Vencido: ").append(stats.get("mantenimientoVencido")).append(" (")
               .append(String.format("%.1f", (Double)stats.get("porcentajeMantenimientoVencido"))).append("%)\n\n");
        
        resumen.append("DISTRIBUCIÓN POR TIPO DE ACTIVO:\n");
        @SuppressWarnings("unchecked")
        Map<String, Integer> distribucion = (Map<String, Integer>) stats.get("distribucionPorTipo");
        if (distribucion != null && !distribucion.isEmpty()) {
            distribucion.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .forEach(entry -> resumen.append("- ").append(entry.getKey()).append(": ").append(entry.getValue()).append("\n"));
        } else {
            resumen.append("Sin datos disponibles.\n");
        }
        
        return resumen.toString();
    }
    
    private String generarResumenMantenimientos(List<ReporteMantenimientos> datos, Map<String, Object> stats) {
        StringBuilder resumen = new StringBuilder();
        resumen.append("RESUMEN EJECUTIVO - MANTENIMIENTOS\n");
        resumen.append("===================================\n\n");
        
        resumen.append("Total de Mantenimientos: ").append(stats.get("totalMantenimientos")).append("\n");
        resumen.append("Tiempo Promedio de Resolución: ")
               .append(String.format("%.1f", (Double)stats.get("tiempoPromedioResolucion"))).append(" horas\n");
        
        // Convertir explícitamente a Double antes de formatear
        Double costoTotal = (Double) stats.get("costoTotal");
        Double costoPromedio = (Double) stats.get("costoPromedioPorMantenimiento");
        
        resumen.append("Costo Total: ").append(formatoMoneda.format(costoTotal != null ? costoTotal : 0.0)).append("\n");
        resumen.append("Costo Promedio por Mantenimiento: ")
               .append(formatoMoneda.format(costoPromedio != null ? costoPromedio : 0.0)).append("\n\n");
        
        resumen.append("RANKING DE TÉCNICOS MÁS PRODUCTIVOS:\n");
        @SuppressWarnings("unchecked")
        Map<String, Integer> ranking = (Map<String, Integer>) stats.get("rankingTecnicos");
        if (ranking != null && !ranking.isEmpty()) {
            ranking.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(5)
                .forEach(entry -> resumen.append("- ").append(entry.getKey()).append(": ").append(entry.getValue()).append(" mantenimientos\n"));
        } else {
            resumen.append("Sin datos disponibles.\n");
        }
        
        return resumen.toString();
    }
    
    private String generarResumenFallas(List<ReporteFallas> datos, Map<String, Object> stats) {
        StringBuilder resumen = new StringBuilder();
        resumen.append("RESUMEN EJECUTIVO - FALLAS\n");
        resumen.append("===========================\n\n");
        
        resumen.append("Total de Fallas Registradas: ").append(stats.get("totalFallas")).append("\n");
        
        // Manejar posible valor null para efectividad
        Double efectividad = (Double) stats.get("efectividadPromedioReparacion");
        resumen.append("Efectividad Promedio de Reparación: ")
               .append(String.format("%.1f", efectividad != null ? efectividad : 0.0)).append("%\n\n");
        
        resumen.append("ACTIVOS MÁS PROBLEMÁTICOS:\n");
        @SuppressWarnings("unchecked")
        Map<String, Integer> problematicos = (Map<String, Integer>) stats.get("activosMasProblematicos");
        if (problematicos != null && !problematicos.isEmpty()) {
            problematicos.entrySet().stream()
                .limit(5)
                .forEach(entry -> resumen.append("- Activo ").append(entry.getKey()).append(": ").append(entry.getValue()).append(" fallas\n"));
        } else {
            resumen.append("Sin datos disponibles.\n");
        }
        
        return resumen.toString();
    }
    
    private String generarResumenTraslados(List<ReporteTraslados> datos, Map<String, Object> stats) {
        StringBuilder resumen = new StringBuilder();
        resumen.append("RESUMEN EJECUTIVO - TRASLADOS\n");
        resumen.append("==============================\n\n");
        
        resumen.append("Total de Traslados: ").append(stats.get("totalTraslados")).append("\n");
        
        // Manejar posible valor null
        Double diasPromedio = (Double) stats.get("diasPromedioEnUbicacion");
        resumen.append("Días Promedio en Ubicación: ")
               .append(String.format("%.1f", diasPromedio != null ? diasPromedio : 0.0)).append(" días\n\n");
        
        resumen.append("DISTRIBUCIÓN POR ESTADO:\n");
        @SuppressWarnings("unchecked")
        Map<String, Long> estados = (Map<String, Long>) stats.get("distribucionPorEstado");
        if (estados != null && !estados.isEmpty()) {
            estados.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .forEach(entry -> resumen.append("- ").append(entry.getKey()).append(": ").append(entry.getValue()).append("\n"));
        } else {
            resumen.append("Sin datos disponibles.\n");
        }
        
        return resumen.toString();
    }
    
    /**
     * MÉTODOS DE SOPORTE PARA CONSULTAS DINÁMICAS
     */
    
    private String construirSQLDinamico(ConsultaDinamica consulta) {
        StringBuilder sql = new StringBuilder();
        
        // SELECT
        sql.append("SELECT ");
        if (consulta.getCamposSeleccionados() != null && !consulta.getCamposSeleccionados().isEmpty()) {
            sql.append(String.join(", ", consulta.getCamposSeleccionados()));
        } else {
            sql.append("*");
        }
        
        // FROM
        sql.append(" FROM ").append(consulta.getTablaBase());
        
        // JOINs
        if (consulta.getJoins() != null) {
            consulta.getJoins().forEach(join -> sql.append(" ").append(join));
        }
        
        // WHERE
        if (consulta.getFiltros() != null && !consulta.getFiltros().isEmpty()) {
            sql.append(" WHERE ");
            List<String> condiciones = new ArrayList<>();
            consulta.getFiltros().forEach((campo, valor) -> {
                if (valor instanceof String) {
                    condiciones.add(campo + " LIKE ?");
                } else {
                    condiciones.add(campo + " = ?");
                }
            });
            sql.append(String.join(" AND ", condiciones));
        }
        
        // GROUP BY
        if (consulta.getCamposAgrupacion() != null && !consulta.getCamposAgrupacion().isEmpty()) {
            sql.append(" GROUP BY ").append(String.join(", ", consulta.getCamposAgrupacion()));
        }
        
        // ORDER BY
        if (consulta.getOrdenamiento() != null && !consulta.getOrdenamiento().isEmpty()) {
            sql.append(" ORDER BY ");
            List<String> ordenamientos = new ArrayList<>();
            consulta.getOrdenamiento().forEach((campo, direccion) -> {
                ordenamientos.add(campo + " " + direccion);
            });
            sql.append(String.join(", ", ordenamientos));
        }
        
        // LIMIT
        if (consulta.getLimite() > 0) {
            sql.append(" LIMIT ").append(consulta.getLimite());
        }
        
        return sql.toString();
    }
    
    private Map<String, Object> construirParametros(ConsultaDinamica consulta) {
        Map<String, Object> parametros = new HashMap<>();
        
        if (consulta.getFiltros() != null) {
            consulta.getFiltros().forEach((campo, valor) -> {
                if (valor instanceof String) {
                    parametros.put(campo, "%" + valor + "%");
                } else {
                    parametros.put(campo, valor);
                }
            });
        }
        
        return parametros;
    }
    
    private List<Map<String, Object>> aplicarFiltrosPostProceso(List<Map<String, Object>> datos, 
                                                               Map<String, Object> filtros) {
        return datos.stream()
            .filter(fila -> {
                return filtros.entrySet().stream().allMatch(filtro -> {
                    Object valorFila = fila.get(filtro.getKey());
                    Object valorFiltro = filtro.getValue();
                    
                    if (valorFila == null) return false;
                    if (valorFiltro instanceof String) {
                        return valorFila.toString().toLowerCase().contains(valorFiltro.toString().toLowerCase());
                    }
                    return valorFila.equals(valorFiltro);
                });
            })
            .collect(Collectors.toList());
    }
    
    private List<Map<String, Object>> aplicarOrdenamiento(List<Map<String, Object>> datos,
                                                         Map<String, String> ordenamiento) {
        return datos.stream()
            .sorted((fila1, fila2) -> {
                for (Map.Entry<String, String> orden : ordenamiento.entrySet()) {
                    String campo = orden.getKey();
                    String direccion = orden.getValue();
                    
                    Object valor1 = fila1.get(campo);
                    Object valor2 = fila2.get(campo);
                    
                    if (valor1 == null && valor2 == null) continue;
                    if (valor1 == null) return "ASC".equals(direccion) ? -1 : 1;
                    if (valor2 == null) return "ASC".equals(direccion) ? 1 : -1;
                    
                    int comparacion;
                    if (valor1 instanceof Comparable && valor2 instanceof Comparable) {
                        @SuppressWarnings("unchecked")
                        Comparable<Object> comp1 = (Comparable<Object>) valor1;
                        comparacion = comp1.compareTo(valor2);
                    } else {
                        comparacion = valor1.toString().compareTo(valor2.toString());
                    }
                    
                    if (comparacion != 0) {
                        return "DESC".equals(direccion) ? -comparacion : comparacion;
                    }
                }
                return 0;
            })
            .collect(Collectors.toList());
    }
    
    /**
     * Enriquece los datos del dashboard con cálculos adicionales
     */
    private void enriquecerDashboard(DashboardData dashboard) {
        // Calcular porcentajes de disponibilidad
        int totalActivos = dashboard.getTotalActivos();
        if (totalActivos > 0) {
            double porcentajeOperativo = (dashboard.getActivosOperativos() * 100.0) / totalActivos;
            double porcentajeMantenimiento = (dashboard.getActivosEnMantenimiento() * 100.0) / totalActivos;
            double porcentajeFueraServicio = (dashboard.getActivosFueraServicio() * 100.0) / totalActivos;
            
            // Agregar como campos adicionales (se podrían agregar al modelo DashboardData si es necesario)
            LOGGER.log(Level.INFO, "Porcentajes calculados: Operativo {0}%, Mantenimiento {1}%, Fuera de Servicio {2}%",
                      new Object[]{porcentajeOperativo, porcentajeMantenimiento, porcentajeFueraServicio});
        }
        
        // Calcular eficiencia del equipo técnico
        if (dashboard.getMantenimientosCompletadosMes() > 0 && dashboard.getTiempoPromedioResolucion() > 0) {
            double eficiencia = dashboard.getMantenimientosCompletadosMes() / dashboard.getTiempoPromedioResolucion();
            LOGGER.log(Level.INFO, "Eficiencia del equipo técnico: {0} mantenimientos/hora", eficiencia);
        }
    }
    
    /**
     * MÉTODOS DE UTILIDAD
     */
    
    /**
     * Obtiene las opciones disponibles para filtros
     */
    public Map<String, List<String>> obtenerOpcionesFiltros() {
        return reportesDAO.obtenerOpcionesFiltros();
    }
    
    /**
     * Obtiene estadísticas resumidas para un período
     */
    public Map<String, Object> obtenerEstadisticasResumen(LocalDate fechaInicio, LocalDate fechaFin) {
        return reportesDAO.obtenerEstadisticasResumen(fechaInicio, fechaFin);
    }
}
