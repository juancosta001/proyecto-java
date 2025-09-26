package com.ypacarai.cooperativa.activos.view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.regex.Pattern;

import com.ypacarai.cooperativa.activos.model.Usuario;
import com.ypacarai.cooperativa.activos.service.GestionUsuariosService;

/**
 * Ventana para Crear Nuevos Usuarios
 * Sistema de Gestión de Activos - Cooperativa Ypacaraí LTDA
 */
public class CrearUsuarioWindow extends JFrame {
    
    // Colores corporativos
    private static final Color COLOR_VERDE_COOPERATIVA = new Color(34, 139, 34);
    private static final Color COLOR_VERDE_CLARO = new Color(144, 238, 144);
    private static final Color COLOR_BLANCO = Color.WHITE;
    private static final Color COLOR_GRIS_TEXTO = new Color(64, 64, 64);
    private static final Color COLOR_ROJO = new Color(220, 20, 60);
    private static final Color COLOR_AZUL = new Color(70, 130, 180);
    
    // Componentes de la interfaz
    private JTextField txtNombre;
    private JTextField txtUsuario;
    private JPasswordField txtPassword;
    private JPasswordField txtConfirmarPassword;
    private JTextField txtEmail;
    private JComboBox<Usuario.Rol> cmbRol;
    private JCheckBox chkActivo;
    private JButton btnGuardar;
    private JButton btnCancelar;
    private JButton btnLimpiar;
    private JLabel lblStatus;
    
    // Servicios
    private GestionUsuariosService gestionUsuariosService;
    private Usuario usuarioActual;
    private JFrame ventanaPadre;
    
