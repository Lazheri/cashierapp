
package com.cashier.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Vente {
    private int id;
    private LocalDateTime dateVente;
    private double total;
    private String paymentMethod;
    private double amountPaid;
    private double changeDue;
    private String paymentReference;
    private double discountAmount;

    public Vente(int id, LocalDateTime dateVente, double total) {
        this.id = id;
        this.dateVente = dateVente;
        this.total = total;
        this.discountAmount = 0;
    }

    public Vente(LocalDateTime dateVente, double total) {
        this.dateVente = dateVente;
        this.total = total;
        this.discountAmount = 0;
    }

    public Vente(int id, LocalDateTime dateVente, double total, String paymentMethod, double amountPaid, double changeDue, String paymentReference) {
        this.id = id;
        this.dateVente = dateVente;
        this.total = total;
        this.paymentMethod = paymentMethod;
        this.amountPaid = amountPaid;
        this.changeDue = changeDue;
        this.paymentReference = paymentReference;
        this.discountAmount = 0;
    }

    public Vente(int id, LocalDateTime dateVente, double total, String paymentMethod, double amountPaid, double changeDue, String paymentReference, double discountAmount) {
        this.id = id;
        this.dateVente = dateVente;
        this.total = total;
        this.paymentMethod = paymentMethod;
        this.amountPaid = amountPaid;
        this.changeDue = changeDue;
        this.paymentReference = paymentReference;
        this.discountAmount = discountAmount;
    }

    public Vente(LocalDateTime dateVente, double total, String paymentMethod, double amountPaid, double changeDue, String paymentReference) {
        this.dateVente = dateVente;
        this.total = total;
        this.paymentMethod = paymentMethod;
        this.amountPaid = amountPaid;
        this.changeDue = changeDue;
        this.paymentReference = paymentReference;
        this.discountAmount = 0;
    }

    public Vente(LocalDateTime dateVente, double total, String paymentMethod, double amountPaid, double changeDue, String paymentReference, double discountAmount) {
        this.dateVente = dateVente;
        this.total = total;
        this.paymentMethod = paymentMethod;
        this.amountPaid = amountPaid;
        this.changeDue = changeDue;
        this.paymentReference = paymentReference;
        this.discountAmount = discountAmount;
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

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public double getAmountPaid() {
        return amountPaid;
    }

    public void setAmountPaid(double amountPaid) {
        this.amountPaid = amountPaid;
    }

    public double getChangeDue() {
        return changeDue;
    }

    public void setChangeDue(double changeDue) {
        this.changeDue = changeDue;
    }

    public String getPaymentReference() {
        return paymentReference;
    }

    public void setPaymentReference(String paymentReference) {
        this.paymentReference = paymentReference;
    }

    public double getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(double discountAmount) {
        this.discountAmount = discountAmount;
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return "Vente{" +
                "id=" + id +
                ", dateVente=\"" + dateVente.format(formatter) + "\"" +
                ", total=" + total +
                ", paymentMethod='" + paymentMethod + '\'' +
                ", amountPaid=" + amountPaid +
                ", changeDue=" + changeDue +
                ", paymentReference='" + paymentReference + '\'' +
                ", discountAmount=" + discountAmount +
                "}\n";
    }
    }


