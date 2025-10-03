package com.ypacarai.cooperativa.activos.view;

import com.ypacarai.cooperativa.activos.model.*;
import com.ypacarai.cooperativa.activos.service.MantenimientoTercerizadoService;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Ventana para registrar retiro y entrega de equipos en mantenimiento tercerizado
 * Sistema de Gestión de Activos - Cooperativa Ypacaraí LTDA
 */
public class RetiroEntregaWindow extends JDialog {
    
    // Colores corporativos
    private static final Color COLOR_VERDE_COOPERATIVA = new Color(34, 139, 34);
    private static final Color COLOR_VERDE_CLARO = new Color(144, 238, 144);
    private static final Color COLOR_BLANCO = Color.WHITE;
    private static final Color COLOR_GRIS_CLARO = new Color(245, 245, 245);
    private static final Color COLOR_GRIS_TEXTO = new Color(64, 64, 64);
    private static final Color COLOR_AZUL_INFO = new Color(70, 130, 180);
    private static final Color COLOR_NARANJA_WARNING = new Color(255, 140, 0);
    
    private final MantenimientoTercerizadoService service;
    private final MantenimientoTercerizado mantenimiento;
    private final boolean esRetiro; // true = retiro, false = entrega
    private final Usuario usuarioActual;
    private final MantenimientoTercerizadoPanel panelPadre;
    
    // Componentes del formulario
    private JTextArea txtObservaciones;
    private JTextField txtCostoFinal;
    private JSpinner spnGarantiaDias;
    private JTextArea txtServicioRealizado;
    private JComboBox<String> cmbEstadoPago;
    
    public RetiroEntregaWindow(MantenimientoTercerizadoPanel parent, MantenimientoTercerizado mantenimiento, 
                              boolean esRetiro, Usuario usuario) {
        super((JFrame) SwingUtilities.getWindowAncestor(parent), true);
        this.panelPadre = parent;
        this.service = new MantenimientoTercerizadoService();
        this.mantenimiento = mantenimiento;
        this.esRetiro = esRetiro;
        this.usuarioActual = usuario;
        
        initializeComponents();
        setupEventListeners();
        
        setLocationRelativeTo(parent);
    }
    
    private void initializeComponents() {
        String titulo = esRetiro ? "📤 Registrar Retiro de Equipo" : "📥 Registrar Entrega de Equipo";
        setTitle(titulo);
        setSize(600, esRetiro ? 500 : 700);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        
        // Panel principal
        JPanel panelPrincipal = new JPanel(new BorderLayout());
        panelPrincipal.setBorder(new EmptyBorder(20, 20, 20, 20));
        panelPrincipal.setBackground(COLOR_BLANCO);
        
        // Header con información del mantenimiento
        JPanel headerPanel = createHeaderPanel();
        panelPrincipal.add(headerPanel, BorderLayout.NORTH);
        
        // Formulario específico según el tipo (retiro/entrega)
        JPanel formPanel = esRetiro ? createRetiroPanel() : createEntregaPanel();
        panelPrincipal.add(formPanel, BorderLayout.CENTER);
        
        // Botones de acción
        JPanel buttonPanel = createButtonPanel();
        panelPrincipal.add(buttonPanel, BorderLayout.SOUTH);
        
        add(panelPrincipal);
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(0, 0, 20, 0));
        
        // Icono
        String icono = esRetiro ? "📤" : "📥";
        JLabel lblIcono = new JLabel(icono);
        lblIcono.setFont(new Font("Segoe UI", Font.PLAIN, 32));
        
        // Información del mantenimiento
        JPanel infoPanel = new JPanel();
        infoPanel.setOpaque(false);
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        
        JLabel lblTitulo = new JLabel(esRetiro ? "Registrar Retiro de Equipo" : "Registrar Entrega de Equipo");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitulo.setForeground(COLOR_VERDE_COOPERATIVA);
        
        String infoText = String.format(
            "<html>" +
            "<b>Mantenimiento ID:</b> %d<br>" +
            "<b>Estado Actual:</b> %s<br>" +
            "<b>Proveedor:</b> %s" +
            "</html>",
            mantenimiento.getMantTercId(),
            mantenimiento.getEstado(),
            mantenimiento.getNombreProveedor() != null ? mantenimiento.getNombreProveedor() : "N/A"
        );
        
        JLabel lblInfo = new JLabel(infoText);
        lblInfo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblInfo.setForeground(COLOR_GRIS_TEXTO);
        
        infoPanel.add(lblTitulo);
        infoPanel.add(Box.createVerticalStrut(10));
        infoPanel.add(lblInfo);
        
