package Main;

import controllers.*; // Importar todos los controladores del paquete
import model.Usuario;
import repository.Conexion; // Para cerrar la conexión al salir

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection; // Para el test de conexión inicial
import java.sql.SQLException;

public class MainGui extends JFrame {

    private CardLayout cardLayout;
    private JPanel cardPanel;
    private JPanel loginRegisterPanel;
    private JPanel mainMenuPanel;

    // Componentes de Login
    private JTextField usernameField;
    private JPasswordField passwordField;

    // Componentes del Menú Principal
    private JLabel welcomeLabel;
    // Botones de funcionalidades
    private JButton miPerfilButton;
    private JButton gestionUsuariosButton;
    private JButton gestionarAnimalesButton;
    private JButton catalogoAnimalesButton;
    private JButton misSolicitudesButton;
    private JButton gestionarSolicitudesButton;
    private JButton registrarActividadVoluntariadoButton;
    private JButton gestionarActividadesVoluntariadoButton;
    private JButton realizarDonacionButton;
    private JButton gestionarDonacionesButton;
    private JButton verEventosButton;
    private JButton gestionarEventosButton;

    // Controladores (instanciados en el constructor)
    private AuthController authController;
    private UsuarioController usuarioController;
    private AnimalController animalController;
    private EventoController eventoController;
    private SolicitudAdopcionController solicitudAdopcionController;
    private DonacionController donacionController;
    private VoluntariadoController voluntariadoController;


    private static final String LOGIN_PANEL = "LoginRegisterPanel";
    private static final String MAIN_MENU_PANEL = "MainMenuPanel";

