-- =====================================================================
-- SISTEMA DE CONFIGURACIÓN - COOPERATIVA YPACARAÍ LTDA
-- Script de creación de tablas para el módulo de configuración
-- =====================================================================

-- Crear tabla para configuraciones generales del sistema
CREATE TABLE IF NOT EXISTS configuracion_sistema (
    conf_id                 INT AUTO_INCREMENT PRIMARY KEY,
    conf_clave              VARCHAR(100) NOT NULL UNIQUE,
    conf_valor              TEXT,
    conf_descripcion        VARCHAR(255),
    conf_tipo               ENUM('TEXTO','NUMERO','BOOLEAN','TIEMPO','COLOR','EMAIL','JSON') NOT NULL DEFAULT 'TEXTO',
    conf_categoria          ENUM('GENERAL','MANTENIMIENTO','ALERTAS','EMAIL','HORARIOS','NOTIFICACIONES','SEGURIDAD','REPORTES') NOT NULL DEFAULT 'GENERAL',
    conf_valor_defecto      TEXT,
    conf_obligatoria        BOOLEAN NOT NULL DEFAULT FALSE,
    conf_activa             BOOLEAN NOT NULL DEFAULT TRUE,
    conf_validacion         VARCHAR(255) COMMENT 'Regex o regla de validación',
    conf_opciones           TEXT COMMENT 'JSON con opciones disponibles para el campo',
    creado_en               DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    actualizado_en          DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX ix_conf_categoria (conf_categoria),
    INDEX ix_conf_activa (conf_activa),
    INDEX ix_conf_clave (conf_clave)
);

-- Crear tabla para configuraciones específicas de alertas
CREATE TABLE IF NOT EXISTS configuracion_alertas (
    alerta_config_id        INT AUTO_INCREMENT PRIMARY KEY,
    tipo_alerta             ENUM('MANTENIMIENTO_PREVENTIVO','MANTENIMIENTO_CORRECTIVO','TRASLADO_VENCIDO','ACTIVO_FUERA_SERVICIO','TICKET_VENCIDO','SISTEMA_GENERAL') NOT NULL,
    activa                  BOOLEAN NOT NULL DEFAULT TRUE,
    dias_anticipacion       INT NOT NULL DEFAULT 7,
    frecuencia_revision     ENUM('DIARIA','SEMANAL','CADA_3_DIAS','CADA_2_HORAS','PERSONALIZADA') NOT NULL DEFAULT 'DIARIA',
    intervalo_periodo_minutos INT NULL COMMENT 'Para frecuencia personalizada en minutos',
    prioridad_por_defecto   ENUM('BAJA','MEDIA','ALTA','CRITICA') NOT NULL DEFAULT 'MEDIA',
    color_indicador         VARCHAR(7) COMMENT 'Color hexadecimal para indicadores visuales',
    sonido_habilitado       BOOLEAN NOT NULL DEFAULT FALSE,
    archivo_sonido          VARCHAR(255) COMMENT 'Ruta al archivo de sonido personalizado',
    mensaje_personalizado   TEXT COMMENT 'Mensaje personalizado para este tipo de alerta',
    enviar_email            BOOLEAN NOT NULL DEFAULT TRUE,
    destinatarios_email     TEXT COMMENT 'Lista de emails separados por comas',
    plantilla_email         TEXT COMMENT 'Plantilla HTML para el email',
    mostrar_en_dashboard    BOOLEAN NOT NULL DEFAULT TRUE,
    habilitar_notificacion_push BOOLEAN NOT NULL DEFAULT FALSE,
    creado_en               DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    actualizado_en          DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    UNIQUE KEY uk_tipo_alerta (tipo_alerta),
    INDEX ix_alerta_activa (activa)
);

