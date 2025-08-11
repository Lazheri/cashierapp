#!/bin/bash

# Script de lancement du système de caisse enregistreuse
# Assurez-vous que Java 17+ est installé sur votre système

echo "=== Système de Caisse Enregistreuse ==="
echo "Vérification de Java..."

# Vérifier la version de Java
if command -v java &> /dev/null; then
    JAVA_VERSION=$(java -version 2>&1 | head -n 1 | cut -d'"' -f2 | cut -d'.' -f1)
    if [ "$JAVA_VERSION" -ge 17 ]; then
        echo "Java $JAVA_VERSION détecté - OK"
    else
        echo "Erreur: Java 17 ou supérieur requis. Version détectée: $JAVA_VERSION"
        exit 1
    fi
else
    echo "Erreur: Java n'est pas installé ou n'est pas dans le PATH"
    exit 1
fi

# Vérifier si Maven est disponible
if command -v mvn &> /dev/null; then
    echo "Maven détecté - Lancement avec Maven..."
    mvn clean javafx:run
else
    echo "Maven non détecté - Tentative de lancement du JAR..."
    
    # Chercher le fichier JAR
    JAR_FILE=$(find target -name "*.jar" -type f | head -n 1)
    
    if [ -n "$JAR_FILE" ]; then
        echo "JAR trouvé: $JAR_FILE"
        echo "Note: Assurez-vous que JavaFX est disponible dans votre environnement"
        java -jar "$JAR_FILE"
    else
        echo "Erreur: Aucun fichier JAR trouvé. Veuillez compiler le projet avec 'mvn package'"
        exit 1
    fi
fi

