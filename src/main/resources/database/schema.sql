-- =====================================================================
--  SISTEMA INTELIGENTE DE GESTIÓN DE ACTIVOS MEDIANTE TICKETS AUTOMATIZADOS
--  COOPERATIVA YPACARAÍ LTDA - CASA CENTRAL
--  Año 2025
-- =====================================================================

DROP DATABASE IF EXISTS sistema_activos_ypacarai;
CREATE DATABASE sistema_activos_ypacarai;
USE sistema_activos_ypacarai;

-- =====================================================================
--  1) GESTIÓN DE USUARIOS Y ROLES
-- =====================================================================

CREATE TABLE USUARIO (
    usu_id          INT AUTO_INCREMENT PRIMARY KEY,
    usu_nombre      VARCHAR(120) NOT NULL,
    usu_usuario     VARCHAR(60) NOT NULL UNIQUE,
    usu_password    VARCHAR(200) NOT NULL,  -- hash bcrypt
    usu_rol         ENUM('Jefe_Informatica','Tecnico','Consulta') NOT NULL DEFAULT 'Tecnico',
    usu_email       VARCHAR(150),           -- para notificaciones Zimbra
    activo          TINYINT(1) NOT NULL DEFAULT 1,
    creado_en       DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP(),
    actualizado_en  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP() ON UPDATE CURRENT_TIMESTAMP()
);

-- Usuario inicial
INSERT INTO USUARIO (usu_nombre, usu_usuario, usu_password, usu_rol, usu_email)
VALUES 
('Jefe de Informática','jefe_info','test1','Jefe_Informatica','jefe.informatica@ypacarai.coop.py'),
('Técnico Principal','tecnico1','test2','Tecnico','tecnico1@ypacarai.coop.py');

-- =====================================================================
--  2) TIPOS DE ACTIVOS (Solo PC e Impresoras según protocolo)
-- =====================================================================

CREATE TABLE TIPO_ACTIVO (
    tip_act_id      INT AUTO_INCREMENT PRIMARY KEY,
    nombre          VARCHAR(50) NOT NULL UNIQUE,
    descripcion     VARCHAR(255),
    activo          TINYINT(1) NOT NULL DEFAULT 1
);

INSERT INTO TIPO_ACTIVO (nombre, descripcion) VALUES 
('PC','Computadoras de escritorio'),
('Impresora','Impresoras de todo tipo');

-- =====================================================================
--  3) UBICACIONES (Casa Central y Sucursales)
-- =====================================================================

CREATE TABLE UBICACION (
    ubi_id          INT AUTO_INCREMENT PRIMARY KEY,
    ubi_codigo      VARCHAR(20) NOT NULL UNIQUE,
    ubi_nombre      VARCHAR(120) NOT NULL,
    ubi_tipo        ENUM('Casa_Central','Sucursal') NOT NULL,
    ubi_direccion   VARCHAR(255),
    ubi_telefono    VARCHAR(50),
    activo          TINYINT(1) NOT NULL DEFAULT 1,
    creado_en       DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP()
);

-- Ubicaciones iniciales
INSERT INTO UBICACION (ubi_codigo, ubi_nombre, ubi_tipo, ubi_direccion) VALUES
('CC-001','Casa Central - Administración','Casa_Central','Ypacaraí Centro'),
('CC-002','Casa Central - Contabilidad','Casa_Central','Ypacaraí Centro'),
('CC-003','Casa Central - Informática','Casa_Central','Ypacaraí Centro'),
('CC-004','Casa Central - Atención al Socio','Casa_Central','Ypacaraí Centro'),
('SUC-001','Sucursal San Lorenzo','Sucursal','San Lorenzo, Central'),
('SUC-002','Sucursal Capiatá','Sucursal','Capiatá, Central');

-- =====================================================================
--  4) GESTIÓN DE ACTIVOS (PC e Impresoras)
-- =====================================================================

CREATE TABLE ACTIVO (
    act_id                  INT AUTO_INCREMENT PRIMARY KEY,
    act_numero_activo       VARCHAR(50) NOT NULL UNIQUE,  -- Identificador único del activo
    tip_act_id              INT NOT NULL,
    act_marca               VARCHAR(60),
    act_modelo              VARCHAR(60),
    act_numero_serie        VARCHAR(60),
    act_especificaciones    TEXT,                        -- Detalles técnicos
    act_fecha_adquisicion   DATE,
    act_estado              ENUM('Operativo','En_Mantenimiento','Fuera_Servicio','Trasladado') NOT NULL DEFAULT 'Operativo',
    act_ubicacion_actual    INT NOT NULL,
    act_responsable_actual  VARCHAR(120),                -- Persona a cargo del activo
    act_observaciones       TEXT,
    creado_por              INT NOT NULL,
    creado_en               DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP(),
    actualizado_en          DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP() ON UPDATE CURRENT_TIMESTAMP(),
    
    CONSTRAINT fk_activo_tipo       FOREIGN KEY (tip_act_id) REFERENCES TIPO_ACTIVO(tip_act_id),
    CONSTRAINT fk_activo_ubicacion  FOREIGN KEY (act_ubicacion_actual) REFERENCES UBICACION(ubi_id),
    CONSTRAINT fk_activo_creador    FOREIGN KEY (creado_por) REFERENCES USUARIO(usu_id)
);

CREATE INDEX ix_activo_ubicacion ON ACTIVO(act_ubicacion_actual);
CREATE INDEX ix_activo_estado ON ACTIVO(act_estado);
CREATE INDEX ix_activo_numero ON ACTIVO(act_numero_activo);

-- =====================================================================
--  5) SISTEMA DE TICKETS AUTOMATIZADOS
-- =====================================================================

