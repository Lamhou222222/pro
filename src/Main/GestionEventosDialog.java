package Main;

import controllers.AuthController;
import controllers.EventoController;
import controllers.UsuarioController; // Para obtener nombres de organizadores
import model.EventoRefugio;
import model.Usuario;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.HashMap; // Para cachear nombres de usuario
import java.util.Map;

public class GestionEventosDialog extends JDialog {
    private EventoController eventoController;
    private UsuarioController usuarioController; // Para buscar nombres de organizadores
    private JTable tablaEventos;
    private DefaultTableModel tableModel;

    private JButton btnCrearEvento;
    private JButton btnEditarEvento; // Placeholder
    private JButton btnEliminarEvento;
    private JButton btnRefrescar;
    private JButton btnCerrar;

    // Cache para nombres de usuario para no consultar la BD repetidamente
    private Map<Integer, String> cacheNombresOrganizador = new HashMap<>();
    private final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    public GestionEventosDialog(Frame owner, EventoController eventoCtrl, UsuarioController usrCtrl) {
        super(owner, "Gestión de Eventos [MODO TEXTO PLANO]", true);
        this.eventoController = eventoCtrl;
        this.usuarioController = usrCtrl; // Necesario para obtener el nombre del organizador

        initComponents();
        cargarEventosEnTabla();
        configurarTabla();

        setSize(900, 600);
        setLocationRelativeTo(owner);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        ((JPanel)getContentPane()).setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        // --- Panel Superior con Botón de Crear ---
        JPanel panelSuperior = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnCrearEvento = new JButton("Crear Nuevo Evento");
        btnCrearEvento.addActionListener(this::abrirFormularioEvento);
        panelSuperior.add(btnCrearEvento);
        add(panelSuperior, BorderLayout.NORTH);
        
        // --- Tabla de Eventos ---
        String[] columnNames = {"ID", "Nombre del Evento", "Fecha Inicio", "Fecha Fin", "Ubicación", "Tipo", "Organizador"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tablaEventos = new JTable(tableModel);
        add(new JScrollPane(tablaEventos), BorderLayout.CENTER);

        // --- Panel de Acciones Inferior ---
        JPanel panelAcciones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnEditarEvento = new JButton("Editar Seleccionado");
        btnEliminarEvento = new JButton("Eliminar Seleccionado");
        btnRefrescar = new JButton("Refrescar Lista");
        
        btnEditarEvento.addActionListener(this::editarEventoSeleccionado);
        btnEliminarEvento.addActionListener(this::eliminarEventoSeleccionado);
        btnRefrescar.addActionListener(e -> cargarEventosEnTabla());

        panelAcciones.add(btnEditarEvento);
        panelAcciones.add(btnEliminarEvento);
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
        tablaEventos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaEventos.getTableHeader().setReorderingAllowed(false);
        
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        tablaEventos.setRowSorter(sorter);

        tablaEventos.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                actualizarEstadoBotonesAccion();
            }
        });
        actualizarEstadoBotonesAccion(); // Estado inicial
    }

    private void actualizarEstadoBotonesAccion() {
        boolean haySeleccion = tablaEventos.getSelectedRow() != -1;
        btnEditarEvento.setEnabled(haySeleccion);
        btnEliminarEvento.setEnabled(haySeleccion);
        // Solo Empleado o Admin pueden crear/editar/eliminar
        boolean puedeGestionar = AuthController.isEmpleado(); // Incluye Admin
        btnCrearEvento.setEnabled(puedeGestionar);
        btnEditarEvento.setEnabled(haySeleccion && puedeGestionar);
        btnEliminarEvento.setEnabled(haySeleccion && puedeGestionar);
    }

    private void cargarEventosEnTabla() {
        System.out.println("GEST_EVENTO_DIALOG: [TEXTO PLANO CONTEXTO] Cargando eventos...");
        // Admin y Empleado ven todos, otros roles podrían ver una lista filtrada (no implementado aquí)
        List<EventoRefugio> eventos = eventoController.obtenerTodosLosEventosPublicos(); 
        
        tableModel.setRowCount(0); 
        if (eventos != null) {
            for (EventoRefugio evento : eventos) {
                String nombreOrganizador = "N/A";
                if (evento.getIdUsuarioOrganizador() != null) {
                    nombreOrganizador = cacheNombresOrganizador.computeIfAbsent(evento.getIdUsuarioOrganizador(), id -> {
                        Usuario organizador = usuarioController.obtenerUsuarioPorId(id);
                        return organizador != null ? organizador.getNombreCompleto() : "ID: " + id;
                    });
                }
                tableModel.addRow(new Object[]{
                        evento.getId(),
                        evento.getNombreEvento(),
                        evento.getFechaInicioEvento() != null ? sdf.format(evento.getFechaInicioEvento()) : "N/A",
                        evento.getFechaFinEvento() != null ? sdf.format(evento.getFechaFinEvento()) : "N/A",
                        evento.getUbicacion() != null ? evento.getUbicacion() : "N/A",
                        evento.getTipoEvento() != null ? evento.getTipoEvento() : "N/A",
                        nombreOrganizador
                });
            }
        }
        actualizarEstadoBotonesAccion();
    }

    private EventoRefugio getEventoSeleccionado() {
        int selectedRow = tablaEventos.getSelectedRow();
        if (selectedRow != -1) {
            int modelRow = tablaEventos.convertRowIndexToModel(selectedRow);
            int eventoId = (Integer) tableModel.getValueAt(modelRow, 0);
            return eventoController.obtenerDetallesEvento(eventoId);
        }
        return null;
    }

    private void abrirFormularioEvento(ActionEvent e) {
        // Si se pasa null, es para crear un nuevo evento
        FormularioEventoDialog formDialog = new FormularioEventoDialog(this, eventoController, usuarioController, null);
        formDialog.setVisible(true);
        if (formDialog.isGuardadoExitoso()) { // Si el formulario se guardó
            cargarEventosEnTabla(); // Refrescar la tabla
        }
    }

    private void editarEventoSeleccionado(ActionEvent e) {
        EventoRefugio evento = getEventoSeleccionado();
        if (evento != null) {
            FormularioEventoDialog formDialog = new FormularioEventoDialog(this, eventoController, usuarioController, evento);
            formDialog.setVisible(true);
            if (formDialog.isGuardadoExitoso()) {
                cargarEventosEnTabla();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Seleccione un evento de la lista para editar.", "Error", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void eliminarEventoSeleccionado(ActionEvent e) {
        EventoRefugio evento = getEventoSeleccionado();
        if (evento != null) {
            int confirm = JOptionPane.showConfirmDialog(this, 
                "¿Está seguro que desea ELIMINAR el evento '" + evento.getNombreEvento() + "' (ID: " + evento.getId() + ")?",
                "Confirmar Eliminación", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (confirm == JOptionPane.YES_OPTION) {
                if (eventoController.eliminarEvento(evento.getId())) {
                    JOptionPane.showMessageDialog(this, "Evento eliminado con éxito.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    cargarEventosEnTabla();
                } else {
                    JOptionPane.showMessageDialog(this, "Error al eliminar el evento. Revise la consola.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Seleccione un evento de la lista para eliminar.", "Error", JOptionPane.WARNING_MESSAGE);
        }
    }
}