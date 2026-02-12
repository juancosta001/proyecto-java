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
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
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
import com.ypacarai.cooperativa.activos.dao.UbicacionDAO;
import com.ypacarai.cooperativa.activos.model.Activo;
import com.ypacarai.cooperativa.activos.model.Traslado;
import com.ypacarai.cooperativa.activos.model.Traslado.EstadoTraslado;
import com.ypacarai.cooperativa.activos.model.Ubicacion;
import com.ypacarai.cooperativa.activos.model.Usuario;
import com.ypacarai.cooperativa.activos.service.TrasladoService;
import com.ypacarai.cooperativa.activos.util.IconManager;

/**
 * Panel completo de gestión de traslados de activos
 * Gestiona traslados entre Casa Central y Sucursales
 * Sistema de Gestión de Activos - Cooperativa Ypacaraí LTDA
 */
public class TrasladosPanel extends JPanel {

    // Colores corporativos (mismos que SistemaTicketsPanel)
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
    
    // Panel principal de tabla
    private JTable tablaTraslados;
    private DefaultTableModel modeloTabla;
    private TableRowSorter<DefaultTableModel> sorter;
    
    // Filtros de búsqueda
    private JTextField txtBusqueda;
    private JComboBox<EstadoTraslado> cmbFiltroEstado;
    private JComboBox<Ubicacion> cmbFiltroUbicacion;
    
    // Botones de acción
    private JButton btnNuevoTraslado;
    private JButton btnVerDetalles;
    private JButton btnConfirmarEntrega;
    private JButton btnConfirmarDevolucion;
    private JButton btnActualizar;
    private JButton btnCancelar;
    
    // Panel de formulario de traslado
    private JComboBox<Activo> cmbActivo;
    private JComboBox<Ubicacion> cmbUbicacionOrigen;
    private JComboBox<Ubicacion> cmbUbicacionDestino;
    private JSpinner spnFechaSalida;
    private JSpinner spnFechaDevolucionProg;
    private JTextField txtMotivo;
    private JTextField txtResponsableEnvio;
    private JTextField txtResponsableRecibo;
    private JTextArea txtObservaciones;
    
    // Variables de control
    private Traslado trasladoEnEdicion;
    private JLabel lblTituloFormulario;
    
    // Etiquetas de estadísticas
    private JLabel lblProgramados;
    private JLabel lblEnTransito;
    private JLabel lblPendientesDevolucion;
    
    // Servicios y DAOs
    private TrasladoService trasladoService;
    private ActivoDAO activoDAO;
    private UbicacionDAO ubicacionDAO;
    
    private DateTimeFormatter formateadorFecha = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private DateTimeFormatter formateadorFechaHora = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    
    public TrasladosPanel(Usuario usuarioActual) {
        this.usuarioActual = usuarioActual;
        
        inicializarServicios();
        configurarPanel();
        crearInterfaz();
        cargarDatosIniciales();
    }
    
