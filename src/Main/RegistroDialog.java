package Main;

import controllers.AuthController; // Usaremos el AuthController normal que usa el repo de texto plano
import model.Usuario;

import javax.swing.*;
import java.awt.*;

public class RegistroDialog extends JDialog {
    private AuthController authController;

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JTextField nombreCompletoField;
    private JTextField emailField;
    private JTextField telefonoField;
    private JComboBox<Usuario.RolUsuario> rolComboBox;

    private JLabel disponibilidadLabel, areasInteresLabel, direccionLabel, tipoViviendaLabel, experienciaLabel;
    private JTextField disponibilidadField, areasInteresField, direccionField, tipoViviendaField;
    private JCheckBox experienciaCheckBox;


    public RegistroDialog(Frame owner, AuthController authController) {
        super(owner, "Registro de Nuevo Usuario", true);
        this.authController = authController;
        System.out.println("REG_DIALOG: [TEXTO PLANO] Creando diálogo de registro.");
        initComponents();
        updateRoleSpecificFields();
        pack();
        setResizable(false); 
        setLocationRelativeTo(owner);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    }

    private void initComponents() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        int yPos = 0;

        addLabelAndField("Username*:", usernameField = new JTextField(25), yPos++, gbc);
        addLabelAndField("Password*:", passwordField = new JPasswordField(25), yPos++, gbc);
        addLabelAndField("Confirmar Password*:", confirmPasswordField = new JPasswordField(25), yPos++, gbc);
        addLabelAndField("Nombre Completo*:", nombreCompletoField = new JTextField(25), yPos++, gbc);
        addLabelAndField("Email*:", emailField = new JTextField(25), yPos++, gbc);
        addLabelAndField("Teléfono:", telefonoField = new JTextField(25), yPos++, gbc);
        
        gbc.gridx = 0; gbc.gridy = yPos; add(new JLabel("Registrarse como*:"), gbc);
        rolComboBox = new JComboBox<>(new Usuario.RolUsuario[]{
                Usuario.RolUsuario.ADOPTANTE_POTENCIAL,
                Usuario.RolUsuario.VOLUNTARIO,
                Usuario.RolUsuario.EMPLEADO
        });
        gbc.gridx = 1; gbc.gridy = yPos++; add(rolComboBox, gbc);

        disponibilidadLabel = new JLabel("Disponibilidad (Voluntario):");
        addLabelAndField(disponibilidadLabel, disponibilidadField = new JTextField(25), yPos++, gbc);
        
        areasInteresLabel = new JLabel("Áreas de Interés (Voluntario):");
        addLabelAndField(areasInteresLabel, areasInteresField = new JTextField(25), yPos++, gbc);

        direccionLabel = new JLabel("Dirección (Adoptante):");
        addLabelAndField(direccionLabel, direccionField = new JTextField(25), yPos++, gbc);

        tipoViviendaLabel = new JLabel("Tipo de Vivienda (Adoptante):");
        addLabelAndField(tipoViviendaLabel, tipoViviendaField = new JTextField(25), yPos++, gbc);
        
        experienciaLabel = new JLabel("Experiencia con Animales (Adoptante):");
        experienciaCheckBox = new JCheckBox();
        experienciaCheckBox.setHorizontalAlignment(SwingConstants.LEFT);
        gbc.gridx = 0; gbc.gridy = yPos; add(experienciaLabel, gbc);
        gbc.gridx = 1; gbc.gridy = yPos++; gbc.fill = GridBagConstraints.NONE; gbc.anchor = GridBagConstraints.WEST; add(experienciaCheckBox, gbc);
        
        gbc.fill = GridBagConstraints.HORIZONTAL; // Restaurar fill
        gbc.anchor = GridBagConstraints.CENTER; // Restaurar anchor

        rolComboBox.addActionListener(e -> {
            System.out.println("REG_DIALOG: [TEXTO PLANO] Rol cambiado a " + rolComboBox.getSelectedItem());
            updateRoleSpecificFields();
        });

        JButton registrarButton = new JButton("Registrar");
        JButton cancelarButton = new JButton("Cancelar");
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.add(registrarButton);
        buttonPanel.add(cancelarButton);

        gbc.gridx = 0; gbc.gridy = yPos; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(15, 5, 5, 5);
        add(buttonPanel, gbc);

