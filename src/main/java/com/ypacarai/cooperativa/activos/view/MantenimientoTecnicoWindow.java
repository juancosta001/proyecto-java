package com.ypacarai.cooperativa.activos.view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.ypacarai.cooperativa.activos.dao.TicketDAO;
import com.ypacarai.cooperativa.activos.dao.MantenimientoDAO;
import com.ypacarai.cooperativa.activos.model.Ticket;
import com.ypacarai.cooperativa.activos.model.Mantenimiento;
import com.ypacarai.cooperativa.activos.model.Usuario;

/**
 * Ventana Simplificada para Técnicos - Gestión de Mantenimientos
 * Permite a los técnicos ver y completar mantenimientos de forma eficiente
 */
public class MantenimientoTecnicoWindow extends JFrame {
    
    // Colores de la interfaz
    private static final Color COLOR_VERDE_COOPERATIVA = new Color(34, 139, 34);
    private static final Color COLOR_AZUL_INFO = new Color(70, 130, 180);
    private static final Color COLOR_NARANJA_WARNING = new Color(255, 165, 0);
    private static final Color COLOR_ROJO_ERROR = new Color(220, 20, 60);
    private static final Color COLOR_GRIS_CLARO = new Color(248, 249, 250);
    private static final Color COLOR_BLANCO = Color.WHITE;
    
    // Componentes de la interfaz
    private Usuario usuarioTecnico;
    private JTable tablaMantenimientos;
    private DefaultTableModel modeloTabla;
    private JComboBox<String> cmbEstadoCompletado;
    private JTextArea txtObservaciones;
    private JButton btnCompletarMantenimiento;
    private JButton btnActualizar;
    private JButton btnCerrar;
    
    // DAOs
    private TicketDAO ticketDAO;
    private MantenimientoDAO mantenimientoDAO;
    
    public MantenimientoTecnicoWindow(JFrame parent, Usuario usuarioTecnico) {
        super();
        this.usuarioTecnico = usuarioTecnico;
        this.ticketDAO = new TicketDAO();
        this.mantenimientoDAO = new MantenimientoDAO();
        
        initializeComponents();
        setupUI();
        cargarDatos();
        setupEventHandlers();
        
        setTitle("🔧 Mis Mantenimientos - " + usuarioTecnico.getUsuNombre());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(parent);
        setResizable(true);
    }
    
    private void initializeComponents() {
        // Tabla de mantenimientos
        String[] columnas = {
            "ID Ticket", "Equipo", "Ubicación", "Tipo Mantenimiento", 
            "Prioridad", "Estado", "Fecha Creación", "Fecha Programada"
        };
        
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tablaMantenimientos = new JTable(modeloTabla);
        setupTableStyle();
        
        // ComboBox para estado completado
        cmbEstadoCompletado = new JComboBox<>(new String[]{
            "Completado - Sin problemas",
            "Completado - Con observaciones menores",
            "Completado - Requiere seguimiento",
            "No completado - Falta repuestos",
            "No completado - Requiere especialista",
            "Reprogramar - Equipo en uso",
            "Reprogramar - Condiciones adversas"
        });
        
        // Área de texto para observaciones
        txtObservaciones = new JTextArea(4, 30);
        txtObservaciones.setLineWrap(true);
        txtObservaciones.setWrapStyleWord(true);
        txtObservaciones.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        // Botones
        btnCompletarMantenimiento = createStyledButton("✅ Completar Mantenimiento", COLOR_VERDE_COOPERATIVA, COLOR_BLANCO, true);
        btnActualizar = createStyledButton("🔄 Actualizar Lista", COLOR_AZUL_INFO, COLOR_BLANCO, true);
        btnCerrar = createStyledButton("❌ Cerrar", COLOR_ROJO_ERROR, COLOR_BLANCO, true);
    }
    
