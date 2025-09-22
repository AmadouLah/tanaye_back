@echo off
echo 🔍 VÉRIFICATION DE SÉCURITÉ
echo ============================
echo.

echo Vérification des fichiers sensibles...
echo.

if exist "src\main\resources\application-local.properties" (
    echo ✅ application-local.properties existe
    git check-ignore "src\main\resources\application-local.properties" >nul
    if %ERRORLEVEL% EQU 0 (
        echo ✅ Ignoré par Git
    ) else (
        echo ❌ PAS ignoré par Git
    )
) else (
    echo ⚠️  application-local.properties n'existe pas
)

echo.

if exist "start-with-secrets.bat" (
    echo ✅ start-with-secrets.bat existe
    git check-ignore "start-with-secrets.bat" >nul
    if %ERRORLEVEL% EQU 0 (
        echo ✅ Ignoré par Git
    ) else (
        echo ❌ PAS ignoré par Git
    )
) else (
    echo ⚠️  start-with-secrets.bat n'existe pas
)

echo.
echo Résumé: Vérification terminée
pause
