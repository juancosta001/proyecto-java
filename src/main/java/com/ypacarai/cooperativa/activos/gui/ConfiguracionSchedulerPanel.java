package com.ypacarai.cooperativa.activos.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import com.ypacarai.cooperativa.activos.service.ConfiguracionService;
import com.ypacarai.cooperativa.activos.service.SchedulerService;

/**
 * Panel de configuración del SchedulerService
 * Permite modificar los intervalos de ejecución y otras configuraciones relacionadas
 * 
 * Cooperativa Ypacaraí LTDA - Sistema de Activos
 */
public class ConfiguracionSchedulerPanel extends JPanel {
    private ConfiguracionService configuracionService;
    private SchedulerService schedulerService;
    
    // Campos de entrada
    private JTextField txtIntervaloAlertas;
    private JTextField txtIntervaloMantenimiento; 
    private JTextField txtDelayInicial;
    private JTextField txtMaxHilos;
    private JCheckBox chkAutoInicio;
    private JTextArea txtConfiguracionActual;
    private JTextArea txtEstadoScheduler;
    
    public ConfiguracionSchedulerPanel() {
        this.configuracionService = new ConfiguracionService();
        
        initComponents();
        cargarConfiguracionesActuales();
        actualizarEstado();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("⚙️ Configuración del Scheduler"));
        
