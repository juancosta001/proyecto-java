package com.ypacarai.cooperativa.activos.view.components;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/**
 * Panel con bordes redondeados
 */
public class RoundedPanel extends JPanel {
    
    private int arcWidth;
    private int arcHeight;
    private Color borderColor;
    private int borderWidth;
    
    public RoundedPanel(int radius) {
        this(radius, radius);
    }
    
    public RoundedPanel(int arcWidth, int arcHeight) {
        this.arcWidth = arcWidth;
        this.arcHeight = arcHeight;
        this.borderColor = new Color(200, 200, 200);
        this.borderWidth = 1;
        setOpaque(false);
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        int width = getWidth();
        int height = getHeight();
        
        // Fondo del panel
        g2d.setColor(getBackground());
        g2d.fill(new RoundRectangle2D.Float(0, 0, width, height, arcWidth, arcHeight));
        
        // Borde del panel (opcional)
        if (borderWidth > 0) {
            g2d.setColor(borderColor);
            g2d.setStroke(new BasicStroke(borderWidth));
            g2d.draw(new RoundRectangle2D.Float(borderWidth/2f, borderWidth/2f, 
                                               width - borderWidth, height - borderWidth, 
                                               arcWidth, arcHeight));
        }
        
        g2d.dispose();
        super.paintComponent(g);
    }
    
    public void setBorderColor(Color borderColor) {
        this.borderColor = borderColor;
        repaint();
    }
    
    public void setBorderWidth(int borderWidth) {
        this.borderWidth = borderWidth;
        repaint();
    }
}
