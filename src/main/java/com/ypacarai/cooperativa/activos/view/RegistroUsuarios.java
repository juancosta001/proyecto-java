package com.ypacarai.cooperativa.activos.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

import com.ypacarai.cooperativa.activos.model.Usuario;
import com.ypacarai.cooperativa.activos.service.GestionUsuariosService;
import com.ypacarai.cooperativa.activos.util.ControlAccesoRoles;

/**
 * Ventana de Listado y Gestión de Usuarios
 * Sistema de Gestión de Activos - Cooperativa Ypacaraí LTDA
 */
public class RegistroUsuarios extends JFrame {
    
    // Colores corporativos
    private static final Color COLOR_VERDE_COOPERATIVA = new Color(34, 139, 34);
    private static final Color COLOR_AZUL_INFO = new Color(70, 130, 180);
    private static final Color COLOR_BLANCO = Color.WHITE;
    private static final Color COLOR_GRIS_FONDO = new Color(248, 249, 250);
    
    // Componentes de la interfaz
    private JTable tablaUsuarios;
    private DefaultTableModel modeloTabla;
    private JTextField txtBuscar;
    private JLabel lblTotal;
    private JButton btnActualizar;
    private JButton btnCrearUsuario;
    
    // Servicios y datos
    private GestionUsuariosService gestionUsuariosService;
    private Usuario usuarioActual;
    private JFrame ventanaPadre;
    
    public RegistroUsuarios(JFrame parent, Usuario usuarioActual) {
        this.ventanaPadre = parent;
        this.usuarioActual = usuarioActual;
        this.gestionUsuariosService = new GestionUsuariosService();
        
        initializeComponents();
        setupLayout();
        setupEventListeners();
        cargarUsuarios();
    }
    
    private void initializeComponents() {
        // Configuración básica de la ventana
        setTitle("📋 Listado de Usuarios - Sistema de Activos");
        setSize(900, 600);
        setLocationRelativeTo(ventanaPadre);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(true);
        
        // Panel principal
        setLayout(new BorderLayout());
        getContentPane().setBackground(COLOR_GRIS_FONDO);
        
        // Crear modelo de tabla
        String[] columnas = {"ID", "Nombre", "Usuario", "Email", "Rol", "Estado"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Tabla de solo lectura
            }
        };
        
        // Crear tabla
        tablaUsuarios = new JTable(modeloTabla);
        tablaUsuarios.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaUsuarios.setRowHeight(25);
        tablaUsuarios.getTableHeader().setBackground(COLOR_VERDE_COOPERATIVA);
        tablaUsuarios.getTableHeader().setForeground(COLOR_BLANCO);
        tablaUsuarios.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        
        // Configurar ancho de columnas
        tablaUsuarios.getColumnModel().getColumn(0).setPreferredWidth(50);  // ID
        tablaUsuarios.getColumnModel().getColumn(1).setPreferredWidth(200); // Nombre
        tablaUsuarios.getColumnModel().getColumn(2).setPreferredWidth(120); // Usuario
        tablaUsuarios.getColumnModel().getColumn(3).setPreferredWidth(200); // Email
        tablaUsuarios.getColumnModel().getColumn(4).setPreferredWidth(120); // Rol
        tablaUsuarios.getColumnModel().getColumn(5).setPreferredWidth(80);  // Estado
        
        // Configurar ordenamiento
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(modeloTabla);
        tablaUsuarios.setRowSorter(sorter);
        
        // Componentes de búsqueda y control
        txtBuscar = new JTextField(20);
        txtBuscar.setToolTipText("Buscar por nombre, usuario o email");
        
        lblTotal = new JLabel("Total: 0 usuarios");
        lblTotal.setFont(new Font("Arial", Font.BOLD, 11));
        
        btnActualizar = createStyledButton("🔄 Actualizar", COLOR_AZUL_INFO);
        btnCrearUsuario = createStyledButton("➕ Crear Usuario", COLOR_VERDE_COOPERATIVA);
        
