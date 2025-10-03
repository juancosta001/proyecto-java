package com.ypacarai.cooperativa.activos.view;

import com.ypacarai.cooperativa.activos.model.*;
import com.ypacarai.cooperativa.activos.service.MantenimientoTercerizadoService;
import com.ypacarai.cooperativa.activos.service.ActivoService;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.util.List;

/**
 * Ventana para registrar una nueva solicitud de mantenimiento tercerizado
 * Sistema de Gestión de Activos - Cooperativa Ypacaraí LTDA
 */
public class SolicitudMantenimientoTercerizadoWindow extends JDialog {
    
    // Colores corporativos
    private static final Color COLOR_VERDE_COOPERATIVA = new Color(34, 139, 34);
    private static final Color COLOR_BLANCO = Color.WHITE;
    private static final Color COLOR_GRIS_CLARO = new Color(245, 245, 245);
    private static final Color COLOR_GRIS_TEXTO = new Color(64, 64, 64);
    private static final Color COLOR_ROJO_DANGER = new Color(220, 20, 60);
    
    private final Window parent;
    private final Usuario usuarioActual;
    private final MantenimientoTercerizadoService mantenimientoService;
    private final ActivoService activoService;
    
    // Componentes del formulario
    private JComboBox<Activo> cmbActivo;
    private JComboBox<ProveedorServicio> cmbProveedor;
    private JTextArea txtDescripcionProblema;
    private JTextArea txtEstadoEquipoAntes;
    private JTextField txtMontoPresupuestado;
    private JButton btnGuardar;
    private JButton btnCancelar;
    
    public SolicitudMantenimientoTercerizadoWindow(Window parent, Usuario usuario) {
        super(parent, "Solicitar Mantenimiento Técnico Tercerizado", ModalityType.APPLICATION_MODAL);
        this.parent = parent;
        this.usuarioActual = usuario;
        this.mantenimientoService = new MantenimientoTercerizadoService();
        this.activoService = new ActivoService();
        
        initializeComponents();
        setupEventListeners();
        loadComboBoxData();
        
        setLocationRelativeTo(parent);
    }
    
    private void initializeComponents() {
        setSize(700, 650);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setResizable(false);
        
        // Panel principal
        JPanel panelPrincipal = new JPanel(new BorderLayout(15, 15));
        panelPrincipal.setBackground(COLOR_BLANCO);
        panelPrincipal.setBorder(new EmptyBorder(25, 25, 25, 25));
        
        // Título
        JLabel lblTitulo = new JLabel("🔧 Nueva Solicitud de Mantenimiento Tercerizado");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitulo.setForeground(COLOR_VERDE_COOPERATIVA);
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        lblTitulo.setBorder(new EmptyBorder(0, 0, 20, 0));
        
        panelPrincipal.add(lblTitulo, BorderLayout.NORTH);
        
        // Panel del formulario
        JPanel panelFormulario = createFormularioPanel();
        panelPrincipal.add(panelFormulario, BorderLayout.CENTER);
        
        // Panel de botones
        JPanel panelBotones = createPanelBotones();
        panelPrincipal.add(panelBotones, BorderLayout.SOUTH);
        
        add(panelPrincipal);
    }
    
    private JPanel createFormularioPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Selección de Activo
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Activo a reparar: *"), gbc);
        
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        cmbActivo = new JComboBox<>();
        cmbActivo.setPreferredSize(new Dimension(300, 30));
        cmbActivo.setRenderer(new ActivoComboBoxRenderer());
        panel.add(cmbActivo, gbc);
        
        // Selección de Proveedor
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0.0;
        panel.add(new JLabel("Proveedor de servicios: *"), gbc);
        
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        cmbProveedor = new JComboBox<>();
        cmbProveedor.setPreferredSize(new Dimension(300, 30));
        panel.add(cmbProveedor, gbc);
        
        // Descripción del problema
        gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0.0;
        panel.add(new JLabel("Descripción del problema: *"), gbc);
        
