package com.cashier;

import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Database {

    private static final String DEFAULT_URL = "jdbc:sqlite:cashier.db";
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static String getUrl() {
        return System.getProperty("cashier.db.url", DEFAULT_URL);
    }

    public static Connection connect() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(getUrl());
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
        String sqlUsers = "CREATE TABLE IF NOT EXISTS Users (\n" +
                          " id INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                          " username TEXT NOT NULL UNIQUE,\n" +
                          " password_hash TEXT NOT NULL,\n" +
                          " role TEXT NOT NULL,\n" +
                          " status TEXT NOT NULL,\n" +
                          " created_at TEXT NOT NULL\n" +
                          ");";

        String sqlProduits = "CREATE TABLE IF NOT EXISTS Produits (\n" +
                             " id INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                             " nom TEXT NOT NULL,\n" +
                             " prix REAL NOT NULL,\n" +
                             " quantite REAL NOT NULL,\n" +
                             " code_barres TEXT,\n" +
                             " type TEXT\n" +
                             ");";

        String sqlVentes = "CREATE TABLE IF NOT EXISTS Ventes (\n" +
                           " id INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                           " date_vente TEXT NOT NULL,\n" +
                           " total REAL NOT NULL,\n" +
                           " user_id INTEGER,\n" +
                           " FOREIGN KEY (user_id) REFERENCES Users(id)\n" +
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
            stmt.execute("PRAGMA foreign_keys = ON");

            stmt.execute(sqlUsers);
            stmt.execute(sqlProduits);
            stmt.execute(sqlVentes);
            stmt.execute(sqlLignesVente);

            ensureColumnExists(conn, "Ventes", "user_id", "INTEGER");
            seedDefaultUsers(conn);

            System.out.println("Tables created or already exist.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void ensureColumnExists(Connection conn, String tableName, String columnName, String columnDefinition) throws SQLException {
        if (columnExists(conn, tableName, columnName)) {
            return;
        }

        try (Statement stmt = conn.createStatement()) {
            stmt.execute("ALTER TABLE " + tableName + " ADD COLUMN " + columnName + " " + columnDefinition);
        }
    }

    private static boolean columnExists(Connection conn, String tableName, String columnName) throws SQLException {
        String sql = "PRAGMA table_info(" + tableName + ")";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String name = rs.getString("name");
                if (columnName.equalsIgnoreCase(name)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static void seedDefaultUsers(Connection conn) throws SQLException {
        insertUserIfMissing(conn, "admin", "admin", "ADMIN");
        insertUserIfMissing(conn, "cashier", "cashier", "CASHIER");
    }

    private static void insertUserIfMissing(Connection conn, String username, String plainPassword, String role) throws SQLException {
        String sql = "INSERT OR IGNORE INTO Users(username, password_hash, role, status, created_at) VALUES(?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, BCrypt.hashpw(plainPassword, BCrypt.gensalt(12)));
            pstmt.setString(3, role);
            pstmt.setString(4, "ACTIVE");
            pstmt.setString(5, LocalDateTime.now().format(DATE_TIME_FORMATTER));
            pstmt.executeUpdate();
        }
    }
}
