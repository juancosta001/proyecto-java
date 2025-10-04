package com.ypacarai.cooperativa.activos.view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.ypacarai.cooperativa.activos.dao.TicketDAO;
import com.ypacarai.cooperativa.activos.model.Ticket;
import com.ypacarai.cooperativa.activos.model.Usuario;

/**
 * Panel integrado para técnicos - Gestión de Mantenimientos
 * Permite a los técnicos ver y completar mantenimientos directamente en la ventana principal
 */
public class MantenimientoTecnicoPanel extends JPanel {
    
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
    
    // DAOs
    private TicketDAO ticketDAO;
    
    public MantenimientoTecnicoPanel(Usuario usuarioTecnico) {
        super(new BorderLayout());
        this.usuarioTecnico = usuarioTecnico;
        this.ticketDAO = new TicketDAO();
        
        initializeComponents();
        setupUI();
        cargarDatos();
        setupEventHandlers();
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
        btnCompletarMantenimiento = createStyledButton("✅ Completar Mantenimiento", COLOR_VERDE_COOPERATIVA, COLOR_BLANCO, false);
        btnActualizar = createStyledButton("🔄 Actualizar Lista", COLOR_AZUL_INFO, COLOR_BLANCO, true);
    }
    
    private void setupUI() {
        setBackground(COLOR_BLANCO);
        setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Panel superior con título
        add(createTitlePanel(), BorderLayout.NORTH);
        
        // Panel central con tabla
        add(createCentralPanel(), BorderLayout.CENTER);
        
        // Panel inferior con formulario
        add(createFormPanel(), BorderLayout.SOUTH);
    }
    