CREATE TABLE TICKET (
    tick_id                 INT AUTO_INCREMENT PRIMARY KEY,
    act_id                  INT NOT NULL,
    tick_numero             VARCHAR(50) NOT NULL UNIQUE,   -- TKT-YYYY-NNNN
    tick_tipo               ENUM('Preventivo','Correctivo') NOT NULL,
    tick_prioridad          ENUM('Baja','Media','Alta','Critica') NOT NULL DEFAULT 'Media',
    tick_titulo             VARCHAR(200) NOT NULL,
    tick_descripcion        TEXT,
    tick_estado             ENUM('Abierto','En_Proceso','Resuelto','Cerrado','Cancelado') NOT NULL DEFAULT 'Abierto',
    tick_fecha_apertura     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP(),
    tick_fecha_vencimiento  DATETIME NULL,                 -- Para mantenimientos preventivos
    tick_fecha_cierre       DATETIME NULL,
    tick_asignado_a         INT NULL,                      -- Técnico asignado
    tick_reportado_por      INT NOT NULL,                  -- Usuario que reporta
    tick_solucion           TEXT,                          -- Descripción de la solución aplicada
    tick_tiempo_resolucion  INT NULL,                      -- Minutos transcurridos
    tick_notificacion_enviada TINYINT(1) NOT NULL DEFAULT 0,
    creado_en               DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP(),
    actualizado_en          DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP() ON UPDATE CURRENT_TIMESTAMP(),
    
    CONSTRAINT fk_ticket_activo         FOREIGN KEY (act_id) REFERENCES ACTIVO(act_id) ON DELETE CASCADE,
    CONSTRAINT fk_ticket_asignado       FOREIGN KEY (tick_asignado_a) REFERENCES USUARIO(usu_id),
    CONSTRAINT fk_ticket_reportado      FOREIGN KEY (tick_reportado_por) REFERENCES USUARIO(usu_id)
);

CREATE INDEX ix_ticket_estado ON TICKET(tick_estado);
CREATE INDEX ix_ticket_tipo ON TICKET(tick_tipo);
CREATE INDEX ix_ticket_vencimiento ON TICKET(tick_fecha_vencimiento);
CREATE INDEX ix_ticket_asignado ON TICKET(tick_asignado_a);

-- Función para generar número de ticket automático
DELIMITER //
CREATE TRIGGER trg_ticket_numero
BEFORE INSERT ON TICKET
FOR EACH ROW
BEGIN
    DECLARE ultimo_numero INT DEFAULT 0;
    DECLARE nuevo_numero VARCHAR(50);
    DECLARE anio_actual VARCHAR(4);
    
    SET anio_actual = YEAR(CURRENT_DATE);
    
    SELECT COALESCE(MAX(CAST(SUBSTRING(tick_numero, -4) AS UNSIGNED)), 0) + 1 
    INTO ultimo_numero
    FROM TICKET 
    WHERE tick_numero LIKE CONCAT('TKT-', anio_actual, '-%');
    
    SET nuevo_numero = CONCAT('TKT-', anio_actual, '-', LPAD(ultimo_numero, 4, '0'));
    SET NEW.tick_numero = nuevo_numero;
END;
//
DELIMITER ;

-- =====================================================================
--  6) PLANIFICACIÓN DE MANTENIMIENTOS PREVENTIVOS
-- =====================================================================

CREATE TABLE PLAN_MANTENIMIENTO (
    plan_id                 INT AUTO_INCREMENT PRIMARY KEY,
    tip_act_id              INT NOT NULL,                  -- Tipo de activo (PC/Impresora)
    plan_nombre             VARCHAR(120) NOT NULL,
    plan_descripcion        TEXT,
    plan_frecuencia_dias    INT NOT NULL DEFAULT 90,      -- Cada cuántos días
    plan_dias_alerta        INT NOT NULL DEFAULT 7,       -- Días antes para generar alerta
    plan_activo             TINYINT(1) NOT NULL DEFAULT 1,
    plan_procedimiento      TEXT,                          -- Pasos del mantenimiento
    creado_por              INT NOT NULL,
    creado_en               DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP(),
    actualizado_en          DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP() ON UPDATE CURRENT_TIMESTAMP(),
    
    CONSTRAINT fk_plan_tipo     FOREIGN KEY (tip_act_id) REFERENCES TIPO_ACTIVO(tip_act_id),
    CONSTRAINT fk_plan_creador  FOREIGN KEY (creado_por) REFERENCES USUARIO(usu_id)
);

-- Planes de mantenimiento por defecto
INSERT INTO PLAN_MANTENIMIENTO (tip_act_id, plan_nombre, plan_descripcion, plan_frecuencia_dias, plan_dias_alerta, plan_procedimiento, creado_por) VALUES
(1, 'Mantenimiento PC Estándar', 'Limpieza interna, actualización de software, verificación de componentes', 90, 7, 
'1. Apagar equipo y desconectar\n2. Limpieza externa e interna\n3. Verificar conexiones\n4. Actualizar sistema operativo\n5. Verificar antivirus\n6. Pruebas de funcionamiento', 1),
(2, 'Mantenimiento Impresora', 'Limpieza de cabezales, verificación de consumibles, calibración', 60, 5,
'1. Limpieza de cabezales\n2. Verificar nivel de tinta/toner\n3. Limpieza de rodillos\n4. Calibración de impresión\n5. Prueba de impresión', 1);

-- =====================================================================
--  7) REGISTRO DE MANTENIMIENTOS
-- =====================================================================

