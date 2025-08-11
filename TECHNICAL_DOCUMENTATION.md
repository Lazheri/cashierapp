# Documentation Technique - Système de Caisse Enregistreuse

## Architecture du Projet

### Vue d'Ensemble

Le système de caisse enregistreuse suit une architecture MVC (Model-View-Controller) avec les couches suivantes :

- **Model** : Classes de données (Produit, Vente, LigneVente)
- **View** : Fichiers FXML pour l'interface utilisateur
- **Controller** : Contrôleurs JavaFX pour la logique d'interface
- **DAO** : Couche d'accès aux données (Data Access Object)
- **Database** : Gestion de la connexion SQLite

### Structure des Packages

```
com.cashier/
├── model/           # Classes de modèle de données
├── dao/             # Couche d'accès aux données
├── controller/      # Contrôleurs JavaFX
├── Database.java    # Gestion de la base de données
├── App.java         # Classe principale
└── TestData.java    # Données de test
```

## Technologies Utilisées

### Frameworks et Bibliothèques

- **Java 17** : Langage de programmation principal
- **JavaFX 17.0.2** : Framework d'interface utilisateur
- **SQLite JDBC 3.42.0.0** : Driver de base de données
- **JUnit Jupiter 5.10.0** : Framework de tests unitaires
- **Maven** : Gestionnaire de dépendances et de build

### Outils de Développement

- **Maven** : Gestion des dépendances et compilation
- **JavaFX Maven Plugin** : Exécution de l'application JavaFX
- **Surefire Plugin** : Exécution des tests

## Modèle de Données

### Classe Produit

```java
public class Produit {
    private int id;
    private String nom;
    private double prix;
    private double quantite; // Changé de int à double pour supporter les quantités fractionnées
    private String codeBarres;
    private String type;     // Nouveau champ: "item" pour les articles, "weight" pour les produits au poids
}
```

**Responsabilités** :
- Représentation d'un produit en stock
- Validation des données produit
- Gestion des informations de base (nom, prix, quantité, code-barres, type)

### Classe Vente

```java
public class Vente {
    private int id;
    private LocalDateTime dateVente;
    private double total;
}
```

**Responsabilités** :
- Représentation d'une transaction de vente
- Gestion de la date et du montant total
- Liaison avec les lignes de vente

### Classe LigneVente

```java
public class LigneVente {
    private int id;
    private int venteId;
    private int produitId;
    private double quantite; // Changé de int à double pour supporter les quantités fractionnées
    private double prixUnitaire;
}
```

**Responsabilités** :
- Détail d'un produit vendu dans une transaction
- Liaison entre vente et produit
- Conservation du prix au moment de la vente

## Couche d'Accès aux Données (DAO)

### ProduitDAO

**Méthodes principales** :
- `addProduit(Produit)` : Ajout d'un nouveau produit (prend en compte le type et la quantité double)
- `getProduitById(int)` : Récupération par ID
- `getProduitByCodeBarres(String)` : Recherche par code-barres
- `getAllProduits()` : Liste de tous les produits
- `updateProduit(Produit)` : Mise à jour d'un produit (prend en compte le type et la quantité double)
- `deleteProduit(int)` : Suppression d'un produit
- `deleteAllProduits()` : Suppression de tous les produits (utilisé pour les données de test)

### VenteDAO

**Méthodes principales** :
- `addVente(Vente)` : Enregistrement d'une nouvelle vente (retourne l'ID de la vente en double pour compatibilité)
- `getVenteById(int)` : Récupération d'une vente
- `getAllVentes()` : Liste de toutes les ventes
- `updateVente(Vente)` : Modification d'une vente
- `deleteVente(int)` : Suppression d'une vente

### LigneVenteDAO

**Méthodes principales** :
- `addLigneVente(LigneVente)` : Ajout d'une ligne de vente (prend en compte la quantité double)
- `getLignesVenteByVenteId(int)` : Récupération des lignes d'une vente
- `deleteLignesVenteByVenteId(int)` : Suppression des lignes d'une vente

## Interface Utilisateur

### Architecture FXML

L'interface utilise des fichiers FXML séparés pour chaque vue :

- **MainView.fxml** : Interface principale de caisse (affiche les produits directement, devise en TND, inclut le Spinner de quantité, le ComboBox de catégorie et le bouton "Ajouter Catégorie au Panier")
- **ProductManagement.fxml** : Gestion des produits (prix en TND, inclut le champ pour le type de produit)
- **SalesHistory.fxml** : Historique des ventes (totaux en TND)

### Contrôleurs

#### MainController

**Responsabilités** :
- Gestion de l'interface de caisse
- Affichage des produits disponibles
- Gestion du panier d'achat
- Traitement des paiements
- Navigation vers les autres vues
- **Nouveau** : Gestion du Spinner de quantité (pas de 0.1 pour les produits "weight", 1.0 pour les "item").
- **Nouveau** : Filtrage des produits par catégorie via un ComboBox.
- **Nouveau** : Ajout de tous les produits d'une catégorie au panier en une seule fois.

