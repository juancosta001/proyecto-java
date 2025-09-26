package com.ypacarai.cooperativa.activos.view;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.Timer;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.text.MaskFormatter;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import com.ypacarai.cooperativa.activos.dao.ActivoDAO;
import com.ypacarai.cooperativa.activos.dao.TicketDAO;
import com.ypacarai.cooperativa.activos.dao.UsuarioDAO;
import com.ypacarai.cooperativa.activos.model.Activo;
import com.ypacarai.cooperativa.activos.model.Ticket;
import com.ypacarai.cooperativa.activos.model.Usuario;
import com.ypacarai.cooperativa.activos.service.ActivoService;
import com.ypacarai.cooperativa.activos.service.TicketService;

/**
 * Panel completo de sistema de tickets con funcionalidad avanzada
 * Incluye: Gestión completa, generación automática, cronómetro, flujo de estados
 * Sistema de Gestión de Activos - Cooperativa Ypacaraí LTDA
 */
public class SistemaTicketsPanel extends JPanel {

    // Colores corporativos
    private static final Color VERDE_PRINCIPAL = new Color(0, 128, 55);
    private static final Color VERDE_SECUNDARIO = new Color(0, 100, 40);
    private static final Color AZUL_INFO = new Color(52, 144, 220);
    private static final Color NARANJA_WARNING = new Color(255, 193, 7);
    private static final Color ROJO_DANGER = new Color(220, 53, 69);
    private static final Color GRIS_CLARO = new Color(245, 245, 245);
    private static final Color GRIS_OSCURO = new Color(64, 64, 64);
    private static final Color PURPLE_INFO = new Color(123, 104, 238);

    // Componentes principales
    private final Usuario usuarioActual;
    private CardLayout cardLayout;
    private JPanel panelContenedor;
    
    // Panel principal de tabla
    private JTable tablaTickets;
    private DefaultTableModel modeloTabla;
    private TableRowSorter<DefaultTableModel> sorter;
    
    // Filtros de búsqueda
    private JTextField txtBusqueda;
    private JComboBox<Ticket.Estado> cmbFiltroEstado;
    private JComboBox<Ticket.Tipo> cmbFiltroTipo;
    private JComboBox<Ticket.Prioridad> cmbFiltroPrioridad;
    private JComboBox<Usuario> cmbFiltroTecnico;
    
    // Botones de acción
    private JButton btnNuevoTicket;
    private JButton btnVerDetalles;
    private JButton btnAsignar;
    private JButton btnCambiarEstado;
    private JButton btnActualizar;
    private JButton btnGenerarAutomaticos;
    
    // Panel de formulario de ticket
    private JComboBox<Activo> cmbActivo;
    private JComboBox<Ticket.Tipo> cmbTipo;
    private JComboBox<Ticket.Prioridad> cmbPrioridad;
    private JComboBox<Usuario> cmbTecnicoAsignado;
    private JTextField txtTitulo;
    private JTextArea txtDescripcion;
    private JFormattedTextField txtFechaVencimiento;
    private JTextArea txtObservaciones;
    
    // Variables de control
    private Ticket ticketEnEdicion;
    private JLabel lblTituloFormulario;
    
    // Servicios y DAOs
    private ActivoService activoService;
    private TicketService ticketService;
    private TicketDAO ticketDAO;
    private UsuarioDAO usuarioDAO;
    
    // Panel de cronómetro
    private JLabel lblCronometro;
    private Timer cronometro;
    private long tiempoInicio;
    private boolean cronometroActivo = false;
    
    public SistemaTicketsPanel(Usuario usuarioActual) {
        this.usuarioActual = usuarioActual;
        
        inicializarServicios();
        configurarPanel();
        crearInterfaz();
        cargarDatosIniciales();
    }
    
