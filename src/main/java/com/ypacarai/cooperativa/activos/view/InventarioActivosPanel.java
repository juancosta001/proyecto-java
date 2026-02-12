package com.ypacarai.cooperativa.activos.view;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
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
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.text.MaskFormatter;

import com.ypacarai.cooperativa.activos.dao.TipoActivoDAO;
import com.ypacarai.cooperativa.activos.dao.UbicacionDAO;
import com.ypacarai.cooperativa.activos.dao.UsuarioDAO;
import com.ypacarai.cooperativa.activos.model.Activo;
import com.ypacarai.cooperativa.activos.model.TipoActivo;
import com.ypacarai.cooperativa.activos.model.Ubicacion;
import com.ypacarai.cooperativa.activos.model.Usuario;
import com.ypacarai.cooperativa.activos.service.ActivoService;
import com.ypacarai.cooperativa.activos.util.IconManager;

/**
 * Panel completo para gestión de inventario de activos
 * Funcionalidades: Listar, Buscar, Editar, Eliminar
 * Sistema de Gestión de Activos - Cooperativa Ypacaraí LTDA
 */
public class InventarioActivosPanel extends JPanel {

    // Colores corporativos
    private static final Color VERDE_PRINCIPAL = new Color(0, 128, 55);
    private static final Color VERDE_SECUNDARIO = new Color(0, 100, 40);
    private static final Color GRIS_CLARO = new Color(245, 245, 245);
    private static final Color AZUL_INFO = new Color(70, 130, 180);

    // Componentes principales
    private final Usuario usuarioActual;
    private CardLayout cardLayout;
    private JPanel panelContenedor;
    
    // Panel principal de tabla
    private JTable tablaActivos;
    private DefaultTableModel modeloTabla;
    private TableRowSorter<DefaultTableModel> sorter;
    
    // Filtros de búsqueda
    private JTextField txtBusqueda;
    private JComboBox<TipoActivo> cmbFiltroTipo;
    private JComboBox<Activo.Estado> cmbFiltroEstado;
    private JComboBox<Ubicacion> cmbFiltroUbicacion;
    
    // Botones de acción
    private JButton btnNuevo;
    private JButton btnEditar;
    private JButton btnEliminar;
    private JButton btnActualizar;
    
    // Panel de edición
    private JTextField txtNumeroActivo;
    private JComboBox<TipoActivo> cmbTipoActivo;
    private JTextField txtMarca;
    private JTextField txtModelo;
    private JTextField txtNumeroSerie;
    private JTextArea txtEspecificaciones;
    private JFormattedTextField txtFechaAdquisicion;
    private JComboBox<Activo.Estado> cmbEstado;
    private JComboBox<Ubicacion> cmbUbicacion;
    private JTextField txtResponsable;
    
    // Variables de control
    private Activo activoEnEdicion;
    private JButton btnGuardarFormulario;
    private JLabel lblTituloFormulario;
    private JTextArea txtObservaciones;
    
    // Servicios y DAOs
    private ActivoService activoService;
    private TipoActivoDAO tipoActivoDAO;
    private UbicacionDAO ubicacionDAO;
    private IconManager iconManager = IconManager.getInstance();
    
    public InventarioActivosPanel(Usuario usuarioActual) {
        this.usuarioActual = usuarioActual;
        
        inicializarServicios();
        configurarPanel();
        crearInterfaz();
        cargarDatosIniciales();
    }
    
