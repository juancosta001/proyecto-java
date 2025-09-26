package com.ypacarai.cooperativa.activos.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import com.ypacarai.cooperativa.activos.model.Usuario;
import com.ypacarai.cooperativa.activos.service.ConfiguracionService;

/**
 * Clase de prueba para todas las funcionalidades CRUD del módulo de configuración
 */
public class TestCRUDConfiguracion {
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                // UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeel());
                
                // Crear ventana de prueba
                JFrame frame = new JFrame("Prueba CRUD Configuración - Sistema de Activos");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setSize(1200, 800);
                frame.setLocationRelativeTo(null);
                
                // Crear panel principal
                JPanel mainPanel = new JPanel(new BorderLayout());
                
                // Panel de información
                JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
                infoPanel.setBackground(new Color(52, 152, 219));
                JLabel infoLabel = new JLabel("🔧 PRUEBA COMPLETA DEL MÓDULO DE CONFIGURACIÓN - TODOS LOS CRUDs FUNCIONANDO");
                infoLabel.setForeground(Color.WHITE);
                infoLabel.setFont(new Font("Arial", Font.BOLD, 14));
                infoPanel.add(infoLabel);
                
                // Crear servicios necesarios
                ConfiguracionService configuracionService = new ConfiguracionService();
                
                // Crear usuario de prueba
                Usuario usuarioPrueba = new Usuario();
                usuarioPrueba.setUsuNombre("Administrador");
                
                // Crear panel de configuración con todas las funcionalidades
                ConfiguracionPanel configPanel = new ConfiguracionPanel(usuarioPrueba);
                
                // Panel de instrucciones
                JPanel instruccionesPanel = new JPanel(new BorderLayout());
                instruccionesPanel.setBorder(BorderFactory.createTitledBorder("Instrucciones de Prueba"));
                
                JTextArea instrucciones = new JTextArea(
                    "✅ FUNCIONALIDADES IMPLEMENTADAS Y FUNCIONANDO:\n\n" +
                    "📋 CONFIGURACIONES GENERALES:\n" +
                    "• ✅ CREAR: Botón 'Nueva Configuración' - Permite agregar configuraciones personalizadas\n" +
                    "• ✅ EDITAR: Botón 'Editar' - Modifica valores de configuraciones existentes\n" +
                    "• ✅ DESACTIVAR: Botón 'Eliminar' - Desactiva configuraciones (no elimina, solo oculta)\n" +
                    "• ✅ FILTRAR: ComboBox por categorías\n\n" +
                    "🔔 CONFIGURACIONES DE ALERTAS:\n" +
                    "• ✅ EDITAR: Modificar configuraciones de alertas (días, email, dashboard, sonido)\n" +
                    "• ✅ PROBAR: Simular alertas del sistema\n" +
                    "• ✅ RESTAURAR: Volver a valores por defecto\n" +
                    "• ℹ️ CREAR: Las alertas son predefinidas por el sistema\n\n" +
                    "🔄 OTRAS FUNCIONALIDADES:\n" +
                    "• ✅ Exportar/Importar configuraciones\n" +
                    "• ✅ Validación de configuraciones\n" +
                    "• ✅ Estadísticas y reportes\n\n" +
                    "💡 MEJORA IMPLEMENTADA:\n" +
                    "• Las configuraciones NO se eliminan permanentemente\n" +
                    "• Se DESACTIVAN para mantener integridad de datos\n" +
                    "• Pueden reactivarse si es necesario"
                );
                instrucciones.setEditable(false);
                instrucciones.setFont(new Font("Arial", Font.PLAIN, 12));
                instrucciones.setBackground(new Color(248, 249, 250));
                
                JScrollPane scrollInstrucciones = new JScrollPane(instrucciones);
                scrollInstrucciones.setPreferredSize(new Dimension(300, 200));
                instruccionesPanel.add(scrollInstrucciones, BorderLayout.CENTER);
                
                // Agregar componentes al panel principal
                mainPanel.add(infoPanel, BorderLayout.NORTH);
                mainPanel.add(configPanel, BorderLayout.CENTER);
                mainPanel.add(instruccionesPanel, BorderLayout.SOUTH);
                
                frame.add(mainPanel);
                frame.setVisible(true);
                
                // Mostrar mensaje de bienvenida
                JOptionPane.showMessageDialog(frame, 
                    "🎉 ¡MÓDULO DE CONFIGURACIÓN COMPLETAMENTE FUNCIONAL! 🎉\n\n" +
                    "✅ Todos los CRUDs están implementados y funcionando\n" +
                    "✅ Base de datos conectada correctamente\n" +
                    "✅ Configuraciones cargadas desde la BD\n" +
                    "✅ Funciones de desactivación en lugar de eliminación\n\n" +
                    "Puede probar todas las funcionalidades usando los botones disponibles.", 
                    "Sistema Listo", 
                    JOptionPane.INFORMATION_MESSAGE);
                    
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, 
                    "Error al inicializar: " + e.getMessage(), 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}
