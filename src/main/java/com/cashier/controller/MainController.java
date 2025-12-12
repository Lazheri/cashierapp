package com.cashier.controller;

import com.cashier.dao.ProduitDAO;
import com.cashier.dao.VenteDAO;
import com.cashier.dao.LigneVenteDAO;
import com.cashier.model.Produit;
import com.cashier.model.Vente;
import com.cashier.model.LigneVente;
import javafx.animation.PauseTransition;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.StringConverter;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.stream.Collectors;

public class MainController implements Initializable {

    @FXML private TextField searchField;
    @FXML private TextField barcodeField;
    @FXML private ComboBox<String> categoryFilter;
    @FXML private Label connectionStatusLabel;

    @FXML private TilePane productGrid;

    @FXML private TableView<CartItem> cartTable;
    @FXML private TableColumn<CartItem, String> cartProductColumn;
    @FXML private TableColumn<CartItem, Double> cartQuantityColumn;
    @FXML private TableColumn<CartItem, Double> cartPriceColumn;
    @FXML private TableColumn<CartItem, Double> cartTotalColumn;
    @FXML private Label subtotalLabel;
    @FXML private Label totalLabel;
    @FXML private Button payButton;
    @FXML private Label messageLabel;
    
    // Hidden spinner for logic consistency if needed, though we use direct add
    @FXML private Spinner<Double> quantitySpinner;

    private ProduitDAO produitDAO;
    private VenteDAO venteDAO;
    private LigneVenteDAO ligneVenteDAO;
    private ObservableList<CartItem> cartItems;
    private ObservableList<Produit> allProducts; // Cache of all products
    
    private PauseTransition searchDebounce;
    private static final double LOW_STOCK_THRESHOLD = 5.0;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        produitDAO = new ProduitDAO();
        venteDAO = new VenteDAO();
        ligneVenteDAO = new LigneVenteDAO();
        cartItems = FXCollections.observableArrayList();
        allProducts = FXCollections.observableArrayList();

        setupCartTable();
        loadProducts(); // Loads all products and populates grid
        