-- Crear tabla para configuraciones de horarios laborales detallados
CREATE TABLE IF NOT EXISTS configuracion_horarios (
    horario_id              INT AUTO_INCREMENT PRIMARY KEY,
    dia_semana              ENUM('LUNES','MARTES','MIERCOLES','JUEVES','VIERNES','SABADO','DOMINGO') NOT NULL,
    es_laboral              BOOLEAN NOT NULL DEFAULT TRUE,
    hora_inicio             TIME NOT NULL DEFAULT '08:00:00',
    hora_fin                TIME NOT NULL DEFAULT '17:00:00',
    hora_almuerzo_inicio    TIME DEFAULT '12:00:00',
    hora_almuerzo_fin       TIME DEFAULT '13:00:00',
    activo                  BOOLEAN NOT NULL DEFAULT TRUE,
    creado_en               DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    actualizado_en          DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    UNIQUE KEY uk_dia_semana (dia_semana)
);

-- Crear tabla para configuraciones específicas por tipo de activo
CREATE TABLE IF NOT EXISTS configuracion_mantenimiento_tipos (
    config_mant_id          INT AUTO_INCREMENT PRIMARY KEY,
    tip_act_id              INT NOT NULL,
    periodicidad_dias       INT NOT NULL DEFAULT 90,
    dias_anticipacion       INT NOT NULL DEFAULT 7,
    requiere_aprobacion     BOOLEAN NOT NULL DEFAULT FALSE,
    costo_estimado_base     DECIMAL(10,2),
    tiempo_estimado_horas   INT DEFAULT 2,
    checklist_mantenimiento TEXT COMMENT 'JSON con checklist específico',
    instrucciones_especiales TEXT,
    tecnico_especialista_id INT NULL COMMENT 'Técnico especializado para este tipo',
    activo                  BOOLEAN NOT NULL DEFAULT TRUE,
    creado_en               DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    actualizado_en          DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (tip_act_id) REFERENCES TIPO_ACTIVO(tip_act_id) ON DELETE CASCADE,
    FOREIGN KEY (tecnico_especialista_id) REFERENCES USUARIO(usu_id) ON DELETE SET NULL,
    UNIQUE KEY uk_tipo_activo (tip_act_id)
);

-- Crear tabla para configuraciones de ubicaciones
CREATE TABLE IF NOT EXISTS configuracion_ubicaciones (
    config_ubi_id           INT AUTO_INCREMENT PRIMARY KEY,
    ubi_id                  INT NOT NULL,
    requiere_autorizacion   BOOLEAN NOT NULL DEFAULT FALSE,
    responsable_ubicacion_id INT NULL,
    capacidad_maxima        INT,
    tipo_ubicacion          ENUM('OFICINA','SERVIDOR','DEPOSITO','TALLER','EXTERNA') NOT NULL DEFAULT 'OFICINA',
    nivel_seguridad         ENUM('BASICO','MEDIO','ALTO','CRITICO') NOT NULL DEFAULT 'BASICO',
    horario_acceso_inicio   TIME DEFAULT '08:00:00',
    horario_acceso_fin      TIME DEFAULT '18:00:00',
    observaciones           TEXT,
    activo                  BOOLEAN NOT NULL DEFAULT TRUE,
    creado_en               DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    actualizado_en          DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (ubi_id) REFERENCES UBICACION(ubi_id) ON DELETE CASCADE,
    FOREIGN KEY (responsable_ubicacion_id) REFERENCES USUARIO(usu_id) ON DELETE SET NULL,
    UNIQUE KEY uk_ubicacion (ubi_id)
);

-- Crear tabla para log de cambios de configuración
CREATE TABLE IF NOT EXISTS log_cambios_configuracion (
    log_id                  INT AUTO_INCREMENT PRIMARY KEY,
    tipo_configuracion      ENUM('SISTEMA','ALERTA','HORARIO','MANTENIMIENTO','UBICACION') NOT NULL,
    referencia_id           INT NOT NULL,
    campo_modificado        VARCHAR(100) NOT NULL,
    valor_anterior          TEXT,
    valor_nuevo             TEXT,
    usuario_id              INT NOT NULL,
    fecha_cambio            DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ip_origen               VARCHAR(45),
    observaciones           TEXT,
    
    FOREIGN KEY (usuario_id) REFERENCES USUARIO(usu_id) ON DELETE CASCADE,
    INDEX ix_fecha_cambio (fecha_cambio),
    INDEX ix_tipo_config (tipo_configuracion),
    INDEX ix_usuario (usuario_id)
);

