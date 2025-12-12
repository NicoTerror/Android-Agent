"""
Archivo WSGI para PythonAnywhere
Este archivo permite que PythonAnywhere ejecute tu aplicación FastAPI
"""
import sys
import os

# Agregar el directorio del proyecto al path
path = '/home/TU_USUARIO/Androidcontrol/backend-fastapi'
if path not in sys.path:
    sys.path.insert(0, path)

# Cambiar al directorio de la app
os.chdir(path)

# Importar la aplicación FastAPI
from app.main import app

# PythonAnywhere necesita que la variable se llame 'application'
application = app

