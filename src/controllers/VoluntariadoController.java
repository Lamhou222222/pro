package controllers;

import model.ActividadVoluntariado;
import model.Usuario;
import repository.UsuarioRepository; // Necesario para validación de rol en registrarActividad
import repository.VoluntariadoRepository;
import java.util.List;
import java.util.ArrayList;

public class VoluntariadoController {
    private VoluntariadoRepository voluntariadoRepository;
    private UsuarioRepository usuarioRepository; // Para validaciones de rol
    private final String LOG_PREFIX = "VOL_CTRL: [TEXTO PLANO CONTEXTO] ";

    public VoluntariadoController() {
        this.voluntariadoRepository = new VoluntariadoRepository();
        this.usuarioRepository = new UsuarioRepository(); // Instanciar para validaciones
        System.out.println(LOG_PREFIX + "Inicializado.");
    }

    public boolean registrarActividad(ActividadVoluntariado actividad) {
        System.out.println(LOG_PREFIX + "Solicitud para registrar actividad.");
        Usuario currentUser = AuthController.getUsuarioActual();
        if (!AuthController.isLoggedIn()) {
            System.out.println(LOG_PREFIX + "No hay usuario logueado.");
            return false;
        }

        if (AuthController.isVoluntario() && !AuthController.isAdmin()) {
            if (actividad.getIdVoluntario() != 0 && actividad.getIdVoluntario() != currentUser.getId()) {
                System.out.println(LOG_PREFIX + "Un voluntario solo puede registrar actividades para sí mismo.");
                // Forzar ID al usuario actual si es un voluntario registrando
                // actividad.setIdVoluntario(currentUser.getId()); 
                // O simplemente rechazar si el ID no coincide (más seguro si la UI permite cambiarlo)
                return false; 
            }
            actividad.setIdVoluntario(currentUser.getId());
        } else if (AuthController.isAdmin()) {
            if (actividad.getIdVoluntario() == 0) {
                System.out.println(LOG_PREFIX + "Admin debe especificar un ID de voluntario válido para registrar la actividad.");
                return false;
            }
            // Validación opcional pero recomendada:
            Usuario voluntarioTarget = usuarioRepository.buscarPorId(actividad.getIdVoluntario());
            if (voluntarioTarget == null || voluntarioTarget.getRol() != Usuario.RolUsuario.VOLUNTARIO) {
                System.out.println(LOG_PREFIX + "El ID (" + actividad.getIdVoluntario() + ") especificado no corresponde a un voluntario válido o activo.");
                return false;
            }
        } else {
            System.out.println(LOG_PREFIX + "Acceso denegado. Debe ser Voluntario o Administrador para registrar actividad.");
            return false;
        }
        
        ActividadVoluntariado creada = voluntariadoRepository.crearActividad(actividad);
        if (creada != null) {
            System.out.println(LOG_PREFIX + "Actividad registrada con ID: " + creada.getId());
            return true;
        }
        System.out.println(LOG_PREFIX + "Fallo al registrar la actividad (ver logs del repositorio).");
        return false;
    }

    public List<ActividadVoluntariado> obtenerMisActividades() {
        System.out.println(LOG_PREFIX + "Solicitud de 'mis actividades'.");
        Usuario currentUser = AuthController.getUsuarioActual();
        if (currentUser != null && AuthController.isVoluntario()) {
            return voluntariadoRepository.listarPorVoluntario(currentUser.getId());
        }
        if (currentUser == null) System.out.println(LOG_PREFIX + "No hay usuario logueado.");
        else System.out.println(LOG_PREFIX + "Usuario no es Voluntario.");
        return new ArrayList<>();
    }

    public ActividadVoluntariado obtenerActividadPorId(int idActividad) {
        System.out.println(LOG_PREFIX + "Solicitud para obtener actividad ID: " + idActividad);
        if (!AuthController.isLoggedIn()) {
            System.out.println(LOG_PREFIX + "No hay usuario logueado.");
            return null;
        }
        ActividadVoluntariado actividad = voluntariadoRepository.buscarPorId(idActividad);
        if (actividad == null) {
            System.out.println(LOG_PREFIX + "Actividad ID " + idActividad + " no encontrada en el repositorio.");
            return null;
        }

        // Admin puede ver cualquiera. Voluntario solo las suyas.
        if (AuthController.isAdmin() || 
            (AuthController.isVoluntario() && actividad.getIdVoluntario() == AuthController.getUsuarioActual().getId())) {
            System.out.println(LOG_PREFIX + "Acceso concedido para ver actividad ID: " + idActividad);
            return actividad;
        }
        System.out.println(LOG_PREFIX + "Permiso denegado para ver actividad ID: " + idActividad);
        return null;
    }

