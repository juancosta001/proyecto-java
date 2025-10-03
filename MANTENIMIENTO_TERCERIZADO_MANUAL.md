# Sistema de Mantenimiento Técnico Tercerizado
**Sistema de Gestión de Activos - Cooperativa Ypacaraí LTDA**

## 🎯 Propósito

Este módulo permite gestionar el mantenimiento de equipos (UPS, impresoras, etc.) que requieren servicio técnico tercerizado. Controla todo el flujo desde la solicitud hasta la entrega del equipo reparado.

## 📋 Funcionalidades Implementadas

### ✅ Gestión de Proveedores de Servicios
- **Registro de proveedores**: Empresas que brindan servicios técnicos
- **Información completa**: Nombre, teléfono, email, dirección, contacto principal, especialidades
- **Estado activo/inactivo**: Control de proveedores disponibles
- **Validaciones**: Campos obligatorios y formato de datos

### ✅ Gestión de Mantenimientos Tercerizados
- **Solicitud de mantenimiento**: Registrar equipos que necesitan servicio externo
- **Control de estados**: Solicitado → En Proceso → Finalizado → Cancelado
- **Seguimiento de fechas**: Retiro y entrega del equipo
- **Control de costos**: Presupuesto inicial y monto final cobrado
- **Garantías**: Registro de garantía con días de vigencia
- **Observaciones**: Detalle del problema inicial y trabajo realizado

### ✅ Estados de Activos
Se agregó el nuevo estado **"En_Servicio_Externo"** para equipos retirados por proveedores.

## 🔄 Flujo de Trabajo

### 1. Registro de Proveedores
```
Panel Mantenimiento → Pestaña "Mantenimiento Tercerizado" 
→ Pestaña "Proveedores" → Botón "Nuevo Proveedor"
```

**Datos requeridos:**
- ✅ Nombre de la empresa (obligatorio)
- ✅ Número de teléfono (obligatorio)  
- ✅ Contacto principal (obligatorio)
- ⚪ Email (opcional)
- ⚪ Dirección (opcional)
- ⚪ Especialidades (opcional)

### 2. Solicitud de Mantenimiento

```
Panel Mantenimiento → Pestaña "Mantenimiento Tercerizado" 
→ Pestaña "Mantenimientos" → Botón "Solicitar Mantenimiento"
```

**Datos requeridos:**
- ✅ Activo a reparar (solo activos no en servicio externo)
- ✅ Proveedor de servicios
- ✅ Descripción del problema
- ✅ Estado del equipo antes del retiro
- ⚪ Monto presupuestado (opcional)

**Resultado:**
- Se crea el registro con estado "Solicitado"
- Se asigna un ID único al mantenimiento

### 3. Registro de Retiro

```
Seleccionar mantenimiento → Botón "Registrar Retiro"
```

**Datos requeridos:**
- ✅ Fecha de retiro
- ⚪ Observaciones del retiro

**Resultado:**
- Estado cambia a "En_Proceso"
- Activo cambia a estado "En_Servicio_Externo"

### 4. Registro de Entrega

```
Seleccionar mantenimiento → Botón "Registrar Entrega"
```

**Datos requeridos:**
- ✅ Fecha de entrega
- ✅ Estado del equipo después de la reparación
- ✅ Trabajo realizado
- ⚪ Observaciones de entrega
- ⚪ Monto cobrado (si es diferente al presupuestado)
- ⚪ Garantía (sí/no y días de vigencia)

**Resultado:**
- Estado cambia a "Finalizado"
- Activo vuelve a estado "Operativo" (o según el estado reportado)

### 5. Cancelación (opcional)

```
Seleccionar mantenimiento → Botón "Cancelar"
```

- Solo disponible para mantenimientos en estado "Solicitado"
- El activo mantiene su estado original

## 📊 Reportes y Consultas

### Estadísticas Disponibles
- **Mantenimientos pendientes**: Solicitados + En proceso
- **Mantenimientos en garantía**: Finalizados con garantía vigente
- **Costos por período**: Total gastado en mantenimientos tercerizados