    private void setupUI() {
        setLayout(new BorderLayout(10, 10));
        
        // Panel principal con gradiente
        JPanel panelPrincipal = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                GradientPaint gradient = new GradientPaint(
                    0, 0, COLOR_BLANCO,
                    0, getHeight(), COLOR_GRIS_CLARO
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        panelPrincipal.setLayout(new BorderLayout(15, 15));
        panelPrincipal.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Panel de título
        JPanel panelTitulo = createTitlePanel();
        panelPrincipal.add(panelTitulo, BorderLayout.NORTH);
        
        // Panel central - tabla
        JPanel panelCentral = createCentralPanel();
        panelPrincipal.add(panelCentral, BorderLayout.CENTER);
        
        // Panel inferior - formulario de completado
        JPanel panelInferior = createFormPanel();
        panelPrincipal.add(panelInferior, BorderLayout.SOUTH);
        
        add(panelPrincipal, BorderLayout.CENTER);
    }
    
    private JPanel createTitlePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        
        JLabel lblTitulo = new JLabel("🔧 Mis Mantenimientos Asignados");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitulo.setForeground(COLOR_VERDE_COOPERATIVA);
        
        JLabel lblSubtitulo = new JLabel("Técnico: " + usuarioTecnico.getUsuNombre() + " | " + 
                                      LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        lblSubtitulo.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblSubtitulo.setForeground(Color.GRAY);
        
        panel.add(lblTitulo, BorderLayout.NORTH);
        panel.add(lblSubtitulo, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createCentralPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setOpaque(false);
        
        // Panel de información
        JPanel panelInfo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelInfo.setOpaque(false);
        
        JLabel lblInfo = new JLabel("💡 Selecciona un mantenimiento de la tabla para completarlo");
        lblInfo.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblInfo.setForeground(COLOR_AZUL_INFO);
        panelInfo.add(lblInfo);
        
        // Tabla con scroll
        JScrollPane scrollPane = new JScrollPane(tablaMantenimientos);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(COLOR_VERDE_COOPERATIVA, 2),
                " Mantenimientos Pendientes ",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 12),
                COLOR_VERDE_COOPERATIVA
            ),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        scrollPane.getViewport().setBackground(COLOR_BLANCO);
        
        panel.add(panelInfo, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(COLOR_VERDE_COOPERATIVA, 2),
                " Completar Mantenimiento ",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 12),
                COLOR_VERDE_COOPERATIVA
            ),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        panel.setOpaque(false);
        
        // Panel de formulario
        JPanel panelFormulario = new JPanel(new GridBagLayout());
        panelFormulario.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Estado del completado
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        JLabel lblEstado = new JLabel("Estado del Mantenimiento:");
        lblEstado.setFont(new Font("Segoe UI", Font.BOLD, 12));
        panelFormulario.add(lblEstado, gbc);
        
        gbc.gridx = 1; gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        cmbEstadoCompletado.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        panelFormulario.add(cmbEstadoCompletado, gbc);
        
        // Observaciones
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.NORTHEAST;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        JLabel lblObservaciones = new JLabel("Observaciones:");
        lblObservaciones.setFont(new Font("Segoe UI", Font.BOLD, 12));
        panelFormulario.add(lblObservaciones, gbc);
        
        gbc.gridx = 1; gbc.gridy = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        JScrollPane scrollObservaciones = new JScrollPane(txtObservaciones);
        scrollObservaciones.setBorder(BorderFactory.createLoweredBevelBorder());
        panelFormulario.add(scrollObservaciones, gbc);
        
        panel.add(panelFormulario, BorderLayout.CENTER);
        
        // Panel de botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        panelBotones.setOpaque(false);
        
        panelBotones.add(btnActualizar);
        panelBotones.add(btnCompletarMantenimiento);
        panelBotones.add(btnCerrar);
        
        panel.add(panelBotones, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private void setupTableStyle() {
        // Configuración básica de la tabla
        tablaMantenimientos.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        tablaMantenimientos.setRowHeight(25);
        tablaMantenimientos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaMantenimientos.setBackground(COLOR_BLANCO);
        tablaMantenimientos.setGridColor(COLOR_GRIS_CLARO);
        
        // Header styling
        tablaMantenimientos.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 11));
        tablaMantenimientos.getTableHeader().setBackground(COLOR_VERDE_COOPERATIVA);
        tablaMantenimientos.getTableHeader().setForeground(COLOR_BLANCO);
        tablaMantenimientos.getTableHeader().setReorderingAllowed(false);
        
