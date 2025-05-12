package repository;

import model.ActividadVoluntariado;
import java.sql.*; // Contiene java.sql.Date
import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal; // Asegurar import

public class VoluntariadoRepository {
    private final String LOG_PREFIX = "REPO_VOL: [TEXTO PLANO CONTEXTO] ";

    public ActividadVoluntariado crearActividad(ActividadVoluntariado actividad) {
        String sql = "INSERT INTO voluntariado_actividades (id_voluntario, id_animal_asociado, fecha_actividad, tipo_actividad, duracion_horas, descripcion) VALUES (?, ?, ?, ?, ?, ?)";
        System.out.println(LOG_PREFIX + "Creando actividad para voluntario ID: " + actividad.getIdVoluntario());
        Connection conn = Conexion.getConnection();
        if (conn == null) {
            System.err.println(LOG_PREFIX + "crearActividad - No se pudo obtener conexión.");
            return null;
        }
        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, actividad.getIdVoluntario());
            if (actividad.getIdAnimalAsociado() != null) pstmt.setInt(2, actividad.getIdAnimalAsociado()); else pstmt.setNull(2, Types.INTEGER);
            
            // CAMBIO: Convertir java.util.Date a java.sql.Date
            if (actividad.getFechaActividad() != null) {
                pstmt.setDate(3, new java.sql.Date(actividad.getFechaActividad().getTime()));
            } else {
                pstmt.setNull(3, Types.DATE);
            }
            
