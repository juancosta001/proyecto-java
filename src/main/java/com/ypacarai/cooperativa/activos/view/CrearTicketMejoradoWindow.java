package com.ypacarai.cooperativa.activos.view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.time.LocalDateTime;
import java.util.List;

import com.ypacarai.cooperativa.activos.dao.ActivoDAO;
import com.ypacarai.cooperativa.activos.dao.UbicacionDAO;
import com.ypacarai.cooperativa.activos.dao.UsuarioDAO;
import com.ypacarai.cooperativa.activos.dao.TicketDAO;
import com.ypacarai.cooperativa.activos.model.Activo;
import com.ypacarai.cooperativa.activos.model.Ubicacion;
import com.ypacarai.cooperativa.activos.model.Usuario;
import com.ypacarai.cooperativa.activos.model.Ticket;

/**
 * Ventana Mejorada para Crear Tickets por Ubicación
 * Sistema de Gestión de Activos - Cooperativa Ypacaraí LTDA
 */
public class CrearTicketMejoradoWindow extends JFrame {
    
    // Colores corporativos
    private static final Color COLOR_VERDE_COOPERATIVA = new Color(34, 139, 34);
    private static final Color COLOR_VERDE_CLARO = new Color(144, 238, 144);
    private static final Color COLOR_BLANCO = Color.WHITE;
    private static final Color COLOR_GRIS_TEXTO = new Color(64, 64, 64);
    private static final Color COLOR_AZUL = new Color(70, 130, 180);
    private static final Color COLOR_ROJO = new Color(220, 20, 60);
    
    // Componentes de la interfaz
    private JComboBox<Ubicacion> cmbUbicacion;
    private JComboBox<Activo> cmbActivo;
    private JComboBox<Ticket.Tipo> cmbTipo;
    private JComboBox<Ticket.Prioridad> cmbPrioridad;
    private JComboBox<Usuario> cmbTecnicoAsignado;
    private JTextField txtTitulo;
    private JTextArea txtDescripcion;
    private JCheckBox chkSeleccionarTodos;
    private JList<Activo> listaActivos;
    private DefaultListModel<Activo> modeloListaActivos;
    private JLabel lblCantidadSeleccionados;
    
    private JButton btnGuardar;
    private JButton btnCancelar;
    private JLabel lblStatus;
    
    // DAOs
    private UbicacionDAO ubicacionDAO;
    private ActivoDAO activoDAO;
    private UsuarioDAO usuarioDAO;
    private TicketDAO ticketDAO;
    
    // Usuario actual
    private Usuario usuarioActual;
    private JFrame ventanaPadre;
    
    public CrearTicketMejoradoWindow(JFrame parent, Usuario usuarioActual) {
        this.ventanaPadre = parent;
        this.usuarioActual = usuarioActual;
        
        inicializarDAOs();
        initComponents();
        setupEventListeners();
        cargarDatosIniciales();
    }
    
