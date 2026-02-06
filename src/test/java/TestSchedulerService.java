package com.ypacarai.cooperativa.activos.test;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.CompletableFuture;

import com.ypacarai.cooperativa.activos.service.SchedulerService;

/**
 * Panel de pruebas para el SchedulerService automático
 * Permite probar la funcionalidad crítica de automatización implementada
 * 
 * Sistema de Gestión de Activos - Cooperativa Ypacaraí LTDA
 */
public class TestSchedulerService extends JFrame {
    
    private static final long serialVersionUID = 1L;
    
    private SchedulerService schedulerService;
    private JTextArea logArea;
    private JLabel statusLabel;
    private JLabel estadisticasLabel;
    
    // Controles
    private JButton btnIniciar;
    private JButton btnDetener;
    private JButton btnEjecutarAlertasAhora;
    private JButton btnEjecutarMantenimientoAhora;
    private JButton btnVerEstado;
    private JButton btnLimpiarLog;
    
    public TestSchedulerService() {
        schedulerService = new SchedulerService();
        inicializarInterfaz();
        configurarEventos();
        actualizarEstado();
        
        log("🚀 Test del SchedulerService iniciado");
        log("📋 Ready para probar automatización de alertas y mantenimiento preventivo");
    }
    
    private void inicializarInterfaz() {
        setTitle("Test SchedulerService - Sistema de Activos Ypacaraí");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        // Panel superior - Estado
        JPanel panelEstado = new JPanel(new BorderLayout());
        panelEstado.setBorder(BorderFactory.createTitledBorder("Estado del Scheduler"));
        
        statusLabel = new JLabel("🔴 DETENIDO");
        statusLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        estadisticasLabel = new JLabel("Sin estadísticas disponibles");
        estadisticasLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        estadisticasLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        panelEstado.add(statusLabel, BorderLayout.NORTH);
        panelEstado.add(estadisticasLabel, BorderLayout.SOUTH);
        
        add(panelEstado, BorderLayout.NORTH);
        
        // Panel central - Controles
        JPanel panelControles = new JPanel(new GridLayout(2, 3, 10, 10));
        panelControles.setBorder(BorderFactory.createTitledBorder("Controles"));
        
        btnIniciar = new JButton("▶️ Iniciar Scheduler");
        btnIniciar.setBackground(new Color(34, 139, 34));
        btnIniciar.setForeground(Color.WHITE);
        
        btnDetener = new JButton("⏹️ Detener Scheduler");
        btnDetener.setBackground(new Color(220, 20, 60));
        btnDetener.setForeground(Color.WHITE);
        btnDetener.setEnabled(false);
        
        btnEjecutarAlertasAhora = new JButton("🔔 Ejecutar Alertas Ahora");
        btnEjecutarAlertasAhora.setBackground(new Color(255, 140, 0));
        btnEjecutarAlertasAhora.setForeground(Color.WHITE);
        
        btnEjecutarMantenimientoAhora = new JButton("🔧 Ejecutar Mantenimiento Ahora");
        btnEjecutarMantenimientoAhora.setBackground(new Color(70, 130, 180));
        btnEjecutarMantenimientoAhora.setForeground(Color.WHITE);
        
        btnVerEstado = new JButton("📊 Ver Estado Detallado");
        btnVerEstado.setBackground(new Color(128, 0, 128));
        btnVerEstado.setForeground(Color.WHITE);
        
        btnLimpiarLog = new JButton("🧹 Limpiar Log");
        
        panelControles.add(btnIniciar);
        panelControles.add(btnDetener);
        panelControles.add(btnEjecutarAlertasAhora);
        panelControles.add(btnEjecutarMantenimientoAhora);
        panelControles.add(btnVerEstado);
        panelControles.add(btnLimpiarLog);
        
        add(panelControles, BorderLayout.CENTER);
        
        // Panel inferior - Log
        JPanel panelLog = new JPanel(new BorderLayout());
        panelLog.setBorder(BorderFactory.createTitledBorder("Log de Actividad"));
        
        logArea = new JTextArea(15, 80);
        logArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 11));
        logArea.setEditable(false);
        logArea.setBackground(new Color(248, 248, 248));
        
        JScrollPane scrollPane = new JScrollPane(logArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        
        panelLog.add(scrollPane, BorderLayout.CENTER);
        add(panelLog, BorderLayout.SOUTH);
        
        // Configurar ventana
        pack();
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
    }
    
    private void configurarEventos() {
        btnIniciar.addActionListener(e -> iniciarScheduler());
        btnDetener.addActionListener(e -> detenerScheduler());
        btnEjecutarAlertasAhora.addActionListener(e -> ejecutarAlertasManual());
        btnEjecutarMantenimientoAhora.addActionListener(e -> ejecutarMantenimientoManual());
        btnVerEstado.addActionListener(e -> mostrarEstadoDetallado());
        btnLimpiarLog.addActionListener(e -> limpiarLog());
        
        // Cleanup al cerrar
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                log("🚪 Cerrando aplicación...");
                if (schedulerService.isSchedulerActivo()) {
                    schedulerService.shutdown();
                }
                System.exit(0);
            }
        });
    }
    
    private void iniciarScheduler() {
        log("▶️ Iniciando SchedulerService automático...");
        
        CompletableFuture.runAsync(() -> {
            try {
                schedulerService.iniciarScheduler();
                
                SwingUtilities.invokeLater(() -> {
                    log("✅ SchedulerService iniciado exitosamente");
                    log("🔔 Alertas programadas cada 8 horas");
                    log("🔧 Mantenimiento preventivo programado cada 24 horas");
                    log("⏰ Próxima ejecución en 5 minutos");
                    
                    btnIniciar.setEnabled(false);
                    btnDetener.setEnabled(true);
                    actualizarEstado();
                });
                
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> {
                    log("❌ Error iniciando scheduler: " + e.getMessage());
                    e.printStackTrace();
                });
            }
        });
    }
    
    private void detenerScheduler() {
        log("⏹️ Deteniendo SchedulerService...");
        
        CompletableFuture.runAsync(() -> {
            try {
                schedulerService.detenerScheduler();
                
                SwingUtilities.invokeLater(() -> {
                    log("✅ SchedulerService detenido exitosamente");
                    
                    btnIniciar.setEnabled(true);
                    btnDetener.setEnabled(false);
                    actualizarEstado();
                });
                
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> {
                    log("❌ Error deteniendo scheduler: " + e.getMessage());
                });
            }
        });
    }
    
    private void ejecutarAlertasManual() {
        log("🔔 Ejecutando proceso de alertas manualmente...");
        
        CompletableFuture.runAsync(() -> {
            try {
                long inicio = System.currentTimeMillis();
                schedulerService.ejecutarAlertasAhora();
                long duracion = System.currentTimeMillis() - inicio;
                
                SwingUtilities.invokeLater(() -> {
                    log("✅ Proceso de alertas completado en " + duracion + "ms");
                    actualizarEstado();
                });
                
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> {
                    log("❌ Error ejecutando alertas: " + e.getMessage());
                });
            }
        });
    }
    
    private void ejecutarMantenimientoManual() {
        log("🔧 Ejecutando proceso de mantenimiento preventivo manualmente...");
        
        CompletableFuture.runAsync(() -> {
            try {
                long inicio = System.currentTimeMillis();
                schedulerService.ejecutarMantenimientoPreventivoAhora();
                long duracion = System.currentTimeMillis() - inicio;
                
                SwingUtilities.invokeLater(() -> {
                    log("✅ Proceso de mantenimiento preventivo completado en " + duracion + "ms");
                    actualizarEstado();
                });
                
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> {
                    log("❌ Error ejecutando mantenimiento preventivo: " + e.getMessage());
                });
            }
        });
    }
    
    private void mostrarEstadoDetallado() {
        String estadoDetallado = schedulerService.getEstadoScheduler();
        log("📊 ESTADO DETALLADO DEL SCHEDULER:");
        for (String linea : estadoDetallado.split("\n")) {
            if (!linea.trim().isEmpty()) {
                log("   " + linea);
            }
        }
    }
    
    private void actualizarEstado() {
        boolean activo = schedulerService.isSchedulerActivo();
        
        if (activo) {
            statusLabel.setText("🟢 ACTIVO - Ejecutando automáticamente");
            statusLabel.setForeground(new Color(34, 139, 34));
        } else {
            statusLabel.setText("🔴 DETENIDO - Ejecución manual únicamente");
            statusLabel.setForeground(new Color(220, 20, 60));
        }
        
        // Actualizar estadísticas
        String stats = String.format(
            "Ejecuciones Alertas: %d | Ejecuciones Mantenimiento: %d",
            schedulerService.getEjecucionesAlertas(),
            schedulerService.getEjecucionesMantenimiento()
        );
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        
        if (schedulerService.getUltimaEjecucionAlertas() != null) {
            stats += " | Última Alerta: " + schedulerService.getUltimaEjecucionAlertas().format(formatter);
        }
        
        if (schedulerService.getUltimaEjecucionMantenimiento() != null) {
            stats += " | Último Mantenimiento: " + schedulerService.getUltimaEjecucionMantenimiento().format(formatter);
        }
        
        estadisticasLabel.setText(stats);
    }
    
    private void limpiarLog() {
        logArea.setText("");
        log("🧹 Log limpiado");
    }
    
    private void log(String mensaje) {
        String timestamp = java.time.LocalDateTime.now()
            .format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        String lineaLog = "[" + timestamp + "] " + mensaje + "\n";
        
        SwingUtilities.invokeLater(() -> {
            logArea.append(lineaLog);
            logArea.setCaretPosition(logArea.getDocument().getLength());
        });
    }
    
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeel());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> {
            System.out.println("=== TEST SCHEDULERSERVICE ===");
            System.out.println("🧪 Iniciando interfaz de pruebas para SchedulerService");
            System.out.println("💡 Esta aplicación permite probar la automatización crítica faltante");
            
            try {
                TestSchedulerService testApp = new TestSchedulerService();
                testApp.setVisible(true);
                
                System.out.println("✅ Interfaz de test cargada exitosamente");
                System.out.println("📋 Use los botones para probar las funcionalidades automáticas");
                
            } catch (Exception e) {
                System.err.println("❌ Error iniciando test de SchedulerService: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }
}