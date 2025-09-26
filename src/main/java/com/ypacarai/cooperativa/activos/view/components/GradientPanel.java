package com.ypacarai.cooperativa.activos.view.components;

import javax.swing.*;
import java.awt.*;

/**
 * Panel con gradiente de fondo para la interfaz
 */
public class GradientPanel extends JPanel {
    
    private Color colorInicio;
    private Color colorFin;
    private boolean vertical;
    
    public GradientPanel() {
        this(new Color(46, 125, 50), new Color(27, 94, 32), true);
    }
    
    public GradientPanel(Color colorInicio, Color colorFin, boolean vertical) {
        this.colorInicio = colorInicio;
        this.colorFin = colorFin;
        this.vertical = vertical;
        setOpaque(false);
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        int width = getWidth();
        int height = getHeight();
        
        GradientPaint gradient;
        if (vertical) {
            gradient = new GradientPaint(0, 0, colorInicio, 0, height, colorFin);
        } else {
            gradient = new GradientPaint(0, 0, colorInicio, width, 0, colorFin);
        }
        
        g2d.setPaint(gradient);
        g2d.fillRect(0, 0, width, height);
        
        g2d.dispose();
    }
    
    public void setColores(Color colorInicio, Color colorFin) {
        this.colorInicio = colorInicio;
        this.colorFin = colorFin;
        repaint();
    }
}
