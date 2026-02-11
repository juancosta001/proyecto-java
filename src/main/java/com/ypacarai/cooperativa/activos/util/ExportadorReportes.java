package com.ypacarai.cooperativa.activos.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
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
     * Exporta un reporte a Excel con formato profesional
     */
    public static boolean exportarExcel(ReporteCompleto reporte, JFrame ventanaPadre) {
        try {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Exportar Reporte a Excel");
            
            String nombreArchivo = "reporte_" + reporte.getTipoReporte().toLowerCase().replace(" ", "_") + 
                                  "_" + LocalDateTime.now().format(FORMATO_FECHA_ARCHIVO) + ".xlsx";
            fileChooser.setSelectedFile(new File(nombreArchivo));
            
            FileNameExtensionFilter filter = new FileNameExtensionFilter("Archivos Excel (*.xlsx)", "xlsx");
            fileChooser.setFileFilter(filter);
            
            int resultado = fileChooser.showSaveDialog(ventanaPadre);
            if (resultado == JFileChooser.APPROVE_OPTION) {
                File archivo = fileChooser.getSelectedFile();
                if (!archivo.getName().toLowerCase().endsWith(".xlsx")) {
                    archivo = new File(archivo.getAbsolutePath() + ".xlsx");
                }
                
                escribirExcel(reporte, archivo);
                
                JOptionPane.showMessageDialog(ventanaPadre, 
                    "Reporte exportado exitosamente a:\n" + archivo.getAbsolutePath(),
                    "Exportación Exitosa", 
                    JOptionPane.INFORMATION_MESSAGE);
                return true;
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(ventanaPadre, 
                "Error exportando reporte a Excel: " + e.getMessage(),
                "Error de Exportación", 
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
        return false;
    }
    
    private static void escribirExcel(ReporteCompleto reporte, File archivo) throws IOException {
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            // Estilos
            CellStyle estiloTitulo = crearEstiloTitulo(workbook);
            CellStyle estiloEncabezado = crearEstiloEncabezado(workbook);
            CellStyle estiloDatos = crearEstiloDatos(workbook);
            CellStyle estiloFecha = crearEstiloFecha(workbook);
            CellStyle estiloNumero = crearEstiloNumero(workbook);
            CellStyle estiloResaltado = crearEstiloResaltado(workbook);
            CellStyle estiloSeccion = crearEstiloSeccion(workbook);
            
            // Hoja 1: Resumen Ejecutivo
            crearHojaResumenEjecutivo(workbook, reporte, estiloTitulo, estiloEncabezado, 
                                     estiloDatos, estiloNumero, estiloResaltado, estiloSeccion);
            
            // Hoja 2: Estadísticas Principales
            crearHojaEstadisticas(workbook, reporte, estiloTitulo, estiloEncabezado, 
                                 estiloDatos, estiloNumero, estiloResaltado);
            
            // Hoja 3: Estado de Activos (análisis)
            crearHojaEstadoActivos(workbook, reporte, estiloTitulo, estiloEncabezado, 
                                  estiloDatos, estiloNumero, estiloResaltado);
            
            // Hoja 4: Datos Detallados (tabla original)
            XSSFSheet sheetDatos = workbook.createSheet("Datos Detallados");
            escribirDatosExcelPorTipo(reporte, sheetDatos, 0, estiloEncabezado, estiloDatos, estiloFecha, estiloNumero);
            
            // Ajustar anchos de columna en hoja de datos
            for (int i = 0; i < 10; i++) {
                sheetDatos.setColumnWidth(i, 4000);
            }
            
            // Guardar archivo
            try (FileOutputStream out = new FileOutputStream(archivo)) {
                workbook.write(out);
            }
        }
    }
    
    private static CellStyle crearEstiloTitulo(XSSFWorkbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 14);
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        return style;
    }
    
    private static CellStyle crearEstiloEncabezado(XSSFWorkbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 12);
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.DARK_GREEN.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }
    
    private static CellStyle crearEstiloDatos(XSSFWorkbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }
    
    private static CellStyle crearEstiloFecha(XSSFWorkbook workbook) {
        CellStyle style = crearEstiloDatos(workbook);
        DataFormat format = workbook.createDataFormat();
        style.setDataFormat(format.getFormat("dd/mm/yyyy"));
        return style;
    }
    
    private static CellStyle crearEstiloNumero(XSSFWorkbook workbook) {
        CellStyle style = crearEstiloDatos(workbook);
        DataFormat format = workbook.createDataFormat();
        style.setDataFormat(format.getFormat("#,##0.00"));
        return style;
    }
    
    private static CellStyle crearEstiloResaltado(XSSFWorkbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 11);
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }
    
    private static CellStyle crearEstiloSeccion(XSSFWorkbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 11);
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.DARK_TEAL.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }
    
    private static void crearHojaResumenEjecutivo(XSSFWorkbook workbook, ReporteCompleto reporte,
                                                  CellStyle estiloTitulo, CellStyle estiloEncabezado,
                                                  CellStyle estiloDatos, CellStyle estiloNumero,
                                                  CellStyle estiloResaltado, CellStyle estiloSeccion) {
        XSSFSheet sheet = workbook.createSheet("Resumen Ejecutivo");
        int filaActual = 0;
        
        // Título
        Row filaTitulo = sheet.createRow(filaActual++);
        Cell celdaTitulo = filaTitulo.createCell(0);
        celdaTitulo.setCellValue("📊 RESUMEN EJECUTIVO");
        celdaTitulo.setCellStyle(estiloTitulo);
        sheet.addMergedRegion(new CellRangeAddress(filaActual - 1, filaActual - 1, 0, 5));
        filaActual++;
        
        // Información del reporte
        Row filaReporte = sheet.createRow(filaActual++);
        filaReporte.createCell(0).setCellValue("Tipo de Reporte:");
        filaReporte.getCell(0).setCellStyle(estiloResaltado);
        filaReporte.createCell(1).setCellValue(reporte.getTipoReporte());
        filaReporte.getCell(1).setCellStyle(estiloDatos);
        
        Row filaFecha = sheet.createRow(filaActual++);
        filaFecha.createCell(0).setCellValue("Fecha de Generación:");
        filaFecha.getCell(0).setCellStyle(estiloResaltado);
        filaFecha.createCell(1).setCellValue(reporte.getFechaGeneracion());
        filaFecha.getCell(1).setCellStyle(estiloDatos);
        
        Row filaTotal = sheet.createRow(filaActual++);
        filaTotal.createCell(0).setCellValue("Total de Registros:");
        filaTotal.getCell(0).setCellStyle(estiloResaltado);
        Cell celdaTotalVal = filaTotal.createCell(1);
        celdaTotalVal.setCellValue(reporte.getTotalRegistros());
        celdaTotalVal.setCellStyle(estiloNumero);
        
        filaActual += 2;
        
        // Resumen ejecutivo detallado
        Row filaSeccion = sheet.createRow(filaActual++);
        Cell celdaSeccion = filaSeccion.createCell(0);
        celdaSeccion.setCellValue("📝 ANÁLISIS GENERAL");
        celdaSeccion.setCellStyle(estiloSeccion);
        sheet.addMergedRegion(new CellRangeAddress(filaActual - 1, filaActual - 1, 0, 5));
        filaActual++;
        
        if (reporte.getResumenEjecutivo() != null && !reporte.getResumenEjecutivo().isEmpty()) {
            String[] lineas = reporte.getResumenEjecutivo().split("\n");
            for (String linea : lineas) {
                if (!linea.trim().isEmpty()) {
                    Row filaLinea = sheet.createRow(filaActual++);
                    Cell celdaLinea = filaLinea.createCell(0);
                    celdaLinea.setCellValue("• " + linea.trim());
                    celdaLinea.setCellStyle(estiloDatos);
                    sheet.addMergedRegion(new CellRangeAddress(filaActual - 1, filaActual - 1, 0, 5));
                }
            }
        } else {
            Row filaInfo = sheet.createRow(filaActual++);
            Cell celdaInfo = filaInfo.createCell(0);
            celdaInfo.setCellValue("Este reporte contiene " + reporte.getTotalRegistros() + " registros de " + reporte.getTipoReporte());
            celdaInfo.setCellStyle(estiloDatos);
            sheet.addMergedRegion(new CellRangeAddress(filaActual - 1, filaActual - 1, 0, 5));
        }
        
        filaActual += 2;
        
        // Conclusiones y recomendaciones
        Row filaConc = sheet.createRow(filaActual++);
        Cell celdaConc = filaConc.createCell(0);
        celdaConc.setCellValue("💡 CONCLUSIONES");
        celdaConc.setCellStyle(estiloSeccion);
        sheet.addMergedRegion(new CellRangeAddress(filaActual - 1, filaActual - 1, 0, 5));
        filaActual++;
        
        // Agregar conclusiones basadas en el tipo de reporte
        agregarConclusiones(sheet, reporte, filaActual, estiloDatos);
        
        // Ajustar anchos
        sheet.setColumnWidth(0, 8000);
        sheet.setColumnWidth(1, 10000);
        for (int i = 2; i < 6; i++) {
            sheet.setColumnWidth(i, 4000);
        }
    }
    
    private static void agregarConclusiones(XSSFSheet sheet, ReporteCompleto reporte, 
                                           int filaInicio, CellStyle estiloDatos) {
        int filaActual = filaInicio;
        
        // Conclusiones generales basadas en estadísticas
        if (reporte.getEstadisticas() != null && !reporte.getEstadisticas().isEmpty()) {
            for (Map.Entry<String, Object> stat : reporte.getEstadisticas().entrySet()) {
                String conclusion = generarConclusionDesdEstadistica(stat.getKey(), stat.getValue());
                if (conclusion != null) {
                    Row fila = sheet.createRow(filaActual++);
                    Cell celda = fila.createCell(0);
                    celda.setCellValue("✓ " + conclusion);
                    celda.setCellStyle(estiloDatos);
                    sheet.addMergedRegion(new CellRangeAddress(filaActual - 1, filaActual - 1, 0, 5));
                }
            }
        }
        
        if (filaActual == filaInicio) {
            Row fila = sheet.createRow(filaActual);
            Cell celda = fila.createCell(0);
            celda.setCellValue("✓ Todos los datos han sido procesados correctamente.");
            celda.setCellStyle(estiloDatos);
            sheet.addMergedRegion(new CellRangeAddress(filaActual, filaActual, 0, 5));
        }
    }
    
    private static String generarConclusionDesdEstadistica(String clave, Object valor) {
        // Ignorar Maps y Collections
        if (valor instanceof Map || valor instanceof java.util.Collection) {
            return null;
        }
        
        if (valor instanceof Number) {
            double num = ((Number) valor).doubleValue();
            
            if (clave.contains("mantenimientoVencido") || clave.contains("Vencido")) {
                if (num > 0) {
                    return String.format("Se detectaron %.0f activos con mantenimiento vencido que requieren atención inmediata.", num);
                }
            } else if (clave.contains("proximosMantenimiento") || clave.contains("Proximos")) {
                if (num > 0) {
                    return String.format("Hay %.0f activos próximos a mantenimiento. Planificar atención preventiva.", num);
                }
            } else if (clave.contains("costoTotal") || clave.contains("Costo")) {
                return String.format("El costo total registrado es de Gs. %.2f", num);
            } else if (clave.contains("efectividad") || clave.contains("Efectividad")) {
                if (num >= 80) {
                    return String.format("La efectividad de %.1f%% indica un buen desempeño en resolución.", num);
                } else if (num < 60) {
                    return String.format("La efectividad de %.1f%% es baja. Revisar procedimientos de mantenimiento.", num);
                }
            }
        }
        return null;
    }
    
    private static void crearHojaEstadisticas(XSSFWorkbook workbook, ReporteCompleto reporte,
                                             CellStyle estiloTitulo, CellStyle estiloEncabezado,
                                             CellStyle estiloDatos, CellStyle estiloNumero,
                                             CellStyle estiloResaltado) {
        XSSFSheet sheet = workbook.createSheet("Estadísticas Principales");
        int filaActual = 0;
        
        // Título
        Row filaTitulo = sheet.createRow(filaActual++);
        Cell celdaTitulo = filaTitulo.createCell(0);
        celdaTitulo.setCellValue("📈 ESTADÍSTICAS PRINCIPALES");
        celdaTitulo.setCellStyle(estiloTitulo);
        sheet.addMergedRegion(new CellRangeAddress(filaActual - 1, filaActual - 1, 0, 3));
        filaActual += 2;
        
        // Encabezados
        Row filaEnc = sheet.createRow(filaActual++);
        Cell celdaEnc1 = filaEnc.createCell(0);
        celdaEnc1.setCellValue("📊 Indicador");
        celdaEnc1.setCellStyle(estiloEncabezado);
        
        Cell celdaEnc2 = filaEnc.createCell(1);
        celdaEnc2.setCellValue("💯 Valor");
        celdaEnc2.setCellStyle(estiloEncabezado);
        
        Cell celdaEnc3 = filaEnc.createCell(2);
        celdaEnc3.setCellValue("📝 Descripción");
        celdaEnc3.setCellStyle(estiloEncabezado);
        
        // Datos de estadísticas
        if (reporte.getEstadisticas() != null && !reporte.getEstadisticas().isEmpty()) {
            for (Map.Entry<String, Object> stat : reporte.getEstadisticas().entrySet()) {
                // Saltar Maps y Collections - se mostrarán en hoja "Estado de Activos"
                if (stat.getValue() instanceof Map || stat.getValue() instanceof java.util.Collection) {
                    continue;
                }
                
                Row filaStat = sheet.createRow(filaActual++);
                
                // Nombre del indicador
                Cell celdaNombre = filaStat.createCell(0);
                celdaNombre.setCellValue(formatearNombreEstadistica(stat.getKey()));
                celdaNombre.setCellStyle(estiloDatos);
                
                // Valor
                Cell celdaValor = filaStat.createCell(1);
                if (stat.getValue() instanceof Number) {
                    celdaValor.setCellValue(((Number) stat.getValue()).doubleValue());
                    celdaValor.setCellStyle(estiloNumero);
                } else {
                    celdaValor.setCellValue(stat.getValue().toString());
                    celdaValor.setCellStyle(estiloDatos);
                }
                
                // Descripción
                Cell celdaDesc = filaStat.createCell(2);
                celdaDesc.setCellValue(obtenerDescripcionEstadistica(stat.getKey()));
                celdaDesc.setCellStyle(estiloDatos);
            }
        } else {
            Row filaSinDatos = sheet.createRow(filaActual++);
            Cell celdaSinDatos = filaSinDatos.createCell(0);
            celdaSinDatos.setCellValue("No hay estadísticas disponibles para este reporte.");
            celdaSinDatos.setCellStyle(estiloDatos);
            sheet.addMergedRegion(new CellRangeAddress(filaActual - 1, filaActual - 1, 0, 2));
        }
        
        // Ajustar anchos
        sheet.setColumnWidth(0, 9000);
        sheet.setColumnWidth(1, 5000);
        sheet.setColumnWidth(2, 12000);
    }
    
    private static String obtenerDescripcionEstadistica(String clave) {
        Map<String, String> descripciones = new HashMap<>();
        descripciones.put("totalActivos", "Cantidad total de activos registrados en el sistema");
        descripciones.put("proximosMantenimiento", "Activos que requieren mantenimiento en los próximos días");
        descripciones.put("mantenimientoVencido", "Activos con mantenimiento atrasado que necesitan atención urgente");
        descripciones.put("porcentajeProximoMantenimiento", "Porcentaje de activos próximos a mantenimiento");
        descripciones.put("porcentajeMantenimientoVencido", "Porcentaje de activos con mantenimiento vencido");
        descripciones.put("activosOperativos", "Activos actualmente en operación");
        descripciones.put("activosFueraServicio", "Activos que no están operativos");
        descripciones.put("activosEnMantenimiento", "Activos que se encuentran en proceso de mantenimiento");
        descripciones.put("costoTotalMantenimiento", "Suma total de costos de mantenimiento registrados");
        descripciones.put("tiempoPromedioResolucion", "Tiempo promedio en horas para resolver mantenimientos");
        descripciones.put("totalMantenimientos", "Cantidad total de mantenimientos realizados");
        descripciones.put("totalFallas", "Cantidad total de fallas registradas");
        descripciones.put("efectividadPromedio", "Porcentaje de efectividad promedio en resolución de problemas");
        
        return descripciones.getOrDefault(clave, "Métrica relacionada con " + formatearNombreEstadistica(clave));
    }
    
    private static void crearHojaEstadoActivos(XSSFWorkbook workbook, ReporteCompleto reporte,
                                              CellStyle estiloTitulo, CellStyle estiloEncabezado,
                                              CellStyle estiloDatos, CellStyle estiloNumero,
                                              CellStyle estiloResaltado) {
        XSSFSheet sheet = workbook.createSheet("Estado de Activos");
        int filaActual = 0;
        
        // Título
        Row filaTitulo = sheet.createRow(filaActual++);
        Cell celdaTitulo = filaTitulo.createCell(0);
        celdaTitulo.setCellValue("🏢 ESTADO GENERAL DE ACTIVOS");
        celdaTitulo.setCellStyle(estiloTitulo);
        sheet.addMergedRegion(new CellRangeAddress(filaActual - 1, filaActual - 1, 0, 4));
        filaActual += 2;
        
        // Resumen por estado
        Row filaSecEstado = sheet.createRow(filaActual++);
        Cell celdaSecEstado = filaSecEstado.createCell(0);
        celdaSecEstado.setCellValue("📊 DISTRIBUCIÓN POR ESTADO");
        celdaSecEstado.setCellStyle(estiloEncabezado);
        sheet.addMergedRegion(new CellRangeAddress(filaActual - 1, filaActual - 1, 0, 4));
        filaActual++;
        
        // Contar y mostrar distribución por estado
        Map<String, Integer> distribucionEstado = calcularDistribucionEstado(reporte);
        if (!distribucionEstado.isEmpty()) {
            Row filaEncEstado = sheet.createRow(filaActual++);
            filaEncEstado.createCell(0).setCellValue("Estado");
            filaEncEstado.getCell(0).setCellStyle(estiloEncabezado);
            filaEncEstado.createCell(1).setCellValue("Cantidad");
            filaEncEstado.getCell(1).setCellStyle(estiloEncabezado);
            filaEncEstado.createCell(2).setCellValue("Porcentaje");
            filaEncEstado.getCell(2).setCellStyle(estiloEncabezado);
            
            int totalActivos = distribucionEstado.values().stream().mapToInt(Integer::intValue).sum();
            for (Map.Entry<String, Integer> entry : distribucionEstado.entrySet()) {
                Row fila = sheet.createRow(filaActual++);
                fila.createCell(0).setCellValue(entry.getKey());
                fila.getCell(0).setCellStyle(estiloDatos);
                
                Cell celdaCant = fila.createCell(1);
                celdaCant.setCellValue(entry.getValue());
                celdaCant.setCellStyle(estiloNumero);
                
                Cell celdaPorce = fila.createCell(2);
                double porcentaje = totalActivos > 0 ? (entry.getValue() * 100.0 / totalActivos) : 0;
                celdaPorce.setCellValue(porcentaje);
                celdaPorce.setCellStyle(estiloNumero);
            }
        }
        
        filaActual += 2;
        
        // Resumen por ubicación
        Row filaSecUbicacion = sheet.createRow(filaActual++);
        Cell celdaSecUbicacion = filaSecUbicacion.createCell(0);
        celdaSecUbicacion.setCellValue("📍 DISTRIBUCIÓN POR UBICACIÓN");
        celdaSecUbicacion.setCellStyle(estiloEncabezado);
        sheet.addMergedRegion(new CellRangeAddress(filaActual - 1, filaActual - 1, 0, 4));
        filaActual++;
        
        Map<String, Integer> distribucionUbicacion = calcularDistribucionUbicacion(reporte);
        if (!distribucionUbicacion.isEmpty()) {
            Row filaEncUbic = sheet.createRow(filaActual++);
            filaEncUbic.createCell(0).setCellValue("Ubicación");
            filaEncUbic.getCell(0).setCellStyle(estiloEncabezado);
            filaEncUbic.createCell(1).setCellValue("Cantidad");
            filaEncUbic.getCell(1).setCellStyle(estiloEncabezado);
            
            for (Map.Entry<String, Integer> entry : distribucionUbicacion.entrySet()) {
                Row fila = sheet.createRow(filaActual++);
                fila.createCell(0).setCellValue(entry.getKey());
                fila.getCell(0).setCellStyle(estiloDatos);
                
                Cell celdaCant = fila.createCell(1);
                celdaCant.setCellValue(entry.getValue());
                celdaCant.setCellStyle(estiloNumero);
            }
        }
        
        filaActual += 2;
        
        // Resumen por tipo
        Row filaSecTipo = sheet.createRow(filaActual++);
        Cell celdaSecTipo = filaSecTipo.createCell(0);
        celdaSecTipo.setCellValue("🔧 DISTRIBUCIÓN POR TIPO");
        celdaSecTipo.setCellStyle(estiloEncabezado);
        sheet.addMergedRegion(new CellRangeAddress(filaActual - 1, filaActual - 1, 0, 4));
        filaActual++;
        
        Map<String, Integer> distribucionTipo = calcularDistribucionTipo(reporte);
        if (!distribucionTipo.isEmpty()) {
            Row filaEncTipo = sheet.createRow(filaActual++);
            filaEncTipo.createCell(0).setCellValue("Tipo de Activo");
            filaEncTipo.getCell(0).setCellStyle(estiloEncabezado);
            filaEncTipo.createCell(1).setCellValue("Cantidad");
            filaEncTipo.getCell(1).setCellStyle(estiloEncabezado);
            
            for (Map.Entry<String, Integer> entry : distribucionTipo.entrySet()) {
                Row fila = sheet.createRow(filaActual++);
                fila.createCell(0).setCellValue(entry.getKey());
                fila.getCell(0).setCellStyle(estiloDatos);
                
                Cell celdaCant = fila.createCell(1);
                celdaCant.setCellValue(entry.getValue());
                celdaCant.setCellStyle(estiloNumero);
            }
        }
        
        filaActual += 2;
        
        // Alertas y recomendaciones
        Row filaSecAlertas = sheet.createRow(filaActual++);
        Cell celdaSecAlertas = filaSecAlertas.createCell(0);
        celdaSecAlertas.setCellValue("⚠️ ALERTAS Y RECOMENDACIONES");
        celdaSecAlertas.setCellStyle(estiloEncabezado);
        sheet.addMergedRegion(new CellRangeAddress(filaActual - 1, filaActual - 1, 0, 4));
        filaActual++;
        
        List<String> alertas = generarAlertas(reporte);
        if (!alertas.isEmpty()) {
            for (String alerta : alertas) {
                Row fila = sheet.createRow(filaActual++);
                Cell celda = fila.createCell(0);
                celda.setCellValue(alerta);
                celda.setCellStyle(estiloDatos);
                sheet.addMergedRegion(new CellRangeAddress(filaActual - 1, filaActual - 1, 0, 4));
            }
        } else {
            Row fila = sheet.createRow(filaActual++);
            Cell celda = fila.createCell(0);
            celda.setCellValue("✓ No hay alertas críticas en este momento.");
            celda.setCellStyle(estiloDatos);
            sheet.addMergedRegion(new CellRangeAddress(filaActual - 1, filaActual - 1, 0, 4));
        }
        
        // Ajustar anchos
        sheet.setColumnWidth(0, 10000);
        sheet.setColumnWidth(1, 5000);
        sheet.setColumnWidth(2, 5000);
        sheet.setColumnWidth(3, 5000);
        sheet.setColumnWidth(4, 5000);
    }
    
    private static Map<String, Integer> calcularDistribucionEstado(ReporteCompleto reporte) {
        Map<String, Integer> distribucion = new HashMap<>();
        
        if ("Estado de Activos".equals(reporte.getTipoReporte()) && reporte.getDatosOriginales() != null) {
            @SuppressWarnings("unchecked")
            List<com.ypacarai.cooperativa.activos.model.ReporteEstadoActivos> datos = 
                (List<com.ypacarai.cooperativa.activos.model.ReporteEstadoActivos>) reporte.getDatosOriginales();
            
            for (com.ypacarai.cooperativa.activos.model.ReporteEstadoActivos item : datos) {
                String estado = item.getEstado() != null ? item.getEstado() : "Sin Estado";
                distribucion.put(estado, distribucion.getOrDefault(estado, 0) + item.getCantidadTotal());
            }
        }
        
        return distribucion;
    }
    
    private static Map<String, Integer> calcularDistribucionTipo(ReporteCompleto reporte) {
        Map<String, Integer> distribucion = new HashMap<>();
        
        if ("Estado de Activos".equals(reporte.getTipoReporte()) && reporte.getDatosOriginales() != null) {
            @SuppressWarnings("unchecked")
            List<com.ypacarai.cooperativa.activos.model.ReporteEstadoActivos> datos = 
                (List<com.ypacarai.cooperativa.activos.model.ReporteEstadoActivos>) reporte.getDatosOriginales();
            
            for (com.ypacarai.cooperativa.activos.model.ReporteEstadoActivos item : datos) {
                String tipo = item.getTipoActivo() != null ? item.getTipoActivo() : "Sin Tipo";
                distribucion.put(tipo, distribucion.getOrDefault(tipo, 0) + item.getCantidadTotal());
            }
        }
        
        return distribucion;
    }
    
    private static Map<String, Integer> calcularDistribucionUbicacion(ReporteCompleto reporte) {
        Map<String, Integer> distribucion = new HashMap<>();
        
        if ("Estado de Activos".equals(reporte.getTipoReporte()) && reporte.getDatosOriginales() != null) {
            @SuppressWarnings("unchecked")
            List<com.ypacarai.cooperativa.activos.model.ReporteEstadoActivos> datos = 
                (List<com.ypacarai.cooperativa.activos.model.ReporteEstadoActivos>) reporte.getDatosOriginales();
            
            for (com.ypacarai.cooperativa.activos.model.ReporteEstadoActivos item : datos) {
                String ubicacion = item.getUbicacion() != null ? item.getUbicacion() : "Sin Ubicación";
                distribucion.put(ubicacion, distribucion.getOrDefault(ubicacion, 0) + item.getCantidadTotal());
            }
        }
        
        return distribucion;
    }
    
    private static List<String> generarAlertas(ReporteCompleto reporte) {
        List<String> alertas = new java.util.ArrayList<>();
        
        // Verificar estadísticas para generar alertas
        if (reporte.getEstadisticas() != null) {
            for (Map.Entry<String, Object> stat : reporte.getEstadisticas().entrySet()) {
                if (stat.getValue() instanceof Number) {
                    double valor = ((Number) stat.getValue()).doubleValue();
                    
                    if ((stat.getKey().contains("mantenimientoVencido") || stat.getKey().contains("Vencido")) && valor > 0) {
                        alertas.add(String.format("⚠️ CRÍTICO: %.0f activos con mantenimiento vencido requieren atención urgente.", valor));
                    }
                    
                    if ((stat.getKey().contains("proximosMantenimiento") || stat.getKey().contains("Proximos")) && valor > 5) {
                        alertas.add(String.format("⚡ IMPORTANTE: %.0f activos próximos a mantenimiento. Programar intervenciones.", valor));
                    }
                    
                    if (stat.getKey().contains("efectividad") && valor < 60) {
                        alertas.add(String.format("⚠️ ALERTA: Efectividad de %.1f%% está por debajo del umbral aceptable (60%%).", valor));
                    }
                    
                    if (stat.getKey().contains("FueraServicio") && valor > 0) {
                        alertas.add(String.format("🔴 ATENCIÓN: %.0f activos fuera de servicio. Evaluar para reparación o baja.", valor));
                    }
                }
            }
        }
        
        return alertas;
    }
    
    private static void escribirDatosExcelPorTipo(ReporteCompleto reporte, XSSFSheet sheet, int filaInicio,
                                                   CellStyle estiloEncabezado, CellStyle estiloDatos,
                                                   CellStyle estiloFecha, CellStyle estiloNumero) {
        List<?> datos = reporte.getDatosOriginales();
        if (datos == null || datos.isEmpty()) {
            return;
        }
        
        String tipoReporte = reporte.getTipoReporte();
        switch (tipoReporte) {
            case "Estado de Activos":
                escribirExcelEstadoActivos(datos, sheet, filaInicio, estiloEncabezado, estiloDatos, estiloNumero);
                break;
            case "Mantenimientos":
                escribirExcelMantenimientos(datos, sheet, filaInicio, estiloEncabezado, estiloDatos, estiloNumero);
                break;
            case "Fallas":
                escribirExcelFallas(datos, sheet, filaInicio, estiloEncabezado, estiloDatos, estiloFecha, estiloNumero);
                break;
            case "Traslados":
                escribirExcelTraslados(datos, sheet, filaInicio, estiloEncabezado, estiloDatos, estiloNumero);
                break;
        }
    }
    
    @SuppressWarnings("unchecked")
    private static void escribirExcelEstadoActivos(List<?> datos, XSSFSheet sheet, int filaInicio,
                                                    CellStyle estiloEncabezado, CellStyle estiloDatos, CellStyle estiloNumero) {
        List<com.ypacarai.cooperativa.activos.model.ReporteEstadoActivos> reportes = 
            (List<com.ypacarai.cooperativa.activos.model.ReporteEstadoActivos>) datos;
        
        // Encabezados
        Row filaEncabezado = sheet.createRow(filaInicio++);
        String[] encabezados = {"📋 Tipo de Activo", "🟢 Estado", "🔢 Cantidad", "📍 Ubicación", "⏰ Próx. Mant.", "⚠️ Mant. Vencidos"};
        for (int i = 0; i < encabezados.length; i++) {
            Cell celda = filaEncabezado.createCell(i);
            celda.setCellValue(encabezados[i]);
            celda.setCellStyle(estiloEncabezado);
        }
        
        // Datos
        for (com.ypacarai.cooperativa.activos.model.ReporteEstadoActivos item : reportes) {
            Row fila = sheet.createRow(filaInicio++);
            fila.createCell(0).setCellValue(item.getTipoActivo());
            fila.createCell(1).setCellValue(item.getEstado());
            
            Cell celdaCantidad = fila.createCell(2);
            celdaCantidad.setCellValue(item.getCantidadTotal());
            celdaCantidad.setCellStyle(estiloNumero);
            
            fila.createCell(3).setCellValue(item.getUbicacion());
            
            Cell celdaProximos = fila.createCell(4);
            celdaProximos.setCellValue(item.getActivosProximosMantenimiento());
            celdaProximos.setCellStyle(estiloNumero);
            
            Cell celdaVencidos = fila.createCell(5);
            celdaVencidos.setCellValue(item.getActivosMantenimientoVencido());
            celdaVencidos.setCellStyle(estiloNumero);
        }
    }
    
    @SuppressWarnings("unchecked")
    private static void escribirExcelMantenimientos(List<?> datos, XSSFSheet sheet, int filaInicio,
                                                     CellStyle estiloEncabezado, CellStyle estiloDatos, CellStyle estiloNumero) {
        List<com.ypacarai.cooperativa.activos.model.ReporteMantenimientos> reportes = 
            (List<com.ypacarai.cooperativa.activos.model.ReporteMantenimientos>) datos;
        
        Row filaEncabezado = sheet.createRow(filaInicio++);
        String[] encabezados = {"🔧 Tipo Mant.", "📋 Tipo Activo", "🔢 Total", "⏱️ Tiempo (h)", "👨‍🔧 Técnico", "💵 Costo Total", "🟢 Estado"};
        for (int i = 0; i < encabezados.length; i++) {
            Cell celda = filaEncabezado.createCell(i);
            celda.setCellValue(encabezados[i]);
            celda.setCellStyle(estiloEncabezado);
        }
        
        for (com.ypacarai.cooperativa.activos.model.ReporteMantenimientos item : reportes) {
            Row fila = sheet.createRow(filaInicio++);
            fila.createCell(0).setCellValue(item.getTipoMantenimiento());
            fila.createCell(1).setCellValue(item.getTipoActivo());
            
            Cell celdaTotal = fila.createCell(2);
            celdaTotal.setCellValue(item.getTotalMantenimientos());
            celdaTotal.setCellStyle(estiloNumero);
            
            Cell celdaTiempo = fila.createCell(3);
            celdaTiempo.setCellValue(item.getTiempoPromedioResolucion());
            celdaTiempo.setCellStyle(estiloNumero);
            
            fila.createCell(4).setCellValue(item.getTecnicoAsignado());
            
            Cell celdaCosto = fila.createCell(5);
            celdaCosto.setCellValue(item.getCostoTotal());
            celdaCosto.setCellStyle(estiloNumero);
            
            fila.createCell(6).setCellValue(item.getEstadoMantenimiento());
        }
    }
    
    @SuppressWarnings("unchecked")
    private static void escribirExcelFallas(List<?> datos, XSSFSheet sheet, int filaInicio,
                                            CellStyle estiloEncabezado, CellStyle estiloDatos, 
                                            CellStyle estiloFecha, CellStyle estiloNumero) {
        List<com.ypacarai.cooperativa.activos.model.ReporteFallas> reportes = 
            (List<com.ypacarai.cooperativa.activos.model.ReporteFallas>) datos;
        
        Row filaEncabezado = sheet.createRow(filaInicio++);
        String[] encabezados = {"Tipo Activo", "Número", "Descripción Falla", "Frecuencia", "Efectividad %", "Última Falla"};
        for (int i = 0; i < encabezados.length; i++) {
            Cell celda = filaEncabezado.createCell(i);
            celda.setCellValue(encabezados[i]);
            celda.setCellStyle(estiloEncabezado);
        }
        
        for (com.ypacarai.cooperativa.activos.model.ReporteFallas item : reportes) {
            Row fila = sheet.createRow(filaInicio++);
            fila.createCell(0).setCellValue(item.getTipoActivo());
            fila.createCell(1).setCellValue(item.getNumeroActivo());
            fila.createCell(2).setCellValue(item.getDescripcionFalla());
            
            Cell celdaFrec = fila.createCell(3);
            celdaFrec.setCellValue(item.getFrecuenciaFallas());
            celdaFrec.setCellStyle(estiloNumero);
            
            Cell celdaEfec = fila.createCell(4);
            celdaEfec.setCellValue(item.getEfectividadReparacion());
            celdaEfec.setCellStyle(estiloNumero);
            
            if (item.getFechaUltimaFalla() != null) {
                Cell celdaFecha = fila.createCell(5);
                celdaFecha.setCellValue(item.getFechaUltimaFalla().toString());
                celdaFecha.setCellStyle(estiloFecha);
            }
        }
    }
    
    @SuppressWarnings("unchecked")
    private static void escribirExcelTraslados(List<?> datos, XSSFSheet sheet, int filaInicio,
                                               CellStyle estiloEncabezado, CellStyle estiloDatos, CellStyle estiloNumero) {
        List<com.ypacarai.cooperativa.activos.model.ReporteTraslados> reportes = 
            (List<com.ypacarai.cooperativa.activos.model.ReporteTraslados>) datos;
        
        Row filaEncabezado = sheet.createRow(filaInicio++);
        String[] encabezados = {"🎯 Número", "📋 Tipo", "📍 Origen", "📍 Destino", "🟢 Estado", "📅 Días", "👤 Responsable"};
        for (int i = 0; i < encabezados.length; i++) {
            Cell celda = filaEncabezado.createCell(i);
            celda.setCellValue(encabezados[i]);
            celda.setCellStyle(estiloEncabezado);
        }
        
        for (com.ypacarai.cooperativa.activos.model.ReporteTraslados item : reportes) {
            Row fila = sheet.createRow(filaInicio++);
            fila.createCell(0).setCellValue(item.getNumeroActivo());
            fila.createCell(1).setCellValue(item.getTipoActivo());
            fila.createCell(2).setCellValue(item.getUbicacionOrigen());
            fila.createCell(3).setCellValue(item.getUbicacionDestino());
            fila.createCell(4).setCellValue(item.getEstadoTraslado());
            
            Cell celdaDias = fila.createCell(5);
            celdaDias.setCellValue(item.getDiasEnUbicacion());
            celdaDias.setCellStyle(estiloNumero);
            
            fila.createCell(6).setCellValue(item.getResponsableEnvio());
        }
    }
    
    /**
     * Exporta un reporte a PDF con formato profesional
     */
    public static boolean exportarPDF(ReporteCompleto reporte, JFrame ventanaPadre) {
        try {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Exportar Reporte a PDF");
            
            String nombreArchivo = "reporte_" + reporte.getTipoReporte().toLowerCase().replace(" ", "_") + 
                                  "_" + LocalDateTime.now().format(FORMATO_FECHA_ARCHIVO) + ".pdf";
            fileChooser.setSelectedFile(new File(nombreArchivo));
            
            FileNameExtensionFilter filter = new FileNameExtensionFilter("Archivos PDF (*.pdf)", "pdf");
            fileChooser.setFileFilter(filter);
            
            int resultado = fileChooser.showSaveDialog(ventanaPadre);
            if (resultado == JFileChooser.APPROVE_OPTION) {
                File archivo = fileChooser.getSelectedFile();
                if (!archivo.getName().toLowerCase().endsWith(".pdf")) {
                    archivo = new File(archivo.getAbsolutePath() + ".pdf");
                }
                
                escribirPDF(reporte, archivo);
                
                JOptionPane.showMessageDialog(ventanaPadre, 
                    "Reporte exportado exitosamente a:\n" + archivo.getAbsolutePath(),
                    "Exportación Exitosa", 
                    JOptionPane.INFORMATION_MESSAGE);
                return true;
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(ventanaPadre, 
                "Error exportando reporte a PDF: " + e.getMessage(),
                "Error de Exportación", 
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
        return false;
    }
    
    private static void escribirPDF(ReporteCompleto reporte, File archivo) throws Exception {
        PdfWriter writer = new PdfWriter(archivo);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);
        
        // Fuentes
        PdfFont fontBold = PdfFontFactory.createFont("Helvetica-Bold");
        PdfFont fontNormal = PdfFontFactory.createFont("Helvetica");
        
        // Colores
        DeviceRgb colorAzul = new DeviceRgb(0, 51, 102);
        DeviceRgb colorVerde = new DeviceRgb(0, 102, 51);
        
        // Título principal
        Paragraph titulo = new Paragraph("COOPERATIVA YPACARAÍ LTDA")
            .setFont(fontBold)
            .setFontSize(20)
            .setFontColor(colorAzul)
            .setTextAlignment(TextAlignment.CENTER)
            .setMarginBottom(3);
        document.add(titulo);
        
        Paragraph subtitulo = new Paragraph("SISTEMA DE GESTIÓN DE ACTIVOS")
            .setFont(fontNormal)
            .setFontSize(12)
            .setFontColor(colorAzul)
            .setTextAlignment(TextAlignment.CENTER)
            .setMarginBottom(25);
        document.add(subtitulo);
        
        // Información del reporte
        Paragraph tipoReporte = new Paragraph("📄 Reporte: " + reporte.getTipoReporte())
            .setFont(fontBold)
            .setFontSize(14)
            .setFontColor(colorVerde)
            .setMarginBottom(8);
        document.add(tipoReporte);
        
        Paragraph fechaGen = new Paragraph("📅 Fecha de Generación: " + reporte.getFechaGeneracion())
            .setFont(fontNormal)
            .setFontSize(10)
            .setMarginBottom(5);
        document.add(fechaGen);
        
        Paragraph totalReg = new Paragraph("📊 Total de Registros: " + reporte.getTotalRegistros())
            .setFont(fontNormal)
            .setFontSize(10)
            .setMarginBottom(20);
        document.add(totalReg);
        
        // Línea divisoria
        document.add(new Paragraph("_______________________________________________________________________________")
            .setTextAlignment(TextAlignment.CENTER)
            .setFontColor(colorVerde)
            .setMarginTop(5)
            .setMarginBottom(5));
        
        // Resumen ejecutivo completo
        Paragraph resumenTitulo = new Paragraph("📝 RESUMEN EJECUTIVO")
            .setFont(fontBold)
            .setFontSize(14)
            .setFontColor(colorVerde)
            .setMarginTop(15)
            .setMarginBottom(10);
        document.add(resumenTitulo);
        
        if (reporte.getResumenEjecutivo() != null && !reporte.getResumenEjecutivo().isEmpty()) {
            String[] lineas = reporte.getResumenEjecutivo().split("\n");
            for (String linea : lineas) {
                if (!linea.trim().isEmpty()) {
                    Paragraph resumenLinea = new Paragraph("• " + linea.trim())
                        .setFont(fontNormal)
                        .setFontSize(10)
                        .setMarginLeft(15)
                        .setMarginBottom(5);
                    document.add(resumenLinea);
                }
            }
        } else {
            Paragraph resumenDefault = new Paragraph("Este reporte contiene " + reporte.getTotalRegistros() + 
                " registros correspondientes a " + reporte.getTipoReporte() + ".")
                .setFont(fontNormal)
                .setFontSize(10)
                .setMarginBottom(10);
            document.add(resumenDefault);
        }
        
        // Estadísticas principales expandidas
        if (reporte.getEstadisticas() != null && !reporte.getEstadisticas().isEmpty()) {
            Paragraph estTitulo = new Paragraph("📊 ESTADÍSTICAS PRINCIPALES")
                .setFont(fontBold)
                .setFontSize(14)
                .setFontColor(colorVerde)
                .setMarginTop(20)
                .setMarginBottom(10);
            document.add(estTitulo);
            
            Table tablaEst = new Table(UnitValue.createPercentArray(new float[]{5, 2, 5}));
            tablaEst.setWidth(UnitValue.createPercentValue(100));
            
            // Encabezados de tabla
            DeviceRgb colorEncabezado = new DeviceRgb(0, 102, 51);
            tablaEst.addHeaderCell(new com.itextpdf.layout.element.Cell()
                .add(new Paragraph("Indicador")
                    .setFont(fontBold)
                    .setFontSize(10)
                    .setFontColor(ColorConstants.WHITE))
                .setBackgroundColor(colorEncabezado)
                .setPadding(8)
                .setTextAlignment(TextAlignment.LEFT));
            tablaEst.addHeaderCell(new com.itextpdf.layout.element.Cell()
                .add(new Paragraph("Valor")
                    .setFont(fontBold)
                    .setFontSize(10)
                    .setFontColor(ColorConstants.WHITE))
                .setBackgroundColor(colorEncabezado)
                .setPadding(8)
                .setTextAlignment(TextAlignment.CENTER));
            tablaEst.addHeaderCell(new com.itextpdf.layout.element.Cell()
                .add(new Paragraph("Descripción")
                    .setFont(fontBold)
                    .setFontSize(10)
                    .setFontColor(ColorConstants.WHITE))
                .setBackgroundColor(colorEncabezado)
                .setPadding(8)
                .setTextAlignment(TextAlignment.LEFT));
            
            // Datos con filas alternadas
            int filaNum = 0;
            DeviceRgb colorFila1 = new DeviceRgb(255, 255, 255);
            DeviceRgb colorFila2 = new DeviceRgb(245, 245, 245);
            
            for (Map.Entry<String, Object> stat : reporte.getEstadisticas().entrySet()) {
                // Saltar Maps y Collections - se mostrarán en sección Estado de Activos
                if (stat.getValue() instanceof Map || stat.getValue() instanceof java.util.Collection) {
                    continue;
                }
                
                DeviceRgb colorFondo = (filaNum++ % 2 == 0) ? colorFila1 : colorFila2;
                
                // Celda de nombre
                tablaEst.addCell(new com.itextpdf.layout.element.Cell()
                    .add(new Paragraph(formatearNombreEstadistica(stat.getKey()))
                        .setFont(fontNormal)
                        .setFontSize(9))
                    .setBackgroundColor(colorFondo)
                    .setPadding(6));
                
                // Celda de valor
                String valorFormateado = stat.getValue() instanceof Number 
                    ? formatearNumero(stat.getValue())
                    : stat.getValue().toString();
                tablaEst.addCell(new com.itextpdf.layout.element.Cell()
                    .add(new Paragraph(valorFormateado)
                        .setFont(fontBold)
                        .setFontSize(9))
                    .setBackgroundColor(colorFondo)
                    .setPadding(6)
                    .setTextAlignment(TextAlignment.CENTER));
                
                // Celda de descripción
                tablaEst.addCell(new com.itextpdf.layout.element.Cell()
                    .add(new Paragraph(obtenerDescripcionEstadistica(stat.getKey()))
                        .setFont(fontNormal)
                        .setFontSize(8))
                    .setBackgroundColor(colorFondo)
                    .setPadding(6));
            }
            document.add(tablaEst);
        }
        
        // Estado de activos y alertas
        agregarEstadoActivosPDF(document, reporte, fontBold, fontNormal, colorVerde);
        
        // Datos detallados
        List<?> datos = reporte.getDatosOriginales();
        if (datos != null && !datos.isEmpty()) {
            document.add(new Paragraph("📊 DATOS DETALLADOS")
                .setFont(fontBold)
                .setFontSize(12)
                .setFontColor(colorVerde)
                .setMarginTop(20)
                .setMarginBottom(10));
            
            escribirDatosPDFPorTipo(reporte, document, fontBold, fontNormal, colorVerde);
        }
        
        // Pie de página
        Paragraph pie = new Paragraph("Documento generado automáticamente | " + 
                                     java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) + 
                                     " | Sistema de Gestión de Activos")
            .setFont(fontNormal)
            .setFontSize(8)
            .setTextAlignment(TextAlignment.CENTER)
            .setFontColor(ColorConstants.GRAY)
            .setMarginTop(30);
        document.add(pie);
        
        document.close();
    }
    
    private static void escribirDatosPDFPorTipo(ReporteCompleto reporte, Document document,
                                                 PdfFont fontBold, PdfFont fontNormal, DeviceRgb colorVerde) {
        List<?> datos = reporte.getDatosOriginales();
        String tipoReporte = reporte.getTipoReporte();
        
        switch (tipoReporte) {
            case "Estado de Activos":
                escribirPDFEstadoActivos(datos, document, fontBold, fontNormal);
                break;
            case "Mantenimientos":
                escribirPDFMantenimientos(datos, document, fontBold, fontNormal);
                break;
            case "Fallas":
                escribirPDFFallas(datos, document, fontBold, fontNormal);
                break;
            case "Traslados":
                escribirPDFTraslados(datos, document, fontBold, fontNormal);
                break;
            default:
                document.add(new Paragraph("Tipo de reporte no soportado para PDF"));
        }
    }
    
    @SuppressWarnings("unchecked")
    private static void escribirPDFEstadoActivos(List<?> datos, Document document, PdfFont fontBold, PdfFont fontNormal) {
        List<com.ypacarai.cooperativa.activos.model.ReporteEstadoActivos> reportes = 
            (List<com.ypacarai.cooperativa.activos.model.ReporteEstadoActivos>) datos;
        
        Table tabla = new Table(UnitValue.createPercentArray(new float[]{2.5f, 2f, 1f, 2.5f, 1f, 1f}));
        tabla.setWidth(UnitValue.createPercentValue(100));
        tabla.setMarginTop(10);
        
        // Encabezados con color
        DeviceRgb colorEncabezado = new DeviceRgb(0, 102, 51);
        String[] encabezados = {"Tipo de Activo", "Estado", "Cant.", "Ubicación", "Próx.", "Venc."};
        for (String enc : encabezados) {
            tabla.addHeaderCell(new com.itextpdf.layout.element.Cell()
                .add(new Paragraph(enc).setFont(fontBold).setFontSize(9).setFontColor(ColorConstants.WHITE))
                .setBackgroundColor(colorEncabezado)
                .setPadding(5)
                .setTextAlignment(TextAlignment.CENTER));
        }
        
        // Datos con filas alternadas
        int fila = 0;
        DeviceRgb colorFondo1 = new DeviceRgb(255, 255, 255);
        DeviceRgb colorFondo2 = new DeviceRgb(245, 245, 245);
        
        for (com.ypacarai.cooperativa.activos.model.ReporteEstadoActivos item : reportes) {
            DeviceRgb colorFondo = (fila++ % 2 == 0) ? colorFondo1 : colorFondo2;
            
            tabla.addCell(new com.itextpdf.layout.element.Cell()
                .add(new Paragraph(item.getTipoActivo()).setFont(fontNormal).setFontSize(8))
                .setBackgroundColor(colorFondo).setPadding(4));
            tabla.addCell(new com.itextpdf.layout.element.Cell()
                .add(new Paragraph(item.getEstado()).setFont(fontNormal).setFontSize(8))
                .setBackgroundColor(colorFondo).setPadding(4));
            tabla.addCell(new com.itextpdf.layout.element.Cell()
                .add(new Paragraph(String.valueOf(item.getCantidadTotal())).setFont(fontBold).setFontSize(8))
                .setBackgroundColor(colorFondo).setPadding(4).setTextAlignment(TextAlignment.CENTER));
            tabla.addCell(new com.itextpdf.layout.element.Cell()
                .add(new Paragraph(item.getUbicacion()).setFont(fontNormal).setFontSize(8))
                .setBackgroundColor(colorFondo).setPadding(4));
            tabla.addCell(new com.itextpdf.layout.element.Cell()
                .add(new Paragraph(String.valueOf(item.getActivosProximosMantenimiento())).setFont(fontNormal).setFontSize(8))
                .setBackgroundColor(colorFondo).setPadding(4).setTextAlignment(TextAlignment.CENTER));
            tabla.addCell(new com.itextpdf.layout.element.Cell()
                .add(new Paragraph(String.valueOf(item.getActivosMantenimientoVencido())).setFont(fontNormal).setFontSize(8))
                .setBackgroundColor(colorFondo).setPadding(4).setTextAlignment(TextAlignment.CENTER));
        }
        
        document.add(tabla);
    }
    
    @SuppressWarnings("unchecked")
    private static void escribirPDFMantenimientos(List<?> datos, Document document, PdfFont fontBold, PdfFont fontNormal) {
        List<com.ypacarai.cooperativa.activos.model.ReporteMantenimientos> reportes = 
            (List<com.ypacarai.cooperativa.activos.model.ReporteMantenimientos>) datos;
        
        Table tabla = new Table(UnitValue.createPercentArray(new float[]{2f, 2f, 1f, 1f, 2.5f, 1f, 2f}));
        tabla.setWidth(UnitValue.createPercentValue(100));
        tabla.setMarginTop(10);
        
        DeviceRgb colorEncabezado = new DeviceRgb(0, 102, 51);
        String[] encabezados = {"Tipo Mant.", "Tipo Activo", "Total", "Tiemp.", "Técnico", "Costo", "Estado"};
        for (String enc : encabezados) {
            tabla.addHeaderCell(new com.itextpdf.layout.element.Cell()
                .add(new Paragraph(enc).setFont(fontBold).setFontSize(9).setFontColor(ColorConstants.WHITE))
                .setBackgroundColor(colorEncabezado)
                .setPadding(5)
                .setTextAlignment(TextAlignment.CENTER));
        }
        
        int fila = 0;
        DeviceRgb colorFondo1 = new DeviceRgb(255, 255, 255);
        DeviceRgb colorFondo2 = new DeviceRgb(245, 245, 245);
        
        for (com.ypacarai.cooperativa.activos.model.ReporteMantenimientos item : reportes) {
            DeviceRgb colorFondo = (fila++ % 2 == 0) ? colorFondo1 : colorFondo2;
            
            tabla.addCell(new com.itextpdf.layout.element.Cell()
                .add(new Paragraph(item.getTipoMantenimiento()).setFont(fontNormal).setFontSize(8))
                .setBackgroundColor(colorFondo).setPadding(4));
            tabla.addCell(new com.itextpdf.layout.element.Cell()
                .add(new Paragraph(item.getTipoActivo()).setFont(fontNormal).setFontSize(8))
                .setBackgroundColor(colorFondo).setPadding(4));
            tabla.addCell(new com.itextpdf.layout.element.Cell()
                .add(new Paragraph(String.valueOf(item.getTotalMantenimientos())).setFont(fontBold).setFontSize(8))
                .setBackgroundColor(colorFondo).setPadding(4).setTextAlignment(TextAlignment.CENTER));
            tabla.addCell(new com.itextpdf.layout.element.Cell()
                .add(new Paragraph(String.format("%.1f", item.getTiempoPromedioResolucion())).setFont(fontNormal).setFontSize(8))
                .setBackgroundColor(colorFondo).setPadding(4).setTextAlignment(TextAlignment.CENTER));
            tabla.addCell(new com.itextpdf.layout.element.Cell()
                .add(new Paragraph(item.getTecnicoAsignado()).setFont(fontNormal).setFontSize(8))
                .setBackgroundColor(colorFondo).setPadding(4));
            tabla.addCell(new com.itextpdf.layout.element.Cell()
                .add(new Paragraph(String.format("%.2f", item.getCostoTotal())).setFont(fontNormal).setFontSize(8))
                .setBackgroundColor(colorFondo).setPadding(4).setTextAlignment(TextAlignment.RIGHT));
            tabla.addCell(new com.itextpdf.layout.element.Cell()
                .add(new Paragraph(item.getEstadoMantenimiento()).setFont(fontNormal).setFontSize(8))
                .setBackgroundColor(colorFondo).setPadding(4));
        }
        
        document.add(tabla);
    }
    
    @SuppressWarnings("unchecked")
    private static void escribirPDFFallas(List<?> datos, Document document, PdfFont fontBold, PdfFont fontNormal) {
        List<com.ypacarai.cooperativa.activos.model.ReporteFallas> reportes = 
            (List<com.ypacarai.cooperativa.activos.model.ReporteFallas>) datos;
        
        Table tabla = new Table(UnitValue.createPercentArray(new float[]{1.5f, 1.5f, 3f, 1f, 1f, 2f}));
        tabla.setWidth(UnitValue.createPercentValue(100));
        tabla.setMarginTop(10);
        
        DeviceRgb colorEncabezado = new DeviceRgb(0, 102, 51);
        String[] encabezados = {"Tipo", "Número", "Descripción", "Frec.", "Efec.%", "Última Falla"};
        for (String enc : encabezados) {
            tabla.addHeaderCell(new com.itextpdf.layout.element.Cell()
                .add(new Paragraph(enc).setFont(fontBold).setFontSize(9).setFontColor(ColorConstants.WHITE))
                .setBackgroundColor(colorEncabezado)
                .setPadding(5)
                .setTextAlignment(TextAlignment.CENTER));
        }
        
        int fila = 0;
        DeviceRgb colorFondo1 = new DeviceRgb(255, 255, 255);
        DeviceRgb colorFondo2 = new DeviceRgb(245, 245, 245);
        
        for (com.ypacarai.cooperativa.activos.model.ReporteFallas item : reportes) {
            DeviceRgb colorFondo = (fila++ % 2 == 0) ? colorFondo1 : colorFondo2;
            
            tabla.addCell(new com.itextpdf.layout.element.Cell()
                .add(new Paragraph(item.getTipoActivo()).setFont(fontNormal).setFontSize(8))
                .setBackgroundColor(colorFondo).setPadding(4));
            tabla.addCell(new com.itextpdf.layout.element.Cell()
                .add(new Paragraph(item.getNumeroActivo()).setFont(fontNormal).setFontSize(8))
                .setBackgroundColor(colorFondo).setPadding(4));
            tabla.addCell(new com.itextpdf.layout.element.Cell()
                .add(new Paragraph(item.getDescripcionFalla()).setFont(fontNormal).setFontSize(8))
                .setBackgroundColor(colorFondo).setPadding(4));
            tabla.addCell(new com.itextpdf.layout.element.Cell()
                .add(new Paragraph(String.valueOf(item.getFrecuenciaFallas())).setFont(fontBold).setFontSize(8))
                .setBackgroundColor(colorFondo).setPadding(4).setTextAlignment(TextAlignment.CENTER));
            tabla.addCell(new com.itextpdf.layout.element.Cell()
                .add(new Paragraph(String.format("%.1f", item.getEfectividadReparacion())).setFont(fontNormal).setFontSize(8))
                .setBackgroundColor(colorFondo).setPadding(4).setTextAlignment(TextAlignment.CENTER));
            String fecha = item.getFechaUltimaFalla() != null ? item.getFechaUltimaFalla().toString() : "N/A";
            tabla.addCell(new com.itextpdf.layout.element.Cell()
                .add(new Paragraph(fecha).setFont(fontNormal).setFontSize(8))
                .setBackgroundColor(colorFondo).setPadding(4).setTextAlignment(TextAlignment.CENTER));
        }
        
        document.add(tabla);
    }
    
    @SuppressWarnings("unchecked")
    private static void escribirPDFTraslados(List<?> datos, Document document, PdfFont fontBold, PdfFont fontNormal) {
        List<com.ypacarai.cooperativa.activos.model.ReporteTraslados> reportes = 
            (List<com.ypacarai.cooperativa.activos.model.ReporteTraslados>) datos;
        
        Table tabla = new Table(UnitValue.createPercentArray(new float[]{1.5f, 2f, 2.5f, 2.5f, 1.5f, 1f, 2f}));
        tabla.setWidth(UnitValue.createPercentValue(100));
        tabla.setMarginTop(10);
        
        DeviceRgb colorEncabezado = new DeviceRgb(0, 102, 51);
        String[] encabezados = {"Número", "Tipo", "Origen", "Destino", "Estado", "Días", "Responsable"};
        for (String enc : encabezados) {
            tabla.addHeaderCell(new com.itextpdf.layout.element.Cell()
                .add(new Paragraph(enc).setFont(fontBold).setFontSize(9).setFontColor(ColorConstants.WHITE))
                .setBackgroundColor(colorEncabezado)
                .setPadding(5)
                .setTextAlignment(TextAlignment.CENTER));
        }
        
        int fila = 0;
        DeviceRgb colorFondo1 = new DeviceRgb(255, 255, 255);
        DeviceRgb colorFondo2 = new DeviceRgb(245, 245, 245);
        
        for (com.ypacarai.cooperativa.activos.model.ReporteTraslados item : reportes) {
            DeviceRgb colorFondo = (fila++ % 2 == 0) ? colorFondo1 : colorFondo2;
            
            tabla.addCell(new com.itextpdf.layout.element.Cell()
                .add(new Paragraph(item.getNumeroActivo()).setFont(fontNormal).setFontSize(8))
                .setBackgroundColor(colorFondo).setPadding(4));
            tabla.addCell(new com.itextpdf.layout.element.Cell()
                .add(new Paragraph(item.getTipoActivo()).setFont(fontNormal).setFontSize(8))
                .setBackgroundColor(colorFondo).setPadding(4));
            tabla.addCell(new com.itextpdf.layout.element.Cell()
                .add(new Paragraph(item.getUbicacionOrigen()).setFont(fontNormal).setFontSize(8))
                .setBackgroundColor(colorFondo).setPadding(4));
            tabla.addCell(new com.itextpdf.layout.element.Cell()
                .add(new Paragraph(item.getUbicacionDestino()).setFont(fontNormal).setFontSize(8))
                .setBackgroundColor(colorFondo).setPadding(4));
            tabla.addCell(new com.itextpdf.layout.element.Cell()
                .add(new Paragraph(item.getEstadoTraslado()).setFont(fontNormal).setFontSize(8))
                .setBackgroundColor(colorFondo).setPadding(4).setTextAlignment(TextAlignment.CENTER));
            tabla.addCell(new com.itextpdf.layout.element.Cell()
                .add(new Paragraph(String.valueOf(item.getDiasEnUbicacion())).setFont(fontBold).setFontSize(8))
                .setBackgroundColor(colorFondo).setPadding(4).setTextAlignment(TextAlignment.CENTER));
            tabla.addCell(new com.itextpdf.layout.element.Cell()
                .add(new Paragraph(item.getResponsableEnvio()).setFont(fontNormal).setFontSize(8))
                .setBackgroundColor(colorFondo).setPadding(4));
        }
        
        document.add(tabla);
    }
    
    private static void agregarEstadoActivosPDF(Document document, ReporteCompleto reporte,
                                                PdfFont fontBold, PdfFont fontNormal, DeviceRgb colorVerde) throws Exception {
        // Estado de activos
        Paragraph estadoTitulo = new Paragraph("🏢 ESTADO DE ACTIVOS")
            .setFont(fontBold)
            .setFontSize(14)
            .setFontColor(colorVerde)
            .setMarginTop(20)
            .setMarginBottom(10);
        document.add(estadoTitulo);
        
        // Distribuciones
        Map<String, Integer> distEstado = calcularDistribucionEstado(reporte);
        Map<String, Integer> distTipo = calcularDistribucionTipo(reporte);
        Map<String, Integer> distUbicacion = calcularDistribucionUbicacion(reporte);
        
        if (!distEstado.isEmpty() || !distTipo.isEmpty() || !distUbicacion.isEmpty()) {
            // Tabla con 3 columnas para las tres distribuciones
            Table tablaDistribucion = new Table(UnitValue.createPercentArray(new float[]{1, 1, 1}));
            tablaDistribucion.setWidth(UnitValue.createPercentValue(100));
            
            // Columna 1: Por Estado
            if (!distEstado.isEmpty()) {
                com.itextpdf.layout.element.Cell celdaEstado = new com.itextpdf.layout.element.Cell();
                celdaEstado.add(new Paragraph("🟢 Distribución por Estado")
                    .setFont(fontBold)
                    .setFontSize(10)
                    .setMarginBottom(5));
                
                int totalEst = distEstado.values().stream().mapToInt(Integer::intValue).sum();
                for (Map.Entry<String, Integer> entry : distEstado.entrySet()) {
                    double porcentaje = totalEst > 0 ? (entry.getValue() * 100.0 / totalEst) : 0;
                    celdaEstado.add(new Paragraph(String.format("• %s: %d (%.1f%%)", 
                        entry.getKey(), entry.getValue(), porcentaje))
                        .setFont(fontNormal)
                        .setFontSize(9)
                        .setMarginLeft(10));
                }
                tablaDistribucion.addCell(celdaEstado);
            } else {
                tablaDistribucion.addCell(new com.itextpdf.layout.element.Cell()
                    .add(new Paragraph("No disponible").setFont(fontNormal).setFontSize(9)));
            }
            
            // Columna 2: Por Tipo
            if (!distTipo.isEmpty()) {
                com.itextpdf.layout.element.Cell celdaTipo = new com.itextpdf.layout.element.Cell();
                celdaTipo.add(new Paragraph("🔧 Distribución por Tipo")
                    .setFont(fontBold)
                    .setFontSize(10)
                    .setMarginBottom(5));
                
                for (Map.Entry<String, Integer> entry : distTipo.entrySet()) {
                    celdaTipo.add(new Paragraph(String.format("• %s: %d", entry.getKey(), entry.getValue()))
                        .setFont(fontNormal)
                        .setFontSize(9)
                        .setMarginLeft(10));
                }
                tablaDistribucion.addCell(celdaTipo);
            } else {
                tablaDistribucion.addCell(new com.itextpdf.layout.element.Cell()
                    .add(new Paragraph("No disponible").setFont(fontNormal).setFontSize(9)));
            }
            
            // Columna 3: Por Ubicación
            if (!distUbicacion.isEmpty()) {
                com.itextpdf.layout.element.Cell celdaUbicacion = new com.itextpdf.layout.element.Cell();
                celdaUbicacion.add(new Paragraph("📍 Distribución por Ubicación")
                    .setFont(fontBold)
                    .setFontSize(10)
                    .setMarginBottom(5));
                
                for (Map.Entry<String, Integer> entry : distUbicacion.entrySet()) {
                    celdaUbicacion.add(new Paragraph(String.format("• %s: %d", entry.getKey(), entry.getValue()))
                        .setFont(fontNormal)
                        .setFontSize(9)
                        .setMarginLeft(10));
                }
                tablaDistribucion.addCell(celdaUbicacion);
            } else {
                tablaDistribucion.addCell(new com.itextpdf.layout.element.Cell()
                    .add(new Paragraph("No disponible").setFont(fontNormal).setFontSize(9)));
            }
            
            document.add(tablaDistribucion);
        }
        
        // Alertas y recomendaciones
        List<String> alertas = generarAlertas(reporte);
        if (!alertas.isEmpty()) {
            Paragraph alertasTitulo = new Paragraph("⚠️ ALERTAS Y RECOMENDACIONES")
                .setFont(fontBold)
                .setFontSize(12)
                .setFontColor(new DeviceRgb(184, 0, 0))
                .setMarginTop(15)
                .setMarginBottom(8);
            document.add(alertasTitulo);
            
            for (String alerta : alertas) {
                Paragraph alertaParrafo = new Paragraph(alerta)
                    .setFont(fontNormal)
                    .setFontSize(9)
                    .setMarginLeft(15)
                    .setMarginBottom(5);
                document.add(alertaParrafo);
            }
        }
    }
    
    /**
     * Convierte nombres de variables en títulos legibles
     */
    private static String formatearNombreEstadistica(String nombreVariable) {
        // Mapeo de nombres comunes
        Map<String, String> nombresEspeciales = new HashMap<>();
        nombresEspeciales.put("totalActivos", "Total de Activos");
        nombresEspeciales.put("proximosMantenimiento", "Próximos a Mantenimiento");
        nombresEspeciales.put("mantenimientoVencido", "Mantenimiento Vencido");
        nombresEspeciales.put("porcentajeProximoMantenimiento", "% Próximos a Mantenimiento");
        nombresEspeciales.put("porcentajeMantenimientoVencido", "% Mantenimiento Vencido");
        nombresEspeciales.put("distribucionPorUbicacion", "Distribución por Ubicación");
        nombresEspeciales.put("distribucionPorTipo", "Distribución por Tipo");
        nombresEspeciales.put("distribucionPorEstado", "Distribución por Estado");
        nombresEspeciales.put("activosOperativos", "Activos Operativos");
        nombresEspeciales.put("activosFueraServicio", "Activos Fuera de Servicio");
        nombresEspeciales.put("activosEnMantenimiento", "Activos en Mantenimiento");
        nombresEspeciales.put("costoTotalMantenimiento", "Costo Total de Mantenimiento");
        nombresEspeciales.put("tiempoPromedioResolucion", "Tiempo Promedio de Resolución");
        nombresEspeciales.put("totalMantenimientos", "Total de Mantenimientos");
        nombresEspeciales.put("totalFallas", "Total de Fallas");
        nombresEspeciales.put("efectividadPromedio", "Efectividad Promedio");
        
        // Si existe en el mapeo, retornar el nombre especial
        if (nombresEspeciales.containsKey(nombreVariable)) {
            return nombresEspeciales.get(nombreVariable);
        }
        
        // Si no, convertir camelCase a Título Legible
        StringBuilder resultado = new StringBuilder();
        for (int i = 0; i < nombreVariable.length(); i++) {
            char c = nombreVariable.charAt(i);
            if (i == 0) {
                resultado.append(Character.toUpperCase(c));
            } else if (Character.isUpperCase(c)) {
                resultado.append(" ").append(c);
            } else {
                resultado.append(c);
            }
        }
        return resultado.toString();
    }
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
