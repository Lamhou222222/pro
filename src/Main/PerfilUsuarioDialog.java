package Main; // Asegúrate de que el nombre del paquete sea correcto

import controllers.AuthController; // Cambiado a controller singular
import controllers.UsuarioController; // Cambiado a controller singular
import model.Usuario;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
// import java.awt.event.ActionListener; // No es necesario si solo usas lambdas

public class PerfilUsuarioDialog extends JDialog {
    private UsuarioController usuarioController;
    private Usuario usuarioEditado; // Una copia del usuario para editar y luego guardar

    // Componentes UI
    private JTextField usernameField;
    private JTextField nombreCompletoField;
    private JTextField emailField;
    private JTextField telefonoField;
    private JComboBox<Usuario.RolUsuario> rolComboBox;
    private JCheckBox activoCheckBox;

    // Campos específicos de rol
    private JLabel disponibilidadLabel, areasInteresLabel, direccionLabel, tipoViviendaLabel, experienciaLabel;
    private JTextField disponibilidadField, areasInteresField, direccionField, tipoViviendaField;
    private JCheckBox experienciaPerfilCheckBox;

    private JButton btnGuardarCambios;
    private JButton btnAbrirDialogoCambiarPassword; // Cambiado el nombre
    private JButton btnDarseDeBaja;
    private JButton btnCerrar;

    private boolean esPerfilPropio;
    private boolean esAdminViendoOtro;
    private MainGui ownerFrame; // Guardar referencia al owner para llamar a sus métodos

    public PerfilUsuarioDialog(MainGui owner, UsuarioController controller, Usuario usuarioAMostrar) { // Cambiado Frame a MainGui
        super(owner, "Perfil de Usuario: " + usuarioAMostrar.getUsername(), true);
        this.ownerFrame = owner; // Guardar la referencia
        this.usuarioController = controller;
        this.usuarioEditado = clonarUsuario(usuarioAMostrar); 
        
        Usuario usuarioLogueado = AuthController.getUsuarioActual(); // Asumimos que AuthController está bien importado
        this.esPerfilPropio = (usuarioLogueado != null && usuarioLogueado.getId() == usuarioAMostrar.getId());
        // Un admin también puede estar viendo su propio perfil
        this.esAdminViendoOtro = AuthController.isAdmin() && (usuarioLogueado != null && usuarioLogueado.getId() != usuarioAMostrar.getId());


        initComponents();
        cargarDatosUsuario();
        configurarVisibilidadYEditabilidad(); // Renombrado para más claridad

        pack();
        // setMinimumSize(new Dimension(550, getHeight())); // pack() suele ser suficiente
        setResizable(false);
        setLocationRelativeTo(owner);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    }

