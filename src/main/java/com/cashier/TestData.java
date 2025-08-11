package com.cashier;

import com.cashier.dao.ProduitDAO;
import com.cashier.model.Produit;

public class TestData {

    public static void main(String[] args) {
        // Initialize database
        Database.createTables();

        ProduitDAO produitDAO = new ProduitDAO();

        // Clear existing products to avoid duplicates during testing
        produitDAO.deleteAllProduits(); // Add this line to clear existing products

        // Add some Tunisian test products (prices in TND)
        produitDAO.addProduit(new Produit("Baguette", 0.230, 100.0, "001", "unit"));
        produitDAO.addProduit(new Produit("Lait (1L)", 1.400, 50.0, "002", "unit"));
        produitDAO.addProduit(new Produit("Huile d'olive (1L)", 15.000, 20.0, "003", "unit"));
        produitDAO.addProduit(new Produit("Thon en conserve", 3.500, 40.0, "004", "unit"));
        produitDAO.addProduit(new Produit("Harissa (petit pot)", 1.200, 60.0, "005", "unit"));
        produitDAO.addProduit(new Produit("Dattes (500g)", 4.800, 30.0, "006", "weight"));
        produitDAO.addProduit(new Produit("Eau Minérale (1.5L)", 0.700, 120.0, "007", "unit"));
        produitDAO.addProduit(new Produit("Café Turc (250g)", 5.000, 35.0, "008", "unit"));
        produitDAO.addProduit(new Produit("Couscous (1kg)", 2.100, 45.0, "009", "weight"));
        produitDAO.addProduit(new Produit("Fromage Blanc (250g)", 2.800, 25.0, "010", "unit"));
        produitDAO.addProduit(new Produit("Tomates", 1.500, 50.0, "011", "weight"));
        produitDAO.addProduit(new Produit("Concombres", 1.000, 40.0, "012", "weight"));

        System.out.println("Données de test tunisiennes ajoutées avec succès!");
        System.out.println("Produits dans la base de données:");
        produitDAO.getAllProduits().forEach(System.out::print);
    }
}