CREATE TABLE MANTENIMIENTO (
    mant_id                 INT AUTO_INCREMENT PRIMARY KEY,
    tick_id                 INT NOT NULL,                  -- Ticket asociado
    act_id                  INT NOT NULL,                  -- Activo
    plan_id                 INT NULL,                      -- Plan de mantenimiento (si es preventivo)
    mant_fecha_inicio       DATETIME NOT NULL,
    mant_fecha_fin          DATETIME NULL,
    mant_tipo               ENUM('Preventivo','Correctivo') NOT NULL,
    mant_descripcion_inicial TEXT,                         -- Problema reportado
    mant_diagnostico        TEXT,                          -- Diagnóstico técnico
    mant_procedimiento      TEXT,                          -- Pasos realizados
    mant_resultado          TEXT,                          -- Resultado obtenido
    mant_proxima_fecha      DATE NULL,                     -- Próximo mantenimiento preventivo
    mant_tecnico_asignado   INT NOT NULL,
    mant_estado             ENUM('Programado','En_Proceso','Completado','Suspendido') NOT NULL DEFAULT 'Programado',
    mant_observaciones      TEXT,
    creado_en               DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP(),
    actualizado_en          DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP() ON UPDATE CURRENT_TIMESTAMP(),
    
    CONSTRAINT fk_mant_ticket   FOREIGN KEY (tick_id) REFERENCES TICKET(tick_id) ON DELETE CASCADE,
    CONSTRAINT fk_mant_activo   FOREIGN KEY (act_id) REFERENCES ACTIVO(act_id),
    CONSTRAINT fk_mant_plan     FOREIGN KEY (plan_id) REFERENCES PLAN_MANTENIMIENTO(plan_id),
    CONSTRAINT fk_mant_tecnico  FOREIGN KEY (mant_tecnico_asignado) REFERENCES USUARIO(usu_id)
);

CREATE INDEX ix_mant_activo ON MANTENIMIENTO(act_id);
CREATE INDEX ix_mant_proxima ON MANTENIMIENTO(mant_proxima_fecha);
CREATE INDEX ix_mant_tecnico ON MANTENIMIENTO(mant_tecnico_asignado);

-- =====================================================================
--  8) FICHAS DE REPORTE PARA MANTENIMIENTOS CORRECTIVOS
-- =====================================================================

CREATE TABLE FICHA_REPORTE (
    ficha_id                INT AUTO_INCREMENT PRIMARY KEY,
    mant_id                 INT NOT NULL,
    ficha_numero            VARCHAR(50) NOT NULL UNIQUE,   -- FR-YYYY-NNNN
    ficha_fecha             DATE NOT NULL,
    ficha_problema_reportado TEXT NOT NULL,
    ficha_diagnostico       TEXT,
    ficha_solucion_aplicada TEXT,
    ficha_componentes_cambio TEXT,                         -- Componentes reemplazados
    ficha_tiempo_estimado   INT,                           -- Minutos
    ficha_tiempo_real       INT,                           -- Minutos reales
    ficha_observaciones     TEXT,
    ficha_tecnico_firma     VARCHAR(120),                  -- Nombre del técnico
    ficha_usuario_firma     VARCHAR(120),                  -- Usuario que reportó
    ficha_estado            ENUM('Borrador','Enviada','Archivada') NOT NULL DEFAULT 'Borrador',
    creado_por              INT NOT NULL,
    creado_en               DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP(),
    actualizado_en          DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP() ON UPDATE CURRENT_TIMESTAMP(),
    
    CONSTRAINT fk_ficha_mant    FOREIGN KEY (mant_id) REFERENCES MANTENIMIENTO(mant_id) ON DELETE CASCADE,
    CONSTRAINT fk_ficha_creador FOREIGN KEY (creado_por) REFERENCES USUARIO(usu_id)
);

-- Generar número automático para fichas de reporte
DELIMITER //
CREATE TRIGGER trg_ficha_numero
BEFORE INSERT ON FICHA_REPORTE
FOR EACH ROW
BEGIN
    DECLARE ultimo_numero INT DEFAULT 0;
    DECLARE nuevo_numero VARCHAR(50);
    DECLARE anio_actual VARCHAR(4);
    
    SET anio_actual = YEAR(CURRENT_DATE);
    
    SELECT COALESCE(MAX(CAST(SUBSTRING(ficha_numero, -4) AS UNSIGNED)), 0) + 1 
    INTO ultimo_numero
    FROM FICHA_REPORTE 
    WHERE ficha_numero LIKE CONCAT('FR-', anio_actual, '-%');
    
    SET nuevo_numero = CONCAT('FR-', anio_actual, '-', LPAD(ultimo_numero, 4, '0'));
    SET NEW.ficha_numero = nuevo_numero;
END;
//
DELIMITER ;

-- =====================================================================
--  9) TRASLADOS ENTRE CASA CENTRAL Y SUCURSALES
-- =====================================================================

CREATE TABLE TRASLADO (
    tras_id                 INT AUTO_INCREMENT PRIMARY KEY,
    act_id                  INT NOT NULL,
    tras_numero             VARCHAR(50) NOT NULL UNIQUE,   -- TR-YYYY-NNNN
    tras_fecha_salida       DATETIME NOT NULL,
    tras_fecha_retorno      DATETIME NULL,
    tras_ubicacion_origen   INT NOT NULL,
    tras_ubicacion_destino  INT NOT NULL,
    tras_motivo             VARCHAR(255) NOT NULL,
    tras_estado             ENUM('Programado','En_Transito','Entregado','Devuelto') NOT NULL DEFAULT 'Programado',
    tras_responsable_envio  VARCHAR(120),                  -- Persona que entrega
    tras_responsable_recibo VARCHAR(120),                  -- Persona que recibe
    tras_observaciones      TEXT,
    tras_fecha_devolucion_prog DATE NULL,                 -- Fecha programada de devolución
    autorizado_por          INT NOT NULL,                  -- Usuario que autoriza
    creado_por              INT NOT NULL,
    creado_en               DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP(),
    actualizado_en          DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP() ON UPDATE CURRENT_TIMESTAMP(),
    
    CONSTRAINT fk_tras_activo       FOREIGN KEY (act_id) REFERENCES ACTIVO(act_id),
    CONSTRAINT fk_tras_origen       FOREIGN KEY (tras_ubicacion_origen) REFERENCES UBICACION(ubi_id),
    CONSTRAINT fk_tras_destino      FOREIGN KEY (tras_ubicacion_destino) REFERENCES UBICACION(ubi_id),
    CONSTRAINT fk_tras_autorizado   FOREIGN KEY (autorizado_por) REFERENCES USUARIO(usu_id),
    CONSTRAINT fk_tras_creador      FOREIGN KEY (creado_por) REFERENCES USUARIO(usu_id)
);

