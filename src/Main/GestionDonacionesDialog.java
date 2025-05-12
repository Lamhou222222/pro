package Main;

import controllers.AuthController; // Para permisos
import controllers.DonacionController;
import controllers.UsuarioController; // Para nombres de donantes
import model.Donacion;
import model.Usuario;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.text.NumberFormat; // Para formatear moneda
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GestionDonacionesDialog extends JDialog {
    private DonacionController donacionController;
    private UsuarioController usuarioController;
    private JTable tablaDonaciones;
    private DefaultTableModel tableModel;

    private JButton btnVerDetalles;
    private JButton btnEliminarDonacion;
    private JButton btnRefrescar;
    private JButton btnCerrar;
    // TODO: Añadir filtros si se desea (por tipo, por fecha, por donante)

    private Map<Integer, String> cacheNombresDonantes = new HashMap<>();
    private final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();


    public GestionDonacionesDialog(Frame owner, DonacionController donCtrl, UsuarioController usrCtrl) {
        super(owner, "Gestión de Donaciones (Admin) [TEXTO PLANO CONTEXTO]", true);
        this.donacionController = donCtrl;
        this.usuarioController = usrCtrl;

        initComponents();
        cargarDonacionesEnTabla();
        configurarTabla();

        setSize(850, 550); // Ajustado
        setLocationRelativeTo(owner);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    }

    private void initComponents() {
        setLayout(new BorderLayout(10,10));
        ((JPanel)getContentPane()).setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        // TODO: Panel de Filtros (opcional)
        // JPanel panelFiltros = new JPanel(new FlowLayout(FlowLayout.LEFT));
        // add(panelFiltros, BorderLayout.NORTH);


        String[] columnNames = {"ID", "Fecha", "Donante", "Tipo", "Monto", "Descripción Items (Corto)"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        tablaDonaciones = new JTable(tableModel);
        add(new JScrollPane(tablaDonaciones), BorderLayout.CENTER);

        JPanel panelAcciones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnVerDetalles = new JButton("Ver Detalles Completos");
        btnEliminarDonacion = new JButton("Eliminar Donación");
        btnRefrescar = new JButton("Refrescar Lista");

        btnVerDetalles.addActionListener(this::verDetallesDonacion);
        btnEliminarDonacion.addActionListener(this::eliminarDonacionSeleccionada);
        btnRefrescar.addActionListener(e -> cargarDonacionesEnTabla());
        
        panelAcciones.add(btnVerDetalles);
        panelAcciones.add(btnEliminarDonacion);
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
        tablaDonaciones.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaDonaciones.getTableHeader().setReorderingAllowed(false);
        
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        tablaDonaciones.setRowSorter(sorter);

        tablaDonaciones.getColumnModel().getColumn(0).setPreferredWidth(40);  // ID
        tablaDonaciones.getColumnModel().getColumn(1).setPreferredWidth(120); // Fecha
        tablaDonaciones.getColumnModel().getColumn(2).setPreferredWidth(180); // Donante
        tablaDonaciones.getColumnModel().getColumn(3).setPreferredWidth(100); // Tipo
        tablaDonaciones.getColumnModel().getColumn(4).setPreferredWidth(80);  // Monto
        tablaDonaciones.getColumnModel().getColumn(4).setCellRenderer(new CurrencyRenderer()); // Formato moneda
        tablaDonaciones.getColumnModel().getColumn(5).setPreferredWidth(250); // Desc

        tablaDonaciones.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                actualizarEstadoBotonesAccion();
            }
        });
        actualizarEstadoBotonesAccion();
    }

    private void actualizarEstadoBotonesAccion() {
        boolean haySeleccion = tablaDonaciones.getSelectedRow() != -1;
        btnVerDetalles.setEnabled(haySeleccion);
        btnEliminarDonacion.setEnabled(haySeleccion && AuthController.isAdmin()); // Solo Admin
    }

    private void cargarDonacionesEnTabla() {
        System.out.println("GEST_DON_DIALOG: [TEXTO PLANO CONTEXTO] Cargando donaciones...");
        // Solo Admin puede ver todas las donaciones desde este diálogo
        List<Donacion> donaciones = AuthController.isAdmin() ? donacionController.obtenerTodasLasDonaciones() : new ArrayList<>();
        
        tableModel.setRowCount(0); 
        if (donaciones != null) {
            for (Donacion donacion : donaciones) {
                String nombreDonante = "Anónimo";
                if (donacion.getIdUsuarioDonante() != null) {
                    nombreDonante = cacheNombresDonantes.computeIfAbsent(donacion.getIdUsuarioDonante(), id -> {
                        Usuario donanteObj = usuarioController.obtenerUsuarioPorId(id);
                        return donanteObj != null ? donanteObj.getNombreCompleto() : "Usuario ID: " + id;
                    });
                } else if (donacion.getNombreDonanteAnonimo() != null && !donacion.getNombreDonanteAnonimo().isEmpty()) {
                    nombreDonante = donacion.getNombreDonanteAnonimo();
                }

                String descCorta = donacion.getDescripcionItems();
                if (descCorta != null && descCorta.length() > 50) {
                    descCorta = descCorta.substring(0, 47) + "...";
                }

                tableModel.addRow(new Object[]{
                        donacion.getId(),
                        donacion.getFechaDonacion() != null ? sdf.format(donacion.getFechaDonacion()) : "N/A",
                        nombreDonante,
                        donacion.getTipoDonacion(),
                        donacion.getMonto(), // Se formateará por el renderer si es monetaria
                        descCorta != null ? descCorta : (donacion.getMonto() != null ? "" : "N/A") // Evitar "null" si es monetaria
                });
            }
        }
        actualizarEstadoBotonesAccion();
    }

    private Donacion getDonacionSeleccionada() {
        int selectedRow = tablaDonaciones.getSelectedRow();
        if (selectedRow != -1) {
            int modelRow = tablaDonaciones.convertRowIndexToModel(selectedRow);
            int donacionId = (Integer) tableModel.getValueAt(modelRow, 0);
            return donacionController.obtenerDonacionPorId(donacionId); // Admin tiene permiso
        }
        return null;
    }

    private void verDetallesDonacion(ActionEvent e) {
        Donacion donacion = getDonacionSeleccionada();
        if (donacion != null) {
            JTextArea textArea = new JTextArea(10, 40);
            StringBuilder detalles = new StringBuilder();
            detalles.append("ID Donación: ").append(donacion.getId()).append("\n");
            detalles.append("Fecha: ").append(donacion.getFechaDonacion() != null ? sdf.format(donacion.getFechaDonacion()) : "N/A").append("\n");
            
            String nombreDonante = "Anónimo";
             if (donacion.getIdUsuarioDonante() != null) {
                Usuario donanteObj = usuarioController.obtenerUsuarioPorId(donacion.getIdUsuarioDonante());
                nombreDonante = donanteObj != null ? donanteObj.getNombreCompleto() + " (ID: "+donanteObj.getId()+")" : "Usuario ID: " + donacion.getIdUsuarioDonante();
            } else if (donacion.getNombreDonanteAnonimo() != null && !donacion.getNombreDonanteAnonimo().isEmpty()) {
                nombreDonante = donacion.getNombreDonanteAnonimo();
            }
            detalles.append("Donante: ").append(nombreDonante).append("\n");
            
            detalles.append("Tipo: ").append(donacion.getTipoDonacion()).append("\n");
            if (donacion.getMonto() != null) {
                detalles.append("Monto: ").append(currencyFormat.format(donacion.getMonto())).append("\n");
            }
            detalles.append("Descripción Items:\n").append(donacion.getDescripcionItems() != null ? donacion.getDescripcionItems() : "N/A");
            
            textArea.setText(detalles.toString());
            textArea.setWrapStyleWord(true);
            textArea.setLineWrap(true);
            textArea.setEditable(false);
            JOptionPane.showMessageDialog(this, new JScrollPane(textArea), "Detalles de Donación ID: " + donacion.getId(), JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Seleccione una donación de la lista.", "Selección Requerida", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void eliminarDonacionSeleccionada(ActionEvent e) {
        Donacion donacion = getDonacionSeleccionada();
        if (donacion != null) {
            int confirm = JOptionPane.showConfirmDialog(this, 
                "¿Está seguro que desea ELIMINAR la donación ID: " + donacion.getId() + "?\nEsta acción no se puede deshacer.",
                "Confirmar Eliminación de Donación", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (confirm == JOptionPane.YES_OPTION) {
                if (donacionController.eliminarDonacion(donacion.getId())) {
                    JOptionPane.showMessageDialog(this, "Donación eliminada con éxito.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    cargarDonacionesEnTabla();
                } else {
                    JOptionPane.showMessageDialog(this, "Error al eliminar la donación. Revise la consola.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Seleccione una donación de la lista para eliminar.", "Selección Requerida", JOptionPane.WARNING_MESSAGE);
        }
    }
}

// Helper class para renderizar moneda en la tabla (ya lo tenías en una respuesta anterior)
// class CurrencyRenderer extends javax.swing.table.DefaultTableCellRenderer { ... }