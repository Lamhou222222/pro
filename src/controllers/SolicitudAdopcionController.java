package controllers;

import model.SolicitudAdopcion;
import model.Usuario;
import model.Animal;
import repository.SolicitudAdopcionRepository;
import repository.AnimalRepository;
import java.util.List;
import java.util.ArrayList;

public class SolicitudAdopcionController {
    private SolicitudAdopcionRepository solicitudRepository;
    private AnimalRepository animalRepository;

    public SolicitudAdopcionController() {
        this.solicitudRepository = new SolicitudAdopcionRepository();
        this.animalRepository = new AnimalRepository();
        System.out.println("SOL_ADOP_CTRL: [TEXTO PLANO CONTEXTO] Inicializado.");
    }

    public boolean crearSolicitud(SolicitudAdopcion solicitud) {
        System.out.println("SOL_ADOP_CTRL: [TEXTO PLANO CONTEXTO] Solicitud para crear solicitud de adopción.");
        Usuario currentUser = AuthController.getUsuarioActual();
        if (!AuthController.isLoggedIn()) {
            System.out.println("SOL_ADOP_CTRL: [TEXTO PLANO CONTEXTO] Debe estar logueado para solicitar adopción.");
            return false;
        }

        if (!(AuthController.isAdoptante() || AuthController.isAdmin())) {
            System.out.println("SOL_ADOP_CTRL: [TEXTO PLANO CONTEXTO] Rol no permitido. Se requiere ADOPTANTE_POTENCIAL o ADMIN.");
            return false;
        }
        
        if (solicitud.getIdUsuarioSolicitante() == 0 && AuthController.isLoggedIn()) { // Si no se especifica, y está logueado
            solicitud.setIdUsuarioSolicitante(currentUser.getId());
        } else if (AuthController.isAdoptante() && solicitud.getIdUsuarioSolicitante() != currentUser.getId()){
            System.out.println("SOL_ADOP_CTRL: [TEXTO PLANO CONTEXTO] Un adoptante solo puede crear solicitudes para sí mismo.");
            solicitud.setIdUsuarioSolicitante(currentUser.getId()); // Forzar al usuario actual
        }

        Animal animalSolicitado = animalRepository.buscarAnimalPorId(solicitud.getIdAnimal());
        if (animalSolicitado == null) {
            System.out.println("SOL_ADOP_CTRL: [TEXTO PLANO CONTEXTO] Animal ID " + solicitud.getIdAnimal() + " no encontrado.");
            return false;
        }
        if (animalSolicitado.getEstadoAdopcion() != Animal.EstadoAdopcion.DISPONIBLE) {
            System.out.println("SOL_ADOP_CTRL: [TEXTO PLANO CONTEXTO] Animal '" + animalSolicitado.getNombre() + "' no está DISPONIBLE. Estado: " + animalSolicitado.getEstadoAdopcion());
            return false;
        }
        
        if (solicitud.getEstado() == null) {
            solicitud.setEstado(SolicitudAdopcion.EstadoSolicitud.ENVIADA);
        }

        SolicitudAdopcion creada = solicitudRepository.crearSolicitud(solicitud);
        if (creada != null) {
            // Opcional: Cambiar estado del animal a RESERVADO
            // animalSolicitado.setEstadoAdopcion(Animal.EstadoAdopcion.RESERVADO);
            // animalRepository.actualizarAnimal(animalSolicitado);
            System.out.println("SOL_ADOP_CTRL: [TEXTO PLANO CONTEXTO] Solicitud creada exitosamente ID: " + creada.getId());
            return true;
        }
        return false;
    }

    public List<SolicitudAdopcion> obtenerMisSolicitudes() {
        System.out.println("SOL_ADOP_CTRL: [TEXTO PLANO CONTEXTO] Solicitud de 'mis solicitudes'.");
        Usuario currentUser = AuthController.getUsuarioActual();
        if (currentUser != null && AuthController.isAdoptante()) {
            return solicitudRepository.listarPorUsuario(currentUser.getId());
        }
         if (currentUser == null) System.out.println("SOL_ADOP_CTRL: [TEXTO PLANO CONTEXTO] No hay usuario logueado.");
        else System.out.println("SOL_ADOP_CTRL: [TEXTO PLANO CONTEXTO] Usuario no es Adoptante Potencial.");
        return new ArrayList<>();
    }

