package com.ypacarai.cooperativa.activos.view;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

import com.ypacarai.cooperativa.activos.dao.ActivoDAO;
import com.ypacarai.cooperativa.activos.dao.ReportesDAOSimple;
import com.ypacarai.cooperativa.activos.dao.TicketDAO;
import com.ypacarai.cooperativa.activos.dao.TipoActivoDAO;
import com.ypacarai.cooperativa.activos.dao.UbicacionDAO;
import com.ypacarai.cooperativa.activos.dao.UsuarioDAO;
import com.ypacarai.cooperativa.activos.model.Activo;
import com.ypacarai.cooperativa.activos.model.DashboardData;
import com.ypacarai.cooperativa.activos.model.TipoActivo;
import com.ypacarai.cooperativa.activos.model.Ubicacion;
import com.ypacarai.cooperativa.activos.model.Usuario;
import com.ypacarai.cooperativa.activos.service.ActivoService;
import com.ypacarai.cooperativa.activos.service.GestionUsuariosService;
import com.ypacarai.cooperativa.activos.service.MantenimientoPreventivoService;
import com.ypacarai.cooperativa.activos.util.ControlAccesoRoles;


/**
 * Ventana Principal del Sistema de Gestión de Activos
 * Cooperativa Ypacaraí LTDA
 * 
 * Funcionalidades principales:
 * - Dashboard con KPIs
 * - Gestión de Activos
 * - Sistema de Tickets
 * - Mantenimiento Preventivo/Correctivo
 * - Reportes
 * - Gestión de Usuarios
 */
public class MainWindowNew extends JFrame {
    
    // Colores corporativos
    private static final Color COLOR_VERDE_COOPERATIVA = new Color(34, 139, 34);
    private static final Color COLOR_VERDE_CLARO = new Color(144, 238, 144);
    private static final Color COLOR_BLANCO = Color.WHITE;
    private static final Color COLOR_GRIS_CLARO = new Color(245, 245, 245);
    private static final Color COLOR_GRIS_TEXTO = new Color(64, 64, 64);
    private static final Color COLOR_AZUL_INFO = new Color(70, 130, 180);
    private static final Color COLOR_NARANJA_WARNING = new Color(255, 140, 0);
    private static final Color COLOR_ROJO_DANGER = new Color(220, 20, 60);
    
    // Usuario actual
    private Usuario usuarioActual;
    
    // Componentes principales
    private JPanel panelContenido;
    private JLabel lblUsuarioActual;
    private JLabel lblHoraActual;
    
    // Servicios y DAOs
    private ActivoService activoService;
    private MantenimientoPreventivoService mantenimientoPreventivoService;
    private GestionUsuariosService gestionUsuariosService;
    private ActivoDAO activoDAO;
    private TipoActivoDAO tipoActivoDAO;
    private UbicacionDAO ubicacionDAO;
    private UsuarioDAO usuarioDAO;
    private TicketDAO ticketDAO;
    
    // Paneles de funcionalidades
    private JPanel panelDashboard;
    private JPanel panelActivos;
    private JPanel panelTickets;
    private JPanel panelMantenimiento;
    private JPanel panelReportes;
    private JPanel panelUsuarios;
    private JPanel panelConfiguracion;
    
    public MainWindowNew(Usuario usuario) {
        this.usuarioActual = usuario;
        initializeServices();
        initializeComponents();
        setupEventListeners();
        updateUserInterface();
        showDashboard();
    }
    
