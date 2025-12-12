package com.cashier.dao;

import com.cashier.Database;
import com.cashier.model.Vente;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class VenteDAO {

    public double addVente(Vente vente) {
        String sql = "INSERT INTO Ventes(date_vente, total, customer_id, loyalty_points_used) VALUES(?, ?, ?, ?)";
        double venteId = -1;
        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, vente.getDateVente().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            pstmt.setDouble(2, vente.getTotal());
            if (vente.getCustomerId() != null) {
                pstmt.setInt(3, vente.getCustomerId());
            } else {
                pstmt.setNull(3, java.sql.Types.INTEGER);
            }
            pstmt.setInt(4, vente.getLoyaltyPointsUsed());
            pstmt.executeUpdate();

            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                venteId = rs.getDouble(1);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return venteId;
    }

    public List<Vente> getAllVentes() {
        String sql = "SELECT id, date_vente, total, customer_id, loyalty_points_used FROM Ventes ORDER BY date_vente DESC";
        List<Vente> ventes = new ArrayList<>();
        try (Connection conn = Database.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Integer customerId = rs.getObject("customer_id") != null ? rs.getInt("customer_id") : null;
                Vente vente = new Vente(
                    rs.getInt("id"),
                    LocalDateTime.parse(rs.getString("date_vente"), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                    rs.getDouble("total"),
                    customerId,
                    rs.getInt("loyalty_points_used")
                );
                ventes.add(vente);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return ventes;
    }

    public Vente getVenteById(int id) {
        String sql = "SELECT id, date_vente, total, customer_id, loyalty_points_used FROM Ventes WHERE id = ?";
        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                Integer customerId = rs.getObject("customer_id") != null ? rs.getInt("customer_id") : null;
                return new Vente(
                    rs.getInt("id"),
                    LocalDateTime.parse(rs.getString("date_vente"), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                    rs.getDouble("total"),
                    customerId,
                    rs.getInt("loyalty_points_used")
                );
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public void deleteVente(int id) {
        String sql = "DELETE FROM Ventes WHERE id = ?";
        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}


