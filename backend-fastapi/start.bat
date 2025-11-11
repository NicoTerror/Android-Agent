@echo off
echo Iniciando Backend FastAPI...
cd /d %~dp0

REM Intentar usar py si python no está disponible
where python >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    set PYTHON_CMD=py
) else (
    set PYTHON_CMD=python
)

if not exist venv (
    echo Creando entorno virtual...
    %PYTHON_CMD% -m venv venv
)
call venv\Scripts\activate.bat
echo Instalando dependencias...
pip install -r requirements.txt
echo.
echo Iniciando servidor en http://localhost:8000
echo Presiona Ctrl+C para detener
echo.
cd app
REM Usar el Python del entorno virtual si existe, sino intentar py o python
if exist "..\venv\Scripts\python.exe" (
    ..\venv\Scripts\python.exe main.py
) else (
    REM Intentar usar py si python no está disponible
    where python >nul 2>&1
    if %ERRORLEVEL% NEQ 0 (
        py main.py
    ) else (
        python main.py
    )
)
pause