### Filtros de Búsqueda
- Por estado del mantenimiento
- Por proveedor
- Por activo
- Búsqueda general en texto

## 🗂️ Estructura de Base de Datos

### Tabla: `proveedor_servicio`
```sql
- prv_id (PK)
- prv_nombre
- prv_numero_telefono  
- prv_email
- prv_direccion
- prv_contacto_principal
- prv_especialidades
- activo
- creado_en, actualizado_en
```

### Tabla: `mantenimiento_tercerizado`
```sql
- mant_terc_id (PK)
- activo_id (FK)
- proveedor_id (FK)
- descripcion_problema
- fecha_retiro, fecha_entrega
- monto_presupuestado, monto_cobrado
- estado (ENUM: Solicitado, En_Proceso, Finalizado, Cancelado)
- observaciones_retiro, observaciones_entrega
- estado_equipo_antes, estado_equipo_despues
- trabajo_realizado
- garantia, dias_garantia
- registrado_por (FK)
- creado_en, actualizado_en
```

## 🚀 Instalación

### 1. Aplicar Cambios de Base de Datos
```batch
# Ejecutar el script de instalación
setup_mantenimiento_tercerizado.bat
```

O manualmente:
```sql
mysql -u root -p sistema_activos_cooperativa < src/main/resources/database/mantenimiento_tercerizado_schema.sql
```

### 2. Compilar y Ejecutar
```bash
mvn compile
mvn exec:java -Dexec.mainClass="com.ypacarai.cooperativa.activos.view.LoginWindowNew"
```

## 🔐 Permisos por Rol

### Jefe de Informática
- ✅ Gestión completa de proveedores (crear, editar, activar/desactivar)
- ✅ Gestión completa de mantenimientos (solicitar, registrar retiros/entregas)
- ✅ Ver todos los reportes y estadísticas

### Técnico  
- ⚪ Solo lectura de proveedores
- ✅ Solicitar mantenimientos
- ✅ Registrar retiros y entregas de sus mantenimientos
- ⚪ Reportes básicos

### Consulta
- ⚪ Solo lectura de todo el módulo
- ❌ No puede crear ni modificar registros

## 💡 Casos de Uso Comunes

### Escenario 1: UPS que no enciende
1. **Solicitud**: "UPS no enciende, LED rojo parpadeante"
2. **Estado inicial**: "Equipo totalmente apagado, no responde"  
3. **Retiro**: 15/01/2024, proveedor recoge equipo
4. **Entrega**: 22/01/2024, "Reemplazo de batería interna"
5. **Resultado**: Equipo operativo, garantía 90 días

### Escenario 2: Impresora con atasco recurrente
1. **Solicitud**: "Impresora se atasca constantemente"
2. **Estado inicial**: "Papel se atasca en rodillo, ruidos extraños"
3. **Retiro**: 10/02/2024, llevada al taller
4. **Entrega**: 17/02/2024, "Limpieza y ajuste de rodillos"
5. **Resultado**: Funcionando correctamente, garantía 30 días

## ⚠️ Consideraciones Importantes

### Seguridad
- Todos los cambios se registran con usuario y timestamp
- Los activos no pueden perderse del sistema (están "En_Servicio_Externo")
- Validaciones estrictas en cada paso del proceso

### Integridad de Datos
- No se pueden eliminar proveedores con mantenimientos asociados
- Los estados de activos se actualizan automáticamente
- Validación de fechas lógicas (entrega después de retiro)

### Respaldos
- Toda la información se almacena en la base de datos principal
- Los reportes pueden exportarse para auditorías
- Historial completo de cada mantenimiento

## 📞 Soporte

Para consultas sobre el sistema, contactar al administrador del sistema.

**Versión**: 1.0  
**Fecha**: Enero 2024  
**Sistema**: Gestión de Activos - Cooperativa Ypacaraí LTDA