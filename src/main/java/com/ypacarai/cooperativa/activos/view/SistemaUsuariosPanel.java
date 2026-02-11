package com.ypacarai.cooperativa.activos.view;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

import com.ypacarai.cooperativa.activos.dao.UsuarioDAO;
import com.ypacarai.cooperativa.activos.model.Usuario;
import com.ypacarai.cooperativa.activos.service.GestionUsuariosService;
import com.ypacarai.cooperativa.activos.util.ControlAccesoRoles;

/**
 * Panel completo de gestión de usuarios con diseño similar al sistema de tickets
 * Incluye: CRUD completo, validaciones mejoradas, interfaz intuitiva
 * Sistema de Gestión de Activos - Cooperativa Ypacaraí LTDA
 */
public class SistemaUsuariosPanel extends JPanel {
    
    // Colores del tema
    private static final Color VERDE_PRINCIPAL = new Color(34, 139, 34);
    private static final Color VERDE_SECUNDARIO = new Color(40, 167, 69);
    private static final Color AZUL_INFO = new Color(70, 130, 180);
    private static final Color ROJO_DANGER = new Color(220, 53, 69);
    private static final Color NARANJA_WARNING = new Color(255, 193, 7);
    private static final Color GRIS_CLARO = new Color(248, 249, 250);
    private static final Color GRIS_OSCURO = new Color(64, 64, 64);
    
