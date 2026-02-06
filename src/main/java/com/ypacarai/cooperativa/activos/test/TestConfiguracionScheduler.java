package com.ypacarai.cooperativa.activos.test;

import com.ypacarai.cooperativa.activos.gui.ConfiguracionSchedulerPanel;

/**
 * Aplicación de prueba para configuración del SchedulerService
 * Permite cambiar intervalos de ejecución a través de interfaz gráfica
 * 
 * Cooperativa Ypacaraí LTDA - Sistema de Activos
 */
public class TestConfiguracionScheduler {
    
    public static void main(String[] args) {
        // Configurar Look and Feel
        try {
            javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getCrossPlatformLookAndFeel());
        } catch (Exception e) {
            System.out.println("⚠️ No se pudo aplicar Look and Feel del sistema");
        }
        
        System.out.println("🚀 Iniciando configurador del SchedulerService...");
        System.out.println("📋 Esta aplicación permite:");
        System.out.println("   • Cambiar intervalos de alertas y mantenimiento");
        System.out.println("   • Configurar delay inicial y número de hilos");
        System.out.println("   • Habilitar/deshabilitar auto-inicio");
        System.out.println("   • Reiniciar scheduler con nuevas configuraciones");
        System.out.println();
        
        // Mostrar ventana de configuración
        ConfiguracionSchedulerPanel.mostrarVentana();
    }
}