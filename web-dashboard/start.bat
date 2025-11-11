@echo off
echo Iniciando Web Dashboard...
cd /d %~dp0
if not exist node_modules (
    echo Instalando dependencias...
    call npm install
)
echo.
echo Iniciando servidor de desarrollo...
echo El dashboard estara disponible en http://localhost:5173
echo Presiona Ctrl+C para detener
echo.
call npm run dev
pause

