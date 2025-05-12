package Main;

import controllers.AnimalController; // Para obtener nombres de animales
import controllers.SolicitudAdopcionController;
import controllers.UsuarioController; // Para obtener nombres de solicitantes
import model.Animal;
import model.SolicitudAdopcion;
import model.Usuario;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.HashMap; // Para cachear nombres
import java.util.Map;

public class GestionSolicitudesDialog extends JDialog {
    private SolicitudAdopcionController solicitudController;
    private UsuarioController usuarioController; // Para nombres de solicitantes
    private AnimalController animalController;   // Para nombres de animales
    private JTable tablaSolicitudes;
    private DefaultTableModel tableModel;

    // Filtros
    private JTextField filtroIdAnimalField;
    private JTextField filtroIdUsuarioField;
    private JComboBox<String> filtroEstadoComboBox;

    // Botones
    private JButton btnFiltrar;
    private JButton btnLimpiarFiltros;
    private JButton btnVerMotivacion;
    private JButton btnActualizarEstado; // Podría abrir otro diálogo o tener submenú
    private JButton btnRefrescar;
    private JButton btnCerrar;

    // Cache para nombres
    private Map<Integer, String> cacheNombresSolicitante = new HashMap<>();
    private Map<Integer, String> cacheNombresAnimal = new HashMap<>();
    private final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    private final String[] ESTADOS_FILTRO = {"TODOS", "ENVIADA", "EN_REVISION", "APROBADA", "RECHAZADA", "CANCELADA_POR_USUARIO", "ENTREVISTA_PROGRAMADA"};


