package com.cashier.dao;

import com.cashier.Database;
import com.cashier.model.User;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class UserDAO {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public User getUserByUsername(String username) {
        String sql = "SELECT id, username, password_hash, role, status, created_at FROM Users WHERE username = ?";
        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToUser(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting user by username: " + e.getMessage());
        }
        return null;
    }

    public User authenticate(String username, String plainPassword) {
        User user = getUserByUsername(username);
        if (user == null) {
            return null;
        }

        if (user.getStatus() == null || !"ACTIVE".equalsIgnoreCase(user.getStatus())) {
            return null;
        }

        if (user.getPasswordHash() == null || !BCrypt.checkpw(plainPassword, user.getPasswordHash())) {
            return null;
        }

        return user;
    }

    public int addUser(User user) {
        String sql = "INSERT INTO Users(username, password_hash, role, status, created_at) VALUES(?, ?, ?, ?, ?)";
        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPasswordHash());
            pstmt.setString(3, user.getRole());
            pstmt.setString(4, user.getStatus() == null ? "ACTIVE" : user.getStatus());
            pstmt.setString(5, (user.getCreatedAt() == null ? LocalDateTime.now() : user.getCreatedAt()).format(DATE_TIME_FORMATTER));
            pstmt.executeUpdate();

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error adding user: " + e.getMessage());
        }
        return -1;
    }

    private static User mapRowToUser(ResultSet rs) throws SQLException {
        LocalDateTime createdAt = null;
        String createdAtRaw = rs.getString("created_at");
        if (createdAtRaw != null && !createdAtRaw.isBlank()) {
            createdAt = LocalDateTime.parse(createdAtRaw, DATE_TIME_FORMATTER);
        }

        return new User(
            rs.getInt("id"),
            rs.getString("username"),
            rs.getString("password_hash"),
            rs.getString("role"),
            rs.getString("status"),
            createdAt
        );
    }
}
