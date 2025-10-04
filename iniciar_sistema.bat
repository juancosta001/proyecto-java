@echo off
cd /d "c:\Users\jose4\OneDrive\Desktop\coope\sistema-activos-ypacarai\sistema-activos-ypacarai"
echo Iniciando Sistema de Activos - Cooperativa Ypacarai...
echo.
java -cp "target/classes;lib/*" com.ypacarai.cooperativa.activos.view.LoginWindowNew
pause