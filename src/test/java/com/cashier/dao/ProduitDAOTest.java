package com.cashier.dao;

import com.cashier.Database;
import com.cashier.model.Produit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ProduitDAOTest {

    private ProduitDAO produitDAO;

    @BeforeEach
    public void setUp() {
        // Create tables for testing
        Database.createTables();
        produitDAO = new ProduitDAO();
    }

    @Test
    public void testAddProduit() {
        Produit produit = new Produit("Test Product", 10.50, 20.0, "TEST123", "unit");
        produitDAO.addProduit(produit);

        Produit retrieved = produitDAO.getProduitByCodeBarres("TEST123");
        assertNotNull(retrieved);
        assertEquals("Test Product", retrieved.getNom());
        assertEquals(10.50, retrieved.getPrix(), 0.01);
        assertEquals(20.0, retrieved.getQuantite(), 0.01);
        assertEquals("TEST123", retrieved.getCodeBarres());
    }

    @Test
    public void testGetProduitById() {
        Produit produit = new Produit("Test Product 2", 15.75, 30.0, "TEST456", "unit");
        produitDAO.addProduit(produit);

        List<Produit> allProducts = produitDAO.getAllProduits();
        assertFalse(allProducts.isEmpty());

        Produit firstProduct = allProducts.get(allProducts.size() - 1); // Get the last added
        Produit retrieved = produitDAO.getProduitById(firstProduct.getId());
        
        assertNotNull(retrieved);
        assertEquals("Test Product 2", retrieved.getNom());
    }

    @Test
    public void testUpdateProduit() {
        Produit produit = new Produit("Original Name", 5.00, 10.0, "UPDATE123", "unit");
        produitDAO.addProduit(produit);

        Produit retrieved = produitDAO.getProduitByCodeBarres("UPDATE123");
        assertNotNull(retrieved);

        retrieved.setNom("Updated Name");
        retrieved.setPrix(7.50);
        retrieved.setQuantite(15.0);
        produitDAO.updateProduit(retrieved);

        Produit updated = produitDAO.getProduitById(retrieved.getId());
        assertEquals("Updated Name", updated.getNom());
        assertEquals(7.50, updated.getPrix(), 0.01);
        assertEquals(15.0, updated.getQuantite(), 0.01);
    }

    @Test
    public void testDeleteProduit() {
        Produit produit = new Produit("To Delete", 1.00, 5.0, "DELETE123", "unit");
        produitDAO.addProduit(produit);

        Produit retrieved = produitDAO.getProduitByCodeBarres("DELETE123");
        assertNotNull(retrieved);

        produitDAO.deleteProduit(retrieved.getId());

        Produit deleted = produitDAO.getProduitById(retrieved.getId());
        assertNull(deleted);
    }

    @Test
    public void testGetAllProduits() {
        int initialCount = produitDAO.getAllProduits().size();

        produitDAO.addProduit(new Produit("Product 1", 1.00, 10.0, "ALL1", "unit"));
        produitDAO.addProduit(new Produit("Product 2", 2.00, 20.0, "ALL2", "unit"));

        List<Produit> allProducts = produitDAO.getAllProduits();
        assertEquals(initialCount + 2, allProducts.size());
    }
}


