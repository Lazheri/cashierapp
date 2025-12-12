# Système de Caisse Enregistreuse

Ce projet est un système de caisse enregistreuse développé en Java, JavaFX et SQLite, conçu pour être une application de bureau multiplateforme avec une interface utilisateur propre, un support de base de données locale robuste et des capacités hors ligne.

## Fonctionnalités

- **Interface de Caisse Moderne** : Recherche de produits, gestion du panier, calcul du total.
- **Gestion des Produits** : Ajout, modification, suppression de produits.
- **Historique des Ventes** : Consultation des transactions passées.
- **Base de Données Locale** : Utilisation de SQLite pour la persistance des données.
- **Gestion des Stocks** : Mise à jour automatique des stocks après chaque vente.
- **Devise en Dinar Tunisien (TND)** : Toutes les transactions et affichages de prix sont en TND.
- **Produits Tunisiens** : Base de données de test pré-remplie avec des exemples de produits tunisiens.
- **Affichage Direct des Produits** : Les produits sont affichés directement sur l'interface principale sans recherche préalable.
- **Sélection de Quantité au Panier** : Possibilité de spécifier la quantité d'un produit avant de l'ajouter au panier.
- **Quantités Fractionnées** : Prise en charge des quantités décimales (ex: 0.300 kg) pour les produits vendus au poids (fruits, légumes).
- **Ajout par Catégorie** : Possibilité d'ajouter plusieurs produits d'une même catégorie au panier en une seule fois.
- **Améliorations UX/UI** : Interface plus intuitive et confortable pour l'utilisateur.

## Technologies Utilisées

- **Frontend (GUI)**: JavaFX (framework GUI moderne de Java)
- **Backend Logic**: Java
- **Database**: SQLite (base de données légère, basée sur des fichiers)
- **Build Tool**: Apache Maven

## Installation et Lancement

### Prérequis

- Java Development Kit (JDK) 17 ou plus récent.
- Apache Maven.

### Étapes

