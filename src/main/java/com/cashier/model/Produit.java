package com.cashier.model;

public class Produit {
    private int id;
    private String nom;
    private double prix;
    private double quantite;
    private String codeBarres;
    private String type; // 'unit' or 'weight'

    public Produit(int id, String nom, double prix, double quantite, String codeBarres, String type) {
        this.id = id;
        this.nom = nom;
        this.prix = prix;
        this.quantite = quantite;
        this.codeBarres = codeBarres;
        this.type = type;
    }

    public Produit(String nom, double prix, double quantite, String codeBarres, String type) {
        this.nom = nom;
        this.prix = prix;
        this.quantite = quantite;
        this.codeBarres = codeBarres;
        this.type = type;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public double getPrix() {
        return prix;
    }

    public void setPrix(double prix) {
        this.prix = prix;
    }

    public double getQuantite() {
        return quantite;
    }

    public void setQuantite(double quantite) {
        this.quantite = quantite;
    }

    public String getCodeBarres() {
        return codeBarres;
    }

    public void setCodeBarres(String codeBarres) {
        this.codeBarres = codeBarres;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Produit{" +
               "id=" + id +
               ", nom=\'" + nom + '\'' +
               ", prix=" + prix +
               ", quantite=" + quantite +
               ", codeBarres=\'" + codeBarres + '\'' +
               ", type=\'" + type + '\'' +
               '}' + "\n";
    }
}


