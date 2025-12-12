package com.cashier.model;

public class LigneVente {
    private int id;
    private int venteId;
    private int produitId;
    private double quantite; // Changed to double
    private double prixUnitaire;
    private double lineDiscount;

    public LigneVente(int id, int venteId, int produitId, double quantite, double prixUnitaire) {
        this.id = id;
        this.venteId = venteId;
        this.produitId = produitId;
        this.quantite = quantite;
        this.prixUnitaire = prixUnitaire;
        this.lineDiscount = 0;
    }

    public LigneVente(int id, int venteId, int produitId, double quantite, double prixUnitaire, double lineDiscount) {
        this.id = id;
        this.venteId = venteId;
        this.produitId = produitId;
        this.quantite = quantite;
        this.prixUnitaire = prixUnitaire;
        this.lineDiscount = lineDiscount;
    }

    public LigneVente(int venteId, int produitId, double quantite, double prixUnitaire) {
        this.venteId = venteId;
        this.produitId = produitId;
        this.quantite = quantite;
        this.prixUnitaire = prixUnitaire;
        this.lineDiscount = 0;
    }

    public LigneVente(int venteId, int produitId, double quantite, double prixUnitaire, double lineDiscount) {
        this.venteId = venteId;
        this.produitId = produitId;
        this.quantite = quantite;
        this.prixUnitaire = prixUnitaire;
        this.lineDiscount = lineDiscount;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getVenteId() {
        return venteId;
    }

    public void setVenteId(int venteId) {
        this.venteId = venteId;
    }

    public int getProduitId() {
        return produitId;
    }

    public void setProduitId(int produitId) {
        this.produitId = produitId;
    }

    public double getQuantite() { // Changed to double
        return quantite;
    }

    public void setQuantite(double quantite) { // Changed to double
        this.quantite = quantite;
    }

    public double getPrixUnitaire() {
        return prixUnitaire;
    }

    public void setPrixUnitaire(double prixUnitaire) {
        this.prixUnitaire = prixUnitaire;
    }

    public double getLineDiscount() {
        return lineDiscount;
    }

    public void setLineDiscount(double lineDiscount) {
        this.lineDiscount = lineDiscount;
    }

    @Override
    public String toString() {
        return "LigneVente{" +
                "id=" + id +
                ", venteId=" + venteId +
                ", produitId=" + produitId +
                ", quantite=" + quantite +
                ", prixUnitaire=" + prixUnitaire +
                ", lineDiscount=" + lineDiscount +
                "}\n";
    }
    }