-- =====================================================================
-- INSERTAR CONFIGURACIONES POR DEFECTO
-- =====================================================================

-- Insertar configuraciones generales por defecto
INSERT IGNORE INTO configuracion_sistema (conf_clave, conf_valor, conf_descripcion, conf_tipo, conf_categoria, conf_valor_defecto, conf_obligatoria) VALUES
-- Configuraciones Generales
('sistema.nombre', 'Sistema de Gestión de Activos', 'Nombre del sistema', 'TEXTO', 'GENERAL', 'Sistema de Gestión de Activos', TRUE),
('sistema.version', '1.0.0', 'Versión actual del sistema', 'TEXTO', 'GENERAL', '1.0.0', FALSE),
('sistema.organizacion', 'Cooperativa Ypacaraí LTDA', 'Nombre de la organización', 'TEXTO', 'GENERAL', 'Cooperativa Ypacaraí LTDA', TRUE),
('sistema.zona_horaria', 'America/Asuncion', 'Zona horaria del sistema', 'TEXTO', 'GENERAL', 'America/Asuncion', TRUE),
('sistema.idioma', 'es', 'Idioma del sistema', 'TEXTO', 'GENERAL', 'es', TRUE),

-- Configuraciones de Mantenimiento
('mantenimiento.dias_anticipacion_default', '7', 'Días de anticipación por defecto para alertas', 'NUMERO', 'MANTENIMIENTO', '7', TRUE),
('mantenimiento.periodicidad_computadoras', '90', 'Días entre mantenimientos para computadoras', 'NUMERO', 'MANTENIMIENTO', '90', TRUE),
('mantenimiento.periodicidad_impresoras', '30', 'Días entre mantenimientos para impresoras', 'NUMERO', 'MANTENIMIENTO', '30', TRUE),
('mantenimiento.periodicidad_servidores', '60', 'Días entre mantenimientos para servidores', 'NUMERO', 'MANTENIMIENTO', '60', TRUE),
('mantenimiento.periodicidad_ups', '120', 'Días entre mantenimientos para UPS', 'NUMERO', 'MANTENIMIENTO', '120', TRUE),
('mantenimiento.periodicidad_proyectores', '180', 'Días entre mantenimientos para proyectores', 'NUMERO', 'MANTENIMIENTO', '180', TRUE),

-- Configuraciones de Horarios
('horarios.inicio_laboral', '08:00', 'Hora de inicio de jornada laboral', 'TIEMPO', 'HORARIOS', '08:00', TRUE),
('horarios.fin_laboral', '17:00', 'Hora de fin de jornada laboral', 'TIEMPO', 'HORARIOS', '17:00', TRUE),
('horarios.almuerzo_inicio', '12:00', 'Hora de inicio de almuerzo', 'TIEMPO', 'HORARIOS', '12:00', FALSE),
('horarios.almuerzo_fin', '13:00', 'Hora de fin de almuerzo', 'TIEMPO', 'HORARIOS', '13:00', FALSE),
('horarios.sabado_laboral', 'false', 'Si el sábado es día laboral', 'BOOLEAN', 'HORARIOS', 'false', FALSE),

