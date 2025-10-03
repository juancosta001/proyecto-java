package com.ypacarai.cooperativa.activos.view;

import com.ypacarai.cooperativa.activos.model.*;
import com.ypacarai.cooperativa.activos.service.MantenimientoTercerizadoService;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Ventana para mostrar los detalles completos de un mantenimiento tercerizado
 * Sistema de Gestión de Activos - Cooperativa Ypacaraí LTDA
 */
public class DetallesMantenimientoWindow extends JDialog {
    
    // Colores corporativos
    private static final Color COLOR_VERDE_COOPERATIVA = new Color(34, 139, 34);
    private static final Color COLOR_VERDE_CLARO = new Color(144, 238, 144);
    private static final Color COLOR_BLANCO = Color.WHITE;
    private static final Color COLOR_GRIS_CLARO = new Color(245, 245, 245);
    private static final Color COLOR_GRIS_TEXTO = new Color(64, 64, 64);
    private static final Color COLOR_AZUL_INFO = new Color(70, 130, 180);
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    
    private final MantenimientoTercerizadoService service;
    private final MantenimientoTercerizado mantenimiento;
    
    public DetallesMantenimientoWindow(Component parent, MantenimientoTercerizado mantenimiento) {
        super((JFrame) SwingUtilities.getWindowAncestor(parent), true);
        this.service = new MantenimientoTercerizadoService();
        this.mantenimiento = mantenimiento;
        
        initializeComponents();
        cargarDatos();
        
        setLocationRelativeTo(parent);
    }
    
    private void initializeComponents() {
        setTitle("📋 Detalles del Mantenimiento Tercerizado - ID: " + mantenimiento.getMantTercId());
        setSize(800, 700);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        
        // Panel principal con scroll
        JPanel panelPrincipal = new JPanel(new BorderLayout());
        panelPrincipal.setBorder(new EmptyBorder(20, 20, 20, 20));
        panelPrincipal.setBackground(COLOR_BLANCO);
        
        // Panel de contenido
        JPanel contenidoPanel = new JPanel();
        contenidoPanel.setLayout(new BoxLayout(contenidoPanel, BoxLayout.Y_AXIS));
        contenidoPanel.setBackground(COLOR_BLANCO);
        
        // Header
        contenidoPanel.add(createHeaderPanel());
        contenidoPanel.add(Box.createVerticalStrut(20));
        
        // Información general
        contenidoPanel.add(createInfoGeneralPanel());
        contenidoPanel.add(Box.createVerticalStrut(15));
        
        // Información del activo
        contenidoPanel.add(createActivoPanel());
        contenidoPanel.add(Box.createVerticalStrut(15));
        
        // Información del proveedor
        contenidoPanel.add(createProveedorPanel());
        contenidoPanel.add(Box.createVerticalStrut(15));
        
        // Detalles del mantenimiento
        contenidoPanel.add(createDetallesPanel());
        contenidoPanel.add(Box.createVerticalStrut(15));
        
        // Timeline de estados
        contenidoPanel.add(createTimelinePanel());
        
        // Scroll para todo el contenido
        JScrollPane scrollPane = new JScrollPane(contenidoPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        panelPrincipal.add(scrollPane, BorderLayout.CENTER);
        
        // Botón cerrar
        JPanel buttonPanel = createButtonPanel();
        panelPrincipal.add(buttonPanel, BorderLayout.SOUTH);
        
        add(panelPrincipal);
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        
        // Icono y título
        JLabel lblIcono = new JLabel("📋");
        lblIcono.setFont(new Font("Segoe UI", Font.PLAIN, 32));
        
        JPanel titlePanel = new JPanel();
        titlePanel.setOpaque(false);
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        
        JLabel lblTitulo = new JLabel("Detalles del Mantenimiento Tercerizado");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitulo.setForeground(COLOR_VERDE_COOPERATIVA);
        
        JLabel lblSubtitulo = new JLabel("ID: " + mantenimiento.getMantTercId() + " - Estado: " + mantenimiento.getEstado());
        lblSubtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblSubtitulo.setForeground(COLOR_GRIS_TEXTO);
        
        titlePanel.add(lblTitulo);
        titlePanel.add(lblSubtitulo);
        
        panel.add(lblIcono, BorderLayout.WEST);
        panel.add(Box.createHorizontalStrut(15), BorderLayout.CENTER);
        panel.add(titlePanel, BorderLayout.EAST);
        
        return panel;
    }
    
    private JPanel createInfoGeneralPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(COLOR_GRIS_CLARO);
        panel.setBorder(createTitledBorder("📊 Información General"));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Fila 1: ID y Estado
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(createLabelBold("ID:"), gbc);
        gbc.gridx = 1;
        panel.add(createLabelValue(String.valueOf(mantenimiento.getMantTercId())), gbc);
        
        gbc.gridx = 2;
        panel.add(createLabelBold("Estado:"), gbc);
        gbc.gridx = 3;
        panel.add(createEstadoLabel(mantenimiento.getEstado().toString()), gbc);
        
        // Fila 2: Fechas
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(createLabelBold("Fecha Creación:"), gbc);
        gbc.gridx = 1;
        panel.add(createLabelValue(formatDate(mantenimiento.getCreadoEn())), gbc);
        
        if (mantenimiento.getFechaRetiro() != null) {
            gbc.gridx = 2;
            panel.add(createLabelBold("Fecha Retiro:"), gbc);
            gbc.gridx = 3;
            panel.add(createLabelValue(formatDate(mantenimiento.getFechaRetiro())), gbc);
        }
        
        // Fila 3: Entrega
        if (mantenimiento.getFechaEntrega() != null) {
            gbc.gridx = 0; gbc.gridy = 2;
            panel.add(createLabelBold("Fecha Entrega:"), gbc);
            gbc.gridx = 1;
            panel.add(createLabelValue(formatDate(mantenimiento.getFechaEntrega())), gbc);
        
            if (mantenimiento.isGarantia() && mantenimiento.getDiasGarantia() > 0) {
                gbc.gridx = 2; gbc.gridy = 2;
                panel.add(createLabelBold("Garantía días:"), gbc);
                gbc.gridx = 3;
                panel.add(createLabelValue(String.valueOf(mantenimiento.getDiasGarantia())), gbc);
            }
        }
        
        return panel;
    }
    
