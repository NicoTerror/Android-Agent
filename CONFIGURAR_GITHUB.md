# Configurar GitHub para Push

## Problema
GitHub ya **NO acepta contraseñas** para hacer push. Necesitas un **Personal Access Token (PAT)**.

## Solución: Crear un Personal Access Token

### Paso 1: Crear el Token en GitHub

1. Ve a GitHub.com e inicia sesión
2. Click en tu **avatar** (esquina superior derecha) → **Settings**
3. En el menú lateral izquierdo, ve a **Developer settings** (al final)
4. Click en **Personal access tokens** → **Tokens (classic)**
5. Click en **Generate new token** → **Generate new token (classic)**
6. Configura el token:
   - **Note**: "Android Control Project" (o el nombre que quieras)
   - **Expiration**: Elige una duración (90 días, 1 año, o sin expiración)
   - **Scopes**: Marca al menos:
     - ✅ `repo` (acceso completo a repositorios)
7. Click en **Generate token** (abajo)
8. **⚠️ IMPORTANTE**: Copia el token inmediatamente (solo se muestra una vez)
   - Ejemplo: `ghp_xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx`

### Paso 2: Usar el Token para Push

Tienes **3 opciones**:

#### Opción A: Usar el token directamente (más fácil)

Cuando hagas `git push`, usa el token como contraseña:

```powershell
git push origin main
# Username: tu-usuario-github
# Password: pega-aqui-tu-token (ghp_xxxxx...)
```

#### Opción B: Guardar el token en Git Credential Manager (recomendado)

```powershell
# Configurar Git para usar el token
git config --global credential.helper manager-core

# Hacer push (te pedirá usuario y contraseña una vez)
git push origin main
# Username: tu-usuario-github
# Password: pega-tu-token-aqui
# Git guardará las credenciales automáticamente
```

#### Opción C: Usar el token en la URL (temporal)

```powershell
# Agregar el remote con el token
git remote set-url origin https://TU-TOKEN@github.com/TU-USUARIO/TU-REPO.git

# O crear el remote si no existe
git remote add origin https://TU-TOKEN@github.com/TU-USUARIO/TU-REPO.git
```

**⚠️ CUIDADO**: Esta opción guarda el token en texto plano. No es recomendable para repositorios públicos.

## Pasos Completos para Subir el Proyecto

### 1. Inicializar Git (si no está inicializado)

```powershell
cd C:\Users\davso\Documents\Androidcontrol
git init
```

### 2. Configurar usuario de Git (si no está configurado)

```powershell
git config --global user.name "Tu Nombre"
git config --global user.email "tu-email@ejemplo.com"
```

### 3. Crear repositorio en GitHub

1. Ve a https://github.com/new
2. Nombre del repositorio: `androidcontrol` (o el que prefieras)
3. Elige **Private** o **Public**
4. **NO marques** "Initialize with README" (ya tienes archivos)
5. Click en **Create repository**

### 4. Agregar archivos y hacer commit

```powershell
# Agregar todos los archivos
git add .

# Hacer commit
git commit -m "Proyecto inicial - Android Control"

# Agregar el remote (reemplaza TU-USUARIO y TU-REPO)
git remote add origin https://github.com/TU-USUARIO/TU-REPO.git

# Verificar el remote
git remote -v
```

### 5. Hacer Push con el Token

```powershell
# Configurar credential helper (solo una vez)
git config --global credential.helper manager-core

# Hacer push
git push -u origin main

# Cuando te pida:
# Username: tu-usuario-github
# Password: pega-tu-personal-access-token-aqui
```

## Solución de Problemas

### Error: "remote: Support for password authentication was removed"

**Solución**: Necesitas usar un Personal Access Token en lugar de tu contraseña.

### Error: "fatal: could not read Username"

**Solución**: Configura el remote correctamente:
```powershell
git remote set-url origin https://github.com/TU-USUARIO/TU-REPO.git
```

### Error: "Permission denied"

**Solución**: Verifica que el token tenga el scope `repo` habilitado.

### Limpiar credenciales guardadas (si hay problemas)

```powershell
# Windows
git credential-manager-core erase
# O desde el Panel de Control: Credential Manager → Windows Credentials → Buscar "git:" y eliminar
```

## Alternativa: Usar SSH (más seguro a largo plazo)

Si prefieres no usar tokens, puedes configurar SSH:

1. Generar clave SSH:
```powershell
ssh-keygen -t ed25519 -C "tu-email@ejemplo.com"
```

2. Copiar la clave pública:
```powershell
cat ~/.ssh/id_ed25519.pub
```

3. Agregar la clave en GitHub:
   - Settings → SSH and GPG keys → New SSH key
   - Pega la clave pública

4. Cambiar el remote a SSH:
```powershell
git remote set-url origin git@github.com:TU-USUARIO/TU-REPO.git
```

## Nota Importante

- **Nunca compartas tu Personal Access Token**
- Si el token se compromete, revócalo inmediatamente en GitHub
- Los tokens tienen expiración (configúrala según tus necesidades)