    // Patterns para validación
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );
    
    public CrearUsuarioWindow(JFrame parent, Usuario usuarioActual) {
        this.ventanaPadre = parent;
        this.usuarioActual = usuarioActual;
        this.gestionUsuariosService = new GestionUsuariosService();
        
        initComponents();
        setupEventListeners();
        validarPermisos();
    }
    
    private void initComponents() {
        setTitle("Crear Nuevo Usuario - Sistema de Gestión de Activos");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);
        setSize(500, 650);
        setLocationRelativeTo(ventanaPadre);
        
        // Panel principal con gradiente
        JPanel panelPrincipal = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Gradiente de fondo
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
        
        // Formulario
        JPanel formPanel = createFormPanel();
        panelPrincipal.add(formPanel, BorderLayout.CENTER);
        
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
        
        // Título principal
        JLabel lblTitulo = new JLabel("👤 Crear Nuevo Usuario");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitulo.setForeground(COLOR_VERDE_COOPERATIVA);
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Subtítulo
        JLabel lblSubtitulo = new JLabel("Complete todos los campos requeridos");
        lblSubtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblSubtitulo.setForeground(COLOR_GRIS_TEXTO);
        lblSubtitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        panel.add(lblTitulo);
        panel.add(Box.createVerticalStrut(5));
        panel.add(lblSubtitulo);
        
        return panel;
    }
    
    private JPanel createFormPanel() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(10, 30, 10, 30));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        
        // Panel del formulario con fondo blanco
        JPanel formContainer = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Fondo blanco con bordes redondeados
                g2d.setColor(COLOR_BLANCO);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                
                // Borde sutil
                g2d.setColor(COLOR_VERDE_COOPERATIVA);
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 15, 15);
            }
        };
        formContainer.setOpaque(false);
        formContainer.setLayout(new BoxLayout(formContainer, BoxLayout.Y_AXIS));
        formContainer.setBorder(new EmptyBorder(25, 25, 25, 25));
        
        // Campos del formulario
        formContainer.add(createFieldPanel("Nombre Completo *", txtNombre = createTextField(), "👤"));
        formContainer.add(Box.createVerticalStrut(15));
        
        formContainer.add(createFieldPanel("Usuario *", txtUsuario = createTextField(), "🔑"));
        formContainer.add(Box.createVerticalStrut(15));
        
        formContainer.add(createFieldPanel("Contraseña *", txtPassword = createPasswordField(), "🔒"));
        formContainer.add(Box.createVerticalStrut(15));
        
        formContainer.add(createFieldPanel("Confirmar Contraseña *", txtConfirmarPassword = createPasswordField(), "🔒"));
        formContainer.add(Box.createVerticalStrut(15));
        
        formContainer.add(createFieldPanel("Correo Electrónico *", txtEmail = createTextField(), "📧"));
        formContainer.add(Box.createVerticalStrut(15));
        
        // ComboBox de roles
        cmbRol = new JComboBox<>(Usuario.Rol.values());
        cmbRol.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cmbRol.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_VERDE_COOPERATIVA, 1),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        cmbRol.setMaximumSize(new Dimension(Integer.MAX_VALUE, cmbRol.getPreferredSize().height));
        
        formContainer.add(createFieldPanel("Rol del Usuario *", cmbRol, "👑"));
        formContainer.add(Box.createVerticalStrut(15));
        
        // Checkbox para usuario activo
        chkActivo = new JCheckBox("Usuario Activo");
        chkActivo.setSelected(true);
        chkActivo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        chkActivo.setOpaque(false);
        chkActivo.setForeground(COLOR_GRIS_TEXTO);
        
        JPanel checkPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        checkPanel.setOpaque(false);
        checkPanel.add(new JLabel("✅ "));
        checkPanel.add(chkActivo);
        
        formContainer.add(checkPanel);
        formContainer.add(Box.createVerticalStrut(15));
        
        // Label de estado
        lblStatus = new JLabel(" ");
        lblStatus.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblStatus.setAlignmentX(Component.LEFT_ALIGNMENT);
        formContainer.add(lblStatus);
        
        panel.add(formContainer);
        return panel;
    }
    
    private JPanel createFieldPanel(String labelText, JComponent field, String icon) {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        
        // Label con icono
        JPanel labelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        labelPanel.setOpaque(false);
        
        JLabel iconLabel = new JLabel(icon + " ");
        iconLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        label.setForeground(COLOR_GRIS_TEXTO);
        
        labelPanel.add(iconLabel);
        labelPanel.add(label);
        
        panel.add(labelPanel);
        panel.add(Box.createVerticalStrut(5));
        panel.add(field);
        
        return panel;
    }
    
    private JTextField createTextField() {
        JTextField field = new JTextField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_VERDE_COOPERATIVA, 1),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, field.getPreferredSize().height));
        return field;
    }
    
    private JPasswordField createPasswordField() {
        JPasswordField field = new JPasswordField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_VERDE_COOPERATIVA, 1),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, field.getPreferredSize().height));
        return field;
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 20));
        panel.setOpaque(false);
        
        btnGuardar = createStyledButton("💾 Guardar", COLOR_VERDE_COOPERATIVA, COLOR_BLANCO, true);
        btnLimpiar = createStyledButton("🔄 Limpiar", COLOR_AZUL, COLOR_BLANCO, false);
        btnCancelar = createStyledButton("❌ Cancelar", COLOR_ROJO, COLOR_BLANCO, false);
        
        panel.add(btnGuardar);
        panel.add(btnLimpiar);
        panel.add(btnCancelar);
        
        return panel;
    }
    
    private JButton createStyledButton(String text, Color bgColor, Color textColor, boolean isPrimary) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Color de fondo según el estado
                Color backgroundColor = bgColor;
                if (getModel().isPressed()) {
                    backgroundColor = backgroundColor.darker();
                } else if (getModel().isRollover()) {
                    backgroundColor = backgroundColor.brighter();
                }
                
                g2d.setColor(backgroundColor);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                
                // Texto centrado
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
        button.setPreferredSize(new Dimension(120, 35));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        return button;
    }
    
    private void setupEventListeners() {
        // Navegación con Enter entre campos
        txtNombre.addKeyListener(createEnterKeyListener(txtUsuario));
        txtUsuario.addKeyListener(createEnterKeyListener(txtPassword));
        txtPassword.addKeyListener(createEnterKeyListener(txtConfirmarPassword));
        txtConfirmarPassword.addKeyListener(createEnterKeyListener(txtEmail));
        txtEmail.addKeyListener(createEnterKeyListener(cmbRol));
        
        // Validación en tiempo real
        txtEmail.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                validarEmail();
            }
        });
        
        txtUsuario.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                validarUsuario();
            }
        });
        
        txtConfirmarPassword.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                validarPasswords();
            }
        });
        
        // Botones
        btnGuardar.addActionListener(e -> guardarUsuario());
        btnLimpiar.addActionListener(e -> limpiarFormulario());
        btnCancelar.addActionListener(e -> dispose());
        
        // ESC para cerrar
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "cerrar");
        getRootPane().getActionMap().put("cerrar", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
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
        // Solo Jefe de Informática puede crear usuarios
        if (usuarioActual.getUsuRol() != Usuario.Rol.Jefe_Informatica) {
            mostrarError("❌ No tiene permisos para crear usuarios");
            btnGuardar.setEnabled(false);
        }
    }
    
    private boolean validarFormulario() {
        // Limpiar estado anterior
        lblStatus.setText(" ");
        
        // Validar campos obligatorios
        if (txtNombre.getText().trim().isEmpty()) {
            mostrarError("❌ El nombre es obligatorio");
            txtNombre.requestFocus();
            return false;
        }
        
        if (txtUsuario.getText().trim().isEmpty()) {
            mostrarError("❌ El usuario es obligatorio");
            txtUsuario.requestFocus();
            return false;
        }
        
        if (txtPassword.getPassword().length == 0) {
            mostrarError("❌ La contraseña es obligatoria");
            txtPassword.requestFocus();
            return false;
        }
        
        if (txtConfirmarPassword.getPassword().length == 0) {
            mostrarError("❌ Debe confirmar la contraseña");
            txtConfirmarPassword.requestFocus();
            return false;
        }
        
        if (txtEmail.getText().trim().isEmpty()) {
            mostrarError("❌ El correo electrónico es obligatorio");
            txtEmail.requestFocus();
            return false;
        }
        
        // Validar formato de email
        if (!EMAIL_PATTERN.matcher(txtEmail.getText().trim()).matches()) {
            mostrarError("❌ El formato del correo electrónico no es válido");
            txtEmail.requestFocus();
            return false;
        }
        
        // Validar que las contraseñas coincidan
        String password = new String(txtPassword.getPassword());
        String confirmarPassword = new String(txtConfirmarPassword.getPassword());
        
        if (!password.equals(confirmarPassword)) {
            mostrarError("❌ Las contraseñas no coinciden");
            txtConfirmarPassword.requestFocus();
            return false;
        }
        
        // Validar longitud mínima de contraseña
        if (password.length() < 4) {
            mostrarError("❌ La contraseña debe tener al menos 4 caracteres");
            txtPassword.requestFocus();
            return false;
        }
        
        return true;
    }
    
    private void validarEmail() {
        String email = txtEmail.getText().trim();
        if (!email.isEmpty() && !EMAIL_PATTERN.matcher(email).matches()) {
            lblStatus.setText("⚠️ Formato de email inválido");
            lblStatus.setForeground(Color.ORANGE);
        } else {
            lblStatus.setText(" ");
        }
    }
    
    private void validarUsuario() {
        String usuario = txtUsuario.getText().trim();
        if (usuario.length() > 0 && usuario.length() < 3) {
            lblStatus.setText("⚠️ El usuario debe tener al menos 3 caracteres");
            lblStatus.setForeground(Color.ORANGE);
        } else {
            lblStatus.setText(" ");
        }
    }
    
    private void validarPasswords() {
        String password = new String(txtPassword.getPassword());
        String confirmarPassword = new String(txtConfirmarPassword.getPassword());
        
        if (!password.isEmpty() && !confirmarPassword.isEmpty()) {
            if (!password.equals(confirmarPassword)) {
                lblStatus.setText("⚠️ Las contraseñas no coinciden");
                lblStatus.setForeground(Color.ORANGE);
            } else {
                lblStatus.setText("✅ Las contraseñas coinciden");
                lblStatus.setForeground(COLOR_VERDE_COOPERATIVA);
            }
        }
    }
    
    private void guardarUsuario() {
        System.out.println("DEBUG: Iniciando guardarUsuario()");
        
        if (!validarFormulario()) {
            System.out.println("DEBUG: Validación de formulario falló");
            return;
        }
        
        System.out.println("DEBUG: Formulario válido, procediendo a guardar");
        
        // Deshabilitar botón para evitar dobles clics
        btnGuardar.setEnabled(false);
        lblStatus.setText("💾 Guardando usuario...");
        lblStatus.setForeground(COLOR_AZUL);
        
        SwingUtilities.invokeLater(() -> {
            try {
                String nombre = txtNombre.getText().trim();
                String usuario = txtUsuario.getText().trim();
                String password = new String(txtPassword.getPassword());
                String email = txtEmail.getText().trim();
                Usuario.Rol rol = (Usuario.Rol) cmbRol.getSelectedItem();
                
                System.out.println("DEBUG: Datos a guardar:");
                System.out.println("  - Nombre: " + nombre);
                System.out.println("  - Usuario: " + usuario);
                System.out.println("  - Email: " + email);
                System.out.println("  - Rol: " + rol);
                System.out.println("  - Usuario actual ID: " + usuarioActual.getUsuId());
                
                var resultado = gestionUsuariosService.crearUsuario(
                    nombre,
                    usuario,
                    password,
                    email,
                    rol,
                    usuarioActual.getUsuId()
                );
                
                System.out.println("DEBUG: Resultado del servicio:");
                System.out.println("  - Exitoso: " + resultado.isExitoso());
                System.out.println("  - Mensaje: " + resultado.getMensaje());
                if (resultado.getId() != null) {
                    System.out.println("  - ID: " + resultado.getId());
                }
                
                if (resultado.isExitoso()) {
                    mostrarExito("✅ Usuario creado exitosamente!");
                    
                    // Mostrar diálogo de confirmación
                    int respuesta = JOptionPane.showConfirmDialog(
                        this,
                        "Usuario creado exitosamente.\n\n" +
                        "👤 Nombre: " + nombre + "\n" +
                        "🔑 Usuario: " + usuario + "\n" +
                        "📧 Email: " + email + "\n" +
                        "👑 Rol: " + rol + "\n\n" +
                        "¿Desea crear otro usuario?",
                        "Usuario Creado",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE
                    );
                    
                    if (respuesta == JOptionPane.YES_OPTION) {
                        limpiarFormulario();
                    } else {
                        dispose();
                    }
                    
                } else {
                    mostrarError("❌ Error al crear usuario: " + resultado.getMensaje());
                }
                
            } catch (Exception ex) {
                System.out.println("DEBUG: Excepción capturada: " + ex.getMessage());
                ex.printStackTrace();
                mostrarError("❌ Error inesperado: " + ex.getMessage());
            } finally {
                btnGuardar.setEnabled(true);
            }
        });
    }
    
    private void limpiarFormulario() {
        txtNombre.setText("");
        txtUsuario.setText("");
        txtPassword.setText("");
        txtConfirmarPassword.setText("");
        txtEmail.setText("");
        cmbRol.setSelectedIndex(0);
        chkActivo.setSelected(true);
        lblStatus.setText(" ");
        txtNombre.requestFocus();
    }
    
    private void mostrarError(String mensaje) {
        lblStatus.setText(mensaje);
        lblStatus.setForeground(COLOR_ROJO);
    }
    
    private void mostrarExito(String mensaje) {
        lblStatus.setText(mensaje);
        lblStatus.setForeground(COLOR_VERDE_COOPERATIVA);
    }
    
    // Método principal para pruebas
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Usuario de prueba
            Usuario usuarioPrueba = new Usuario();
            usuarioPrueba.setUsuId(1);
            usuarioPrueba.setUsuNombre("Admin");
            usuarioPrueba.setUsuUsuario("admin");
            usuarioPrueba.setUsuRol(Usuario.Rol.Jefe_Informatica);
            
            CrearUsuarioWindow window = new CrearUsuarioWindow(null, usuarioPrueba);
            window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            window.setVisible(true);
        });
    }
}