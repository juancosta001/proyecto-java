package com.ypacarai.cooperativa.activos.view;

import com.ypacarai.cooperativa.activos.model.ProveedorServicio;
import com.ypacarai.cooperativa.activos.service.MantenimientoTercerizadoService;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Ventana para crear/editar proveedores de servicios técnicos
 * Sistema de Gestión de Activos - Cooperativa Ypacaraí LTDA
 */
public class ProveedorServicioWindow extends JDialog {
    
    // Colores corporativos
    private static final Color COLOR_VERDE_COOPERATIVA = new Color(34, 139, 34);
    private static final Color COLOR_BLANCO = Color.WHITE;
    private static final Color COLOR_GRIS_CLARO = new Color(245, 245, 245);
    private static final Color COLOR_GRIS_TEXTO = new Color(64, 64, 64);
    
    private final Window parent;
    private final MantenimientoTercerizadoService mantenimientoService;
    private ProveedorServicio proveedor;
    private boolean modoEdicion;
    
    // Componentes del formulario
    private JTextField txtNombre;
    private JTextField txtTelefono;
    private JTextField txtEmail;
    private JTextArea txtDireccion;
    private JTextField txtContactoPrincipal;
    private JTextArea txtEspecialidades;
    private JButton btnGuardar;
    private JButton btnCancelar;
    
    /**
     * Constructor para crear nuevo proveedor
     */
    public ProveedorServicioWindow(Window parent) {
        this(parent, null);
    }
    
    /**
     * Constructor para editar proveedor existente
     */
    public ProveedorServicioWindow(Window parent, ProveedorServicio proveedor) {
        super(parent, proveedor == null ? "Nuevo Proveedor de Servicios" : "Editar Proveedor", 
              ModalityType.APPLICATION_MODAL);
        
        this.parent = parent;
        this.proveedor = proveedor;
        this.modoEdicion = (proveedor != null);
        this.mantenimientoService = new MantenimientoTercerizadoService();
        
        initializeComponents();
        setupEventListeners();
        
        if (modoEdicion) {
            cargarDatosProveedor();
        }
        
        setLocationRelativeTo(parent);
    }
    
