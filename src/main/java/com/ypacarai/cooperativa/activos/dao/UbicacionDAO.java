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
import com.ypacarai.cooperativa.activos.model.Ubicacion;

/**
 * DAO para la gestión de ubicaciones
 */
public class UbicacionDAO {
    
    /**
     * Obtiene todas las ubicaciones activas
     */
    public List<Ubicacion> obtenerTodas() throws SQLException {
        List<Ubicacion> ubicaciones = new ArrayList<>();
        String sql = "SELECT ubi_id, ubi_codigo, ubi_nombre, ubi_tipo, ubi_direccion, " +
                     "ubi_telefono, activo, creado_en FROM UBICACION WHERE activo = 1 " +
                     "ORDER BY ubi_tipo, ubi_nombre";
        
        try (Connection conn = DatabaseConfigComplete.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                Ubicacion ubicacion = mapearDesdeResultSet(rs);
                ubicaciones.add(ubicacion);
            }
        }
        
        return ubicaciones;
    }
    
    /**
     * Obtiene ubicaciones por tipo
     */
    public List<Ubicacion> obtenerPorTipo(Ubicacion.TipoUbicacion tipo) throws SQLException {
        List<Ubicacion> ubicaciones = new ArrayList<>();
        String sql = "SELECT ubi_id, ubi_codigo, ubi_nombre, ubi_tipo, ubi_direccion, " +
                     "ubi_telefono, activo, creado_en FROM UBICACION WHERE activo = 1 AND ubi_tipo = ? " +
                     "ORDER BY ubi_nombre";
        
        try (Connection conn = DatabaseConfigComplete.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, tipo.name());
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Ubicacion ubicacion = mapearDesdeResultSet(rs);
                    ubicaciones.add(ubicacion);
                }
            }
        }
        
        return ubicaciones;
    }
    
    /**
     * Busca una ubicación por ID
     */
    public Optional<Ubicacion> buscarPorId(Integer id) throws SQLException {
        String sql = "SELECT ubi_id, ubi_codigo, ubi_nombre, ubi_tipo, ubi_direccion, " +
                     "ubi_telefono, activo, creado_en FROM UBICACION WHERE ubi_id = ?";
        
        try (Connection conn = DatabaseConfigComplete.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearDesdeResultSet(rs));
                }
            }
        }
        
        return Optional.empty();
    }
    
    /**
     * Busca una ubicación por código
     */
    public Optional<Ubicacion> buscarPorCodigo(String codigo) throws SQLException {
        String sql = "SELECT ubi_id, ubi_codigo, ubi_nombre, ubi_tipo, ubi_direccion, " +
                     "ubi_telefono, activo, creado_en FROM UBICACION WHERE ubi_codigo = ?";
        
        try (Connection conn = DatabaseConfigComplete.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, codigo);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearDesdeResultSet(rs));
                }
            }
        }
        
        return Optional.empty();
    }
    
    /**
     * Guarda una nueva ubicación
     */
    public Ubicacion guardar(Ubicacion ubicacion) throws SQLException {
        String sql = "INSERT INTO UBICACION (ubi_codigo, ubi_nombre, ubi_tipo, ubi_direccion, ubi_telefono, activo) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConfigComplete.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, ubicacion.getUbiCodigo());
            pstmt.setString(2, ubicacion.getUbiNombre());
            pstmt.setString(3, ubicacion.getUbiTipo().name());
            pstmt.setString(4, ubicacion.getUbiDireccion());
            pstmt.setString(5, ubicacion.getUbiTelefono());
            pstmt.setBoolean(6, ubicacion.getActivo());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Error al crear ubicación, no se afectaron filas.");
            }
            
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    ubicacion.setUbiId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Error al crear ubicación, no se obtuvo el ID.");
                }
            }
        }
        
        return ubicacion;
    }
    
    /**
     * Actualiza una ubicación existente
     */
    public void actualizar(Ubicacion ubicacion) throws SQLException {
        String sql = "UPDATE UBICACION SET ubi_codigo = ?, ubi_nombre = ?, ubi_tipo = ?, ubi_direccion = ?, " +
                     "ubi_telefono = ?, activo = ? WHERE ubi_id = ?";
        
        try (Connection conn = DatabaseConfigComplete.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, ubicacion.getUbiCodigo());
            pstmt.setString(2, ubicacion.getUbiNombre());
            pstmt.setString(3, ubicacion.getUbiTipo().name());
            pstmt.setString(4, ubicacion.getUbiDireccion());
            pstmt.setString(5, ubicacion.getUbiTelefono());
            pstmt.setBoolean(6, ubicacion.getActivo());
            pstmt.setInt(7, ubicacion.getUbiId());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("No se encontró la ubicación con ID: " + ubicacion.getUbiId());
            }
        }
    }
    
    /**
     * Desactiva una ubicación (eliminación lógica)
     */
    public void desactivar(Integer id) throws SQLException {
        String sql = "UPDATE UBICACION SET activo = 0 WHERE ubi_id = ?";
        
        try (Connection conn = DatabaseConfigComplete.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("No se encontró la ubicación con ID: " + id);
            }
        }
    }
    
    /**
     * Mapea un ResultSet a una entidad Ubicacion
     */
    private Ubicacion mapearDesdeResultSet(ResultSet rs) throws SQLException {
        Ubicacion ubicacion = new Ubicacion();
        ubicacion.setUbiId(rs.getInt("ubi_id"));
        ubicacion.setUbiCodigo(rs.getString("ubi_codigo"));
        ubicacion.setUbiNombre(rs.getString("ubi_nombre"));
        ubicacion.setUbiTipo(Ubicacion.TipoUbicacion.valueOf(rs.getString("ubi_tipo")));
        ubicacion.setUbiDireccion(rs.getString("ubi_direccion"));
        ubicacion.setUbiTelefono(rs.getString("ubi_telefono"));
        ubicacion.setActivo(rs.getBoolean("activo"));
        
        Timestamp timestamp = rs.getTimestamp("creado_en");
        if (timestamp != null) {
            ubicacion.setCreadoEn(timestamp.toLocalDateTime());
        }
        
        return ubicacion;
    }
}