CREATE INDEX ix_tras_activo ON TRASLADO(act_id);
CREATE INDEX ix_tras_estado ON TRASLADO(tras_estado);
CREATE INDEX ix_tras_fecha_salida ON TRASLADO(tras_fecha_salida);

-- Generar número automático para traslados
DELIMITER //
CREATE TRIGGER trg_traslado_numero
BEFORE INSERT ON TRASLADO
FOR EACH ROW
BEGIN
    DECLARE ultimo_numero INT DEFAULT 0;
    DECLARE nuevo_numero VARCHAR(50);
    DECLARE anio_actual VARCHAR(4);
    
    SET anio_actual = YEAR(CURRENT_DATE);
    
    SELECT COALESCE(MAX(CAST(SUBSTRING(tras_numero, -4) AS UNSIGNED)), 0) + 1 
    INTO ultimo_numero
    FROM TRASLADO 
    WHERE tras_numero LIKE CONCAT('TR-', anio_actual, '-%');
    
    SET nuevo_numero = CONCAT('TR-', anio_actual, '-', LPAD(ultimo_numero, 4, '0'));
    SET NEW.tras_numero = nuevo_numero;
END;
//

-- Actualizar ubicación del activo cuando se confirma la entrega del traslado
CREATE TRIGGER trg_traslado_actualizar_ubicacion
AFTER UPDATE ON TRASLADO
FOR EACH ROW
BEGIN
    IF NEW.tras_estado = 'Entregado' AND OLD.tras_estado != 'Entregado' THEN
        UPDATE ACTIVO 
        SET act_ubicacion_actual = NEW.tras_ubicacion_destino,
            act_estado = 'Trasladado'
        WHERE act_id = NEW.act_id;
    END IF;
    
    IF NEW.tras_estado = 'Devuelto' AND OLD.tras_estado != 'Devuelto' THEN
        UPDATE ACTIVO 
        SET act_ubicacion_actual = NEW.tras_ubicacion_origen,
            act_estado = 'Operativo'
        WHERE act_id = NEW.act_id;
    END IF;
END;
//
DELIMITER ;

-- =====================================================================
--  10) SISTEMA DE ALERTAS AUTOMÁTICAS
-- =====================================================================

CREATE TABLE ALERTA (
    ale_id                  INT AUTO_INCREMENT PRIMARY KEY,
    act_id                  INT NOT NULL,
    ale_tipo                ENUM('Mantenimiento_Proximo','Mantenimiento_Vencido','Traslado_Vencido') NOT NULL,
    ale_titulo              VARCHAR(200) NOT NULL,
    ale_mensaje             TEXT NOT NULL,
    ale_fecha_objetivo      DATE NOT NULL,             -- Fecha del mantenimiento/devolución
    ale_fecha_alerta        DATETIME NOT NULL,         -- Cuando se debe mostrar la alerta
    ale_prioridad           ENUM('Info','Advertencia','Critica') NOT NULL DEFAULT 'Info',
    ale_estado              ENUM('Pendiente','Enviada','Atendida','Cancelada') NOT NULL DEFAULT 'Pendiente',
    ale_email_enviado       TINYINT(1) NOT NULL DEFAULT 0,
    ale_fecha_envio         DATETIME NULL,
    referencia_id           INT NULL,                  -- ID del mantenimiento o traslado relacionado
    creado_en               DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP(),
    actualizado_en          DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP() ON UPDATE CURRENT_TIMESTAMP(),
    
    CONSTRAINT fk_ale_activo FOREIGN KEY (act_id) REFERENCES ACTIVO(act_id) ON DELETE CASCADE
);

CREATE INDEX ix_alerta_fecha ON ALERTA(ale_fecha_alerta);
CREATE INDEX ix_alerta_estado ON ALERTA(ale_estado);
CREATE INDEX ix_alerta_activo ON ALERTA(act_id);

-- =====================================================================
--  11) CONFIGURACIÓN DEL SISTEMA ZIMBRA
-- =====================================================================

CREATE TABLE CONFIGURACION_EMAIL (
    conf_id                 INT AUTO_INCREMENT PRIMARY KEY,
    conf_servidor_smtp      VARCHAR(120) NOT NULL DEFAULT 'mail.ypacarai.coop.py',
    conf_puerto_smtp        INT NOT NULL DEFAULT 587,
    conf_usuario_sistema    VARCHAR(120) NOT NULL DEFAULT 'sistema.activos@ypacarai.coop.py',
    conf_password_sistema   VARCHAR(200) NOT NULL,        -- Encriptado
    conf_email_jefe         VARCHAR(120) NOT NULL,        -- Email del jefe de informática
    conf_usa_ssl            TINYINT(1) NOT NULL DEFAULT 1,
    conf_plantilla_alerta   TEXT,                          -- Template HTML para emails
    conf_activo             TINYINT(1) NOT NULL DEFAULT 1,
    actualizado_en          DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP() ON UPDATE CURRENT_TIMESTAMP()
);