    private void inicializarServicios() {
        try {
            this.activoService = new ActivoService();
            this.ticketService = new TicketService();
            this.ticketDAO = new TicketDAO();
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
        
        // Panel central con filtros y tabla
        JPanel panelCentral = new JPanel(new BorderLayout());
        
        // Panel de filtros
        JPanel panelFiltros = crearPanelFiltros();
        panelCentral.add(panelFiltros, BorderLayout.NORTH);
        
        // Tabla de tickets
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
        panel.setBackground(AZUL_INFO);
        panel.setBorder(new EmptyBorder(15, 20, 15, 20));

        JLabel lblTitulo = new JLabel("🎫 SISTEMA DE TICKETS DE MANTENIMIENTO");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitulo.setForeground(Color.WHITE);

        JLabel lblSubtitulo = new JLabel("Gestión completa de tickets - Preventivos y Correctivos");
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
        
        // Estadísticas se actualizarán dinámicamente
        JLabel lblPendientes = new JLabel("Pendientes: 0");
        lblPendientes.setForeground(Color.WHITE);
        lblPendientes.setFont(new Font("Segoe UI", Font.BOLD, 11));
        
        JLabel lblCriticos = new JLabel("Críticos: 0");
        lblCriticos.setForeground(Color.YELLOW);
        lblCriticos.setFont(new Font("Segoe UI", Font.BOLD, 11));
        
        JLabel lblVencidos = new JLabel("Vencidos: 0");
        lblVencidos.setForeground(Color.RED);
        lblVencidos.setFont(new Font("Segoe UI", Font.BOLD, 11));
        
        panel.add(lblPendientes);
        panel.add(new JLabel(" | "));
        lblPendientes.setForeground(Color.WHITE);
        panel.add(lblCriticos);
        panel.add(new JLabel(" | "));
        lblCriticos.setForeground(Color.WHITE);
        panel.add(lblVencidos);
        
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
        panel.add(new JLabel("🔍 Buscar:"), gbc);
        
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        txtBusqueda = new JTextField(20);
        txtBusqueda.setToolTipText("Buscar por número, título o descripción");
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
        cmbFiltroEstado.addActionListener(e -> aplicarFiltros());
        panel.add(cmbFiltroEstado, gbc);

        // Segunda fila: Tipo y Prioridad
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Tipo:"), gbc);
        
        gbc.gridx = 1;
        cmbFiltroTipo = new JComboBox<>();
        cmbFiltroTipo.addActionListener(e -> aplicarFiltros());
        panel.add(cmbFiltroTipo, gbc);

        gbc.gridx = 2;
        panel.add(new JLabel("Prioridad:"), gbc);
        
        gbc.gridx = 3;
        cmbFiltroPrioridad = new JComboBox<>();
        cmbFiltroPrioridad.addActionListener(e -> aplicarFiltros());
        panel.add(cmbFiltroPrioridad, gbc);

        // Tercera fila: Técnico y Limpiar
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Técnico:"), gbc);
        
        gbc.gridx = 1;
        cmbFiltroTecnico = new JComboBox<>();
        cmbFiltroTecnico.addActionListener(e -> aplicarFiltros());
        panel.add(cmbFiltroTecnico, gbc);

        gbc.gridx = 2; gbc.gridwidth = 2;
        JButton btnLimpiarFiltros = new JButton("Limpiar Filtros");
        btnLimpiarFiltros.setBackground(VERDE_SECUNDARIO);
        btnLimpiarFiltros.setForeground(Color.WHITE);
        btnLimpiarFiltros.addActionListener(e -> limpiarFiltros());
        panel.add(btnLimpiarFiltros, gbc);

        return panel;
    }
    
    private JPanel crearPanelTabla() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Crear modelo de tabla con todas las columnas necesarias
        String[] columnas = {
            "ID", "Número", "Tipo", "Activo", "Estado", "Prioridad", 
            "Título", "Técnico Asignado", "Fecha Creación", "Fecha Vencimiento", 
            "Tiempo Invertido", "Progreso"
        };
        
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Tabla de solo lectura
            }
        };
        
        tablaTickets = new JTable(modeloTabla);
        tablaTickets.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaTickets.setRowHeight(28);
        tablaTickets.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        tablaTickets.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 11));
        tablaTickets.getTableHeader().setBackground(AZUL_INFO);
        tablaTickets.getTableHeader().setForeground(Color.WHITE);
        
        // Configurar anchos de columnas
        tablaTickets.getColumnModel().getColumn(0).setMaxWidth(50);   // ID
        tablaTickets.getColumnModel().getColumn(1).setPreferredWidth(120); // Número
        tablaTickets.getColumnModel().getColumn(2).setPreferredWidth(80);  // Tipo
        tablaTickets.getColumnModel().getColumn(3).setPreferredWidth(120); // Activo
        tablaTickets.getColumnModel().getColumn(4).setPreferredWidth(80);  // Estado
        tablaTickets.getColumnModel().getColumn(5).setPreferredWidth(70);  // Prioridad
        tablaTickets.getColumnModel().getColumn(6).setPreferredWidth(200); // Título
        tablaTickets.getColumnModel().getColumn(7).setPreferredWidth(120); // Técnico
        tablaTickets.getColumnModel().getColumn(8).setPreferredWidth(100); // F. Creación
        tablaTickets.getColumnModel().getColumn(9).setPreferredWidth(100); // F. Vencimiento
        tablaTickets.getColumnModel().getColumn(10).setPreferredWidth(80); // Tiempo
        tablaTickets.getColumnModel().getColumn(11).setPreferredWidth(100); // Progreso
        
        // Renderers personalizados
        tablaTickets.getColumnModel().getColumn(4).setCellRenderer(new EstadoCellRenderer());
        tablaTickets.getColumnModel().getColumn(5).setCellRenderer(new PrioridadCellRenderer());
        tablaTickets.getColumnModel().getColumn(11).setCellRenderer(new ProgresoCellRenderer());
        
        // Configurar sorter
        sorter = new TableRowSorter<>(modeloTabla);
        tablaTickets.setRowSorter(sorter);
        
        // Listener para selección
        tablaTickets.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                actualizarBotones();
            }
        });
        
        // Doble clic para ver detalles
        tablaTickets.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && tablaTickets.getSelectedRow() != -1) {
                    verDetallesTicket();
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(tablaTickets);
        scrollPane.setBorder(new TitledBorder("Tickets Registrados"));
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel crearPanelBotones() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panel.setBackground(GRIS_CLARO);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        btnNuevoTicket = crearBoton("➕ Nuevo Ticket", VERDE_PRINCIPAL, e -> nuevoTicket());
        btnVerDetalles = crearBoton("👁️ Ver Detalles", AZUL_INFO, e -> verDetallesTicket());
        btnAsignar = crearBoton("👤 Asignar/Reasignar", PURPLE_INFO, e -> asignarTicket());
        btnCambiarEstado = crearBoton("🔄 Cambiar Estado", NARANJA_WARNING, e -> cambiarEstadoTicket());
        btnActualizar = crearBoton("🔄 Actualizar Lista", VERDE_SECUNDARIO, e -> actualizarTabla());
        btnGenerarAutomaticos = crearBoton("⚙️ Generar Automáticos", GRIS_OSCURO, e -> generarTicketsAutomaticos());
        
        panel.add(btnNuevoTicket);
        panel.add(btnVerDetalles);
        panel.add(btnAsignar);
        panel.add(btnCambiarEstado);
        panel.add(btnActualizar);
        panel.add(btnGenerarAutomaticos);
        
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
    
    // Renderers personalizados para la tabla
    
    private class EstadoCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            
            if (value instanceof Ticket.Estado) {
                Ticket.Estado estado = (Ticket.Estado) value;
                switch (estado) {
                    case Abierto:
                        c.setForeground(AZUL_INFO);
                        setText("🆕 " + estado.name());
                        break;
                    case En_Proceso:
                        c.setForeground(NARANJA_WARNING);
                        setText("⚙️ " + estado.name());
                        break;
                    case Resuelto:
                        c.setForeground(VERDE_PRINCIPAL);
                        setText("✅ " + estado.name());
                        break;
                    case Cerrado:
                        c.setForeground(GRIS_OSCURO);
                        setText("🔒 " + estado.name());
                        break;
                    case Cancelado:
                        c.setForeground(ROJO_DANGER);
                        setText("❌ " + estado.name());
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
    
    private class PrioridadCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            
            if (value instanceof Ticket.Prioridad) {
                Ticket.Prioridad prioridad = (Ticket.Prioridad) value;
                switch (prioridad) {
                    case Critica:
                        c.setForeground(ROJO_DANGER);
                        setText("🔴 " + prioridad.name());
                        break;
                    case Alta:
                        c.setForeground(NARANJA_WARNING);
                        setText("🟠 " + prioridad.name());
                        break;
                    case Media:
                        c.setForeground(AZUL_INFO);
                        setText("🔵 " + prioridad.name());
                        break;
                    case Baja:
                        c.setForeground(VERDE_PRINCIPAL);
                        setText("🟢 " + prioridad.name());
                        break;
                    default:
                        c.setForeground(Color.BLACK);
                        setText(prioridad.name());
                }
            }
            
            if (isSelected) {
                c.setForeground(Color.WHITE);
            }
            
            return c;
        }
    }
    
    private class ProgresoCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            
            if (value instanceof String) {
                Object estadoObj = modeloTabla.getValueAt(row, 4); // Columna estado
                String estado;
                
                if (estadoObj instanceof Ticket.Estado) {
                    estado = ((Ticket.Estado) estadoObj).name();
                } else {
                    estado = estadoObj.toString();
                }
                
                int progreso = calcularProgreso(estado);
                
                JProgressBar progressBar = new JProgressBar(0, 100);
                progressBar.setValue(progreso);
                progressBar.setStringPainted(true);
                progressBar.setString(progreso + "%");
                
                // Colores según el progreso
                if (progreso < 25) {
                    progressBar.setForeground(ROJO_DANGER);
                } else if (progreso < 75) {
                    progressBar.setForeground(NARANJA_WARNING);
                } else {
                    progressBar.setForeground(VERDE_PRINCIPAL);
                }
                
                return progressBar;
            }
            
            return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        }
        
        private int calcularProgreso(String estado) {
            switch (estado.replace("🆕 ", "").replace("⚙️ ", "").replace("✅ ", "").replace("🔒 ", "").replace("❌ ", "")) {
                case "Abierto": return 10;
                case "En_Proceso": return 50;
                case "Resuelto": return 90;
                case "Cerrado": return 100;
                case "Cancelado": return 0;
                default: return 0;
            }
        }
    }
    
    // Panel de formulario para crear/editar tickets
    
    private JPanel crearPanelFormulario() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Título dinámico
        JPanel panelTitulo = new JPanel(new BorderLayout());
        panelTitulo.setBackground(VERDE_PRINCIPAL);
        panelTitulo.setBorder(new EmptyBorder(15, 20, 15, 20));
        
        lblTituloFormulario = new JLabel("➕ NUEVO TICKET");
        lblTituloFormulario.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTituloFormulario.setForeground(Color.WHITE);
        panelTitulo.add(lblTituloFormulario, BorderLayout.WEST);
        
        panel.add(panelTitulo, BorderLayout.NORTH);
        
        // Formulario principal
        JPanel formulario = crearFormularioTicket();
        JScrollPane scrollFormulario = new JScrollPane(formulario);
        scrollFormulario.setBorder(null);
        panel.add(scrollFormulario, BorderLayout.CENTER);
        
        // Botones del formulario
        JPanel panelBotonesForm = crearPanelBotonesFormulario();
        panel.add(panelBotonesForm, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel crearFormularioTicket() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(20, 30, 20, 30));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        
        int fila = 0;
        
        // Activo relacionado
        gbc.gridy = fila++; gbc.gridx = 0;
        panel.add(new JLabel("* Activo:"), gbc);
        
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        cmbActivo = new JComboBox<>();
        panel.add(cmbActivo, gbc);
        
        // Tipo de mantenimiento
        gbc.gridy = fila++; gbc.gridx = 0;
        gbc.weightx = 0; gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("* Tipo:"), gbc);
        
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        cmbTipo = new JComboBox<>(Ticket.Tipo.values());
        panel.add(cmbTipo, gbc);
        
        // Prioridad
        gbc.gridy = fila++; gbc.gridx = 0;
        gbc.weightx = 0; gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("* Prioridad:"), gbc);
        
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        cmbPrioridad = new JComboBox<>(Ticket.Prioridad.values());
        cmbPrioridad.setSelectedItem(Ticket.Prioridad.Media); // Por defecto
        panel.add(cmbPrioridad, gbc);
        
        // Técnico asignado
        gbc.gridy = fila++; gbc.gridx = 0;
        gbc.weightx = 0; gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("Técnico Asignado:"), gbc);
        
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        cmbTecnicoAsignado = new JComboBox<>();
        panel.add(cmbTecnicoAsignado, gbc);
        
        // Título
        gbc.gridy = fila++; gbc.gridx = 0;
        gbc.weightx = 0; gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("* Título:"), gbc);
        
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        txtTitulo = new JTextField(30);
        panel.add(txtTitulo, gbc);
        
        // Descripción
        gbc.gridy = fila++; gbc.gridx = 0;
        gbc.weightx = 0; gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        panel.add(new JLabel("* Descripción:"), gbc);
        
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0; gbc.weighty = 0.3;
        txtDescripcion = new JTextArea(4, 30);
        txtDescripcion.setLineWrap(true);
        txtDescripcion.setWrapStyleWord(true);
        JScrollPane scrollDesc = new JScrollPane(txtDescripcion);
        panel.add(scrollDesc, gbc);
        
        // Reset weighty
        gbc.weighty = 0; gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        
        // Fecha de vencimiento
        gbc.gridy = fila++; gbc.gridx = 0;
        gbc.weightx = 0;
        panel.add(new JLabel("Fecha Vencimiento:"), gbc);
        
        gbc.gridx = 1; gbc.weightx = 1.0;
        try {
            MaskFormatter dateFormatter = new MaskFormatter("##/##/####");
            dateFormatter.setPlaceholderCharacter('_');
            txtFechaVencimiento = new JFormattedTextField(dateFormatter);
            txtFechaVencimiento.setColumns(10);
        } catch (ParseException e) {
            txtFechaVencimiento = new JFormattedTextField();
            txtFechaVencimiento.setColumns(10);
        }
        txtFechaVencimiento.setToolTipText("Formato: DD/MM/AAAA (Opcional)");
        panel.add(txtFechaVencimiento, gbc);
        
        // Observaciones
        gbc.gridy = fila++; gbc.gridx = 0;
        gbc.weightx = 0; gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        panel.add(new JLabel("Observaciones:"), gbc);
        
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0; gbc.weighty = 0.3;
        txtObservaciones = new JTextArea(3, 30);
        txtObservaciones.setLineWrap(true);
        txtObservaciones.setWrapStyleWord(true);
        JScrollPane scrollObs = new JScrollPane(txtObservaciones);
        panel.add(scrollObs, gbc);
        
        return panel;
    }
    
    private JPanel crearPanelBotonesFormulario() {
        JPanel panel = new JPanel(new FlowLayout());
        panel.setBackground(GRIS_CLARO);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        JButton btnCancelar = crearBoton("❌ Cancelar", ROJO_DANGER, e -> cancelarFormulario());
        JButton btnGuardar = crearBoton("💾 Guardar Ticket", VERDE_PRINCIPAL, e -> guardarTicket());
        
        panel.add(btnCancelar);
        panel.add(btnGuardar);
        
        return panel;
    }
    
    // Métodos de funcionalidad
    
    private void cargarDatosIniciales() {
        try {
            // Cargar activos en combo
            List<Activo> activos = activoService.obtenerActivosPorEstado(Activo.Estado.Operativo);
            cmbActivo.removeAllItems();
            cmbActivo.addItem(null); // Opción vacía
            for (Activo activo : activos) {
                cmbActivo.addItem(activo);
            }
            
            // Cargar técnicos (usuarios con rol Técnico o Jefe_Informatica)
            List<Usuario> tecnicos = usuarioDAO.obtenerTecnicos();
            cmbTecnicoAsignado.removeAllItems();
            cmbTecnicoAsignado.addItem(null); // Opción "Sin asignar"
            for (Usuario tecnico : tecnicos) {
                cmbTecnicoAsignado.addItem(tecnico);
            }
            
            // Configurar renderers personalizados
            configurarRenderers();
            
            // Cargar filtros
            cargarFiltros(tecnicos);
            
            // Actualizar tabla
            actualizarTabla();
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error al cargar datos iniciales: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void configurarRenderers() {
        // Renderer para el combo de activos - mostrar número y descripción
        cmbActivo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Activo) {
                    Activo activo = (Activo) value;
                    setText(activo.getActNumeroActivo() + " - " + activo.getActModelo());
                } else if (value == null) {
                    setText("-- Seleccionar Activo --");
                }
                return this;
            }
        });
        
        // Renderer para el combo de técnico asignado - mostrar solo el nombre
        cmbTecnicoAsignado.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Usuario) {
                    Usuario usuario = (Usuario) value;
                    setText(usuario.getUsuNombre());
                } else if (value == null) {
                    setText("-- Sin asignar --");
                }
                return this;
            }
        });
        
        // Renderer para el filtro de técnico
        cmbFiltroTecnico.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Usuario) {
                    Usuario usuario = (Usuario) value;
                    setText(usuario.getUsuNombre());
                } else if (value == null) {
                    setText("-- Todos los técnicos --");
                }
                return this;
            }
        });
    }
    
    private void cargarFiltros(List<Usuario> tecnicos) {
        // Filtros de estado
        cmbFiltroEstado.addItem(null); // Opción "Todos"
        for (Ticket.Estado estado : Ticket.Estado.values()) {
            cmbFiltroEstado.addItem(estado);
        }
        
        // Filtros de tipo
        cmbFiltroTipo.addItem(null);
        for (Ticket.Tipo tipo : Ticket.Tipo.values()) {
            cmbFiltroTipo.addItem(tipo);
        }
        
        // Filtros de prioridad
        cmbFiltroPrioridad.addItem(null);
        for (Ticket.Prioridad prioridad : Ticket.Prioridad.values()) {
            cmbFiltroPrioridad.addItem(prioridad);
        }
        
        // Filtros de técnico
        cmbFiltroTecnico.addItem(null);
        for (Usuario tecnico : tecnicos) {
            cmbFiltroTecnico.addItem(tecnico);
        }
    }
    
    private void actualizarTabla() {
        try {
            List<Ticket> tickets = ticketService.obtenerTodosLosTickets();
            
            // Limpiar tabla
            modeloTabla.setRowCount(0);
            
            // Agregar tickets
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            
            for (Ticket ticket : tickets) {
                Object[] fila = {
                    ticket.getTickId(),
                    ticket.getTickNumero(),
                    ticket.getTickTipo(),
                    obtenerNombreActivo(ticket.getActId()),
                    ticket.getTickEstado(),
                    ticket.getTickPrioridad(),
                    ticket.getTickTitulo(),
                    obtenerNombreTecnico(ticket.getTickAsignadoA()),
                    ticket.getTickFechaApertura() != null ? 
                        ticket.getTickFechaApertura().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : "",
                    ticket.getTickFechaVencimiento() != null ?
                        ticket.getTickFechaVencimiento().format(formatter) : "",
                    formatearTiempoInvertido(ticket.getTickTiempoResolucion()),
                    "" // Progreso se renderiza dinámicamente
                };
                modeloTabla.addRow(fila);
            }
            
            actualizarBotones();
            actualizarEstadisticas();
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error al cargar tickets: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private String obtenerNombreActivo(Integer activoId) {
        try {
            if (activoId == null) return "";
            Activo activo = activoService.buscarActivoPorId(activoId);
            return activo != null ? activo.getActNumeroActivo() : "Sin asignar";
        } catch (Exception e) {
            return "Error";
        }
    }
    
    private String obtenerNombreTecnico(Integer usuarioId) {
        try {
            if (usuarioId == null) return "Sin asignar";
            Optional<Usuario> usuario = usuarioDAO.findById(usuarioId);
            return usuario.map(Usuario::getUsuNombre).orElse("Sin asignar");
        } catch (Exception e) {
            return "Error";
        }
    }
    
    private String formatearTiempoInvertido(Integer minutos) {
        if (minutos == null || minutos == 0) return "Sin registrar";
        
        int horas = minutos / 60;
        int mins = minutos % 60;
        
        if (horas > 0) {
            return String.format("%dh %dm", horas, mins);
        } else {
            return String.format("%dm", mins);
        }
    }
    
    private void actualizarEstadisticas() {
        // TODO: Implementar actualización de estadísticas en el panel de título
        // Contar tickets por estado, prioridad, etc.
    }
    
    private void aplicarFiltros() {
        List<RowFilter<Object, Object>> filtros = new ArrayList<>();
        
        // Filtro de búsqueda de texto
        String textoBusqueda = txtBusqueda.getText().trim();
        if (!textoBusqueda.isEmpty()) {
            filtros.add(RowFilter.regexFilter("(?i)" + textoBusqueda, 1, 6, 7)); // Número, título, descripción
        }
        
        // Filtro por estado
        Ticket.Estado estadoSeleccionado = (Ticket.Estado) cmbFiltroEstado.getSelectedItem();
        if (estadoSeleccionado != null) {
            filtros.add(RowFilter.regexFilter(estadoSeleccionado.name(), 4));
        }
        
        // Filtro por tipo
        Ticket.Tipo tipoSeleccionado = (Ticket.Tipo) cmbFiltroTipo.getSelectedItem();
        if (tipoSeleccionado != null) {
            filtros.add(RowFilter.regexFilter(tipoSeleccionado.name(), 2));
        }
        
        // Filtro por prioridad
        Ticket.Prioridad prioridadSeleccionada = (Ticket.Prioridad) cmbFiltroPrioridad.getSelectedItem();
        if (prioridadSeleccionada != null) {
            filtros.add(RowFilter.regexFilter(prioridadSeleccionada.name(), 5));
        }
        
        // Filtro por técnico
        Usuario tecnicoSeleccionado = (Usuario) cmbFiltroTecnico.getSelectedItem();
        if (tecnicoSeleccionado != null) {
            filtros.add(RowFilter.regexFilter(tecnicoSeleccionado.getUsuNombre(), 7));
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
        cmbFiltroEstado.setSelectedIndex(0);
        cmbFiltroTipo.setSelectedIndex(0);
        cmbFiltroPrioridad.setSelectedIndex(0);
        cmbFiltroTecnico.setSelectedIndex(0);
        sorter.setRowFilter(null);
    }
    
    private void actualizarBotones() {
        boolean haySeleccion = tablaTickets.getSelectedRow() != -1;
        boolean puedeGestionarTickets = puedeGestionarTickets();
        
        btnVerDetalles.setEnabled(haySeleccion);
        btnAsignar.setEnabled(haySeleccion && puedeGestionarTickets);
        btnCambiarEstado.setEnabled(haySeleccion && puedeGestionarTickets);
    }
    
    private boolean puedeGestionarTickets() {
        return usuarioActual.getUsuRol() == Usuario.Rol.Jefe_Informatica ||
               usuarioActual.getUsuRol() == Usuario.Rol.Tecnico;
    }
    
    // Acciones de botones
    
    private void nuevoTicket() {
        limpiarFormulario();
        lblTituloFormulario.setText("➕ NUEVO TICKET");
        ticketEnEdicion = null;
        cardLayout.show(panelContenedor, "FORMULARIO");
        
        // Enfocar el primer campo
        cmbActivo.requestFocus();
    }
    
    private void limpiarFormulario() {
        if (cmbActivo.getItemCount() > 0) {
            cmbActivo.setSelectedIndex(0);
        }
        cmbTipo.setSelectedIndex(0);
        cmbPrioridad.setSelectedItem(Ticket.Prioridad.Media);
        if (cmbTecnicoAsignado.getItemCount() > 0) {
            cmbTecnicoAsignado.setSelectedIndex(0);
        }
        txtTitulo.setText("");
        txtDescripcion.setText("");
        txtFechaVencimiento.setValue(null);
        txtObservaciones.setText("");
    }
    
    private void verDetallesTicket() {
        int filaSeleccionada = tablaTickets.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this,
                "Por favor seleccione un ticket para ver sus detalles.",
                "Selección Requerida",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            int filaModelo = tablaTickets.convertRowIndexToModel(filaSeleccionada);
            Integer ticketId = (Integer) modeloTabla.getValueAt(filaModelo, 0);
            
            Optional<Ticket> ticketOpt = ticketDAO.buscarPorId(ticketId);
            if (ticketOpt.isPresent()) {
                mostrarDialogoDetalles(ticketOpt.get());
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error al cargar detalles del ticket: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void mostrarDialogoDetalles(Ticket ticket) {
        JDialog dialogo = new JDialog((JDialog) null, "Detalles del Ticket", true);
        dialogo.setSize(600, 500);
        dialogo.setLocationRelativeTo(this);
        dialogo.setLayout(new BorderLayout());
        
        // Panel de información
        JPanel panelInfo = new JPanel(new GridBagLayout());
        panelInfo.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        int fila = 0;
        
        // Información básica
        agregarCampoDetalle(panelInfo, gbc, fila++, "Número:", ticket.getTickNumero());
        agregarCampoDetalle(panelInfo, gbc, fila++, "Tipo:", ticket.getTickTipo().toString());
        agregarCampoDetalle(panelInfo, gbc, fila++, "Estado:", ticket.getTickEstado().toString());
        agregarCampoDetalle(panelInfo, gbc, fila++, "Prioridad:", ticket.getTickPrioridad().toString());
        agregarCampoDetalle(panelInfo, gbc, fila++, "Activo:", obtenerNombreActivo(ticket.getActId()));
        agregarCampoDetalle(panelInfo, gbc, fila++, "Técnico Asignado:", obtenerNombreTecnico(ticket.getTickAsignadoA()));
        agregarCampoDetalle(panelInfo, gbc, fila++, "Título:", ticket.getTickTitulo());
        
        // Fechas
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        agregarCampoDetalle(panelInfo, gbc, fila++, "Fecha Creación:", 
            ticket.getTickFechaApertura() != null ? ticket.getTickFechaApertura().format(formatter) : "");
        agregarCampoDetalle(panelInfo, gbc, fila++, "Fecha Vencimiento:", 
            ticket.getTickFechaVencimiento() != null ? ticket.getTickFechaVencimiento().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "Sin definir");
        agregarCampoDetalle(panelInfo, gbc, fila++, "Tiempo Invertido:", 
            formatearTiempoInvertido(ticket.getTickTiempoResolucion()));
        
        // Descripción y observaciones
        gbc.gridy = fila++; gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        panelInfo.add(new JLabel("Descripción:"), gbc);
        
        gbc.gridx = 1; gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0; gbc.weighty = 0.3;
        JTextArea txtDescripcionDetalle = new JTextArea(ticket.getTickDescripcion());
        txtDescripcionDetalle.setEditable(false);
        txtDescripcionDetalle.setLineWrap(true);
        txtDescripcionDetalle.setWrapStyleWord(true);
        JScrollPane scrollDesc = new JScrollPane(txtDescripcionDetalle);
        scrollDesc.setPreferredSize(new Dimension(300, 80));
        panelInfo.add(scrollDesc, gbc);
        
        // Panel de botones del diálogo
        JPanel panelBotonesDialogo = new JPanel(new FlowLayout());
        JButton btnCerrarDialogo = new JButton("Cerrar");
        btnCerrarDialogo.addActionListener(e -> dialogo.dispose());
        panelBotonesDialogo.add(btnCerrarDialogo);
        
        dialogo.add(new JScrollPane(panelInfo), BorderLayout.CENTER);
        dialogo.add(panelBotonesDialogo, BorderLayout.SOUTH);
        
        dialogo.setVisible(true);
    }
    
    private void agregarCampoDetalle(JPanel panel, GridBagConstraints gbc, int fila, String etiqueta, String valor) {
        gbc.gridy = fila; gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0; gbc.weighty = 0;
        
        JLabel lblEtiqueta = new JLabel(etiqueta);
        lblEtiqueta.setFont(new Font("Segoe UI", Font.BOLD, 12));
        panel.add(lblEtiqueta, gbc);
        
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        
        JLabel lblValor = new JLabel(valor != null ? valor : "");
        panel.add(lblValor, gbc);
    }
    
    private void asignarTicket() {
        int filaSeleccionada = tablaTickets.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this,
                "Por favor seleccione un ticket para asignar.",
                "Selección Requerida",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            int filaModelo = tablaTickets.convertRowIndexToModel(filaSeleccionada);
            Integer ticketId = (Integer) modeloTabla.getValueAt(filaModelo, 0);
            
            Optional<Ticket> ticketOpt = ticketDAO.buscarPorId(ticketId);
            if (!ticketOpt.isPresent()) {
                JOptionPane.showMessageDialog(this,
                    "No se pudo cargar el ticket seleccionado.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            mostrarDialogoAsignacion(ticketOpt.get());
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error al procesar la asignación: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void mostrarDialogoAsignacion(Ticket ticket) {
        JDialog dialogo = new JDialog((JDialog) null, "Asignar Ticket", true);
        dialogo.setSize(400, 250);
        dialogo.setLocationRelativeTo(this);
        dialogo.setLayout(new BorderLayout());
        
        // Panel principal
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Información del ticket
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        JLabel lblInfo = new JLabel("Ticket: " + ticket.getTickNumero());
        lblInfo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        panel.add(lblInfo, gbc);
        
        // Técnico actual
        gbc.gridy = 1; gbc.gridwidth = 1;
        panel.add(new JLabel("Técnico actual:"), gbc);
        
        gbc.gridx = 1;
        String tecnicoActual = obtenerNombreTecnico(ticket.getTickAsignadoA());
        JLabel lblTecnicoActual = new JLabel(tecnicoActual);
        panel.add(lblTecnicoActual, gbc);
        
        // Nuevo técnico
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Nuevo técnico:"), gbc);
        
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        JComboBox<Usuario> cmbNuevoTecnico = new JComboBox<>();
        
        // Cargar técnicos
        try {
            List<Usuario> tecnicos = usuarioDAO.obtenerTecnicos();
            cmbNuevoTecnico.addItem(null); // Opción "Sin asignar"
            for (Usuario tecnico : tecnicos) {
                cmbNuevoTecnico.addItem(tecnico);
            }
            
            // Seleccionar técnico actual si existe
            if (ticket.getTickAsignadoA() != null) {
                for (int i = 0; i < cmbNuevoTecnico.getItemCount(); i++) {
                    Usuario item = cmbNuevoTecnico.getItemAt(i);
                    if (item != null && item.getUsuId() == ticket.getTickAsignadoA()) {
                        cmbNuevoTecnico.setSelectedIndex(i);
                        break;
                    }
                }
            }
            
            // Configurar renderer para mostrar solo el nombre del técnico
            cmbNuevoTecnico.setRenderer(new DefaultListCellRenderer() {
                @Override
                public Component getListCellRendererComponent(JList<?> list, Object value,
                        int index, boolean isSelected, boolean cellHasFocus) {
                    super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                    if (value instanceof Usuario) {
                        Usuario usuario = (Usuario) value;
                        setText(usuario.getUsuNombre());
                    } else if (value == null) {
                        setText("-- Sin asignar --");
                    }
                    return this;
                }
            });
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(dialogo,
                "Error al cargar técnicos: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
            dialogo.dispose();
            return;
        }
        
        panel.add(cmbNuevoTecnico, gbc);
        
        dialogo.add(panel, BorderLayout.CENTER);
        
        // Panel de botones
        JPanel panelBotones = new JPanel(new FlowLayout());
        
        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.addActionListener(e -> dialogo.dispose());
        
        JButton btnAsignar = new JButton("Asignar");
        btnAsignar.setBackground(VERDE_PRINCIPAL);
        btnAsignar.setForeground(Color.WHITE);
        btnAsignar.addActionListener(e -> {
            try {
                Usuario tecnicoSeleccionado = (Usuario) cmbNuevoTecnico.getSelectedItem();
                Integer nuevoTecnicoId = tecnicoSeleccionado != null ? tecnicoSeleccionado.getUsuId() : null;
                
                ticketService.asignarTicket(ticket.getTickId(), nuevoTecnicoId);
                
                String mensaje = tecnicoSeleccionado != null ?
                    "Ticket asignado a " + tecnicoSeleccionado.getUsuNombre() :
                    "Asignación removida del ticket";
                
                JOptionPane.showMessageDialog(dialogo,
                    mensaje,
                    "Asignación Exitosa",
                    JOptionPane.INFORMATION_MESSAGE);
                
                dialogo.dispose();
                actualizarTabla();
                
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialogo,
                    "Error al asignar ticket: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        });
        
        panelBotones.add(btnCancelar);
        panelBotones.add(btnAsignar);
        
        dialogo.add(panelBotones, BorderLayout.SOUTH);
        dialogo.setVisible(true);
    }
    
    private void cambiarEstadoTicket() {
        int filaSeleccionada = tablaTickets.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this,
                "Por favor seleccione un ticket para cambiar estado.",
                "Selección Requerida",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            int filaModelo = tablaTickets.convertRowIndexToModel(filaSeleccionada);
            Integer ticketId = (Integer) modeloTabla.getValueAt(filaModelo, 0);
            
            Optional<Ticket> ticketOpt = ticketDAO.buscarPorId(ticketId);
            if (!ticketOpt.isPresent()) {
                JOptionPane.showMessageDialog(this,
                    "No se pudo cargar el ticket seleccionado.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            mostrarDialogoCambioEstado(ticketOpt.get());
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error al procesar el cambio de estado: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void mostrarDialogoCambioEstado(Ticket ticket) {
        JDialog dialogo = new JDialog((JDialog) null, "Cambiar Estado del Ticket", true);
        dialogo.setSize(450, 350);
        dialogo.setLocationRelativeTo(this);
        dialogo.setLayout(new BorderLayout());
        
        // Panel principal
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Información del ticket
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        JLabel lblInfo = new JLabel("Ticket: " + ticket.getTickNumero());
        lblInfo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        panel.add(lblInfo, gbc);
        
        // Estado actual
        gbc.gridy = 1; gbc.gridwidth = 1;
        panel.add(new JLabel("Estado actual:"), gbc);
        
        gbc.gridx = 1;
        JLabel lblEstadoActual = new JLabel(ticket.getTickEstado().toString());
        lblEstadoActual.setFont(new Font("Segoe UI", Font.BOLD, 12));
        
        // Colorear según el estado actual
        switch (ticket.getTickEstado()) {
            case Abierto:
                lblEstadoActual.setForeground(AZUL_INFO);
                break;
            case En_Proceso:
                lblEstadoActual.setForeground(NARANJA_WARNING);
                break;
            case Resuelto:
                lblEstadoActual.setForeground(VERDE_PRINCIPAL);
                break;
            case Cerrado:
                lblEstadoActual.setForeground(GRIS_OSCURO);
                break;
            case Cancelado:
                lblEstadoActual.setForeground(ROJO_DANGER);
                break;
        }
        
        panel.add(lblEstadoActual, gbc);
        
        // Nuevo estado
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Nuevo estado:"), gbc);
        
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        JComboBox<Ticket.Estado> cmbNuevoEstado = new JComboBox<>();
        
        // Cargar estados válidos según el estado actual
        cargarEstadosValidos(ticket.getTickEstado(), cmbNuevoEstado);
        
        panel.add(cmbNuevoEstado, gbc);
        
        // Campo para solución (si se resuelve)
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        JLabel lblSolucion = new JLabel("Solución:");
        lblSolucion.setVisible(false);
        panel.add(lblSolucion, gbc);
        
        gbc.gridx = 1; gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0; gbc.weighty = 0.5;
        JTextArea txtSolucion = new JTextArea(4, 20);
        txtSolucion.setLineWrap(true);
        txtSolucion.setWrapStyleWord(true);
        txtSolucion.setBorder(new LineBorder(Color.LIGHT_GRAY));
        txtSolucion.setVisible(false);
        JScrollPane scrollSolucion = new JScrollPane(txtSolucion);
        scrollSolucion.setVisible(false);
        panel.add(scrollSolucion, gbc);
        
        // Listener para mostrar campo de solución
        cmbNuevoEstado.addActionListener(e -> {
            Ticket.Estado estadoSeleccionado = (Ticket.Estado) cmbNuevoEstado.getSelectedItem();
            boolean requiereSolucion = estadoSeleccionado == Ticket.Estado.Resuelto;
            
            lblSolucion.setVisible(requiereSolucion);
            scrollSolucion.setVisible(requiereSolucion);
            dialogo.revalidate();
            dialogo.repaint();
        });
        
        dialogo.add(panel, BorderLayout.CENTER);
        
        // Panel de botones
        JPanel panelBotones = new JPanel(new FlowLayout());
        
        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.addActionListener(e -> dialogo.dispose());
        
        JButton btnCambiar = new JButton("Cambiar Estado");
        btnCambiar.setBackground(NARANJA_WARNING);
        btnCambiar.setForeground(Color.WHITE);
        btnCambiar.addActionListener(e -> {
            try {
                Ticket.Estado nuevoEstado = (Ticket.Estado) cmbNuevoEstado.getSelectedItem();
                
                if (nuevoEstado == null) {
                    JOptionPane.showMessageDialog(dialogo,
                        "Debe seleccionar un nuevo estado.",
                        "Validación",
                        JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                if (nuevoEstado == ticket.getTickEstado()) {
                    JOptionPane.showMessageDialog(dialogo,
                        "El nuevo estado debe ser diferente al actual.",
                        "Validación",
                        JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                // Si se resuelve, validar solución
                if (nuevoEstado == Ticket.Estado.Resuelto && txtSolucion.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(dialogo,
                        "Debe describir la solución al resolver el ticket.",
                        "Validación",
                        JOptionPane.WARNING_MESSAGE);
                    txtSolucion.requestFocus();
                    return;
                }
                
                // Actualizar solución si corresponde
                if (nuevoEstado == Ticket.Estado.Resuelto) {
                    ticket.setTickSolucion(txtSolucion.getText().trim());
                }
                
                // Cambiar estado
                ticketService.cambiarEstadoTicket(ticket.getTickId(), nuevoEstado, usuarioActual.getUsuId());
                
                JOptionPane.showMessageDialog(dialogo,
                    "Estado del ticket cambiado exitosamente a: " + nuevoEstado,
                    "Cambio de Estado Exitoso",
                    JOptionPane.INFORMATION_MESSAGE);
                
                dialogo.dispose();
                actualizarTabla();
                
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialogo,
                    "Error al cambiar estado: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        });
        
        panelBotones.add(btnCancelar);
        panelBotones.add(btnCambiar);
        
        dialogo.add(panelBotones, BorderLayout.SOUTH);
        dialogo.setVisible(true);
    }
    
    private void cargarEstadosValidos(Ticket.Estado estadoActual, JComboBox<Ticket.Estado> combo) {
        combo.removeAllItems();
        
        switch (estadoActual) {
            case Abierto:
                combo.addItem(Ticket.Estado.En_Proceso);
                combo.addItem(Ticket.Estado.Cancelado);
                break;
                
            case En_Proceso:
                combo.addItem(Ticket.Estado.Resuelto);
                combo.addItem(Ticket.Estado.Abierto);
                combo.addItem(Ticket.Estado.Cancelado);
                break;
                
            case Resuelto:
                combo.addItem(Ticket.Estado.Cerrado);
                combo.addItem(Ticket.Estado.En_Proceso); // Por si necesita más trabajo
                break;
                
            case Cerrado:
                combo.addItem(Ticket.Estado.En_Proceso); // Reabrir si es necesario
                break;
                
            case Cancelado:
                combo.addItem(Ticket.Estado.Abierto); // Reactivar
                break;
        }
        
        if (combo.getItemCount() == 0) {
            JOptionPane.showMessageDialog(this,
                "No hay estados válidos para transición desde: " + estadoActual,
                "Sin Transiciones Disponibles",
                JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void generarTicketsAutomaticos() {
        try {
            // Llamar al servicio para generar tickets automáticos
            int ticketsGenerados = ticketService.generarTicketsPreventivos();
            
            JOptionPane.showMessageDialog(this,
                "Se generaron " + ticketsGenerados + " tickets preventivos automáticamente.",
                "Generación Automática Completada",
                JOptionPane.INFORMATION_MESSAGE);
            
            // Actualizar la tabla
            actualizarTabla();
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error al generar tickets automáticos: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void guardarTicket() {
        if (!validarFormulario()) {
            return;
        }
        
        try {
            boolean esNuevoTicket = (ticketEnEdicion == null);
            Ticket ticket = esNuevoTicket ? new Ticket() : ticketEnEdicion;
            
            // Llenar datos del formulario
            ticket.setActId(((Activo) cmbActivo.getSelectedItem()).getActId());
            ticket.setTickTipo((Ticket.Tipo) cmbTipo.getSelectedItem());
            ticket.setTickPrioridad((Ticket.Prioridad) cmbPrioridad.getSelectedItem());
            ticket.setTickTitulo(txtTitulo.getText().trim());
            ticket.setTickDescripcion(txtDescripcion.getText().trim());
            
            // Validar que el usuario actual tenga un ID válido, sino usar ID 1 (Jefe de Informática)
            int usuarioReportaId = (usuarioActual != null && usuarioActual.getUsuId() > 0) 
                                  ? usuarioActual.getUsuId() 
                                  : 1; // ID 1 = Jefe de Informática por defecto
            ticket.setTickReportadoPor(usuarioReportaId);
            
            // Técnico asignado (puede ser null)
            Usuario tecnicoSeleccionado = (Usuario) cmbTecnicoAsignado.getSelectedItem();
            if (tecnicoSeleccionado != null) {
                ticket.setTickAsignadoA(tecnicoSeleccionado.getUsuId());
            }
            
            // Fecha de vencimiento (opcional)
            Object fechaValue = txtFechaVencimiento.getValue();
            if (fechaValue != null && !fechaValue.toString().trim().isEmpty()) {
                String fechaTexto = fechaValue.toString().trim();
                if (!fechaTexto.contains("_")) {
                    try {
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                        LocalDate fecha = LocalDate.parse(fechaTexto, formatter);
                        ticket.setTickFechaVencimiento(fecha.atStartOfDay());
                    } catch (DateTimeParseException e) {
                        JOptionPane.showMessageDialog(this,
                            "Formato de fecha incorrecto.\nUse DD/MM/AAAA (ej: 15/03/2024)",
                            "Error de Validación",
                            JOptionPane.ERROR_MESSAGE);
                        txtFechaVencimiento.requestFocus();
                        return;
                    }
                }
            }
            
            if (esNuevoTicket) {
                // Establecer valores por defecto para nuevos tickets
                ticket.setTickEstado(Ticket.Estado.Abierto);
                ticket.setTickFechaApertura(LocalDateTime.now());
            }
            
            // Guardar en base de datos
            boolean exito;
            if (esNuevoTicket) {
                exito = ticketService.crearTicket(ticket);
            } else {
                ticketService.actualizarTicket(ticket);
                exito = true;
            }
            
            if (exito) {
                JOptionPane.showMessageDialog(this,
                    esNuevoTicket ? "Ticket creado exitosamente." : "Ticket actualizado exitosamente.",
                    esNuevoTicket ? "Ticket Creado" : "Ticket Actualizado",
                    JOptionPane.INFORMATION_MESSAGE);
                
                // Volver al listado y actualizar
                cardLayout.show(panelContenedor, "LISTADO");
                actualizarTabla();
            } else {
                JOptionPane.showMessageDialog(this,
                    "Error al guardar el ticket. Verifique los datos e intente nuevamente.",
                    "Error de Guardado",
                    JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error al guardar el ticket: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void cancelarFormulario() {
        cardLayout.show(panelContenedor, "LISTADO");
        ticketEnEdicion = null;
    }
    
    private boolean validarFormulario() {
        // Validar activo seleccionado
        if (cmbActivo.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this,
                "Debe seleccionar un activo.",
                "Validación",
                JOptionPane.WARNING_MESSAGE);
            cmbActivo.requestFocus();
            return false;
        }
        
        // Validar título
        if (txtTitulo.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Debe ingresar un título para el ticket.",
                "Validación",
                JOptionPane.WARNING_MESSAGE);
            txtTitulo.requestFocus();
            return false;
        }
        
        if (txtTitulo.getText().trim().length() > 200) {
            JOptionPane.showMessageDialog(this,
                "El título no puede exceder 200 caracteres.",
                "Validación",
                JOptionPane.WARNING_MESSAGE);
            txtTitulo.requestFocus();
            return false;
        }
        
        // Validar descripción
        if (txtDescripcion.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Debe ingresar una descripción del trabajo o problema.",
                "Validación",
                JOptionPane.WARNING_MESSAGE);
            txtDescripcion.requestFocus();
            return false;
        }
        
        return true;
    }
}