    private void initializeServices() {
        try {
            this.activoService = new ActivoService();
            this.activoDAO = new ActivoDAO();
            this.tipoActivoDAO = new TipoActivoDAO();
            this.ubicacionDAO = new UbicacionDAO();
            this.usuarioDAO = new UsuarioDAO();
            this.ticketDAO = new TicketDAO();
            
            // Inicializar nuevos servicios
            this.mantenimientoPreventivoService = new MantenimientoPreventivoService();
            this.gestionUsuariosService = new GestionUsuariosService();
            
            // Inicializar configuraciones de mantenimiento por defecto
            this.mantenimientoPreventivoService.inicializarConfiguracionesPorDefecto();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error al inicializar servicios: " + e.getMessage(), 
                "Error de Inicialización", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    private void initializeComponents() {
        setTitle("Sistema de Gestión de Activos - Cooperativa Ypacaraí LTDA");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setMinimumSize(new Dimension(1200, 800));
        
        // Panel principal con gradiente
        JPanel panelPrincipal = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                
                // Gradiente de fondo sutil
                GradientPaint gradient = new GradientPaint(
                    0, 0, COLOR_GRIS_CLARO,
                    0, getHeight(), COLOR_BLANCO
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        panelPrincipal.setLayout(new BorderLayout());
        
        // Barra superior
        JPanel barraSuperior = createBarraSuperior();
        panelPrincipal.add(barraSuperior, BorderLayout.NORTH);
        
        // Panel lateral de navegación
        JPanel panelNavegacion = createPanelNavegacion();
        panelPrincipal.add(panelNavegacion, BorderLayout.WEST);
        
        // Panel de contenido principal
        panelContenido = new JPanel(new CardLayout());
        panelContenido.setOpaque(false);
        panelPrincipal.add(panelContenido, BorderLayout.CENTER);
        
        // Barra de estado
        JPanel barraEstado = createBarraEstado();
        panelPrincipal.add(barraEstado, BorderLayout.SOUTH);
        
        add(panelPrincipal);
        
        // Crear paneles de funcionalidades
        // Crear y agregar paneles al contenido principal
        JPanel dashboard = createPanelDashboard();
        panelContenido.add(dashboard, "dashboard");
        createPanelActivos();
        createPanelTickets();
        createPanelMantenimiento();
        createPanelReportes();
        createPanelUsuarios();
        createPanelConfiguracion();
    }
    
    private JPanel createBarraSuperior() {
        JPanel barra = new JPanel();
        barra.setBackground(COLOR_VERDE_COOPERATIVA);
        barra.setLayout(new BorderLayout());
        barra.setBorder(new EmptyBorder(10, 15, 10, 15));
        barra.setPreferredSize(new Dimension(0, 60));
        
        // Logo y título
        JPanel panelTitulo = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        panelTitulo.setOpaque(false);
        
        JLabel lblLogo = new JLabel("🏛️");
        lblLogo.setFont(new Font("Segoe UI", Font.PLAIN, 24));
        
        JLabel lblTitulo = new JLabel("COOPERATIVA YPACARAÍ LTDA - Sistema de Gestión de Activos");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitulo.setForeground(COLOR_BLANCO);
        
        panelTitulo.add(lblLogo);
        panelTitulo.add(lblTitulo);
        
        // Panel de usuario
        JPanel panelUsuario = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        panelUsuario.setOpaque(false);
        
        lblUsuarioActual = new JLabel();
        lblUsuarioActual.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblUsuarioActual.setForeground(COLOR_BLANCO);
        lblUsuarioActual.setIcon(createUserIcon());
        
        lblHoraActual = new JLabel();
        lblHoraActual.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblHoraActual.setForeground(COLOR_BLANCO);
        
        JButton btnCerrarSesion = createStyledButton("Cerrar Sesión", COLOR_ROJO_DANGER, COLOR_BLANCO, false);
        btnCerrarSesion.addActionListener(e -> cerrarSesion());
        
        panelUsuario.add(lblUsuarioActual);
        panelUsuario.add(new JLabel(" | "));
        panelUsuario.add(lblHoraActual);
        panelUsuario.add(Box.createHorizontalStrut(20));
        panelUsuario.add(btnCerrarSesion);
        
        barra.add(panelTitulo, BorderLayout.WEST);
        barra.add(panelUsuario, BorderLayout.EAST);
        
        return barra;
    }
    
    private JPanel createPanelNavegacion() {
        JPanel panel = new JPanel();
        panel.setBackground(COLOR_BLANCO);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, COLOR_VERDE_CLARO));
        panel.setPreferredSize(new Dimension(220, 0));
        
        // Título del menú
        JLabel lblMenu = new JLabel("MENÚ PRINCIPAL");
        lblMenu.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblMenu.setForeground(COLOR_GRIS_TEXTO);
        lblMenu.setBorder(new EmptyBorder(15, 15, 10, 15));
        lblMenu.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(lblMenu);
        
        // Información del usuario actual
        if (usuarioActual != null) {
            JLabel lblUsuario = new JLabel("👤 " + usuarioActual.getUsuNombre());
            lblUsuario.setFont(new Font("Segoe UI", Font.BOLD, 10));
            lblUsuario.setForeground(COLOR_VERDE_COOPERATIVA);
            lblUsuario.setBorder(new EmptyBorder(5, 15, 5, 15));
            lblUsuario.setAlignmentX(Component.LEFT_ALIGNMENT);
            panel.add(lblUsuario);
            
            JLabel lblRol = new JLabel("🔐 " + usuarioActual.getUsuRol());
            lblRol.setFont(new Font("Segoe UI", Font.PLAIN, 9));
            lblRol.setForeground(COLOR_GRIS_TEXTO);
            lblRol.setBorder(new EmptyBorder(0, 15, 10, 15));
            lblRol.setAlignmentX(Component.LEFT_ALIGNMENT);
            panel.add(lblRol);
        }
        
        // Botones de navegación - Solo mostrar los permitidos
        String[][] menuItems = {
            {"📊", "Dashboard", "dashboard"},
            {"💻", "Gestión de Activos", "activos"},
            {"🎫", "Sistema de Tickets", "tickets"},
            {"🔧", "Mantenimiento", "mantenimiento"},
            {"📈", "Reportes", "reportes"},
            {"👥", "Usuarios", "usuarios"},
            {"⚙️", "Configuración", "configuracion"}
        };
        
        for (String[] item : menuItems) {
            String modulo = item[2];
            
            // Solo mostrar botones a los que el usuario tiene acceso
            if (ControlAccesoRoles.puedeAccederModulo(usuarioActual, modulo)) {
                JButton btnMenu = createMenuButton(item[0] + " " + item[1], modulo);
                panel.add(btnMenu);
            }
            // No agregar nada si no tiene permisos - el botón simplemente no aparece
        }
        
        panel.add(Box.createVerticalGlue());
        
        return panel;
    }
    
    private JButton createMenuButton(String text, String action) {
        JButton button = new JButton(text) {
            private boolean isSelected = false;
            
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Background color based on state
                Color backgroundColor = COLOR_BLANCO;
                if (isSelected) {
                    backgroundColor = COLOR_VERDE_CLARO;
                } else if (getModel().isRollover()) {
                    backgroundColor = COLOR_GRIS_CLARO;
                }
                
                g2d.setColor(backgroundColor);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                
                // Left border for selected state
                if (isSelected) {
                    g2d.setColor(COLOR_VERDE_COOPERATIVA);
                    g2d.fillRect(0, 0, 4, getHeight());
                }
                
                // Text
                g2d.setColor(isSelected ? COLOR_VERDE_COOPERATIVA : COLOR_GRIS_TEXTO);
                g2d.setFont(getFont());
                FontMetrics fm = g2d.getFontMetrics();
                int textX = 15;
                int textY = (getHeight() + fm.getAscent()) / 2 - 2;
                g2d.drawString(getText(), textX, textY);
            }
            
            public void setSelected(boolean selected) {
                this.isSelected = selected;
                repaint();
            }
        };
        
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setPreferredSize(new Dimension(220, 40));
        button.setMaximumSize(new Dimension(220, 40));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        button.addActionListener(e -> {
            selectMenuItem(button, action);
            showPanel(action);
        });
        
        return button;
    }
    
