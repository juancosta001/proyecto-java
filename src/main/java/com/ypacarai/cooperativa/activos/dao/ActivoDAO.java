package com.ypacarai.cooperativa.activos.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.ypacarai.cooperativa.activos.config.DatabaseConfigComplete;
import com.ypacarai.cooperativa.activos.model.Activo;

/**
 * DAO para la entidad Activo
 * Sistema de Gestión de Activos - Cooperativa Ypacaraí LTDA
 */
public class ActivoDAO {
    

    
    // Queries SQL
    private static final String SELECT_ALL = 
        "SELECT a.act_id, a.act_numero_activo, a.tip_act_id, a.act_marca, a.act_modelo, " +
        "a.act_numero_serie, a.act_especificaciones, a.act_fecha_adquisicion, a.act_estado, " +
        "a.act_ubicacion_actual, a.act_responsable_actual, a.act_observaciones, " +
        "a.creado_por, a.creado_en, a.actualizado_en, " +
        "ta.nombre as tipo_activo_nombre, u.ubi_nombre as ubicacion_nombre, " +
        "us.usu_nombre as usuario_creador_nombre " +
        "FROM ACTIVO a " +
        "LEFT JOIN TIPO_ACTIVO ta ON ta.tip_act_id = a.tip_act_id " +
        "LEFT JOIN UBICACION u ON u.ubi_id = a.act_ubicacion_actual " +
        "LEFT JOIN USUARIO us ON us.usu_id = a.creado_por";
    
    private static final String SELECT_BY_ID = SELECT_ALL + " WHERE a.act_id = ?";
    
    private static final String SELECT_BY_NUMERO = SELECT_ALL + " WHERE a.act_numero_activo = ?";
    
    private static final String SELECT_BY_ESTADO = SELECT_ALL + " WHERE a.act_estado = ?";
    
    private static final String SELECT_BY_UBICACION = SELECT_ALL + " WHERE a.act_ubicacion_actual = ?";
    
    private static final String INSERT_ACTIVO = 
        "INSERT INTO ACTIVO (act_numero_activo, tip_act_id, act_marca, act_modelo, " +
        "act_numero_serie, act_especificaciones, act_fecha_adquisicion, act_estado, " +
        "act_ubicacion_actual, act_responsable_actual, act_observaciones, creado_por) " +
        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    
    private static final String UPDATE_ACTIVO = 
        "UPDATE ACTIVO SET act_marca = ?, act_modelo = ?, act_numero_serie = ?, " +
        "act_especificaciones = ?, act_fecha_adquisicion = ?, act_estado = ?, " +
        "act_ubicacion_actual = ?, act_responsable_actual = ?, act_observaciones = ? " +
        "WHERE act_id = ?";
    
    private static final String UPDATE_ESTADO = 
        "UPDATE ACTIVO SET act_estado = ? WHERE act_id = ?";
    
    private static final String UPDATE_UBICACION = 
        "UPDATE ACTIVO SET act_ubicacion_actual = ?, act_estado = ? WHERE act_id = ?";
    
