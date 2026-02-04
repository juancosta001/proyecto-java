@echo off
echo =====================================
echo  COMPILACION DEL SISTEMA DE ACTIVOS
echo =====================================

cd src\main\java

echo Compilando clases principales...
javac -cp "..\..\..\target\classes;..\..\..\lib\mysql-connector-j-8.0.33.jar;..\..\..\lib\javax.mail-1.6.2.jar;..\..\..\lib\activation-1.1.1.jar" -d ..\..\..\target\classes com\ypacarai\cooperativa\activos\view\*.java
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Fallo en la compilacion
    pause
    exit /b 1
)

javac -cp "..\..\..\target\classes;..\..\..\lib\mysql-connector-j-8.0.33.jar;..\..\..\lib\javax.mail-1.6.2.jar;..\..\..\lib\activation-1.1.1.jar" -d ..\..\..\target\classes com\ypacarai\cooperativa\activos\service\*.java
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Fallo en la compilacion de servicios
    pause
    exit /b 1
)

javac -cp "..\..\..\target\classes;..\..\..\lib\mysql-connector-j-8.0.33.jar;..\..\..\lib\javax.mail-1.6.2.jar;..\..\..\lib\activation-1.1.1.jar" -d ..\..\..\target\classes com\ypacarai\cooperativa\activos\dao\*.java
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Fallo en la compilacion de DAOs
    pause
    exit /b 1
)

javac -cp "..\..\..\target\classes;..\..\..\lib\mysql-connector-j-8.0.33.jar;..\..\..\lib\javax.mail-1.6.2.jar;..\..\..\lib\activation-1.1.1.jar" -d ..\..\..\target\classes com\ypacarai\cooperativa\activos\model\*.java
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Fallo en la compilacion de modelos
    pause
    exit /b 1
)

javac -cp "..\..\..\target\classes;..\..\..\lib\mysql-connector-j-8.0.33.jar" -d ..\..\..\target\classes com\ypacarai\cooperativa\activos\config\*.java
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Fallo en la compilacion de configuracion
    pause
    exit /b 1
)

cd ..\..\..

echo.
echo =====================================
echo  COMPILACION COMPLETADA EXITOSAMENTE
echo =====================================
echo.
pause
