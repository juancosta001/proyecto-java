package com.ypacarai.cooperativa.activos.view;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

import com.ypacarai.cooperativa.activos.dao.ActivoDAO;
import com.ypacarai.cooperativa.activos.dao.MantenimientoDAO;
import com.ypacarai.cooperativa.activos.dao.UsuarioDAO;
import com.ypacarai.cooperativa.activos.util.IconManager;
import com.ypacarai.cooperativa.activos.model.Activo;
import com.ypacarai.cooperativa.activos.model.FichaReporte;
import com.ypacarai.cooperativa.activos.model.FichaReporte.EstadoFicha;
import com.ypacarai.cooperativa.activos.model.Mantenimiento;
import com.ypacarai.cooperativa.activos.model.Usuario;
import com.ypacarai.cooperativa.activos.model.Usuario;
import com.ypacarai.cooperativa.activos.service.FichaReporteService;

/**
 * Panel de gestión de Fichas de Reporte de Mantenimientos Correctivos
 * Permite crear, consultar y enviar fichas técnicas al Jefe de Informática
 * Sistema de Gestión de Activos - Cooperativa Ypacaraí LTDA
 */
public class FichaReportePanel extends JPanel {
    
    // Colores corporativos
    private static final Color VERDE_PRINCIPAL = new Color(0, 128, 55);
    private static final Color VERDE_SECUNDARIO = new Color(0, 100, 40);
    private static final Color AZUL_INFO = new Color(52, 144, 220);
    private static final Color NARANJA_WARNING = new Color(255, 193, 7);
    private static final Color ROJO_DANGER = new Color(220, 53, 69);
    private static final Color GRIS_CLARO = new Color(245, 245, 245);
    private static final Color GRIS_OSCURO = new Color(64, 64, 64);
    private static final Color PURPLE_INFO = new Color(123, 104, 238);
    
    // Gestor de iconos
    private static final IconManager iconManager = IconManager.getInstance();
    
    // Componentes principales
    private final Usuario usuarioActual;
    private CardLayout cardLayout;
    private JPanel panelContenedor;
    
    // Panel de tabla
    private JTable tablaFichas;
    private DefaultTableModel modeloTabla;
    private TableRowSorter<DefaultTableModel> sorter;
    
    // Filtros
    private JTextField txtBusqueda;
    private JComboBox<EstadoFicha> cmbFiltroEstado;
    
    // Botones
    private JButton btnNuevaFicha;
    private JButton btnVerDetalles;
    private JButton btnEnviarFicha;
    private JButton btnActualizar;
    
    // Formulario
    private JComboBox<Mantenimiento> cmbMantenimiento;
    private JComboBox<Activo> cmbActivo;
    private JTextArea txtProblemaReportado;
    private JTextArea txtDiagnostico;
    private JTextArea txtSolucionAplicada;
    private JTextArea txtAccionRealizada; // Alias para txtSolucionAplicada
    private JTextArea txtComponentesCambio;
    private JTextArea txtComponentesReemplazados; // Alias para txtComponentesCambio
    private JTextArea txtObservaciones;
    
    // Variables
    private FichaReporte fichaEnEdicion;
    private JLabel lblTituloFormulario;
    
    // Estadísticas
    private JLabel lblPendientes;
    private JLabel lblEnviadas;
    private JLabel lblTotal;
    
    // Servicios
    private FichaReporteService fichaReporteService;
    private ActivoDAO activoDAO;
    private MantenimientoDAO mantenimientoDAO;
    private UsuarioDAO usuarioDAO;
    
    private DateTimeFormatter formateadorFechaHora = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    
    public FichaReportePanel(Usuario usuarioActual) {
        this.usuarioActual = usuarioActual;
        
        inicializarServicios();
        configurarPanel();
        crearInterfaz();
        cargarDatosIniciales();
    }
    
    private void inicializarServicios() {
        try {
            this.fichaReporteService = new FichaReporteService();
            this.activoDAO = new ActivoDAO();  
            this.mantenimientoDAO = new MantenimientoDAO();
            this.usuarioDAO = new UsuarioDAO();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error al inicializar servicios: " + e.getMessage(),
                "Error del Sistema",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void configurarPanel() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(10, 10, 10, 10));
        this.cardLayout = new CardLayout();
        this.panelContenedor = new JPanel(cardLayout);
    }
    
    private void crearInterfaz() {
        // Panel de listado
        JPanel panelListado = crearPanelListado();
        panelContenedor.add(panelListado, "LISTADO");
        
        // Panel de formulario
        JPanel panelFormulario = crearPanelFormulario();
        panelContenedor.add(panelFormulario, "FORMULARIO");
        
        add(panelContenedor, BorderLayout.CENTER);
        
        // Mostrar listado por defecto
        cardLayout.show(panelContenedor, "LISTADO");
    }
    
    private JPanel crearPanelListado() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Título con estadísticas
        JPanel panelTitulo = crearPanelTitulo();
        panel.add(panelTitulo, BorderLayout.NORTH);
        
        // Panel central
        JPanel panelCentral = new JPanel(new BorderLayout());
        
        // Filtros
        JPanel panelFiltros = crearPanelFiltros();
        panelCentral.add(panelFiltros, BorderLayout.NORTH);
        
        // Tabla
        JPanel panelTabla = crearPanelTabla();
        panelCentral.add(panelTabla, BorderLayout.CENTER);
        
        panel.add(panelCentral, BorderLayout.CENTER);
        
