package com.ypacarai.cooperativa.activos.view.components;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ypacarai.cooperativa.activos.model.Usuario;
import com.ypacarai.cooperativa.activos.model.TicketAsignacion;

/**
 * Panel para selección múltiple de técnicos con roles
 * Sistema de Gestión de Activos - Cooperativa Ypacaraí LTDA
 */
public class MultiTecnicoSelectorPanel extends JPanel {
    
    private static final Color COLOR_VERDE_COOPERATIVA = new Color(34, 139, 34);
    private static final Color COLOR_VERDE_CLARO = new Color(144, 238, 144);
    private static final Color COLOR_AZUL = new Color(70, 130, 180);
    
    private List<Usuario> tecnicosDisponibles;
    private Map<Usuario, TecnicoRow> filasAsignacion;
    private JPanel panelFilas;
    private JButton btnAgregarTecnico;
    private JLabel lblContador;
    
    // Listeners para notificar cambios
    private List<Runnable> changeListeners;
    
    public MultiTecnicoSelectorPanel() {
        this.tecnicosDisponibles = new ArrayList<>();
        this.filasAsignacion = new HashMap<>();
        this.changeListeners = new ArrayList<>();
        initComponents();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(COLOR_VERDE_COOPERATIVA, 2),
                " 👥 Asignación de Técnicos ",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 12),
                COLOR_VERDE_COOPERATIVA
            ),
            new EmptyBorder(10, 10, 10, 10)
        ));
        
        // Panel superior con botón y contador
        JPanel panelSuperior = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnAgregarTecnico = new JButton("➕ Agregar Técnico");
        btnAgregarTecnico.setBackground(COLOR_VERDE_COOPERATIVA);
        btnAgregarTecnico.setForeground(Color.WHITE);
        btnAgregarTecnico.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnAgregarTecnico.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAgregarTecnico.setFocusPainted(false);
        
        lblContador = new JLabel("0 técnicos asignados");
        lblContador.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblContador.setForeground(COLOR_AZUL);
        
        panelSuperior.add(btnAgregarTecnico);
        panelSuperior.add(Box.createHorizontalStrut(15));
        panelSuperior.add(lblContador);
        
        add(panelSuperior, BorderLayout.NORTH);
        
        // Panel scrolleable para las filas
        panelFilas = new JPanel();
        panelFilas.setLayout(new BoxLayout(panelFilas, BoxLayout.Y_AXIS));
        
        JScrollPane scroll = new JScrollPane(panelFilas);
        scroll.setPreferredSize(new Dimension(400, 150));
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        
        add(scroll, BorderLayout.CENTER);
        
        // Event listeners
        btnAgregarTecnico.addActionListener(e -> agregarFilaTecnico());
        
        actualizarEstado();
    }
    
    public void setTecnicosDisponibles(List<Usuario> tecnicos) {
        this.tecnicosDisponibles = new ArrayList<>(tecnicos);
        actualizarEstado();
    }
    
    public void addChangeListener(Runnable listener) {
        changeListeners.add(listener);
    }
    
    private void notificarCambios() {
        for (Runnable listener : changeListeners) {
            listener.run();
        }
    }
    
    private void agregarFilaTecnico() {
        if (hayTecnicosDisponibles()) {
            TecnicoRow fila = new TecnicoRow();
            panelFilas.add(fila);
            panelFilas.add(Box.createVerticalStrut(5));
            
            revalidate();
            repaint();
            actualizarEstado();
        } else {
            JOptionPane.showMessageDialog(this,
                "No hay más técnicos disponibles para asignar.",
                "Sin técnicos",
                JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void removerFilaTecnico(TecnicoRow fila) {
        // Liberar el técnico seleccionado
        if (fila.getTecnicoSeleccionado() != null) {
            filasAsignacion.remove(fila.getTecnicoSeleccionado());
        }
        
        // Remover del panel
        panelFilas.remove(fila);
        
        // Remover espaciador si existe
        Component[] components = panelFilas.getComponents();
        for (int i = 0; i < components.length - 1; i++) {
            if (components[i] == fila && i + 1 < components.length) {
                Component siguiente = components[i + 1];
                if (siguiente instanceof Box.Filler) {
                    panelFilas.remove(siguiente);
                    break;
                }
            }
        }
        
        revalidate();
        repaint();
        actualizarEstado();
    }
    
    private boolean hayTecnicosDisponibles() {
        return filasAsignacion.size() < tecnicosDisponibles.size();
    }
    
    private List<Usuario> obtenerTecnicosDisponibles() {
        List<Usuario> disponibles = new ArrayList<>();
        for (Usuario tecnico : tecnicosDisponibles) {
            if (!filasAsignacion.containsKey(tecnico)) {
                disponibles.add(tecnico);
            }
        }
        return disponibles;
    }
    
    private void actualizarEstado() {
        btnAgregarTecnico.setEnabled(hayTecnicosDisponibles());
        lblContador.setText(filasAsignacion.size() + " técnicos asignados");
        
        // Actualizar combos de técnicos en todas las filas
        for (TecnicoRow fila : filasAsignacion.values()) {
            fila.actualizarComboTecnicos();
        }
        
        // Notificar cambios
        notificarCambios();
    }
    
    public List<TicketAsignacion> obtenerAsignaciones() {
        List<TicketAsignacion> asignaciones = new ArrayList<>();
        
        for (Map.Entry<Usuario, TecnicoRow> entry : filasAsignacion.entrySet()) {
            Usuario tecnico = entry.getKey();
            TecnicoRow fila = entry.getValue();
            
            TicketAsignacion asignacion = new TicketAsignacion();
            asignacion.setUsuId(tecnico.getUsuId());
            asignacion.setTasRolAsignacion(fila.getRolSeleccionado());
            asignacion.setTasObservaciones(fila.getObservaciones());
            
            asignaciones.add(asignacion);
        }
        
        return asignaciones;
    }
    
    public void limpiar() {
        filasAsignacion.clear();
        panelFilas.removeAll();
        revalidate();
        repaint();
        actualizarEstado();
    }
    
    public boolean hayAsignaciones() {
        return !filasAsignacion.isEmpty();
    }
    
    // Clase interna para cada fila de asignación
    private class TecnicoRow extends JPanel {
        private JComboBox<Usuario> cmbTecnico;
        private JComboBox<TicketAsignacion.RolAsignacion> cmbRol;
        private JTextField txtObservaciones;
        private JButton btnRemover;
        
        public TecnicoRow() {
            initComponents();
            setupEventListeners();
        }
        
        private void initComponents() {
            setLayout(new GridBagLayout());
            setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                new EmptyBorder(8, 8, 8, 8)
            ));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
            
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(2, 2, 2, 2);
            gbc.anchor = GridBagConstraints.WEST;
            
            // Técnico
            gbc.gridx = 0; gbc.gridy = 0;
            add(new JLabel("Técnico:"), gbc);
            gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 0.4;
            cmbTecnico = new JComboBox<>();
            cmbTecnico.setRenderer(new DefaultListCellRenderer() {
                @Override
                public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                        boolean isSelected, boolean cellHasFocus) {
                    super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                    if (value instanceof Usuario) {
                        setText(((Usuario) value).getUsuNombre());
                    }
                    return this;
                }
            });
            add(cmbTecnico, gbc);
            
            // Rol
            gbc.gridx = 2; gbc.weightx = 0.3;
            add(new JLabel("Rol:"), gbc);
            gbc.gridx = 3;
            cmbRol = new JComboBox<>(TicketAsignacion.RolAsignacion.values());
            add(cmbRol, gbc);
            
            // Observaciones
            gbc.gridx = 4; gbc.weightx = 0.2;
            add(new JLabel("Obs:"), gbc);
            gbc.gridx = 5;
            txtObservaciones = new JTextField(10);
            txtObservaciones.setToolTipText("Observaciones opcionales");
            add(txtObservaciones, gbc);
            
            // Botón remover
            gbc.gridx = 6; gbc.weightx = 0; gbc.fill = GridBagConstraints.NONE;
            btnRemover = new JButton("❌");
            btnRemover.setPreferredSize(new Dimension(30, 25));
            btnRemover.setToolTipText("Remover técnico");
            btnRemover.setFont(new Font("Segoe UI", Font.PLAIN, 10));
            btnRemover.setFocusPainted(false);
            add(btnRemover, gbc);
            
            cargarTecnicosDisponibles();
        }
        
        private void setupEventListeners() {
            cmbTecnico.addActionListener(e -> {
                Usuario anteriorTecnico = null;
                
                // Buscar el técnico anterior de esta fila
                for (Map.Entry<Usuario, TecnicoRow> entry : filasAsignacion.entrySet()) {
                    if (entry.getValue() == this) {
                        anteriorTecnico = entry.getKey();
                        break;
                    }
                }
                
                // Remover asignación anterior si existe
                if (anteriorTecnico != null) {
                    filasAsignacion.remove(anteriorTecnico);
                }
                
                // Asignar nuevo técnico
                Usuario nuevoTecnico = (Usuario) cmbTecnico.getSelectedItem();
                if (nuevoTecnico != null) {
                    filasAsignacion.put(nuevoTecnico, this);
                }
                
                MultiTecnicoSelectorPanel.this.actualizarEstado();
            });
            
            btnRemover.addActionListener(e -> removerFilaTecnico(this));
        }
        
        private void cargarTecnicosDisponibles() {
            cmbTecnico.removeAllItems();
            for (Usuario tecnico : obtenerTecnicosDisponibles()) {
                cmbTecnico.addItem(tecnico);
            }
            
            // Auto-seleccionar el primer técnico si hay alguno disponible
            if (cmbTecnico.getItemCount() > 0) {
                Usuario primerTecnico = cmbTecnico.getItemAt(0);
                filasAsignacion.put(primerTecnico, this);
            }
        }
        
        public void actualizarComboTecnicos() {
            Usuario tecnicoActual = getTecnicoSeleccionado();
            
            cmbTecnico.removeAllItems();
            
            // Agregar técnicos disponibles
            for (Usuario tecnico : obtenerTecnicosDisponibles()) {
                cmbTecnico.addItem(tecnico);
            }
            
            // Re-agregar el técnico actual si existe
            if (tecnicoActual != null) {
                boolean yaEstaEnLista = false;
                for (int i = 0; i < cmbTecnico.getItemCount(); i++) {
                    if (cmbTecnico.getItemAt(i).getUsuId() == tecnicoActual.getUsuId()) {
                        yaEstaEnLista = true;
                        break;
                    }
                }
                
                if (!yaEstaEnLista) {
                    cmbTecnico.addItem(tecnicoActual);
                }
                
                cmbTecnico.setSelectedItem(tecnicoActual);
            }
        }
        
        public Usuario getTecnicoSeleccionado() {
            return (Usuario) cmbTecnico.getSelectedItem();
        }
        
        public TicketAsignacion.RolAsignacion getRolSeleccionado() {
            return (TicketAsignacion.RolAsignacion) cmbRol.getSelectedItem();
        }
        
        public String getObservaciones() {
            return txtObservaciones.getText().trim();
        }
    }
}