        // Column widths
        if (tablaMantenimientos.getColumnModel().getColumnCount() > 0) {
            tablaMantenimientos.getColumnModel().getColumn(0).setPreferredWidth(80);  // ID
            tablaMantenimientos.getColumnModel().getColumn(1).setPreferredWidth(150); // Equipo
            tablaMantenimientos.getColumnModel().getColumn(2).setPreferredWidth(120); // Ubicación
            tablaMantenimientos.getColumnModel().getColumn(3).setPreferredWidth(130); // Tipo
            tablaMantenimientos.getColumnModel().getColumn(4).setPreferredWidth(80);  // Prioridad
            tablaMantenimientos.getColumnModel().getColumn(5).setPreferredWidth(90);  // Estado
            tablaMantenimientos.getColumnModel().getColumn(6).setPreferredWidth(100); // F. Creación
            tablaMantenimientos.getColumnModel().getColumn(7).setPreferredWidth(100); // F. Programada
        }
        
        // Renderer personalizado para colores por prioridad
        tablaMantenimientos.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                
                Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (!isSelected) {
                    String prioridad = (String) table.getValueAt(row, 4);
                    if ("Alta".equals(prioridad)) {
                        component.setBackground(new Color(255, 240, 240));
                    } else if ("Media".equals(prioridad)) {
                        component.setBackground(new Color(255, 250, 240));
                    } else {
                        component.setBackground(COLOR_BLANCO);
                    }
                }
                