    public GestionSolicitudesDialog(Frame owner, SolicitudAdopcionController solCtrl, UsuarioController usrCtrl, AnimalController anCtrl) {
        super(owner, "Gestión de Solicitudes de Adopción (Staff) [TEXTO PLANO CONTEXTO]", true);
        this.solicitudController = solCtrl;
        this.usuarioController = usrCtrl;
        this.animalController = anCtrl;

        initComponents();
        aplicarFiltros(null); // Carga inicial
        configurarTabla();

        setSize(950, 600);
        setLocationRelativeTo(owner);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        ((JPanel)getContentPane()).setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        // --- Panel de Filtros ---
        JPanel panelFiltros = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panelFiltros.setBorder(BorderFactory.createTitledBorder("Filtrar Solicitudes"));
        panelFiltros.add(new JLabel("ID Animal:"));
        filtroIdAnimalField = new JTextField(5);
        panelFiltros.add(filtroIdAnimalField);
        panelFiltros.add(new JLabel("ID Solicitante:"));
        filtroIdUsuarioField = new JTextField(5);
        panelFiltros.add(filtroIdUsuarioField);
        panelFiltros.add(new JLabel("Estado:"));
        filtroEstadoComboBox = new JComboBox<>(ESTADOS_FILTRO);
        panelFiltros.add(filtroEstadoComboBox);
        btnFiltrar = new JButton("Filtrar");
        btnFiltrar.addActionListener(this::aplicarFiltros);
        panelFiltros.add(btnFiltrar);
        btnLimpiarFiltros = new JButton("Limpiar");
        btnLimpiarFiltros.addActionListener(this::limpiarFiltros);
        panelFiltros.add(btnLimpiarFiltros);
        add(panelFiltros, BorderLayout.NORTH);

        // --- Tabla de Solicitudes ---
        String[] columnNames = {"ID Sol.", "Fecha Sol.", "ID Animal", "Animal", "ID Solicitante", "Solicitante", "Estado", "Notas Admin"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        tablaSolicitudes = new JTable(tableModel);
        add(new JScrollPane(tablaSolicitudes), BorderLayout.CENTER);

        // --- Panel de Acciones Inferior ---
        JPanel panelAcciones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnVerMotivacion = new JButton("Ver Motivación");
        btnActualizarEstado = new JButton("Actualizar Estado");
        btnRefrescar = new JButton("Refrescar Lista");
        
        btnVerMotivacion.addActionListener(this::verMotivacionSolicitud);
        btnActualizarEstado.addActionListener(this::actualizarEstadoSolicitud);
        btnRefrescar.addActionListener(e -> aplicarFiltros(null));
        
        panelAcciones.add(btnVerMotivacion);
        panelAcciones.add(btnActualizarEstado);
        panelAcciones.add(btnRefrescar);

        JPanel panelCerrar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnCerrar = new JButton("Cerrar");
        btnCerrar.addActionListener(e -> dispose());
        panelCerrar.add(btnCerrar);

        JPanel southPanelContainer = new JPanel(new BorderLayout());
        southPanelContainer.add(panelAcciones, BorderLayout.WEST);
        southPanelContainer.add(panelCerrar, BorderLayout.EAST);
        add(southPanelContainer, BorderLayout.SOUTH);
    }

    private void configurarTabla() {
        tablaSolicitudes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaSolicitudes.getTableHeader().setReorderingAllowed(false);
        
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        tablaSolicitudes.setRowSorter(sorter);
        
        // Ajustar anchos de columnas
        tablaSolicitudes.getColumnModel().getColumn(0).setPreferredWidth(50); // ID Solicitud
        tablaSolicitudes.getColumnModel().getColumn(1).setPreferredWidth(120); // Fecha
        tablaSolicitudes.getColumnModel().getColumn(2).setPreferredWidth(60);  // ID Animal
        tablaSolicitudes.getColumnModel().getColumn(3).setPreferredWidth(150); // Animal
        tablaSolicitudes.getColumnModel().getColumn(4).setPreferredWidth(80);  // ID Solicitante
        tablaSolicitudes.getColumnModel().getColumn(5).setPreferredWidth(150); // Solicitante
        tablaSolicitudes.getColumnModel().getColumn(6).setPreferredWidth(120); // Estado
        tablaSolicitudes.getColumnModel().getColumn(7).setPreferredWidth(200); // Notas Admin


        tablaSolicitudes.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                actualizarEstadoBotonesAccion();
            }
        });
        actualizarEstadoBotonesAccion();
    }

    private void actualizarEstadoBotonesAccion() {
        boolean haySeleccion = tablaSolicitudes.getSelectedRow() != -1;
        btnVerMotivacion.setEnabled(haySeleccion);
        btnActualizarEstado.setEnabled(haySeleccion);
        // Permisos ya controlados por quién puede abrir este diálogo (Empleado/Admin)
    }

    private void cargarSolicitudesEnTabla(Integer idAnimalF, Integer idUsuarioF, String estadoF) {
        System.out.println("GEST_SOL_DIALOG: [TEXTO PLANO CONTEXTO] Cargando solicitudes. Filtros: Animal=" + idAnimalF + ", Usuario=" + idUsuarioF + ", Estado=" + estadoF);
        List<SolicitudAdopcion> solicitudes = solicitudController.filtrarSolicitudes(idAnimalF, idUsuarioF, estadoF);
        
        tableModel.setRowCount(0); 
        if (solicitudes != null) {
            for (SolicitudAdopcion sol : solicitudes) {
                String nombreSolicitante = cacheNombresSolicitante.computeIfAbsent(sol.getIdUsuarioSolicitante(), id -> {
                    Usuario u = usuarioController.obtenerUsuarioPorId(id);
                    return u != null ? u.getNombreCompleto() : "ID: " + id;
                });
                String nombreAnimal = cacheNombresAnimal.computeIfAbsent(sol.getIdAnimal(), id -> {
                    Animal a = animalController.obtenerDetallesAnimal(id);
                    return a != null ? a.getNombre() : "ID: " + id;
                });

                tableModel.addRow(new Object[]{
                        sol.getId(),
                        sol.getFechaSolicitud() != null ? sdf.format(sol.getFechaSolicitud()) : "N/A",
                        sol.getIdAnimal(),
                        nombreAnimal,
                        sol.getIdUsuarioSolicitante(),
                        nombreSolicitante,
                        sol.getEstado().name(),
                        sol.getNotasAdmin() != null ? sol.getNotasAdmin() : ""
                });
            }
        }
        actualizarEstadoBotonesAccion();
    }

    private void aplicarFiltros(ActionEvent e) {
        Integer idAnimal = null;
        Integer idUsuario = null;
        try {
            if (!filtroIdAnimalField.getText().trim().isEmpty()) idAnimal = Integer.parseInt(filtroIdAnimalField.getText().trim());
            if (!filtroIdUsuarioField.getText().trim().isEmpty()) idUsuario = Integer.parseInt(filtroIdUsuarioField.getText().trim());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "ID de Animal o Solicitante debe ser numérico.", "Error de Filtro", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String estadoSeleccionado = (String) filtroEstadoComboBox.getSelectedItem();
        String estadoParaFiltrar = ("TODOS".equals(estadoSeleccionado) || estadoSeleccionado == null) ? null : estadoSeleccionado;
        
        cargarSolicitudesEnTabla(idAnimal, idUsuario, estadoParaFiltrar);
    }

    private void limpiarFiltros(ActionEvent e) {
        filtroIdAnimalField.setText("");
        filtroIdUsuarioField.setText("");
        filtroEstadoComboBox.setSelectedItem("TODOS");
        aplicarFiltros(null);
    }

    private SolicitudAdopcion getSolicitudSeleccionada() {
        int selectedRow = tablaSolicitudes.getSelectedRow();
        if (selectedRow != -1) {
            int modelRow = tablaSolicitudes.convertRowIndexToModel(selectedRow);
            int solicitudId = (Integer) tableModel.getValueAt(modelRow, 0); // Columna ID Sol.
            return solicitudController.obtenerSolicitudPorId(solicitudId); // Empleado/Admin tiene permiso
        }
        return null;
    }

    private void verMotivacionSolicitud(ActionEvent e) {
        SolicitudAdopcion solicitud = getSolicitudSeleccionada();
        if (solicitud != null) {
            JTextArea textArea = new JTextArea(10, 40);
            textArea.setText("Motivación para la solicitud ID " + solicitud.getId() + 
                             " (Animal: " + cacheNombresAnimal.getOrDefault(solicitud.getIdAnimal(), "ID:"+solicitud.getIdAnimal()) + 
                             ", Solicitante: " + cacheNombresSolicitante.getOrDefault(solicitud.getIdUsuarioSolicitante(), "ID:"+solicitud.getIdUsuarioSolicitante()) + "):\n\n" +
                             (solicitud.getMotivacion() != null ? solicitud.getMotivacion() : "No especificada."));
            textArea.setWrapStyleWord(true);
            textArea.setLineWrap(true);
            textArea.setEditable(false);
            JOptionPane.showMessageDialog(this, new JScrollPane(textArea), "Motivación de Solicitud", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Seleccione una solicitud de la lista.", "Error", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void actualizarEstadoSolicitud(ActionEvent e) {
        SolicitudAdopcion solicitud = getSolicitudSeleccionada();
        if (solicitud == null) {
            JOptionPane.showMessageDialog(this, "Seleccione una solicitud de la lista para actualizar.", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Crear panel para el diálogo de actualización de estado
        JPanel panelActualizar = new JPanel(new GridLayout(0, 1, 5, 5));
        panelActualizar.add(new JLabel("Solicitud ID: " + solicitud.getId()));
        panelActualizar.add(new JLabel("Animal: " + cacheNombresAnimal.getOrDefault(solicitud.getIdAnimal(), "")));
        panelActualizar.add(new JLabel("Solicitante: " + cacheNombresSolicitante.getOrDefault(solicitud.getIdUsuarioSolicitante(), "")));
        panelActualizar.add(new JLabel("Estado Actual: " + solicitud.getEstado().name()));
        
        panelActualizar.add(new JSeparator());
        
        panelActualizar.add(new JLabel("Nuevo Estado*:"));
        JComboBox<SolicitudAdopcion.EstadoSolicitud> nuevoEstadoComboBox = new JComboBox<>(SolicitudAdopcion.EstadoSolicitud.values());
        nuevoEstadoComboBox.setSelectedItem(solicitud.getEstado()); // Preseleccionar estado actual
        panelActualizar.add(nuevoEstadoComboBox);

        panelActualizar.add(new JLabel("Notas del Administrador:"));
        JTextArea notasAdminArea = new JTextArea(3, 30);
        notasAdminArea.setText(solicitud.getNotasAdmin() != null ? solicitud.getNotasAdmin() : "");
        notasAdminArea.setLineWrap(true);
        notasAdminArea.setWrapStyleWord(true);
        panelActualizar.add(new JScrollPane(notasAdminArea));

        int result = JOptionPane.showConfirmDialog(this, panelActualizar, "Actualizar Estado de Solicitud",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            SolicitudAdopcion.EstadoSolicitud nuevoEstado = (SolicitudAdopcion.EstadoSolicitud) nuevoEstadoComboBox.getSelectedItem();
            String notas = notasAdminArea.getText().trim();

            if (nuevoEstado == null) {
                JOptionPane.showMessageDialog(this, "Debe seleccionar un nuevo estado.", "Error de Validación", JOptionPane.ERROR_MESSAGE);
                return;
            }

            System.out.println("GEST_SOL_DIALOG: [TEXTO PLANO CONTEXTO] Actualizando estado de Sol.ID " + solicitud.getId() + " a " + nuevoEstado);
            if (solicitudController.actualizarEstadoSolicitud(solicitud.getId(), nuevoEstado, notas.isEmpty() ? null : notas)) {
                JOptionPane.showMessageDialog(this, "Estado de la solicitud actualizado con éxito.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                // El controller ya maneja la lógica de actualizar el estado del animal si es APROBADA
                aplicarFiltros(null); // Recargar la tabla
            } else {
                JOptionPane.showMessageDialog(this, "Error al actualizar el estado de la solicitud. Revise la consola.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}