package repository;

import model.SolicitudAdopcion;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SolicitudAdopcionRepository {
    private final String LOG_PREFIX = "REPO_SOL_ADOP: [TEXTO PLANO CONTEXTO] ";

    public SolicitudAdopcion crearSolicitud(SolicitudAdopcion solicitud) {
        String sql = "INSERT INTO solicitudes_adopcion (id_animal, id_usuario_solicitante, estado, motivacion, notas_admin) VALUES (?, ?, ?, ?, ?)";
        System.out.println(LOG_PREFIX + "Creando solicitud para animal ID: " + solicitud.getIdAnimal() + " por usuario ID: " + solicitud.getIdUsuarioSolicitante());
        Connection conn = Conexion.getConnection();
        if (conn == null) {
            System.err.println(LOG_PREFIX + "crearSolicitud - No se pudo obtener conexión.");
            return null;
        }
        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, solicitud.getIdAnimal());
            pstmt.setInt(2, solicitud.getIdUsuarioSolicitante());
            pstmt.setString(3, solicitud.getEstado().name());
            pstmt.setString(4, solicitud.getMotivacion());
            pstmt.setString(5, solicitud.getNotasAdmin());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        solicitud.setId(generatedKeys.getInt(1));
                        SolicitudAdopcion inserted = buscarPorId(solicitud.getId());
                        if(inserted != null) solicitud.setFechaSolicitud(inserted.getFechaSolicitud());
                        System.out.println(LOG_PREFIX + "Solicitud creada con ID: " + solicitud.getId());
                        return solicitud;
                    }
                }
            }
        } catch (SQLException e) {
            handleSQLException("crearSolicitud", "AnimalID:" + solicitud.getIdAnimal(), e);
        }
        return null;
    }

    public SolicitudAdopcion buscarPorId(int id) {
        String sql = "SELECT * FROM solicitudes_adopcion WHERE id = ?";
        System.out.println(LOG_PREFIX + "Buscando solicitud por ID: " + id);
        Connection conn = Conexion.getConnection();
        if (conn == null) {
            System.err.println(LOG_PREFIX + "buscarPorId - No se pudo obtener conexión.");
            return null;
        }
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    System.out.println(LOG_PREFIX + "Solicitud con ID " + id + " encontrada.");
                    return mapResultSetToSolicitudAdopcion(rs);
                } else {
                    System.out.println(LOG_PREFIX + "Solicitud con ID " + id + " NO encontrada.");
                }
            }
        } catch (SQLException e) {
            handleSQLException("buscarPorId", String.valueOf(id), e);
        }
        return null;
    }

    public List<SolicitudAdopcion> listarTodas() {
        List<SolicitudAdopcion> solicitudes = new ArrayList<>();
        String sql = "SELECT * FROM solicitudes_adopcion ORDER BY fecha_solicitud DESC";
        System.out.println(LOG_PREFIX + "Listando todas las solicitudes.");
        Connection conn = Conexion.getConnection();
        if (conn == null) {
            System.err.println(LOG_PREFIX + "listarTodas - No se pudo obtener conexión.");
            return solicitudes;
        }
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                solicitudes.add(mapResultSetToSolicitudAdopcion(rs));
            }
            System.out.println(LOG_PREFIX + "Total solicitudes listadas: " + solicitudes.size());
        } catch (SQLException e) {
            handleSQLException("listarTodas", null, e);
        }
        return solicitudes;
    }

    public List<SolicitudAdopcion> listarPorUsuario(int idUsuarioSolicitante) {
        List<SolicitudAdopcion> solicitudes = new ArrayList<>();
        String sql = "SELECT * FROM solicitudes_adopcion WHERE id_usuario_solicitante = ? ORDER BY fecha_solicitud DESC";
        System.out.println(LOG_PREFIX + "Listando solicitudes para usuario ID: " + idUsuarioSolicitante);
        Connection conn = Conexion.getConnection();
        if (conn == null) {
            System.err.println(LOG_PREFIX + "listarPorUsuario - No se pudo obtener conexión.");
            return solicitudes;
        }
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idUsuarioSolicitante);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    solicitudes.add(mapResultSetToSolicitudAdopcion(rs));
                }
            }
            System.out.println(LOG_PREFIX + "Total solicitudes listadas para usuario " + idUsuarioSolicitante + ": " + solicitudes.size());
        } catch (SQLException e) {
            handleSQLException("listarPorUsuario", "UserID:" + idUsuarioSolicitante, e);
        }
        return solicitudes;
    }
    
    public List<SolicitudAdopcion> listarPorAnimal(int idAnimal) {
        List<SolicitudAdopcion> solicitudes = new ArrayList<>();
        String sql = "SELECT * FROM solicitudes_adopcion WHERE id_animal = ? ORDER BY fecha_solicitud DESC";
        System.out.println(LOG_PREFIX + "Listando solicitudes para animal ID: " + idAnimal);
        Connection conn = Conexion.getConnection();
        if (conn == null) {
            System.err.println(LOG_PREFIX + "listarPorAnimal - No se pudo obtener conexión.");
            return solicitudes;
        }
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idAnimal);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    solicitudes.add(mapResultSetToSolicitudAdopcion(rs));
                }
            }
            System.out.println(LOG_PREFIX + "Total solicitudes listadas para animal " + idAnimal + ": " + solicitudes.size());
        } catch (SQLException e) {
            handleSQLException("listarPorAnimal", "AnimalID:" + idAnimal, e);
        }
        return solicitudes;
    }
    
    public List<SolicitudAdopcion> filtrarSolicitudes(Integer idAnimal, Integer idUsuario, String estado) {
        List<SolicitudAdopcion> solicitudes = new ArrayList<>();
        StringBuilder sqlBuilder = new StringBuilder("SELECT * FROM solicitudes_adopcion WHERE 1=1");
        List<Object> params = new ArrayList<>();
        System.out.println(LOG_PREFIX + "Filtrando solicitudes. AnimalID: " + idAnimal + ", UsuarioID: " + idUsuario + ", Estado: " + estado);

        if (idAnimal != null) { sqlBuilder.append(" AND id_animal = ?"); params.add(idAnimal); }
        if (idUsuario != null) { sqlBuilder.append(" AND id_usuario_solicitante = ?"); params.add(idUsuario); }
        if (estado != null && !estado.isEmpty()) {
            try {
                 SolicitudAdopcion.EstadoSolicitud.valueOf(estado.toUpperCase());
                 sqlBuilder.append(" AND estado = ?");
                 params.add(estado.toUpperCase());
            } catch (IllegalArgumentException e) {
                System.err.println(LOG_PREFIX + "Estado de solicitud inválido para filtro: " + estado);
            }
        }
        sqlBuilder.append(" ORDER BY fecha_solicitud DESC");

        Connection conn = Conexion.getConnection();
        if (conn == null) {
            System.err.println(LOG_PREFIX + "filtrarSolicitudes - No se pudo obtener conexión.");
            return solicitudes;
        }
        try (PreparedStatement pstmt = conn.prepareStatement(sqlBuilder.toString())) {
            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    solicitudes.add(mapResultSetToSolicitudAdopcion(rs));
                }
            }
            System.out.println(LOG_PREFIX + "Total solicitudes filtradas: " + solicitudes.size());
        } catch (SQLException e) {
            handleSQLException("filtrarSolicitudes", null, e);
        }
        return solicitudes;
    }

    public boolean actualizarSolicitud(SolicitudAdopcion solicitud) {
        String sql = "UPDATE solicitudes_adopcion SET id_animal = ?, id_usuario_solicitante = ?, estado = ?, motivacion = ?, notas_admin = ? WHERE id = ?";
        System.out.println(LOG_PREFIX + "Actualizando solicitud ID: " + solicitud.getId());
        Connection conn = Conexion.getConnection();
        if (conn == null) {
            System.err.println(LOG_PREFIX + "actualizarSolicitud - No se pudo obtener conexión.");
            return false;
        }
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, solicitud.getIdAnimal());
            pstmt.setInt(2, solicitud.getIdUsuarioSolicitante());
            pstmt.setString(3, solicitud.getEstado().name());
            pstmt.setString(4, solicitud.getMotivacion());
            pstmt.setString(5, solicitud.getNotasAdmin());
            pstmt.setInt(6, solicitud.getId());
            
            int affectedRows = pstmt.executeUpdate();
            System.out.println(LOG_PREFIX + "Filas afectadas por actualización de solicitud: " + affectedRows);
            return affectedRows > 0;
        } catch (SQLException e) {
            handleSQLException("actualizarSolicitud", "SolID:" + solicitud.getId(), e);
        }
        return false;
    }
    
    public boolean actualizarEstadoSolicitud(int idSolicitud, SolicitudAdopcion.EstadoSolicitud nuevoEstado, String notasAdmin) {
        String sql = "UPDATE solicitudes_adopcion SET estado = ?, notas_admin = ? WHERE id = ?";
        System.out.println(LOG_PREFIX + "Actualizando estado de solicitud ID: " + idSolicitud + " a " + nuevoEstado);
        Connection conn = Conexion.getConnection();
        if (conn == null) {
            System.err.println(LOG_PREFIX + "actualizarEstadoSolicitud - No se pudo obtener conexión.");
            return false;
        }
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nuevoEstado.name());
            pstmt.setString(2, notasAdmin); // Puede ser null
            pstmt.setInt(3, idSolicitud);
            
            int affectedRows = pstmt.executeUpdate();
            System.out.println(LOG_PREFIX + "Filas afectadas por actualización de estado: " + affectedRows);
            return affectedRows > 0;
        } catch (SQLException e) {
            handleSQLException("actualizarEstadoSolicitud", "SolID:" + idSolicitud, e);
        }
        return false;
    }

    public boolean eliminarSolicitud(int id) {
        String sql = "DELETE FROM solicitudes_adopcion WHERE id = ?";
        System.out.println(LOG_PREFIX + "Eliminando solicitud ID: " + id);
        Connection conn = Conexion.getConnection();
        if (conn == null) {
            System.err.println(LOG_PREFIX + "eliminarSolicitud - No se pudo obtener conexión.");
            return false;
        }
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            int affectedRows = pstmt.executeUpdate();
            System.out.println(LOG_PREFIX + "Filas afectadas por eliminación de solicitud: " + affectedRows);
            return affectedRows > 0;
        } catch (SQLException e) {
            handleSQLException("eliminarSolicitud", String.valueOf(id), e);
        }
        return false;
    }

    private SolicitudAdopcion mapResultSetToSolicitudAdopcion(ResultSet rs) throws SQLException {
        SolicitudAdopcion solicitud = new SolicitudAdopcion();
        solicitud.setId(rs.getInt("id"));
        solicitud.setIdAnimal(rs.getInt("id_animal"));
        solicitud.setIdUsuarioSolicitante(rs.getInt("id_usuario_solicitante"));
        solicitud.setFechaSolicitud(rs.getTimestamp("fecha_solicitud"));
        try {
            String estadoDb = rs.getString("estado");
            if (estadoDb != null) {
                solicitud.setEstado(SolicitudAdopcion.EstadoSolicitud.valueOf(estadoDb.toUpperCase()));
            } else {
                solicitud.setEstado(SolicitudAdopcion.EstadoSolicitud.ENVIADA); 
            }
        } catch (IllegalArgumentException e) {
            System.err.println(LOG_PREFIX + "Estado de solicitud desconocido en DB: '" + rs.getString("estado") + "' para solicitud ID: " + rs.getInt("id") + ". Asignando ENVIADA.");
            solicitud.setEstado(SolicitudAdopcion.EstadoSolicitud.ENVIADA);
        }
        solicitud.setMotivacion(rs.getString("motivacion"));
        solicitud.setNotasAdmin(rs.getString("notas_admin"));
        return solicitud;
    }

    private void handleSQLException(String methodName, String contextInfo, SQLException e) {
        System.err.println(LOG_PREFIX + "Error SQL en " + methodName + (contextInfo != null ? " para '" + contextInfo + "'" : "") + ": " + e.getMessage());
        e.printStackTrace();
    }
}