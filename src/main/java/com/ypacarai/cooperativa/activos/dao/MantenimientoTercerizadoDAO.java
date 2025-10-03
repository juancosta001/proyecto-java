package com.ypacarai.cooperativa.activos.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.ypacarai.cooperativa.activos.config.DatabaseConfig;
import com.ypacarai.cooperativa.activos.model.MantenimientoTercerizado;

/**
 * DAO para la entidad MantenimientoTercerizado
 * Sistema de Gestión de Activos - Cooperativa Ypacaraí LTDA
 */
public class MantenimientoTercerizadoDAO {
    
    private static final String INSERT = 
        "INSERT INTO mantenimiento_tercerizado (act_id, proveedor_id, problema_inicial, " +
        "costo_estimado, estado, usu_id, fecha_creacion, fecha_solicitud) " +
        "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    
    private static final String SELECT_ALL = 
        "SELECT mt.id, mt.act_id, mt.proveedor_id, mt.problema_inicial, " +
        "mt.fecha_retiro, mt.fecha_entrega, mt.costo_estimado, mt.costo_final, " +
        "mt.estado, mt.descripcion_servicio, mt.observaciones, " +
        "mt.garantia_dias, mt.fecha_vencimiento_garantia, mt.usu_id, " +
        "mt.fecha_creacion, mt.fecha_actualizacion, mt.estado_pago, mt.fecha_solicitud, " +
        "ps.nombre as proveedor_nombre, ps.contacto as proveedor_contacto, " +
        "ps.telefono as proveedor_telefono, " +
        "a.act_numero_activo as activo_numero, a.act_marca as activo_marca, " +
        "a.act_modelo as activo_modelo, " +
        "u.usu_nombre as usuario_nombre " +
        "FROM mantenimiento_tercerizado mt " +
        "INNER JOIN proveedor_servicio ps ON ps.id = mt.proveedor_id " +
        "INNER JOIN activo a ON a.act_id = mt.act_id " +
        "INNER JOIN usuario u ON u.usu_id = mt.usu_id " +
        "ORDER BY mt.fecha_creacion DESC";
    
    private static final String SELECT_BY_ID = SELECT_ALL.replace("ORDER BY mt.fecha_creacion DESC", "WHERE mt.id = ?");
    
    private static final String SELECT_BY_ACTIVO = SELECT_ALL.replace("ORDER BY mt.fecha_creacion DESC", "WHERE mt.act_id = ? ORDER BY mt.fecha_creacion DESC");
    
    private static final String SELECT_BY_PROVEEDOR = SELECT_ALL.replace("ORDER BY mt.fecha_creacion DESC", "WHERE mt.proveedor_id = ? ORDER BY mt.fecha_creacion DESC");
    
    private static final String SELECT_BY_ESTADO = SELECT_ALL.replace("ORDER BY mt.fecha_creacion DESC", "WHERE mt.estado = ? ORDER BY mt.fecha_creacion DESC");
    
    private static final String UPDATE = 
        "UPDATE mantenimiento_tercerizado SET fecha_retiro = ?, fecha_entrega = ?, " +
        "costo_estimado = ?, costo_final = ?, estado = ?, descripcion_servicio = ?, " +
        "observaciones = ?, garantia_dias = ?, fecha_vencimiento_garantia = ?, " +
        "estado_pago = ?, fecha_actualizacion = CURRENT_TIMESTAMP " +
        "WHERE id = ?";
    
    private static final String UPDATE_ESTADO = 
        "UPDATE mantenimiento_tercerizado SET estado = ?, fecha_actualizacion = CURRENT_TIMESTAMP WHERE id = ?";
    
    private static final String UPDATE_FECHA_RETIRO = 
        "UPDATE mantenimiento_tercerizado SET fecha_retiro = DATE_ADD(CURRENT_TIMESTAMP, INTERVAL 1 SECOND), observaciones = ?, " +
        "estado = 'En_Proceso', fecha_actualizacion = CURRENT_TIMESTAMP WHERE id = ?";
    
    private static final String UPDATE_FECHA_ENTREGA = 
        "UPDATE mantenimiento_tercerizado SET fecha_entrega = DATE_ADD(CURRENT_TIMESTAMP, INTERVAL 2 SECOND), descripcion_servicio = ?, " +
        "costo_final = ?, garantia_dias = ?, fecha_vencimiento_garantia = ?, " +
        "estado = 'Finalizado', estado_pago = ?, observaciones = ?, " +
        "fecha_actualizacion = CURRENT_TIMESTAMP WHERE id = ?";
    
