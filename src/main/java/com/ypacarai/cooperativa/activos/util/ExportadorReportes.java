package com.ypacarai.cooperativa.activos.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.ypacarai.cooperativa.activos.model.ReporteCompleto;

/**
 * Utilidad para exportación de reportes en múltiples formatos
 * Implementación básica sin dependencias externas
 */
public class ExportadorReportes {
    
    private static final DateTimeFormatter FORMATO_FECHA_ARCHIVO = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
    
    /**
     * Exporta un reporte a CSV
     */
    public static boolean exportarCSV(ReporteCompleto reporte, JFrame ventanaPadre) {
        try {
            // Seleccionar archivo de destino
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Exportar Reporte a CSV");
            
            String nombreArchivo = "reporte_" + reporte.getTipoReporte().toLowerCase().replace(" ", "_") + 
                                  "_" + LocalDateTime.now().format(FORMATO_FECHA_ARCHIVO) + ".csv";
            fileChooser.setSelectedFile(new File(nombreArchivo));
            
            FileNameExtensionFilter filter = new FileNameExtensionFilter("Archivos CSV (*.csv)", "csv");
            fileChooser.setFileFilter(filter);
            
            int resultado = fileChooser.showSaveDialog(ventanaPadre);
            if (resultado == JFileChooser.APPROVE_OPTION) {
                File archivo = fileChooser.getSelectedFile();
                if (!archivo.getName().toLowerCase().endsWith(".csv")) {
                    archivo = new File(archivo.getAbsolutePath() + ".csv");
                }
                
                escribirCSV(reporte, archivo);
                
                JOptionPane.showMessageDialog(ventanaPadre, 
                    "Reporte exportado exitosamente a:\n" + archivo.getAbsolutePath(),
                    "Exportación Exitosa", 
                    JOptionPane.INFORMATION_MESSAGE);
                return true;
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(ventanaPadre, 
                "Error exportando reporte: " + e.getMessage(),
                "Error de Exportación", 
                JOptionPane.ERROR_MESSAGE);
        }
        return false;
    }
    
    private static void escribirCSV(ReporteCompleto reporte, File archivo) throws IOException {
        try (FileWriter writer = new FileWriter(archivo, java.nio.charset.StandardCharsets.UTF_8)) {
            // Encabezado del reporte
            writer.write("REPORTE: " + reporte.getTipoReporte() + "\n");
            writer.write("GENERADO: " + reporte.getFechaGeneracion() + "\n");
            writer.write("TOTAL REGISTROS: " + reporte.getTotalRegistros() + "\n");
            writer.write("\n");
            
            // Resumen ejecutivo
            if (reporte.getResumenEjecutivo() != null && !reporte.getResumenEjecutivo().isEmpty()) {
                writer.write("RESUMEN EJECUTIVO:\n");
                String[] lineasResumen = reporte.getResumenEjecutivo().split("\n");
                for (String linea : lineasResumen) {
                    writer.write("\"" + linea.replace("\"", "\"\"") + "\"\n");
                }
                writer.write("\n");
            }
            
            // Estadísticas
            if (reporte.getEstadisticas() != null && !reporte.getEstadisticas().isEmpty()) {
                writer.write("ESTADISTICAS PRINCIPALES:\n");
                for (Map.Entry<String, Object> stat : reporte.getEstadisticas().entrySet()) {
                    writer.write(stat.getKey() + "," + stat.getValue() + "\n");
                }
                writer.write("\n");
            }
            
            // Datos principales - esto requeriría conocer la estructura específica de cada tipo
            writer.write("DATOS DETALLADOS:\n");
            escribirDatosCSVPorTipo(reporte, writer);
        }
    }
    
    private static void escribirDatosCSVPorTipo(ReporteCompleto reporte, FileWriter writer) throws IOException {
        List<?> datos = reporte.getDatosOriginales();
        if (datos == null || datos.isEmpty()) {
            writer.write("No hay datos para mostrar\n");
            return;
        }
        
        String tipoReporte = reporte.getTipoReporte();
        switch (tipoReporte) {
            case "Estado de Activos":
                escribirCSVEstadoActivos(datos, writer);
                break;
            case "Mantenimientos":
                escribirCSVMantenimientos(datos, writer);
                break;
            case "Fallas":
                escribirCSVFallas(datos, writer);
                break;
            case "Traslados":
                escribirCSVTraslados(datos, writer);
                break;
            default:
                writer.write("Tipo de reporte no soportado para exportación CSV\n");
        }
    }
    
    @SuppressWarnings("unchecked")
    private static void escribirCSVEstadoActivos(List<?> datos, FileWriter writer) throws IOException {
        List<com.ypacarai.cooperativa.activos.model.ReporteEstadoActivos> reportes = 
            (List<com.ypacarai.cooperativa.activos.model.ReporteEstadoActivos>) datos;
        
        // Encabezados
        writer.write("Tipo Activo,Estado,Cantidad Total,Ubicacion,Proximos Mantenimiento,Mantenimiento Vencido,Fecha Consulta\n");
        
        // Datos
        for (com.ypacarai.cooperativa.activos.model.ReporteEstadoActivos item : reportes) {
            writer.write(String.format("%s,%s,%d,%s,%d,%d,%s\n",
                csvEscape(item.getTipoActivo()),
                csvEscape(item.getEstado()),
                item.getCantidadTotal(),
                csvEscape(item.getUbicacion()),
                item.getActivosProximosMantenimiento(),
                item.getActivosMantenimientoVencido(),
                item.getFechaConsulta() != null ? item.getFechaConsulta().toString() : ""
            ));
        }
    }
    
    @SuppressWarnings("unchecked")
    private static void escribirCSVMantenimientos(List<?> datos, FileWriter writer) throws IOException {
        List<com.ypacarai.cooperativa.activos.model.ReporteMantenimientos> reportes = 
            (List<com.ypacarai.cooperativa.activos.model.ReporteMantenimientos>) datos;
        
        // Encabezados
        writer.write("Tipo Mantenimiento,Tipo Activo,Total Mantenimientos,Tiempo Promedio (h),Tecnico Asignado,Costo Total,Estado\n");
        
        // Datos
        for (com.ypacarai.cooperativa.activos.model.ReporteMantenimientos item : reportes) {
            writer.write(String.format("%s,%s,%d,%.2f,%s,%.2f,%s\n",
                csvEscape(item.getTipoMantenimiento()),
                csvEscape(item.getTipoActivo()),
                item.getTotalMantenimientos(),
                item.getTiempoPromedioResolucion(),
                csvEscape(item.getTecnicoAsignado()),
                item.getCostoTotal(),
                csvEscape(item.getEstadoMantenimiento())
            ));
        }
    }
    
    @SuppressWarnings("unchecked")
    private static void escribirCSVFallas(List<?> datos, FileWriter writer) throws IOException {
        List<com.ypacarai.cooperativa.activos.model.ReporteFallas> reportes = 
            (List<com.ypacarai.cooperativa.activos.model.ReporteFallas>) datos;
        
        // Encabezados
        writer.write("Tipo Activo,Numero Activo,Descripcion Falla,Frecuencia,Efectividad Reparacion (%),Fecha Ultima Falla\n");
        
        // Datos
        for (com.ypacarai.cooperativa.activos.model.ReporteFallas item : reportes) {
            writer.write(String.format("%s,%s,%s,%d,%.2f,%s\n",
                csvEscape(item.getTipoActivo()),
                csvEscape(item.getNumeroActivo()),
                csvEscape(item.getDescripcionFalla()),
                item.getFrecuenciaFallas(),
                item.getEfectividadReparacion(),
                item.getFechaUltimaFalla() != null ? item.getFechaUltimaFalla().toString() : ""
            ));
        }
    }
    
    @SuppressWarnings("unchecked")
    private static void escribirCSVTraslados(List<?> datos, FileWriter writer) throws IOException {
        List<com.ypacarai.cooperativa.activos.model.ReporteTraslados> reportes = 
            (List<com.ypacarai.cooperativa.activos.model.ReporteTraslados>) datos;
        
        // Encabezados
        writer.write("Numero Activo,Tipo Activo,Ubicacion Origen,Ubicacion Destino,Estado Traslado,Dias en Ubicacion,Responsable Envio\n");
        
        // Datos
        for (com.ypacarai.cooperativa.activos.model.ReporteTraslados item : reportes) {
            writer.write(String.format("%s,%s,%s,%s,%s,%d,%s\n",
                csvEscape(item.getNumeroActivo()),
                csvEscape(item.getTipoActivo()),
                csvEscape(item.getUbicacionOrigen()),
                csvEscape(item.getUbicacionDestino()),
                csvEscape(item.getEstadoTraslado()),
                item.getDiasEnUbicacion(),
                csvEscape(item.getResponsableEnvio())
            ));
        }
    }
    
    /**
     * Escapa caracteres especiales para CSV
     */
    private static String csvEscape(String valor) {
        if (valor == null) {
            return "";
        }
        
        // Si contiene comas, comillas o saltos de línea, envolver en comillas
        if (valor.contains(",") || valor.contains("\"") || valor.contains("\n")) {
            // Duplicar comillas internas y envolver en comillas
            return "\"" + valor.replace("\"", "\"\"") + "\"";
        }
        
        return valor;
    }
    
    /**
     * Exporta un reporte a texto plano (implementación básica)
     */
    public static boolean exportarTexto(ReporteCompleto reporte, JFrame ventanaPadre) {
        try {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Exportar Reporte a Texto");
            
            String nombreArchivo = "reporte_" + reporte.getTipoReporte().toLowerCase().replace(" ", "_") + 
                                  "_" + LocalDateTime.now().format(FORMATO_FECHA_ARCHIVO) + ".txt";
            fileChooser.setSelectedFile(new File(nombreArchivo));
            
            FileNameExtensionFilter filter = new FileNameExtensionFilter("Archivos de Texto (*.txt)", "txt");
            fileChooser.setFileFilter(filter);
            
            int resultado = fileChooser.showSaveDialog(ventanaPadre);
            if (resultado == JFileChooser.APPROVE_OPTION) {
                File archivo = fileChooser.getSelectedFile();
                if (!archivo.getName().toLowerCase().endsWith(".txt")) {
                    archivo = new File(archivo.getAbsolutePath() + ".txt");
                }
                
                try (FileWriter writer = new FileWriter(archivo, java.nio.charset.StandardCharsets.UTF_8)) {
                    // Encabezado
                    writer.write("==========================================\n");
                    writer.write("  SISTEMA DE ACTIVOS - COOPERATIVA YPACARAÍ\n");
                    writer.write("==========================================\n\n");
                    writer.write("Reporte: " + reporte.getTipoReporte() + "\n");
                    writer.write("Generado: " + reporte.getFechaGeneracion() + "\n");
                    writer.write("Total de registros: " + reporte.getTotalRegistros() + "\n\n");
                    
                    // Resumen ejecutivo
                    if (reporte.getResumenEjecutivo() != null) {
                        writer.write(reporte.getResumenEjecutivo());
                        writer.write("\n\n");
                    }
                    
                    // Estadísticas
                    if (reporte.getEstadisticas() != null && !reporte.getEstadisticas().isEmpty()) {
                        writer.write("ESTADÍSTICAS PRINCIPALES:\n");
                        writer.write("========================\n");
                        for (Map.Entry<String, Object> stat : reporte.getEstadisticas().entrySet()) {
                            writer.write("• " + stat.getKey() + ": " + stat.getValue() + "\n");
                        }
                        writer.write("\n");
                    }
                }
                
                JOptionPane.showMessageDialog(ventanaPadre, 
                    "Reporte exportado exitosamente a:\n" + archivo.getAbsolutePath(),
                    "Exportación Exitosa", 
                    JOptionPane.INFORMATION_MESSAGE);
                return true;
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(ventanaPadre, 
                "Error exportando reporte: " + e.getMessage(),
                "Error de Exportación", 
                JOptionPane.ERROR_MESSAGE);
        }
        return false;
    }
    
    /**
     * Muestra información sobre funcionalidades futuras
     */
    public static void mostrarInfoExportacionAvanzada(JFrame ventanaPadre, String formato) {
        String mensaje = String.format(
            "Exportación a %s\n\n" +
            "Para una implementación completa, se recomienda integrar:\n\n" +
            "• Para Excel: Apache POI (poi-ooxml)\n" +
            "• Para PDF: iText 7 o Apache PDFBox\n" +
            "• Para gráficos: JFreeChart\n\n" +
            "Por ahora puede usar la exportación a CSV que está completamente funcional.",
            formato
        );
        
        JOptionPane.showMessageDialog(ventanaPadre, 
            mensaje,
            "Exportación " + formato, 
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Método de utilidad para formatear números en reportes
     */
    public static String formatearNumero(Object numero) {
        if (numero == null) return "0";
        
        if (numero instanceof Double) {
            return String.format("%.2f", (Double) numero);
        } else if (numero instanceof Float) {
            return String.format("%.2f", (Float) numero);
        } else if (numero instanceof Integer) {
            return String.format("%,d", (Integer) numero);
        }
        
        return numero.toString();
    }
    
    /**
     * Método de utilidad para formatear fechas en reportes
     */
    public static String formatearFecha(Object fecha) {
        if (fecha == null) return "N/A";
        
        if (fecha instanceof LocalDateTime) {
            return ((LocalDateTime) fecha).format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        } else if (fecha instanceof java.time.LocalDate) {
            return ((java.time.LocalDate) fecha).format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        }
        
        return fecha.toString();
    }
}
