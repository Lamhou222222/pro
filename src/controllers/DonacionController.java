package controllers;

import model.Donacion;
import model.Usuario;
import repository.DonacionRepository;
import java.util.List;
import java.util.ArrayList;

public class DonacionController {
    private DonacionRepository donacionRepository;

    public DonacionController() {
        this.donacionRepository = new DonacionRepository();
        System.out.println("DONACION_CTRL: [TEXTO PLANO CONTEXTO] Inicializado.");
    }

    public boolean registrarDonacion(Donacion donacion) {
        System.out.println("DONACION_CTRL: [TEXTO PLANO CONTEXTO] Solicitud para registrar donación. Tipo: " + donacion.getTipoDonacion());
        Usuario currentUser = AuthController.getUsuarioActual();

        if (currentUser != null && donacion.getIdUsuarioDonante() == null && 
            (donacion.getNombreDonanteAnonimo() == null || donacion.getNombreDonanteAnonimo().trim().isEmpty())) {
            donacion.setIdUsuarioDonante(currentUser.getId());
            System.out.println("DONACION_CTRL: [TEXTO PLANO CONTEXTO] Donación asignada al usuario logueado ID: " + currentUser.getId());
        } else if (donacion.getIdUsuarioDonante() == null && 
                   (donacion.getNombreDonanteAnonimo() == null || donacion.getNombreDonanteAnonimo().trim().isEmpty())) {
             System.out.println("DONACION_CTRL: [TEXTO PLANO CONTEXTO] Se requiere un donante (registrado o anónimo).");
             return false;
        }
        
        Donacion creada = donacionRepository.crearDonacion(donacion);
        return creada != null;
    }

    public List<Donacion> obtenerMisDonaciones() {
        System.out.println("DONACION_CTRL: [TEXTO PLANO CONTEXTO] Solicitud de 'mis donaciones'.");
        Usuario currentUser = AuthController.getUsuarioActual();
        if (currentUser != null) {
            return donacionRepository.listarPorUsuario(currentUser.getId());
        }
        System.out.println("DONACION_CTRL: [TEXTO PLANO CONTEXTO] Debe estar logueado para ver sus donaciones.");
        return new ArrayList<>();
    }

    public List<Donacion> obtenerTodasLasDonaciones() {
        System.out.println("DONACION_CTRL: [TEXTO PLANO CONTEXTO] Solicitud de todas las donaciones (Admin).");
        if (AuthController.isAdmin()) {
            return donacionRepository.listarTodas();
        }
        System.out.println("DONACION_CTRL: [TEXTO PLANO CONTEXTO] Acceso denegado. Se requiere rol Administrador.");
        return new ArrayList<>();
    }
    
    public Donacion obtenerDonacionPorId(int id){
        System.out.println("DONACION_CTRL: [TEXTO PLANO CONTEXTO] Solicitud de donación por ID: "+id+" (Admin).");
        if (AuthController.isAdmin()) {
            return donacionRepository.buscarPorId(id);
        }
        System.out.println("DONACION_CTRL: [TEXTO PLANO CONTEXTO] Acceso denegado para ver donación por ID.");
        return null;
    }

    public boolean actualizarDonacion(Donacion donacion) {
        System.out.println("DONACION_CTRL: [TEXTO PLANO CONTEXTO] Solicitud para actualizar donación ID: " + donacion.getId() + " (Admin).");
        if (AuthController.isAdmin()) {
            return donacionRepository.actualizarDonacion(donacion);
        }
        System.out.println("DONACION_CTRL: [TEXTO PLANO CONTEXTO] Acceso denegado. Se requiere rol Administrador.");
        return false;
    }

    public boolean eliminarDonacion(int idDonacion) {
        System.out.println("DONACION_CTRL: [TEXTO PLANO CONTEXTO] Solicitud para eliminar donación ID: " + idDonacion + " (Admin).");
        if (AuthController.isAdmin()) {
            return donacionRepository.eliminarDonacion(idDonacion);
        }
        System.out.println("DONACION_CTRL: [TEXTO PLANO CONTEXTO] Acceso denegado. Se requiere rol Administrador.");
        return false;
    }
}