                return component;
            }
        });
    }
    
    private void setupEventHandlers() {
        // Selección en tabla
        tablaMantenimientos.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                boolean haySeleccion = tablaMantenimientos.getSelectedRow() != -1;
                btnCompletarMantenimiento.setEnabled(haySeleccion);
            }
        });
        
        // Botón completar
        btnCompletarMantenimiento.addActionListener(this::completarMantenimiento);
        
        // Botón actualizar
        btnActualizar.addActionListener(e -> cargarDatos());
        
        // Botón cerrar
        btnCerrar.addActionListener(e -> dispose());
        
        // Tecla ESC para cerrar
        getRootPane().registerKeyboardAction(
            e -> dispose(),
            KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
            JComponent.WHEN_IN_FOCUSED_WINDOW
        );
        
        // Enter para completar mantenimiento
        getRootPane().registerKeyboardAction(
            this::completarMantenimiento,
            KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, KeyEvent.CTRL_DOWN_MASK),
            JComponent.WHEN_IN_FOCUSED_WINDOW
        );
    }
    
    private void cargarDatos() {
        try {
            // Limpiar tabla
            modeloTabla.setRowCount(0);
            
            // Cargar todos los tickets y filtrar los asignados al técnico
            List<Ticket> todosTickets = ticketDAO.obtenerTodos();
            
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            
            for (Ticket ticket : todosTickets) {
                // Solo mostrar tickets asignados al técnico y que no están completados
                if ((ticket.getTickAsignadoA() == usuarioTecnico.getUsuId()) &&
                    (ticket.getTickEstado() == Ticket.Estado.Abierto || 
                     ticket.getTickEstado() == Ticket.Estado.En_Proceso)) {
                    
                    Object[] fila = {
                        ticket.getTickId(),
                        ticket.getActivoNumero() != null ? ticket.getActivoNumero() : "N/A",
                        "N/A", // Ubicación - simplificado
                        ticket.getTickTipo().toString().replace("_", " "),
                        ticket.getTickPrioridad().toString(),
                        ticket.getTickEstado().toString().replace("_", " "),
                        ticket.getTickFechaApertura().format(formatter),
                        ticket.getTickFechaVencimiento() != null ? 
                            ticket.getTickFechaVencimiento().format(formatter) : "No programada"
                    };
                    modeloTabla.addRow(fila);
                }
            }
            
            // Actualizar título con cantidad
            setTitle("🔧 Mis Mantenimientos (" + modeloTabla.getRowCount() + ") - " + usuarioTecnico.getUsrNombre());
            
        } catch (Exception e) {
            mostrarError("Error al cargar datos: " + e.getMessage());
        }
    }
    
    private void completarMantenimiento(ActionEvent e) {
        int filaSeleccionada = tablaMantenimientos.getSelectedRow();
        if (filaSeleccionada == -1) {
            mostrarAdvertencia("Por favor, selecciona un mantenimiento de la lista.");
            return;
        }
        
        try {
            // Obtener datos de la fila seleccionada
            int ticketId = (Integer) tablaMantenimientos.getValueAt(filaSeleccionada, 0);
            String equipoNombre = (String) tablaMantenimientos.getValueAt(filaSeleccionada, 1);
            String estadoSeleccionado = (String) cmbEstadoCompletado.getSelectedItem();
            String observaciones = txtObservaciones.getText().trim();
            
            // Validaciones básicas
            if (observaciones.isEmpty() && estadoSeleccionado.contains("Con observaciones")) {
                mostrarAdvertencia("Debes agregar observaciones para este tipo de estado.");
                return;
            }
            
            // Confirmar acción
            int confirmacion = JOptionPane.showConfirmDialog(this,
                "¿Confirmar completado del mantenimiento?\n\n" +
                "🔧 Equipo: " + equipoNombre + "\n" +
                "📋 Estado: " + estadoSeleccionado + "\n" +
                "📝 Observaciones: " + (observaciones.isEmpty() ? "(Sin observaciones)" : observaciones.substring(0, Math.min(50, observaciones.length())) + "..."),
                "Confirmar Mantenimiento",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
            
            if (confirmacion != JOptionPane.YES_OPTION) {
                return;
            }
            
            // Actualizar ticket y crear registro de mantenimiento
            Ticket ticket = ticketDAO.findById(ticketId);
            if (ticket != null) {
                // Determinar nuevo estado basado en la selección
                Ticket.Estado nuevoEstado;
                if (estadoSeleccionado.startsWith("Completado")) {
                    nuevoEstado = Ticket.Estado.Resuelto;
                } else if (estadoSeleccionado.startsWith("Reprogramar")) {
                    nuevoEstado = Ticket.Estado.Pendiente;
                } else {
                    nuevoEstado = Ticket.Estado.En_Progreso;
                }
                
                ticket.setTickEstado(nuevoEstado);
                ticket.setTickObservaciones(observaciones);
                ticket.setTickFechaCierre(LocalDateTime.now());
                
                // Guardar ticket actualizado
                ticketDAO.actualizar(ticket);
                
                // Crear registro de mantenimiento
                Mantenimiento mantenimiento = new Mantenimiento();
                mantenimiento.setTicket(ticket);
                mantenimiento.setMantFecha(LocalDateTime.now());
                mantenimiento.setMantTipo(ticket.getTickTipo().toString());
                mantenimiento.setMantDescripcion("Mantenimiento completado: " + estadoSeleccionado);
                mantenimiento.setMantObservaciones(observaciones);
                mantenimiento.setMantRealizado(estadoSeleccionado.startsWith("Completado"));
                mantenimiento.setUsuarioTecnico(usuarioTecnico);
                
                mantenimientoDAO.guardar(mantenimiento);
                
                mostrarExito("✅ Mantenimiento completado exitosamente!");
                
                // Limpiar formulario
                cmbEstadoCompletado.setSelectedIndex(0);
                txtObservaciones.setText("");
                
                // Actualizar lista
                cargarDatos();
            }
            
        } catch (Exception ex) {
            mostrarError("Error al completar mantenimiento: " + ex.getMessage());
        }
    }
    
    // Métodos de utilidad
    private JButton createStyledButton(String text, Color bgColor, Color fgColor, boolean enabled) {
        JButton button = new JButton(text);
        button.setBackground(bgColor);
        button.setForeground(fgColor);
        button.setFont(new Font("Segoe UI", Font.BOLD, 11));
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createRaisedBevelBorder(),
            BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));
        button.setEnabled(enabled);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setFocusPainted(false);
        
        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (button.isEnabled()) {
                    button.setBackground(bgColor.brighter());
                }
            }
            
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (button.isEnabled()) {
                    button.setBackground(bgColor);
                }
            }
        });
        
        return button;
    }
    
    private void mostrarExito(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "✅ Éxito", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void mostrarAdvertencia(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "⚠️ Advertencia", JOptionPane.WARNING_MESSAGE);
    }
    
    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "❌ Error", JOptionPane.ERROR_MESSAGE);
    }
    
    // Método main para pruebas
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeel());
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            // Usuario de prueba
            Usuario usuarioPrueba = new Usuario();
            usuarioPrueba.setUsrId(1);
            usuarioPrueba.setUsrNombre("Juan Técnico");
            usuarioPrueba.setUsrRol("Tecnico");
            
            MantenimientoTecnicoWindow window = new MantenimientoTecnicoWindow(null, usuarioPrueba);
            window.setVisible(true);
        });
    }
}