    private JPanel createActivoPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(COLOR_BLANCO);
        panel.setBorder(createTitledBorder("🖥️ Información del Activo"));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Código y Nombre
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(createLabelBold("Código:"), gbc);
        gbc.gridx = 1;
        panel.add(createLabelValue(mantenimiento.getNumeroActivo() != null ? mantenimiento.getNumeroActivo() : "N/A"), gbc);
        
        gbc.gridx = 2;
        panel.add(createLabelBold("Marca:"), gbc);
        gbc.gridx = 3;
        panel.add(createLabelValue(mantenimiento.getMarcaActivo() != null ? mantenimiento.getMarcaActivo() : "N/A"), gbc);
        
        return panel;
    }
    
    private JPanel createProveedorPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(COLOR_GRIS_CLARO);
        panel.setBorder(createTitledBorder("🏢 Información del Proveedor"));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Nombre y Contacto
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(createLabelBold("Proveedor:"), gbc);
        gbc.gridx = 1;
        panel.add(createLabelValue(mantenimiento.getNombreProveedor() != null ? mantenimiento.getNombreProveedor() : "N/A"), gbc);
        
        gbc.gridx = 2;
        panel.add(createLabelBold("Teléfono:"), gbc);
        gbc.gridx = 3;
        panel.add(createLabelValue(mantenimiento.getTelefonoProveedor() != null ? mantenimiento.getTelefonoProveedor() : "N/A"), gbc);
        
        return panel;
    }
    
    private JPanel createDetallesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(COLOR_BLANCO);
        panel.setBorder(createTitledBorder("📝 Detalles del Mantenimiento"));
        
        JPanel contenido = new JPanel();
        contenido.setLayout(new BoxLayout(contenido, BoxLayout.Y_AXIS));
        contenido.setBackground(COLOR_BLANCO);
        contenido.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Descripción del problema
        if (mantenimiento.getDescripcionProblema() != null && !mantenimiento.getDescripcionProblema().trim().isEmpty()) {
            contenido.add(createDetailSection("🔧 Problema Reportado:", mantenimiento.getDescripcionProblema()));
            contenido.add(Box.createVerticalStrut(10));
        }
        
        // Servicio realizado
        if (mantenimiento.getTrabajoRealizado() != null && !mantenimiento.getTrabajoRealizado().trim().isEmpty()) {
            contenido.add(createDetailSection("✅ Servicio Realizado:", mantenimiento.getTrabajoRealizado()));
            contenido.add(Box.createVerticalStrut(10));
        }
        
        // Observaciones del retiro
        if (mantenimiento.getObservacionesRetiro() != null && !mantenimiento.getObservacionesRetiro().trim().isEmpty()) {
            contenido.add(createDetailSection("📤 Observaciones del Retiro:", mantenimiento.getObservacionesRetiro()));
            contenido.add(Box.createVerticalStrut(10));
        }
        
        // Observaciones de entrega
        if (mantenimiento.getObservacionesEntrega() != null && !mantenimiento.getObservacionesEntrega().trim().isEmpty()) {
            contenido.add(createDetailSection("📥 Observaciones de Entrega:", mantenimiento.getObservacionesEntrega()));
            contenido.add(Box.createVerticalStrut(10));
        }
        
        // Información de costos
        JPanel costosPanel = new JPanel(new GridBagLayout());
        costosPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.anchor = GridBagConstraints.WEST;
        
        if (mantenimiento.getMontoPresupuestado() != null) {
            gbc.gridx = 0; gbc.gridy = 0;
            costosPanel.add(createLabelBold("💰 Costo Estimado:"), gbc);
            gbc.gridx = 1;
            costosPanel.add(createLabelValue("₲ " + formatMoney(mantenimiento.getMontoPresupuestado())), gbc);
        }
        
        if (mantenimiento.getMontoCobrado() != null) {
            gbc.gridx = 2; gbc.gridy = 0;
            costosPanel.add(createLabelBold("💳 Costo Final:"), gbc);
            gbc.gridx = 3;
            costosPanel.add(createLabelValue("₲ " + formatMoney(mantenimiento.getMontoCobrado())), gbc);
        }
        
        contenido.add(costosPanel);
        
        panel.add(contenido, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createTimelinePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(COLOR_GRIS_CLARO);
        panel.setBorder(createTitledBorder("📅 Cronología del Mantenimiento"));
        
        JPanel timeline = new JPanel();
        timeline.setLayout(new BoxLayout(timeline, BoxLayout.Y_AXIS));
        timeline.setBackground(COLOR_GRIS_CLARO);
        timeline.setBorder(new EmptyBorder(10, 20, 10, 20));
        
        // Eventos del timeline
        if (mantenimiento.getCreadoEn() != null) {
            timeline.add(createTimelineItem("📝", "Solicitud Creada", 
                formatDate(mantenimiento.getCreadoEn()), true));
        }
        
        if (mantenimiento.getFechaRetiro() != null) {
            timeline.add(createTimelineItem("📤", "Equipo Retirado", 
                formatDate(mantenimiento.getFechaRetiro()), true));
        }
        
        if (mantenimiento.getFechaEntrega() != null) {
            timeline.add(createTimelineItem("📥", "Equipo Entregado", 
                formatDate(mantenimiento.getFechaEntrega()), true));
        }
        
        if (mantenimiento.isGarantia() && mantenimiento.getDiasGarantia() > 0) {
            LocalDate fechaVencimiento = mantenimiento.getFechaEntrega().plusDays(mantenimiento.getDiasGarantia());
            timeline.add(createTimelineItem("🛡️", "Garantía Vence", 
                formatDate(fechaVencimiento), false));
        }
        
        panel.add(timeline, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(10, 0, 0, 0));
        
        JButton btnCerrar = new JButton("✖️ Cerrar");
        btnCerrar.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnCerrar.setBackground(COLOR_VERDE_COOPERATIVA);
        btnCerrar.setForeground(COLOR_BLANCO);
        btnCerrar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COLOR_VERDE_COOPERATIVA.darker()),
            new EmptyBorder(8, 20, 8, 20)
        ));
        btnCerrar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCerrar.setFocusPainted(false);
        btnCerrar.addActionListener(e -> dispose());
        
        panel.add(btnCerrar);
        
        return panel;
    }
    
    private void cargarDatos() {
        // Los datos ya vienen en el objeto mantenimiento
        // Aquí podríamos cargar datos adicionales si fuera necesario
    }
    
    // Métodos auxiliares
    
    private TitledBorder createTitledBorder(String title) {
        TitledBorder border = BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(COLOR_VERDE_CLARO, 2), 
            title,
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 12),
            COLOR_VERDE_COOPERATIVA
        );
        return border;
    }
    
    private JLabel createLabelBold(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        label.setForeground(COLOR_GRIS_TEXTO);
        return label;
    }
    
    private JLabel createLabelValue(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        label.setForeground(Color.BLACK);
        return label;
    }
    
    private JLabel createEstadoLabel(String estado) {
        JLabel label = new JLabel(estado);
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        label.setOpaque(true);
        label.setBorder(new EmptyBorder(2, 8, 2, 8));
        
        switch (estado.toLowerCase()) {
            case "solicitado":
                label.setBackground(COLOR_AZUL_INFO);
                label.setForeground(COLOR_BLANCO);
                break;
            case "en_proceso":
                label.setBackground(Color.ORANGE);
                label.setForeground(COLOR_BLANCO);
                break;
            case "finalizado":
                label.setBackground(COLOR_VERDE_COOPERATIVA);
                label.setForeground(COLOR_BLANCO);
                break;
            default:
                label.setBackground(COLOR_GRIS_CLARO);
                label.setForeground(COLOR_GRIS_TEXTO);
        }
        
        return label;
    }
    
    private JPanel createDetailSection(String titulo, String contenido) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        
        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblTitulo.setForeground(COLOR_VERDE_COOPERATIVA);
        
        JTextArea txtContenido = new JTextArea(contenido);
        txtContenido.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        txtContenido.setEditable(false);
        txtContenido.setOpaque(false);
        txtContenido.setLineWrap(true);
        txtContenido.setWrapStyleWord(true);
        txtContenido.setBorder(new EmptyBorder(5, 10, 5, 10));
        txtContenido.setBackground(COLOR_GRIS_CLARO);
        
        panel.add(lblTitulo, BorderLayout.NORTH);
        panel.add(txtContenido, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createTimelineItem(String icono, String evento, String fecha, boolean completado) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(5, 0, 5, 0));
        
        // Icono
        JLabel lblIcono = new JLabel(icono);
        lblIcono.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblIcono.setPreferredSize(new Dimension(30, 20));
        
        // Contenido
        JPanel contenido = new JPanel(new BorderLayout());
        contenido.setOpaque(false);
        contenido.setBorder(new EmptyBorder(0, 10, 0, 0));
        
        JLabel lblEvento = new JLabel(evento);
        lblEvento.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblEvento.setForeground(completado ? Color.BLACK : COLOR_GRIS_TEXTO);
        
        JLabel lblFecha = new JLabel(fecha);
        lblFecha.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblFecha.setForeground(COLOR_GRIS_TEXTO);
        
        contenido.add(lblEvento, BorderLayout.NORTH);
        contenido.add(lblFecha, BorderLayout.SOUTH);
        
        panel.add(lblIcono, BorderLayout.WEST);
        panel.add(contenido, BorderLayout.CENTER);
        
        return panel;
    }
    
    private String formatDate(Object date) {
        if (date == null) return "N/A";
        if (date instanceof java.time.LocalDate) {
            return ((java.time.LocalDate) date).format(DATE_FORMATTER);
        }
        if (date instanceof java.time.LocalDateTime) {
            return ((java.time.LocalDateTime) date).format(DATETIME_FORMATTER);
        }
        return date.toString();
    }
    
    private String formatMoney(Object amount) {
        if (amount == null) return "0";
        String amountStr = amount.toString();
        try {
            double value = Double.parseDouble(amountStr);
            return String.format("%,.0f", value);
        } catch (NumberFormatException e) {
            return amountStr; // Return original if can't parse
        }
    }
}