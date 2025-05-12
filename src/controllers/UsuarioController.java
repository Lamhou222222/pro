package controllers;

import model.Usuario;
import repository.UsuarioRepository; // Asume que este es el UsuarioRepository para TEXTO PLANO
import java.util.List;
import java.util.ArrayList;

public class UsuarioController {
    private UsuarioRepository usuarioRepository;
    private final String LOG_PREFIX = "USER_CTRL: [TEXTO PLANO] ";

    public UsuarioController() {
        this.usuarioRepository = new UsuarioRepository();
        System.out.println(LOG_PREFIX + "Controlador de Usuario inicializado.");
    }

    public boolean actualizarPerfil(Usuario usuarioConNuevosDatos) {
        System.out.println(LOG_PREFIX + "Solicitud para actualizar perfil del usuario ID: " + usuarioConNuevosDatos.getId());
        Usuario currentUser = AuthController.getUsuarioActual();

        if (!AuthController.isLoggedIn()) {
            System.out.println(LOG_PREFIX + "Error - No hay usuario logueado para actualizar perfil.");
            return false;
        }
        if (usuarioConNuevosDatos.getId() <= 0) {
            System.out.println(LOG_PREFIX + "Error - El usuario a actualizar no tiene un ID válido.");
            return false;
        }

        boolean tienePermisoParaGuardar = false;
        Usuario usuarioOriginalParaValidaciones = usuarioRepository.buscarPorId(usuarioConNuevosDatos.getId());
        if (usuarioOriginalParaValidaciones == null) {
            System.out.println(LOG_PREFIX + "Error - Usuario a actualizar (ID: " + usuarioConNuevosDatos.getId() + ") no encontrado en BD.");
            return false;
        }

        if (currentUser.getId() == usuarioConNuevosDatos.getId()) { // Usuario actualizando su propio perfil
            tienePermisoParaGuardar = true;
            System.out.println(LOG_PREFIX + "Usuario ID: " + currentUser.getId() + " actualizando su propio perfil.");
            usuarioConNuevosDatos.setRol(currentUser.getRol()); // No puede cambiar su propio rol
            usuarioConNuevosDatos.setActivo(currentUser.isActivo()); // No puede cambiar su propio estado activo
        } else if (AuthController.isAdmin()) { // Admin actualizando el perfil de otro
            tienePermisoParaGuardar = true;
            System.out.println(LOG_PREFIX + "Admin ID: " + currentUser.getId() + " actualizando perfil de usuario ID: " + usuarioConNuevosDatos.getId());
            // Restricciones para Admin: No puede degradar/inactivar a otro Admin desde PerfilUsuarioDialog
            if (usuarioOriginalParaValidaciones.getRol() == Usuario.RolUsuario.ADMINISTRADOR) {
                if (usuarioConNuevosDatos.getRol() != Usuario.RolUsuario.ADMINISTRADOR) {
                    System.out.println(LOG_PREFIX + "Admin no puede cambiar el rol de otro Admin a no-Admin desde PerfilUsuarioDialog. Se revierte.");
                    usuarioConNuevosDatos.setRol(Usuario.RolUsuario.ADMINISTRADOR);
                }
                if (!usuarioConNuevosDatos.isActivo()) {
                    System.out.println(LOG_PREFIX + "Admin no puede INACTIVAR a otro Admin desde PerfilUsuarioDialog. Se revierte.");
                    usuarioConNuevosDatos.setActivo(true);
                }
            }
        }

        if (tienePermisoParaGuardar) {
            // El UsuarioRepository.actualizarUsuario NO debe tocar password_hash
            boolean exito = usuarioRepository.actualizarUsuario(usuarioConNuevosDatos);
            if (exito) {
                System.out.println(LOG_PREFIX + "Perfil de usuario ID: " + usuarioConNuevosDatos.getId() + " actualizado en BD.");
                if (currentUser.getId() == usuarioConNuevosDatos.getId()) { // Si actualizó su propio perfil
                    actualizarUsuarioEnSesion(usuarioConNuevosDatos); // Actualizar datos en AuthController
                }
            } else {
                System.out.println(LOG_PREFIX + "Fallo al actualizar perfil de usuario ID: " + usuarioConNuevosDatos.getId() + " en BD.");
            }
            return exito;
        }
        
        System.out.println(LOG_PREFIX + "Permiso DENEGADO para actualizar perfil ID: " + usuarioConNuevosDatos.getId());
        return false;
    }

