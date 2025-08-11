@echo off
REM Script de lancement du système de caisse enregistreuse pour Windows
REM Assurez-vous que Java 17+ est installé sur votre système

echo === Système de Caisse Enregistreuse ===
echo Vérification de Java...

REM Vérifier si Java est disponible
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo Erreur: Java n'est pas installé ou n'est pas dans le PATH
    pause
    exit /b 1
)

echo Java détecté - OK

REM Vérifier si Maven est disponible
mvn -version >nul 2>&1
if %errorlevel% equ 0 (
    echo Maven détecté - Lancement avec Maven...
    mvn clean javafx:run
) else (
    echo Maven non détecté - Tentative de lancement du JAR...
    
    REM Chercher le fichier JAR
    for /r target %%i in (*.jar) do (
        set JAR_FILE=%%i
        goto :found_jar
    )
    
    echo Erreur: Aucun fichier JAR trouvé. Veuillez compiler le projet avec 'mvn package'
    pause
    exit /b 1
    
    :found_jar
    echo JAR trouvé: %JAR_FILE%
    echo Note: Assurez-vous que JavaFX est disponible dans votre environnement
    java -jar "%JAR_FILE%"
)

pause

