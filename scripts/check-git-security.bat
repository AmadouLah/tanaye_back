@echo off
REM ===========================================
REM VÉRIFICATION DE SÉCURITÉ GIT - WINDOWS
REM ===========================================
REM 
REM Ce script vérifie que vos fichiers sensibles ne sont pas dans Git

echo 🔍 VÉRIFICATION DE SÉCURITÉ GIT
echo ================================
echo.

REM Vérifier que nous sommes dans un dépôt Git
if not exist ".git" (
    echo ❌ Ce script doit être exécuté dans un dépôt Git
    pause
    exit /b 1
)

echo 🔍 Vérification des fichiers sensibles dans Git...
echo.

REM Fichiers sensibles à vérifier
set SENSITIVE_FILES=application-local.properties GUIDE-SECRETS.md start-with-secrets.bat start-with-secrets.sh

set FOUND_SENSITIVE=0

for %%f in (%SENSITIVE_FILES%) do (
    git ls-files | findstr /i "%%f" >nul 2>nul
    if !ERRORLEVEL! EQU 0 (
        echo ❌ FICHIER SENSIBLE DÉTECTÉ: %%f
        echo    Ce fichier est dans Git et contient des secrets !
        set /a FOUND_SENSITIVE+=1
    ) else (
        echo ✅ %%f - Non trouvé dans Git (sécurisé)
    )
)

echo.

REM Vérifier les patterns de secrets dans les fichiers committés
echo 🔍 Recherche de secrets dans les fichiers committés...
echo.

git ls-files | findstr /i "secret password key" >nul 2>nul
if %ERRORLEVEL% EQU 0 (
    echo ⚠️  Fichiers avec des noms suspects trouvés:
    git ls-files | findstr /i "secret password key"
    echo.
)

REM Vérifier le contenu des fichiers pour des secrets
echo 🔍 Vérification du contenu des fichiers...
echo.

REM Chercher des patterns de secrets dans les fichiers committés
REM Vérifier seulement les URLs complètes avec des identifiants
git grep -l "jdbc:postgresql://.*:.*@" -- "*.properties" "*.java" "*.md" 2>nul
if %ERRORLEVEL% EQU 0 (
    echo ❌ SECRETS DÉTECTÉS dans les fichiers committés !
    echo    Fichiers contenant des URLs de base de données avec identifiants:
    git grep -l "jdbc:postgresql://.*:.*@" -- "*.properties" "*.java" "*.md" 2>nul
    set /a FOUND_SENSITIVE+=1
) else (
    echo ✅ Aucun secret de base de données trouvé dans les fichiers committés
)

git grep -l "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9" -- "*.properties" "*.java" "*.md" 2>nul
if %ERRORLEVEL% EQU 0 (
    echo ❌ SECRETS DÉTECTÉS dans les fichiers committés !
    echo    Fichiers contenant des clés JWT:
    git grep -l "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9" -- "*.properties" "*.java" "*.md" 2>nul
    set /a FOUND_SENSITIVE+=1
) else (
    echo ✅ Aucune clé JWT trouvée dans les fichiers committés
)

echo.

REM Résumé
echo 📊 RÉSUMÉ DE LA VÉRIFICATION
echo =============================

if %FOUND_SENSITIVE% EQU 0 (
    echo ✅ Aucun fichier sensible détecté dans Git
    echo 🔒 Votre dépôt est sécurisé !
) else (
    echo ❌ %FOUND_SENSITIVE% problème(s) de sécurité détecté(s)
    echo.
    echo 🚨 ACTIONS REQUISES:
    echo    1. Supprimez les fichiers sensibles de Git
    echo    2. Ajoutez-les au .gitignore
    echo    3. Régénérez les secrets compromis
    echo.
    echo 💡 Commandes utiles:
    echo    git rm --cached application-local.properties
    echo    git rm --cached GUIDE-SECRETS.md
    echo    git commit -m "Remove sensitive files"
)

echo.
pause
