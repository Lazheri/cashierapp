package com.cashier.dao;

import com.cashier.Database;
import com.cashier.model.Customer;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerDAO {

    public void addCustomer(Customer customer) {
        String sql = "INSERT INTO Customers(name, phone, email, loyalty_points, notes) VALUES(?,?,?,?,?)";
        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, customer.getName());
            pstmt.setString(2, customer.getPhone());
            pstmt.setString(3, customer.getEmail());
            pstmt.setInt(4, customer.getLoyaltyPoints());
            pstmt.setString(5, customer.getNotes());
            pstmt.executeUpdate();
            System.out.println("Customer added: " + customer.getName());
        } catch (SQLException e) {
            System.err.println("Error adding customer: " + e.getMessage());
        }
    }

    public Customer getCustomerById(int id) {
        String sql = "SELECT id, name, phone, email, loyalty_points, notes FROM Customers WHERE id = ?";
        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Customer(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("phone"),
                    rs.getString("email"),
                    rs.getInt("loyalty_points"),
                    rs.getString("notes")
                );
            }
        } catch (SQLException e) {
            System.err.println("Error getting customer by id: " + e.getMessage());
        }
        return null;
    }

    public Customer getCustomerByPhone(String phone) {
        String sql = "SELECT id, name, phone, email, loyalty_points, notes FROM Customers WHERE phone = ?";
        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, phone);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Customer(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("phone"),
                    rs.getString("email"),
                    rs.getInt("loyalty_points"),
                    rs.getString("notes")
                );
            }
        } catch (SQLException e) {
            System.err.println("Error getting customer by phone: " + e.getMessage());
        }
        return null;
    }

    public List<Customer> getAllCustomers() {
        List<Customer> customers = new ArrayList<>();
        String sql = "SELECT id, name, phone, email, loyalty_points, notes FROM Customers ORDER BY name";
        try (Connection conn = Database.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                customers.add(new Customer(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("phone"),
                    rs.getString("email"),
                    rs.getInt("loyalty_points"),
                    rs.getString("notes")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error getting all customers: " + e.getMessage());
        }
        return customers;
    }

    public List<Customer> searchCustomers(String query) {
        List<Customer> customers = new ArrayList<>();
        String sql = "SELECT id, name, phone, email, loyalty_points, notes FROM Customers " +
                     "WHERE name LIKE ? OR phone LIKE ? ORDER BY name";
        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            String searchTerm = "%" + query + "%";
            pstmt.setString(1, searchTerm);
            pstmt.setString(2, searchTerm);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                customers.add(new Customer(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("phone"),
                    rs.getString("email"),
                    rs.getInt("loyalty_points"),
                    rs.getString("notes")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error searching customers: " + e.getMessage());
        }
        return customers;
    }

    public void updateCustomer(Customer customer) {
        String sql = "UPDATE Customers SET name = ?, phone = ?, email = ?, loyalty_points = ?, notes = ? WHERE id = ?";
        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, customer.getName());
            pstmt.setString(2, customer.getPhone());
            pstmt.setString(3, customer.getEmail());
            pstmt.setInt(4, customer.getLoyaltyPoints());
            pstmt.setString(5, customer.getNotes());
            pstmt.setInt(6, customer.getId());
            pstmt.executeUpdate();
            System.out.println("Customer updated: " + customer.getName());
        } catch (SQLException e) {
            System.err.println("Error updating customer: " + e.getMessage());
        }
    }

    public void deleteCustomer(int id) {
        String sql = "DELETE FROM Customers WHERE id = ?";
        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            System.out.println("Customer deleted with id: " + id);
        } catch (SQLException e) {
            System.err.println("Error deleting customer: " + e.getMessage());
        }
    }

    public List<Customer> getTopCustomers(int limit) {
        List<Customer> customers = new ArrayList<>();
        String sql = "SELECT c.id, c.name, c.phone, c.email, c.loyalty_points, c.notes, " +
                     "COALESCE(SUM(v.total), 0) as total_spent " +
                     "FROM Customers c LEFT JOIN Ventes v ON c.id = v.customer_id " +
                     "GROUP BY c.id ORDER BY total_spent DESC LIMIT ?";
        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, limit);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                customers.add(new Customer(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("phone"),
                    rs.getString("email"),
                    rs.getInt("loyalty_points"),
                    rs.getString("notes")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error getting top customers: " + e.getMessage());
        }
        return customers;
    }

    public List<Integer> getPurchaseHistoryIds(int customerId) {
        List<Integer> saleIds = new ArrayList<>();
        String sql = "SELECT id FROM Ventes WHERE customer_id = ? ORDER BY date_vente DESC";
        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, customerId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                saleIds.add(rs.getInt("id"));
            }
        } catch (SQLException e) {
            System.err.println("Error getting purchase history: " + e.getMessage());
        }
        return saleIds;
    }

    public double getTotalSpentByCustomer(int customerId) {
        String sql = "SELECT COALESCE(SUM(total), 0) as total FROM Ventes WHERE customer_id = ?";
        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, customerId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble("total");
            }
        } catch (SQLException e) {
            System.err.println("Error getting total spent: " + e.getMessage());
        }
        return 0.0;
    }
}
