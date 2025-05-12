package Main;

import controllers.AnimalController;
import controllers.AuthController;
import controllers.UsuarioController; // Para el formulario de creación/edición
import model.Animal;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class GestionAnimalesDialog extends JDialog {
    private AnimalController animalController;
    private UsuarioController usuarioController; // Para pasarlo a FormularioAnimalDialog
    private JTable tablaAnimales;
    private DefaultTableModel tableModel;

    private JButton btnCrearAnimal;
    private JButton btnEditarAnimal;
    private JButton btnEliminarAnimal;
    private JButton btnRefrescar;
    private JButton btnCerrar;
    
    // TODO: Podrías añadir filtros aquí (especie, estado_adopcion, etc.)

    public GestionAnimalesDialog(Frame owner, AnimalController animalCtrl, UsuarioController usrCtrl) {
        super(owner, "Gestión de Animales (Staff) [TEXTO PLANO CONTEXTO]", true);
        this.animalController = animalCtrl;
        this.usuarioController = usrCtrl;

        initComponents();
        cargarAnimalesEnTabla();
        configurarTabla();

        setSize(950, 650);
        setLocationRelativeTo(owner);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        ((JPanel)getContentPane()).setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        // --- Panel Superior con Botón de Crear ---
        JPanel panelSuperior = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnCrearAnimal = new JButton("Registrar Nuevo Animal");
        btnCrearAnimal.addActionListener(this::abrirFormularioAnimal);
        panelSuperior.add(btnCrearAnimal);
        // TODO: Añadir campos de filtro aquí si se desea
        add(panelSuperior, BorderLayout.NORTH);
        
        // --- Tabla de Animales ---
        String[] columnNames = {"ID", "Nombre", "Especie", "Raza", "Edad", "Género", "Tamaño", "Estado Adopción"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tablaAnimales = new JTable(tableModel);
        add(new JScrollPane(tablaAnimales), BorderLayout.CENTER);

        // --- Panel de Acciones Inferior ---
        JPanel panelAcciones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnEditarAnimal = new JButton("Editar Seleccionado");
        btnEliminarAnimal = new JButton("Eliminar Seleccionado");
        btnRefrescar = new JButton("Refrescar Lista");
        
        btnEditarAnimal.addActionListener(this::editarAnimalSeleccionado);
        btnEliminarAnimal.addActionListener(this::eliminarAnimalSeleccionado);
        btnRefrescar.addActionListener(e -> cargarAnimalesEnTabla());

        panelAcciones.add(btnEditarAnimal);
        panelAcciones.add(btnEliminarAnimal);
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
        tablaAnimales.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaAnimales.getTableHeader().setReorderingAllowed(false);
        
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        tablaAnimales.setRowSorter(sorter);

        tablaAnimales.getColumnModel().getColumn(0).setPreferredWidth(30); // ID más angosto
        tablaAnimales.getColumnModel().getColumn(1).setPreferredWidth(150); // Nombre
        tablaAnimales.getColumnModel().getColumn(2).setPreferredWidth(100); // Especie
        tablaAnimales.getColumnModel().getColumn(4).setPreferredWidth(60); // Edad


        tablaAnimales.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                actualizarEstadoBotonesAccion();
            }
        });
        actualizarEstadoBotonesAccion();
    }

    private void actualizarEstadoBotonesAccion() {
        boolean haySeleccion = tablaAnimales.getSelectedRow() != -1;
        btnEditarAnimal.setEnabled(haySeleccion);
        btnEliminarAnimal.setEnabled(haySeleccion); 
        // Crear siempre está habilitado para Empleado/Admin (ya controlado por quién abre el diálogo)
    }

    private void cargarAnimalesEnTabla() {
        System.out.println("GEST_ANIMAL_DIALOG: [TEXTO PLANO CONTEXTO] Cargando todos los animales...");
        // Empleado/Admin ven todos los animales
        List<Animal> animales = animalController.obtenerTodosLosAnimales(); 
        
        tableModel.setRowCount(0); 
        if (animales != null) {
            for (Animal a : animales) {
                String edad = (a.getEdadEstimadaAnios() != null ? a.getEdadEstimadaAnios() + "a " : "") +
                              (a.getEdadEstimadaMeses() != null ? a.getEdadEstimadaMeses() + "m" : "");
                if (edad.trim().isEmpty()) edad = "N/A";

                tableModel.addRow(new Object[]{
                        a.getId(),
                        a.getNombre(),
                        a.getEspecie(),
                        a.getRaza() != null ? a.getRaza() : "N/A",
                        edad,
                        a.getGenero() != null ? a.getGenero() : "N/A",
                        a.getTamanio() != null ? a.getTamanio() : "N/A",
                        a.getEstadoAdopcion().name()
                });
            }
        }
        actualizarEstadoBotonesAccion();
    }

    private Animal getAnimalSeleccionadoDeTabla() {
        int selectedRow = tablaAnimales.getSelectedRow();
        if (selectedRow != -1) {
            int modelRow = tablaAnimales.convertRowIndexToModel(selectedRow);
            int animalId = (Integer) tableModel.getValueAt(modelRow, 0);
            return animalController.obtenerDetallesAnimal(animalId); // Para tener el objeto completo
        }
        return null;
    }

    private void abrirFormularioAnimal(ActionEvent e) {
        FormularioAnimalDialog formDialog = new FormularioAnimalDialog(this, animalController, usuarioController, null); // null para nuevo animal
        formDialog.setVisible(true);
        if (formDialog.isGuardadoExitoso()) {
            cargarAnimalesEnTabla(); // Refrescar la tabla
        }
    }

    private void editarAnimalSeleccionado(ActionEvent e) {
        Animal animal = getAnimalSeleccionadoDeTabla();
        if (animal != null) {
            FormularioAnimalDialog formDialog = new FormularioAnimalDialog(this, animalController, usuarioController, animal);
            formDialog.setVisible(true);
            if (formDialog.isGuardadoExitoso()) {
                cargarAnimalesEnTabla();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Seleccione un animal de la lista para editar.", "Error", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void eliminarAnimalSeleccionado(ActionEvent e) {
        Animal animal = getAnimalSeleccionadoDeTabla();
        if (animal != null) {
            // Solo Admin puede eliminar, el botón ya debería estar deshabilitado para Empleado si esta es la política.
            // Si se permite a Empleado eliminar, esta verificación extra no es necesaria aquí, sino en el controller.
            if (!AuthController.isAdmin()){
                 JOptionPane.showMessageDialog(this, "Solo los Administradores pueden eliminar animales.", "Permiso Denegado", JOptionPane.ERROR_MESSAGE);
                 return;
            }

            int confirm = JOptionPane.showConfirmDialog(this, 
                "¿Está seguro que desea ELIMINAR al animal '" + animal.getNombre() + "' (ID: " + animal.getId() + ")?\nEsto también eliminará sus solicitudes de adopción asociadas.",
                "Confirmar Eliminación", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (confirm == JOptionPane.YES_OPTION) {
                if (animalController.eliminarAnimal(animal.getId())) {
                    JOptionPane.showMessageDialog(this, "Animal eliminado con éxito.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    cargarAnimalesEnTabla();
                } else {
                    JOptionPane.showMessageDialog(this, "Error al eliminar el animal. Revise la consola.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Seleccione un animal de la lista para eliminar.", "Error", JOptionPane.WARNING_MESSAGE);
        }
    }
}