package com.ypacarai.cooperativa.activos.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import com.ypacarai.cooperativa.activos.model.Usuario;
import com.ypacarai.cooperativa.activos.service.GestionUsuariosService;

/**
 * Ventana para Editar Usuarios Existentes
 * Sistema de Gestión de Activos - Cooperativa Ypacaraí LTDA
 */
public class EditarUsuarioWindow extends JFrame {
    
    // Colores corporativos
    private static final Color COLOR_VERDE_COOPERATIVA = new Color(34, 139, 34);
    private static final Color COLOR_AZUL = new Color(70, 130, 180);
    private static final Color COLOR_ROJO = new Color(220, 53, 69);
    private static final Color COLOR_GRIS = new Color(108, 117, 125);
    private static final Color COLOR_BLANCO = Color.WHITE;
    private static final Color COLOR_GRIS_FONDO = new Color(248, 249, 250);
    
    // Componentes de la interfaz
    private JTextField txtNombre;
    private JTextField txtUsuario;
    private JPasswordField txtPassword;
    private JPasswordField txtConfirmarPassword;
    private JTextField txtEmail;
    private JComboBox<Usuario.Rol> cmbRol;
    private JButton btnGuardar;
    private JButton btnCancelar;
    private JButton btnLimpiar;
    private JLabel lblStatus;
    
    // Servicios y datos
    private GestionUsuariosService gestionUsuariosService;
    private Usuario usuarioActual;
    private Usuario usuarioAEditar;
    private JFrame ventanaPadre;
    
    public EditarUsuarioWindow(JFrame parent, Usuario usuarioActual, Usuario usuarioAEditar) {
        this.ventanaPadre = parent;
        this.usuarioActual = usuarioActual;
        this.usuarioAEditar = usuarioAEditar;
        this.gestionUsuariosService = new GestionUsuariosService();
        
        initComponents();
        setupEventListeners();
        validarPermisos();
        cargarDatosUsuario();
    }
    
    private void initComponents() {
        // Configuración básica de la ventana
        setTitle("✏️ Editar Usuario - Sistema de Activos");
        setSize(500, 650);
        setLocationRelativeTo(ventanaPadre);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);
        
        // Panel principal
        setLayout(new BorderLayout());
        getContentPane().setBackground(COLOR_GRIS_FONDO);
        
        // Panel de encabezado
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(COLOR_VERDE_COOPERATIVA);
        headerPanel.setBorder(new EmptyBorder(15, 20, 15, 20));
        
        JLabel lblTitulo = new JLabel("✏️ Editar Usuario");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitulo.setForeground(COLOR_BLANCO);
        
        JLabel lblSubtitulo = new JLabel("Modificar información del usuario existente");
        lblSubtitulo.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblSubtitulo.setForeground(COLOR_BLANCO);
        
        headerPanel.add(lblTitulo, BorderLayout.NORTH);
        headerPanel.add(lblSubtitulo, BorderLayout.SOUTH);
        
