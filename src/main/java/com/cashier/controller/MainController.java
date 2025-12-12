package com.cashier.controller;

import com.cashier.SessionContext;
import com.cashier.dao.ProduitDAO;
import com.cashier.dao.VenteDAO;
import com.cashier.dao.LigneVenteDAO;
import com.cashier.model.Produit;
import com.cashier.model.Vente;
import com.cashier.model.LigneVente;
import com.cashier.model.User;
import javafx.application.Platform;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    @FXML private Menu gestionMenu;
    @FXML private MenuItem productManagementMenuItem;
    @FXML private MenuItem salesHistoryMenuItem;
    @FXML private Label cashierNameLabel;

    @FXML private TableView<Produit> productsTable;
    @FXML private TableColumn<Produit, Integer> productIdColumn;
    @FXML private TableColumn<Produit, String> productNameColumn;
    @FXML private TableColumn<Produit, Double> productPriceColumn;
    @FXML private TableColumn<Produit, Double> productStockColumn;

    @FXML private Spinner<Double> quantitySpinner;

    @FXML private TableView<CartItem> cartTable;
    @FXML private TableColumn<CartItem, String> cartProductColumn;
    @FXML private TableColumn<CartItem, Double> cartQuantityColumn;
    @FXML private TableColumn<CartItem, Double> cartPriceColumn;
    @FXML private TableColumn<CartItem, Double> cartTotalColumn;
    @FXML private Label subtotalLabel;
    @FXML private Label totalLabel;
    @FXML private Button payButton;
    @FXML private Label messageLabel;

    private ProduitDAO produitDAO;
    private VenteDAO venteDAO;
    private LigneVenteDAO ligneVenteDAO;
    private ObservableList<CartItem> cartItems;
    private ObservableList<Produit> availableProducts;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        produitDAO = new ProduitDAO();
        venteDAO = new VenteDAO();
        ligneVenteDAO = new LigneVenteDAO();
        cartItems = FXCollections.observableArrayList();
        availableProducts = FXCollections.observableArrayList();

        setupProductsTable();
        setupCartTable();
        setupQuantitySpinner();
        loadProducts();
        clearMessage();

        applySessionContext();

        productsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                quantitySpinner.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(0.0, 9999.0, 0.1, 0.1));
            }
        });
    }

    private void applySessionContext() {
        if (!SessionContext.isLoggedIn()) {
            Platform.runLater(this::navigateToLogin);
            return;
        }

        User user = SessionContext.getCurrentUser();
        if (user != null) {
            cashierNameLabel.setText("Caissier: " + user.getUsername() + " (" + user.getRole() + ")");
        }

        if (!SessionContext.isAdmin()) {
            if (gestionMenu != null) {
                gestionMenu.setDisable(true);
                gestionMenu.setVisible(false);
            }
            if (productManagementMenuItem != null) {
                productManagementMenuItem.setDisable(true);
                productManagementMenuItem.setVisible(false);
            }
            if (salesHistoryMenuItem != null) {
                salesHistoryMenuItem.setDisable(true);
                salesHistoryMenuItem.setVisible(false);
            }
        }
    }

    private void setupProductsTable() {
        productIdColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getId()).asObject());
        productNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNom()));
        productPriceColumn.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getPrix()).asObject());
        productStockColumn.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getQuantite()).asObject());
        productsTable.setItems(availableProducts);
    }

    private void loadProducts() {
        availableProducts.clear();
        availableProducts.addAll(produitDAO.getAllProduits());
    }

    private void setupCartTable() {
        cartProductColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getProductName()));
        cartQuantityColumn.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getQuantity()).asObject());
        cartPriceColumn.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getUnitPrice()).asObject());
        cartTotalColumn.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getTotal()).asObject());

        cartTable.setItems(cartItems);

        cartQuantityColumn.setCellFactory(TextFieldTableCell.forTableColumn(new StringConverter<Double>() {
            @Override
            public String toString(Double object) {
                return object.toString();
            }

            @Override
            public Double fromString(String string) {
                try {
                    return Double.valueOf(string);
                } catch (NumberFormatException e) {
                    return 0.0;
                }
            }
        }));
        cartQuantityColumn.setOnEditCommit(event -> {
            CartItem item = event.getRowValue();
            double oldQuantity = item.getQuantity();
            double newQuantity = event.getNewValue();

            Produit productInStock = produitDAO.getProduitById(item.getProductId());
            if (productInStock != null) {
                if (newQuantity > productInStock.getQuantite()) {
                    showMessage("Quantité demandée supérieure au stock disponible pour " + item.getProductName() + ".");
                    item.setQuantity(oldQuantity);
                    cartTable.refresh();
                } else if (newQuantity <= 0) {
                    cartItems.remove(item);
                } else {
                    item.setQuantity(newQuantity);
                }
                updateTotals();
            }
        });

        cartTable.setEditable(true);
    }

    private void setupQuantitySpinner() {
        quantitySpinner.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(0.0, 9999.0, 0.1, 0.1));
        quantitySpinner.setEditable(true);
    }

    @FXML
    private void addProductToCartFromTable() {
        Produit selectedProduct = productsTable.getSelectionModel().getSelectedItem();
        if (selectedProduct == null) {
            showMessage("Veuillez sélectionner un produit dans la liste.");
            return;
        }

        double requestedQuantity = quantitySpinner.getValue();

        if (requestedQuantity <= 0) {
            showMessage("Veuillez entrer une quantité valide.");
            return;
        }

        if (requestedQuantity > selectedProduct.getQuantite()) {
            showMessage("Quantité demandée supérieure au stock disponible.");
            return;
        }

        CartItem existingItem = null;
        for (CartItem item : cartItems) {
            if (item.getProductId() == selectedProduct.getId()) {
                existingItem = item;
                break;
            }
        }

        if (existingItem != null) {
            double newQuantity = existingItem.getQuantity() + requestedQuantity;
            if (newQuantity > selectedProduct.getQuantite()) {
                showMessage("Quantité totale demandée supérieure au stock disponible.");
                return;
            }
            existingItem.setQuantity(newQuantity);
        } else {
            CartItem newItem = new CartItem(
                selectedProduct.getId(),
                selectedProduct.getNom(),
                requestedQuantity,
                selectedProduct.getPrix()
            );
            cartItems.add(newItem);
        }

        updateTotals();
        clearMessage();
    }

    @FXML
    private void removeFromCart() {
        CartItem selectedItem = cartTable.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            cartItems.remove(selectedItem);
            updateTotals();
        }
    }

    @FXML
    private void clearCart() {
        cartItems.clear();
        updateTotals();
        clearMessage();
    }

    private void updateTotals() {
        double total = cartItems.stream().mapToDouble(CartItem::getTotal).sum();
        subtotalLabel.setText(String.format("%.3f TND", total));
        totalLabel.setText(String.format("%.3f TND", total));
        payButton.setDisable(cartItems.isEmpty());
    }

    @FXML
    private void processPayment() {
        if (cartItems.isEmpty()) {
            showMessage("Le panier est vide.");
            return;
        }

        User currentUser = SessionContext.getCurrentUser();
        if (currentUser == null) {
            showMessage("Session expirée. Veuillez vous reconnecter.");
            navigateToLogin();
            return;
        }

        try {
            double total = cartItems.stream().mapToDouble(CartItem::getTotal).sum();
            Vente vente = new Vente(LocalDateTime.now(), total, currentUser.getId());
            double venteId = venteDAO.addVente(vente);

            if (venteId > 0) {
                for (CartItem item : cartItems) {
                    LigneVente ligneVente = new LigneVente((int) venteId, item.getProductId(), item.getQuantity(), item.getUnitPrice());
                    ligneVenteDAO.addLigneVente(ligneVente);

                    Produit produit = produitDAO.getProduitById(item.getProductId());
                    if (produit != null) {
                        produit.setQuantite(produit.getQuantite() - item.getQuantity());
                        produitDAO.updateProduit(produit);
                    }
                }

                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ReceiptView.fxml"));
                Parent root = loader.load();
                ReceiptController receiptController = loader.getController();
                receiptController.setReceiptDetails((int) venteId, vente.getDateVente(), cartItems, total);

                Stage receiptStage = new Stage();
                receiptStage.setTitle("Reçu de Vente");
                receiptStage.setScene(new Scene(root));
                receiptStage.initModality(Modality.APPLICATION_MODAL);
                receiptStage.showAndWait();

                showMessage("Vente enregistrée avec succès! ID: " + (int) venteId);
                clearCart();
                loadProducts();
            } else {
                showMessage("Erreur lors de l\"enregistrement de la vente.");
            }
        } catch (Exception e) {
            showMessage("Erreur: " + e.getMessage());
        }
    }

    @FXML
    private void cancelSale() {
        clearCart();
        showMessage("Vente annulée.");
    }

    @FXML
    private void showProductManagement() {
        if (!ensureAdminAccess()) {
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ProductManagement.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Gestion des Produits");
            stage.setScene(new Scene(root, 800, 600));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            loadProducts();
        } catch (IOException e) {
            showMessage("Erreur lors de l\"ouverture de la gestion des produits: " + e.getMessage());
        }
    }

    @FXML
    private void showSalesHistory() {
        if (!ensureAdminAccess()) {
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/SalesHistory.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Historique des Ventes");
            stage.setScene(new Scene(root, 800, 600));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();
        } catch (IOException e) {
            showMessage("Erreur lors de l\"ouverture de l\"historique des ventes: " + e.getMessage());
        }
    }

    private boolean ensureAdminAccess() {
        if (SessionContext.isAdmin()) {
            return true;
        }

        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Accès refusé");
        alert.setHeaderText("Accès administrateur requis");
        alert.setContentText("Vous n'avez pas les droits pour accéder à cette fonctionnalité.");
        alert.showAndWait();
        return false;
    }

    @FXML
    private void handleLogout() {
        SessionContext.clear();
        navigateToLogin();
    }

    private void navigateToLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/LoginView.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) payButton.getScene().getWindow();
            Scene scene = new Scene(root, 450, 320);
            scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
            stage.setScene(scene);
            stage.setResizable(false);
            stage.centerOnScreen();
        } catch (IOException e) {
            showMessage("Erreur lors de la déconnexion: " + e.getMessage());
        }
    }

    @FXML
    private void handleExit() {
        System.exit(0);
    }

    private void showMessage(String message) {
        messageLabel.setText(message);
    }

    private void clearMessage() {
        messageLabel.setText("");
    }

    public static class CartItem {
        private int productId;
        private String productName;
        private double quantity;
        private double unitPrice;

        public CartItem(int productId, String productName, double quantity, double unitPrice) {
            this.productId = productId;
            this.productName = productName;
            this.quantity = quantity;
            this.unitPrice = unitPrice;
        }

        public int getProductId() {
            return productId;
        }

        public String getProductName() {
            return productName;
        }

        public double getQuantity() {
            return quantity;
        }

        public void setQuantity(double quantity) {
            this.quantity = quantity;
        }

        public double getUnitPrice() {
            return unitPrice;
        }

        public double getTotal() {
            return quantity * unitPrice;
        }
    }
}