        registrarButton.addActionListener(e -> handleRegistro());
        cancelarButton.addActionListener(e -> {
            System.out.println("REG_DIALOG: [TEXTO PLANO] Registro cancelado.");
            dispose();
        });
    }
    
    private void addLabelAndField(String labelText, JComponent field, int yPos, GridBagConstraints gbcParent) {
        GridBagConstraints gbcLabel = (GridBagConstraints) gbcParent.clone();
        gbcLabel.gridx = 0;
        gbcLabel.gridy = yPos;
        gbcLabel.anchor = GridBagConstraints.WEST;
        gbcLabel.fill = GridBagConstraints.NONE;
        add(new JLabel(labelText), gbcLabel);

        GridBagConstraints gbcField = (GridBagConstraints) gbcParent.clone();
        gbcField.gridx = 1;
        gbcField.gridy = yPos;
        gbcField.anchor = GridBagConstraints.EAST; // o WEST
        gbcField.fill = GridBagConstraints.HORIZONTAL;
        add(field, gbcField);
    }
     private void addLabelAndField(JLabel label, JComponent field, int yPos, GridBagConstraints gbcParent) {
        GridBagConstraints gbcLabel = (GridBagConstraints) gbcParent.clone();
        gbcLabel.gridx = 0;
        gbcLabel.gridy = yPos;
        gbcLabel.anchor = GridBagConstraints.WEST;
        gbcLabel.fill = GridBagConstraints.NONE;
        add(label, gbcLabel);

        GridBagConstraints gbcField = (GridBagConstraints) gbcParent.clone();
        gbcField.gridx = 1;
        gbcField.gridy = yPos;
        gbcField.anchor = GridBagConstraints.EAST; // o WEST
        gbcField.fill = GridBagConstraints.HORIZONTAL;
        add(field, gbcField);
    }


    private void updateRoleSpecificFields() {
        Usuario.RolUsuario selectedRole = (Usuario.RolUsuario) rolComboBox.getSelectedItem();
        System.out.println("REG_DIALOG: [TEXTO PLANO] Actualizando campos para rol: " + selectedRole);

        boolean isVoluntario = (selectedRole == Usuario.RolUsuario.VOLUNTARIO);
        disponibilidadLabel.setVisible(isVoluntario);
        disponibilidadField.setVisible(isVoluntario);
        areasInteresLabel.setVisible(isVoluntario);
        areasInteresField.setVisible(isVoluntario);

        boolean isAdoptante = (selectedRole == Usuario.RolUsuario.ADOPTANTE_POTENCIAL);
        direccionLabel.setVisible(isAdoptante);
        direccionField.setVisible(isAdoptante);
        tipoViviendaLabel.setVisible(isAdoptante);
        tipoViviendaField.setVisible(isAdoptante);
        experienciaLabel.setVisible(isAdoptante);
        experienciaCheckBox.setVisible(isAdoptante);
        
        pack();
    }

    private void handleRegistro() {
        System.out.println("REG_DIALOG: [TEXTO PLANO] Botón 'Registrar' presionado.");
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());
        String nombreCompleto = nombreCompletoField.getText().trim();
        String email = emailField.getText().trim();
        String telefono = telefonoField.getText().trim();
        Usuario.RolUsuario rol = (Usuario.RolUsuario) rolComboBox.getSelectedItem();

        if (username.isEmpty() || password.isEmpty() || nombreCompleto.isEmpty() || email.isEmpty() || rol == null) {
            JOptionPane.showMessageDialog(this, "Los campos marcados con '*' son obligatorios.", "Error de Registro", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "Las contraseñas no coinciden.", "Error de Registro", JOptionPane.ERROR_MESSAGE);
            passwordField.setText(""); confirmPasswordField.setText(""); passwordField.requestFocus();
            return;
        }
        // No hay validación de longitud de contraseña en modo texto plano, pero podrías añadirla
        // if (password.length() < 1) { ... } 

        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setUsername(username);
        nuevoUsuario.setNombreCompleto(nombreCompleto);
        nuevoUsuario.setEmail(email);
        nuevoUsuario.setTelefono(telefono.isEmpty() ? null : telefono);
        nuevoUsuario.setRol(rol);

        if (rol == Usuario.RolUsuario.VOLUNTARIO) {
            nuevoUsuario.setDisponibilidadHoraria(disponibilidadField.getText().trim().isEmpty() ? null : disponibilidadField.getText().trim());
            nuevoUsuario.setAreasInteres(areasInteresField.getText().trim().isEmpty() ? null : areasInteresField.getText().trim());
        } else if (rol == Usuario.RolUsuario.ADOPTANTE_POTENCIAL) {
            nuevoUsuario.setDireccion(direccionField.getText().trim().isEmpty() ? null : direccionField.getText().trim());
            nuevoUsuario.setTipoVivienda(tipoViviendaField.getText().trim().isEmpty() ? null : tipoViviendaField.getText().trim());
            nuevoUsuario.setExperienciaAnimales(experienciaCheckBox.isSelected());
        }

        System.out.println("REG_DIALOG: [TEXTO PLANO] Intentando registrar con AuthController. Usuario: " + nuevoUsuario.getUsername() + ", Rol: " + nuevoUsuario.getRol());
        if (authController.registrar(nuevoUsuario, password)) { // Se pasa la contraseña en crudo
            JOptionPane.showMessageDialog(this,
                    "Registro exitoso. Su cuenta ha sido creada y está pendiente de activación por un administrador.",
                    "Registro Completado", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Error en el registro. El username o email podría ya estar en uso, o hubo un problema con la base de datos.\nRevise la consola para más detalles.",
                    "Error de Registro", JOptionPane.ERROR_MESSAGE);
        }
    }
}