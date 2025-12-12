package com.cashier;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {

    private static final String URL = "jdbc:sqlite:cashier.db";

    public static Connection connect() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(URL);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }

    public static void createNewDatabase() {
        try (Connection conn = connect()) {
            if (conn != null) {
                System.out.println("A new database has been created.");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void createTables() {
        String sqlProduits = "CREATE TABLE IF NOT EXISTS Produits (\n" +
                             " id INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                             " nom TEXT NOT NULL,\n" +
                             " prix REAL NOT NULL,\n" +
                             " quantite REAL NOT NULL,\n" +
                             " code_barres TEXT,\n" +
                             " type TEXT,\n" +
                             " categorie TEXT\n" +
                             ");";

        String sqlVentes = "CREATE TABLE IF NOT EXISTS Ventes (\n" +
                           " id INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                           " date_vente TEXT NOT NULL,\n" +
                           " total REAL NOT NULL\n" +
                           ");";

        String sqlLignesVente = "CREATE TABLE IF NOT EXISTS LignesVente (\n" +
                                " id INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                                " vente_id INTEGER NOT NULL,\n" +
                                " produit_id INTEGER NOT NULL,\n" +
                                " quantite REAL NOT NULL,\n" +
                                " prix_unitaire REAL NOT NULL,\n" +
                                " FOREIGN KEY (vente_id) REFERENCES Ventes(id),\n" +
                                " FOREIGN KEY (produit_id) REFERENCES Produits(id)\n" +
                                ");";

        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sqlProduits);
            stmt.execute(sqlVentes);
            stmt.execute(sqlLignesVente);

            // Attempt to add 'categorie' column for existing databases
            try {
                stmt.execute("ALTER TABLE Produits ADD COLUMN categorie TEXT");
                System.out.println("Column 'categorie' added to Produits table.");
            } catch (SQLException e) {
                // Ignore error if column already exists (Duplicate column name)
                if (!e.getMessage().contains("duplicate column name")) {
                     // In some sqlite versions/drivers the message might be different, but usually it complains about duplicate column
                     // System.out.println("Note: " + e.getMessage()); 
                }
            }

            System.out.println("Tables created or already exist.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}


