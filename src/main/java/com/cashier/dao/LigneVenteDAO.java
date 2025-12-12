package com.cashier.dao;

import com.cashier.Database;
import com.cashier.model.LigneVente;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LigneVenteDAO {

    public void addLigneVente(LigneVente ligneVente) {
        String sql = "INSERT INTO LignesVente(vente_id, produit_id, quantite, prix_unitaire, line_discount) VALUES(?,?,?,?,?)";
        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, ligneVente.getVenteId());
            pstmt.setInt(2, ligneVente.getProduitId());
            pstmt.setDouble(3, ligneVente.getQuantite()); // Changed to setDouble
            pstmt.setDouble(4, ligneVente.getPrixUnitaire());
            pstmt.setDouble(5, ligneVente.getLineDiscount());
            pstmt.executeUpdate();
            System.out.println("LigneVente added for Vente ID: " + ligneVente.getVenteId());
        } catch (SQLException e) {
            System.err.println("Error adding ligneVente: " + e.getMessage());
        }
    }

    public List<LigneVente> getLignesVenteByVenteId(int venteId) {
        List<LigneVente> lignesVente = new ArrayList<>();
        String sql = "SELECT id, vente_id, produit_id, quantite, prix_unitaire, line_discount FROM LignesVente WHERE vente_id = ?";
        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, venteId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                lignesVente.add(new LigneVente(
                    rs.getInt("id"),
                    rs.getInt("vente_id"),
                    rs.getInt("produit_id"),
                    rs.getDouble("quantite"), // Changed to getDouble
                    rs.getDouble("prix_unitaire"),
                    rs.getDouble("line_discount")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error getting lignesVente by vente id: " + e.getMessage());
        }
        return lignesVente;
    }

    public void deleteLignesVenteByVenteId(int venteId) {
        String sql = "DELETE FROM LignesVente WHERE vente_id = ?";
        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, venteId);
            pstmt.executeUpdate();
            System.out.println("LignesVente deleted for Vente ID: " + venteId);
        } catch (SQLException e) {
            System.err.println("Error deleting lignesVente: " + e.getMessage());
        }
    }
}


