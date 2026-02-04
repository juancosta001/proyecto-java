package com.ypacarai.cooperativa.activos.test;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;

import com.ypacarai.cooperativa.activos.service.EmailService;
import com.ypacarai.cooperativa.activos.service.NotificationService;
import com.ypacarai.cooperativa.activos.model.Activo;

/**
 * Panel de pruebas para el sistema de email y notificaciones
 * Permite probar el envío de emails a MailHog
 */
public class EmailTestPanel {
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> crearVentanaPruebas());
    }
    
    private static void crearVentanaPruebas() {
        JFrame frame = new JFrame("🧪 Pruebas de Email - Sistema de Activos");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        
        // Panel principal
        JPanel panelPrincipal = new JPanel(new GridBagLayout());
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Título
        JLabel lblTitulo = new JLabel("🧪 Panel de Pruebas de Email");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitulo.setForeground(new Color(52, 152, 219));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panelPrincipal.add(lblTitulo, gbc);
        
        // Estado de conexión
        JLabel lblEstado = new JLabel("🔗 Estado: Verificando...");
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2;
        panelPrincipal.add(lblEstado, gbc);
        
        // Campo de email destinatario
        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 2;
        panelPrincipal.add(new JLabel("📧 Email destinatario:"), gbc);
        
        JTextField txtEmail = new JTextField("prueba@test.local", 25);
        gbc.gridx = 1; gbc.gridy = 2;
        panelPrincipal.add(txtEmail, gbc);
        
        // Área de resultados
        JTextArea txtResultados = new JTextArea(15, 50);
        txtResultados.setEditable(false);
        txtResultados.setFont(new Font("Courier New", Font.PLAIN, 12));
        txtResultados.setBackground(new Color(248, 249, 250));
        JScrollPane scrollResultados = new JScrollPane(txtResultados);
        scrollResultados.setBorder(BorderFactory.createTitledBorder("📋 Resultados de Pruebas"));
        
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0; gbc.weighty = 1.0;
        panelPrincipal.add(scrollResultados, gbc);
        
        // Panel de botones
        JPanel panelBotones = new JPanel(new FlowLayout());
        panelBotones.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        
        // Servicios
        EmailService emailService = new EmailService();
        NotificationService notificationService = new NotificationService(emailService);
        
        // Función para agregar logs
        Runnable verificarEstado = () -> {
            try {
                String config = notificationService.obtenerInformacionConfiguracion();
                boolean estado = notificationService.verificarEstadoServicio();
                
                SwingUtilities.invokeLater(() -> {
                    lblEstado.setText(estado ? "🟢 Estado: Conectado" : "🔴 Estado: Error de conexión");
                    lblEstado.setForeground(estado ? new Color(39, 174, 96) : new Color(231, 76, 60));
                    
                    txtResultados.append("=== CONFIGURACIÓN ACTUAL ===\\n");
                    txtResultados.append(config + "\\n\\n");
                });
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> {
                    lblEstado.setText("🔴 Estado: Error");
                    lblEstado.setForeground(new Color(231, 76, 60));
                    txtResultados.append("ERROR verificando estado: " + e.getMessage() + "\\n\\n");
                });
            }
        };
        
        // Botón 1: Verificar configuración
        JButton btnVerificar = new JButton("🔧 Verificar Configuración");
        btnVerificar.addActionListener(e -> {
            txtResultados.append("\\n[" + LocalDateTime.now().toString().substring(11, 19) + "] Verificando configuración...\\n");
            new Thread(verificarEstado).start();
        });
        panelBotones.add(btnVerificar);
        
        // Botón 2: Email simple
        JButton btnEmailSimple = new JButton("📧 Email Simple");
        btnEmailSimple.addActionListener(e -> {
            String destinatario = txtEmail.getText().trim();
            if (destinatario.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Ingrese un email destinatario");
                return;
            }
            
            txtResultados.append("\\n[" + LocalDateTime.now().toString().substring(11, 19) + "] Enviando email simple a: " + destinatario + "\\n");
            
            new Thread(() -> {
                try {
                    boolean resultado = emailService.enviarEmail(
                        destinatario,
                        "Prueba Email Simple - Sistema Activos",
                        "Este es un email de prueba enviado desde el sistema de gestión de activos.\\n\\n" +
                        "Fecha: " + LocalDateTime.now() + "\\n" +
                        "Sistema: Cooperativa Ypacaraí LTDA"
                    );
                    
                    SwingUtilities.invokeLater(() -> {
                        String estado = resultado ? "✅ ENVIADO" : "❌ ERROR";
                        txtResultados.append("Resultado: " + estado + "\\n");
                        txtResultados.append("Verificar en: http://localhost:8025\\n\\n");
                        txtResultados.setCaretPosition(txtResultados.getDocument().getLength());
                    });
                } catch (Exception ex) {
                    SwingUtilities.invokeLater(() -> {
                        txtResultados.append("❌ EXCEPCIÓN: " + ex.getMessage() + "\\n\\n");
                        txtResultados.setCaretPosition(txtResultados.getDocument().getLength());
                    });
                }
            }).start();
        });
        panelBotones.add(btnEmailSimple);
        
        // Botón 3: Email HTML
        JButton btnEmailHTML = new JButton("🎨 Email HTML");
        btnEmailHTML.addActionListener(e -> {
            String destinatario = txtEmail.getText().trim();
            if (destinatario.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Ingrese un email destinatario");
                return;
            }
            
            txtResultados.append("\\n[" + LocalDateTime.now().toString().substring(11, 19) + "] Enviando email HTML a: " + destinatario + "\\n");
            
            new Thread(() -> {
                try {
                    boolean resultado = emailService.enviarAlerta(
                        destinatario,
                        "🔧 Alerta de Prueba - Sistema Activos",
                        "ACT-2024-001",
                        "Este es un email de prueba con formato HTML desde el sistema de gestión de activos.",
                        LocalDateTime.now().toString().substring(0, 16)
                    );
                    
                    SwingUtilities.invokeLater(() -> {
                        String estado = resultado ? "✅ ENVIADO" : "❌ ERROR";
                        txtResultados.append("Resultado: " + estado + "\\n");
                        txtResultados.append("Verificar en: http://localhost:8025\\n\\n");
                        txtResultados.setCaretPosition(txtResultados.getDocument().getLength());
                    });
                } catch (Exception ex) {
                    SwingUtilities.invokeLater(() -> {
                        txtResultados.append("❌ EXCEPCIÓN: " + ex.getMessage() + "\\n\\n");
                        txtResultados.setCaretPosition(txtResultados.getDocument().getLength());
                    });
                }
            }).start();
        });
        panelBotones.add(btnEmailHTML);
        
        // Botón 4: Notificación mantenimiento
        JButton btnNotificacion = new JButton("🔧 Notificación Mantenimiento");
        btnNotificacion.addActionListener(e -> {
            String destinatario = txtEmail.getText().trim();
            txtResultados.append("\\n[" + LocalDateTime.now().toString().substring(11, 19) + "] Enviando notificación de mantenimiento...\\n");
            
            new Thread(() -> {
                try {
                    // Crear activo de prueba
                    Activo activoPrueba = new Activo();
                    activoPrueba.setActNumeroActivo("PC-PRUEBA-001");
                    activoPrueba.setTipoActivoNombre("PC");
                    activoPrueba.setUbicacionNombre("Oficina Principal");
                    activoPrueba.setActEstado(Activo.Estado.Operativo);
                    
                    boolean resultado = notificationService.notificarMantenimientoPreventivo(activoPrueba, 3);
                    
                    SwingUtilities.invokeLater(() -> {
                        String estado = resultado ? "✅ ENVIADO" : "❌ ERROR";
                        txtResultados.append("Resultado: " + estado + "\\n");
                        txtResultados.append("Verificar en: http://localhost:8025\\n\\n");
                        txtResultados.setCaretPosition(txtResultados.getDocument().getLength());
                    });
                } catch (Exception ex) {
                    SwingUtilities.invokeLater(() -> {
                        txtResultados.append("❌ EXCEPCIÓN: " + ex.getMessage() + "\\n\\n");
                        txtResultados.setCaretPosition(txtResultados.getDocument().getLength());
                    });
                }
            }).start();
        });
        panelBotones.add(btnNotificacion);
        
        // Botón 5: Abrir MailHog
        JButton btnMailHog = new JButton("🌐 Abrir MailHog");
        btnMailHog.addActionListener(e -> {
            try {
                Desktop.getDesktop().browse(java.net.URI.create("http://localhost:8025"));
                txtResultados.append("\\n[" + LocalDateTime.now().toString().substring(11, 19) + "] Abriendo MailHog en navegador...\\n\\n");
            } catch (Exception ex) {
                txtResultados.append("❌ Error abriendo navegador: " + ex.getMessage() + "\\n\\n");
            }
        });
        panelBotones.add(btnMailHog);
        
        // Botón 6: Limpiar log
        JButton btnLimpiar = new JButton("🧹 Limpiar");
        btnLimpiar.addActionListener(e -> txtResultados.setText(""));
        panelBotones.add(btnLimpiar);
        
        // Agregar componentes al frame
        frame.add(panelPrincipal, BorderLayout.CENTER);
        frame.add(panelBotones, BorderLayout.SOUTH);
        
        // Configurar frame
        frame.setSize(800, 700);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        
        // Verificar estado inicial
        SwingUtilities.invokeLater(() -> new Thread(verificarEstado).start());
    }
}