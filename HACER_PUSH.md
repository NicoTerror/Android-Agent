# Cómo Hacer Push a GitHub - Guía Rápida

## Tu Repositorio
- **Remote**: `https://github.com/NicoTerror/Android-Agent.git`
- **Rama**: `main`

## El Problema
GitHub **ya no acepta contraseñas**. Necesitas un **Personal Access Token (PAT)**.

## Solución Rápida (3 pasos)

### Paso 1: Crear el Token en GitHub

1. Ve a: https://github.com/settings/tokens
2. Click en **"Generate new token"** → **"Generate new token (classic)"**
3. Configura:
   - **Note**: "Android Control"
   - **Expiration**: Elige (90 días, 1 año, etc.)
   - **Scopes**: Marca ✅ **`repo`** (todo)
4. Click **"Generate token"**
5. **⚠️ COPIA EL TOKEN INMEDIATAMENTE** (solo se muestra una vez)
   - Ejemplo: `ghp_xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx`

### Paso 2: Hacer Push

Ejecuta en PowerShell:

```powershell
cd C:\Users\davso\Documents\Androidcontrol
git add .
git commit -m "Actualización del proyecto"
git push origin main
```

### Paso 3: Cuando te pida credenciales

```
Username: NicoTerror
Password: [PEGA AQUÍ TU TOKEN - NO TU CONTRASEÑA]
```

**Importante**: 
- Usa tu **token** (ghp_xxxxx...), NO tu contraseña de GitHub
- Git guardará las credenciales automáticamente (solo la primera vez)

## Si Ya Tienes el Token

Solo ejecuta:

```powershell
git push origin main
```

Y cuando te pida la contraseña, pega tu token.

## Verificar que Funcionó

Después del push, verifica en:
https://github.com/NicoTerror/Android-Agent

Deberías ver tus cambios actualizados.

## Si Sigue Fallando

1. **Limpiar credenciales guardadas**:
   ```powershell
   # Abre el Panel de Control de Windows
   # Ve a: Credential Manager → Windows Credentials
   # Busca "git:https://github.com" y elimínalo
   ```

2. **Verificar el token**:
   - Asegúrate de que el token tenga el scope `repo`
   - Verifica que no haya expirado

3. **Usar el token en la URL** (temporal):
   ```powershell
   git remote set-url origin https://TU-TOKEN@github.com/NicoTerror/Android-Agent.git
   git push origin main
   ```
   (Reemplaza TU-TOKEN con tu token real)

## Nota de Seguridad

- **Nunca compartas tu token**
- Si lo compartiste por error, revócalo en GitHub y crea uno nuevo
- El token es como una contraseña: trátalo con cuidado