        // Panel del formulario
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(COLOR_BLANCO);
        formPanel.setBorder(new EmptyBorder(30, 40, 20, 40));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 5, 8, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Nombre completo
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("👤 Nombre Completo:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        txtNombre = new JTextField(20);
        txtNombre.setFont(new Font("Arial", Font.PLAIN, 12));
        formPanel.add(txtNombre, gbc);
        
        // Nombre de usuario
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        formPanel.add(new JLabel("🔑 Usuario:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        txtUsuario = new JTextField(20);
        txtUsuario.setFont(new Font("Arial", Font.PLAIN, 12));
        txtUsuario.setEnabled(false); // El username no se puede cambiar
        txtUsuario.setBackground(COLOR_GRIS_FONDO);
        txtUsuario.setToolTipText("El nombre de usuario no se puede modificar");
        formPanel.add(txtUsuario, gbc);
        
        // Nueva contraseña (opcional)
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        formPanel.add(new JLabel("🔒 Nueva Contraseña:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        txtPassword = new JPasswordField(20);
        txtPassword.setFont(new Font("Arial", Font.PLAIN, 12));
        txtPassword.setToolTipText("Dejar vacío para mantener la contraseña actual");
        formPanel.add(txtPassword, gbc);
        
        // Confirmar nueva contraseña
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        formPanel.add(new JLabel("🔒 Confirmar Contraseña:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        txtConfirmarPassword = new JPasswordField(20);
        txtConfirmarPassword.setFont(new Font("Arial", Font.PLAIN, 12));
        formPanel.add(txtConfirmarPassword, gbc);
        
        // Email
        gbc.gridx = 0; gbc.gridy = 4;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        formPanel.add(new JLabel("📧 Email:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        txtEmail = new JTextField(20);
        txtEmail.setFont(new Font("Arial", Font.PLAIN, 12));
        formPanel.add(txtEmail, gbc);
        
        // Rol
        gbc.gridx = 0; gbc.gridy = 5;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        formPanel.add(new JLabel("👑 Rol del Usuario:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        cmbRol = new JComboBox<>(Usuario.Rol.values());
        cmbRol.setFont(new Font("Arial", Font.PLAIN, 12));
        formPanel.add(cmbRol, gbc);
        
        // Panel de información
        gbc.gridx = 0; gbc.gridy = 6;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(15, 5, 10, 5);
        
        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setBackground(new Color(217, 237, 247));
        infoPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(174, 213, 235)),
            new EmptyBorder(10, 15, 10, 15)
        ));
        
        JLabel lblInfo = new JLabel("<html>" +
            "💡 <b>Información sobre la edición:</b><br>" +
            "• El nombre de usuario no se puede modificar<br>" +
            "• Dejar contraseña vacía para mantener la actual<br>" +
            "• Todos los cambios requieren confirmación" +
            "</html>");
        lblInfo.setFont(new Font("Arial", Font.PLAIN, 11));
        infoPanel.add(lblInfo, BorderLayout.CENTER);
        formPanel.add(infoPanel, gbc);
        
        // Panel de botones
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        buttonPanel.setBackground(COLOR_BLANCO);
        
        btnGuardar = createStyledButton("💾 Guardar Cambios", COLOR_VERDE_COOPERATIVA);
        btnLimpiar = createStyledButton("🔄 Restablecer", COLOR_AZUL);
        btnCancelar = createStyledButton("❌ Cancelar", COLOR_GRIS);
        
        buttonPanel.add(btnGuardar);
        buttonPanel.add(btnLimpiar);
        buttonPanel.add(btnCancelar);
        
        // Label de status
        lblStatus = new JLabel(" ");
        lblStatus.setFont(new Font("Arial", Font.ITALIC, 11));
        lblStatus.setHorizontalAlignment(JLabel.CENTER);
        
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        statusPanel.setBackground(COLOR_BLANCO);
        statusPanel.add(lblStatus);
        
        // Ensamblar layout
        add(headerPanel, BorderLayout.NORTH);
        add(formPanel, BorderLayout.CENTER);
        
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(buttonPanel, BorderLayout.NORTH);
        bottomPanel.add(statusPanel, BorderLayout.SOUTH);
        add(bottomPanel, BorderLayout.SOUTH);
    }
    
    private void setupEventListeners() {
        // Navegación con Enter
        txtNombre.addKeyListener(createEnterKeyListener(txtEmail));
        txtEmail.addKeyListener(createEnterKeyListener(cmbRol));
        txtPassword.addKeyListener(createEnterKeyListener(txtConfirmarPassword));
        txtConfirmarPassword.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    btnGuardar.doClick();
                }
            }
        });
        
        // Botones
        btnGuardar.addActionListener(e -> guardarCambios());
        btnLimpiar.addActionListener(e -> cargarDatosUsuario());
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
    
    private KeyAdapter createEnterKeyListener(JComponent nextComponent) {
        return new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    nextComponent.requestFocus();
                }
            }
        };
    }
    
    private void validarPermisos() {
        // Solo Jefe de Informática puede editar usuarios
        if (usuarioActual.getUsuRol() != Usuario.Rol.Jefe_Informatica) {
            mostrarError("❌ No tiene permisos para editar usuarios");
            btnGuardar.setEnabled(false);
        }
    }
    
    private void cargarDatosUsuario() {
        if (usuarioAEditar == null) return;
        
        txtNombre.setText(usuarioAEditar.getUsuNombre());
        txtUsuario.setText(usuarioAEditar.getUsuUsuario());
        txtPassword.setText(""); // No mostrar contraseña actual
        txtConfirmarPassword.setText("");
        txtEmail.setText(usuarioAEditar.getUsuEmail());
        cmbRol.setSelectedItem(usuarioAEditar.getUsuRol());
        
        lblStatus.setText("✏️ Editando: " + usuarioAEditar.getUsuNombre());
        lblStatus.setForeground(COLOR_AZUL);
    }
    
    private void guardarCambios() {
        if (!validarFormulario()) {
            return;
        }
        
        // Deshabilitar botón para evitar dobles clics
        btnGuardar.setEnabled(false);
        lblStatus.setText("💾 Guardando cambios...");
        lblStatus.setForeground(COLOR_AZUL);
        
        SwingUtilities.invokeLater(() -> {
            try {
                // Preparar datos actualizados
                String nombre = txtNombre.getText().trim();
                String email = txtEmail.getText().trim();
                Usuario.Rol rol = (Usuario.Rol) cmbRol.getSelectedItem();
                String nuevaPassword = new String(txtPassword.getPassword()).trim();
                
                // Actualizar datos del usuario
                usuarioAEditar.setUsuNombre(nombre);
                usuarioAEditar.setUsuEmail(email);
                usuarioAEditar.setUsuRol(rol);
                
                // Solo actualizar contraseña si se proporcionó una nueva
                if (!nuevaPassword.isEmpty()) {
                    usuarioAEditar.setUsuPassword(gestionUsuariosService.hashPassword(nuevaPassword));
                }
                
                // Guardar cambios
                boolean actualizado = new com.ypacarai.cooperativa.activos.dao.UsuarioDAO().update(usuarioAEditar);
                
                if (actualizado) {
                    mostrarExito("✅ Usuario actualizado exitosamente");
                    
                    // Cerrar ventana después de 1 segundo
                    javax.swing.Timer timer = new javax.swing.Timer(1000, e -> dispose());
                    timer.setRepeats(false);
                    timer.start();
                } else {
                    mostrarError("❌ Error al actualizar el usuario");
                    btnGuardar.setEnabled(true);
                }
                
            } catch (Exception e) {
                mostrarError("❌ Error: " + e.getMessage());
                btnGuardar.setEnabled(true);
            }
        });
    }
    
    private boolean validarFormulario() {
        // Validar nombre
        if (txtNombre.getText().trim().isEmpty()) {
            mostrarError("❌ El nombre es obligatorio");
            txtNombre.requestFocus();
            return false;
        }
        
        // Validar email
        String email = txtEmail.getText().trim();
        if (email.isEmpty()) {
            mostrarError("❌ El email es obligatorio");
            txtEmail.requestFocus();
            return false;
        }
        
        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            mostrarError("❌ Email inválido");
            txtEmail.requestFocus();
            return false;
        }
        
        // Validar contraseñas si se proporcionaron
        String password = new String(txtPassword.getPassword());
        String confirmarPassword = new String(txtConfirmarPassword.getPassword());
        
        if (!password.isEmpty() || !confirmarPassword.isEmpty()) {
            if (password.length() < 6) {
                mostrarError("❌ La contraseña debe tener al menos 6 caracteres");
                txtPassword.requestFocus();
                return false;
            }
            
            if (!password.equals(confirmarPassword)) {
                mostrarError("❌ Las contraseñas no coinciden");
                txtConfirmarPassword.requestFocus();
                return false;
            }
        }
        
        return true;
    }
    
    private void mostrarError(String mensaje) {
        lblStatus.setText(mensaje);
        lblStatus.setForeground(COLOR_ROJO);
    }
    
    private void mostrarExito(String mensaje) {
        lblStatus.setText(mensaje);
        lblStatus.setForeground(COLOR_VERDE_COOPERATIVA);
    }
    
    private JButton createStyledButton(String text, Color backgroundColor) {
        JButton button = new JButton(text);
        button.setBackground(backgroundColor);
        button.setForeground(COLOR_BLANCO);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        
        // Efecto hover
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            Color originalColor = backgroundColor;
            
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                if (button.isEnabled()) {
                    button.setBackground(originalColor.brighter());
                }
            }
            
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                button.setBackground(originalColor);
            }
        });
        
        return button;
    }
}