package com.ypacarai.cooperativa.activos.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.ypacarai.cooperativa.activos.config.DatabaseConfigComplete;
import com.ypacarai.cooperativa.activos.model.TipoActivo;

/**
 * DAO para la gestión de tipos de activos
 */
public class TipoActivoDAO {
    
    /**
     * Obtiene todos los tipos de activos activos
     */
    public List<TipoActivo> obtenerTodos() throws SQLException {
        List<TipoActivo> tipos = new ArrayList<>();
        String sql = "SELECT tip_act_id, nombre, descripcion, activo FROM TIPO_ACTIVO WHERE activo = 1 ORDER BY nombre";
        
        try (Connection conn = DatabaseConfigComplete.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                TipoActivo tipo = new TipoActivo();
                tipo.setTipActId(rs.getInt("tip_act_id"));
                tipo.setNombre(rs.getString("nombre"));
                tipo.setDescripcion(rs.getString("descripcion"));
                tipo.setActivo(rs.getBoolean("activo"));
                tipos.add(tipo);
            }
        }
        
        return tipos;
    }
    
    /**
     * Busca un tipo de activo por ID
     */
    public Optional<TipoActivo> buscarPorId(Integer id) throws SQLException {
        String sql = "SELECT tip_act_id, nombre, descripcion, activo FROM TIPO_ACTIVO WHERE tip_act_id = ?";
        
        try (Connection conn = DatabaseConfigComplete.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    TipoActivo tipo = new TipoActivo();
                    tipo.setTipActId(rs.getInt("tip_act_id"));
                    tipo.setNombre(rs.getString("nombre"));
                    tipo.setDescripcion(rs.getString("descripcion"));
                    tipo.setActivo(rs.getBoolean("activo"));
                    return Optional.of(tipo);
                }
            }
        }
        
        return Optional.empty();
    }
    
    /**
     * Busca un tipo de activo por nombre
     */
    public Optional<TipoActivo> buscarPorNombre(String nombre) throws SQLException {
        String sql = "SELECT tip_act_id, nombre, descripcion, activo FROM TIPO_ACTIVO WHERE nombre = ?";
        
        try (Connection conn = DatabaseConfigComplete.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, nombre);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    TipoActivo tipo = new TipoActivo();
                    tipo.setTipActId(rs.getInt("tip_act_id"));
                    tipo.setNombre(rs.getString("nombre"));
                    tipo.setDescripcion(rs.getString("descripcion"));
                    tipo.setActivo(rs.getBoolean("activo"));
                    return Optional.of(tipo);
                }
            }
        }
        
        return Optional.empty();
    }
    
    /**
     * Guarda un nuevo tipo de activo
     */
    public TipoActivo guardar(TipoActivo tipo) throws SQLException {
        String sql = "INSERT INTO TIPO_ACTIVO (nombre, descripcion, activo) VALUES (?, ?, ?)";
        
        try (Connection conn = DatabaseConfigComplete.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, tipo.getNombre());
            pstmt.setString(2, tipo.getDescripcion());
            pstmt.setBoolean(3, tipo.getActivo());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Error al crear tipo de activo, no se afectaron filas.");
            }
            
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    tipo.setTipActId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Error al crear tipo de activo, no se obtuvo el ID.");
                }
            }
        }
        
        return tipo;
    }
    
    /**
     * Actualiza un tipo de activo existente
     */
    public void actualizar(TipoActivo tipo) throws SQLException {
        String sql = "UPDATE TIPO_ACTIVO SET nombre = ?, descripcion = ?, activo = ? WHERE tip_act_id = ?";
        
        try (Connection conn = DatabaseConfigComplete.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, tipo.getNombre());
            pstmt.setString(2, tipo.getDescripcion());
            pstmt.setBoolean(3, tipo.getActivo());
            pstmt.setInt(4, tipo.getTipActId());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("No se encontró el tipo de activo con ID: " + tipo.getTipActId());
            }
        }
    }
    
    /**
     * Desactiva un tipo de activo (eliminación lógica)
     */
    public void desactivar(Integer id) throws SQLException {
        String sql = "UPDATE TIPO_ACTIVO SET activo = 0 WHERE tip_act_id = ?";
        
        try (Connection conn = DatabaseConfigComplete.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("No se encontró el tipo de activo con ID: " + id);
            }
        }
    }
}
