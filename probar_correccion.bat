@echo off
echo =====================================
echo PRUEBA CORRECCION ACTIVOS OPERATIVOS
echo =====================================
echo.
echo Verificando que NO se envien emails
echo de "Fuera de Servicio" cuando todos
echo los activos estan "Operativos"...
echo.

REM Ejecutar solo el método corregido
java -cp "target\classes;lib\mysql-connector-j-8.0.33.jar;lib\javax.mail-1.6.2.jar;lib\activation-1.1.1.jar" com.ypacarai.cooperativa.activos.test.TestActivosOperativos

echo.
echo =====================================
echo Verificar emails en: http://localhost:8025
echo =====================================
pause