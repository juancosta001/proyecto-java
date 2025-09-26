package com.ypacarai.cooperativa.activos.view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;

import com.ypacarai.cooperativa.activos.dao.UsuarioDAO;
import com.ypacarai.cooperativa.activos.model.Usuario;

/**
 * Ventana de Login del Sistema de Gestión de Activos
 * Cooperativa Ypacaraí LTDA
 */
public class LoginWindowNew extends JFrame {
    
    // Colores corporativos de la cooperativa
    private static final Color COLOR_VERDE_COOPERATIVA = new Color(34, 139, 34); // Verde oscuro
    private static final Color COLOR_VERDE_CLARO = new Color(144, 238, 144); // Verde claro
    private static final Color COLOR_BLANCO = Color.WHITE;
    private static final Color COLOR_GRIS_TEXTO = new Color(64, 64, 64);
    
    // Componentes de la interfaz
    private JTextField txtUsuario;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JButton btnSalir;
    private JLabel lblStatus;
    private UsuarioDAO usuarioDAO;
    
    public LoginWindowNew() {
        this.usuarioDAO = new UsuarioDAO();
        initComponents();
        setupEventListeners();
    }
    
    private void initComponents() {
        setTitle("Sistema de Gestión de Activos - Cooperativa Ypacaraí LTDA");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setSize(450, 550);
        setLocationRelativeTo(null);
        
        // Panel principal con gradiente
        JPanel panelPrincipal = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                
                // Gradiente de fondo
                GradientPaint gradient = new GradientPaint(
                    0, 0, COLOR_BLANCO,
                    0, getHeight(), COLOR_VERDE_CLARO
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        panelPrincipal.setLayout(new BorderLayout());
        
        // Panel del logo y título
        JPanel panelSuperior = new JPanel();
        panelSuperior.setOpaque(false);
        panelSuperior.setLayout(new BoxLayout(panelSuperior, BoxLayout.Y_AXIS));
        panelSuperior.setBorder(new EmptyBorder(30, 20, 20, 20));
        
        // Logo (simulado con texto estilizado)
        JLabel lblLogo = new JLabel("🏛️");
        lblLogo.setFont(new Font("Segoe UI", Font.PLAIN, 48));
        lblLogo.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Título principal
        JLabel lblTitulo = new JLabel("COOPERATIVA YPACARAÍ LTDA");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitulo.setForeground(COLOR_VERDE_COOPERATIVA);
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Subtítulo
        JLabel lblSubtitulo = new JLabel("Sistema de Gestión de Activos");
        lblSubtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblSubtitulo.setForeground(COLOR_GRIS_TEXTO);
        lblSubtitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        panelSuperior.add(lblLogo);
        panelSuperior.add(Box.createVerticalStrut(10));
        panelSuperior.add(lblTitulo);
        panelSuperior.add(Box.createVerticalStrut(5));
        panelSuperior.add(lblSubtitulo);
        
        // Panel del formulario de login
        JPanel panelFormulario = createLoginPanel();
        
        // Panel inferior con información
        JPanel panelInferior = new JPanel();
        panelInferior.setOpaque(false);
        panelInferior.setBorder(new EmptyBorder(10, 20, 20, 20));
        
        JLabel lblVersion = new JLabel("Versión 1.0 - " + java.time.LocalDate.now().getYear());
        lblVersion.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lblVersion.setForeground(COLOR_GRIS_TEXTO);
        lblVersion.setHorizontalAlignment(SwingConstants.CENTER);
        panelInferior.add(lblVersion);
        
        // Ensamblar la ventana
        panelPrincipal.add(panelSuperior, BorderLayout.NORTH);
        panelPrincipal.add(panelFormulario, BorderLayout.CENTER);
        panelPrincipal.add(panelInferior, BorderLayout.SOUTH);
        
        add(panelPrincipal);
    }
    
    private JPanel createLoginPanel() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(20, 40, 20, 40));
        
