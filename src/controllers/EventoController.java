package controllers;

import model.EventoRefugio;
import model.Usuario;
import repository.EventoRefugioRepository;
import java.util.List;
import java.util.ArrayList;

public class EventoController {
    private EventoRefugioRepository eventoRepository;

    public EventoController() {
        this.eventoRepository = new EventoRefugioRepository();
        System.out.println("EVENTO_CTRL: [TEXTO PLANO CONTEXTO] Inicializado.");
    }

    public List<EventoRefugio> obtenerTodosLosEventosPublicos() {
        System.out.println("EVENTO_CTRL: [TEXTO PLANO CONTEXTO] Solicitud de todos los eventos públicos.");
        return eventoRepository.listarTodos();
    }

    public EventoRefugio obtenerDetallesEvento(int id) {
        System.out.println("EVENTO_CTRL: [TEXTO PLANO CONTEXTO] Solicitud de detalles para evento ID: " + id);
        return eventoRepository.buscarPorId(id);
    }

    public boolean crearEvento(EventoRefugio evento) {
        System.out.println("EVENTO_CTRL: [TEXTO PLANO CONTEXTO] Solicitud para crear evento: " + evento.getNombreEvento());
        if (AuthController.isEmpleado()) { // Incluye Admin
            if (evento.getIdUsuarioOrganizador() == null && AuthController.isLoggedIn()) {
                evento.setIdUsuarioOrganizador(AuthController.getUsuarioActual().getId());
            }
            EventoRefugio creado = eventoRepository.crearEvento(evento);
            return creado != null;
        }
        System.out.println("EVENTO_CTRL: [TEXTO PLANO CONTEXTO] Acceso denegado. Se requiere rol Empleado o Administrador.");
        return false;
    }

    public boolean actualizarEvento(EventoRefugio evento) {
        System.out.println("EVENTO_CTRL: [TEXTO PLANO CONTEXTO] Solicitud para actualizar evento ID: " + evento.getId());
        Usuario currentUser = AuthController.getUsuarioActual();
        if (!AuthController.isLoggedIn()) {
            System.out.println("EVENTO_CTRL: [TEXTO PLANO CONTEXTO] No hay usuario logueado.");
            return false;
        }

        EventoRefugio eventoExistente = eventoRepository.buscarPorId(evento.getId());
        if (eventoExistente == null) {
            System.out.println("EVENTO_CTRL: [TEXTO PLANO CONTEXTO] Evento ID " + evento.getId() + " no encontrado.");
            return false;
        }

        boolean puedeActualizar = AuthController.isAdmin() || 
                                 (AuthController.isEmpleado() && 
                                  eventoExistente.getIdUsuarioOrganizador() != null &&
                                  eventoExistente.getIdUsuarioOrganizador().equals(currentUser.getId()));
        
        if (puedeActualizar) {
            return eventoRepository.actualizarEvento(evento);
        }
        System.out.println("EVENTO_CTRL: [TEXTO PLANO CONTEXTO] Acceso denegado para actualizar evento ID: " + evento.getId());
        return false;
    }

    public boolean eliminarEvento(int id) {
        System.out.println("EVENTO_CTRL: [TEXTO PLANO CONTEXTO] Solicitud para eliminar evento ID: " + id);
         Usuario currentUser = AuthController.getUsuarioActual();
        if (!AuthController.isLoggedIn()) {
            System.out.println("EVENTO_CTRL: [TEXTO PLANO CONTEXTO] No hay usuario logueado.");
            return false;
        }

        EventoRefugio eventoExistente = eventoRepository.buscarPorId(id);
        if (eventoExistente == null) {
            System.out.println("EVENTO_CTRL: [TEXTO PLANO CONTEXTO] Evento ID " + id + " no encontrado.");
            return false;
        }
        
        boolean puedeEliminar = AuthController.isAdmin() || 
                                 (AuthController.isEmpleado() && 
                                  eventoExistente.getIdUsuarioOrganizador() != null &&
                                  eventoExistente.getIdUsuarioOrganizador().equals(currentUser.getId()));

        if (puedeEliminar) {
            return eventoRepository.eliminarEvento(id);
        }
        System.out.println("EVENTO_CTRL: [TEXTO PLANO CONTEXTO] Acceso denegado para eliminar evento ID: " + id);
        return false;
    }

    public List<EventoRefugio> filtrarEventos(String nombre, String tipo, Integer idOrganizador) {
        System.out.println("EVENTO_CTRL: [TEXTO PLANO CONTEXTO] Solicitud para filtrar eventos.");
        // Usualmente público o para staff, ajustar permisos si es necesario
        return eventoRepository.filtrarEventos(nombre, tipo, idOrganizador);
    }
}