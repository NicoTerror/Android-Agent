@echo off
echo ========================================
echo Iniciando Backend FastAPI
echo ========================================
echo.

cd /d "%~dp0"

REM Verificar si existe el entorno virtual
if not exist "venv" (
    echo Creando entorno virtual...
    py -m venv venv
    if %ERRORLEVEL% NEQ 0 (
        echo ERROR: No se pudo crear el entorno virtual.
        echo Verifica que Python esté instalado.
        pause
        exit /b 1
    )
)

echo Instalando dependencias...
if exist "venv\Scripts\python.exe" (
    venv\Scripts\python.exe -m pip install -r requirements.txt >nul 2>&1
) else (
    echo ERROR: Entorno virtual no encontrado.
    echo Creando entorno virtual...
    py -m venv venv
    if %ERRORLEVEL% NEQ 0 (
        echo ERROR: No se pudo crear el entorno virtual.
        pause
        exit /b 1
    )
    venv\Scripts\python.exe -m pip install -r requirements.txt
)

echo.
echo ========================================
echo Backend iniciando en http://localhost:8000
echo ========================================
echo.
echo Presiona Ctrl+C para detener el servidor
echo.

cd app
REM Usar siempre el Python del entorno virtual
if exist "..\venv\Scripts\python.exe" (
    ..\venv\Scripts\python.exe main.py
) else (
    echo ERROR: Python del entorno virtual no encontrado.
    echo Verifica que el entorno virtual se haya creado correctamente.
    pause
    exit /b 1
)

