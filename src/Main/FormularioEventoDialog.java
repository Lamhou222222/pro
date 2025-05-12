package Main;

import controllers.AuthController;
import controllers.EventoController;
import controllers.UsuarioController;
import model.EventoRefugio;
import model.Usuario;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.FocusListener;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar; // Para construir la fecha
import java.util.Date;    // Para cargar datos existentes
import java.util.List;

public class FormularioEventoDialog extends JDialog {
    private EventoController eventoController;
    private UsuarioController usuarioController;
    private EventoRefugio eventoActual;
    private boolean guardadoExitoso = false;

    // Componentes UI para Fecha/Hora con campos separados
    private JTextField nombreEventoField;
    private JTextArea descripcionEventoArea;

    // Para Fecha Inicio
    private JTextField diaInicioField, mesInicioField, anioInicioField;
    private JTextField horaInicioField, minInicioField;

    // Para Fecha Fin (opcional)
    private JTextField diaFinField, mesFinField, anioFinField;
    private JTextField horaFinField, minFinField;
    private JCheckBox chkSinFechaFin;

    private JTextField ubicacionField;
    private JTextField tipoEventoField;
    private JComboBox<Usuario> organizadorComboBox;


    public FormularioEventoDialog(Dialog owner, EventoController evCtrl, UsuarioController usrCtrl, EventoRefugio evento) {
        super(owner, (evento == null ? "Crear Nuevo Evento" : "Editar Evento") + " [Entrada Manual Fecha/Hora]", true);
        this.eventoController = evCtrl;
        this.usuarioController = usrCtrl;
        this.eventoActual = evento;

        initComponents();
        if (eventoActual != null) {
            cargarDatosEvento();
        } else {
            // Placeholders para nuevos eventos
            diaInicioField.setText("dd"); mesInicioField.setText("mm"); anioInicioField.setText("aaaa");
            horaInicioField.setText("HH"); minInicioField.setText("MM");
            chkSinFechaFin.setSelected(true); // Por defecto sin fecha de fin
            toggleFechaFinFields(false); // Deshabilitar campos de fecha fin
        }

        pack();
        setMinimumSize(new Dimension(600, getHeight())); // Ajustar ancho mínimo
        setLocationRelativeTo(owner);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    }