1.  **Cloner le dépôt (ou décompresser l'archive)**:
    ```bash
    git clone <URL_DU_DEPOT>
    cd cashier-system
    ```
    Si vous avez reçu une archive `.tar.gz`, décompressez-la :
    ```bash
    tar -xzvf cashier-system-updated.tar.gz
    cd cashier-system
    ```

2.  **Installer les dépendances Maven**:
    ```bash
    mvn clean install
    ```

3.  **Lancer l'application**:
    ```bash
    mvn clean javafx:run
    ```

    Vous pouvez également utiliser les scripts de lancement fournis :
    - Pour Linux/macOS:
      ```bash
      ./run.sh
      ```
    - Pour Windows:
      ```bash
      run.bat
      ```

### Comptes par défaut (démo)

Au premier lancement, l'application crée automatiquement deux utilisateurs :

- **admin / admin** (rôle **ADMIN**)
- **cashier / cashier** (rôle **CASHIER**)


### Création d'un Installateur (Raccourci d'Installation)

Pour créer un installateur natif (MSI pour Windows, DEB/RPM pour Linux, DMG pour macOS) qui peut être facilement distribué et installé sur n'importe quelle machine, vous pouvez utiliser l'outil `jpackage` inclus dans le JDK.

1.  **Assurez-vous que `jpackage` est disponible** dans votre PATH (il est généralement inclus avec le JDK).

2.  **Modifiez le `pom.xml`** pour inclure le plugin `javafx-maven-plugin` avec la configuration `jpackage`.
    Assurez-vous que la section `<build>` de votre `pom.xml` contient une configuration similaire à celle-ci (les versions peuvent varier) :

    ```xml
    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.10.1</version>
                <configuration>
                    <source>17</source>
                    <target>17</target>
                </configuration>
            </plugin>
            <plugin>
                <groupId>org.openjfx</groupId>
                <artifactId>javafx-maven-plugin</artifactId>
                <version>0.0.8</version>
                <configuration>
                    <mainClass>com.cashier.App</mainClass>
                    <jlinkImageName>cashier-system</jlinkImageName>
                    <jlinkZipFile>cashier-system-bundle</jlinkZipFile>
                    <noJdk>true</noJdk>
                    <launcher>cashier-system</launcher>
                    <jpackage>
                        <type>msi</type> <!-- ou deb, rpm, dmg, exe -->
                        <vendor>Votre Entreprise</vendor>
                        <appName>Cashier System</appName>
                        <appVersion>1.0.0</appVersion>
                        <input>target/cashier-system-bundle/bin</input>
                        <output>target/installer</output>
                        <icon>path/to/your/icon.ico</icon> <!-- Optionnel: chemin vers une icône -->
                    </jpackage>
                </configuration>
                <executions>
                    <execution>
                        <id>default-cli</id>
                        <goals>
                            <goal>run</goal>
                        </goals>
                        <configuration>
                            <launcher>cashier-system</launcher>
                        </configuration>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
    ```

3.  **Générez l'installateur**:
    ```bash
    mvn javafx:jpackage
    ```
    L'installateur sera créé dans le répertoire `target/installer`.

## Utilisation

### Interface Principale

-   **Table des Produits** : Affiche tous les produits disponibles. Vous pouvez filtrer par catégorie en utilisant la liste déroulante.
-   **Spinner de Quantité** : Utilisez-le pour ajuster la quantité du produit sélectionné avant de l'ajouter au panier. Pour les produits au poids (type "weight"), le pas est de 0.1 (100g).
-   **Bouton "Ajouter au Panier"** : Ajoute le produit sélectionné avec la quantité spécifiée au panier.
-   **Table du Panier** : Affiche les articles actuellement dans le panier. Vous pouvez modifier la quantité d'un article directement dans la table ou le supprimer.
-   **Bouton "Ajouter Catégorie au Panier"** : Ajoute tous les produits de la catégorie sélectionnée au panier avec une quantité par défaut (1 pour les articles, 0.1 pour les produits au poids).
-   **Labels "Sous-total" et "Total"** : Affichent le total des articles dans le panier en TND.
-   **Bouton "Payer"** : Finalise la vente, met à jour les stocks et enregistre la transaction.
-   **Bouton "Annuler Vente"** : Vide le panier.

### Gestion des Produits

Cliquez sur "Gestion des Produits" pour ouvrir une nouvelle fenêtre où vous pouvez ajouter, modifier ou supprimer des produits. Assurez-vous de spécifier le type de produit ("item" ou "weight") pour une gestion correcte des quantités.

### Historique des Ventes

Cliquez sur "Historique des Ventes" pour consulter toutes les ventes enregistrées.

## Structure du Projet

-   `src/main/java/com/cashier/`: Contient le code source Java.
    -   `App.java`: Classe principale de l'application JavaFX.
    -   `Database.java`: Gestion de la connexion et du schéma de la base de données SQLite.
    -   `TestData.java`: Classe pour insérer des données de test dans la base de données.
    -   `controller/`: Contrôleurs JavaFX pour la logique métier.
    -   `dao/`: Objets d'Accès aux Données (DAO) pour interagir avec la base de données.
    -   `model/`: Classes de modèle pour les entités (Produit, Vente, LigneVente).
-   `src/main/resources/`: Contient les ressources de l'interface utilisateur.
    -   `fxml/`: Fichiers FXML pour la définition de l'interface.
    -   `css/`: Fichiers CSS pour le style de l'application.
-   `pom.xml`: Fichier de configuration Maven.
-   `cashier.db`: Base de données SQLite (créée lors du premier lancement).

## Contribution

Les contributions sont les bienvenues. Veuillez ouvrir une issue ou soumettre une pull request.

## Licence

Ce projet est sous licence MIT. Voir le fichier `LICENSE` pour plus de détails.


