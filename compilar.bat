@echo off
echo =====================================
echo  COMPILACION DEL SISTEMA DE ACTIVOS
echo =====================================

set MAVEN_BIN="C:\Program Files\NetBeans-19\netbeans\java\maven\bin\mvn.cmd"

echo Compilando con Maven (incluye todas las dependencias)...
call %MAVEN_BIN% clean compile -q

if %errorlevel% neq 0 (
    echo.
    echo ERROR: Fallo en la compilacion
    echo.
    pause
    exit /b %errorlevel%
)

echo.
echo =====================================
echo  COMPILACION COMPLETADA EXITOSAMENTE
echo =====================================
echo.

pause
pause