        // Panel principal de configuración
        JPanel panelConfiguracion = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Título
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        JLabel lblTitulo = new JLabel("🔧 Configuraciones de Automatización");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 16));
        panelConfiguracion.add(lblTitulo, gbc);
        
        gbc.gridwidth = 1;
        
        // Intervalo de alertas
        gbc.gridx = 0; gbc.gridy = 1;
        panelConfiguracion.add(new JLabel("🔔 Intervalo alertas (horas):"), gbc);
        gbc.gridx = 1;
        txtIntervaloAlertas = new JTextField(10);
        panelConfiguracion.add(txtIntervaloAlertas, gbc);
        
        // Intervalo de mantenimiento  
        gbc.gridx = 0; gbc.gridy = 2;
        panelConfiguracion.add(new JLabel("🔧 Intervalo mantenimiento (horas):"), gbc);
        gbc.gridx = 1;
        txtIntervaloMantenimiento = new JTextField(10);
        panelConfiguracion.add(txtIntervaloMantenimiento, gbc);
        
        // Delay inicial
        gbc.gridx = 0; gbc.gridy = 3;
        panelConfiguracion.add(new JLabel("⏰ Delay inicial (minutos):"), gbc);
        gbc.gridx = 1;
        txtDelayInicial = new JTextField(10);
        panelConfiguracion.add(txtDelayInicial, gbc);
        
        // Máximo hilos
        gbc.gridx = 0; gbc.gridy = 4;
        panelConfiguracion.add(new JLabel("🧵 Máximo hilos:"), gbc);
        gbc.gridx = 1;
        txtMaxHilos = new JTextField(10);
        panelConfiguracion.add(txtMaxHilos, gbc);
        
        // Auto-inicio
        gbc.gridx = 0; gbc.gridy = 5;
        panelConfiguracion.add(new JLabel("🚀 Auto-inicio:"), gbc);
        gbc.gridx = 1;
        chkAutoInicio = new JCheckBox("Iniciar automáticamente al arrancar sistema");
        panelConfiguracion.add(chkAutoInicio, gbc);
        
        // Botones  
        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2;
        JPanel panelBotones = new JPanel(new FlowLayout());
        
        JButton btnGuardar = new JButton("💾 Guardar Configuración");
        btnGuardar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                guardarConfiguracion();
            }
        });
        
        JButton btnRecargar = new JButton("🔄 Recargar desde BD");
        btnRecargar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cargarConfiguracionesActuales();
            }
        });
        
        JButton btnReiniciar = new JButton("🚀 Reiniciar Scheduler");
        btnReiniciar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                reiniciarScheduler();
            }
        });
        
        panelBotones.add(btnGuardar);
        panelBotones.add(btnRecargar);
        panelBotones.add(btnReiniciar);
        panelConfiguracion.add(panelBotones, gbc);
        
        add(panelConfiguracion, BorderLayout.NORTH);
        
        // Panel de información
        JPanel panelInfo = new JPanel(new GridLayout(1, 2, 10, 0));
        
        // Configuración actual
        txtConfiguracionActual = new JTextArea(8, 30);
        txtConfiguracionActual.setEditable(false);
        txtConfiguracionActual.setFont(new Font("Consolas", Font.PLAIN, 12));
        JScrollPane scrollConfig = new JScrollPane(txtConfiguracionActual);
        scrollConfig.setBorder(BorderFactory.createTitledBorder("📋 Configuración Actual"));
        
        // Estado del scheduler
        txtEstadoScheduler = new JTextArea(8, 30);  
        txtEstadoScheduler.setEditable(false);
        txtEstadoScheduler.setFont(new Font("Consolas", Font.PLAIN, 12));
        JScrollPane scrollEstado = new JScrollPane(txtEstadoScheduler);
        scrollEstado.setBorder(BorderFactory.createTitledBorder("📊 Estado del Scheduler"));
        
        panelInfo.add(scrollConfig);
        panelInfo.add(scrollEstado);
        
        add(panelInfo, BorderLayout.CENTER);
    }
    
    private void cargarConfiguracionesActuales() {
        try {
            txtIntervaloAlertas.setText(configuracionService.obtenerValorConfiguracion("scheduler.alertas_intervalo_horas", "8"));
            txtIntervaloMantenimiento.setText(configuracionService.obtenerValorConfiguracion("scheduler.mantenimiento_intervalo_horas", "24"));
            txtDelayInicial.setText(configuracionService.obtenerValorConfiguracion("scheduler.delay_inicial_minutos", "5"));
            txtMaxHilos.setText(configuracionService.obtenerValorConfiguracion("scheduler.max_hilos", "3"));
            chkAutoInicio.setSelected(Boolean.parseBoolean(configuracionService.obtenerValorConfiguracion("scheduler.auto_inicio", "true")));
            
            actualizarConfiguracionMostrada();
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "❌ Error cargando configuraciones: " + e.getMessage(), 
                                        "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void guardarConfiguracion() {
        try {
            // Validar valores
            int intervalos[] = {
                Integer.parseInt(txtIntervaloAlertas.getText().trim()),
                Integer.parseInt(txtIntervaloMantenimiento.getText().trim()),
                Integer.parseInt(txtDelayInicial.getText().trim()),
                Integer.parseInt(txtMaxHilos.getText().trim())
            };
            
            // Validaciones básicas
            if (intervalos[0] <= 0 || intervalos[1] <= 0 || intervalos[2] < 0 || intervalos[3] <= 0) {
                throw new IllegalArgumentException("Los valores deben ser números positivos");
            }
            
            if (intervalos[3] > 10) {
                throw new IllegalArgumentException("Máximo 10 hilos permitidos");
            }
            
            // Guardar en BD
            configuracionService.guardarConfiguracion("scheduler.alertas_intervalo_horas", String.valueOf(intervalos[0]));
            configuracionService.guardarConfiguracion("scheduler.mantenimiento_intervalo_horas", String.valueOf(intervalos[1]));
            configuracionService.guardarConfiguracion("scheduler.delay_inicial_minutos", String.valueOf(intervalos[2]));
            configuracionService.guardarConfiguracion("scheduler.max_hilos", String.valueOf(intervalos[3]));
            configuracionService.guardarConfiguracion("scheduler.auto_inicio", String.valueOf(chkAutoInicio.isSelected()));
            
            actualizarConfiguracionMostrada();
            
            JOptionPane.showMessageDialog(this, 
                "✅ Configuración guardada exitosamente\n\n" +
                "⚠️ Para aplicar cambios, use 'Reiniciar Scheduler'", 
                "Éxito", JOptionPane.INFORMATION_MESSAGE);
                
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "❌ Error: Ingrese solo números válidos", 
                                        "Error de Validación", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "❌ Error guardando configuración: " + e.getMessage(), 
                                        "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void reiniciarScheduler() {
        try {
            if (schedulerService == null) {
                schedulerService = new SchedulerService();
            }
            
            schedulerService.recargarConfiguracionesYReiniciar();
            actualizarEstado();
            
            JOptionPane.showMessageDialog(this, "✅ Scheduler reiniciado con nuevas configuraciones", 
                                        "Éxito", JOptionPane.INFORMATION_MESSAGE);
                                        
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "❌ Error reiniciando scheduler: " + e.getMessage(), 
                                        "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void actualizarConfiguracionMostrada() {
        StringBuilder config = new StringBuilder();
        config.append("=== CONFIGURACIONES ACTUALES ===\n");
        config.append("🔔 Alertas cada: ").append(txtIntervaloAlertas.getText()).append(" horas\n");
        config.append("🔧 Mantenimiento cada: ").append(txtIntervaloMantenimiento.getText()).append(" horas\n");
        config.append("⏰ Delay inicial: ").append(txtDelayInicial.getText()).append(" minutos\n");
        config.append("🧵 Hilos máximo: ").append(txtMaxHilos.getText()).append("\n");
        config.append("🚀 Auto-inicio: ").append(chkAutoInicio.isSelected() ? "SÍ" : "NO").append("\n");
        config.append("\n");
        config.append("ℹ️ Configuración almacenada en tabla:\n");
        config.append("   configuracion_sistema\n");
        config.append("\n");
        config.append("⚠️ Los cambios requieren reinicio\n");
        config.append("   del scheduler para aplicarse");
        
        txtConfiguracionActual.setText(config.toString());
    }
    
    private void actualizarEstado() {
        if (schedulerService != null) {
            txtEstadoScheduler.setText(schedulerService.obtenerEstado());
        } else {
            txtEstadoScheduler.setText("❌ Scheduler no inicializado\n\nUse 'Reiniciar Scheduler' para crear una instancia");
        }
    }
    
    // Método para uso externo  
    public static void mostrarVentana() {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("⚙️ Configuración del Scheduler");
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.add(new ConfiguracionSchedulerPanel());
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}