    private void inicializarDAOs() {
        try {
            this.ubicacionDAO = new UbicacionDAO();
            this.activoDAO = new ActivoDAO();
            this.usuarioDAO = new UsuarioDAO();
            this.ticketDAO = new TicketDAO();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error al inicializar conexiones: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void initComponents() {
        setTitle("🎫 Crear Tickets por Ubicación - Sistema de Gestión de Activos");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(800, 700);
        setLocationRelativeTo(ventanaPadre);
        
        // Panel principal con gradiente
        JPanel panelPrincipal = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                GradientPaint gradient = new GradientPaint(
                    0, 0, COLOR_VERDE_CLARO.brighter(),
                    0, getHeight(), COLOR_BLANCO
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        panelPrincipal.setLayout(new BorderLayout());
        
        // Header
        JPanel headerPanel = createHeaderPanel();
        panelPrincipal.add(headerPanel, BorderLayout.NORTH);
        
        // Contenido principal
        JPanel mainPanel = createMainPanel();
        panelPrincipal.add(mainPanel, BorderLayout.CENTER);
        
        // Botones
        JPanel buttonPanel = createButtonPanel();
        panelPrincipal.add(buttonPanel, BorderLayout.SOUTH);
        
        add(panelPrincipal);
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(20, 20, 10, 20));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        
        JLabel lblTitulo = new JLabel("🎫 Crear Tickets por Ubicación");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitulo.setForeground(COLOR_VERDE_COOPERATIVA);
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel lblSubtitulo = new JLabel("Seleccione una ubicación para ver todos los equipos disponibles");
        lblSubtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblSubtitulo.setForeground(COLOR_GRIS_TEXTO);
        lblSubtitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        panel.add(lblTitulo);
        panel.add(Box.createVerticalStrut(5));
        panel.add(lblSubtitulo);
        
        return panel;
    }
    
    private JPanel createMainPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(10, 20, 10, 20));
        
        // Panel izquierdo - Configuración del ticket
        JPanel leftPanel = createConfigurationPanel();
        
        // Panel derecho - Selección de equipos
        JPanel rightPanel = createEquipmentSelectionPanel();
        
        panel.add(leftPanel, BorderLayout.WEST);
        panel.add(rightPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createConfigurationPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(COLOR_VERDE_COOPERATIVA, 2),
            "⚙️ Configuración del Ticket",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 14),
            COLOR_VERDE_COOPERATIVA));
        panel.setBackground(COLOR_BLANCO);
        panel.setPreferredSize(new Dimension(350, 0));
        
        // Ubicación
        panel.add(createFieldPanel("🏢 Ubicación:", cmbUbicacion = new JComboBox<>()));
        panel.add(Box.createVerticalStrut(10));
        
        // Tipo
        panel.add(createFieldPanel("📋 Tipo:", cmbTipo = new JComboBox<>(Ticket.Tipo.values())));
        panel.add(Box.createVerticalStrut(10));
        
        // Prioridad
        panel.add(createFieldPanel("⚡ Prioridad:", cmbPrioridad = new JComboBox<>(Ticket.Prioridad.values())));
        cmbPrioridad.setSelectedItem(Ticket.Prioridad.Media);
        panel.add(Box.createVerticalStrut(10));
        
        // Técnico asignado
        panel.add(createFieldPanel("👨‍💻 Técnico Asignado:", cmbTecnicoAsignado = new JComboBox<>()));
        panel.add(Box.createVerticalStrut(10));
        
        // Título
        panel.add(createFieldPanel("📝 Título:", txtTitulo = new JTextField()));
        panel.add(Box.createVerticalStrut(10));
        
        // Descripción
        JLabel lblDescripcion = new JLabel("📄 Descripción:");
        lblDescripcion.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblDescripcion.setForeground(COLOR_GRIS_TEXTO);
        panel.add(lblDescripcion);
        panel.add(Box.createVerticalStrut(5));
        
        txtDescripcion = new JTextArea(4, 20);
        txtDescripcion.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtDescripcion.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COLOR_VERDE_COOPERATIVA, 1),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        txtDescripcion.setLineWrap(true);
        txtDescripcion.setWrapStyleWord(true);
        
        JScrollPane scrollDescripcion = new JScrollPane(txtDescripcion);
        scrollDescripcion.setPreferredSize(new Dimension(320, 100));
        panel.add(scrollDescripcion);
        
        // Status
        lblStatus = new JLabel(" ");
        lblStatus.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        panel.add(Box.createVerticalStrut(10));
        panel.add(lblStatus);
        
        return panel;
    }
    
    private JPanel createEquipmentSelectionPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(COLOR_AZUL, 2),
            "💻 Equipos Disponibles",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 14),
            COLOR_AZUL));
        panel.setBackground(COLOR_BLANCO);
        
        // Panel superior con checkbox para seleccionar todos
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        
        chkSeleccionarTodos = new JCheckBox("✅ Seleccionar todos los equipos");
        chkSeleccionarTodos.setFont(new Font("Segoe UI", Font.BOLD, 12));
        chkSeleccionarTodos.setForeground(COLOR_AZUL);
        chkSeleccionarTodos.setOpaque(false);
        topPanel.add(chkSeleccionarTodos, BorderLayout.WEST);
        
        lblCantidadSeleccionados = new JLabel("0 equipos seleccionados");
        lblCantidadSeleccionados.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblCantidadSeleccionados.setForeground(COLOR_GRIS_TEXTO);
        topPanel.add(lblCantidadSeleccionados, BorderLayout.EAST);
        
        panel.add(topPanel, BorderLayout.NORTH);
        
        // Lista de equipos
        modeloListaActivos = new DefaultListModel<>();
        listaActivos = new JList<>(modeloListaActivos);
        listaActivos.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        listaActivos.setCellRenderer(new ActivoCellRenderer());
        
        JScrollPane scrollLista = new JScrollPane(listaActivos);
        scrollLista.setPreferredSize(new Dimension(0, 400));
        panel.add(scrollLista, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createFieldPanel(String labelText, JComponent field) {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setOpaque(false);
        
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        label.setForeground(COLOR_GRIS_TEXTO);
        
        if (field instanceof JTextField) {
            field.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            ((JTextField) field).setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_VERDE_COOPERATIVA, 1),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        } else if (field instanceof JComboBox) {
            field.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            ((JComboBox<?>) field).setBorder(BorderFactory.createLineBorder(COLOR_VERDE_COOPERATIVA, 1));
        }
        
        panel.add(label, BorderLayout.NORTH);
        panel.add(field, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 20));
        panel.setOpaque(false);
        
        btnGuardar = createStyledButton("💾 Crear Tickets", COLOR_VERDE_COOPERATIVA, COLOR_BLANCO, true);
        btnCancelar = createStyledButton("❌ Cancelar", COLOR_ROJO, COLOR_BLANCO, false);
        
        panel.add(btnGuardar);
        panel.add(btnCancelar);
        
        return panel;
    }
    
    private JButton createStyledButton(String text, Color bgColor, Color textColor, boolean isPrimary) {
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
        button.setFont(new Font("Segoe UI", isPrimary ? Font.BOLD : Font.PLAIN, 12));
        button.setPreferredSize(new Dimension(140, 40));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        return button;
    }
    
    private void setupEventListeners() {
        // Cambio de ubicación
        cmbUbicacion.addActionListener(e -> cargarEquiposPorUbicacion());
        
        // Seleccionar todos
        chkSeleccionarTodos.addActionListener(e -> seleccionarTodosEquipos());
        
        // Cambio en selección de equipos
        listaActivos.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                actualizarContadorSeleccionados();
            }
        });
        
        // Botones
        btnGuardar.addActionListener(e -> crearTickets());
        btnCancelar.addActionListener(e -> dispose());
        
        // ESC para cerrar
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "cerrar");
        getRootPane().getActionMap().put("cerrar", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
    }
    
    private void cargarDatosIniciales() {
        try {
            // Cargar ubicaciones
            List<Ubicacion> ubicaciones = ubicacionDAO.obtenerTodas();
            cmbUbicacion.removeAllItems();
            cmbUbicacion.addItem(null); // Opción vacía
            for (Ubicacion ubicacion : ubicaciones) {
                cmbUbicacion.addItem(ubicacion);
            }
            
            // Cargar técnicos
            List<Usuario> tecnicos = usuarioDAO.obtenerTecnicos();
            cmbTecnicoAsignado.removeAllItems();
            cmbTecnicoAsignado.addItem(null); // Sin asignar
            for (Usuario tecnico : tecnicos) {
                cmbTecnicoAsignado.addItem(tecnico);
            }
            
            // Configurar renderers
            configurarRenderers();
            
        } catch (Exception e) {
            mostrarError("Error al cargar datos: " + e.getMessage());
        }
    }
    
    private void configurarRenderers() {
        // Renderer para ubicaciones
        cmbUbicacion.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Ubicacion) {
                    Ubicacion ubicacion = (Ubicacion) value;
                    setText(ubicacion.getUbiNombre() + " (" + ubicacion.getUbiTipo() + ")");
                } else if (value == null) {
                    setText("-- Seleccionar ubicación --");
                }
                return this;
            }
        });
        
        // Renderer para técnicos
        cmbTecnicoAsignado.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Usuario) {
                    Usuario usuario = (Usuario) value;
                    setText(usuario.getUsuNombre() + " (" + usuario.getUsuRol() + ")");
                } else if (value == null) {
                    setText("-- Sin asignar --");
                }
                return this;
            }
        });
    }
    
    private void cargarEquiposPorUbicacion() {
        Ubicacion ubicacionSeleccionada = (Ubicacion) cmbUbicacion.getSelectedItem();
        
        modeloListaActivos.clear();
        chkSeleccionarTodos.setSelected(false);
        actualizarContadorSeleccionados();
        
        if (ubicacionSeleccionada != null) {
            try {
                List<Activo> equipos = activoDAO.findByUbicacion(ubicacionSeleccionada.getUbiId());
                for (Activo equipo : equipos) {
                    if (equipo.getActEstado() == Activo.Estado.Operativo) {
                        modeloListaActivos.addElement(equipo);
                    }
                }
                
                mostrarInfo("Cargados " + modeloListaActivos.getSize() + " equipos operativos");
                
            } catch (Exception e) {
                mostrarError("Error al cargar equipos: " + e.getMessage());
            }
        }
    }
    
    private void seleccionarTodosEquipos() {
        if (chkSeleccionarTodos.isSelected()) {
            listaActivos.setSelectionInterval(0, modeloListaActivos.getSize() - 1);
        } else {
            listaActivos.clearSelection();
        }
        actualizarContadorSeleccionados();
    }
    
    private void actualizarContadorSeleccionados() {
        int seleccionados = listaActivos.getSelectedIndices().length;
        lblCantidadSeleccionados.setText(seleccionados + " equipos seleccionados");
        
        // Actualizar el botón
        btnGuardar.setEnabled(seleccionados > 0 && validarFormulario());
    }
    
    private boolean validarFormulario() {
        if (cmbUbicacion.getSelectedItem() == null) return false;
        if (txtTitulo.getText().trim().isEmpty()) return false;
        if (txtDescripcion.getText().trim().isEmpty()) return false;
        return true;
    }
    
    private void crearTickets() {
        if (!validarFormulario()) {
            mostrarError("Complete todos los campos obligatorios");
            return;
        }
        
        List<Activo> equiposSeleccionados = listaActivos.getSelectedValuesList();
        if (equiposSeleccionados.isEmpty()) {
            mostrarError("Seleccione al menos un equipo");
            return;
        }
        
        // Confirmar creación
        int respuesta = JOptionPane.showConfirmDialog(this,
            "¿Está seguro de crear " + equiposSeleccionados.size() + " tickets?\n\n" +
            "Ubicación: " + ((Ubicacion) cmbUbicacion.getSelectedItem()).getUbiNombre() + "\n" +
            "Tipo: " + cmbTipo.getSelectedItem() + "\n" +
            "Prioridad: " + cmbPrioridad.getSelectedItem() + "\n" +
            "Técnico: " + (cmbTecnicoAsignado.getSelectedItem() != null ? 
                        ((Usuario) cmbTecnicoAsignado.getSelectedItem()).getUsuNombre() : "Sin asignar"),
            "Confirmar Creación de Tickets",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);
        
        if (respuesta != JOptionPane.YES_OPTION) {
            return;
        }
        
        // Crear tickets
        try {
            btnGuardar.setEnabled(false);
            mostrarInfo("Creando tickets...");
            
            int ticketsCreados = 0;
            for (Activo equipo : equiposSeleccionados) {
                Ticket ticket = new Ticket();
                ticket.setActId(equipo.getActId());
                ticket.setTickTipo((Ticket.Tipo) cmbTipo.getSelectedItem());
                ticket.setTickPrioridad((Ticket.Prioridad) cmbPrioridad.getSelectedItem());
                ticket.setTickTitulo(txtTitulo.getText().trim());
                ticket.setTickDescripcion(txtDescripcion.getText().trim() + 
                    "\n\nEquipo: " + equipo.getActNumeroActivo() + " - " + equipo.getActMarca() + " " + equipo.getActModelo());
                ticket.setTickEstado(Ticket.Estado.Abierto);
                ticket.setTickFechaApertura(LocalDateTime.now());
                ticket.setTickReportadoPor(usuarioActual.getUsuId());
                
                if (cmbTecnicoAsignado.getSelectedItem() != null) {
                    Usuario tecnico = (Usuario) cmbTecnicoAsignado.getSelectedItem();
                    ticket.setTickAsignadoA(tecnico.getUsuId());
                }
                
                // Generar número único
                String numeroTicket = "TK-" + System.currentTimeMillis() + "-" + equipo.getActId();
                ticket.setTickNumero(numeroTicket);
                
                ticketDAO.guardar(ticket);
                ticketsCreados++;
            }
            
            mostrarExito("✅ Se crearon " + ticketsCreados + " tickets exitosamente");
            
            // Mostrar diálogo de confirmación
            JOptionPane.showMessageDialog(this,
                "Tickets creados exitosamente!\n\n" +
                "📊 Total: " + ticketsCreados + " tickets\n" +
                "🏢 Ubicación: " + ((Ubicacion) cmbUbicacion.getSelectedItem()).getUbiNombre() + "\n" +
                "📋 Tipo: " + cmbTipo.getSelectedItem() + "\n" +
                "⚡ Prioridad: " + cmbPrioridad.getSelectedItem(),
                "Tickets Creados",
                JOptionPane.INFORMATION_MESSAGE);
            
            dispose();
            
        } catch (Exception e) {
            mostrarError("Error al crear tickets: " + e.getMessage());
        } finally {
            btnGuardar.setEnabled(true);
        }
    }
    
    private void mostrarError(String mensaje) {
        lblStatus.setText("❌ " + mensaje);
        lblStatus.setForeground(COLOR_ROJO);
    }
    
    private void mostrarExito(String mensaje) {
        lblStatus.setText(mensaje);
        lblStatus.setForeground(COLOR_VERDE_COOPERATIVA);
    }
    
    private void mostrarInfo(String mensaje) {
        lblStatus.setText("ℹ️ " + mensaje);
        lblStatus.setForeground(COLOR_AZUL);
    }
    // Renderer personalizado para la lista de activos
    private class ActivoCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value,
                int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            
            if (value instanceof Activo) {
                Activo activo = (Activo) value;
                setText(String.format("🖥️ %s - %s %s (%s)", 
                    activo.getActNumeroActivo(),
                    activo.getActMarca(),
                    activo.getActModelo(),
                    activo.getActEstado()));
            }
            
            return this;
        }
    }
    
    // Método principal para pruebas
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Usuario usuarioPrueba = new Usuario();
            usuarioPrueba.setUsuId(1);
            usuarioPrueba.setUsuNombre("Admin");
            usuarioPrueba.setUsuRol(Usuario.Rol.Jefe_Informatica);
            
            CrearTicketMejoradoWindow window = new CrearTicketMejoradoWindow(null, usuarioPrueba);
            window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            window.setVisible(true);
        });
    }
}