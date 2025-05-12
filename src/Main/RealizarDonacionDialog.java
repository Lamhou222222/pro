package Main;

import controllers.AuthController;
import controllers.DonacionController;
import model.Donacion;
import model.Usuario;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.math.BigDecimal;
import java.util.Objects; // Para Objects.equals

public class RealizarDonacionDialog extends JDialog {
    private DonacionController donacionController;
    private boolean donacionRealizada = false;

    // Componentes UI
    private JCheckBox anonimoCheckBox;
    private JTextField nombreDonanteField; // Para donante anónimo o si no está logueado
    private JComboBox<String> tipoDonacionComboBox;
    private JTextField montoField; // Para donaciones monetarias
    private JTextArea descripcionItemsArea; // Para donaciones de items
    private JLabel montoLabel;
    private JLabel descripcionItemsLabel;
    private JScrollPane descripcionItemsScrollPane;

    private final String[] TIPOS_DONACION = {"Monetaria", "Alimento", "Mantas", "Juguetes", "Medicamentos", "Otro"};

    public RealizarDonacionDialog(Frame owner, DonacionController donCtrl) {
        super(owner, "Realizar Donación [TEXTO PLANO CONTEXTO]", true);
        this.donacionController = donCtrl;

        initComponents();
        actualizarCamposSegunTipo(); // Configurar visibilidad inicial

        pack();
        setMinimumSize(new Dimension(450, getHeight()));
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

        // Nombre del Donante / Anonimato
        anonimoCheckBox = new JCheckBox("Realizar donación de forma anónima");
        gbc.gridx = 0; gbc.gridy = yPos; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.CENTER;
        formPanel.add(anonimoCheckBox, gbc);
        gbc.gridwidth = 1; gbc.anchor = GridBagConstraints.WEST; yPos++;
        
        addLabelAndField(formPanel, "Nombre del Donante:", nombreDonanteField = new JTextField(30), yPos++, gbc);
        
        // Si el usuario está logueado, prellenar y deshabilitar si no es anónimo
        if (AuthController.isLoggedIn() && AuthController.getUsuarioActual() != null) {
            nombreDonanteField.setText(AuthController.getUsuarioActual().getNombreCompleto());
            nombreDonanteField.setEditable(false); // No puede cambiar su nombre si está logueado
            anonimoCheckBox.addActionListener(e -> {
                nombreDonanteField.setEditable(anonimoCheckBox.isSelected());
                if (!anonimoCheckBox.isSelected()) { // Si desmarca anónimo, restaurar su nombre
                    nombreDonanteField.setText(AuthController.getUsuarioActual().getNombreCompleto());
                } else {
                    nombreDonanteField.setText(""); // Limpiar para nombre anónimo
                }
            });
        } else { // No logueado, el nombre es para donante anónimo si no marca la casilla
            anonimoCheckBox.setSelected(true); // Por defecto anónimo si no hay login
            nombreDonanteField.setEditable(true);
            anonimoCheckBox.addActionListener(e -> nombreDonanteField.setEditable(anonimoCheckBox.isSelected()));
        }


        // Tipo de Donación
        gbc.gridx = 0; gbc.gridy = yPos; gbc.anchor = GridBagConstraints.EAST; formPanel.add(new JLabel("Tipo de Donación*:"), gbc);
        tipoDonacionComboBox = new JComboBox<>(TIPOS_DONACION);
        gbc.gridx = 1; gbc.gridy = yPos++; gbc.anchor = GridBagConstraints.WEST; formPanel.add(tipoDonacionComboBox, gbc);
        tipoDonacionComboBox.addActionListener(e -> actualizarCamposSegunTipo());

        // Monto (para donaciones monetarias)
        montoLabel = new JLabel("Monto (EUR)*:");
        addLabelAndField(formPanel, montoLabel, montoField = new JTextField(10), yPos++, gbc);

        // Descripción Items (para donaciones no monetarias)
        descripcionItemsLabel = new JLabel("Descripción de Items*:");
        gbc.gridx = 0; gbc.gridy = yPos; gbc.anchor = GridBagConstraints.NORTHEAST; formPanel.add(descripcionItemsLabel, gbc);
        descripcionItemsArea = new JTextArea(4, 30);
        descripcionItemsArea.setLineWrap(true);
        descripcionItemsArea.setWrapStyleWord(true);
        descripcionItemsScrollPane = new JScrollPane(descripcionItemsArea);
        gbc.gridx = 1; gbc.gridy = yPos++; gbc.weighty = 0.4; gbc.fill = GridBagConstraints.BOTH; gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(descripcionItemsScrollPane, gbc);
        gbc.weighty = 0; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.anchor = GridBagConstraints.CENTER;


        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnDonar = new JButton("Realizar Donación");
        JButton btnCancelar = new JButton("Cancelar");

        btnDonar.addActionListener(this::realizarDonacion);
        btnCancelar.addActionListener(e -> dispose());

        buttonPanel.add(btnCancelar);
        buttonPanel.add(btnDonar);

        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
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

    private void actualizarCamposSegunTipo() {
        String tipoSeleccionado = (String) tipoDonacionComboBox.getSelectedItem();
        boolean esMonetaria = "Monetaria".equals(tipoSeleccionado);

        montoLabel.setVisible(esMonetaria);
        montoField.setVisible(esMonetaria);
        descripcionItemsLabel.setVisible(!esMonetaria);
        descripcionItemsScrollPane.setVisible(!esMonetaria);
        descripcionItemsArea.setVisible(!esMonetaria); // Asegurar que el área de texto también se oculte/muestre
        pack(); // Reajustar tamaño del diálogo
    }

    private void realizarDonacion(ActionEvent e) {
        Donacion nuevaDonacion = new Donacion();
        
        Usuario currentUser = AuthController.getUsuarioActual();
        if (!anonimoCheckBox.isSelected() && currentUser != null) {
            nuevaDonacion.setIdUsuarioDonante(currentUser.getId());
            // El nombre del donante se tomará del objeto Usuario en el controller/repo si es necesario
        } else {
            String nombreAnon = nombreDonanteField.getText().trim();
            if (nombreAnon.isEmpty() && anonimoCheckBox.isSelected()) { // Si es anónimo y no puso nombre, usar genérico
                nombreAnon = "Donante Anónimo";
            } else if (nombreAnon.isEmpty() && !anonimoCheckBox.isSelected()){ // No logueado, no anónimo, sin nombre -> error
                 JOptionPane.showMessageDialog(this, "Por favor, ingrese un nombre para el donante o marque como anónimo.", "Error de Validación", JOptionPane.ERROR_MESSAGE);
                return;
            }
            nuevaDonacion.setNombreDonanteAnonimo(nombreAnon.isEmpty() ? null : nombreAnon);
        }

        String tipoDonacion = (String) tipoDonacionComboBox.getSelectedItem();
        if (tipoDonacion == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un tipo de donación.", "Error de Validación", JOptionPane.ERROR_MESSAGE);
            return;
        }
        nuevaDonacion.setTipoDonacion(tipoDonacion);

        if ("Monetaria".equals(tipoDonacion)) {
            String montoStr = montoField.getText().trim();
            if (montoStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "El monto es obligatorio para donaciones monetarias.", "Error de Validación", JOptionPane.ERROR_MESSAGE);
                return;
            }
            try {
                BigDecimal monto = new BigDecimal(montoStr);
                if (monto.compareTo(BigDecimal.ZERO) <= 0) {
                    JOptionPane.showMessageDialog(this, "El monto debe ser un valor positivo.", "Error de Validación", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                nuevaDonacion.setMonto(monto);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Formato de monto inválido. Use '.' como separador decimal.", "Error de Formato", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } else { // Donación de Items
            String descripcion = descripcionItemsArea.getText().trim();
            if (descripcion.isEmpty()) {
                JOptionPane.showMessageDialog(this, "La descripción de los items es obligatoria para este tipo de donación.", "Error de Validación", JOptionPane.ERROR_MESSAGE);
                return;
            }
            nuevaDonacion.setDescripcionItems(descripcion);
        }

        System.out.println("REALIZAR_DON_DIALOG: [TEXTO PLANO CONTEXTO] Intentando registrar donación.");
        if (donacionController.registrarDonacion(nuevaDonacion)) {
            donacionRealizada = true;
            JOptionPane.showMessageDialog(this.getOwner(), // Mostrar sobre MainGui
                    "¡Gracias por su donación!", "Donación Realizada", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Error al registrar la donación. Revise la consola.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isDonacionRealizada() {
        return donacionRealizada;
    }
}