    public List<ActividadVoluntariado> obtenerTodasLasActividades() {
        System.out.println(LOG_PREFIX + "Solicitud de todas las actividades (Admin).");
        if (AuthController.isAdmin()) {
            return voluntariadoRepository.listarTodas();
        }
        System.out.println(LOG_PREFIX + "Acceso denegado. Se requiere rol Administrador.");
        return new ArrayList<>();
    }
    
    public List<ActividadVoluntariado> obtenerActividadesPorVoluntario(int idVoluntario) {
        System.out.println(LOG_PREFIX + "Solicitud de actividades para voluntario ID: " + idVoluntario + " (Admin).");
         if (AuthController.isAdmin()) {
            return voluntariadoRepository.listarPorVoluntario(idVoluntario);
        }
        System.out.println(LOG_PREFIX + "Acceso denegado. Se requiere rol Administrador.");
        return new ArrayList<>();
    }

    public boolean actualizarActividad(ActividadVoluntariado actividad) {
        System.out.println(LOG_PREFIX + "Solicitud para actualizar actividad ID: " + actividad.getId());
        Usuario currentUser = AuthController.getUsuarioActual();
        if (!AuthController.isLoggedIn()) {
            System.out.println(LOG_PREFIX + "No hay usuario logueado.");
            return false;
        }

        ActividadVoluntariado existente = voluntariadoRepository.buscarPorId(actividad.getId());
        if (existente == null) {
            System.out.println(LOG_PREFIX + "Actividad ID " + actividad.getId() + " no encontrada para actualizar.");
            return false;
        }

        boolean puedeActualizar = false;
        if (AuthController.isAdmin()) {
            puedeActualizar = true;
            // Admin puede cambiar el idVoluntario si es necesario (ya debería estar en el objeto 'actividad')
            // Opcional: Validar que el nuevo idVoluntario sea un VOLUNTARIO válido
            if (actividad.getIdVoluntario() != existente.getIdVoluntario()) {
                Usuario nuevoVoluntario = usuarioRepository.buscarPorId(actividad.getIdVoluntario());
                if (nuevoVoluntario == null || nuevoVoluntario.getRol() != Usuario.RolUsuario.VOLUNTARIO) {
                    System.out.println(LOG_PREFIX + "Admin intentó asignar actividad a un ID de no-voluntario inválido: " + actividad.getIdVoluntario());
                    return false;
                }
            }
        } else if (AuthController.isVoluntario() && existente.getIdVoluntario() == currentUser.getId()) {
            if (actividad.getIdVoluntario() != currentUser.getId()) {
                System.out.println(LOG_PREFIX + "Un voluntario no puede cambiar el propietario de la actividad. Forzando al ID actual.");
                actividad.setIdVoluntario(currentUser.getId()); 
            }
            puedeActualizar = true;
        }

        if (puedeActualizar) {
            System.out.println(LOG_PREFIX + "Permiso concedido para actualizar actividad ID: " + actividad.getId());
            return voluntariadoRepository.actualizarActividad(actividad);
        }
        System.out.println(LOG_PREFIX + "Acceso denegado para actualizar actividad ID: " + actividad.getId());
        return false;
    }

    public boolean eliminarActividad(int idActividad) {
        System.out.println(LOG_PREFIX + "Solicitud para eliminar actividad ID: " + idActividad);
        Usuario currentUser = AuthController.getUsuarioActual();
         if (!AuthController.isLoggedIn()) {
            System.out.println(LOG_PREFIX + "No hay usuario logueado.");
            return false;
        }

        ActividadVoluntariado existente = voluntariadoRepository.buscarPorId(idActividad);
        if (existente == null) {
            System.out.println(LOG_PREFIX + "Actividad ID " + idActividad + " no encontrada para eliminar.");
            return false;
        }
        
        boolean puedeEliminar = false;
        if (AuthController.isAdmin()) {
            puedeEliminar = true;
        } else if (AuthController.isVoluntario() && existente.getIdVoluntario() == currentUser.getId()) {
            puedeEliminar = true;
        }
        
        if (puedeEliminar) {
            System.out.println(LOG_PREFIX + "Permiso concedido para eliminar actividad ID: " + idActividad);
            return voluntariadoRepository.eliminarActividad(idActividad);
        }
        System.out.println(LOG_PREFIX + "Acceso denegado para eliminar actividad ID: " + idActividad);
        return false;
    }
}