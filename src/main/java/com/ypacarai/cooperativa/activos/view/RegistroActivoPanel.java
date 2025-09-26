package com.ypacarai.cooperativa.activos.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import com.ypacarai.cooperativa.activos.dao.TipoActivoDAO;
import com.ypacarai.cooperativa.activos.dao.UbicacionDAO;
import com.ypacarai.cooperativa.activos.model.Activo;
import com.ypacarai.cooperativa.activos.model.TipoActivo;
import com.ypacarai.cooperativa.activos.model.Ubicacion;
import com.ypacarai.cooperativa.activos.service.ActivoService;

/**
 * Panel de registro completo de activos con formularios detallados
 * Sistema de Gestión de Activos - Cooperativa Ypacaraí LTDA
 */
public class RegistroActivoPanel extends JPanel {

    // Colores corporativos
    private static final Color VERDE_PRINCIPAL = new Color(0, 128, 55);
    private static final Color VERDE_SECUNDARIO = new Color(0, 100, 40);
    private static final Color GRIS_CLARO = new Color(245, 245, 245);
    private static final Color GRIS_OSCURO = new Color(64, 64, 64);

    // Campos del formulario
    private JTextField txtNumeroActivo;
    private JComboBox<TipoActivo> cmbTipoActivo;
    private JTextField txtMarca;
    private JTextField txtModelo;
    private JTextField txtNumeroSerie;
    private JTextArea txtEspecificaciones;
    private JTextField txtFechaAdquisicion;
    private JComboBox<Activo.Estado> cmbEstado;
    private JComboBox<Ubicacion> cmbUbicacion;
    private JTextField txtResponsable;
    private JTextArea txtObservaciones;
    
    // Labels de validación
    private JLabel lblValidacionNumero;
    private JLabel lblValidacionFecha;
    private JLabel lblValidacionSerie;
    
    // Botones
    private JButton btnGuardar;
    private JButton btnLimpiar;
    private JButton btnCancelar;
    
    // Services
    private ActivoService activoService;
    private TipoActivoDAO tipoActivoDAO;
    private UbicacionDAO ubicacionDAO;
    
    // Variables de control
    private final MainWindowNew ventanaPrincipal;
    private final int usuarioLogueadoId;

    public RegistroActivoPanel(MainWindowNew ventanaPrincipal, int usuarioId) {
        this.ventanaPrincipal = ventanaPrincipal;
        this.usuarioLogueadoId = usuarioId;
        
        // Inicializar servicios
        inicializarServicios();
        
        // Configurar el panel
        configurarPanel();
        
        // Crear la interfaz
        crearInterfaz();
        
        // Cargar datos iniciales
        cargarDatosIniciales();
    }

    private void inicializarServicios() {
        try {
            this.activoService = new ActivoService();
            this.tipoActivoDAO = new TipoActivoDAO();
            this.ubicacionDAO = new UbicacionDAO();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error al inicializar los servicios: " + e.getMessage(),
                "Error del Sistema",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void configurarPanel() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(20, 20, 20, 20));
    }

    private void crearInterfaz() {
        // Título
        JPanel panelTitulo = crearPanelTitulo();
        add(panelTitulo, BorderLayout.NORTH);
        
        // Formulario principal
        JPanel panelFormulario = crearPanelFormulario();
        JScrollPane scrollFormulario = new JScrollPane(panelFormulario);
        scrollFormulario.setBorder(null);
        scrollFormulario.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollFormulario, BorderLayout.CENTER);
        
