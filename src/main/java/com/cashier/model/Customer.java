package com.cashier.model;

public class Customer {
    private int id;
    private String name;
    private String phone;
    private String email;
    private int loyaltyPoints;
    private String notes;

    public Customer(int id, String name, String phone, String email, int loyaltyPoints, String notes) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.loyaltyPoints = loyaltyPoints;
        this.notes = notes;
    }

    public Customer(String name, String phone, String email, int loyaltyPoints, String notes) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.loyaltyPoints = loyaltyPoints;
        this.notes = notes;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getLoyaltyPoints() {
        return loyaltyPoints;
    }

    public void setLoyaltyPoints(int loyaltyPoints) {
        this.loyaltyPoints = loyaltyPoints;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @Override
    public String toString() {
        return "Customer{" +
               "id=" + id +
               ", name='" + name + '\'' +
               ", phone='" + phone + '\'' +
               ", email='" + email + '\'' +
               ", loyaltyPoints=" + loyaltyPoints +
               ", notes='" + notes + '\'' +
               '}';
    }
}