-- Configuración inicial para Zimbra
INSERT INTO CONFIGURACION_EMAIL (conf_servidor_smtp, conf_usuario_sistema, conf_password_sistema, conf_email_jefe, conf_plantilla_alerta)
VALUES (
    'mail.ypacarai.coop.py',
    'sistema.activos@ypacarai.coop.py',
    'encrypted_password_here',
    'jefe.informatica@ypacarai.coop.py',
    '<html><body><h2>Alerta del Sistema de Gestión de Activos</h2><p><strong>Activo:</strong> {ACTIVO_NUMERO}</p><p><strong>Mensaje:</strong> {MENSAJE}</p><p><strong>Fecha Objetivo:</strong> {FECHA_OBJETIVO}</p><br><p>Sistema de Gestión de Activos - Cooperativa Ypacaraí LTDA</p></body></html>'
);

-- =====================================================================
--  12) LOG DE NOTIFICACIONES ENVIADAS
-- =====================================================================

CREATE TABLE LOG_NOTIFICACION (
    log_id                  INT AUTO_INCREMENT PRIMARY KEY,
    ale_id                  INT NOT NULL,
    log_tipo                ENUM('Email','Sistema') NOT NULL,
    log_destinatario        VARCHAR(120) NOT NULL,
    log_asunto              VARCHAR(200),
    log_mensaje             TEXT,
    log_estado              ENUM('Enviado','Error','Pendiente') NOT NULL,
    log_error               TEXT NULL,                     -- Descripción del error si falló
    log_fecha_envio         DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP(),
    
    CONSTRAINT fk_log_alerta FOREIGN KEY (ale_id) REFERENCES ALERTA(ale_id) ON DELETE CASCADE
);

-- =====================================================================
--  13) PROCEDIMIENTOS ALMACENADOS PARA AUTOMATIZACIÓN
-- =====================================================================

-- Procedimiento para generar tickets de mantenimiento preventivo
DELIMITER //
CREATE PROCEDURE sp_generar_tickets_preventivos()
BEGIN
    DECLARE done INT DEFAULT FALSE;
    DECLARE v_act_id INT;
    DECLARE v_plan_id INT;
    DECLARE v_plan_nombre VARCHAR(120);
    DECLARE v_fecha_objetivo DATE;
    
    -- Cursor para activos que necesitan mantenimiento preventivo
    DECLARE cur_activos CURSOR FOR
        SELECT DISTINCT 
            a.act_id,
            pm.plan_id,
            pm.plan_nombre,
            DATE_ADD(COALESCE(MAX(m.mant_proxima_fecha), a.creado_en), INTERVAL pm.plan_frecuencia_dias DAY) as fecha_objetivo
        FROM ACTIVO a
        JOIN PLAN_MANTENIMIENTO pm ON pm.tip_act_id = a.tip_act_id
        LEFT JOIN MANTENIMIENTO m ON m.act_id = a.act_id AND m.mant_tipo = 'Preventivo'
        WHERE pm.plan_activo = 1
        AND a.act_estado IN ('Operativo', 'Trasladado')
        GROUP BY a.act_id, pm.plan_id, pm.plan_nombre, pm.plan_frecuencia_dias
        HAVING fecha_objetivo <= DATE_ADD(CURRENT_DATE, INTERVAL pm.plan_dias_alerta DAY)
        AND NOT EXISTS (
            SELECT 1 FROM TICKET t 
            WHERE t.act_id = a.act_id 
            AND t.tick_tipo = 'Preventivo' 
            AND t.tick_estado IN ('Abierto','En_Proceso')
        );
    
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;
    
    OPEN cur_activos;
    
    read_loop: LOOP
        FETCH cur_activos INTO v_act_id, v_plan_id, v_plan_nombre, v_fecha_objetivo;
        IF done THEN
            LEAVE read_loop;
        END IF;
        
        -- Crear ticket de mantenimiento preventivo
        INSERT INTO TICKET (
            act_id, 
            tick_tipo, 
            tick_prioridad, 
            tick_titulo, 
            tick_descripcion,
            tick_fecha_vencimiento,
            tick_reportado_por,
            tick_asignado_a
        )
        SELECT 
            v_act_id,
            'Preventivo',
            'Media',
            CONCAT('Mantenimiento Preventivo - ', v_plan_nombre),
            CONCAT('Mantenimiento preventivo programado según plan: ', v_plan_nombre),
            v_fecha_objetivo,
            1, -- Sistema
            (SELECT usu_id FROM USUARIO WHERE usu_rol = 'Tecnico' AND activo = 1 LIMIT 1);
        
    END LOOP;
    
    CLOSE cur_activos;
END;
//