    private void inicializarServicios() {
        try {
            this.trasladoService = new TrasladoService();
            this.activoDAO = new ActivoDAO();
            this.ubicacionDAO = new UbicacionDAO();
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
        
        // Panel central con filtros y tabla
        JPanel panelCentral = new JPanel(new BorderLayout());
        
        // Panel de filtros
        JPanel panelFiltros = crearPanelFiltros();
        panelCentral.add(panelFiltros, BorderLayout.NORTH);
        
        // Tabla de traslados
        JPanel panelTabla = crearPanelTabla();
        panelCentral.add(panelTabla, BorderLayout.CENTER);
        
        panel.add(panelCentral, BorderLayout.CENTER);
        
        // Panel de botones de acción
        JPanel panelBotones = crearPanelBotones();
        panel.add(panelBotones, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel crearPanelTitulo() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(PURPLE_INFO);
        panel.setBorder(new EmptyBorder(15, 20, 15, 20));

        JLabel lblTitulo = new JLabel(iconManager.withIcon("TRASLADO", "GESTIÓN DE TRASLADOS DE ACTIVOS"));
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitulo.setForeground(Color.WHITE);

        JLabel lblSubtitulo = new JLabel("Control de traslados entre Casa Central y Sucursales");
        lblSubtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSubtitulo.setForeground(Color.WHITE);

        JPanel panelTextos = new JPanel(new BorderLayout());
        panelTextos.setOpaque(false);
        panelTextos.add(lblTitulo, BorderLayout.NORTH);
        panelTextos.add(lblSubtitulo, BorderLayout.SOUTH);

        panel.add(panelTextos, BorderLayout.WEST);
        
        // Panel de estadísticas rápidas
        JPanel panelEstadisticas = crearPanelEstadisticas();
        panel.add(panelEstadisticas, BorderLayout.EAST);

        return panel;
    }
    
    private JPanel crearPanelEstadisticas() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        panel.setOpaque(false);
        
        lblProgramados = new JLabel("Programados: 0");
        lblProgramados.setForeground(Color.WHITE);
        lblProgramados.setFont(new Font("Segoe UI", Font.BOLD, 11));
        
        lblEnTransito = new JLabel("En Tránsito: 0");
        lblEnTransito.setForeground(Color.YELLOW);
        lblEnTransito.setFont(new Font("Segoe UI", Font.BOLD, 11));
        
        lblPendientesDevolucion = new JLabel("Pendientes Dev.: 0");
        lblPendientesDevolucion.setForeground(Color.ORANGE);
        lblPendientesDevolucion.setFont(new Font("Segoe UI", Font.BOLD, 11));
        
        JLabel separador1 = new JLabel(" | ");
        separador1.setForeground(Color.WHITE);
        JLabel separador2 = new JLabel(" | ");
        separador2.setForeground(Color.WHITE);
        
        panel.add(lblProgramados);
        panel.add(separador1);
        panel.add(lblEnTransito);
        panel.add(separador2);
        panel.add(lblPendientesDevolucion);
        
        return panel;
    }
    
    private JPanel crearPanelFiltros() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(GRIS_CLARO);
        panel.setBorder(new TitledBorder("Filtros de Búsqueda"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Primera fila: Búsqueda y Estado
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel(iconManager.withIcon("BUSCAR", "Buscar:")), gbc);
        
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        txtBusqueda = new JTextField(20);
        txtBusqueda.setToolTipText("Buscar por número, activo o responsable");
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
                    setText("-- Todos los estados --");
                }
                return this;
            }
        });
        cmbFiltroEstado.addActionListener(e -> aplicarFiltros());
        panel.add(cmbFiltroEstado, gbc);

        // Segunda fila: Ubicación y Limpiar
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Ubicación:"), gbc);
        
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        cmbFiltroUbicacion = new JComboBox<>();
        cmbFiltroUbicacion.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value == null) {
                    setText("-- Todas las ubicaciones --");
                } else if (value instanceof Ubicacion) {
                    setText(((Ubicacion) value).getUbiNombre());
                }
                return this;
            }
        });
        cmbFiltroUbicacion.addActionListener(e -> aplicarFiltros());
        panel.add(cmbFiltroUbicacion, gbc);

        gbc.gridx = 2; gbc.gridwidth = 2; gbc.weightx = 0; gbc.fill = GridBagConstraints.NONE;
        JButton btnLimpiarFiltros = new JButton("Limpiar Filtros");
        btnLimpiarFiltros.setBackground(VERDE_SECUNDARIO);
        btnLimpiarFiltros.setForeground(Color.WHITE);
        btnLimpiarFiltros.addActionListener(e -> limpiarFiltros());
        panel.add(btnLimpiarFiltros, gbc);

        return panel;
    }
    
    private JPanel crearPanelTabla() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Crear modelo de tabla
        String[] columnas = {
            "ID", "Número", "Activo", "Origen", "Destino", 
            "Fecha Salida", "Fecha Dev. Prog", "Estado", 
            "Responsable Envío", "Motivo"
        };
        
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Tabla de solo lectura
            }
        };
        
        tablaTraslados = new JTable(modeloTabla);
        tablaTraslados.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaTraslados.setRowHeight(28);
        tablaTraslados.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        tablaTraslados.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 11));
        tablaTraslados.getTableHeader().setBackground(PURPLE_INFO);
        tablaTraslados.getTableHeader().setForeground(Color.WHITE);
        
        // Configurar anchos de columnas
        tablaTraslados.getColumnModel().getColumn(0).setMaxWidth(50);   // ID
        tablaTraslados.getColumnModel().getColumn(1).setPreferredWidth(120); // Número
        tablaTraslados.getColumnModel().getColumn(2).setPreferredWidth(120); // Activo
        tablaTraslados.getColumnModel().getColumn(3).setPreferredWidth(120); // Origen
        tablaTraslados.getColumnModel().getColumn(4).setPreferredWidth(120); // Destino
        tablaTraslados.getColumnModel().getColumn(5).setPreferredWidth(110); // Fecha Salida
        tablaTraslados.getColumnModel().getColumn(6).setPreferredWidth(110); // Fecha Dev. Prog
        tablaTraslados.getColumnModel().getColumn(7).setPreferredWidth(100); // Estado
        tablaTraslados.getColumnModel().getColumn(8).setPreferredWidth(120); // Responsable
        tablaTraslados.getColumnModel().getColumn(9).setPreferredWidth(200); // Motivo
        
        // Renderer personalizado para la columna de Estado
        tablaTraslados.getColumnModel().getColumn(7).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (!isSelected && value != null) {
                    String estado = value.toString();
                    switch (estado) {
                        case "Programado":
                            c.setBackground(new Color(240, 240, 240));
                            setForeground(GRIS_OSCURO);
                            break;
                        case "En_Transito":
                            c.setBackground(new Color(255, 248, 220));
                            setForeground(new Color(200, 140, 0));
                            break;
                        case "Entregado":
                            c.setBackground(new Color(220, 255, 220));
                            setForeground(new Color(0, 128, 0));
                            break;
                        case "Devuelto":
                            c.setBackground(new Color(220, 240, 255));
                            setForeground(AZUL_INFO);
                            break;
                        default:
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
        tablaTraslados.getColumnModel().getColumn(0).setCellRenderer(centerRenderer); // ID
        tablaTraslados.getColumnModel().getColumn(5).setCellRenderer(centerRenderer); // Fecha Salida
        tablaTraslados.getColumnModel().getColumn(6).setCellRenderer(centerRenderer); // Fecha Dev. Prog
        
        // Agregar listener para doble clic
        tablaTraslados.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    verDetallesTraslado();
                }
            }
        });
        
        // Configurar sorter para filtros
        sorter = new TableRowSorter<>(modeloTabla);
        tablaTraslados.setRowSorter(sorter);
        
        JScrollPane scrollPane = new JScrollPane(tablaTraslados);
        scrollPane.setBorder(new TitledBorder("Lista de Traslados"));
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel crearPanelBotones() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panel.setBackground(GRIS_CLARO);
        
        btnNuevoTraslado = new JButton(iconManager.withIcon("NUEVO", "Nuevo Traslado"));
        btnNuevoTraslado.setBackground(VERDE_PRINCIPAL);
        btnNuevoTraslado.setForeground(Color.WHITE);
        btnNuevoTraslado.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnNuevoTraslado.setPreferredSize(new Dimension(160, 35));
        btnNuevoTraslado.addActionListener(e -> mostrarFormularioNuevo());
        
        btnVerDetalles = new JButton(iconManager.withIcon("VER", "Ver Detalles"));
        btnVerDetalles.setBackground(AZUL_INFO);
        btnVerDetalles.setForeground(Color.WHITE);
        btnVerDetalles.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnVerDetalles.setPreferredSize(new Dimension(140, 35));
        btnVerDetalles.addActionListener(e -> verDetallesTraslado());
        
        btnConfirmarEntrega = new JButton(iconManager.withIcon("SUCCESS", "Confirmar Entrega"));
        btnConfirmarEntrega.setBackground(VERDE_SECUNDARIO);
        btnConfirmarEntrega.setForeground(Color.WHITE);
        btnConfirmarEntrega.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnConfirmarEntrega.setPreferredSize(new Dimension(170, 35));
        btnConfirmarEntrega.addActionListener(e -> confirmarEntrega());
        
        btnConfirmarDevolucion = new JButton(iconManager.withIcon("ACTUALIZAR", "Confirmar Devolución"));
        btnConfirmarDevolucion.setBackground(PURPLE_INFO);
        btnConfirmarDevolucion.setForeground(Color.WHITE);
        btnConfirmarDevolucion.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnConfirmarDevolucion.setPreferredSize(new Dimension(180, 35));
        btnConfirmarDevolucion.addActionListener(e -> confirmarDevolucion());
        
        btnActualizar = new JButton(iconManager.withIcon("ACTUALIZAR", "Actualizar"));
        btnActualizar.setBackground(GRIS_OSCURO);
        btnActualizar.setForeground(Color.WHITE);
        btnActualizar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnActualizar.setPreferredSize(new Dimension(130, 35));
        btnActualizar.addActionListener(e -> actualizarTabla());
        
        panel.add(btnNuevoTraslado);
        panel.add(btnVerDetalles);
        panel.add(btnConfirmarEntrega);
        panel.add(btnConfirmarDevolucion);
        panel.add(btnActualizar);
        
        return panel;
    }
    
    private JPanel crearPanelFormulario() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        
        // Panel de título
        JPanel panelTituloForm = new JPanel(new BorderLayout());
        panelTituloForm.setBackground(VERDE_PRINCIPAL);
        panelTituloForm.setBorder(new EmptyBorder(15, 20, 15, 20));
        
        lblTituloFormulario = new JLabel(iconManager.withIcon("NUEVO", "NUEVO TRASLADO DE ACTIVO"));
        lblTituloFormulario.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTituloFormulario.setForeground(Color.WHITE);
        panelTituloForm.add(lblTituloFormulario, BorderLayout.WEST);
        
        panel.add(panelTituloForm, BorderLayout.NORTH);
        
        // Panel de campos del formulario
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
                    Activo activo = (Activo) value;
                    setText(activo.getActNumeroActivo() + " - " + activo.getActModelo());
                }
                return this;
            }
        });
        // Listener para autorellenar ubicación origen con la del activo
        cmbActivo.addActionListener(e -> {
            Activo activoSeleccionado = (Activo) cmbActivo.getSelectedItem();
            if (activoSeleccionado != null) {
                try {
                    Integer ubicacionActualId = activoSeleccionado.getActUbicacionActual();
                    if (ubicacionActualId != null) {
                        // Buscar la ubicación en el combo
                        for (int i = 0; i < cmbUbicacionOrigen.getItemCount(); i++) {
                            Ubicacion ubi = cmbUbicacionOrigen.getItemAt(i);
                            if (ubi != null && ubi.getUbiId().equals(ubicacionActualId)) {
                                cmbUbicacionOrigen.setSelectedIndex(i);
                                break;
                            }
                        }
                    }
                } catch (Exception ex) {
                    System.err.println("Error al autorellenar ubicación: " + ex.getMessage());
                }
            }
        });
        panelCampos.add(cmbActivo, gbc);
        
        fila++;
        
        // Ubicación Origen
        gbc.gridx = 0; gbc.gridy = fila; gbc.weightx = 0;
        JLabel lblOrigen = new JLabel("* Ubicación Origen:");
        lblOrigen.setFont(new Font("Segoe UI", Font.BOLD, 12));
        panelCampos.add(lblOrigen, gbc);
        
        gbc.gridx = 1; gbc.weightx = 1.0;
        cmbUbicacionOrigen = new JComboBox<>();
        cmbUbicacionOrigen.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cmbUbicacionOrigen.setEnabled(false); // No editable - se autocompleta del activo
        cmbUbicacionOrigen.setBackground(new Color(240, 240, 240)); // Fondo gris para indicar deshabilitado
        cmbUbicacionOrigen.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Ubicacion) {
                    setText(((Ubicacion) value).getUbiNombre());
                }
                return this;
            }
        });
        panelCampos.add(cmbUbicacionOrigen, gbc);
        
        fila++;
        
        // Ubicación Destino
        gbc.gridx = 0; gbc.gridy = fila; gbc.weightx = 0;
        JLabel lblDestino = new JLabel("* Ubicación Destino:");
        lblDestino.setFont(new Font("Segoe UI", Font.BOLD, 12));
        panelCampos.add(lblDestino, gbc);
        
        gbc.gridx = 1; gbc.weightx = 1.0;
        cmbUbicacionDestino = new JComboBox<>();
        cmbUbicacionDestino.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cmbUbicacionDestino.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Ubicacion) {
                    setText(((Ubicacion) value).getUbiNombre());
                }
                return this;
            }
        });
        panelCampos.add(cmbUbicacionDestino, gbc);
        
        fila++;
        
        // Fecha de Salida
        gbc.gridx = 0; gbc.gridy = fila; gbc.weightx = 0;
        JLabel lblFechaSalida = new JLabel("Fecha de Salida:");
        lblFechaSalida.setFont(new Font("Segoe UI", Font.BOLD, 12));
        panelCampos.add(lblFechaSalida, gbc);
        
        gbc.gridx = 1; gbc.weightx = 1.0;
        spnFechaSalida = new JSpinner(new javax.swing.SpinnerDateModel());
        JSpinner.DateEditor editorFechaSalida = new JSpinner.DateEditor(spnFechaSalida, "dd/MM/yyyy");
        spnFechaSalida.setEditor(editorFechaSalida);
        spnFechaSalida.setValue(java.util.Date.from(LocalDate.now().atStartOfDay(java.time.ZoneId.systemDefault()).toInstant()));
        panelCampos.add(spnFechaSalida, gbc);
        
        fila++;
        
        // Fecha Devolución Programada
        gbc.gridx = 0; gbc.gridy = fila; gbc.weightx = 0;
        JLabel lblFechaDev = new JLabel("Fecha Devolución Prog.:");
        lblFechaDev.setFont(new Font("Segoe UI", Font.BOLD, 12));
        panelCampos.add(lblFechaDev, gbc);
        
        gbc.gridx = 1; gbc.weightx = 1.0;
        spnFechaDevolucionProg = new JSpinner(new javax.swing.SpinnerDateModel());
        JSpinner.DateEditor editorFechaDev = new JSpinner.DateEditor(spnFechaDevolucionProg, "dd/MM/yyyy");
        spnFechaDevolucionProg.setEditor(editorFechaDev);
        spnFechaDevolucionProg.setValue(new java.util.Date());
        panelCampos.add(spnFechaDevolucionProg, gbc);
        
        fila++;
        
        // Motivo
        gbc.gridx = 0; gbc.gridy = fila; gbc.weightx = 0;
        JLabel lblMotivo = new JLabel("* Motivo:");
        lblMotivo.setFont(new Font("Segoe UI", Font.BOLD, 12));
        panelCampos.add(lblMotivo, gbc);
        
        gbc.gridx = 1; gbc.weightx = 1.0;
        txtMotivo = new JTextField();
        txtMotivo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        panelCampos.add(txtMotivo, gbc);
        
        fila++;
        
        // Responsable Envío
        gbc.gridx = 0; gbc.gridy = fila; gbc.weightx = 0;
        JLabel lblRespEnvio = new JLabel("* Responsable Envío:");
        lblRespEnvio.setFont(new Font("Segoe UI", Font.BOLD, 12));
        panelCampos.add(lblRespEnvio, gbc);
        
        gbc.gridx = 1; gbc.weightx = 1.0;
        txtResponsableEnvio = new JTextField();
        txtResponsableEnvio.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtResponsableEnvio.setText(usuarioActual.getUsuNombre());
        panelCampos.add(txtResponsableEnvio, gbc);
        
        fila++;
        
        // Observaciones
        gbc.gridx = 0; gbc.gridy = fila; gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        JLabel lblObservaciones = new JLabel("Observaciones:");
        lblObservaciones.setFont(new Font("Segoe UI", Font.BOLD, 12));
        panelCampos.add(lblObservaciones, gbc);
        
        gbc.gridx = 1; gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        txtObservaciones = new JTextArea(3, 20);
        txtObservaciones.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtObservaciones.setLineWrap(true);
        txtObservaciones.setWrapStyleWord(true);
        JScrollPane scrollObservaciones = new JScrollPane(txtObservaciones);
        panelCampos.add(scrollObservaciones, gbc);
        
        JScrollPane scrollCampos = new JScrollPane(panelCampos);
        scrollCampos.setBorder(null);
        panel.add(scrollCampos, BorderLayout.CENTER);
        
        // Panel de botones del formulario
        JPanel panelBotonesForm = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        panelBotonesForm.setBackground(GRIS_CLARO);
        
        JButton btnGuardar = new JButton(iconManager.withIcon("GUARDAR", "Guardar"));
        btnGuardar.setBackground(VERDE_PRINCIPAL);
        btnGuardar.setForeground(Color.WHITE);
        btnGuardar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnGuardar.setPreferredSize(new Dimension(140, 40));
        btnGuardar.addActionListener(e -> guardarTraslado());
        
        JButton btnCancelar2 = new JButton(iconManager.withIcon("CANCELAR", "Cancelar"));
        btnCancelar2.setBackground(ROJO_DANGER);
        btnCancelar2.setForeground(Color.WHITE);
        btnCancelar2.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnCancelar2.setPreferredSize(new Dimension(140, 40));
        btnCancelar2.addActionListener(e -> volverAListado());
        
        panelBotonesForm.add(btnGuardar);
        panelBotonesForm.add(btnCancelar2);
        
        panel.add(panelBotonesForm, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private void cargarDatosIniciales() {
        cargarCombos();
        actualizarTabla();
    }
    
    private void cargarCombos() {
        try {
            // Cargar estados en filtro
            cmbFiltroEstado.addItem(null); // Opción "Todos"
            for (EstadoTraslado estado : EstadoTraslado.values()) {
                cmbFiltroEstado.addItem(estado);
            }
            
            // Cargar ubicaciones
            List<Ubicacion> ubicaciones = ubicacionDAO.obtenerTodas();
            
            cmbFiltroUbicacion.addItem(null); // Opción "Todas"
            DefaultComboBoxModel<Ubicacion> modeloUbicacion = new DefaultComboBoxModel<>();
            for (Ubicacion ubi : ubicaciones) {
                cmbFiltroUbicacion.addItem(ubi);
                modeloUbicacion.addElement(ubi);
            }
            
            cmbUbicacionOrigen.setModel(modeloUbicacion);
            
            DefaultComboBoxModel<Ubicacion> modeloUbicacionDest = new DefaultComboBoxModel<>();
            for (Ubicacion ubi : ubicaciones) {
                modeloUbicacionDest.addElement(ubi);
            }
            cmbUbicacionDestino.setModel(modeloUbicacionDest);
            
            // Cargar activos operativos
            List<Activo> activos = activoDAO.findByEstado(Activo.Estado.Operativo);
            DefaultComboBoxModel<Activo> modeloActivos = new DefaultComboBoxModel<>();
            for (Activo activo : activos) {
                modeloActivos.addElement(activo);
            }
            cmbActivo.setModel(modeloActivos);
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error al cargar datos: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void actualizarTabla() {
        try {
            modeloTabla.setRowCount(0);
            List<Traslado> traslados = trasladoService.obtenerTodos();
            
            int programados = 0;
            int enTransito = 0;
            int pendientesDev = 0;
            
            for (Traslado traslado : traslados) {
                // Actualizar estadísticas
                switch (traslado.getTrasEstado()) {
                    case Programado:
                        programados++;
                        break;
                    case En_Transito:
                        enTransito++;
                        break;
                    case Entregado:
                        pendientesDev++;
                        break;
                }
                
                // Obtener nombres de activo y ubicaciones
                String numActivo = "";
                try {
                    Activo activo = activoDAO.findById(traslado.getActId()).orElse(null);
                    if (activo != null) {
                        numActivo = activo.getActNumeroActivo();
                    }
                } catch (Exception ignored) {}
                
                String nombreOrigen = "";
                String nombreDestino = "";
                try {
                    Ubicacion origen = ubicacionDAO.buscarPorId(traslado.getTrasUbicacionOrigen()).orElse(null);
                    if (origen != null) {
                        nombreOrigen = origen.getUbiNombre();
                    }
                    Ubicacion destino = ubicacionDAO.buscarPorId(traslado.getTrasUbicacionDestino()).orElse(null);
                    if (destino != null) {
                        nombreDestino = destino.getUbiNombre();
                    }
                } catch (Exception ignored) {}
                
                String fechaSalida = traslado.getTrasFechaSalida() != null ? 
                    traslado.getTrasFechaSalida().format(formateadorFecha) : "";
                    
                String fechaDevProg = traslado.getTrasFechaDevolucionProg() != null ? 
                    traslado.getTrasFechaDevolucionProg().format(formateadorFecha) : "";
                
                modeloTabla.addRow(new Object[]{
                    traslado.getTrasId(),
                    traslado.getTrasNumero(),
                    numActivo,
                    nombreOrigen,
                    nombreDestino,
                    fechaSalida,
                    fechaDevProg,
                    traslado.getTrasEstado(),
                    traslado.getTrasResponsableEnvio(),
                    traslado.getTrasMotivo()
                });
            }
            
            // Actualizar estadísticas
            lblProgramados.setText("Programados: " + programados);
            lblEnTransito.setText("En Tránsito: " + enTransito);
            lblPendientesDevolucion.setText("Pendientes Dev.: " + pendientesDev);
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error al actualizar tabla: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void aplicarFiltros() {
        String textoBusqueda = txtBusqueda.getText().toLowerCase();
        EstadoTraslado estadoSeleccionado = (EstadoTraslado) cmbFiltroEstado.getSelectedItem();
        Ubicacion ubicacionSeleccionada = (Ubicacion) cmbFiltroUbicacion.getSelectedItem();
        
        sorter.setRowFilter(new RowFilter<DefaultTableModel, Integer>() {
            @Override
            public boolean include(Entry<? extends DefaultTableModel, ? extends Integer> entry) {
                // Filtro por texto de búsqueda
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
                
                // Filtro por estado
                if (estadoSeleccionado != null) {
                    String estadoFila = entry.getStringValue(7); // Columna Estado
                    if (!estadoFila.equals(estadoSeleccionado.toString())) {
                        return false;
                    }
                }
                
                // Filtro por ubicación (origen o destino)
                if (ubicacionSeleccionada != null) {
                    String nombreUbicacion = ubicacionSeleccionada.getUbiNombre();
                    String origen = entry.getStringValue(3);
                    String destino = entry.getStringValue(4);
                    if (!origen.equals(nombreUbicacion) && !destino.equals(nombreUbicacion)) {
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
        cmbFiltroUbicacion.setSelectedIndex(0);
        aplicarFiltros();
    }
    
    private void mostrarFormularioNuevo() {
        trasladoEnEdicion = null;
        lblTituloFormulario.setText(iconManager.withIcon("NUEVO", "NUEVO TRASLADO DE ACTIVO"));
        limpiarFormulario();
        cardLayout.show(panelContenedor, "FORMULARIO");
    }
    
    private void limpiarFormulario() {
        cmbActivo.setSelectedIndex(-1);
        cmbUbicacionOrigen.setSelectedIndex(-1);
        cmbUbicacionDestino.setSelectedIndex(-1);
        spnFechaSalida.setValue(java.util.Date.from(LocalDate.now().atStartOfDay(java.time.ZoneId.systemDefault()).toInstant()));
        spnFechaDevolucionProg.setValue(new java.util.Date());
        txtMotivo.setText("");
        txtResponsableEnvio.setText(usuarioActual.getUsuNombre());
        txtObservaciones.setText("");
    }
    
    private void guardarTraslado() {
        try {
            // Validaciones
            if (cmbActivo.getSelectedItem() == null) {
                JOptionPane.showMessageDialog(this, "Debe seleccionar un activo", "Validación", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            if (cmbUbicacionOrigen.getSelectedItem() == null) {
                JOptionPane.showMessageDialog(this, "Debe seleccionar ubicación de origen", "Validación", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            if (cmbUbicacionDestino.getSelectedItem() == null) {
                JOptionPane.showMessageDialog(this, "Debe seleccionar ubicación de destino", "Validación", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            if (txtMotivo.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Debe ingresar el motivo del traslado", "Validación", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            if (txtResponsableEnvio.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Debe ingresar el responsable de envío", "Validación", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Crear traslado
            Traslado traslado = new Traslado();
            Activo activoSeleccionado = (Activo) cmbActivo.getSelectedItem();
            traslado.setActId(activoSeleccionado.getActId());
            
            Ubicacion origen = (Ubicacion) cmbUbicacionOrigen.getSelectedItem();
            traslado.setTrasUbicacionOrigen(origen.getUbiId());
            
            Ubicacion destino = (Ubicacion) cmbUbicacionDestino.getSelectedItem();
            traslado.setTrasUbicacionDestino(destino.getUbiId());
            
            if (spnFechaSalida.getValue() != null) {
                java.util.Date fechaSalidaUtil = (java.util.Date) spnFechaSalida.getValue();
                LocalDate fechaSalida = fechaSalidaUtil.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
                traslado.setTrasFechaSalida(fechaSalida.atStartOfDay());
            }
            
            if (spnFechaDevolucionProg.getValue() != null) {
                java.util.Date fechaDevUtil = (java.util.Date) spnFechaDevolucionProg.getValue();
                LocalDate fechaDev = fechaDevUtil.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
                traslado.setTrasFechaDevolucionProg(fechaDev);
            }
            
            traslado.setTrasMotivo(txtMotivo.getText().trim());
            traslado.setTrasResponsableEnvio(txtResponsableEnvio.getText().trim());
            traslado.setTrasObservaciones(txtObservaciones.getText().trim());
            traslado.setAutorizadoPor(usuarioActual.getUsuId()); // Usuario que autoriza
            traslado.setCreadoPor(usuarioActual.getUsuId());
            
            // Guardar
            boolean exito = trasladoService.registrarTraslado(traslado);
            
            if (exito) {
                JOptionPane.showMessageDialog(this,
                    "Traslado registrado exitosamente",
                    "Éxito",
                    JOptionPane.INFORMATION_MESSAGE);
                volverAListado();
                actualizarTabla();
            } else {
                JOptionPane.showMessageDialog(this,
                    "No se pudo registrar el traslado",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error al guardar traslado: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void verDetallesTraslado() {
        int filaSeleccionada = tablaTraslados.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this,
                "Debe seleccionar un traslado",
                "Validación",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int filaModelo = tablaTraslados.convertRowIndexToModel(filaSeleccionada);
        Integer trasladoId = (Integer) modeloTabla.getValueAt(filaModelo, 0);
        
        try {
            Traslado traslado = trasladoService.buscarPorId(trasladoId);
            if (traslado != null) {
                mostrarDialogoDetalles(traslado);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error al obtener detalles: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void mostrarDialogoDetalles(Traslado traslado) {
        JDialog dialogo = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Detalles del Traslado", true);
        dialogo.setSize(600, 500);
        dialogo.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        // Área de texto con detalles
        JTextArea txtDetalles = new JTextArea();
        txtDetalles.setEditable(false);
        txtDetalles.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        StringBuilder detalles = new StringBuilder();
        detalles.append("═══════════════════════════════════════════════════\n");
        detalles.append("           DETALLES DEL TRASLADO\n");
        detalles.append("═══════════════════════════════════════════════════\n\n");
        
        detalles.append(String.format("Número: %s\n", traslado.getTrasNumero()));
        detalles.append(String.format("Estado: %s\n\n", traslado.getTrasEstado()));
        
        try {
            Activo activo = activoDAO.findById(traslado.getActId()).orElse(null);
            if (activo != null) {
                detalles.append(String.format("Activo: %s\n", activo.getActNumeroActivo()));
                detalles.append(String.format("Modelo: %s\n\n", activo.getActModelo()));
            }
            
            Ubicacion origen = ubicacionDAO.buscarPorId(traslado.getTrasUbicacionOrigen()).orElse(null);
            Ubicacion destino = ubicacionDAO.buscarPorId(traslado.getTrasUbicacionDestino()).orElse(null);
            
            if (origen != null) {
                detalles.append(String.format("Ubicación Origen: %s\n", origen.getUbiNombre()));
            }
            if (destino != null) {
                detalles.append(String.format("Ubicación Destino: %s\n\n", destino.getUbiNombre()));
            }
        } catch (Exception ignored) {}
        
        if (traslado.getTrasFechaSalida() != null) {
            detalles.append(String.format("Fecha Salida: %s\n", traslado.getTrasFechaSalida().format(formateadorFechaHora)));
        }
        
        if (traslado.getTrasFechaDevolucionProg() != null) {
            detalles.append(String.format("Fecha Devolución Programada: %s\n", traslado.getTrasFechaDevolucionProg().format(formateadorFecha)));
        }
        
        if (traslado.getTrasFechaRetorno() != null) {
            detalles.append(String.format("Fecha Retorno Real: %s\n\n", traslado.getTrasFechaRetorno().format(formateadorFechaHora)));
        } else {
            detalles.append("\n");
        }
        
        detalles.append(String.format("Motivo: %s\n\n", traslado.getTrasMotivo()));
        detalles.append(String.format("Responsable Envío: %s\n", traslado.getTrasResponsableEnvio()));
        
        if (traslado.getTrasResponsableRecibo() != null) {
            detalles.append(String.format("Responsable Recibo: %s\n", traslado.getTrasResponsableRecibo()));
        }
        
        if (traslado.getTrasObservaciones() != null && !traslado.getTrasObservaciones().isEmpty()) {
            detalles.append(String.format("\nObservaciones:\n%s\n", traslado.getTrasObservaciones()));
        }
        
        txtDetalles.setText(detalles.toString());
        
        JScrollPane scroll = new JScrollPane(txtDetalles);
        panel.add(scroll, BorderLayout.CENTER);
        
        JButton btnCerrar = new JButton("Cerrar");
        btnCerrar.setBackground(GRIS_OSCURO);
        btnCerrar.setForeground(Color.WHITE);
        btnCerrar.addActionListener(e -> dialogo.dispose());
        
        JPanel panelBoton = new JPanel();
        panelBoton.add(btnCerrar);
        panel.add(panelBoton, BorderLayout.SOUTH);
        
        dialogo.add(panel);
        dialogo.setVisible(true);
    }
    
    private void confirmarEntrega() {
        int filaSeleccionada = tablaTraslados.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this,
                "Debe seleccionar un traslado",
                "Validación",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int filaModelo = tablaTraslados.convertRowIndexToModel(filaSeleccionada);
        Integer trasladoId = (Integer) modeloTabla.getValueAt(filaModelo, 0);
        
        try {
            Traslado traslado = trasladoService.buscarPorId(trasladoId);
            if (traslado == null) {
                JOptionPane.showMessageDialog(this, "Traslado no encontrado", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (traslado.getTrasEstado() != EstadoTraslado.En_Transito) {
                JOptionPane.showMessageDialog(this,
                    "Solo se pueden confirmar entregas de traslados En Tránsito",
                    "Validación",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            String responsableRecibo = JOptionPane.showInputDialog(this,
                "Ingrese el nombre del responsable que recibe:",
                "Confirmar Entrega",
                JOptionPane.QUESTION_MESSAGE);
            
            if (responsableRecibo == null || responsableRecibo.trim().isEmpty()) {
                return;
            }
            
            String observaciones = JOptionPane.showInputDialog(this,
                "Observaciones (opcional):",
                "Confirmar Entrega",
                JOptionPane.QUESTION_MESSAGE);
            
            boolean exito = trasladoService.confirmarEntrega(trasladoId, responsableRecibo.trim(), observaciones);
            
            if (exito) {
                JOptionPane.showMessageDialog(this,
                    "Entrega confirmada exitosamente",
                    "Éxito",
                    JOptionPane.INFORMATION_MESSAGE);
                actualizarTabla();
            } else {
                JOptionPane.showMessageDialog(this,
                    "No se pudo confirmar la entrega",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error al confirmar entrega: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void confirmarDevolucion() {
        int filaSeleccionada = tablaTraslados.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this,
                "Debe seleccionar un traslado",
                "Validación",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int filaModelo = tablaTraslados.convertRowIndexToModel(filaSeleccionada);
        Integer trasladoId = (Integer) modeloTabla.getValueAt(filaModelo, 0);
        
        try {
            Traslado traslado = trasladoService.buscarPorId(trasladoId);
            if (traslado == null) {
                JOptionPane.showMessageDialog(this, "Traslado no encontrado", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (traslado.getTrasEstado() != EstadoTraslado.Entregado) {
                JOptionPane.showMessageDialog(this,
                    "Solo se pueden devolver traslados Entregados",
                    "Validación",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            int confirmar = JOptionPane.showConfirmDialog(this,
                "¿Confirmar la devolución del activo?\nEsto actualizará el estado del traslado.",
                "Confirmar Devolución",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
            
            if (confirmar != JOptionPane.YES_OPTION) {
                return;
            }
            
            String observaciones = JOptionPane.showInputDialog(this,
                "Observaciones (opcional):",
                "Confirmar Devolución",
                JOptionPane.QUESTION_MESSAGE);
            
            boolean exito = trasladoService.confirmarDevolucion(trasladoId, observaciones);
            
            if (exito) {
                JOptionPane.showMessageDialog(this,
                    "Devolución confirmada exitosamente.\nEl activo ha retornado a su ubicación original.",
                    "Éxito",
                    JOptionPane.INFORMATION_MESSAGE);
                actualizarTabla();
            } else {
                JOptionPane.showMessageDialog(this,
                    "No se pudo confirmar la devolución",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error al confirmar devolución: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void volverAListado() {
        cardLayout.show(panelContenedor, "LISTADO");
        actualizarTabla();
    }
}
