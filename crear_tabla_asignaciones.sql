-- Script para crear la tabla ticket_asignaciones si no existe
-- Sistema de Gestión de Activos - Cooperativa Ypacaraí LTDA

USE sistema_activos_ypacarai;

-- Verificar si la tabla existe
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

-- Crear índices para optimización
CREATE INDEX IF NOT EXISTS idx_tas_ticket_activo ON ticket_asignaciones(tick_id, tas_activo);
CREATE INDEX IF NOT EXISTS idx_tas_usuario_activo ON ticket_asignaciones(usu_id, tas_activo);
CREATE INDEX IF NOT EXISTS idx_tas_rol ON ticket_asignaciones(tas_rol_asignacion);

-- Verificar que la tabla se creó correctamente
SELECT 'Tabla ticket_asignaciones verificada/creada exitosamente' as mensaje;
SHOW TABLES LIKE 'ticket_asignaciones';