    private void actualizarUsuarioEnSesion(Usuario datosNuevos) {
        Usuario usuarioEnSesion = AuthController.getUsuarioActual();
        if (usuarioEnSesion != null && usuarioEnSesion.getId() == datosNuevos.getId()) {
            usuarioEnSesion.setNombreCompleto(datosNuevos.getNombreCompleto());
            usuarioEnSesion.setEmail(datosNuevos.getEmail());
            usuarioEnSesion.setTelefono(datosNuevos.getTelefono());
            // Mantener el rol y activo del usuario en sesión, ya que no se cambian por el propio usuario
            // usuarioEnSesion.setRol(datosNuevos.getRol()); 
            // usuarioEnSesion.setActivo(datosNuevos.isActivo());
            usuarioEnSesion.setDisponibilidadHoraria(datosNuevos.getDisponibilidadHoraria());
            usuarioEnSesion.setAreasInteres(datosNuevos.getAreasInteres());
            usuarioEnSesion.setDireccion(datosNuevos.getDireccion());
            usuarioEnSesion.setTipoVivienda(datosNuevos.getTipoVivienda());
            usuarioEnSesion.setExperienciaAnimales(datosNuevos.getExperienciaAnimales());
            System.out.println(LOG_PREFIX + "Datos del usuario en sesión (ID: " + usuarioEnSesion.getId() + ") actualizados.");
        }
    }

    public boolean cambiarPassword(String passwordActual, String nuevaPassword) {
        Usuario currentUser = AuthController.getUsuarioActual();
        if (currentUser == null) {
            System.out.println(LOG_PREFIX + "Error - No hay usuario logueado para cambiar password.");
            return false;
        }
        int usuarioId = currentUser.getId();
        System.out.println(LOG_PREFIX + "Solicitud para cambiar password del usuario ID: " + usuarioId);
        
        if (passwordActual == null || passwordActual.isEmpty() || nuevaPassword == null || nuevaPassword.isEmpty()) {
            System.out.println(LOG_PREFIX + "La contraseña actual y la nueva no pueden estar vacías.");
            return false;
        }
        // Aquí puedes añadir una validación de longitud mínima para nuevaPassword si quieres
        // if (nuevaPassword.length() < 1) { ... }

        // currentUser.getPasswordHash() contiene la contraseña actual en texto plano
        if (!usuarioRepository.verificarPassword(passwordActual, currentUser.getPasswordHash())) {
            System.out.println(LOG_PREFIX + "Contraseña actual INCORRECTA para usuario ID: " + usuarioId);
            return false;
        }
        
        System.out.println(LOG_PREFIX + "Contraseña actual verificada. Intentando actualizar a nueva contraseña.");
        boolean exito = usuarioRepository.actualizarPassword(usuarioId, nuevaPassword); // Guarda en texto plano
        if (exito) {
            // Actualizar la contraseña (texto plano) en el objeto del usuario logueado en AuthController
            currentUser.setPasswordHash(nuevaPassword);
            System.out.println(LOG_PREFIX + "Contraseña cambiada con éxito en BD y en objeto AuthController para ID: " + usuarioId);
        } else {
             System.out.println(LOG_PREFIX + "Fallo al actualizar contraseña en BD para ID: " + usuarioId);
        }
        return exito;
    }