**Méthodes clés** :
- `addProductToCartFromTable()` : Ajout d'un produit sélectionné au panier avec la quantité spécifiée par le Spinner.
- `processPayment()` : Finalisation de la vente, mise à jour des stocks (gère les quantités double).
- `updateTotals()` : Calcul des totaux en TND.
- `setupQuantitySpinner()` : Initialise le Spinner de quantité.
- `setupCategoryComboBox()` : Initialise le ComboBox de catégorie.
- `filterProductsByCategory(String)` : Filtre les produits affichés par catégorie.
- `addCategoryToCart()` : Ajoute tous les produits d'une catégorie au panier.

#### ProductManagementController

**Responsabilités** :
- Interface de gestion des produits
- CRUD complet sur les produits
- Validation des données saisies (inclut le type de produit et la quantité double).

#### SalesHistoryController

**Responsabilités** :
- Affichage de l'historique des ventes
- Détails des transactions (totaux en TND)
- Suppression de ventes

### Styles CSS

Le fichier `style.css` définit :
- Palette de couleurs cohérente
- Styles pour les boutons et contrôles
- Mise en forme des tableaux
- Responsive design pour différentes tailles d'écran

## Base de Données

### Schéma SQLite

```sql
-- Table des produits
CREATE TABLE Produits (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    nom TEXT NOT NULL,
    prix REAL NOT NULL,
    quantite REAL NOT NULL, -- Changé de INTEGER à REAL
    code_barres TEXT UNIQUE,
    type TEXT                -- Nouveau champ: "item" ou "weight"
);

-- Table des ventes
CREATE TABLE Ventes (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    date_vente TEXT NOT NULL,
    total REAL NOT NULL
);

-- Table des lignes de vente
CREATE TABLE LignesVente (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    vente_id INTEGER NOT NULL,
    produit_id INTEGER NOT NULL,
    quantite REAL NOT NULL, -- Changé de INTEGER à REAL
    prix_unitaire REAL NOT NULL,
    FOREIGN KEY (vente_id) REFERENCES Ventes(id),
    FOREIGN KEY (produit_id) REFERENCES Produits(id)
);
```

### Gestion des Connexions

La classe `Database` utilise le pattern Singleton pour :
- Centraliser la gestion des connexions
- Assurer la création automatique des tables (avec les nouvelles colonnes `quantite` en `REAL` et `type` en `TEXT` pour `Produits`)
- Fournir une interface simple pour les DAO

## Tests

### Tests Unitaires

Le projet inclut des tests JUnit 5 pour :
- Validation des opérations CRUD
- Test de l'intégrité des données
- Vérification des contraintes de base de données

### Structure des Tests

```
src/test/java/com/cashier/dao/
└── ProduitDAOTest.java
```

**Tests couverts** :
- Ajout de produits
- Récupération par ID et code-barres
- Mise à jour de produits
- Suppression de produits
- Récupération de tous les produits

## Build et Déploiement

### Configuration Maven

Le fichier `pom.xml` configure :
- Dépendances JavaFX et SQLite
- Plugins de compilation Java 17
- Plugin JavaFX pour l'exécution
- Plugin Surefire pour les tests

### Commandes de Build

```bash
# Compilation
mvn compile

# Tests
mvn test

# Package JAR
mvn package

# Exécution
mvn javafx:run
```

### Packaging

L'application peut être packagée en :
- **JAR exécutable** : Inclut toutes les dépendances
- **Distribution native** : Avec jpackage (Java 14+)
- **Exécutable Windows** : Avec Launch4j (optionnel)

## Sécurité et Performance

### Sécurité

- **Injection SQL** : Utilisation de PreparedStatement
- **Validation des données** : Contrôles côté client et base
- **Gestion des erreurs** : Try-catch appropriés

### Performance

- **Connexions DB** : Ouverture/fermeture automatique
- **Mémoire** : Gestion appropriée des collections ObservableList
- **Interface** : Mise à jour asynchrone pour éviter le blocage

## Extensibilité

### Points d'Extension

1. **Nouveaux types de produits** : Extension du modèle Produit
2. **Modes de paiement** : Ajout de nouvelles méthodes de paiement
3. **Rapports** : Module de génération de rapports
4. **Multi-utilisateurs** : Système d'authentification
5. **Synchronisation** : Base de données distante

### Patterns Utilisés

- **DAO Pattern** : Séparation de la logique d'accès aux données
- **MVC Pattern** : Séparation des responsabilités
- **Observer Pattern** : JavaFX Properties pour la réactivité

## Maintenance

### Logs et Debugging

- Utilisation de `System.out.println` pour les opérations DB
- Messages d'erreur informatifs dans l'interface
- Gestion des exceptions avec affichage utilisateur

### Sauvegarde

- Base de données SQLite dans un fichier unique
- Sauvegarde simple par copie du fichier `.db`
- Possibilité d'export/import des données

## Évolutions Futures

### Améliorations Suggérées

1. **Interface** :
   - Mode sombre/clair
   - Raccourcis clavier
   - Interface tactile

2. **Fonctionnalités** :
   - Gestion des promotions
   - Fidélité client
   - Inventaire avancé
   - Rapports de vente

3. **Technique** :
   - Migration vers base de données relationnelle
   - API REST pour intégration
   - Application mobile compagnon

### Considérations Techniques

- **Scalabilité** : Migration vers PostgreSQL/MySQL
- **Déploiement** : Containerisation avec Docker
- **Monitoring** : Intégration de métriques et logs
- **Tests** : Couverture de tests plus complète