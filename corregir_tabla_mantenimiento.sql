-- =====================================================
-- VERIFICACIÓN Y CORRECCIÓN DE TABLA MANTENIMIENTO_TERCERIZADO
-- Script para corregir nombres de columnas
-- =====================================================

USE sistema_activos_ypacarai;

-- Verificar estructura actual
SELECT 'Estructura actual de la tabla:' as Info;
DESCRIBE mantenimiento_tercerizado;

-- Si la tabla tiene columnas con nombres incorrectos, podemos corregirla
-- Primero hacer backup de datos existentes (si los hay)
CREATE TABLE IF NOT EXISTS mantenimiento_tercerizado_backup AS 
SELECT * FROM mantenimiento_tercerizado;

-- Eliminar tabla actual
DROP TABLE IF EXISTS mantenimiento_tercerizado;

-- Recrear tabla con estructura correcta
CREATE TABLE mantenimiento_tercerizado (
    id INT AUTO_INCREMENT PRIMARY KEY,
    activo_id INT NOT NULL COMMENT 'ID del activo en mantenimiento',
    proveedor_id INT NOT NULL,
    usuario_solicita_id INT NOT NULL COMMENT 'ID del usuario que solicita',
    
    -- Estado del mantenimiento
    estado ENUM('Solicitado', 'En_Proceso', 'Finalizado') NOT NULL DEFAULT 'Solicitado',
    
    -- Fechas del proceso
    fecha_solicitud TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_retiro TIMESTAMP NULL,
    fecha_entrega TIMESTAMP NULL,
    
    -- Información del problema y servicio
    problema_inicial TEXT NOT NULL COMMENT 'Descripción del problema antes del retiro',
    descripcion_servicio TEXT COMMENT 'Descripción del servicio realizado',
    
    -- Información de costos
    costo_estimado DECIMAL(10,2) DEFAULT 0.00,
    costo_final DECIMAL(10,2) DEFAULT 0.00,
    estado_pago ENUM('Pendiente', 'Pagado') DEFAULT 'Pendiente',
    
    -- Garantía
    garantia_dias INT DEFAULT 0,
    fecha_vencimiento_garantia DATE NULL,
    
    -- Observaciones adicionales
    observaciones TEXT,
    
    -- Auditoría
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    -- Solo clave foránea para proveedor
    FOREIGN KEY (proveedor_id) REFERENCES proveedor_servicio(id) ON DELETE RESTRICT ON UPDATE CASCADE,
    
    -- Índices
    INDEX idx_activo (activo_id),
    INDEX idx_proveedor (proveedor_id),
    INDEX idx_usuario_solicita (usuario_solicita_id),
    INDEX idx_estado (estado),
    INDEX idx_fecha_solicitud (fecha_solicitud),
    INDEX idx_fecha_retiro (fecha_retiro),
    INDEX idx_fecha_entrega (fecha_entrega),
    INDEX idx_estado_pago (estado_pago),
    
    -- Restricciones de validación
    CONSTRAINT chk_costos CHECK (costo_estimado >= 0 AND costo_final >= 0),
    CONSTRAINT chk_garantia CHECK (garantia_dias >= 0)
    
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='Tabla para gestionar el mantenimiento tercerizado de activos';

-- Verificar que la tabla se creó correctamente
SELECT 'Nueva estructura de la tabla:' as Info;
DESCRIBE mantenimiento_tercerizado;

SELECT 'Tabla recreada exitosamente con estructura correcta' as Resultado;