        // Panel de botones
        JPanel panelBotones = crearPanelBotones();
        add(panelBotones, BorderLayout.SOUTH);
    }

    private JPanel crearPanelTitulo() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(VERDE_PRINCIPAL);
        panel.setBorder(new EmptyBorder(15, 20, 15, 20));

        JLabel lblTitulo = new JLabel("📋 REGISTRO DE ACTIVOS");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setHorizontalAlignment(JLabel.LEFT);

        JLabel lblSubtitulo = new JLabel("Complete todos los campos obligatorios para registrar un nuevo activo");
        lblSubtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSubtitulo.setForeground(Color.WHITE);

        JPanel panelTextos = new JPanel(new BorderLayout());
        panelTextos.setBackground(VERDE_PRINCIPAL);
        panelTextos.add(lblTitulo, BorderLayout.NORTH);
        panelTextos.add(lblSubtitulo, BorderLayout.SOUTH);

        panel.add(panelTextos, BorderLayout.WEST);
        return panel;
    }

    private JPanel crearPanelFormulario() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(30, 30, 30, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        // Sección 1: Información Básica
        agregarSeccion(panel, gbc, 0, "INFORMACIÓN BÁSICA", VERDE_PRINCIPAL);

        // Número de Activo (obligatorio)
        gbc.gridy = 1;
        gbc.gridx = 0;
        gbc.weightx = 0;
        panel.add(crearLabel("* Número de Activo:", true), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        txtNumeroActivo = crearTextField();
        txtNumeroActivo.setToolTipText("Formato: ACT-YYYY-NNNN (ej: ACT-2024-0001)");
        panel.add(txtNumeroActivo, gbc);
        
        gbc.gridx = 2;
        gbc.weightx = 0;
        lblValidacionNumero = crearLabelValidacion();
        panel.add(lblValidacionNumero, gbc);

        // Tipo de Activo (obligatorio)
        gbc.gridy = 2;
        gbc.gridx = 0;
        gbc.weightx = 0;
        panel.add(crearLabel("* Tipo de Activo:", true), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        cmbTipoActivo = crearComboBox();
        panel.add(cmbTipoActivo, gbc);

        // Marca (obligatorio)
        gbc.gridy = 3;
        gbc.gridx = 0;
        gbc.weightx = 0;
        panel.add(crearLabel("* Marca:", true), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        txtMarca = crearTextField();
        panel.add(txtMarca, gbc);

        // Modelo (obligatorio)
        gbc.gridy = 4;
        gbc.gridx = 0;
        gbc.weightx = 0;
        panel.add(crearLabel("* Modelo:", true), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        txtModelo = crearTextField();
        panel.add(txtModelo, gbc);

        // Número de Serie (obligatorio)
        gbc.gridy = 5;
        gbc.gridx = 0;
        gbc.weightx = 0;
        panel.add(crearLabel("* Número de Serie:", true), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        txtNumeroSerie = crearTextField();
        panel.add(txtNumeroSerie, gbc);
        
        gbc.gridx = 2;
        gbc.weightx = 0;
        lblValidacionSerie = crearLabelValidacion();
        panel.add(lblValidacionSerie, gbc);

        // Especificaciones
        gbc.gridy = 6;
        gbc.gridx = 0;
        gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        panel.add(crearLabel("Especificaciones:", false), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 0.3;
        txtEspecificaciones = crearTextArea(4);
        txtEspecificaciones.setToolTipText("Detalles técnicos del activo (procesador, RAM, etc.)");
        JScrollPane scrollEspec = new JScrollPane(txtEspecificaciones);
        panel.add(scrollEspec, gbc);

        // Sección 2: Información de Adquisición
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        agregarSeccion(panel, gbc, 7, "INFORMACIÓN DE ADQUISICIÓN", VERDE_SECUNDARIO);

        // Fecha de Adquisición (obligatorio)
        gbc.gridy = 8;
        gbc.gridx = 0;
        gbc.weightx = 0;
        panel.add(crearLabel("* Fecha de Adquisición:", true), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        txtFechaAdquisicion = crearTextField();
        txtFechaAdquisicion.setToolTipText("Formato: dd/MM/yyyy (ej: 15/03/2024)");
        panel.add(txtFechaAdquisicion, gbc);
        
        gbc.gridx = 2;
        gbc.weightx = 0;
        lblValidacionFecha = crearLabelValidacion();
        panel.add(lblValidacionFecha, gbc);

        // Sección 3: Ubicación y Estado
        agregarSeccion(panel, gbc, 9, "UBICACIÓN Y ESTADO", VERDE_SECUNDARIO);

        // Estado
        gbc.gridy = 10;
        gbc.gridx = 0;
        gbc.weightx = 0;
        panel.add(crearLabel("Estado:", false), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        cmbEstado = crearComboBox();
        for (Activo.Estado estado : Activo.Estado.values()) {
            cmbEstado.addItem(estado);
        }
        cmbEstado.setSelectedItem(Activo.Estado.Operativo);
        panel.add(cmbEstado, gbc);

        // Ubicación Actual (obligatorio)
        gbc.gridy = 11;
        gbc.gridx = 0;
        gbc.weightx = 0;
        panel.add(crearLabel("* Ubicación Actual:", true), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        cmbUbicacion = crearComboBox();
        panel.add(cmbUbicacion, gbc);

        // Responsable Actual
        gbc.gridy = 12;
        gbc.gridx = 0;
        gbc.weightx = 0;
        panel.add(crearLabel("Responsable Actual:", false), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        txtResponsable = crearTextField();
        txtResponsable.setToolTipText("Nombre del responsable del activo");
        panel.add(txtResponsable, gbc);

        // Observaciones
        gbc.gridy = 13;
        gbc.gridx = 0;
        gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        panel.add(crearLabel("Observaciones:", false), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 0.3;
        txtObservaciones = crearTextArea(3);
        JScrollPane scrollObs = new JScrollPane(txtObservaciones);
        panel.add(scrollObs, gbc);

        return panel;
    }

    private void agregarSeccion(JPanel panel, GridBagConstraints gbc, int fila, String titulo, Color color) {
        gbc.gridy = fila;
        gbc.gridx = 0;
        gbc.gridwidth = 3;
        gbc.weightx = 1.0;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        
        JPanel panelSeccion = new JPanel(new BorderLayout());
        panelSeccion.setBackground(color);
        panelSeccion.setBorder(new CompoundBorder(
            new LineBorder(color.darker(), 1),
            new EmptyBorder(8, 15, 8, 15)
        ));
        
        JLabel lblSeccion = new JLabel(titulo);
        lblSeccion.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblSeccion.setForeground(Color.WHITE);
        panelSeccion.add(lblSeccion, BorderLayout.WEST);
        
        panel.add(panelSeccion, gbc);
        
        // Resetear gridwidth para los siguientes elementos
        gbc.gridwidth = 1;
    }

    private JLabel crearLabel(String texto, boolean obligatorio) {
        JLabel label = new JLabel(texto);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        if (obligatorio) {
            label.setForeground(GRIS_OSCURO);
        } else {
            label.setForeground(GRIS_OSCURO);
        }
        return label;
    }

    private JLabel crearLabelValidacion() {
        JLabel label = new JLabel();
        label.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        label.setPreferredSize(new Dimension(20, 20));
        return label;
    }

    private JTextField crearTextField() {
        JTextField field = new JTextField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        field.setBorder(new CompoundBorder(
            new LineBorder(Color.LIGHT_GRAY, 1),
            new EmptyBorder(5, 10, 5, 10)
        ));
        field.setPreferredSize(new Dimension(200, 35));
        
        // Agregar validación en tiempo real
        field.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent e) {
                validarCampos();
            }
        });
        
        return field;
    }

    private <T> JComboBox<T> crearComboBox() {
        JComboBox<T> combo = new JComboBox<>();
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        combo.setBorder(new CompoundBorder(
            new LineBorder(Color.LIGHT_GRAY, 1),
            new EmptyBorder(2, 8, 2, 8)
        ));
        combo.setPreferredSize(new Dimension(200, 35));
        combo.setBackground(Color.WHITE);
        return combo;
    }

    private JTextArea crearTextArea(int filas) {
        JTextArea area = new JTextArea(filas, 20);
        area.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        area.setBorder(new EmptyBorder(8, 8, 8, 8));
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        return area;
    }

    private JPanel crearPanelBotones() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        panel.setBackground(GRIS_CLARO);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        btnCancelar = crearBoton("Cancelar", Color.GRAY, e -> cancelarRegistro());
        btnLimpiar = crearBoton("Limpiar", VERDE_SECUNDARIO, e -> limpiarFormulario());
        btnGuardar = crearBoton("Guardar Activo", VERDE_PRINCIPAL, e -> guardarActivo());

        panel.add(btnCancelar);
        panel.add(btnLimpiar);
        panel.add(btnGuardar);

        return panel;
    }

    private JButton crearBoton(String texto, Color color, ActionListener listener) {
        JButton boton = new JButton(texto);
        boton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        boton.setBackground(color);
        boton.setForeground(Color.WHITE);
        boton.setBorder(new EmptyBorder(10, 20, 10, 20));
        boton.setFocusPainted(false);
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        boton.addActionListener(listener);

        // Efectos hover
        boton.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                boton.setBackground(color.darker());
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                boton.setBackground(color);
            }
        });

        return boton;
    }

    private void cargarDatosIniciales() {
        try {
            // Cargar tipos de activos
            List<TipoActivo> tiposActivos = tipoActivoDAO.obtenerTodos();
            cmbTipoActivo.removeAllItems();
            cmbTipoActivo.addItem(null); // Opción vacía
            for (TipoActivo tipo : tiposActivos) {
                cmbTipoActivo.addItem(tipo);
            }

            // Cargar ubicaciones
            List<Ubicacion> ubicaciones = ubicacionDAO.obtenerTodas();
            cmbUbicacion.removeAllItems();
            cmbUbicacion.addItem(null); // Opción vacía
            for (Ubicacion ubicacion : ubicaciones) {
                cmbUbicacion.addItem(ubicacion);
            }

            // Generar número de activo automático
            generarNumeroActivoAutomatico();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error al cargar los datos iniciales: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void generarNumeroActivoAutomatico() {
        try {
            String numeroSugerido = activoService.generarNumeroActivoAutomatico();
            txtNumeroActivo.setText(numeroSugerido);
            lblValidacionNumero.setText("✓");
            lblValidacionNumero.setForeground(Color.GREEN);
        } catch (Exception e) {
            lblValidacionNumero.setText("⚠");
            lblValidacionNumero.setForeground(Color.ORANGE);
        }
    }

    private void validarCampos() {
        // Validar número de activo
        validarNumeroActivo();
        
        // Validar fecha
        validarFecha();
        
        // Validar número de serie
        validarNumeroSerie();
        
        // Habilitar/deshabilitar botón guardar
        actualizarEstadoBotonGuardar();
    }

    private void validarNumeroActivo() {
        String numero = txtNumeroActivo.getText().trim();
        if (numero.isEmpty()) {
            lblValidacionNumero.setText("");
            return;
        }
        
        // Validar formato ACT-YYYY-NNNN
        Pattern patron = Pattern.compile("^ACT-\\d{4}-\\d{4}$");
        if (patron.matcher(numero).matches()) {
            try {
                // Verificar que no exista
                boolean existe = activoService.existeNumeroActivo(numero);
                if (existe) {
                    lblValidacionNumero.setText("✗");
                    lblValidacionNumero.setForeground(Color.RED);
                    lblValidacionNumero.setToolTipText("El número ya existe");
                } else {
                    lblValidacionNumero.setText("✓");
                    lblValidacionNumero.setForeground(Color.GREEN);
                    lblValidacionNumero.setToolTipText("Número válido");
                }
            } catch (Exception e) {
                lblValidacionNumero.setText("⚠");
                lblValidacionNumero.setForeground(Color.ORANGE);
                lblValidacionNumero.setToolTipText("Error al validar");
            }
        } else {
            lblValidacionNumero.setText("✗");
            lblValidacionNumero.setForeground(Color.RED);
            lblValidacionNumero.setToolTipText("Formato incorrecto (ACT-YYYY-NNNN)");
        }
    }

    private void validarFecha() {
        String fecha = txtFechaAdquisicion.getText().trim();
        if (fecha.isEmpty()) {
            lblValidacionFecha.setText("");
            return;
        }
        
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            LocalDate fechaParsed = LocalDate.parse(fecha, formatter);
            
            // Validar que no sea futura
            if (fechaParsed.isAfter(LocalDate.now())) {
                lblValidacionFecha.setText("⚠");
                lblValidacionFecha.setForeground(Color.ORANGE);
                lblValidacionFecha.setToolTipText("La fecha no puede ser futura");
            } else {
                lblValidacionFecha.setText("✓");
                lblValidacionFecha.setForeground(Color.GREEN);
                lblValidacionFecha.setToolTipText("Fecha válida");
            }
        } catch (DateTimeParseException e) {
            lblValidacionFecha.setText("✗");
            lblValidacionFecha.setForeground(Color.RED);
            lblValidacionFecha.setToolTipText("Formato incorrecto (dd/MM/yyyy)");
        }
    }

    private void validarNumeroSerie() {
        String numero = txtNumeroSerie.getText().trim();
        if (numero.isEmpty()) {
            lblValidacionSerie.setText("");
            return;
        }
        
        try {
            boolean existe = activoService.existeNumeroSerie(numero);
            if (existe) {
                lblValidacionSerie.setText("⚠");
                lblValidacionSerie.setForeground(Color.ORANGE);
                lblValidacionSerie.setToolTipText("El número de serie ya existe");
            } else {
                lblValidacionSerie.setText("✓");
                lblValidacionSerie.setForeground(Color.GREEN);
                lblValidacionSerie.setToolTipText("Número de serie válido");
            }
        } catch (Exception e) {
            lblValidacionSerie.setText("⚠");
            lblValidacionSerie.setForeground(Color.ORANGE);
            lblValidacionSerie.setToolTipText("Error al validar");
        }
    }

    private void actualizarEstadoBotonGuardar() {
        boolean formValido = validarFormularioCompleto();
        btnGuardar.setEnabled(formValido);
        
        if (formValido) {
            btnGuardar.setBackground(VERDE_PRINCIPAL);
            btnGuardar.setToolTipText("Guardar el activo");
        } else {
            btnGuardar.setBackground(Color.GRAY);
            btnGuardar.setToolTipText("Complete todos los campos obligatorios");
        }
    }

    private boolean validarFormularioCompleto() {
        return !txtNumeroActivo.getText().trim().isEmpty() &&
               cmbTipoActivo.getSelectedItem() != null &&
               !txtMarca.getText().trim().isEmpty() &&
               !txtModelo.getText().trim().isEmpty() &&
               !txtNumeroSerie.getText().trim().isEmpty() &&
               !txtFechaAdquisicion.getText().trim().isEmpty() &&
               cmbUbicacion.getSelectedItem() != null &&
               "✓".equals(lblValidacionNumero.getText()) &&
               "✓".equals(lblValidacionFecha.getText());
    }

    private void guardarActivo() {
        if (!validarFormularioCompleto()) {
            JOptionPane.showMessageDialog(this,
                "Por favor complete todos los campos obligatorios correctamente.",
                "Validación",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // Crear el objeto Activo
            Activo nuevoActivo = new Activo();
            nuevoActivo.setActNumeroActivo(txtNumeroActivo.getText().trim());
            
            TipoActivo tipoSeleccionado = (TipoActivo) cmbTipoActivo.getSelectedItem();
            nuevoActivo.setTipActId(tipoSeleccionado.getTipActId());
            
            nuevoActivo.setActMarca(txtMarca.getText().trim());
            nuevoActivo.setActModelo(txtModelo.getText().trim());
            nuevoActivo.setActNumeroSerie(txtNumeroSerie.getText().trim());
            nuevoActivo.setActEspecificaciones(txtEspecificaciones.getText().trim());
            
            // Parsear fecha
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            LocalDate fecha = LocalDate.parse(txtFechaAdquisicion.getText().trim(), formatter);
            nuevoActivo.setActFechaAdquisicion(fecha);
            
            nuevoActivo.setActEstado((Activo.Estado) cmbEstado.getSelectedItem());
            
            Ubicacion ubicacionSeleccionada = (Ubicacion) cmbUbicacion.getSelectedItem();
            nuevoActivo.setActUbicacionActual(ubicacionSeleccionada.getUbiId());
            
            nuevoActivo.setActResponsableActual(txtResponsable.getText().trim());
            nuevoActivo.setActObservaciones(txtObservaciones.getText().trim());
            nuevoActivo.setCreadoPor(usuarioLogueadoId);

            // Guardar en la base de datos
            boolean guardado = activoService.crearActivo(nuevoActivo);

            if (guardado) {
                JOptionPane.showMessageDialog(this,
                    "✅ Activo registrado exitosamente!\n\n" +
                    "Número: " + nuevoActivo.getActNumeroActivo() + "\n" +
                    "Tipo: " + tipoSeleccionado.getNombre() + "\n" +
                    "Marca/Modelo: " + nuevoActivo.getActMarca() + " " + nuevoActivo.getActModelo(),
                    "Registro Exitoso",
                    JOptionPane.INFORMATION_MESSAGE);

                // Limpiar formulario para siguiente registro
                limpiarFormulario();
                
                // Actualizar la ventana principal si es necesario
                if (ventanaPrincipal != null) {
                    ventanaPrincipal.actualizarDashboard();
                }
            } else {
                JOptionPane.showMessageDialog(this,
                    "Error al guardar el activo. Verifique los datos e intente nuevamente.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error al guardar el activo: " + e.getMessage(),
                "Error del Sistema",
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void limpiarFormulario() {
        txtNumeroActivo.setText("");
        if (cmbTipoActivo.getItemCount() > 0) {
            cmbTipoActivo.setSelectedIndex(0);
        }
        txtMarca.setText("");
        txtModelo.setText("");
        txtNumeroSerie.setText("");
        txtEspecificaciones.setText("");
        txtFechaAdquisicion.setText("");
        cmbEstado.setSelectedItem(Activo.Estado.Operativo);
        if (cmbUbicacion.getItemCount() > 0) {
            cmbUbicacion.setSelectedIndex(0);
        }
        txtResponsable.setText("");
        txtObservaciones.setText("");
        
        // Limpiar validaciones
        lblValidacionNumero.setText("");
        lblValidacionFecha.setText("");
        lblValidacionSerie.setText("");
        
        // Generar nuevo número automático
        generarNumeroActivoAutomatico();
        
        // Enfocar primer campo
        txtNumeroActivo.requestFocus();
    }

    private void cancelarRegistro() {
        int respuesta = JOptionPane.showConfirmDialog(this,
            "¿Está seguro que desea cancelar? Se perderán todos los datos ingresados.",
            "Confirmar Cancelación",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);
            
        if (respuesta == JOptionPane.YES_OPTION) {
            if (ventanaPrincipal != null) {
                ventanaPrincipal.mostrarDashboard();
            }
        }
    }
}