    private void inicializarServicios() {
        try {
            this.activoService = new ActivoService();
            this.tipoActivoDAO = new TipoActivoDAO();
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
        
        // Panel de edición
        JPanel panelEdicion = crearPanelEdicion();
        panelContenedor.add(panelEdicion, "EDICION");
        
        add(panelContenedor, BorderLayout.CENTER);
        
        // Mostrar listado por defecto
        cardLayout.show(panelContenedor, "LISTADO");
    }
    
    private JPanel crearPanelListado() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Título
        JPanel panelTitulo = crearPanelTitulo();
        panel.add(panelTitulo, BorderLayout.NORTH);
        
        // Panel central con filtros y tabla
        JPanel panelCentral = new JPanel(new BorderLayout());
        
        // Panel de filtros
        JPanel panelFiltros = crearPanelFiltros();
        panelCentral.add(panelFiltros, BorderLayout.NORTH);
        
        // Tabla de activos
        JPanel panelTabla = crearPanelTabla();
        panelCentral.add(panelTabla, BorderLayout.CENTER);
        
        panel.add(panelCentral, BorderLayout.CENTER);
        
        // Panel de botones
        JPanel panelBotones = crearPanelBotones();
        panel.add(panelBotones, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel crearPanelTitulo() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(VERDE_PRINCIPAL);
        panel.setBorder(new EmptyBorder(15, 20, 15, 20));

        JLabel lblTitulo = new JLabel("📋 INVENTARIO DE ACTIVOS");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitulo.setForeground(Color.WHITE);

        JLabel lblSubtitulo = new JLabel("Gestión completa del inventario - Buscar, Editar, Eliminar");
        lblSubtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSubtitulo.setForeground(Color.WHITE);

        JPanel panelTextos = new JPanel(new BorderLayout());
        panelTextos.setOpaque(false);
        panelTextos.add(lblTitulo, BorderLayout.NORTH);
        panelTextos.add(lblSubtitulo, BorderLayout.SOUTH);

        panel.add(panelTextos, BorderLayout.WEST);
        return panel;
    }
    
    private JPanel crearPanelFiltros() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(GRIS_CLARO);
        panel.setBorder(new TitledBorder("Filtros de Búsqueda"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Búsqueda rápida
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("🔍 Buscar:"), gbc);
        
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        txtBusqueda = new JTextField(20);
        txtBusqueda.setToolTipText("Buscar por número, marca, modelo o serie");
        txtBusqueda.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent evt) {
                aplicarFiltros();
            }
        });
        panel.add(txtBusqueda, gbc);

        // Filtro por tipo
        gbc.gridx = 2;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("Tipo:"), gbc);
        
        gbc.gridx = 3;
        cmbFiltroTipo = new JComboBox<>();
        cmbFiltroTipo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value == null) {
                    setText("-- Todos los tipos --");
                }
                return this;
            }
        });
        cmbFiltroTipo.addActionListener(e -> aplicarFiltros());
        panel.add(cmbFiltroTipo, gbc);

        // Filtro por estado
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Estado:"), gbc);
        
        gbc.gridx = 1;
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

        // Filtro por ubicación
        gbc.gridx = 2;
        panel.add(new JLabel("Ubicación:"), gbc);
        
        gbc.gridx = 3;
        cmbFiltroUbicacion = new JComboBox<>();
        cmbFiltroUbicacion.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value == null) {
                    setText("-- Todas las ubicaciones --");
                }
                return this;
            }
        });
        cmbFiltroUbicacion.addActionListener(e -> aplicarFiltros());
        panel.add(cmbFiltroUbicacion, gbc);

        // Botón limpiar filtros
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
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
            "ID", "Número", "Tipo", "Marca", "Modelo", "Serie", 
            "Estado", "Ubicación", "Responsable", "Fecha Adquisición"
        };
        
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Tabla de solo lectura
            }
        };
        
        tablaActivos = new JTable(modeloTabla);
        tablaActivos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaActivos.setRowHeight(25);
        tablaActivos.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tablaActivos.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tablaActivos.getTableHeader().setBackground(VERDE_PRINCIPAL);
        tablaActivos.getTableHeader().setForeground(Color.WHITE);
        
        // Configurar anchos de columnas
        tablaActivos.getColumnModel().getColumn(0).setMaxWidth(50);  // ID
        tablaActivos.getColumnModel().getColumn(1).setPreferredWidth(120); // Número
        tablaActivos.getColumnModel().getColumn(2).setPreferredWidth(100); // Tipo
        tablaActivos.getColumnModel().getColumn(3).setPreferredWidth(100); // Marca
        tablaActivos.getColumnModel().getColumn(4).setPreferredWidth(100); // Modelo
        tablaActivos.getColumnModel().getColumn(5).setPreferredWidth(120); // Serie
        tablaActivos.getColumnModel().getColumn(6).setPreferredWidth(100); // Estado
        tablaActivos.getColumnModel().getColumn(7).setPreferredWidth(120); // Ubicación
        tablaActivos.getColumnModel().getColumn(8).setPreferredWidth(120); // Responsable
        tablaActivos.getColumnModel().getColumn(9).setPreferredWidth(100); // Fecha
        
        // Renderer personalizado para estados
        tablaActivos.getColumnModel().getColumn(6).setCellRenderer(new EstadoCellRenderer());
        
        // Configurar sorter
        sorter = new TableRowSorter<>(modeloTabla);
        tablaActivos.setRowSorter(sorter);
        
        // Agregar listener para selección
        tablaActivos.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                actualizarBotones();
            }
        });
        
        // Doble clic para editar
        tablaActivos.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && tablaActivos.getSelectedRow() != -1) {
                    editarActivo();
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(tablaActivos);
        scrollPane.setBorder(new TitledBorder("Activos Registrados"));
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel crearPanelBotones() {
        JPanel panel = new JPanel();
        panel.setBackground(GRIS_CLARO);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        btnNuevo = crearBoton("➕ Nuevo Activo", VERDE_PRINCIPAL, e -> nuevoActivo());
        btnEditar = crearBoton("✏️ Editar", AZUL_INFO, e -> editarActivo());
        btnEliminar = crearBoton("🗑️ Dar de Baja", Color.RED, e -> eliminarActivo());
        btnActualizar = crearBoton("🔄 Actualizar", VERDE_SECUNDARIO, e -> actualizarTabla());
        
        panel.add(btnNuevo);
        panel.add(btnEditar);
        panel.add(btnEliminar);
        panel.add(btnActualizar);
        
        return panel;
    }
    
    private JButton crearBoton(String texto, Color color, ActionListener action) {
        JButton boton = new JButton(texto);
        boton.setBackground(color);
        boton.setForeground(Color.WHITE);
        boton.setFont(new Font("Segoe UI", Font.BOLD, 11));
        boton.setBorder(new EmptyBorder(8, 12, 8, 12));
        boton.setFocusPainted(false);
        boton.addActionListener(action);
        return boton;
    }
    
    private JPanel crearPanelEdicion() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Título
        JPanel panelTitulo = new JPanel(new BorderLayout());
        panelTitulo.setBackground(AZUL_INFO);
        panelTitulo.setBorder(new EmptyBorder(15, 20, 15, 20));
        
        lblTituloFormulario = new JLabel("✏️ EDICIÓN DE ACTIVO");
        lblTituloFormulario.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTituloFormulario.setForeground(Color.WHITE);
        panelTitulo.add(lblTituloFormulario, BorderLayout.WEST);
        
        panel.add(panelTitulo, BorderLayout.NORTH);
        
        // Formulario de edición
        JPanel formulario = crearFormularioEdicion();
        JScrollPane scrollFormulario = new JScrollPane(formulario);
        scrollFormulario.setBorder(null);
        panel.add(scrollFormulario, BorderLayout.CENTER);
        
        // Botones de edición
        JPanel panelBotonesEdicion = crearPanelBotonesEdicion();
        panel.add(panelBotonesEdicion, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel crearFormularioEdicion() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(20, 30, 20, 30));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        
        int fila = 0;
        
        // Número de Activo
        gbc.gridy = fila++;
        gbc.gridx = 0;
        panel.add(new JLabel("* Número de Activo:"), gbc);
        
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        txtNumeroActivo = new JTextField(20);
        txtNumeroActivo.setEnabled(true); // Habilitado por defecto, se controlará dinámicamente
        panel.add(txtNumeroActivo, gbc);
        
        // Tipo de Activo
        gbc.gridy = fila++;
        gbc.gridx = 0;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("* Tipo de Activo:"), gbc);
        
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        cmbTipoActivo = new JComboBox<>();
        panel.add(cmbTipoActivo, gbc);
        
        // Marca
        gbc.gridy = fila++;
        gbc.gridx = 0;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("* Marca:"), gbc);
        
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        txtMarca = new JTextField(20);
        panel.add(txtMarca, gbc);
        
        // Modelo
        gbc.gridy = fila++;
        gbc.gridx = 0;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("* Modelo:"), gbc);
        
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        txtModelo = new JTextField(20);
        panel.add(txtModelo, gbc);
        
        // Número de Serie
        gbc.gridy = fila++;
        gbc.gridx = 0;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("* Número de Serie:"), gbc);
        
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        txtNumeroSerie = new JTextField(20);
        panel.add(txtNumeroSerie, gbc);
        
        // Especificaciones
        gbc.gridy = fila++;
        gbc.gridx = 0;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        panel.add(new JLabel("Especificaciones:"), gbc);
        
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 0.3;
        txtEspecificaciones = new JTextArea(4, 20);
        txtEspecificaciones.setLineWrap(true);
        txtEspecificaciones.setWrapStyleWord(true);
        JScrollPane scrollEspec = new JScrollPane(txtEspecificaciones);
        panel.add(scrollEspec, gbc);
        
        // Reset weighty
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        
        // Fecha de Adquisición
        gbc.gridy = fila++;
        gbc.gridx = 0;
        gbc.weightx = 0;
        panel.add(new JLabel("* Fecha de Adquisición:"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        try {
            MaskFormatter dateFormatter = new MaskFormatter("##/##/####");
            dateFormatter.setPlaceholderCharacter('_');
            txtFechaAdquisicion = new JFormattedTextField(dateFormatter);
            txtFechaAdquisicion.setColumns(10);
        } catch (ParseException e) {
            // Fallback si falla la máscara
            txtFechaAdquisicion = new JFormattedTextField();
            txtFechaAdquisicion.setColumns(10);
        }
        txtFechaAdquisicion.setToolTipText("Formato: DD/MM/AAAA (ej: 15/03/2024)");
        panel.add(txtFechaAdquisicion, gbc);
        
        // Estado
        gbc.gridy = fila++;
        gbc.gridx = 0;
        gbc.weightx = 0;
        panel.add(new JLabel("Estado:"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        cmbEstado = new JComboBox<>(Activo.Estado.values());
        panel.add(cmbEstado, gbc);
        
        // Ubicación
        gbc.gridy = fila++;
        gbc.gridx = 0;
        gbc.weightx = 0;
        panel.add(new JLabel("* Ubicación:"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        cmbUbicacion = new JComboBox<>();
        panel.add(cmbUbicacion, gbc);
        
        // Responsable
        gbc.gridy = fila++;
        gbc.gridx = 0;
        gbc.weightx = 0;
        panel.add(new JLabel("Responsable:"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        txtResponsable = new JTextField(20);
        panel.add(txtResponsable, gbc);
        
        // Observaciones
        gbc.gridy = fila++;
        gbc.gridx = 0;
        gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        panel.add(new JLabel("Observaciones:"), gbc);
        
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 0.3;
        txtObservaciones = new JTextArea(3, 20);
        txtObservaciones.setLineWrap(true);
        txtObservaciones.setWrapStyleWord(true);
        JScrollPane scrollObs = new JScrollPane(txtObservaciones);
        panel.add(scrollObs, gbc);
        
        return panel;
    }
    
    private JPanel crearPanelBotonesEdicion() {
        JPanel panel = new JPanel();
        panel.setBackground(GRIS_CLARO);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        JButton btnCancelar = crearBoton("Cancelar", Color.GRAY, e -> cancelarEdicion());
        JButton btnGuardar = crearBoton("💾 Guardar Cambios", VERDE_PRINCIPAL, e -> guardarEdicion());
        
        panel.add(btnCancelar);
        panel.add(btnGuardar);
        
        return panel;
    }
    
    // Renderer personalizado para la columna de estado
    private class EstadoCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            
            if (value instanceof Activo.Estado) {
                Activo.Estado estado = (Activo.Estado) value;
                switch (estado) {
                    case Operativo:
                        c.setForeground(Color.GREEN.darker());
                        setText(iconManager.getIcon("SUCCESS") + " " + estado.name());
                        break;
                    case En_Mantenimiento:
                        c.setForeground(Color.ORANGE.darker());
                        setText(iconManager.getIcon("MANTENIMIENTO") + " " + estado.name());
                        break;
                    case Fuera_Servicio:
                        c.setForeground(Color.RED.darker());
                        setText(iconManager.getIcon("ERROR") + " " + estado.name());
                        break;
                    case Trasladado:
                        c.setForeground(Color.GRAY);
                        setText(iconManager.getIcon("EXPORTAR") + " " + estado.name());
                        break;
                    default:
                        c.setForeground(Color.BLACK);
                        setText(estado.name());
                }
            }
            
            if (isSelected) {
                c.setForeground(Color.WHITE);
            }
            
            return c;
        }
    }
    
    // Métodos de funcionalidad
    
    private void cargarDatosIniciales() {
        try {
            // Cargar tipos de activos en filtros
            List<TipoActivo> tiposActivos = tipoActivoDAO.obtenerTodos();
            cmbFiltroTipo.addItem(null); // Opción "Todos"
            for (TipoActivo tipo : tiposActivos) {
                cmbFiltroTipo.addItem(tipo);
            }
            
            // Cargar tipos para edición
            cmbTipoActivo.removeAllItems();
            for (TipoActivo tipo : tiposActivos) {
                cmbTipoActivo.addItem(tipo);
            }
            
            // Cargar estados en filtros
            cmbFiltroEstado.addItem(null); // Opción "Todos"
            for (Activo.Estado estado : Activo.Estado.values()) {
                cmbFiltroEstado.addItem(estado);
            }
            
            // Cargar ubicaciones en filtros
            List<Ubicacion> ubicaciones = ubicacionDAO.obtenerTodas();
            cmbFiltroUbicacion.addItem(null); // Opción "Todas"
            for (Ubicacion ubicacion : ubicaciones) {
                cmbFiltroUbicacion.addItem(ubicacion);
            }
            
            // Cargar ubicaciones para edición
            cmbUbicacion.removeAllItems();
            for (Ubicacion ubicacion : ubicaciones) {
                cmbUbicacion.addItem(ubicacion);
            }
            
            // Cargar datos de activos
            actualizarTabla();
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error al cargar datos iniciales: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void actualizarTabla() {
        try {
            List<Activo> activos = activoService.obtenerTodosLosActivos();
            
            // Limpiar tabla
            modeloTabla.setRowCount(0);
            
            // Agregar activos
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            
            for (Activo activo : activos) {
                Object[] fila = {
                    activo.getActId(),
                    activo.getActNumeroActivo(),
                    obtenerNombreTipoActivo(activo.getTipActId()),
                    activo.getActMarca(),
                    activo.getActModelo(),
                    activo.getActNumeroSerie(),
                    activo.getActEstado(),
                    obtenerNombreUbicacion(activo.getActUbicacionActual()),
                    activo.getActResponsableActual(),
                    activo.getActFechaAdquisicion() != null ? 
                        activo.getActFechaAdquisicion().format(formatter) : ""
                };
                modeloTabla.addRow(fila);
            }
            
            // Actualizar habilitación de botones
            actualizarBotones();
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error al cargar activos: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private String obtenerNombreTipoActivo(Integer tipoId) {
        try {
            if (tipoId == null) return "";
            List<TipoActivo> tipos = tipoActivoDAO.obtenerTodos();
            TipoActivo tipo = tipos.stream()
                .filter(t -> t.getTipActId().equals(tipoId))
                .findFirst().orElse(null);
            return tipo != null ? tipo.getNombre() : "Sin tipo";
        } catch (Exception e) {
            return "Error";
        }
    }
    
    private String obtenerNombreUbicacion(Integer ubicacionId) {
        try {
            if (ubicacionId == null) return "";
            List<Ubicacion> ubicaciones = ubicacionDAO.obtenerTodas();
            Ubicacion ubicacion = ubicaciones.stream()
                .filter(u -> u.getUbiId().equals(ubicacionId))
                .findFirst().orElse(null);
            return ubicacion != null ? ubicacion.getUbiNombre() : "Sin ubicación";
        } catch (Exception e) {
            return "Error";
        }
    }
    
    private void aplicarFiltros() {
        List<RowFilter<Object, Object>> filtros = new ArrayList<>();
        
        // Filtro de búsqueda de texto
        String textoBusqueda = txtBusqueda.getText().trim();
        if (!textoBusqueda.isEmpty()) {
            filtros.add(RowFilter.regexFilter("(?i)" + textoBusqueda, 1, 3, 4, 5)); // Número, marca, modelo, serie
        }
        
        // Filtro por tipo
        TipoActivo tipoSeleccionado = (TipoActivo) cmbFiltroTipo.getSelectedItem();
        if (tipoSeleccionado != null) {
            filtros.add(RowFilter.regexFilter(tipoSeleccionado.getNombre(), 2));
        }
        
        // Filtro por estado
        Activo.Estado estadoSeleccionado = (Activo.Estado) cmbFiltroEstado.getSelectedItem();
        if (estadoSeleccionado != null) {
            filtros.add(RowFilter.regexFilter(estadoSeleccionado.name(), 6));
        }
        
        // Filtro por ubicación
        Ubicacion ubicacionSeleccionada = (Ubicacion) cmbFiltroUbicacion.getSelectedItem();
        if (ubicacionSeleccionada != null) {
            filtros.add(RowFilter.regexFilter(ubicacionSeleccionada.getUbiNombre(), 7));
        }
        
        // Aplicar filtros combinados
        if (!filtros.isEmpty()) {
            RowFilter<Object, Object> filtroCompleto = RowFilter.andFilter(filtros);
            sorter.setRowFilter(filtroCompleto);
        } else {
            sorter.setRowFilter(null);
        }
    }
    
    private void limpiarFiltros() {
        txtBusqueda.setText("");
        cmbFiltroTipo.setSelectedIndex(0);
        cmbFiltroEstado.setSelectedIndex(0);
        cmbFiltroUbicacion.setSelectedIndex(0);
        sorter.setRowFilter(null);
    }
    
    private void actualizarBotones() {
        boolean haySeleccion = tablaActivos.getSelectedRow() != -1;
        boolean puedeEditar = puedeEditarActivos();
        boolean puedeEliminar = puedeEliminarActivos();
        
        btnEditar.setEnabled(haySeleccion && puedeEditar);
        btnEliminar.setEnabled(haySeleccion && puedeEliminar);
        
        // Cambiar texto del botón según el estado del activo seleccionado
        if (haySeleccion) {
            int filaSeleccionada = tablaActivos.getSelectedRow();
            int filaModelo = tablaActivos.convertRowIndexToModel(filaSeleccionada);
            Object estadoObj = modeloTabla.getValueAt(filaModelo, 6);
            String estadoActivo = estadoObj.toString();
            
            if ("Fuera_Servicio".equals(estadoActivo)) {
                btnEliminar.setText("🔄 Reactivar");
                btnEliminar.setBackground(VERDE_PRINCIPAL);
            } else {
                btnEliminar.setText("🗑️ Dar de Baja");
                btnEliminar.setBackground(Color.RED);
            }
        }
    }
    
    private boolean puedeEditarActivos() {
        return usuarioActual.getUsuRol() == Usuario.Rol.Jefe_Informatica ||
               usuarioActual.getUsuRol() == Usuario.Rol.Tecnico;
    }
    
    private boolean puedeEliminarActivos() {
        return usuarioActual.getUsuRol() == Usuario.Rol.Jefe_Informatica;
    }
    
    // Acciones de botones
    
    private void nuevoActivo() {
        // Limpiar el formulario para nuevo activo
        limpiarFormulario();
        
        // Cambiar el título para nuevo activo
        lblTituloFormulario.setText(iconManager.withIcon("NUEVO", "NUEVO ACTIVO"));
        
        // Cambiar a la vista de edición
        cardLayout.show(panelContenedor, "EDICION");
        
        // Habilitar el campo número de activo para nuevos registros
        txtNumeroActivo.setEnabled(true);
        
        // Enfocar el primer campo
        txtNumeroActivo.requestFocus();
        
        // Marcar como nuevo activo (no edición)
        activoEnEdicion = null;
    }
    
    private void limpiarFormulario() {
        txtNumeroActivo.setText("");
        txtNumeroActivo.setEnabled(true); // Habilitar para nuevos registros
        txtMarca.setText("");
        txtModelo.setText("");
        txtNumeroSerie.setText("");
        txtFechaAdquisicion.setValue(null); // Para JFormattedTextField
        txtResponsable.setText("");
        if (txtEspecificaciones != null) {
            txtEspecificaciones.setText("");
        }
        if (txtObservaciones != null) {
            txtObservaciones.setText("");
        }
        
        if (cmbTipoActivo.getItemCount() > 0) {
            cmbTipoActivo.setSelectedIndex(0);
        }
        if (cmbEstado.getItemCount() > 0) {
            cmbEstado.setSelectedIndex(0);
        }
        if (cmbUbicacion.getItemCount() > 0) {
            cmbUbicacion.setSelectedIndex(0);
        }
    }
    
    private void editarActivo() {
        int filaSeleccionada = tablaActivos.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this,
                "Por favor seleccione un activo para editar.",
                "Selección Requerida",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            // Obtener ID del activo seleccionado
            int filaModelo = tablaActivos.convertRowIndexToModel(filaSeleccionada);
            Integer activoId = (Integer) modeloTabla.getValueAt(filaModelo, 0);
            
            // Buscar activo completo
            Activo activo = activoService.buscarActivoPorId(activoId);
            if (activo != null) {
                cargarActivoEnFormulario(activo);
                activoEnEdicion = activo; // Usar la variable correcta
                
                // Cambiar el título para edición
                lblTituloFormulario.setText(iconManager.withIcon("EDITAR", "EDICIÓN DE ACTIVO"));
                
                // Deshabilitar número de activo en edición (no debe cambiarse)
                txtNumeroActivo.setEnabled(false);
                
                cardLayout.show(panelContenedor, "EDICION");
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error al cargar activo para edición: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void eliminarActivo() {
        int filaSeleccionada = tablaActivos.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this,
                "Por favor seleccione un activo para cambiar su estado.",
                "Selección Requerida",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int filaModelo = tablaActivos.convertRowIndexToModel(filaSeleccionada);
        String numeroActivo = (String) modeloTabla.getValueAt(filaModelo, 1);
        Object estadoObj = modeloTabla.getValueAt(filaModelo, 6);
        String estadoActual = estadoObj.toString();
        
        boolean estaFueraServicio = "Fuera_Servicio".equals(estadoActual);
        String accion = estaFueraServicio ? "reactivar" : "dar de baja";
        String nuevoEstado = estaFueraServicio ? "Operativo" : "Fuera de Servicio";
        
        int respuesta = JOptionPane.showConfirmDialog(this,
            "¿Está seguro que desea " + accion + " el activo " + numeroActivo + "?\n" +
            "El activo cambiará a estado '" + nuevoEstado + "'.",
            "Confirmar " + (estaFueraServicio ? "Reactivación" : "Baja") + " de Activo",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (respuesta == JOptionPane.YES_OPTION) {
            try {
                Integer activoId = (Integer) modeloTabla.getValueAt(filaModelo, 0);
                
                // Cambiar estado según el estado actual
                Activo.Estado nuevoEstadoEnum = estaFueraServicio ? 
                    Activo.Estado.Operativo : Activo.Estado.Fuera_Servicio;
                activoService.actualizarEstadoActivo(activoId, nuevoEstadoEnum);
                
                JOptionPane.showMessageDialog(this,
                    "Activo " + (estaFueraServicio ? "reactivado" : "dado de baja") + " exitosamente.",
                    (estaFueraServicio ? "Reactivación" : "Baja") + " Completada",
                    JOptionPane.INFORMATION_MESSAGE);
                
                actualizarTabla();
                
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                    "Error al cambiar el estado del activo: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void cargarActivoEnFormulario(Activo activo) {
        txtNumeroActivo.setText(activo.getActNumeroActivo());
        
        // Seleccionar tipo de activo
        for (int i = 0; i < cmbTipoActivo.getItemCount(); i++) {
            TipoActivo tipo = (TipoActivo) cmbTipoActivo.getItemAt(i);
            if (tipo.getTipActId().equals(activo.getTipActId())) {
                cmbTipoActivo.setSelectedIndex(i);
                break;
            }
        }
        
        txtMarca.setText(activo.getActMarca());
        txtModelo.setText(activo.getActModelo());
        txtNumeroSerie.setText(activo.getActNumeroSerie());
        txtEspecificaciones.setText(activo.getActEspecificaciones());
        
        if (activo.getActFechaAdquisicion() != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            txtFechaAdquisicion.setValue(activo.getActFechaAdquisicion().format(formatter));
        } else {
            txtFechaAdquisicion.setValue(null);
        }
        
        cmbEstado.setSelectedItem(activo.getActEstado());
        
        // Seleccionar ubicación
        for (int i = 0; i < cmbUbicacion.getItemCount(); i++) {
            Ubicacion ubicacion = (Ubicacion) cmbUbicacion.getItemAt(i);
            if (ubicacion.getUbiId().equals(activo.getActUbicacionActual())) {
                cmbUbicacion.setSelectedIndex(i);
                break;
            }
        }
        
        txtResponsable.setText(activo.getActResponsableActual());
        txtObservaciones.setText(activo.getActObservaciones());
    }
    
    private void guardarEdicion() {
        if (!validarFormularioEdicion()) {
            return;
        }
        
        try {
            boolean esNuevoActivo = (activoEnEdicion == null);
            Activo activo = esNuevoActivo ? new Activo() : activoEnEdicion;
            
            // Llenar datos del formulario
            if (esNuevoActivo) {
                activo.setActNumeroActivo(txtNumeroActivo.getText().trim());
                
                // Validación de usuario: usar un usuario válido de la base de datos
                int usuarioValidoId = validarYObtenerUsuarioValido();
                activo.setCreadoPor(usuarioValidoId); // Asignar usuario creador validado
            }
            
            activo.setTipActId(((TipoActivo) cmbTipoActivo.getSelectedItem()).getTipActId());
            activo.setActMarca(txtMarca.getText().trim());
            activo.setActModelo(txtModelo.getText().trim());
            activo.setActNumeroSerie(txtNumeroSerie.getText().trim());
            activo.setActEspecificaciones(txtEspecificaciones.getText().trim());
            activo.setActObservaciones(txtObservaciones.getText().trim());
            
            // Parsear y validar fecha
            Object fechaValue = txtFechaAdquisicion.getValue();
            if (fechaValue != null && !fechaValue.toString().trim().isEmpty()) {
                String fechaTexto = fechaValue.toString().trim();
                
                // Verificar que la fecha esté completamente llena (no tenga _ del placeholder)
                if (fechaTexto.contains("_")) {
                    JOptionPane.showMessageDialog(this,
                        "Por favor complete la fecha de adquisición.\nFormato: DD/MM/AAAA",
                        "Error de Validación",
                        JOptionPane.ERROR_MESSAGE);
                    txtFechaAdquisicion.requestFocus();
                    return;
                }
                
                try {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                    LocalDate fecha = LocalDate.parse(fechaTexto, formatter);
                    
                    // Validar que la fecha no sea futura
                    if (fecha.isAfter(LocalDate.now())) {
                        JOptionPane.showMessageDialog(this,
                            "La fecha de adquisición no puede ser futura.",
                            "Error de Validación",
                            JOptionPane.ERROR_MESSAGE);
                        txtFechaAdquisicion.requestFocus();
                        return;
                    }
                    
                    // Validar que la fecha sea razonable (no muy antigua)
                    if (fecha.isBefore(LocalDate.of(1990, 1, 1))) {
                        JOptionPane.showMessageDialog(this,
                            "La fecha de adquisición debe ser posterior al año 1990.",
                            "Error de Validación",
                            JOptionPane.ERROR_MESSAGE);
                        txtFechaAdquisicion.requestFocus();
                        return;
                    }
                    
                    activo.setActFechaAdquisicion(fecha);
                } catch (DateTimeParseException e) {
                    JOptionPane.showMessageDialog(this,
                        "Formato de fecha incorrecto.\nUse DD/MM/AAAA (ej: 15/03/2024)",
                        "Error de Validación",
                        JOptionPane.ERROR_MESSAGE);
                    txtFechaAdquisicion.requestFocus();
                    return;
                }
            }
            
            activo.setActEstado((Activo.Estado) cmbEstado.getSelectedItem());
            activo.setActUbicacionActual(((Ubicacion) cmbUbicacion.getSelectedItem()).getUbiId());
            activo.setActResponsableActual(txtResponsable.getText().trim());
            
            // Guardar en base de datos
            if (esNuevoActivo) {
                boolean exito = activoService.crearActivo(activo);
                if (exito) {
                    JOptionPane.showMessageDialog(this,
                        "Activo registrado exitosamente.",
                        "Registro Completo",
                        JOptionPane.INFORMATION_MESSAGE);
                    
                    // Volver al listado y actualizar
                    cardLayout.show(panelContenedor, "LISTADO");
                    actualizarTabla();
                } else {
                    JOptionPane.showMessageDialog(this,
                        "Error al registrar el activo. Verifique los datos e intente nuevamente.",
                        "Error de Registro",
                        JOptionPane.ERROR_MESSAGE);
                    return; // No cambiar de pantalla si hay error
                }
            } else {
                activoService.actualizarActivo(activo);
                JOptionPane.showMessageDialog(this,
                    "Activo actualizado exitosamente.",
                    "Actualización Completa",
                    JOptionPane.INFORMATION_MESSAGE);
                
                // Volver al listado y actualizar
                cardLayout.show(panelContenedor, "LISTADO");
                actualizarTabla();
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error al guardar el activo: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void cancelarEdicion() {
        cardLayout.show(panelContenedor, "LISTADO");
        activoEnEdicion = null;
    }
    
    private boolean validarFormularioEdicion() {
        // Validar número de activo para nuevos registros
        if (activoEnEdicion == null && txtNumeroActivo.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe ingresar un número de activo.", "Validación", JOptionPane.WARNING_MESSAGE);
            txtNumeroActivo.requestFocus();
            return false;
        }
        
        if (cmbTipoActivo.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un tipo de activo.", "Validación", JOptionPane.WARNING_MESSAGE);
            cmbTipoActivo.requestFocus();
            return false;
        }
        
        if (txtMarca.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe ingresar una marca.", "Validación", JOptionPane.WARNING_MESSAGE);
            txtMarca.requestFocus();
            return false;
        }
        
        if (txtModelo.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe ingresar un modelo.", "Validación", JOptionPane.WARNING_MESSAGE);
            txtModelo.requestFocus();
            return false;
        }
        
        if (txtNumeroSerie.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe ingresar un número de serie.", "Validación", JOptionPane.WARNING_MESSAGE);
            txtNumeroSerie.requestFocus();
            return false;
        }
        
        if (cmbUbicacion.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar una ubicación.", "Validación", JOptionPane.WARNING_MESSAGE);
            cmbUbicacion.requestFocus();
            return false;
        }
        
        // Validar longitudes de campos
        if (txtMarca.getText().trim().length() > 60) {
            JOptionPane.showMessageDialog(this, "La marca no puede exceder 60 caracteres.", "Validación", JOptionPane.WARNING_MESSAGE);
            txtMarca.requestFocus();
            return false;
        }
        
        if (txtModelo.getText().trim().length() > 60) {
            JOptionPane.showMessageDialog(this, "El modelo no puede exceder 60 caracteres.", "Validación", JOptionPane.WARNING_MESSAGE);
            txtModelo.requestFocus();
            return false;
        }
        
        if (txtNumeroSerie.getText().trim().length() > 60) {
            JOptionPane.showMessageDialog(this, "El número de serie no puede exceder 60 caracteres.", "Validación", JOptionPane.WARNING_MESSAGE);
            txtNumeroSerie.requestFocus();
            return false;
        }
        
        if (txtResponsable.getText().trim().length() > 120) {
            JOptionPane.showMessageDialog(this, "El responsable no puede exceder 120 caracteres.", "Validación", JOptionPane.WARNING_MESSAGE);
            txtResponsable.requestFocus();
            return false;
        }
        
        return true;
    }
    
    /**
     * Valida y obtiene un ID de usuario válido para crear activos.
     * Primero intenta usar el usuario actual, si no es válido, usa el primer usuario disponible.
     */
    private int validarYObtenerUsuarioValido() {
        try {
            // Verificar si el usuario actual es válido
            if (usuarioActual != null && usuarioActual.getUsuId() > 0) {
                // Verificar que el usuario existe en la base de datos
                UsuarioDAO usuarioDAO = new UsuarioDAO();
                Optional<Usuario> usuarioExiste = usuarioDAO.findById(usuarioActual.getUsuId());
                if (usuarioExiste.isPresent()) {
                    System.out.println("✅ Usando usuario actual: " + usuarioActual.getUsuNombre() + " (ID: " + usuarioActual.getUsuId() + ")");
                    return usuarioActual.getUsuId();
                }
            }
            
            // Si el usuario actual no es válido, usar el primer usuario disponible
            System.out.println("⚠️ Usuario actual no válido, buscando usuario alternativo...");
            UsuarioDAO usuarioDAO = new UsuarioDAO();
            List<Usuario> usuarios = usuarioDAO.findAll();
            
            if (!usuarios.isEmpty()) {
                Usuario primerUsuario = usuarios.get(0);
                System.out.println("✅ Usando usuario alternativo: " + primerUsuario.getUsuNombre() + " (ID: " + primerUsuario.getUsuId() + ")");
                return primerUsuario.getUsuId();
            }
            
            // Si no hay usuarios, crear error
            throw new RuntimeException("No hay usuarios válidos en la base de datos para crear activos");
            
        } catch (Exception e) {
            System.err.println("❌ Error al validar usuario: " + e.getMessage());
            // Como último recurso, usar ID 1 (que sabemos que existe según nuestro diagnóstico)
            System.out.println("🔧 Usando ID de usuario por defecto: 1");
            return 1;
        }
    }
}