-- Configuraciones de Alertas
('alertas.sonido_habilitado', 'false', 'Habilitar sonidos para alertas críticas', 'BOOLEAN', 'ALERTAS', 'false', FALSE),
('alertas.color_critica', '#dc3545', 'Color para alertas críticas', 'COLOR', 'ALERTAS', '#dc3545', FALSE),
('alertas.color_advertencia', '#ffc107', 'Color para alertas de advertencia', 'COLOR', 'ALERTAS', '#ffc107', FALSE),
('alertas.color_info', '#17a2b8', 'Color para alertas informativas', 'COLOR', 'ALERTAS', '#17a2b8', FALSE),
('alertas.color_exito', '#28a745', 'Color para alertas de éxito', 'COLOR', 'ALERTAS', '#28a745', FALSE),
('alertas.frecuencia_revision', 'diaria', 'Frecuencia de revisión automática', 'TEXTO', 'ALERTAS', 'diaria', TRUE),
('alertas.max_por_dashboard', '10', 'Máximo de alertas visibles en dashboard', 'NUMERO', 'ALERTAS', '10', FALSE),

-- Configuraciones de Email
('email.servidor_smtp', 'mail.ypacarai.coop.py', 'Servidor SMTP', 'TEXTO', 'EMAIL', 'mail.ypacarai.coop.py', TRUE),
('email.puerto_smtp', '587', 'Puerto SMTP', 'NUMERO', 'EMAIL', '587', TRUE),
('email.usuario_sistema', 'sistema.activos@ypacarai.coop.py', 'Usuario de correo del sistema', 'EMAIL', 'EMAIL', 'sistema.activos@ypacarai.coop.py', TRUE),
('email.jefe_informatica', 'jefe.informatica@ypacarai.coop.py', 'Correo del jefe de informática', 'EMAIL', 'EMAIL', 'jefe.informatica@ypacarai.coop.py', TRUE),
('email.usar_ssl', 'true', 'Usar SSL para conexión SMTP', 'BOOLEAN', 'EMAIL', 'true', TRUE),
('email.timeout_segundos', '30', 'Timeout de conexión en segundos', 'NUMERO', 'EMAIL', '30', FALSE),

-- Configuraciones de Seguridad
('seguridad.intentos_login_max', '3', 'Máximo intentos de login fallidos', 'NUMERO', 'SEGURIDAD', '3', TRUE),
('seguridad.tiempo_bloqueo_minutos', '15', 'Tiempo de bloqueo tras intentos fallidos', 'NUMERO', 'SEGURIDAD', '15', TRUE),
('seguridad.caducidad_sesion_horas', '8', 'Horas de inactividad antes de cerrar sesión', 'NUMERO', 'SEGURIDAD', '8', TRUE),
('seguridad.requiere_cambio_password', 'false', 'Requiere cambio de contraseña periódico', 'BOOLEAN', 'SEGURIDAD', 'false', FALSE),

-- Configuraciones de Reportes
('reportes.max_registros_excel', '10000', 'Máximo registros en exportación Excel', 'NUMERO', 'REPORTES', '10000', TRUE),
('reportes.formato_fecha_defecto', 'dd/MM/yyyy', 'Formato de fecha por defecto', 'TEXTO', 'REPORTES', 'dd/MM/yyyy', TRUE),
('reportes.incluir_logo', 'true', 'Incluir logo en reportes PDF', 'BOOLEAN', 'REPORTES', 'true', FALSE),
('reportes.ruta_temporal', 'temp/reportes/', 'Ruta temporal para archivos de reporte', 'TEXTO', 'REPORTES', 'temp/reportes/', TRUE),

-- Configuraciones del SchedulerService (Automatización)
('scheduler.alertas_intervalo_horas', '8', 'Intervalo en horas para proceso automático de alertas', 'NUMERO', 'SCHEDULER', '8', TRUE),
('scheduler.mantenimiento_intervalo_horas', '24', 'Intervalo en horas para mantenimiento preventivo automático', 'NUMERO', 'SCHEDULER', '24', TRUE),
('scheduler.delay_inicial_minutos', '5', 'Minutos de espera antes del primer job automático', 'NUMERO', 'SCHEDULER', '5', TRUE),
('scheduler.auto_inicio', 'true', 'Iniciar scheduler automáticamente al arrancar sistema', 'BOOLEAN', 'SCHEDULER', 'true', TRUE),
('scheduler.max_hilos', '3', 'Número máximo de hilos para pool de ScheduledExecutor', 'NUMERO', 'SCHEDULER', '3', FALSE),

