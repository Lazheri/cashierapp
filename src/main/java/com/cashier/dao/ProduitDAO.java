package com.cashier.dao;

import com.cashier.Database;
import com.cashier.model.Produit;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProduitDAO {

    public void addProduit(Produit produit) {
        String sql = "INSERT INTO Produits(nom, prix, quantite, code_barres, type) VALUES(?,?,?,?,?)";
        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, produit.getNom());
            pstmt.setDouble(2, produit.getPrix());
            pstmt.setDouble(3, produit.getQuantite());
            pstmt.setString(4, produit.getCodeBarres());
            pstmt.setString(5, produit.getType());
            pstmt.executeUpdate();
            System.out.println("Produit added: " + produit.getNom());
        } catch (SQLException e) {
            System.err.println("Error adding produit: " + e.getMessage());
        }
    }

    public Produit getProduitById(int id) {
        String sql = "SELECT id, nom, prix, quantite, code_barres, type FROM Produits WHERE id = ?";
        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Produit(
                    rs.getInt("id"),
                    rs.getString("nom"),
                    rs.getDouble("prix"),
                    rs.getDouble("quantite"),
                    rs.getString("code_barres"),
                    rs.getString("type")
                );
            }
        } catch (SQLException e) {
            System.err.println("Error getting produit by id: " + e.getMessage());
        }
        return null;
    }

    public Produit getProduitByCodeBarres(String codeBarres) {
        String sql = "SELECT id, nom, prix, quantite, code_barres, type FROM Produits WHERE code_barres = ?";
        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, codeBarres);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Produit(
                    rs.getInt("id"),
                    rs.getString("nom"),
                    rs.getDouble("prix"),
                    rs.getDouble("quantite"),
                    rs.getString("code_barres"),
                    rs.getString("type")
                );
            }
        } catch (SQLException e) {
            System.err.println("Error getting produit by code barres: " + e.getMessage());
        }
        return null;
    }

    public List<Produit> getAllProduits() {
        List<Produit> produits = new ArrayList<>();
        String sql = "SELECT id, nom, prix, quantite, code_barres, type FROM Produits";
        try (Connection conn = Database.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                produits.add(new Produit(
                    rs.getInt("id"),
                    rs.getString("nom"),
                    rs.getDouble("prix"),
                    rs.getDouble("quantite"),
                    rs.getString("code_barres"),
                    rs.getString("type")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error getting all produits: " + e.getMessage());
        }
        return produits;
    }

    public void updateProduit(Produit produit) {
        String sql = "UPDATE Produits SET nom = ?, prix = ?, quantite = ?, code_barres = ?, type = ? WHERE id = ?";
        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, produit.getNom());
            pstmt.setDouble(2, produit.getPrix());
            pstmt.setDouble(3, produit.getQuantite());
            pstmt.setString(4, produit.getCodeBarres());
            pstmt.setString(5, produit.getType());
            pstmt.setInt(6, produit.getId());
            pstmt.executeUpdate();
            System.out.println("Produit updated: " + produit.getNom());
        } catch (SQLException e) {
            System.err.println("Error updating produit: " + e.getMessage());
        }
    }

    public void deleteProduit(int id) {
        String sql = "DELETE FROM Produits WHERE id = ?";
        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            System.out.println("Produit deleted with id: " + id);
        } catch (SQLException e) {
            System.err.println("Error deleting produit: " + e.getMessage());
        }
    }

    public void deleteAllProduits() {
        String sql = "DELETE FROM Produits";
        try (Connection conn = Database.connect();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
            System.out.println("All products deleted.");
        } catch (SQLException e) {
            System.err.println("Error deleting all products: " + e.getMessage());
        }
    }
}


