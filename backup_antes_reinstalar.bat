@echo off
echo =====================================
echo  BACKUP DE DATOS ANTES DE REINSTALAR
echo =====================================
echo.

REM Crear directorio de backup
set BACKUP_DIR=C:\backup_xampp_%date:~-4%_%date:~4,2%_%date:~7,2%
if not exist "%BACKUP_DIR%" mkdir "%BACKUP_DIR%"

echo Creando backup en: %BACKUP_DIR%
echo.

REM 1. Backup de la base de datos (si MySQL está funcionando)
echo [1/4] Intentando backup de base de datos...
if exist "C:\xampp\mysql\bin\mysqldump.exe" (
    "C:\xampp\mysql\bin\mysqldump.exe" -u root --all-databases > "%BACKUP_DIR%\backup_completo.sql" 2>nul
    if %ERRORLEVEL% == 0 (
        echo ✓ Backup de base de datos completado
    ) else (
        echo ⚠️ Error en backup de BD - MySQL no responde
        echo ℹ️  Usaremos el script setup_database.sql para recrear
    )
) else (
    echo ⚠️ mysqldump no encontrado
)

REM 2. Backup de archivos de configuración
echo.
echo [2/4] Backup de configuración...
if exist "C:\xampp\mysql\bin\my.ini" copy "C:\xampp\mysql\bin\my.ini" "%BACKUP_DIR%\" >nul 2>&1
if exist "C:\xampp\apache\conf\httpd.conf" copy "C:\xampp\apache\conf\httpd.conf" "%BACKUP_DIR%\" >nul 2>&1
if exist "C:\xampp\php\php.ini" copy "C:\xampp\php\php.ini" "%BACKUP_DIR%\" >nul 2>&1
echo ✓ Archivos de configuración respaldados

REM 3. Backup del proyecto actual
echo.
echo [3/4] Backup del proyecto actual...
xcopy "C:\xampp\htdocs\sistema-activos-ypacarai" "%BACKUP_DIR%\proyecto\" /E /I /H /Y >nul 2>&1
if %ERRORLEVEL% == 0 (
    echo ✓ Proyecto respaldado exitosamente
) else (
    echo ⚠️ Error respaldando proyecto
)

REM 4. Backup de datos de MySQL (archivos físicos si existen)
echo.
echo [4/4] Backup de datos MySQL (archivos físicos)...
if exist "C:\xampp\mysql\data" (
    xcopy "C:\xampp\mysql\data" "%BACKUP_DIR%\mysql_data\" /E /I /H /Y >nul 2>&1
    echo ✓ Datos de MySQL respaldados
) else (
    echo ⚠️ Directorio de datos MySQL no encontrado
)

echo.
echo =====================================
echo  BACKUP COMPLETADO
echo =====================================
echo Ubicación: %BACKUP_DIR%
echo.
echo PRÓXIMOS PASOS:
echo 1. Desinstalar XAMPP actual
echo 2. Descargar XAMPP más reciente
echo 3. Instalar XAMPP en C:\xampp
echo 4. Ejecutar restore_backup.bat
echo.
pause