    private Usuario clonarUsuario(Usuario original) {
        Usuario clon = new Usuario();
        // Copiar todos los campos
        clon.setId(original.getId());
        clon.setUsername(original.getUsername());
        clon.setPasswordHash(original.getPasswordHash()); // Contendrá la contraseña en texto plano
        clon.setNombreCompleto(original.getNombreCompleto());
        clon.setEmail(original.getEmail());
        clon.setTelefono(original.getTelefono());
        clon.setRol(original.getRol());
        clon.setActivo(original.isActivo());
        clon.setFechaRegistro(original.getFechaRegistro());
        clon.setDisponibilidadHoraria(original.getDisponibilidadHoraria());
        clon.setAreasInteres(original.getAreasInteres());
        clon.setDireccion(original.getDireccion());
        clon.setTipoVivienda(original.getTipoVivienda());
        clon.setExperienciaAnimales(original.getExperienciaAnimales());
        return clon;
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15)); // Más padding
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        int yPos = 0;

        // --- Sección Datos del Perfil ---
        gbc.gridx = 0; gbc.gridy = yPos++; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.CENTER;
        formPanel.add(createTituloLabel("Datos del Perfil"), gbc);
        gbc.gridwidth = 1; gbc.anchor = GridBagConstraints.WEST;


        addLabelAndField(formPanel, "Username:", usernameField = new JTextField(25), yPos++, gbc);
        usernameField.setEditable(false);

        addLabelAndField(formPanel, "Nombre Completo*:", nombreCompletoField = new JTextField(25), yPos++, gbc);
        addLabelAndField(formPanel, "Email*:", emailField = new JTextField(25), yPos++, gbc);
        addLabelAndField(formPanel, "Teléfono:", telefonoField = new JTextField(25), yPos++, gbc);

        gbc.gridx = 0; gbc.gridy = yPos; formPanel.add(new JLabel("Rol:"), gbc);
        rolComboBox = new JComboBox<>(Usuario.RolUsuario.values());
        gbc.gridx = 1; gbc.gridy = yPos++; formPanel.add(rolComboBox, gbc);

        gbc.gridx = 0; gbc.gridy = yPos; formPanel.add(new JLabel("Activo:"), gbc);
        activoCheckBox = new JCheckBox();
        gbc.gridx = 1; gbc.gridy = yPos++; gbc.fill = GridBagConstraints.NONE; gbc.anchor = GridBagConstraints.WEST; formPanel.add(activoCheckBox, gbc);
        gbc.fill = GridBagConstraints.HORIZONTAL; gbc.anchor = GridBagConstraints.CENTER; // Restaurar


        // Campos específicos de rol
        disponibilidadLabel = new JLabel("Disponibilidad (Voluntario):");
        addLabelAndField(formPanel, disponibilidadLabel, disponibilidadField = new JTextField(25), yPos++, gbc);
        areasInteresLabel = new JLabel("Áreas de Interés (Voluntario):");
        addLabelAndField(formPanel, areasInteresLabel, areasInteresField = new JTextField(25), yPos++, gbc);
        direccionLabel = new JLabel("Dirección (Adoptante):");
        addLabelAndField(formPanel, direccionLabel, direccionField = new JTextField(25), yPos++, gbc);
        tipoViviendaLabel = new JLabel("Tipo de Vivienda (Adoptante):");
        addLabelAndField(formPanel, tipoViviendaLabel, tipoViviendaField = new JTextField(25), yPos++, gbc);
        experienciaLabel = new JLabel("Experiencia con Animales (Adoptante):");
        experienciaPerfilCheckBox = new JCheckBox();
        gbc.gridx = 0; gbc.gridy = yPos; formPanel.add(experienciaLabel, gbc);
        gbc.gridx = 1; gbc.gridy = yPos++; gbc.fill = GridBagConstraints.NONE; gbc.anchor = GridBagConstraints.WEST; formPanel.add(experienciaPerfilCheckBox, gbc);
        gbc.fill = GridBagConstraints.HORIZONTAL; gbc.anchor = GridBagConstraints.CENTER;

        rolComboBox.addActionListener(e -> configurarVisibilidadCamposPorRol());

        // Botones de acción del perfil
        btnGuardarCambios = new JButton("Guardar Cambios de Perfil");
        btnGuardarCambios.addActionListener(this::guardarCambiosPerfil);
        gbc.gridx = 0; gbc.gridy = yPos++; gbc.gridwidth = 2;
        formPanel.add(btnGuardarCambios, gbc);
        gbc.gridwidth = 1;


        // --- Separador y Sección Cambiar Contraseña ---
        gbc.gridx = 0; gbc.gridy = yPos++; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(15, 0, 10, 0); // Más espacio vertical
        formPanel.add(new JSeparator(), gbc);
        gbc.insets = new Insets(5, 5, 5, 5); // Restaurar insets
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = yPos++; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.CENTER;
        formPanel.add(createTituloLabel("Cambiar Contraseña"), gbc);
        gbc.gridwidth = 1; gbc.anchor = GridBagConstraints.WEST;
        
        btnAbrirDialogoCambiarPassword = new JButton("Abrir Formulario para Cambiar Contraseña");
        gbc.gridx = 0; gbc.gridy = yPos++; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.CENTER;
        formPanel.add(btnAbrirDialogoCambiarPassword, gbc);
        gbc.gridwidth = 1;

        // --- Separador y Sección Darse de Baja ---
        if (esPerfilPropio && usuarioEditado.getRol() != Usuario.RolUsuario.ADMINISTRADOR) {
            gbc.gridx = 0; gbc.gridy = yPos++; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.insets = new Insets(15, 0, 10, 0);
            formPanel.add(new JSeparator(), gbc);
            gbc.insets = new Insets(5, 5, 5, 5);
            gbc.fill = GridBagConstraints.HORIZONTAL;

            gbc.gridx = 0; gbc.gridy = yPos++; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.CENTER;
            formPanel.add(createTituloLabel("Acciones de Cuenta"), gbc);
            gbc.gridwidth = 1; gbc.anchor = GridBagConstraints.WEST;

            btnDarseDeBaja = new JButton("Darme de Baja (Eliminar Cuenta)");
            btnDarseDeBaja.setForeground(Color.RED);
            btnDarseDeBaja.addActionListener(this::darseDeBaja);
            gbc.gridx = 0; gbc.gridy = yPos++; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.CENTER;
            formPanel.add(btnDarseDeBaja, gbc);
            gbc.gridwidth = 1;
        }


        add(new JScrollPane(formPanel), BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnCerrar = new JButton("Cerrar Ventana");
        btnCerrar.addActionListener(e -> dispose());
        bottomPanel.add(btnCerrar);
        add(bottomPanel, BorderLayout.SOUTH);

        // Listeners
        btnAbrirDialogoCambiarPassword.addActionListener(this::abrirDialogoCambiarPassword);
    }

    private JLabel createTituloLabel(String text) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 16));
        return label;
    }
    
    private void addLabelAndField(JPanel panel, String labelText, JComponent field, int yPos, GridBagConstraints gbcParent) {
        GridBagConstraints gbcLabel = (GridBagConstraints) gbcParent.clone();
        gbcLabel.gridx = 0; gbcLabel.gridy = yPos; gbcLabel.anchor = GridBagConstraints.EAST; gbcLabel.weightx = 0.3;
        panel.add(new JLabel(labelText), gbcLabel);
        GridBagConstraints gbcField = (GridBagConstraints) gbcParent.clone();
        gbcField.gridx = 1; gbcField.gridy = yPos; gbcField.anchor = GridBagConstraints.WEST; gbcField.weightx = 0.7;
        panel.add(field, gbcField);
    }
     private void addLabelAndField(JPanel panel, JLabel label, JComponent field, int yPos, GridBagConstraints gbcParent) {
        GridBagConstraints gbcLabel = (GridBagConstraints) gbcParent.clone();
        gbcLabel.gridx = 0; gbcLabel.gridy = yPos; gbcLabel.anchor = GridBagConstraints.EAST; gbcLabel.weightx = 0.3;
        panel.add(label, gbcLabel);
        GridBagConstraints gbcField = (GridBagConstraints) gbcParent.clone();
        gbcField.gridx = 1; gbcField.gridy = yPos; gbcField.anchor = GridBagConstraints.WEST; gbcField.weightx = 0.7;
        panel.add(field, gbcField);
    }

    private void cargarDatosUsuario() {
        usernameField.setText(usuarioEditado.getUsername());
        nombreCompletoField.setText(usuarioEditado.getNombreCompleto());
        emailField.setText(usuarioEditado.getEmail());
        telefonoField.setText(usuarioEditado.getTelefono() != null ? usuarioEditado.getTelefono() : "");
        rolComboBox.setSelectedItem(usuarioEditado.getRol());
        activoCheckBox.setSelected(usuarioEditado.isActivo());

        // Campos específicos (se mostrarán/ocultarán según rol)
        disponibilidadField.setText(usuarioEditado.getDisponibilidadHoraria() != null ? usuarioEditado.getDisponibilidadHoraria() : "");
        areasInteresField.setText(usuarioEditado.getAreasInteres() != null ? usuarioEditado.getAreasInteres() : "");
        direccionField.setText(usuarioEditado.getDireccion() != null ? usuarioEditado.getDireccion() : "");
        tipoViviendaField.setText(usuarioEditado.getTipoVivienda() != null ? usuarioEditado.getTipoVivienda() : "");
        experienciaPerfilCheckBox.setSelected(usuarioEditado.getExperienciaAnimales() != null && usuarioEditado.getExperienciaAnimales());
    }

    private void configurarVisibilidadYEditabilidad() {
        // Editabilidad general
        nombreCompletoField.setEditable(esPerfilPropio || esAdminViendoOtro);
        emailField.setEditable(esPerfilPropio || esAdminViendoOtro);
        telefonoField.setEditable(esPerfilPropio || esAdminViendoOtro);
        
        rolComboBox.setEnabled(esAdminViendoOtro); // Solo admin puede cambiar rol de OTRO
        activoCheckBox.setEnabled(esAdminViendoOtro && usuarioEditado.getRol() != Usuario.RolUsuario.ADMINISTRADOR);

        // Botones
        btnGuardarCambios.setVisible(esPerfilPropio || esAdminViendoOtro);
        btnAbrirDialogoCambiarPassword.setVisible(esPerfilPropio);
        if (btnDarseDeBaja != null) { // btnDarseDeBaja solo se crea si es perfil propio y no admin
             btnDarseDeBaja.setVisible(esPerfilPropio && usuarioEditado.getRol() != Usuario.RolUsuario.ADMINISTRADOR);
        }


        // Si NO es perfil propio Y NO es admin viendo otro (caso improbable si se abre correctamente)
        // todo debería ser no editable.
        if (!esPerfilPropio && !esAdminViendoOtro) {
            nombreCompletoField.setEditable(false);
            emailField.setEditable(false);
            telefonoField.setEditable(false);
            // rolComboBox ya está deshabilitado
            // activoCheckBox ya está deshabilitado
            disponibilidadField.setEditable(false);
            areasInteresField.setEditable(false);
            direccionField.setEditable(false);
            tipoViviendaField.setEditable(false);
            experienciaPerfilCheckBox.setEnabled(false);
        }
        configurarVisibilidadCamposPorRol();
    }
    
    private void configurarVisibilidadCamposPorRol(){
        Usuario.RolUsuario rolParaCampos = (Usuario.RolUsuario) rolComboBox.getSelectedItem();
        // Si el ComboBox aún no ha disparado un evento o está deshabilitado, usar el rol del usuarioEditado
        if (rolParaCampos == null) {
            rolParaCampos = usuarioEditado.getRol();
        }

        boolean esVoluntario = (rolParaCampos == Usuario.RolUsuario.VOLUNTARIO);
        disponibilidadLabel.setVisible(esVoluntario);
        disponibilidadField.setVisible(esVoluntario);
        disponibilidadField.setEditable(esVoluntario && (esPerfilPropio || esAdminViendoOtro));
        areasInteresLabel.setVisible(esVoluntario);
        areasInteresField.setVisible(esVoluntario);
        areasInteresField.setEditable(esVoluntario && (esPerfilPropio || esAdminViendoOtro));

        boolean esAdoptante = (rolParaCampos == Usuario.RolUsuario.ADOPTANTE_POTENCIAL);
        direccionLabel.setVisible(esAdoptante);
        direccionField.setVisible(esAdoptante);
        direccionField.setEditable(esAdoptante && (esPerfilPropio || esAdminViendoOtro));
        tipoViviendaLabel.setVisible(esAdoptante);
        tipoViviendaField.setVisible(esAdoptante);
        tipoViviendaField.setEditable(esAdoptante && (esPerfilPropio || esAdminViendoOtro));
        experienciaLabel.setVisible(esAdoptante);
        experienciaPerfilCheckBox.setVisible(esAdoptante);
        experienciaPerfilCheckBox.setEnabled(esAdoptante && (esPerfilPropio || esAdminViendoOtro));
        
        if (rolParaCampos == Usuario.RolUsuario.EMPLEADO || rolParaCampos == Usuario.RolUsuario.ADMINISTRADOR) {
            disponibilidadLabel.setVisible(false); disponibilidadField.setVisible(false);
            areasInteresLabel.setVisible(false); areasInteresField.setVisible(false);
            direccionLabel.setVisible(false); direccionField.setVisible(false);
            tipoViviendaLabel.setVisible(false); tipoViviendaField.setVisible(false);
            experienciaLabel.setVisible(false); experienciaPerfilCheckBox.setVisible(false);
        }
        pack(); // Reajustar tamaño del diálogo
    }

    private void guardarCambiosPerfil(ActionEvent e) {
        System.out.println("PERFIL_DIALOG: [TEXTO PLANO] Guardando cambios de perfil...");
        if (nombreCompletoField.getText().trim().isEmpty() || emailField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nombre completo y email son obligatorios.", "Error de Validación", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Actualizar el objeto 'usuarioEditado' con los datos de los campos
        usuarioEditado.setNombreCompleto(nombreCompletoField.getText().trim());
        usuarioEditado.setEmail(emailField.getText().trim());
        usuarioEditado.setTelefono(telefonoField.getText().trim().isEmpty() ? null : telefonoField.getText().trim());
        
        if (esAdminViendoOtro) {
            usuarioEditado.setRol((Usuario.RolUsuario) rolComboBox.getSelectedItem());
            usuarioEditado.setActivo(activoCheckBox.isSelected());
        }

        Usuario.RolUsuario rolActualizado = usuarioEditado.getRol(); // Usar el rol del objeto que se guardará
        if (rolActualizado == Usuario.RolUsuario.VOLUNTARIO) {
            usuarioEditado.setDisponibilidadHoraria(disponibilidadField.getText().trim().isEmpty() ? null : disponibilidadField.getText().trim());
            usuarioEditado.setAreasInteres(areasInteresField.getText().trim().isEmpty() ? null : areasInteresField.getText().trim());
        } else if (rolActualizado == Usuario.RolUsuario.ADOPTANTE_POTENCIAL) {
            usuarioEditado.setDireccion(direccionField.getText().trim().isEmpty() ? null : direccionField.getText().trim());
            usuarioEditado.setTipoVivienda(tipoViviendaField.getText().trim().isEmpty() ? null : tipoViviendaField.getText().trim());
            usuarioEditado.setExperienciaAnimales(experienciaPerfilCheckBox.isSelected());
        } else { // Limpiar campos de rol si el nuevo rol es ADMIN o EMPLEADO
            usuarioEditado.setDisponibilidadHoraria(null);
            usuarioEditado.setAreasInteres(null);
            usuarioEditado.setDireccion(null);
            usuarioEditado.setTipoVivienda(null);
            usuarioEditado.setExperienciaAnimales(null);
        }


        if (usuarioController.actualizarPerfil(usuarioEditado)) {
            JOptionPane.showMessageDialog(this, "Perfil actualizado con éxito.", "Perfil Actualizado", JOptionPane.INFORMATION_MESSAGE);
            if (esPerfilPropio) {
                // Actualizar el objeto en AuthController también
                AuthController.getUsuarioActual().setNombreCompleto(usuarioEditado.getNombreCompleto());
                AuthController.getUsuarioActual().setEmail(usuarioEditado.getEmail());
                AuthController.getUsuarioActual().setTelefono(usuarioEditado.getTelefono());
                // Actualizar campos específicos si el rol no cambió
                if (AuthController.getUsuarioActual().getRol() == rolActualizado) {
                    AuthController.getUsuarioActual().setDisponibilidadHoraria(usuarioEditado.getDisponibilidadHoraria());
                    AuthController.getUsuarioActual().setAreasInteres(usuarioEditado.getAreasInteres());
                    AuthController.getUsuarioActual().setDireccion(usuarioEditado.getDireccion());
                    AuthController.getUsuarioActual().setTipoVivienda(usuarioEditado.getTipoVivienda());
                    AuthController.getUsuarioActual().setExperienciaAnimales(usuarioEditado.getExperienciaAnimales());
                }
                ownerFrame.actualizarWelcomeLabel();
            }
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Error al actualizar el perfil. Revise la consola.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void abrirDialogoCambiarPassword(ActionEvent e) {
        if (!esPerfilPropio) { // Solo el usuario propio puede cambiar su contraseña
            return;
        }
        System.out.println("PERFIL_DIALOG: [TEXTO PLANO] Abriendo diálogo para cambiar contraseña.");

        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
        JPasswordField passActualField = new JPasswordField(20);
        JPasswordField nuevaPassField = new JPasswordField(20);
        JPasswordField confirmarPassField = new JPasswordField(20);

        panel.add(new JLabel("Contraseña Actual*:"));
        panel.add(passActualField);
        panel.add(new JLabel("Nueva Contraseña*:"));
        panel.add(nuevaPassField);
        panel.add(new JLabel("Confirmar Nueva Contraseña*:"));
        panel.add(confirmarPassField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Cambiar Contraseña",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String actual = new String(passActualField.getPassword());
            String nueva = new String(nuevaPassField.getPassword());
            String confirmar = new String(confirmarPassField.getPassword());

            if (actual.isEmpty() || nueva.isEmpty() || confirmar.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Todos los campos de contraseña son obligatorios.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (!nueva.equals(confirmar)) {
                JOptionPane.showMessageDialog(this, "Las nuevas contraseñas no coinciden.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            // Podrías añadir validación de longitud de nueva contraseña aquí
            // if (nueva.length() < 1 && !esAdminViendoOtro) { ... } // En modo texto plano, cualquier longitud es "válida"

            // Llamar al método del controller que toma (idUsuario, passActual, nuevaPass)
            // En UsuarioController, el método cambiarPassword es para el usuario logueado,
            // así que toma (passActual, nuevaPass)
            if (usuarioController.cambiarPassword(actual, nueva)) {
                JOptionPane.showMessageDialog(this, "Contraseña cambiada con éxito.", "Contraseña Actualizada", JOptionPane.INFORMATION_MESSAGE);
                // Actualizar el password en el objeto 'usuarioEditado' y en AuthController.usuarioActual
                // (recordar que es texto plano aquí)
                this.usuarioEditado.setPasswordHash(nueva);
                if (AuthController.isLoggedIn() && AuthController.getUsuarioActual().getId() == this.usuarioEditado.getId()) {
                    AuthController.getUsuarioActual().setPasswordHash(nueva);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Error al cambiar la contraseña.\nVerifique su contraseña actual o revise la consola.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void darseDeBaja(ActionEvent e) {
        System.out.println("PERFIL_DIALOG: [TEXTO PLANO] Solicitando darse de baja...");
        if (!esPerfilPropio || AuthController.isAdmin() && usuarioEditado.getId() == AuthController.getUsuarioActual().getId() ) {
             // Un admin no puede eliminarse a sí mismo desde su propio perfil de esta forma
            if (AuthController.isAdmin()) {
                 JOptionPane.showMessageDialog(this, "Un administrador no puede eliminar su propia cuenta desde aquí.", "Acción no permitida", JOptionPane.WARNING_MESSAGE);
            }
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Está seguro que desea ELIMINAR su cuenta?\nEsta acción es irreversible y será desconectado.",
                "Confirmar Baja de Cuenta", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            if (usuarioController.darseDeBaja()) { // El controller usa AuthController.getUsuarioActual()
                JOptionPane.showMessageDialog(ownerFrame, // Mostrar sobre MainGui
                        "Su cuenta ha sido eliminada. Será desconectado.", 
                        "Cuenta Eliminada", JOptionPane.INFORMATION_MESSAGE);
                dispose();
                ownerFrame.forzarLogoutYMostrarLogin();
            } else {
                JOptionPane.showMessageDialog(this, "Error al eliminar la cuenta. Revise la consola.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}