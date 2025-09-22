@echo off
REM ===========================================
REM GÉNÉRATEUR DE CLÉS SÉCURISÉES - TANAYE
REM ===========================================

echo 🔐 GÉNÉRATEUR DE CLÉS SÉCURISÉES
echo =================================
echo.

REM Vérifier si OpenSSL est installé
where openssl >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo ❌ OpenSSL n'est pas installé. Installez-le d'abord.
    pause
    exit /b 1
)

echo 🔑 Génération des clés...
echo.

REM Générer JWT Secret
for /f %%i in ('openssl rand -base64 32') do set JWT_SECRET=%%i
echo JWT_SECRET=%JWT_SECRET%
echo.

REM Générer mot de passe de base de données
for /f %%i in ('openssl rand -base64 24') do set DB_PASSWORD=%%i
echo DATABASE_PASSWORD=%DB_PASSWORD%
echo.

echo ✅ Clés générées !
echo.
echo ⚠️  Copiez ces valeurs dans application-local.properties
echo.
pause
