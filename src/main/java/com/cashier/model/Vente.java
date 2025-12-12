
package com.cashier.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Vente {
    private int id;
    private LocalDateTime dateVente;
    private double total;
    private Integer customerId;
    private int loyaltyPointsUsed;

    public Vente(int id, LocalDateTime dateVente, double total) {
        this.id = id;
        this.dateVente = dateVente;
        this.total = total;
        this.customerId = null;
        this.loyaltyPointsUsed = 0;
    }

    public Vente(int id, LocalDateTime dateVente, double total, Integer customerId, int loyaltyPointsUsed) {
        this.id = id;
        this.dateVente = dateVente;
        this.total = total;
        this.customerId = customerId;
        this.loyaltyPointsUsed = loyaltyPointsUsed;
    }

    public Vente(LocalDateTime dateVente, double total) {
        this.dateVente = dateVente;
        this.total = total;
        this.customerId = null;
        this.loyaltyPointsUsed = 0;
    }

    public Vente(LocalDateTime dateVente, double total, Integer customerId, int loyaltyPointsUsed) {
        this.dateVente = dateVente;
        this.total = total;
        this.customerId = customerId;
        this.loyaltyPointsUsed = loyaltyPointsUsed;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDateTime getDateVente() {
        return dateVente;
    }

    public void setDateVente(LocalDateTime dateVente) {
        this.dateVente = dateVente;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    public int getLoyaltyPointsUsed() {
        return loyaltyPointsUsed;
    }

    public void setLoyaltyPointsUsed(int loyaltyPointsUsed) {
        this.loyaltyPointsUsed = loyaltyPointsUsed;
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return "Vente{" +
               "id=" + id +
               ", dateVente=\"" + dateVente.format(formatter) + "\"" +
               ", total=" + total +
               ", customerId=" + customerId +
               ", loyaltyPointsUsed=" + loyaltyPointsUsed +
               "}\n";
    }
}


