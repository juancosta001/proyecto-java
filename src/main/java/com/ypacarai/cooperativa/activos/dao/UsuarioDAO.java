package com.ypacarai.cooperativa.activos.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.ypacarai.cooperativa.activos.config.DatabaseConfigComplete;
import com.ypacarai.cooperativa.activos.model.Usuario;

/**
 * DAO para la entidad Usuario
 * Sistema de Gestión de Activos - Cooperativa Ypacaraí LTDA
 */
public class UsuarioDAO {
    

    
    // Queries SQL
    private static final String SELECT_ALL = 
        "SELECT usu_id, usu_nombre, usu_usuario, usu_password, usu_rol, usu_email, " +
        "activo, creado_en, actualizado_en FROM USUARIO WHERE activo = 1";
    
    private static final String SELECT_BY_ID = 
        SELECT_ALL.replace("WHERE activo = 1", "WHERE usu_id = ? AND activo = 1");
    
    private static final String SELECT_BY_USERNAME = 
        SELECT_ALL.replace("WHERE activo = 1", "WHERE usu_usuario = ? AND activo = 1");
    
    private static final String INSERT_USUARIO = 
        "INSERT INTO USUARIO (usu_nombre, usu_usuario, usu_password, usu_rol, usu_email) " +
        "VALUES (?, ?, ?, ?, ?)";
    
    private static final String UPDATE_USUARIO = 
        "UPDATE USUARIO SET usu_nombre = ?, usu_email = ?, usu_rol = ? " +
        "WHERE usu_id = ? AND activo = 1";
    
    private static final String DELETE_USUARIO = 
        "UPDATE USUARIO SET activo = 0 WHERE usu_id = ?";
    
    private static final String AUTHENTICATE_USER = 
        "SELECT usu_id, usu_nombre, usu_usuario, usu_rol, usu_email " +
        "FROM USUARIO WHERE usu_usuario = ? AND usu_password = ? AND activo = 1";
    
