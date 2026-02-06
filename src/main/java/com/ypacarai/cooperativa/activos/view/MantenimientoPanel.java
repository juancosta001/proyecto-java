package com.ypacarai.cooperativa.activos.view;

import java.awt.*;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.DefaultListCellRenderer;

import com.ypacarai.cooperativa.activos.dao.ActivoDAO;
import com.ypacarai.cooperativa.activos.dao.MantenimientoDAO;
import com.ypacarai.cooperativa.activos.dao.UsuarioDAO;
import com.ypacarai.cooperativa.activos.dao.TicketDAO;
import com.ypacarai.cooperativa.activos.model.Activo;
import com.ypacarai.cooperativa.activos.model.AlertaMantenimiento;
import com.ypacarai.cooperativa.activos.model.ConfiguracionMantenimiento;
import com.ypacarai.cooperativa.activos.model.Mantenimiento;
import com.ypacarai.cooperativa.activos.model.Usuario;
import com.ypacarai.cooperativa.activos.model.Ticket;
import com.ypacarai.cooperativa.activos.service.MantenimientoPreventivoService;

/**
 * Panel completo de gestión de mantenimientos preventivos y correctivos
 * Sistema de Gestión de Activos - Cooperativa Ypacaraí LTDA
 * 
 * Funcionalidades:
 * - Gestión de mantenimientos preventivos y correctivos
 * - Programación automática de mantenimientos
 * - Gestión de alertas
 * - Configuración de calendarios de mantenimiento
 * - Seguimiento de técnicos y estados
 * - Reportes de mantenimiento
 */
public class MantenimientoPanel extends JPanel {
    
    // Colores del sistema
    private static final Color VERDE_PRINCIPAL = new Color(34, 139, 34);
    private static final Color VERDE_SECUNDARIO = new Color(46, 125, 50);
    private static final Color AZUL_INFO = new Color(70, 130, 180);
    private static final Color NARANJA_WARNING = new Color(255, 140, 0);
    private static final Color ROJO_DANGER = new Color(220, 20, 60);
    private static final Color GRIS_CLARO = new Color(245, 245, 245);
    private static final Color BLANCO = Color.WHITE;
    
    // Componentes principales
    private final Usuario usuarioActual;
    private JPanel panelContenedor;
    private CardLayout cardLayout;
    
    // Servicios y DAOs
    private MantenimientoPreventivoService mantenimientoPreventivoService;
    private MantenimientoDAO mantenimientoDAO;
    private ActivoDAO activoDAO;
    private UsuarioDAO usuarioDAO;
    private TicketDAO ticketDAO;
    
    // Componentes de interfaz
    private JTable tablaMantenimientos;
    private JTable tablaAlertas;
    private DefaultTableModel modeloMantenimientos;
    private DefaultTableModel modeloAlertas;
    
    // Formulario de mantenimiento
    private JComboBox<Activo> cmbActivo;
    private JComboBox<Mantenimiento.TipoMantenimiento> cmbTipo;
    private JComboBox<Usuario> cmbTecnicoAsignado;
    private JComboBox<Mantenimiento.EstadoMantenimiento> cmbEstado;
    private JTextArea txtDescripcionInicial;
    private JTextArea txtDiagnostico;
    private JTextArea txtProcedimiento;
    private JTextArea txtResultado;
    private JTextArea txtObservaciones;
    private JSpinner spnFechaInicio;
    private JSpinner spnFechaFin;
    private JSpinner spnProximaFecha;
    
    // Variables de control
    private Mantenimiento mantenimientoEditando;
    private boolean modoEdicion = false;
    
    public MantenimientoPanel(Usuario usuarioActual) {
        this.usuarioActual = usuarioActual;
        
        inicializarServicios();
        configurarPanel();
        crearInterfaz();
        cargarDatosIniciales();
    }
    
    private void inicializarServicios() {
        try {
            this.mantenimientoPreventivoService = new MantenimientoPreventivoService();
            this.mantenimientoDAO = new MantenimientoDAO();
            this.activoDAO = new ActivoDAO();
            this.usuarioDAO = new UsuarioDAO();
            this.ticketDAO = new TicketDAO();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error al inicializar servicios de mantenimiento: " + e.getMessage(),
                "Error del Sistema",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void configurarPanel() {
        setLayout(new BorderLayout());
        setBackground(BLANCO);
        setBorder(new EmptyBorder(10, 10, 10, 10));
        
        cardLayout = new CardLayout();
        panelContenedor = new JPanel(cardLayout);
        panelContenedor.setBackground(BLANCO);
        
        add(panelContenedor, BorderLayout.CENTER);
    }
    
    private void crearInterfaz() {
        // Panel principal con pestañas
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 12));
        
        // Pestaña 1: Dashboard de Mantenimientos
        JPanel panelDashboard = crearPanelDashboard();
        tabbedPane.addTab("📊 Dashboard", panelDashboard);
        
        // Pestaña 2: Gestión de Mantenimientos
        JPanel panelGestion = crearPanelGestion();
        tabbedPane.addTab("🔧 Gestión", panelGestion);
        
        // Pestaña 3: Alertas
        JPanel panelAlertas = crearPanelAlertas();
        tabbedPane.addTab("🚨 Alertas", panelAlertas);
        
        panelContenedor.add(tabbedPane, "PRINCIPAL");
        cardLayout.show(panelContenedor, "PRINCIPAL");
    }
    
    /**
     * Panel Dashboard con KPIs y resumen ejecutivo
     */
    private JPanel crearPanelDashboard() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(BLANCO);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Título
        JLabel lblTitulo = new JLabel("Dashboard de Mantenimientos");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitulo.setForeground(VERDE_PRINCIPAL);
        
        // Panel de KPIs
        JPanel panelKPIs = new JPanel(new GridLayout(2, 4, 15, 15));
        panelKPIs.setBackground(BLANCO);
        
        // KPIs principales - Cargar datos reales
        panelKPIs.add(crearKPICard("Mantenimientos Activos", String.valueOf(obtenerMantenimientosActivos()), "⚙️", AZUL_INFO));
        panelKPIs.add(crearKPICard("Preventivos Hoy", String.valueOf(obtenerPreventivosHoy()), "📅", VERDE_PRINCIPAL));
        panelKPIs.add(crearKPICard("Correctivos Urgentes", String.valueOf(obtenerCorrectivosUrgentes()), "🚨", ROJO_DANGER));
        panelKPIs.add(crearKPICard("Completados Esta Semana", String.valueOf(obtenerCompletadosSemana()), "✅", VERDE_SECUNDARIO));
        
        panelKPIs.add(crearKPICard("Alertas Pendientes", String.valueOf(obtenerAlertasPendientes()), "⚠️", NARANJA_WARNING));
        panelKPIs.add(crearKPICard("Técnicos Disponibles", String.valueOf(obtenerTecnicosDisponibles()), "👨‍🔧", AZUL_INFO));
        panelKPIs.add(crearKPICard("Activos Monitoreados", String.valueOf(obtenerActivosMonitoreados()), "📱", VERDE_PRINCIPAL));
        panelKPIs.add(crearKPICard("Eficiencia (%)", String.valueOf(calcularEficiencia()), "📊", VERDE_SECUNDARIO));
        
        // Panel de acciones rápidas
        JPanel panelAcciones = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panelAcciones.setBackground(BLANCO);
        panelAcciones.setBorder(new TitledBorder("Acciones Rápidas"));
        
        JButton btnEjecutarProceso = crearBoton("🔄 Ejecutar Proceso Diario", VERDE_PRINCIPAL, e -> ejecutarProcesoAlertasDiario());
        JButton btnNuevoMantenimiento = crearBoton("➕ Nuevo Mantenimiento", AZUL_INFO, e -> mostrarFormularioMantenimiento());
        JButton btnVerAlertas = crearBoton("🚨 Ver Alertas", NARANJA_WARNING, e -> mostrarPanelAlertas());
        JButton btnReportesCompletos = crearBoton("📊 Reportes Completos", new Color(106, 90, 205), e -> abrirReportesCompletos());
        JButton btnConfiguraciones = crearBoton("⚙️ Ver Configuraciones", new Color(100, 149, 237), e -> mostrarDialogoConfiguraciones());
        