        // Botones
        JPanel panelBotones = crearPanelBotones();
        panel.add(panelBotones, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel crearPanelTitulo() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(VERDE_PRINCIPAL);
        panel.setBorder(new EmptyBorder(15, 20, 15, 20));
        
        JLabel lblTitulo = new JLabel(iconManager.withIcon("FICHA", "FICHAS DE REPORTE - MANTENIMIENTOS CORRECTIVOS"));
        lblTitulo.setFont(iconManager.getIconFont(20).deriveFont(Font.BOLD));
        lblTitulo.setForeground(Color.WHITE);
        
        JLabel lblSubtitulo = new JLabel("Registro y envío de fichas técnicas al Jefe de Informática");
        lblSubtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSubtitulo.setForeground(Color.WHITE);
        
        JPanel panelTextos = new JPanel(new BorderLayout());
        panelTextos.setOpaque(false);
        panelTextos.add(lblTitulo, BorderLayout.NORTH);
        panelTextos.add(lblSubtitulo, BorderLayout.SOUTH);
        
        panel.add(panelTextos, BorderLayout.WEST);
        
        // Estadísticas
        JPanel panelEstadisticas = crearPanelEstadisticas();
        panel.add(panelEstadisticas, BorderLayout.EAST);
        
        return panel;
    }
    
    private JPanel crearPanelEstadisticas() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        panel.setOpaque(false);
        
        lblPendientes = new JLabel("Borradores: 0");
        lblPendientes.setForeground(Color.YELLOW);
        lblPendientes.setFont(new Font("Segoe UI", Font.BOLD, 11));
        
        lblEnviadas = new JLabel("Enviadas: 0");
        lblEnviadas.setForeground(Color.WHITE);
        lblEnviadas.setFont(new Font("Segoe UI", Font.BOLD, 11));
        
