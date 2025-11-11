#!/bin/bash
echo "Iniciando Web Dashboard..."
cd "$(dirname "$0")"

if [ ! -d "node_modules" ]; then
    echo "Instalando dependencias..."
    npm install
fi

echo ""
echo "Iniciando servidor de desarrollo..."
echo "El dashboard estará disponible en http://localhost:5173"
echo "Presiona Ctrl+C para detener"
echo ""
npm run dev

