package Main;

import controllers.AuthController;
import controllers.VoluntariadoController;
import controllers.UsuarioController;
import controllers.AnimalController;
import model.ActividadVoluntariado;
import model.Animal;
import model.Usuario;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.FocusListener;
import java.math.BigDecimal;
// import java.text.ParseException; // NO SE USA
// import java.text.SimpleDateFormat; // NO SE USA para parsear, solo para mostrar si es necesario
import java.util.ArrayList;
import java.util.Calendar; // Para construir la fecha
import java.util.Date;    // El modelo usa java.util.Date
import java.util.List;
import java.text.SimpleDateFormat; // Solo para formatear al cargar

public class FormularioActividadVoluntariadoDialog extends JDialog {
    private VoluntariadoController voluntariadoController;
    private UsuarioController usuarioController;
    private AnimalController animalController;
    private ActividadVoluntariado actividadOriginal;
    private ActividadVoluntariado actividadParaEditar;
    private boolean guardadoExitoso = false;

    // Componentes UI
    private JComboBox<Usuario> voluntarioComboBox;
    private JComboBox<Animal> animalAsociadoComboBox;
    // Campos para fecha manual separados
    private JTextField diaActividadField, mesActividadField, anioActividadField;
    private JTextField tipoActividadField;
    private JTextField duracionHorasField;
    private JTextArea descripcionArea;

    private final SimpleDateFormat sdfDisplay = new SimpleDateFormat("dd/MM/yyyy"); // Para mostrar fecha cargada

    public FormularioActividadVoluntariadoDialog(MainGui mainGui, VoluntariadoController volCtrl, UsuarioController usrCtrl, AnimalController aniCtrl, ActividadVoluntariado actividadAEditar) {
        super(mainGui, (actividadAEditar == null ? "Registrar Actividad" : "Editar Actividad") + " Voluntariado [Fecha Manual]", true);
        this.voluntariadoController = volCtrl;
        this.usuarioController = usrCtrl;
        this.animalController = aniCtrl;
        this.actividadOriginal = actividadAEditar;
        
        if (actividadAEditar != null) {
            this.actividadParaEditar = clonarActividad(actividadAEditar);
        } else {
            this.actividadParaEditar = new ActividadVoluntariado();
        }
        
        initComponents();

        if (this.actividadOriginal != null) {
            cargarDatosActividad();
        } else {
            // Placeholders para nuevos eventos
            addPlaceholderFocusListener(diaActividadField, "dd");
            addPlaceholderFocusListener(mesActividadField, "mm");
            addPlaceholderFocusListener(anioActividadField, "aaaa");
            configurarVoluntarioParaNuevaActividad();
        }

        pack();
        setMinimumSize(new Dimension(550, getHeight() + 40));
        setLocationRelativeTo(mainGui);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    }

    private ActividadVoluntariado clonarActividad(ActividadVoluntariado original) {
        ActividadVoluntariado clon = new ActividadVoluntariado();
        clon.setId(original.getId());
        clon.setIdVoluntario(original.getIdVoluntario());
        clon.setIdAnimalAsociado(original.getIdAnimalAsociado());
        clon.setFechaActividad(original.getFechaActividad() != null ? new Date(original.getFechaActividad().getTime()) : null);
        clon.setTipoActividad(original.getTipoActividad());
        clon.setDuracionHoras(original.getDuracionHoras());
        clon.setDescripcion(original.getDescripcion());
        return clon;
    }
    
    private void configurarVoluntarioParaNuevaActividad() {
        Usuario currentUser = AuthController.getUsuarioActual();
        if (AuthController.isVoluntario() && !AuthController.isAdmin() && currentUser != null) {
            seleccionarItemEnComboBox(voluntarioComboBox, currentUser.getId(), Usuario.class);
            voluntarioComboBox.setEnabled(false);
        }
    }

    private void initComponents() {
        setLayout(new BorderLayout(10,10));
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(15,15,15,15));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        int yPos = 0;

        gbc.gridx = 0; gbc.gridy = yPos; gbc.anchor = GridBagConstraints.EAST; 
        formPanel.add(new JLabel("Voluntario*:"), gbc);
        voluntarioComboBox = new JComboBox<>();
        cargarVoluntarios();
        gbc.gridx = 1; gbc.gridy = yPos++; gbc.anchor = GridBagConstraints.WEST; 
        formPanel.add(voluntarioComboBox, gbc);