        lblTotal = new JLabel("Total: 0");
        lblTotal.setForeground(Color.CYAN);
        lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 11));
        
        JLabel sep1 = new JLabel(" | ");
        sep1.setForeground(Color.WHITE);
        JLabel sep2 = new JLabel(" | ");
        sep2.setForeground(Color.WHITE);
        
        panel.add(lblPendientes);
        panel.add(sep1);
        panel.add(lblEnviadas);
        panel.add(sep2);
        panel.add(lblTotal);
        
        return panel;
    }
    
    private JPanel crearPanelFiltros() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(GRIS_CLARO);
        panel.setBorder(new TitledBorder("Filtros de Búsqueda"));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Búsqueda
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel(iconManager.withIcon("BUSCAR", "Buscar:")), gbc);
        
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        txtBusqueda = new JTextField(20);
        txtBusqueda.setToolTipText("Buscar por número, activo o diagnóstico");
        txtBusqueda.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent evt) {
                aplicarFiltros();
            }
        });
        panel.add(txtBusqueda, gbc);
        
        gbc.gridx = 2; gbc.weightx = 0; gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("Estado:"), gbc);
        
        gbc.gridx = 3;
        cmbFiltroEstado = new JComboBox<>();
        cmbFiltroEstado.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value == null) {
                    setText("-- Todos --");
                }
                return this;
            }
        });
        cmbFiltroEstado.addActionListener(e -> aplicarFiltros());
        panel.add(cmbFiltroEstado, gbc);
        
        gbc.gridx = 4;
        JButton btnLimpiar = new JButton("Limpiar");
        btnLimpiar.setBackground(VERDE_SECUNDARIO);
        btnLimpiar.setForeground(Color.WHITE);
        btnLimpiar.addActionListener(e -> limpiarFiltros());
        panel.add(btnLimpiar, gbc);
        
        return panel;
    }
    
    private JPanel crearPanelTabla() {
        JPanel panel = new JPanel(new BorderLayout());
        
        String[] columnas = {
            "ID", "Número", "Activo", "Técnico", "Fecha Creación",
            "Estado", "Fecha Envío", "Diagnóstico"
        };
        
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tablaFichas = new JTable(modeloTabla);
        tablaFichas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaFichas.setRowHeight(28);
        tablaFichas.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        tablaFichas.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 11));
        tablaFichas.getTableHeader().setBackground(VERDE_PRINCIPAL);
        tablaFichas.getTableHeader().setForeground(Color.WHITE);
        
        // Configurar anchos
        tablaFichas.getColumnModel().getColumn(0).setMaxWidth(50);
        tablaFichas.getColumnModel().getColumn(1).setPreferredWidth(120);
        tablaFichas.getColumnModel().getColumn(2).setPreferredWidth(150);
        tablaFichas.getColumnModel().getColumn(3).setPreferredWidth(120);
        tablaFichas.getColumnModel().getColumn(4).setPreferredWidth(130);
        tablaFichas.getColumnModel().getColumn(5).setPreferredWidth(100);
        tablaFichas.getColumnModel().getColumn(6).setPreferredWidth(130);
        tablaFichas.getColumnModel().getColumn(7).setPreferredWidth(250);
        
        // Renderer para estado
        tablaFichas.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (!isSelected && value != null) {
                    String estado = value.toString();
                    if (estado.equals("Borrador")) {
                        c.setBackground(new Color(255, 248, 220));
                        setForeground(new Color(200, 140, 0));
                    } else if (estado.equals("Enviada")) {
                        c.setBackground(new Color(220, 255, 220));
                        setForeground(new Color(0, 128, 0));
                    } else if (estado.equals("Archivada")) {
                        c.setBackground(new Color(220, 220, 220));
                        setForeground(new Color(100, 100, 100));
                    } else {
                        c.setBackground(Color.WHITE);
                        setForeground(Color.BLACK);
                    }
                }
                
                setHorizontalAlignment(CENTER);
                setFont(getFont().deriveFont(Font.BOLD));
                
                return c;
            }
        });
        
        // Centrar algunas columnas
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        tablaFichas.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        tablaFichas.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);
        tablaFichas.getColumnModel().getColumn(6).setCellRenderer(centerRenderer);
        
        // Doble clic
        tablaFichas.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    verDetallesFicha();
                }
            }
        });
        
        sorter = new TableRowSorter<>(modeloTabla);
        tablaFichas.setRowSorter(sorter);
        
        JScrollPane scrollPane = new JScrollPane(tablaFichas);
        scrollPane.setBorder(new TitledBorder("Lista de Fichas de Reporte"));
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel crearPanelBotones() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panel.setBackground(GRIS_CLARO);
        
        btnNuevaFicha = new JButton(iconManager.withIcon("NUEVO", "Nueva Ficha"));
        btnNuevaFicha.setBackground(VERDE_PRINCIPAL);
        btnNuevaFicha.setForeground(Color.WHITE);
        btnNuevaFicha.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnNuevaFicha.setPreferredSize(new Dimension(150, 35));
        btnNuevaFicha.addActionListener(e -> mostrarFormularioNuevo());
        
        btnVerDetalles = new JButton(iconManager.withIcon("VER", "Ver Detalles"));
        btnVerDetalles.setBackground(AZUL_INFO);
        btnVerDetalles.setForeground(Color.WHITE);
        btnVerDetalles.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnVerDetalles.setPreferredSize(new Dimension(140, 35));
        btnVerDetalles.addActionListener(e -> verDetallesFicha());
        
        btnEnviarFicha = new JButton(iconManager.withIcon("EMAIL", "Enviar a Jefe"));
        btnEnviarFicha.setBackground(NARANJA_WARNING);
        btnEnviarFicha.setForeground(Color.WHITE);
        btnEnviarFicha.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnEnviarFicha.setPreferredSize(new Dimension(150, 35));
        btnEnviarFicha.addActionListener(e -> enviarFicha());
        
        btnActualizar = new JButton(iconManager.withIcon("ACTUALIZAR", "Actualizar"));
        btnActualizar.setBackground(GRIS_OSCURO);
        btnActualizar.setForeground(Color.WHITE);
        btnActualizar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnActualizar.setPreferredSize(new Dimension(130, 35));
        btnActualizar.addActionListener(e -> actualizarTabla());
        
        panel.add(btnNuevaFicha);
        panel.add(btnVerDetalles);
        panel.add(btnEnviarFicha);
        panel.add(btnActualizar);
        
        return panel;
    }
    
    private JPanel crearPanelFormulario() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        
        // Título
        JPanel panelTituloForm = new JPanel(new BorderLayout());
        panelTituloForm.setBackground(VERDE_PRINCIPAL);
        panelTituloForm.setBorder(new EmptyBorder(15, 20, 15, 20));
        
        lblTituloFormulario = new JLabel(iconManager.withIcon("FICHA", "NUEVA FICHA DE REPORTE"));
        lblTituloFormulario.setFont(iconManager.getIconFont(18).deriveFont(Font.BOLD));
        lblTituloFormulario.setForeground(Color.WHITE);
        panelTituloForm.add(lblTituloFormulario, BorderLayout.WEST);
        
        panel.add(panelTituloForm, BorderLayout.NORTH);
        
        // Campos
        JPanel panelCampos = new JPanel(new GridBagLayout());
        panelCampos.setBackground(Color.WHITE);
        panelCampos.setBorder(new EmptyBorder(20, 30, 20, 30));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        int fila = 0;
        
        // Activo
        gbc.gridx = 0; gbc.gridy = fila; gbc.weightx = 0;
        JLabel lblActivo = new JLabel("* Activo:");
        lblActivo.setFont(new Font("Segoe UI", Font.BOLD, 12));
        panelCampos.add(lblActivo, gbc);
        
        gbc.gridx = 1; gbc.weightx = 1.0;
        cmbActivo = new JComboBox<>();
        cmbActivo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cmbActivo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Activo) {
                    Activo a = (Activo) value;
                    setText(a.getActNumeroActivo() + " - " + a.getActModelo());
                }
                return this;
            }
        });
        
        // Agregar listener para filtrar mantenimientos por activo
        cmbActivo.addActionListener(e -> {
            Activo activoSeleccionado = (Activo) cmbActivo.getSelectedItem();
            cargarMantenimientosPorActivo(activoSeleccionado);
        });
        
        panelCampos.add(cmbActivo, gbc);
        
        fila++;
        
        // Mantenimiento (opcional)
        gbc.gridx = 0; gbc.gridy = fila; gbc.weightx = 0;
        JLabel lblMant = new JLabel("Mantenimiento:");
        lblMant.setFont(new Font("Segoe UI", Font.BOLD, 12));
        panelCampos.add(lblMant, gbc);
        
        gbc.gridx = 1; gbc.weightx = 1.0;
        cmbMantenimiento = new JComboBox<>();
        cmbMantenimiento.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cmbMantenimiento.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value == null) {
                    setText("-- Sin Mantenimiento --");
                } else if (value instanceof Mantenimiento) {
                    Mantenimiento m = (Mantenimiento) value;
                    setText("ID: " + m.getMantId() + " - " + m.getMantTipo());
                }
                return this;
            }
        });
        panelCampos.add(cmbMantenimiento, gbc);
        
        fila++;
        
        // Problema Reportado
        gbc.gridx = 0; gbc.gridy = fila; gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        JLabel lblProblema = new JLabel("* Problema Reportado:");
        lblProblema.setFont(new Font("Segoe UI", Font.BOLD, 12));
        panelCampos.add(lblProblema, gbc);
        
        gbc.gridx = 1; gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        txtProblemaReportado = new JTextArea(4, 30);
        txtProblemaReportado.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtProblemaReportado.setLineWrap(true);
        txtProblemaReportado.setWrapStyleWord(true);
        JScrollPane scrollProblema = new JScrollPane(txtProblemaReportado);
        panelCampos.add(scrollProblema, gbc);
        
        fila++;
        
        // Diagnóstico
        gbc.gridx = 0; gbc.gridy = fila; gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        JLabel lblDiag = new JLabel("* Diagnóstico:");
        lblDiag.setFont(new Font("Segoe UI", Font.BOLD, 12));
        panelCampos.add(lblDiag, gbc);
        
        gbc.gridx = 1; gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        txtDiagnostico = new JTextArea(4, 30);
        txtDiagnostico.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtDiagnostico.setLineWrap(true);
        txtDiagnostico.setWrapStyleWord(true);
        JScrollPane scrollDiag = new JScrollPane(txtDiagnostico);
        panelCampos.add(scrollDiag, gbc);
        
        fila++;
        
        // Acción realizada
        gbc.gridx = 0; gbc.gridy = fila; gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        JLabel lblAccion = new JLabel("* Acción Realizada:");
        lblAccion.setFont(new Font("Segoe UI", Font.BOLD, 12));
        panelCampos.add(lblAccion, gbc);
        
        gbc.gridx = 1; gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        txtAccionRealizada = new JTextArea(4, 30);
        txtAccionRealizada.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtAccionRealizada.setLineWrap(true);
        txtAccionRealizada.setWrapStyleWord(true);
        JScrollPane scrollAccion = new JScrollPane(txtAccionRealizada);
        panelCampos.add(scrollAccion, gbc);
        
        fila++;
        
        // Componentes reemplazados
        gbc.gridx = 0; gbc.gridy = fila; gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        JLabel lblComp = new JLabel("Componentes Reemplazados:");
        lblComp.setFont(new Font("Segoe UI", Font.BOLD, 12));
        panelCampos.add(lblComp, gbc);
        
        gbc.gridx = 1; gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        txtComponentesReemplazados = new JTextArea(3, 30);
        txtComponentesReemplazados.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtComponentesReemplazados.setLineWrap(true);
        txtComponentesReemplazados.setWrapStyleWord(true);
        JScrollPane scrollComp = new JScrollPane(txtComponentesReemplazados);
        panelCampos.add(scrollComp, gbc);
        
        fila++;
        
        // Observaciones
        gbc.gridx = 0; gbc.gridy = fila; gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        JLabel lblObs = new JLabel("Observaciones:");
        lblObs.setFont(new Font("Segoe UI", Font.BOLD, 12));
        panelCampos.add(lblObs, gbc);
        
        gbc.gridx = 1; gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        txtObservaciones = new JTextArea(3, 30);
        txtObservaciones.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtObservaciones.setLineWrap(true);
        txtObservaciones.setWrapStyleWord(true);
        JScrollPane scrollObs = new JScrollPane(txtObservaciones);
        panelCampos.add(scrollObs, gbc);
        
        // Asignar alias para compatibilidad
        txtSolucionAplicada = txtAccionRealizada;
        txtComponentesCambio = txtComponentesReemplazados;
        
        JScrollPane scrollCampos = new JScrollPane(panelCampos);
        scrollCampos.setBorder(null);
        panel.add(scrollCampos, BorderLayout.CENTER);
        
        // Botones
        JPanel panelBotonesForm = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        panelBotonesForm.setBackground(GRIS_CLARO);
        
        JButton btnGuardar = new JButton(iconManager.withIcon("GUARDAR", "Guardar"));
        btnGuardar.setBackground(VERDE_PRINCIPAL);
        btnGuardar.setForeground(Color.WHITE);
        btnGuardar.setFont(iconManager.getIconFont(13).deriveFont(Font.BOLD));
        btnGuardar.setPreferredSize(new Dimension(140, 40));
        btnGuardar.addActionListener(e -> guardarFicha());
        
        JButton btnCancelar = new JButton(iconManager.withIcon("CANCELAR", "Cancelar"));
        btnCancelar.setBackground(ROJO_DANGER);
        btnCancelar.setForeground(Color.WHITE);
        btnCancelar.setFont(iconManager.getIconFont(13).deriveFont(Font.BOLD));
        btnCancelar.setPreferredSize(new Dimension(140, 40));
        btnCancelar.addActionListener(e -> volverAListado());
        
        panelBotonesForm.add(btnGuardar);
        panelBotonesForm.add(btnCancelar);
        
        panel.add(panelBotonesForm, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private void cargarDatosIniciales() {
        cargarCombos();
        actualizarTabla();
    }
    
    private void cargarCombos() {
        try {
            // Estados
            cmbFiltroEstado.addItem(null);
            for (EstadoFicha estado : EstadoFicha.values()) {
                cmbFiltroEstado.addItem(estado);
            }
            
            // Activos
            List<Activo> activos = activoDAO.findAll();
            DefaultComboBoxModel<Activo> modeloActivos = new DefaultComboBoxModel<>();
            for (Activo activo : activos) {
                modeloActivos.addElement(activo);
            }
            cmbActivo.setModel(modeloActivos);
            
            // Cargar mantenimientos inicialmente sin filtro
            cargarMantenimientosPorActivo(null);
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error al cargar datos: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Carga mantenimientos filtrados por activo seleccionado
     */
    private void cargarMantenimientosPorActivo(Activo activoSeleccionado) {
        try {
            cmbMantenimiento.removeAllItems();
            cmbMantenimiento.addItem(null); // Opción "Sin Mantenimiento"
            
            if (activoSeleccionado == null) {
                // Sin activo seleccionado, mostrar todos los mantenimientos
                List<Mantenimiento> mantenimientos = mantenimientoDAO.findAll();
                for (Mantenimiento m : mantenimientos) {
                    cmbMantenimiento.addItem(m);
                }
            } else {
                // Filtrar mantenimientos por activo
                List<Mantenimiento> mantenimientos = mantenimientoDAO.findAll();
                List<Mantenimiento> mantenimientosFiltrados = new ArrayList<>();
                
                for (Mantenimiento m : mantenimientos) {
                    if (m.getActId() != null && m.getActId().equals(activoSeleccionado.getActId())) {
                        mantenimientosFiltrados.add(m);
                    }
                }
                
                // Agregar mantenimientos filtrados al combo
                for (Mantenimiento m : mantenimientosFiltrados) {
                    cmbMantenimiento.addItem(m);
                }
                
                // Mostrar mensaje si no hay mantenimientos para este activo
                if (mantenimientosFiltrados.isEmpty()) {
                    // Crear item temporal para mostrar el mensaje
                    JOptionPane.showMessageDialog(this,
                        "No hay registros de mantenimiento para el activo seleccionado:\\n" + 
                        activoSeleccionado.getActNumeroActivo() + " - " + activoSeleccionado.getActModelo(),
                        "Información",
                        JOptionPane.INFORMATION_MESSAGE);
                }
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error al filtrar mantenimientos: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void actualizarTabla() {
        try {
            modeloTabla.setRowCount(0);
            List<FichaReporte> fichas = fichaReporteService.obtenerTodas();
            
            int borradores = 0;
            int enviadas = 0;
            
            for (FichaReporte ficha : fichas) {
                if (ficha.getFichaEstado() == EstadoFicha.Borrador) {
                    borradores++;
                } else if (ficha.getFichaEstado() == EstadoFicha.Enviada) {
                    enviadas++;
                }
                
                String numActivo = "";
                try {
                    if (ficha.getMantId() != null) {
                        Mantenimiento mant = mantenimientoDAO.findById(ficha.getMantId());
                        if (mant != null && mant.getActId() != null) {
                            Activo activo = activoDAO.findById(mant.getActId()).orElse(null);
                            if (activo != null) {
                                numActivo = activo.getActNumeroActivo();
                            }
                        }
                    }
                } catch (Exception ignored) {}
                
                String tecnico = "N/A";
                if (ficha.getCreadoPor() != null) {
                    try {
                        Usuario usu = usuarioDAO.findById(ficha.getCreadoPor()).orElse(null);
                        if (usu != null) {
                            tecnico = usu.getUsuNombre();
                        }
                    } catch (Exception ignored) {}
                }
                
                String fechaCreacion = ficha.getCreadoEn() != null ?
                    ficha.getCreadoEn().format(formateadorFechaHora) : "";
                    
                String fechaEnvio = "";
                if (ficha.getFichaEstado() == EstadoFicha.Enviada && ficha.getActualizadoEn() != null) {
                    fechaEnvio = ficha.getActualizadoEn().format(formateadorFechaHora);
                }
                
                String diagnosticoCorto = ficha.getFichaDiagnostico();
                if (diagnosticoCorto != null && diagnosticoCorto.length() > 50) {
                    diagnosticoCorto = diagnosticoCorto.substring(0, 47) + "...";
                }
                
                modeloTabla.addRow(new Object[]{
                    ficha.getFichaId(),
                    ficha.getFichaNumero(),
                    numActivo,
                    tecnico,
                    fechaCreacion,
                    ficha.getFichaEstado(),
                    fechaEnvio,
                    diagnosticoCorto
                });
            }
            
            lblPendientes.setText("Borradores: " + borradores);
            lblEnviadas.setText("Enviadas: " + enviadas);
            lblTotal.setText("Total: " + fichas.size());
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error al actualizar tabla: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void aplicarFiltros() {
        String textoBusqueda = txtBusqueda.getText().toLowerCase();
        EstadoFicha estadoSeleccionado = (EstadoFicha) cmbFiltroEstado.getSelectedItem();
        
        sorter.setRowFilter(new RowFilter<DefaultTableModel, Integer>() {
            @Override
            public boolean include(Entry<? extends DefaultTableModel, ? extends Integer> entry) {
                if (!textoBusqueda.isEmpty()) {
                    boolean encontrado = false;
                    for (int i = 0; i < entry.getValueCount(); i++) {
                        if (entry.getStringValue(i).toLowerCase().contains(textoBusqueda)) {
                            encontrado = true;
                            break;
                        }
                    }
                    if (!encontrado) return false;
                }
                
                if (estadoSeleccionado != null) {
                    String estadoFila = entry.getStringValue(5);
                    if (!estadoFila.equals(estadoSeleccionado.toString())) {
                        return false;
                    }
                }
                
                return true;
            }
        });
    }
    
    private void limpiarFiltros() {
        txtBusqueda.setText("");
        cmbFiltroEstado.setSelectedIndex(0);
        aplicarFiltros();
    }
    
    private void mostrarFormularioNuevo() {
        fichaEnEdicion = null;
        lblTituloFormulario.setText(iconManager.withIcon("NUEVO", "NUEVA FICHA DE REPORTE"));
        limpiarFormulario();
        cardLayout.show(panelContenedor, "FORMULARIO");
    }
    
    private void limpiarFormulario() {
        cmbMantenimiento.setSelectedIndex(-1);
        if (txtProblemaReportado != null) {
            txtProblemaReportado.setText("");
        }
        txtDiagnostico.setText("");
        txtSolucionAplicada.setText("");
        txtComponentesCambio.setText("");
        txtObservaciones.setText("");
    }
    
    private void guardarFicha() {
        try {
            // Validaciones
            if (cmbMantenimiento.getSelectedItem() == null) {
                JOptionPane.showMessageDialog(this, "Debe seleccionar un mantenimiento", "Validación", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            if (txtDiagnostico.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "El diagnóstico es obligatorio", "Validación", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            if (txtSolucionAplicada.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "La solución aplicada es obligatoria", "Validación", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Crear ficha
            FichaReporte ficha = new FichaReporte();
            
            Mantenimiento mantSel = (Mantenimiento) cmbMantenimiento.getSelectedItem();
            ficha.setMantId(mantSel.getMantId());
            
            // Problema Reportado es obligatorio
            if (txtProblemaReportado == null || txtProblemaReportado.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "El campo 'Problema Reportado' es obligatorio.",
                    "Error de Validación",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            ficha.setFichaProblemaReportado(txtProblemaReportado.getText().trim());
            
            ficha.setFichaDiagnostico(txtDiagnostico.getText().trim());
            ficha.setFichaSolucionAplicada(txtSolucionAplicada.getText().trim());
            ficha.setFichaComponentesCambio(txtComponentesCambio.getText().trim());
            ficha.setFichaObservaciones(txtObservaciones.getText().trim());
            ficha.setCreadoPor(usuarioActual.getUsuId());
            
            ficha = fichaReporteService.crearFicha(ficha);
            
            JOptionPane.showMessageDialog(this,
                "Ficha registrada exitosamente\nNúmero: " + ficha.getFichaNumero(),
                "Éxito",
                JOptionPane.INFORMATION_MESSAGE);
            
            volverAListado();
            actualizarTabla();
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error al guardar ficha: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void verDetallesFicha() {
        int filaSeleccionada = tablaFichas.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this,
                "Debe seleccionar una ficha",
                "Validación",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int filaModelo = tablaFichas.convertRowIndexToModel(filaSeleccionada);
        Integer fichaId = (Integer) modeloTabla.getValueAt(filaModelo, 0);
        
        try {
            FichaReporte ficha = fichaReporteService.buscarPorId(fichaId);
            if (ficha != null) {
                mostrarDialogoDetalles(ficha);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error al obtener detalles: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void mostrarDialogoDetalles(FichaReporte ficha) {
        JDialog dialogo = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), 
                                     iconManager.withIcon("FICHA", "Detalles de la Ficha"), true);
        dialogo.setSize(800, 700);
        dialogo.setLocationRelativeTo(this);
        
        // Panel principal
        JPanel panelPrincipal = new JPanel(new BorderLayout());
        panelPrincipal.setBackground(Color.WHITE);
        
        // Header con título y estado
        JPanel panelHeader = crearPanelHeader(ficha);
        panelPrincipal.add(panelHeader, BorderLayout.NORTH);
        
        // Contenido principal con scroll
        JPanel panelContenido = crearPanelContenidoDetalles(ficha);
        JScrollPane scrollContenido = new JScrollPane(panelContenido);
        scrollContenido.setBorder(null);
        scrollContenido.getVerticalScrollBar().setUnitIncrement(16);
        panelPrincipal.add(scrollContenido, BorderLayout.CENTER);
        
        // Botones
        JPanel panelBotones = crearPanelBotonesDetalles(dialogo, ficha);
        panelPrincipal.add(panelBotones, BorderLayout.SOUTH);
        
        dialogo.add(panelPrincipal);
        dialogo.setVisible(true);
    }
    
    private JPanel crearPanelHeader(FichaReporte ficha) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(VERDE_PRINCIPAL);
        panel.setBorder(new EmptyBorder(20, 25, 20, 25));
        
        // Título principal
        JLabel lblTitulo = new JLabel(iconManager.withIcon("FICHA", "FICHA DE REPORTE CORRECTIVO"));
        lblTitulo.setFont(iconManager.getIconFont(20).deriveFont(Font.BOLD));
        lblTitulo.setForeground(Color.WHITE);
        
        // Información del número y estado
        JPanel panelInfo = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        panelInfo.setOpaque(false);
        
        JLabel lblNumero = new JLabel("N° " + ficha.getFichaNumero());
        lblNumero.setFont(iconManager.getIconFont(16).deriveFont(Font.BOLD));
        lblNumero.setForeground(Color.WHITE);
        
        JLabel lblEstado = new JLabel();
        lblEstado.setFont(iconManager.getIconFont(14).deriveFont(Font.BOLD));
        lblEstado.setOpaque(true);
        lblEstado.setBorder(new EmptyBorder(5, 10, 5, 10));
        
        // Configurar color según estado
        Color colorEstado;
        String textoEstado;
        switch (ficha.getFichaEstado()) {
            case Enviada:
                colorEstado = new Color(76, 175, 80); // Verde
                textoEstado = iconManager.withIcon("ESTADO_ENVIADA", "ENVIADA");
                break;
            case Archivada:
                colorEstado = new Color(158, 158, 158); // Gris
                textoEstado = iconManager.withIcon("ESTADO_ARCHIVADA", "ARCHIVADA");
                break;
            default:
                colorEstado = new Color(255, 152, 0); // Naranja
                textoEstado = iconManager.withIcon("ESTADO_BORRADOR", "BORRADOR");
                break;
        }
        
        lblEstado.setBackground(colorEstado);
        lblEstado.setForeground(Color.WHITE);
        lblEstado.setText(textoEstado);
        
        panelInfo.add(lblNumero);
        panelInfo.add(Box.createHorizontalStrut(15));
        panelInfo.add(lblEstado);
        
        panel.add(lblTitulo, BorderLayout.WEST);
        panel.add(panelInfo, BorderLayout.EAST);
        
        return panel;
    }
    
    private JPanel crearPanelContenidoDetalles(FichaReporte ficha) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(25, 25, 25, 25));
        
        JPanel panelCampos = new JPanel(new GridBagLayout());
        panelCampos.setBackground(Color.WHITE);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 0, 12, 0);
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        
        int fila = 0;
        
        // Información del activo
        try {
            Mantenimiento mant = mantenimientoDAO.findById(ficha.getMantId());
            Activo activo = null;
            if (mant != null) {
                activo = activoDAO.findById(mant.getActId()).orElse(null);
            }
            
            if (activo != null) {
                gbc.gridy = fila++;
                panelCampos.add(crearSeccionInfo(iconManager.withIcon("ACTIVO", "Activo"), activo.getActNumeroActivo()), gbc);
                
                gbc.gridy = fila++;
                panelCampos.add(crearSeccionInfo(iconManager.withIcon("COMPUTER", "Modelo"), activo.getActModelo()), gbc);
            }
        } catch (Exception e) {
            // Ignorar errores de carga de activo
        }
        
        // Fechas
        gbc.gridy = fila++;
        panelCampos.add(crearSeccionInfo(iconManager.withIcon("FECHA", "Fecha de Creación"), 
            ficha.getCreadoEn().format(formateadorFechaHora)), gbc);
        
        if (ficha.getFichaEstado() == EstadoFicha.Enviada) {
            gbc.gridy = fila++;
            panelCampos.add(crearSeccionInfo(iconManager.withIcon("ENVIAR", "Fecha de Envío"), 
                ficha.getActualizadoEn().format(formateadorFechaHora)), gbc);
        }
        
        // Problema reportado
        if (ficha.getFichaProblemaReportado() != null && !ficha.getFichaProblemaReportado().trim().isEmpty()) {
            gbc.gridy = fila++;
            panelCampos.add(crearSeccionTexto(iconManager.withIcon("PROBLEMA", "Problema Reportado"), 
                ficha.getFichaProblemaReportado()), gbc);
        }
        
        // Diagnóstico
        if (ficha.getFichaDiagnostico() != null && !ficha.getFichaDiagnostico().trim().isEmpty()) {
            gbc.gridy = fila++;
            panelCampos.add(crearSeccionTexto(iconManager.withIcon("BUSCAR", "Diagnóstico"), 
                ficha.getFichaDiagnostico()), gbc);
        }
        
        // Solución aplicada
        if (ficha.getFichaSolucionAplicada() != null && !ficha.getFichaSolucionAplicada().trim().isEmpty()) {
            gbc.gridy = fila++;
            panelCampos.add(crearSeccionTexto(iconManager.withIcon("HERRAMIENTAS", "Acción Realizada"), 
                ficha.getFichaSolucionAplicada()), gbc);
        }
        
        // Componentes reemplazados
        if (ficha.getFichaComponentesCambio() != null && !ficha.getFichaComponentesCambio().trim().isEmpty()) {
            gbc.gridy = fila++;
            panelCampos.add(crearSeccionTexto(iconManager.withIcon("COMPONENTES", "Componentes Reemplazados"), 
                ficha.getFichaComponentesCambio()), gbc);
        }
        
        // Observaciones
        if (ficha.getFichaObservaciones() != null && !ficha.getFichaObservaciones().trim().isEmpty()) {
            gbc.gridy = fila++;
            panelCampos.add(crearSeccionTexto(iconManager.withIcon("OBSERVACIONES", "Observaciones"), 
                ficha.getFichaObservaciones()), gbc);
        }
        
        panel.add(panelCampos, BorderLayout.NORTH);
        return panel;
    }
    
    private JPanel crearSeccionInfo(String titulo, String valor) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(GRIS_CLARO);
        panel.setBorder(new EmptyBorder(15, 20, 15, 20));
        
        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTitulo.setForeground(GRIS_OSCURO);
        
        JLabel lblValor = new JLabel(valor != null ? valor : "No especificado");
        lblValor.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblValor.setForeground(Color.BLACK);
        
        panel.add(lblTitulo, BorderLayout.NORTH);
        panel.add(lblValor, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel crearSeccionTexto(String titulo, String contenido) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(GRIS_CLARO, 1),
            new EmptyBorder(15, 20, 15, 20)
        ));
        
        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTitulo.setForeground(GRIS_OSCURO);
        lblTitulo.setBorder(new EmptyBorder(0, 0, 10, 0));
        
        JTextArea txtContenido = new JTextArea(contenido != null ? contenido : "No especificado");
        txtContenido.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtContenido.setForeground(Color.BLACK);
        txtContenido.setBackground(Color.WHITE);
        txtContenido.setEditable(false);
        txtContenido.setLineWrap(true);
        txtContenido.setWrapStyleWord(true);
        txtContenido.setBorder(null);
        
        panel.add(lblTitulo, BorderLayout.NORTH);
        panel.add(txtContenido, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel crearPanelBotonesDetalles(JDialog dialogo, FichaReporte ficha) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 20));
        panel.setBackground(GRIS_CLARO);
        panel.setBorder(new EmptyBorder(10, 20, 10, 20));
        
        // Botón Cerrar
        JButton btnCerrar = new JButton(iconManager.withIcon("CERRAR", "Cerrar"));
        btnCerrar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnCerrar.setBackground(GRIS_OSCURO);
        btnCerrar.setForeground(Color.WHITE);
        btnCerrar.setPreferredSize(new Dimension(120, 40));
        btnCerrar.addActionListener(e -> dialogo.dispose());
        
        panel.add(btnCerrar);
        
        // Botón Enviar (solo si está en borrador)
        if (ficha.getFichaEstado() == EstadoFicha.Borrador) {
            JButton btnEnviar = new JButton(iconManager.withIcon("ENVIAR", "Enviar al Supervisor"));
            btnEnviar.setFont(new Font("Segoe UI", Font.BOLD, 13));
            btnEnviar.setBackground(VERDE_PRINCIPAL);
            btnEnviar.setForeground(Color.WHITE);
            btnEnviar.setPreferredSize(new Dimension(180, 40));
            btnEnviar.addActionListener(e -> {
                dialogo.dispose();
                enviarFichaDesdeDetalles(ficha.getFichaId());
            });
            panel.add(btnEnviar);
        }
        
        return panel;
    }
    
    private void enviarFichaDesdeDetalles(Integer fichaId) {
        try {
            FichaReporte ficha = fichaReporteService.buscarPorId(fichaId);
            if (ficha == null) {
                JOptionPane.showMessageDialog(this, "Ficha no encontrada", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Verificar que hay supervisores configurados
            List<Usuario> jefes = usuarioDAO.findByRol(Usuario.Rol.Jefe_Informatica);
            if (jefes.isEmpty()) {
                int opcion = JOptionPane.showOptionDialog(this,
                    iconManager.withIcon("WARNING", "No hay supervisores (Jefes de Informática) configurados para recibir las fichas.") + "\n\n" +
                    "¿Desea configurar uno ahora?",
                    "Sin Supervisores Configurados",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE,
                    null,
                    new String[]{"Configurar Supervisor", "Cancelar"},
                    "Configurar Supervisor");
                
                if (opcion == 0) {
                    mostrarConfiguracionSupervisores();
                }
                return;
            }
            
            // Mostrar lista de supervisores y confirmar envío
            StringBuilder supervisores = new StringBuilder();
            for (Usuario jefe : jefes) {
                supervisores.append("• ").append(jefe.getUsuNombre())
                          .append(" (").append(jefe.getUsuEmail()).append(")\n");
            }
            
            int confirmar = JOptionPane.showConfirmDialog(this,
                "📤 Se enviará la ficha " + ficha.getFichaNumero() + " a los siguientes supervisores:\n\n" +
                supervisores.toString() + "\n¿Continuar con el envío?",
                "Confirmar Envío",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
            
            if (confirmar != JOptionPane.YES_OPTION) {
                return;
            }
            
            boolean exito = fichaReporteService.enviarFichaAJefe(fichaId);
            
            if (exito) {
                JOptionPane.showMessageDialog(this,
                    "✅ Ficha enviada exitosamente por email a los supervisores",
                    "Envío Exitoso",
                    JOptionPane.INFORMATION_MESSAGE);
                actualizarTabla();
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "❌ Error al enviar ficha: " + e.getMessage(),
                "Error de Envío",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void mostrarConfiguracionSupervisores() {
        JDialog dialogo = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), 
                                     "⚙️ Configuración de Supervisores", true);
        dialogo.setSize(600, 400);
        dialogo.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Título
        JPanel panelTitulo = new JPanel(new BorderLayout());
        panelTitulo.setBackground(VERDE_PRINCIPAL);
        panelTitulo.setBorder(new EmptyBorder(15, 20, 15, 20));
        
        JLabel lblTitulo = new JLabel(iconManager.withIcon("SUPERVISOR", "Gestión de Supervisores"));
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitulo.setForeground(Color.WHITE);
        panelTitulo.add(lblTitulo);
        
        panel.add(panelTitulo, BorderLayout.NORTH);
        
        // Información
        JTextArea txtInfo = new JTextArea(
            "Los supervisores (usuarios con rol 'Jefe_Informatica') reciben por email " +
            "las fichas de reporte cuando son enviadas por los técnicos.\n\n" +
            "Para configurar supervisores:\n" +
            "1. Vaya al módulo 'Usuarios'\n" +
            "2. Cree o edite usuarios\n" +
            "3. Asigne el rol 'Jefe_Informatica'\n" + 
            "4. Configure un email válido\n\n" +
            "Nota: Debe tener al menos un supervisor configurado para poder enviar fichas."
        );
        txtInfo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtInfo.setBackground(GRIS_CLARO);
        txtInfo.setEditable(false);
        txtInfo.setLineWrap(true);
        txtInfo.setWrapStyleWord(true);
        txtInfo.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        panel.add(txtInfo, BorderLayout.CENTER);
        
        // Botones
        JPanel panelBotones = new JPanel(new FlowLayout());
        panelBotones.setBackground(Color.WHITE);
        
        JButton btnIrUsuarios = new JButton(iconManager.withIcon("USUARIOS", "Ir a Usuarios"));
        btnIrUsuarios.setBackground(VERDE_PRINCIPAL);
        btnIrUsuarios.setForeground(Color.WHITE);
        btnIrUsuarios.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnIrUsuarios.addActionListener(e -> {
            dialogo.dispose();
            // Aquí podrías agregar lógica para navegar al módulo de usuarios
            JOptionPane.showMessageDialog(this, 
                "Navegue al módulo 'Usuarios' desde el menú principal", 
                "Información", 
                JOptionPane.INFORMATION_MESSAGE);
        });
        
        JButton btnCerrar = new JButton("Cerrar");
        btnCerrar.setBackground(GRIS_OSCURO);
        btnCerrar.setForeground(Color.WHITE);
        btnCerrar.addActionListener(e -> dialogo.dispose());
        
        panelBotones.add(btnIrUsuarios);
        panelBotones.add(btnCerrar);
        
        panel.add(panelBotones, BorderLayout.SOUTH);
        
        dialogo.add(panel);
        dialogo.setVisible(true);
    }
    
    private void enviarFicha() {
        int filaSeleccionada = tablaFichas.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this,
                "Debe seleccionar una ficha",
                "Validación",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int filaModelo = tablaFichas.convertRowIndexToModel(filaSeleccionada);
        Integer fichaId = (Integer) modeloTabla.getValueAt(filaModelo, 0);
        
        try {
            FichaReporte ficha = fichaReporteService.buscarPorId(fichaId);
            if (ficha == null) {
                JOptionPane.showMessageDialog(this, "Ficha no encontrada", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (ficha.getFichaEstado() == EstadoFicha.Enviada) {
                JOptionPane.showMessageDialog(this,
                    "Esta ficha ya fue enviada",
                    "Información",
                    JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            
            // Verificar que hay supervisores configurados
            List<Usuario> jefes = usuarioDAO.findByRol(Usuario.Rol.Jefe_Informatica);
            if (jefes.isEmpty()) {
                int opcion = JOptionPane.showOptionDialog(this,
                    iconManager.withIcon("WARNING", "No hay supervisores (Jefes de Informática) configurados para recibir las fichas.") + "\n\n" +
                    "¿Desea configurar uno ahora?",
                    "Sin Supervisores Configurados",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE,
                    null,
                    new String[]{"Configurar Supervisor", "Cancelar"},
                    "Configurar Supervisor");
                
                if (opcion == 0) {
                    mostrarConfiguracionSupervisores();
                }
                return;
            }
            
            // Mostrar lista de supervisores y confirmar envío
            StringBuilder supervisores = new StringBuilder();
            for (Usuario jefe : jefes) {
                supervisores.append("• ").append(jefe.getUsuNombre())
                          .append(" (").append(jefe.getUsuEmail()).append(")\n");
            }
            
            int confirmar = JOptionPane.showConfirmDialog(this,
                iconManager.withIcon("ENVIAR", "Se enviará la ficha " + ficha.getFichaNumero() + " a los siguientes supervisores:") + "\n\n" +
                supervisores.toString() + "\n¿Continuar con el envío?",
                "Confirmar Envío",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
            
            if (confirmar != JOptionPane.YES_OPTION) {
                return;
            }
            
            boolean exito = fichaReporteService.enviarFichaAJefe(fichaId);
            
            if (exito) {
                JOptionPane.showMessageDialog(this,
                    iconManager.withIcon("SUCCESS", "Ficha enviada exitosamente por email a los supervisores"),
                    "Envío Exitoso",
                    JOptionPane.INFORMATION_MESSAGE);
                actualizarTabla();
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                iconManager.withIcon("ERROR", "Error al enviar ficha: " + e.getMessage()),
                "Error de Envío",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void volverAListado() {
        cardLayout.show(panelContenedor, "LISTADO");
        actualizarTabla();
    }
}
