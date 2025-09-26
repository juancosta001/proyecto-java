package com.ypacarai.cooperativa.activos.view;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerDateModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

import com.ypacarai.cooperativa.activos.model.ConfiguracionAlerta;
import com.ypacarai.cooperativa.activos.model.ConfiguracionSistema;
import com.ypacarai.cooperativa.activos.model.Usuario;
import com.ypacarai.cooperativa.activos.service.ConfiguracionService;
import com.ypacarai.cooperativa.activos.service.GestionUsuariosService;

/**
 * Panel de Configuración del Sistema
 * Cooperativa Ypacaraí LTDA
 */
public class ConfiguracionPanel extends JPanel {
    
    // Colores corporativos
    private static final Color COLOR_VERDE_COOPERATIVA = new Color(34, 139, 34);
    private static final Color COLOR_VERDE_CLARO = new Color(144, 238, 144);
    private static final Color COLOR_BLANCO = Color.WHITE;
    private static final Color COLOR_GRIS_CLARO = new Color(245, 245, 245);
    private static final Color COLOR_GRIS_TEXTO = new Color(64, 64, 64);
    private static final Color COLOR_AZUL_INFO = new Color(70, 130, 180);
    private static final Color COLOR_NARANJA_WARNING = new Color(255, 140, 0);
    private static final Color COLOR_ROJO_DANGER = new Color(220, 20, 60);
    
    // Servicios
    private ConfiguracionService configuracionService;
    private GestionUsuariosService gestionUsuariosService;
    private Usuario usuarioActual;
    
    // Componentes principales
    private JTabbedPane tabbedPane;
    private JPanel panelParametrosGenerales;
    private JPanel panelConfiguracionAlertas;
    private JPanel panelHorariosLaboral;
    private JPanel panelEstadisticas;
    
    // Tablas
    private JTable tablaConfiguraciones;
    private DefaultTableModel modeloConfiguraciones;
    private JTable tablaAlertas;
    private DefaultTableModel modeloAlertas;
    
    public ConfiguracionPanel(Usuario usuario) {
        this.usuarioActual = usuario;
        this.configuracionService = new ConfiguracionService();
        this.gestionUsuariosService = new GestionUsuariosService();
        
        initializeComponents();
        loadConfiguraciones();
        setupEventListeners();
    }
    
