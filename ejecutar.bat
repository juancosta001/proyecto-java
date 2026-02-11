@echo off
echo ====================================
echo  EJECUTAR SISTEMA DE ACTIVOS
echo ====================================

set MAVEN_BIN="C:\Program Files\NetBeans-19\netbeans\java\maven\bin\mvn.cmd"

echo Compilando y ejecutando con Maven...
echo (Esto incluye todas las dependencias: POI, iText, MySQL, etc.)

call %MAVEN_BIN% clean compile -q

if %errorlevel% neq 0 (
    echo Error en compilacion
    pause
    exit /b %errorlevel%
)

call %MAVEN_BIN% exec:java -Dexec.mainClass="com.ypacarai.cooperativa.activos.view.LoginWindowNew"

pause
