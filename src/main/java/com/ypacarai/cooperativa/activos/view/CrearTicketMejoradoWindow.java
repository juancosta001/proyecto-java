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
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                GradientPaint gradient = new GradientPaint(
                    0, 0, COLOR_BLANCO,
                    0, getHeight(), new Color(240, 248, 255)
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        panelPrincipal.setLayout(new BorderLayout());
        panelPrincipal.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Panel del título
        JPanel panelTitulo = createTitlePanel();
        panelPrincipal.add(panelTitulo, BorderLayout.NORTH);
        
        // Panel del formulario
        JPanel panelFormulario = createFormPanel();
        panelPrincipal.add(panelFormulario, BorderLayout.CENTER);
        
        // Panel de botones
        JPanel panelBotones = createButtonPanel();
        panelPrincipal.add(panelBotones, BorderLayout.SOUTH);
        
        add(panelPrincipal);
    }
    
    private JPanel createTitlePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        
        JLabel lblTitulo = new JLabel("🎫 Crear Tickets por Ubicación");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitulo.setForeground(COLOR_VERDE_COOPERATIVA);
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        
        JLabel lblSubtitulo = new JLabel("Selecciona la ubicación y los equipos para crear tickets de mantenimiento");
        lblSubtitulo.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblSubtitulo.setForeground(COLOR_GRIS_TEXTO);
        lblSubtitulo.setHorizontalAlignment(SwingConstants.CENTER);
        
        panel.add(lblTitulo, BorderLayout.NORTH);
        panel.add(lblSubtitulo, BorderLayout.SOUTH);
        
        panel.setBorder(new EmptyBorder(0, 0, 20, 0));
        
        return panel;
    }
    
    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setOpaque(false);
        
        // Panel superior - Datos del ticket
        JPanel panelDatos = createDataPanel();
        panel.add(panelDatos, BorderLayout.NORTH);
        
        // Panel central - Selección de equipos
        JPanel panelEquipos = createEquipmentPanel();
        panel.add(panelEquipos, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createDataPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(COLOR_VERDE_COOPERATIVA, 2),
                " Datos del Ticket ",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 12),
                COLOR_VERDE_COOPERATIVA
            ),
            new EmptyBorder(15, 15, 15, 15)
        ));
        panel.setOpaque(false);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Ubicación
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("🏢 Ubicación:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        panel.add(cmbUbicacion = new JComboBox<>(), gbc);
        
        // Tipo
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        panel.add(new JLabel("🔧 Tipo:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        panel.add(cmbTipo = new JComboBox<>(Ticket.Tipo.values()), gbc);
        
        // Prioridad
        gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        panel.add(new JLabel("⚡ Prioridad:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        panel.add(cmbPrioridad = new JComboBox<>(Ticket.Prioridad.values()), gbc);
        cmbPrioridad.setSelectedItem(Ticket.Prioridad.Media);
        
        // Técnico
        gbc.gridx = 0; gbc.gridy = 3; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        panel.add(new JLabel("👨‍💻 Técnico:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        panel.add(cmbTecnicoAsignado = new JComboBox<>(), gbc);
        
        // Título
        gbc.gridx = 0; gbc.gridy = 4; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        panel.add(new JLabel("📝 Título:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        panel.add(txtTitulo = new JTextField(), gbc);
        
        // Descripción
        gbc.gridx = 0; gbc.gridy = 5; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0; gbc.anchor = GridBagConstraints.NORTHWEST;
        panel.add(new JLabel("📋 Descripción:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.BOTH; gbc.weightx = 1.0; gbc.weighty = 1.0;
        txtDescripcion = new JTextArea(3, 30);
        txtDescripcion.setLineWrap(true);
        txtDescripcion.setWrapStyleWord(true);
        txtDescripcion.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        panel.add(new JScrollPane(txtDescripcion), gbc);
        
        return panel;
    }
    
    private JPanel createEquipmentPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(COLOR_VERDE_COOPERATIVA, 2),
                " Selección de Equipos ",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 12),
                COLOR_VERDE_COOPERATIVA
            ),
            new EmptyBorder(15, 15, 15, 15)
        ));
        panel.setOpaque(false);
        
        // Panel superior con checkbox "Seleccionar todos"
        JPanel panelSuperior = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelSuperior.setOpaque(false);
        chkSeleccionarTodos = new JCheckBox("Seleccionar todos los equipos");
        chkSeleccionarTodos.setFont(new Font("Segoe UI", Font.BOLD, 12));
        chkSeleccionarTodos.setOpaque(false);
        panelSuperior.add(chkSeleccionarTodos);
        
        lblCantidadSeleccionados = new JLabel("0 equipos seleccionados");
        lblCantidadSeleccionados.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblCantidadSeleccionados.setForeground(COLOR_AZUL);
        panelSuperior.add(lblCantidadSeleccionados);
        
        panel.add(panelSuperior, BorderLayout.NORTH);
        
        // Lista de activos
        modeloListaActivos = new DefaultListModel<>();
        listaActivos = new JList<>(modeloListaActivos);
        listaActivos.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        listaActivos.setCellRenderer(new ActivoCellRenderer());
        
        JScrollPane scrollLista = new JScrollPane(listaActivos);
        scrollLista.setPreferredSize(new Dimension(400, 200));
        panel.add(scrollLista, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(20, 0, 0, 0));
        
        // Status label
        lblStatus = new JLabel(" ");
        lblStatus.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        panel.add(lblStatus, BorderLayout.WEST);
        
        // Botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        panelBotones.setOpaque(false);
        
        btnCancelar = createStyledButton("❌ Cancelar", COLOR_ROJO, COLOR_BLANCO, true);
        btnGuardar = createStyledButton("💾 Crear Tickets", COLOR_VERDE_COOPERATIVA, COLOR_BLANCO, false);
        
        panelBotones.add(btnCancelar);
        panelBotones.add(btnGuardar);
        
        panel.add(panelBotones, BorderLayout.EAST);
        
        return panel;
    }
    
    private JButton createStyledButton(String text, Color bgColor, Color fgColor, boolean enabled) {
        JButton button = new JButton(text);
        button.setBackground(bgColor);
        button.setForeground(fgColor);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createRaisedBevelBorder(),
            BorderFactory.createEmptyBorder(10, 20, 10, 20)
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
    
    private void setupEventListeners() {
        // Evento para cambio de ubicación
        cmbUbicacion.addActionListener(e -> cargarEquiposPorUbicacion());
        
        // Evento para seleccionar todos
        chkSeleccionarTodos.addActionListener(e -> {
            if (chkSeleccionarTodos.isSelected()) {
                listaActivos.setSelectionInterval(0, modeloListaActivos.getSize() - 1);
            } else {
                listaActivos.clearSelection();
            }
        });
        
        // Evento para cambio de selección en la lista
        listaActivos.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                actualizarEstadoBoton();
            }
        });
        
        // Eventos de botones
        btnGuardar.addActionListener(e -> {
            System.out.println("BOTÓN GUARDAR CLICKEADO - INICIANDO DEBUG");
            crearTickets();
        });
        
        btnCancelar.addActionListener(e -> dispose());
        
        // Tecla ESC para cancelar
        getRootPane().registerKeyboardAction(
            e -> dispose(),
            KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
            JComponent.WHEN_IN_FOCUSED_WINDOW
        );
        
        // Eventos de campos de texto para validación
        txtTitulo.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { actualizarEstadoBoton(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { actualizarEstadoBoton(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { actualizarEstadoBoton(); }
        });
        
        txtDescripcion.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { actualizarEstadoBoton(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { actualizarEstadoBoton(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { actualizarEstadoBoton(); }
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
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value == null) {
                    setText("Seleccione una ubicación...");
                    setForeground(Color.GRAY);
                } else {
                    Ubicacion ubicacion = (Ubicacion) value;
                    setText(ubicacion.getUbiNombre());
                }
                return this;
            }
        });
        
        // Renderer para técnicos
        cmbTecnicoAsignado.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value == null) {
                    setText("Sin asignar");
                    setForeground(Color.GRAY);
                } else {
                    Usuario usuario = (Usuario) value;
                    setText(usuario.getUsuNombre());
                }
                return this;
            }
        });
    }
    
    private void cargarEquiposPorUbicacion() {
        Ubicacion ubicacionSeleccionada = (Ubicacion) cmbUbicacion.getSelectedItem();
        
        modeloListaActivos.clear();
        chkSeleccionarTodos.setSelected(false);
        
        if (ubicacionSeleccionada == null) {
            actualizarEstadoBoton();
            return;
        }
        
        try {
            List<Activo> equipos = activoDAO.findByUbicacion(ubicacionSeleccionada.getUbiId());
            
            for (Activo equipo : equipos) {
                // Solo mostrar equipos operativos
                if (equipo.getActEstado() == Activo.Estado.Operativo) {
                    modeloListaActivos.addElement(equipo);
                }
            }
            
            if (modeloListaActivos.isEmpty()) {
                mostrarInfo("No hay equipos operativos en esta ubicación");
            } else {
                mostrarInfo("Se encontraron " + modeloListaActivos.getSize() + " equipos operativos");
            }
            
        } catch (Exception e) {
            mostrarError("Error al cargar equipos: " + e.getMessage());
        }
        
        actualizarEstadoBoton();
    }
    
    private void actualizarEstadoBoton() {
        int seleccionados = listaActivos.getSelectedValuesList().size();
        
        lblCantidadSeleccionados.setText(seleccionados + " equipos seleccionados");
        
        // Actualizar el botón con debug
        boolean formularioValido = validarFormulario();
        boolean botonHabilitado = seleccionados > 0 && formularioValido;
        
        System.out.println("=== DEBUG BOTÓN ===");
        System.out.println("Equipos seleccionados: " + seleccionados);
        System.out.println("Formulario válido: " + formularioValido);
        System.out.println("Botón habilitado: " + botonHabilitado);
        
        btnGuardar.setEnabled(botonHabilitado);
    }
    
    private boolean validarFormulario() {
        boolean ubicacionValida = cmbUbicacion.getSelectedItem() != null;
        boolean tituloValido = !txtTitulo.getText().trim().isEmpty();
        boolean descripcionValida = !txtDescripcion.getText().trim().isEmpty();
        
        System.out.println("Validación formulario:");
        System.out.println("  Ubicación válida: " + ubicacionValida + " (" + cmbUbicacion.getSelectedItem() + ")");
        System.out.println("  Título válido: " + tituloValido + " (" + txtTitulo.getText().trim() + ")");
        System.out.println("  Descripción válida: " + descripcionValida + " (" + txtDescripcion.getText().trim() + ")");
        
        return ubicacionValida && tituloValido && descripcionValida;
    }
    
    private void crearTickets() {
        System.out.println("=== INICIANDO CREACIÓN DE TICKETS ===");
        
        if (!validarFormulario()) {
            System.out.println("ERROR: Formulario no válido");
            mostrarError("Complete todos los campos obligatorios");
            return;
        }
        
        List<Activo> equiposSeleccionados = listaActivos.getSelectedValuesList();
        if (equiposSeleccionados.isEmpty()) {
            System.out.println("ERROR: No hay equipos seleccionados");
            mostrarError("Debe seleccionar al menos un equipo");
            return;
        }
        
        System.out.println("Equipos seleccionados: " + equiposSeleccionados.size());
        
        // Confirmación
        int respuesta = JOptionPane.showConfirmDialog(this,
            "¿Está seguro de crear " + equiposSeleccionados.size() + " tickets?\n\n" +
            "🏢 Ubicación: " + ((Ubicacion) cmbUbicacion.getSelectedItem()).getUbiNombre() + "\n" +
            "📋 Tipo: " + cmbTipo.getSelectedItem() + "\n" +
            "⚡ Prioridad: " + cmbPrioridad.getSelectedItem() + "\n" +
            "👨‍💻 Técnico: " + (cmbTecnicoAsignado.getSelectedItem() != null ? 
                        ((Usuario) cmbTecnicoAsignado.getSelectedItem()).getUsuNombre() : "Sin asignar"),
            "Confirmar Creación de Tickets",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);
        
        if (respuesta != JOptionPane.YES_OPTION) {
            System.out.println("Usuario canceló la operación");
            return;
        }
        
        // Crear tickets
        try {
            btnGuardar.setEnabled(false);
            mostrarInfo("Creando tickets...");
            
            int ticketsCreados = 0;
            for (Activo equipo : equiposSeleccionados) {
                try {
                    System.out.println("Creando ticket para equipo: " + equipo.getActId() + " - " + equipo.getActNumeroActivo());
                    
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
                    
                    Ticket ticketGuardado = ticketDAO.guardar(ticket);
                    System.out.println("Ticket creado con ID: " + ticketGuardado.getTickId());
                    
                    ticketsCreados++;
                } catch (Exception e) {
                    System.err.println("Error al crear ticket para activo " + equipo.getActId() + ": " + e.getMessage());
                    e.printStackTrace();
                }
            }
            
            System.out.println("Tickets creados exitosamente: " + ticketsCreados);
            
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
            System.err.println("Error general al crear tickets: " + e.getMessage());
            e.printStackTrace();
            mostrarError("Error al crear tickets: " + e.getMessage());
        } finally {
            btnGuardar.setEnabled(true);
        }
    }
    
    // Métodos de utilidad para mensajes
    private void mostrarInfo(String mensaje) {
        lblStatus.setText("ℹ️ " + mensaje);
        lblStatus.setForeground(COLOR_AZUL);
    }
    
    private void mostrarExito(String mensaje) {
        lblStatus.setText("✅ " + mensaje);
        lblStatus.setForeground(COLOR_VERDE_COOPERATIVA);
    }
    
    private void mostrarError(String mensaje) {
        lblStatus.setText("❌ " + mensaje);
        lblStatus.setForeground(COLOR_ROJO);
    }
    
    // Renderer personalizado para la lista de activos
    private class ActivoCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                boolean isSelected, boolean cellHasFocus) {
            
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            
            if (value instanceof Activo) {
                Activo activo = (Activo) value;
                setText("<html><b>" + activo.getActNumeroActivo() + "</b><br>" +
                       "<small>" + activo.getActMarca() + " " + activo.getActModelo() + "</small></html>");
                
                if (isSelected) {
                    setBackground(COLOR_VERDE_CLARO);
                    setForeground(COLOR_GRIS_TEXTO);
                }
            }
            
            return this;
        }
    }
    
    // Método main para pruebas
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
            } catch (Exception e) {
                // Usar Look and Feel por defecto
            }
            
            // Usuario de prueba
            Usuario usuarioPrueba = new Usuario();
            usuarioPrueba.setUsuId(1);
            usuarioPrueba.setUsuNombre("Admin Prueba");
            
            CrearTicketMejoradoWindow window = new CrearTicketMejoradoWindow(null, usuarioPrueba);
            window.setVisible(true);
        });
    }
}