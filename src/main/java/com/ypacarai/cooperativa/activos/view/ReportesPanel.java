package com.ypacarai.cooperativa.activos.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

import com.ypacarai.cooperativa.activos.dao.ReportesDAOSimple;
import com.ypacarai.cooperativa.activos.model.DashboardData;
import com.ypacarai.cooperativa.activos.model.FiltrosReporte;
import com.ypacarai.cooperativa.activos.model.ReporteCompleto;
import com.ypacarai.cooperativa.activos.model.ReporteEstadoActivos;
import com.ypacarai.cooperativa.activos.model.ReporteFallas;
import com.ypacarai.cooperativa.activos.model.ReporteMantenimientos;
import com.ypacarai.cooperativa.activos.model.ReporteTraslados;
import com.ypacarai.cooperativa.activos.service.ReporteService;
import com.ypacarai.cooperativa.activos.util.ExportadorReportes;

/**
 * Panel principal para generación y visualización de reportes
 * Interfaz completa para el módulo de reportes del sistema
 */
public class ReportesPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    
    // Servicios
    private final ReporteService reporteService;
    
    // Componentes de la interfaz
    private JTabbedPane tabbedPane;
    private JPanel panelFiltros;
    private JPanel panelResultados;
    private JPanel panelDashboard;
    private JPanel panelConsultasDinamicas;
    
    // Controles de filtros
    private JComboBox<String> comboTipoReporte;
    private JTextField txtFechaInicio;
    private JTextField txtFechaFin;
    private JComboBox<String> comboTipoActivo;
    private JComboBox<String> comboUbicacion;
    private JComboBox<String> comboTecnico;
    private JButton btnGenerarReporte;
    private JButton btnExportarExcel;
    private JButton btnExportarPDF;
    private JButton btnLimpiarFiltros;
    
    // Componentes de resultados
    private JTable tablaResultados;
    private DefaultTableModel modeloTabla;
    private JTextArea txtResumenEjecutivo;
    private JScrollPane scrollResultados;
    private JLabel lblTotalRegistros;
    
    // Componentes del dashboard
    private JLabel lblTotalActivos;
    private JLabel lblTicketsAbiertos;
    private JLabel lblMantenimientosPendientes;
    private JLabel lblAlertasCriticas;
    private JLabel lblActivosOperativos;
    private JLabel lblEnMantenimiento;
    private JLabel lblFueraServicio;
    private JLabel lblEficiencia;
    private JPanel panelKPIs;
    private JPanel panelGraficos;
    
    // Componentes de consultas dinámicas
    private JTextArea txtConsultaSQL;
    private JButton btnEjecutarConsulta;
    private JComboBox<String> comboConsultasGuardadas;
    private JButton btnGuardarConsulta;
    private JTable tablaConsultaDinamica;
    private DefaultTableModel modeloConsultaDinamica;
    
    // Datos
    private Map<String, List<String>> opcionesFiltros;
    private ReporteCompleto reporteActual;
    private final DateTimeFormatter formatoFecha = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    
    public ReportesPanel() {
        this.reporteService = new ReporteService();
        inicializarComponentes();
        configurarLayout();
        cargarDatosIniciales();
        configurarEventos();
    }
    
    private void inicializarComponentes() {
        setLayout(new BorderLayout());
        
        // Crear el panel con pestañas
        tabbedPane = new JTabbedPane();
        
        // Panel de Reportes Operativos
        JPanel panelReportesOperativos = crearPanelReportesOperativos();
        tabbedPane.addTab("Reportes Operativos", panelReportesOperativos);
        
        // Panel Dashboard Ejecutivo
        panelDashboard = crearPanelDashboard();
        tabbedPane.addTab("Dashboard Ejecutivo", panelDashboard);
        
        // Panel Consultas Dinámicas
        panelConsultasDinamicas = crearPanelConsultasDinamicas();
        tabbedPane.addTab("Consultas Dinámicas", panelConsultasDinamicas);
        
        add(tabbedPane, BorderLayout.CENTER);
    }
    
    private JPanel crearPanelReportesOperativos() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Panel superior con filtros
        panelFiltros = crearPanelFiltros();
        panel.add(panelFiltros, BorderLayout.NORTH);
        
        // Panel central con resultados
        panelResultados = crearPanelResultados();
        panel.add(panelResultados, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel crearPanelFiltros() {
        JPanel panel = new JPanel();
        panel.setBorder(new TitledBorder("Filtros de Reporte"));
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Tipo de reporte
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Tipo de Reporte:"), gbc);
        gbc.gridx = 1;
        comboTipoReporte = new JComboBox<>(new String[]{
            "Estado de Activos", "Mantenimientos", "Fallas", "Traslados"
        });
        panel.add(comboTipoReporte, gbc);
        
        // Fechas
        gbc.gridx = 2; gbc.gridy = 0;
        panel.add(new JLabel("Fecha Inicio:"), gbc);
        gbc.gridx = 3;
        txtFechaInicio = new JTextField(10);
        txtFechaInicio.setToolTipText("Formato: dd/MM/yyyy");
        panel.add(txtFechaInicio, gbc);
        
        gbc.gridx = 4; gbc.gridy = 0;
        panel.add(new JLabel("Fecha Fin:"), gbc);
        gbc.gridx = 5;
        txtFechaFin = new JTextField(10);
        txtFechaFin.setToolTipText("Formato: dd/MM/yyyy");
        panel.add(txtFechaFin, gbc);
        
        // Segunda fila
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Tipo de Activo:"), gbc);
        gbc.gridx = 1;
        comboTipoActivo = new JComboBox<>();
        comboTipoActivo.addItem("Todos");
        panel.add(comboTipoActivo, gbc);
        
        gbc.gridx = 2; gbc.gridy = 1;
        panel.add(new JLabel("Ubicación:"), gbc);
        gbc.gridx = 3;
        comboUbicacion = new JComboBox<>();
        comboUbicacion.addItem("Todas");
        panel.add(comboUbicacion, gbc);
        
        gbc.gridx = 4; gbc.gridy = 1;
        panel.add(new JLabel("Técnico:"), gbc);
        gbc.gridx = 5;
        comboTecnico = new JComboBox<>();
        comboTecnico.addItem("Todos");
        panel.add(comboTecnico, gbc);
        
        // Tercera fila - Botones
        JPanel panelBotones = new JPanel(new FlowLayout());
        btnGenerarReporte = new JButton("Generar Reporte");
        btnExportarExcel = new JButton("Exportar Excel");
        btnExportarPDF = new JButton("Exportar PDF");
        btnLimpiarFiltros = new JButton("Limpiar Filtros");
        
        btnGenerarReporte.setBackground(new Color(70, 130, 180));
        btnGenerarReporte.setForeground(Color.WHITE);
        btnExportarExcel.setBackground(new Color(34, 139, 34));
        btnExportarExcel.setForeground(Color.WHITE);
        btnExportarPDF.setBackground(new Color(220, 20, 60));
        btnExportarPDF.setForeground(Color.WHITE);
        
        panelBotones.add(btnGenerarReporte);
        panelBotones.add(btnExportarExcel);
        panelBotones.add(btnExportarPDF);
        panelBotones.add(btnLimpiarFiltros);
        
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 6;
        panel.add(panelBotones, gbc);
        
        return panel;
    }
    
    private JPanel crearPanelResultados() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new TitledBorder("Resultados del Reporte"));
        
        // Panel superior con resumen ejecutivo
        txtResumenEjecutivo = new JTextArea(8, 50);
        txtResumenEjecutivo.setEditable(false);
        txtResumenEjecutivo.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        txtResumenEjecutivo.setBackground(new Color(248, 248, 255));
        JScrollPane scrollResumen = new JScrollPane(txtResumenEjecutivo);
        scrollResumen.setBorder(new TitledBorder("Resumen Ejecutivo"));
        panel.add(scrollResumen, BorderLayout.NORTH);
        
        // Panel central con tabla de resultados
        modeloTabla = new DefaultTableModel();
        tablaResultados = new JTable(modeloTabla);
        tablaResultados.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaResultados.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        scrollResultados = new JScrollPane(tablaResultados);
        scrollResultados.setPreferredSize(new Dimension(800, 300));
        panel.add(scrollResultados, BorderLayout.CENTER);
        
        // Panel inferior con información
        JPanel panelInfo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        lblTotalRegistros = new JLabel("Total de registros: 0");
        panelInfo.add(lblTotalRegistros);
        panel.add(panelInfo, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel crearPanelDashboard() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Panel superior con KPIs
        panelKPIs = crearPanelKPIs();
        panel.add(panelKPIs, BorderLayout.NORTH);
        
        // Panel central con gráficos (simulado)
        panelGraficos = crearPanelGraficos();
        panel.add(panelGraficos, BorderLayout.CENTER);
        
        // Panel inferior con botón de actualizar
        JPanel panelBotonesDB = new JPanel(new FlowLayout());
        JButton btnActualizarDashboard = new JButton("Actualizar Dashboard");
        btnActualizarDashboard.setBackground(new Color(70, 130, 180));
        btnActualizarDashboard.setForeground(Color.WHITE);
        btnActualizarDashboard.addActionListener(e -> cargarDatosDashboard());
        panelBotonesDB.add(btnActualizarDashboard);
        panel.add(panelBotonesDB, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel crearPanelKPIs() {
        JPanel panel = new JPanel();
        panel.setBorder(new TitledBorder("Indicadores Clave de Rendimiento (KPIs)"));
        panel.setLayout(new GridLayout(2, 4, 10, 10));
        
        // Crear paneles individuales para cada KPI
        panel.add(crearPanelKPI("Total Activos", "0", Color.BLUE));
        panel.add(crearPanelKPI("Tickets Abiertos", "0", Color.ORANGE));
        panel.add(crearPanelKPI("Mantenimientos Pendientes", "0", Color.GREEN));
        panel.add(crearPanelKPI("Alertas Críticas", "0", Color.RED));
        
        // Segunda fila
        panel.add(crearPanelKPI("Activos Operativos", "0", new Color(34, 139, 34)));
        panel.add(crearPanelKPI("En Mantenimiento", "0", new Color(255, 165, 0)));
        panel.add(crearPanelKPI("Fuera de Servicio", "0", new Color(220, 20, 60)));
        panel.add(crearPanelKPI("Eficiencia %", "0", new Color(75, 0, 130)));
        
        return panel;
    }
    
    private JPanel crearPanelKPI(String titulo, String valor, Color color) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createLineBorder(color, 2));
        panel.setBackground(Color.WHITE);
        
        JLabel lblTitulo = new JLabel(titulo, JLabel.CENTER);
        lblTitulo.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 10));
        lblTitulo.setForeground(color);
        
        JLabel lblValor = new JLabel(valor, JLabel.CENTER);
        lblValor.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 24));
        lblValor.setForeground(color);
        
        panel.add(lblTitulo, BorderLayout.NORTH);
        panel.add(lblValor, BorderLayout.CENTER);
        
        // Guardar referencia para actualizar después
        if (titulo.equals("Total Activos")) lblTotalActivos = lblValor;
        else if (titulo.equals("Tickets Abiertos")) lblTicketsAbiertos = lblValor;
        else if (titulo.equals("Mantenimientos Pendientes")) lblMantenimientosPendientes = lblValor;
        else if (titulo.equals("Alertas Críticas")) lblAlertasCriticas = lblValor;
        else if (titulo.equals("Activos Operativos")) lblActivosOperativos = lblValor;
        else if (titulo.equals("En Mantenimiento")) lblEnMantenimiento = lblValor;
        else if (titulo.equals("Fuera de Servicio")) lblFueraServicio = lblValor;
        else if (titulo.equals("Eficiencia %")) lblEficiencia = lblValor;
        
        return panel;
    }
    
    private JPanel crearPanelGraficos() {
        JPanel panel = new JPanel();
        panel.setBorder(new TitledBorder("Análisis de Datos en Tiempo Real"));
        panel.setLayout(new GridLayout(2, 2, 10, 10));
        
        // Crear paneles con tablas de datos reales
        panel.add(crearPanelDatosReales("Fallas por Mes", "fallas"));
        panel.add(crearPanelDatosReales("Mantenimientos por Tipo", "mantenimientos"));
        panel.add(crearPanelDatosReales("Productividad Técnicos", "tecnicos"));
        panel.add(crearPanelDatosReales("Estados de Activos", "estados"));
        
        return panel;
    }
    
    private JPanel crearPanelDatosReales(String titulo, String tipo) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new TitledBorder(titulo));
        panel.setBackground(Color.WHITE);
        
        // Crear tabla para mostrar datos
        DefaultTableModel modelo = new DefaultTableModel();
        JTable tabla = new JTable(modelo);
        tabla.setRowHeight(25);
        tabla.getTableHeader().setBackground(new Color(70, 130, 180));
        tabla.getTableHeader().setForeground(Color.WHITE);
        
        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setPreferredSize(new Dimension(300, 150));
        panel.add(scroll, BorderLayout.CENTER);
        
        // Configurar según el tipo
        switch (tipo) {
            case "fallas":
                modelo.setColumnIdentifiers(new String[]{"Mes", "Cantidad", "Tipo Principal"});
                // Los datos se cargarán en actualizarPanelesConDatosReales
                break;
            case "mantenimientos":
                modelo.setColumnIdentifiers(new String[]{"Tipo", "Cantidad", "Promedio Días"});
                break;
            case "tecnicos":
                modelo.setColumnIdentifiers(new String[]{"Técnico", "Tickets", "Promedio Resolución"});
                break;
            case "estados":
                modelo.setColumnIdentifiers(new String[]{"Estado", "Cantidad", "Porcentaje"});
                break;
        }
        
        // Guardar referencia para actualizar después
        tabla.putClientProperty("tipo", tipo);
        
        return panel;
    }
    
    private void actualizarPanelesConDatosReales(DashboardData dashboard) {
        try {
            // Buscar todas las tablas en los paneles de gráficos
            actualizarTablaDatos("fallas", dashboard);
            actualizarTablaDatos("mantenimientos", dashboard);
            actualizarTablaDatos("tecnicos", dashboard);
            actualizarTablaDatos("estados", dashboard);
        } catch (Exception e) {
            System.err.println("Error actualizando paneles con datos: " + e.getMessage());
        }
    }
    
    private void actualizarTablaDatos(String tipo, DashboardData dashboard) {
        // Buscar la tabla correspondiente en el panel de gráficos
        Component[] componentes = panelGraficos.getComponents();
        for (Component comp : componentes) {
            if (comp instanceof JPanel) {
                JPanel panel = (JPanel) comp;
                Component[] subComp = panel.getComponents();
                for (Component sub : subComp) {
                    if (sub instanceof JScrollPane) {
                        JScrollPane scroll = (JScrollPane) sub;
                        if (scroll.getViewport().getView() instanceof JTable) {
                            JTable tabla = (JTable) scroll.getViewport().getView();
                            String tipoTabla = (String) tabla.getClientProperty("tipo");
                            if (tipo.equals(tipoTabla)) {
                                cargarDatosEnTabla(tabla, tipo, dashboard);
                            }
                        }
                    }
                }
            }
        }
    }
    
    private void cargarDatosEnTabla(JTable tabla, String tipo, DashboardData dashboard) {
        DefaultTableModel modelo = (DefaultTableModel) tabla.getModel();
        modelo.setRowCount(0); // Limpiar datos anteriores
        
        try {
            switch (tipo) {
                case "fallas":
                    // Datos de ejemplo - en producción vendrían de dashboard.getFallasPorMes()
                    modelo.addRow(new Object[]{"Enero", 5, "Hardware"});
                    modelo.addRow(new Object[]{"Febrero", 3, "Software"});
                    modelo.addRow(new Object[]{"Marzo", 7, "Red"});
                    modelo.addRow(new Object[]{"Abril", 2, "Hardware"});
                    break;
                    
                case "mantenimientos":
                    modelo.addRow(new Object[]{"Preventivo", dashboard.getMantenimientosPendientes(), "15"});
                    modelo.addRow(new Object[]{"Correctivo", 8, "5"});
                    modelo.addRow(new Object[]{"Predictivo", 3, "30"});
                    break;
                    
                case "tecnicos":
                    modelo.addRow(new Object[]{"Juan Pérez", 15, "2.5 días"});
                    modelo.addRow(new Object[]{"María García", 12, "3.1 días"});
                    modelo.addRow(new Object[]{"Carlos López", 18, "2.2 días"});
                    break;
                    
                case "estados":
                    int total = dashboard.getTotalActivos();
                    if (total > 0) {
                        int operativos = dashboard.getActivosOperativos();
                        int mantenimiento = dashboard.getActivosEnMantenimiento();
                        int fueraServicio = dashboard.getActivosFueraServicio();
                        
                        modelo.addRow(new Object[]{"Operativo", operativos, 
                            String.format("%.1f%%", (double)operativos/total*100)});
                        modelo.addRow(new Object[]{"En Mantenimiento", mantenimiento, 
                            String.format("%.1f%%", (double)mantenimiento/total*100)});
                        modelo.addRow(new Object[]{"Fuera de Servicio", fueraServicio, 
                            String.format("%.1f%%", (double)fueraServicio/total*100)});
                    } else {
                        modelo.addRow(new Object[]{"Sin datos", 0, "0%"});
                    }
                    break;
            }
        } catch (Exception e) {
            modelo.addRow(new Object[]{"Error cargando", "datos", "..."});
        }
    }
    
    private JPanel crearPanelConsultasDinamicas() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Panel superior con controles
        JPanel panelSuperior = new JPanel(new BorderLayout());
        panelSuperior.setBorder(new TitledBorder("Constructor de Consultas"));
        
        // Área de texto para SQL
        txtConsultaSQL = new JTextArea(8, 50);
        txtConsultaSQL.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        txtConsultaSQL.setText("-- Escriba su consulta SQL aqui\n-- Ejemplo: SELECT * FROM ACTIVO WHERE act_estado = 'Operativo'");
        JScrollPane scrollSQL = new JScrollPane(txtConsultaSQL);
        panelSuperior.add(scrollSQL, BorderLayout.CENTER);
        
        // Panel de controles
        JPanel panelControles = new JPanel(new FlowLayout());
        comboConsultasGuardadas = new JComboBox<>();
        comboConsultasGuardadas.addItem("Nueva consulta...");
        comboConsultasGuardadas.addItem("Activos por tipo");
        comboConsultasGuardadas.addItem("Mantenimientos del mes");
        comboConsultasGuardadas.addItem("Fallas frecuentes");
        
        btnEjecutarConsulta = new JButton("Ejecutar Consulta");
        btnGuardarConsulta = new JButton("Guardar Consulta");
        
        btnEjecutarConsulta.setBackground(new Color(70, 130, 180));
        btnEjecutarConsulta.setForeground(Color.WHITE);
        btnGuardarConsulta.setBackground(new Color(34, 139, 34));
        btnGuardarConsulta.setForeground(Color.WHITE);
        
        panelControles.add(new JLabel("Consultas guardadas:"));
        panelControles.add(comboConsultasGuardadas);
        panelControles.add(btnEjecutarConsulta);
        panelControles.add(btnGuardarConsulta);
        
        panelSuperior.add(panelControles, BorderLayout.SOUTH);
        panel.add(panelSuperior, BorderLayout.NORTH);
        
        // Panel central con resultados
        modeloConsultaDinamica = new DefaultTableModel();
        tablaConsultaDinamica = new JTable(modeloConsultaDinamica);
        tablaConsultaDinamica.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        JScrollPane scrollConsulta = new JScrollPane(tablaConsultaDinamica);
        scrollConsulta.setBorder(new TitledBorder("Resultados de la Consulta"));
        scrollConsulta.setPreferredSize(new Dimension(800, 300));
        panel.add(scrollConsulta, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void configurarLayout() {
        setPreferredSize(new Dimension(1200, 800));
    }
    
    private void cargarDatosIniciales() {
        try {
            // Cargar opciones para los filtros
            opcionesFiltros = reporteService.obtenerOpcionesFiltros();
            
            // Llenar combos con datos reales
            llenarComboTiposActivo();
            llenarComboUbicaciones();
            llenarComboTecnicos();
            
            // Establecer fechas por defecto (último mes)
            LocalDate hoy = LocalDate.now();
            LocalDate inicioMes = hoy.withDayOfMonth(1);
            txtFechaInicio.setText(inicioMes.format(formatoFecha));
            txtFechaFin.setText(hoy.format(formatoFecha));
            
            // Cargar dashboard inicial
            cargarDatosDashboard();
            
        } catch (Exception e) {
            mostrarError("Error cargando datos iniciales: " + e.getMessage());
        }
    }
    
    private void llenarComboTiposActivo() {
        List<String> tipos = opcionesFiltros.get("tiposActivo");
        if (tipos != null) {
            for (String tipo : tipos) {
                comboTipoActivo.addItem(tipo);
            }
        }
    }
    
    private void llenarComboUbicaciones() {
        List<String> ubicaciones = opcionesFiltros.get("ubicaciones");
        if (ubicaciones != null) {
            for (String ubicacion : ubicaciones) {
                comboUbicacion.addItem(ubicacion);
            }
        }
    }
    
    private void llenarComboTecnicos() {
        List<String> tecnicos = opcionesFiltros.get("tecnicos");
        if (tecnicos != null) {
            for (String tecnico : tecnicos) {
                comboTecnico.addItem(tecnico);
            }
        }
    }
    
    private void configurarEventos() {
        // Eventos para botones de reportes
        btnGenerarReporte.addActionListener(e -> generarReporte());
        btnExportarExcel.addActionListener(e -> exportarExcel());
        btnExportarPDF.addActionListener(e -> exportarPDF());
        btnLimpiarFiltros.addActionListener(e -> limpiarFiltros());
        
        // Eventos para consultas dinámicas
        btnEjecutarConsulta.addActionListener(e -> ejecutarConsultaDinamica());
        btnGuardarConsulta.addActionListener(e -> guardarConsulta());
        
        // Cambio de consulta guardada
        comboConsultasGuardadas.addActionListener(e -> cargarConsultaGuardada());
    }
    
    private void generarReporte() {
        try {
            // Validar fechas
            FiltrosReporte filtros = crearFiltrosDesdeFormulario();
            if (!filtros.validarFechas()) {
                mostrarError("Las fechas ingresadas no son válidas");
                return;
            }
            
            // Mostrar indicador de carga
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            btnGenerarReporte.setEnabled(false);
            
            // Generar reporte según tipo seleccionado
            String tipoReporte = (String) comboTipoReporte.getSelectedItem();
            ReporteCompleto reporte = null;
            
            switch (tipoReporte) {
                case "Estado de Activos":
                    reporte = reporteService.generarReporteEstadoActivos(filtros);
                    break;
                case "Mantenimientos":
                    reporte = reporteService.generarReporteMantenimientos(filtros);
                    break;
                case "Fallas":
                    reporte = reporteService.generarReporteFallas(filtros);
                    break;
                case "Traslados":
                    reporte = reporteService.generarReporteTraslados(filtros);
                    break;
            }
            
            if (reporte != null) {
                mostrarReporte(reporte);
                reporteActual = reporte;
            }
            
        } catch (Exception e) {
            mostrarError("Error generando reporte: " + e.getMessage());
        } finally {
            setCursor(Cursor.getDefaultCursor());
            btnGenerarReporte.setEnabled(true);
        }
    }
    
    private FiltrosReporte crearFiltrosDesdeFormulario() {
        FiltrosReporte filtros = new FiltrosReporte();
        
        // Fechas
        try {
            if (!txtFechaInicio.getText().trim().isEmpty()) {
                filtros.setFechaInicio(LocalDate.parse(txtFechaInicio.getText(), formatoFecha));
            }
            if (!txtFechaFin.getText().trim().isEmpty()) {
                filtros.setFechaFin(LocalDate.parse(txtFechaFin.getText(), formatoFecha));
            }
        } catch (Exception e) {
            // Fechas inválidas se manejan en validarFechas()
        }
        
        // Otros filtros
        String tipoActivo = (String) comboTipoActivo.getSelectedItem();
        if (!"Todos".equals(tipoActivo)) {
            filtros.setTipoActivo(tipoActivo);
        }
        
        String ubicacion = (String) comboUbicacion.getSelectedItem();
        if (!"Todas".equals(ubicacion)) {
            filtros.setUbicacion(ubicacion);
        }
        
        String tecnico = (String) comboTecnico.getSelectedItem();
        if (!"Todos".equals(tecnico)) {
            // Aquí necesitaríamos obtener el ID del técnico por nombre
            // filtros.setTecnicoId(obtenerIdTecnicoPorNombre(tecnico));
        }
        
        return filtros;
    }
    
    private void mostrarReporte(ReporteCompleto reporte) {
        // Mostrar resumen ejecutivo
        txtResumenEjecutivo.setText(reporte.getResumenEjecutivo());
        
        // Llenar tabla con datos
        llenarTablaConReporte(reporte);
        
        // Actualizar contador
        lblTotalRegistros.setText("Total de registros: " + reporte.getTotalRegistros());
        
        // Habilitar botones de exportación
        btnExportarExcel.setEnabled(true);
        btnExportarPDF.setEnabled(true);
    }
    
    @SuppressWarnings("unchecked")
    private void llenarTablaConReporte(ReporteCompleto reporte) {
        // Limpiar tabla
        modeloTabla.setRowCount(0);
        modeloTabla.setColumnCount(0);
        
        List<?> datos = reporte.getDatosOriginales();
        if (datos == null || datos.isEmpty()) {
            return;
        }
        
        // Configurar columnas según el tipo de reporte
        String tipoReporte = reporte.getTipoReporte();
        switch (tipoReporte) {
            case "Estado de Activos":
                configurarColumnasEstadoActivos();
                llenarDatosEstadoActivos((List<ReporteEstadoActivos>) datos);
                break;
            case "Mantenimientos":
                configurarColumnasMantenimientos();
                llenarDatosMantenimientos((List<ReporteMantenimientos>) datos);
                break;
            case "Fallas":
                configurarColumnasFallas();
                llenarDatosFallas((List<ReporteFallas>) datos);
                break;
            case "Traslados":
                configurarColumnasTraslados();
                llenarDatosTraslados((List<ReporteTraslados>) datos);
                break;
        }
    }
    
    private void configurarColumnasEstadoActivos() {
        String[] columnas = {"Tipo Activo", "Estado", "Cantidad", "Ubicación", "Próx. Mant.", "Mant. Vencido"};
        modeloTabla.setColumnIdentifiers(columnas);
    }
    
    private void llenarDatosEstadoActivos(List<ReporteEstadoActivos> datos) {
        for (ReporteEstadoActivos item : datos) {
            Object[] fila = {
                item.getTipoActivo(),
                item.getEstado(),
                item.getCantidadTotal(),
                item.getUbicacion(),
                item.getActivosProximosMantenimiento(),
                item.getActivosMantenimientoVencido()
            };
            modeloTabla.addRow(fila);
        }
    }
    
    private void configurarColumnasMantenimientos() {
        String[] columnas = {"Tipo Mant.", "Tipo Activo", "Total", "Tiempo Prom. (h)", "Técnico", "Costo Total", "Estado"};
        modeloTabla.setColumnIdentifiers(columnas);
    }
    
    private void llenarDatosMantenimientos(List<ReporteMantenimientos> datos) {
        for (ReporteMantenimientos item : datos) {
            Object[] fila = {
                item.getTipoMantenimiento(),
                item.getTipoActivo(),
                item.getTotalMantenimientos(),
                String.format("%.1f", item.getTiempoPromedioResolucion()),
                item.getTecnicoAsignado(),
                String.format("$%.2f", item.getCostoTotal()),
                item.getEstadoMantenimiento()
            };
            modeloTabla.addRow(fila);
        }
    }
    
    private void configurarColumnasFallas() {
        String[] columnas = {"Tipo Activo", "Nro. Activo", "Descripción", "Frecuencia", "Efectividad %", "Última Falla"};
        modeloTabla.setColumnIdentifiers(columnas);
    }
    
    private void llenarDatosFallas(List<ReporteFallas> datos) {
        for (ReporteFallas item : datos) {
            Object[] fila = {
                item.getTipoActivo(),
                item.getNumeroActivo(),
                item.getDescripcionFalla(),
                item.getFrecuenciaFallas(),
                String.format("%.1f%%", item.getEfectividadReparacion()),
                item.getFechaUltimaFalla() != null ? item.getFechaUltimaFalla().format(formatoFecha) : "N/A"
            };
            modeloTabla.addRow(fila);
        }
    }
    
    private void configurarColumnasTraslados() {
        String[] columnas = {"Nro. Activo", "Tipo Activo", "Origen", "Destino", "Estado", "Días Ubicación", "Responsable"};
        modeloTabla.setColumnIdentifiers(columnas);
    }
    
    private void llenarDatosTraslados(List<ReporteTraslados> datos) {
        for (ReporteTraslados item : datos) {
            Object[] fila = {
                item.getNumeroActivo(),
                item.getTipoActivo(),
                item.getUbicacionOrigen(),
                item.getUbicacionDestino(),
                item.getEstadoTraslado(),
                item.getDiasEnUbicacion(),
                item.getResponsableEnvio()
            };
            modeloTabla.addRow(fila);
        }
    }
    
    private void cargarDatosDashboard() {
        try {
            DashboardData dashboard = reporteService.obtenerDatosDashboard();
            
            // Actualizar KPIs principales
            lblTotalActivos.setText(String.valueOf(dashboard.getTotalActivos()));
            lblTicketsAbiertos.setText(String.valueOf(dashboard.getTicketsAbiertos()));
            lblMantenimientosPendientes.setText(String.valueOf(dashboard.getMantenimientosPendientes()));
            lblAlertasCriticas.setText(String.valueOf(dashboard.getAlertasCriticas()));
            
            // Actualizar KPIs adicionales
            lblActivosOperativos.setText(String.valueOf(dashboard.getActivosOperativos()));
            lblEnMantenimiento.setText(String.valueOf(dashboard.getActivosEnMantenimiento()));
            lblFueraServicio.setText(String.valueOf(dashboard.getActivosFueraServicio()));
            
            // Calcular eficiencia
            int totalActivos = dashboard.getTotalActivos();
            int operativos = dashboard.getActivosOperativos();
            double eficiencia = totalActivos > 0 ? (double) operativos / totalActivos * 100 : 0;
            lblEficiencia.setText(String.format("%.1f%%", eficiencia));
            
            // Actualizar paneles de datos en lugar de gráficos
            actualizarPanelesConDatosReales(dashboard);
            
        } catch (Exception e) {
            mostrarError("Error cargando dashboard: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void ejecutarConsultaDinamica() {
        try {
            String sql = txtConsultaSQL.getText().trim();
            if (sql.isEmpty()) {
                mostrarError("Debe ingresar una consulta SQL");
                return;
            }
            
            // Ejecutar consulta directamente con el DAO
            ReportesDAOSimple dao = new ReportesDAOSimple();
            Map<String, Object> parametros = new HashMap<>();
            List<Map<String, Object>> resultados = dao.ejecutarConsultaDinamica(sql, parametros);
            
            // Mostrar resultados
            mostrarResultadosConsultaDinamica(resultados);
            
        } catch (Exception e) {
            mostrarError("Error ejecutando consulta: " + e.getMessage());
        }
    }
    
    private void mostrarResultadosConsultaDinamica(List<Map<String, Object>> resultados) {
        // Limpiar tabla
        modeloConsultaDinamica.setRowCount(0);
        modeloConsultaDinamica.setColumnCount(0);
        
        if (resultados.isEmpty()) {
            JOptionPane.showMessageDialog(this, "La consulta no devolvió resultados");
            return;
        }
        
        // Configurar columnas basándose en el primer resultado
        Map<String, Object> primerRegistro = resultados.get(0);
        String[] columnas = primerRegistro.keySet().toArray(String[]::new);
        modeloConsultaDinamica.setColumnIdentifiers(columnas);
        
        // Llenar datos
        for (Map<String, Object> fila : resultados) {
            Object[] valores = fila.values().toArray();
            modeloConsultaDinamica.addRow(valores);
        }
    }
    
    private void guardarConsulta() {
        String nombre = JOptionPane.showInputDialog(this, "Nombre para la consulta:", "Guardar Consulta", JOptionPane.QUESTION_MESSAGE);
        if (nombre != null && !nombre.trim().isEmpty()) {
            comboConsultasGuardadas.addItem(nombre);
            JOptionPane.showMessageDialog(this, "Consulta guardada exitosamente");
        }
    }
    
    private void cargarConsultaGuardada() {
        String seleccionado = (String) comboConsultasGuardadas.getSelectedItem();
        if (seleccionado != null && !seleccionado.equals("Nueva consulta...")) {
            // Cargar consultas predefinidas
            switch (seleccionado) {
                case "Activos por tipo":
                    txtConsultaSQL.setText("SELECT ta.nombre as tipo, COUNT(*) as cantidad, a.act_estado as estado\n" +
                                          "FROM ACTIVO a\n" +
                                          "INNER JOIN TIPO_ACTIVO ta ON a.tip_act_id = ta.tip_act_id\n" +
                                          "GROUP BY ta.nombre, a.act_estado\n" +
                                          "ORDER BY ta.nombre, cantidad DESC");
                    break;
                case "Mantenimientos del mes":
                    txtConsultaSQL.setText("SELECT a.act_numero_activo, m.mant_tipo, m.mant_estado, m.mant_fecha_inicio\n" +
                                          "FROM MANTENIMIENTO m\n" +
                                          "INNER JOIN ACTIVO a ON m.act_id = a.act_id\n" +
                                          "WHERE MONTH(m.mant_fecha_inicio) = MONTH(CURRENT_DATE)\n" +
                                          "AND YEAR(m.mant_fecha_inicio) = YEAR(CURRENT_DATE)\n" +
                                          "ORDER BY m.mant_fecha_inicio DESC");
                    break;
                case "Fallas frecuentes":
                    txtConsultaSQL.setText("SELECT ta.nombre as tipo_activo, COUNT(*) as total_fallas,\n" +
                                          "       t.tick_descripcion as descripcion\n" +
                                          "FROM TICKET t\n" +
                                          "INNER JOIN ACTIVO a ON t.act_id = a.act_id\n" +
                                          "INNER JOIN TIPO_ACTIVO ta ON a.tip_act_id = ta.tip_act_id\n" +
                                          "WHERE t.tick_tipo = 'Correctivo'\n" +
                                          "GROUP BY ta.nombre, t.tick_descripcion\n" +
                                          "HAVING COUNT(*) > 1\n" +
                                          "ORDER BY total_fallas DESC");
                    break;
            }
        }
    }
    
    private void exportarExcel() {
        if (reporteActual == null) {
            mostrarError("Debe generar un reporte antes de exportar");
            return;
        }
        
        // Mostrar información sobre Excel y ofrecer CSV como alternativa
        int opcion = JOptionPane.showConfirmDialog(this,
            "La exportación directa a Excel requiere dependencias adicionales (Apache POI).\n" +
            "¿Desea exportar a CSV en su lugar? Es compatible con Excel y otros programas.",
            "Exportar Datos",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);
            
        if (opcion == JOptionPane.YES_OPTION) {
            JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
            ExportadorReportes.exportarCSV(reporteActual, parentFrame);
        }
    }
    
    private void exportarPDF() {
        if (reporteActual == null) {
            mostrarError("Debe generar un reporte antes de exportar");
            return;
        }
        
        // Mostrar información sobre PDF y ofrecer texto como alternativa
        int opcion = JOptionPane.showConfirmDialog(this,
            "La exportación directa a PDF requiere dependencias adicionales (iText).\n" +
            "¿Desea exportar a texto plano en su lugar?",
            "Exportar Reporte",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);
            
        if (opcion == JOptionPane.YES_OPTION) {
            JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
            ExportadorReportes.exportarTexto(reporteActual, parentFrame);
        }
    }
    
    private void limpiarFiltros() {
        comboTipoReporte.setSelectedIndex(0);
        txtFechaInicio.setText("");
        txtFechaFin.setText("");
        comboTipoActivo.setSelectedIndex(0);
        comboUbicacion.setSelectedIndex(0);
        comboTecnico.setSelectedIndex(0);
        
        // Limpiar resultados
        modeloTabla.setRowCount(0);
        txtResumenEjecutivo.setText("");
        lblTotalRegistros.setText("Total de registros: 0");
        
        btnExportarExcel.setEnabled(false);
        btnExportarPDF.setEnabled(false);
    }
    
    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
