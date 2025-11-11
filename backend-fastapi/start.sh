#!/bin/bash
echo "Iniciando Backend FastAPI..."
cd "$(dirname "$0")"

if [ ! -d "venv" ]; then
    echo "Creando entorno virtual..."
    python3 -m venv venv
fi

source venv/bin/activate
echo "Instalando dependencias..."
pip install -r requirements.txt
echo ""
echo "Iniciando servidor en http://localhost:8000"
echo "Presiona Ctrl+C para detener"
echo ""
cd app
python main.py

