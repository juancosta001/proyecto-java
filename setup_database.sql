-- =====================================================
-- SCRIPT DE CONFIGURACIÓN COMPLETO
-- Sistema de Gestión de Activos - Cooperativa Ypacaraí LTDA
-- =====================================================

-- Crear la base de datos si no existe
CREATE DATABASE IF NOT EXISTS sistema_activos_ypacarai 
    CHARACTER SET utf8mb4 
    COLLATE utf8mb4_unicode_ci;

-- Usar la base de datos
USE sistema_activos_ypacarai;

-- =====================================================
-- CREAR TODAS LAS TABLAS
-- =====================================================

-- Tabla USUARIO
CREATE TABLE IF NOT EXISTS USUARIO (
    usu_id INT AUTO_INCREMENT PRIMARY KEY,
    usu_nombre VARCHAR(100) NOT NULL,
    usu_usuario VARCHAR(50) NOT NULL UNIQUE,
    usu_password VARCHAR(255) NOT NULL,
    usu_rol ENUM('Jefe_Informatica', 'Tecnico', 'Consulta') NOT NULL,
    usu_email VARCHAR(100),
    activo BOOLEAN DEFAULT TRUE,
    creado_en TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    actualizado_en TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Tabla TIPO_ACTIVO
CREATE TABLE IF NOT EXISTS TIPO_ACTIVO (
    tip_act_id INT AUTO_INCREMENT PRIMARY KEY,
    tip_act_nombre VARCHAR(100) NOT NULL,
    tip_act_descripcion TEXT,
    activo BOOLEAN DEFAULT TRUE,
    creado_en TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabla UBICACION
CREATE TABLE IF NOT EXISTS UBICACION (
    ubi_id INT AUTO_INCREMENT PRIMARY KEY,
    ubi_nombre VARCHAR(100) NOT NULL,
    ubi_descripcion TEXT,
    activo BOOLEAN DEFAULT TRUE,
    creado_en TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabla ACTIVO
CREATE TABLE IF NOT EXISTS ACTIVO (
    act_id INT AUTO_INCREMENT PRIMARY KEY,
    act_numero_activo VARCHAR(50) NOT NULL UNIQUE,
    tip_act_id INT NOT NULL,
    act_marca VARCHAR(100),
    act_modelo VARCHAR(100),
    act_numero_serie VARCHAR(100),
    act_especificaciones TEXT,
    act_fecha_adquisicion DATE,
    act_estado ENUM('Operativo', 'En_Mantenimiento', 'Fuera_Servicio', 'Trasladado') DEFAULT 'Operativo',
    act_ubicacion_actual INT NOT NULL,
    act_responsable_actual VARCHAR(100),
    act_observaciones TEXT,
    creado_por INT NOT NULL,
    creado_en TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    actualizado_en TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (tip_act_id) REFERENCES TIPO_ACTIVO(tip_act_id),
    FOREIGN KEY (act_ubicacion_actual) REFERENCES UBICACION(ubi_id),
    FOREIGN KEY (creado_por) REFERENCES USUARIO(usu_id)
);

-- Tabla TICKET
CREATE TABLE IF NOT EXISTS TICKET (
    tick_id INT AUTO_INCREMENT PRIMARY KEY,
    act_id INT NOT NULL,
    tick_tipo ENUM('Mantenimiento', 'Reparacion', 'Traslado', 'Consulta') NOT NULL,
    tick_prioridad ENUM('Baja', 'Media', 'Alta', 'Critica') DEFAULT 'Media',
    tick_estado ENUM('Abierto', 'En_Progreso', 'Resuelto', 'Cerrado') DEFAULT 'Abierto',
    tick_descripcion TEXT NOT NULL,
    tick_solucion TEXT,
    tick_costo DECIMAL(10,2),
    tick_fecha_estimada DATE,
    creado_por INT NOT NULL,
    asignado_a INT,
    creado_en TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    actualizado_en TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (act_id) REFERENCES ACTIVO(act_id),
    FOREIGN KEY (creado_por) REFERENCES USUARIO(usu_id),
    FOREIGN KEY (asignado_a) REFERENCES USUARIO(usu_id)
);

-- =====================================================
-- INSERTAR DATOS DE PRUEBA
-- =====================================================

-- Insertar usuarios de prueba
INSERT IGNORE INTO USUARIO (usu_nombre, usu_usuario, usu_password, usu_rol, usu_email) VALUES
('Jefe de Informática', 'jefe_info', 'test1', 'Jefe_Informatica', 'jefe@ypacarai.coop.py'),
('Técnico Principal', 'tecnico1', 'test2', 'Tecnico', 'tecnico1@ypacarai.coop.py'),
('Técnico Soporte', 'tecnico2', 'test3', 'Tecnico', 'tecnico2@ypacarai.coop.py'),
('Usuario Consulta', 'consulta1', 'test4', 'Consulta', 'consulta@ypacarai.coop.py');

-- Insertar tipos de activos
INSERT IGNORE INTO TIPO_ACTIVO (tip_act_nombre, tip_act_descripcion) VALUES
('Computadora de Escritorio', 'PCs para uso administrativo'),
('Laptop', 'Computadoras portátiles'),
('Servidor', 'Servidores de red y aplicaciones'),
('Impresora', 'Impresoras y multifuncionales'),
('Monitor', 'Monitores y pantallas'),
('Proyector', 'Proyectores para presentaciones'),
('Router/Switch', 'Equipos de red'),
('Teléfono IP', 'Teléfonos VoIP'),
('UPS', 'Sistemas de alimentación ininterrumpida'),
('Scanner', 'Escáneres de documentos');

-- Insertar ubicaciones
INSERT IGNORE INTO UBICACION (ubi_nombre, ubi_descripcion) VALUES
('Oficina Principal', 'Oficina principal de la cooperativa'),
('Sala de Servidores', 'Datacenter principal'),
('Gerencia General', 'Oficina del gerente general'),
('Contabilidad', 'Departamento de contabilidad'),
('Recursos Humanos', 'Departamento de RRHH'),
('Atención al Cliente', 'Área de atención al público'),
('Sala de Reuniones', 'Sala de juntas y reuniones'),
('Informática', 'Departamento de sistemas'),
('Archivo', 'Área de archivo y documentos'),
('Deposito', 'Almacén de equipos');

-- Insertar activos de prueba
INSERT IGNORE INTO ACTIVO (act_numero_activo, tip_act_id, act_marca, act_modelo, act_numero_serie, 
                          act_fecha_adquisicion, act_estado, act_ubicacion_actual, act_responsable_actual, 
                          creado_por) VALUES
('PC-001', 1, 'Dell', 'OptiPlex 3080', 'DL001234', '2024-01-15', 'Operativo', 1, 'Juan Pérez', 1),
('PC-002', 1, 'HP', 'EliteDesk 800', 'HP567890', '2024-02-10', 'Operativo', 4, 'María González', 1),
('LAP-001', 2, 'Lenovo', 'ThinkPad E14', 'LN111222', '2024-03-05', 'Operativo', 3, 'Carlos Rodríguez', 1),
('SRV-001', 3, 'Dell', 'PowerEdge R740', 'DL999888', '2023-12-01', 'Operativo', 2, 'Admin Sistema', 1),
('IMP-001', 4, 'Canon', 'ImageRUNNER 2530i', 'CN445566', '2024-01-20', 'Operativo', 1, 'Secretaria', 1),
('MON-001', 5, 'Samsung', 'S24F350', 'SM778899', '2024-01-15', 'Operativo', 1, 'Juan Pérez', 1),
('MON-002', 5, 'LG', '24MK430H', 'LG334455', '2024-02-10', 'Operativo', 4, 'María González', 1),
('PROJ-001', 6, 'Epson', 'PowerLite X41+', 'EP123789', '2023-11-15', 'Operativo', 7, 'Sala Reuniones', 1),
('RTR-001', 7, 'TP-Link', 'Archer C80', 'TP987654', '2024-01-01', 'Operativo', 2, 'Admin Red', 1),
('UPS-001', 9, 'APC', 'Smart-UPS 1500VA', 'APC456123', '2023-12-15', 'Operativo', 2, 'Admin Sistema', 1);

-- Insertar tickets de prueba
INSERT IGNORE INTO TICKET (act_id, tick_tipo, tick_prioridad, tick_estado, tick_descripcion, 
                          creado_por, asignado_a) VALUES
(1, 'Mantenimiento', 'Media', 'Abierto', 'Limpieza general y actualización de software', 1, 2),
(4, 'Mantenimiento', 'Alta', 'En_Progreso', 'Actualización del sistema operativo del servidor', 1, 2),
(5, 'Reparacion', 'Baja', 'Abierto', 'Problema con alimentación de papel', 1, 3),
(8, 'Mantenimiento', 'Media', 'Resuelto', 'Cambio de lámpara del proyector', 1, 2);

-- =====================================================
-- CREAR VISTAS ÚTILES
-- =====================================================

-- Vista de activos completa con información relacionada
CREATE OR REPLACE VIEW vista_activos_completa AS
SELECT 
    a.act_id,
    a.act_numero_activo,
    ta.tip_act_nombre as tipo_activo,
    a.act_marca,
    a.act_modelo,
    a.act_numero_serie,
    a.act_fecha_adquisicion,
    a.act_estado,
    u.ubi_nombre as ubicacion,
    a.act_responsable_actual,
    usr.usu_nombre as creado_por_nombre,
    a.creado_en,
    a.actualizado_en
FROM ACTIVO a
LEFT JOIN TIPO_ACTIVO ta ON a.tip_act_id = ta.tip_act_id
LEFT JOIN UBICACION u ON a.act_ubicacion_actual = u.ubi_id
LEFT JOIN USUARIO usr ON a.creado_por = usr.usu_id
WHERE ta.activo = TRUE AND u.activo = TRUE;

-- Vista de tickets con información completa
CREATE OR REPLACE VIEW vista_tickets_completa AS
SELECT 
    t.tick_id,
    t.act_id,
    a.act_numero_activo,
    t.tick_tipo,
    t.tick_prioridad,
    t.tick_estado,
    t.tick_descripcion,
    t.tick_solucion,
    t.tick_costo,
    t.tick_fecha_estimada,
    uc.usu_nombre as creado_por_nombre,
    ua.usu_nombre as asignado_a_nombre,
    t.creado_en,
    t.actualizado_en
FROM TICKET t
LEFT JOIN ACTIVO a ON t.act_id = a.act_id
LEFT JOIN USUARIO uc ON t.creado_por = uc.usu_id
LEFT JOIN USUARIO ua ON t.asignado_a = ua.usu_id;

-- =====================================================
-- MOSTRAR RESULTADOS
-- =====================================================

-- Mostrar usuarios creados
SELECT 'USUARIOS CREADOS:' as RESULTADO;
SELECT usu_id, usu_nombre, usu_usuario, usu_rol, usu_email FROM USUARIO;

-- Mostrar activos creados
SELECT 'ACTIVOS CREADOS:' as RESULTADO;
SELECT act_id, act_numero_activo, act_marca, act_modelo, act_estado FROM ACTIVO LIMIT 5;

-- Mostrar estadísticas
SELECT 'ESTADÍSTICAS GENERALES:' as RESULTADO;
SELECT 
    (SELECT COUNT(*) FROM USUARIO WHERE activo = TRUE) as total_usuarios,
    (SELECT COUNT(*) FROM ACTIVO) as total_activos,
    (SELECT COUNT(*) FROM ACTIVO WHERE act_estado = 'Operativo') as activos_operativos,
    (SELECT COUNT(*) FROM TICKET WHERE tick_estado != 'Cerrado') as tickets_abiertos;

SELECT '¡CONFIGURACIÓN COMPLETADA EXITOSAMENTE!' as RESULTADO;