        panel.add(lblIcono, BorderLayout.WEST);
        panel.add(Box.createHorizontalStrut(15), BorderLayout.CENTER);
        panel.add(infoPanel, BorderLayout.EAST);
        
        return panel;
    }
    
    private JPanel createRetiroPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.NORTHWEST;
        
        // Observaciones del retiro
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("* Observaciones del Retiro:"), gbc);
        
        gbc.gridx = 1; gbc.fill = GridBagConstraints.BOTH; gbc.weightx = 1.0; gbc.weighty = 1.0;
        txtObservaciones = new JTextArea(6, 0);
        txtObservaciones.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtObservaciones.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COLOR_VERDE_CLARO),
            BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));
        txtObservaciones.setLineWrap(true);
        txtObservaciones.setWrapStyleWord(true);
        
        JScrollPane scrollObservaciones = new JScrollPane(txtObservaciones);
        scrollObservaciones.setPreferredSize(new Dimension(400, 150));
        panel.add(scrollObservaciones, gbc);
        
        // Ayuda
        gbc.gridx = 1; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0; gbc.weighty = 0;
        JLabel lblAyuda = new JLabel("<html><i><small>Registre las condiciones en que se retiró el equipo, fecha/hora, persona responsable, etc.</small></i></html>");
        lblAyuda.setFont(new Font("Segoe UI", Font.ITALIC, 10));
        lblAyuda.setForeground(COLOR_GRIS_TEXTO);
        panel.add(lblAyuda, gbc);
        
        return panel;
    }
    
    private JPanel createEntregaPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.NORTHWEST;
        
        // Servicio realizado
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("* Servicio Realizado:"), gbc);
        
        gbc.gridx = 1; gbc.fill = GridBagConstraints.BOTH; gbc.weightx = 1.0; gbc.weighty = 0.4;
        txtServicioRealizado = new JTextArea(4, 0);
        txtServicioRealizado.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtServicioRealizado.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COLOR_VERDE_CLARO),
            BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));
        txtServicioRealizado.setLineWrap(true);
        txtServicioRealizado.setWrapStyleWord(true);
        
        JScrollPane scrollServicio = new JScrollPane(txtServicioRealizado);
        scrollServicio.setPreferredSize(new Dimension(400, 100));
        panel.add(scrollServicio, gbc);
        
        // Costo final
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0; gbc.weighty = 0;
        panel.add(new JLabel("Costo Final (₲):"), gbc);
        
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 0.5;
        txtCostoFinal = new JTextField(15);
        txtCostoFinal.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtCostoFinal.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COLOR_VERDE_CLARO),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        panel.add(txtCostoFinal, gbc);
        
        // Estado de pago
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        panel.add(new JLabel("Estado de Pago:"), gbc);
        
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 0.5;
        cmbEstadoPago = new JComboBox<>(new String[]{"Pendiente", "Pagado"});
        cmbEstadoPago.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        panel.add(cmbEstadoPago, gbc);
        
        // Garantía en días
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0;
        panel.add(new JLabel("Garantía (días):"), gbc);
        
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 0.3;
        spnGarantiaDias = new JSpinner(new SpinnerNumberModel(0, 0, 365, 1));
        spnGarantiaDias.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        panel.add(spnGarantiaDias, gbc);
        
        // Observaciones de entrega
        gbc.gridx = 0; gbc.gridy = 4; gbc.anchor = GridBagConstraints.NORTHWEST;
        panel.add(new JLabel("Observaciones:"), gbc);
        
        gbc.gridx = 1; gbc.fill = GridBagConstraints.BOTH; gbc.weightx = 1.0; gbc.weighty = 0.6;
        txtObservaciones = new JTextArea(4, 0);
        txtObservaciones.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtObservaciones.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COLOR_VERDE_CLARO),
            BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));
        txtObservaciones.setLineWrap(true);
        txtObservaciones.setWrapStyleWord(true);
        
        JScrollPane scrollObservacionesEntrega = new JScrollPane(txtObservaciones);
        scrollObservacionesEntrega.setPreferredSize(new Dimension(400, 100));
        panel.add(scrollObservacionesEntrega, gbc);
        
        return panel;
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(20, 0, 0, 0));
        
        JButton btnCancelar = new JButton("❌ Cancelar");
        btnCancelar.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnCancelar.setBackground(COLOR_GRIS_CLARO);
        btnCancelar.setForeground(COLOR_GRIS_TEXTO);
        btnCancelar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY),
            new EmptyBorder(8, 15, 8, 15)
        ));
        btnCancelar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCancelar.setFocusPainted(false);
        btnCancelar.addActionListener(e -> dispose());
        
        String textoBtnPrincipal = esRetiro ? "📤 Registrar Retiro" : "📥 Registrar Entrega";
        JButton btnPrincipal = new JButton(textoBtnPrincipal);
        btnPrincipal.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnPrincipal.setBackground(COLOR_VERDE_COOPERATIVA);
        btnPrincipal.setForeground(COLOR_BLANCO);
        btnPrincipal.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COLOR_VERDE_COOPERATIVA.darker()),
            new EmptyBorder(8, 20, 8, 20)
        ));
        btnPrincipal.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnPrincipal.setFocusPainted(false);
        btnPrincipal.addActionListener(e -> procesarAccion());
        
        panel.add(btnCancelar);
        panel.add(btnPrincipal);
        
        return panel;
    }
    
    private void setupEventListeners() {
        // Los listeners ya están configurados en createButtonPanel
    }
    
    private void procesarAccion() {
        try {
            if (esRetiro) {
                registrarRetiro();
            } else {
                registrarEntrega();
            }
        } catch (Exception e) {
            mostrarError("Error al procesar la acción: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void registrarRetiro() throws Exception {
        // Validar observaciones obligatorias
        if (txtObservaciones.getText().trim().isEmpty()) {
            mostrarError("Las observaciones del retiro son obligatorias");
            txtObservaciones.requestFocus();
            return;
        }
        
        // Registrar retiro
        boolean resultado = service.registrarRetiroEquipo(
            mantenimiento.getMantTercId(),
            LocalDate.now(),
            txtObservaciones.getText().trim()
        );
        
        if (resultado) {
            JOptionPane.showMessageDialog(this,
                "✅ Retiro registrado exitosamente.\n" +
                "El equipo ahora está en estado 'En Proceso'.",
                "Retiro Registrado",
                JOptionPane.INFORMATION_MESSAGE);
            
            // Refrescar datos en el panel padre
            if (panelPadre != null) {
                panelPadre.actualizarTablas();
            }
            
            dispose();
        } else {
            mostrarError("No se pudo registrar el retiro");
        }
    }
    
    private void registrarEntrega() throws Exception {
        // Validar servicio realizado obligatorio
        if (txtServicioRealizado.getText().trim().isEmpty()) {
            mostrarError("La descripción del servicio realizado es obligatoria");
            txtServicioRealizado.requestFocus();
            return;
        }
        
        // Obtener datos del formulario
        String servicioRealizado = txtServicioRealizado.getText().trim();
        String observaciones = txtObservaciones.getText().trim();
        int garantiaDias = (Integer) spnGarantiaDias.getValue();
        String estadoPago = (String) cmbEstadoPago.getSelectedItem();
        
        // Costo final (opcional)
        BigDecimal costoFinal = null;
        String costoStr = txtCostoFinal.getText().trim();
        if (!costoStr.isEmpty()) {
            try {
                costoFinal = new BigDecimal(costoStr.replace(".", "").replace(",", "."));
            } catch (NumberFormatException e) {
                mostrarError("El formato del costo final no es válido");
                txtCostoFinal.requestFocus();
                return;
            }
        }
        
        // Fecha de vencimiento de garantía
        LocalDate fechaVencimientoGarantia = null;
        if (garantiaDias > 0) {
            fechaVencimientoGarantia = LocalDate.now().plusDays(garantiaDias);
        }
        
        // Registrar entrega
        boolean resultado = service.registrarEntregaEquipo(
            mantenimiento.getMantTercId(),
            LocalDate.now(),
            servicioRealizado,
            observaciones,
            costoFinal,
            garantiaDias,
            fechaVencimientoGarantia,
            estadoPago
        );
        
        if (resultado) {
            String mensaje = "✅ Entrega registrada exitosamente.\n" +
                           "El mantenimiento ahora está finalizado.";
            
            if (garantiaDias > 0) {
                mensaje += "\n🛡️ Garantía: " + garantiaDias + " días (hasta " + 
                          fechaVencimientoGarantia.toString() + ")";
            }
            
            JOptionPane.showMessageDialog(this, mensaje, "Entrega Registrada", 
                JOptionPane.INFORMATION_MESSAGE);
            
            // Refrescar datos en el panel padre
            if (panelPadre != null) {
                panelPadre.actualizarTablas();
            }
            
            dispose();
        } else {
            mostrarError("No se pudo registrar la entrega");
        }
    }
    
    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this,
            "⚠️ " + mensaje,
            "Error",
            JOptionPane.WARNING_MESSAGE);
    }
}