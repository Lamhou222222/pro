package repository;

import model.Donacion;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DonacionRepository {
    private final String LOG_PREFIX = "REPO_DONACION: [TEXTO PLANO CONTEXTO] ";

    public Donacion crearDonacion(Donacion donacion) {
        String sql = "INSERT INTO donaciones (id_usuario_donante, nombre_donante_anonimo, monto, tipo_donacion, descripcion_items) VALUES (?, ?, ?, ?, ?)";
        System.out.println(LOG_PREFIX + "Creando donación. Tipo: " + donacion.getTipoDonacion());
        Connection conn = Conexion.getConnection();
        if (conn == null) {
            System.err.println(LOG_PREFIX + "crearDonacion - No se pudo obtener conexión.");
            return null;
        }
        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            if (donacion.getIdUsuarioDonante() != null) pstmt.setInt(1, donacion.getIdUsuarioDonante()); else pstmt.setNull(1, Types.INTEGER);
            pstmt.setString(2, donacion.getNombreDonanteAnonimo());
            if (donacion.getMonto() != null) pstmt.setBigDecimal(3, donacion.getMonto()); else pstmt.setNull(3, Types.DECIMAL);
            pstmt.setString(4, donacion.getTipoDonacion());
            pstmt.setString(5, donacion.getDescripcionItems());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        donacion.setId(generatedKeys.getInt(1));
                        // Para obtener la fecha generada por DB
                        Donacion inserted = buscarPorId(donacion.getId()); // Puede ser ineficiente
                        if(inserted != null) donacion.setFechaDonacion(inserted.getFechaDonacion());
                        
                        System.out.println(LOG_PREFIX + "Donación creada con ID: " + donacion.getId());
                        return donacion;
                    }
                }
            }
        } catch (SQLException e) {
            handleSQLException("crearDonacion", "Tipo:" + donacion.getTipoDonacion(), e);
        }
        return null;
    }

    public Donacion buscarPorId(int id) {
        String sql = "SELECT * FROM donaciones WHERE id = ?";
        System.out.println(LOG_PREFIX + "Buscando donación por ID: " + id);
        Connection conn = Conexion.getConnection();
        if (conn == null) {
            System.err.println(LOG_PREFIX + "buscarPorId - No se pudo obtener conexión.");
            return null;
        }
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    System.out.println(LOG_PREFIX + "Donación con ID " + id + " encontrada.");
                    return mapResultSetToDonacion(rs);
                } else {
                    System.out.println(LOG_PREFIX + "Donación con ID " + id + " NO encontrada.");
                }
            }
        } catch (SQLException e) {
            handleSQLException("buscarPorId", String.valueOf(id), e);
        }
        return null;
    }

    public List<Donacion> listarTodas() {
        List<Donacion> donaciones = new ArrayList<>();
        String sql = "SELECT * FROM donaciones ORDER BY fecha_donacion DESC";
        System.out.println(LOG_PREFIX + "Listando todas las donaciones.");
        Connection conn = Conexion.getConnection();
        if (conn == null) {
            System.err.println(LOG_PREFIX + "listarTodas - No se pudo obtener conexión.");
            return donaciones;
        }
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                donaciones.add(mapResultSetToDonacion(rs));
            }
            System.out.println(LOG_PREFIX + "Total donaciones listadas: " + donaciones.size());
        } catch (SQLException e) {
            handleSQLException("listarTodas", null, e);
        }
        return donaciones;
    }
    
    public List<Donacion> listarPorUsuario(int idUsuarioDonante) {
        List<Donacion> donaciones = new ArrayList<>();
        String sql = "SELECT * FROM donaciones WHERE id_usuario_donante = ? ORDER BY fecha_donacion DESC";
        System.out.println(LOG_PREFIX + "Listando donaciones para usuario ID: " + idUsuarioDonante);
        Connection conn = Conexion.getConnection();
        if (conn == null) {
            System.err.println(LOG_PREFIX + "listarPorUsuario - No se pudo obtener conexión.");
            return donaciones;
        }
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idUsuarioDonante);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    donaciones.add(mapResultSetToDonacion(rs));
                }
            }
            System.out.println(LOG_PREFIX + "Total donaciones listadas para usuario " + idUsuarioDonante + ": " + donaciones.size());
        } catch (SQLException e) {
            handleSQLException("listarPorUsuario", "UserID:" + idUsuarioDonante, e);
        }
        return donaciones;
    }

    public boolean actualizarDonacion(Donacion donacion) {
        String sql = "UPDATE donaciones SET id_usuario_donante = ?, nombre_donante_anonimo = ?, monto = ?, tipo_donacion = ?, descripcion_items = ? WHERE id = ?";
        System.out.println(LOG_PREFIX + "Actualizando donación ID: " + donacion.getId());
        Connection conn = Conexion.getConnection();
        if (conn == null) {
            System.err.println(LOG_PREFIX + "actualizarDonacion - No se pudo obtener conexión.");
            return false;
        }
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            if (donacion.getIdUsuarioDonante() != null) pstmt.setInt(1, donacion.getIdUsuarioDonante()); else pstmt.setNull(1, Types.INTEGER);
            pstmt.setString(2, donacion.getNombreDonanteAnonimo());
            if (donacion.getMonto() != null) pstmt.setBigDecimal(3, donacion.getMonto()); else pstmt.setNull(3, Types.DECIMAL);
            pstmt.setString(4, donacion.getTipoDonacion());
            pstmt.setString(5, donacion.getDescripcionItems());
            pstmt.setInt(6, donacion.getId());
            
            int affectedRows = pstmt.executeUpdate();
            System.out.println(LOG_PREFIX + "Filas afectadas por actualización de donación: " + affectedRows);
            return affectedRows > 0;
        } catch (SQLException e) {
            handleSQLException("actualizarDonacion", "DonID:" + donacion.getId(), e);
        }
        return false;
    }

    public boolean eliminarDonacion(int id) {
        String sql = "DELETE FROM donaciones WHERE id = ?";
        System.out.println(LOG_PREFIX + "Eliminando donación ID: " + id);
        Connection conn = Conexion.getConnection();
        if (conn == null) {
            System.err.println(LOG_PREFIX + "eliminarDonacion - No se pudo obtener conexión.");
            return false;
        }
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            int affectedRows = pstmt.executeUpdate();
            System.out.println(LOG_PREFIX + "Filas afectadas por eliminación de donación: " + affectedRows);
            return affectedRows > 0;
        } catch (SQLException e) {
            handleSQLException("eliminarDonacion", String.valueOf(id), e);
        }
        return false;
    }

    private Donacion mapResultSetToDonacion(ResultSet rs) throws SQLException {
        Donacion donacion = new Donacion();
        donacion.setId(rs.getInt("id"));
        donacion.setIdUsuarioDonante(rs.getObject("id_usuario_donante", Integer.class));
        donacion.setNombreDonanteAnonimo(rs.getString("nombre_donante_anonimo"));
        donacion.setMonto(rs.getBigDecimal("monto"));
        donacion.setTipoDonacion(rs.getString("tipo_donacion"));
        donacion.setDescripcionItems(rs.getString("descripcion_items"));
        donacion.setFechaDonacion(rs.getTimestamp("fecha_donacion"));
        return donacion;
    }

    private void handleSQLException(String methodName, String contextInfo, SQLException e) {
        System.err.println(LOG_PREFIX + "Error SQL en " + methodName + (contextInfo != null ? " para '" + contextInfo + "'" : "") + ": " + e.getMessage());
        e.printStackTrace();
    }
}