        // Verificar permisos para crear usuarios
        if (!ControlAccesoRoles.tienePermiso(usuarioActual, ControlAccesoRoles.Permiso.CREAR_USUARIOS)) {
            btnCrearUsuario.setEnabled(false);
            btnCrearUsuario.setToolTipText("No tiene permisos para crear usuarios");
        }
    }
    
    private void setupLayout() {
        // Panel superior (búsqueda y controles)
        JPanel panelSuperior = new JPanel(new BorderLayout());
        panelSuperior.setBorder(new EmptyBorder(15, 15, 10, 15));
        panelSuperior.setBackground(COLOR_GRIS_FONDO);
        
        // Panel de búsqueda
        JPanel panelBusqueda = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelBusqueda.setBackground(COLOR_GRIS_FONDO);
        panelBusqueda.add(new JLabel("🔍 Buscar:"));
        panelBusqueda.add(txtBuscar);
        panelBusqueda.add(Box.createHorizontalStrut(20));
        panelBusqueda.add(lblTotal);
        
        // Panel de botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelBotones.setBackground(COLOR_GRIS_FONDO);
        panelBotones.add(btnActualizar);
        panelBotones.add(Box.createHorizontalStrut(10));
        panelBotones.add(btnCrearUsuario);
        
        panelSuperior.add(panelBusqueda, BorderLayout.WEST);
        panelSuperior.add(panelBotones, BorderLayout.EAST);
        
        // Panel central (tabla)
        JScrollPane scrollPane = new JScrollPane(tablaUsuarios);
        scrollPane.setBorder(BorderFactory.createLoweredBevelBorder());
        scrollPane.getViewport().setBackground(COLOR_BLANCO);
        
        // Panel inferior (información adicional)
        JPanel panelInferior = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelInferior.setBorder(new EmptyBorder(5, 15, 15, 15));
        panelInferior.setBackground(COLOR_GRIS_FONDO);
        
        JLabel lblInfo = new JLabel("💡 Doble clic en una fila para ver detalles del usuario");
        lblInfo.setFont(new Font("Arial", Font.ITALIC, 11));
        lblInfo.setForeground(Color.GRAY);
        panelInferior.add(lblInfo);
        
        // Ensamblar layout
        add(panelSuperior, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(panelInferior, BorderLayout.SOUTH);
    }
    
    private void setupEventListeners() {
        // Botón actualizar
        btnActualizar.addActionListener(e -> cargarUsuarios());
        
        // Botón crear usuario
        btnCrearUsuario.addActionListener(e -> {
            CrearUsuarioWindow crearUsuario = new CrearUsuarioWindow(this, usuarioActual);
            crearUsuario.setVisible(true);
        });
        
        // Búsqueda en tiempo real
        txtBuscar.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent e) {
                filtrarUsuarios();
            }
        });
        
        // Doble clic para ver detalles
        tablaUsuarios.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    mostrarDetallesUsuario();
                }
            }
        });
    }
    
    private void cargarUsuarios() {
        try {
            // Mostrar indicador de carga
            lblTotal.setText("Cargando usuarios...");
            
            SwingUtilities.invokeLater(() -> {
                try {
                    List<Usuario> usuarios = gestionUsuariosService.obtenerTodosLosUsuarios();
                    
                    // Limpiar tabla
                    modeloTabla.setRowCount(0);
                    
                    // Cargar datos
                    for (Usuario usuario : usuarios) {
                        Object[] fila = {
                            usuario.getUsuId(),
                            usuario.getUsuNombre(),
                            usuario.getUsuUsuario(),
                            usuario.getUsuEmail(),
                            usuario.getUsuRol().toString().replace("_", " "),
                            usuario.isActivo() ? "✅ Activo" : "❌ Inactivo"
                        };
                        modeloTabla.addRow(fila);
                    }
                    
                    // Actualizar contador
                    lblTotal.setText("Total: " + usuarios.size() + " usuarios");
                    
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(
                        RegistroUsuarios.this,
                        "Error al cargar usuarios: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                    );
                    lblTotal.setText("Error al cargar");
                }
            });
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                this,
                "Error al conectar con el servicio de usuarios: " + ex.getMessage(),
                "Error de Conexión",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }
    
    private void filtrarUsuarios() {
        String textoBusqueda = txtBuscar.getText().toLowerCase().trim();
        TableRowSorter<DefaultTableModel> sorter = 
            (TableRowSorter<DefaultTableModel>) tablaUsuarios.getRowSorter();
        
        if (textoBusqueda.isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            // Filtrar por nombre, usuario o email
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + textoBusqueda, 1, 2, 3));
        }
        
        // Actualizar contador de resultados filtrados
        int filasVisibles = tablaUsuarios.getRowCount();
        lblTotal.setText("Mostrando: " + filasVisibles + " usuarios");
    }
    
    private void mostrarDetallesUsuario() {
        int filaSeleccionada = tablaUsuarios.getSelectedRow();
        if (filaSeleccionada == -1) return;
        
        // Obtener datos de la fila seleccionada
        int usuarioId = (Integer) tablaUsuarios.getValueAt(filaSeleccionada, 0);
        String nombre = (String) tablaUsuarios.getValueAt(filaSeleccionada, 1);
        String usuario = (String) tablaUsuarios.getValueAt(filaSeleccionada, 2);
        String email = (String) tablaUsuarios.getValueAt(filaSeleccionada, 3);
        String rol = (String) tablaUsuarios.getValueAt(filaSeleccionada, 4);
        String estado = (String) tablaUsuarios.getValueAt(filaSeleccionada, 5);
        
        // Mostrar información del usuario
        String mensaje = String.format(
            "📋 INFORMACIÓN DEL USUARIO\n\n" +
            "🆔 ID: %d\n" +
            "👤 Nombre: %s\n" +
            "🔑 Usuario: %s\n" +
            "📧 Email: %s\n" +
            "👑 Rol: %s\n" +
            "📊 Estado: %s",
            usuarioId, nombre, usuario, email, rol, estado
        );
        
        JOptionPane.showMessageDialog(
            this,
            mensaje,
            "Detalles del Usuario",
            JOptionPane.INFORMATION_MESSAGE
        );
    }
    
    private JButton createStyledButton(String text, Color backgroundColor) {
        JButton button = new JButton(text);
        button.setBackground(backgroundColor);
        button.setForeground(COLOR_BLANCO);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setFont(new Font("Arial", Font.BOLD, 11));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Efecto hover
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                button.setBackground(backgroundColor.brighter());
            }
            
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                button.setBackground(backgroundColor);
            }
        });
        
        return button;
    }
}