-- Procedimiento para generar alertas automáticas
CREATE PROCEDURE sp_generar_alertas_automaticas()
BEGIN
    -- Alertas para mantenimientos próximos (7 días antes)
    INSERT INTO ALERTA (act_id, ale_tipo, ale_titulo, ale_mensaje, ale_fecha_objetivo, ale_fecha_alerta, ale_prioridad, referencia_id)
    SELECT 
        t.act_id,
        'Mantenimiento_Proximo',
        CONCAT('Mantenimiento próximo - Activo ', a.act_numero_activo),
        CONCAT('El activo ', a.act_numero_activo, ' (', ta.nombre, ' ', a.act_marca, ' ', a.act_modelo, ') tiene un mantenimiento preventivo programado para el ', DATE_FORMAT(t.tick_fecha_vencimiento, '%d/%m/%Y')),
        DATE(t.tick_fecha_vencimiento),
        DATE_SUB(t.tick_fecha_vencimiento, INTERVAL 7 DAY),
        'Advertencia',
        t.tick_id
    FROM TICKET t
    JOIN ACTIVO a ON a.act_id = t.act_id
    JOIN TIPO_ACTIVO ta ON ta.tip_act_id = a.tip_act_id
    WHERE t.tick_tipo = 'Preventivo'
    AND t.tick_estado IN ('Abierto','En_Proceso')
    AND DATE(t.tick_fecha_vencimiento) < CURRENT_DATE
    AND NOT EXISTS (
        SELECT 1 FROM ALERTA al 
        WHERE al.act_id = t.act_id 
        AND al.ale_tipo = 'Mantenimiento_Vencido' 
        AND al.referencia_id = t.tick_id
        AND al.ale_estado != 'Cancelada'
    );
    
    -- Alertas para traslados con devolución vencida
    INSERT INTO ALERTA (act_id, ale_tipo, ale_titulo, ale_mensaje, ale_fecha_objetivo, ale_fecha_alerta, ale_prioridad, referencia_id)
    SELECT 
        tr.act_id,
        'Traslado_Vencido',
        CONCAT('Devolución vencida - Activo ', a.act_numero_activo),
        CONCAT('El activo ', a.act_numero_activo, ' trasladado a ', ub.ubi_nombre, ' debía ser devuelto el ', DATE_FORMAT(tr.tras_fecha_devolucion_prog, '%d/%m/%Y')),
        tr.tras_fecha_devolucion_prog,
        CURRENT_TIMESTAMP,
        'Advertencia',
        tr.tras_id
    FROM TRASLADO tr
    JOIN ACTIVO a ON a.act_id = tr.act_id
    JOIN UBICACION ub ON ub.ubi_id = tr.tras_ubicacion_destino
    WHERE tr.tras_estado = 'Entregado'
    AND tr.tras_fecha_devolucion_prog < CURRENT_DATE
    AND NOT EXISTS (
        SELECT 1 FROM ALERTA al 
        WHERE al.act_id = tr.act_id 
        AND al.ale_tipo = 'Traslado_Vencido' 
        AND al.referencia_id = tr.tras_id
        AND al.ale_estado != 'Cancelada'
    );
END;
//

-- Procedimiento para envío de alertas por correo (integración con Zimbra)
CREATE PROCEDURE sp_enviar_alertas_email()
BEGIN
    DECLARE done INT DEFAULT FALSE;
    DECLARE v_ale_id INT;
    DECLARE v_titulo VARCHAR(200);
    DECLARE v_mensaje TEXT;
    DECLARE v_activo_numero VARCHAR(50);
    DECLARE v_fecha_objetivo VARCHAR(10);
    DECLARE v_email_jefe VARCHAR(120);
    DECLARE v_plantilla TEXT;
    DECLARE v_mensaje_final TEXT;
    
    -- Cursor para alertas pendientes de envío
    DECLARE cur_alertas CURSOR FOR
        SELECT 
            al.ale_id,
            al.ale_titulo,
            al.ale_mensaje,
            a.act_numero_activo,
            DATE_FORMAT(al.ale_fecha_objetivo, '%d/%m/%Y') as fecha_objetivo
        FROM ALERTA al
        JOIN ACTIVO a ON a.act_id = al.act_id
        WHERE al.ale_estado = 'Pendiente'
        AND al.ale_fecha_alerta <= CURRENT_TIMESTAMP
        AND al.ale_email_enviado = 0;
    
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;
    
    -- Obtener configuración de email
    SELECT conf_email_jefe, conf_plantilla_alerta 
    INTO v_email_jefe, v_plantilla
    FROM CONFIGURACION_EMAIL 
    WHERE conf_activo = 1 
    LIMIT 1;
    
    IF v_email_jefe IS NOT NULL THEN
        OPEN cur_alertas;
        
        read_loop: LOOP
            FETCH cur_alertas INTO v_ale_id, v_titulo, v_mensaje, v_activo_numero, v_fecha_objetivo;
            IF done THEN
                LEAVE read_loop;
            END IF;
            
            -- Preparar mensaje personalizado
            SET v_mensaje_final = REPLACE(v_plantilla, '{ACTIVO_NUMERO}', v_activo_numero);
            SET v_mensaje_final = REPLACE(v_mensaje_final, '{MENSAJE}', v_mensaje);
            SET v_mensaje_final = REPLACE(v_mensaje_final, '{FECHA_OBJETIVO}', v_fecha_objetivo);
            
            -- Registrar en log (aquí se integraría con la API de Zimbra)
            INSERT INTO LOG_NOTIFICACION (ale_id, log_tipo, log_destinatario, log_asunto, log_mensaje, log_estado)
            VALUES (v_ale_id, 'Email', v_email_jefe, v_titulo, v_mensaje_final, 'Pendiente');
            
            -- Marcar alerta como enviada (se actualizará a 'Enviado' cuando Zimbra confirme)
            UPDATE ALERTA 
            SET ale_email_enviado = 1, 
                ale_fecha_envio = CURRENT_TIMESTAMP,
                ale_estado = 'Enviada'
            WHERE ale_id = v_ale_id;
            
        END LOOP;
        
        CLOSE cur_alertas;
    END IF;
END;
//

-- Procedimiento principal para ejecutar tareas automáticas (se debe ejecutar diariamente)
CREATE PROCEDURE sp_ejecutar_tareas_automaticas()
BEGIN
    -- Generar tickets de mantenimiento preventivo
    CALL sp_generar_tickets_preventivos();
    
    -- Generar alertas automáticas
    CALL sp_generar_alertas_automaticas();
    
    -- Enviar alertas por email
    CALL sp_enviar_alertas_email();
    
    -- Actualizar estado de tickets vencidos
    UPDATE TICKET 
    SET tick_estado = 'Vencido'
    WHERE tick_tipo = 'Preventivo'
    AND tick_estado IN ('Abierto','En_Proceso')
    AND tick_fecha_vencimiento < CURRENT_DATE;
END;
//
DELIMITER ;

-- =====================================================================
--  14) VISTAS PARA REPORTES Y CONSULTAS
-- =====================================================================

