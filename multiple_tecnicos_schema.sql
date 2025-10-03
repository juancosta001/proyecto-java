-- Sistema de Gestión de Activos - Cooperativa Ypacaraí LTDA
-- Script para agregar soporte de múltiples técnicos asignados a tickets
-- Fecha: 26 de Septiembre, 2025

-- ================================================
-- 1. CREAR TABLA DE ASIGNACIONES MÚLTIPLES
-- ================================================

CREATE TABLE IF NOT EXISTS ticket_asignaciones (
    tas_id INT PRIMARY KEY AUTO_INCREMENT,
    tick_id INT NOT NULL,
    usu_id INT NOT NULL,
    tas_fecha_asignacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    tas_activo BOOLEAN DEFAULT TRUE,
    tas_rol_asignacion ENUM('Responsable', 'Colaborador', 'Supervisor') DEFAULT 'Responsable',
    tas_observaciones TEXT,
    creado_en TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    actualizado_en TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    -- Claves foráneas
    CONSTRAINT fk_tas_ticket FOREIGN KEY (tick_id) REFERENCES ticket(tick_id) ON DELETE CASCADE,
    CONSTRAINT fk_tas_usuario FOREIGN KEY (usu_id) REFERENCES usuario(usu_id) ON DELETE CASCADE,
    
    -- Índices únicos para evitar duplicados
    UNIQUE KEY uk_ticket_usuario (tick_id, usu_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ================================================
-- 2. MIGRAR ASIGNACIONES EXISTENTES
-- ================================================

-- Migrar todas las asignaciones existentes de tick_asignado_a a la nueva tabla
INSERT INTO ticket_asignaciones (tick_id, usu_id, tas_fecha_asignacion, tas_rol_asignacion)
SELECT 
    tick_id, 
    tick_asignado_a, 
    tick_fecha_apertura,
    'Responsable' as rol
FROM ticket 
WHERE tick_asignado_a IS NOT NULL;

-- ================================================
-- 3. CREAR VISTA PARA COMPATIBILIDAD
-- ================================================

-- Vista que mantiene compatibilidad con el código existente
CREATE OR REPLACE VIEW v_ticket_principal AS
SELECT 
    t.*,
    ta.usu_id as tick_asignado_a_principal,
    u.usu_nombre as tecnico_principal,
    GROUP_CONCAT(
        CONCAT(u2.usu_nombre, ' (', ta2.tas_rol_asignacion, ')')
        ORDER BY ta2.tas_rol_asignacion, u2.usu_nombre
        SEPARATOR ', '
    ) as tecnicos_asignados
FROM ticket t
LEFT JOIN ticket_asignaciones ta ON t.tick_id = ta.tick_id AND ta.tas_rol_asignacion = 'Responsable' AND ta.tas_activo = TRUE
LEFT JOIN usuario u ON ta.usu_id = u.usu_id
LEFT JOIN ticket_asignaciones ta2 ON t.tick_id = ta2.tick_id AND ta2.tas_activo = TRUE
LEFT JOIN usuario u2 ON ta2.usu_id = u2.usu_id
GROUP BY t.tick_id;

-- ================================================
-- 4. PROCEDIMIENTOS ALMACENADOS
-- ================================================

DELIMITER //

-- Procedimiento para asignar múltiples técnicos a un ticket
CREATE PROCEDURE sp_asignar_tecnicos_ticket(
    IN p_tick_id INT,
    IN p_tecnicos JSON, -- Array de objetos: [{"usu_id": 1, "rol": "Responsable"}, {"usu_id": 2, "rol": "Colaborador"}]
    IN p_observaciones TEXT
)
BEGIN
    DECLARE done INT DEFAULT FALSE;
    DECLARE v_usu_id INT;
    DECLARE v_rol VARCHAR(50);
    DECLARE i INT DEFAULT 0;
    DECLARE array_length INT;
    
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        RESIGNAL;
    END;
    
    START TRANSACTION;
    
    -- Desactivar asignaciones existentes
    UPDATE ticket_asignaciones 
    SET tas_activo = FALSE, actualizado_en = NOW()
    WHERE tick_id = p_tick_id;
    
    -- Obtener la longitud del array JSON
    SET array_length = JSON_LENGTH(p_tecnicos);
    
    -- Iterar sobre el array JSON
    WHILE i < array_length DO
        SET v_usu_id = JSON_UNQUOTE(JSON_EXTRACT(p_tecnicos, CONCAT('$[', i, '].usu_id')));
        SET v_rol = IFNULL(JSON_UNQUOTE(JSON_EXTRACT(p_tecnicos, CONCAT('$[', i, '].rol'))), 'Responsable');
        
        -- Insertar o reactivar asignación
        INSERT INTO ticket_asignaciones (tick_id, usu_id, tas_rol_asignacion, tas_observaciones, tas_activo)
        VALUES (p_tick_id, v_usu_id, v_rol, p_observaciones, TRUE)
        ON DUPLICATE KEY UPDATE 
            tas_activo = TRUE,
            tas_rol_asignacion = VALUES(tas_rol_asignacion),
            tas_observaciones = VALUES(tas_observaciones),
            actualizado_en = NOW();
        
        SET i = i + 1;
    END WHILE;
    
    COMMIT;
END//

-- Procedimiento para obtener técnicos asignados a un ticket
CREATE PROCEDURE sp_obtener_tecnicos_ticket(IN p_tick_id INT)
BEGIN
    SELECT 
        ta.tas_id,
        ta.tick_id,
        ta.usu_id,
        u.usu_nombre,
        u.usu_email,
        ta.tas_rol_asignacion,
        ta.tas_fecha_asignacion,
        ta.tas_observaciones
    FROM ticket_asignaciones ta
    INNER JOIN usuario u ON ta.usu_id = u.usu_id
    WHERE ta.tick_id = p_tick_id 
    AND ta.tas_activo = TRUE
    ORDER BY 
        CASE ta.tas_rol_asignacion 
            WHEN 'Responsable' THEN 1
            WHEN 'Supervisor' THEN 2
            WHEN 'Colaborador' THEN 3
            ELSE 4
        END,
        u.usu_nombre;
END//

DELIMITER ;

-- ================================================
-- 5. ÍNDICES PARA OPTIMIZACIÓN
-- ================================================

CREATE INDEX idx_tas_ticket_activo ON ticket_asignaciones(tick_id, tas_activo);
CREATE INDEX idx_tas_usuario_activo ON ticket_asignaciones(usu_id, tas_activo);
CREATE INDEX idx_tas_rol ON ticket_asignaciones(tas_rol_asignacion);

-- ================================================
-- 6. DATOS DE PRUEBA
-- ================================================

-- Ejemplo de asignación múltiple (solo si existe el ticket con ID 1)
-- CALL sp_asignar_tecnicos_ticket(1, '[{"usu_id": 2, "rol": "Responsable"}, {"usu_id": 3, "rol": "Colaborador"}]', 'Asignación de equipo de trabajo');

-- ================================================
-- VERIFICACIONES
-- ================================================

-- Verificar la migración
SELECT 'Verificación de migración de asignaciones:' as mensaje;
SELECT COUNT(*) as total_asignaciones_migradas FROM ticket_asignaciones;

-- Ver ejemplo de la vista
SELECT 'Ejemplo de vista con múltiples técnicos:' as mensaje;
SELECT tick_id, tick_titulo, tecnico_principal, tecnicos_asignados 
FROM v_ticket_principal 
LIMIT 5;

SELECT '✅ Script de múltiples técnicos ejecutado exitosamente' as resultado;