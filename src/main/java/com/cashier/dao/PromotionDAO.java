package com.cashier.dao;

import com.cashier.Database;
import com.cashier.model.Promotion;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class PromotionDAO {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public double addPromotion(Promotion promotion) {
        String sql = "INSERT INTO Promotions(code, description, type, value, valid_from, valid_to, usage_cap, current_usage, minimum_basket, active) " +
                     "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        double promotionId = -1;
        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, promotion.getCode());
            pstmt.setString(2, promotion.getDescription());
            pstmt.setString(3, promotion.getType());
            pstmt.setDouble(4, promotion.getValue());
            pstmt.setString(5, promotion.getValidFrom() != null ? promotion.getValidFrom().format(DATE_FORMATTER) : null);
            pstmt.setString(6, promotion.getValidTo() != null ? promotion.getValidTo().format(DATE_FORMATTER) : null);
            pstmt.setObject(7, promotion.getUsageCap());
            pstmt.setInt(8, promotion.getCurrentUsage());
            pstmt.setDouble(9, promotion.getMinimumBasket());
            pstmt.setInt(10, promotion.isActive() ? 1 : 0);
            pstmt.executeUpdate();

            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                promotionId = rs.getDouble(1);
            }
        } catch (SQLException e) {
            System.out.println("Error adding promotion: " + e.getMessage());
        }
        return promotionId;
    }

    public Promotion getPromotionByCode(String code) {
        String sql = "SELECT id, code, description, type, value, valid_from, valid_to, usage_cap, current_usage, minimum_basket, active FROM Promotions WHERE code = ?";
        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, code);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return mapRowToPromotion(rs);
            }
        } catch (SQLException e) {
            System.out.println("Error getting promotion by code: " + e.getMessage());
        }
        return null;
    }

    public Promotion getPromotionById(int id) {
        String sql = "SELECT id, code, description, type, value, valid_from, valid_to, usage_cap, current_usage, minimum_basket, active FROM Promotions WHERE id = ?";
        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return mapRowToPromotion(rs);
            }
        } catch (SQLException e) {
            System.out.println("Error getting promotion by id: " + e.getMessage());
        }
        return null;
    }

    public List<Promotion> getAllPromotions() {
        String sql = "SELECT id, code, description, type, value, valid_from, valid_to, usage_cap, current_usage, minimum_basket, active FROM Promotions ORDER BY code";
        List<Promotion> promotions = new ArrayList<>();
        try (Connection conn = Database.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                promotions.add(mapRowToPromotion(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error getting all promotions: " + e.getMessage());
        }
        return promotions;
    }

    public List<Promotion> getActivePromotions() {
        String sql = "SELECT id, code, description, type, value, valid_from, valid_to, usage_cap, current_usage, minimum_basket, active FROM Promotions WHERE active = 1 ORDER BY code";
        List<Promotion> promotions = new ArrayList<>();
        try (Connection conn = Database.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                promotions.add(mapRowToPromotion(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error getting active promotions: " + e.getMessage());
        }
        return promotions;
    }

    public void updatePromotion(Promotion promotion) {
        String sql = "UPDATE Promotions SET code = ?, description = ?, type = ?, value = ?, valid_from = ?, valid_to = ?, usage_cap = ?, current_usage = ?, minimum_basket = ?, active = ? WHERE id = ?";
        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, promotion.getCode());
            pstmt.setString(2, promotion.getDescription());
            pstmt.setString(3, promotion.getType());
            pstmt.setDouble(4, promotion.getValue());
            pstmt.setString(5, promotion.getValidFrom() != null ? promotion.getValidFrom().format(DATE_FORMATTER) : null);
            pstmt.setString(6, promotion.getValidTo() != null ? promotion.getValidTo().format(DATE_FORMATTER) : null);
            pstmt.setObject(7, promotion.getUsageCap());
            pstmt.setInt(8, promotion.getCurrentUsage());
            pstmt.setDouble(9, promotion.getMinimumBasket());
            pstmt.setInt(10, promotion.isActive() ? 1 : 0);
            pstmt.setInt(11, promotion.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error updating promotion: " + e.getMessage());
        }
    }

    public void deletePromotion(int id) {
        String sql = "DELETE FROM Promotions WHERE id = ?";
        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error deleting promotion: " + e.getMessage());
        }
    }

    public void incrementUsage(int promotionId) {
        String sql = "UPDATE Promotions SET current_usage = current_usage + 1 WHERE id = ?";
        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, promotionId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error incrementing promotion usage: " + e.getMessage());
        }
    }

    private Promotion mapRowToPromotion(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String code = rs.getString("code");
        String description = rs.getString("description");
        String type = rs.getString("type");
        double value = rs.getDouble("value");
        String validFromStr = rs.getString("valid_from");
        String validToStr = rs.getString("valid_to");
        Integer usageCap = rs.getObject("usage_cap") != null ? rs.getInt("usage_cap") : null;
        int currentUsage = rs.getInt("current_usage");
        double minimumBasket = rs.getDouble("minimum_basket");
        boolean active = rs.getInt("active") == 1;

        LocalDateTime validFrom = validFromStr != null ? LocalDateTime.parse(validFromStr, DATE_FORMATTER) : null;
        LocalDateTime validTo = validToStr != null ? LocalDateTime.parse(validToStr, DATE_FORMATTER) : null;

        return new Promotion(id, code, description, type, value, validFrom, validTo, usageCap, currentUsage, minimumBasket, active);
    }
}