-- Vista resumen de activos
CREATE OR REPLACE VIEW VW_ACTIVOS_RESUMEN AS
SELECT
    a.act_id,
    a.act_numero_activo,
    ta.nombre AS tipo_activo,
    a.act_marca,
    a.act_modelo,
    a.act_estado,
    u.ubi_nombre AS ubicacion_actual,
    u.ubi_tipo AS tipo_ubicacion,
    a.act_responsable_actual,
    a.creado_en,
    -- Último mantenimiento
    (SELECT MAX(m.mant_fecha_fin) 
     FROM MANTENIMIENTO m 
     WHERE m.act_id = a.act_id 
     AND m.mant_estado = 'Completado') AS ultimo_mantenimiento,
    -- Próximo mantenimiento
    (SELECT MIN(m.mant_proxima_fecha) 
     FROM MANTENIMIENTO m 
     WHERE m.act_id = a.act_id 
     AND m.mant_proxima_fecha > CURRENT_DATE) AS proximo_mantenimiento,
    -- Tickets abiertos
    (SELECT COUNT(*) 
     FROM TICKET t 
     WHERE t.act_id = a.act_id 
     AND t.tick_estado IN ('Abierto','En_Proceso')) AS tickets_abiertos
FROM ACTIVO a
JOIN TIPO_ACTIVO ta ON ta.tip_act_id = a.tip_act_id
LEFT JOIN UBICACION u ON u.ubi_id = a.act_ubicacion_actual;

-- Vista de tickets pendientes
CREATE OR REPLACE VIEW VW_TICKETS_PENDIENTES AS
SELECT
    t.tick_id,
    t.tick_numero,
    t.tick_tipo,
    t.tick_prioridad,
    t.tick_titulo,
    t.tick_estado,
    a.act_numero_activo,
    ta.nombre AS tipo_activo,
    u_asignado.usu_nombre AS tecnico_asignado,
    u_reporta.usu_nombre AS reportado_por,
    t.tick_fecha_apertura,
    t.tick_fecha_vencimiento,
    CASE 
        WHEN t.tick_fecha_vencimiento < CURRENT_DATE THEN 'VENCIDO'
        WHEN t.tick_fecha_vencimiento <= DATE_ADD(CURRENT_DATE, INTERVAL 3 DAY) THEN 'PRÓXIMO'
        ELSE 'NORMAL'
    END AS estado_urgencia,
    DATEDIFF(CURRENT_DATE, t.tick_fecha_apertura) AS dias_abierto
FROM TICKET t
JOIN ACTIVO a ON a.act_id = t.act_id
JOIN TIPO_ACTIVO ta ON ta.tip_act_id = a.tip_act_id
LEFT JOIN USUARIO u_asignado ON u_asignado.usu_id = t.tick_asignado_a
LEFT JOIN USUARIO u_reporta ON u_reporta.usu_id = t.tick_reportado_por
WHERE t.tick_estado IN ('Abierto','En_Proceso')
ORDER BY 
    CASE t.tick_prioridad
        WHEN 'Critica' THEN 1
        WHEN 'Alta' THEN 2
        WHEN 'Media' THEN 3
        WHEN 'Baja' THEN 4
    END,
    t.tick_fecha_vencimiento ASC;

-- Vista de mantenimientos por período
CREATE OR REPLACE VIEW VW_MANTENIMIENTOS_PERIODO AS
SELECT
    m.mant_id,
    m.mant_fecha_inicio,
    m.mant_fecha_fin,
    m.mant_tipo,
    a.act_numero_activo,
    ta.nombre AS tipo_activo,
    a.act_marca,
    a.act_modelo,
    u.usu_nombre AS tecnico,
    m.mant_estado,
    t.tick_numero,
    CASE 
        WHEN m.mant_fecha_fin IS NOT NULL 
        THEN TIMESTAMPDIFF(MINUTE, m.mant_fecha_inicio, m.mant_fecha_fin)
        ELSE NULL 
    END AS duracion_minutos
FROM MANTENIMIENTO m
JOIN ACTIVO a ON a.act_id = m.act_id
JOIN TIPO_ACTIVO ta ON ta.tip_act_id = a.tip_act_id
JOIN USUARIO u ON u.usu_id = m.mant_tecnico_asignado
LEFT JOIN TICKET t ON t.tick_id = m.tick_id;

-- Vista de traslados activos
CREATE OR REPLACE VIEW VW_TRASLADOS_ACTIVOS AS
SELECT
    tr.tras_id,
    tr.tras_numero,
    a.act_numero_activo,
    ta.nombre AS tipo_activo,
    uo.ubi_nombre AS origen,
    ud.ubi_nombre AS destino,
    tr.tras_fecha_salida,
    tr.tras_fecha_devolucion_prog,
    tr.tras_estado,
    tr.tras_motivo,
    CASE 
        WHEN tr.tras_fecha_devolucion_prog < CURRENT_DATE AND tr.tras_estado = 'Entregado' THEN 'VENCIDO'
        WHEN tr.tras_fecha_devolucion_prog <= DATE_ADD(CURRENT_DATE, INTERVAL 3 DAY) AND tr.tras_estado = 'Entregado' THEN 'PRÓXIMO'
        ELSE 'NORMAL'
    END AS estado_devolucion,
    DATEDIFF(CURRENT_DATE, tr.tras_fecha_salida) AS dias_fuera
FROM TRASLADO tr
JOIN ACTIVO a ON a.act_id = tr.act_id
JOIN TIPO_ACTIVO ta ON ta.tip_act_id = a.tip_act_id
JOIN UBICACION uo ON uo.ubi_id = tr.tras_ubicacion_origen
JOIN UBICACION ud ON ud.ubi_id = tr.tras_ubicacion_destino
WHERE tr.tras_estado IN ('Programado','En_Transito','Entregado');

