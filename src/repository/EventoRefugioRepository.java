package repository;

import model.EventoRefugio;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EventoRefugioRepository {
    private final String LOG_PREFIX = "REPO_EVENTO: [TEXTO PLANO CONTEXTO] ";

    public EventoRefugio crearEvento(EventoRefugio evento) {
        String sql = "INSERT INTO eventos_refugio (nombre_evento, descripcion_evento, fecha_inicio_evento, fecha_fin_evento, ubicacion, tipo_evento, id_usuario_organizador) VALUES (?, ?, ?, ?, ?, ?, ?)";
        System.out.println(LOG_PREFIX + "Creando evento: " + evento.getNombreEvento());
        Connection conn = Conexion.getConnection();
        if (conn == null) {
            System.err.println(LOG_PREFIX + "crearEvento - No se pudo obtener conexión.");
            return null;
        }
        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, evento.getNombreEvento());
            pstmt.setString(2, evento.getDescripcionEvento());
            pstmt.setTimestamp(3, evento.getFechaInicioEvento());
            if (evento.getFechaFinEvento() != null) pstmt.setTimestamp(4, evento.getFechaFinEvento()); else pstmt.setNull(4, Types.TIMESTAMP);
            pstmt.setString(5, evento.getUbicacion());
            pstmt.setString(6, evento.getTipoEvento());
            if (evento.getIdUsuarioOrganizador() != null) pstmt.setInt(7, evento.getIdUsuarioOrganizador()); else pstmt.setNull(7, Types.INTEGER);

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        evento.setId(generatedKeys.getInt(1));
                        System.out.println(LOG_PREFIX + "Evento '" + evento.getNombreEvento() + "' creado con ID: " + evento.getId());
                        return evento;
                    }
                }
            }
        } catch (SQLException e) {
            handleSQLException("crearEvento", evento.getNombreEvento(), e);
        }
        return null;
    }

    public EventoRefugio buscarPorId(int id) {
        String sql = "SELECT * FROM eventos_refugio WHERE id = ?";
        System.out.println(LOG_PREFIX + "Buscando evento por ID: " + id);
        Connection conn = Conexion.getConnection();
        if (conn == null) {
            System.err.println(LOG_PREFIX + "buscarPorId - No se pudo obtener conexión.");
            return null;
        }
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    System.out.println(LOG_PREFIX + "Evento con ID " + id + " encontrado.");
                    return mapResultSetToEventoRefugio(rs);
                } else {
                    System.out.println(LOG_PREFIX + "Evento con ID " + id + " NO encontrado.");
                }
            }
        } catch (SQLException e) {
            handleSQLException("buscarPorId", String.valueOf(id), e);
        }
        return null;
    }

    public List<EventoRefugio> listarTodos() {
        List<EventoRefugio> eventos = new ArrayList<>();
        String sql = "SELECT * FROM eventos_refugio ORDER BY fecha_inicio_evento DESC";
        System.out.println(LOG_PREFIX + "Listando todos los eventos.");
        Connection conn = Conexion.getConnection();
        if (conn == null) {
            System.err.println(LOG_PREFIX + "listarTodos - No se pudo obtener conexión.");
            return eventos;
        }
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                eventos.add(mapResultSetToEventoRefugio(rs));
            }
            System.out.println(LOG_PREFIX + "Total eventos listados: " + eventos.size());
        } catch (SQLException e) {
            handleSQLException("listarTodos", null, e);
        }
        return eventos;
    }
    
    public List<EventoRefugio> filtrarEventos(String nombreEvento, String tipoEvento, Integer idOrganizador) {
        List<EventoRefugio> eventos = new ArrayList<>();
        StringBuilder sqlBuilder = new StringBuilder("SELECT * FROM eventos_refugio WHERE 1=1");
        List<Object> params = new ArrayList<>();
        System.out.println(LOG_PREFIX + "Filtrando eventos. Nombre: " + nombreEvento + ", Tipo: " + tipoEvento + ", OrganizadorID: " + idOrganizador);

        if (nombreEvento != null && !nombreEvento.isEmpty()) {
            sqlBuilder.append(" AND nombre_evento LIKE ?");
            params.add("%" + nombreEvento + "%");
        }
        if (tipoEvento != null && !tipoEvento.isEmpty()) {
            sqlBuilder.append(" AND tipo_evento = ?");
            params.add(tipoEvento);
        }
        if (idOrganizador != null) {
            sqlBuilder.append(" AND id_usuario_organizador = ?");
            params.add(idOrganizador);
        }
        sqlBuilder.append(" ORDER BY fecha_inicio_evento DESC");

        Connection conn = Conexion.getConnection();
        if (conn == null) {
            System.err.println(LOG_PREFIX + "filtrarEventos - No se pudo obtener conexión.");
            return eventos;
        }
        try (PreparedStatement pstmt = conn.prepareStatement(sqlBuilder.toString())) {
            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    eventos.add(mapResultSetToEventoRefugio(rs));
                }
            }
            System.out.println(LOG_PREFIX + "Total eventos filtrados: " + eventos.size());
        } catch (SQLException e) {
            handleSQLException("filtrarEventos", null, e);
        }
        return eventos;
    }

    public boolean actualizarEvento(EventoRefugio evento) {
        String sql = "UPDATE eventos_refugio SET nombre_evento = ?, descripcion_evento = ?, fecha_inicio_evento = ?, fecha_fin_evento = ?, ubicacion = ?, tipo_evento = ?, id_usuario_organizador = ? WHERE id = ?";
        System.out.println(LOG_PREFIX + "Actualizando evento ID: " + evento.getId());
        Connection conn = Conexion.getConnection();
        if (conn == null) {
            System.err.println(LOG_PREFIX + "actualizarEvento - No se pudo obtener conexión.");
            return false;
        }
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, evento.getNombreEvento());
            pstmt.setString(2, evento.getDescripcionEvento());
            pstmt.setTimestamp(3, evento.getFechaInicioEvento());
            if (evento.getFechaFinEvento() != null) pstmt.setTimestamp(4, evento.getFechaFinEvento()); else pstmt.setNull(4, Types.TIMESTAMP);
            pstmt.setString(5, evento.getUbicacion());
            pstmt.setString(6, evento.getTipoEvento());
            if (evento.getIdUsuarioOrganizador() != null) pstmt.setInt(7, evento.getIdUsuarioOrganizador()); else pstmt.setNull(7, Types.INTEGER);
            pstmt.setInt(8, evento.getId());
            
            int affectedRows = pstmt.executeUpdate();
            System.out.println(LOG_PREFIX + "Filas afectadas por actualización de evento: " + affectedRows);
            return affectedRows > 0;
        } catch (SQLException e) {
            handleSQLException("actualizarEvento", String.valueOf(evento.getId()), e);
        }
        return false;
    }

    public boolean eliminarEvento(int id) {
        String sql = "DELETE FROM eventos_refugio WHERE id = ?";
        System.out.println(LOG_PREFIX + "Eliminando evento ID: " + id);
        Connection conn = Conexion.getConnection();
        if (conn == null) {
            System.err.println(LOG_PREFIX + "eliminarEvento - No se pudo obtener conexión.");
            return false;
        }
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            int affectedRows = pstmt.executeUpdate();
            System.out.println(LOG_PREFIX + "Filas afectadas por eliminación de evento: " + affectedRows);
            return affectedRows > 0;
        } catch (SQLException e) {
            handleSQLException("eliminarEvento", String.valueOf(id), e);
        }
        return false;
    }

    private EventoRefugio mapResultSetToEventoRefugio(ResultSet rs) throws SQLException {
        EventoRefugio evento = new EventoRefugio();
        evento.setId(rs.getInt("id"));
        evento.setNombreEvento(rs.getString("nombre_evento"));
        evento.setDescripcionEvento(rs.getString("descripcion_evento"));
        evento.setFechaInicioEvento(rs.getTimestamp("fecha_inicio_evento"));
        evento.setFechaFinEvento(rs.getTimestamp("fecha_fin_evento")); // Puede ser null
        evento.setUbicacion(rs.getString("ubicacion"));
        evento.setTipoEvento(rs.getString("tipo_evento"));
        evento.setIdUsuarioOrganizador(rs.getObject("id_usuario_organizador", Integer.class)); // Maneja NULL
        return evento;
    }

    private void handleSQLException(String methodName, String contextInfo, SQLException e) {
        System.err.println(LOG_PREFIX + "Error SQL en " + methodName + (contextInfo != null ? " para '" + contextInfo + "'" : "") + ": " + e.getMessage());
        e.printStackTrace();
    }
}