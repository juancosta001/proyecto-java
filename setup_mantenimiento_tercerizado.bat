@echo off
echo ========================================
echo  CREACION DE TABLAS MANTENIMIENTO TERCERIZADO
echo  Sistema de Gestion de Activos - Cooperativa Ypacarai
echo ========================================
echo.

echo Aplicando cambios de base de datos...
echo.

REM Configurar variables de conexion
set MYSQL_HOST=localhost
set MYSQL_USER=root
set MYSQL_DB=sistema_activos_cooperativa

REM Ejecutar el script SQL
echo Ejecutando script mantenimiento_tercerizado_schema.sql...
mysql -h %MYSQL_HOST% -u %MYSQL_USER% -p %MYSQL_DB% < src\main\resources\database\mantenimiento_tercerizado_schema.sql

if %ERRORLEVEL% EQU 0 (
    echo.
    echo *** TABLAS CREADAS EXITOSAMENTE ***
    echo.
    echo Se crearon las siguientes tablas:
    echo - proveedor_servicio
    echo - mantenimiento_tercerizado
    echo.
    echo Se agregaron proveedores de ejemplo.
    echo.
    echo IMPORTANTE:
    echo - Se agrego el nuevo estado 'En_Servicio_Externo' al enum de Activo
    echo - Ahora puede registrar mantenimientos tercerizados desde el menu
    echo - El sistema seguira el flujo: Solicitud → Retiro → Entrega
    echo.
) else (
    echo.
    echo *** ERROR AL CREAR TABLAS ***
    echo Verifique:
    echo - Que MySQL este ejecutandose
    echo - Que la base de datos existe
    echo - Las credenciales de conexion
)

echo.
pause