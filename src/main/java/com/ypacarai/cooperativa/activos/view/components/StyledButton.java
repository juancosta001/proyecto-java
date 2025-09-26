package com.ypacarai.cooperativa.activos.view.components;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/**
 * Botón estilizado para la interfaz de la cooperativa
 */
public class StyledButton extends JButton {
    
    private boolean isPrimary;
    private Color primaryColor = new Color(46, 125, 50);
    private Color primaryHoverColor = new Color(76, 175, 80);
    private Color secondaryColor = new Color(245, 245, 245);
    private Color secondaryHoverColor = new Color(230, 230, 230);
    private boolean isHovered = false;
    
    public StyledButton(String text) {
        this(text, true);
    }
    
    public StyledButton(String text, boolean isPrimary) {
        super(text);
        this.isPrimary = isPrimary;
        configurarEstilo();
        configurarEventos();
    }
    
    private void configurarEstilo() {
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setOpaque(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        if (isPrimary) {
            setFont(new Font("Arial", Font.BOLD, 14));
            setForeground(Color.WHITE);
        } else {
            setFont(new Font("Arial", Font.PLAIN, 12));
            setForeground(primaryColor);
        }
    }
    
    private void configurarEventos() {
        addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                isHovered = true;
                repaint();
            }
            
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                isHovered = false;
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
        
        // Determinar color de fondo
        Color backgroundColor;
        if (isPrimary) {
            backgroundColor = isHovered ? primaryHoverColor : primaryColor;
        } else {
            backgroundColor = isHovered ? secondaryHoverColor : secondaryColor;
        }
        
        // Dibujar fondo redondeado
        g2d.setColor(backgroundColor);
        g2d.fill(new RoundRectangle2D.Float(0, 0, width, height, 10, 10));
        
        // Agregar sombra si es botón primario
        if (isPrimary && !isHovered) {
            g2d.setColor(new Color(0, 0, 0, 30));
            g2d.fill(new RoundRectangle2D.Float(2, 2, width, height, 10, 10));
        }
        
        g2d.dispose();
        super.paintComponent(g);
    }
    
    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        if (enabled) {
            setForeground(isPrimary ? Color.WHITE : primaryColor);
        } else {
            setForeground(Color.GRAY);
        }
        repaint();
    }
}
