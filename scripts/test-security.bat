@echo off
REM ===========================================
REM TEST DE SÉCURITÉ SIMPLE - TANAYE BACKEND
REM ===========================================

echo 🔍 TEST DE SÉCURITÉ - TANAYE BACKEND
echo ====================================
echo.

echo 🔍 Vérification des fichiers sensibles...
echo.

REM Vérifier application-local.properties
if exist "src\main\resources\application-local.properties" (
    echo ✅ application-local.properties - Existe
    git check-ignore "src\main\resources\application-local.properties" >nul 2>nul
    if %ERRORLEVEL% EQU 0 (
        echo ✅ Ignoré par Git (SÉCURISÉ)
    ) else (
        echo ❌ PAS ignoré par Git (DANGEREUX!)
    )
) else (
    echo ⚠️  application-local.properties - N'existe pas
)

echo.

REM Vérifier GUIDE-SECRETS.md
if exist "GUIDE-SECRETS.md" (
    echo ✅ GUIDE-SECRETS.md - Existe
    git check-ignore "GUIDE-SECRETS.md" >nul 2>nul
    if %ERRORLEVEL% EQU 0 (
        echo ✅ Ignoré par Git (SÉCURISÉ)
    ) else (
        echo ❌ PAS ignoré par Git (DANGEREUX!)
    )
) else (
    echo ⚠️  GUIDE-SECRETS.md - N'existe pas
)

echo.

REM Vérifier start-with-secrets.bat
if exist "start-with-secrets.bat" (
    echo ✅ start-with-secrets.bat - Existe
    git check-ignore "start-with-secrets.bat" >nul 2>nul
    if %ERRORLEVEL% EQU 0 (
        echo ✅ Ignoré par Git (SÉCURISÉ)
    ) else (
        echo ❌ PAS ignoré par Git (DANGEREUX!)
    )
) else (
    echo ⚠️  start-with-secrets.bat - N'existe pas
)

echo.
echo 📊 RÉSUMÉ
echo =========

REM Compter les fichiers non ignorés
set UNSAFE_COUNT=0
git check-ignore "src\main\resources\application-local.properties" >nul 2>nul || set /a UNSAFE_COUNT+=1
git check-ignore "GUIDE-SECRETS.md" >nul 2>nul || set /a UNSAFE_COUNT+=1
git check-ignore "start-with-secrets.bat" >nul 2>nul || set /a UNSAFE_COUNT+=1

if %UNSAFE_COUNT% EQU 0 (
    echo ✅ TOUS les fichiers sensibles sont protégés !
    echo 🔒 Votre dépôt est sécurisé !
) else (
    echo ❌ %UNSAFE_COUNT% fichier(s) sensible(s) NON protégé(s)
    echo 🚨 ACTION REQUISE !
)

echo.
pause