    public boolean darseDeBaja() {
        Usuario currentUser = AuthController.getUsuarioActual();
        if (currentUser == null) {
            System.out.println(LOG_PREFIX + "Error - No hay usuario logueado para darse de baja.");
            return false;
        }
        System.out.println(LOG_PREFIX + "Solicitud de baja para usuario ID: " + currentUser.getId() + " (" + currentUser.getUsername() + ")");

        if (currentUser.getRol() == Usuario.RolUsuario.ADMINISTRADOR) {
            System.out.println(LOG_PREFIX + "Un administrador no puede darse de baja a sí mismo desde esta función.");
            return false;
        }

        boolean eliminado = usuarioRepository.eliminarUsuario(currentUser.getId());
        if (eliminado) {
            System.out.println(LOG_PREFIX + "Usuario ID: " + currentUser.getId() + " eliminado correctamente de BD.");
            AuthController.logout(); 
            return true;
        }
        System.out.println(LOG_PREFIX + "Error al eliminar usuario ID: " + currentUser.getId() + " de BD.");
        return false;
    }

    // --- Métodos de Administrador ---
    public List<Usuario> listarTodosLosUsuarios() {
        // ... (código igual que en la respuesta anterior) ...
        System.out.println(LOG_PREFIX + "Solicitud para listar todos los usuarios.");
        if (AuthController.isAdmin()) {
            return usuarioRepository.listarTodosLosUsuarios();
        }
        System.out.println(LOG_PREFIX + "Permiso DENEGADO. Se requiere rol Administrador.");
        return new ArrayList<>();
    }

    public List<Usuario> filtrarUsuarios(String nombre, String rol, Boolean activo) {
        // ... (código igual que en la respuesta anterior) ...
        System.out.println(LOG_PREFIX + "Solicitud para filtrar usuarios (Nombre: "+nombre+", Rol: "+rol+", Activo: "+activo+").");
        if (AuthController.isAdmin()) {
            return usuarioRepository.filtrarUsuarios(nombre, rol, activo);
        }
        System.out.println(LOG_PREFIX + "Permiso DENEGADO. Se requiere rol Administrador.");
        return new ArrayList<>();
    }

    public boolean bloquearUsuario(int usuarioId) {
        // ... (código igual que en la respuesta anterior) ...
        System.out.println(LOG_PREFIX + "Solicitud para bloquear usuario ID: " + usuarioId);
        if (AuthController.isAdmin()) {
            Usuario adminActual = AuthController.getUsuarioActual();
            if (adminActual != null && adminActual.getId() == usuarioId) {
                System.out.println(LOG_PREFIX + "Un administrador no puede bloquearse a sí mismo.");
                return false;
            }
            Usuario usuarioABloquear = usuarioRepository.buscarPorId(usuarioId);
            if (usuarioABloquear == null) {
                System.out.println(LOG_PREFIX + "Usuario ID " + usuarioId + " no encontrado.");
                return false;
            }
            if (usuarioABloquear.getRol() == Usuario.RolUsuario.ADMINISTRADOR) {
                System.out.println(LOG_PREFIX + "No se puede bloquear a otro administrador.");
                return false;
            }
            return usuarioRepository.cambiarEstadoActivo(usuarioId, false);
        }
        System.out.println(LOG_PREFIX + "Permiso DENEGADO. Se requiere rol Administrador.");
        return false;
    }

    public boolean desbloquearUsuario(int usuarioId) {
        // ... (código igual que en la respuesta anterior) ...
        System.out.println(LOG_PREFIX + "Solicitud para desbloquear usuario ID: " + usuarioId);
        if (AuthController.isAdmin()) {
            Usuario usuarioADesbloquear = usuarioRepository.buscarPorId(usuarioId);
             if (usuarioADesbloquear == null) {
                System.out.println(LOG_PREFIX + "Usuario ID " + usuarioId + " no encontrado.");
                return false;
            }
            return usuarioRepository.cambiarEstadoActivo(usuarioId, true);
        }
        System.out.println(LOG_PREFIX + "Permiso DENEGADO. Se requiere rol Administrador.");
        return false;
    }