-- Configuraciones del Servidor
('servidor.puerto_app', '8080', 'Puerto de la aplicación principal', 'NUMERO', 'SERVIDOR', '8080', TRUE),
('servidor.ambiente', 'produccion', 'Ambiente de ejecución (desarrollo/test/produccion)', 'TEXTO', 'SERVIDOR', 'produccion', TRUE),
('servidor.log_level', 'INFO', 'Nivel de logging (DEBUG/INFO/WARN/ERROR)', 'TEXTO', 'SERVIDOR', 'INFO', TRUE),
('servidor.backup_automatico', 'true', 'Habilitar backup automático diario', 'BOOLEAN', 'SERVIDOR', 'true', TRUE),
('servidor.backup_hora', '02:00', 'Hora del backup automático diario (HH:mm)', 'TEXTO', 'SERVIDOR', '02:00', TRUE);

-- Insertar configuraciones de alertas por defecto
INSERT IGNORE INTO configuracion_alertas (tipo_alerta, activa, dias_anticipacion, frecuencia_revision, prioridad_por_defecto, color_indicador, sonido_habilitado, mensaje_personalizado, enviar_email, destinatarios_email, plantilla_email) VALUES
('MANTENIMIENTO_PREVENTIVO', TRUE, 7, 'DIARIA', 'MEDIA', '#ffc107', FALSE, 'Mantenimiento preventivo programado en {DIAS} días para {ACTIVO}', TRUE, 'jefe.informatica@ypacarai.coop.py,tecnicos@ypacarai.coop.py', 'Mantenimiento preventivo requerido para {ACTIVO} en {DIAS} días'),
('MANTENIMIENTO_CORRECTIVO', TRUE, 3, 'CADA_2_HORAS', 'ALTA', '#fd7e14', TRUE, 'Mantenimiento correctivo URGENTE requerido para {ACTIVO}', TRUE, 'jefe.informatica@ypacarai.coop.py', 'URGENTE: Mantenimiento correctivo requerido para {ACTIVO}'),
('TRASLADO_VENCIDO', TRUE, 1, 'DIARIA', 'CRITICA', '#dc3545', TRUE, 'Traslado VENCIDO - {ACTIVO} debe ser devuelto inmediatamente', TRUE, 'jefe.informatica@ypacarai.coop.py,gerencia@ypacarai.coop.py', 'CRÍTICO: Traslado vencido - {ACTIVO} debe ser devuelto'),
('ACTIVO_FUERA_SERVICIO', TRUE, 0, 'CADA_2_HORAS', 'ALTA', '#dc3545', FALSE, 'Activo {ACTIVO} fuera de servicio requiere atención inmediata', TRUE, 'jefe.informatica@ypacarai.coop.py', 'Activo fuera de servicio: {ACTIVO} requiere atención'),
('TICKET_VENCIDO', TRUE, 1, 'DIARIA', 'ALTA', '#fd7e14', FALSE, 'Ticket #{TICKET} VENCIDO sin resolver para {ACTIVO}', TRUE, 'jefe.informatica@ypacarai.coop.py', 'Ticket vencido sin resolver: {TICKET} para {ACTIVO}'),
('SISTEMA_GENERAL', TRUE, 0, 'DIARIA', 'BAJA', '#17a2b8', FALSE, 'Notificación del sistema: {MENSAJE}', FALSE, 'jefe.informatica@ypacarai.coop.py', 'Notificación del sistema: {MENSAJE}');

-- Insertar configuración de horarios laborales por defecto
INSERT IGNORE INTO configuracion_horarios (dia_semana, es_laboral, hora_inicio, hora_fin) VALUES
('LUNES', TRUE, '08:00:00', '17:00:00'),
('MARTES', TRUE, '08:00:00', '17:00:00'),
('MIERCOLES', TRUE, '08:00:00', '17:00:00'),
('JUEVES', TRUE, '08:00:00', '17:00:00'),
('VIERNES', TRUE, '08:00:00', '17:00:00'),
('SABADO', FALSE, '08:00:00', '12:00:00'),
('DOMINGO', FALSE, '08:00:00', '12:00:00');

