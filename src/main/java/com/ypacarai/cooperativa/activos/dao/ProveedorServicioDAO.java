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

import com.ypacarai.cooperativa.activos.config.DatabaseConfig;
import com.ypacarai.cooperativa.activos.model.ProveedorServicio;

/**
 * DAO para la entidad ProveedorServicio
 * Sistema de Gestión de Activos - Cooperativa Ypacaraí LTDA
 */
public class ProveedorServicioDAO {
    
    private static final String INSERT = 
        "INSERT INTO proveedor_servicio (nombre, telefono, email, " +
        "direccion, contacto, especialidades, activo, fecha_registro) " +
        "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    
    private static final String SELECT_ALL = 
        "SELECT id, nombre, telefono, email, direccion, " +
        "contacto, especialidades, activo, fecha_registro, fecha_actualizacion " +
        "FROM proveedor_servicio ORDER BY nombre";
    
    private static final String SELECT_BY_ID = SELECT_ALL.replace("ORDER BY nombre", "WHERE id = ?");
    
    private static final String SELECT_ACTIVOS = SELECT_ALL.replace("ORDER BY nombre", "WHERE activo = true ORDER BY nombre");
    
    private static final String UPDATE = 
        "UPDATE proveedor_servicio SET nombre = ?, telefono = ?, email = ?, " +
        "direccion = ?, contacto = ?, especialidades = ?, " +
        "fecha_actualizacion = CURRENT_TIMESTAMP WHERE id = ?";
    
    private static final String TOGGLE_ACTIVO = 
        "UPDATE proveedor_servicio SET activo = ?, fecha_actualizacion = CURRENT_TIMESTAMP WHERE id = ?";
    
    private static final String COUNT_MANTENIMIENTOS = 
        "SELECT COUNT(*) FROM mantenimiento_tercerizado WHERE proveedor_id = ?";
    
    /**
     * Inserta un nuevo proveedor de servicio
     */
    public int insert(ProveedorServicio proveedor) {
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(INSERT, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, proveedor.getPrvNombre());
            stmt.setString(2, proveedor.getPrvNumeroTelefono());
            stmt.setString(3, proveedor.getPrvEmail());
            stmt.setString(4, proveedor.getPrvDireccion());
            stmt.setString(5, proveedor.getPrvContactoPrincipal());
            stmt.setString(6, proveedor.getPrvEspecialidades());
            stmt.setBoolean(7, proveedor.isActivo());
            stmt.setTimestamp(8, Timestamp.valueOf(proveedor.getCreadoEn()));
            
            int result = stmt.executeUpdate();
            
            if (result > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        proveedor.setPrvId(rs.getInt(1));
                        return proveedor.getPrvId();
                    }
                }
            }
            
            return 0;
        } catch (SQLException e) {
            System.err.println("Error insertando proveedor de servicio: " + e.getMessage());
            throw new RuntimeException("Error al insertar proveedor de servicio", e);
        }
    }
    
    /**
     * Obtiene todos los proveedores
     */
    public List<ProveedorServicio> findAll() {
        List<ProveedorServicio> proveedores = new ArrayList<>();
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_ALL);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                proveedores.add(mapResultSetToProveedor(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error obteniendo proveedores: " + e.getMessage());
            throw new RuntimeException("Error al obtener proveedores", e);
        }
        
        return proveedores;
    }
    
    /**
     * Obtiene solo los proveedores activos
     */
    public List<ProveedorServicio> findActivos() {
        List<ProveedorServicio> proveedores = new ArrayList<>();
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_ACTIVOS);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                proveedores.add(mapResultSetToProveedor(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error obteniendo proveedores activos: " + e.getMessage());
            throw new RuntimeException("Error al obtener proveedores activos", e);
        }
        
        return proveedores;
    }
    
    /**
     * Busca un proveedor por ID
     */
    public Optional<ProveedorServicio> findById(int id) {
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_BY_ID)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToProveedor(rs));
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error buscando proveedor por ID: " + e.getMessage());
            throw new RuntimeException("Error al buscar proveedor", e);
        }
        
        return Optional.empty();
    }
    
    /**
     * Actualiza un proveedor
     */
    public boolean update(ProveedorServicio proveedor) {
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(UPDATE)) {
            
            stmt.setString(1, proveedor.getPrvNombre());
            stmt.setString(2, proveedor.getPrvNumeroTelefono());
            stmt.setString(3, proveedor.getPrvEmail());
            stmt.setString(4, proveedor.getPrvDireccion());
            stmt.setString(5, proveedor.getPrvContactoPrincipal());
            stmt.setString(6, proveedor.getPrvEspecialidades());
            stmt.setInt(7, proveedor.getPrvId());
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error actualizando proveedor: " + e.getMessage());
            throw new RuntimeException("Error al actualizar proveedor", e);
        }
    }
    
    /**
     * Activa o desactiva un proveedor
     */
    public boolean toggleActivo(int id, boolean activo) {
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(TOGGLE_ACTIVO)) {
            
            stmt.setBoolean(1, activo);
            stmt.setInt(2, id);
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error cambiando estado del proveedor: " + e.getMessage());
            throw new RuntimeException("Error al cambiar estado del proveedor", e);
        }
    }
    
    /**
     * Verifica si un proveedor puede ser eliminado (no tiene mantenimientos asociados)
     */
    public boolean puedeEliminar(int id) {
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(COUNT_MANTENIMIENTOS)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) == 0;
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error verificando si se puede eliminar proveedor: " + e.getMessage());
            throw new RuntimeException("Error al verificar eliminación de proveedor", e);
        }
        
        return false;
    }
    
    /**
     * Mapea un ResultSet a un objeto ProveedorServicio
     */
    private ProveedorServicio mapResultSetToProveedor(ResultSet rs) throws SQLException {
        ProveedorServicio proveedor = new ProveedorServicio();
        
        proveedor.setPrvId(rs.getInt("id"));
        proveedor.setPrvNombre(rs.getString("nombre"));
        proveedor.setPrvNumeroTelefono(rs.getString("telefono"));
        proveedor.setPrvEmail(rs.getString("email"));
        proveedor.setPrvDireccion(rs.getString("direccion"));
        proveedor.setPrvContactoPrincipal(rs.getString("contacto"));
        proveedor.setPrvEspecialidades(rs.getString("especialidades"));
        proveedor.setActivo(rs.getBoolean("activo"));
        
        // Manejar fechas
        Timestamp creadoEn = rs.getTimestamp("fecha_registro");
        if (creadoEn != null) {
            proveedor.setCreadoEn(creadoEn.toLocalDateTime());
        }
        
        Timestamp actualizadoEn = rs.getTimestamp("fecha_actualizacion");
        if (actualizadoEn != null) {
            proveedor.setActualizadoEn(actualizadoEn.toLocalDateTime());
        }
        
        return proveedor;
    }
}