    public MainGui() {
        // Instanciar controladores
        authController = new AuthController();
        usuarioController = new UsuarioController();
        animalController = new AnimalController();
        eventoController = new EventoController();
        solicitudAdopcionController = new SolicitudAdopcionController();
        donacionController = new DonacionController();
        voluntariadoController = new VoluntariadoController();

        setTitle("Amigos Peludos - Gestión [MODO TEXTO PLANO]");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                handleExit();
            }
        });

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        createLoginRegisterPanel();
        createMainMenuPanel();

        cardPanel.add(loginRegisterPanel, LOGIN_PANEL);
        cardPanel.add(mainMenuPanel, MAIN_MENU_PANEL);

        add(cardPanel);
        cardLayout.show(cardPanel, LOGIN_PANEL);
        
        // Ajustar tamaño para acomodar todos los botones del menú
        setMinimumSize(new Dimension(700, 700)); 
        pack(); 
        setLocationRelativeTo(null);
    }
    
    // Método para ser llamado desde PerfilUsuarioDialog si el nombre del usuario cambia
    public void actualizarWelcomeLabel() {
        if (AuthController.isLoggedIn()) {
            Usuario currentUser = AuthController.getUsuarioActual();
            if (currentUser != null) {
                 welcomeLabel.setText("Bienvenido, " + currentUser.getNombreCompleto() + " (Rol: " + currentUser.getRol() + ")");
            }
        } else {
            welcomeLabel.setText("Bienvenido!");
        }
    }

    // Método para ser llamado desde PerfilUsuarioDialog si el usuario se da de baja
    public void forzarLogoutYMostrarLogin() {
        handleLogout();
    }

    private void createLoginRegisterPanel() {
        loginRegisterPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("Bienvenido a Amigos Peludos", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        loginRegisterPanel.add(titleLabel, gbc);

        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        
        JLabel userLabel = new JLabel("Username:");
        gbc.gridx = 0; gbc.gridy = 1;
        loginRegisterPanel.add(userLabel, gbc);

        usernameField = new JTextField(20);
        gbc.gridx = 1; gbc.gridy = 1; gbc.anchor = GridBagConstraints.WEST;
        loginRegisterPanel.add(usernameField, gbc);

        JLabel passLabel = new JLabel("Password:");
        gbc.gridx = 0; gbc.gridy = 2; gbc.anchor = GridBagConstraints.EAST;
        loginRegisterPanel.add(passLabel, gbc);

        passwordField = new JPasswordField(20);
        gbc.gridx = 1; gbc.gridy = 2; gbc.anchor = GridBagConstraints.WEST;
        loginRegisterPanel.add(passwordField, gbc);
        
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;
        
        JButton loginButtonLocal = new JButton("Login");
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        loginRegisterPanel.add(loginButtonLocal, gbc);

        JButton registerButtonPromptLocal = new JButton("Registrarse");
        gbc.gridx = 0; gbc.gridy = 4;
        loginRegisterPanel.add(registerButtonPromptLocal, gbc);

        loginButtonLocal.addActionListener(e -> handleLogin());
        registerButtonPromptLocal.addActionListener(e -> {
            System.out.println("GUI: [TEXTO PLANO] Botón 'Registrarse' presionado.");
            RegistroDialog registroDialog = new RegistroDialog(MainGui.this, authController);
            registroDialog.setVisible(true);
        });
    }

    private void createMainMenuPanel() {
        mainMenuPanel = new JPanel(new BorderLayout(10, 10));
        mainMenuPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        welcomeLabel = new JLabel("Bienvenido!", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        mainMenuPanel.add(welcomeLabel, BorderLayout.NORTH);

        JPanel funcionalidadesPanel = new JPanel();
        funcionalidadesPanel.setLayout(new BoxLayout(funcionalidadesPanel, BoxLayout.Y_AXIS));
        funcionalidadesPanel.setBorder(BorderFactory.createTitledBorder("Menú de Opciones"));
        
        // --- Creación de TODOS los Botones ---
        miPerfilButton = createMenuButton("Mi Perfil");
        catalogoAnimalesButton = createMenuButton("Catálogo de Animales y Adopción");
        misSolicitudesButton = createMenuButton("Mis Solicitudes de Adopción");
        registrarActividadVoluntariadoButton = createMenuButton("Registrar Actividad de Voluntariado"); // Botón general
        realizarDonacionButton = createMenuButton("Realizar una Donación");
        verEventosButton = createMenuButton("Ver Próximos Eventos");
        
        gestionUsuariosButton = createMenuButton("GESTIÓN: Usuarios");
        gestionarAnimalesButton = createMenuButton("GESTIÓN: Animales");
        gestionarEventosButton = createMenuButton("GESTIÓN: Eventos"); 
        gestionarSolicitudesButton = createMenuButton("GESTIÓN: Solicitudes de Adopción");
        gestionarActividadesVoluntariadoButton = createMenuButton("GESTIÓN: Actividades de Voluntariado");
        gestionarDonacionesButton = createMenuButton("GESTIÓN: Donaciones");       

        // --- Añadir TODOS los Botones al Panel ---
        funcionalidadesPanel.add(miPerfilButton);
        funcionalidadesPanel.add(Box.createRigidArea(new Dimension(0, 5))); 
        funcionalidadesPanel.add(catalogoAnimalesButton);
        funcionalidadesPanel.add(misSolicitudesButton);
        funcionalidadesPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        funcionalidadesPanel.add(registrarActividadVoluntariadoButton); 
        funcionalidadesPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        funcionalidadesPanel.add(realizarDonacionButton);
        funcionalidadesPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        funcionalidadesPanel.add(verEventosButton);
        
        funcionalidadesPanel.add(Box.createRigidArea(new Dimension(0, 15))); 
        JSeparator separator = new JSeparator();
        separator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1)); 
        funcionalidadesPanel.add(separator);
        funcionalidadesPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        funcionalidadesPanel.add(gestionUsuariosButton);
        funcionalidadesPanel.add(gestionarAnimalesButton);
        funcionalidadesPanel.add(gestionarEventosButton); 
        funcionalidadesPanel.add(gestionarSolicitudesButton);
        funcionalidadesPanel.add(gestionarActividadesVoluntariadoButton); 
        funcionalidadesPanel.add(gestionarDonacionesButton);

        // --- Action Listeners (Llamando a los diálogos implementados y placeholders) ---
        miPerfilButton.addActionListener(e -> {
            if (AuthController.isLoggedIn()) {
                Usuario currentUser = AuthController.getUsuarioActual();
                if (currentUser != null) {
                    PerfilUsuarioDialog perfilDialog = new PerfilUsuarioDialog(this, usuarioController, currentUser);
                    perfilDialog.setVisible(true);
                    actualizarWelcomeLabel(); 
                }
            } else { showErrorPermisoLogin(); }
        });

        catalogoAnimalesButton.addActionListener(e -> {
            if (AuthController.isLoggedIn()) {
                CatalogoAnimalesDialog cad = new CatalogoAnimalesDialog(this, animalController, solicitudAdopcionController);
                cad.setVisible(true);
            } else { showErrorPermisoLogin(); }
        });
        
        misSolicitudesButton.addActionListener(e -> {
            if(AuthController.isAdoptante()){
                // new MisSolicitudesDialog(this, solicitudAdopcionController).setVisible(true); // TODO
                JOptionPane.showMessageDialog(this, "FUNCIONALIDAD: Abrir Diálogo Mis Solicitudes (Adoptante)");
            } else if (!AuthController.isLoggedIn()) { showErrorPermisoLogin(); }
            else { showErrorPermiso(); }
        });

        // Botón para registrar actividad (Voluntario o Admin para otros)
        registrarActividadVoluntariadoButton.addActionListener(e -> {
            System.out.println("GUI: [TEXTO PLANO] Botón 'Registrar Actividad de Voluntariado' presionado.");
            if(AuthController.isVoluntario() || AuthController.isAdmin()){
                FormularioActividadVoluntariadoDialog formDialog = new FormularioActividadVoluntariadoDialog(
                        this, voluntariadoController, usuarioController, animalController, null); // null para nueva actividad
                formDialog.setVisible(true);
                // Si se abre desde aquí, el refresco de la tabla de gestión no es inmediato.
                // La tabla en GestionActividadesVoluntariadoDialog se refrescará cuando se abra.
            } else if (!AuthController.isLoggedIn()) { showErrorPermisoLogin(); }
            else { showErrorPermiso(); }
        });

         realizarDonacionButton.addActionListener(e -> {
            RealizarDonacionDialog rdDialog = new RealizarDonacionDialog(this, donacionController);
            rdDialog.setVisible(true);
            // Opcional: if (rdDialog.isDonacionRealizada()) { /* Alguna acción de feedback */ }
        });

         verEventosButton.addActionListener(e -> { 
            if (AuthController.isLoggedIn()) {
                 GestionEventosDialog geDialog = new GestionEventosDialog(this, eventoController, usuarioController);
                 geDialog.setTitle("Ver Eventos [MODO TEXTO PLANO]"); 
                 // En un escenario real, podrías pasar un flag para modo "solo lectura"
                 // o tener un diálogo separado más simple para solo ver.
                 geDialog.setVisible(true);
            } else { showErrorPermisoLogin(); }
        });

        // --- Action Listeners para Botones de GESTIÓN ---
        gestionUsuariosButton.addActionListener(e -> {
            if(AuthController.isAdmin()) {
                GestionUsuariosDialog guDialog = new GestionUsuariosDialog(this, usuarioController);
                guDialog.setVisible(true);
            } else { showErrorPermiso(); }
        });

        gestionarAnimalesButton.addActionListener(e -> {
            if(AuthController.isEmpleado()) { // Incluye Admin
                GestionAnimalesDialog gaDialog = new GestionAnimalesDialog(this, animalController, usuarioController);
                gaDialog.setVisible(true);
            } else { showErrorPermiso(); }
        });

        gestionarEventosButton.addActionListener(e -> {
            if(AuthController.isEmpleado()) { // Incluye Admin
                GestionEventosDialog geDialog = new GestionEventosDialog(this, eventoController, usuarioController);
                geDialog.setVisible(true);
            } else { showErrorPermiso(); }
        });
        
        gestionarSolicitudesButton.addActionListener(e -> {
            if(AuthController.isEmpleado()) { // Incluye Admin
                GestionSolicitudesDialog gsDialog = new GestionSolicitudesDialog(this, solicitudAdopcionController, usuarioController, animalController);
                gsDialog.setVisible(true);
            } else { showErrorPermiso(); }
        });

        // Botón para que el ADMIN gestione TODAS las actividades de voluntariado
        gestionarActividadesVoluntariadoButton.addActionListener(e -> {
             System.out.println("GUI: [TEXTO PLANO] Botón 'GESTIÓN: Actividades de Voluntariado' presionado.");
             if(AuthController.isAdmin()){
                GestionActividadesVoluntariadoDialog gavDialog = 
                    new GestionActividadesVoluntariadoDialog(this, voluntariadoController, usuarioController, animalController);
                gavDialog.setVisible(true);
             } else { showErrorPermiso(); }
        });

        gestionarDonacionesButton.addActionListener(e -> {
            if(AuthController.isAdmin()){
                GestionDonacionesDialog gdDialog = new GestionDonacionesDialog(this, donacionController, usuarioController);
                gdDialog.setVisible(true);
            } else { showErrorPermiso(); }
        });

        mainMenuPanel.add(new JScrollPane(funcionalidadesPanel), BorderLayout.CENTER);

        JButton logoutButtonLocal = new JButton("Logout");
        logoutButtonLocal.addActionListener(e -> handleLogout());
        JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        southPanel.add(logoutButtonLocal);
        mainMenuPanel.add(southPanel, BorderLayout.SOUTH);

        updateMainMenuBasedOnRole(null); // Configurar visibilidad inicial
    }
    
    private JButton createMenuButton(String text) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.LEFT_ALIGNMENT); // Para BoxLayout
        // Hacer que los botones ocupen un ancho más consistente y tengan un poco más de padding
        button.setPreferredSize(new Dimension(350, 30)); // Ajusta el ancho (350) como necesites
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35)); // Permitir que se estire verticalmente un poco
        button.setMargin(new Insets(5,10,5,10)); 
        return button;
    }
    
    private void showErrorPermiso(){
        JOptionPane.showMessageDialog(this, "No tiene los permisos necesarios para acceder a esta función.", "Acceso Denegado", JOptionPane.ERROR_MESSAGE);
    }

    private void showErrorPermisoLogin(){
        JOptionPane.showMessageDialog(this, "Debe iniciar sesión para acceder a esta función.", "Inicio de Sesión Requerido", JOptionPane.WARNING_MESSAGE);
    }

    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        System.out.println("GUI: [TEXTO PLANO] Intentando login con Username: '" + username + "', Password: '" + password.replaceAll(".", "*") + "'");

        if (authController.login(username, password)) {
            Usuario currentUser = AuthController.getUsuarioActual();
            welcomeLabel.setText("Bienvenido, " + currentUser.getNombreCompleto() + " (Rol: " + currentUser.getRol() + ")");
            updateMainMenuBasedOnRole(currentUser);
            cardLayout.show(cardPanel, MAIN_MENU_PANEL);
            clearLoginFields();
        } else {
            JOptionPane.showMessageDialog(this, "Username o password incorrecto, o cuenta inactiva.", "Error de Login", JOptionPane.ERROR_MESSAGE);
            passwordField.setText("");
            passwordField.requestFocus();
        }
    }

    private void updateMainMenuBasedOnRole(Usuario currentUser) {
        boolean isLoggedIn = AuthController.isLoggedIn();
        boolean isAdmin = AuthController.isAdmin();
        boolean isEmpleado = AuthController.isEmpleado();
        boolean isVoluntario = AuthController.isVoluntario();
        boolean isAdoptante = AuthController.isAdoptante();

        // Visibilidad de botones según rol
        miPerfilButton.setVisible(isLoggedIn);
        catalogoAnimalesButton.setVisible(isLoggedIn); 
        misSolicitudesButton.setVisible(isAdoptante);
        registrarActividadVoluntariadoButton.setVisible(isVoluntario || isAdmin); // Voluntario o Admin
        realizarDonacionButton.setVisible(true); // Todos pueden intentar donar
        verEventosButton.setVisible(isLoggedIn);

        // Funciones de Gestión
        gestionUsuariosButton.setVisible(isAdmin);
        gestionarAnimalesButton.setVisible(isEmpleado); // Incluye Admin
        gestionarEventosButton.setVisible(isEmpleado); // Incluye Admin
        gestionarSolicitudesButton.setVisible(isEmpleado); // Incluye Admin
        gestionarActividadesVoluntariadoButton.setVisible(isAdmin); // Solo Admin gestiona todas
        gestionarDonacionesButton.setVisible(isAdmin); // Solo Admin gestiona todas
        
        mainMenuPanel.revalidate();
        mainMenuPanel.repaint();
    }

    private void handleLogout() {
        System.out.println("GUI: [TEXTO PLANO] Botón 'Logout' presionado.");
        authController.logout();
        updateMainMenuBasedOnRole(null); // Ocultar/deshabilitar botones específicos de rol
        cardLayout.show(cardPanel, LOGIN_PANEL); // Volver al panel de login
        welcomeLabel.setText("Bienvenido!"); // Resetear etiqueta
        usernameField.setText(""); 
        passwordField.setText("");
        usernameField.requestFocusInWindow(); // Poner foco en username para nuevo login
    }

    private void clearLoginFields() {
        // No limpiar username para comodidad si falla el password
        passwordField.setText("");//hj
    }

    private void handleExit() {
        int response = JOptionPane.showConfirmDialog(this,
                "¿Está seguro que desea salir de la aplicación?", "Confirmar Salida", 
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (response == JOptionPane.YES_OPTION) {
            System.out.println("GUI: [TEXTO PLANO] Saliendo de la aplicación...");
            Conexion.closeConnection(); // Cerrar conexión a BD
            System.exit(0); // Terminar la aplicación
        } else {
            System.out.println("GUI: [TEXTO PLANO] Salida cancelada por el usuario.");
        }
    }

    public static void main(String[] args) {
        System.out.println("MAIN_GUI: [TEXTO PLANO] Iniciando aplicación...");
        try {
            // Usar el LookAndFeel del sistema para una apariencia más nativa
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("MAIN_GUI: [TEXTO PLANO] Error al establecer Look and Feel del sistema: " + e.getMessage());
        }

        SwingUtilities.invokeLater(() -> { // Ejecutar la creación de la GUI en el Event Dispatch Thread
            System.out.println("MAIN_GUI: [TEXTO PLANO] Creando GUI en EDT.");
            
            boolean dbConnected = false;
            Connection testConn = null;
            try {
                testConn = Conexion.getConnection(); // Intenta obtener una conexión
                if (testConn != null && !testConn.isClosed()) {
                    dbConnected = true;
                    System.out.println("MAIN_GUI: [TEXTO PLANO] Conexión inicial a la BD exitosa.");
                } else {
                     System.err.println("MAIN_GUI: [TEXTO PLANO] Conexion.getConnection() devolvió null o una conexión cerrada al inicio.");
                }
            } catch (SQLException e) { // Por si isClosed() lanza SQLException
                System.err.println("MAIN_GUI: [TEXTO PLANO] SQLException al probar conexión inicial: " + e.getMessage());
            }
            
            if (!dbConnected) {
                 int choice = JOptionPane.showConfirmDialog(null, 
                    "No se pudo conectar a la base de datos al iniciar.\n" +
                    "Verifique la configuración y que el servidor MySQL esté en ejecución.\n\n" +
                    "¿Desea intentar continuar de todas formas?\n" +
                    "(La aplicación podría no funcionar correctamente).", 
                    "Error de Conexión Crítico", 
                    JOptionPane.YES_NO_OPTION, 
                    JOptionPane.ERROR_MESSAGE);
                if (choice == JOptionPane.NO_OPTION || choice == JOptionPane.CLOSED_OPTION) {
                    System.err.println("MAIN_GUI: [TEXTO PLANO] Fallo crítico al obtener conexión inicial y el usuario eligió salir. Saliendo.");
                    System.exit(1); // Salir de la aplicación
                }
                System.out.println("MAIN_GUI: [TEXTO PLANO] Usuario eligió continuar a pesar del fallo de conexión inicial.");
            }
            
            new MainGui().setVisible(true); // Crear y mostrar la ventana principal
            System.out.println("MAIN_GUI: [TEXTO PLANO] GUI visible.");
        });
    }
}