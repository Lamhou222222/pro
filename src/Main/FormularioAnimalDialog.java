package Main;

import controllers.AnimalController;

import controllers.UsuarioController; // Para el ComboBox de responsables
import model.Animal;
import model.Usuario;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.FocusListener;
// import java.sql.Timestamp; // No se maneja directamente aquí, el repo lo hace
import java.util.ArrayList;
import java.util.List;

public class FormularioAnimalDialog extends JDialog {
    private AnimalController animalController;
    private UsuarioController usuarioController; // Para cargar responsables
    private Animal animalActual; // null si es nuevo, o el animal a editar
    private boolean guardadoExitoso = false;

    // Componentes UI
    private JTextField nombreField;
    private JTextField especieField; // Podría ser un JComboBox si las especies son fijas
    private JTextField razaField;
    private JTextField edadAniosField;
    private JTextField edadMesesField;
    private JComboBox<String> generoComboBox;
    private JTextField colorField;
    private JComboBox<String> tamanioComboBox;
    private JTextArea descripcionCaracterArea;
    private JTextArea historialMedicoArea;
    private JTextArea necesidadesEspecialesArea;
    private JTextField fotoUrlField;
    private JComboBox<Animal.EstadoAdopcion> estadoAdopcionComboBox;
    private JComboBox<Usuario> responsableComboBox;

    private final String[] GENEROS = {"Macho", "Hembra", "Desconocido"};
    private final String[] TAMANIOS = {"Pequeño", "Mediano", "Grande", "Gigante"};


