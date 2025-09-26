@echo off
echo ====================================
echo  EJECUTAR SISTEMA DE ACTIVOS
echo ====================================

echo Iniciando sistema con Login...
java -cp "target\classes;lib\mysql-connector-j-8.0.33.jar" com.ypacarai.cooperativa.activos.view.LoginWindowNew

pause