    private JPanel createTitlePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(5, 5, 15, 5));
        
        JLabel lblTitulo = new JLabel("🔧 Mis Mantenimientos Pendientes");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
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
        
        // Panel de información y botón actualizar
        JPanel panelInfo = new JPanel(new BorderLayout());
        panelInfo.setOpaque(false);
        
        JLabel lblInfo = new JLabel("💡 Selecciona un mantenimiento de la tabla para completarlo");
        lblInfo.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblInfo.setForeground(COLOR_AZUL_INFO);
        
        JPanel panelBtnActualizar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelBtnActualizar.setOpaque(false);
        panelBtnActualizar.add(btnActualizar);
        
        panelInfo.add(lblInfo, BorderLayout.WEST);
        panelInfo.add(panelBtnActualizar, BorderLayout.EAST);
        
        // Tabla con scroll
        JScrollPane scrollPane = new JScrollPane(tablaMantenimientos);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(COLOR_VERDE_COOPERATIVA, 2),
                " Mantenimientos Asignados ",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 12),
                COLOR_VERDE_COOPERATIVA
            ),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        scrollPane.getViewport().setBackground(COLOR_BLANCO);
        scrollPane.setPreferredSize(new Dimension(0, 250));
        
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
        gbc.weightx = 0.7;
        cmbEstadoCompletado.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        panelFormulario.add(cmbEstadoCompletado, gbc);
        
        // Botón completar
        gbc.gridx = 2; gbc.gridy = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        gbc.insets = new Insets(5, 15, 5, 5);
        panelFormulario.add(btnCompletarMantenimiento, gbc);
        
        // Observaciones
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.NORTHEAST;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        gbc.insets = new Insets(5, 5, 5, 5);
        JLabel lblObservaciones = new JLabel("Observaciones:");
        lblObservaciones.setFont(new Font("Segoe UI", Font.BOLD, 12));
        panelFormulario.add(lblObservaciones, gbc);
        
        gbc.gridx = 1; gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        JScrollPane scrollObservaciones = new JScrollPane(txtObservaciones);
        scrollObservaciones.setBorder(BorderFactory.createLoweredBevelBorder());
        scrollObservaciones.setPreferredSize(new Dimension(0, 80));
        panelFormulario.add(scrollObservaciones, gbc);
        
        panel.add(panelFormulario, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void setupTableStyle() {
        tablaMantenimientos.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        tablaMantenimientos.setRowHeight(25);
        tablaMantenimientos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaMantenimientos.setBackground(COLOR_BLANCO);
        
        // Mejorar visibilidad de la selección con color más contrastante
        tablaMantenimientos.setSelectionBackground(new Color(144, 238, 144)); // Verde claro
        tablaMantenimientos.setSelectionForeground(new Color(0, 100, 0)); // Verde oscuro para el texto
        tablaMantenimientos.setGridColor(Color.LIGHT_GRAY);
        
        // Centrar contenido de las celdas
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        
        for (int i = 0; i < tablaMantenimientos.getColumnCount(); i++) {
            tablaMantenimientos.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
        
        // Ajustar ancho de columnas
        int[] anchos = {80, 150, 120, 150, 80, 100, 120, 120};
        for (int i = 0; i < anchos.length && i < tablaMantenimientos.getColumnCount(); i++) {
            tablaMantenimientos.getColumnModel().getColumn(i).setPreferredWidth(anchos[i]);
        }
        
        tablaMantenimientos.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 11));
        tablaMantenimientos.getTableHeader().setBackground(COLOR_VERDE_COOPERATIVA);
        tablaMantenimientos.getTableHeader().setForeground(COLOR_BLANCO);
    }
    
    private void setupEventHandlers() {
        // Listener para selección de tabla
        tablaMantenimientos.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                boolean haySeleccion = tablaMantenimientos.getSelectedRow() != -1;
                btnCompletarMantenimiento.setEnabled(haySeleccion);
                
                // Cambiar color del botón según el estado
                if (haySeleccion) {
                    btnCompletarMantenimiento.setBackground(COLOR_VERDE_COOPERATIVA);
                    btnCompletarMantenimiento.setForeground(COLOR_BLANCO);
                } else {
                    btnCompletarMantenimiento.setBackground(Color.GRAY);
                    btnCompletarMantenimiento.setForeground(Color.LIGHT_GRAY);
                }
            }
        });
        
        // Listeners para botones
        btnCompletarMantenimiento.addActionListener(this::completarMantenimiento);
        btnActualizar.addActionListener(e -> cargarDatos());
        
        // Manejar Enter para completar mantenimiento
        this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
            KeyStroke.getKeyStroke("ENTER"), "completarMantenimiento");
        this.getActionMap().put("completarMantenimiento", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (btnCompletarMantenimiento.isEnabled()) {
                    completarMantenimiento(e);
                }
            }
        });
    }
    
    private void cargarDatos() {
        try {
            // Limpiar tabla
            modeloTabla.setRowCount(0);
            
            // Obtener mantenimientos del técnico usando TicketDAO
            List<Ticket> mantenimientos = ticketDAO.obtenerPorTecnico(usuarioTecnico.getUsuId());
            
            // Agregar datos a la tabla
            for (Ticket ticket : mantenimientos) {
                if (ticket.getTickEstado() == Ticket.Estado.Abierto || 
                    ticket.getTickEstado() == Ticket.Estado.En_Proceso) {
                    
                    Object[] fila = {
                        ticket.getTickId(),
                        ticket.getActivoNumero() != null ? ticket.getActivoNumero() : "N/A",
                        ticket.getUbicacionNombre() != null ? ticket.getUbicacionNombre() : "N/A",
                        ticket.getTickTipo(),
                        ticket.getTickPrioridad(),
                        ticket.getTickEstado(),
                        ticket.getTickFechaApertura() != null ? 
                            ticket.getTickFechaApertura().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "N/A",
                        ticket.getTickFechaVencimiento() != null ? 
                            ticket.getTickFechaVencimiento().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "N/A"
                    };
                    modeloTabla.addRow(fila);
                }
            }
            
            // Mostrar mensaje si no hay mantenimientos
            if (modeloTabla.getRowCount() == 0) {
                Object[] fila = {"", "No hay mantenimientos pendientes", "", "", "", "", "", ""};
                modeloTabla.addRow(fila);
                btnCompletarMantenimiento.setEnabled(false);
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error al cargar mantenimientos: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void completarMantenimiento(ActionEvent e) {
        int filaSeleccionada = tablaMantenimientos.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this,
                "Por favor, selecciona un mantenimiento de la tabla.",
                "Selección requerida",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Validar que hay contenido en la fila
        Object idObj = modeloTabla.getValueAt(filaSeleccionada, 0);
        if (idObj == null || idObj.toString().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "No hay mantenimientos válidos para completar.",
                "Sin datos",
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        try {
            int ticketId = Integer.parseInt(idObj.toString());
            String estadoSeleccionado = (String) cmbEstadoCompletado.getSelectedItem();
            String observaciones = txtObservaciones.getText().trim();
            
            // Confirmar acción
            int confirmacion = JOptionPane.showConfirmDialog(this,
                "¿Confirma que desea actualizar el estado del mantenimiento?\n\n" +
                "Ticket ID: " + ticketId + "\n" +
                "Estado: " + estadoSeleccionado + "\n" +
                "Observaciones: " + (observaciones.isEmpty() ? "Ninguna" : observaciones),
                "Confirmar Completado",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
                
            if (confirmacion == JOptionPane.YES_OPTION) {
                // Obtener todos los tickets y buscar el específico
                List<Ticket> tickets = ticketDAO.obtenerTodos();
                Ticket ticket = null;
                for (Ticket t : tickets) {
                    if (t.getTickId() == ticketId) {
                        ticket = t;
                        break;
                    }
                }
                
                if (ticket != null) {
                    // Actualizar estado según la selección
                    Ticket.Estado nuevoEstado;
                    if (estadoSeleccionado.startsWith("Completado")) {
                        nuevoEstado = Ticket.Estado.Resuelto;
                    } else if (estadoSeleccionado.startsWith("No completado")) {
                        nuevoEstado = Ticket.Estado.Abierto;
                    } else { // Reprogramar
                        nuevoEstado = Ticket.Estado.Abierto;
                    }
                    
                    // Agregar observaciones técnicas
                    String observacionesCompletas = "TÉCNICO: " + usuarioTecnico.getUsuNombre() + 
                        " (" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) + ")\n" +
                        "ESTADO: " + estadoSeleccionado + "\n";
                    
                    if (!observaciones.isEmpty()) {
                        observacionesCompletas += "OBSERVACIONES: " + observaciones + "\n";
                    }
                    
                    // Agregar a observaciones existentes (usar descripción como campo de observaciones)
                    String observacionesExistentes = ticket.getTickDescripcion();
                    if (observacionesExistentes != null && !observacionesExistentes.trim().isEmpty()) {
                        observacionesCompletas = observacionesExistentes + "\n\n--- ACTUALIZACIÓN ---\n" + observacionesCompletas;
                    }
                    
                    // Actualizar ticket
                    ticket.setTickEstado(nuevoEstado);
                    ticket.setTickDescripcion(observacionesCompletas);
                    if (nuevoEstado == Ticket.Estado.Resuelto) {
                        ticket.setTickFechaCierre(LocalDateTime.now());
                    }
                    ticket.setActualizadoEn(LocalDateTime.now());
                    
                    ticketDAO.actualizar(ticket);
                    
                    JOptionPane.showMessageDialog(this,
                        "✅ Mantenimiento actualizado correctamente.",
                        "Éxito",
                        JOptionPane.INFORMATION_MESSAGE);
                        
                    // Limpiar formulario
                    cmbEstadoCompletado.setSelectedIndex(0);
                    txtObservaciones.setText("");
                    
                    // Recargar datos
                    cargarDatos();
                } else {
                    JOptionPane.showMessageDialog(this,
                        "No se pudo encontrar el ticket seleccionado.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                "Error en el ID del ticket seleccionado.",
                "Error",
                JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Error al procesar el mantenimiento: " + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private JButton createStyledButton(String text, Color bgColor, Color fgColor, boolean enabled) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 11));
        button.setBorder(new EmptyBorder(8, 16, 8, 16));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setFocusPainted(false);
        button.setEnabled(enabled);
        
        // Configurar colores según estado
        if (enabled) {
            button.setBackground(bgColor);
            button.setForeground(fgColor);
        } else {
            // Para botón deshabilitado, usar gris
            button.setBackground(Color.GRAY);
            button.setForeground(Color.LIGHT_GRAY);
        }
        
        return button;
    }
    
    /**
     * Método público para refrescar los datos desde la ventana principal
     */
    public void refrescarDatos() {
        cargarDatos();
    }
}