    public boolean cancelarMiSolicitud(int idSolicitud) {
        System.out.println("SOL_ADOP_CTRL: [TEXTO PLANO CONTEXTO] Solicitud para cancelar solicitud ID: " + idSolicitud);
        Usuario currentUser = AuthController.getUsuarioActual();
        if (!AuthController.isLoggedIn()) {
             System.out.println("SOL_ADOP_CTRL: [TEXTO PLANO CONTEXTO] No hay usuario logueado.");
            return false;
        }

        SolicitudAdopcion solicitud = solicitudRepository.buscarPorId(idSolicitud);
        if (solicitud == null) {
            System.out.println("SOL_ADOP_CTRL: [TEXTO PLANO CONTEXTO] Solicitud ID " + idSolicitud + " no encontrada.");
            return false;
        }

        if (solicitud.getIdUsuarioSolicitante() == currentUser.getId()) {
            if (solicitud.getEstado() == SolicitudAdopcion.EstadoSolicitud.ENVIADA || 
                solicitud.getEstado() == SolicitudAdopcion.EstadoSolicitud.EN_REVISION ||
                solicitud.getEstado() == SolicitudAdopcion.EstadoSolicitud.ENTREVISTA_PROGRAMADA) {
                
                boolean cancelada = solicitudRepository.actualizarEstadoSolicitud(idSolicitud, SolicitudAdopcion.EstadoSolicitud.CANCELADA_POR_USUARIO, "Cancelada por el usuario.");
                if(cancelada){
                    // Lógica para cambiar estado del animal si estaba RESERVADO por esta solicitud
                    Animal animal = animalRepository.buscarAnimalPorId(solicitud.getIdAnimal());
                    if(animal != null && (animal.getEstadoAdopcion() == Animal.EstadoAdopcion.RESERVADO || animal.getEstadoAdopcion() == Animal.EstadoAdopcion.EN_PROCESO_DE_ADOPCION)) {
                       // Solo volver a disponible si no hay OTRA solicitud APROBADA para este animal.
                       // Esto requiere una consulta adicional o una política más simple.
                       // Por simplicidad, si se cancela, se podría volver a disponible tentativamente.
                       List<SolicitudAdopcion> otrasSolicitudes = solicitudRepository.listarPorAnimal(animal.getId());
                       boolean hayOtraAprobada = otrasSolicitudes.stream().anyMatch(s -> s.getEstado() == SolicitudAdopcion.EstadoSolicitud.APROBADA && s.getId() != idSolicitud);
                       if (!hayOtraAprobada) {
                           animal.setEstadoAdopcion(Animal.EstadoAdopcion.DISPONIBLE);
                           animalRepository.actualizarAnimal(animal);
                           System.out.println("SOL_ADOP_CTRL: [TEXTO PLANO CONTEXTO] Animal ID " + animal.getId() + " vuelto a DISPONIBLE tras cancelación.");
                       }
                    }
                }
                return cancelada;
            } else {
                System.out.println("SOL_ADOP_CTRL: [TEXTO PLANO CONTEXTO] No se puede cancelar una solicitud en estado: " + solicitud.getEstado());
                return false;
            }
        }
        System.out.println("SOL_ADOP_CTRL: [TEXTO PLANO CONTEXTO] Permiso denegado. La solicitud no pertenece al usuario.");
        return false;
    }

    public List<SolicitudAdopcion> obtenerTodasLasSolicitudes() {
        System.out.println("SOL_ADOP_CTRL: [TEXTO PLANO CONTEXTO] Solicitud de todas las solicitudes (Staff).");
        if (AuthController.isEmpleado()) { // Incluye Admin
            return solicitudRepository.listarTodas();
        }
        System.out.println("SOL_ADOP_CTRL: [TEXTO PLANO CONTEXTO] Acceso denegado.");
        return new ArrayList<>();
    }
    
    public SolicitudAdopcion obtenerSolicitudPorId(int id) {
        System.out.println("SOL_ADOP_CTRL: [TEXTO PLANO CONTEXTO] Solicitud para obtener solicitud por ID: " + id + " (Staff).");
        if (AuthController.isEmpleado()) { // Incluye Admin
            return solicitudRepository.buscarPorId(id);
        }
        System.out.println("SOL_ADOP_CTRL: [TEXTO PLANO CONTEXTO] Acceso denegado para ver solicitud por ID.");
        return null;
    }