    // Patrones de validación
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9._-]{3,20}$");
    
    // Componentes principales
    private final Usuario usuarioActual;
    private CardLayout cardLayout;
    private JPanel panelContenedor;
    
    // Panel principal de tabla
    private JTable tablaUsuarios;
    private DefaultTableModel modeloTabla;
    private TableRowSorter<DefaultTableModel> sorter;
    
    // Filtros de búsqueda  
    private JTextField txtBusqueda;
    private JComboBox<Usuario.Rol> cmbFiltroRol;
    private JComboBox<String> cmbFiltroEstado;
    
    // Botones de acción
    private JButton btnNuevoUsuario;
    private JButton btnEditarUsuario;
    private JButton btnEliminarUsuario;
    private JButton btnActualizar;
    
    // Panel de formulario de usuario
    private JTextField txtNombre;
    private JTextField txtUsuario;
    private JPasswordField txtPassword;
    private JPasswordField txtConfirmarPassword;
    private JTextField txtEmail;
    private JComboBox<Usuario.Rol> cmbRol;
    private JLabel lblTituloFormulario;
    private JLabel lblValidacionNombre;
    private JLabel lblValidacionUsuario;
    private JLabel lblValidacionPassword;
    private JLabel lblValidacionEmail;
    
    // Servicios
    private GestionUsuariosService gestionUsuariosService;
    private UsuarioDAO usuarioDAO;
    
    // Estado del formulario
    private Usuario usuarioEnEdicion = null;
    
    public SistemaUsuariosPanel(Usuario usuarioActual) {
        this.usuarioActual = usuarioActual;
        
        inicializarServicios();
        configurarPanel();
        crearInterfaz();
        cargarDatosIniciales();
    }
    
    private void inicializarServicios() {
        try {
            this.gestionUsuariosService = new GestionUsuariosService();
            this.usuarioDAO = new UsuarioDAO();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error al inicializar servicios: " + e.getMessage(),
                "Error del Sistema",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void configurarPanel() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(10, 10, 10, 10));
        this.cardLayout = new CardLayout();
        this.panelContenedor = new JPanel(cardLayout);
    }
    
    private void crearInterfaz() {
        // Panel de listado
        JPanel panelListado = crearPanelListado();
        panelContenedor.add(panelListado, "LISTADO");
        
        // Panel de formulario
        JPanel panelFormulario = crearPanelFormulario();
        panelContenedor.add(panelFormulario, "FORMULARIO");
        
        add(panelContenedor, BorderLayout.CENTER);
        
        // Mostrar listado por defecto
        cardLayout.show(panelContenedor, "LISTADO");
    }
    
    private JPanel crearPanelListado() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Título con estadísticas
        JPanel panelTitulo = crearPanelTitulo();
        panel.add(panelTitulo, BorderLayout.NORTH);
        
        // Panel central con filtros y tabla
        JPanel panelCentral = new JPanel(new BorderLayout());
        
        // Panel de filtros
        JPanel panelFiltros = crearPanelFiltros();
        panelCentral.add(panelFiltros, BorderLayout.NORTH);
        
        // Tabla de usuarios
        JPanel panelTabla = crearPanelTabla();
        panelCentral.add(panelTabla, BorderLayout.CENTER);
        
        panel.add(panelCentral, BorderLayout.CENTER);
        
        // Panel de botones de acción
        JPanel panelBotones = crearPanelBotones();
        panel.add(panelBotones, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel crearPanelTitulo() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(VERDE_PRINCIPAL);
        panel.setBorder(new EmptyBorder(20, 25, 20, 25));
        
        JLabel lblTitulo = new JLabel("👥 Gestión de Usuarios");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitulo.setForeground(Color.WHITE);
        
        JLabel lblSubtitulo = new JLabel("Administración integral de usuarios del sistema");
        lblSubtitulo.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        lblSubtitulo.setForeground(Color.WHITE);
        
        JPanel panelTextos = new JPanel(new BorderLayout());
        panelTextos.setOpaque(false);
        panelTextos.add(lblTitulo, BorderLayout.NORTH);
        panelTextos.add(lblSubtitulo, BorderLayout.SOUTH);

        panel.add(panelTextos, BorderLayout.WEST);
        
        // Panel de estadísticas rápidas
        JPanel panelEstadisticas = crearPanelEstadisticas();
        panel.add(panelEstadisticas, BorderLayout.EAST);

        return panel;
    }
    
    private JPanel crearPanelEstadisticas() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        panel.setOpaque(false);
        
        try {
            List<Usuario> usuarios = usuarioDAO.findAll();
            long usuariosActivos = usuarios.stream().filter(Usuario::isActivo).count();
            long usuariosInactivos = usuarios.size() - usuariosActivos;
            
            // Total de usuarios
            JLabel lblTotal = crearEtiquetaEstadistica("👥", String.valueOf(usuarios.size()), "Total");
            panel.add(lblTotal);
            
            // Usuarios activos
            JLabel lblActivos = crearEtiquetaEstadistica("✅", String.valueOf(usuariosActivos), "Activos");
            panel.add(lblActivos);
            
            // Usuarios inactivos
            if (usuariosInactivos > 0) {
                JLabel lblInactivos = crearEtiquetaEstadistica("❌", String.valueOf(usuariosInactivos), "Inactivos");
                panel.add(lblInactivos);
            }
            
        } catch (Exception e) {
            JLabel lblError = crearEtiquetaEstadistica("⚠️", "Error", "Al cargar datos");
            panel.add(lblError);
        }
        
        return panel;
    }
    
    private JLabel crearEtiquetaEstadistica(String icono, String numero, String descripcion) {
        JLabel label = new JLabel(String.format(
            "<html><div style='text-align: center; color: white;'>" +
            "<div style='font-size: 11px;'>%s %s</div>" +
            "<div style='font-size: 18px; font-weight: bold;'>%s</div>" +
            "</div></html>", 
            icono, descripcion, numero));
        label.setForeground(Color.WHITE);
        label.setBorder(new EmptyBorder(5, 10, 5, 10));
        return label;
    }
    
    private JPanel crearPanelFiltros() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(GRIS_CLARO);
        panel.setBorder(new EmptyBorder(15, 20, 15, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 10);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Campo de búsqueda
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("🔍 Buscar:"), gbc);
        
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        txtBusqueda = new JTextField(20);
        txtBusqueda.setToolTipText("Buscar por nombre, usuario o email");
        txtBusqueda.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent evt) {
                aplicarFiltros();
            }
        });
        panel.add(txtBusqueda, gbc);

        gbc.gridx = 2; gbc.weightx = 0; gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("👑 Rol:"), gbc);
        
        gbc.gridx = 3;
        cmbFiltroRol = new JComboBox<>();
        cmbFiltroRol.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value == null) {
                    setText("-- Todos los roles --");
                }
                return this;
            }
        });
        cmbFiltroRol.addItem(null); // Opción "Todos"
        for (Usuario.Rol rol : Usuario.Rol.values()) {
            cmbFiltroRol.addItem(rol);
        }
        cmbFiltroRol.addActionListener(e -> aplicarFiltros());
        panel.add(cmbFiltroRol, gbc);

        gbc.gridx = 4;
        panel.add(new JLabel("📊 Estado:"), gbc);
        
        gbc.gridx = 5;
        cmbFiltroEstado = new JComboBox<>(new String[]{"Todos", "Activos", "Inactivos"});
        cmbFiltroEstado.addActionListener(e -> aplicarFiltros());
        panel.add(cmbFiltroEstado, gbc);
        
        return panel;
    }
    
    private JPanel crearPanelTabla() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Crear modelo de tabla
        String[] columnas = {"ID", "Nombre", "Usuario", "Email", "Rol", "Estado", "Fecha Creación"};
        
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Tabla de solo lectura
            }
        };
        
        tablaUsuarios = new JTable(modeloTabla);
        tablaUsuarios.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaUsuarios.setRowHeight(28);
        tablaUsuarios.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        tablaUsuarios.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 11));
        tablaUsuarios.getTableHeader().setBackground(AZUL_INFO);
        tablaUsuarios.getTableHeader().setForeground(Color.WHITE);
        
        // Configurar anchos de columnas
        tablaUsuarios.getColumnModel().getColumn(0).setMaxWidth(60);   // ID
        tablaUsuarios.getColumnModel().getColumn(1).setPreferredWidth(200); // Nombre
        tablaUsuarios.getColumnModel().getColumn(2).setPreferredWidth(120); // Usuario
        tablaUsuarios.getColumnModel().getColumn(3).setPreferredWidth(200); // Email
        tablaUsuarios.getColumnModel().getColumn(4).setPreferredWidth(100); // Rol
        tablaUsuarios.getColumnModel().getColumn(5).setPreferredWidth(80);  // Estado
        tablaUsuarios.getColumnModel().getColumn(6).setPreferredWidth(120); // Fecha
        
        // Configurar renderers personalizados
        tablaUsuarios.getColumnModel().getColumn(4).setCellRenderer(new RolCellRenderer());
        tablaUsuarios.getColumnModel().getColumn(5).setCellRenderer(new EstadoCellRenderer());
        
        // Configurar sorter
        sorter = new TableRowSorter<>(modeloTabla);
        tablaUsuarios.setRowSorter(sorter);
        
        // Listener para selección
        tablaUsuarios.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                actualizarBotones();
            }
        });
        
        // Doble clic para editar
        tablaUsuarios.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && tablaUsuarios.getSelectedRow() != -1) {
                    editarUsuario();
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(tablaUsuarios);
        scrollPane.setBorder(new TitledBorder("Usuarios Registrados"));
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel crearPanelBotones() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panel.setBackground(GRIS_CLARO);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        btnNuevoUsuario = crearBoton("➕ Nuevo Usuario", VERDE_PRINCIPAL, e -> nuevoUsuario());
        btnEditarUsuario = crearBoton("✏️ Editar Usuario", AZUL_INFO, e -> editarUsuario());
        btnEliminarUsuario = crearBoton("🗑️ Eliminar Usuario", ROJO_DANGER, e -> eliminarUsuario());
        btnActualizar = crearBoton("🔄 Actualizar Lista", VERDE_SECUNDARIO, e -> actualizarTabla());
        
        panel.add(btnNuevoUsuario);
        panel.add(btnEditarUsuario);
        panel.add(btnEliminarUsuario);
        panel.add(Box.createHorizontalStrut(20));
        panel.add(btnActualizar);
        
        // Verificar permisos iniciales
        actualizarBotones();
        
        return panel;
    }
    
    private JButton crearBoton(String texto, Color color, ActionListener action) {
        JButton boton = new JButton(texto);
        boton.setBackground(color);
        boton.setForeground(Color.WHITE);
        boton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        boton.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        boton.setFocusPainted(false);
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        boton.addActionListener(action);
        
        // Efecto hover
        boton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (boton.isEnabled()) {
                    boton.setBackground(color.brighter());
                }
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                boton.setBackground(color);
            }
        });
        
        return boton;
    }
    
    // Renderers personalizados para la tabla
    
    private class RolCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, 
                boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            
            if (value != null && value instanceof Usuario.Rol) {
                Usuario.Rol rol = (Usuario.Rol) value;
                setText(rol.toString().replace("_", " "));
                
                // Colores según el rol
                if (!isSelected) {
                    switch (rol) {
                        case Jefe_Informatica:
                            setBackground(new Color(255, 243, 205));
                            setForeground(new Color(133, 100, 4));
                            break;
                        case Tecnico:
                            setBackground(new Color(217, 237, 247));
                            setForeground(new Color(31, 81, 104));
                            break;
                        case Consulta:
                            setBackground(new Color(230, 245, 233));
                            setForeground(new Color(21, 87, 36));
                            break;
                    }
                } else {
                    setBackground(table.getSelectionBackground());
                    setForeground(table.getSelectionForeground());
                }
            }
            
            setHorizontalAlignment(CENTER);
            return this;
        }
    }
    
    private class EstadoCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, 
                boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            
            if (value != null) {
                String estado = value.toString();
                if (estado.contains("Activo")) {
                    setText("✅ Activo");
                    if (!isSelected) {
                        setBackground(new Color(212, 237, 218));
                        setForeground(new Color(21, 87, 36));
                    }
                } else {
                    setText("❌ Inactivo");
                    if (!isSelected) {
                        setBackground(new Color(248, 215, 218));
                        setForeground(new Color(114, 28, 36));
                    }
                }
                
                if (isSelected) {
                    setBackground(table.getSelectionBackground());
                    setForeground(table.getSelectionForeground());
                }
            }
            
            setHorizontalAlignment(CENTER);
            return this;
        }
    }
    
    // Panel de formulario para crear/editar usuarios
    
    private JPanel crearPanelFormulario() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Título dinámico
        JPanel panelTitulo = new JPanel(new BorderLayout());
        panelTitulo.setBackground(VERDE_PRINCIPAL);
        panelTitulo.setBorder(new EmptyBorder(15, 20, 15, 20));
        
        lblTituloFormulario = new JLabel("➕ NUEVO USUARIO");
        lblTituloFormulario.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTituloFormulario.setForeground(Color.WHITE);
        panelTitulo.add(lblTituloFormulario, BorderLayout.WEST);
        
        panel.add(panelTitulo, BorderLayout.NORTH);
        
        // Formulario principal
        JPanel formulario = crearFormularioUsuario();
        JScrollPane scrollFormulario = new JScrollPane(formulario);
        scrollFormulario.setBorder(null);
        panel.add(scrollFormulario, BorderLayout.CENTER);
        
        // Botones del formulario
        JPanel panelBotonesForm = crearPanelBotonesFormulario();
        panel.add(panelBotonesForm, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel crearFormularioUsuario() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(30, 40, 30, 40));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 5, 10);
        gbc.anchor = GridBagConstraints.WEST;
        
        int fila = 0;
        
        // Nombre completo
        gbc.gridy = fila++; gbc.gridx = 0;
        gbc.weightx = 0; gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("👤 Nombre Completo:"), gbc);
        
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        txtNombre = new JTextField(25);
        txtNombre.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtNombre.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                validarNombre();
            }
        });
        panel.add(txtNombre, gbc);
        
        // Label de validación para nombre
        gbc.gridy = fila++; gbc.gridx = 1;
        lblValidacionNombre = new JLabel(" ");
        lblValidacionNombre.setFont(new Font("Segoe UI", Font.ITALIC, 10));
        panel.add(lblValidacionNombre, gbc);
        
        // Nombre de usuario
        gbc.gridy = fila++; gbc.gridx = 0;
        gbc.weightx = 0; gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("🔑 Nombre de Usuario:"), gbc);
        
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        txtUsuario = new JTextField(25);
        txtUsuario.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtUsuario.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                validarUsuario();
            }
        });
        panel.add(txtUsuario, gbc);
        
        // Label de validación para usuario
        gbc.gridy = fila++; gbc.gridx = 1;
        lblValidacionUsuario = new JLabel(" ");
        lblValidacionUsuario.setFont(new Font("Segoe UI", Font.ITALIC, 10));
        panel.add(lblValidacionUsuario, gbc);
        
        // Contraseña
        gbc.gridy = fila++; gbc.gridx = 0;
        gbc.weightx = 0; gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("🔒 Contraseña:"), gbc);
        
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        txtPassword = new JPasswordField(25);
        txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtPassword.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                validarPassword();
            }
        });
        panel.add(txtPassword, gbc);
        
        // Confirmar contraseña
        gbc.gridy = fila++; gbc.gridx = 0;
        gbc.weightx = 0; gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("🔒 Confirmar Contraseña:"), gbc);
        
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        txtConfirmarPassword = new JPasswordField(25);
        txtConfirmarPassword.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtConfirmarPassword.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                validarPassword();
            }
        });
        panel.add(txtConfirmarPassword, gbc);
        
        // Label de validación para contraseña
        gbc.gridy = fila++; gbc.gridx = 1;
        lblValidacionPassword = new JLabel(" ");
        lblValidacionPassword.setFont(new Font("Segoe UI", Font.ITALIC, 10));
        panel.add(lblValidacionPassword, gbc);
        
        // Email
        gbc.gridy = fila++; gbc.gridx = 0;
        gbc.weightx = 0; gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("📧 Correo Electrónico:"), gbc);
        
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        txtEmail = new JTextField(25);
        txtEmail.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtEmail.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                validarEmail();
            }
        });
        panel.add(txtEmail, gbc);
        
        // Label de validación para email
        gbc.gridy = fila++; gbc.gridx = 1;
        lblValidacionEmail = new JLabel(" ");
        lblValidacionEmail.setFont(new Font("Segoe UI", Font.ITALIC, 10));
        panel.add(lblValidacionEmail, gbc);
        
        // Rol del usuario
        gbc.gridy = fila++; gbc.gridx = 0;
        gbc.weightx = 0; gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("👑 Rol del Usuario:"), gbc);
        
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        cmbRol = new JComboBox<>(Usuario.Rol.values());
        cmbRol.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        panel.add(cmbRol, gbc);
        
        // Información adicional
        gbc.gridy = fila++; gbc.gridx = 0; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(20, 10, 10, 10);
        
        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setBackground(new Color(217, 237, 247));
        infoPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(174, 213, 235)),
            new EmptyBorder(15, 20, 15, 20)
        ));
        
        JLabel lblInfo = new JLabel("<html>" +
            "💡 <b>Información importante:</b><br>" +
            "• Los campos marcados con validación en tiempo real se verifican automáticamente<br>" +
            "• El nombre de usuario debe ser único en el sistema<br>" +
            "• La contraseña debe tener al menos 6 caracteres<br>" +
            "• Asegúrese de que el correo electrónico sea válido" +
            "</html>");
        lblInfo.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        infoPanel.add(lblInfo, BorderLayout.CENTER);
        panel.add(infoPanel, gbc);
        
        return panel;
    }
    
    private JPanel crearPanelBotonesFormulario() {
        JPanel panel = new JPanel(new FlowLayout());
        panel.setBackground(GRIS_CLARO);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        JButton btnCancelar = crearBoton("❌ Cancelar", ROJO_DANGER, e -> cancelarFormulario());
        JButton btnGuardar = crearBoton("💾 Guardar Usuario", VERDE_PRINCIPAL, e -> guardarUsuario());
        
        panel.add(btnCancelar);
        panel.add(btnGuardar);
        
        return panel;
    }
    
    // Métodos de validación en tiempo real
    
    private void validarNombre() {
        String nombre = txtNombre.getText().trim();
        if (nombre.isEmpty()) {
            mostrarValidacion(lblValidacionNombre, "❌ El nombre es obligatorio", ROJO_DANGER);
        } else if (nombre.length() < 2) {
            mostrarValidacion(lblValidacionNombre, "⚠️ El nombre debe tener al menos 2 caracteres", NARANJA_WARNING);
        } else {
            mostrarValidacion(lblValidacionNombre, "✅ Nombre válido", VERDE_PRINCIPAL);
        }
    }
    
    private void validarUsuario() {
        String usuario = txtUsuario.getText().trim();
        if (usuario.isEmpty()) {
            mostrarValidacion(lblValidacionUsuario, "❌ El nombre de usuario es obligatorio", ROJO_DANGER);
        } else if (!USERNAME_PATTERN.matcher(usuario).matches()) {
            mostrarValidacion(lblValidacionUsuario, "⚠️ Solo letras, números, punto, guión y guión bajo (3-20 caracteres)", NARANJA_WARNING);
        } else {
            // Verificar si el usuario ya existe (solo al crear)
            if (usuarioEnEdicion == null) {
                try {
                    if (usuarioDAO.findByUsername(usuario).isPresent()) {
                        mostrarValidacion(lblValidacionUsuario, "❌ Este nombre de usuario ya existe", ROJO_DANGER);
                    } else {
                        mostrarValidacion(lblValidacionUsuario, "✅ Nombre de usuario disponible", VERDE_PRINCIPAL);
                    }
                } catch (Exception e) {
                    mostrarValidacion(lblValidacionUsuario, "⚠️ No se pudo verificar disponibilidad", NARANJA_WARNING);
                }
            } else {
                mostrarValidacion(lblValidacionUsuario, "✅ Formato válido", VERDE_PRINCIPAL);
            }
        }
    }
    
    private void validarPassword() {
        String password = new String(txtPassword.getPassword());
        String confirmar = new String(txtConfirmarPassword.getPassword());
        
        if (usuarioEnEdicion != null && password.isEmpty()) {
            // En modo edición, contraseña vacía significa mantener la actual
            mostrarValidacion(lblValidacionPassword, "ℹ️ Dejar vacío para mantener la contraseña actual", AZUL_INFO);
            return;
        }
        
        if (password.isEmpty()) {
            mostrarValidacion(lblValidacionPassword, "❌ La contraseña es obligatoria", ROJO_DANGER);
        } else if (password.length() < 6) {
            mostrarValidacion(lblValidacionPassword, "⚠️ La contraseña debe tener al menos 6 caracteres", NARANJA_WARNING);
        } else if (!confirmar.isEmpty() && !password.equals(confirmar)) {
            mostrarValidacion(lblValidacionPassword, "❌ Las contraseñas no coinciden", ROJO_DANGER);
        } else if (!confirmar.isEmpty() && password.equals(confirmar)) {
            mostrarValidacion(lblValidacionPassword, "✅ Contraseñas válidas y coinciden", VERDE_PRINCIPAL);
        } else if (confirmar.isEmpty()) {
            mostrarValidacion(lblValidacionPassword, "⚠️ Confirme la contraseña", NARANJA_WARNING);
        }
    }
    
    private void validarEmail() {
        String email = txtEmail.getText().trim();
        if (email.isEmpty()) {
            mostrarValidacion(lblValidacionEmail, "❌ El correo electrónico es obligatorio", ROJO_DANGER);
        } else if (!EMAIL_PATTERN.matcher(email).matches()) {
            mostrarValidacion(lblValidacionEmail, "⚠️ Formato de correo electrónico inválido", NARANJA_WARNING);
        } else {
            mostrarValidacion(lblValidacionEmail, "✅ Correo electrónico válido", VERDE_PRINCIPAL);
        }
    }
    
    private void mostrarValidacion(JLabel label, String mensaje, Color color) {
        label.setText(mensaje);
        label.setForeground(color);
    }
    
    // Métodos de funcionalidad
    
    private void cargarDatosIniciales() {
        try {
            actualizarTabla();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error al cargar datos iniciales: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void actualizarTabla() {
        try {
            List<Usuario> usuarios = usuarioDAO.findAll();
            
            // Limpiar tabla
            modeloTabla.setRowCount(0);
            
            // Cargar datos
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            
            for (Usuario usuario : usuarios) {
                Object[] fila = {
                    usuario.getUsuId(),
                    usuario.getUsuNombre(),
                    usuario.getUsuUsuario(),
                    usuario.getUsuEmail(),
                    usuario.getUsuRol(),
                    usuario.isActivo() ? "✅ Activo" : "❌ Inactivo",
                    usuario.getCreadoEn() != null ? usuario.getCreadoEn().format(formatter) : "N/A"
                };
                modeloTabla.addRow(fila);
            }
            
            // Actualizar estadísticas en el título
            actualizarEstadisticas();
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error al actualizar la tabla: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void actualizarEstadisticas() {
        // No recrear toda la interfaz, solo actualizar las estadísticas si es necesario
        // Por ahora, omitimos la actualización automática de estadísticas
        // Las estadísticas se actualizarán la próxima vez que se acceda al panel
    }
    
    private void aplicarFiltros() {
        if (sorter == null) return;
        
        java.util.List<RowFilter<Object, Object>> filtros = new java.util.ArrayList<>();
        
        // Filtro de búsqueda por texto
        String textoBusqueda = txtBusqueda.getText().trim();
        if (!textoBusqueda.isEmpty()) {
            filtros.add(RowFilter.regexFilter("(?i)" + textoBusqueda, 1, 2, 3)); // Nombre, Usuario, Email
        }
        
        // Filtro por rol
        Usuario.Rol rolSeleccionado = (Usuario.Rol) cmbFiltroRol.getSelectedItem();
        if (rolSeleccionado != null) {
            filtros.add(RowFilter.regexFilter(rolSeleccionado.toString(), 4)); // Columna Rol
        }
        
        // Filtro por estado
        String estadoSeleccionado = (String) cmbFiltroEstado.getSelectedItem();
        if (estadoSeleccionado != null && !"Todos".equals(estadoSeleccionado)) {
            if ("Activos".equals(estadoSeleccionado)) {
                filtros.add(RowFilter.regexFilter("Activo", 5)); // Columna Estado
            } else if ("Inactivos".equals(estadoSeleccionado)) {
                filtros.add(RowFilter.regexFilter("Inactivo", 5)); // Columna Estado
            }
        }
        
        // Aplicar todos los filtros
        if (filtros.isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.andFilter(filtros));
        }
    }
    
    private void actualizarBotones() {
        boolean haySeleccion = tablaUsuarios.getSelectedRow() != -1;
        boolean puedeCrear = ControlAccesoRoles.tienePermiso(usuarioActual, ControlAccesoRoles.Permiso.CREAR_USUARIOS);
        
        btnNuevoUsuario.setEnabled(puedeCrear);
        btnEditarUsuario.setEnabled(haySeleccion && puedeCrear);
        btnEliminarUsuario.setEnabled(haySeleccion && puedeCrear);
        
        // Tooltips informativos
        if (!puedeCrear) {
            btnNuevoUsuario.setToolTipText("No tiene permisos para gestionar usuarios");
            btnEditarUsuario.setToolTipText("No tiene permisos para editar usuarios");
            btnEliminarUsuario.setToolTipText("No tiene permisos para eliminar usuarios");
        } else {
            btnNuevoUsuario.setToolTipText("Crear un nuevo usuario");
            btnEditarUsuario.setToolTipText(haySeleccion ? "Editar usuario seleccionado" : "Seleccione un usuario para editar");
            btnEliminarUsuario.setToolTipText(haySeleccion ? "Eliminar usuario seleccionado" : "Seleccione un usuario para eliminar");
        }
    }
    
    // Métodos CRUD principales
    
    private void nuevoUsuario() {
        limpiarFormulario();
        lblTituloFormulario.setText("➕ NUEVO USUARIO");
        usuarioEnEdicion = null;
        
        // Configurar campos para creación
        txtUsuario.setEnabled(true);
        txtPassword.setToolTipText("Ingrese la contraseña del nuevo usuario");
        txtConfirmarPassword.setToolTipText("Confirme la contraseña");
        
        cardLayout.show(panelContenedor, "FORMULARIO");
        
        // Enfocar el primer campo
        txtNombre.requestFocus();
    }
    
    private void editarUsuario() {
        int filaSeleccionada = tablaUsuarios.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this,
                "Por favor seleccione un usuario para editar.",
                "Selección Requerida",
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        try {
            // Obtener ID del usuario seleccionado
            int usuarioId = (Integer) tablaUsuarios.getValueAt(filaSeleccionada, 0);
            
            // Buscar usuario completo
            java.util.Optional<Usuario> usuarioOpt = usuarioDAO.findById(usuarioId);
            if (!usuarioOpt.isPresent()) {
                JOptionPane.showMessageDialog(this,
                    "Error: No se pudo encontrar el usuario seleccionado.",
                    "Usuario No Encontrado",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            usuarioEnEdicion = usuarioOpt.get();
            
            // Cargar datos en el formulario
            cargarUsuarioEnFormulario(usuarioEnEdicion);
            
            lblTituloFormulario.setText("✏️ EDITAR USUARIO");
            
            // Configurar campos para edición
            txtUsuario.setEnabled(false); // No se puede cambiar el username
            txtUsuario.setBackground(GRIS_CLARO);
            txtPassword.setToolTipText("Dejar vacío para mantener la contraseña actual");
            txtConfirmarPassword.setToolTipText("Solo necesario si cambia la contraseña");
            
            cardLayout.show(panelContenedor, "FORMULARIO");
            
            // Enfocar el primer campo editable
            txtNombre.requestFocus();
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error al cargar datos del usuario: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void eliminarUsuario() {
        int filaSeleccionada = tablaUsuarios.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this,
                "Por favor seleccione un usuario para eliminar.",
                "Selección Requerida",
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        // Obtener datos del usuario a eliminar
        int usuarioId = (Integer) tablaUsuarios.getValueAt(filaSeleccionada, 0);
        String nombreUsuario = (String) tablaUsuarios.getValueAt(filaSeleccionada, 1);
        String username = (String) tablaUsuarios.getValueAt(filaSeleccionada, 2);
        
        // Prevenir auto-eliminación
        if (usuarioId == usuarioActual.getUsuId()) {
            JOptionPane.showMessageDialog(this,
                "No puede eliminar su propia cuenta de usuario.",
                "Operación No Permitida",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Confirmar eliminación
        int confirmacion = JOptionPane.showConfirmDialog(this,
            String.format(
                "<html><div style='text-align: center;'>" +
                "<h3>⚠️ Confirmar Eliminación</h3>" +
                "<p>¿Está seguro que desea eliminar este usuario?</p><br>" +
                "<b>👤 Nombre:</b> %s<br>" +
                "<b>🔑 Usuario:</b> %s<br><br>" +
                "<p style='color: red;'><b>⚠️ Esta acción no se puede deshacer.</b></p>" +
                "</div></html>",
                nombreUsuario, username
            ),
            "Confirmar Eliminación",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (confirmacion != JOptionPane.YES_OPTION) {
            return;
        }
        
        try {
            // Eliminar usuario
            boolean eliminado = usuarioDAO.delete(usuarioId);
            
            if (eliminado) {
                JOptionPane.showMessageDialog(this,
                    "✅ Usuario eliminado exitosamente.",
                    "Eliminación Exitosa",
                    JOptionPane.INFORMATION_MESSAGE);
                actualizarTabla(); // Actualizar tabla
            } else {
                JOptionPane.showMessageDialog(this,
                    "❌ Error al eliminar el usuario. Verifique que no tenga registros asociados.",
                    "Error de Eliminación",
                    JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "❌ Error al eliminar usuario: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void guardarUsuario() {
        if (!validarFormularioCompleto()) {
            return;
        }
        
        try {
            boolean esNuevoUsuario = (usuarioEnEdicion == null);
            String mensaje = esNuevoUsuario ? "Creando usuario..." : "Actualizando usuario...";
            
            // Aquí podrías agregar un indicador de progreso
            
            if (esNuevoUsuario) {
                crearNuevoUsuario();
            } else {
                actualizarUsuarioExistente();
            }
            
            // Volver al listado y actualizar
            cardLayout.show(panelContenedor, "LISTADO");
            actualizarTabla();
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "❌ Error al guardar el usuario: " + e.getMessage(),
                "Error de Guardado",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void crearNuevoUsuario() throws Exception {
        String nombre = txtNombre.getText().trim();
        String username = txtUsuario.getText().trim();
        String password = new String(txtPassword.getPassword());
        String email = txtEmail.getText().trim();
        Usuario.Rol rol = (Usuario.Rol) cmbRol.getSelectedItem();
        
        var resultado = gestionUsuariosService.crearUsuario(
            nombre, username, password, email, rol, usuarioActual.getUsuId()
        );
        
        if (resultado.isExitoso()) {
            JOptionPane.showMessageDialog(this,
                "✅ Usuario creado exitosamente.",
                "Creación Exitosa",
                JOptionPane.INFORMATION_MESSAGE);
        } else {
            throw new Exception(resultado.getMensaje());
        }
    }
    
    private void actualizarUsuarioExistente() throws Exception {
        usuarioEnEdicion.setUsuNombre(txtNombre.getText().trim());
        usuarioEnEdicion.setUsuEmail(txtEmail.getText().trim());
        usuarioEnEdicion.setUsuRol((Usuario.Rol) cmbRol.getSelectedItem());
        usuarioEnEdicion.setActualizadoEn(LocalDateTime.now());
        
        // Solo actualizar contraseña si se proporcionó una nueva
        String nuevaPassword = new String(txtPassword.getPassword()).trim();
        if (!nuevaPassword.isEmpty()) {
            usuarioEnEdicion.setUsuPassword(gestionUsuariosService.hashPassword(nuevaPassword));
        }
        
        boolean actualizado = usuarioDAO.update(usuarioEnEdicion);
        
        if (actualizado) {
            JOptionPane.showMessageDialog(this,
                "✅ Usuario actualizado exitosamente.",
                "Actualización Exitosa",
                JOptionPane.INFORMATION_MESSAGE);
        } else {
            throw new Exception("No se pudo actualizar el usuario en la base de datos");
        }
    }
    
    private void cancelarFormulario() {
        cardLayout.show(panelContenedor, "LISTADO");
        usuarioEnEdicion = null;
    }
    
    // Métodos auxiliares
    
    private void limpiarFormulario() {
        txtNombre.setText("");
        txtUsuario.setText("");
        txtPassword.setText("");
        txtConfirmarPassword.setText("");
        txtEmail.setText("");
        cmbRol.setSelectedIndex(0);
        
        // Limpiar validaciones
        lblValidacionNombre.setText(" ");
        lblValidacionUsuario.setText(" ");
        lblValidacionPassword.setText(" ");
        lblValidacionEmail.setText(" ");
        
        // Restaurar configuración normal
        txtUsuario.setEnabled(true);
        txtUsuario.setBackground(Color.WHITE);
        txtPassword.setToolTipText("Ingrese la contraseña del usuario");
        txtConfirmarPassword.setToolTipText("Confirme la contraseña");
    }
    
    private void cargarUsuarioEnFormulario(Usuario usuario) {
        txtNombre.setText(usuario.getUsuNombre());
        txtUsuario.setText(usuario.getUsuUsuario());
        txtPassword.setText(""); // No mostrar contraseña actual
        txtConfirmarPassword.setText("");
        txtEmail.setText(usuario.getUsuEmail());
        cmbRol.setSelectedItem(usuario.getUsuRol());
        
        // Ejecutar validaciones iniciales
        validarNombre();
        validarUsuario();
        validarEmail();
    }
    
    private boolean validarFormularioCompleto() {
        // Re-ejecutar todas las validaciones
        validarNombre();
        validarUsuario();
        validarPassword();
        validarEmail();
        
        // Verificar si hay errores
        boolean nombreValido = !lblValidacionNombre.getText().contains("❌");
        boolean usuarioValido = !lblValidacionUsuario.getText().contains("❌");
        boolean passwordValido = !lblValidacionPassword.getText().contains("❌");
        boolean emailValido = !lblValidacionEmail.getText().contains("❌");
        
        // Validaciones específicas adicionales
        if (txtNombre.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "❌ El nombre es obligatorio.",
                "Campo Requerido",
                JOptionPane.ERROR_MESSAGE);
            txtNombre.requestFocus();
            return false;
        }
        
        if (txtUsuario.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "❌ El nombre de usuario es obligatorio.",
                "Campo Requerido",
                JOptionPane.ERROR_MESSAGE);
            txtUsuario.requestFocus();
            return false;
        }
        
        if (txtEmail.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "❌ El correo electrónico es obligatorio.",
                "Campo Requerido",
                JOptionPane.ERROR_MESSAGE);
            txtEmail.requestFocus();
            return false;
        }
        
        // Para nuevos usuarios, la contraseña es obligatoria
        if (usuarioEnEdicion == null && new String(txtPassword.getPassword()).trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "❌ La contraseña es obligatoria para nuevos usuarios.",
                "Campo Requerido",
                JOptionPane.ERROR_MESSAGE);
            txtPassword.requestFocus();
            return false;
        }
        
        if (!nombreValido || !usuarioValido || !passwordValido || !emailValido) {
            JOptionPane.showMessageDialog(this,
                "❌ Por favor corrija los errores de validación antes de guardar.",
                "Errores de Validación",
                JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        return true;
    }
}