-- =====================================================================
-- PROCEDIMIENTOS PARA GESTIÓN DE CONFIGURACIÓN
-- =====================================================================

DELIMITER //

-- Procedimiento para obtener valor de configuración con fallback a defecto
CREATE FUNCTION obtener_config_valor(clave_config VARCHAR(100))
RETURNS TEXT
READS SQL DATA
DETERMINISTIC
BEGIN
    DECLARE valor_resultado TEXT DEFAULT NULL;
    
    SELECT conf_valor INTO valor_resultado
    FROM configuracion_sistema 
    WHERE conf_clave = clave_config 
    AND conf_activa = TRUE;
    
    -- Si no se encontró, devolver valor por defecto
    IF valor_resultado IS NULL THEN
        SELECT conf_valor_defecto INTO valor_resultado
        FROM configuracion_sistema 
        WHERE conf_clave = clave_config;
    END IF;
    
    RETURN valor_resultado;
END;
//

-- Procedimiento para registrar cambio de configuración
CREATE PROCEDURE registrar_cambio_configuracion(
    IN tipo_config ENUM('SISTEMA','ALERTA','HORARIO','MANTENIMIENTO','UBICACION'),
    IN ref_id INT,
    IN campo VARCHAR(100),
    IN valor_ant TEXT,
    IN valor_nvo TEXT,
    IN user_id INT,
    IN ip_addr VARCHAR(45),
    IN obs TEXT
)
BEGIN
    INSERT INTO log_cambios_configuracion (
        tipo_configuracion, referencia_id, campo_modificado, 
        valor_anterior, valor_nuevo, usuario_id, ip_origen, observaciones
    ) VALUES (
        tipo_config, ref_id, campo, valor_ant, valor_nvo, user_id, ip_addr, obs
    );
END;
//

DELIMITER ;

-- =====================================================================
-- ÍNDICES ADICIONALES PARA OPTIMIZACIÓN
-- =====================================================================

-- Crear índices compuestos para consultas frecuentes
CREATE INDEX ix_config_categoria_activa ON configuracion_sistema(conf_categoria, conf_activa);
CREATE INDEX ix_alertas_tipo_activa ON configuracion_alertas(tipo_alerta, activa);
CREATE INDEX ix_horarios_laboral ON configuracion_horarios(es_laboral, activo);

-- =====================================================================
-- COMENTARIOS Y DOCUMENTACIÓN
-- =====================================================================

ALTER TABLE configuracion_sistema 
COMMENT = 'Configuraciones generales del sistema organizadas por categorías';

ALTER TABLE configuracion_alertas 
COMMENT = 'Configuraciones específicas para cada tipo de alerta del sistema';

ALTER TABLE configuracion_horarios 
COMMENT = 'Configuración detallada de horarios laborales por día de la semana';

ALTER TABLE configuracion_mantenimiento_tipos 
COMMENT = 'Configuraciones específicas de mantenimiento por tipo de activo';

ALTER TABLE configuracion_ubicaciones 
COMMENT = 'Configuraciones específicas por ubicación del sistema';

ALTER TABLE log_cambios_configuracion 
COMMENT = 'Registro de auditoría para cambios en configuraciones del sistema';

-- =====================================================================
-- VERIFICAR CREACIÓN DE TABLAS
-- =====================================================================

SELECT 
    'Configuración del Sistema creada exitosamente' as RESULTADO,
    COUNT(*) as total_configuraciones_insertadas
FROM configuracion_sistema;

SELECT 
    'Configuraciones de Alertas creadas exitosamente' as RESULTADO,
    COUNT(*) as total_alertas_configuradas  
FROM configuracion_alertas;

SELECT 
    'Configuraciones de Horarios creadas exitosamente' as RESULTADO,
    COUNT(*) as total_horarios_configurados
FROM configuracion_horarios;