    public FormularioAnimalDialog(Dialog owner, AnimalController animalCtrl, UsuarioController usrCtrl, Animal animal) {
        super(owner, (animal == null ? "Crear Nuevo Animal" : "Editar Animal") + " [TEXTO PLANO CONTEXTO]", true);
        this.animalController = animalCtrl;
        this.usuarioController = usrCtrl;
        this.animalActual = animal; // Si es null, estamos creando uno nuevo

        initComponents();
        if (animalActual != null) {
            cargarDatosAnimal();
        }

        pack();
        setMinimumSize(new Dimension(600, 700)); // Puede necesitar ajuste
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

        addLabelAndField(formPanel, "Nombre*:", nombreField = new JTextField(30), yPos++, gbc);
        addLabelAndField(formPanel, "Especie*:", especieField = new JTextField(30), yPos++, gbc); // O JComboBox
        addLabelAndField(formPanel, "Raza:", razaField = new JTextField(30), yPos++, gbc);
        
        // Edad
        JPanel edadPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        edadAniosField = new JTextField(3);
        edadMesesField = new JTextField(3);
        addPlaceholderFocusListener(edadAniosField, "años");
        addPlaceholderFocusListener(edadMesesField, "meses");
        edadPanel.add(new JLabel("Edad Estimada:"));
        edadPanel.add(edadAniosField);
        edadPanel.add(new JLabel("años y"));
        edadPanel.add(edadMesesField);
        edadPanel.add(new JLabel("meses"));
        gbc.gridx = 0; gbc.gridy = yPos; formPanel.add(new JLabel(""), gbc); // Etiqueta vacía para alinear
        gbc.gridx = 1; gbc.gridy = yPos++; formPanel.add(edadPanel, gbc);


        gbc.gridx = 0; gbc.gridy = yPos; formPanel.add(new JLabel("Género:"), gbc);
        generoComboBox = new JComboBox<>(GENEROS);
        generoComboBox.insertItemAt("--- Seleccione ---", 0);
        generoComboBox.setSelectedIndex(0);
        gbc.gridx = 1; gbc.gridy = yPos++; formPanel.add(generoComboBox, gbc);

        addLabelAndField(formPanel, "Color:", colorField = new JTextField(30), yPos++, gbc);

        gbc.gridx = 0; gbc.gridy = yPos; formPanel.add(new JLabel("Tamaño:"), gbc);
        tamanioComboBox = new JComboBox<>(TAMANIOS);
        tamanioComboBox.insertItemAt("--- Seleccione ---", 0);
        tamanioComboBox.setSelectedIndex(0);
        gbc.gridx = 1; gbc.gridy = yPos++; formPanel.add(tamanioComboBox, gbc);

        gbc.gridx = 0; gbc.gridy = yPos; gbc.anchor = GridBagConstraints.NORTHEAST; formPanel.add(new JLabel("Carácter:"), gbc);
        descripcionCaracterArea = new JTextArea(3, 30);
        setupTextArea(descripcionCaracterArea);
        gbc.gridx = 1; gbc.gridy = yPos++; gbc.weighty = 0.2; gbc.fill = GridBagConstraints.BOTH; gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(new JScrollPane(descripcionCaracterArea), gbc);
        gbc.weighty = 0; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.anchor = GridBagConstraints.CENTER;

        gbc.gridx = 0; gbc.gridy = yPos; gbc.anchor = GridBagConstraints.NORTHEAST; formPanel.add(new JLabel("Hist. Médico:"), gbc);
        historialMedicoArea = new JTextArea(3, 30);
        setupTextArea(historialMedicoArea);
        gbc.gridx = 1; gbc.gridy = yPos++; gbc.weighty = 0.2; gbc.fill = GridBagConstraints.BOTH; gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(new JScrollPane(historialMedicoArea), gbc);
        gbc.weighty = 0; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.anchor = GridBagConstraints.CENTER;

        gbc.gridx = 0; gbc.gridy = yPos; gbc.anchor = GridBagConstraints.NORTHEAST; formPanel.add(new JLabel("Nec. Especiales:"), gbc);
        necesidadesEspecialesArea = new JTextArea(3, 30);
        setupTextArea(necesidadesEspecialesArea);
        gbc.gridx = 1; gbc.gridy = yPos++; gbc.weighty = 0.2; gbc.fill = GridBagConstraints.BOTH; gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(new JScrollPane(necesidadesEspecialesArea), gbc);
        gbc.weighty = 0; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.anchor = GridBagConstraints.CENTER;

        addLabelAndField(formPanel, "URL Foto:", fotoUrlField = new JTextField(30), yPos++, gbc);

        gbc.gridx = 0; gbc.gridy = yPos; formPanel.add(new JLabel("Estado Adopción*:"), gbc);
        estadoAdopcionComboBox = new JComboBox<>(Animal.EstadoAdopcion.values());
        gbc.gridx = 1; gbc.gridy = yPos++; formPanel.add(estadoAdopcionComboBox, gbc);

        gbc.gridx = 0; gbc.gridy = yPos; formPanel.add(new JLabel("Usuario Responsable:"), gbc);
        responsableComboBox = new JComboBox<>();
        cargarResponsables();
        gbc.gridx = 1; gbc.gridy = yPos++; formPanel.add(responsableComboBox, gbc);


        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnGuardar = new JButton(animalActual == null ? "Crear Animal" : "Guardar Cambios");
        JButton btnCancelar = new JButton("Cancelar");

        btnGuardar.addActionListener(this::guardarAnimal);
        btnCancelar.addActionListener(e -> {
            guardadoExitoso = false;
            dispose();
        });

        buttonPanel.add(btnCancelar);
        buttonPanel.add(btnGuardar);

        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void setupTextArea(JTextArea textArea) {
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setFont(new JTextField().getFont()); // Usar la misma fuente que JTextField
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
        gbcLabel.gridx = 0; gbcLabel.gridy = yPos; gbcLabel.anchor = GridBagConstraints.EAST; gbcLabel.weightx = 0.2;
        panel.add(new JLabel(labelText), gbcLabel);
        GridBagConstraints gbcField = (GridBagConstraints) gbcParent.clone();
        gbcField.gridx = 1; gbcField.gridy = yPos; gbcField.anchor = GridBagConstraints.WEST; gbcField.weightx = 0.8;
        panel.add(field, gbcField);
    }

    private void cargarResponsables() {
        List<Usuario> posiblesResponsables = new ArrayList<>();
        List<Usuario> todos = usuarioController.listarTodosLosUsuarios(); // Asume que quien abre esto tiene permiso
        if (todos != null) {
            for (Usuario u : todos) {
                if (u.getRol() == Usuario.RolUsuario.EMPLEADO || u.getRol() == Usuario.RolUsuario.ADMINISTRADOR) {
                    if(u.isActivo()){ 
                        posiblesResponsables.add(u);
                    }
                }
            }
        }
        responsableComboBox.addItem(null); // Opción para "Sin Asignar"
        for (Usuario u : posiblesResponsables) {
            responsableComboBox.addItem(u);
        }
        // Usar el toString() de Usuario para mostrar en ComboBox
    }

    private void cargarDatosAnimal() {
        nombreField.setText(animalActual.getNombre());
        especieField.setText(animalActual.getEspecie());
        razaField.setText(animalActual.getRaza());

        if (animalActual.getEdadEstimadaAnios() != null) edadAniosField.setText(String.valueOf(animalActual.getEdadEstimadaAnios())); else addPlaceholderFocusListener(edadAniosField,"años");
        if (animalActual.getEdadEstimadaMeses() != null) edadMesesField.setText(String.valueOf(animalActual.getEdadEstimadaMeses())); else addPlaceholderFocusListener(edadMesesField,"meses");
        clearPlaceholderColorIfValue(edadAniosField, "años");
        clearPlaceholderColorIfValue(edadMesesField, "meses");


        if (animalActual.getGenero() != null) generoComboBox.setSelectedItem(animalActual.getGenero()); else generoComboBox.setSelectedIndex(0);
        colorField.setText(animalActual.getColor());
        if (animalActual.getTamanio() != null) tamanioComboBox.setSelectedItem(animalActual.getTamanio()); else tamanioComboBox.setSelectedIndex(0);
        
        descripcionCaracterArea.setText(animalActual.getDescripcionCaracter());
        historialMedicoArea.setText(animalActual.getHistorialMedico());
        necesidadesEspecialesArea.setText(animalActual.getNecesidadesEspeciales());
        fotoUrlField.setText(animalActual.getFotoUrl());
        estadoAdopcionComboBox.setSelectedItem(animalActual.getEstadoAdopcion());
        
        if (animalActual.getIdUsuarioResponsable() != null) {
            for (int i = 0; i < responsableComboBox.getItemCount(); i++) {
                Usuario u = responsableComboBox.getItemAt(i);
                if (u != null && u.getId() == animalActual.getIdUsuarioResponsable()) {
                    responsableComboBox.setSelectedIndex(i);
                    break;
                }
            }
        } else {
            responsableComboBox.setSelectedIndex(0); // "Sin Asignar"
        }
    }
    
    private void clearPlaceholderColorIfValue(JTextField field, String placeholder){
        if(!field.getText().equals(placeholder) && !field.getText().isEmpty()){
            field.setForeground(Color.BLACK);
        }
    }

    private Integer parseIntegerOrNull(JTextField field, String placeholder) {
        String text = field.getText().trim();
        if (text.isEmpty() || text.equals(placeholder)) {
            return null;
        }
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Valor numérico inválido para '" + placeholder + "'.", "Error de Formato", JOptionPane.ERROR_MESSAGE);
            field.requestFocus();
            return null; // O lanzar una excepción para detener el guardado
        }
    }


