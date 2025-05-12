package repository;

import model.Usuario;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioRepository {

    // --- MANEJO DE CONTRASEÑAS EN TEXTO PLANO ---
    private String internalProcessPassword(String plainPassword) {
        System.out.println("REPO_USER: [TEXTO PLANO] internalProcessPassword para registro/actualización, devolviendo password tal cual: '" + plainPassword + "'");
        return plainPassword;
    }

    public boolean verificarPassword(String plainPasswordIngresada, String passwordAlmacenadaEnDB) {
        System.out.println("REPO_USER: [TEXTO PLANO] Verificando Password. Ingresada: '" + plainPasswordIngresada + "', Almacenada en DB: '" + passwordAlmacenadaEnDB + "'");
        if (plainPasswordIngresada == null || passwordAlmacenadaEnDB == null) {
            System.out.println("REPO_USER: [TEXTO PLANO] - Una de las contraseñas es nula. Comparación falla.");
            return false;
        }
        boolean match = plainPasswordIngresada.equals(passwordAlmacenadaEnDB);
        System.out.println("REPO_USER: [TEXTO PLANO] - Password match? " + match);
        return match;
    }
    // --- FIN MANEJO DE CONTRASEÑAS EN TEXTO PLANO ---

    public Usuario registrarUsuario(Usuario usuario) {
        String plainPassword = usuario.getPasswordHash();
        if (plainPassword == null || plainPassword.isEmpty()) {
            System.err.println("REPO_USER: [TEXTO PLANO] Intento de registrar usuario '" + usuario.getUsername() + "' sin contraseña.");
            return null;
        }
        String passwordParaGuardar = internalProcessPassword(plainPassword);

        String sql = "INSERT INTO usuarios (username, password_hash, nombre_completo, email, telefono, rol, activo, disponibilidad_horaria, areas_interes, direccion, tipo_vivienda, experiencia_animales) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        System.out.println("REPO_USER: [TEXTO PLANO] Intentando registrar usuario: " + usuario.getUsername());

        Connection conn = Conexion.getConnection();
        if (conn == null) {
            System.err.println("REPO_USER: [TEXTO PLANO] registrarUsuario - No se pudo obtener conexión a la BD.");
            return null;
        }

        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            fillPreparedStatementForUsuarioInsert(pstmt, usuario, passwordParaGuardar);

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        usuario.setId(generatedKeys.getInt(1));
                        usuario.setActivo(false);
                        usuario.setPasswordHash(passwordParaGuardar);
                        System.out.println("REPO_USER: [TEXTO PLANO] Usuario '" + usuario.getUsername() + "' registrado con ID: " + usuario.getId());
                        return usuario;
                    }
                }
            }
        } catch (SQLException e) {
            handleSQLException("registrarUsuario", usuario.getUsername(), e);
        }
        System.out.println("REPO_USER: [TEXTO PLANO] Falla al registrar usuario: " + usuario.getUsername());
        return null;
    }

    public Usuario buscarPorUsername(String username) {
        String sql = "SELECT * FROM usuarios WHERE username = ?";
        System.out.println("REPO_USER: [TEXTO PLANO] Buscando usuario por username: '" + username + "'");
        Connection conn = Conexion.getConnection();
        if (conn == null) {
            System.err.println("REPO_USER: [TEXTO PLANO] buscarPorUsername - No se pudo obtener conexión a la BD.");
            return null;
        }
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    System.out.println("REPO_USER: [TEXTO PLANO] Usuario '" + username + "' encontrado en DB.");
                    return mapResultSetToUsuario(rs);
                } else {
                    System.out.println("REPO_USER: [TEXTO PLANO] Usuario '" + username + "' NO encontrado en DB.");
                }
            }
        } catch (SQLException e) {
            handleSQLException("buscarPorUsername", username, e);
        }
        return null;
    }

    public Usuario buscarPorId(int id) {
        String sql = "SELECT * FROM usuarios WHERE id = ?";
        System.out.println("REPO_USER: [TEXTO PLANO] Buscando usuario por ID: " + id);
        Connection conn = Conexion.getConnection();
        if (conn == null) {
            System.err.println("REPO_USER: [TEXTO PLANO] buscarPorId - No se pudo obtener conexión.");
            return null;
        }
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    System.out.println("REPO_USER: [TEXTO PLANO] Usuario con ID " + id + " encontrado.");
                    return mapResultSetToUsuario(rs);
                } else {
                    System.out.println("REPO_USER: [TEXTO PLANO] Usuario con ID " + id + " NO encontrado.");
                }
            }
        } catch (SQLException e) {
            handleSQLException("buscarPorId", String.valueOf(id), e);
        }
        return null;
    }

    public boolean actualizarUsuario(Usuario usuario) {
        String sql = "UPDATE usuarios SET nombre_completo = ?, email = ?, telefono = ?, rol = ?, " +
                     "disponibilidad_horaria = ?, areas_interes = ?, direccion = ?, tipo_vivienda = ?, experiencia_animales = ?, activo = ? " +
                     "WHERE id = ?";
        System.out.println("REPO_USER: [TEXTO PLANO] Actualizando datos (sin password) del usuario ID: " + usuario.getId());
        Connection conn = Conexion.getConnection();
        if (conn == null) {
            System.err.println("REPO_USER: [TEXTO PLANO] actualizarUsuario - No se pudo obtener conexión.");
            return false;
        }
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            fillPreparedStatementForUsuarioUpdate(pstmt, usuario);
            
            int affectedRows = pstmt.executeUpdate();
            System.out.println("REPO_USER: [TEXTO PLANO] Filas afectadas por actualización de datos de usuario: " + affectedRows);
            return affectedRows > 0;
        } catch (SQLException e) {
            handleSQLException("actualizarUsuario", String.valueOf(usuario.getId()), e);
        }
        return false;
    }

    public boolean actualizarPassword(int usuarioId, String nuevaPasswordEnCrudo) {
        if (nuevaPasswordEnCrudo == null || nuevaPasswordEnCrudo.isEmpty()) {
            System.err.println("REPO_USER: [TEXTO PLANO] Intento de actualizar a una contraseña vacía para usuario ID: " + usuarioId);
            return false;
        }
        String sql = "UPDATE usuarios SET password_hash = ? WHERE id = ?";
        String passwordParaGuardar = internalProcessPassword(nuevaPasswordEnCrudo);
        System.out.println("REPO_USER: [TEXTO PLANO] Actualizando password para usuario ID: " + usuarioId);
        Connection conn = Conexion.getConnection();
        if (conn == null) {
            System.err.println("REPO_USER: [TEXTO PLANO] actualizarPassword - No se pudo obtener conexión.");
            return false;
        }
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, passwordParaGuardar);
            pstmt.setInt(2, usuarioId);
            int affectedRows = pstmt.executeUpdate();
            System.out.println("REPO_USER: [TEXTO PLANO] Filas afectadas por actualización de password: " + affectedRows);
            return affectedRows > 0;
        } catch (SQLException e) {
            handleSQLException("actualizarPassword", String.valueOf(usuarioId), e);
        }
        return false;
    }

    public boolean eliminarUsuario(int usuarioId) {
        String sql = "DELETE FROM usuarios WHERE id = ?";
        System.out.println("REPO_USER: [TEXTO PLANO] Eliminando usuario ID: " + usuarioId);
        Connection conn = Conexion.getConnection();
        if (conn == null) {
            System.err.println("REPO_USER: [TEXTO PLANO] eliminarUsuario - No se pudo obtener conexión.");
            return false;
        }
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, usuarioId);
            int affectedRows = pstmt.executeUpdate();
            System.out.println("REPO_USER: [TEXTO PLANO] Filas afectadas por eliminación de usuario: " + affectedRows);
            return affectedRows > 0;
        } catch (SQLException e) {
            handleSQLException("eliminarUsuario", String.valueOf(usuarioId), e);
        }
        return false;
    }

    public boolean cambiarEstadoActivo(int usuarioId, boolean activo) {
        String sql = "UPDATE usuarios SET activo = ? WHERE id = ?";
        System.out.println("REPO_USER: [TEXTO PLANO] Cambiando estado activo a " + activo + " para usuario ID: " + usuarioId);
        Connection conn = Conexion.getConnection();
        if (conn == null) {
            System.err.println("REPO_USER: [TEXTO PLANO] cambiarEstadoActivo - No se pudo obtener conexión.");
            return false;
        }
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setBoolean(1, activo);
            pstmt.setInt(2, usuarioId);
            int affectedRows = pstmt.executeUpdate();
            System.out.println("REPO_USER: [TEXTO PLANO] Filas afectadas por cambio de estado activo: " + affectedRows);
            return affectedRows > 0;
        } catch (SQLException e) {
            handleSQLException("cambiarEstadoActivo", String.valueOf(usuarioId), e);
        }
        return false;
    }

    public List<Usuario> listarTodosLosUsuarios() {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT * FROM usuarios ORDER BY nombre_completo";
        System.out.println("REPO_USER: [TEXTO PLANO] Listando todos los usuarios.");
        Connection conn = Conexion.getConnection();
        if (conn == null) {
            System.err.println("REPO_USER: [TEXTO PLANO] listarTodosLosUsuarios - No se pudo obtener conexión.");
            return usuarios;
        }
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                usuarios.add(mapResultSetToUsuario(rs));
            }
            System.out.println("REPO_USER: [TEXTO PLANO] Total usuarios listados: " + usuarios.size());
        } catch (SQLException e) {
            handleSQLException("listarTodosLosUsuarios", null, e);
        }
        return usuarios;
    }
    
    public List<Usuario> filtrarUsuarios(String nombreFilter, String rolFilter, Boolean activoFilter) {
        List<Usuario> usuarios = new ArrayList<>();
        StringBuilder sqlBuilder = new StringBuilder("SELECT * FROM usuarios WHERE 1=1");
        List<Object> params = new ArrayList<>();
        System.out.println("REPO_USER: [TEXTO PLANO] Filtrando usuarios. Nombre: " + nombreFilter + ", Rol: " + rolFilter + ", Activo: " + activoFilter);

        if (nombreFilter != null && !nombreFilter.isEmpty()) {
            sqlBuilder.append(" AND nombre_completo LIKE ?");
            params.add("%" + nombreFilter + "%");
        }
        if (rolFilter != null && !rolFilter.isEmpty()) {
            try {
                 Usuario.RolUsuario.valueOf(rolFilter.toUpperCase());
                 sqlBuilder.append(" AND rol = ?");
                 params.add(rolFilter.toUpperCase());
            } catch (IllegalArgumentException e) {
                System.err.println("REPO_USER: [TEXTO PLANO] Rol inválido para filtro: " + rolFilter);
            }
        }
        if (activoFilter != null) {
            sqlBuilder.append(" AND activo = ?");
            params.add(activoFilter);
        }
        sqlBuilder.append(" ORDER BY nombre_completo");
        
        Connection conn = Conexion.getConnection();
        if (conn == null) {
            System.err.println("REPO_USER: [TEXTO PLANO] filtrarUsuarios - No se pudo obtener conexión.");
            return usuarios;
        }
        try (PreparedStatement pstmt = conn.prepareStatement(sqlBuilder.toString())) {
            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
            }
            try(ResultSet rs = pstmt.executeQuery()){
                while (rs.next()) {
                    usuarios.add(mapResultSetToUsuario(rs));
                }
            }
            System.out.println("REPO_USER: [TEXTO PLANO] Total usuarios filtrados: " + usuarios.size());
        } catch (SQLException e) {
            handleSQLException("filtrarUsuarios", null, e);
        }
        return usuarios;
    }

    private void fillPreparedStatementForUsuarioInsert(PreparedStatement pstmt, Usuario usuario, String passwordAGuardar) throws SQLException {
        pstmt.setString(1, usuario.getUsername());
        pstmt.setString(2, passwordAGuardar); // password_hash (texto plano en este caso)
        pstmt.setString(3, usuario.getNombreCompleto());
        pstmt.setString(4, usuario.getEmail());
        pstmt.setString(5, usuario.getTelefono());
        pstmt.setString(6, usuario.getRol().name());
        pstmt.setBoolean(7, false); // 'activo' es false por defecto en INSERT
        pstmt.setString(8, usuario.getDisponibilidadHoraria());
        pstmt.setString(9, usuario.getAreasInteres());
        pstmt.setString(10, usuario.getDireccion());
        pstmt.setString(11, usuario.getTipoVivienda());
        if (usuario.getExperienciaAnimales() != null) {
            pstmt.setBoolean(12, usuario.getExperienciaAnimales());
        } else {
            pstmt.setNull(12, Types.BOOLEAN);
        }
    }
    
    private void fillPreparedStatementForUsuarioUpdate(PreparedStatement pstmt, Usuario usuario) throws SQLException {
        pstmt.setString(1, usuario.getNombreCompleto());
        pstmt.setString(2, usuario.getEmail());
        pstmt.setString(3, usuario.getTelefono());
        pstmt.setString(4, usuario.getRol().name());
        pstmt.setString(5, usuario.getDisponibilidadHoraria());
        pstmt.setString(6, usuario.getAreasInteres());
        pstmt.setString(7, usuario.getDireccion());
        pstmt.setString(8, usuario.getTipoVivienda());
        if (usuario.getExperienciaAnimales() != null) {
            pstmt.setBoolean(9, usuario.getExperienciaAnimales());
        } else {
            pstmt.setNull(9, Types.BOOLEAN);
        }
        pstmt.setBoolean(10, usuario.isActivo());
        pstmt.setInt(11, usuario.getId());
    }

    private Usuario mapResultSetToUsuario(ResultSet rs) throws SQLException {
        Usuario usuario = new Usuario();
        usuario.setId(rs.getInt("id"));
        usuario.setUsername(rs.getString("username"));
        usuario.setPasswordHash(rs.getString("password_hash")); // Contiene texto plano
        usuario.setNombreCompleto(rs.getString("nombre_completo"));
        usuario.setEmail(rs.getString("email"));
        usuario.setTelefono(rs.getString("telefono"));
        try {
            String rolDb = rs.getString("rol");
            if (rolDb != null) {
                usuario.setRol(Usuario.RolUsuario.valueOf(rolDb.toUpperCase()));
            } else {
                 System.err.println("REPO_USER: [TEXTO PLANO] Rol es NULL en DB para usuario ID: " + rs.getInt("id") + ". Asignando default ADOPTANTE_POTENCIAL.");
                 usuario.setRol(Usuario.RolUsuario.ADOPTANTE_POTENCIAL);
            }
        } catch (IllegalArgumentException e) {
            System.err.println("REPO_USER: [TEXTO PLANO] Rol desconocido en DB: '" + rs.getString("rol") + "' para usuario ID: " + rs.getInt("id") + ". Asignando default ADOPTANTE_POTENCIAL.");
            usuario.setRol(Usuario.RolUsuario.ADOPTANTE_POTENCIAL); 
        }
        usuario.setActivo(rs.getBoolean("activo"));
        usuario.setFechaRegistro(rs.getTimestamp("fecha_registro"));
        usuario.setDisponibilidadHoraria(rs.getString("disponibilidad_horaria"));
        usuario.setAreasInteres(rs.getString("areas_interes"));
        usuario.setDireccion(rs.getString("direccion"));
        usuario.setTipoVivienda(rs.getString("tipo_vivienda"));
        Object expAnimalesObj = rs.getObject("experiencia_animales");
        if (expAnimalesObj == null) {
            usuario.setExperienciaAnimales(null);
        } else {
            usuario.setExperienciaAnimales((Boolean) expAnimalesObj);
        }
        return usuario;
    }

    private void handleSQLException(String methodName, String contextInfo, SQLException e) {
        String logPrefix = "REPO_USER: [TEXTO PLANO]";
        System.err.println(logPrefix + " Error SQL en " + methodName + (contextInfo != null ? " para '" + contextInfo + "'" : "") + ": " + e.getMessage());
        if (e.getSQLState() != null && e.getSQLState().equals("23000")) { // Error de constraint UNIQUE
             System.err.println(logPrefix + " El username o email probablemente ya existe.");
        }
        e.printStackTrace(); // Mostrar traza completa para más detalles
    }
}