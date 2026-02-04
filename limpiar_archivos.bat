@echo off
echo =====================================
echo  LIMPIEZA DE ARCHIVOS TEMPORALES
echo =====================================

echo Eliminando archivos de test temporales...

REM Tests creados durante debugging
if exist "AnalisisFechasVencimiento.java" del "AnalisisFechasVencimiento.java"
if exist "SistemaNotificacionesCorregido.java" del "SistemaNotificacionesCorregido.java"
if exist "TestCorreccionFinal.java" del "TestCorreccionFinal.java"
if exist "TestLogicaCorregida.java" del "TestLogicaCorregida.java"
if exist "VerificarDatosTickets.java" del "VerificarDatosTickets.java"

REM Tests antiguos innecesarios
if exist "Test*.java" del "Test*.java" /Q 2>nul
if exist "Verificar*.java" del "Verificar*.java" /Q 2>nul

REM Archivos .class generados en raiz
if exist "*.class" del "*.class" /Q 2>nul

echo =====================================
echo  LIMPIEZA COMPLETADA
echo =====================================
echo.
echo Archivos mantenidos:
echo - src/main/java/ (codigo fuente)
echo - lib/ (librerias)
echo - target/ (compilados)
echo - *.bat (scripts)
echo - pom.xml (maven)
echo.
echo Presiona cualquier tecla para continuar...
pause >nul