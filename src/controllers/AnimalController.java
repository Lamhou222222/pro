package controllers;

import model.Animal;

import model.Usuario; // Para AuthController.getUsuarioActual() si es necesario
import repository.AnimalRepository;
import java.util.List;
import java.util.ArrayList;

public class AnimalController {
    private AnimalRepository animalRepository;

    public AnimalController() {
        this.animalRepository = new AnimalRepository();
        System.out.println("ANIMAL_CTRL: [TEXTO PLANO CONTEXTO] Inicializado.");
    }

    public List<Animal> obtenerAnimalesDisponiblesParaAdopcion() {
        System.out.println("ANIMAL_CTRL: [TEXTO PLANO CONTEXTO] Solicitud de animales disponibles.");
        return animalRepository.listarAnimalesDisponibles();
    }

    public Animal obtenerDetallesAnimal(int id) {
        System.out.println("ANIMAL_CTRL: [TEXTO PLANO CONTEXTO] Solicitud de detalles para animal ID: " + id);
        return animalRepository.buscarAnimalPorId(id);
    }

    public List<Animal> obtenerTodosLosAnimales() {
        System.out.println("ANIMAL_CTRL: [TEXTO PLANO CONTEXTO] Solicitud de todos los animales (Staff).");
        if (AuthController.isEmpleado()) { // Incluye Admin
            return animalRepository.listarTodosLosAnimales();
        }
        System.out.println("ANIMAL_CTRL: [TEXTO PLANO CONTEXTO] Acceso denegado. Se requiere rol Empleado o Administrador.");
        return new ArrayList<>();
    }

    public boolean crearAnimal(Animal animal) {
        System.out.println("ANIMAL_CTRL: [TEXTO PLANO CONTEXTO] Solicitud para crear animal: " + animal.getNombre());
        if (AuthController.isEmpleado()) { // Incluye Admin
            // Si el responsable no está asignado y hay un usuario logueado, se podría asignar por defecto.
            if (animal.getIdUsuarioResponsable() == null && AuthController.isLoggedIn()) {
                // Aquí podrías decidir si el creador es automáticamente el responsable.
                // Por ejemplo: animal.setIdUsuarioResponsable(AuthController.getUsuarioActual().getId());
                // O dejarlo null si se asigna después.
            }
            Animal creado = animalRepository.crearAnimal(animal);
            return creado != null;
        }
        System.out.println("ANIMAL_CTRL: [TEXTO PLANO CONTEXTO] Acceso denegado. Se requiere rol Empleado o Administrador.");
        return false;
    }

    public boolean actualizarAnimal(Animal animal) {
        System.out.println("ANIMAL_CTRL: [TEXTO PLANO CONTEXTO] Solicitud para actualizar animal ID: " + animal.getId());
        if (AuthController.isEmpleado()) { // Incluye Admin
            return animalRepository.actualizarAnimal(animal);
        }
        System.out.println("ANIMAL_CTRL: [TEXTO PLANO CONTEXTO] Acceso denegado. Se requiere rol Empleado o Administrador.");
        return false;
    }

    public boolean eliminarAnimal(int id) {
        System.out.println("ANIMAL_CTRL: [TEXTO PLANO CONTEXTO] Solicitud para eliminar animal ID: " + id);
        if (AuthController.isAdmin()) { // Solo Admin puede eliminar
            // Aquí podrías añadir lógica para verificar si el animal tiene solicitudes pendientes, etc.
            return animalRepository.eliminarAnimal(id);
        }
        System.out.println("ANIMAL_CTRL: [TEXTO PLANO CONTEXTO] Acceso denegado. Se requiere rol Administrador para eliminar animales.");
        return false;
    }
    
    // TODO: Implementar método de filtrado de animales con chequeo de permisos si es necesario.
    // public List<Animal> filtrarAnimales(String especieFiltro, String estadoFiltro, ...) { ... }
}