        // Panel del formulario con fondo blanco y bordes redondeados
        JPanel formulario = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Fondo blanco con bordes redondeados
                g2d.setColor(COLOR_BLANCO);
                g2d.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);
                
                // Borde sutil
                g2d.setColor(COLOR_VERDE_COOPERATIVA);
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 15, 15);
            }
        };
        formulario.setOpaque(false);
        formulario.setLayout(new BoxLayout(formulario, BoxLayout.Y_AXIS));
        formulario.setBorder(new EmptyBorder(25, 25, 25, 25));
        
        // Título del formulario
        JLabel lblLoginTitulo = new JLabel("Iniciar Sesión");
        lblLoginTitulo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblLoginTitulo.setForeground(COLOR_VERDE_COOPERATIVA);
        lblLoginTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Campo usuario
        JLabel lblUsuario = new JLabel("Usuario:");
        lblUsuario.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblUsuario.setForeground(COLOR_GRIS_TEXTO);
        lblUsuario.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        txtUsuario = new JTextField(15);
        txtUsuario.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtUsuario.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_VERDE_COOPERATIVA, 1),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        txtUsuario.setMaximumSize(new Dimension(Integer.MAX_VALUE, txtUsuario.getPreferredSize().height));
        
        // Campo contraseña
        JLabel lblPassword = new JLabel("Contraseña:");
        lblPassword.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblPassword.setForeground(COLOR_GRIS_TEXTO);
        lblPassword.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        txtPassword = new JPasswordField(15);
        txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtPassword.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_VERDE_COOPERATIVA, 1),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        txtPassword.setMaximumSize(new Dimension(Integer.MAX_VALUE, txtPassword.getPreferredSize().height));
        
        // Botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        panelBotones.setOpaque(false);
        
        btnLogin = createStyledButton("Ingresar", COLOR_VERDE_COOPERATIVA, COLOR_BLANCO, true);
        btnSalir = createStyledButton("Salir", Color.GRAY, COLOR_BLANCO, false);
        
        panelBotones.add(btnLogin);
        panelBotones.add(btnSalir);
        
        // Label de estado
        lblStatus = new JLabel(" ");
        lblStatus.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblStatus.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblStatus.setHorizontalAlignment(SwingConstants.CENTER);
        
        // Ensamblar formulario
        formulario.add(lblLoginTitulo);
        formulario.add(Box.createVerticalStrut(20));
        formulario.add(lblUsuario);
        formulario.add(Box.createVerticalStrut(5));
        formulario.add(txtUsuario);
        formulario.add(Box.createVerticalStrut(15));
        formulario.add(lblPassword);
        formulario.add(Box.createVerticalStrut(5));
        formulario.add(txtPassword);
        formulario.add(Box.createVerticalStrut(20));
        formulario.add(panelBotones);
        formulario.add(Box.createVerticalStrut(10));
        formulario.add(lblStatus);
        
        panel.add(formulario);
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
        button.setPreferredSize(new Dimension(90, 35));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        return button;
    }
    
    private void setupEventListeners() {
        // Enter en campo usuario pasa a contraseña
        txtUsuario.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    txtPassword.requestFocus();
                }
            }
        });
        
        // Enter en contraseña ejecuta login
        txtPassword.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    performLogin();
                }
            }
        });
        
        // Botón login
        btnLogin.addActionListener(e -> performLogin());
        
        // Botón salir
        btnSalir.addActionListener(e -> System.exit(0));
        
        // Valores por defecto para pruebas (remover en producción)
        txtUsuario.setText("admin");
        txtPassword.setText("admin");
    }
    
    private void performLogin() {
        String usuario = txtUsuario.getText().trim();
        String password = new String(txtPassword.getPassword());
        
        // Validaciones básicas
        if (usuario.isEmpty()) {
            mostrarError("Por favor ingrese su usuario");
            txtUsuario.requestFocus();
            return;
        }
        
        if (password.isEmpty()) {
            mostrarError("Por favor ingrese su contraseña");
            txtPassword.requestFocus();
            return;
        }
        
        // Mostrar estado de carga
        mostrarInfo("Verificando credenciales...");
        btnLogin.setEnabled(false);
        
        // Ejecutar autenticación en hilo separado para no bloquear UI
        SwingWorker<Usuario, Void> worker = new SwingWorker<Usuario, Void>() {
            @Override
            protected Usuario doInBackground() throws Exception {
                return autenticarUsuario(usuario, password);
            }
            
            @Override
            protected void done() {
                try {
                    Usuario usuarioAutenticado = get();
                    
                    if (usuarioAutenticado != null) {
                        mostrarExito("¡Bienvenido " + usuarioAutenticado.getUsuNombre() + "!");
                        
                        // Abrir ventana principal después de una pausa
                        Timer timer = new Timer(1000, e -> {
                            abrirVentanaPrincipal(usuarioAutenticado);
                            dispose();
                        });
                        timer.setRepeats(false);
                        timer.start();
                        
                    } else {
                        mostrarError("Usuario o contraseña incorrectos");
                        txtPassword.setText("");
                        txtUsuario.requestFocus();
                    }
                    
                } catch (Exception e) {
                    mostrarError("Error al verificar credenciales: " + e.getMessage());
                    e.printStackTrace();
                } finally {
                    btnLogin.setEnabled(true);
                }
            }
        };
        
        worker.execute();
    }
    
    private Usuario autenticarUsuario(String usuario, String password) {
        try {
            // Buscar usuario real en la base de datos
            Optional<Usuario> usuarioOpt = usuarioDAO.findByUsername(usuario);
            
            if (usuarioOpt.isPresent()) {
                Usuario usuarioEncontrado = usuarioOpt.get();
                
                // Verificar que el usuario esté activo
                if (usuarioEncontrado.isActivo()) {
                    // Verificar contraseña (comparando hash SHA-256)
                    String passwordHash = hashPassword(password);
                    if (passwordHash.equals(usuarioEncontrado.getUsuPassword())) {
                        System.out.println("✅ Login exitoso para usuario: " + usuarioEncontrado.getUsuNombre() + 
                                         " - Rol: " + usuarioEncontrado.getUsuRol());
                        return usuarioEncontrado;
                    } else {
                        System.out.println("❌ Contraseña incorrecta para usuario: " + usuario);
                    }
                } else {
                    System.out.println("❌ Usuario inactivo: " + usuario);
                }
            } else {
                System.out.println("❌ Usuario no encontrado: " + usuario);
            }
            
            // Fallback para pruebas - crear usuario admin si no existe en BD
            if ("admin".equals(usuario) && "admin".equals(password)) {
                System.out.println("⚠️ Usando usuario admin por defecto (para pruebas)");
                Usuario usuarioAdmin = new Usuario();
                usuarioAdmin.setUsuId(1);
                usuarioAdmin.setUsuNombre("Administrador del Sistema");
                usuarioAdmin.setUsuUsuario("admin");
                usuarioAdmin.setUsuRol(Usuario.Rol.Jefe_Informatica);
                usuarioAdmin.setActivo(true);
                return usuarioAdmin;
            }
            
            return null;
            
        } catch (Exception e) {
            System.err.println("Error durante autenticación: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = md.digest(password.getBytes());
            
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
            
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error al generar hash de contraseña", e);
        }
    }
    
    private void abrirVentanaPrincipal(Usuario usuario) {
        SwingUtilities.invokeLater(() -> {
            MainWindowNew mainWindow = new MainWindowNew(usuario);
            mainWindow.setVisible(true);
        });
    }
    
    private void mostrarError(String mensaje) {
        lblStatus.setText(mensaje);
        lblStatus.setForeground(Color.RED);
    }
    
    private void mostrarInfo(String mensaje) {
        lblStatus.setText(mensaje);
        lblStatus.setForeground(COLOR_GRIS_TEXTO);
    }
    
    private void mostrarExito(String mensaje) {
        lblStatus.setText(mensaje);
        lblStatus.setForeground(COLOR_VERDE_COOPERATIVA);
    }
    
    public static void main(String[] args) {
        // Configurar Look and Feel
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> {
            new LoginWindowNew().setVisible(true);
        });
    }
}
