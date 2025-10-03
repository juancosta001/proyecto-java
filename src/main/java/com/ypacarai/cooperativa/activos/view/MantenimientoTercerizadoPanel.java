package com.ypacarai.cooperativa.activos.view;

import com.ypacarai.cooperativa.activos.model.*;
import com.ypacarai.cooperativa.activos.service.MantenimientoTercerizadoService;
import com.ypacarai.cooperativa.activos.service.ActivoService;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Panel principal para la gestión de Mantenimiento Técnico Tercerizado
 * Sistema de Gestión de Activos - Cooperativa Ypacaraí LTDA
 */
public class MantenimientoTercerizadoPanel extends JPanel {
    
    // Colores corporativos
    private static final Color COLOR_VERDE_COOPERATIVA = new Color(34, 139, 34);
    private static final Color COLOR_VERDE_CLARO = new Color(144, 238, 144);
    private static final Color COLOR_BLANCO = Color.WHITE;
    private static final Color COLOR_GRIS_CLARO = new Color(245, 245, 245);
    private static final Color COLOR_GRIS_TEXTO = new Color(64, 64, 64);
    private static final Color COLOR_AZUL_INFO = new Color(70, 130, 180);
    private static final Color COLOR_NARANJA_WARNING = new Color(255, 140, 0);
    private static final Color COLOR_ROJO_DANGER = new Color(220, 20, 60);
    
    private final Usuario usuarioActual;
    private final MantenimientoTercerizadoService mantenimientoService;
    private final ActivoService activoService;
    
    // Componentes principales
    private JTabbedPane tabbedPane;
    private JTable tablaMantenimientos;
    private JTable tablaProveedores;
    private DefaultTableModel modeloMantenimientos;
    private DefaultTableModel modeloProveedores;
    private JComboBox<String> cmbFiltroEstado;
    private JTextField txtBuscar;
    
    public MantenimientoTercerizadoPanel(Usuario usuario) {
        this.usuarioActual = usuario;
        this.mantenimientoService = new MantenimientoTercerizadoService();
        this.activoService = new ActivoService();
        
        initializeComponents();
        setupEventListeners();
        cargarDatos();
    }
    
    private void initializeComponents() {
        setLayout(new BorderLayout());
        setBackground(COLOR_BLANCO);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Título principal
        JLabel lblTitulo = new JLabel("🔧 Mantenimiento Técnico Tercerizado");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitulo.setForeground(COLOR_VERDE_COOPERATIVA);
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        
        // Panel de título con estadísticas
        JPanel panelHeader = new JPanel(new BorderLayout());
        panelHeader.setOpaque(false);
        panelHeader.add(lblTitulo, BorderLayout.WEST);
        panelHeader.add(createPanelEstadisticas(), BorderLayout.EAST);
        
        add(panelHeader, BorderLayout.NORTH);
        
        // Crear el panel con pestañas
        tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        tabbedPane.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        // Pestaña de Mantenimientos
        JPanel panelMantenimientos = createPanelMantenimientos();
        tabbedPane.addTab("📋 Mantenimientos", panelMantenimientos);
        
        // Pestaña de Proveedores
        JPanel panelProveedores = createPanelProveedores();
        tabbedPane.addTab("🏢 Proveedores", panelProveedores);
        
        add(tabbedPane, BorderLayout.CENTER);
    }
    
    private JPanel createPanelEstadisticas() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        panel.setOpaque(false);
        
        try {
            List<MantenimientoTercerizado> pendientes = mantenimientoService.obtenerMantenimientosPendientes();
            List<MantenimientoTercerizado> enGarantia = mantenimientoService.obtenerMantenimientosEnGarantia();
            
            panel.add(createStatCard("⏳ Pendientes", String.valueOf(pendientes.size()), COLOR_NARANJA_WARNING));
            panel.add(createStatCard("🛡️ En Garantía", String.valueOf(enGarantia.size()), COLOR_AZUL_INFO));
            
        } catch (Exception e) {
            System.err.println("Error cargando estadísticas: " + e.getMessage());
        }
        
