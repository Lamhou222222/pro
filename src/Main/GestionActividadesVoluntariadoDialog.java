package Main;

import controllers.AnimalController;
import controllers.AuthController; // Para permisos
import controllers.UsuarioController;
import controllers.VoluntariadoController;
import model.ActividadVoluntariado;
import model.Animal;
import model.Usuario;
// Ya no necesitamos importar VoluntariadoRepository directamente aquí si el controller tiene todos los métodos

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.FocusListener;
import java.text.SimpleDateFormat; // Para mostrar fechas en la tabla
import java.util.List;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Date; // Para el tipo de dato en el filtro de fecha

public class GestionActividadesVoluntariadoDialog extends JDialog {
    private VoluntariadoController voluntariadoController;
    private UsuarioController usuarioController;
    private AnimalController animalController;
    private JTable tablaActividades;
    private DefaultTableModel tableModel;

    // Filtros
    private JTextField filtroVoluntarioIdField;
    private JTextField filtroTipoActividadField;
    // Usaremos JTextField para fechas de filtro, similar al formulario
    private JTextField filtroFechaDesdeDia, filtroFechaDesdeMes, filtroFechaDesdeAnio;
    private JTextField filtroFechaHastaDia, filtroFechaHastaMes, filtroFechaHastaAnio;


    private JButton btnFiltrar;
    private JButton btnLimpiarFiltros;

    private JButton btnRegistrarActividad;
    private JButton btnEditarActividad;
    private JButton btnEliminarActividad;
    private JButton btnRefrescar;
    private JButton btnCerrar;

    private Map<Integer, String> cacheNombresVoluntarios = new HashMap<>();
    private Map<Integer, String> cacheNombresAnimales = new HashMap<>();
    private final SimpleDateFormat sdfTablaDisplay = new SimpleDateFormat("dd/MM/yyyy");


