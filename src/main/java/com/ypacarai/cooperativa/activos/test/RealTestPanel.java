package com.ypacarai.cooperativa.activos.test;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.time.LocalDateTime;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

/**
 * Panel avanzado para pruebas reales con datos del sistema
 * Integra con la base de datos real y ejecuta escenarios completos
 */
public class RealTestPanel {
    
    private JFrame frame;
    private JTextArea txtResultados;
    private JLabel lblEstado;
    private RealTestService realTestService;
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new RealTestPanel().crearInterfaz());
    }
    
    public void crearInterfaz() {
        // Inicializar servicio
        realTestService = new RealTestService();
        
        frame = new JFrame("🧪 Pruebas Reales - Sistema de Gestión de Activos");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        
        // Panel superior con información
        JPanel panelSuperior = crearPanelSuperior();
        frame.add(panelSuperior, BorderLayout.NORTH);
        
        // Panel central con resultados
        JPanel panelCentral = crearPanelCentral();
        frame.add(panelCentral, BorderLayout.CENTER);
        
        // Panel inferior con botones de prueba
        JPanel panelBotones = crearPanelBotones();
        frame.add(panelBotones, BorderLayout.SOUTH);
        
        // Configurar frame
        frame.setSize(1000, 800);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        
        // Cargar resumen inicial
        cargarResumenInicial();
    }
    
    private JPanel crearPanelSuperior() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        panel.setBackground(new Color(248, 249, 250));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Título principal
        JLabel lblTitulo = new JLabel("🧪 Pruebas Reales del Sistema");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 24));
        lblTitulo.setForeground(new Color(52, 152, 219));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(lblTitulo, gbc);
        
        // Subtítulo
        JLabel lblSubtitulo = new JLabel("Integración con base de datos real y notificaciones por email");
        lblSubtitulo.setFont(new Font("Arial", Font.ITALIC, 14));
        lblSubtitulo.setForeground(new Color(108, 117, 125));
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2;
        panel.add(lblSubtitulo, gbc);
        
        // Estado del sistema
        lblEstado = new JLabel("🔄 Cargando estado del sistema...");
        lblEstado.setFont(new Font("Arial", Font.BOLD, 16));
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(lblEstado, gbc);
        
        // Botón para abrir MailHog
        JButton btnMailHog = new JButton("🌐 Abrir MailHog");
        btnMailHog.setBackground(new Color(40, 167, 69));
        btnMailHog.setForeground(Color.WHITE);
        btnMailHog.setFont(new Font("Arial", Font.BOLD, 12));
        btnMailHog.setFocusPainted(false);
        btnMailHog.addActionListener(e -> abrirMailHog());
        gbc.gridx = 1; gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(btnMailHog, gbc);
        
        return panel;
    }
    
    private JPanel crearPanelCentral() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(0, 15, 15, 15));
        
        // Área de resultados
        txtResultados = new JTextArea();
        txtResultados.setEditable(false);
        txtResultados.setFont(new Font("Courier New", Font.PLAIN, 12));
        txtResultados.setBackground(new Color(248, 249, 250));
        txtResultados.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JScrollPane scrollPane = new JScrollPane(txtResultados);
        scrollPane.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(52, 152, 219), 2),
            " 📋 Resultados de Pruebas ",
            0, 0, new Font("Arial", Font.BOLD, 14), new Color(52, 152, 219)
        ));
        
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }
    
    private JPanel crearPanelBotones() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        panel.setBackground(new Color(248, 249, 250));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        
        // Fila 1: Pruebas principales
        gbc.gridy = 0;
        
        gbc.gridx = 0;
        panel.add(crearBoton("🔧 Prueba Activos Reales", 
            "Envía notificaciones usando activos reales del sistema",
            new Color(52, 152, 219), e -> ejecutarPruebaActivos()), gbc);
        
        gbc.gridx = 1;
        panel.add(crearBoton("🎫 Prueba Tickets Reales", 
            "Envía notificaciones usando tickets reales del sistema",
            new Color(255, 193, 7), e -> ejecutarPruebaTickets()), gbc);
        
        gbc.gridx = 2;
        panel.add(crearBoton("⛔ Activos Fuera Servicio", 
            "Simula notificaciones de activos con problemas",
            new Color(220, 53, 69), e -> ejecutarPruebaFueraServicio()), gbc);
        
        // Fila 2: Pruebas avanzadas
        gbc.gridy = 1;
        
        gbc.gridx = 0;
        panel.add(crearBoton("🚀 Prueba de Estrés", 
            "Envía múltiples notificaciones en ráfaga",
            new Color(111, 66, 193), e -> ejecutarPruebaEstres()), gbc);
        
        gbc.gridx = 1;
        panel.add(crearBoton("📊 Resumen Sistema", 
            "Muestra estadísticas actuales del sistema",
            new Color(40, 167, 69), e -> mostrarResumenSistema()), gbc);
        
        gbc.gridx = 2;
        panel.add(crearBoton("🧹 Limpiar", 
            "Limpia el área de resultados",
            new Color(108, 117, 125), e -> limpiarResultados()), gbc);
        
        // Fila 3: Notificaciones masivas
        gbc.gridy = 2;
        gbc.gridx = 0;
        gbc.gridwidth = 3; // Ocupa todo el ancho
        JButton btnMasivo = crearBoton("🚨 NOTIFICACIONES MASIVAS", 
            "Envía notificaciones usando TODOS los datos del sistema - Prueba completa",
            new Color(220, 20, 60), e -> ejecutarNotificacionesMasivas());
        btnMasivo.setPreferredSize(new Dimension(600, 60));
        btnMasivo.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(btnMasivo, gbc);
        gbc.gridwidth = 1; // Resetear para futuros elementos
        
        return panel;
    }
    
    private JButton crearBoton(String texto, String tooltip, Color color, java.awt.event.ActionListener action) {
        JButton boton = new JButton(texto);
        boton.setToolTipText(tooltip);
        boton.setBackground(color);
        boton.setForeground(Color.WHITE);
        boton.setFont(new Font("Arial", Font.BOLD, 12));
        boton.setFocusPainted(false);
        boton.setPreferredSize(new Dimension(200, 50));
        boton.addActionListener(action);
        return boton;
    }
    
    private void cargarResumenInicial() {
        new Thread(() -> {
            try {
                String resumen = realTestService.obtenerResumenSistema();
                SwingUtilities.invokeLater(() -> {
                    txtResultados.setText(resumen);
                    lblEstado.setText("✅ Sistema listo para pruebas");
                    lblEstado.setForeground(new Color(40, 167, 69));
                });
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> {
                    lblEstado.setText("❌ Error conectando al sistema");
                    lblEstado.setForeground(new Color(220, 53, 69));
                    txtResultados.setText("ERROR cargando resumen inicial: " + e.getMessage());
                });
            }
        }).start();
    }
    
    private void ejecutarPruebaActivos() {
        ejecutarPruebaAsincrona("Activos Reales", () -> realTestService.ejecutarPruebaActivosReales());
    }
    
    private void ejecutarPruebaTickets() {
        ejecutarPruebaAsincrona("Tickets Reales", () -> realTestService.ejecutarPruebaTicketsReales());
    }
    
    private void ejecutarPruebaFueraServicio() {
        ejecutarPruebaAsincrona("Fuera de Servicio", () -> realTestService.ejecutarPruebaActivosFueraServicio());
    }
    
    private void ejecutarPruebaEstres() {
        int confirmacion = JOptionPane.showConfirmDialog(
            frame,
            "Esta prueba enviará 5 emails rápidamente.\n¿Desea continuar?",
            "Confirmar Prueba de Estrés",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );
        
        if (confirmacion == JOptionPane.YES_OPTION) {
            ejecutarPruebaAsincrona("Estrés", () -> realTestService.ejecutarPruebaEstres());
        }
    }
    
    private void mostrarResumenSistema() {
        ejecutarPruebaAsincrona("Resumen", () -> realTestService.obtenerResumenSistema());
    }
    
    private void ejecutarPruebaAsincrona(String nombrePrueba, java.util.function.Supplier<String> prueba) {
        SwingUtilities.invokeLater(() -> {
            txtResultados.append(String.format("\\n[%s] Iniciando prueba: %s...\\n", 
                LocalDateTime.now().toString().substring(11, 19), nombrePrueba));
            lblEstado.setText("🔄 Ejecutando prueba: " + nombrePrueba);
            lblEstado.setForeground(new Color(255, 193, 7));
        });
        
        new Thread(() -> {
            try {
                String resultado = prueba.get();
                SwingUtilities.invokeLater(() -> {
                    txtResultados.append(resultado);
                    txtResultados.append("\\n" + "=".repeat(60) + "\\n");
                    txtResultados.setCaretPosition(txtResultados.getDocument().getLength());
                    lblEstado.setText("✅ Prueba completada: " + nombrePrueba);
                    lblEstado.setForeground(new Color(40, 167, 69));
                });
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> {
                    txtResultados.append(String.format("❌ ERROR EN PRUEBA %s: %s\\n\\n", nombrePrueba, e.getMessage()));
                    txtResultados.setCaretPosition(txtResultados.getDocument().getLength());
                    lblEstado.setText("❌ Error en prueba: " + nombrePrueba);
                    lblEstado.setForeground(new Color(220, 53, 69));
                });
            }
        }).start();
    }
    
    private void abrirMailHog() {
        try {
            Desktop.getDesktop().browse(java.net.URI.create("http://localhost:8025"));
            txtResultados.append(String.format("\\n[%s] Abriendo MailHog en navegador...\\n", 
                LocalDateTime.now().toString().substring(11, 19)));
        } catch (Exception e) {
            txtResultados.append(String.format("❌ Error abriendo MailHog: %s\\n", e.getMessage()));
        }
    }
    
    private void limpiarResultados() {
        txtResultados.setText("");
        lblEstado.setText("📋 Área de resultados limpiada");
        lblEstado.setForeground(new Color(108, 117, 125));
    }
    
    private void ejecutarNotificacionesMasivas() {
        // Confirmación antes de ejecutar
        int confirmacion = JOptionPane.showConfirmDialog(
            null,
            "⚠️ Esta operación enviará notificaciones usando TODOS los datos del sistema.\n" +
            "Esto puede generar un gran volumen de emails de prueba.\n\n" +
            "¿Está seguro que desea continuar?",
            "Confirmación - Notificaciones Masivas",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );
        
        if (confirmacion == JOptionPane.YES_OPTION) {
            ejecutarPruebaAsincrona("Notificaciones Masivas", () -> realTestService.ejecutarNotificacionesMasivas());
        }
    }
}