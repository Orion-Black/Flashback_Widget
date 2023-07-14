@echo off
echo --Configuracion de FlashBack Widget--
echo Este script modificara el sistema para ejecutar la aplicacion al iniciar el sistema
echo Desea continuar? (Y/N)
set /p respuesta=
if /i "%respuesta%"=="Y" (
    cd /d "%~dp0"
    copy "FlashBack Widget.exe" "%APPDATA%\Microsoft\Windows\Start Menu\Programs\Startup"
    echo Finalizo exitosamente
    pause >nul
) else if /i "%respuesta%"=="N" (
    echo Saliendo del script...
    pause
    exit
) else (
    echo Respuesta invalida. Por favor, ingresa "Y" o "N".
    pause
)