    /**
     * Inserta un nuevo mantenimiento tercerizado
     */
    public int insert(MantenimientoTercerizado mantenimiento) {
        // Debug: imprimir los valores que se van a insertar
        System.out.println("=== DEBUG: Insertando mantenimiento tercerizado ===");
        System.out.println("Activo ID: " + mantenimiento.getActivoId());
        System.out.println("Proveedor ID: " + mantenimiento.getProveedorId());
        System.out.println("Problema: " + mantenimiento.getDescripcionProblema());
        System.out.println("Monto presupuestado: " + mantenimiento.getMontoPresupuestado());
        System.out.println("Estado: " + mantenimiento.getEstado().name());
        System.out.println("Usuario ID: " + mantenimiento.getRegistradoPor());
        
        // Validar que existe el usuario
        if (!validarUsuarioExiste(mantenimiento.getRegistradoPor())) {
            throw new RuntimeException("El usuario con ID " + mantenimiento.getRegistradoPor() + " no existe en la base de datos");
        }
        
        // Validar que existe el activo
        if (!validarActivoExiste(mantenimiento.getActivoId())) {
            throw new RuntimeException("El activo con ID " + mantenimiento.getActivoId() + " no existe en la base de datos");
        }
        
        // Validar que existe el proveedor  
        if (!validarProveedorExiste(mantenimiento.getProveedorId())) {
            throw new RuntimeException("El proveedor con ID " + mantenimiento.getProveedorId() + " no existe en la base de datos");
        }
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(INSERT, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, mantenimiento.getActivoId());
            stmt.setInt(2, mantenimiento.getProveedorId());
            stmt.setString(3, mantenimiento.getDescripcionProblema());
            
            // Monto presupuestado (costo_estimado)
            if (mantenimiento.getMontoPresupuestado() != null) {
                stmt.setBigDecimal(4, mantenimiento.getMontoPresupuestado());
            } else {
                stmt.setNull(4, java.sql.Types.DECIMAL);
            }
            
            stmt.setString(5, mantenimiento.getEstado().name());
            stmt.setInt(6, mantenimiento.getRegistradoPor());
            stmt.setTimestamp(7, new Timestamp(System.currentTimeMillis()));
            stmt.setTimestamp(8, new Timestamp(System.currentTimeMillis())); // fecha_solicitud
            
            int result = stmt.executeUpdate();
            
            if (result > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        mantenimiento.setMantTercId(rs.getInt(1));
                        return mantenimiento.getMantTercId();
                    }
                }
            }
            
            return 0;
        } catch (SQLException e) {
            System.err.println("Error insertando mantenimiento tercerizado: " + e.getMessage());
            throw new RuntimeException("Error al insertar mantenimiento tercerizado", e);
        }
    }
    
    /**
     * Obtiene todos los mantenimientos
     */
    public List<MantenimientoTercerizado> findAll() {
        List<MantenimientoTercerizado> mantenimientos = new ArrayList<>();
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_ALL);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                mantenimientos.add(mapResultSetToMantenimiento(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error obteniendo mantenimientos: " + e.getMessage());
            throw new RuntimeException("Error al obtener mantenimientos", e);
        }
        
        return mantenimientos;
    }
    
    /**
     * Busca un mantenimiento por ID
     */
    public Optional<MantenimientoTercerizado> findById(int id) {
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_BY_ID)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToMantenimiento(rs));
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error buscando mantenimiento por ID: " + e.getMessage());
            throw new RuntimeException("Error al buscar mantenimiento", e);
        }
        
        return Optional.empty();
    }
    
    /**
     * Obtiene mantenimientos por activo
     */
    public List<MantenimientoTercerizado> findByActivo(int activoId) {
        List<MantenimientoTercerizado> mantenimientos = new ArrayList<>();
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_BY_ACTIVO)) {
            
            stmt.setInt(1, activoId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    mantenimientos.add(mapResultSetToMantenimiento(rs));
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error obteniendo mantenimientos por activo: " + e.getMessage());
            throw new RuntimeException("Error al obtener mantenimientos por activo", e);
        }
        
        return mantenimientos;
    }
    
    /**
     * Obtiene mantenimientos por proveedor
     */
    public List<MantenimientoTercerizado> findByProveedor(int proveedorId) {
        List<MantenimientoTercerizado> mantenimientos = new ArrayList<>();
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_BY_PROVEEDOR)) {
            
            stmt.setInt(1, proveedorId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    mantenimientos.add(mapResultSetToMantenimiento(rs));
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error obteniendo mantenimientos por proveedor: " + e.getMessage());
            throw new RuntimeException("Error al obtener mantenimientos por proveedor", e);
        }
        
        return mantenimientos;
    }
    
    /**
     * Obtiene mantenimientos por estado
     */
    public List<MantenimientoTercerizado> findByEstado(MantenimientoTercerizado.EstadoMantenimiento estado) {
        List<MantenimientoTercerizado> mantenimientos = new ArrayList<>();
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_BY_ESTADO)) {
            
            stmt.setString(1, estado.name());
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    mantenimientos.add(mapResultSetToMantenimiento(rs));
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error obteniendo mantenimientos por estado: " + e.getMessage());
            throw new RuntimeException("Error al obtener mantenimientos por estado", e);
        }
        
        return mantenimientos;
    }
    
    /**
     * Actualiza un mantenimiento completo
     */
    public boolean update(MantenimientoTercerizado mantenimiento) {
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(UPDATE)) {
            
            // Manejo de fecha de retiro
            if (mantenimiento.getFechaRetiro() != null) {
                stmt.setDate(1, Date.valueOf(mantenimiento.getFechaRetiro()));
            } else {
                stmt.setNull(1, Types.DATE);
            }
            
            // Manejo de fecha de entrega
            if (mantenimiento.getFechaEntrega() != null) {
                stmt.setDate(2, Date.valueOf(mantenimiento.getFechaEntrega()));
            } else {
                stmt.setNull(2, Types.DATE);
            }
            
            // Manejo de montos
            if (mantenimiento.getMontoPresupuestado() != null) {
                stmt.setBigDecimal(3, mantenimiento.getMontoPresupuestado());
            } else {
                stmt.setNull(3, Types.DECIMAL);
            }
            
            if (mantenimiento.getMontoCobrado() != null) {
                stmt.setBigDecimal(4, mantenimiento.getMontoCobrado());
            } else {
                stmt.setNull(4, Types.DECIMAL);
            }
            
            stmt.setString(5, mantenimiento.getEstado().name());
            stmt.setString(6, mantenimiento.getObservacionesRetiro());
            stmt.setString(7, mantenimiento.getObservacionesEntrega());
            stmt.setString(8, mantenimiento.getEstadoEquipoDespues());
            stmt.setString(9, mantenimiento.getTrabajoRealizado());
            stmt.setBoolean(10, mantenimiento.isGarantia());
            stmt.setInt(11, mantenimiento.getDiasGarantia());
            stmt.setInt(12, mantenimiento.getMantTercId());
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error actualizando mantenimiento: " + e.getMessage());
            throw new RuntimeException("Error al actualizar mantenimiento", e);
        }
    }
    
    /**
     * Registra el retiro del equipo
     */
    public boolean registrarRetiro(int mantTercId, LocalDate fechaRetiro, String observaciones) {
        System.out.println("=== DEBUG: Registrando retiro ===");
        System.out.println("Mantenimiento ID: " + mantTercId);
        System.out.println("Observaciones: " + observaciones);
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(UPDATE_FECHA_RETIRO)) {
            
            // Ya no necesitamos el parámetro fecha_retiro porque usamos CURRENT_TIMESTAMP
            stmt.setString(1, observaciones);
            stmt.setInt(2, mantTercId);
            
            int result = stmt.executeUpdate();
            System.out.println("Filas actualizadas: " + result);
            
            return result > 0;
            
        } catch (SQLException e) {
            System.err.println("Error registrando retiro: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error al registrar retiro", e);
        }
    }
    
    /**
     * Registra la entrega del equipo
     */
    public boolean registrarEntrega(int mantTercId, LocalDate fechaEntrega, String observacionesEntrega,
                                  String estadoEquipoDespues, String trabajoRealizado, 
                                  java.math.BigDecimal montoCobrado, boolean garantia, int diasGarantia) {
        System.out.println("=== DEBUG: Registrando entrega ===");
        System.out.println("Mantenimiento ID: " + mantTercId);
        System.out.println("Trabajo realizado: " + trabajoRealizado);
        System.out.println("Monto cobrado: " + montoCobrado);
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(UPDATE_FECHA_ENTREGA)) {
            
            // Ya no necesitamos el parámetro fecha_entrega porque usamos CURRENT_TIMESTAMP
            stmt.setString(1, trabajoRealizado); // descripcion_servicio
            
            if (montoCobrado != null) {
                stmt.setBigDecimal(2, montoCobrado); // costo_final
            } else {
                stmt.setNull(2, Types.DECIMAL);
            }
            
            stmt.setInt(3, diasGarantia); // garantia_dias
            
            // fecha_vencimiento_garantia
            if (garantia && diasGarantia > 0) {
                LocalDate fechaVencimiento = LocalDate.now().plusDays(diasGarantia);
                stmt.setDate(4, Date.valueOf(fechaVencimiento));
            } else {
                stmt.setNull(4, Types.DATE);
            }
            
            stmt.setString(5, "Pendiente"); // estado_pago por defecto
            stmt.setString(6, observacionesEntrega); // observaciones
            stmt.setInt(7, mantTercId); // id
            
            int result = stmt.executeUpdate();
            System.out.println("Filas actualizadas en entrega: " + result);
            
            return result > 0;
            
        } catch (SQLException e) {
            System.err.println("Error registrando entrega: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error al registrar entrega", e);
        }
    }
    
    /**
     * Actualiza solo el estado
     */
    public boolean updateEstado(int mantTercId, MantenimientoTercerizado.EstadoMantenimiento estado) {
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(UPDATE_ESTADO)) {
            
            stmt.setString(1, estado.name());
            stmt.setInt(2, mantTercId);
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error actualizando estado: " + e.getMessage());
            throw new RuntimeException("Error al actualizar estado", e);
        }
    }
    
    /**
     * Mapea un ResultSet a un objeto MantenimientoTercerizado
     */
    private MantenimientoTercerizado mapResultSetToMantenimiento(ResultSet rs) throws SQLException {
        MantenimientoTercerizado mantenimiento = new MantenimientoTercerizado();
        
        mantenimiento.setMantTercId(rs.getInt("id"));
        mantenimiento.setActivoId(rs.getInt("act_id"));
        mantenimiento.setProveedorId(rs.getInt("proveedor_id"));
        mantenimiento.setDescripcionProblema(rs.getString("problema_inicial"));
        mantenimiento.setRegistradoPor(rs.getInt("usu_id"));
        
        // Estado
        String estadoStr = rs.getString("estado");
        if (estadoStr != null) {
            mantenimiento.setEstado(MantenimientoTercerizado.EstadoMantenimiento.valueOf(estadoStr));
        }
        
        // Fechas
        Date fechaRetiro = rs.getDate("fecha_retiro");
        if (fechaRetiro != null) {
            mantenimiento.setFechaRetiro(fechaRetiro.toLocalDate());
        }
        
        Date fechaEntrega = rs.getDate("fecha_entrega");
        if (fechaEntrega != null) {
            mantenimiento.setFechaEntrega(fechaEntrega.toLocalDate());
        }
        
        Timestamp creadoEn = rs.getTimestamp("fecha_creacion");
        if (creadoEn != null) {
            mantenimiento.setCreadoEn(creadoEn.toLocalDateTime());
        }
        
        Timestamp actualizadoEn = rs.getTimestamp("fecha_actualizacion");
        if (actualizadoEn != null) {
            mantenimiento.setActualizadoEn(actualizadoEn.toLocalDateTime());
        }
        
        // Montos
        mantenimiento.setMontoPresupuestado(rs.getBigDecimal("costo_estimado"));
        mantenimiento.setMontoCobrado(rs.getBigDecimal("costo_final"));
        
        // Campos adicionales de la nueva estructura
        mantenimiento.setObservacionesRetiro(rs.getString("observaciones"));
        mantenimiento.setTrabajoRealizado(rs.getString("descripcion_servicio"));
        mantenimiento.setDiasGarantia(rs.getInt("garantia_dias"));
        
        Date fechaVencimientoGarantia = rs.getDate("fecha_vencimiento_garantia");
        if (fechaVencimientoGarantia != null) {
            mantenimiento.setGarantia(true);
        }
        
        // Campos relacionados del proveedor
        mantenimiento.setNombreProveedor(rs.getString("proveedor_nombre"));
        mantenimiento.setTelefonoProveedor(rs.getString("proveedor_telefono"));
        
        // Campos relacionados del activo
        mantenimiento.setNumeroActivo(rs.getString("activo_numero"));
        mantenimiento.setMarcaActivo(rs.getString("activo_marca"));
        mantenimiento.setModeloActivo(rs.getString("activo_modelo"));
        
        // Campos relacionados del usuario
        String nombreUsuario = rs.getString("usuario_nombre");
        if (nombreUsuario != null) {
            mantenimiento.setNombreRegistrador(nombreUsuario);
        }
        
        return mantenimiento;
    }
    
    /**
     * Valida si existe un usuario
     */
    private boolean validarUsuarioExiste(int usuarioId) {
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) FROM usuario WHERE usu_id = ?")) {
            
            stmt.setInt(1, usuarioId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error validando usuario: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Valida si existe un activo
     */
    private boolean validarActivoExiste(int activoId) {
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) FROM activo WHERE act_id = ?")) {
            
            stmt.setInt(1, activoId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error validando activo: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Valida si existe un proveedor
     */
    private boolean validarProveedorExiste(int proveedorId) {
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) FROM proveedor_servicio WHERE id = ?")) {
            
            stmt.setInt(1, proveedorId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error validando proveedor: " + e.getMessage());
        }
        
        return false;
    }
}