@echo off
echo =====================================
echo REFACTOR Y LIMPIEZA DEL PROYECTO
echo =====================================
echo.

echo 1. Eliminando archivos temporales de configuracion...
if exist "src\main\java\com\ypacarai\cooperativa\activos\view\ConfiguracionPanelSimple.java" (
    del "src\main\java\com\ypacarai\cooperativa\activos\view\ConfiguracionPanelSimple.java"
    echo    - ConfiguracionPanelSimple.java eliminado
)

echo.
echo 2. Eliminando tests temporales de desarrollo...

REM Tests de configuracion duplicados/temporales
if exist "src\main\java\TestConfiguracionCompleto.java" (
    del "src\main\java\TestConfiguracionCompleto.java"
    echo    - TestConfiguracionCompleto.java eliminado
)

if exist "src\main\java\com\ypacarai\cooperativa\activos\view\TestMainWindowConfiguracion.java" (
    del "src\main\java\com\ypacarai\cooperativa\activos\view\TestMainWindowConfiguracion.java"
    echo    - TestMainWindowConfiguracion.java eliminado
)

if exist "src\main\java\com\ypacarai\cooperativa\activos\view\TestCRUDConfiguracion.java" (
    del "src\main\java\com\ypacarai\cooperativa\activos\view\TestCRUDConfiguracion.java"
    echo    - TestCRUDConfiguracion.java (duplicado) eliminado
)

if exist "src\main\java\com\ypacarai\cooperativa\activos\view\TestConfiguracion.java" (
    del "src\main\java\com\ypacarai\cooperativa\activos\view\TestConfiguracion.java"
    echo    - TestConfiguracion.java eliminado
)

REM Tests de desarrollo ya obsoletos
if exist "src\main\java\com\ypacarai\cooperativa\activos\TestComparacionMetodos.java" (
    del "src\main\java\com\ypacarai\cooperativa\activos\TestComparacionMetodos.java"
    echo    - TestComparacionMetodos.java eliminado
)

if exist "src\main\java\com\ypacarai\cooperativa\activos\TestMantenimientoEspecifico.java" (
    del "src\main\java\com\ypacarai\cooperativa\activos\TestMantenimientoEspecifico.java"
    echo    - TestMantenimientoEspecifico.java eliminado
)

if exist "src\main\java\com\ypacarai\cooperativa\activos\TestVisualizacionActivos.java" (
    del "src\main\java\com\ypacarai\cooperativa\activos\TestVisualizacionActivos.java"
    echo    - TestVisualizacionActivos.java eliminado
)

echo.
echo 3. Eliminando archivos de debug temporales...

if exist "src\main\java\com\ypacarai\cooperativa\activos\DiagnosticoAutoIncrement.java" (
    del "src\main\java\com\ypacarai\cooperativa\activos\DiagnosticoAutoIncrement.java"
    echo    - DiagnosticoAutoIncrement.java eliminado
)

if exist "src\main\java\com\ypacarai\cooperativa\activos\DebugUsuarios.java" (
    del "src\main\java\com\ypacarai\cooperativa\activos\DebugUsuarios.java"
    echo    - DebugUsuarios.java eliminado
)

if exist "src\main\java\com\ypacarai\cooperativa\activos\DiagnosticoUsuarios.java" (
    del "src\main\java\com\ypacarai\cooperativa\activos\DiagnosticoUsuarios.java"
    echo    - DiagnosticoUsuarios.java eliminado
)

echo.
echo 4. Verificando duplicados de DAO de configuracion...
if exist "src\main\java\com\ypacarai\cooperativa\activos\dao\ConfiguracionMantenimientoDAOFixed.java" (
    echo    - ATENCION: ConfiguracionMantenimientoDAOFixed.java existe
    echo      Revisar manualmente si reemplazar ConfiguracionMantenimientoDAO.java
)

echo.
echo 5. Eliminando archivos generados temporalmente...
if exist "VerificarEstadosActivos.java" (
    del "VerificarEstadosActivos.java"
    echo    - VerificarEstadosActivos.java eliminado
)

if exist "VerificarEstructuraBD.java" (
    del "VerificarEstructuraBD.java"
    echo    - VerificarEstructuraBD.java eliminado
)

echo.
echo =====================================
echo LIMPIEZA COMPLETADA
echo =====================================
echo.
echo Archivos MANTENIDOS (funcionando):
echo   + RealTestPanel.java
echo   + EmailTestPanel.java
echo   + Todo el sistema de mantenimiento tercerizado
echo   + Toda la funcionalidad principal
echo.
echo Siguiente paso: Recompilar el sistema
echo.
pause