        panelAcciones.add(btnEjecutarProceso);
        panelAcciones.add(btnNuevoMantenimiento);
        panelAcciones.add(btnVerAlertas);
        panelAcciones.add(btnReportesCompletos);
        panelAcciones.add(btnConfiguraciones);
        
        // Ensamble del dashboard
        panel.add(lblTitulo, BorderLayout.NORTH);
        panel.add(panelKPIs, BorderLayout.CENTER);
        panel.add(panelAcciones, BorderLayout.SOUTH);
        
        return panel;
    }
    
    /**
     * Panel de gestión completa de mantenimientos
     */
    private JPanel crearPanelGestion() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(BLANCO);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Panel de filtros y controles
        JPanel panelControles = new JPanel(new BorderLayout(10, 10));
        panelControles.setBackground(BLANCO);
        panelControles.setBorder(new TitledBorder("Controles"));
        
        // Filtros
        JPanel panelFiltros = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panelFiltros.setBackground(BLANCO);
        
        JLabel lblFiltroTipo = new JLabel("Tipo:");
        JComboBox<String> cmbFiltroTipo = new JComboBox<>(new String[]{"Todos", "Preventivo", "Correctivo"});
        
        JLabel lblFiltroEstado = new JLabel("Estado:");
        JComboBox<String> cmbFiltroEstado = new JComboBox<>(new String[]{"Todos", "Programado", "En_Proceso", "Completado", "Suspendido"});
        
        JLabel lblFiltroTecnico = new JLabel("Técnico:");
        JComboBox<String> cmbFiltroTecnico = new JComboBox<>(new String[]{"Todos"});
        
        JButton btnFiltrar = crearBoton("🔍 Filtrar", AZUL_INFO, e -> aplicarFiltros());
        JButton btnLimpiar = crearBoton("🧹 Limpiar", Color.GRAY, e -> limpiarFiltros());
        
        panelFiltros.add(lblFiltroTipo);
        panelFiltros.add(cmbFiltroTipo);
        panelFiltros.add(lblFiltroEstado);
        panelFiltros.add(cmbFiltroEstado);
        panelFiltros.add(lblFiltroTecnico);
        panelFiltros.add(cmbFiltroTecnico);
        panelFiltros.add(btnFiltrar);
        panelFiltros.add(btnLimpiar);
        
        // Botones de acción
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        panelBotones.setBackground(BLANCO);
        
        JButton btnNuevo = crearBoton("➕ Nuevo", VERDE_PRINCIPAL, e -> mostrarFormularioMantenimiento());
        JButton btnEditar = crearBoton("✏️ Editar", AZUL_INFO, e -> editarMantenimientoSeleccionado());
        JButton btnEliminar = crearBoton("🗑️ Eliminar", ROJO_DANGER, e -> eliminarMantenimientoSeleccionado());
        JButton btnActualizar = crearBoton("🔄 Actualizar", VERDE_SECUNDARIO, e -> actualizarTablaMantenimientos());
        
        panelBotones.add(btnNuevo);
        panelBotones.add(btnEditar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnActualizar);
        
        panelControles.add(panelFiltros, BorderLayout.CENTER);
        panelControles.add(panelBotones, BorderLayout.EAST);
        
        // Tabla de mantenimientos
        crearTablaMantenimientos();
        JScrollPane scrollMantenimientos = new JScrollPane(tablaMantenimientos);
        scrollMantenimientos.setBorder(new TitledBorder("Lista de Mantenimientos"));
        
        panel.add(panelControles, BorderLayout.NORTH);
        panel.add(scrollMantenimientos, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * Panel de alertas de mantenimiento
     */
    private JPanel crearPanelAlertas() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(BLANCO);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Panel de controles de alertas
        JPanel panelControles = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panelControles.setBackground(BLANCO);
        panelControles.setBorder(new TitledBorder("Gestión de Alertas"));
        
        JButton btnMarcarLeida = crearBoton("👁️ Marcar como Leída", AZUL_INFO, e -> marcarAlertaComoLeida());
        JButton btnDesactivar = crearBoton("❌ Desactivar", ROJO_DANGER, e -> desactivarAlerta());
        JButton btnCrearTicket = crearBoton("🎫 Crear Ticket", VERDE_PRINCIPAL, e -> crearTicketDesdeAlerta());
        JButton btnActualizarAlertas = crearBoton("🔄 Actualizar", VERDE_SECUNDARIO, e -> actualizarTablaAlertas());
        
        panelControles.add(btnMarcarLeida);
        panelControles.add(btnDesactivar);
        panelControles.add(btnCrearTicket);
        panelControles.add(btnActualizarAlertas);
        
        // Tabla de alertas
        crearTablaAlertas();
        JScrollPane scrollAlertas = new JScrollPane(tablaAlertas);
        scrollAlertas.setBorder(new TitledBorder("Alertas Activas"));
        
        panel.add(panelControles, BorderLayout.NORTH);
        panel.add(scrollAlertas, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * Panel de configuraciones de mantenimiento
     */
    private JPanel crearPanelConfiguraciones() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(BLANCO);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        JLabel lblTitulo = new JLabel("Configuraciones de Mantenimiento Preventivo");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitulo.setForeground(VERDE_PRINCIPAL);
        
        // Panel de configuraciones por tipo
        JPanel panelConfiguraciones = new JPanel(new GridLayout(0, 1, 10, 10));
        panelConfiguraciones.setBackground(BLANCO);
        
        try {
            List<ConfiguracionMantenimiento> configuraciones = mantenimientoPreventivoService.obtenerConfiguraciones();
            
            if (configuraciones.isEmpty()) {
                JLabel lblSinConfig = new JLabel("No hay configuraciones disponibles. Inicialice las configuraciones por defecto.");
                lblSinConfig.setHorizontalAlignment(SwingConstants.CENTER);
                panelConfiguraciones.add(lblSinConfig);
                
                JButton btnInicializar = crearBoton("Inicializar Configuraciones", VERDE_PRINCIPAL, 
                    e -> {
                        mantenimientoPreventivoService.inicializarConfiguracionesPorDefecto();
                        actualizarPanelConfiguraciones(panelConfiguraciones);
                    });
                panelConfiguraciones.add(btnInicializar);
            } else {
                for (ConfiguracionMantenimiento config : configuraciones) {
                    JPanel panelConfig = crearPanelConfiguracion(config);
                    panelConfiguraciones.add(panelConfig);
                }
            }
        } catch (Exception e) {
            JLabel lblError = new JLabel("Error al cargar configuraciones: " + e.getMessage());
            lblError.setForeground(ROJO_DANGER);
            panelConfiguraciones.add(lblError);
        }
        
        JScrollPane scrollConfig = new JScrollPane(panelConfiguraciones);
        scrollConfig.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        
        panel.add(lblTitulo, BorderLayout.NORTH);
        panel.add(scrollConfig, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * Panel de reportes de mantenimiento
     */
    /**
     * Abre el módulo centralizado de reportes
     */
    private void abrirReportesCompletos() {
        try {
            JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
            if (frame instanceof MainWindowNew) {
                // Cambiar a la pestaña de Reportes en la ventana principal
                ((MainWindowNew) frame).mostrarPanelReportes();
                JOptionPane.showMessageDialog(this,
                    "📊 Accediendo al módulo de Reportes Completos\n\n" +
                    "Encontrarás reportes avanzados con:\n" +
                    "• Filtros personalizables por fecha, tipo, ubicación\n" +
                    "• Exportación a Excel y PDF\n" +
                    "• Dashboard ejecutivo con KPIs en tiempo real\n" +
                    "• Consultas dinámicas personalizadas",
                    "Reportes Completos",
                    JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "No se pudo acceder al módulo de reportes.\n" +
                "Error: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Muestra un diálogo con las configuraciones de mantenimiento preventivo
     */
    private void mostrarDialogoConfiguraciones() {
        try {
            // Crear diálogo
            JDialog dialogo = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), 
                "⚙️ Configuraciones de Mantenimiento Preventivo", true);
            dialogo.setLayout(new BorderLayout(10, 10));
            dialogo.setSize(800, 600);
            dialogo.setLocationRelativeTo(this);
            
            // Panel principal con las configuraciones
            JPanel panelConfiguraciones = new JPanel();
            panelConfiguraciones.setLayout(new BoxLayout(panelConfiguraciones, BoxLayout.Y_AXIS));
            panelConfiguraciones.setBackground(BLANCO);
            panelConfiguraciones.setBorder(new EmptyBorder(15, 15, 15, 15));
            
            // Título
            JLabel lblTitulo = new JLabel("Configuraciones de Mantenimiento por Tipo de Activo");
            lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 16));
            lblTitulo.setForeground(VERDE_PRINCIPAL);
            lblTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);
            panelConfiguraciones.add(lblTitulo);
            panelConfiguraciones.add(Box.createVerticalStrut(15));
            
            // Cargar y mostrar configuraciones
            List<ConfiguracionMantenimiento> configuraciones = mantenimientoPreventivoService.obtenerConfiguraciones();
            
            if (configuraciones.isEmpty()) {
                JLabel lblSinDatos = new JLabel("⚠️ No hay configuraciones registradas");
                lblSinDatos.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                lblSinDatos.setForeground(NARANJA_WARNING);
                lblSinDatos.setAlignmentX(Component.LEFT_ALIGNMENT);
                panelConfiguraciones.add(lblSinDatos);
            } else {
                for (ConfiguracionMantenimiento config : configuraciones) {
                    JPanel panelConfig = crearPanelConfiguracionCompacto(config);
                    panelConfig.setAlignmentX(Component.LEFT_ALIGNMENT);
                    panelConfiguraciones.add(panelConfig);
                    panelConfiguraciones.add(Box.createVerticalStrut(10));
                }
            }
            
            // ScrollPane para el contenido
            JScrollPane scrollPane = new JScrollPane(panelConfiguraciones);
            scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
            scrollPane.setBorder(BorderFactory.createEmptyBorder());
            
            // Panel de botones
            JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            panelBotones.setBackground(BLANCO);
            JButton btnCerrar = crearBoton("Cerrar", Color.GRAY, e -> dialogo.dispose());
            panelBotones.add(btnCerrar);
            
            dialogo.add(scrollPane, BorderLayout.CENTER);
            dialogo.add(panelBotones, BorderLayout.SOUTH);
            
            dialogo.setVisible(true);
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error al cargar configuraciones: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Crea un panel compacto para mostrar una configuración
     */
    private JPanel crearPanelConfiguracionCompacto(ConfiguracionMantenimiento config) {
        JPanel panel = new JPanel(new BorderLayout(10, 5));
        panel.setBackground(new Color(248, 249, 250));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            new EmptyBorder(10, 15, 10, 15)
        ));
        
        // Título
        JLabel lblTipo = new JLabel("🔧 " + (config.getTipoActivo() != null ? config.getTipoActivo().getDescripcion() : "Tipo no definido"));
        lblTipo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTipo.setForeground(AZUL_INFO);
        
        // Información
        JPanel panelInfo = new JPanel(new GridLayout(2, 2, 10, 5));
        panelInfo.setBackground(new Color(248, 249, 250));
        
        panelInfo.add(new JLabel("📅 Frecuencia: " + (config.getDiasMantenimiento() != null ? config.getDiasMantenimiento() + " días" : "No configurado")));
        panelInfo.add(new JLabel("⏰ Anticipación alerta: " + (config.getDiasAnticipoAlerta() != null ? config.getDiasAnticipoAlerta() + " días" : "No configurado")));
        panelInfo.add(new JLabel("👤 Técnico ID: " + (config.getTecnicoDefaultId() != null ? config.getTecnicoDefaultId() : "No asignado")));
        panelInfo.add(new JLabel("✅ Estado: " + (config.getActivo() ? "Activo" : "Inactivo")));
        
        panel.add(lblTipo, BorderLayout.NORTH);
        panel.add(panelInfo, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * Crea la tabla de mantenimientos
     */
    private void crearTablaMantenimientos() {
        String[] columnas = {
            "ID", "Activo", "Tipo", "Estado", "Técnico Asignado", 
            "Fecha Inicio", "Fecha Fin", "Descripción", "Acciones"
        };
        
        modeloMantenimientos = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 8; // Solo la columna de acciones es editable
            }
        };
        
        tablaMantenimientos = new JTable(modeloMantenimientos);
        tablaMantenimientos.setRowHeight(25);
        tablaMantenimientos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaMantenimientos.setRowSorter(new TableRowSorter<>(modeloMantenimientos));
        
        // Configurar renderizadores
        configurarRenderizadoresMantenimientos();
        
        // Ajustar anchos de columnas
        tablaMantenimientos.getColumnModel().getColumn(0).setPreferredWidth(50);  // ID
        tablaMantenimientos.getColumnModel().getColumn(1).setPreferredWidth(120); // Activo
        tablaMantenimientos.getColumnModel().getColumn(2).setPreferredWidth(80);  // Tipo
        tablaMantenimientos.getColumnModel().getColumn(3).setPreferredWidth(100); // Estado
        tablaMantenimientos.getColumnModel().getColumn(4).setPreferredWidth(120); // Técnico
        tablaMantenimientos.getColumnModel().getColumn(5).setPreferredWidth(120); // Fecha Inicio
        tablaMantenimientos.getColumnModel().getColumn(6).setPreferredWidth(120); // Fecha Fin
        tablaMantenimientos.getColumnModel().getColumn(7).setPreferredWidth(200); // Descripción
        tablaMantenimientos.getColumnModel().getColumn(8).setPreferredWidth(100); // Acciones
    }
    
    /**
     * Crea la tabla de alertas
     */
    private void crearTablaAlertas() {
        String[] columnas = {
            "ID", "Activo", "Tipo Alerta", "Urgencia", "Días Restantes", 
            "Fecha Creación", "Técnico Asignado", "Mensaje"
        };
        
        modeloAlertas = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tablaAlertas = new JTable(modeloAlertas);
        tablaAlertas.setRowHeight(25);
        tablaAlertas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaAlertas.setRowSorter(new TableRowSorter<>(modeloAlertas));
        
        // Configurar renderizadores
        configurarRenderizadoresAlertas();
        
        // Ajustar anchos de columnas
        tablaAlertas.getColumnModel().getColumn(0).setPreferredWidth(50);  // ID
        tablaAlertas.getColumnModel().getColumn(1).setPreferredWidth(120); // Activo
        tablaAlertas.getColumnModel().getColumn(2).setPreferredWidth(120); // Tipo
        tablaAlertas.getColumnModel().getColumn(3).setPreferredWidth(80);  // Urgencia
        tablaAlertas.getColumnModel().getColumn(4).setPreferredWidth(80);  // Días Restantes
        tablaAlertas.getColumnModel().getColumn(5).setPreferredWidth(120); // Fecha Creación
        tablaAlertas.getColumnModel().getColumn(6).setPreferredWidth(120); // Técnico
        tablaAlertas.getColumnModel().getColumn(7).setPreferredWidth(250); // Mensaje
    }
    
    /**
     * Configurar renderizadores para la tabla de mantenimientos
     */
    private void configurarRenderizadoresMantenimientos() {
        // Renderizador para estados
        tablaMantenimientos.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (value != null) {
                    String estado = value.toString();
                    switch (estado) {
                        case "Programado":
                            setBackground(isSelected ? AZUL_INFO.darker() : new Color(173, 216, 230));
                            break;
                        case "En_Proceso":
                            setBackground(isSelected ? NARANJA_WARNING.darker() : new Color(255, 218, 185));
                            break;
                        case "Completado":
                            setBackground(isSelected ? VERDE_PRINCIPAL.darker() : new Color(144, 238, 144));
                            break;
                        case "Suspendido":
                            setBackground(isSelected ? ROJO_DANGER.darker() : new Color(255, 182, 193));
                            break;
                        default:
                            setBackground(isSelected ? Color.BLUE : Color.WHITE);
                    }
                }
                
                return this;
            }
        });
    }
    
    /**
     * Configurar renderizadores para la tabla de alertas
     */
    private void configurarRenderizadoresAlertas() {
        // Renderizador para urgencia
        tablaAlertas.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (value != null) {
                    String urgencia = value.toString();
                    switch (urgencia) {
                        case "CRITICO":
                            setBackground(isSelected ? ROJO_DANGER.darker() : new Color(255, 182, 193));
                            setForeground(ROJO_DANGER.darker());
                            setFont(getFont().deriveFont(Font.BOLD));
                            break;
                        case "URGENTE":
                            setBackground(isSelected ? NARANJA_WARNING.darker() : new Color(255, 218, 185));
                            setForeground(NARANJA_WARNING.darker());
                            break;
                        case "ADVERTENCIA":
                            setBackground(isSelected ? Color.ORANGE.darker() : new Color(255, 239, 213));
                            break;
                        default:
                            setBackground(isSelected ? Color.BLUE : Color.WHITE);
                            setForeground(Color.BLACK);
                            setFont(getFont().deriveFont(Font.PLAIN));
                    }
                }
                
                return this;
            }
        });
    }
    
    /**
     * Carga los datos iniciales
     */
    private void cargarDatosIniciales() {
        SwingUtilities.invokeLater(() -> {
            actualizarTablaMantenimientos();
            actualizarTablaAlertas();
            actualizarKPIs();
        });
    }
    
    /**
     * Actualiza la tabla de mantenimientos
     */
    private void actualizarTablaMantenimientos() {
        try {
            List<Mantenimiento> mantenimientos = mantenimientoDAO.findAll();
            
            // Limpiar tabla
            modeloMantenimientos.setRowCount(0);
            
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            
            for (Mantenimiento mant : mantenimientos) {
                Object[] fila = new Object[9];
                fila[0] = mant.getMantId();
                fila[1] = obtenerNombreActivo(mant.getActId());
                fila[2] = mant.getMantTipo() != null ? mant.getMantTipo().name() : "";
                fila[3] = mant.getMantEstado() != null ? mant.getMantEstado().name() : "";
                fila[4] = obtenerNombreTecnico(mant.getMantTecnicoAsignado());
                fila[5] = mant.getMantFechaInicio() != null ? mant.getMantFechaInicio().format(formatter) : "";
                fila[6] = mant.getMantFechaFin() != null ? mant.getMantFechaFin().format(formatter) : "";
                fila[7] = truncarTexto(mant.getMantDescripcionInicial(), 50);
                fila[8] = "Acciones";
                
                modeloMantenimientos.addRow(fila);
            }
            
            tablaMantenimientos.repaint();
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error al actualizar tabla de mantenimientos: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Actualiza la tabla de alertas
     */
    private void actualizarTablaAlertas() {
        try {
            List<AlertaMantenimiento> alertas = mantenimientoPreventivoService.obtenerAlertasActivasNoLeidas();
            
            // Limpiar tabla
            modeloAlertas.setRowCount(0);
            
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            
            for (AlertaMantenimiento alerta : alertas) {
                Object[] fila = new Object[8];
                fila[0] = alerta.getAlertaId(); // Usar el método correcto para el ID
                fila[1] = alerta.getActivoDescripcion();
                fila[2] = alerta.getTipoAlerta() != null ? alerta.getTipoAlerta().getDescripcion() : "";
                fila[3] = alerta.getNivelUrgencia() != null ? alerta.getNivelUrgencia().name() : "";
                fila[4] = alerta.getDiasRestantes() != null ? alerta.getDiasRestantes() : Integer.valueOf(0);
                fila[5] = alerta.getFechaCreacion() != null ? alerta.getFechaCreacion().format(formatter) : "";
                fila[6] = alerta.getUsuarioAsignado();
                fila[7] = truncarTexto(alerta.getMensaje(), 80);
                
                modeloAlertas.addRow(fila);
            }
            
            tablaAlertas.repaint();
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error al actualizar tabla de alertas: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Actualiza los KPIs del dashboard
     */
    private void actualizarKPIs() {
        SwingUtilities.invokeLater(() -> {
            try {
                // Obtener estadísticas reales
                List<Mantenimiento> todosMantenimientos = mantenimientoDAO.findAll();
                List<AlertaMantenimiento> alertasActivas = mantenimientoPreventivoService.obtenerAlertasActivasNoLeidas();
                
                // Calcular métricas
                long mantActivos = todosMantenimientos.stream()
                    .filter(m -> m.getMantEstado() == Mantenimiento.EstadoMantenimiento.En_Proceso ||
                                 m.getMantEstado() == Mantenimiento.EstadoMantenimiento.Programado)
                    .count();
                
                long preventivosHoy = todosMantenimientos.stream()
                    .filter(m -> m.getMantTipo() == Mantenimiento.TipoMantenimiento.Preventivo &&
                                 m.getMantFechaInicio() != null &&
                                 m.getMantFechaInicio().toLocalDate().equals(LocalDate.now()))
                    .count();
                
                long correctivosUrgentes = todosMantenimientos.stream()
                    .filter(m -> m.getMantTipo() == Mantenimiento.TipoMantenimiento.Correctivo &&
                                 m.getMantEstado() == Mantenimiento.EstadoMantenimiento.En_Proceso)
                    .count();
                
                LocalDate inicioSemana = LocalDate.now().minusDays(7);
                long completadosEstaSemana = todosMantenimientos.stream()
                    .filter(m -> m.getMantEstado() == Mantenimiento.EstadoMantenimiento.Completado &&
                                 m.getMantFechaFin() != null &&
                                 m.getMantFechaFin().toLocalDate().isAfter(inicioSemana))
                    .count();
                
                // Actualizar KPIs en el dashboard (esto requeriría acceso a los componentes KPI)
                System.out.println("KPIs actualizados:");
                System.out.println("Mantenimientos Activos: " + mantActivos);
                System.out.println("Preventivos Hoy: " + preventivosHoy);
                System.out.println("Correctivos Urgentes: " + correctivosUrgentes);
                System.out.println("Completados Esta Semana: " + completadosEstaSemana);
                System.out.println("Alertas Pendientes: " + alertasActivas.size());
                
            } catch (Exception e) {
                System.err.println("Error actualizando KPIs: " + e.getMessage());
            }
        });
    }
    
    // ===== MÉTODOS DE ACCIONES =====
    
    /**
     * Ejecuta el proceso diario de alertas
     */
    private void ejecutarProcesoAlertasDiario() {
        SwingUtilities.invokeLater(() -> {
            JProgressBar progressBar = new JProgressBar();
            progressBar.setIndeterminate(true);
            progressBar.setString("Ejecutando proceso de alertas...");
            progressBar.setStringPainted(true);
            
            JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Procesando", true);
            dialog.add(progressBar);
            dialog.setSize(300, 80);
            dialog.setLocationRelativeTo(this);
            
            SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {
                    mantenimientoPreventivoService.ejecutarProcesoAlertasDiario();
                    return null;
                }
                
                @Override
                protected void done() {
                    dialog.dispose();
                    try {
                        get();
                        JOptionPane.showMessageDialog(MantenimientoPanel.this,
                            "Proceso de alertas ejecutado exitosamente.\n" +
                            "Se han generado alertas para mantenimientos próximos a vencer.",
                            "Proceso Completado",
                            JOptionPane.INFORMATION_MESSAGE);
                        
                        actualizarTablaAlertas();
                        actualizarKPIs();
                        
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(MantenimientoPanel.this,
                            "Error al ejecutar proceso de alertas: " + e.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    }
                }
            };
            
            worker.execute();
            dialog.setVisible(true);
        });
    }
    
    /**
     * Muestra el formulario para nuevo mantenimiento
     */
    private void mostrarFormularioMantenimiento() {
        modoEdicion = false;
        mantenimientoEditando = null;
        mostrarFormularioMantenimiento(null);
    }
    
    /**
     * Muestra el formulario de mantenimiento
     */
    private void mostrarFormularioMantenimiento(Mantenimiento mantenimiento) {
        JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), 
            modoEdicion ? "Editar Mantenimiento" : "Nuevo Mantenimiento", true);
        
        JPanel panel = crearFormularioMantenimiento(mantenimiento);
        
        dialog.add(panel);
        dialog.setSize(800, 600);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
    
    /**
     * Muestra el panel de alertas (cambio de pestaña)
     */
    private void mostrarPanelAlertas() {
        JTabbedPane tabbedPane = (JTabbedPane) panelContenedor.getComponent(0);
        tabbedPane.setSelectedIndex(2); // Índice de la pestaña de alertas
    }
    
    // ===== MÉTODOS AUXILIARES =====
    
    /**
     * Crea un KPI card
     */
    private JPanel crearKPICard(String titulo, String valor, String icono, Color color) {
        JPanel card = new JPanel(new BorderLayout(5, 5));
        card.setBackground(BLANCO);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color, 2),
            new EmptyBorder(15, 15, 15, 15)
        ));
        card.setPreferredSize(new Dimension(180, 100));
        
        JLabel lblIcono = new JLabel(icono, SwingConstants.CENTER);
        lblIcono.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
        
        JLabel lblValor = new JLabel(valor, SwingConstants.CENTER);
        lblValor.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblValor.setForeground(color);
        
        JLabel lblTitulo = new JLabel(titulo, SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblTitulo.setForeground(Color.GRAY);
        
        card.add(lblIcono, BorderLayout.NORTH);
        card.add(lblValor, BorderLayout.CENTER);
        card.add(lblTitulo, BorderLayout.SOUTH);
        
        return card;
    }
    
    /**
     * Crea un botón estilizado
     */
    private JButton crearBoton(String texto, Color color, ActionListener accion) {
        JButton boton = new JButton(texto);
        boton.setBackground(color);
        boton.setForeground(BLANCO);
        boton.setFont(new Font("Segoe UI", Font.BOLD, 11));
        boton.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        boton.setFocusPainted(false);
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        if (accion != null) {
            boton.addActionListener(accion);
        }
        
        return boton;
    }
    
    /**
     * Crea el formulario de mantenimiento
     */
    private JPanel crearFormularioMantenimiento(Mantenimiento mantenimiento) {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Panel de campos
        JPanel panelCampos = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Fila 1: Activo y Tipo
        gbc.gridx = 0; gbc.gridy = 0;
        panelCampos.add(new JLabel("Activo:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        cmbActivo = new JComboBox<>();
        cargarActivos();
        panelCampos.add(cmbActivo, gbc);
        
        gbc.gridx = 2; gbc.fill = GridBagConstraints.NONE;
        panelCampos.add(new JLabel("Tipo:"), gbc);
        gbc.gridx = 3; gbc.fill = GridBagConstraints.HORIZONTAL;
        cmbTipo = new JComboBox<>(Mantenimiento.TipoMantenimiento.values());
        panelCampos.add(cmbTipo, gbc);
        
        // Fila 2: Técnico y Estado
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE;
        panelCampos.add(new JLabel("Técnico Asignado:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        cmbTecnicoAsignado = new JComboBox<>();
        cargarTecnicos();
        panelCampos.add(cmbTecnicoAsignado, gbc);
        
        gbc.gridx = 2; gbc.fill = GridBagConstraints.NONE;
        panelCampos.add(new JLabel("Estado:"), gbc);
        gbc.gridx = 3; gbc.fill = GridBagConstraints.HORIZONTAL;
        cmbEstado = new JComboBox<>(Mantenimiento.EstadoMantenimiento.values());
        panelCampos.add(cmbEstado, gbc);
        
        // Fila 3: Fechas
        gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE;
        panelCampos.add(new JLabel("Fecha Inicio:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        spnFechaInicio = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor editorInicio = new JSpinner.DateEditor(spnFechaInicio, "dd/MM/yyyy HH:mm");
        spnFechaInicio.setEditor(editorInicio);
        panelCampos.add(spnFechaInicio, gbc);
        
        gbc.gridx = 2; gbc.fill = GridBagConstraints.NONE;
        panelCampos.add(new JLabel("Fecha Fin:"), gbc);
        gbc.gridx = 3; gbc.fill = GridBagConstraints.HORIZONTAL;
        spnFechaFin = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor editorFin = new JSpinner.DateEditor(spnFechaFin, "dd/MM/yyyy HH:mm");
        spnFechaFin.setEditor(editorFin);
        panelCampos.add(spnFechaFin, gbc);
        
        // Fila 4: Próxima fecha
        gbc.gridx = 0; gbc.gridy = 3; gbc.fill = GridBagConstraints.NONE;
        panelCampos.add(new JLabel("Próximo Mantenimiento:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        spnProximaFecha = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor editorProxima = new JSpinner.DateEditor(spnProximaFecha, "dd/MM/yyyy");
        spnProximaFecha.setEditor(editorProxima);
        panelCampos.add(spnProximaFecha, gbc);
        
        // Área de texto para descripciones
        JPanel panelTextos = new JPanel(new GridLayout(2, 2, 10, 10));
        panelTextos.setBorder(new TitledBorder("Detalles del Mantenimiento"));
        
        // Descripción Inicial
        JPanel panelDescInicial = new JPanel(new BorderLayout());
        panelDescInicial.add(new JLabel("Descripción Inicial:"), BorderLayout.NORTH);
        txtDescripcionInicial = new JTextArea(4, 30);
        txtDescripcionInicial.setLineWrap(true);
        txtDescripcionInicial.setWrapStyleWord(true);
        panelDescInicial.add(new JScrollPane(txtDescripcionInicial), BorderLayout.CENTER);
        
        // Diagnóstico
        JPanel panelDiagnostico = new JPanel(new BorderLayout());
        panelDiagnostico.add(new JLabel("Diagnóstico:"), BorderLayout.NORTH);
        txtDiagnostico = new JTextArea(4, 30);
        txtDiagnostico.setLineWrap(true);
        txtDiagnostico.setWrapStyleWord(true);
        panelDiagnostico.add(new JScrollPane(txtDiagnostico), BorderLayout.CENTER);
        
        // Procedimiento
        JPanel panelProcedimiento = new JPanel(new BorderLayout());
        panelProcedimiento.add(new JLabel("Procedimiento Realizado:"), BorderLayout.NORTH);
        txtProcedimiento = new JTextArea(4, 30);
        txtProcedimiento.setLineWrap(true);
        txtProcedimiento.setWrapStyleWord(true);
        panelProcedimiento.add(new JScrollPane(txtProcedimiento), BorderLayout.CENTER);
        
        // Observaciones
        JPanel panelObservaciones = new JPanel(new BorderLayout());
        panelObservaciones.add(new JLabel("Observaciones:"), BorderLayout.NORTH);
        txtObservaciones = new JTextArea(4, 30);
        txtObservaciones.setLineWrap(true);
        txtObservaciones.setWrapStyleWord(true);
        panelObservaciones.add(new JScrollPane(txtObservaciones), BorderLayout.CENTER);
        
        panelTextos.add(panelDescInicial);
        panelTextos.add(panelDiagnostico);
        panelTextos.add(panelProcedimiento);
        panelTextos.add(panelObservaciones);
        
        // Panel de botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnGuardar = crearBoton("💾 Guardar", VERDE_PRINCIPAL, e -> guardarMantenimiento());
        JButton btnCancelar = crearBoton("❌ Cancelar", Color.GRAY, e -> cerrarFormulario());
        
        panelBotones.add(btnCancelar);
        panelBotones.add(btnGuardar);
        
        // Llenar datos si está editando
        if (mantenimiento != null) {
            llenarFormulario(mantenimiento);
        } else {
            // Valores por defecto para nuevo mantenimiento
            spnFechaInicio.setValue(new java.util.Date());
            cmbEstado.setSelectedItem(Mantenimiento.EstadoMantenimiento.Programado);
        }
        
        panel.add(panelCampos, BorderLayout.NORTH);
        panel.add(panelTextos, BorderLayout.CENTER);
        panel.add(panelBotones, BorderLayout.SOUTH);
        
        return panel;
    }
    
    /**
     * Carga los activos en el combobox
     */
    private void cargarActivos() {
        try {
            List<Activo> activos = activoDAO.findAll();
            cmbActivo.removeAllItems();
            for (Activo activo : activos) {
                cmbActivo.addItem(activo);
            }
            
            // Configurar renderizador para mostrar descripción
            cmbActivo.setRenderer(new DefaultListCellRenderer() {
                @Override
                public Component getListCellRendererComponent(JList<?> list, Object value,
                        int index, boolean isSelected, boolean cellHasFocus) {
                    super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                    if (value instanceof Activo) {
                        Activo activo = (Activo) value;
                        setText(activo.getActNumeroActivo() + " - " + 
                               (activo.getActEspecificaciones() != null ? activo.getActEspecificaciones() : "Sin descripción"));
                    }
                    return this;
                }
            });
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error al cargar activos: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Carga los técnicos en el combobox
     */
    private void cargarTecnicos() {
        try {
            List<Usuario> tecnicos = usuarioDAO.findByRol(Usuario.Rol.Tecnico);
            cmbTecnicoAsignado.removeAllItems();
            cmbTecnicoAsignado.addItem(null); // Opción "Sin asignar"
            for (Usuario tecnico : tecnicos) {
                cmbTecnicoAsignado.addItem(tecnico);
            }
            
            // Configurar renderizador para mostrar nombre
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
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error al cargar técnicos: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Llena el formulario con datos del mantenimiento
     */
    private void llenarFormulario(Mantenimiento mantenimiento) {
        // Seleccionar activo
        for (int i = 0; i < cmbActivo.getItemCount(); i++) {
            Activo activo = cmbActivo.getItemAt(i);
            if (activo != null && activo.getActId() == mantenimiento.getActId()) {
                cmbActivo.setSelectedIndex(i);
                break;
            }
        }
        
        // Seleccionar tipo
        if (mantenimiento.getMantTipo() != null) {
            cmbTipo.setSelectedItem(mantenimiento.getMantTipo());
        }
        
        // Seleccionar técnico
        for (int i = 0; i < cmbTecnicoAsignado.getItemCount(); i++) {
            Usuario tecnico = cmbTecnicoAsignado.getItemAt(i);
            if (tecnico != null && tecnico.getUsuId() == mantenimiento.getMantTecnicoAsignado()) {
                cmbTecnicoAsignado.setSelectedIndex(i);
                break;
            }
        }
        
        // Estado
        if (mantenimiento.getMantEstado() != null) {
            cmbEstado.setSelectedItem(mantenimiento.getMantEstado());
        }
        
        // Fechas
        if (mantenimiento.getMantFechaInicio() != null) {
            spnFechaInicio.setValue(java.sql.Timestamp.valueOf(mantenimiento.getMantFechaInicio()));
        }
        
        if (mantenimiento.getMantFechaFin() != null) {
            spnFechaFin.setValue(java.sql.Timestamp.valueOf(mantenimiento.getMantFechaFin()));
        }
        
        if (mantenimiento.getMantProximaFecha() != null) {
            spnProximaFecha.setValue(java.sql.Date.valueOf(mantenimiento.getMantProximaFecha()));
        }
        
        // Textos
        txtDescripcionInicial.setText(mantenimiento.getMantDescripcionInicial() != null ? mantenimiento.getMantDescripcionInicial() : "");
        txtDiagnostico.setText(mantenimiento.getMantDiagnostico() != null ? mantenimiento.getMantDiagnostico() : "");
        txtProcedimiento.setText(mantenimiento.getMantProcedimiento() != null ? mantenimiento.getMantProcedimiento() : "");
        txtObservaciones.setText(mantenimiento.getMantObservaciones() != null ? mantenimiento.getMantObservaciones() : "");
    }
    
    /**
     * Guarda el mantenimiento
     */
    private void guardarMantenimiento() {
        try {
            // Validaciones
            if (cmbActivo.getSelectedItem() == null) {
                JOptionPane.showMessageDialog(this,
                    "Debe seleccionar un activo.",
                    "Error de Validación", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (cmbTipo.getSelectedItem() == null) {
                JOptionPane.showMessageDialog(this,
                    "Debe seleccionar un tipo de mantenimiento.",
                    "Error de Validación", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Crear o actualizar mantenimiento
            Mantenimiento mantenimiento = mantenimientoEditando != null ? mantenimientoEditando : new Mantenimiento();
            
            Activo activoSeleccionado = (Activo) cmbActivo.getSelectedItem();
            mantenimiento.setActId(activoSeleccionado.getActId());
            mantenimiento.setMantTipo((Mantenimiento.TipoMantenimiento) cmbTipo.getSelectedItem());
            mantenimiento.setMantEstado((Mantenimiento.EstadoMantenimiento) cmbEstado.getSelectedItem());
            
            Usuario tecnico = (Usuario) cmbTecnicoAsignado.getSelectedItem();
            mantenimiento.setMantTecnicoAsignado(tecnico != null ? tecnico.getUsuId() : null);
            
            // Fechas
            java.util.Date fechaInicio = (java.util.Date) spnFechaInicio.getValue();
            if (fechaInicio != null) {
                mantenimiento.setMantFechaInicio(LocalDateTime.ofInstant(fechaInicio.toInstant(), 
                    java.time.ZoneId.systemDefault()));
            }
            
            java.util.Date fechaFin = (java.util.Date) spnFechaFin.getValue();
            if (fechaFin != null) {
                mantenimiento.setMantFechaFin(LocalDateTime.ofInstant(fechaFin.toInstant(), 
                    java.time.ZoneId.systemDefault()));
            }
            
            java.util.Date proximaFecha = (java.util.Date) spnProximaFecha.getValue();
            if (proximaFecha != null) {
                mantenimiento.setMantProximaFecha(proximaFecha.toInstant()
                    .atZone(java.time.ZoneId.systemDefault()).toLocalDate());
            }
            
            // Textos
            mantenimiento.setMantDescripcionInicial(txtDescripcionInicial.getText().trim());
            mantenimiento.setMantDiagnostico(txtDiagnostico.getText().trim());
            mantenimiento.setMantProcedimiento(txtProcedimiento.getText().trim());
            mantenimiento.setMantObservaciones(txtObservaciones.getText().trim());
            
            // Guardar
            boolean exito;
            if (modoEdicion) {
                exito = mantenimientoDAO.update(mantenimiento);
            } else {
                exito = mantenimientoDAO.save(mantenimiento);
            }
            
            if (exito) {
                JOptionPane.showMessageDialog(this,
                    "Mantenimiento " + (modoEdicion ? "actualizado" : "guardado") + " exitosamente.",
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
                
                actualizarTablaMantenimientos();
                actualizarKPIs();
                cerrarFormulario();
            } else {
                JOptionPane.showMessageDialog(this,
                    "Error al " + (modoEdicion ? "actualizar" : "guardar") + " el mantenimiento.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error al procesar mantenimiento: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    /**
     * Cierra el formulario
     */
    private void cerrarFormulario() {
        // Cerrar la ventana de diálogo
        Window window = SwingUtilities.getWindowAncestor(this);
        if (window instanceof JDialog) {
            window.dispose();
        }
    }
    
    /**
     * Crea panel de configuración individual
     */
    private JPanel crearPanelConfiguracion(ConfiguracionMantenimiento config) {
        JPanel panel = new JPanel(new BorderLayout(10, 5));
        panel.setBackground(BLANCO);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(VERDE_PRINCIPAL, 1),
            new EmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel lblTipo = new JLabel(config.getTipoActivo().getDescripcion());
        lblTipo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTipo.setForeground(VERDE_PRINCIPAL);
        
        JLabel lblInfo = new JLabel(String.format(
            "Mantenimiento cada %d días | Alerta %d días antes",
            config.getDiasMantenimiento(),
            config.getDiasAnticipoAlerta()
        ));
        lblInfo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        panel.add(lblTipo, BorderLayout.NORTH);
        panel.add(lblInfo, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * Obtiene el nombre del activo por ID
     */
    private String obtenerNombreActivo(Integer activoId) {
        if (activoId == null) return "";
        
        try {
            java.util.Optional<Activo> activoOpt = activoDAO.findById(activoId);
            if (activoOpt.isPresent()) {
                Activo activo = activoOpt.get();
                return activo.getActNumeroActivo() + " - " + activo.getActEspecificaciones();
            }
            return "Activo " + activoId;
        } catch (Exception e) {
            return "Activo " + activoId;
        }
    }
    
    /**
     * Obtiene el nombre del técnico por ID
     */
    private String obtenerNombreTecnico(Integer usuarioId) {
        if (usuarioId == null) return "Sin asignar";
        
        try {
            java.util.Optional<Usuario> usuarioOpt = usuarioDAO.findById(usuarioId);
            if (usuarioOpt.isPresent()) {
                Usuario usuario = usuarioOpt.get();
                return usuario.getUsuNombre();
            }
            return "Usuario " + usuarioId;
        } catch (Exception e) {
            return "Usuario " + usuarioId;
        }
    }
    
    /**
     * Trunca texto para mostrar en tablas
     */
    private String truncarTexto(String texto, int maxLength) {
        if (texto == null) return "";
        if (texto.length() <= maxLength) return texto;
        return texto.substring(0, maxLength - 3) + "...";
    }
    
    /**
     * Actualiza el panel de configuraciones
     */
    private void actualizarPanelConfiguraciones(JPanel panelConfiguraciones) {
        SwingUtilities.invokeLater(() -> {
            panelConfiguraciones.removeAll();
            try {
                List<ConfiguracionMantenimiento> configuraciones = mantenimientoPreventivoService.obtenerConfiguraciones();
                for (ConfiguracionMantenimiento config : configuraciones) {
                    JPanel panelConfig = crearPanelConfiguracion(config);
                    panelConfiguraciones.add(panelConfig);
                }
            } catch (Exception e) {
                JLabel lblError = new JLabel("Error al cargar configuraciones: " + e.getMessage());
                lblError.setForeground(ROJO_DANGER);
                panelConfiguraciones.add(lblError);
            }
            panelConfiguraciones.revalidate();
            panelConfiguraciones.repaint();
        });
    }
    
    // ===== MÉTODOS STUB PARA FUNCIONALIDADES ADICIONALES =====
    
    private void aplicarFiltros() {
        // TODO: Implementar filtros avanzados
        JOptionPane.showMessageDialog(this, "Funcionalidad de filtros en desarrollo", "En Desarrollo", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void limpiarFiltros() {
        actualizarTablaMantenimientos();
    }
    
    private void editarMantenimientoSeleccionado() {
        int filaSeleccionada = tablaMantenimientos.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this,
                "Seleccione un mantenimiento para editar.",
                "Selección Requerida", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            Integer mantId = (Integer) tablaMantenimientos.getValueAt(filaSeleccionada, 0);
            Mantenimiento mantenimiento = mantenimientoDAO.findById(mantId);
            
            if (mantenimiento != null) {
                modoEdicion = true;
                mantenimientoEditando = mantenimiento;
                mostrarFormularioMantenimiento(mantenimiento);
            } else {
                JOptionPane.showMessageDialog(this,
                    "No se pudo cargar el mantenimiento seleccionado.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error al cargar mantenimiento: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void eliminarMantenimientoSeleccionado() {
        int filaSeleccionada = tablaMantenimientos.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this,
                "Seleccione un mantenimiento para eliminar.",
                "Selección Requerida", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int confirmacion = JOptionPane.showConfirmDialog(this,
            "¿Está seguro que desea eliminar este mantenimiento?\nEsta acción no se puede deshacer.",
            "Confirmar Eliminación", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        
        if (confirmacion != JOptionPane.YES_OPTION) {
            return;
        }
        
        try {
            Integer mantId = (Integer) tablaMantenimientos.getValueAt(filaSeleccionada, 0);
            
            boolean eliminado = mantenimientoDAO.delete(mantId);
            
            if (eliminado) {
                JOptionPane.showMessageDialog(this,
                    "Mantenimiento eliminado exitosamente.",
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
                
                actualizarTablaMantenimientos();
                actualizarKPIs();
            } else {
                JOptionPane.showMessageDialog(this,
                    "Error al eliminar el mantenimiento.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error al eliminar mantenimiento: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void marcarAlertaComoLeida() {
        int filaSeleccionada = tablaAlertas.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this,
                "Seleccione una alerta para marcar como leída.",
                "Selección Requerida", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            Integer alertaId = (Integer) tablaAlertas.getValueAt(filaSeleccionada, 0);
            
            boolean marcada = mantenimientoPreventivoService.marcarAlertaComoLeida(alertaId);
            
            if (marcada) {
                JOptionPane.showMessageDialog(this,
                    "Alerta marcada como leída.",
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
                
                actualizarTablaAlertas();
                actualizarKPIs();
            } else {
                JOptionPane.showMessageDialog(this,
                    "Error al marcar la alerta como leída.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error al marcar alerta: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void desactivarAlerta() {
        int filaSeleccionada = tablaAlertas.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this,
                "Seleccione una alerta para desactivar.",
                "Selección Requerida", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int confirmacion = JOptionPane.showConfirmDialog(this,
            "¿Está seguro que desea desactivar esta alerta?",
            "Confirmar Desactivación", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        
        if (confirmacion != JOptionPane.YES_OPTION) {
            return;
        }
        
        try {
            Integer alertaId = (Integer) tablaAlertas.getValueAt(filaSeleccionada, 0);
            
            boolean desactivada = mantenimientoPreventivoService.desactivarAlerta(alertaId);
            
            if (desactivada) {
                JOptionPane.showMessageDialog(this,
                    "Alerta desactivada exitosamente.",
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
                
                actualizarTablaAlertas();
                actualizarKPIs();
            } else {
                JOptionPane.showMessageDialog(this,
                    "Error al desactivar la alerta.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error al desactivar alerta: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void crearTicketDesdeAlerta() {
        int filaSeleccionada = tablaAlertas.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this,
                "Seleccione una alerta para crear un ticket.",
                "Selección Requerida", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            // Obtener ID de la alerta desde la tabla
            Integer alertaId = (Integer) modeloAlertas.getValueAt(filaSeleccionada, 0);
            
            // Obtener la alerta completa desde el servicio
            AlertaMantenimiento alerta = mantenimientoPreventivoService.obtenerAlertaPorId(alertaId);
            
            if (alerta == null) {
                JOptionPane.showMessageDialog(this,
                    "No se pudo cargar la información de la alerta.",
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Verificar si ya existe un ticket abierto o en proceso para este activo
            if (existeTicketActivoParaActivo(alerta.getActivoId())) {
                int respuesta = JOptionPane.showConfirmDialog(this,
                    "Ya existe un ticket activo para este activo.\n" +
                    "¿Desea crear un nuevo ticket de todas formas?",
                    "Ticket Existente", 
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);
                    
                if (respuesta != JOptionPane.YES_OPTION) {
                    return;
                }
            }
            
            // Obtener el activo
            Optional<Activo> activoOpt = activoDAO.findById(alerta.getActivoId());
            if (!activoOpt.isPresent()) {
                JOptionPane.showMessageDialog(this,
                    "No se pudo cargar la información del activo.",
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            Activo activo = activoOpt.get();
            
            // Crear el ticket con los datos de la alerta
            Ticket ticket = new Ticket();
            ticket.setActId(activo.getActId());
            ticket.setTickTipo(Ticket.Tipo.Preventivo);
            ticket.setTickPrioridad(mapearPrioridadDeAlerta(alerta.getNivelUrgencia()));
            ticket.setTickTitulo(alerta.getTitulo());
            ticket.setTickDescripcion("TICKET CREADO DESDE ALERTA\n\n" +
                "Alerta ID: " + alerta.getAlertaId() + "\n" +
                "Tipo de Alerta: " + alerta.getTipoAlerta().getDescripcion() + "\n" +
                "Nivel de Urgencia: " + alerta.getNivelUrgencia().getDescripcion() + "\n" +
                "Días Restantes: " + alerta.getDiasRestantes() + "\n\n" +
                "Mensaje de Alerta:\n" + alerta.getMensaje() + "\n\n" +
                "---\n" +
                "Activo: " + activo.getActNumeroActivo() + " - " + activo.getActEspecificaciones());
            ticket.setTickEstado(Ticket.Estado.Abierto);
            ticket.setTickReportadoPor(usuarioActual.getUsuId());
            
            // Asignar técnico si la alerta tiene uno asignado
            if (alerta.getUsuarioAsignadoId() != null) {
                ticket.setTickAsignadoA(alerta.getUsuarioAsignadoId());
            }
            
            // Establecer fecha de vencimiento basada en la urgencia
            LocalDateTime fechaVencimiento = LocalDateTime.now();
            switch (alerta.getNivelUrgencia()) {
                case CRITICO:
                    fechaVencimiento = fechaVencimiento.plusHours(24); // 1 día
                    break;
                case URGENTE:
                    fechaVencimiento = fechaVencimiento.plusDays(2);
                    break;
                case ADVERTENCIA:
                    fechaVencimiento = fechaVencimiento.plusDays(5);
                    break;
                case INFO:
                    fechaVencimiento = fechaVencimiento.plusDays(7);
                    break;
            }
            ticket.setTickFechaVencimiento(fechaVencimiento);
            
            // Guardar el ticket
            boolean ticketCreado = ticketDAO.save(ticket);
            
            if (ticketCreado) {
                // Marcar la alerta como leída ya que se atendió
                mantenimientoPreventivoService.marcarAlertaComoLeida(alertaId);
                
                JOptionPane.showMessageDialog(this,
                    "✅ Ticket creado exitosamente\n\n" +
                    "Número de Ticket: #" + ticket.getTickId() + "\n" +
                    "Prioridad: " + ticket.getTickPrioridad() + "\n" +
                    "Fecha de Vencimiento: " + fechaVencimiento.format(
                        DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) + "\n\n" +
                    "El ticket ha sido creado y asignado correctamente.",
                    "Ticket Creado", 
                    JOptionPane.INFORMATION_MESSAGE);
                
                // Actualizar la tabla de alertas
                actualizarTablaAlertas();
            } else {
                JOptionPane.showMessageDialog(this,
                    "No se pudo crear el ticket.\n" +
                    "Por favor, intente nuevamente o contacte al administrador.",
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Error al crear ticket desde alerta: " + e.getMessage() + "\n\n" +
                "Detalles: " + e.getClass().getSimpleName(),
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Mapea el nivel de urgencia de una alerta a la prioridad de un ticket
     */
    private Ticket.Prioridad mapearPrioridadDeAlerta(AlertaMantenimiento.NivelUrgencia urgencia) {
        if (urgencia == null) {
            return Ticket.Prioridad.Media;
        }
        
        switch (urgencia) {
            case CRITICO:
                return Ticket.Prioridad.Critica;
            case URGENTE:
                return Ticket.Prioridad.Alta;
            case ADVERTENCIA:
                return Ticket.Prioridad.Media;
            case INFO:
                return Ticket.Prioridad.Baja;
            default:
                return Ticket.Prioridad.Media;
        }
    }
    
    /**
     * Verifica si existe un ticket activo (Abierto o En_Proceso) para el activo
     */
    private boolean existeTicketActivoParaActivo(Integer activoId) {
        try {
            List<Ticket> tickets = ticketDAO.findByActivo(activoId);
            
            if (tickets == null || tickets.isEmpty()) {
                return false;
            }
            
            // Verificar si hay tickets en estado Abierto o En_Proceso
            return tickets.stream()
                .anyMatch(t -> t.getTickEstado() == Ticket.Estado.Abierto || 
                              t.getTickEstado() == Ticket.Estado.En_Proceso);
                              
        } catch (Exception e) {
            System.err.println("Error al verificar tickets para activo " + activoId + ": " + e.getMessage());
            return false;
        }
    }
    
    // ===== REPORTES AHORA CENTRALIZADOS EN REPORTESPANEL =====
    // Los métodos de generación de reportes fueron movidos al módulo centralizado
    // Acceso mediante el botón "Reportes Completos" en el Dashboard
    
    // ===== MÉTODOS PARA OBTENER DATOS REALES DEL DASHBOARD =====
    
    private int obtenerMantenimientosActivos() {
        try {
            // Valor temporal - implementar consulta real después
            return 4;
        } catch (Exception e) {
            System.err.println("Error obteniendo mantenimientos activos: " + e.getMessage());
            return 0;
        }
    }
    
    private int obtenerPreventivosHoy() {
        try {
            // Implementación básica - obtener preventivos programados para hoy
            return 2; // Valor temporal hasta implementar consulta real
        } catch (Exception e) {
            System.err.println("Error obteniendo preventivos hoy: " + e.getMessage());
            return 0;
        }
    }
    
    private int obtenerCorrectivosUrgentes() {
        try {
            // Implementación básica
            return 1; // Valor temporal
        } catch (Exception e) {
            System.err.println("Error obteniendo correctivos urgentes: " + e.getMessage());
            return 0;
        }
    }
    
    private int obtenerCompletadosSemana() {
        try {
            // Implementación básica
            return 5; // Valor temporal
        } catch (Exception e) {
            System.err.println("Error obteniendo completados esta semana: " + e.getMessage());
            return 0;
        }
    }
    
    private int obtenerAlertasPendientes() {
        try {
            // Valor temporal - implementar consulta real después
            return 3;
        } catch (Exception e) {
            System.err.println("Error obteniendo alertas pendientes: " + e.getMessage());
            return 0;
        }
    }
    
    private int obtenerTecnicosDisponibles() {
        try {
            // Implementación básica - contar usuarios técnicos
            return 3; // Valor temporal
        } catch (Exception e) {
            System.err.println("Error obteniendo técnicos disponibles: " + e.getMessage());
            return 0;
        }
    }
    
    private int obtenerActivosMonitoreados() {
        try {
            // Valor temporal - implementar consulta real después 
            return 15;
        } catch (Exception e) {
            System.err.println("Error obteniendo activos monitoreados: " + e.getMessage());
            return 0;
        }
    }
    
    private int calcularEficiencia() {
        try {
            // Cálculo básico de eficiencia
            int completados = obtenerCompletadosSemana();
            int totales = obtenerMantenimientosActivos() + completados;
            return totales > 0 ? (int) ((completados * 100) / totales) : 100;
        } catch (Exception e) {
            System.err.println("Error calculando eficiencia: " + e.getMessage());
            return 85; // Valor por defecto
        }
    }
}
