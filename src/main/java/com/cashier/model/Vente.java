package com.cashier.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Vente {
    private int id;
    private LocalDateTime dateVente;
    private double total;
    private Integer userId;

    public Vente(int id, LocalDateTime dateVente, double total) {
        this(id, dateVente, total, null);
    }

    public Vente(int id, LocalDateTime dateVente, double total, Integer userId) {
        this.id = id;
        this.dateVente = dateVente;
        this.total = total;
        this.userId = userId;
    }

    public Vente(LocalDateTime dateVente, double total) {
        this(dateVente, total, null);
    }

    public Vente(LocalDateTime dateVente, double total, Integer userId) {
        this.dateVente = dateVente;
        this.total = total;
        this.userId = userId;
    }

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

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return "Vente{" +
               "id=" + id +
               ", dateVente=\"" + dateVente.format(formatter) + "\"" +
               ", total=" + total +
               ", userId=" + userId +
               "}\n";
    }
}