    private JButton createMenuButtonDisabled(String text, String modulo) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Fondo gris para botón deshabilitado
                g2d.setColor(COLOR_GRIS_CLARO);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                
                // Texto gris
                g2d.setColor(Color.LIGHT_GRAY);
                g2d.setFont(getFont());
                FontMetrics fm = g2d.getFontMetrics();
                int textX = 15;
                int textY = (getHeight() + fm.getAscent()) / 2 - 2;
                g2d.drawString(getText(), textX, textY);
            }
        };
        
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setPreferredSize(new Dimension(220, 40));
        button.setMaximumSize(new Dimension(220, 40));
        button.setEnabled(false);
        
        // Tooltip explicativo
        String mensaje = ControlAccesoRoles.mensajeAccesoDenegado(usuarioActual, "acceder al módulo " + modulo);
        button.setToolTipText("<html><div style='width: 200px;'>" + mensaje.replace("\n", "<br>") + "</div></html>");
        
        return button;
    }
    
    private void selectMenuItem(JButton selectedButton, String action) {
        // Deselect all buttons
        Component[] components = ((JPanel) selectedButton.getParent()).getComponents();
        for (Component comp : components) {
            if (comp instanceof JButton) {
                try {
                    comp.getClass().getMethod("setSelected", boolean.class).invoke(comp, false);
                } catch (Exception e) {
                    // Ignore
                }
            }
        }
        
        // Select the clicked button
        try {
            selectedButton.getClass().getMethod("setSelected", boolean.class).invoke(selectedButton, true);
        } catch (Exception e) {
            // Ignore
        }
    }
    
    private JPanel createBarraEstado() {
        JPanel barra = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        barra.setBackground(COLOR_GRIS_CLARO);
        barra.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY));
        barra.setPreferredSize(new Dimension(0, 30));
        
        JLabel lblEstado = new JLabel("✅ Sistema operativo - Conexión establecida");
        lblEstado.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblEstado.setForeground(COLOR_VERDE_COOPERATIVA);
        
        barra.add(lblEstado);
        
        return barra;
    }
    
    private JPanel createPanelDashboard() {
        JPanel dashboard = new JPanel(new BorderLayout());
        dashboard.setOpaque(false);
        dashboard.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Título
        JLabel lblTitulo = new JLabel("Dashboard - Resumen Ejecutivo");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitulo.setForeground(COLOR_VERDE_COOPERATIVA);
        lblTitulo.setBorder(new EmptyBorder(0, 0, 20, 0));
        
        // Panel de KPIs
        JPanel panelKPIs = createPanelKPIs();
        
        // Panel de gráficos y alertas
        JPanel panelCentral = new JPanel(new GridLayout(1, 2, 20, 0));
        panelCentral.setOpaque(false);
        
        JPanel panelGraficos = createPanelGraficos();
        JPanel panelAlertas = createPanelAlertas();
        
        panelCentral.add(panelGraficos);
        panelCentral.add(panelAlertas);
        
        dashboard.add(lblTitulo, BorderLayout.NORTH);
        dashboard.add(panelKPIs, BorderLayout.CENTER);
        dashboard.add(panelCentral, BorderLayout.SOUTH);
        
        // También asignar a la variable de instancia para compatibilidad
        panelDashboard = dashboard;
        
        return dashboard;
    }
    
    private JPanel createPanelKPIs() {
        JPanel panel = new JPanel(new GridLayout(2, 4, 15, 15));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(0, 0, 20, 0));
        
        // KPIs principales con datos reales de la base de datos
        try {
            // Obtener datos reales del dashboard
            ReportesDAOSimple reportesDAO = new ReportesDAOSimple();
            DashboardData dashboardData = reportesDAO.obtenerDatosDashboard();
            
            // También obtener datos básicos de activos para complementar
            List<Activo> activos = activoService.obtenerTodosLosActivos();
            List<TipoActivo> tipos = activoService.obtenerTodosTiposActivos();
            List<Ubicacion> ubicaciones = activoService.obtenerTodasUbicaciones();
            
            // KPIs principales usando datos reales del dashboard
            panel.add(createKPICard("Total Activos", String.valueOf(dashboardData.getTotalActivos()), "💻", COLOR_AZUL_INFO));
            panel.add(createKPICard("Operativos", String.valueOf(dashboardData.getActivosOperativos()), "✅", COLOR_VERDE_COOPERATIVA));
            panel.add(createKPICard("En Mantenimiento", String.valueOf(dashboardData.getActivosEnMantenimiento()), "🔧", COLOR_NARANJA_WARNING));
            panel.add(createKPICard("Fuera de Servicio", String.valueOf(dashboardData.getActivosFueraServicio()), "❌", COLOR_ROJO_DANGER));
            
            // Segunda fila - KPIs complementarios
            panel.add(createKPICard("Tipos de Activos", String.valueOf(tipos.size()), "📋", COLOR_AZUL_INFO));
            panel.add(createKPICard("Ubicaciones", String.valueOf(ubicaciones.size()), "📍", COLOR_VERDE_COOPERATIVA));
            panel.add(createKPICard("Tickets Abiertos", String.valueOf(dashboardData.getTicketsAbiertos()), "🎫", COLOR_NARANJA_WARNING));
            panel.add(createKPICard("Mant. Vencidos", String.valueOf(dashboardData.getMantenimientosPendientes()), "⏰", COLOR_ROJO_DANGER));
            
        } catch (Exception e) {
            System.err.println("Error cargando datos del dashboard: " + e.getMessage());
            e.printStackTrace();
            
            // Fallback con datos básicos si falla el dashboard completo
            try {
                List<Activo> activos = activoService.obtenerTodosLosActivos();
                List<TipoActivo> tipos = activoService.obtenerTodosTiposActivos();
                List<Ubicacion> ubicaciones = activoService.obtenerTodasUbicaciones();
                
                int totalActivos = activos.size();
                int activosOperativos = (int) activos.stream().filter(a -> a.getActEstado() == Activo.Estado.Operativo).count();
                int activosMantenimiento = (int) activos.stream().filter(a -> a.getActEstado() == Activo.Estado.En_Mantenimiento).count();
                int activosFueraServicio = (int) activos.stream().filter(a -> a.getActEstado() == Activo.Estado.Fuera_Servicio).count();
                
                // Cards de KPIs básicos
                panel.add(createKPICard("Total Activos", String.valueOf(totalActivos), "💻", COLOR_AZUL_INFO));
                panel.add(createKPICard("Operativos", String.valueOf(activosOperativos), "✅", COLOR_VERDE_COOPERATIVA));
                panel.add(createKPICard("En Mantenimiento", String.valueOf(activosMantenimiento), "🔧", COLOR_NARANJA_WARNING));
                panel.add(createKPICard("Fuera de Servicio", String.valueOf(activosFueraServicio), "❌", COLOR_ROJO_DANGER));
                
                panel.add(createKPICard("Tipos de Activos", String.valueOf(tipos.size()), "📋", COLOR_AZUL_INFO));
                panel.add(createKPICard("Ubicaciones", String.valueOf(ubicaciones.size()), "📍", COLOR_VERDE_COOPERATIVA));
                panel.add(createKPICard("Tickets Abiertos", "⚠️", "🎫", COLOR_NARANJA_WARNING));
                panel.add(createKPICard("Mant. Vencidos", "⚠️", "⏰", COLOR_ROJO_DANGER));
                
            } catch (Exception fallbackException) {
                JLabel lblError = new JLabel("Error cargando KPIs: " + fallbackException.getMessage());
                lblError.setForeground(Color.RED);
                panel.add(lblError);
                fallbackException.printStackTrace();
            }
        }
        
        return panel;
    }
    
    private JPanel createKPICard(String titulo, String valor, String icono, Color color) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Background with shadow effect
                g2d.setColor(new Color(0, 0, 0, 10));
                g2d.fillRoundRect(2, 2, getWidth() - 2, getHeight() - 2, 10, 10);
                
                g2d.setColor(COLOR_BLANCO);
                g2d.fillRoundRect(0, 0, getWidth() - 2, getHeight() - 2, 10, 10);
                
                // Colored top border
                g2d.setColor(color);
                g2d.setStroke(new BasicStroke(3));
                g2d.drawLine(10, 5, getWidth() - 10, 5);
            }
        };
        
        card.setOpaque(false);
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(15, 20, 15, 20));
        card.setPreferredSize(new Dimension(200, 100));
        
        // Icono
        JLabel lblIcono = new JLabel(icono);
        lblIcono.setFont(new Font("Segoe UI", Font.PLAIN, 24));
        lblIcono.setHorizontalAlignment(SwingConstants.RIGHT);
        
        // Contenido
        JPanel panelContenido = new JPanel();
        panelContenido.setOpaque(false);
        panelContenido.setLayout(new BoxLayout(panelContenido, BoxLayout.Y_AXIS));
        
        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblTitulo.setForeground(COLOR_GRIS_TEXTO);
        lblTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel lblValor = new JLabel(valor);
        lblValor.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblValor.setForeground(color);
        lblValor.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        panelContenido.add(lblTitulo);
        panelContenido.add(Box.createVerticalStrut(5));
        panelContenido.add(lblValor);
        
        card.add(panelContenido, BorderLayout.WEST);
        card.add(lblIcono, BorderLayout.EAST);
        
        return card;
    }
    
    private JPanel createPanelGraficos() {
        JPanel panel = createWhitePanel("Distribución de Activos por Estado");
        
        try {
            // Obtener datos reales de distribución
            ReportesDAOSimple reportesDAO = new ReportesDAOSimple();
            DashboardData dashboardData = reportesDAO.obtenerDatosDashboard();
            
            // Panel principal de gráfico
            JPanel graficoPanel = new JPanel(new BorderLayout(10, 10));
            graficoPanel.setOpaque(false);
            graficoPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
            
            // Panel de barras de distribución
            JPanel barrasPanel = new JPanel();
            barrasPanel.setLayout(new BoxLayout(barrasPanel, BoxLayout.Y_AXIS));
            barrasPanel.setOpaque(false);
            
            int totalActivos = dashboardData.getTotalActivos();
            
            if (totalActivos > 0) {
                // Título del gráfico
                JLabel titulo = new JLabel("Estado Actual de " + totalActivos + " Activos");
                titulo.setFont(new Font("Segoe UI", Font.BOLD, 16));
                titulo.setForeground(COLOR_AZUL_INFO);
                titulo.setAlignmentX(Component.CENTER_ALIGNMENT);
                barrasPanel.add(titulo);
                barrasPanel.add(Box.createVerticalStrut(15));
                
                // Barras de estado con datos reales
                barrasPanel.add(createBarraEstado("🟢 Operativos", 
                    dashboardData.getActivosOperativos(), totalActivos, COLOR_VERDE_COOPERATIVA));
                barrasPanel.add(Box.createVerticalStrut(10));
                
                barrasPanel.add(createBarraEstado("🟡 En Mantenimiento", 
                    dashboardData.getActivosEnMantenimiento(), totalActivos, COLOR_NARANJA_WARNING));
                barrasPanel.add(Box.createVerticalStrut(10));
                
                barrasPanel.add(createBarraEstado("🔴 Fuera de Servicio", 
                    dashboardData.getActivosFueraServicio(), totalActivos, COLOR_ROJO_DANGER));
                barrasPanel.add(Box.createVerticalStrut(10));
                
                // Otros estados
                int otrosEstados = totalActivos - dashboardData.getActivosOperativos() 
                    - dashboardData.getActivosEnMantenimiento() - dashboardData.getActivosFueraServicio();
                if (otrosEstados > 0) {
                    barrasPanel.add(createBarraEstado("⚪ Otros Estados", 
                        otrosEstados, totalActivos, COLOR_GRIS_TEXTO));
                }
                
                // Resumen estadístico
                barrasPanel.add(Box.createVerticalStrut(20));
                double porcentajeOperativos = (double) dashboardData.getActivosOperativos() / totalActivos * 100;
                JLabel resumen = new JLabel(String.format(
                    "<html><div style='text-align:center'>" +
                    "<b>%.1f%% de activos operativos</b><br>" +
                    "<small>Actualizado: %s</small>" +
                    "</div></html>",
                    porcentajeOperativos,
                    java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM HH:mm"))
                ));
                resumen.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                resumen.setForeground(COLOR_GRIS_TEXTO);
                resumen.setAlignmentX(Component.CENTER_ALIGNMENT);
                barrasPanel.add(resumen);
                
            } else {
                JLabel sinDatos = new JLabel("📊 No hay activos registrados");
                sinDatos.setFont(new Font("Segoe UI", Font.PLAIN, 16));
                sinDatos.setForeground(COLOR_GRIS_TEXTO);
                sinDatos.setAlignmentX(Component.CENTER_ALIGNMENT);
                barrasPanel.add(sinDatos);
            }
            
            graficoPanel.add(barrasPanel, BorderLayout.CENTER);
            panel.add(graficoPanel, BorderLayout.CENTER);
            
        } catch (Exception e) {
            System.err.println("Error cargando gráfico de distribución: " + e.getMessage());
            e.printStackTrace();
            
            JLabel lblError = new JLabel("⚠️ Error cargando distribución de activos");
            lblError.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            lblError.setForeground(COLOR_ROJO_DANGER);
            lblError.setHorizontalAlignment(SwingConstants.CENTER);
            panel.add(lblError, BorderLayout.CENTER);
        }
        
        return panel;
    }
    
    private JPanel createBarraEstado(String etiqueta, int cantidad, int total, Color color) {
        JPanel panel = new JPanel(new BorderLayout(10, 5));
        panel.setOpaque(false);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        
        // Calcular porcentaje
        double porcentaje = total > 0 ? (double) cantidad / total * 100 : 0;
        
        // Etiqueta con cantidad y porcentaje
        JLabel lblEtiqueta = new JLabel(String.format("%s: %d (%.1f%%)", etiqueta, cantidad, porcentaje));
        lblEtiqueta.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblEtiqueta.setForeground(color);
        lblEtiqueta.setPreferredSize(new Dimension(200, 25));
        panel.add(lblEtiqueta, BorderLayout.WEST);
        
        // Barra de progreso visual
        JProgressBar barra = new JProgressBar(0, total > 0 ? total : 1);
        barra.setValue(cantidad);
        barra.setStringPainted(false);
        barra.setForeground(color);
        barra.setBackground(new Color(240, 240, 240));
        barra.setBorderPainted(true);
        barra.setBorder(BorderFactory.createLineBorder(color.darker(), 1));
        barra.setPreferredSize(new Dimension(300, 20));
        
        panel.add(barra, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createPanelAlertas() {
        JPanel panel = createWhitePanel("Alertas y Notificaciones");
        
        JPanel contenido = new JPanel();
        contenido.setOpaque(false);
        contenido.setLayout(new BoxLayout(contenido, BoxLayout.Y_AXIS));
        
        try {
            // Obtener datos reales de alertas
            ReportesDAOSimple reportesDAO = new ReportesDAOSimple();
            DashboardData dashboardData = reportesDAO.obtenerDatosDashboard();
            
            // Contador de alertas
            int totalAlertas = 0;
            
            // Alerta por activos fuera de servicio
            if (dashboardData.getActivosFueraServicio() > 0) {
                contenido.add(createAlerta("❌", 
                    dashboardData.getActivosFueraServicio() + " activo(s) fuera de servicio", 
                    COLOR_ROJO_DANGER));
                totalAlertas++;
            }
            
            // Alerta por mantenimientos pendientes
            if (dashboardData.getMantenimientosPendientes() > 0) {
                contenido.add(createAlerta("⚠️", 
                    dashboardData.getMantenimientosPendientes() + " mantenimiento(s) vencido(s)", 
                    COLOR_ROJO_DANGER));
                totalAlertas++;
            }
            
            // Alerta por activos en mantenimiento
            if (dashboardData.getActivosEnMantenimiento() > 0) {
                contenido.add(createAlerta("🔧", 
                    dashboardData.getActivosEnMantenimiento() + " activo(s) en mantenimiento", 
                    COLOR_NARANJA_WARNING));
                totalAlertas++;
            }
            
            // Alerta por tickets abiertos
            if (dashboardData.getTicketsAbiertos() > 0) {
                contenido.add(createAlerta("🎫", 
                    dashboardData.getTicketsAbiertos() + " ticket(s) abierto(s)", 
                    COLOR_AZUL_INFO));
                totalAlertas++;
            }
            
            // Verificar estado general del sistema
            double porcentajeOperativos = dashboardData.getTotalActivos() > 0 ? 
                (double) dashboardData.getActivosOperativos() / dashboardData.getTotalActivos() * 100 : 0;
            
            if (porcentajeOperativos >= 90) {
                contenido.add(createAlerta("✅", 
                    String.format("Sistema operativo al %.1f%% - Estado óptimo", porcentajeOperativos), 
                    COLOR_VERDE_COOPERATIVA));
                totalAlertas++;
            } else if (porcentajeOperativos >= 75) {
                contenido.add(createAlerta("⚠️", 
                    String.format("Sistema operativo al %.1f%% - Requiere atención", porcentajeOperativos), 
                    COLOR_NARANJA_WARNING));
                totalAlertas++;
            } else {
                contenido.add(createAlerta("❌", 
                    String.format("Sistema operativo al %.1f%% - Estado crítico", porcentajeOperativos), 
                    COLOR_ROJO_DANGER));
                totalAlertas++;
            }
            
            // Si no hay alertas importantes, mostrar estado normal
            if (totalAlertas == 1 && porcentajeOperativos >= 90) {
                contenido.add(createAlerta("🔔", 
                    "No hay alertas pendientes", 
                    COLOR_GRIS_TEXTO));
            }
            
            // Última actualización
            contenido.add(Box.createVerticalStrut(10));
            JLabel lblActualizacion = new JLabel(String.format(
                "<html><div style='text-align:center'>" +
                "<small>Última actualización: %s</small>" +
                "</div></html>",
                java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))
            ));
            lblActualizacion.setFont(new Font("Segoe UI", Font.ITALIC, 10));
            lblActualizacion.setForeground(COLOR_GRIS_TEXTO);
            lblActualizacion.setAlignmentX(Component.CENTER_ALIGNMENT);
            contenido.add(lblActualizacion);
            
        } catch (Exception e) {
            System.err.println("Error cargando alertas del sistema: " + e.getMessage());
            e.printStackTrace();
            
            // Alertas de fallback en caso de error
            contenido.add(createAlerta("⚠️", "Error cargando alertas del sistema", COLOR_ROJO_DANGER));
            contenido.add(createAlerta("🔄", "Verificando conexión con base de datos...", COLOR_AZUL_INFO));
        }
        
        panel.add(contenido, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createAlerta(String icono, String mensaje, Color color) {
        JPanel alerta = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        alerta.setOpaque(false);
        
        JLabel lblIcono = new JLabel(icono);
        JLabel lblMensaje = new JLabel(mensaje);
        lblMensaje.setForeground(color);
        lblMensaje.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        alerta.add(lblIcono);
        alerta.add(lblMensaje);
        
        return alerta;
    }
    
    private void createPanelActivos() {
        panelActivos = createWhitePanel("Gestión de Activos");
        
        // Usar nuestro panel completo de inventario de activos
        InventarioActivosPanel inventarioPanel = new InventarioActivosPanel(usuarioActual);
        
        panelActivos.add(inventarioPanel, BorderLayout.CENTER);
        panelContenido.add(panelActivos, "activos");
    }
    
    /**
     * Muestra el formulario de registro de activos
     */
    private void mostrarFormularioRegistroActivo() {
        SwingUtilities.invokeLater(() -> {
            // Crear el panel de registro
            RegistroActivoPanel registroPanel = new RegistroActivoPanel(this, usuarioActual.getUsuId());
            
            // Crear una nueva ventana para el formulario
            JFrame ventanaRegistro = new JFrame("Registro de Activos");
            ventanaRegistro.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            ventanaRegistro.setSize(900, 700);
            ventanaRegistro.setLocationRelativeTo(this);
            ventanaRegistro.add(registroPanel);
            ventanaRegistro.setVisible(true);
        });
    }
    
    private JTable createTablaActivos() {
        String[] columnas = {"ID", "Número", "Tipo", "Marca", "Modelo", "Estado", "Ubicación", "Fecha Adquisición"};
        DefaultTableModel modelo = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        JTable tabla = new JTable(modelo);
        tabla.setRowHeight(25);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setRowSorter(new TableRowSorter<>(modelo));
        
        // Cargar datos
        cargarDatosActivos(modelo);
        
        return tabla;
    }
    
    private void cargarDatosActivos(DefaultTableModel modelo) {
        try {
            List<Activo> activos = activoService.obtenerTodosLosActivos();
            
            for (Activo activo : activos) {
                Object[] fila = {
                    activo.getActId(),
                    activo.getActNumeroActivo(),
                    "Tipo " + activo.getTipActId(),
                    activo.getActMarca() != null ? activo.getActMarca() : "",
                    activo.getActModelo() != null ? activo.getActModelo() : "",
                    activo.getActEstado(),
                    "Ubicación " + activo.getActUbicacionActual(),
                    activo.getActFechaAdquisicion() != null ? 
                        activo.getActFechaAdquisicion().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : ""
                };
                modelo.addRow(fila);
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error cargando activos: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void createPanelTickets() {
        // Crear el panel completo de tickets usando nuestro SistemaTicketsPanel
        panelTickets = new JPanel(new BorderLayout());
        panelTickets.setBackground(Color.WHITE);
        
        // Crear el sistema de tickets con funcionalidad completa
        SistemaTicketsPanel sistemaTickets = new SistemaTicketsPanel(usuarioActual);
        panelTickets.add(sistemaTickets, BorderLayout.CENTER);
        
        panelContenido.add(panelTickets, "tickets");
    }
    
    private void createPanelMantenimiento() {
        // Verificar si el usuario es técnico para mostrar interfaz integrada
        if (usuarioActual != null && usuarioActual.getUsuRol() == Usuario.Rol.Tecnico) {
            // Para técnicos, usar el panel integrado directamente
            panelMantenimiento = new MantenimientoTecnicoPanel(usuarioActual);
        } else {
            // Para otros roles, crear un panel con pestañas para diferentes tipos de mantenimiento
            panelMantenimiento = createWhitePanel("🔧 Sistema de Mantenimiento");
            
            // Crear panel con pestañas
            JTabbedPane tabbedMantenimiento = new JTabbedPane(JTabbedPane.TOP);
            tabbedMantenimiento.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            
            // Pestaña de mantenimiento preventivo/correctivo tradicional
            MantenimientoPanel mantenimientoPanel = new MantenimientoPanel(usuarioActual);
            tabbedMantenimiento.addTab("🔧 Mantenimiento Interno", mantenimientoPanel);
            
            // Pestaña de mantenimiento tercerizado
            MantenimientoTercerizadoPanel mantenimientoTercerizadoPanel = new MantenimientoTercerizadoPanel(usuarioActual);
            tabbedMantenimiento.addTab("🏢 Mantenimiento Tercerizado", mantenimientoTercerizadoPanel);
            
            panelMantenimiento.add(tabbedMantenimiento, BorderLayout.CENTER);
        }
        
        panelContenido.add(panelMantenimiento, "mantenimiento");
    }
    
    private void createPanelReportes() {
        // Crear el panel completo de reportes
        ReportesPanel reportesPanel = new ReportesPanel();
        
        // Crear panel contenedor con título
        panelReportes = createWhitePanel("📈 Reportes y Consultas");
        panelReportes.add(reportesPanel, BorderLayout.CENTER);
        panelContenido.add(panelReportes, "reportes");
    }
    
    private void createPanelUsuarios() {
        // Crear el panel completo de usuarios usando nuestro nuevo SistemaUsuariosPanel
        panelUsuarios = new JPanel(new BorderLayout());
        panelUsuarios.setBackground(Color.WHITE);
        
        // Crear el sistema de usuarios con funcionalidad completa
        SistemaUsuariosPanel sistemaUsuarios = new SistemaUsuariosPanel(usuarioActual);
        panelUsuarios.add(sistemaUsuarios, BorderLayout.CENTER);
        
        panelContenido.add(panelUsuarios, "usuarios");
    }
    
    private void createPanelConfiguracion() {
        // Usar el panel completo de configuración con todas las funcionalidades CRUD
        ConfiguracionPanel configuracionPanel = new ConfiguracionPanel(this.usuarioActual);
        
        // Crear panel contenedor con título
        panelConfiguracion = createWhitePanel("⚙️ Configuración del Sistema");
        panelConfiguracion.add(configuracionPanel, BorderLayout.CENTER);
        panelContenido.add(panelConfiguracion, "configuracion");
    }
    
    private JPanel createWhitePanel(String titulo) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(COLOR_BLANCO);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        if (titulo != null) {
            JLabel lblTitulo = new JLabel(titulo);
            lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
            lblTitulo.setForeground(COLOR_VERDE_COOPERATIVA);
            lblTitulo.setBorder(new EmptyBorder(0, 0, 15, 0));
            panel.add(lblTitulo, BorderLayout.NORTH);
        }
        
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
        button.setFont(new Font("Segoe UI", isPrimary ? Font.BOLD : Font.PLAIN, 11));
        button.setPreferredSize(new Dimension(button.getPreferredSize().width + 20, 32));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        return button;
    }
    
    private ImageIcon createUserIcon() {
        return new ImageIcon(new java.awt.image.BufferedImage(16, 16, java.awt.image.BufferedImage.TYPE_INT_ARGB) {
            {
                Graphics2D g2d = createGraphics();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(COLOR_BLANCO);
                g2d.fillOval(2, 2, 12, 12);
                g2d.dispose();
            }
        });
    }
    
    private void setupEventListeners() {
        // Evento de cierre de ventana
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                cerrarSesion();
            }
        });
        
        // Timer para actualizar hora
        Timer timer = new Timer(1000, e -> updateTime());
        timer.start();
    }
    
    private void updateUserInterface() {
        if (usuarioActual != null) {
            lblUsuarioActual.setText("👤 " + usuarioActual.getUsuNombre() + " (" + usuarioActual.getUsuRol() + ")");
        }
        updateTime();
    }
    
    private void updateTime() {
        String horaActual = java.time.LocalTime.now().format(
            java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss")
        );
        String fechaActual = java.time.LocalDate.now().format(
            java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")
        );
        lblHoraActual.setText("🕐 " + fechaActual + " " + horaActual);
    }
    
    private void showPanel(String panelName) {
        CardLayout cl = (CardLayout) panelContenido.getLayout();
        cl.show(panelContenido, panelName);
    }
    
    private void showDashboard() {
        showPanel("dashboard");
        // Select dashboard button by default
        Component[] components = ((JPanel) ((JPanel) getContentPane().getComponent(0)).getComponent(1)).getComponents();
        if (components.length > 1 && components[1] instanceof JButton) {
            try {
                components[1].getClass().getMethod("setSelected", boolean.class).invoke(components[1], true);
            } catch (Exception e) {
                // Ignore
            }
        }
    }
    
    private void cerrarSesion() {
        int opcion = JOptionPane.showConfirmDialog(this,
            "¿Está seguro que desea cerrar la sesión?",
            "Cerrar Sesión",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);
        
        if (opcion == JOptionPane.YES_OPTION) {
            dispose();
            SwingUtilities.invokeLater(() -> {
                new LoginWindowNew().setVisible(true);
            });
        }
    }
    
    /**
     * Actualiza el dashboard con datos frescos de la base de datos
     */
    public void actualizarDashboard() {
        SwingUtilities.invokeLater(() -> {
            try {
                // Actualizar KPIs del dashboard
                actualizarKPIs();
                
                // Actualizar cualquier tabla visible
                Component componenteActivo = panelContenido.getComponent(0);
                if (componenteActivo instanceof JPanel) {
                    actualizarTablasVisibles((JPanel) componenteActivo);
                }
                
            } catch (Exception e) {
                System.err.println("Error al actualizar dashboard: " + e.getMessage());
            }
        });
    }
    
    /**
     * Muestra el panel de dashboard
     */
    public void mostrarDashboard() {
        SwingUtilities.invokeLater(() -> {
            CardLayout cardLayout = (CardLayout) panelContenido.getLayout();
            cardLayout.show(panelContenido, "dashboard");
            actualizarKPIs();
        });
    }
    
    /**
     * Muestra el panel de reportes completos
     */
    public void mostrarPanelReportes() {
        SwingUtilities.invokeLater(() -> {
            CardLayout cardLayout = (CardLayout) panelContenido.getLayout();
            cardLayout.show(panelContenido, "reportes");
        });
    }
    
    /**
     * Actualiza los KPIs del dashboard con datos reales
     */
    private void actualizarKPIs() {
        try {
            System.out.println("Actualizando KPIs con datos reales...");
            
            // Obtener datos reales del dashboard
            ReportesDAOSimple reportesDAO = new ReportesDAOSimple();
            DashboardData dashboardData = reportesDAO.obtenerDatosDashboard();
            
            // También obtener datos básicos para complementar
            List<Activo> activos = activoService.obtenerTodosLosActivos();
            List<TipoActivo> tipos = activoService.obtenerTodosTiposActivos();
            List<Ubicacion> ubicaciones = activoService.obtenerTodasUbicaciones();
            
            // Actualizar KPIs principales con datos reales
            actualizarKPIEnPanel(panelContenido, "Total Activos", String.valueOf(dashboardData.getTotalActivos()));
            actualizarKPIEnPanel(panelContenido, "Operativos", String.valueOf(dashboardData.getActivosOperativos()));
            actualizarKPIEnPanel(panelContenido, "En Mantenimiento", String.valueOf(dashboardData.getActivosEnMantenimiento()));
            actualizarKPIEnPanel(panelContenido, "Fuera de Servicio", String.valueOf(dashboardData.getActivosFueraServicio()));
            
            // Actualizar KPIs complementarios
            actualizarKPIEnPanel(panelContenido, "Tipos de Activos", String.valueOf(tipos.size()));
            actualizarKPIEnPanel(panelContenido, "Ubicaciones", String.valueOf(ubicaciones.size()));
            actualizarKPIEnPanel(panelContenido, "Tickets Abiertos", String.valueOf(dashboardData.getTicketsAbiertos()));
            actualizarKPIEnPanel(panelContenido, "Mant. Vencidos", String.valueOf(dashboardData.getMantenimientosPendientes()));
            
            System.out.println("KPIs actualizados exitosamente");
            
        } catch (Exception e) {
            System.err.println("Error al actualizar KPIs con datos reales: " + e.getMessage());
            e.printStackTrace();
            
            // Fallback con datos básicos
            try {
                List<Activo> activos = activoService.obtenerTodosLosActivos();
                long operativos = activos.stream().filter(a -> a.getActEstado() == Activo.Estado.Operativo).count();
                long mantenimiento = activos.stream().filter(a -> a.getActEstado() == Activo.Estado.En_Mantenimiento).count();
                long fueraServicio = activos.stream().filter(a -> a.getActEstado() == Activo.Estado.Fuera_Servicio).count();
                
                actualizarKPIEnPanel(panelContenido, "Total Activos", String.valueOf(activos.size()));
                actualizarKPIEnPanel(panelContenido, "Operativos", String.valueOf(operativos));
                actualizarKPIEnPanel(panelContenido, "En Mantenimiento", String.valueOf(mantenimiento));
                actualizarKPIEnPanel(panelContenido, "Fuera de Servicio", String.valueOf(fueraServicio));
            } catch (Exception fallbackException) {
                System.err.println("Error en fallback de KPIs: " + fallbackException.getMessage());
            }
        }
    }
    
    /**
     * Inicia el timer para actualización automática del dashboard
     */
    private void iniciarTimerActualizacion() {
        // Timer para actualizar cada 5 minutos (300000 ms)
        Timer timerActualizacion = new Timer(300000, e -> {
            System.out.println("Ejecutando actualización automática del dashboard...");
            actualizarDashboard();
        });
        timerActualizacion.setRepeats(true);
        timerActualizacion.start();
        
        System.out.println("Timer de actualización automática iniciado (cada 5 minutos)");
    }
    
    /**
     * Actualiza un KPI específico en el panel
     */
    private void actualizarKPIEnPanel(JPanel panel, String tituloKPI, String nuevoValor) {
        for (Component comp : panel.getComponents()) {
            if (comp instanceof JPanel) {
                actualizarKPIEnPanelRecursivo((JPanel) comp, tituloKPI, nuevoValor);
            }
        }
    }
    
    /**
     * Busca y actualiza KPI de forma recursiva
     */
    private void actualizarKPIEnPanelRecursivo(JPanel panel, String tituloKPI, String nuevoValor) {
        for (Component comp : panel.getComponents()) {
            if (comp instanceof JPanel) {
                actualizarKPIEnPanelRecursivo((JPanel) comp, tituloKPI, nuevoValor);
            } else if (comp instanceof JLabel) {
                JLabel label = (JLabel) comp;
                if (label.getText().contains(tituloKPI)) {
                    // Buscar el label del valor (usualmente el siguiente)
                    Component parent = label.getParent();
                    if (parent instanceof JPanel) {
                        Component[] hermanos = ((JPanel) parent).getComponents();
                        for (int i = 0; i < hermanos.length; i++) {
                            if (hermanos[i] == label && i + 1 < hermanos.length) {
                                if (hermanos[i + 1] instanceof JLabel) {
                                    ((JLabel) hermanos[i + 1]).setText(nuevoValor);
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    /**
     * Actualiza las tablas visibles
     */
    private void actualizarTablasVisibles(JPanel panel) {
        for (Component comp : panel.getComponents()) {
            if (comp instanceof JScrollPane) {
                JScrollPane scroll = (JScrollPane) comp;
                if (scroll.getViewport().getView() instanceof JTable) {
                    JTable tabla = (JTable) scroll.getViewport().getView();
                    if (tabla.getModel() instanceof DefaultTableModel) {
                        // Aquí se podría recargar los datos de la tabla
                        // Por ahora solo notificamos que los datos cambiaron
                        tabla.repaint();
                    }
                }
            } else if (comp instanceof JPanel) {
                actualizarTablasVisibles((JPanel) comp);
            }
        }
    }
    
    // ===== MÉTODOS AUXILIARES PARA MANTENIMIENTO =====
    
    private JPanel createKpiPanel(String titulo, String valor, String icono, Color color) {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(COLOR_BLANCO);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color, 2),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        panel.setPreferredSize(new Dimension(180, 120));
        
        JLabel lblIcono = new JLabel(icono, SwingConstants.CENTER);
        lblIcono.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 32));
        
        JLabel lblValor = new JLabel(valor, SwingConstants.CENTER);
        lblValor.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblValor.setForeground(color);
        
        JLabel lblTitulo = new JLabel(titulo, SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblTitulo.setForeground(COLOR_GRIS_TEXTO);
        
        panel.add(lblIcono, BorderLayout.NORTH);
        panel.add(lblValor, BorderLayout.CENTER);
        panel.add(lblTitulo, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private void updateKpiValue(JPanel kpiPanel, String newValue) {
        Component[] components = kpiPanel.getComponents();
        for (Component comp : components) {
            if (comp instanceof JLabel) {
                JLabel label = (JLabel) comp;
                // Buscar el label del valor (el que tiene font bold y grande)
                if (label.getFont().isBold() && label.getFont().getSize() == 24) {
                    label.setText(newValue);
                    break;
                }
            }
        }
        kpiPanel.repaint();
    }
    
    private JPanel createRoleInfoPanel(String titulo, String descripcion, Color color) {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBackground(COLOR_BLANCO);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color, 1),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        
        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTitulo.setForeground(color);
        
        JLabel lblDescripcion = new JLabel("<html><small>" + descripcion + "</small></html>");
        lblDescripcion.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblDescripcion.setForeground(COLOR_GRIS_TEXTO);
        
        panel.add(lblTitulo, BorderLayout.NORTH);
        panel.add(lblDescripcion, BorderLayout.CENTER);
        
        return panel;
    }
    
    public static void main(String[] args) {
        // Para pruebas
        SwingUtilities.invokeLater(() -> {
            Usuario usuarioPrueba = new Usuario();
            usuarioPrueba.setUsuNombre("Usuario de Prueba");
            usuarioPrueba.setUsuRol(Usuario.Rol.Jefe_Informatica);
            
            new MainWindowNew(usuarioPrueba).setVisible(true);
        });
    }
}
