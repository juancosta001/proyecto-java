package com.ypacarai.cooperativa.activos.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.ypacarai.cooperativa.activos.config.DatabaseConfig;
import com.ypacarai.cooperativa.activos.model.FichaReporte;
import com.ypacarai.cooperativa.activos.model.FichaReporte.EstadoFicha;

/**
 * DAO para el manejo de FICHA_REPORTE
 * Gestiona las fichas de reporte de mantenimientos correctivos
 */
public class FichaReporteDAO {
    
    private DatabaseConfig databaseConfig;
    
    public FichaReporteDAO() {
        this.databaseConfig = new DatabaseConfig();
    }
    
    public FichaReporteDAO(DatabaseConfig databaseConfig) {
        this.databaseConfig = databaseConfig;
    }
    
    /**
     * Guarda una nueva ficha de reporte en la base de datos
     */
    public boolean save(FichaReporte ficha) {
        String sql = "INSERT INTO FICHA_REPORTE (mant_id, ficha_fecha, ficha_problema_reportado, " +
                    "ficha_diagnostico, ficha_solucion_aplicada, ficha_componentes_cambio, " +
                    "ficha_tiempo_estimado, ficha_tiempo_real, ficha_observaciones, " +
                    "ficha_tecnico_firma, ficha_usuario_firma, ficha_estado, " +
                    "creado_por, creado_en, actualizado_en) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, ficha.getMantId());
            stmt.setDate(2, Date.valueOf(ficha.getFichaFecha()));
            stmt.setString(3, ficha.getFichaProblemaReportado());
            stmt.setString(4, ficha.getFichaDiagnostico());
            stmt.setString(5, ficha.getFichaSolucionAplicada());
            stmt.setString(6, ficha.getFichaComponentesCambio());
            stmt.setObject(7, ficha.getFichaTiempoEstimado());
            stmt.setObject(8, ficha.getFichaTiempoReal());
            stmt.setString(9, ficha.getFichaObservaciones());
            stmt.setString(10, ficha.getFichaTecnicoFirma());
            stmt.setString(11, ficha.getFichaUsuarioFirma());
            stmt.setString(12, ficha.getFichaEstado() != null ? 
                          ficha.getFichaEstado().name() : EstadoFicha.Borrador.name());
            stmt.setObject(13, ficha.getCreadoPor());
            stmt.setTimestamp(14, Timestamp.valueOf(ficha.getCreadoEn()));
            stmt.setTimestamp(15, Timestamp.valueOf(ficha.getActualizadoEn()));
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        ficha.setFichaId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Error al guardar ficha de reporte: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Actualiza una ficha de reporte existente
     */
    public boolean update(FichaReporte ficha) {
        String sql = "UPDATE FICHA_REPORTE SET mant_id = ?, ficha_fecha = ?, " +
                    "ficha_problema_reportado = ?, ficha_diagnostico = ?, " +
                    "ficha_solucion_aplicada = ?, ficha_componentes_cambio = ?, " +
                    "ficha_tiempo_estimado = ?, ficha_tiempo_real = ?, " +
                    "ficha_observaciones = ?, ficha_tecnico_firma = ?, " +
                    "ficha_usuario_firma = ?, ficha_estado = ?, " +
                    "actualizado_en = ? WHERE ficha_id = ?";
        
        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, ficha.getMantId());
            stmt.setDate(2, Date.valueOf(ficha.getFichaFecha()));
            stmt.setString(3, ficha.getFichaProblemaReportado());
            stmt.setString(4, ficha.getFichaDiagnostico());
            stmt.setString(5, ficha.getFichaSolucionAplicada());
            stmt.setString(6, ficha.getFichaComponentesCambio());
            stmt.setObject(7, ficha.getFichaTiempoEstimado());
            stmt.setObject(8, ficha.getFichaTiempoReal());
            stmt.setString(9, ficha.getFichaObservaciones());
            stmt.setString(10, ficha.getFichaTecnicoFirma());
            stmt.setString(11, ficha.getFichaUsuarioFirma());
            stmt.setString(12, ficha.getFichaEstado().name());
            stmt.setTimestamp(13, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setInt(14, ficha.getFichaId());
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al actualizar ficha de reporte: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Encuentra una ficha de reporte por ID
     */
    public FichaReporte findById(Integer id) {
        String sql = "SELECT * FROM FICHA_REPORTE WHERE ficha_id = ?";
        
        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToFichaReporte(rs);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error al buscar ficha de reporte por ID: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Encuentra una ficha de reporte por ID de mantenimiento
     */
    public FichaReporte findByMantenimiento(Integer mantId) {
        String sql = "SELECT * FROM FICHA_REPORTE WHERE mant_id = ?";
        
        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, mantId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToFichaReporte(rs);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error al buscar ficha por mantenimiento: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Obtiene todas las fichas de reporte
     */
    public List<FichaReporte> findAll() {
        List<FichaReporte> fichas = new ArrayList<>();
        String sql = "SELECT * FROM FICHA_REPORTE ORDER BY ficha_fecha DESC";
        
        try (Connection conn = databaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                fichas.add(mapResultSetToFichaReporte(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error al obtener todas las fichas de reporte: " + e.getMessage());
            e.printStackTrace();
        }
        
        return fichas;
    }
    
    /**
     * Busca fichas por estado
     */
    public List<FichaReporte> findByEstado(EstadoFicha estado) {
        List<FichaReporte> fichas = new ArrayList<>();
        String sql = "SELECT * FROM FICHA_REPORTE WHERE ficha_estado = ? ORDER BY ficha_fecha DESC";
        
        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, estado.name());
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    fichas.add(mapResultSetToFichaReporte(rs));
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error al buscar fichas por estado: " + e.getMessage());
            e.printStackTrace();
        }
        
        return fichas;
    }
    
    /**
     * Busca fichas por rango de fechas
     */
    public List<FichaReporte> findByFechaRango(java.time.LocalDate fechaInicio, java.time.LocalDate fechaFin) {
        List<FichaReporte> fichas = new ArrayList<>();
        String sql = "SELECT * FROM FICHA_REPORTE WHERE ficha_fecha BETWEEN ? AND ? ORDER BY ficha_fecha DESC";
        
        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDate(1, Date.valueOf(fechaInicio));
            stmt.setDate(2, Date.valueOf(fechaFin));
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    fichas.add(mapResultSetToFichaReporte(rs));
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error al buscar fichas por rango de fechas: " + e.getMessage());
            e.printStackTrace();
        }
        
        return fichas;
    }
    
    /**
     * Obtiene fichas pendientes (en estado Borrador)
     */
    public List<FichaReporte> findPendientes() {
        return findByEstado(EstadoFicha.Borrador);
    }
    
    /**
     * Elimina una ficha de reporte
     */
    public boolean delete(Integer id) {
        String sql = "DELETE FROM FICHA_REPORTE WHERE ficha_id = ?";
        
        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al eliminar ficha de reporte: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Marca una ficha como enviada
     */
    public boolean marcarComoEnviada(Integer fichaId) {
        String sql = "UPDATE FICHA_REPORTE SET ficha_estado = ?, actualizado_en = ? WHERE ficha_id = ?";
        
        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, EstadoFicha.Enviada.name());
            stmt.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setInt(3, fichaId);
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al marcar ficha como enviada: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Mapea un ResultSet a un objeto FichaReporte
     */
    private FichaReporte mapResultSetToFichaReporte(ResultSet rs) throws SQLException {
        FichaReporte ficha = new FichaReporte();
        
        ficha.setFichaId(rs.getInt("ficha_id"));
        ficha.setMantId(rs.getInt("mant_id"));
        ficha.setFichaNumero(rs.getString("ficha_numero"));
        
        Date fichaFecha = rs.getDate("ficha_fecha");
        if (fichaFecha != null) {
            ficha.setFichaFecha(fichaFecha.toLocalDate());
        }
        
        ficha.setFichaProblemaReportado(rs.getString("ficha_problema_reportado"));
        ficha.setFichaDiagnostico(rs.getString("ficha_diagnostico"));
        ficha.setFichaSolucionAplicada(rs.getString("ficha_solucion_aplicada"));
        ficha.setFichaComponentesCambio(rs.getString("ficha_componentes_cambio"));
        
        int tiempoEstimado = rs.getInt("ficha_tiempo_estimado");
        if (!rs.wasNull()) {
            ficha.setFichaTiempoEstimado(tiempoEstimado);
        }
        
        int tiempoReal = rs.getInt("ficha_tiempo_real");
        if (!rs.wasNull()) {
            ficha.setFichaTiempoReal(tiempoReal);
        }
        
        ficha.setFichaObservaciones(rs.getString("ficha_observaciones"));
        ficha.setFichaTecnicoFirma(rs.getString("ficha_tecnico_firma"));
        ficha.setFichaUsuarioFirma(rs.getString("ficha_usuario_firma"));
        
        String estadoStr = rs.getString("ficha_estado");
        if (estadoStr != null) {
            ficha.setFichaEstado(EstadoFicha.valueOf(estadoStr));
        }
        
        int creadoPor = rs.getInt("creado_por");
        if (!rs.wasNull()) {
            ficha.setCreadoPor(creadoPor);
        }
        
        Timestamp creadoEn = rs.getTimestamp("creado_en");
        if (creadoEn != null) {
            ficha.setCreadoEn(creadoEn.toLocalDateTime());
        }
        
        Timestamp actualizadoEn = rs.getTimestamp("actualizado_en");
        if (actualizadoEn != null) {
            ficha.setActualizadoEn(actualizadoEn.toLocalDateTime());
        }
        
        return ficha;
    }
    
    /**
     * Cuenta fichas por estado
     */
    public int countByEstado(EstadoFicha estado) {
        String sql = "SELECT COUNT(*) FROM FICHA_REPORTE WHERE ficha_estado = ?";
        
        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, estado.name());
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error al contar fichas por estado: " + e.getMessage());
            e.printStackTrace();
        }
        
        return 0;
    }
}
