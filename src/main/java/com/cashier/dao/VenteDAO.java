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
        String sql = "INSERT INTO Ventes(date_vente, total, payment_method, amount_paid, change_due, payment_reference, discount_amount) VALUES(?, ?, ?, ?, ?, ?, ?)";
        double venteId = -1;
        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, vente.getDateVente().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            pstmt.setDouble(2, vente.getTotal());
            pstmt.setString(3, vente.getPaymentMethod());
            pstmt.setDouble(4, vente.getAmountPaid());
            pstmt.setDouble(5, vente.getChangeDue());
            pstmt.setString(6, vente.getPaymentReference());
            pstmt.setDouble(7, vente.getDiscountAmount());
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
        String sql = "SELECT id, date_vente, total, payment_method, amount_paid, change_due, payment_reference, discount_amount FROM Ventes ORDER BY date_vente DESC";
        List<Vente> ventes = new ArrayList<>();
        try (Connection conn = Database.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String paymentMethod = rs.getString("payment_method");
                double amountPaid = rs.getDouble("amount_paid");
                double changeDue = rs.getDouble("change_due");
                String paymentReference = rs.getString("payment_reference");
                double discountAmount = rs.getDouble("discount_amount");
                
                Vente vente = new Vente(
                    rs.getInt("id"),
                    LocalDateTime.parse(rs.getString("date_vente"), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                    rs.getDouble("total"),
                    paymentMethod,
                    amountPaid,
                    changeDue,
                    paymentReference,
                    discountAmount
                );
                ventes.add(vente);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return ventes;
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