        // Search Debounce
        searchDebounce = new PauseTransition(Duration.millis(300));
        searchDebounce.setOnFinished(e -> renderProductGrid());
        
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            searchDebounce.playFromStart();
        });

        // Barcode Listener
        barcodeField.setOnAction(e -> handleBarcodeEntry());
        
        // Category Filter Listener
        categoryFilter.valueProperty().addListener((obs, oldVal, newVal) -> {
            renderProductGrid();
        });
        
        clearMessage();
    }

    private void loadProducts() {
        allProducts.clear();
        allProducts.addAll(produitDAO.getAllProduits());
        
        // Update Category Filter
        Set<String> categories = allProducts.stream()
            .map(Produit::getCategorie)
            .filter(c -> c != null && !c.isEmpty())
            .collect(Collectors.toSet());
            
        ObservableList<String> categoryList = FXCollections.observableArrayList("Toutes", "Stock Faible");
        categoryList.addAll(categories.stream().sorted().collect(Collectors.toList()));
        
        // Preserve selection if possible
        String currentSelection = categoryFilter.getValue();
        categoryFilter.setItems(categoryList);
        if (currentSelection != null && categoryList.contains(currentSelection)) {
            categoryFilter.setValue(currentSelection);
        } else {
            categoryFilter.setValue("Toutes");
        }
        
        renderProductGrid();
    }

    private void renderProductGrid() {
        productGrid.getChildren().clear();
        
        String searchText = searchField.getText().toLowerCase().trim();
        String selectedCategory = categoryFilter.getValue();
        
        List<Produit> filteredProducts = allProducts.stream()
            .filter(p -> {
                boolean matchesSearch = p.getNom().toLowerCase().contains(searchText) || 
                                        (p.getCodeBarres() != null && p.getCodeBarres().contains(searchText));
                
                boolean matchesCategory = false;
                if (selectedCategory == null || "Toutes".equals(selectedCategory)) {
                    matchesCategory = true;
                } else if ("Stock Faible".equals(selectedCategory)) {
                     matchesCategory = p.getQuantite() < LOW_STOCK_THRESHOLD;
                } else {
                    matchesCategory = p.getCategorie() != null && p.getCategorie().equals(selectedCategory);
                }
                
                return matchesSearch && matchesCategory;
            })
            .collect(Collectors.toList());
            
        for (Produit p : filteredProducts) {
            productGrid.getChildren().add(createProductCard(p));
        }
    }

    private VBox createProductCard(Produit p) {
        VBox card = new VBox(5);
        card.getStyleClass().add("product-card");
        
        if (p.getQuantite() < LOW_STOCK_THRESHOLD) {
            card.getStyleClass().add("product-card-low-stock");
        }

        Label nameLabel = new Label(p.getNom());
        nameLabel.getStyleClass().add("product-card-name");
        nameLabel.setWrapText(true);
        nameLabel.setMaxHeight(40);
        nameLabel.setMinHeight(40);
        nameLabel.setAlignment(Pos.TOP_LEFT);

        Label priceLabel = new Label(String.format("%.3f TND", p.getPrix()));
        priceLabel.getStyleClass().add("product-card-price");

        Label stockLabel = new Label("Stock: " + p.getQuantite());
        stockLabel.getStyleClass().add("product-card-stock");
        if (p.getQuantite() < LOW_STOCK_THRESHOLD) {
            stockLabel.getStyleClass().add("stock-pill-low");
        }

        card.getChildren().addAll(nameLabel, priceLabel, stockLabel);
        
        card.setOnMouseClicked(e -> addProductToCart(p));
        
        return card;
    }
    
    private void handleBarcodeEntry() {
        String barcode = barcodeField.getText().trim();
        if (barcode.isEmpty()) return;
        
        Produit found = produitDAO.getProduitByCodeBarres(barcode);
        if (found != null) {
            addProductToCart(found);
            barcodeField.clear();
        } else {
            showMessage("Produit non trouvé avec le code-barres: " + barcode);
            // Optional: beep sound or visual cue
        }
    }

    private void addProductToCart(Produit product) {
        // Determine quantity step
        double step = 1.0;
        if ("weight".equalsIgnoreCase(product.getType())) {
            step = 1.0; // Or 0.1 if preferred for weight items, but 1.0 is safer default
        }
        
        // Check stock
        if (product.getQuantite() <= 0) {
            showMessage("Produit en rupture de stock: " + product.getNom());
            return;
        }

        // Check if product already in cart
        CartItem existingItem = null;
        for (CartItem item : cartItems) {
            if (item.getProductId() == product.getId()) {
                existingItem = item;
                break;
            }
        }

        double requestedQuantity = step;
        
        if (existingItem != null) {
            double newQuantity = existingItem.getQuantity() + requestedQuantity;
            if (newQuantity > product.getQuantite()) {
                showMessage("Stock insuffisant pour " + product.getNom() + " (Max: " + product.getQuantite() + ")");
                return;
            }
            existingItem.setQuantity(newQuantity);
            cartTable.refresh();
        } else {
            CartItem newItem = new CartItem(
                product.getId(),
                product.getNom(),
                requestedQuantity,
                product.getPrix()
            );
            cartItems.add(newItem);
            
            // Low stock warning when adding to cart
            if (product.getQuantite() < LOW_STOCK_THRESHOLD) {
                 showMessage("Attention: Stock faible pour " + product.getNom());
            } else {
                clearMessage();
            }
        }

        updateTotals();
    }

    private void setupCartTable() {
        cartProductColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getProductName()));
        cartQuantityColumn.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getQuantity()).asObject());
        cartPriceColumn.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getUnitPrice()).asObject());
        cartTotalColumn.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getTotal()).asObject());

        cartTable.setItems(cartItems);

        // Make quantity column editable
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

        try {
            double total = cartItems.stream().mapToDouble(CartItem::getTotal).sum();
            
            FXMLLoader paymentLoader = new FXMLLoader(getClass().getResource("/fxml/PaymentDialog.fxml"));
            Parent paymentRoot = paymentLoader.load();
            PaymentDialogController paymentController = paymentLoader.getController();
            paymentController.setTotal(total);

            Stage paymentStage = new Stage();
            paymentStage.setTitle("Paiement");
            paymentStage.setScene(new Scene(paymentRoot));
            paymentStage.initModality(Modality.APPLICATION_MODAL);
            paymentStage.showAndWait();

            com.cashier.model.PaymentResult paymentResult = paymentController.getResult();
            
            if (paymentResult == null || !paymentResult.isSuccess()) {
                showMessage("Paiement annulé.");
                return;
            }

            Vente vente = new Vente(
                LocalDateTime.now(), 
                total, 
                paymentResult.getPaymentMethod(), 
                paymentResult.getAmountPaid(), 
                paymentResult.getChangeDue(), 
                paymentResult.getPaymentReference()
            );
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
                receiptController.setReceiptDetails((int) venteId, vente, cartItems);

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
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ProductManagement.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Gestion des Produits");
            stage.setScene(new Scene(root, 800, 600));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait(); 
            loadProducts(); // Refresh products after management
        } catch (IOException e) {
            showMessage("Erreur lors de l\"ouverture de la gestion des produits: " + e.getMessage());
        }
    }

    @FXML
    private void showSalesHistory() {
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

    @FXML
    private void handleExit() {
        System.exit(0);
    }

    private void showMessage(String message) {
        messageLabel.setText(message);
        // Fade out message after 3 seconds
        PauseTransition delay = new PauseTransition(Duration.seconds(3));
        delay.setOnFinished(e -> messageLabel.setText(""));
        delay.play();
    }

    private void clearMessage() {
        messageLabel.setText("");
    }

    // Inner class for cart items
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

        public int getProductId() { return productId; }
        public String getProductName() { return productName; }
        public double getQuantity() { return quantity; }
        public void setQuantity(double quantity) { this.quantity = quantity; }
        public double getUnitPrice() { return unitPrice; }
        public double getTotal() { return quantity * unitPrice; }
    }
}
