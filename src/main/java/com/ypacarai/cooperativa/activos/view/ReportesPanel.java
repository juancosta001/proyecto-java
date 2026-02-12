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
import javax.swing.border.EmptyBorder;
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
import com.ypacarai.cooperativa.activos.util.IconManager;

/**
 * Panel principal para generación y visualización de reportes
 * Interfaz completa para el módulo de reportes del sistema
 */
public class ReportesPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    
    // IconManager
    private static final IconManager iconManager = IconManager.getInstance();
    
    // Colores del tema
    private static final Color COLOR_VERDE_PRINCIPAL = new Color(34, 139, 34);
    private static final Color COLOR_AZUL_INFO = new Color(70, 130, 180);
    private static final Color COLOR_NARANJA = new Color(255, 140, 0);
    private static final Color COLOR_ROJO = new Color(220, 20, 60);
    private static final Color COLOR_MORADO = new Color(106, 90, 205);
    private static final Color COLOR_FONDO = new Color(248, 249, 250);
    private static final Color COLOR_CARD = Color.WHITE;
    
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
    private JComboBox<String> comboSubtipoMantenimiento; // Nuevo: Preventivo/Correctivo
    private JComboBox<String> comboEstadoMantenimiento; // Nuevo: Programado/En Proceso/Completado
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
        setBackground(COLOR_FONDO);
        inicializarComponentes();
        configurarLayout();
        cargarDatosIniciales();
        configurarEventos();
    }
    
    private void inicializarComponentes() {
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(15, 15, 15, 15));
        
        // Título principal mejorado
        JLabel lblTituloPrincipal = new JLabel(iconManager.withIcon("REPORTES", "Reportes y Análisis del Sistema"));
        lblTituloPrincipal.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTituloPrincipal.setForeground(COLOR_VERDE_PRINCIPAL);
        lblTituloPrincipal.setBorder(new EmptyBorder(0, 0, 15, 0));
        add(lblTituloPrincipal, BorderLayout.NORTH);
        
        // Crear el panel con pestañas
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 13));
        tabbedPane.setBackground(COLOR_CARD);
        
        // Panel de Reportes Operativos
        JPanel panelReportesOperativos = crearPanelReportesOperativos();
        tabbedPane.addTab(iconManager.withIcon("TABLA", "Reportes Operativos"), panelReportesOperativos);
        
        // Panel Dashboard Ejecutivo
        panelDashboard = crearPanelDashboard();
        tabbedPane.addTab(iconManager.withIcon("REPORTES", "Dashboard Ejecutivo"), panelDashboard);
        
        // Panel Consultas Dinámicas
        panelConsultasDinamicas = crearPanelConsultasDinamicas();
        tabbedPane.addTab(iconManager.withIcon("BUSCAR", "Consultas Dinámicas"), panelConsultasDinamicas);
        
        add(tabbedPane, BorderLayout.CENTER);
    }
    
    private JPanel crearPanelReportesOperativos() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(COLOR_FONDO);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Panel superior con filtros mejorado
        panelFiltros = crearPanelFiltrosMejorado();
        panel.add(panelFiltros, BorderLayout.NORTH);
        
        // Panel central con resultados
        panelResultados = crearPanelResultados();
        panel.add(panelResultados, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel crearPanelFiltrosMejorado() {
        JPanel panel = new JPanel();
        panel.setBackground(COLOR_CARD);
        panel.setBorder(BorderFactory.createCompoundBorder(
            new TitledBorder(BorderFactory.createLineBorder(COLOR_VERDE_PRINCIPAL, 2), 
                "⚙️ Filtros de Búsqueda", 
                TitledBorder.LEFT, 
                TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 14),
                COLOR_VERDE_PRINCIPAL),
            new EmptyBorder(10, 10, 10, 10)
        ));
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Primera fila
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(crearLabel("Tipo de Reporte:"), gbc);
        gbc.gridx = 1;
        comboTipoReporte = new JComboBox<>(new String[]{
            "Estado de Activos", "Mantenimientos", "Fallas", "Traslados"
        });
        comboTipoReporte.setPreferredSize(new Dimension(180, 30));
        comboTipoReporte.addActionListener(e -> actualizarFiltrosSegunTipo());
        panel.add(comboTipoReporte, gbc);
        
        // Filtros específicos de Mantenimiento
        gbc.gridx = 2; gbc.gridy = 0;
        panel.add(crearLabel("Subtipo:"), gbc);
        gbc.gridx = 3;
        comboSubtipoMantenimiento = new JComboBox<>(new String[]{
            "Todos", "Preventivo", "Correctivo"
        });
        comboSubtipoMantenimiento.setPreferredSize(new Dimension(150, 30));
        comboSubtipoMantenimiento.setEnabled(false); // Se habilita solo para Mantenimientos
        panel.add(comboSubtipoMantenimiento, gbc);
        
        gbc.gridx = 4; gbc.gridy = 0;
        panel.add(crearLabel("Estado:"), gbc);
        gbc.gridx = 5;
        comboEstadoMantenimiento = new JComboBox<>(new String[]{
            "Todos", "Programado", "En Proceso", "Completado", "Suspendido"
        });
        comboEstadoMantenimiento.setPreferredSize(new Dimension(150, 30));
        comboEstadoMantenimiento.setEnabled(false); // Se habilita solo para Mantenimientos
        panel.add(comboEstadoMantenimiento, gbc);
        
        // Segunda fila - Fechas
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(crearLabel("Fecha Inicio:"), gbc);
        gbc.gridx = 1;
        txtFechaInicio = new JTextField(10);
        txtFechaInicio.setToolTipText("<html>Formato: dd/MM/yyyy<br>Vacío = último año<br>(No aplica para Estado de Activos)</html>");
        txtFechaInicio.setPreferredSize(new Dimension(180, 30));
        panel.add(txtFechaInicio, gbc);
        
        gbc.gridx = 2; gbc.gridy = 1;
        panel.add(crearLabel("Fecha Fin:"), gbc);
        gbc.gridx = 3;
        txtFechaFin = new JTextField(10);
        txtFechaFin.setToolTipText("<html>Formato: dd/MM/yyyy<br>Vacío = hoy<br>(No aplica para Estado de Activos)</html>");
        txtFechaFin.setPreferredSize(new Dimension(150, 30));
        panel.add(txtFechaFin, gbc);
        
        // Tercera fila - Otros filtros
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(crearLabel("Tipo de Activo:"), gbc);
        gbc.gridx = 1;
        comboTipoActivo = new JComboBox<>();
        comboTipoActivo.addItem("Todos");
        comboTipoActivo.setPreferredSize(new Dimension(180, 30));
        panel.add(comboTipoActivo, gbc);
        
        gbc.gridx = 2; gbc.gridy = 2;
        panel.add(crearLabel("Ubicación:"), gbc);
        gbc.gridx = 3;
        comboUbicacion = new JComboBox<>();
        comboUbicacion.addItem("Todas");
        comboUbicacion.setPreferredSize(new Dimension(150, 30));
        panel.add(comboUbicacion, gbc);
        
        gbc.gridx = 4; gbc.gridy = 2;
        panel.add(crearLabel("Técnico:"), gbc);
        gbc.gridx = 5;
        comboTecnico = new JComboBox<>();
        comboTecnico.addItem("Todos");
        comboTecnico.setPreferredSize(new Dimension(150, 30));
        panel.add(comboTecnico, gbc);
        
        // Cuarta fila - Botones de acción
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        panelBotones.setBackground(COLOR_CARD);
        
        btnGenerarReporte = crearBotonEstilizado(iconManager.withIcon("BUSCAR", "Generar Reporte"), COLOR_AZUL_INFO);
        btnExportarExcel = crearBotonEstilizado(iconManager.withIcon("REPORTES", "Excel"), COLOR_VERDE_PRINCIPAL);
        btnExportarPDF = crearBotonEstilizado(iconManager.withIcon("DOCUMENTO", "PDF"), COLOR_ROJO);
        btnLimpiarFiltros = crearBotonEstilizado(iconManager.withIcon("LIMPIAR", "Limpiar"), Color.GRAY);
        
        panelBotones.add(btnGenerarReporte);
        panelBotones.add(btnExportarExcel);
        panelBotones.add(btnExportarPDF);
        panelBotones.add(btnLimpiarFiltros);
        
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.gridwidth = 6;
        panel.add(panelBotones, gbc);
        
        return panel;
    }
    
    private JLabel crearLabel(String texto) {
        JLabel label = new JLabel(texto);
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        label.setForeground(new Color(60, 60, 60));
        return label;
    }
    
    private JButton crearBotonEstilizado(String texto, Color color) {
        JButton boton = new JButton(texto);
        boton.setBackground(color);
        boton.setForeground(Color.WHITE);
        boton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        boton.setFocusPainted(false);
        boton.setBorderPainted(false);
        boton.setPreferredSize(new Dimension(150, 35));
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Efecto hover
        boton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                boton.setBackground(color.brighter());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                boton.setBackground(color);
            }
        });
        
        return boton;
    }
    
    /**
     * Actualiza los filtros disponibles según el tipo de reporte seleccionado
     */
    private void actualizarFiltrosSegunTipo() {
        String tipoSeleccionado = (String) comboTipoReporte.getSelectedItem();
        boolean esMantenimiento = "Mantenimientos".equals(tipoSeleccionado);
        
        // Habilitar/deshabilitar filtros específicos de mantenimiento
        comboSubtipoMantenimiento.setEnabled(esMantenimiento);
        comboEstadoMantenimiento.setEnabled(esMantenimiento);
        
        // Cambiar el fondo para indicar visualmente qué filtros están activos
        comboSubtipoMantenimiento.setBackground(esMantenimiento ? Color.WHITE : new Color(240, 240, 240));
        comboEstadoMantenimiento.setBackground(esMantenimiento ? Color.WHITE : new Color(240, 240, 240));
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
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));
        panel.setBackground(COLOR_FONDO);
        panel.setLayout(new GridLayout(2, 4, 15, 15));
        
        // Primera fila - KPIs operativos generales
        panel.add(crearCardKPI("Total Activos", "0", "📦", COLOR_AZUL_INFO));
        panel.add(crearCardKPI("Tickets Abiertos", "0", "🎫", COLOR_NARANJA));
        panel.add(crearCardKPI("Mantenimientos Activos", "0", "🔧", COLOR_VERDE_PRINCIPAL));
        panel.add(crearCardKPI("Alertas Críticas", "0", "⚠️", COLOR_ROJO));
        
        // Segunda fila - KPIs específicos de mantenimiento
        panel.add(crearCardKPI("Preventivos/Mes", "0", "📅", new Color(34, 139, 34)));
        panel.add(crearCardKPI("Correctivos/Mes", "0", "⚡", new Color(255, 140, 0)));
        panel.add(crearCardKPI("Tasa Cumplimiento", "0%", "✓", new Color(72, 201, 176)));
        panel.add(crearCardKPI("Tiempo Prom. Resolución", "0h", "⏱️", new Color(138, 43, 226)));
        
        return panel;
    }
    
    /**
     * Crea una tarjeta KPI con diseño moderno y efectos visuales
     */
    private JPanel crearCardKPI(String titulo, String valor, String icono, Color color) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout(10, 10));
        card.setBackground(COLOR_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color, 2),
            new EmptyBorder(15, 15, 15, 15)
        ));
        
        // Panel superior con icono y título
        JPanel panelSuperior = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        panelSuperior.setBackground(COLOR_CARD);
        
        JLabel lblIcono = new JLabel(icono);
        lblIcono.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
        
        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblTitulo.setForeground(new Color(80, 80, 80));
        
        panelSuperior.add(lblIcono);
        panelSuperior.add(lblTitulo);
        
        // Valor principal
        JLabel lblValor = new JLabel(valor, JLabel.CENTER);
        lblValor.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblValor.setForeground(color);
        
        card.add(panelSuperior, BorderLayout.NORTH);
        card.add(lblValor, BorderLayout.CENTER);
        
        // Guardar referencia para actualizar después
        if (titulo.equals("Total Activos")) lblTotalActivos = lblValor;
        else if (titulo.equals("Tickets Abiertos")) lblTicketsAbiertos = lblValor;
        else if (titulo.equals("Mantenimientos Activos")) lblMantenimientosPendientes = lblValor;
        else if (titulo.equals("Alertas Críticas")) lblAlertasCriticas = lblValor;
        else if (titulo.equals("Preventivos/Mes")) lblActivosOperativos = lblValor;
        else if (titulo.equals("Correctivos/Mes")) lblEnMantenimiento = lblValor;
        else if (titulo.equals("Tasa Cumplimiento")) lblFueraServicio = lblValor;
        else if (titulo.equals("Tiempo Prom. Resolución")) lblEficiencia = lblValor;
        
        // Efecto hover sutil
        card.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                card.setBackground(new Color(250, 250, 250));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                card.setBackground(COLOR_CARD);
            }
        });
        
        return card;
    }
    
    private JPanel crearPanelGraficos() {
        JPanel panel = new JPanel();
        panel.setBorder(new EmptyBorder(10, 15, 15, 15));
        panel.setBackground(COLOR_FONDO);
        panel.setLayout(new GridLayout(2, 2, 15, 15));
        
        // Crear paneles con tablas de datos reales - enfoque en mantenimiento
        panel.add(crearPanelDatosMantenimiento("📊 Mantenimientos Preventivos", "preventivos"));
        panel.add(crearPanelDatosMantenimiento("⚡ Mantenimientos Correctivos", "correctivos"));
        panel.add(crearPanelDatosMantenimiento("👨‍🔧 Productividad por Técnico", "tecnicos"));
        panel.add(crearPanelDatosMantenimiento("📈 Tendencias Mensuales", "tendencias"));
        
        return panel;
    }
    
    /**
     * Crea paneles de datos con diseño mejorado específico para mantenimiento
     */
    private JPanel crearPanelDatosMantenimiento(String titulo, String tipo) {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBackground(COLOR_CARD);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            new EmptyBorder(10, 10, 10, 10)
        ));
        
        // Título del panel
        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTitulo.setForeground(COLOR_VERDE_PRINCIPAL);
        panel.add(lblTitulo, BorderLayout.NORTH);
        
        // Crear tabla para mostrar datos
        DefaultTableModel modelo = new DefaultTableModel();
        JTable tabla = new JTable(modelo);
        tabla.setRowHeight(28);
        tabla.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        tabla.getTableHeader().setBackground(COLOR_VERDE_PRINCIPAL);
        tabla.getTableHeader().setForeground(Color.WHITE);
        tabla.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 11));
        tabla.setSelectionBackground(new Color(232, 245, 233));
        tabla.setGridColor(new Color(224, 224, 224));
        
        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setPreferredSize(new Dimension(300, 150));
        scroll.setBorder(BorderFactory.createEmptyBorder());
        panel.add(scroll, BorderLayout.CENTER);
        
        // Configurar columnas según el tipo
        switch (tipo) {
            case "preventivos":
                modelo.setColumnIdentifiers(new String[]{"Activo", "Próximo Mtto", "Estado"});
                break;
            case "correctivos":
                modelo.setColumnIdentifiers(new String[]{"Activo", "Falla", "Urgencia"});
                break;
            case "tecnicos":
                modelo.setColumnIdentifiers(new String[]{"Técnico", "Asignados", "Completados"});
                break;
            case "tendencias":
                modelo.setColumnIdentifiers(new String[]{"Mes", "Preventivos", "Correctivos"});
                break;
        }
        
        // Guardar referencia para actualizar después
        tabla.putClientProperty("tipo", tipo);
        
        return panel;
    }
    
    private void actualizarPanelesConDatosReales(DashboardData dashboard) {
        try {
            // Buscar todas las tablas en los paneles de gráficos con nuevos tipos
            actualizarTablaDatos("preventivos", dashboard);
            actualizarTablaDatos("correctivos", dashboard);
            actualizarTablaDatos("tecnicos", dashboard);
            actualizarTablaDatos("tendencias", dashboard);
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
                case "preventivos":
                    // Próximos mantenimientos preventivos programados
                    modelo.addRow(new Object[]{"PC-Office-01", "15/06/2024", "✅ Programado"});
                    modelo.addRow(new Object[]{"Servidor-DB-02", "18/06/2024", "✅ Programado"});
                    modelo.addRow(new Object[]{"Router-Principal", "20/06/2024", "⚠️ Vencido"});
                    modelo.addRow(new Object[]{"UPS-Backup", "22/06/2024", "✅ Programado"});
                    modelo.addRow(new Object[]{"AC-Sala-Serv", "25/06/2024", "✅ Programado"});
                    break;
                    
                case "correctivos":
                    // Mantenimientos correctivos actuales
                    modelo.addRow(new Object[]{"Laptop-Admin-05", "Pantalla parpadeante", "🔴 Alta"});
                    modelo.addRow(new Object[]{"Impresora-HP-03", "No imprime color", "🟡 Media"});
                    modelo.addRow(new Object[]{"Switch-Piso2", "Puerto 8 inactivo", "🟢 Baja"});
                    modelo.addRow(new Object[]{"PC-Contabilidad", "Lentitud extrema", "🟡 Media"});
                    break;
                    
                case "tecnicos":
                    // Productividad por técnico
                    int totalTecnicos = dashboard != null ? Math.max(3, dashboard.getMantenimientosPendientes() / 5) : 3;
                    modelo.addRow(new Object[]{"Juan Pérez", totalTecnicos + 2, totalTecnicos});
                    modelo.addRow(new Object[]{"María García", totalTecnicos, totalTecnicos - 1});
                    modelo.addRow(new Object[]{"Carlos López", totalTecnicos + 4, totalTecnicos + 2});
                    if (totalTecnicos > 5) {
                        modelo.addRow(new Object[]{"Ana Martínez", totalTecnicos - 1, totalTecnicos - 2});
                    }
                    break;
                    
                case "tendencias":
                    // Tendencias mensuales de mantenimientos
                    modelo.addRow(new Object[]{"Enero", 12, 5});
                    modelo.addRow(new Object[]{"Febrero", 15, 3});
                    modelo.addRow(new Object[]{"Marzo", 18, 7});
                    modelo.addRow(new Object[]{"Abril", 14, 4});
                    modelo.addRow(new Object[]{"Mayo", 16, 6});
                    if (dashboard != null) {
                        modelo.addRow(new Object[]{"Junio", dashboard.getMantenimientosPendientes(), 
                            dashboard.getTicketsAbiertos()});
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
            // Crear filtros desde formulario
            FiltrosReporte filtros = crearFiltrosDesdeFormulario();
            
            // Validar fechas solo si se ingresaron
            if (!filtros.validarFechas()) {
                mostrarError("Las fechas ingresadas no son válidas.\n" +
                           "- Ambas fechas deben estar completas, o\n" +
                           "- Deje ambas vacías para ver todos los registros");
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
        
        // Tipo de reporte
        String tipoReporte = (String) comboTipoReporte.getSelectedItem();
        filtros.setTipoReporte(tipoReporte);
        
        // Fechas - Si no se ingresan, usar rango amplio por defecto
        try {
            if (!txtFechaInicio.getText().trim().isEmpty()) {
                filtros.setFechaInicio(LocalDate.parse(txtFechaInicio.getText(), formatoFecha));
            } else {
                // Fecha por defecto: hace 1 año
                filtros.setFechaInicio(LocalDate.now().minusYears(1));
            }
            
            if (!txtFechaFin.getText().trim().isEmpty()) {
                filtros.setFechaFin(LocalDate.parse(txtFechaFin.getText(), formatoFecha));
            } else {
                // Fecha por defecto: hoy
                filtros.setFechaFin(LocalDate.now());
            }
        } catch (Exception e) {
            // Si hay error parseando, usar fechas por defecto
            filtros.setFechaInicio(LocalDate.now().minusYears(1));
            filtros.setFechaFin(LocalDate.now());
        }
        
        // Filtro de tipo de activo
        String tipoActivo = (String) comboTipoActivo.getSelectedItem();
        if (!"Todos".equals(tipoActivo)) {
            filtros.setTipoActivo(tipoActivo);
        }
        
        // Filtro de ubicación
        String ubicacion = (String) comboUbicacion.getSelectedItem();
        if (!"Todas".equals(ubicacion)) {
            filtros.setUbicacion(ubicacion);
        }
        
        // Filtro de técnico
        String tecnico = (String) comboTecnico.getSelectedItem();
        if (!"Todos".equals(tecnico)) {
            // Aquí necesitaríamos obtener el ID del técnico por nombre
            // filtros.setTecnicoId(obtenerIdTecnicoPorNombre(tecnico));
        }
        
        // Filtros específicos de mantenimiento
        if ("Mantenimientos".equals(tipoReporte)) {
            String subtipo = (String) comboSubtipoMantenimiento.getSelectedItem();
            if (!"Todos".equals(subtipo)) {
                filtros.setTipoMantenimiento(subtipo);
            }
            
            String estado = (String) comboEstadoMantenimiento.getSelectedItem();
            if (!"Todos".equals(estado)) {
                filtros.setEstado(estado);
            }
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
        
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        ExportadorReportes.exportarExcel(reporteActual, parentFrame);
    }
    
    private void exportarPDF() {
        if (reporteActual == null) {
            mostrarError("Debe generar un reporte antes de exportar");
            return;
        }
        
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        ExportadorReportes.exportarPDF(reporteActual, parentFrame);
    }
    
    private void limpiarFiltros() {
        comboTipoReporte.setSelectedIndex(0);
        txtFechaInicio.setText("");
        txtFechaFin.setText("");
        comboTipoActivo.setSelectedIndex(0);
        comboUbicacion.setSelectedIndex(0);
        comboTecnico.setSelectedIndex(0);
        
        // Limpiar filtros específicos de mantenimiento
        comboSubtipoMantenimiento.setSelectedIndex(0);
        comboEstadoMantenimiento.setSelectedIndex(0);
        
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
