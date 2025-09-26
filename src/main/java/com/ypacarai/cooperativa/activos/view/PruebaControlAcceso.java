package com.ypacarai.cooperativa.activos.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.ypacarai.cooperativa.activos.model.Usuario;
import com.ypacarai.cooperativa.activos.util.ControlAccesoRoles;
import com.ypacarai.cooperativa.activos.util.ControlAccesoRoles.Permiso;

/**
 * Prueba del Sistema de Control de Acceso Basado en Roles
 * Sistema de Gestión de Activos - Cooperativa Ypacaraí LTDA
 */
public class PruebaControlAcceso extends JFrame {
    
    private JComboBox<Usuario.Rol> cmbRoles;
    private JTextArea txtResultados;
    private Usuario usuarioPrueba;
    
    public PruebaControlAcceso() {
        initComponents();
        setupEventListeners();
    }
    
    private void initComponents() {
        setTitle("Prueba - Control de Acceso Basado en Roles");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 500);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        
        // Panel superior
        JPanel panelSuperior = new JPanel(new FlowLayout());
        panelSuperior.add(new JLabel("Seleccionar Rol:"));
        
        cmbRoles = new JComboBox<>(Usuario.Rol.values());
        panelSuperior.add(cmbRoles);
        
        JButton btnProbar = new JButton("Probar Permisos");
        panelSuperior.add(btnProbar);
        
        add(panelSuperior, BorderLayout.NORTH);
        
        // Panel central
        txtResultados = new JTextArea();
        txtResultados.setEditable(false);
        txtResultados.setFont(new Font("Monospaced", Font.PLAIN, 12));
        add(new JScrollPane(txtResultados), BorderLayout.CENTER);
        
        // Crear usuario de prueba inicial
        usuarioPrueba = new Usuario();
        usuarioPrueba.setUsuId(1);
        usuarioPrueba.setUsuNombre("Usuario de Prueba");
        usuarioPrueba.setUsuUsuario("prueba");
        usuarioPrueba.setUsuRol(Usuario.Rol.Jefe_Informatica);
        
        btnProbar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                probarPermisos();
            }
        });
        
        // Mostrar permisos iniciales
        probarPermisos();
    }
    
    private void setupEventListeners() {
        cmbRoles.addActionListener(e -> {
            Usuario.Rol rolSeleccionado = (Usuario.Rol) cmbRoles.getSelectedItem();
            usuarioPrueba.setUsuRol(rolSeleccionado);
            probarPermisos();
        });
    }
    
    private void probarPermisos() {
        Usuario.Rol rolActual = usuarioPrueba.getUsuRol();
        StringBuilder sb = new StringBuilder();
        
        sb.append("==========================================\n");
        sb.append("PRUEBA DE CONTROL DE ACCESO BASADO EN ROLES\n");
        sb.append("==========================================\n\n");
        
        sb.append("👤 Usuario: ").append(usuarioPrueba.getUsuNombre()).append("\n");
        sb.append("🔐 Rol Actual: ").append(rolActual).append("\n");
        sb.append("📝 Descripción: ").append(ControlAccesoRoles.obtenerDescripcionRol(rolActual)).append("\n\n");
        
        // Probar acceso a módulos
        sb.append("🏢 ACCESO A MÓDULOS:\n");
        sb.append("====================\n");
        String[] modulos = {"dashboard", "activos", "tickets", "mantenimiento", "reportes", "usuarios", "configuracion"};
        for (String modulo : modulos) {
            boolean tieneAcceso = ControlAccesoRoles.puedeAccederModulo(usuarioPrueba, modulo);
            sb.append(String.format("%-15s: %s\n", 
                modulo.substring(0, 1).toUpperCase() + modulo.substring(1),
                tieneAcceso ? "✅ PERMITIDO" : "❌ DENEGADO"
            ));
        }
        
        sb.append("\n🔑 PERMISOS ESPECÍFICOS:\n");
        sb.append("========================\n");
        
        // Probar permisos específicos importantes
        Permiso[] permisosImportantes = {
            Permiso.VER_DASHBOARD,
            Permiso.CREAR_ACTIVOS,
            Permiso.EDITAR_ACTIVOS,
            Permiso.ELIMINAR_ACTIVOS,
            Permiso.CREAR_TICKETS,
            Permiso.ASIGNAR_TICKETS,
            Permiso.CREAR_USUARIOS,
            Permiso.ELIMINAR_USUARIOS,
            Permiso.VER_REPORTES_AVANZADOS,
            Permiso.EDITAR_CONFIGURACION_AVANZADA,
            Permiso.VER_AUDITORIA,
            Permiso.ADMINISTRAR_SISTEMA
        };
        
        for (Permiso permiso : permisosImportantes) {
            boolean tienePermiso = ControlAccesoRoles.tienePermiso(usuarioPrueba, permiso);
            sb.append(String.format("%-30s: %s\n", 
                permiso.name().replace("_", " "),
                tienePermiso ? "✅ SÍ" : "❌ NO"
            ));
        }
        
        // Mostrar todos los módulos permitidos
        sb.append("\n📋 MÓDULOS PERMITIDOS:\n");
        sb.append("======================\n");
        var modulosPermitidos = ControlAccesoRoles.obtenerModulosPermitidos(usuarioPrueba);
        if (modulosPermitidos.isEmpty()) {
            sb.append("❌ Ningún módulo permitido\n");
        } else {
            for (String modulo : modulosPermitidos) {
                sb.append("✅ ").append(modulo.substring(0, 1).toUpperCase() + modulo.substring(1)).append("\n");
            }
        }
        
        // Ejemplo de mensaje de acceso denegado
        sb.append("\n🚫 MENSAJE DE ACCESO DENEGADO (Ejemplo):\n");
        sb.append("========================================\n");
        sb.append(ControlAccesoRoles.mensajeAccesoDenegado(usuarioPrueba, "eliminar usuarios"));
        
        txtResultados.setText(sb.toString());
        txtResultados.setCaretPosition(0);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new PruebaControlAcceso().setVisible(true);
        });
    }
}