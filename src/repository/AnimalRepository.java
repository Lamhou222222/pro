package repository;

import model.Animal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AnimalRepository {

    private final String LOG_PREFIX = "REPO_ANIMAL: [TEXTO PLANO CONTEXTO] ";

    public Animal crearAnimal(Animal animal) {
        String sql = "INSERT INTO animales (nombre, especie, raza, edad_estimada_anios, edad_estimada_meses, genero, color, tamanio, descripcion_caracter, historial_medico, necesidades_especiales, foto_url, estado_adopcion, id_usuario_responsable) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        System.out.println(LOG_PREFIX + "Creando animal: " + animal.getNombre());
        Connection conn = Conexion.getConnection();
        if (conn == null) {
            System.err.println(LOG_PREFIX + "crearAnimal - No se pudo obtener conexión.");
            return null;
        }
        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, animal.getNombre());
            pstmt.setString(2, animal.getEspecie());
            pstmt.setString(3, animal.getRaza());
            setNullableInt(pstmt, 4, animal.getEdadEstimadaAnios());
            setNullableInt(pstmt, 5, animal.getEdadEstimadaMeses());
            pstmt.setString(6, animal.getGenero());
            pstmt.setString(7, animal.getColor());
            pstmt.setString(8, animal.getTamanio());
            pstmt.setString(9, animal.getDescripcionCaracter());
            pstmt.setString(10, animal.getHistorialMedico());
            pstmt.setString(11, animal.getNecesidadesEspeciales());
            pstmt.setString(12, animal.getFotoUrl());
            pstmt.setString(13, animal.getEstadoAdopcion() != null ? animal.getEstadoAdopcion().name() : Animal.EstadoAdopcion.DISPONIBLE.name());
            setNullableInt(pstmt, 14, animal.getIdUsuarioResponsable());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        animal.setId(generatedKeys.getInt(1));
                        // Para obtener fecha_ingreso generada por DB:
                        Animal insertedAnimal = buscarAnimalPorId(animal.getId());
                        if (insertedAnimal != null) animal.setFechaIngreso(insertedAnimal.getFechaIngreso());

                        System.out.println(LOG_PREFIX + "Animal '" + animal.getNombre() + "' creado con ID: " + animal.getId());
                        return animal;
                    }
                }
            }
        } catch (SQLException e) {
            handleSQLException("crearAnimal", animal.getNombre(), e);
        }
        return null;
    }

    public Animal buscarAnimalPorId(int id) {
        String sql = "SELECT * FROM animales WHERE id = ?";
        System.out.println(LOG_PREFIX + "Buscando animal por ID: " + id);
        Connection conn = Conexion.getConnection();
        if (conn == null) {
            System.err.println(LOG_PREFIX + "buscarAnimalPorId - No se pudo obtener conexión.");
            return null;
        }
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    System.out.println(LOG_PREFIX + "Animal con ID " + id + " encontrado.");
                    return mapResultSetToAnimal(rs);
                } else {
                    System.out.println(LOG_PREFIX + "Animal con ID " + id + " NO encontrado.");
                }
            }
        } catch (SQLException e) {
            handleSQLException("buscarAnimalPorId", String.valueOf(id), e);
        }
        return null;
    }

    public List<Animal> listarTodosLosAnimales() {
        List<Animal> animales = new ArrayList<>();
        String sql = "SELECT * FROM animales ORDER BY nombre";
        System.out.println(LOG_PREFIX + "Listando todos los animales.");
        Connection conn = Conexion.getConnection();
        if (conn == null) {
            System.err.println(LOG_PREFIX + "listarTodosLosAnimales - No se pudo obtener conexión.");
            return animales;
        }
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                animales.add(mapResultSetToAnimal(rs));
            }
            System.out.println(LOG_PREFIX + "Total animales listados: " + animales.size());
        } catch (SQLException e) {
            handleSQLException("listarTodosLosAnimales", null, e);
        }
        return animales;
    }
    
    public List<Animal> listarAnimalesDisponibles() {
        List<Animal> animales = new ArrayList<>();
        String sql = "SELECT * FROM animales WHERE estado_adopcion = 'DISPONIBLE' ORDER BY nombre";
        System.out.println(LOG_PREFIX + "Listando animales DISPONIBLES.");
        Connection conn = Conexion.getConnection();
        if (conn == null) {
            System.err.println(LOG_PREFIX + "listarAnimalesDisponibles - No se pudo obtener conexión.");
            return animales;
        }
        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                animales.add(mapResultSetToAnimal(rs));
            }
             System.out.println(LOG_PREFIX + "Total animales DISPONIBLES listados: " + animales.size());
        } catch (SQLException e) {
            handleSQLException("listarAnimalesDisponibles", null, e);
        }
        return animales;
    }

    public boolean actualizarAnimal(Animal animal) {
        String sql = "UPDATE animales SET nombre=?, especie=?, raza=?, edad_estimada_anios=?, edad_estimada_meses=?, genero=?, color=?, tamanio=?, descripcion_caracter=?, historial_medico=?, necesidades_especiales=?, foto_url=?, estado_adopcion=?, id_usuario_responsable=? WHERE id=?";
        System.out.println(LOG_PREFIX + "Actualizando animal ID: " + animal.getId());
        Connection conn = Conexion.getConnection();
        if (conn == null) {
            System.err.println(LOG_PREFIX + "actualizarAnimal - No se pudo obtener conexión.");
            return false;
        }
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, animal.getNombre());
            pstmt.setString(2, animal.getEspecie());
            pstmt.setString(3, animal.getRaza());
            setNullableInt(pstmt, 4, animal.getEdadEstimadaAnios());
            setNullableInt(pstmt, 5, animal.getEdadEstimadaMeses());
            pstmt.setString(6, animal.getGenero());
            pstmt.setString(7, animal.getColor());
            pstmt.setString(8, animal.getTamanio());
            pstmt.setString(9, animal.getDescripcionCaracter());
            pstmt.setString(10, animal.getHistorialMedico());
            pstmt.setString(11, animal.getNecesidadesEspeciales());
            pstmt.setString(12, animal.getFotoUrl());
            pstmt.setString(13, animal.getEstadoAdopcion() != null ? animal.getEstadoAdopcion().name() : Animal.EstadoAdopcion.DISPONIBLE.name());
            setNullableInt(pstmt, 14, animal.getIdUsuarioResponsable());
            pstmt.setInt(15, animal.getId());
            
            int affectedRows = pstmt.executeUpdate();
            System.out.println(LOG_PREFIX + "Filas afectadas por actualización de animal: " + affectedRows);
            return affectedRows > 0;
        } catch (SQLException e) {
            handleSQLException("actualizarAnimal", String.valueOf(animal.getId()), e);
        }
        return false;
    }

    public boolean eliminarAnimal(int id) {
        String sql = "DELETE FROM animales WHERE id = ?";
        System.out.println(LOG_PREFIX + "Eliminando animal ID: " + id);
        Connection conn = Conexion.getConnection();
        if (conn == null) {
            System.err.println(LOG_PREFIX + "eliminarAnimal - No se pudo obtener conexión.");
            return false;
        }
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            int affectedRows = pstmt.executeUpdate();
            System.out.println(LOG_PREFIX + "Filas afectadas por eliminación de animal: " + affectedRows);
            return affectedRows > 0;
        } catch (SQLException e) {
            handleSQLException("eliminarAnimal", String.valueOf(id), e);
        }
        return false;
    }
    
    // Helper para setear Integers que pueden ser null
    private void setNullableInt(PreparedStatement pstmt, int parameterIndex, Integer value) throws SQLException {
        if (value != null) {
            pstmt.setInt(parameterIndex, value);
        } else {
            pstmt.setNull(parameterIndex, Types.INTEGER);
        }
    }

    private Animal mapResultSetToAnimal(ResultSet rs) throws SQLException {
        Animal animal = new Animal();
        animal.setId(rs.getInt("id"));
        animal.setNombre(rs.getString("nombre"));
        animal.setEspecie(rs.getString("especie"));
        animal.setRaza(rs.getString("raza"));
        animal.setEdadEstimadaAnios(rs.getObject("edad_estimada_anios", Integer.class));
        animal.setEdadEstimadaMeses(rs.getObject("edad_estimada_meses", Integer.class));
        animal.setGenero(rs.getString("genero"));
        animal.setColor(rs.getString("color"));
        animal.setTamanio(rs.getString("tamanio"));
        animal.setDescripcionCaracter(rs.getString("descripcion_caracter"));
        animal.setHistorialMedico(rs.getString("historial_medico"));
        animal.setNecesidadesEspeciales(rs.getString("necesidades_especiales"));
        animal.setFechaIngreso(rs.getTimestamp("fecha_ingreso"));
        animal.setFotoUrl(rs.getString("foto_url"));
        try {
            String estadoDb = rs.getString("estado_adopcion");
            if (estadoDb != null) {
                animal.setEstadoAdopcion(Animal.EstadoAdopcion.valueOf(estadoDb.toUpperCase()));
            } else {
                 animal.setEstadoAdopcion(Animal.EstadoAdopcion.DISPONIBLE);
            }
        } catch (IllegalArgumentException e) {
             System.err.println(LOG_PREFIX + "Estado de adopción desconocido en DB: '" + rs.getString("estado_adopcion") + "' para animal ID: " + rs.getInt("id") + ". Asignando DISPONIBLE.");
             animal.setEstadoAdopcion(Animal.EstadoAdopcion.DISPONIBLE);
        }
        animal.setIdUsuarioResponsable(rs.getObject("id_usuario_responsable", Integer.class));
        return animal;
    }

    private void handleSQLException(String methodName, String contextInfo, SQLException e) {
        System.err.println(LOG_PREFIX + "Error SQL en " + methodName + (contextInfo != null ? " para '" + contextInfo + "'" : "") + ": " + e.getMessage());
        e.printStackTrace();
    }
    // TODO: Implementar métodos de filtrado para animales
}