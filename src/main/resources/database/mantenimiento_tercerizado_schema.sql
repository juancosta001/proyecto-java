-- Script para crear las tablas del sistema de mantenimiento tercerizado
-- Sistema de Gestión de Activos - Cooperativa Ypacaraí LTDA

-- Tabla de Proveedores de Servicios Técnicos
CREATE TABLE IF NOT EXISTS proveedor_servicio (
    prv_id INT AUTO_INCREMENT PRIMARY KEY,
    prv_nombre VARCHAR(200) NOT NULL,
    prv_numero_telefono VARCHAR(50) NOT NULL,
    prv_email VARCHAR(150),
    prv_direccion TEXT,
    prv_contacto_principal VARCHAR(150) NOT NULL,
    prv_especialidades TEXT,
    activo BOOLEAN DEFAULT TRUE,
    creado_en TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    actualizado_en TIMESTAMP NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_proveedor_nombre (prv_nombre),
    INDEX idx_proveedor_activo (activo)
);

-- Tabla de Mantenimiento Tercerizado
CREATE TABLE IF NOT EXISTS mantenimiento_tercerizado (
    mant_terc_id INT AUTO_INCREMENT PRIMARY KEY,
    activo_id INT NOT NULL,
    proveedor_id INT NOT NULL,
    descripcion_problema TEXT NOT NULL,
    fecha_retiro DATE NULL,
    fecha_entrega DATE NULL,
    monto_presupuestado DECIMAL(10,2) NULL,
    monto_cobrado DECIMAL(10,2) NULL,
    estado ENUM('Solicitado', 'En_Proceso', 'Finalizado', 'Cancelado') DEFAULT 'Solicitado',
    observaciones_retiro TEXT NULL,
    observaciones_entrega TEXT NULL,
    estado_equipo_antes TEXT NULL,
    estado_equipo_despues TEXT NULL,
    trabajo_realizado TEXT NULL,
    garantia BOOLEAN DEFAULT FALSE,
    dias_garantia INT DEFAULT 0,
    registrado_por INT NOT NULL,
    creado_en TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    actualizado_en TIMESTAMP NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (activo_id) REFERENCES ACTIVO(act_id) ON DELETE RESTRICT ON UPDATE CASCADE,
    FOREIGN KEY (proveedor_id) REFERENCES proveedor_servicio(prv_id) ON DELETE RESTRICT ON UPDATE CASCADE,
    FOREIGN KEY (registrado_por) REFERENCES USUARIO(usu_id) ON DELETE RESTRICT ON UPDATE CASCADE,
    
    INDEX idx_mant_terc_activo (activo_id),
    INDEX idx_mant_terc_proveedor (proveedor_id),
    INDEX idx_mant_terc_estado (estado),
    INDEX idx_mant_terc_fecha_retiro (fecha_retiro),
    INDEX idx_mant_terc_fecha_entrega (fecha_entrega),
    INDEX idx_mant_terc_creado_en (creado_en)
);

-- Insertar algunos proveedores de ejemplo (opcionales)
INSERT IGNORE INTO proveedor_servicio (prv_nombre, prv_numero_telefono, prv_email, prv_direccion, prv_contacto_principal, prv_especialidades) VALUES
('TecnoServicios Ypacaraí', '0981-123456', 'contacto@tecnoservicios.com.py', 'Av. Mcal. López 123, Ypacaraí', 'Juan Pérez', 'Reparación de PC, impresoras, UPS, equipos de red'),
('Informática Central', '0984-789012', 'info@infocentral.com.py', 'Calle Real 456, Asunción', 'María González', 'Mantenimiento de servidores, equipos de oficina, soporte técnico'),
('Reparaciones Express', '0975-345678', 'reparaciones@express.com.py', 'Ruta 2, Km 25, San Lorenzo', 'Carlos López', 'Reparación rápida de impresoras, fotocopiadoras, escáneres');

-- Comentarios sobre el diseño:
-- 1. La tabla proveedor_servicio almacena todos los datos de los proveedores de servicios técnicos
-- 2. La tabla mantenimiento_tercerizado lleva el registro completo del ciclo de vida del mantenimiento
-- 3. Se mantienen foreign keys con restricción RESTRICT para evitar eliminar registros con dependencias
-- 4. Los estados del mantenimiento siguen el flujo: Solicitado -> En_Proceso -> Finalizado
-- 5. Se pueden cancelar mantenimientos en cualquier momento
-- 6. Los montos son opcionales para permitir trabajos sin costo o presupuestos pendientes
-- 7. La garantía es opcional y se cuenta en días desde la fecha de entrega

-- Mostrar estructura de las tablas creadas
SHOW CREATE TABLE proveedor_servicio;
SHOW CREATE TABLE mantenimiento_tercerizado;