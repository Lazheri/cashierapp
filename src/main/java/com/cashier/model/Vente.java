
package com.cashier.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Vente {
    private int id;
    private LocalDateTime dateVente;
    private double total;

    public Vente(int id, LocalDateTime dateVente, double total) {
        this.id = id;
        this.dateVente = dateVente;
        this.total = total;
    }

    public Vente(LocalDateTime dateVente, double total) {
        this.dateVente = dateVente;
        this.total = total;
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

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return "Vente{" +
               "id=" + id +
               ", dateVente=\"" + dateVente.format(formatter) + "\"" +
               ", total=" + total +
               "}\n";
    }
}