    public boolean adminEliminarUsuario(int usuarioIdAEliminar) {
        // ... (código igual que en la respuesta anterior) ...
        System.out.println(LOG_PREFIX + "Admin intentando eliminar usuario ID: " + usuarioIdAEliminar);
        if (!AuthController.isAdmin()) {
            System.out.println(LOG_PREFIX + "Permiso DENEGADO. Se requiere rol Administrador.");
            return false;
        }
        Usuario adminActual = AuthController.getUsuarioActual();
        if (adminActual != null && adminActual.getId() == usuarioIdAEliminar) {
            System.out.println(LOG_PREFIX + "Un administrador no puede eliminarse a sí mismo desde esta función.");
            return false;
        }
        Usuario usuarioAEliminar = usuarioRepository.buscarPorId(usuarioIdAEliminar);
        if (usuarioAEliminar == null) {
            System.out.println(LOG_PREFIX + "Usuario ID " + usuarioIdAEliminar + " no encontrado para eliminar.");
            return false;
        }
        if (usuarioAEliminar.getRol() == Usuario.RolUsuario.ADMINISTRADOR) {
            System.out.println(LOG_PREFIX + "No se puede eliminar a otro administrador desde esta función.");
            return false;
        }
        return usuarioRepository.eliminarUsuario(usuarioIdAEliminar);
    }

    public boolean adminRegistrarUsuario(Usuario nuevoUsuario, String passwordEnTextoPlano, boolean activarUsuario) {
        // ... (código igual que en la respuesta anterior) ...
        System.out.println(LOG_PREFIX + "Admin intentando registrar nuevo usuario: " + nuevoUsuario.getUsername());
        if (!AuthController.isAdmin()) {
            System.out.println(LOG_PREFIX + "Permiso DENEGADO. Se requiere rol Administrador.");
            return false;
        }
        if (passwordEnTextoPlano == null || passwordEnTextoPlano.isEmpty()) {
             System.out.println(LOG_PREFIX + "Admin debe proveer una contraseña para el nuevo usuario.");
            return false;
        }
        nuevoUsuario.setPasswordHash(passwordEnTextoPlano); 
        
        Usuario registrado = usuarioRepository.registrarUsuario(nuevoUsuario); 
        if (registrado != null) {
            System.out.println(LOG_PREFIX + "Usuario base ("+ registrado.getUsername() +") registrado por admin. ID: " + registrado.getId());
            if (activarUsuario && !registrado.isActivo()) {
                boolean esMismoAdmin = AuthController.getUsuarioActual() != null && 
                                      registrado.getId() == AuthController.getUsuarioActual().getId() &&
                                      registrado.getRol() == Usuario.RolUsuario.ADMINISTRADOR;

                if (esMismoAdmin && registrado.getRol() == Usuario.RolUsuario.ADMINISTRADOR) { // Evitar inactivar al admin actual si se crea a sí mismo y se intenta activar
                     System.out.println(LOG_PREFIX + "El estado activo del admin actual no se modifica aquí.");
                } else {
                    boolean activado = usuarioRepository.cambiarEstadoActivo(registrado.getId(), true);
                    if (activado) {
                        System.out.println(LOG_PREFIX + "Usuario ID " + registrado.getId() + " activado por admin.");
                        registrado.setActivo(true);
                    } else {
                        System.out.println(LOG_PREFIX + "Usuario registrado pero falló la activación para ID: " + registrado.getId());
                    }
                }
            } else if (activarUsuario && registrado.isActivo()){
                 System.out.println(LOG_PREFIX + "Usuario ID " + registrado.getId() + " ya estaba activo o se activó durante el registro.");
            }
            return true;
        }
        System.out.println(LOG_PREFIX + "Falla al registrar nuevo usuario por admin.");
        return false;
    }

     public Usuario obtenerUsuarioPorId(int id) {
        // ... (código igual que en la respuesta anterior) ...
        System.out.println(LOG_PREFIX + "Solicitud para obtener usuario ID: " + id);
        Usuario currentUser = AuthController.getUsuarioActual();
        if (AuthController.isAdmin() || 
            (AuthController.isEmpleado() && (usuarioRepository.buscarPorId(id) != null && usuarioRepository.buscarPorId(id).getRol() != Usuario.RolUsuario.ADMINISTRADOR)) || 
            (AuthController.isLoggedIn() && currentUser != null && currentUser.getId() == id) ) { // Añadido currentUser != null
            return usuarioRepository.buscarPorId(id);
        }
        System.out.println(LOG_PREFIX + "Permiso denegado para ver usuario ID: " + id);
        return null;
    }
}