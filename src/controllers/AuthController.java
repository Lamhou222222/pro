package controllers;

import model.Usuario;
import repository.UsuarioRepository; // Usará la versión de texto plano

public class AuthController {
    private UsuarioRepository usuarioRepository;
    private static Usuario usuarioActual = null;

    public AuthController() {
        this.usuarioRepository = new UsuarioRepository();
    }

    public boolean login(String username, String password) {
        System.out.println("AUTH_CTRL: [TEXTO PLANO] Iniciando login para username: '" + username + "'");
        if (username == null || username.trim().isEmpty() || password == null || password.isEmpty()) {
            System.out.println("AUTH_CTRL: [TEXTO PLANO] Username o password vacíos.");
            return false;
        }

        Usuario usuario = usuarioRepository.buscarPorUsername(username.trim());

        if (usuario == null) {
            System.out.println("AUTH_CTRL: [TEXTO PLANO] Usuario '" + username + "' NO encontrado.");
            return false;
        }
        System.out.println("AUTH_CTRL: [TEXTO PLANO] Usuario encontrado: " + usuario.getUsername() + ", Activo: " + usuario.isActivo() + ", Rol: " + usuario.getRol());
        // No mostrar la contraseña en texto plano en los logs por mínima precaución, incluso en debug.
        // System.out.println("AUTH_CTRL: [TEXTO PLANO] Password en BD para '" + username + "': '" + usuario.getPasswordHash() + "'");

        if (!usuario.isActivo()) {
            System.out.println("AUTH_CTRL: [TEXTO PLANO] Usuario '" + username + "' no está activo.");
            return false;
        }

        System.out.println("AUTH_CTRL: [TEXTO PLANO] Verificando password para '" + username + "'...");
        boolean passwordMatches = usuarioRepository.verificarPassword(password, usuario.getPasswordHash());
        // El repositorio ya imprime el resultado de la comparación

        if (passwordMatches) {
            usuarioActual = usuario;
            System.out.println("AUTH_CTRL: [TEXTO PLANO] Login EXITOSO para: " + usuario.getUsername());
            return true;
        } else {
            System.out.println("AUTH_CTRL: [TEXTO PLANO] Password NO COINCIDE para: " + username);
            return false;
        }
    }

    public boolean registrar(Usuario nuevoUsuario, String rawPassword) {
        System.out.println("AUTH_CTRL: [TEXTO PLANO] Iniciando registro para username: '" + nuevoUsuario.getUsername() + "'");
        if (rawPassword == null || rawPassword.isEmpty()) {
            System.out.println("AUTH_CTRL: [TEXTO PLANO] Contraseña vacía, no se puede registrar.");
            return false;
        }
        // El repositorio guardará la contraseña en texto plano.
        // Se pasa la contraseña en crudo al campo passwordHash del objeto Usuario temporalmente.
        nuevoUsuario.setPasswordHash(rawPassword); 
        
        Usuario registrado = usuarioRepository.registrarUsuario(nuevoUsuario);
        if (registrado != null) {
            System.out.println("AUTH_CTRL: [TEXTO PLANO] Registro exitoso para: " + registrado.getUsername() + ". Su cuenta está pendiente de activación.");
            return true;
        }
        System.out.println("AUTH_CTRL: [TEXTO PLANO] Falla en el registro para username: '" + nuevoUsuario.getUsername() + "' (ver logs del repositorio).");
        return false;
    }

    public static void logout() {
        System.out.println("AUTH_CTRL: [TEXTO PLANO] Logout. Usuario actual era: " + (usuarioActual != null ? usuarioActual.getUsername() : "ninguno"));
        usuarioActual = null;
    }

    public static Usuario getUsuarioActual() { return usuarioActual; }
    public static boolean isLoggedIn() { return usuarioActual != null; }
    public static boolean isAdmin() { return isLoggedIn() && usuarioActual.getRol() == Usuario.RolUsuario.ADMINISTRADOR; }
    public static boolean isEmpleado() { return isLoggedIn() && (usuarioActual.getRol() == Usuario.RolUsuario.EMPLEADO || isAdmin()); }
    public static boolean isVoluntario() { return isLoggedIn() && usuarioActual.getRol() == Usuario.RolUsuario.VOLUNTARIO; }
    public static boolean isAdoptante() { return isLoggedIn() && usuarioActual.getRol() == Usuario.RolUsuario.ADOPTANTE_POTENCIAL; }
}