    /**
     * Obtiene todos los activos
     */
    public List<Activo> findAll() {
        List<Activo> activos = new ArrayList<>();
        
        try (Connection conn = DatabaseConfigComplete.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_ALL + " ORDER BY a.creado_en DESC");
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                activos.add(mapResultSetToActivo(rs));
            }
            

            
        } catch (SQLException e) {

            throw new RuntimeException("Error en base de datos", e);
        }
        
        return activos;
    }
    
    /**
     * Busca un activo por ID
     */
    public Optional<Activo> findById(int id) {
        try (Connection conn = DatabaseConfigComplete.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_BY_ID)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Activo activo = mapResultSetToActivo(rs);

                    return Optional.of(activo);
                }
            }
            
        } catch (SQLException e) {

            throw new RuntimeException("Error en base de datos", e);
        }
        
        return Optional.empty();
    }
    
    /**
     * Busca un activo por número
     */
    public Optional<Activo> findByNumero(String numero) {
        try (Connection conn = DatabaseConfigComplete.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_BY_NUMERO)) {
            
            stmt.setString(1, numero);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Activo activo = mapResultSetToActivo(rs);

                    return Optional.of(activo);
                }
            }
            
        } catch (SQLException e) {

            throw new RuntimeException("Error en base de datos", e);
        }
        
        return Optional.empty();
    }
    
    /**
     * Busca activos por estado
     */
    public List<Activo> findByEstado(Activo.Estado estado) {
        List<Activo> activos = new ArrayList<>();
        
        try (Connection conn = DatabaseConfigComplete.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_BY_ESTADO)) {
            
            stmt.setString(1, estado.name());
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    activos.add(mapResultSetToActivo(rs));
                }
            }
            

            
        } catch (SQLException e) {

            throw new RuntimeException("Error en base de datos", e);
        }
        
        return activos;
    }
    
    /**
     * Busca activos por ubicación
     */
    public List<Activo> findByUbicacion(int ubicacionId) {
        List<Activo> activos = new ArrayList<>();
        
        try (Connection conn = DatabaseConfigComplete.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_BY_UBICACION)) {
            
            stmt.setInt(1, ubicacionId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    activos.add(mapResultSetToActivo(rs));
                }
            }
            

            
        } catch (SQLException e) {

            throw new RuntimeException("Error en base de datos", e);
        }
        
        return activos;
    }
    
    /**
     * Crea un nuevo activo
     */
    public Activo save(Activo activo) {
        try (Connection conn = DatabaseConfigComplete.getConnection();
             PreparedStatement stmt = conn.prepareStatement(INSERT_ACTIVO, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, activo.getActNumeroActivo());
            stmt.setInt(2, activo.getTipActId());
            stmt.setString(3, activo.getActMarca());
            stmt.setString(4, activo.getActModelo());
            stmt.setString(5, activo.getActNumeroSerie());
            stmt.setString(6, activo.getActEspecificaciones());
            
            if (activo.getActFechaAdquisicion() != null) {
                stmt.setDate(7, Date.valueOf(activo.getActFechaAdquisicion()));
            } else {
                stmt.setNull(7, Types.DATE);
            }
            
            stmt.setString(8, activo.getActEstado().name());
            stmt.setInt(9, activo.getActUbicacionActual());
            stmt.setString(10, activo.getActResponsableActual());
            stmt.setString(11, activo.getActObservaciones());
            stmt.setInt(12, activo.getCreadoPor());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Error al crear activo, no se afectaron filas");
            }
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    activo.setActId(generatedKeys.getInt(1));

                } else {
                    throw new SQLException("Error al crear activo, no se obtuvo ID");
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error SQL al crear activo: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error en base de datos", e);
        }
        
        return activo;
    }
    
    /**
     * Actualiza un activo existente
     */
    public void update(Activo activo) {
        try (Connection conn = DatabaseConfigComplete.getConnection();
             PreparedStatement stmt = conn.prepareStatement(UPDATE_ACTIVO)) {
            
            stmt.setString(1, activo.getActMarca());
            stmt.setString(2, activo.getActModelo());
            stmt.setString(3, activo.getActNumeroSerie());
            stmt.setString(4, activo.getActEspecificaciones());
            
            if (activo.getActFechaAdquisicion() != null) {
                stmt.setDate(5, Date.valueOf(activo.getActFechaAdquisicion()));
            } else {
                stmt.setNull(5, Types.DATE);
            }
            
            stmt.setString(6, activo.getActEstado().name());
            stmt.setInt(7, activo.getActUbicacionActual());
            stmt.setString(8, activo.getActResponsableActual());
            stmt.setString(9, activo.getActObservaciones());
            stmt.setInt(10, activo.getActId());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Activo no encontrado para actualizar");
            }
            

            
        } catch (SQLException e) {

            throw new RuntimeException("Error en base de datos", e);
        }
    }
    
    /**
     * Actualiza el estado de un activo
     */
    public void updateEstado(int activoId, Activo.Estado nuevoEstado) {
        try (Connection conn = DatabaseConfigComplete.getConnection();
             PreparedStatement stmt = conn.prepareStatement(UPDATE_ESTADO)) {
            
            stmt.setString(1, nuevoEstado.name());
            stmt.setInt(2, activoId);
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Activo no encontrado para actualizar estado");
            }
            

            
        } catch (SQLException e) {

            throw new RuntimeException("Error en base de datos", e);
        }
    }
    
    /**
     * Actualiza la ubicación y estado de un activo (para traslados)
     */
    public void updateUbicacionYEstado(int activoId, int nuevaUbicacion, Activo.Estado nuevoEstado) {
        try (Connection conn = DatabaseConfigComplete.getConnection();
             PreparedStatement stmt = conn.prepareStatement(UPDATE_UBICACION)) {
            
            stmt.setInt(1, nuevaUbicacion);
            stmt.setString(2, nuevoEstado.name());
            stmt.setInt(3, activoId);
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Activo no encontrado para actualizar ubicación");
            }
            
            System.out.println("Ubicación de activo " + activoId + " actualizada a " + nuevaUbicacion + " con estado " + nuevoEstado);
            
        } catch (SQLException e) {

            throw new RuntimeException("Error en base de datos", e);
        }
    }
    
    /**
     * Mapea un ResultSet a un objeto Activo
     */
    private Activo mapResultSetToActivo(ResultSet rs) throws SQLException {
        Activo activo = new Activo();
        activo.setActId(rs.getInt("act_id"));
        activo.setActNumeroActivo(rs.getString("act_numero_activo"));
        activo.setTipActId(rs.getInt("tip_act_id"));
        activo.setActMarca(rs.getString("act_marca"));
        activo.setActModelo(rs.getString("act_modelo"));
        activo.setActNumeroSerie(rs.getString("act_numero_serie"));
        activo.setActEspecificaciones(rs.getString("act_especificaciones"));
        
        Date fechaAdquisicion = rs.getDate("act_fecha_adquisicion");
        if (fechaAdquisicion != null) {
            activo.setActFechaAdquisicion(fechaAdquisicion.toLocalDate());
        }
        
        activo.setActEstado(Activo.Estado.valueOf(rs.getString("act_estado")));
        activo.setActUbicacionActual(rs.getInt("act_ubicacion_actual"));
        activo.setActResponsableActual(rs.getString("act_responsable_actual"));
        activo.setActObservaciones(rs.getString("act_observaciones"));
        activo.setCreadoPor(rs.getInt("creado_por"));
        
        Timestamp creado = rs.getTimestamp("creado_en");
        if (creado != null) {
            activo.setCreadoEn(creado.toLocalDateTime());
        }
        
        Timestamp actualizado = rs.getTimestamp("actualizado_en");
        if (actualizado != null) {
            activo.setActualizadoEn(actualizado.toLocalDateTime());
        }
        
        // Campos relacionados (joins)
        activo.setTipoActivoNombre(rs.getString("tipo_activo_nombre"));
        activo.setUbicacionNombre(rs.getString("ubicacion_nombre"));
        activo.setUsuarioCreadorNombre(rs.getString("usuario_creador_nombre"));
        
        return activo;
    }
}
