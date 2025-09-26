package com.ypacarai.cooperativa.activos.view.components;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/**
 * Campo de texto estilizado
 */
public class StyledTextField extends JTextField {
    
    private Color borderColor = new Color(200, 200, 200);
    private Color focusColor = new Color(46, 125, 50);
    private boolean isFocused = false;
    
    public StyledTextField() {
        super();
        configurarEstilo();
        configurarEventos();
    }
    
    public StyledTextField(String text) {
        super(text);
        configurarEstilo();
        configurarEventos();
    }
    
    private void configurarEstilo() {
        setFont(new Font("Arial", Font.PLAIN, 14));
        setForeground(new Color(66, 66, 66));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        setOpaque(false);
    }
    
    private void configurarEventos() {
        addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent evt) {
                isFocused = true;
                repaint();
            }
            
            @Override
            public void focusLost(java.awt.event.FocusEvent evt) {
                isFocused = false;
                repaint();
            }
        });
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        int width = getWidth();
        int height = getHeight();
        
        // Fondo blanco redondeado
        g2d.setColor(getBackground());
        g2d.fill(new RoundRectangle2D.Float(0, 0, width, height, 8, 8));
        
        // Borde
        g2d.setColor(isFocused ? focusColor : borderColor);
        g2d.setStroke(new BasicStroke(isFocused ? 2 : 1));
        g2d.draw(new RoundRectangle2D.Float(1, 1, width - 2, height - 2, 8, 8));
        
        g2d.dispose();
        super.paintComponent(g);
    }
    
    public void setBorderColor(Color borderColor) {
        this.borderColor = borderColor;
        repaint();
    }
    
    public void setFocusColor(Color focusColor) {
        this.focusColor = focusColor;
        repaint();
    }
}
