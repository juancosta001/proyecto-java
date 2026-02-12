package com.ypacarai.cooperativa.activos.dao;

import com.ypacarai.cooperativa.activos.config.DatabaseConfig;
import com.ypacarai.cooperativa.activos.model.Traslado;
import com.ypacarai.cooperativa.activos.model.Traslado.EstadoTraslado;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para el manejo de TRASLADO
 * Gestiona traslados de activos entre Casa Central y Sucursales
 */
public class TrasladoDAO {
    
    private DatabaseConfig databaseConfig;
    
    public TrasladoDAO() {
        this.databaseConfig = new DatabaseConfig();
    }
    
    public TrasladoDAO(DatabaseConfig databaseConfig) {
        this.databaseConfig = databaseConfig;
    }
    
    /**
     * Guarda un nuevo traslado en la base de datos
     */
    public boolean save(Traslado traslado) {
        String sql = "INSERT INTO TRASLADO (act_id, tras_fecha_salida, tras_fecha_retorno, " +
                    "tras_ubicacion_origen, tras_ubicacion_destino, tras_motivo, tras_estado, " +
                    "tras_responsable_envio, tras_responsable_recibo, tras_observaciones, " +
                    "tras_fecha_devolucion_prog, autorizado_por, creado_por, creado_en, actualizado_en) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, traslado.getActId());
            stmt.setObject(2, traslado.getTrasFechaSalida() != null ? 
                          Timestamp.valueOf(traslado.getTrasFechaSalida()) : null);
            stmt.setObject(3, traslado.getTrasFechaRetorno() != null ? 
                          Timestamp.valueOf(traslado.getTrasFechaRetorno()) : null);
            stmt.setInt(4, traslado.getTrasUbicacionOrigen());
            stmt.setInt(5, traslado.getTrasUbicacionDestino());
            stmt.setString(6, traslado.getTrasMotivo());
            stmt.setString(7, traslado.getTrasEstado() != null ? 
                          traslado.getTrasEstado().name() : EstadoTraslado.Programado.name());
            stmt.setString(8, traslado.getTrasResponsableEnvio());
            stmt.setString(9, traslado.getTrasResponsableRecibo());
            stmt.setString(10, traslado.getTrasObservaciones());
            stmt.setObject(11, traslado.getTrasFechaDevolucionProg());
            stmt.setObject(12, traslado.getAutorizadoPor());
            stmt.setObject(13, traslado.getCreadoPor());
            stmt.setTimestamp(14, Timestamp.valueOf(traslado.getCreadoEn()));
            stmt.setTimestamp(15, Timestamp.valueOf(traslado.getActualizadoEn()));
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        traslado.setTrasId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Error al guardar traslado: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Actualiza un traslado existente
     */
    public boolean update(Traslado traslado) {
        String sql = "UPDATE TRASLADO SET act_id = ?, tras_fecha_salida = ?, tras_fecha_retorno = ?, " +
                    "tras_ubicacion_origen = ?, tras_ubicacion_destino = ?, tras_motivo = ?, " +
                    "tras_estado = ?, tras_responsable_envio = ?, tras_responsable_recibo = ?, " +
                    "tras_observaciones = ?, tras_fecha_devolucion_prog = ?, autorizado_por = ?, " +
                    "actualizado_en = ? WHERE tras_id = ?";
        
        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, traslado.getActId());
            stmt.setObject(2, traslado.getTrasFechaSalida() != null ? 
                          Timestamp.valueOf(traslado.getTrasFechaSalida()) : null);
            stmt.setObject(3, traslado.getTrasFechaRetorno() != null ? 
                          Timestamp.valueOf(traslado.getTrasFechaRetorno()) : null);
            stmt.setInt(4, traslado.getTrasUbicacionOrigen());
            stmt.setInt(5, traslado.getTrasUbicacionDestino());
            stmt.setString(6, traslado.getTrasMotivo());
            stmt.setString(7, traslado.getTrasEstado().name());
            stmt.setString(8, traslado.getTrasResponsableEnvio());
            stmt.setString(9, traslado.getTrasResponsableRecibo());
            stmt.setString(10, traslado.getTrasObservaciones());
            stmt.setObject(11, traslado.getTrasFechaDevolucionProg());
            stmt.setObject(12, traslado.getAutorizadoPor());
            stmt.setTimestamp(13, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setInt(14, traslado.getTrasId());
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al actualizar traslado: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Encuentra un traslado por ID
     */
    public Traslado findById(Integer id) {
        String sql = "SELECT * FROM TRASLADO WHERE tras_id = ?";
        
        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToTraslado(rs);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error al buscar traslado por ID: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Obtiene todos los traslados
     */
    public List<Traslado> findAll() {
        List<Traslado> traslados = new ArrayList<>();
        String sql = "SELECT * FROM TRASLADO ORDER BY tras_fecha_salida DESC";
        
        try (Connection conn = databaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                traslados.add(mapResultSetToTraslado(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error al obtener todos los traslados: " + e.getMessage());
            e.printStackTrace();
        }
        
        return traslados;
    }
    
    /**
     * Busca traslados por activo
     */
    public List<Traslado> findByActivo(Integer activoId) {
        List<Traslado> traslados = new ArrayList<>();
        String sql = "SELECT * FROM TRASLADO WHERE act_id = ? ORDER BY tras_fecha_salida DESC";
        
        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, activoId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    traslados.add(mapResultSetToTraslado(rs));
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error al buscar traslados por activo: " + e.getMessage());
            e.printStackTrace();
        }
        
        return traslados;
    }
    
    /**
     * Busca traslados por estado
     */
    public List<Traslado> findByEstado(EstadoTraslado estado) {
        List<Traslado> traslados = new ArrayList<>();
        String sql = "SELECT * FROM TRASLADO WHERE tras_estado = ? ORDER BY tras_fecha_salida DESC";
        
        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, estado.name());
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    traslados.add(mapResultSetToTraslado(rs));
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error al buscar traslados por estado: " + e.getMessage());
            e.printStackTrace();
        }
        
        return traslados;
    }
    
    /**
     * Busca traslados pendientes de devolución
     */
    public List<Traslado> findPendientesDevolucion() {
        List<Traslado> traslados = new ArrayList<>();
        String sql = "SELECT * FROM TRASLADO WHERE tras_estado IN ('En_Transito', 'Entregado') " +
                    "AND tras_fecha_devolucion_prog <= CURDATE() ORDER BY tras_fecha_devolucion_prog ASC";
        
        try (Connection conn = databaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                traslados.add(mapResultSetToTraslado(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error al buscar traslados pendientes de devolución: " + e.getMessage());
            e.printStackTrace();
        }
        
        return traslados;
    }
    
    /**
     * Elimina un traslado
     */
    public boolean delete(Integer id) {
        String sql = "DELETE FROM TRASLADO WHERE tras_id = ?";
        
        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al eliminar traslado: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Mapea un ResultSet a un objeto Traslado
     */
    private Traslado mapResultSetToTraslado(ResultSet rs) throws SQLException {
        Traslado traslado = new Traslado();
        
        traslado.setTrasId(rs.getInt("tras_id"));
        traslado.setActId(rs.getInt("act_id"));
        traslado.setTrasNumero(rs.getString("tras_numero"));
        
        Timestamp fechaSalida = rs.getTimestamp("tras_fecha_salida");
        if (fechaSalida != null) {
            traslado.setTrasFechaSalida(fechaSalida.toLocalDateTime());
        }
        
        Timestamp fechaRetorno = rs.getTimestamp("tras_fecha_retorno");
        if (fechaRetorno != null) {
            traslado.setTrasFechaRetorno(fechaRetorno.toLocalDateTime());
        }
        
        traslado.setTrasUbicacionOrigen(rs.getInt("tras_ubicacion_origen"));
        traslado.setTrasUbicacionDestino(rs.getInt("tras_ubicacion_destino"));
        traslado.setTrasMotivo(rs.getString("tras_motivo"));
        
        String estadoStr = rs.getString("tras_estado");
        if (estadoStr != null) {
            traslado.setTrasEstado(EstadoTraslado.valueOf(estadoStr));
        }
        
        traslado.setTrasResponsableEnvio(rs.getString("tras_responsable_envio"));
        traslado.setTrasResponsableRecibo(rs.getString("tras_responsable_recibo"));
        traslado.setTrasObservaciones(rs.getString("tras_observaciones"));
        
        Date fechaDevProg = rs.getDate("tras_fecha_devolucion_prog");
        if (fechaDevProg != null) {
            traslado.setTrasFechaDevolucionProg(fechaDevProg.toLocalDate());
        }
        
        int autorizadoPor = rs.getInt("autorizado_por");
        if (!rs.wasNull()) {
            traslado.setAutorizadoPor(autorizadoPor);
        }
        
        int creadoPor = rs.getInt("creado_por");
        if (!rs.wasNull()) {
            traslado.setCreadoPor(creadoPor);
        }
        
        Timestamp creadoEn = rs.getTimestamp("creado_en");
        if (creadoEn != null) {
            traslado.setCreadoEn(creadoEn.toLocalDateTime());
        }
        
        Timestamp actualizadoEn = rs.getTimestamp("actualizado_en");
        if (actualizadoEn != null) {
            traslado.setActualizadoEn(actualizadoEn.toLocalDateTime());
        }
        
        return traslado;
    }
    
    /**
     * Cuenta traslados por estado
     */
    public int countByEstado(EstadoTraslado estado) {
        String sql = "SELECT COUNT(*) FROM TRASLADO WHERE tras_estado = ?";
        
        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, estado.name());
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error al contar traslados por estado: " + e.getMessage());
            e.printStackTrace();
        }
        
        return 0;
    }
}
