package Main;

import controllers.UsuarioController;
import model.Usuario;

import javax.swing.*;
import java.awt.*;

public class AdminCrearUsuarioDialog extends JDialog {
    private UsuarioController usuarioController;
    private Frame ownerFrame; // Para centrar mensajes

    private JTextField usernameField;
    private JPasswordField passwordField; // Contraseña en texto plano
    private JTextField nombreCompletoField;
    private JTextField emailField;
    private JTextField telefonoField;
    private JComboBox<Usuario.RolUsuario> rolComboBox;
    private JCheckBox activoCheckBox;

    // Campos específicos de rol (opcionales al crear por admin, pero podrían añadirse)
    // private JTextField disponibilidadField, areasInteresField, direccionField, tipoViviendaField;
    // private JCheckBox experienciaCheckBox;

    public AdminCrearUsuarioDialog(Frame owner, UsuarioController controller) {
        super(owner, "Admin: Crear Nuevo Usuario", true);
        this.ownerFrame = owner;
        this.usuarioController = controller;
        initComponents();
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
        addLabelAndField("Nombre Completo*:", nombreCompletoField = new JTextField(25), yPos++, gbc);
        addLabelAndField("Email*:", emailField = new JTextField(25), yPos++, gbc);
        addLabelAndField("Teléfono:", telefonoField = new JTextField(25), yPos++, gbc);

        gbc.gridx = 0; gbc.gridy = yPos; add(new JLabel("Rol*:"), gbc);
        rolComboBox = new JComboBox<>(Usuario.RolUsuario.values()); // Admin puede asignar cualquier rol
        gbc.gridx = 1; gbc.gridy = yPos++; add(rolComboBox, gbc);

        gbc.gridx = 0; gbc.gridy = yPos; add(new JLabel("Activar Usuario:"), gbc);
        activoCheckBox = new JCheckBox("Sí, activar inmediatamente");
        gbc.gridx = 1; gbc.gridy = yPos++; gbc.fill = GridBagConstraints.NONE; add(activoCheckBox, gbc);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Aquí podrías añadir campos específicos de rol si el admin debe llenarlos al crear
        // y lógica similar a RegistroDialog para mostrarlos/ocultarlos según el rolComboBox.
        // Por simplicidad, se omiten por ahora.

        JButton btnCrear = new JButton("Crear Usuario");
        JButton btnCancelar = new JButton("Cancelar");
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.add(btnCrear);
        buttonPanel.add(btnCancelar);

        gbc.gridx = 0; gbc.gridy = yPos; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(15, 5, 5, 5);
        add(buttonPanel, gbc);

        btnCrear.addActionListener(e -> handleCrearUsuario());
        btnCancelar.addActionListener(e -> dispose());
    }

    private void addLabelAndField(String labelText, JComponent field, int yPos, GridBagConstraints gbcParent) {
        GridBagConstraints gbcLabel = (GridBagConstraints) gbcParent.clone();
        gbcLabel.gridx = 0; gbcLabel.gridy = yPos; gbcLabel.anchor = GridBagConstraints.EAST;
        add(new JLabel(labelText), gbcLabel);

        GridBagConstraints gbcField = (GridBagConstraints) gbcParent.clone();
        gbcField.gridx = 1; gbcField.gridy = yPos; gbcField.anchor = GridBagConstraints.WEST;
        add(field, gbcField);
    }

    private void handleCrearUsuario() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String nombreCompleto = nombreCompletoField.getText().trim();
        String email = emailField.getText().trim();
        String telefono = telefonoField.getText().trim();
        Usuario.RolUsuario rol = (Usuario.RolUsuario) rolComboBox.getSelectedItem();
        boolean activar = activoCheckBox.isSelected();

        if (username.isEmpty() || password.isEmpty() || nombreCompleto.isEmpty() || email.isEmpty() || rol == null) {
            JOptionPane.showMessageDialog(this, "Los campos marcados con '*' son obligatorios.", "Error de Creación", JOptionPane.ERROR_MESSAGE);
            return;
        }
        // Se podría añadir validación de longitud de contraseña aquí si se desea

        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setUsername(username);
        // Password se pasa al controller, que lo pondrá en el campo 'passwordHash' del objeto Usuario
        nuevoUsuario.setNombreCompleto(nombreCompleto);
        nuevoUsuario.setEmail(email);
        nuevoUsuario.setTelefono(telefono.isEmpty() ? null : telefono);
        nuevoUsuario.setRol(rol);
        // Los campos específicos de rol (disponibilidad, dirección, etc.) no se piden en este formulario simple.
        // Se podrían añadir y recoger aquí si fuera necesario.

        System.out.println("ADMIN_CREAR_DIALOG: [TEXTO PLANO] Intentando crear usuario con controller.");
        if (usuarioController.adminRegistrarUsuario(nuevoUsuario, password, activar)) {
            JOptionPane.showMessageDialog(ownerFrame, // Mostrar sobre MainGui
                    "Usuario '" + username + "' creado con éxito." + (activar ? " y activado." : " Pendiente de activación si no se marcó."),
                    "Usuario Creado", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Error al crear el usuario. El username o email podría ya estar en uso, o hubo un problema.\nRevise la consola para más detalles.",
                    "Error de Creación", JOptionPane.ERROR_MESSAGE);
        }
    }
}