    public GestionActividadesVoluntariadoDialog(Frame owner, VoluntariadoController volCtrl, UsuarioController usrCtrl, AnimalController aniCtrl) {
        super(owner, "Gestión de Actividades de Voluntariado (Admin)", true);
        this.voluntariadoController = volCtrl;
        this.usuarioController = usrCtrl;
        this.animalController = aniCtrl;

        initComponents();
        aplicarFiltros(null); 
        configurarTabla();

        setSize(1000, 650);
        setLocationRelativeTo(owner);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        ((JPanel)getContentPane()).setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        JPanel panelSuperior = new JPanel(new BorderLayout(10,5));
        JPanel panelFiltros = new JPanel(new GridBagLayout());
        panelFiltros.setBorder(BorderFactory.createTitledBorder("Filtrar Actividades"));
        GridBagConstraints gbcFiltros = new GridBagConstraints();
        gbcFiltros.insets = new Insets(2,5,2,5);
        gbcFiltros.anchor = GridBagConstraints.WEST;

        int yFiltro = 0;
        gbcFiltros.gridx = 0; gbcFiltros.gridy = yFiltro; panelFiltros.add(new JLabel("ID Voluntario:"), gbcFiltros);
        filtroVoluntarioIdField = new JTextField(5);
        gbcFiltros.gridx = 1; gbcFiltros.gridy = yFiltro; panelFiltros.add(filtroVoluntarioIdField, gbcFiltros);

        gbcFiltros.gridx = 2; gbcFiltros.gridy = yFiltro; panelFiltros.add(new JLabel("Tipo Actividad (contiene):"), gbcFiltros);
        filtroTipoActividadField = new JTextField(15);
        gbcFiltros.gridx = 3; gbcFiltros.gridy = yFiltro++; panelFiltros.add(filtroTipoActividadField, gbcFiltros);
        
        // Filtro Fecha Desde
        gbcFiltros.gridx = 0; gbcFiltros.gridy = yFiltro; panelFiltros.add(new JLabel("Fecha Desde (dd/mm/aaaa):"), gbcFiltros);
        JPanel panelFechaDesde = new JPanel(new FlowLayout(FlowLayout.LEFT, 2,0));
        filtroFechaDesdeDia = createNumericField(2, "dd");
        filtroFechaDesdeMes = createNumericField(2, "mm");
        filtroFechaDesdeAnio = createNumericField(4, "aaaa");
        panelFechaDesde.add(filtroFechaDesdeDia); panelFechaDesde.add(new JLabel("/"));
        panelFechaDesde.add(filtroFechaDesdeMes); panelFechaDesde.add(new JLabel("/"));
        panelFechaDesde.add(filtroFechaDesdeAnio);
        gbcFiltros.gridx = 1; gbcFiltros.gridy = yFiltro; panelFiltros.add(panelFechaDesde, gbcFiltros);

        // Filtro Fecha Hasta
        gbcFiltros.gridx = 2; gbcFiltros.gridy = yFiltro; panelFiltros.add(new JLabel("Fecha Hasta (dd/mm/aaaa):"), gbcFiltros);
        JPanel panelFechaHasta = new JPanel(new FlowLayout(FlowLayout.LEFT, 2,0));
        filtroFechaHastaDia = createNumericField(2, "dd");
        filtroFechaHastaMes = createNumericField(2, "mm");
        filtroFechaHastaAnio = createNumericField(4, "aaaa");
        panelFechaHasta.add(filtroFechaHastaDia); panelFechaHasta.add(new JLabel("/"));
        panelFechaHasta.add(filtroFechaHastaMes); panelFechaHasta.add(new JLabel("/"));
        panelFechaHasta.add(filtroFechaHastaAnio);
        gbcFiltros.gridx = 3; gbcFiltros.gridy = yFiltro++; panelFiltros.add(panelFechaHasta, gbcFiltros);


        JPanel panelBotonesFiltro = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnFiltrar = new JButton("Filtrar");
        btnFiltrar.addActionListener(this::aplicarFiltros);
        panelBotonesFiltro.add(btnFiltrar);
        btnLimpiarFiltros = new JButton("Limpiar Filtros");
        btnLimpiarFiltros.addActionListener(this::limpiarFiltros);
        panelBotonesFiltro.add(btnLimpiarFiltros);
        
        gbcFiltros.gridx = 0; gbcFiltros.gridy = yFiltro; gbcFiltros.gridwidth = 4; gbcFiltros.anchor = GridBagConstraints.CENTER;
        panelFiltros.add(panelBotonesFiltro, gbcFiltros);
        
        panelSuperior.add(panelFiltros, BorderLayout.CENTER);

        btnRegistrarActividad = new JButton("Registrar Nueva Actividad");
        btnRegistrarActividad.addActionListener(this::abrirFormularioActividadParaCrear);
        JPanel panelCrear = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelCrear.add(btnRegistrarActividad);
        panelSuperior.add(panelCrear, BorderLayout.EAST);
        
        add(panelSuperior, BorderLayout.NORTH);
        
        String[] columnNames = {"ID Act.", "Fecha", "Voluntario", "Animal Asociado", "Tipo Actividad", "Horas", "Descripción Corta"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        tablaActividades = new JTable(tableModel);
        add(new JScrollPane(tablaActividades), BorderLayout.CENTER);

        JPanel panelAcciones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnEditarActividad = new JButton("Editar Seleccionada");
        btnEliminarActividad = new JButton("Eliminar Seleccionada");
        btnRefrescar = new JButton("Refrescar Lista");
        
        btnEditarActividad.addActionListener(this::editarActividadSeleccionada);
        btnEliminarActividad.addActionListener(this::eliminarActividadSeleccionada);
        btnRefrescar.addActionListener(e -> aplicarFiltros(null));
        
        panelAcciones.add(btnEditarActividad);
        panelAcciones.add(btnEliminarActividad);
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
    
    private JTextField createNumericField(int columns, String placeholder) {
        JTextField field = new JTextField(columns);
        addPlaceholderFocusListener(field, placeholder);
        return field;
    }
    
    private void addPlaceholderFocusListener(JTextField field, String placeholder) {
        if (field.getText().isEmpty()) {
            field.setForeground(Color.GRAY);
            field.setText(placeholder);
        }
        field.addFocusListener(new FocusListener() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(Color.BLACK);
                }
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                if (field.getText().isEmpty()) {
                    field.setForeground(Color.GRAY);
                    field.setText(placeholder);
                }
            }
        });
    }

    private void configurarTabla() {
        tablaActividades.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaActividades.getTableHeader().setReorderingAllowed(false);
        
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        tablaActividades.setRowSorter(sorter);

        tablaActividades.getColumnModel().getColumn(0).setPreferredWidth(60);
        tablaActividades.getColumnModel().getColumn(1).setPreferredWidth(90);
        tablaActividades.getColumnModel().getColumn(2).setPreferredWidth(180);
        tablaActividades.getColumnModel().getColumn(3).setPreferredWidth(150);
        tablaActividades.getColumnModel().getColumn(4).setPreferredWidth(150);
        tablaActividades.getColumnModel().getColumn(5).setPreferredWidth(60);
        tablaActividades.getColumnModel().getColumn(6).setPreferredWidth(250);

        tablaActividades.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                actualizarEstadoBotonesAccion();
            }
        });
        actualizarEstadoBotonesAccion();
    }

    private void actualizarEstadoBotonesAccion() {
        boolean haySeleccion = tablaActividades.getSelectedRow() != -1;
        boolean puedeGestionar = AuthController.isAdmin();

        btnEditarActividad.setEnabled(haySeleccion && puedeGestionar);
        btnEliminarActividad.setEnabled(haySeleccion && puedeGestionar);
        btnRegistrarActividad.setEnabled(puedeGestionar); 
    }

    private Date parseDateFromManualFieldsFilter(JTextField diaF, JTextField mesF, JTextField anioF, String fieldName) {
        String diaStr = diaF.getText().trim();
        String mesStr = mesF.getText().trim();
        String anioStr = anioF.getText().trim();

        // Si todos los campos de fecha son placeholder o vacíos, no hay filtro por esta fecha
        if ( (diaStr.isEmpty() || diaStr.equals("dd")) && 
             (mesStr.isEmpty() || mesStr.equals("mm")) &&
             (anioStr.isEmpty() || anioStr.equals("aaaa")) ) {
            return null; // No filtrar por esta fecha
        }
        // Si alguno está lleno pero otros no, o no son placeholder, es un error de entrada
        if ( diaStr.equals("dd") || mesStr.equals("mm") || anioStr.equals("aaaa") ||
             diaStr.isEmpty() || mesStr.isEmpty() || anioStr.isEmpty() ) {
            JOptionPane.showMessageDialog(this, "Complete todos los campos (día, mes, año) para el filtro de " + fieldName + " o déjelos como placeholders/vacíos para no filtrar.", "Error de Formato de Filtro", JOptionPane.WARNING_MESSAGE);
            return null; // Indica error para no aplicar filtro parcial
        }
        try {
            int dia = Integer.parseInt(diaStr);
            int mes = Integer.parseInt(mesStr);
            int anio = Integer.parseInt(anioStr);

            if (dia < 1 || dia > 31 || mes < 1 || mes > 12 || anio < 1900 || anio > 2100) {
                 JOptionPane.showMessageDialog(this, "Valores inválidos para el filtro de " + fieldName + ".", "Error de Formato de Filtro", JOptionPane.WARNING_MESSAGE);
                return null;
            }
            Calendar calendar = Calendar.getInstance();
            calendar.setLenient(false);
            calendar.set(anio, mes - 1, dia, 0, 0, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            return calendar.getTime(); // Devuelve java.util.Date
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Formato numérico incorrecto para el filtro de " + fieldName + ".", "Error de Formato de Filtro", JOptionPane.WARNING_MESSAGE);
            return null;
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, "Fecha inválida para el filtro de " + fieldName + ".", "Error de Fecha de Filtro", JOptionPane.WARNING_MESSAGE);
            return null;
        }
    }

    private void cargarActividadesEnTabla(Integer idVoluntarioFiltro, String tipoActividadFiltro, Date fechaDesdeFiltro, Date fechaHastaFiltro) {
        System.out.println("GEST_ACT_VOL_DIALOG: Cargando actividades. Filtros: VolID=" + idVoluntarioFiltro + ", Tipo=" + tipoActividadFiltro + ", Desde=" + fechaDesdeFiltro + ", Hasta=" + fechaHastaFiltro);
        
        List<ActividadVoluntariado> actividadesGlobales = voluntariadoController.obtenerTodasLasActividades();
        List<ActividadVoluntariado> actividadesFiltradas = new ArrayList<>();
        
        if (actividadesGlobales != null) {
            for (ActividadVoluntariado act : actividadesGlobales) {
                boolean pasaFiltro = true;
                if (idVoluntarioFiltro != null && act.getIdVoluntario() != idVoluntarioFiltro) {
                    pasaFiltro = false;
                }
                if (pasaFiltro && tipoActividadFiltro != null && !tipoActividadFiltro.isEmpty()) {
                    if (act.getTipoActividad() == null || !act.getTipoActividad().toLowerCase().contains(tipoActividadFiltro.toLowerCase())) {
                        pasaFiltro = false;
                    }
                }
                if (pasaFiltro && fechaDesdeFiltro != null) {
                    if (act.getFechaActividad() == null || act.getFechaActividad().before(fechaDesdeFiltro)) {
                        // Para comparar solo la fecha, ignorando la hora si fechaDesdeFiltro no tiene hora
                        Calendar calAct = Calendar.getInstance(); calAct.setTime(act.getFechaActividad());
                        Calendar calDesde = Calendar.getInstance(); calDesde.setTime(fechaDesdeFiltro);
                        if(calAct.get(Calendar.YEAR) < calDesde.get(Calendar.YEAR) ||
                           (calAct.get(Calendar.YEAR) == calDesde.get(Calendar.YEAR) && calAct.get(Calendar.DAY_OF_YEAR) < calDesde.get(Calendar.DAY_OF_YEAR))){
                            pasaFiltro = false;
                        }
                    }
                }
                if (pasaFiltro && fechaHastaFiltro != null) {
                     if (act.getFechaActividad() == null || act.getFechaActividad().after(fechaHastaFiltro)) {
                        Calendar calAct = Calendar.getInstance(); calAct.setTime(act.getFechaActividad());
                        Calendar calHasta = Calendar.getInstance(); calHasta.setTime(fechaHastaFiltro);
                         if(calAct.get(Calendar.YEAR) > calHasta.get(Calendar.YEAR) ||
                           (calAct.get(Calendar.YEAR) == calHasta.get(Calendar.YEAR) && calAct.get(Calendar.DAY_OF_YEAR) > calHasta.get(Calendar.DAY_OF_YEAR))){
                            pasaFiltro = false;
                        }
                    }
                }

                if (pasaFiltro) {
                    actividadesFiltradas.add(act);
                }
            }
        }
        
        tableModel.setRowCount(0); 
        if (!actividadesFiltradas.isEmpty()) {
            for (ActividadVoluntariado act : actividadesFiltradas) {
                String nombreVoluntario = cacheNombresVoluntarios.computeIfAbsent(act.getIdVoluntario(), id -> {
                    Usuario u = usuarioController.obtenerUsuarioPorId(id);
                    return u != null ? u.getNombreCompleto() : "ID: " + id;
                });
                String nombreAnimal = "N/A";
                if (act.getIdAnimalAsociado() != null && act.getIdAnimalAsociado() != 0) { // Comprobar que no sea 0 si eso indica null en tu BD
                     nombreAnimal = cacheNombresAnimales.computeIfAbsent(act.getIdAnimalAsociado(), id -> {
                        Animal a = animalController.obtenerDetallesAnimal(id);
                        return a != null ? a.getNombre() : "ID Animal: " + id;
                    });
                }

                String descCorta = act.getDescripcion();
                if (descCorta != null && descCorta.length() > 50) {
                    descCorta = descCorta.substring(0, 47) + "...";
                }

                tableModel.addRow(new Object[]{
                        act.getId(),
                        act.getFechaActividad() != null ? sdfTablaDisplay.format(act.getFechaActividad()) : "N/A",
                        nombreVoluntario, 
                        (act.getIdAnimalAsociado() != null && act.getIdAnimalAsociado() != 0) ? nombreAnimal : "Ninguno",
                        act.getTipoActividad(),
                        act.getDuracionHoras(),
                        descCorta != null ? descCorta : ""
                });
            }
        }
        actualizarEstadoBotonesAccion();
    }
    
    private void aplicarFiltros(ActionEvent e) {
        Integer idVoluntario = null;
        if (!filtroVoluntarioIdField.getText().trim().isEmpty()) {
            try {
                idVoluntario = Integer.parseInt(filtroVoluntarioIdField.getText().trim());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "ID de Voluntario debe ser numérico.", "Error de Filtro", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
        String tipoActividad = filtroTipoActividadField.getText().trim();
        Date fechaDesde = parseDateFromManualFieldsFilter(filtroFechaDesdeDia, filtroFechaDesdeMes, filtroFechaDesdeAnio, "Fecha Desde");
        Date fechaHasta = parseDateFromManualFieldsFilter(filtroFechaHastaDia, filtroFechaHastaMes, filtroFechaHastaAnio, "Fecha Hasta");

        // Si hubo error en parseo de fechas (y no fue porque estaban vacías/placeholders para no filtrar), no continuar
        if ((!filtroFechaDesdeDia.getText().trim().equals("dd") && fechaDesde == null) || 
            (!filtroFechaHastaDia.getText().trim().equals("dd") && fechaHasta == null) ) {
            // No hacer nada si el parseo de fecha falló y el usuario ya fue notificado
             if (! ( (filtroFechaDesdeDia.getText().trim().isEmpty() || filtroFechaDesdeDia.getText().trim().equals("dd")) &&
                     (filtroFechaDesdeMes.getText().trim().isEmpty() || filtroFechaDesdeMes.getText().trim().equals("mm")) &&
                     (filtroFechaDesdeAnio.getText().trim().isEmpty() || filtroFechaDesdeAnio.getText().trim().equals("aaaa")) ) && fechaDesde == null ) {
                 return;
             }
             if (! ( (filtroFechaHastaDia.getText().trim().isEmpty() || filtroFechaHastaDia.getText().trim().equals("dd")) &&
                     (filtroFechaHastaMes.getText().trim().isEmpty() || filtroFechaHastaMes.getText().trim().equals("mm")) &&
                     (filtroFechaHastaAnio.getText().trim().isEmpty() || filtroFechaHastaAnio.getText().trim().equals("aaaa")) ) && fechaHasta == null ) {
                 return;
             }
        }


        if ((fechaDesde != null && fechaHasta != null && fechaDesde.after(fechaHasta))) {
            JOptionPane.showMessageDialog(this, "La 'Fecha Desde' no puede ser posterior a la 'Fecha Hasta'.", "Error de Filtro de Fechas", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        cargarActividadesEnTabla(idVoluntario, tipoActividad.isEmpty() ? null : tipoActividad, fechaDesde, fechaHasta);
    }

    private void limpiarFiltros(ActionEvent e) {
        filtroVoluntarioIdField.setText("");
        filtroTipoActividadField.setText("");
        addPlaceholderFocusListener(filtroFechaDesdeDia, "dd");
        addPlaceholderFocusListener(filtroFechaDesdeMes, "mm");
        addPlaceholderFocusListener(filtroFechaDesdeAnio, "aaaa");
        addPlaceholderFocusListener(filtroFechaHastaDia, "dd");
        addPlaceholderFocusListener(filtroFechaHastaMes, "mm");
        addPlaceholderFocusListener(filtroFechaHastaAnio, "aaaa");
        aplicarFiltros(null);
    }

    private ActividadVoluntariado getActividadSeleccionadaParaAccion(boolean completa) {
        int selectedRow = tablaActividades.getSelectedRow();
        if (selectedRow != -1) {
            int modelRow = tablaActividades.convertRowIndexToModel(selectedRow);
            int actividadId = (Integer) tableModel.getValueAt(modelRow, 0); 
            if (completa) {
                // El VoluntariadoController debería tener un método para buscar por ID
                ActividadVoluntariado act = voluntariadoController.obtenerActividadPorId(actividadId);
                if(act == null){
                     JOptionPane.showMessageDialog(this, "No se pudo obtener los detalles completos de la actividad seleccionada.", "Error", JOptionPane.ERROR_MESSAGE);
                }
                return act;
            } else {
                ActividadVoluntariado tempAct = new ActividadVoluntariado();
                tempAct.setId(actividadId);
                // Para eliminar, a veces solo el ID es suficiente si el controller lo maneja así.
                // Pero para ser más robusto, obtener el objeto completo es mejor para permisos.
                return tempAct; // O llamar a obtenerActividadPorId(actividadId) también aquí.
            }
        }
        return null;
    }

    private void abrirFormularioActividadParaCrear(ActionEvent e) {
        System.out.println("GEST_ACT_VOL_DIALOG: Abriendo formulario para crear actividad.");
        FormularioActividadVoluntariadoDialog formDialog = new FormularioActividadVoluntariadoDialog(
            this, voluntariadoController, usuarioController, animalController, null);
        formDialog.setVisible(true);
        if (formDialog.isGuardadoExitoso()) {
            aplicarFiltros(null);
        }
    }

    private void editarActividadSeleccionada(ActionEvent e) {
        ActividadVoluntariado actividad = getActividadSeleccionadaParaAccion(true); // Obtener objeto completo
        if (actividad != null) {
            System.out.println("GEST_ACT_VOL_DIALOG: Abriendo formulario para editar actividad ID: " + actividad.getId());
            FormularioActividadVoluntariadoDialog formDialog = new FormularioActividadVoluntariadoDialog(
                this, voluntariadoController, usuarioController, animalController, actividad);
            formDialog.setVisible(true);
            if (formDialog.isGuardadoExitoso()) {
                aplicarFiltros(null);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Seleccione una actividad de la lista para editar o no tiene permiso.", "Selección Requerida / Permiso", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void eliminarActividadSeleccionada(ActionEvent e) {
        ActividadVoluntariado actividad = getActividadSeleccionadaParaAccion(false); // Solo necesitamos el ID para el controller
        if (actividad != null) {
            // La verificación de si el usuario puede eliminar esta actividad la hace el controller
            int confirm = JOptionPane.showConfirmDialog(this, 
                "¿Está seguro que desea ELIMINAR la actividad ID: " + actividad.getId() + "?",
                "Confirmar Eliminación", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (confirm == JOptionPane.YES_OPTION) {
                System.out.println("GEST_ACT_VOL_DIALOG: Intentando eliminar actividad ID: " + actividad.getId());
                if (voluntariadoController.eliminarActividad(actividad.getId())) {
                    JOptionPane.showMessageDialog(this, "Actividad eliminada con éxito.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    aplicarFiltros(null);
                } else {
                    JOptionPane.showMessageDialog(this, "Error al eliminar la actividad. Verifique la consola para más detalles.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Seleccione una actividad de la lista para eliminar o no tiene permiso.", "Selección Requerida / Permiso", JOptionPane.WARNING_MESSAGE);
        }
    }
}