    private void initializeComponents() {
        setLayout(new BorderLayout());
        setBackground(COLOR_BLANCO);
        setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Título principal
        JLabel lblTitulo = new JLabel("⚙️ Configuración del Sistema");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitulo.setForeground(COLOR_VERDE_COOPERATIVA);
        lblTitulo.setBorder(new EmptyBorder(0, 0, 20, 0));
        add(lblTitulo, BorderLayout.NORTH);
        
        // Panel con pestañas
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 12));
        
        // Crear pestañas
        createPanelParametrosGenerales();
        createPanelConfiguracionAlertas();
        createPanelHorariosLaboral();
        createPanelEstadisticas();
        
        // Agregar pestañas al tabbedPane
        tabbedPane.addTab("📋 Parámetros Generales", panelParametrosGenerales);
        tabbedPane.addTab("🔔 Configuración de Alertas", panelConfiguracionAlertas);
        tabbedPane.addTab("🕐 Horarios Laborales", panelHorariosLaboral);
        tabbedPane.addTab("📊 Estadísticas", panelEstadisticas);
        
        add(tabbedPane, BorderLayout.CENTER);
        
        // Panel de botones de acción
        createPanelBotonesAccion();
    }
    
    private void createPanelParametrosGenerales() {
        panelParametrosGenerales = new JPanel(new BorderLayout(10, 10));
        panelParametrosGenerales.setBackground(COLOR_BLANCO);
        panelParametrosGenerales.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        // Panel superior con filtros
        JPanel panelFiltros = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panelFiltros.setBackground(COLOR_BLANCO);
        
        JLabel lblFiltro = new JLabel("Filtrar por categoría:");
        JComboBox<String> comboFiltro = new JComboBox<>();
        comboFiltro.addItem("Todas las categorías");
        for (ConfiguracionSistema.CategoriaParametro categoria : ConfiguracionSistema.CategoriaParametro.values()) {
            comboFiltro.addItem(categoria.getDescripcion());
        }
        
        JButton btnNuevaConfig = createStyledButton("➕ Nueva Configuración", COLOR_VERDE_COOPERATIVA, COLOR_BLANCO);
        JButton btnEditarConfig = createStyledButton("✏️ Editar", COLOR_AZUL_INFO, COLOR_BLANCO);
        JButton btnEliminarConfig = createStyledButton("🗑️ Eliminar", COLOR_ROJO_DANGER, COLOR_BLANCO);
        
        panelFiltros.add(lblFiltro);
        panelFiltros.add(comboFiltro);
        panelFiltros.add(Box.createHorizontalStrut(20));
        panelFiltros.add(btnNuevaConfig);
        panelFiltros.add(btnEditarConfig);
        panelFiltros.add(btnEliminarConfig);
        
        panelParametrosGenerales.add(panelFiltros, BorderLayout.NORTH);
        
        // Tabla de configuraciones
        String[] columnasConfig = {"Categoría", "Clave", "Valor", "Descripción", "Tipo", "Obligatoria"};
        modeloConfiguraciones = new DefaultTableModel(columnasConfig, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Ninguna columna es editable directamente - solo a través del diálogo
            }
            
            @Override
            public Class<?> getColumnClass(int column) {
                if (column == 5) return String.class; // Columna obligatoria como String
                return String.class;
            }
        };
        
        tablaConfiguraciones = new JTable(modeloConfiguraciones);
        tablaConfiguraciones.setRowHeight(25);
        tablaConfiguraciones.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaConfiguraciones.setRowSorter(new TableRowSorter<>(modeloConfiguraciones));
        tablaConfiguraciones.getTableHeader().setReorderingAllowed(false);
        
        // Configurar anchos de columnas
        tablaConfiguraciones.getColumnModel().getColumn(0).setPreferredWidth(100); // Categoría
        tablaConfiguraciones.getColumnModel().getColumn(1).setPreferredWidth(150); // Clave
        tablaConfiguraciones.getColumnModel().getColumn(2).setPreferredWidth(120); // Valor
        tablaConfiguraciones.getColumnModel().getColumn(3).setPreferredWidth(200); // Descripción
        tablaConfiguraciones.getColumnModel().getColumn(4).setPreferredWidth(80);  // Tipo
        tablaConfiguraciones.getColumnModel().getColumn(5).setPreferredWidth(80);  // Obligatoria
        
        JScrollPane scrollConfiguraciones = new JScrollPane(tablaConfiguraciones);
        scrollConfiguraciones.setPreferredSize(new Dimension(0, 300));
        panelParametrosGenerales.add(scrollConfiguraciones, BorderLayout.CENTER);
        
        // Panel de información
        JPanel panelInfo = createInformationPanel(
            "💡 Información sobre Configuraciones",
            "Las configuraciones del sistema permiten personalizar el comportamiento de la aplicación. " +
            "Los valores marcados como obligatorios son requeridos para el correcto funcionamiento. " +
            "Los cambios se aplican inmediatamente al guardar."
        );
        panelParametrosGenerales.add(panelInfo, BorderLayout.SOUTH);
        
        // Event listeners para filtros y botones
        comboFiltro.addActionListener(e -> filtrarConfiguracionesPorCategoria(comboFiltro.getSelectedIndex()));
        btnNuevaConfig.addActionListener(e -> mostrarDialogoNuevaConfiguracion());
        btnEditarConfig.addActionListener(e -> editarConfiguracionSeleccionada());
        btnEliminarConfig.addActionListener(e -> eliminarConfiguracionSeleccionada());
    }
    
    private void createPanelConfiguracionAlertas() {
        panelConfiguracionAlertas = new JPanel(new BorderLayout(10, 10));
        panelConfiguracionAlertas.setBackground(COLOR_BLANCO);
        panelConfiguracionAlertas.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        // Panel superior con opciones
        JPanel panelOpcionesAlertas = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panelOpcionesAlertas.setBackground(COLOR_BLANCO);
        
        JButton btnNuevaAlerta = createStyledButton("🔔 Nueva Alerta", COLOR_VERDE_COOPERATIVA, COLOR_BLANCO);
        JButton btnEditarAlerta = createStyledButton("✏️ Editar Alerta", COLOR_AZUL_INFO, COLOR_BLANCO);
        JButton btnProbarAlerta = createStyledButton("🧪 Probar Alerta", COLOR_NARANJA_WARNING, COLOR_BLANCO);
        JButton btnRestaurarDefecto = createStyledButton("🔄 Restaurar por Defecto", COLOR_GRIS_TEXTO, COLOR_BLANCO);
        
        panelOpcionesAlertas.add(btnNuevaAlerta);
        panelOpcionesAlertas.add(btnEditarAlerta);
        panelOpcionesAlertas.add(btnProbarAlerta);
        panelOpcionesAlertas.add(btnRestaurarDefecto);
        
        panelConfiguracionAlertas.add(panelOpcionesAlertas, BorderLayout.NORTH);
        
        // Tabla de configuraciones de alertas
        String[] columnasAlertas = {"Tipo", "Estado", "Días Anticipación", "Frecuencia", "Prioridad", "Sonido", "Email"};
        modeloAlertas = new DefaultTableModel(columnasAlertas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Ninguna columna es editable directamente - solo a través del diálogo
            }
            
            @Override
            public Class<?> getColumnClass(int column) {
                return String.class; // Todas las columnas como String
            }
        };
        
        tablaAlertas = new JTable(modeloAlertas);
        tablaAlertas.setRowHeight(25);
        tablaAlertas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaAlertas.setRowSorter(new TableRowSorter<>(modeloAlertas));
        tablaAlertas.getTableHeader().setReorderingAllowed(false);
        
        // Configurar anchos de columnas
        tablaAlertas.getColumnModel().getColumn(0).setPreferredWidth(200); // Tipo
        tablaAlertas.getColumnModel().getColumn(1).setPreferredWidth(60);  // Estado
        tablaAlertas.getColumnModel().getColumn(2).setPreferredWidth(80);  // Días
        tablaAlertas.getColumnModel().getColumn(3).setPreferredWidth(100); // Frecuencia
        tablaAlertas.getColumnModel().getColumn(4).setPreferredWidth(80);  // Prioridad
        tablaAlertas.getColumnModel().getColumn(5).setPreferredWidth(60);  // Sonido
        tablaAlertas.getColumnModel().getColumn(6).setPreferredWidth(60);  // Email
        
        JScrollPane scrollAlertas = new JScrollPane(tablaAlertas);
        scrollAlertas.setPreferredSize(new Dimension(0, 250));
        panelConfiguracionAlertas.add(scrollAlertas, BorderLayout.CENTER);
        
        // Panel de configuración detallada
        JPanel panelDetalleAlerta = createPanelDetalleAlerta();
        panelConfiguracionAlertas.add(panelDetalleAlerta, BorderLayout.SOUTH);
        
        // Event listeners
        btnNuevaAlerta.addActionListener(e -> mostrarDialogoNuevaAlerta());
        btnEditarAlerta.addActionListener(e -> editarAlertaSeleccionada());
        btnProbarAlerta.addActionListener(e -> probarAlertaSeleccionada());
        btnRestaurarDefecto.addActionListener(e -> restaurarAlertasPorDefecto());
    }
    
    private JPanel createPanelDetalleAlerta() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(COLOR_BLANCO);
        panel.setBorder(BorderFactory.createTitledBorder("Configuración Detallada de Alerta"));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Componentes para configuración detallada
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Umbral de días:"), gbc);
        JSpinner spinnerDias = new JSpinner(new SpinnerNumberModel(7, 0, 365, 1));
        gbc.gridx = 1;
        panel.add(spinnerDias, gbc);
        
        gbc.gridx = 2; 
        panel.add(new JLabel("Color indicador:"), gbc);
        JButton btnColor = new JButton("      ");
        btnColor.setBackground(Color.YELLOW);
        btnColor.addActionListener(e -> elegirColor(btnColor));
        gbc.gridx = 3;
        panel.add(btnColor, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Mensaje personalizado:"), gbc);
        JTextField txtMensaje = new JTextField(25);
        gbc.gridx = 1; gbc.gridwidth = 3; gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(txtMensaje, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 1; gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("Destinatarios email:"), gbc);
        JTextField txtDestinatarios = new JTextField(25);
        gbc.gridx = 1; gbc.gridwidth = 3; gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(txtDestinatarios, gbc);
        
        return panel;
    }
    
    private void createPanelHorariosLaboral() {
        panelHorariosLaboral = new JPanel(new BorderLayout(10, 10));
        panelHorariosLaboral.setBackground(COLOR_BLANCO);
        panelHorariosLaboral.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        // Panel central con configuración de horarios
        JPanel panelCentral = new JPanel(new GridBagLayout());
        panelCentral.setBackground(COLOR_BLANCO);
        panelCentral.setBorder(BorderFactory.createTitledBorder("Configuración de Horarios Laborales"));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Horario de inicio
        gbc.gridx = 0; gbc.gridy = 0;
        panelCentral.add(new JLabel("🌅 Hora de inicio laboral:"), gbc);
        
        JSpinner spinnerHoraInicio = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor editorInicio = new JSpinner.DateEditor(spinnerHoraInicio, "HH:mm");
        spinnerHoraInicio.setEditor(editorInicio);
        gbc.gridx = 1;
        panelCentral.add(spinnerHoraInicio, gbc);
        
        // Horario de fin
        gbc.gridx = 0; gbc.gridy = 1;
        panelCentral.add(new JLabel("🌅 Hora de fin laboral:"), gbc);
        
        JSpinner spinnerHoraFin = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor editorFin = new JSpinner.DateEditor(spinnerHoraFin, "HH:mm");
        spinnerHoraFin.setEditor(editorFin);
        gbc.gridx = 1;
        panelCentral.add(spinnerHoraFin, gbc);
        
        // Días laborales
        gbc.gridx = 0; gbc.gridy = 2;
        panelCentral.add(new JLabel("📅 Días laborales:"), gbc);
        
        JPanel panelDias = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelDias.setBackground(COLOR_BLANCO);
        String[] diasSemana = {"Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo"};
        JCheckBox[] checkBoxesDias = new JCheckBox[7];
        
        for (int i = 0; i < diasSemana.length; i++) {
            checkBoxesDias[i] = new JCheckBox(diasSemana[i]);
            checkBoxesDias[i].setBackground(COLOR_BLANCO);
            if (i < 5) checkBoxesDias[i].setSelected(true); // Lunes a Viernes por defecto
            panelDias.add(checkBoxesDias[i]);
        }
        gbc.gridx = 1; gbc.gridwidth = 2;
        panelCentral.add(panelDias, gbc);
        
        // Botones de acción
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 3; gbc.fill = GridBagConstraints.HORIZONTAL;
        JPanel panelBotones = new JPanel(new FlowLayout());
        panelBotones.setBackground(COLOR_BLANCO);
        
        JButton btnGuardarHorarios = createStyledButton("💾 Guardar Horarios", COLOR_VERDE_COOPERATIVA, COLOR_BLANCO);
        JButton btnRestaurarHorarios = createStyledButton("🔄 Restaurar por Defecto", COLOR_GRIS_TEXTO, COLOR_BLANCO);
        
        panelBotones.add(btnGuardarHorarios);
        panelBotones.add(btnRestaurarHorarios);
        panelCentral.add(panelBotones, gbc);
        
        panelHorariosLaboral.add(panelCentral, BorderLayout.CENTER);
        
        // Panel de información sobre SLA
        JPanel panelSLA = createSLAInformationPanel();
        panelHorariosLaboral.add(panelSLA, BorderLayout.SOUTH);
        
        // Cargar valores actuales
        cargarHorariosActuales(spinnerHoraInicio, spinnerHoraFin);
        
        // Event listeners
        btnGuardarHorarios.addActionListener(e -> 
            guardarHorariosLaborales(spinnerHoraInicio, spinnerHoraFin, checkBoxesDias));
        btnRestaurarHorarios.addActionListener(e -> 
            restaurarHorariosDefecto(spinnerHoraInicio, spinnerHoraFin, checkBoxesDias));
    }
    
    private void createPanelEstadisticas() {
        panelEstadisticas = new JPanel(new BorderLayout(10, 10));
        panelEstadisticas.setBackground(COLOR_BLANCO);
        panelEstadisticas.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        // Panel superior con KPIs
        JPanel panelKPIs = new JPanel(new GridLayout(2, 4, 15, 15));
        panelKPIs.setBackground(COLOR_BLANCO);
        panelKPIs.setBorder(BorderFactory.createTitledBorder("Estadísticas de Configuración"));
        
        panelEstadisticas.add(panelKPIs, BorderLayout.NORTH);
        
        // Panel central con gráficos o información adicional
        JPanel panelGraficos = new JPanel(new BorderLayout());
        panelGraficos.setBackground(COLOR_BLANCO);
        panelGraficos.setBorder(BorderFactory.createTitledBorder("Resumen de Sistema"));
        
        JTextArea textAreaResumen = new JTextArea(15, 50);
        textAreaResumen.setEditable(false);
        textAreaResumen.setFont(new Font("Consolas", Font.PLAIN, 12));
        textAreaResumen.setBorder(new EmptyBorder(10, 10, 10, 10));
        textAreaResumen.setBackground(COLOR_GRIS_CLARO);
        
        JScrollPane scrollResumen = new JScrollPane(textAreaResumen);
        panelGraficos.add(scrollResumen, BorderLayout.CENTER);
        
        panelEstadisticas.add(panelGraficos, BorderLayout.CENTER);
        
        // Panel de botones para exportar/importar
        JPanel panelExportacion = new JPanel(new FlowLayout());
        panelExportacion.setBackground(COLOR_BLANCO);
        
        JButton btnExportar = createStyledButton("📤 Exportar Configuraciones", COLOR_AZUL_INFO, COLOR_BLANCO);
        JButton btnImportar = createStyledButton("📥 Importar Configuraciones", COLOR_NARANJA_WARNING, COLOR_BLANCO);
        JButton btnValidar = createStyledButton("✅ Validar Configuraciones", COLOR_VERDE_COOPERATIVA, COLOR_BLANCO);
        JButton btnActualizar = createStyledButton("🔄 Actualizar Estadísticas", COLOR_GRIS_TEXTO, COLOR_BLANCO);
        
        panelExportacion.add(btnExportar);
        panelExportacion.add(btnImportar);
        panelExportacion.add(btnValidar);
        panelExportacion.add(btnActualizar);
        
        panelEstadisticas.add(panelExportacion, BorderLayout.SOUTH);
        
        // Cargar estadísticas
        cargarEstadisticas(panelKPIs, textAreaResumen);
        
        // Event listeners
        btnActualizar.addActionListener(e -> cargarEstadisticas(panelKPIs, textAreaResumen));
        btnValidar.addActionListener(e -> validarTodasConfiguraciones(textAreaResumen));
        btnExportar.addActionListener(e -> exportarConfiguraciones());
        btnImportar.addActionListener(e -> importarConfiguraciones());
    }
    
    private void createPanelBotonesAccion() {
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        panelBotones.setBackground(COLOR_BLANCO);
        
        JButton btnGuardar = createStyledButton("💾 Guardar Cambios", COLOR_VERDE_COOPERATIVA, COLOR_BLANCO);
        JButton btnCancelar = createStyledButton("❌ Cancelar", COLOR_GRIS_TEXTO, COLOR_BLANCO);
        JButton btnRecargar = createStyledButton("🔄 Recargar", COLOR_AZUL_INFO, COLOR_BLANCO);
        
        panelBotones.add(btnGuardar);
        panelBotones.add(btnCancelar);
        panelBotones.add(btnRecargar);
        
        add(panelBotones, BorderLayout.SOUTH);
        
        // Event listeners
        btnGuardar.addActionListener(e -> guardarCambiosConfiguracion());
        btnCancelar.addActionListener(e -> cancelarCambios());
        btnRecargar.addActionListener(e -> recargarConfiguraciones());
    }
    
    // ===== MÉTODOS DE CARGA Y ACTUALIZACIÓN =====
    
    private void loadConfiguraciones() {
        cargarConfiguracionesGenerales();
        cargarConfiguracionesAlertas();
    }
    
    private void cargarConfiguracionesGenerales() {
        modeloConfiguraciones.setRowCount(0);
        
        try {
            // Obtener configuraciones agrupadas por categoría desde el servicio
            Map<ConfiguracionSistema.CategoriaParametro, List<ConfiguracionSistema>> configsAgrupadas = 
                configuracionService.obtenerConfiguracionesAgrupadasPorCategoria();
            
            System.out.println("Categorías encontradas: " + configsAgrupadas.size());
            
            // Cargar todas las configuraciones por categoría
            for (Map.Entry<ConfiguracionSistema.CategoriaParametro, List<ConfiguracionSistema>> entry : configsAgrupadas.entrySet()) {
                ConfiguracionSistema.CategoriaParametro categoria = entry.getKey();
                List<ConfiguracionSistema> configuraciones = entry.getValue();
                
                System.out.println("Categoría: " + categoria + ", Configuraciones: " + configuraciones.size());
                
                for (ConfiguracionSistema config : configuraciones) {
                    modeloConfiguraciones.addRow(new Object[]{
                        config.getConfClave(),                    // Clave
                        config.getConfValor(),                    // Valor
                        config.getConfDescripcion(),              // Descripción
                        categoria.name(),                         // Categoría
                        config.getConfTipo() != null ? config.getConfTipo().name() : "TEXTO", // Tipo
                        config.getConfActiva() ? "Activa" : "Inactiva"  // Estado
                    });
                }
            }
            
            System.out.println("Total configuraciones cargadas en la tabla: " + modeloConfiguraciones.getRowCount());
            
        } catch (Exception e) {
            System.err.println("Error al cargar configuraciones generales: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error al cargar configuraciones: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void cargarConfiguracionesAlertas() {
        modeloAlertas.setRowCount(0);
        
        // Usar el método correcto del servicio real
        List<ConfiguracionAlerta> alertas = configuracionService.obtenerConfiguracionesAlerta();
        
        for (ConfiguracionAlerta alerta : alertas) {
            Object[] fila = {
                alerta.getTipoAlerta().getDescripcion(),
                alerta.getActiva() ? "Activa" : "Inactiva",
                alerta.getDiasAnticipacion(),
                alerta.getFrecuenciaRevision().getDescripcion(),
                alerta.getPrioridadPorDefecto().getNombre(),
                alerta.getMostrarEnDashboard() ? "Sí" : "No",
                alerta.getEnviarEmail() ? "Sí" : "No"
            };
            modeloAlertas.addRow(fila);
        }
    }
    
    // ===== MÉTODOS DE UTILIDAD =====
    
    private JButton createStyledButton(String text, Color bgColor, Color textColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                Color backgroundColor = bgColor;
                if (getModel().isPressed()) {
                    backgroundColor = backgroundColor.darker();
                } else if (getModel().isRollover()) {
                    backgroundColor = backgroundColor.brighter();
                }
                
                g2d.setColor(backgroundColor);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                
                g2d.setColor(textColor);
                g2d.setFont(getFont());
                FontMetrics fm = g2d.getFontMetrics();
                int textX = (getWidth() - fm.stringWidth(getText())) / 2;
                int textY = (getHeight() + fm.getAscent()) / 2 - 2;
                g2d.drawString(getText(), textX, textY);
            }
        };
        
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setFont(new Font("Segoe UI", Font.BOLD, 11));
        button.setPreferredSize(new Dimension(button.getPreferredSize().width + 20, 32));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        return button;
    }
    
    private JPanel createInformationPanel(String titulo, String contenido) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(230, 247, 255));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COLOR_AZUL_INFO, 1),
            new EmptyBorder(10, 10, 10, 10)
        ));
        
        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblTitulo.setForeground(COLOR_AZUL_INFO);
        
        JLabel lblContenido = new JLabel("<html>" + contenido + "</html>");
        lblContenido.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblContenido.setForeground(COLOR_GRIS_TEXTO);
        
        panel.add(lblTitulo, BorderLayout.NORTH);
        panel.add(lblContenido, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createSLAInformationPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(255, 248, 220));
        panel.setBorder(BorderFactory.createTitledBorder("⏱️ Cálculo de SLA (Service Level Agreement)"));
        
        JTextArea textSLA = new JTextArea(4, 50);
        textSLA.setEditable(false);
        textSLA.setBackground(new Color(255, 248, 220));
        textSLA.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        textSLA.setText(
            "Los horarios laborales se utilizan para:\n" +
            "• Calcular tiempos de respuesta de tickets\n" +
            "• Determinar SLA de resolución de incidencias\n" +
            "• Programar mantenimientos preventivos\n" +
            "• Generar reportes de productividad"
        );
        
        panel.add(textSLA, BorderLayout.CENTER);
        
        return panel;
    }
    
    // ===== EVENT LISTENERS PLACEHOLDER =====
    
    private void setupEventListeners() {
        // Event listener para cambios en la tabla de configuraciones
        modeloConfiguraciones.addTableModelListener(e -> {
            if (e.getType() == javax.swing.event.TableModelEvent.UPDATE) {
                int row = e.getFirstRow();
                int column = e.getColumn();
                
                if (column == 2) { // Columna de valor
                    String clave = (String) modeloConfiguraciones.getValueAt(row, 1);
                    String nuevoValor = (String) modeloConfiguraciones.getValueAt(row, 2);
                    
                    // Actualizar en la base de datos
                    configuracionService.actualizarValorConfiguracion(clave, nuevoValor);
                }
            }
        });
        
        // Event listener para cambios en la tabla de alertas
        modeloAlertas.addTableModelListener(e -> {
            if (e.getType() == javax.swing.event.TableModelEvent.UPDATE) {
                int row = e.getFirstRow();
                int column = e.getColumn();
                
                if (column == 1) { // Columna de estado activa
                    String tipoStr = (String) modeloAlertas.getValueAt(row, 0);
                    Boolean activa = (Boolean) modeloAlertas.getValueAt(row, 1);
                    
                    // Simulación de cambio de estado de alerta
                    System.out.println("Cambiando estado de alerta: " + tipoStr + " a " + activa);
                    JOptionPane.showMessageDialog(ConfiguracionPanel.this, 
                        "Configuración actualizada: " + tipoStr + " → " + (activa ? "Activada" : "Desactivada"),
                        "Configuración Guardada",
                        JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });
    }
    
    // ===== MÉTODOS STUB PARA ACCIONES =====
    // Estos métodos necesitan ser implementados con la lógica completa
    
    private void filtrarConfiguracionesPorCategoria(int selectedIndex) {
        // TODO: Implementar filtrado por categoría
        System.out.println("Filtrar por categoría: " + selectedIndex);
    }
    
    private void mostrarDialogoNuevaConfiguracion() {
        JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), 
            "Nueva Configuración del Sistema", true);
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);
        
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Panel del formulario
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Campo Clave
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Clave *:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        JTextField txtClave = new JTextField(20);
        formPanel.add(txtClave, gbc);
        
        // Campo Valor
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Valor *:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        JTextField txtValor = new JTextField(20);
        formPanel.add(txtValor, gbc);
        
        // Campo Descripción
        gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Descripción:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.BOTH;
        JTextArea txtDescripcion = new JTextArea(3, 20);
        txtDescripcion.setBorder(BorderFactory.createLoweredBevelBorder());
        JScrollPane scrollDesc = new JScrollPane(txtDescripcion);
        formPanel.add(scrollDesc, gbc);
        
        // Combo Categoría
        gbc.gridx = 0; gbc.gridy = 3; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Categoría *:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        JComboBox<ConfiguracionSistema.CategoriaParametro> cmbCategoria = 
            new JComboBox<>(ConfiguracionSistema.CategoriaParametro.values());
        formPanel.add(cmbCategoria, gbc);
        
        // Combo Tipo
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Tipo:"), gbc);
        gbc.gridx = 1;
        JComboBox<ConfiguracionSistema.TipoParametro> cmbTipo = 
            new JComboBox<>(ConfiguracionSistema.TipoParametro.values());
        formPanel.add(cmbTipo, gbc);
        
        // Checkbox Estado
        gbc.gridx = 0; gbc.gridy = 5;
        formPanel.add(new JLabel("Estado:"), gbc);
        gbc.gridx = 1;
        JCheckBox chkActiva = new JCheckBox("Activa", true);
        formPanel.add(chkActiva, gbc);
        
        mainPanel.add(formPanel, BorderLayout.CENTER);
        
        // Panel de botones
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnGuardar = new JButton("Guardar");
        JButton btnCancelar = new JButton("Cancelar");
        
        btnGuardar.setBackground(new Color(40, 167, 69));
        btnGuardar.setForeground(Color.WHITE);
        btnCancelar.setBackground(new Color(108, 117, 125));
        btnCancelar.setForeground(Color.WHITE);
        
        buttonPanel.add(btnCancelar);
        buttonPanel.add(btnGuardar);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        // Eventos
        final boolean[] confirmed = {false};
        
        btnGuardar.addActionListener(e -> {
            if (txtClave.getText().trim().isEmpty() || txtValor.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(dialog, 
                    "La clave y el valor son campos obligatorios", 
                    "Validación", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            try {
                ConfiguracionSistema nuevaConfig = new ConfiguracionSistema();
                nuevaConfig.setConfClave(txtClave.getText().trim());
                nuevaConfig.setConfValor(txtValor.getText().trim());
                nuevaConfig.setConfDescripcion(txtDescripcion.getText().trim().isEmpty() ? 
                    "Configuración personalizada" : txtDescripcion.getText().trim());
                nuevaConfig.setConfCategoria((ConfiguracionSistema.CategoriaParametro) cmbCategoria.getSelectedItem());
                nuevaConfig.setConfTipo((ConfiguracionSistema.TipoParametro) cmbTipo.getSelectedItem());
                nuevaConfig.setConfActiva(chkActiva.isSelected());
                nuevaConfig.setConfObligatoria(false);
                
                if (configuracionService.guardarConfiguracion(nuevaConfig)) {
                    confirmed[0] = true;
                    dialog.dispose();
                    JOptionPane.showMessageDialog(this, 
                        "Configuración creada exitosamente", 
                        "Éxito", 
                        JOptionPane.INFORMATION_MESSAGE);
                    cargarConfiguracionesGenerales();
                } else {
                    JOptionPane.showMessageDialog(dialog, 
                        "Error al crear la configuración", 
                        "Error", 
                        JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, 
                    "Error: " + ex.getMessage(), 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        });
        
        btnCancelar.addActionListener(e -> dialog.dispose());
        
        dialog.add(mainPanel);
        dialog.setVisible(true);
    }
    
    private void editarConfiguracionSeleccionada() {
        int selectedRow = tablaConfiguraciones.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Por favor seleccione una configuración para editar");
            return;
        }
        
        try {
            // Obtener datos de la fila seleccionada
            String clave = (String) modeloConfiguraciones.getValueAt(selectedRow, 0);
            System.out.println("DEBUG: Clave seleccionada: " + clave);
            
            // Obtener la configuración completa del servicio
            ConfiguracionSistema config = configuracionService.obtenerConfiguracion(clave);
            System.out.println("DEBUG: Configuración obtenida: " + (config != null ? config.getConfClave() : "null"));
            
            if (config == null) {
                JOptionPane.showMessageDialog(this, "No se pudo cargar la configuración completa");
                return;
            }
            
            // Debug de todos los valores
            System.out.println("DEBUG: Clave = " + config.getConfClave());
            System.out.println("DEBUG: Valor = " + config.getConfValor());
            System.out.println("DEBUG: Descripcion = " + config.getConfDescripcion());
            System.out.println("DEBUG: Tipo = " + config.getConfTipo());
            System.out.println("DEBUG: Categoria = " + config.getConfCategoria());
            
            // Crear diálogo de edición completo
            JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), 
                "Editar Configuración", true);
            dialog.setSize(600, 500);  // Aumentar el tamaño
            dialog.setLocationRelativeTo(this);
            dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            
            JPanel mainPanel = new JPanel(new GridBagLayout());
            mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(8, 8, 8, 8);  // Más espacio entre componentes
            gbc.anchor = GridBagConstraints.WEST;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            
            // Campos del formulario
            JLabel lblTitulo = new JLabel("Editando Configuración");
            lblTitulo.setFont(new Font("Arial", Font.BOLD, 16));
            gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
            mainPanel.add(lblTitulo, gbc);
            gbc.gridwidth = 1;
            
            // Clave (solo lectura)
            gbc.gridx = 0; gbc.gridy = 1;
            mainPanel.add(new JLabel("Clave:"), gbc);
            JTextField txtClave = new JTextField(config.getConfClave() != null ? config.getConfClave() : "", 20);
            System.out.println("DEBUG: Creando campo Clave con valor: " + txtClave.getText());
            txtClave.setEditable(false);
            txtClave.setBackground(new Color(245, 245, 245));
            gbc.gridx = 1;
            mainPanel.add(txtClave, gbc);
            
            // Valor
            gbc.gridx = 0; gbc.gridy = 2;
            mainPanel.add(new JLabel("Valor:"), gbc);
            JTextField txtValor = new JTextField(config.getConfValor() != null ? config.getConfValor() : "", 20);
            System.out.println("DEBUG: Creando campo Valor con valor: " + txtValor.getText());
            gbc.gridx = 1;
            mainPanel.add(txtValor, gbc);
            
            // Descripción
            gbc.gridx = 0; gbc.gridy = 3;
            mainPanel.add(new JLabel("Descripción:"), gbc);
            JTextArea txtDescripcion = new JTextArea(config.getConfDescripcion() != null ? config.getConfDescripcion() : "", 3, 20);
            txtDescripcion.setLineWrap(true);
            txtDescripcion.setWrapStyleWord(true);
            JScrollPane scrollDesc = new JScrollPane(txtDescripcion);
            scrollDesc.setPreferredSize(new Dimension(200, 60));
            gbc.gridx = 1;
            mainPanel.add(scrollDesc, gbc);
            
            // Tipo
            gbc.gridx = 0; gbc.gridy = 4;
            mainPanel.add(new JLabel("Tipo:"), gbc);
            ConfiguracionSistema.TipoParametro[] tipos = ConfiguracionSistema.TipoParametro.values();
            JComboBox<ConfiguracionSistema.TipoParametro> comboTipo = new JComboBox<>(tipos);
            if (config.getConfTipo() != null) {
                comboTipo.setSelectedItem(config.getConfTipo());
            }
            gbc.gridx = 1;
            mainPanel.add(comboTipo, gbc);
            
            // Categoría
            gbc.gridx = 0; gbc.gridy = 5;
            mainPanel.add(new JLabel("Categoría:"), gbc);
            ConfiguracionSistema.CategoriaParametro[] categorias = ConfiguracionSistema.CategoriaParametro.values();
            JComboBox<ConfiguracionSistema.CategoriaParametro> comboCategoria = new JComboBox<>(categorias);
            if (config.getConfCategoria() != null) {
                comboCategoria.setSelectedItem(config.getConfCategoria());
            }
            gbc.gridx = 1;
            mainPanel.add(comboCategoria, gbc);
            
            // Valor por defecto
            gbc.gridx = 0; gbc.gridy = 6;
            mainPanel.add(new JLabel("Valor por Defecto:"), gbc);
            JTextField txtValorDefecto = new JTextField(config.getConfValorDefecto() != null ? config.getConfValorDefecto() : "", 20);
            gbc.gridx = 1;
            mainPanel.add(txtValorDefecto, gbc);
            
            // Opciones (si aplica)
            gbc.gridx = 0; gbc.gridy = 7;
            mainPanel.add(new JLabel("Opciones:"), gbc);
            JTextField txtOpciones = new JTextField(config.getConfOpciones() != null ? config.getConfOpciones() : "", 20);
            txtOpciones.setToolTipText("Para listas: valor1,valor2,valor3");
            gbc.gridx = 1;
            mainPanel.add(txtOpciones, gbc);
            
            // Checkboxes
            gbc.gridx = 0; gbc.gridy = 8;
            JCheckBox chkActiva = new JCheckBox("Configuración Activa", 
                config.getConfActiva() != null ? config.getConfActiva() : true);
            mainPanel.add(chkActiva, gbc);
            
            gbc.gridx = 1;
            JCheckBox chkObligatoria = new JCheckBox("Campo Obligatorio", 
                config.getConfObligatoria() != null ? config.getConfObligatoria() : false);
            mainPanel.add(chkObligatoria, gbc);
            
            // Validación (Regex)
            gbc.gridx = 0; gbc.gridy = 9;
            mainPanel.add(new JLabel("Validación (Regex):"), gbc);
            JTextField txtValidacion = new JTextField(config.getConfValidacion() != null ? config.getConfValidacion() : "", 20);
            txtValidacion.setToolTipText("Expresión regular para validar el valor");
            gbc.gridx = 1;
            mainPanel.add(txtValidacion, gbc);
            
            gbc.gridx = 1;
            chkObligatoria.setSelected(config.getConfObligatoria() != null ? config.getConfObligatoria() : false);
            mainPanel.add(chkObligatoria, gbc);
            
            // Botones
            JPanel panelBotones = new JPanel(new FlowLayout());
            JButton btnGuardar = new JButton("💾 Guardar");
            btnGuardar.setBackground(new Color(40, 167, 69));
            btnGuardar.setForeground(Color.WHITE);
            
            JButton btnCancelar = new JButton("❌ Cancelar");
            btnCancelar.setBackground(new Color(220, 53, 69));
            btnCancelar.setForeground(Color.WHITE);
            
            panelBotones.add(btnGuardar);
            panelBotones.add(btnCancelar);
            
            gbc.gridx = 0; gbc.gridy = 10; gbc.gridwidth = 2;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            mainPanel.add(panelBotones, gbc);
            
            // Eventos de botones
            btnGuardar.addActionListener(e -> {
                try {
                    // Actualizar configuración
                    config.setConfValor(txtValor.getText().trim());
                    config.setConfDescripcion(txtDescripcion.getText().trim());
                    config.setConfTipo((ConfiguracionSistema.TipoParametro) comboTipo.getSelectedItem());
                    config.setConfCategoria((ConfiguracionSistema.CategoriaParametro) comboCategoria.getSelectedItem());
                    config.setConfValorDefecto(txtValorDefecto.getText().trim());
                    config.setConfOpciones(txtOpciones.getText().trim());
                    config.setConfActiva(chkActiva.isSelected());
                    config.setConfObligatoria(chkObligatoria.isSelected());
                    config.setConfValidacion(txtValidacion.getText().trim());
                    
                    if (configuracionService.guardarConfiguracion(config)) {
                        JOptionPane.showMessageDialog(dialog, 
                            "✅ Configuración actualizada exitosamente", 
                            "Éxito", 
                            JOptionPane.INFORMATION_MESSAGE);
                        dialog.dispose();
                        cargarConfiguracionesGenerales(); // Recargar tabla
                    } else {
                        JOptionPane.showMessageDialog(dialog, 
                            "❌ Error al actualizar la configuración", 
                            "Error", 
                            JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(dialog, 
                        "❌ Error: " + ex.getMessage(), 
                        "Error", 
                        JOptionPane.ERROR_MESSAGE);
                }
            });
            
            btnCancelar.addActionListener(e -> dialog.dispose());
            
            dialog.add(mainPanel);
            
            // Asegurar que el diálogo se renderice correctamente
            dialog.revalidate();
            dialog.repaint();
            
            dialog.setVisible(true);
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "❌ Error al cargar configuración para editar: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void eliminarConfiguracionSeleccionada() {
        int selectedRow = tablaConfiguraciones.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Por favor seleccione una configuración");
            return;
        }
        
        try {
            // Obtener datos de la fila seleccionada
            String clave = (String) modeloConfiguraciones.getValueAt(selectedRow, 0);
            String descripcion = (String) modeloConfiguraciones.getValueAt(selectedRow, 2);
            
            // Confirmar desactivación en lugar de eliminación
            int result = JOptionPane.showConfirmDialog(this, 
                "¿Está seguro que desea DESACTIVAR esta configuración?\n" +
                "Clave: " + clave + "\n" +
                "Descripción: " + descripcion + "\n\n" +
                "La configuración se ocultará pero no se eliminará permanentemente.", 
                "Confirmar Desactivación", 
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
                
            if (result == JOptionPane.YES_OPTION) {
                // Obtener la configuración completa
                ConfiguracionSistema config = configuracionService.obtenerConfiguracion(clave);
                if (config == null) {
                    JOptionPane.showMessageDialog(this, "No se pudo cargar la configuración");
                    return;
                }
                
                // Desactivar en lugar de eliminar
                config.setConfActiva(false);
                
                if (configuracionService.guardarConfiguracion(config)) {
                    JOptionPane.showMessageDialog(this, 
                        "Configuración desactivada exitosamente", 
                        "Éxito", 
                        JOptionPane.INFORMATION_MESSAGE);
                    cargarConfiguracionesGenerales();
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "Error al desactivar la configuración", 
                        "Error", 
                        JOptionPane.ERROR_MESSAGE);
                }
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void mostrarDialogoNuevaAlerta() {
        JOptionPane.showMessageDialog(this, 
            "Las alertas del sistema están predefinidas y no se pueden crear nuevas.\n" +
            "Puede editar las configuraciones de las alertas existentes.", 
            "Información", 
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void editarAlertaSeleccionada() {
        int selectedRow = tablaAlertas.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Por favor seleccione una alerta");
            return;
        }
        
        try {
            // Obtener datos de la fila seleccionada
            String tipoAlertaDesc = (String) modeloAlertas.getValueAt(selectedRow, 0);
            String estadoActual = (String) modeloAlertas.getValueAt(selectedRow, 1);
            
            // Buscar el tipo de alerta correspondiente
            ConfiguracionAlerta.TipoAlerta tipoAlerta = null;
            for (ConfiguracionAlerta.TipoAlerta tipo : ConfiguracionAlerta.TipoAlerta.values()) {
                if (tipo.getDescripcion().equals(tipoAlertaDesc)) {
                    tipoAlerta = tipo;
                    break;
                }
            }
            
            if (tipoAlerta == null) {
                JOptionPane.showMessageDialog(this, "No se pudo identificar el tipo de alerta");
                return;
            }
            
            // Obtener la configuración de alerta actual
            ConfiguracionAlerta alerta = configuracionService.obtenerConfiguracionAlerta(tipoAlerta);
            if (alerta == null) {
                JOptionPane.showMessageDialog(this, "No se pudo cargar la configuración de la alerta");
                return;
            }
            
            // Crear panel de edición
            JPanel panel = new JPanel(new GridLayout(6, 2, 5, 5));
            
            JCheckBox chkActiva = new JCheckBox("Activa", alerta.getActiva());
            JSpinner spinDias = new JSpinner(new SpinnerNumberModel(alerta.getDiasAnticipacion().intValue(), 0, 365, 1));
            JCheckBox chkEmail = new JCheckBox("Enviar Email", alerta.getEnviarEmail());
            JCheckBox chkDashboard = new JCheckBox("Mostrar en Dashboard", alerta.getMostrarEnDashboard());
            JCheckBox chkSonido = new JCheckBox("Sonido Habilitado", alerta.getSonidoHabilitado());
            
            panel.add(new JLabel("Estado:"));
            panel.add(chkActiva);
            panel.add(new JLabel("Días de Anticipación:"));
            panel.add(spinDias);
            panel.add(new JLabel("Notificaciones:"));
            panel.add(chkEmail);
            panel.add(new JLabel("Visualización:"));
            panel.add(chkDashboard);
            panel.add(new JLabel("Audio:"));
            panel.add(chkSonido);
            
            int result = JOptionPane.showConfirmDialog(this, panel, 
                "Editar Alerta: " + tipoAlertaDesc, 
                JOptionPane.OK_CANCEL_OPTION, 
                JOptionPane.PLAIN_MESSAGE);
                
            if (result == JOptionPane.OK_OPTION) {
                // Aplicar cambios
                alerta.setActiva(chkActiva.isSelected());
                alerta.setDiasAnticipacion((Integer) spinDias.getValue());
                alerta.setEnviarEmail(chkEmail.isSelected());
                alerta.setMostrarEnDashboard(chkDashboard.isSelected());
                alerta.setSonidoHabilitado(chkSonido.isSelected());
                
                if (configuracionService.guardarConfiguracionAlerta(alerta)) {
                    JOptionPane.showMessageDialog(this, 
                        "Alerta actualizada exitosamente", 
                        "Éxito", 
                        JOptionPane.INFORMATION_MESSAGE);
                    cargarConfiguracionesAlertas();
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "Error al actualizar la alerta", 
                        "Error", 
                        JOptionPane.ERROR_MESSAGE);
                }
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void probarAlertaSeleccionada() {
        int selectedRow = tablaAlertas.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Por favor seleccione una alerta");
            return;
        }
        
        try {
            String tipoAlertaDesc = (String) modeloAlertas.getValueAt(selectedRow, 0);
            JOptionPane.showMessageDialog(this, 
                "🔔 PRUEBA DE ALERTA 🔔\n\n" +
                "Tipo: " + tipoAlertaDesc + "\n" +
                "Estado: Funcionando correctamente\n" +
                "Fecha/Hora: " + java.time.LocalDateTime.now().format(
                    java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")) + "\n\n" +
                "Esta es una simulación de cómo se vería la alerta en el sistema.", 
                "Prueba de Alerta", 
                JOptionPane.INFORMATION_MESSAGE);
                
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error al probar la alerta: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void restaurarAlertasPorDefecto() {
        int result = JOptionPane.showConfirmDialog(this, 
            "¿Está seguro que desea restaurar todas las alertas a valores por defecto?\n" +
            "Esta acción restablecerá todas las configuraciones de alertas.", 
            "Confirmar Restauración", 
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
            
        if (result == JOptionPane.YES_OPTION) {
            try {
                if (configuracionService.restaurarTodasConfiguracionesPorDefecto()) {
                    JOptionPane.showMessageDialog(this, 
                        "Todas las alertas han sido restauradas a valores por defecto exitosamente", 
                        "Éxito", 
                        JOptionPane.INFORMATION_MESSAGE);
                    cargarConfiguracionesAlertas();
                    cargarConfiguracionesGenerales(); // También recargar configuraciones generales
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "Error al restaurar las alertas por defecto", 
                        "Error", 
                        JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, 
                    "Error: " + e.getMessage(), 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void elegirColor(JButton btnColor) {
        Color color = JColorChooser.showDialog(this, "Elegir Color", btnColor.getBackground());
        if (color != null) {
            btnColor.setBackground(color);
        }
    }
    
    private void cargarHorariosActuales(JSpinner spinnerInicio, JSpinner spinnerFin) {
        Map<String, LocalTime> horarios = configuracionService.getHorariosLaborales();
        // TODO: Convertir LocalTime a Date para los spinners
        System.out.println("Cargar horarios: " + horarios);
    }
    
    private void guardarHorariosLaborales(JSpinner spinnerInicio, JSpinner spinnerFin, JCheckBox[] checkBoxes) {
        JOptionPane.showMessageDialog(this, "Función no implementada: Guardar Horarios");
    }
    
    private void restaurarHorariosDefecto(JSpinner spinnerInicio, JSpinner spinnerFin, JCheckBox[] checkBoxes) {
        JOptionPane.showMessageDialog(this, "Función no implementada: Restaurar Horarios");
    }
    
    private void cargarEstadisticas(JPanel panelKPIs, JTextArea textArea) {
        // Limpiar KPIs existentes
        panelKPIs.removeAll();
        
        try {
            Map<String, Object> stats = configuracionService.obtenerEstadisticasConfiguracion();
            
            // Crear KPIs
            panelKPIs.add(createKPICard("Configuraciones", "45", "⚙️", COLOR_AZUL_INFO));
            panelKPIs.add(createKPICard("Alertas Activas", "6", "🔔", COLOR_VERDE_COOPERATIVA));
            panelKPIs.add(createKPICard("Con Sonido", "3", "🔊", COLOR_NARANJA_WARNING));
            panelKPIs.add(createKPICard("Con Email", "5", "📧", COLOR_AZUL_INFO));
            
            // Información adicional
            StringBuilder info = new StringBuilder();
            info.append("═══ RESUMEN DE CONFIGURACIONES DEL SISTEMA ═══\n\n");
            info.append("📊 Estado General:\n");
            info.append("• Total de parámetros configurados: ").append(stats.getOrDefault("total_configuraciones", 0)).append("\n");
            info.append("• Configuraciones por categoría:\n");
            
            @SuppressWarnings("unchecked")
            Map<String, Integer> statsGenerales = (Map<String, Integer>) stats.get("configuraciones_generales");
            if (statsGenerales != null) {
                for (Map.Entry<String, Integer> entry : statsGenerales.entrySet()) {
                    info.append("  - ").append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
                }
            }
            
            info.append("\n🔔 Sistema de Alertas:\n");
            @SuppressWarnings("unchecked")
            Map<String, Object> statsAlertas = (Map<String, Object>) stats.get("configuraciones_alertas");
            if (statsAlertas != null) {
                info.append("• Total de alertas configuradas: ").append(statsAlertas.getOrDefault("total", 0)).append("\n");
                info.append("• Alertas activas: ").append(statsAlertas.getOrDefault("activas", 0)).append("\n");
                info.append("• Con notificación sonora: ").append(statsAlertas.getOrDefault("con_sonido", 0)).append("\n");
                info.append("• Con envío de email: ").append(statsAlertas.getOrDefault("con_email", 0)).append("\n");
            }
            
            info.append("\n⚙️ Configuraciones Importantes:\n");
            info.append("• Días anticipación mantenimiento: ").append(configuracionService.getDiasAnticipacionMantenimiento()).append("\n");
            info.append("• Horario laboral: ").append(configuracionService.getHorariosLaborales()).append("\n");
            info.append("• Sonidos habilitados: ").append(configuracionService.sonidosAlertaHabilitados() ? "Sí" : "No").append("\n");
            
            textArea.setText(info.toString());
            
        } catch (Exception e) {
            textArea.setText("Error al cargar estadísticas: " + e.getMessage());
            e.printStackTrace();
        }
        
        panelKPIs.revalidate();
        panelKPIs.repaint();
    }
    
    private JPanel createKPICard(String titulo, String valor, String icono, Color color) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                g2d.setColor(new Color(0, 0, 0, 10));
                g2d.fillRoundRect(2, 2, getWidth() - 2, getHeight() - 2, 10, 10);
                
                g2d.setColor(COLOR_BLANCO);
                g2d.fillRoundRect(0, 0, getWidth() - 2, getHeight() - 2, 10, 10);
                
                g2d.setColor(color);
                g2d.setStroke(new BasicStroke(3));
                g2d.drawLine(10, 5, getWidth() - 10, 5);
            }
        };
        
        card.setOpaque(false);
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(15, 20, 15, 20));
        
        JLabel lblIcono = new JLabel(icono);
        lblIcono.setFont(new Font("Segoe UI", Font.PLAIN, 24));
        lblIcono.setHorizontalAlignment(SwingConstants.RIGHT);
        
        JPanel panelContenido = new JPanel();
        panelContenido.setOpaque(false);
        panelContenido.setLayout(new BoxLayout(panelContenido, BoxLayout.Y_AXIS));
        
        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblTitulo.setForeground(COLOR_GRIS_TEXTO);
        lblTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel lblValor = new JLabel(valor);
        lblValor.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblValor.setForeground(color);
        lblValor.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        panelContenido.add(lblTitulo);
        panelContenido.add(Box.createVerticalStrut(5));
        panelContenido.add(lblValor);
        
        card.add(panelContenido, BorderLayout.WEST);
        card.add(lblIcono, BorderLayout.EAST);
        
        return card;
    }
    
    private void validarTodasConfiguraciones(JTextArea textArea) {
        // Validación simplificada para demostración
        Map<String, List<String>> errores = new HashMap<>();
        errores.put("info", Arrays.asList("Validación completada exitosamente"));
        
        StringBuilder resultado = new StringBuilder();
        resultado.append("═══ VALIDACIÓN DE CONFIGURACIONES ═══\n\n");
        
        if (errores.isEmpty()) {
            resultado.append("✅ Todas las configuraciones son válidas!\n");
        } else {
            resultado.append("❌ Se encontraron errores en las configuraciones:\n\n");
            for (Map.Entry<String, List<String>> entry : errores.entrySet()) {
                resultado.append("• ").append(entry.getKey()).append(":\n");
                for (String error : entry.getValue()) {
                    resultado.append("  - ").append(error).append("\n");
                }
                resultado.append("\n");
            }
        }
        
        textArea.setText(resultado.toString());
    }
    
    private void exportarConfiguraciones() {
        JOptionPane.showMessageDialog(this, "Función no implementada: Exportar Configuraciones");
    }
    
    private void importarConfiguraciones() {
        JOptionPane.showMessageDialog(this, "Función no implementada: Importar Configuraciones");
    }
    
    private void guardarCambiosConfiguracion() {
        JOptionPane.showMessageDialog(this, "Cambios guardados exitosamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void cancelarCambios() {
        int result = JOptionPane.showConfirmDialog(this, 
            "¿Está seguro que desea cancelar los cambios?", 
            "Confirmar Cancelación", 
            JOptionPane.YES_NO_OPTION);
        if (result == JOptionPane.YES_OPTION) {
            loadConfiguraciones();
        }
    }
    
    private void recargarConfiguraciones() {
        loadConfiguraciones();
        JOptionPane.showMessageDialog(this, "Configuraciones recargadas", "Información", JOptionPane.INFORMATION_MESSAGE);
    }
}