-- Vista de alertas activas
CREATE OR REPLACE VIEW VW_ALERTAS_ACTIVAS AS
SELECT
    al.ale_id,
    al.ale_tipo,
    al.ale_titulo,
    al.ale_mensaje,
    al.ale_fecha_objetivo,
    al.ale_prioridad,
    al.ale_estado,
    a.act_numero_activo,
    ta.nombre AS tipo_activo,
    u.ubi_nombre AS ubicacion_actual,
    al.ale_email_enviado,
    al.ale_fecha_envio
FROM ALERTA al
JOIN ACTIVO a ON a.act_id = al.act_id
JOIN TIPO_ACTIVO ta ON ta.tip_act_id = a.tip_act_id
LEFT JOIN UBICACION u ON u.ubi_id = a.act_ubicacion_actual
WHERE al.ale_estado IN ('Pendiente','Enviada')
ORDER BY 
    CASE al.ale_prioridad
        WHEN 'Critica' THEN 1
        WHEN 'Advertencia' THEN 2
        WHEN 'Info' THEN 3
    END,
    al.ale_fecha_objetivo ASC;

-- =====================================================================
--  15) DATOS DE EJEMPLO PARA TESTING
-- =====================================================================

-- Activos de ejemplo
INSERT INTO ACTIVO (act_numero_activo, tip_act_id, act_marca, act_modelo, act_numero_serie, act_especificaciones, act_fecha_adquisicion, act_estado, act_ubicacion_actual, act_responsable_actual, creado_por) VALUES
('PC-CC-001', 1, 'Dell', 'OptiPlex 3080', 'DL3080-001', 'Intel i5-10400, 8GB RAM, 256GB SSD, Windows 11 Pro', '2024-01-15', 'Operativo', 1, 'María González - Administración', 1),
('PC-CC-002', 1, 'HP', 'ProDesk 400 G7', 'HP400-002', 'Intel i3-10100, 4GB RAM, 500GB HDD, Windows 11 Pro', '2024-02-20', 'Operativo', 2, 'Carlos Ruiz - Contabilidad', 1),
('PC-CC-003', 1, 'Lenovo', 'ThinkCentre M720', 'LN720-003', 'Intel i7-9700, 16GB RAM, 512GB SSD, Windows 11 Pro', '2024-03-10', 'Operativo', 3, 'Ana López - Soporte Técnico', 1),
('IMP-CC-001', 2, 'HP', 'LaserJet Pro 404n', 'HP404-001', 'Impresora láser monocromática, red ethernet', '2024-01-25', 'Operativo', 1, 'Compartida - Administración', 1),
('IMP-CC-002', 2, 'Canon', 'PIXMA G3160', 'CN3160-002', 'Impresora multifunción tinta continua, WiFi', '2024-04-05', 'Operativo', 2, 'Compartida - Contabilidad', 1),
('PC-SUC-001', 1, 'Asus', 'VivoPC VM42', 'AS42-001', 'Intel Celeron N4020, 4GB RAM, 128GB SSD', '2024-05-15', 'Trasladado', 5, 'Roberto Silva - Suc. San Lorenzo', 1);

-- Algunos tickets de ejemplo
INSERT INTO TICKET (act_id, tick_tipo, tick_prioridad, tick_titulo, tick_descripcion, tick_fecha_vencimiento, tick_reportado_por, tick_asignado_a) VALUES
(1, 'Preventivo', 'Media', 'Mantenimiento Preventivo - PC Dell OptiPlex', 'Mantenimiento preventivo programado: limpieza interna, actualización de sistema', DATE_ADD(CURRENT_DATE, INTERVAL 5 DAY), 1, 1),
(4, 'Correctivo', 'Alta', 'Impresora HP no imprime', 'La impresora HP LaserJet no responde, posible problema de conexión de red', NULL, 1, 1),
(2, 'Preventivo', 'Media', 'Mantenimiento Preventivo - PC HP ProDesk', 'Mantenimiento preventivo programado según plan establecido', DATE_ADD(CURRENT_DATE, INTERVAL 10 DAY), 1, 1);

-- Algunos mantenimientos
INSERT INTO MANTENIMIENTO (tick_id, act_id, plan_id, mant_fecha_inicio, mant_tipo, mant_descripcion_inicial, mant_tecnico_asignado, mant_estado) VALUES
(1, 1, 1, CURRENT_TIMESTAMP, 'Preventivo', 'Mantenimiento preventivo programado', 1, 'Programado'),
(2, 4, NULL, CURRENT_TIMESTAMP, 'Correctivo', 'Impresora no imprime - verificar conectividad', 1, 'En_Proceso');

-- Traslado de ejemplo
INSERT INTO TRASLADO (act_id, tras_fecha_salida, tras_ubicacion_origen, tras_ubicacion_destino, tras_motivo, tras_estado, tras_responsable_envio, tras_responsable_recibo, tras_fecha_devolucion_prog, autorizado_por, creado_por) VALUES
(6, '2024-05-15 09:00:00', 3, 5, 'Equipamiento de nueva sucursal San Lorenzo', 'Entregado', 'Ana López', 'Roberto Silva', DATE_ADD(CURRENT_DATE, INTERVAL 30 DAY), 1, 1);

-- Restaurar delimitador por defecto
DELIMITER ;
-- =====================================================================
--  17) ÍNDICES ADICIONALES PARA OPTIMIZACIÓN
-- =====================================================================

CREATE INDEX ix_ticket_fecha_venc_tipo ON TICKET(tick_fecha_vencimiento, tick_tipo);
CREATE INDEX ix_mantenimiento_fecha_prox ON MANTENIMIENTO(mant_proxima_fecha);
CREATE INDEX ix_alerta_fecha_tipo ON ALERTA(ale_fecha_alerta, ale_tipo);
CREATE INDEX ix_traslado_fecha_dev ON TRASLADO(tras_fecha_devolucion_prog);
CREATE INDEX ix_activo_estado_tipo ON ACTIVO(act_estado, tip_act_id);