    private void guardarAnimal(ActionEvent e) {
        String nombre = nombreField.getText().trim();
        String especie = especieField.getText().trim();

        if (nombre.isEmpty() || especie.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nombre y Especie son obligatorios.", "Error de Validación", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Integer anios = parseIntegerOrNull(edadAniosField, "años");
        Integer meses = parseIntegerOrNull(edadMesesField, "meses");
        // Si parseIntegerOrNull devolvió null debido a un error de formato (y no porque estuviera vacío/placeholder)
        // deberíamos detenernos. Esto requiere que parseIntegerOrNull lance una excepción o un valor distintivo.
        // Por ahora, si es null, se guardará null.

        boolean isNew = (animalActual == null);
        if (isNew) {
            animalActual = new Animal();
        }

        animalActual.setNombre(nombre);
        animalActual.setEspecie(especie);
        animalActual.setRaza(razaField.getText().trim().isEmpty() ? null : razaField.getText().trim());
        animalActual.setEdadEstimadaAnios(anios);
        animalActual.setEdadEstimadaMeses(meses);
        
        if (generoComboBox.getSelectedIndex() > 0) animalActual.setGenero((String)generoComboBox.getSelectedItem()); else animalActual.setGenero(null);
        animalActual.setColor(colorField.getText().trim().isEmpty() ? null : colorField.getText().trim());
        if (tamanioComboBox.getSelectedIndex() > 0) animalActual.setTamanio((String)tamanioComboBox.getSelectedItem()); else animalActual.setTamanio(null);

        animalActual.setDescripcionCaracter(descripcionCaracterArea.getText().trim().isEmpty() ? null : descripcionCaracterArea.getText().trim());
        animalActual.setHistorialMedico(historialMedicoArea.getText().trim().isEmpty() ? null : historialMedicoArea.getText().trim());
        animalActual.setNecesidadesEspeciales(necesidadesEspecialesArea.getText().trim().isEmpty() ? null : necesidadesEspecialesArea.getText().trim());
        animalActual.setFotoUrl(fotoUrlField.getText().trim().isEmpty() ? null : fotoUrlField.getText().trim());
        animalActual.setEstadoAdopcion((Animal.EstadoAdopcion)estadoAdopcionComboBox.getSelectedItem());
        
        Usuario responsableSeleccionado = (Usuario) responsableComboBox.getSelectedItem();
        if (responsableSeleccionado != null) {
            animalActual.setIdUsuarioResponsable(responsableSeleccionado.getId());
        } else {
            animalActual.setIdUsuarioResponsable(null);
        }
        
        boolean success;
        if (isNew) {
            success = animalController.crearAnimal(animalActual);
        } else {
            success = animalController.actualizarAnimal(animalActual);
        }

        if (success) {
            guardadoExitoso = true;
            JOptionPane.showMessageDialog(this.getOwner(), // Mostrar sobre el diálogo padre (GestionAnimalesDialog)
                    "Animal guardado con éxito.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Error al guardar el animal. Revise la consola.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isGuardadoExitoso() {
        return guardadoExitoso;
    }
}