    public boolean actualizarEstadoSolicitud(int idSolicitud, SolicitudAdopcion.EstadoSolicitud nuevoEstado, String notasAdmin) {
        System.out.println("SOL_ADOP_CTRL: [TEXTO PLANO CONTEXTO] Solicitud para actualizar estado de solicitud ID: " + idSolicitud + " a " + nuevoEstado + " (Staff).");
        if (AuthController.isEmpleado()) { // Incluye Admin
            SolicitudAdopcion solicitud = solicitudRepository.buscarPorId(idSolicitud);
            if (solicitud == null) {
                System.out.println("SOL_ADOP_CTRL: [TEXTO PLANO CONTEXTO] Solicitud ID " + idSolicitud + " no encontrada.");
                return false;
            }

            boolean actualizado = solicitudRepository.actualizarEstadoSolicitud(idSolicitud, nuevoEstado, notasAdmin);
            if (actualizado) {
                System.out.println("SOL_ADOP_CTRL: [TEXTO PLANO CONTEXTO] Estado de solicitud ID " + idSolicitud + " actualizado a " + nuevoEstado);
                Animal animal = animalRepository.buscarAnimalPorId(solicitud.getIdAnimal());
                if (animal != null) {
                    if (nuevoEstado == SolicitudAdopcion.EstadoSolicitud.APROBADA) {
                        animal.setEstadoAdopcion(Animal.EstadoAdopcion.ADOPTADO); 
                        animalRepository.actualizarAnimal(animal);
                        System.out.println("SOL_ADOP_CTRL: [TEXTO PLANO CONTEXTO] Animal ID " + animal.getId() + " marcado como ADOPTADO.");
                        // Opcional: Rechazar automáticamente otras solicitudes pendientes para este animal
                        // List<SolicitudAdopcion> otras = solicitudRepository.listarPorAnimal(animal.getId());
                        // for(SolicitudAdopcion otra : otras){
                        //    if(otra.getId() != idSolicitud && (otra.getEstado() == SolicitudAdopcion.EstadoSolicitud.ENVIADA || otra.getEstado() == SolicitudAdopcion.EstadoSolicitud.EN_REVISION)){
                        //        solicitudRepository.actualizarEstadoSolicitud(otra.getId(), SolicitudAdopcion.EstadoSolicitud.RECHAZADA, "Animal ya adoptado.");
                        //    }
                        // }
                    } else if ((solicitud.getEstado() == SolicitudAdopcion.EstadoSolicitud.APROBADA || solicitud.getEstado() == SolicitudAdopcion.EstadoSolicitud.EN_PROCESO_DE_ADOPCION || solicitud.getEstado() == SolicitudAdopcion.EstadoSolicitud.RESERVADO) &&
                               (nuevoEstado == SolicitudAdopcion.EstadoSolicitud.RECHAZADA || nuevoEstado == SolicitudAdopcion.EstadoSolicitud.CANCELADA_POR_USUARIO)) {
                        animal.setEstadoAdopcion(Animal.EstadoAdopcion.DISPONIBLE);
                        animalRepository.actualizarAnimal(animal);
                        System.out.println("SOL_ADOP_CTRL: [TEXTO PLANO CONTEXTO] Animal ID " + animal.getId() + " vuelto a DISPONIBLE.");
                    } else if (nuevoEstado == SolicitudAdopcion.EstadoSolicitud.EN_REVISION || nuevoEstado == SolicitudAdopcion.EstadoSolicitud.ENTREVISTA_PROGRAMADA) {
                        if(animal.getEstadoAdopcion() == Animal.EstadoAdopcion.DISPONIBLE){ // Solo reservar si está disponible
                            animal.setEstadoAdopcion(Animal.EstadoAdopcion.RESERVADO);
                            animalRepository.actualizarAnimal(animal);
                            System.out.println("SOL_ADOP_CTRL: [TEXTO PLANO CONTEXTO] Animal ID " + animal.getId() + " marcado como RESERVADO.");
                        }
                    }
                }
            }
            return actualizado;
        }
        System.out.println("SOL_ADOP_CTRL: [TEXTO PLANO CONTEXTO] Acceso denegado.");
        return false;
    }

    public List<SolicitudAdopcion> filtrarSolicitudes(Integer idAnimal, Integer idUsuario, String estado) {
        System.out.println("SOL_ADOP_CTRL: [TEXTO PLANO CONTEXTO] Solicitud para filtrar solicitudes (Staff).");
         if (AuthController.isEmpleado()) { // Incluye Admin
            return solicitudRepository.filtrarSolicitudes(idAnimal, idUsuario, estado);
        }
        System.out.println("SOL_ADOP_CTRL: [TEXTO PLANO CONTEXTO] Acceso denegado.");
        return new ArrayList<>();
    }
}