    private void initializeComponents() {
        setSize(600, 550);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setResizable(false);
        
        // Panel principal
        JPanel panelPrincipal = new JPanel(new BorderLayout(15, 15));
        panelPrincipal.setBackground(COLOR_BLANCO);
        panelPrincipal.setBorder(new EmptyBorder(25, 25, 25, 25));
        
        // Título
        String tituloTexto = modoEdicion ? "✏️ Editar Proveedor de Servicios" : "➕ Nuevo Proveedor de Servicios";
        JLabel lblTitulo = new JLabel(tituloTexto);
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
        
        // Nombre de la empresa
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Nombre de la empresa: *"), gbc);
        
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        txtNombre = new JTextField();
        txtNombre.setPreferredSize(new Dimension(300, 30));
        panel.add(txtNombre, gbc);
        
        // Teléfono
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0.0;
        panel.add(new JLabel("Número de teléfono: *"), gbc);
        
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        txtTelefono = new JTextField();
        txtTelefono.setPreferredSize(new Dimension(200, 30));
        panel.add(txtTelefono, gbc);
        
        // Email
        gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0.0;
        panel.add(new JLabel("Email:"), gbc);
        
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        txtEmail = new JTextField();
        txtEmail.setPreferredSize(new Dimension(300, 30));
        panel.add(txtEmail, gbc);
        
        // Dirección
        gbc.gridx = 0; gbc.gridy = 3; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0.0;
        panel.add(new JLabel("Dirección:"), gbc);
        
        gbc.gridx = 1; gbc.fill = GridBagConstraints.BOTH; gbc.weightx = 1.0; gbc.weighty = 0.2;
        txtDireccion = new JTextArea(2, 30);
        txtDireccion.setLineWrap(true);
        txtDireccion.setWrapStyleWord(true);
        txtDireccion.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY),
            new EmptyBorder(5, 5, 5, 5)
        ));
        JScrollPane scrollDireccion = new JScrollPane(txtDireccion);
        scrollDireccion.setPreferredSize(new Dimension(300, 60));
        panel.add(scrollDireccion, gbc);
        
        // Contacto principal
        gbc.gridx = 0; gbc.gridy = 4; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0.0; gbc.weighty = 0.0;
        panel.add(new JLabel("Contacto principal: *"), gbc);
        
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        txtContactoPrincipal = new JTextField();
        txtContactoPrincipal.setPreferredSize(new Dimension(300, 30));
        panel.add(txtContactoPrincipal, gbc);
        
        // Especialidades
        gbc.gridx = 0; gbc.gridy = 5; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0.0;
        panel.add(new JLabel("Especialidades:"), gbc);
        
        gbc.gridx = 1; gbc.fill = GridBagConstraints.BOTH; gbc.weightx = 1.0; gbc.weighty = 0.4;
        txtEspecialidades = new JTextArea(3, 30);
        txtEspecialidades.setLineWrap(true);
        txtEspecialidades.setWrapStyleWord(true);
        txtEspecialidades.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY),
            new EmptyBorder(5, 5, 5, 5)
        ));
        txtEspecialidades.setToolTipText("Ej: Reparación de PC, impresoras, UPS, servidores, equipos de red, etc.");
        JScrollPane scrollEspecialidades = new JScrollPane(txtEspecialidades);
        scrollEspecialidades.setPreferredSize(new Dimension(300, 80));
        panel.add(scrollEspecialidades, gbc);
        
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
        
        String textoBoton = modoEdicion ? "💾 Actualizar" : "💾 Guardar";
        btnGuardar = new JButton(textoBoton);
        btnGuardar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnGuardar.setPreferredSize(new Dimension(140, 35));
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
                guardarProveedor();
            }
        });
        
        // Validación de teléfono en tiempo real
        txtTelefono.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyTyped(java.awt.event.KeyEvent evt) {
                char c = evt.getKeyChar();
                if (!Character.isDigit(c) && c != '-' && c != '(' && c != ')' && c != ' ' && 
                    c != java.awt.event.KeyEvent.VK_BACK_SPACE) {
                    evt.consume();
                }
            }
        });
    }
    
    private void cargarDatosProveedor() {
        if (proveedor != null) {
            txtNombre.setText(proveedor.getPrvNombre());
            txtTelefono.setText(proveedor.getPrvNumeroTelefono());
            txtEmail.setText(proveedor.getPrvEmail());
            txtDireccion.setText(proveedor.getPrvDireccion());
            txtContactoPrincipal.setText(proveedor.getPrvContactoPrincipal());
            txtEspecialidades.setText(proveedor.getPrvEspecialidades());
        }
    }
    
    private void guardarProveedor() {
        try {
            // Validaciones
            if (!validarFormulario()) {
                return;
            }
            
            // Crear o actualizar proveedor
            ProveedorServicio proveedorGuardar;
            if (modoEdicion) {
                proveedorGuardar = this.proveedor;
            } else {
                proveedorGuardar = new ProveedorServicio();
            }
            
            // Asignar valores
            proveedorGuardar.setPrvNombre(txtNombre.getText().trim());
            proveedorGuardar.setPrvNumeroTelefono(txtTelefono.getText().trim());
            proveedorGuardar.setPrvEmail(txtEmail.getText().trim().isEmpty() ? null : txtEmail.getText().trim());
            proveedorGuardar.setPrvDireccion(txtDireccion.getText().trim().isEmpty() ? null : txtDireccion.getText().trim());
            proveedorGuardar.setPrvContactoPrincipal(txtContactoPrincipal.getText().trim());
            proveedorGuardar.setPrvEspecialidades(txtEspecialidades.getText().trim().isEmpty() ? null : txtEspecialidades.getText().trim());
            
            boolean exito;
            if (modoEdicion) {
                exito = mantenimientoService.actualizarProveedor(proveedorGuardar);
                if (exito) {
                    JOptionPane.showMessageDialog(this,
                        "Proveedor actualizado exitosamente.",
                        "Proveedor Actualizado", JOptionPane.INFORMATION_MESSAGE);
                }
            } else {
                int id = mantenimientoService.registrarProveedor(proveedorGuardar);
                exito = (id > 0);
                if (exito) {
                    JOptionPane.showMessageDialog(this,
                        String.format("Proveedor registrado exitosamente.\nID: %d\nNombre: %s", 
                                    id, proveedorGuardar.getPrvNombre()),
                        "Proveedor Registrado", JOptionPane.INFORMATION_MESSAGE);
                }
            }
            
            if (exito) {
                dispose();
            } else {
                throw new RuntimeException("No se pudo " + (modoEdicion ? "actualizar" : "registrar") + " el proveedor");
            }
            
        } catch (Exception e) {
            System.err.println("Error guardando proveedor: " + e.getMessage());
            JOptionPane.showMessageDialog(this,
                "Error al " + (modoEdicion ? "actualizar" : "registrar") + " el proveedor: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private boolean validarFormulario() {
        StringBuilder errores = new StringBuilder();
        
        if (txtNombre.getText().trim().isEmpty()) {
            errores.append("• El nombre de la empresa es obligatorio\n");
        }
        
        if (txtTelefono.getText().trim().isEmpty()) {
            errores.append("• El número de teléfono es obligatorio\n");
        }
        
        if (txtContactoPrincipal.getText().trim().isEmpty()) {
            errores.append("• El contacto principal es obligatorio\n");
        }
        
        // Validación básica de email si se proporciona
        String email = txtEmail.getText().trim();
        if (!email.isEmpty() && !email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            errores.append("• El formato del email no es válido\n");
        }
        
        if (errores.length() > 0) {
            JOptionPane.showMessageDialog(this,
                "Por favor corrija los siguientes errores:\n\n" + errores.toString(),
                "Errores de validación", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        return true;
    }
}