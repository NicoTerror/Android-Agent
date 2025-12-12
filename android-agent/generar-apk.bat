@echo off
echo ========================================
echo Generando APK para instalacion manual
echo ========================================
echo.

cd /d "%~dp0"

echo Limpiando compilaciones anteriores...
call gradlew clean

echo.
echo Compilando APK Debug...
call gradlew assembleDebug

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ========================================
    echo APK generado exitosamente!
    echo ========================================
    echo.
    echo El APK se encuentra en:
    echo app\build\outputs\apk\debug\app-debug.apk
    echo.
    echo Puedes copiar este archivo a tu dispositivo e instalarlo.
    echo.
    pause
) else (
    echo.
    echo ERROR: La compilacion fallo
    echo.
    pause
    exit /b 1
)