        // --- Fecha Actividad Manual ---
        gbc.gridx = 0; gbc.gridy = yPos; gbc.anchor = GridBagConstraints.EAST; 
        formPanel.add(new JLabel("Fecha (dd/mm/aaaa)*:"), gbc);
        JPanel panelFecha = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 0));
        diaActividadField = createNumericField(2, "dd");
        mesActividadField = createNumericField(2, "mm");
        anioActividadField = createNumericField(4, "aaaa");
        panelFecha.add(diaActividadField); panelFecha.add(new JLabel("/"));
        panelFecha.add(mesActividadField); panelFecha.add(new JLabel("/"));
        panelFecha.add(anioActividadField);
        gbc.gridx = 1; gbc.gridy = yPos++; gbc.anchor = GridBagConstraints.WEST; 
        formPanel.add(panelFecha, gbc);

        addLabelAndField(formPanel, "Tipo Actividad*:", tipoActividadField = new JTextField(30), yPos++, gbc);
        addLabelAndField(formPanel, "Duración (horas)*:", duracionHorasField = new JTextField(5), yPos++, gbc);

        gbc.gridx = 0; gbc.gridy = yPos; gbc.anchor = GridBagConstraints.EAST; 
        formPanel.add(new JLabel("Animal Asociado:"), gbc);
        animalAsociadoComboBox = new JComboBox<>();
        cargarAnimales();
        gbc.gridx = 1; gbc.gridy = yPos++; gbc.anchor = GridBagConstraints.WEST; 
        formPanel.add(animalAsociadoComboBox, gbc);
        
        gbc.gridx = 0; gbc.gridy = yPos; gbc.anchor = GridBagConstraints.NORTHEAST; 
        formPanel.add(new JLabel("Descripción:"), gbc);
        descripcionArea = new JTextArea(4, 30);
        setupTextArea(descripcionArea);
        JScrollPane scrollDesc = new JScrollPane(descripcionArea);
        gbc.gridx = 1; gbc.gridy = yPos++; gbc.weighty = 0.4; gbc.fill = GridBagConstraints.BOTH; gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(scrollDesc, gbc);
        
        gbc.weighty = 0; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.anchor = GridBagConstraints.CENTER;

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnGuardar = new JButton(actividadOriginal == null ? "Registrar" : "Guardar");
        JButton btnCancelar = new JButton("Cancelar");

        btnGuardar.addActionListener(this::guardarActividad);
        btnCancelar.addActionListener(e -> { guardadoExitoso = false; dispose(); });

        buttonPanel.add(btnCancelar);
        buttonPanel.add(btnGuardar);

        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private void setupTextArea(JTextArea textArea) {
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setFont(new JTextField().getFont());
    }
    
    private JTextField createNumericField(int columns, String placeholder) {
        JTextField field = new JTextField(columns);
        addPlaceholderFocusListener(field, placeholder);
        return field;
    }

    private void addPlaceholderFocusListener(JTextField field, String placeholder) {
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
    
    private void addLabelAndField(JPanel panel, String labelText, JComponent field, int yPos, GridBagConstraints gbcParent) {
        GridBagConstraints gbcLabel = (GridBagConstraints) gbcParent.clone();
        gbcLabel.gridx = 0; gbcLabel.gridy = yPos; gbcLabel.anchor = GridBagConstraints.EAST; gbcLabel.weightx = 0.3;
        panel.add(new JLabel(labelText), gbcLabel);
        GridBagConstraints gbcField = (GridBagConstraints) gbcParent.clone();
        gbcField.gridx = 1; gbcField.gridy = yPos; gbcField.anchor = GridBagConstraints.WEST; gbcField.weightx = 0.7;
        panel.add(field, gbcField);
    }

    private void cargarVoluntarios() {
        List<Usuario> todosUsuarios = usuarioController.listarTodosLosUsuarios(); 
        voluntarioComboBox.addItem(null); 
        if (todosUsuarios != null) {
            for (Usuario u : todosUsuarios) {
                if (u.getRol() == Usuario.RolUsuario.VOLUNTARIO && u.isActivo()) {
                    voluntarioComboBox.addItem(u); 
                }
            }
        }
    }

    private void cargarAnimales() {
        List<Animal> animales = animalController.obtenerTodosLosAnimales(); 
        animalAsociadoComboBox.addItem(null); 
        if (animales != null) {
            for (Animal a : animales) {
                if (a.getEstadoAdopcion() != Animal.EstadoAdopcion.ADOPTADO) {
                     animalAsociadoComboBox.addItem(a);
                }
            }
        }
    }

    private void cargarDatosActividad() {
        seleccionarItemEnComboBox(voluntarioComboBox, actividadParaEditar.getIdVoluntario(), Usuario.class);
        if (AuthController.isVoluntario() && !AuthController.isAdmin() &&
            AuthController.getUsuarioActual() != null &&
            actividadParaEditar.getIdVoluntario() == AuthController.getUsuarioActual().getId()) {
            voluntarioComboBox.setEnabled(false);
        }

        seleccionarItemEnComboBox(animalAsociadoComboBox, actividadParaEditar.getIdAnimalAsociado(), Animal.class);

        if(actividadParaEditar.getFechaActividad() != null) { 
            Calendar cal = Calendar.getInstance();
            cal.setTime(actividadParaEditar.getFechaActividad()); // Modelo usa java.util.Date
            diaActividadField.setText(String.format("%02d", cal.get(Calendar.DAY_OF_MONTH)));
            mesActividadField.setText(String.format("%02d", cal.get(Calendar.MONTH) + 1));
            anioActividadField.setText(String.valueOf(cal.get(Calendar.YEAR)));
            clearPlaceholderColor(diaActividadField, "dd");
            clearPlaceholderColor(mesActividadField, "mm");
            clearPlaceholderColor(anioActividadField, "aaaa");
        } else {
             addPlaceholderFocusListener(diaActividadField, "dd");
             addPlaceholderFocusListener(mesActividadField, "mm");
             addPlaceholderFocusListener(anioActividadField, "aaaa");
        }

        tipoActividadField.setText(actividadParaEditar.getTipoActividad());
        if (actividadParaEditar.getDuracionHoras() != null) {
            duracionHorasField.setText(actividadParaEditar.getDuracionHoras().toPlainString());
        } else {
             duracionHorasField.setText("");
        }
        descripcionArea.setText(actividadParaEditar.getDescripcion());
    }
    
    private void clearPlaceholderColor(JTextField field, String placeholder) {
        if(!field.getText().equals(placeholder) && !field.getText().isEmpty()){
            field.setForeground(Color.BLACK);
        }
    }

    private <T> void seleccionarItemEnComboBox(JComboBox<T> comboBox, Integer idBuscado, Class<T> type) {
        if (idBuscado == null || idBuscado == 0) {
            comboBox.setSelectedItem(null);
            return;
        }
        for (int i = 0; i < comboBox.getItemCount(); i++) {
            T item = comboBox.getItemAt(i);
            if (item != null) {
                if (type.isAssignableFrom(Usuario.class) && item instanceof Usuario) {
                    if (((Usuario) item).getId() == idBuscado) { comboBox.setSelectedIndex(i); return; }
                } else if (type.isAssignableFrom(Animal.class) && item instanceof Animal) {
                     if (((Animal) item).getId() == idBuscado) { comboBox.setSelectedIndex(i); return; }
                }
            }
        }
        comboBox.setSelectedItem(null); 
    }
    
    private Date parseDateFromManualFields(String diaStr, String mesStr, String anioStr, String fieldSetName) {
        if (diaStr.isEmpty() || diaStr.equals("dd") || 
            mesStr.isEmpty() || mesStr.equals("mm") ||
            anioStr.isEmpty() || anioStr.equals("aaaa")) {
            JOptionPane.showMessageDialog(this, fieldSetName + " es obligatoria (dd/mm/aaaa).", "Validación", JOptionPane.ERROR_MESSAGE);
            return null;
        }
        try {
            int dia = Integer.parseInt(diaStr);
            int mes = Integer.parseInt(mesStr);
            int anio = Integer.parseInt(anioStr);

            if (dia < 1 || dia > 31 || mes < 1 || mes > 12 || anio < 1900 || anio > 2100) { // Validaciones básicas
                 JOptionPane.showMessageDialog(this, "Valores inválidos para " + fieldSetName + ".", "Error de Formato", JOptionPane.ERROR_MESSAGE);
                return null;
            }

            Calendar calendar = Calendar.getInstance();
            calendar.setLenient(false); // Para validación estricta (ej. 30 de Feb)
            // Calendar.MONTH es 0-indexado (Enero=0, Febrero=1, etc.)
            calendar.set(anio, mes - 1, dia, 0, 0, 0); // Hora, min, seg a 0
            calendar.set(Calendar.MILLISECOND, 0);
            
            // Forzar la validación al obtener el tiempo. Si la fecha es inválida, lanzará IllegalArgumentException
            Date fechaValidada = calendar.getTime(); 
            return fechaValidada; // Devuelve java.util.Date

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Formato numérico incorrecto para " + fieldSetName + ".", "Error de Formato", JOptionPane.ERROR_MESSAGE);
            return null;
        } catch (IllegalArgumentException ex) { // Captura la excepción de Calendar si la fecha no es válida
            JOptionPane.showMessageDialog(this, "Fecha inválida para " + fieldSetName + " (ej. día 30 de febrero). Verifique los valores.", "Error de Fecha", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    private BigDecimal parseBigDecimalSimple(JTextField field, String nombreCampo) {
        // ... (mismo método que en la respuesta anterior) ...
        String texto = field.getText().trim();
        if (texto.isEmpty()) {
            JOptionPane.showMessageDialog(this, nombreCampo + " es obligatorio.", "Validación", JOptionPane.ERROR_MESSAGE);
            return null;
        }
        try {
            BigDecimal valor = new BigDecimal(texto.replace(',', '.'));
            if (valor.compareTo(BigDecimal.ZERO) <= 0) {
                JOptionPane.showMessageDialog(this, nombreCampo + " debe ser un número positivo.", "Validación", JOptionPane.ERROR_MESSAGE);
                return null;
            }
            return valor;
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Valor numérico inválido para " + nombreCampo + ".", "Error de Formato", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    private void guardarActividad(ActionEvent e) {
        Usuario voluntarioSel = (Usuario) voluntarioComboBox.getSelectedItem();
        Date fechaAct = parseDateFromManualFields(diaActividadField.getText(), mesActividadField.getText(), anioActividadField.getText(), "Fecha de Actividad");
        String tipoAct = tipoActividadField.getText().trim();
        BigDecimal duracion = parseBigDecimalSimple(duracionHorasField, "Duración (horas)");

        if (voluntarioSel == null) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un voluntario.", "Validación", JOptionPane.ERROR_MESSAGE);
            voluntarioComboBox.requestFocus(); return;
        }
        if (fechaAct == null) { 
            // El error ya se mostró en parseDateFromManualFields
            // Podríamos poner foco en el primer campo de fecha: diaActividadField.requestFocus();
            return;
        }
        if (tipoAct.isEmpty()) {
            JOptionPane.showMessageDialog(this, "El tipo de actividad es obligatorio.", "Validación", JOptionPane.ERROR_MESSAGE);
            tipoActividadField.requestFocus(); return;
        }
        if (duracion == null) { 
            duracionHorasField.requestFocus(); return;
        }

        actividadParaEditar.setIdVoluntario(voluntarioSel.getId());
        actividadParaEditar.setFechaActividad(fechaAct); // Modelo usa java.util.Date
        actividadParaEditar.setTipoActividad(tipoAct);
        actividadParaEditar.setDuracionHoras(duracion);

        Animal animalSel = (Animal) animalAsociadoComboBox.getSelectedItem();
        actividadParaEditar.setIdAnimalAsociado(animalSel != null ? animalSel.getId() : null);
        actividadParaEditar.setDescripcion(descripcionArea.getText().trim().isEmpty() ? null : descripcionArea.getText().trim());
        
        boolean success;
        if (actividadOriginal == null) { // Creando nueva
            success = voluntariadoController.registrarActividad(actividadParaEditar);
        } else { // Editando existente
            success = voluntariadoController.actualizarActividad(actividadParaEditar);
        }

        if (success) {
            guardadoExitoso = true;
            JOptionPane.showMessageDialog(this.getOwner(), "Actividad guardada con éxito.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Error al guardar la actividad. Revise la consola.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isGuardadoExitoso() {
        return guardadoExitoso;
    }
}