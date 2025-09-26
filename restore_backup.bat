@echo off
echo =====================================
echo  RESTAURAR DATOS DESPUES DE REINSTALAR
echo =====================================
echo.

REM Buscar el directorio de backup más reciente
set BACKUP_DIR=
for /f "delims=" %%i in ('dir /b /ad C:\backup_xampp_* 2^>nul') do set BACKUP_DIR=C:\%%i

if "%BACKUP_DIR%"=="" (
    echo ❌ No se encontró directorio de backup
    echo Por favor, especifica la ruta manualmente
    set /p BACKUP_DIR="Ingresa la ruta del backup: "
)

echo Restaurando desde: %BACKUP_DIR%
echo.

REM 1. Restaurar proyecto
echo [1/4] Restaurando proyecto...
if exist "%BACKUP_DIR%\proyecto" (
    xcopy "%BACKUP_DIR%\proyecto" "C:\xampp\htdocs\sistema-activos-ypacarai\" /E /I /H /Y >nul 2>&1
    echo ✓ Proyecto restaurado
) else (
    echo ⚠️ No se encontró backup del proyecto
)

REM 2. Iniciar MySQL
echo.
echo [2/4] Iniciando servicios XAMPP...
if exist "C:\xampp\xampp_start.exe" (
    start "" "C:\xampp\xampp_start.exe"
    timeout /t 10 /nobreak >nul
    echo ✓ XAMPP iniciado
) else (
    echo ⚠️ Ejecuta manualmente el Panel de Control de XAMPP
    echo ⚠️ Inicia Apache y MySQL
    pause
)

REM 3. Restaurar base de datos
echo.
echo [3/4] Restaurando base de datos...
echo ℹ️  Ejecutando setup_database.sql...

cd /d "C:\xampp\htdocs\sistema-activos-ypacarai"

REM Esperar a que MySQL esté listo
timeout /t 5 /nobreak >nul

REM Crear base de datos usando nuestro script
"C:\xampp\mysql\bin\mysql.exe" -u root < setup_database.sql
if %ERRORLEVEL% == 0 (
    echo ✓ Base de datos creada exitosamente
) else (
    echo ⚠️ Error creando base de datos
    echo ℹ️  Ejecuta manualmente: mysql -u root < setup_database.sql
)

REM 4. Verificar instalación
echo.
echo [4/4] Verificando instalación...
java -cp ".;lib\mysql-connector-j-8.0.33.jar" TestConexionSimple
if %ERRORLEVEL% == 0 (
    echo ✓ Conexión a base de datos exitosa
) else (
    echo ⚠️ Problemas con la conexión
)

echo.
echo =====================================
echo  RESTAURACIÓN COMPLETADA
echo =====================================
echo.
echo EL SISTEMA ESTÁ LISTO PARA USAR
echo Ejecuta: ejecutar.bat para iniciar
echo.
pause
