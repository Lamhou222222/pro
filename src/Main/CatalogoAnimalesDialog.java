package Main;

import controllers.AnimalController;
import controllers.SolicitudAdopcionController; // Para el botón de solicitar
import model.Animal;
import model.Usuario; // Para el AuthController.getUsuarioActual()
import controllers.AuthController; // Para obtener el usuario actual para la solicitud

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.ArrayList;

public class CatalogoAnimalesDialog extends JDialog {
    private AnimalController animalController;
    private SolicitudAdopcionController solicitudAdopcionController; // Para crear solicitudes

    private JTable tablaAnimales;
    private DefaultTableModel tableModel;
    private JButton btnVerDetalles;
    private JButton btnSolicitarAdopcion;
    private JButton btnRefrescar;
    private JButton btnCerrar;
    
    // Podrías añadir filtros aquí (especie, tamaño, etc.)

    public CatalogoAnimalesDialog(Frame owner, AnimalController animalCtrl, SolicitudAdopcionController solicitudCtrl) {
        super(owner, "Catálogo de Animales para Adopción", true);
        this.animalController = animalCtrl;
        this.solicitudAdopcionController = solicitudCtrl;

        initComponents();
        cargarAnimalesDisponibles();
        configurarTabla();

        setSize(800, 600);
        setLocationRelativeTo(owner);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        ((JPanel)getContentPane()).setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        // --- Tabla de Animales ---
        String[] columnNames = {"ID", "Nombre", "Especie", "Raza", "Edad", "Género", "Tamaño"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tablaAnimales = new JTable(tableModel);
        add(new JScrollPane(tablaAnimales), BorderLayout.CENTER);

        // --- Panel de Acciones ---
        JPanel panelAcciones = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        btnVerDetalles = new JButton("Ver Detalles");
        btnSolicitarAdopcion = new JButton("Solicitar Adopción");
        btnRefrescar = new JButton("Refrescar Lista");
        btnCerrar = new JButton("Cerrar");

        btnVerDetalles.addActionListener(this::verDetallesAnimalSeleccionado);
        btnSolicitarAdopcion.addActionListener(this::solicitarAdopcionAnimalSeleccionado);
        btnRefrescar.addActionListener(e -> cargarAnimalesDisponibles());
        btnCerrar.addActionListener(e -> dispose());
        
        btnVerDetalles.setEnabled(false);
        btnSolicitarAdopcion.setEnabled(false);

        panelAcciones.add(btnVerDetalles);
        panelAcciones.add(btnSolicitarAdopcion);
        panelAcciones.add(btnRefrescar);
        
        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.add(panelAcciones, BorderLayout.WEST);
        
        JPanel cerrarPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        cerrarPanel.add(btnCerrar);
        southPanel.add(cerrarPanel, BorderLayout.EAST);

        add(southPanel, BorderLayout.SOUTH);
    }

    private void configurarTabla() {
        tablaAnimales.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaAnimales.getTableHeader().setReorderingAllowed(false);
        
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        tablaAnimales.setRowSorter(sorter);

        tablaAnimales.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                actualizarEstadoBotonesAccion();
            }
        });
    }

    private void actualizarEstadoBotonesAccion() {
        boolean seleccionado = tablaAnimales.getSelectedRow() != -1;
        btnVerDetalles.setEnabled(seleccionado);
        // Solo se puede solicitar si es adoptante y el animal está disponible (ya filtrado)
        btnSolicitarAdopcion.setEnabled(seleccionado && AuthController.isAdoptante());
    }

    private void cargarAnimalesDisponibles() {
        System.out.println("CATALOGO_DIALOG: [TEXTO PLANO CONTEXTO] Cargando animales disponibles.");
        List<Animal> animales = animalController.obtenerAnimalesDisponiblesParaAdopcion();
        
        tableModel.setRowCount(0); 
        if (animales != null) {
            for (Animal a : animales) {
                String edad = (a.getEdadEstimadaAnios() != null ? a.getEdadEstimadaAnios() + "a " : "") +
                              (a.getEdadEstimadaMeses() != null ? a.getEdadEstimadaMeses() + "m" : "N/A");
                if (edad.trim().equals("N/A") && a.getEdadEstimadaAnios() == null && a.getEdadEstimadaMeses() == null) {
                    edad = "Desconocida";
                }

                tableModel.addRow(new Object[]{
                        a.getId(),
                        a.getNombre(),
                        a.getEspecie(),
                        a.getRaza() != null ? a.getRaza() : "N/A",
                        edad,
                        a.getGenero() != null ? a.getGenero() : "N/A",
                        a.getTamanio() != null ? a.getTamanio() : "N/A"
                });
            }
        } else {
            System.out.println("CATALOGO_DIALOG: [TEXTO PLANO CONTEXTO] La lista de animales devuelta es null.");
        }
        actualizarEstadoBotonesAccion();
    }

    private Animal getAnimalSeleccionado() {
        int selectedRow = tablaAnimales.getSelectedRow();
        if (selectedRow != -1) {
            int modelRow = tablaAnimales.convertRowIndexToModel(selectedRow);
            int animalId = (Integer) tableModel.getValueAt(modelRow, 0); // Columna ID
            return animalController.obtenerDetallesAnimal(animalId); // Obtener el objeto completo
        }
        return null;
    }

    private void verDetallesAnimalSeleccionado(ActionEvent e) {
        Animal animal = getAnimalSeleccionado();
        if (animal != null) {
            // TODO: Crear un AnimalFichaDialog para mostrar todos los detalles
            StringBuilder detalles = new StringBuilder();
            detalles.append("ID: ").append(animal.getId()).append("\n");
            detalles.append("Nombre: ").append(animal.getNombre()).append("\n");
            detalles.append("Especie: ").append(animal.getEspecie()).append("\n");
            if(animal.getRaza()!=null) detalles.append("Raza: ").append(animal.getRaza()).append("\n");
            // ... añadir más campos ...
            detalles.append("Descripción: ").append(animal.getDescripcionCaracter() != null ? animal.getDescripcionCaracter() : "No disponible").append("\n");
            detalles.append("Estado Adopción: ").append(animal.getEstadoAdopcion()).append("\n");

            JOptionPane.showMessageDialog(this, detalles.toString(), "Detalles de " + animal.getNombre(), JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Por favor, seleccione un animal de la lista.", "Selección Requerida", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void solicitarAdopcionAnimalSeleccionado(ActionEvent e) {
        Animal animal = getAnimalSeleccionado();
        Usuario solicitante = AuthController.getUsuarioActual();

        if (animal != null && solicitante != null && AuthController.isAdoptante()) {
            if (animal.getEstadoAdopcion() != Animal.EstadoAdopcion.DISPONIBLE) {
                JOptionPane.showMessageDialog(this, "Este animal no está actualmente disponible para adopción.", "Animal no Disponible", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // TODO: Abrir un SolicitudAdopcionFormDialog para que el usuario escriba su motivación.
            // Por ahora, una confirmación simple y una solicitud básica.
            String motivacion = JOptionPane.showInputDialog(this, 
                "Estás a punto de solicitar la adopción de " + animal.getNombre() + ".\n" +
                "Por favor, escribe una breve motivación:",
                "Solicitar Adopción", JOptionPane.PLAIN_MESSAGE);

            if (motivacion != null && !motivacion.trim().isEmpty()) {
                model.SolicitudAdopcion nuevaSolicitud = new model.SolicitudAdopcion();
                nuevaSolicitud.setIdAnimal(animal.getId());
                nuevaSolicitud.setIdUsuarioSolicitante(solicitante.getId());
                nuevaSolicitud.setMotivacion(motivacion.trim());
                // El estado por defecto es ENVIADA (definido en el modelo/repositorio)

                if (solicitudAdopcionController.crearSolicitud(nuevaSolicitud)) {
                    JOptionPane.showMessageDialog(this, "Solicitud de adopción para " + animal.getNombre() + " enviada con éxito.", "Solicitud Enviada", JOptionPane.INFORMATION_MESSAGE);
                    // Opcional: Cambiar estado del animal a RESERVADO o refrescar catálogo
                    cargarAnimalesDisponibles();
                } else {
                    JOptionPane.showMessageDialog(this, "Error al enviar la solicitud. Revise la consola.", "Error de Solicitud", JOptionPane.ERROR_MESSAGE);
                }
            } else if (motivacion != null) { // Si presionó OK pero no escribió nada
                 JOptionPane.showMessageDialog(this, "La motivación no puede estar vacía.", "Error de Solicitud", JOptionPane.WARNING_MESSAGE);
            }
            // Si motivacion es null, el usuario canceló el InputDialog.

        } else if (animal == null) {
            JOptionPane.showMessageDialog(this, "Por favor, seleccione un animal de la lista.", "Selección Requerida", JOptionPane.WARNING_MESSAGE);
        } else if (!AuthController.isAdoptante()) {
             JOptionPane.showMessageDialog(this, "Solo los usuarios con rol ADOPTANTE_POTENCIAL pueden solicitar adopciones.", "Acción no Permitida", JOptionPane.WARNING_MESSAGE);
        }
    }
}