        gbc.gridx = 1; gbc.fill = GridBagConstraints.BOTH; gbc.weightx = 1.0; gbc.weighty = 0.3;
        txtDescripcionProblema = new JTextArea(4, 30);
        txtDescripcionProblema.setLineWrap(true);
        txtDescripcionProblema.setWrapStyleWord(true);
        txtDescripcionProblema.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY),
            new EmptyBorder(5, 5, 5, 5)
        ));
        JScrollPane scrollProblema = new JScrollPane(txtDescripcionProblema);
        scrollProblema.setPreferredSize(new Dimension(300, 100));
        panel.add(scrollProblema, gbc);
        
        // Estado del equipo antes
        gbc.gridx = 0; gbc.gridy = 3; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0.0; gbc.weighty = 0.0;
        panel.add(new JLabel("Estado del equipo antes: *"), gbc);
        
        gbc.gridx = 1; gbc.fill = GridBagConstraints.BOTH; gbc.weightx = 1.0; gbc.weighty = 0.3;
        txtEstadoEquipoAntes = new JTextArea(3, 30);
        txtEstadoEquipoAntes.setLineWrap(true);
        txtEstadoEquipoAntes.setWrapStyleWord(true);
        txtEstadoEquipoAntes.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY),
            new EmptyBorder(5, 5, 5, 5)
        ));
        JScrollPane scrollEstado = new JScrollPane(txtEstadoEquipoAntes);
        scrollEstado.setPreferredSize(new Dimension(300, 80));
        panel.add(scrollEstado, gbc);
        
        // Monto presupuestado
        gbc.gridx = 0; gbc.gridy = 4; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0.0; gbc.weighty = 0.0;
        panel.add(new JLabel("Monto presupuestado (₲):"), gbc);
        
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        txtMontoPresupuestado = new JTextField();
        txtMontoPresupuestado.setPreferredSize(new Dimension(150, 30));
        txtMontoPresupuestado.setToolTipText("Opcional - Ingrese solo números");
        panel.add(txtMontoPresupuestado, gbc);
        
        // Panel de información
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL;
        JPanel panelInfo = createPanelInformacion();
        panel.add(panelInfo, gbc);
        
        return panel;
    }
    
    private JPanel createPanelInformacion() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new TitledBorder("ℹ️ Información importante"));
        panel.setBackground(COLOR_GRIS_CLARO);
        panel.setPreferredSize(new Dimension(0, 120));
        
        JTextArea txtInfo = new JTextArea();
        txtInfo.setText(
            "• El equipo será marcado como 'En Servicio Externo' una vez registrado el retiro.\n" +
            "• El monto presupuestado es opcional y puede actualizarse posteriormente.\n" +
            "• Una vez creada la solicitud, deberá registrar:\n" +
            "  - Fecha de retiro cuando entregue el equipo al proveedor\n" +
            "  - Fecha de entrega cuando reciba el equipo reparado\n" +
            "• Todos los campos marcados con (*) son obligatorios."
        );
        txtInfo.setEditable(false);
        txtInfo.setOpaque(false);
        txtInfo.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        txtInfo.setForeground(COLOR_GRIS_TEXTO);
        txtInfo.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        panel.add(txtInfo, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createPanelBotones() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        panel.setOpaque(false);
        
        btnCancelar = new JButton("❌ Cancelar");
        btnCancelar.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnCancelar.setPreferredSize(new Dimension(120, 35));
        btnCancelar.setBackground(COLOR_GRIS_CLARO);
        btnCancelar.setForeground(COLOR_GRIS_TEXTO);
        btnCancelar.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        btnCancelar.setFocusPainted(false);
        
        btnGuardar = new JButton("💾 Guardar Solicitud");
        btnGuardar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnGuardar.setPreferredSize(new Dimension(160, 35));
        btnGuardar.setBackground(COLOR_VERDE_COOPERATIVA);
        btnGuardar.setForeground(COLOR_BLANCO);
        btnGuardar.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        btnGuardar.setFocusPainted(false);
        btnGuardar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        panel.add(btnCancelar);
        panel.add(btnGuardar);
        
        return panel;
    }
    
    private void setupEventListeners() {
        btnCancelar.addActionListener(e -> dispose());
        
        btnGuardar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                guardarSolicitud();
            }
        });
        
        // Validación en tiempo real del monto
        txtMontoPresupuestado.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyTyped(java.awt.event.KeyEvent evt) {
                char c = evt.getKeyChar();
                if (!Character.isDigit(c) && c != '.' && c != ',' && c != java.awt.event.KeyEvent.VK_BACK_SPACE) {
                    evt.consume();
                }
            }
        });
    }
    
    private void loadComboBoxData() {
        try {
            // Cargar activos operativos y en mantenimiento
            List<Activo> activos = activoService.obtenerTodosLosActivos();
            cmbActivo.removeAllItems();
            cmbActivo.addItem(null); // Opción vacía
            
            for (Activo activo : activos) {
                // Solo mostrar activos que no estén ya en servicio externo
                if (activo.getActEstado() != Activo.Estado.En_Servicio_Externo) {
                    cmbActivo.addItem(activo);
                }
            }
            
            // Cargar proveedores activos
            List<ProveedorServicio> proveedores = mantenimientoService.obtenerProveedoresActivos();
            cmbProveedor.removeAllItems();
            cmbProveedor.addItem(null); // Opción vacía
            
            for (ProveedorServicio proveedor : proveedores) {
                cmbProveedor.addItem(proveedor);
            }
            
            if (proveedores.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "No hay proveedores de servicios registrados.\n" +
                    "Debe registrar al menos un proveedor antes de solicitar mantenimiento.",
                    "Advertencia", JOptionPane.WARNING_MESSAGE);
                btnGuardar.setEnabled(false);
            }
            
        } catch (Exception e) {
            System.err.println("Error cargando datos para combos: " + e.getMessage());
            JOptionPane.showMessageDialog(this,
                "Error cargando datos: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void guardarSolicitud() {
        try {
            // Validaciones
            if (!validarFormulario()) {
                return;
            }
            
            Activo activoSeleccionado = (Activo) cmbActivo.getSelectedItem();
            ProveedorServicio proveedorSeleccionado = (ProveedorServicio) cmbProveedor.getSelectedItem();
            String descripcionProblema = txtDescripcionProblema.getText().trim();
            String estadoEquipoAntes = txtEstadoEquipoAntes.getText().trim();
            
            // Procesar monto presupuestado
            BigDecimal montoPresupuestado = null;
            String montoTexto = txtMontoPresupuestado.getText().trim();
            if (!montoTexto.isEmpty()) {
                try {
                    montoPresupuestado = new BigDecimal(montoTexto.replace(",", ""));
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this,
                        "El monto presupuestado no es válido.",
                        "Error de validación", JOptionPane.ERROR_MESSAGE);
                    txtMontoPresupuestado.requestFocus();
                    return;
                }
            }
            
            // Debug del usuario
            System.out.println("=== DEBUG USUARIO EN SOLICITUD ===");
            System.out.println("Usuario actual: " + (usuarioActual != null ? usuarioActual.getUsuNombre() : "NULL"));
            System.out.println("Usuario ID: " + (usuarioActual != null ? usuarioActual.getUsuId() : "NULL"));
            
            // Guardar la solicitud
            int mantId = mantenimientoService.solicitarMantenimiento(
                activoSeleccionado.getActId(),
                proveedorSeleccionado.getPrvId(),
                descripcionProblema,
                estadoEquipoAntes,
                montoPresupuestado,
                usuarioActual.getUsuId()
            );
            
            if (mantId > 0) {
                JOptionPane.showMessageDialog(this,
                    String.format("Solicitud de mantenimiento registrada exitosamente.\n" +
                                "ID de solicitud: %d\n" +
                                "Activo: %s\n" +
                                "Proveedor: %s\n\n" +
                                "Próximo paso: Registrar la fecha de retiro cuando entregue el equipo.",
                                mantId, activoSeleccionado.getActNumeroActivo(), 
                                proveedorSeleccionado.getPrvNombre()),
                    "Solicitud Registrada", JOptionPane.INFORMATION_MESSAGE);
                
                // Actualizar panel padre si es posible
                // El parent será actualizado externamente
                
                dispose();
            } else {
                throw new RuntimeException("No se pudo generar el ID de la solicitud");
            }
            
        } catch (Exception e) {
            System.err.println("Error guardando solicitud: " + e.getMessage());
            JOptionPane.showMessageDialog(this,
                "Error al guardar la solicitud: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private boolean validarFormulario() {
        StringBuilder errores = new StringBuilder();
        
        if (cmbActivo.getSelectedItem() == null) {
            errores.append("• Debe seleccionar un activo\n");
        }
        
        if (cmbProveedor.getSelectedItem() == null) {
            errores.append("• Debe seleccionar un proveedor\n");
        }
        
        if (txtDescripcionProblema.getText().trim().isEmpty()) {
            errores.append("• Debe ingresar la descripción del problema\n");
        }
        
        if (txtEstadoEquipoAntes.getText().trim().isEmpty()) {
            errores.append("• Debe describir el estado actual del equipo\n");
        }
        
        if (errores.length() > 0) {
            JOptionPane.showMessageDialog(this,
                "Por favor corrija los siguientes errores:\n\n" + errores.toString(),
                "Errores de validación", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        return true;
    }
    
    /**
     * Renderer personalizado para mostrar información detallada de los activos
     */
    private class ActivoComboBoxRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, 
                int index, boolean isSelected, boolean cellHasFocus) {
            
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            
            if (value == null) {
                setText("Seleccione un activo...");
                setForeground(Color.GRAY);
            } else if (value instanceof Activo) {
                Activo activo = (Activo) value;
                setText(String.format("%s - %s %s (%s)", 
                    activo.getActNumeroActivo(),
                    activo.getActMarca() != null ? activo.getActMarca() : "",
                    activo.getActModelo() != null ? activo.getActModelo() : "",
                    activo.getActEstado().toString().replace("_", " ")
                ));
                
                // Color según estado
                if (isSelected) {
                    setForeground(COLOR_BLANCO);
                } else {
                    switch (activo.getActEstado()) {
                        case Operativo:
                            setForeground(COLOR_VERDE_COOPERATIVA);
                            break;
                        case En_Mantenimiento:
                            setForeground(new Color(255, 140, 0));
                            break;
                        case Fuera_Servicio:
                            setForeground(COLOR_ROJO_DANGER);
                            break;
                        default:
                            setForeground(COLOR_GRIS_TEXTO);
                            break;
                    }
                }
            }
            
            return this;
        }
    }
}