    private void initComponents() {
        setLayout(new BorderLayout(10,10));
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        int yPos = 0;

        addLabelAndField(formPanel, "Nombre del Evento*:", nombreEventoField = new JTextField(30), yPos++, gbc);
        
        gbc.gridx = 0; gbc.gridy = yPos; gbc.anchor = GridBagConstraints.NORTHEAST; formPanel.add(new JLabel("Descripción:"), gbc);
        descripcionEventoArea = new JTextArea(4, 30);
        descripcionEventoArea.setLineWrap(true);
        descripcionEventoArea.setWrapStyleWord(true);
        JScrollPane scrollDesc = new JScrollPane(descripcionEventoArea);
        scrollDesc.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        gbc.gridx = 1; gbc.gridy = yPos++; gbc.weighty = 0.3; gbc.fill = GridBagConstraints.BOTH; gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(scrollDesc, gbc);
        gbc.weighty = 0; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.anchor = GridBagConstraints.CENTER; // Reset

        // --- Fecha y Hora Inicio ---
        gbc.gridx = 0; gbc.gridy = yPos; gbc.anchor = GridBagConstraints.EAST; formPanel.add(new JLabel("Fecha Inicio (dd/mm/aaaa)*:"), gbc);
        JPanel panelFechaInicio = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 0));
        diaInicioField = createNumericField(2, "dd"); mesInicioField = createNumericField(2, "mm"); anioInicioField = createNumericField(4, "aaaa");
        panelFechaInicio.add(diaInicioField); panelFechaInicio.add(new JLabel("/")); panelFechaInicio.add(mesInicioField); panelFechaInicio.add(new JLabel("/")); panelFechaInicio.add(anioInicioField);
        gbc.gridx = 1; gbc.gridy = yPos++; gbc.anchor = GridBagConstraints.WEST; formPanel.add(panelFechaInicio, gbc);

        gbc.gridx = 0; gbc.gridy = yPos; gbc.anchor = GridBagConstraints.EAST; formPanel.add(new JLabel("Hora Inicio (HH:MM)*:"), gbc);
        JPanel panelHoraInicio = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 0));
        horaInicioField = createNumericField(2, "HH"); minInicioField = createNumericField(2, "MM");
        panelHoraInicio.add(horaInicioField); panelHoraInicio.add(new JLabel(":")); panelHoraInicio.add(minInicioField);
        gbc.gridx = 1; gbc.gridy = yPos++; gbc.anchor = GridBagConstraints.WEST; formPanel.add(panelHoraInicio, gbc);

        // --- Fecha y Hora Fin ---
        chkSinFechaFin = new JCheckBox("Sin fecha de fin específica");
        chkSinFechaFin.setHorizontalTextPosition(SwingConstants.LEFT);
        gbc.gridx = 0; gbc.gridy = yPos++; gbc.gridwidth=2; gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(chkSinFechaFin, gbc);
        gbc.gridwidth=1;


        gbc.gridx = 0; gbc.gridy = yPos; gbc.anchor = GridBagConstraints.EAST; formPanel.add(new JLabel("Fecha Fin (dd/mm/aaaa):"), gbc);
        JPanel panelFechaFin = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 0));
        diaFinField = createNumericField(2, "dd"); mesFinField = createNumericField(2, "mm"); anioFinField = createNumericField(4, "aaaa");
        panelFechaFin.add(diaFinField); panelFechaFin.add(new JLabel("/")); panelFechaFin.add(mesFinField); panelFechaFin.add(new JLabel("/")); panelFechaFin.add(anioFinField);
        gbc.gridx = 1; gbc.gridy = yPos++; gbc.anchor = GridBagConstraints.WEST; formPanel.add(panelFechaFin, gbc);

        gbc.gridx = 0; gbc.gridy = yPos; gbc.anchor = GridBagConstraints.EAST; formPanel.add(new JLabel("Hora Fin (HH:MM):"), gbc);
        JPanel panelHoraFin = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 0));
        horaFinField = createNumericField(2, "HH"); minFinField = createNumericField(2, "MM");
        panelHoraFin.add(horaFinField); panelHoraFin.add(new JLabel(":")); panelHoraFin.add(minFinField);
        gbc.gridx = 1; gbc.gridy = yPos++; gbc.anchor = GridBagConstraints.WEST; formPanel.add(panelHoraFin, gbc);
        
        addLabelAndField(formPanel, "Ubicación:", ubicacionField = new JTextField(30), yPos++, gbc);
        addLabelAndField(formPanel, "Tipo de Evento:", tipoEventoField = new JTextField(30), yPos++, gbc);

        gbc.gridx = 0; gbc.gridy = yPos; gbc.anchor = GridBagConstraints.EAST; formPanel.add(new JLabel("Organizador:"), gbc);
        organizadorComboBox = new JComboBox<>();
        cargarOrganizadores();
        gbc.gridx = 1; gbc.gridy = yPos++; gbc.anchor = GridBagConstraints.WEST; formPanel.add(organizadorComboBox, gbc);

        // Listener para el checkbox de fecha fin
        chkSinFechaFin.addActionListener(e -> toggleFechaFinFields(!chkSinFechaFin.isSelected()));


        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnGuardar = new JButton(eventoActual == null ? "Crear Evento" : "Guardar Cambios");
        JButton btnCancelar = new JButton("Cancelar");

        btnGuardar.addActionListener(this::guardarEvento);
        btnCancelar.addActionListener(e -> { guardadoExitoso = false; dispose(); });

        buttonPanel.add(btnCancelar);
        buttonPanel.add(btnGuardar);

        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JTextField createNumericField(int columns, String placeholder) {
        JTextField field = new JTextField(columns);
        addPlaceholderFocusListener(field, placeholder);
        // Podrías añadir un DocumentFilter aquí para permitir solo números y limitar longitud
        return field;
    }
    
    private void addPlaceholderFocusListener(JTextField field, String placeholder) {
        // Inicializar con placeholder si está vacío
        if (field.getText().isEmpty()) {
            field.setForeground(Color.GRAY);
            field.setText(placeholder);
        }
        field.addFocusListener(new FocusListener() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(Color.BLACK);
                }
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                if (field.getText().isEmpty()) {
                    field.setForeground(Color.GRAY);
                    field.setText(placeholder);
                }
            }
        });
    }


    private void toggleFechaFinFields(boolean enabled) {
        diaFinField.setEnabled(enabled);
        mesFinField.setEnabled(enabled);
        anioFinField.setEnabled(enabled);
        horaFinField.setEnabled(enabled);
        minFinField.setEnabled(enabled);
        if (!enabled) { 
            if (!"dd".equals(diaFinField.getText())) addPlaceholderFocusListener(diaFinField, "dd"); // Restaura placeholder si no tenía ya uno
            if (!"mm".equals(mesFinField.getText())) addPlaceholderFocusListener(mesFinField, "mm");
            if (!"aaaa".equals(anioFinField.getText())) addPlaceholderFocusListener(anioFinField, "aaaa");
            if (!"HH".equals(horaFinField.getText())) addPlaceholderFocusListener(horaFinField, "HH");
            if (!"MM".equals(minFinField.getText())) addPlaceholderFocusListener(minFinField, "MM");
        } else { 
            // Al habilitar, si tienen placeholder, limpiarlo para que el usuario escriba
            if ("dd".equals(diaFinField.getText())) diaFinField.setText("");
            if ("mm".equals(mesFinField.getText())) mesFinField.setText("");
            if ("aaaa".equals(anioFinField.getText())) anioFinField.setText("");
            if ("HH".equals(horaFinField.getText())) horaFinField.setText("");
            if ("MM".equals(minFinField.getText())) minFinField.setText("");
            diaFinField.setForeground(Color.BLACK); // Asegurar color normal
            // ... y para los otros campos de fecha fin
        }
    }
    
    private void addLabelAndField(JPanel panel, String labelText, JComponent field, int yPos, GridBagConstraints gbcParent) {
        GridBagConstraints gbcLabel = (GridBagConstraints) gbcParent.clone();
        gbcLabel.gridx = 0; gbcLabel.gridy = yPos; gbcLabel.anchor = GridBagConstraints.EAST; gbcLabel.weightx = 0.2;
        panel.add(new JLabel(labelText), gbcLabel);
        GridBagConstraints gbcField = (GridBagConstraints) gbcParent.clone();
        gbcField.gridx = 1; gbcField.gridy = yPos; gbcField.anchor = GridBagConstraints.WEST; gbcField.weightx = 0.8;
        panel.add(field, gbcField);
    }

    private void cargarOrganizadores() {
        List<Usuario> posiblesOrganizadores = new ArrayList<>();
        List<Usuario> todos = usuarioController.listarTodosLosUsuarios(); // Asume que quien abre esto tiene permiso
        if (todos != null) {
            for (Usuario u : todos) {
                if (u.getRol() == Usuario.RolUsuario.EMPLEADO || u.getRol() == Usuario.RolUsuario.ADMINISTRADOR) {
                    if(u.isActivo()){ 
                        posiblesOrganizadores.add(u);
                    }
                }
            }
        }
        organizadorComboBox.addItem(null); 
        for (Usuario u : posiblesOrganizadores) {
            organizadorComboBox.addItem(u);
        }
        organizadorComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Usuario) {
                    setText(((Usuario) value).getNombreCompleto() + " (@" + ((Usuario)value).getUsername() + ")");
                } else if (value == null && index == 0) {
                    setText("--- Sin Asignar / Actual ---");
                } else if (value == null) {
                    setText("---");
                }
                return this;
            }
        });
    }

    private void cargarDatosEvento() {
        nombreEventoField.setText(eventoActual.getNombreEvento());
        descripcionEventoArea.setText(eventoActual.getDescripcionEvento());
        
        if(eventoActual.getFechaInicioEvento() != null) {
            Timestamp tsInicio = eventoActual.getFechaInicioEvento();
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(tsInicio.getTime());
            diaInicioField.setText(String.format("%02d", cal.get(Calendar.DAY_OF_MONTH)));
            mesInicioField.setText(String.format("%02d", cal.get(Calendar.MONTH) + 1));
            anioInicioField.setText(String.valueOf(cal.get(Calendar.YEAR)));
            horaInicioField.setText(String.format("%02d", cal.get(Calendar.HOUR_OF_DAY)));
            minInicioField.setText(String.format("%02d", cal.get(Calendar.MINUTE)));
            clearPlaceholderColors(diaInicioField, mesInicioField, anioInicioField, horaInicioField, minInicioField);
        }
        
        if(eventoActual.getFechaFinEvento() != null) {
            chkSinFechaFin.setSelected(false);
            toggleFechaFinFields(true);
            Timestamp tsFin = eventoActual.getFechaFinEvento();
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(tsFin.getTime());
            diaFinField.setText(String.format("%02d", cal.get(Calendar.DAY_OF_MONTH)));
            mesFinField.setText(String.format("%02d", cal.get(Calendar.MONTH) + 1));
            anioFinField.setText(String.valueOf(cal.get(Calendar.YEAR)));
            horaFinField.setText(String.format("%02d", cal.get(Calendar.HOUR_OF_DAY)));
            minFinField.setText(String.format("%02d", cal.get(Calendar.MINUTE)));
            clearPlaceholderColors(diaFinField, mesFinField, anioFinField, horaFinField, minFinField);
        } else {
            chkSinFechaFin.setSelected(true);
            toggleFechaFinFields(false);
        }

        ubicacionField.setText(eventoActual.getUbicacion());
        tipoEventoField.setText(eventoActual.getTipoEvento());
        
        if (eventoActual.getIdUsuarioOrganizador() != null) {
            for (int i = 0; i < organizadorComboBox.getItemCount(); i++) {
                Usuario u = organizadorComboBox.getItemAt(i);
                if (u != null && u.getId() == eventoActual.getIdUsuarioOrganizador()) {
                    organizadorComboBox.setSelectedIndex(i);
                    break;
                }
            }
        } else {
            organizadorComboBox.setSelectedIndex(0); // "Sin Asignar"
        }
    }
    
    private void clearPlaceholderColors(JTextField... fields) {
        for (JTextField field : fields) {
            field.setForeground(Color.BLACK);
        }
    }

    private Timestamp parseTimestampFromSeparateFields(
            JTextField diaField, JTextField mesField, JTextField anioField,
            JTextField horaField, JTextField minField, String fieldSetName, boolean esOpcionalSiVacio) {

        String diaStr = diaField.getText().trim();
        String mesStr = mesField.getText().trim();
        String anioStr = anioField.getText().trim();
        String horaStr = horaField.getText().trim();
        String minStr = minField.getText().trim();

        // Si es opcional y todos los campos están con placeholder o realmente vacíos, retornar null
        boolean todosPlaceholdersOVacios = 
            ("dd".equals(diaStr) || diaStr.isEmpty()) && 
            ("mm".equals(mesStr) || mesStr.isEmpty()) &&
            ("aaaa".equals(anioStr) || anioStr.isEmpty()) &&
            ("HH".equals(horaStr) || horaStr.isEmpty()) &&
            ("MM".equals(minStr) || minStr.isEmpty());

        if (esOpcionalSiVacio && todosPlaceholdersOVacios) {
            return null;
        }
        
        // Validar que no sean placeholders si se espera un valor (o si se llenó algo en opcional)
        if ( "dd".equals(diaStr) || "mm".equals(mesStr) || "aaaa".equals(anioStr) ||
             "HH".equals(horaStr) || "MM".equals(minStr) ) {
            if (!esOpcionalSiVacio || !todosPlaceholdersOVacios) {
                 JOptionPane.showMessageDialog(this, "Complete todos los campos de " + fieldSetName + " (día, mes, año, hora, minuto) o déjelos como placeholders si es opcional y no desea establecerla.", "Error de Formato", JOptionPane.ERROR_MESSAGE);
                return null; 
            }
        }

        try {
            int dia = Integer.parseInt(diaStr);
            int mes = Integer.parseInt(mesStr); // Mes como 1-12
            int anio = Integer.parseInt(anioStr);
            int hora = Integer.parseInt(horaStr);
            int min = Integer.parseInt(minStr);

            // Validaciones de rango básicas
            if (dia < 1 || dia > 31) { JOptionPane.showMessageDialog(this, "Día inválido para " + fieldSetName, "Error", JOptionPane.ERROR_MESSAGE); return null; }
            if (mes < 1 || mes > 12) { JOptionPane.showMessageDialog(this, "Mes inválido para " + fieldSetName, "Error", JOptionPane.ERROR_MESSAGE); return null; }
            if (anio < 1900 || anio > 2100) { JOptionPane.showMessageDialog(this, "Año inválido para " + fieldSetName, "Error", JOptionPane.ERROR_MESSAGE); return null; } // Rango razonable
            if (hora < 0 || hora > 23) { JOptionPane.showMessageDialog(this, "Hora inválida para " + fieldSetName, "Error", JOptionPane.ERROR_MESSAGE); return null; }
            if (min < 0 || min > 59) { JOptionPane.showMessageDialog(this, "Minuto inválido para " + fieldSetName, "Error", JOptionPane.ERROR_MESSAGE); return null; }
            
            // Validación más estricta de la fecha (ej. 30 de Febrero)
            Calendar cal = Calendar.getInstance();
            cal.setLenient(false); // Importante para validación estricta
            cal.set(anio, mes - 1, dia, hora, min, 0); // mes - 1 porque Calendar es 0-indexado
            cal.set(Calendar.MILLISECOND, 0);
            
            // Intentar obtener la fecha del calendar para forzar la validación
            // Si la fecha no es válida (ej. 30 de febrero), lanzará una excepción
            Date validatedDate = cal.getTime(); 
            
            return new Timestamp(validatedDate.getTime());

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Formato numérico incorrecto en los campos de " + fieldSetName + ".", "Error de Formato", JOptionPane.ERROR_MESSAGE);
            return null;
        } catch (IllegalArgumentException ex) { // Calendar.set puede lanzar esto si la fecha es inválida con lenient=false
             JOptionPane.showMessageDialog(this, "Fecha inválida para " + fieldSetName + " (ej. día 30 de febrero). Verifique día, mes y año.", "Error de Fecha", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }


    private void guardarEvento(ActionEvent e) {
        String nombre = nombreEventoField.getText().trim();
        if (nombre.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nombre del evento es obligatorio.", "Error de Validación", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Timestamp tsInicio = parseTimestampFromSeparateFields(diaInicioField, mesInicioField, anioInicioField, horaInicioField, minInicioField, "Fecha de Inicio", false);
        if (tsInicio == null && !(diaInicioField.getText().equals("dd") && mesInicioField.getText().equals("mm"))) { // Si hubo error de parseo y no eran placeholders
             return;
        }
        if (tsInicio == null) { // Si realmente no se ingresó una fecha de inicio válida
             JOptionPane.showMessageDialog(this, "La Fecha y Hora de Inicio son obligatorias.", "Error de Validación", JOptionPane.ERROR_MESSAGE);
            return;
        }


        Timestamp tsFin = null;
        if (!chkSinFechaFin.isSelected()) {
            tsFin = parseTimestampFromSeparateFields(diaFinField, mesFinField, anioFinField, horaFinField, minFinField, "Fecha de Fin", true);
            if (tsFin == null && !(diaFinField.getText().equals("dd") && mesFinField.getText().equals("mm"))) { 
                // Si parseTimestampFromSeparateFields ya mostró un error (porque se intentó ingresar algo mal)
                // Y los campos no son simplemente placeholders indicando "no quiero fecha fin"
                if(!("dd".equals(diaFinField.getText()) && "mm".equals(mesFinField.getText()) && "aaaa".equals(anioFinField.getText()) &&
                     "HH".equals(horaFinField.getText()) && "MM".equals(minFinField.getText())) ) {
                     // Si no son todos placeholders, significa que el usuario intentó poner una fecha fin y falló el parseo
                    return;
                }
            }

            if (tsFin != null && tsFin.before(tsInicio)) {
                 JOptionPane.showMessageDialog(this, "La fecha de fin no puede ser anterior a la fecha de inicio.", "Error de Fechas", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }


        boolean isNew = (eventoActual == null);
        if (isNew) {
            eventoActual = new EventoRefugio();
        }

        eventoActual.setNombreEvento(nombre);
        eventoActual.setDescripcionEvento(descripcionEventoArea.getText().trim().isEmpty() ? null : descripcionEventoArea.getText().trim());
        eventoActual.setFechaInicioEvento(tsInicio);
        eventoActual.setFechaFinEvento(tsFin); 
        
        eventoActual.setUbicacion(ubicacionField.getText().trim().isEmpty() ? null : ubicacionField.getText().trim());
        eventoActual.setTipoEvento(tipoEventoField.getText().trim().isEmpty() ? null : tipoEventoField.getText().trim());
        
        Usuario organizadorSeleccionado = (Usuario) organizadorComboBox.getSelectedItem();
        if (organizadorSeleccionado != null) {
            eventoActual.setIdUsuarioOrganizador(organizadorSeleccionado.getId());
        } else {
            if(isNew && AuthController.isLoggedIn() && AuthController.isEmpleado()){
                eventoActual.setIdUsuarioOrganizador(AuthController.getUsuarioActual().getId());
            } else if (!isNew && eventoActual.getIdUsuarioOrganizador() != null) {
                eventoActual.setIdUsuarioOrganizador(null);
            } else {
                 eventoActual.setIdUsuarioOrganizador(null);
            }
        }
        
        boolean success;
        if (isNew) {
            success = eventoController.crearEvento(eventoActual);
        } else {
            success = eventoController.actualizarEvento(eventoActual);
        }

        if (success) {
            guardadoExitoso = true;
            JOptionPane.showMessageDialog(this.getParent(), // Mostrar sobre el diálogo padre (GestionEventosDialog)
                    "Evento guardado con éxito.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Error al guardar el evento. Revise la consola para más detalles.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isGuardadoExitoso() {
        return guardadoExitoso;
    }
}