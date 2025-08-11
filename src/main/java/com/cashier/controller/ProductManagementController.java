package com.cashier.controller;

import com.cashier.dao.ProduitDAO;
import com.cashier.model.Produit;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.ResourceBundle;

public class ProductManagementController implements Initializable {

    @FXML private TableView<Produit> productsTable;
    @FXML private TableColumn<Produit, Integer> idColumn;
    @FXML private TableColumn<Produit, String> nameColumn;
    @FXML private TableColumn<Produit, Double> priceColumn;
    @FXML private TableColumn<Produit, Double> stockColumn; // Changed to Double
    @FXML private TableColumn<Produit, String> barcodeColumn;
    @FXML private TableColumn<Produit, String> typeColumn; // New column for type
    
    @FXML private TextField nameField;
    @FXML private TextField priceField;
    @FXML private TextField stockField;
    @FXML private TextField barcodeField;
    @FXML private ChoiceBox<String> typeChoiceBox; // ChoiceBox for type
    @FXML private Label messageLabel;

    private ProduitDAO produitDAO;
    private ObservableList<Produit> products;
    private Produit selectedProduct;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        produitDAO = new ProduitDAO();
        products = FXCollections.observableArrayList();

        setupTable();
        setupTypeChoiceBox();
        loadProducts();
        clearMessage();
    }

    private void setupTable() {
        idColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getId()).asObject());
        nameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNom()));
        priceColumn.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getPrix()).asObject());
        stockColumn.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getQuantite()).asObject()); // Changed to Double
        barcodeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCodeBarres()));
        typeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getType())); // New column

        productsTable.setItems(products);

        // Add selection listener
        productsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectedProduct = newSelection;
                populateForm(newSelection);
            }
        });
    }

    private void setupTypeChoiceBox() {
        typeChoiceBox.setItems(FXCollections.observableArrayList("unit", "weight"));
        typeChoiceBox.setValue("unit"); // Default value
    }

    private void loadProducts() {
        products.clear();
        products.addAll(produitDAO.getAllProduits());
    }

    private void populateForm(Produit produit) {
        nameField.setText(produit.getNom());
        priceField.setText(String.valueOf(produit.getPrix()));
        stockField.setText(String.valueOf(produit.getQuantite())); // Changed to double
        barcodeField.setText(produit.getCodeBarres());
        typeChoiceBox.setValue(produit.getType());
    }

    @FXML
    private void saveProduct() {
        try {
            String name = nameField.getText().trim();
            String priceText = priceField.getText().trim();
            String stockText = stockField.getText().trim();
            String barcode = barcodeField.getText().trim();
            String type = typeChoiceBox.getValue();

            if (name.isEmpty() || priceText.isEmpty() || stockText.isEmpty() || type == null) {
                showMessage("Veuillez remplir tous les champs obligatoires.");
                return;
            }

            double price = Double.parseDouble(priceText);
            double stock = Double.parseDouble(stockText); // Changed to double

            if (price < 0 || stock < 0) {
                showMessage("Le prix et le stock doivent être positifs.");
                return;
            }

            if (selectedProduct == null) {
                // Add new product
                Produit newProduct = new Produit(name, price, stock, barcode, type);
                produitDAO.addProduit(newProduct);
                showMessage("Produit ajouté avec succès.");
            } else {
                // Update existing product
                selectedProduct.setNom(name);
                selectedProduct.setPrix(price);
                selectedProduct.setQuantite(stock);
                selectedProduct.setCodeBarres(barcode);
                selectedProduct.setType(type);
                produitDAO.updateProduit(selectedProduct);
                showMessage("Produit mis à jour avec succès.");
            }

            refreshProducts();
            clearForm();

        } catch (NumberFormatException e) {
            showMessage("Veuillez entrer des valeurs numériques valides pour le prix et le stock.");
        } catch (Exception e) {
            showMessage("Erreur: " + e.getMessage());
        }
    }

    @FXML
    private void deleteProduct() {
        Produit selected = productsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showMessage("Veuillez sélectionner un produit à supprimer.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Supprimer le produit");
        alert.setContentText("Êtes-vous sûr de vouloir supprimer le produit \"" + selected.getNom() + "\" ?");

        if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            produitDAO.deleteProduit(selected.getId());
            showMessage("Produit supprimé avec succès.");
            refreshProducts();
            clearForm();
        }
    }

    @FXML
    private void refreshProducts() {
        loadProducts();
        clearMessage();
    }

    @FXML
    private void clearForm() {
        nameField.clear();
        priceField.clear();
        stockField.clear();
        barcodeField.clear();
        typeChoiceBox.setValue("unit"); // Reset to default
        selectedProduct = null;
        productsTable.getSelectionModel().clearSelection();
        clearMessage();
    }

    private void showMessage(String message) {
        messageLabel.setText(message);
    }

    private void clearMessage() {
        messageLabel.setText("");
    }
}