        return panel;
    }
    
    private JPanel createStatCard(String titulo, String valor, Color color) {
        JPanel card = new JPanel(new BorderLayout(5, 5));
        card.setBackground(COLOR_BLANCO);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color, 2),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        card.setPreferredSize(new Dimension(120, 60));
        
        JLabel lblTitulo = new JLabel(titulo, SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lblTitulo.setForeground(COLOR_GRIS_TEXTO);
        
        JLabel lblValor = new JLabel(valor, SwingConstants.CENTER);
        lblValor.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblValor.setForeground(color);
        
        card.add(lblTitulo, BorderLayout.NORTH);
        card.add(lblValor, BorderLayout.CENTER);
        
        return card;
    }
    
    private JPanel createPanelMantenimientos() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setOpaque(false);
        
        // Panel de filtros y botones
        JPanel panelControles = createPanelControlesMantenimientos();
        panel.add(panelControles, BorderLayout.NORTH);
        
        // Tabla de mantenimientos
        String[] columnasMantenimientos = {
            "ID", "Activo", "Proveedor", "Problema", "Estado", 
            "F. Retiro", "F. Entrega", "Monto", "Registrado Por", "Fecha Solicitud"
        };
        
        modeloMantenimientos = new DefaultTableModel(columnasMantenimientos, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tablaMantenimientos = new JTable(modeloMantenimientos);
        tablaMantenimientos.setRowHeight(28);
        tablaMantenimientos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaMantenimientos.setRowSorter(new TableRowSorter<>(modeloMantenimientos));
        
        // Configurar ancho de columnas
        tablaMantenimientos.getColumnModel().getColumn(0).setPreferredWidth(50);  // ID
        tablaMantenimientos.getColumnModel().getColumn(1).setPreferredWidth(80);  // Activo
        tablaMantenimientos.getColumnModel().getColumn(2).setPreferredWidth(120); // Proveedor
        tablaMantenimientos.getColumnModel().getColumn(3).setPreferredWidth(200); // Problema
        tablaMantenimientos.getColumnModel().getColumn(4).setPreferredWidth(80);  // Estado
        tablaMantenimientos.getColumnModel().getColumn(5).setPreferredWidth(80);  // F. Retiro
        tablaMantenimientos.getColumnModel().getColumn(6).setPreferredWidth(80);  // F. Entrega
        tablaMantenimientos.getColumnModel().getColumn(7).setPreferredWidth(80);  // Monto
        tablaMantenimientos.getColumnModel().getColumn(8).setPreferredWidth(100); // Registrado Por
        tablaMantenimientos.getColumnModel().getColumn(9).setPreferredWidth(100); // Fecha Solicitud
        
        JScrollPane scrollMantenimientos = new JScrollPane(tablaMantenimientos);
        scrollMantenimientos.setPreferredSize(new Dimension(0, 300));
        panel.add(scrollMantenimientos, BorderLayout.CENTER);
        
        // Panel de acciones para la fila seleccionada
        JPanel panelAcciones = createPanelAccionesMantenimiento();
        panel.add(panelAcciones, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createPanelControlesMantenimientos() {
        JPanel panel = new JPanel(new BorderLayout(10, 5));
        panel.setOpaque(false);
        
        // Panel izquierdo - filtros
        JPanel panelFiltros = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        panelFiltros.setOpaque(false);
        
        JLabel lblFiltro = new JLabel("Filtrar por estado:");
        lblFiltro.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        cmbFiltroEstado = new JComboBox<>(new String[]{"Todos", "Solicitado", "En_Proceso", "Finalizado", "Cancelado"});
        cmbFiltroEstado.setPreferredSize(new Dimension(120, 25));
        
        JLabel lblBuscar = new JLabel("Buscar:");
        txtBuscar = new JTextField(15);
        txtBuscar.setPreferredSize(new Dimension(150, 25));
        
        panelFiltros.add(lblFiltro);
        panelFiltros.add(cmbFiltroEstado);
        panelFiltros.add(Box.createHorizontalStrut(20));
        panelFiltros.add(lblBuscar);
        panelFiltros.add(txtBuscar);
        
        // Panel derecho - botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        panelBotones.setOpaque(false);
        
        JButton btnNuevoMantenimiento = createStyledButton("➕ Solicitar Mantenimiento", COLOR_VERDE_COOPERATIVA);
        JButton btnActualizar = createStyledButton("🔄 Actualizar", COLOR_AZUL_INFO);
        
        // Agregar ActionListeners
        btnNuevoMantenimiento.addActionListener(e -> solicitarMantenimiento());
        btnActualizar.addActionListener(e -> cargarDatos());
        
        panelBotones.add(btnNuevoMantenimiento);
        panelBotones.add(btnActualizar);
        
        panel.add(panelFiltros, BorderLayout.WEST);
        panel.add(panelBotones, BorderLayout.EAST);
        
        return panel;
    }
    
    private JPanel createPanelAccionesMantenimiento() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createTitledBorder("Acciones para el mantenimiento seleccionado"));
        
        JButton btnVerDetalles = createStyledButton("👁️ Ver Detalles", COLOR_AZUL_INFO);
        JButton btnRegistrarRetiro = createStyledButton("📤 Registrar Retiro", COLOR_NARANJA_WARNING);
        JButton btnRegistrarEntrega = createStyledButton("📥 Registrar Entrega", COLOR_VERDE_COOPERATIVA);
        JButton btnCancelar = createStyledButton("❌ Cancelar", COLOR_ROJO_DANGER);
        
        // Agregar ActionListeners
        btnVerDetalles.addActionListener(e -> verDetallesMantenimiento());
        btnRegistrarRetiro.addActionListener(e -> registrarRetiro());
        btnRegistrarEntrega.addActionListener(e -> registrarEntrega());
        btnCancelar.addActionListener(e -> cancelarMantenimiento());
        
        panel.add(btnVerDetalles);
        panel.add(btnRegistrarRetiro);
        panel.add(btnRegistrarEntrega);
        panel.add(btnCancelar);
        
        return panel;
    }
    
    private JPanel createPanelProveedores() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setOpaque(false);
        
        // Panel de controles
        JPanel panelControlesProveedores = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        panelControlesProveedores.setOpaque(false);
        
        JButton btnNuevoProveedor = createStyledButton("➕ Nuevo Proveedor", COLOR_VERDE_COOPERATIVA);
        JButton btnEditarProveedor = createStyledButton("✏️ Editar", COLOR_AZUL_INFO);
        JButton btnToggleActivo = createStyledButton("🔄 Activar/Desactivar", COLOR_NARANJA_WARNING);
        
        // Agregar ActionListeners
        btnNuevoProveedor.addActionListener(e -> nuevoProveedor());
        btnEditarProveedor.addActionListener(e -> editarProveedor());
        btnToggleActivo.addActionListener(e -> toggleProveedorActivo());
        
        panelControlesProveedores.add(btnNuevoProveedor);
        panelControlesProveedores.add(btnEditarProveedor);
        panelControlesProveedores.add(btnToggleActivo);
        
        panel.add(panelControlesProveedores, BorderLayout.NORTH);
        
        // Tabla de proveedores
        String[] columnasProveedores = {
            "ID", "Nombre", "Teléfono", "Email", "Contacto", "Especialidades", "Activo"
        };
        
        modeloProveedores = new DefaultTableModel(columnasProveedores, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tablaProveedores = new JTable(modeloProveedores);
        tablaProveedores.setRowHeight(28);
        tablaProveedores.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaProveedores.setRowSorter(new TableRowSorter<>(modeloProveedores));
        
        // Configurar ancho de columnas
        tablaProveedores.getColumnModel().getColumn(0).setPreferredWidth(50);   // ID
        tablaProveedores.getColumnModel().getColumn(1).setPreferredWidth(150);  // Nombre
        tablaProveedores.getColumnModel().getColumn(2).setPreferredWidth(100);  // Teléfono
        tablaProveedores.getColumnModel().getColumn(3).setPreferredWidth(150);  // Email
        tablaProveedores.getColumnModel().getColumn(4).setPreferredWidth(120);  // Contacto
        tablaProveedores.getColumnModel().getColumn(5).setPreferredWidth(200);  // Especialidades
        tablaProveedores.getColumnModel().getColumn(6).setPreferredWidth(60);   // Activo
        
        JScrollPane scrollProveedores = new JScrollPane(tablaProveedores);
        panel.add(scrollProveedores, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JButton createStyledButton(String text, Color backgroundColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        button.setBackground(backgroundColor);
        button.setForeground(COLOR_BLANCO);
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Efecto hover
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(backgroundColor.brighter());
            }
            
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(backgroundColor);
            }
        });
        
        return button;
    }
    
    private void setupEventListeners() {
        // Filtro por estado
        cmbFiltroEstado.addActionListener(e -> aplicarFiltros());
        
        // Búsqueda en tiempo real
        txtBuscar.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent evt) {
                aplicarFiltros();
            }
        });
        
        // TODO: Implementar configuración de listeners de botones cuando se tenga acceso a los componentes
        
        // Double click en tabla para ver detalles
        tablaMantenimientos.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    verDetallesMantenimiento();
                }
            }
        });
        
        tablaProveedores.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    editarProveedor();
                }
            }
        });
    }
    
    private void cargarDatos() {
        cargarMantenimientos();
        cargarProveedores();
    }
    
    private void cargarMantenimientos() {
        try {
            List<MantenimientoTercerizado> mantenimientos = mantenimientoService.obtenerTodosMantenimientos();
            modeloMantenimientos.setRowCount(0);
            
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            
            for (MantenimientoTercerizado mant : mantenimientos) {
                Object[] fila = {
                    mant.getMantTercId(),
                    mant.getNumeroActivo(),
                    mant.getNombreProveedor(),
                    truncarTexto(mant.getDescripcionProblema(), 50),
                    mant.getEstado().toString().replace("_", " "),
                    mant.getFechaRetiro() != null ? mant.getFechaRetiro().format(formatter) : "-",
                    mant.getFechaEntrega() != null ? mant.getFechaEntrega().format(formatter) : "-",
                    mant.getMontoAPagar() != null ? "₲ " + String.format("%,.0f", mant.getMontoAPagar()) : "-",
                    mant.getNombreRegistrador(),
                    mant.getCreadoEn().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
                };
                modeloMantenimientos.addRow(fila);
            }
            
            System.out.println("Mantenimientos cargados: " + mantenimientos.size());
            
        } catch (Exception e) {
            System.err.println("Error cargando mantenimientos: " + e.getMessage());
            JOptionPane.showMessageDialog(this,
                "Error al cargar los mantenimientos: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void cargarProveedores() {
        try {
            List<ProveedorServicio> proveedores = mantenimientoService.obtenerTodosProveedores();
            modeloProveedores.setRowCount(0);
            
            for (ProveedorServicio proveedor : proveedores) {
                Object[] fila = {
                    proveedor.getPrvId(),
                    proveedor.getPrvNombre(),
                    proveedor.getPrvNumeroTelefono(),
                    proveedor.getPrvEmail() != null ? proveedor.getPrvEmail() : "-",
                    proveedor.getPrvContactoPrincipal(),
                    truncarTexto(proveedor.getPrvEspecialidades(), 80),
                    proveedor.isActivo() ? "✅ Sí" : "❌ No"
                };
                modeloProveedores.addRow(fila);
            }
            
            System.out.println("Proveedores cargados: " + proveedores.size());
            
        } catch (Exception e) {
            System.err.println("Error cargando proveedores: " + e.getMessage());
            JOptionPane.showMessageDialog(this,
                "Error al cargar los proveedores: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void aplicarFiltros() {
        // TODO: Implementar filtros de búsqueda
        System.out.println("Aplicando filtros...");
    }
    
    private String truncarTexto(String texto, int maxLength) {
        if (texto == null || texto.length() <= maxLength) {
            return texto;
        }
        return texto.substring(0, maxLength - 3) + "...";
    }
    
    /**
     * Actualiza los datos de las tablas
     */
    public void actualizarDatos() {
        cargarDatos();
    }
    
    // ==================== MÉTODOS DE ACCIÓN ====================
    
    private void abrirSolicitudMantenimiento() {
        SolicitudMantenimientoTercerizadoWindow ventana = new SolicitudMantenimientoTercerizadoWindow(
            SwingUtilities.getWindowAncestor(this), usuarioActual);
        ventana.setVisible(true);
        // Actualizar datos después de cerrar la ventana
        actualizarDatos();
    }
    
    private void registrarRetiro() {
        int selectedRow = tablaMantenimientos.getSelectedRow();
        if (selectedRow >= 0) {
            try {
                // Obtener el mantenimiento seleccionado
                int mantId = (Integer) modeloMantenimientos.getValueAt(selectedRow, 0);
                MantenimientoTercerizado mantenimiento = mantenimientoService.obtenerPorId(mantId);
                
                if (mantenimiento == null) {
                    mostrarError("No se encontró el mantenimiento seleccionado");
                    return;
                }
                
                // Verificar que el estado permite registrar retiro
                if (mantenimiento.getEstado() != MantenimientoTercerizado.EstadoMantenimiento.Solicitado) {
                    mostrarError("Solo se puede registrar retiro para mantenimientos en estado 'Solicitado'");
                    return;
                }
                
                // Abrir ventana de retiro
                RetiroEntregaWindow ventanaRetiro = new RetiroEntregaWindow(
                    this, mantenimiento, true, // true = es retiro
                    usuarioActual
                );
                ventanaRetiro.setVisible(true);
                
            } catch (Exception e) {
                mostrarError("Error al procesar el retiro: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            JOptionPane.showMessageDialog(this,
                "Seleccione un mantenimiento de la tabla.",
                "Información", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void registrarEntrega() {
        int selectedRow = tablaMantenimientos.getSelectedRow();
        if (selectedRow >= 0) {
            try {
                // Obtener el mantenimiento seleccionado
                int mantId = (Integer) modeloMantenimientos.getValueAt(selectedRow, 0);
                MantenimientoTercerizado mantenimiento = mantenimientoService.obtenerPorId(mantId);
                
                if (mantenimiento == null) {
                    mostrarError("No se encontró el mantenimiento seleccionado");
                    return;
                }
                
                // Verificar que el estado permite registrar entrega
                if (mantenimiento.getEstado() != MantenimientoTercerizado.EstadoMantenimiento.En_Proceso) {
                    mostrarError("Solo se puede registrar entrega para mantenimientos en estado 'En Proceso'");
                    return;
                }
                
                // Abrir ventana de entrega
                RetiroEntregaWindow ventanaEntrega = new RetiroEntregaWindow(
                    this, mantenimiento, false, // false = es entrega
                    usuarioActual
                );
                ventanaEntrega.setVisible(true);
                
            } catch (Exception e) {
                mostrarError("Error al procesar la entrega: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            JOptionPane.showMessageDialog(this,
                "Seleccione un mantenimiento de la tabla.",
                "Información", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void cancelarMantenimiento() {
        int selectedRow = tablaMantenimientos.getSelectedRow();
        if (selectedRow >= 0) {
            int mantId = (Integer) modeloMantenimientos.getValueAt(selectedRow, 0);
            String estado = (String) modeloMantenimientos.getValueAt(selectedRow, 4);
            
            if (!estado.equals("Solicitado")) {
                JOptionPane.showMessageDialog(this,
                    "Solo se pueden cancelar mantenimientos en estado 'Solicitado'.",
                    "Advertencia", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            int respuesta = JOptionPane.showConfirmDialog(this,
                "¿Está seguro que desea cancelar este mantenimiento?\nID: " + mantId,
                "Confirmar Cancelación", JOptionPane.YES_NO_OPTION);
            
            if (respuesta == JOptionPane.YES_OPTION) {
                try {
                    boolean cancelado = mantenimientoService.cancelarMantenimiento(mantId, "Cancelado por usuario");
                    if (cancelado) {
                        JOptionPane.showMessageDialog(this,
                            "Mantenimiento cancelado exitosamente.",
                            "Cancelación Exitosa", JOptionPane.INFORMATION_MESSAGE);
                        actualizarDatos();
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this,
                        "Error al cancelar el mantenimiento: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this,
                "Seleccione un mantenimiento de la tabla.",
                "Información", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void nuevoProveedor() {
        try {
            ProveedorServicioWindow ventana = new ProveedorServicioWindow(SwingUtilities.getWindowAncestor(this));
            ventana.setVisible(true);
            
            // Agregar listener para refrescar tabla al cerrar
            ventana.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosed(java.awt.event.WindowEvent e) {
                    cargarProveedores(); // Refrescar tabla al cerrar la ventana
                }
            });
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error al abrir la ventana de proveedor: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void toggleProveedorActivo() {
        int selectedRow = tablaProveedores.getSelectedRow();
        if (selectedRow >= 0) {
            int provId = (Integer) modeloProveedores.getValueAt(selectedRow, 0);
            String nombre = (String) modeloProveedores.getValueAt(selectedRow, 1);
            String estadoActual = (String) modeloProveedores.getValueAt(selectedRow, 6);
            boolean esActivo = estadoActual.contains("Sí");
            
            String accion = esActivo ? "desactivar" : "activar";
            int respuesta = JOptionPane.showConfirmDialog(this,
                "¿Está seguro que desea " + accion + " el proveedor?\n" +
                "Nombre: " + nombre,
                "Confirmar " + accion, JOptionPane.YES_NO_OPTION);
            
            if (respuesta == JOptionPane.YES_OPTION) {
                try {
                    boolean resultado = mantenimientoService.toggleProveedorActivo(provId, !esActivo);
                    if (resultado) {
                        JOptionPane.showMessageDialog(this,
                            "Proveedor " + (esActivo ? "desactivado" : "activado") + " exitosamente.",
                            "Estado Actualizado", JOptionPane.INFORMATION_MESSAGE);
                        cargarProveedores();
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this,
                        "Error al cambiar el estado del proveedor: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this,
                "Seleccione un proveedor de la tabla.",
                "Información", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    // ====== MÉTODOS DE ACCIÓN PARA BOTONES ======
    
    /**
     * Abre la ventana para solicitar un nuevo mantenimiento tercerizado
     */
    private void solicitarMantenimiento() {
        try {
            SolicitudMantenimientoTercerizadoWindow ventana = 
                new SolicitudMantenimientoTercerizadoWindow(
                    SwingUtilities.getWindowAncestor(this), usuarioActual);
            ventana.setVisible(true);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error al abrir la ventana de solicitud: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Ver detalles completos de un mantenimiento
     */
    private void verDetallesMantenimiento() {
        int filaSeleccionada = tablaMantenimientos.getSelectedRow();
        if (filaSeleccionada >= 0) {
            try {
                int mantenimientoId = (Integer) modeloMantenimientos.getValueAt(filaSeleccionada, 0);
                System.out.println("=== DEBUG: Ver Detalles Mantenimiento ===");
                System.out.println("ID seleccionado: " + mantenimientoId);
                
                MantenimientoTercerizado mantenimiento = mantenimientoService.obtenerPorId(mantenimientoId);
                
                if (mantenimiento != null) {
                    System.out.println("Mantenimiento obtenido:");
                    System.out.println("- Número Activo: '" + mantenimiento.getNumeroActivo() + "'");
                    System.out.println("- Marca Activo: '" + mantenimiento.getMarcaActivo() + "'");
                    System.out.println("- Modelo Activo: '" + mantenimiento.getModeloActivo() + "'");
                    System.out.println("- Nombre Registrador: '" + mantenimiento.getNombreRegistrador() + "'");
                    
                    DetallesMantenimientoWindow ventana = new DetallesMantenimientoWindow(this, mantenimiento);
                    ventana.setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(this,
                        "No se pudo cargar la información del mantenimiento",
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                    "Error al cargar los detalles: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * Edita un proveedor seleccionado
     */
    private void editarProveedor() {
        int filaSeleccionada = tablaProveedores.getSelectedRow();
        if (filaSeleccionada >= 0) {
            try {
                int proveedorId = (Integer) modeloProveedores.getValueAt(filaSeleccionada, 0);
                ProveedorServicio proveedor = mantenimientoService.obtenerProveedorPorId(proveedorId);
                
                if (proveedor != null) {
                    ProveedorServicioWindow ventana = new ProveedorServicioWindow(
                        SwingUtilities.getWindowAncestor(this), proveedor);
                    ventana.setVisible(true);
                    // Refrescar tabla después de cerrar la ventana
                    ventana.addWindowListener(new java.awt.event.WindowAdapter() {
                        @Override
                        public void windowClosed(java.awt.event.WindowEvent e) {
                            cargarProveedores();
                        }
                    });
                } else {
                    JOptionPane.showMessageDialog(this,
                        "No se pudo cargar la información del proveedor",
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                    "Error al cargar el proveedor: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this,
                "Seleccione un proveedor de la tabla para editar",
                "Información", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    /**
     * Método para actualizar las tablas desde ventanas externas
     */
    public void actualizarTablas() {
        SwingUtilities.invokeLater(() -> {
            try {
                cargarDatos();
            } catch (Exception e) {
                System.err.println("Error actualizando tablas: " + e.getMessage());
            }
        });
    }
    
    /**
     * Método para mostrar mensajes de error
     */
    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this,
            "⚠️ " + mensaje,
            "Error",
            JOptionPane.ERROR_MESSAGE);
    }
}