    /**
     * Obtiene todos los usuarios activos
     */
    public List<Usuario> findAll() {
        List<Usuario> usuarios = new ArrayList<>();
        
        try (Connection conn = DatabaseConfigComplete.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_ALL);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                usuarios.add(mapResultSetToUsuario(rs));
            }
            

            
        } catch (SQLException e) {

            throw new RuntimeException("Error en base de datos", e);
        }
        
        return usuarios;
    }
    
    /**
     * Busca un usuario por ID
     */
    public Optional<Usuario> findById(int id) {
        try (Connection conn = DatabaseConfigComplete.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_BY_ID)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Usuario usuario = mapResultSetToUsuario(rs);

                    return Optional.of(usuario);
                }
            }
            
        } catch (SQLException e) {

            throw new RuntimeException("Error en base de datos", e);
        }
        
        return Optional.empty();
    }
    
    /**
     * Busca un usuario por nombre de usuario
     */
    public Optional<Usuario> findByUsername(String username) {
        try (Connection conn = DatabaseConfigComplete.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_BY_USERNAME)) {
            
            stmt.setString(1, username);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Usuario usuario = mapResultSetToUsuario(rs);

                    return Optional.of(usuario);
                }
            }
            
        } catch (SQLException e) {

            throw new RuntimeException("Error en base de datos", e);
        }
        
        return Optional.empty();
    }
    
    /**
     * Autentica un usuario
     */
    public Optional<Usuario> authenticate(String username, String password) {
        try (Connection conn = DatabaseConfigComplete.getConnection();
             PreparedStatement stmt = conn.prepareStatement(AUTHENTICATE_USER)) {
            
            stmt.setString(1, username);
            stmt.setString(2, password);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Usuario usuario = new Usuario();
                    usuario.setUsuId(rs.getInt("usu_id"));
                    usuario.setUsuNombre(rs.getString("usu_nombre"));
                    usuario.setUsuUsuario(rs.getString("usu_usuario"));
                    usuario.setUsuRol(Usuario.Rol.valueOf(rs.getString("usu_rol")));
                    usuario.setUsuEmail(rs.getString("usu_email"));
                    

                    return Optional.of(usuario);
                }
            }
            
        } catch (SQLException e) {

            throw new RuntimeException("Error en base de datos", e);
        }
        

        return Optional.empty();
    }
    
    /**
     * Crea un nuevo usuario
     */
    public Usuario save(Usuario usuario) {
        try (Connection conn = DatabaseConfigComplete.getConnection();
             PreparedStatement stmt = conn.prepareStatement(INSERT_USUARIO, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, usuario.getUsuNombre());
            stmt.setString(2, usuario.getUsuUsuario());
            stmt.setString(3, usuario.getUsuPassword());
            stmt.setString(4, usuario.getUsuRol().name());
            stmt.setString(5, usuario.getUsuEmail());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Error al crear usuario, no se afectaron filas");
            }
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    usuario.setUsuId(generatedKeys.getInt(1));

                } else {
                    throw new SQLException("Error al crear usuario, no se obtuvo ID");
                }
            }
            
        } catch (SQLException e) {

            throw new RuntimeException("Error en base de datos", e);
        }
        
        return usuario;
    }
    
    /**
     * Actualiza un usuario existente
     */
    public boolean update(Usuario usuario) {
        try (Connection conn = DatabaseConfigComplete.getConnection();
             PreparedStatement stmt = conn.prepareStatement(UPDATE_USUARIO)) {
            
            stmt.setString(1, usuario.getUsuNombre());
            stmt.setString(2, usuario.getUsuEmail());
            stmt.setString(3, usuario.getUsuRol().name());
            stmt.setInt(4, usuario.getUsuId());
            
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
            
        } catch (SQLException e) {
            System.err.println("Error actualizando usuario: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Elimina un usuario (soft delete)
     */
    public boolean delete(int id) {
        try (Connection conn = DatabaseConfigComplete.getConnection();
             PreparedStatement stmt = conn.prepareStatement(DELETE_USUARIO)) {
            
            stmt.setInt(1, id);
            
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
            
        } catch (SQLException e) {
            System.err.println("Error eliminando usuario: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Mapea un ResultSet a un objeto Usuario
     */
    private Usuario mapResultSetToUsuario(ResultSet rs) throws SQLException {
        Usuario usuario = new Usuario();
        usuario.setUsuId(rs.getInt("usu_id"));
        usuario.setUsuNombre(rs.getString("usu_nombre"));
        usuario.setUsuUsuario(rs.getString("usu_usuario"));
        usuario.setUsuPassword(rs.getString("usu_password"));
        usuario.setUsuRol(Usuario.Rol.valueOf(rs.getString("usu_rol")));
        usuario.setUsuEmail(rs.getString("usu_email"));
        usuario.setActivo(rs.getBoolean("activo"));
        
        // Conversión de Timestamp a LocalDateTime
        Timestamp creado = rs.getTimestamp("creado_en");
        if (creado != null) {
            usuario.setCreadoEn(creado.toLocalDateTime());
        }
        
        Timestamp actualizado = rs.getTimestamp("actualizado_en");
        if (actualizado != null) {
            usuario.setActualizadoEn(actualizado.toLocalDateTime());
        }
        
        return usuario;
    }
    
    /**
     * Obtiene usuarios por rol
     */
    public List<Usuario> findByRol(Usuario.Rol rol) {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = SELECT_ALL + " AND usu_rol = ?";
        
        try (Connection conn = DatabaseConfigComplete.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, rol.name());
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                usuarios.add(mapResultSetToUsuario(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error al buscar usuarios por rol: " + e.getMessage());
            e.printStackTrace();
        }
        
        return usuarios;
    }
    
    /**
     * Obtiene usuarios por rol y estado activo
     */
    public List<Usuario> findByRolAndActive(Usuario.Rol rol, boolean activo) {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT usu_id, usu_nombre, usu_usuario, usu_password, usu_rol, usu_email, " +
                    "activo, creado_en, actualizado_en FROM USUARIO WHERE usu_rol = ? AND activo = ?";
        
        try (Connection conn = DatabaseConfigComplete.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, rol.name());
            stmt.setBoolean(2, activo);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                usuarios.add(mapResultSetToUsuario(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error al buscar usuarios por rol y estado: " + e.getMessage());
            e.printStackTrace();
        }
        
        return usuarios;
    }
    
    /**
     * Obtiene todos los usuarios activos
     */
    public List<Usuario> findAllActive() {
        List<Usuario> usuarios = new ArrayList<>();
        
        try (Connection conn = DatabaseConfigComplete.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_ALL);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                usuarios.add(mapResultSetToUsuario(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error al obtener usuarios activos: " + e.getMessage());
            e.printStackTrace();
        }
        
        return usuarios;
    }
    
    /**
     * Actualiza la contraseña de un usuario
     */
    public boolean updatePassword(int usuarioId, String newPasswordHash) {
        String sql = "UPDATE USUARIO SET usu_password = ? WHERE usu_id = ?";
        
        try (Connection conn = DatabaseConfigComplete.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, newPasswordHash);
            stmt.setInt(2, usuarioId);
            
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
            
        } catch (SQLException e) {
            System.err.println("Error actualizando contraseña: " + e.getMessage());
            return false;
        }
    }

    /**
     * Obtener técnicos (usuarios con rol Técnico o Jefe_Informatica)
     */
    public List<Usuario> obtenerTecnicos() {
        List<Usuario> tecnicos = new ArrayList<>();
        
        try {
            // Obtener técnicos
            List<Usuario> tecnicosDirectos = findByRolAndActive(Usuario.Rol.Tecnico, true);
            tecnicos.addAll(tecnicosDirectos);
            
            // Obtener jefes de informática
            List<Usuario> jefes = findByRolAndActive(Usuario.Rol.Jefe_Informatica, true);
            tecnicos.addAll(jefes);
            
        } catch (Exception e) {
            System.err.println("Error obteniendo técnicos: " + e.getMessage());
        }
        
        return tecnicos;
    }
}