            pstmt.setString(4, actividad.getTipoActividad());
            pstmt.setBigDecimal(5, actividad.getDuracionHoras());
            pstmt.setString(6, actividad.getDescripcion());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        actividad.setId(generatedKeys.getInt(1));
                        System.out.println(LOG_PREFIX + "Actividad creada con ID: " + actividad.getId());
                        return actividad;
                    }
                }
            }
        } catch (SQLException e) {
            handleSQLException("crearActividad", "VolID:" + actividad.getIdVoluntario(), e);
        }
        return null;
    }

    public boolean actualizarActividad(ActividadVoluntariado actividad) {
        String sql = "UPDATE voluntariado_actividades SET id_voluntario = ?, id_animal_asociado = ?, fecha_actividad = ?, tipo_actividad = ?, duracion_horas = ?, descripcion = ? WHERE id = ?";
        System.out.println(LOG_PREFIX + "Actualizando actividad ID: " + actividad.getId());
        Connection conn = Conexion.getConnection();
        if (conn == null) {
            System.err.println(LOG_PREFIX + "actualizarActividad - No se pudo obtener conexión.");
            return false;
        }
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, actividad.getIdVoluntario());
            if (actividad.getIdAnimalAsociado() != null) pstmt.setInt(2, actividad.getIdAnimalAsociado()); else pstmt.setNull(2, Types.INTEGER);

            // CAMBIO: Convertir java.util.Date a java.sql.Date
            if (actividad.getFechaActividad() != null) {
                pstmt.setDate(3, new java.sql.Date(actividad.getFechaActividad().getTime()));
            } else {
                pstmt.setNull(3, Types.DATE);
            }

            pstmt.setString(4, actividad.getTipoActividad());
            pstmt.setBigDecimal(5, actividad.getDuracionHoras());
            pstmt.setString(6, actividad.getDescripcion());
            pstmt.setInt(7, actividad.getId());
            
            int affectedRows = pstmt.executeUpdate();
            System.out.println(LOG_PREFIX + "Filas afectadas por actualización de actividad: " + affectedRows);
            return affectedRows > 0;
        } catch (SQLException e) {
            handleSQLException("actualizarActividad", "ActID:" + actividad.getId(), e);
        }
        return false;
    }

    private ActividadVoluntariado mapResultSetToActividadVoluntariado(ResultSet rs) throws SQLException {
        ActividadVoluntariado actividad = new ActividadVoluntariado();
        actividad.setId(rs.getInt("id"));
        actividad.setIdVoluntario(rs.getInt("id_voluntario"));
        actividad.setIdAnimalAsociado(rs.getObject("id_animal_asociado", Integer.class));
        
        // CAMBIO: java.sql.Date de la BD se convierte implícitamente a java.util.Date
        // al hacer getObject o si se lee como java.sql.Date y luego se asigna.
        // Para ser explícitos, si rs.getDate() devuelve java.sql.Date:
        java.sql.Date sqlDate = rs.getDate("fecha_actividad");
        if (sqlDate != null) {
            actividad.setFechaActividad(new java.util.Date(sqlDate.getTime()));
        } else {
            actividad.setFechaActividad(null);
        }
        
        actividad.setTipoActividad(rs.getString("tipo_actividad"));
        actividad.setDuracionHoras(rs.getBigDecimal("duracion_horas"));
        actividad.setDescripcion(rs.getString("descripcion"));
        return actividad;
    }

    // ... (buscarPorId, listarTodas, listarPorVoluntario, eliminarActividad, handleSQLException sin cambios en su lógica principal)
    // Asegúrate de que estos métodos existan y funcionen como en la versión anterior.
    // Los incluyo por completitud.

    public ActividadVoluntariado buscarPorId(int id) {
        String sql = "SELECT * FROM voluntariado_actividades WHERE id = ?";
        System.out.println(LOG_PREFIX + "Buscando actividad por ID: " + id);
        Connection conn = Conexion.getConnection();
        if (conn == null) {
            System.err.println(LOG_PREFIX + "buscarPorId - No se pudo obtener conexión.");
            return null;
        }
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    System.out.println(LOG_PREFIX + "Actividad con ID " + id + " encontrada.");
                    return mapResultSetToActividadVoluntariado(rs);
                } else {
                    System.out.println(LOG_PREFIX + "Actividad con ID " + id + " NO encontrada.");
                }
            }
        } catch (SQLException e) {
            handleSQLException("buscarPorId", String.valueOf(id), e);
        }
        return null;
    }

    public List<ActividadVoluntariado> listarTodas() {
        List<ActividadVoluntariado> actividades = new ArrayList<>();
        String sql = "SELECT * FROM voluntariado_actividades ORDER BY fecha_actividad DESC, id DESC";
        System.out.println(LOG_PREFIX + "Listando todas las actividades.");
        Connection conn = Conexion.getConnection();
        if (conn == null) {
            System.err.println(LOG_PREFIX + "listarTodas - No se pudo obtener conexión.");
            return actividades;
        }
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                actividades.add(mapResultSetToActividadVoluntariado(rs));
            }
            System.out.println(LOG_PREFIX + "Total actividades listadas: " + actividades.size());
        } catch (SQLException e) {
            handleSQLException("listarTodas", null, e);
        }
        return actividades;
    }
    
    public List<ActividadVoluntariado> listarPorVoluntario(int idVoluntario) {
        List<ActividadVoluntariado> actividades = new ArrayList<>();
        String sql = "SELECT * FROM voluntariado_actividades WHERE id_voluntario = ? ORDER BY fecha_actividad DESC, id DESC";
        System.out.println(LOG_PREFIX + "Listando actividades para voluntario ID: " + idVoluntario);
        Connection conn = Conexion.getConnection();
        if (conn == null) {
            System.err.println(LOG_PREFIX + "listarPorVoluntario - No se pudo obtener conexión.");
            return actividades;
        }
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idVoluntario);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    actividades.add(mapResultSetToActividadVoluntariado(rs));
                }
            }
            System.out.println(LOG_PREFIX + "Total actividades listadas para voluntario " + idVoluntario + ": " + actividades.size());
        } catch (SQLException e) {
            handleSQLException("listarPorVoluntario", "VolID:" + idVoluntario, e);
        }
        return actividades;
    }

    public boolean eliminarActividad(int id) {
        String sql = "DELETE FROM voluntariado_actividades WHERE id = ?";
        System.out.println(LOG_PREFIX + "Eliminando actividad ID: " + id);
        Connection conn = Conexion.getConnection();
        if (conn == null) {
            System.err.println(LOG_PREFIX + "eliminarActividad - No se pudo obtener conexión.");
            return false;
        }
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            int affectedRows = pstmt.executeUpdate();
            System.out.println(LOG_PREFIX + "Filas afectadas por eliminación de actividad: " + affectedRows);
            return affectedRows > 0;
        } catch (SQLException e) {
            handleSQLException("eliminarActividad", String.valueOf(id), e);
        }
        return false;
    }
    
    private void handleSQLException(String methodName, String contextInfo, SQLException e) {
        System.err.println(LOG_PREFIX + "Error SQL en " + methodName + (contextInfo != null ? " para '" + contextInfo + "'" : "") + ": " + e.getMessage());
        e.printStackTrace();
    }
}