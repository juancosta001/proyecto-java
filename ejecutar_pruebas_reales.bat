@echo off
echo ====================================
echo  PANEL DE PRUEBAS REALES - EMAIL
echo ====================================

echo Verificando MailHog...
tasklist /FI "IMAGENAME eq mailhog.exe" 2>NUL | find /I /N "mailhog.exe">NUL
if %ERRORLEVEL%==0 (
    echo   Mailhog ya esta ejecutandose
) else (
    echo   Iniciando MailHog en segundo plano...
    start /B mailhog.exe
    timeout /T 3 /NOBREAK >NUL
)

echo.
echo Verificando Web Interface...
echo   http://localhost:8025 (MailHog Web UI)
echo.

echo Iniciando Panel de Pruebas Reales...
java -cp "target\classes;lib\mysql-connector-j-8.0.33.jar;lib\javax.mail-1.6.2.jar;lib\activation-1.1.1.jar" com.ypacarai.cooperativa.activos.test.RealTestPanel

pause