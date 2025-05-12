package Main;


import controllers.UsuarioController;
import model.Usuario; // Necesario para el enum RolUsuario y el objeto Usuario
import controllers.AuthController; // Para verificar si el admin intenta eliminarse a sí mismo

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.ArrayList;

public class GestionUsuariosDialog extends JDialog {
    private UsuarioController usuarioController;
    private JTable tablaUsuarios;
    private DefaultTableModel tableModel;

    // Filtros
    private JTextField filtroNombreField;
    private JComboBox<String> filtroRolComboBox;
    private JComboBox<String> filtroActivoComboBox;

    // Botones de acción para la tabla
    private JButton btnBloquear;
    private JButton btnDesbloquear;
    private JButton btnEliminarUsuario; // NUEVO

    // Botones generales
    private JButton btnCrearUsuario; // NUEVO
    private JButton btnFiltrar;
    private JButton btnLimpiarFiltros;
    private JButton btnRefrescar;
    private JButton btnCerrar;

    private final String[] ROLES_FILTRO = {"TODOS", "ADMINISTRADOR", "EMPLEADO", "VOLUNTARIO", "ADOPTANTE_POTENCIAL"};
    private final String[] ESTADO_FILTRO = {"TODOS", "ACTIVO", "INACTIVO"};


    public GestionUsuariosDialog(Frame owner, UsuarioController controller) {
        super(owner, "Gestión de Usuarios (Admin) [MODO TEXTO PLANO]", true);
        this.usuarioController = controller;

        initComponents();
        aplicarFiltros(null); // Carga inicial con filtros por defecto (todos)
        configurarTabla();

        setSize(850, 650); // Un poco más grande
        setLocationRelativeTo(owner);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        ((JPanel)getContentPane()).setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        // --- Panel de Filtros y Creación ---
        JPanel panelSuperior = new JPanel(new BorderLayout(10,5));
        
        JPanel panelFiltros = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panelFiltros.setBorder(BorderFactory.createTitledBorder("Filtros"));
        panelFiltros.add(new JLabel("Nombre:"));
        filtroNombreField = new JTextField(15);
        panelFiltros.add(filtroNombreField);
        panelFiltros.add(new JLabel("Rol:"));
        filtroRolComboBox = new JComboBox<>(ROLES_FILTRO);
        panelFiltros.add(filtroRolComboBox);
        panelFiltros.add(new JLabel("Estado:"));
        filtroActivoComboBox = new JComboBox<>(ESTADO_FILTRO);
        panelFiltros.add(filtroActivoComboBox);
        btnFiltrar = new JButton("Filtrar");
        btnFiltrar.addActionListener(this::aplicarFiltros);
        panelFiltros.add(btnFiltrar);
        btnLimpiarFiltros = new JButton("Limpiar");
        btnLimpiarFiltros.addActionListener(this::limpiarFiltros);
        panelFiltros.add(btnLimpiarFiltros);
        
        panelSuperior.add(panelFiltros, BorderLayout.CENTER);

        btnCrearUsuario = new JButton("Crear Nuevo Usuario"); // Botón de Dar de Alta
        btnCrearUsuario.addActionListener(this::crearNuevoUsuario);
        JPanel panelCrear = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelCrear.add(btnCrearUsuario);
        panelSuperior.add(panelCrear, BorderLayout.EAST);
        
        add(panelSuperior, BorderLayout.NORTH);

        // --- Tabla de Usuarios ---
        String[] columnNames = {"ID", "Username", "Nombre Completo", "Email", "Rol", "Activo"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                 if (columnIndex == 5) return Boolean.class; // Activo
                 return super.getColumnClass(columnIndex);
            }
        };
        tablaUsuarios = new JTable(tableModel);
        add(new JScrollPane(tablaUsuarios), BorderLayout.CENTER);

        // --- Panel de Acciones Inferior ---
        JPanel panelAcciones = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5)); // Acciones a la izquierda
        btnBloquear = new JButton("Bloquear");
        btnDesbloquear = new JButton("Desbloquear");
        btnEliminarUsuario = new JButton("Eliminar Usuario"); // NUEVO
        btnEliminarUsuario.setForeground(Color.RED);
        
        btnBloquear.addActionListener(this::bloquearUsuarioSeleccionado);
        btnDesbloquear.addActionListener(this::desbloquearUsuarioSeleccionado);
        btnEliminarUsuario.addActionListener(this::eliminarUsuarioSeleccionado);
        
        panelAcciones.add(btnBloquear);
        panelAcciones.add(btnDesbloquear);
        panelAcciones.add(btnEliminarUsuario);

        JPanel panelCerrar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5)); // Cerrar a la derecha
        btnRefrescar = new JButton("Refrescar Lista");
        btnCerrar = new JButton("Cerrar");
        btnRefrescar.addActionListener(e -> aplicarFiltros(null));
        btnCerrar.addActionListener(e -> dispose());
        panelCerrar.add(btnRefrescar);
        panelCerrar.add(btnCerrar);
        
        JPanel southPanelContainer = new JPanel(new BorderLayout());
        southPanelContainer.add(panelAcciones, BorderLayout.WEST);
        southPanelContainer.add(panelCerrar, BorderLayout.EAST);
        
        add(southPanelContainer, BorderLayout.SOUTH);
    }

    private void configurarTabla() {
        tablaUsuarios.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaUsuarios.getTableHeader().setReorderingAllowed(false);
        
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        tablaUsuarios.setRowSorter(sorter);

        tablaUsuarios.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                actualizarEstadoBotonesAccion();
            }
        });
        
        // Opcional: Doble clic para editar (más complejo, se omite por ahora)
        // tablaUsuarios.addMouseListener(new MouseAdapter() { ... });
        
        actualizarEstadoBotonesAccion(); // Estado inicial de los botones
    }
    
    private void actualizarEstadoBotonesAccion() {
        int selectedRow = tablaUsuarios.getSelectedRow();
        boolean haySeleccion = selectedRow != -1;
        
        btnBloquear.setEnabled(false);
        btnDesbloquear.setEnabled(false);
        btnEliminarUsuario.setEnabled(false);

        if (haySeleccion) {
            int modelRow = tablaUsuarios.convertRowIndexToModel(selectedRow);
            boolean activo = (Boolean) tableModel.getValueAt(modelRow, 5);
            String rol = (String) tableModel.getValueAt(modelRow, 4);
            int idUsuarioSeleccionado = (Integer) tableModel.getValueAt(modelRow, 0);
            
            boolean esAdminSeleccionado = "ADMINISTRADOR".equalsIgnoreCase(rol);
            boolean esElMismoAdminLogueado = (AuthController.isLoggedIn() && idUsuarioSeleccionado == AuthController.getUsuarioActual().getId());

            btnBloquear.setEnabled(!esAdminSeleccionado && !esElMismoAdminLogueado && activo);
            btnDesbloquear.setEnabled(!esAdminSeleccionado && !esElMismoAdminLogueado && !activo);
            btnEliminarUsuario.setEnabled(!esAdminSeleccionado && !esElMismoAdminLogueado);
        }
    }

    private void cargarUsuariosEnTabla(String nombreFiltro, String rolFiltro, Boolean activoFiltro) {
        System.out.println("GEST_USER_DIALOG: [TEXTO PLANO] Cargando usuarios. Filtros: N=" + nombreFiltro + ", R=" + rolFiltro + ", A=" + activoFiltro);
        List<Usuario> usuarios = usuarioController.filtrarUsuarios(nombreFiltro, rolFiltro, activoFiltro);
        
        tableModel.setRowCount(0); 
        if (usuarios != null) {
            for (Usuario u : usuarios) {
                tableModel.addRow(new Object[]{
                        u.getId(), u.getUsername(), u.getNombreCompleto(),
                        u.getEmail(), u.getRol().name(), u.isActivo()
                });
            }
        }
        actualizarEstadoBotonesAccion();
    }

    private void aplicarFiltros(ActionEvent e) {
        String nombre = filtroNombreField.getText().trim();
        String rolSeleccionado = (String) filtroRolComboBox.getSelectedItem();
        String estadoSeleccionado = (String) filtroActivoComboBox.getSelectedItem();

        String rolParaFiltrar = ("TODOS".equals(rolSeleccionado) || rolSeleccionado == null) ? null : rolSeleccionado;
        Boolean activoParaFiltrar = null;
        if ("ACTIVO".equals(estadoSeleccionado)) activoParaFiltrar = true;
        else if ("INACTIVO".equals(estadoSeleccionado)) activoParaFiltrar = false;
        
        cargarUsuariosEnTabla(nombre.isEmpty() ? null : nombre, rolParaFiltrar, activoParaFiltrar);
    }

    private void limpiarFiltros(ActionEvent e) {
        filtroNombreField.setText("");
        filtroRolComboBox.setSelectedItem("TODOS");
        filtroActivoComboBox.setSelectedItem("TODOS");
        aplicarFiltros(null);
    }

    private Usuario getUsuarioSeleccionadoDeTabla() {
        int selectedRow = tablaUsuarios.getSelectedRow();
        if (selectedRow != -1) {
            int modelRow = tablaUsuarios.convertRowIndexToModel(selectedRow);
            int usuarioId = (Integer) tableModel.getValueAt(modelRow, 0);
            // Para acciones como eliminar o verificar rol, podríamos necesitar el objeto completo
            // o al menos más datos que solo el ID. Por ahora, solo el ID.
            // Aquí, para simplificar la verificación de rol, lo leemos de la tabla.
            // Si necesitáramos el objeto completo, haríamos: return usuarioController.obtenerUsuarioPorId(usuarioId);
            Usuario u = new Usuario();
            u.setId(usuarioId);
            u.setRol(Usuario.RolUsuario.valueOf((String) tableModel.getValueAt(modelRow, 4)));
            u.setUsername((String) tableModel.getValueAt(modelRow,1)); // Para mensaje de confirmación
            return u;
        }
        return null;
    }

    private void bloquearUsuarioSeleccionado(ActionEvent e) {
        Usuario usuario = getUsuarioSeleccionadoDeTabla();
        if (usuario != null) {
            if (usuario.getRol() == Usuario.RolUsuario.ADMINISTRADOR || (AuthController.isLoggedIn() && usuario.getId() == AuthController.getUsuarioActual().getId())) {
                JOptionPane.showMessageDialog(this, "No se puede bloquear a un administrador o a sí mismo.", "Acción no permitida", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int confirm = JOptionPane.showConfirmDialog(this, 
                "¿Está seguro que desea BLOQUEAR al usuario '" + usuario.getUsername() + "' (ID: " + usuario.getId() + ")?",
                "Confirmar Bloqueo", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                if (usuarioController.bloquearUsuario(usuario.getId())) {
                    JOptionPane.showMessageDialog(this, "Usuario bloqueado con éxito.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    aplicarFiltros(null); // Recargar tabla
                } else {
                    JOptionPane.showMessageDialog(this, "Error al bloquear el usuario.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Seleccione un usuario de la lista.", "Error", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void desbloquearUsuarioSeleccionado(ActionEvent e) {
        Usuario usuario = getUsuarioSeleccionadoDeTabla();
        if (usuario != null) {
             if (usuario.getRol() == Usuario.RolUsuario.ADMINISTRADOR) {
                JOptionPane.showMessageDialog(this, "Los administradores no se gestionan con bloqueo/desbloqueo.", "Acción no permitida", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int confirm = JOptionPane.showConfirmDialog(this, 
                "¿Está seguro que desea DESBLOQUEAR al usuario '" + usuario.getUsername() + "' (ID: " + usuario.getId() + ")?",
                "Confirmar Desbloqueo", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                if (usuarioController.desbloquearUsuario(usuario.getId())) {
                    JOptionPane.showMessageDialog(this, "Usuario desbloqueado con éxito.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    aplicarFiltros(null); // Recargar tabla
                } else {
                    JOptionPane.showMessageDialog(this, "Error al desbloquear el usuario.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Seleccione un usuario de la lista.", "Error", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void eliminarUsuarioSeleccionado(ActionEvent e) {
        Usuario usuario = getUsuarioSeleccionadoDeTabla();
        if (usuario != null) {
            if (usuario.getRol() == Usuario.RolUsuario.ADMINISTRADOR || (AuthController.isLoggedIn() && usuario.getId() == AuthController.getUsuarioActual().getId())) {
                JOptionPane.showMessageDialog(this, "No se puede eliminar a un administrador o a sí mismo desde aquí.", "Acción no permitida", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int confirm = JOptionPane.showConfirmDialog(this, 
                "¿Está seguro que desea ELIMINAR PERMANENTEMENTE al usuario '" + usuario.getUsername() + "' (ID: " + usuario.getId() + ")?\nEsta acción no se puede deshacer.",
                "Confirmar Eliminación", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (confirm == JOptionPane.YES_OPTION) {
                if (usuarioController.adminEliminarUsuario(usuario.getId())) {
                    JOptionPane.showMessageDialog(this, "Usuario eliminado con éxito.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    aplicarFiltros(null); // Recargar tabla
                } else {
                    JOptionPane.showMessageDialog(this, "Error al eliminar el usuario. Verifique la consola.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Seleccione un usuario de la lista para eliminar.", "Error", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void crearNuevoUsuario(ActionEvent e) {
        System.out.println("GEST_USER_DIALOG: [TEXTO PLANO] Botón 'Crear Nuevo Usuario' presionado.");
        AdminCrearUsuarioDialog crearDialog = new AdminCrearUsuarioDialog( (Frame) SwingUtilities.getWindowAncestor(this) , usuarioController);
        crearDialog.setVisible(true);
        // Después de que el diálogo de creación se cierre, refrescar la lista de usuarios
        aplicarFiltros(null);
    }
}